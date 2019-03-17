package org.compiere.invoicing.test

import org.compiere.accounting.MProduct
import org.compiere.accounting.MStorageOnHand
import org.compiere.accounting.MOrder
import org.compiere.accounting.MOrderLine
import org.compiere.accounting.MPayment
import org.compiere.invoicing.MInOut
import org.compiere.invoicing.MInOutLine
import org.compiere.invoicing.MInvoice
import org.compiere.order.X_M_InOut
import org.compiere.orm.DefaultModelFactory
import org.compiere.orm.IModelFactory
import org.compiere.orm.MDocType
import org.compiere.process.DocAction
import org.compiere.process.ProcessInfo
import org.compiere.production.MLocator
import org.compiere.production.MProduction
import org.idempiere.common.util.Env
import org.idempiere.process.ProductionCreate
import org.junit.Before
import org.junit.Test
import software.hsharp.core.util.DB
import software.hsharp.core.util.asResource
import software.hsharp.core.util.queryOf
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import org.compiere.crm.MBPartner
import org.compiere.crm.MCountry
import org.compiere.crm.MRegion
import org.compiere.crm.MLocation
import org.compiere.crm.MBPartnerLocation
import org.compiere.model.I_M_PriceList
import org.compiere.model.I_M_Product
import org.compiere.model.I_M_PriceList_Version
import org.compiere.model.I_M_InOut
import org.compiere.model.I_M_InOutLine
import org.compiere.model.I_C_BPartner
import org.compiere.model.I_C_Invoice
import org.compiere.model.I_C_Payment
import org.compiere.model.I_M_Production
import org.compiere.product.MProductBOM
import org.compiere.product.MPriceList
import org.compiere.product.MPriceListVersion
import org.compiere.product.MDiscountSchema
import org.compiere.product.MProductPrice
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

data class InvoiceImportantTestAttributes(
    val grandTotal: BigDecimal,
    val grandTotalVAT: BigDecimal,
    val reverseCharge: Boolean,
    val dueDate1: Date,
    val dueDate2: Date
)

data class MaterialMovementImportantTestAttributes(
    val productName: String,
    val moveDate: Date?,
    val amountIn: BigDecimal,
    val amountOut: BigDecimal
)

class InvoiceTest : BaseComponentTest() {
    companion object {
        const val QTY = 1
        private var index = 0
        const val MAT = "Mat1-"
        const val BOM = "BOM1-"
    }

    private var _testProduct: I_M_Product? = null
    private val testProduct get() = _testProduct!!
    private var _salesPriceList: I_M_PriceList? = null
    private val salesPriceList get() = _salesPriceList!!
    private var _now: Timestamp? = null
    private val now get() = _now!!

