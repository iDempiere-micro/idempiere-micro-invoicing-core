package org.compiere.invoicing.test

import company.bigger.idempiere.service.SimpleModelFactory
import org.compiere.accounting.MAcctSchema
import org.compiere.accounting.MCharge
import org.compiere.accounting.MCostDetail
import org.compiere.accounting.MCostElement
import org.compiere.accounting.MWarehouse
import org.compiere.bank.MBank
import org.compiere.bank.MBankAccount
import org.compiere.invoicing.MInventory
import org.compiere.invoicing.MInventoryLine
import org.compiere.invoicing.MPaymentTerm
import org.compiere.invoicing.test.SetupClientTests.Companion.createClient
import org.compiere.model.Client
import org.compiere.model.ClientOrganization
import org.compiere.model.I_C_BankAccount
import org.compiere.model.I_C_Charge
import org.compiere.model.I_C_PaymentTerm
import org.compiere.model.I_C_Tax
import org.compiere.model.I_C_TaxCategory
import org.compiere.model.I_M_Product
import org.compiere.model.I_M_Warehouse
import org.compiere.order.MPaySchedule
import org.compiere.orm.Query
import org.compiere.orm.getClientDocumentTypes
import org.compiere.orm.getClientOrganizations
import org.compiere.orm.getOrg
import org.compiere.process.DocAction
import org.compiere.product.MAttributeSetInstance
import org.compiere.product.MProduct
import org.compiere.product.MUOM
import org.compiere.tax.MTax
import org.compiere.tax.MTaxCategory
import org.idempiere.common.util.AdempiereSystemError
import org.idempiere.common.util.EnvironmentServiceImpl
import org.idempiere.icommon.model.PersistentObject
import org.junit.Before
import org.slf4j.impl.SimpleLogger
import software.hsharp.core.util.DB
import software.hsharp.core.util.Environment
import software.hsharp.core.util.HikariCPI
import java.util.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal val sessionUrl =
    System.getenv("SESSION_URL") ?: "jdbc:postgresql://localhost:5433/idempiere?autosave=conservative"

/**
 * Generate a random string (small letters)
 */
fun randomString(length: Int): String {
    fun ClosedRange<Char>.randomString(length: Int) =
        (1..length)
            .map { (Random().nextInt(endInclusive.toInt() - start.toInt()) + start.toInt()).toChar() }
            .joinToString("")
    return ('a'..'z').randomString(length)
}

internal val environmentService = EnvironmentServiceImpl(0, 0, 0)
internal val modelFactory = SimpleModelFactory()
private val mainLogicModule = MainLogicModule()
private val mainEnvironmentModule = MainEnvironmentModule()
internal val baseModule = ModuleImpl(
    environment = mainEnvironmentModule,
    logic = mainLogicModule,
    data = MainDataModule(mainEnvironmentModule)
)
internal val environment = Environment(baseModule)

abstract class BaseComponentTest {
    companion object {
        val NEW_AD_CLIENT_ID = 1000000
        const val EUR = 102
    }

    init {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN")
        HikariCPI.default(sessionUrl, "adempiere", "adempiere")
    }

    private var _taxCategory: I_C_TaxCategory? = null
    protected val taxCategory: I_C_TaxCategory get() = _taxCategory ?: throw AdempiereSystemError("tax category is null")
    private var _tax: I_C_Tax? = null
    protected val tax: I_C_Tax get() = _tax ?: throw AdempiereSystemError("tax is null")

    protected val org: ClientOrganization
        get() = getOrg(1000000)
    protected val warehouse: I_M_Warehouse
        get() = MWarehouse(1000000)
    private var _paymentTerm: I_C_PaymentTerm? = null
    protected val paymentTerm: I_C_PaymentTerm get() = _paymentTerm ?: throw AdempiereSystemError("payment term is null")
    private var _bankAccount: I_C_BankAccount? = null
    protected val bankAccount get() = _bankAccount ?: throw AdempiereSystemError("bank account is null")
    private var _charge: I_C_Charge? = null
    protected val charge get() = _charge ?: throw AdempiereSystemError("charge is null")

    @Before
    fun prepareEnv() {
        environment.run {
            environmentService.login(NEW_AD_CLIENT_ID, 0, 0)
            DB.run {
                val query = Query<Client>("AD_Client", "ad_client_id=$NEW_AD_CLIENT_ID")
                val result = query.list()
                if (result.isEmpty()) {
                    environmentService.login(0, 0, 0)
                    createClient()
                }

                if (_taxCategory == null) ensureTaxCategory10Pct()
                if (_paymentTerm == null) ensurePaymentTerm14Days()
                if (_bankAccount == null) ensureBankAccount()
                if (_charge == null) ensureCharge()
            }
        }
    }