    @Before
    fun createProdAndReceipt() {
        DB.run {
            _now = Timestamp(System.currentTimeMillis())
            val always = Timestamp(0)

            fun createSalesPriceList(): MPriceList {
                val salesPriceList = MPriceList(ctx, 0)
                salesPriceList.setIsDefault(true)
                salesPriceList.setIsSOPriceList(true)
                salesPriceList.name = "S-" + randomString(10)
                salesPriceList.currencyId = EUR
                salesPriceList.save()
                val check1: MPriceList = getById(salesPriceList.id, I_M_PriceList.Table_Name)
                assertNotNull(check1)

                return salesPriceList
            }

            // make sure the price list is there
            _salesPriceList = MPriceList.getDefault(ctx, true) ?: createSalesPriceList()

            fun createSalesPriceListVersion(): MPriceListVersion {
                val priceListVersion = MPriceListVersion(salesPriceList)
                priceListVersion.name = salesPriceList.name
                priceListVersion.validFrom = always
                priceListVersion.setDiscountSchemaId(MDiscountSchema(ctx, 1000000).id)
                priceListVersion.save()

                val check2: MPriceListVersion = getById(priceListVersion.id, I_M_PriceList_Version.Table_Name)
                assertNotNull(check2)

                return priceListVersion
            }

            // get the product on hand
            val product: MProduct = createAProduct(MAT + randomString(5), I_M_Product.PRODUCTTYPE_Item) as MProduct
            _testProduct = product

            // put the product on the price list
            val currentPriceListVersion = salesPriceList.getPriceListVersion(now) ?: createSalesPriceListVersion()
            val price = 1.toBigDecimal()
            val productPrice = MProductPrice(currentPriceListVersion, product.id, price, price, price)
            productPrice.save()

            val vendor = createBPartner()

            val vendorShipment = MInOut(ctx, 0)
            vendorShipment.setOrgId(org.orgId)
            vendorShipment.setIsSOTrx(false)
            vendorShipment.movementType = X_M_InOut.MOVEMENTTYPE_VendorReceipts
            vendorShipment.setDocumentTypeId()
            vendorShipment.businessPartnerId = vendor.id
            vendorShipment.businessPartnerLocationId = vendor.locations.first().businessPartnerLocationId
            vendorShipment.warehouseId = warehouse.warehouseId
            vendorShipment.save()
            val receipt = getById<MInOut>(vendorShipment.id, I_M_InOut.Table_Name)
            assertNotNull(receipt)
            val receiptLine = MInOutLine(receipt)
            receiptLine.setOrgId(org.orgId)
            receiptLine.product = product
            receiptLine.movementQty = 1000000.toBigDecimal()
            receiptLine.locatorId = MLocator(ctx, MLocator(ctx, 1000000).id).id
            receiptLine.save()
            val line = getById<MInOutLine>(receiptLine.id, I_M_InOutLine.Table_Name)
            assertNotNull(line)
            (vendorShipment as X_M_InOut).docStatus = X_M_InOut.DOCSTATUS_Completed
            vendorShipment.save()
            vendorShipment.setDocAction(DocAction.STATUS_Completed)
            vendorShipment.completeIt()
            val storageOnHand = MStorageOnHand.getOfProduct(ctx, product.id).first()
            assertEquals(0.0.toBigDecimal(), receiptLine.movementQty - storageOnHand.qtyOnHand)
        }
    }

    private fun createBPartner(): I_C_BPartner {
        val newPartner = MBPartner.getTemplate(ctx, AD_CLIENT_ID)
        val name = "Test " + randomString(10)
        newPartner.name = name
        val value = "t-" + randomString(5)
        newPartner.searchKey = value
        newPartner.save()

        val defaultCountry = MCountry.getDefault(ctx)
        val defaultRegion = MRegion.getDefault(ctx)
        val location = MLocation(defaultCountry, defaultRegion)
        location.save()
        val partnerLocation = MBPartnerLocation(newPartner)
        partnerLocation.locationId = location.locationId
        partnerLocation.save()

        return newPartner
    }

    @Test
    fun `get invoice by id`() {
        DB.run {
            loginClient(11)
            val invoiceId = 106

            val invoice: MInvoice = getById(invoiceId, I_C_Invoice.Table_Name)
            assertNotNull(invoice)
            assertEquals(invoiceId, invoice.id)
            val lines = invoice.lines
            assertNotNull(lines)
            assertEquals(6, lines.count())
        }
    }

    private fun createOrder(c_DocType_ID: Int, product_id: Int): Triple<MOrder, Int, Int> {
        val order = MOrder(Env.getCtx(), 0)
        order.setOrgId(1000000)
        order.warehouseId = 1000000
        order.setIsSOTrx(true)
        order.documentTypeId =
            c_DocType_ID // 133 on credit order (generates invoice), 130 prepay order, 132 standard order
        order.targetDocumentTypeId = c_DocType_ID
        order.priceListId = MPriceList.getDefault(ctx, true).id
        order.setCurrencyId(EUR) // EUR
        order.paymentTermId = paymentTerm.id
        order.salesRepresentativeId = 1000000

        val partner = createBPartner()

        val product = MProduct.get(Env.getCtx(), product_id)

        order.setBPartner(partner)
        order.save()

        val orderLine = MOrderLine(order)
        orderLine.product = product
        orderLine.taxId = tax.taxId
        val qty = QTY
        orderLine.setQty(qty.toBigDecimal())
        orderLine.save()

        return Triple(order, partner.id, product_id)
    }

    @Test
    fun `create invoice from prepay order after receiving the payment`() {
        DB.run {
            createInvoiceFromOrder(1000030, testProduct.id, BigDecimal("1.10")) {
                val payment = MPayment(ctx, 0)
                payment.businessPartnerId = it.businessPartnerId
                payment.setOrgId(org.orgId)
                payment.bankAccountId = bankAccount.id
                payment.setCurrencyId(EUR) // EUR
                payment.payAmt = 1.10.toBigDecimal()
                payment.save()

                val pay: MPayment = getById(payment.id, I_C_Payment.Table_Name)
                assertNotNull(pay)

                it.paymentId = pay.id
                it.save()
            }
        }
    }

    private fun createInvoiceFromOrder(
        c_DocType_ID: Int,
        productId: Int,
        expectedPrice: BigDecimal,
        doAfterOrderTask: (MOrder) -> Unit
    ) {
        val (order, id, product_id) = createOrder(c_DocType_ID, productId)
        doAfterOrderTask(order)

        order.docStatus = MOrder.DOCSTATUS_Completed
        order.save()
        order.setDocAction(DocAction.STATUS_Completed)
        val completion = order.completeIt()
        order.save()

        val invoice = completion.result as I_C_Invoice
        assertEquals(1, order.invoices.count())
        assertEquals(invoice, order.invoices.first())
        assertEquals(id, invoice.businessPartnerId)
        val lines = invoice.getLines(false)
        assertEquals(1, lines.count())
        val line = lines.first()
        assertEquals(product_id, line.productId)
        assertEquals(QTY.toBigDecimal(), line.qtyInvoiced)

        val modelFactory: IModelFactory = DefaultModelFactory()
        val result = modelFactory.getPO(I_C_Invoice.Table_Name, invoice.id)
        println(result)
        assertNotNull(result)
        val invoice1 = result as MInvoice
        assertNotNull(invoice1)
        assertEquals(invoice.id, invoice1.id)
        assertEquals(expectedPrice, invoice1.getGrandTotal(false))
        val lines1 = invoice1.lines
        assertNotNull(lines1)
        assertEquals(1, lines1.count())

        "/sql/invoice_details.sql".asResource {
            val sql = it.replace("\$P{RECORD_ID}", invoice1.id.toString())
            val loadQuery =
                queryOf(sql, listOf())
                    .map { row ->
                        InvoiceImportantTestAttributes(
                            row.bigDecimal("grandtotal"), row.bigDecimal("grandtotalvat"),
                            row.boolean("reverse_charge"), row.sqlDate("due_previous_business_day"),
                            row.sqlDate("due_previous_5business_days")
                        )
                    }.asList
            val list = DB.current.run(loadQuery)
            assertEquals(1, list.count())
            val details = list.first()
            assertFalse(details.reverseCharge)
            assertEquals(expectedPrice, details.grandTotal)
            assertEquals(expectedPrice - (expectedPrice / 1.10.toBigDecimal()), details.grandTotalVAT)
            val now = java.util.Date()
            assertTrue(details.dueDate1 < now)
            assertTrue(details.dueDate2 < now)
        }
        index++
        `should have specific no of material movements after all runs`()
    }

    @Test
    fun `create invoice from order (on credit)`() {
        DB.run {
            val salesOrderTypes = MDocType.getOfDocBaseType(ctx, MDocType.DOCBASETYPE_SalesOrder)
            val onCreditOrder = salesOrderTypes.first { it.name == "Credit Order" }
            createInvoiceFromOrder(onCreditOrder.id, testProduct.id, BigDecimal("1.10")) {}
        }
    }