    private fun ensureBankAccount() {
        val newBank = MBank(0)
        newBank.name = "B-" + randomString(10)
        newBank.routingNo = newBank.name
        newBank.save()
        val newBankAccount = MBankAccount(0)
        newBankAccount.bankId = newBank.id
        newBankAccount.name = newBank.name
        newBankAccount.setSearchKey(newBankAccount.name)
        newBankAccount.currencyId = EUR
        newBankAccount.accountNo = newBank.name
        newBankAccount.save()
        _bankAccount = getById(newBankAccount.id, I_C_BankAccount.Table_Name)
        assertNotNull(_bankAccount)
    }

    private fun ensureCharge() {
        val newCharge = MCharge(0)
        newCharge.name = "CH-" + randomString(5)
        newCharge.save()
        _charge = getById(newCharge.id, I_C_Charge.Table_Name)
    }

    private fun ensurePaymentTerm14Days() {
        val newPaymentTerm = MPaymentTerm(0)
        newPaymentTerm.setIsValid(true)
        newPaymentTerm.name = "P-" + randomString(10)
        newPaymentTerm.setSearchKey(newPaymentTerm.name)
        newPaymentTerm.save()
        _paymentTerm = getById(newPaymentTerm.id, I_C_PaymentTerm.Table_Name)
        assertNotNull(_paymentTerm)
        val paySchedule = MPaySchedule(0)
        paySchedule.paymentTermId = paymentTerm.id
        paySchedule.percentage = 100.toBigDecimal()
        paySchedule.save()
    }

    fun <T : PersistentObject> getById(id: Int, tableName: String): T {
        val result: T = modelFactory.getPO(tableName, id)
        println(result)
        assertNotNull(result)
        assertEquals(id, result.id)
        return result
    }

    protected fun getProductById(product_id: Int): I_M_Product {
        val product: I_M_Product = modelFactory.getPO(I_M_Product.Table_Name, product_id)
        println(product)
        assertNotNull(product)
        assertEquals(product_id, product.id)
        return product
    }

    fun ensureTaxCategory10Pct() {
        val newCategory = MTaxCategory(0)
        newCategory.name = "T-" + randomString(10)
        newCategory.save()
        _taxCategory = getById(newCategory.id, I_C_TaxCategory.Table_Name)
        assertNotNull(_taxCategory)
        val newTax = MTax(newCategory.name, 10.toBigDecimal(), taxCategory.id)
        newTax.setIsActive(true)
        newTax.setIsDefault(true)
        newTax.save()
        _tax = getById(newTax.id, I_C_Tax.Table_Name)
        assertNotNull(_tax)
    }

    fun createAProduct(name: String, productType: String): I_M_Product {
        val standardProduct = MProduct.get("name = 'Standard'").first()
        val product = MProduct(0)
        product.name = name
        product.searchKey = name
        product.uomId = MUOM.getDefault_UOMId()
        product.productCategoryId = standardProduct.productCategoryId
        product.taxCategoryId = taxCategory.taxCategoryId
        product.productType = productType // I_M_Product.PRODUCTTYPE_Service
        product.save()

        val org = getClientOrganizations(product).first()
        val warehouse = MWarehouse.getForOrg(org.id).first()
        val attributeSetInstance = MAttributeSetInstance.get(0, product.id)

        val inventory = MInventory(warehouse)
        inventory.documentTypeId = getClientDocumentTypes.first { it.docSubTypeInv == "PI" }.id
        inventory.save()

        val inventoryLine = MInventoryLine(
            inventory,
            warehouse.defaultLocator.id,
            product.id,
            attributeSetInstance.id,
            0.toBigDecimal(),
            0.toBigDecimal()
        )
        inventoryLine.chargeId = charge.id
        inventoryLine.qtyInternalUse = 0.toBigDecimal()
        inventoryLine.save()

        assertTrue(
            MCostDetail.createInventory(
                MAcctSchema.getClientAcctSchema(NEW_AD_CLIENT_ID).first(),
                org.id,
                product.id,
                attributeSetInstance.id,
                inventoryLine.id,
                MCostElement.getElements().first().id,
                // amount (costs), quantity
                0.8.toBigDecimal(), 1.toBigDecimal(),
                "initial", null
            )
        )

        inventory.docStatus = MInventory.DOCSTATUS_Completed
        inventory.save()

        inventory.setDocAction(DocAction.STATUS_Completed)
        inventory.save()

        inventory.completeIt()

        return getProductById(product.id)
    }
}