    @Test
    fun `create invoice from BOM order with production step in between - on credit`() {
        DB.run {
            val bomProduct = createAProduct(BOM + randomString(5), I_M_Product.PRODUCTTYPE_Item) as MProduct
            bomProduct.setIsBOM(true)
            bomProduct.save()
            val innerProduct = MProductBOM(ctx, 0)
            innerProduct.bomQty = 10.toBigDecimal()
            innerProduct.bomProductId = testProduct.id
            innerProduct.productId = bomProduct.id
            innerProduct.line = 10
            innerProduct.save()

            // put the product on the price list
            val currentPriceListVersion = salesPriceList.getPriceListVersion(now)!!
            val price = 11.0.toBigDecimal()
            val productPrice = MProductPrice(currentPriceListVersion, bomProduct.id, price, price, price)
            productPrice.save()

            createInvoiceFromOrder(1000033, bomProduct.productId, BigDecimal("12.10")) {
                val orderLine = it.lines.first()
                val production = MProduction(orderLine)
                production.setOrgId(1000000)
                production.productId =
                    orderLine.productId // TODO: Why? Should not this be done automatically in the constructor?
                production.productionQty =
                    orderLine.qtyOrdered // TODO: Why? Should not this be done automatically in the constructor?
                production.locatorId = 1000000
                production.save()

                val productionCreate = ProductionCreate(m_production = production)
                val pi = ProcessInfo("", 0)
                productionCreate.startProcess(ctx, pi)

                val prod: MProduction = getById(production.id, I_M_Production.Table_Name)
                prod.docStatus = MProduction.DOCSTATUS_Completed
                prod.save()

                prod.setDocAction(DocAction.STATUS_Completed)
                prod.save()

                prod.completeIt()
            }
        }
    }

    @Test
    fun `create invoice from order (on credit) without price list should fail`() {
        DB.run {
            val product = createAProduct("Other 1-" + randomString(5), I_M_Product.PRODUCTTYPE_Item)
            try {
                createInvoiceFromOrder(1000033, product.id, BigDecimal("1.10")) {}
                fail("Invoice was created for a product not on a price list")
            } catch (e: Exception) {
            }
        }
    }

    @Test
    fun `create invoice from order (on credit) without amount on hand should fail`() {
        DB.run {
            val product = createAProduct("Other 1-" + randomString(5), I_M_Product.PRODUCTTYPE_Item)
            val pl = MPriceList(ctx, 1000000)
            val plv = pl.getPriceListVersion(Timestamp.from(Instant.now()))!!
            val price = 10.toBigDecimal()
            val pp = MProductPrice(ctx, plv.id, product.id, price, price, price, null)
            pp.save()
            try {
                createInvoiceFromOrder(1000033, product.id, BigDecimal("11.00")) {}
                fail("Invoice was created for a product with negative inventory")
            } catch (e: Exception) {
            }
        }
    }

    private fun `should have specific no of material movements after all runs`() {
        if (index == 3) {
            "/sql/recent_material_movements.sql".asResource { sql ->
                val loadQuery =
                    queryOf(sql, listOf())
                        .map { row ->
                            MaterialMovementImportantTestAttributes(
                                row.string("pro_name"), row.sqlDate("move_date"),
                                row.bigDecimal("amout_in"), row.bigDecimal("amout_out")
                            )
                        }
                        .asList

                val list = DB.current.run(loadQuery)
                kotlin.test.assertEquals(11, list.count())
                val standards = list.filter { it.productName.startsWith(MAT) }
                val bom1s = list.filter { it.productName.startsWith(BOM) }
                kotlin.test.assertEquals(9, standards.count())
                kotlin.test.assertEquals(
                    6 * 1000000 - 2 * 1 - 10,
                    standards.sumBy { (it.amountIn - it.amountOut).toInt() })
                kotlin.test.assertEquals(2, bom1s.count())
                kotlin.test.assertEquals(1 - 1, bom1s.sumBy { (it.amountIn - it.amountOut).toInt() })
            }
        }
    }
}
