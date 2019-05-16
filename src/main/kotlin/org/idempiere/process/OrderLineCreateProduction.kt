package org.idempiere.process

import org.compiere.accounting.MOrder
import org.compiere.accounting.MOrderLine
import org.compiere.accounting.MProduct
import org.compiere.accounting.MWarehouse
import org.compiere.model.I_C_Order
import org.compiere.model.I_M_Production
import org.compiere.order.OrderConstants.DOCSTATUS_Completed
import org.compiere.orm.MDocType
import org.compiere.process.SvrProcess
import org.compiere.production.MProduction
import org.compiere.util.parseTranslation
import org.idempiere.common.util.Env
import software.hsharp.core.util.getSQLValueString
import java.sql.Timestamp
import java.util.logging.Level

/**
 * Create (Generate) Invoice from Shipment
 *
 * @author Jorg Janke
 * @version $Id: OrderLineCreateProduction.java,v 1.1 2007/07/23 05:34:35 mfuggle Exp $
 */
class OrderLineCreateProduction(
    var p_C_OrderLine_ID: Int = 0,
    var p_MovementDate: Timestamp? = null,
    var ignorePrevProduction: Boolean = false
) : SvrProcess() {
    /**	Shipment					 */

    /**
     * Prepare - e.g., get Parameters.
     */
    override fun prepare() {
        val para = parameter
        for (i in para.indices) {
            val name = para[i].parameterName
            if (para[i].parameter == null)

                if (name == "MovementDate")
                    p_MovementDate = para[i].parameter as Timestamp
                else if (name == "IgnorePrevProduction")
                    ignorePrevProduction = "Y" == para[i].parameter
                else
                    log.log(Level.SEVERE, "Unknown Parameter: $name")
        }

        if (p_MovementDate == null)
            p_MovementDate = Env.getContextAsDate()
        if (p_MovementDate == null)
            p_MovementDate = Timestamp(System.currentTimeMillis())

        if (p_C_OrderLine_ID == 0) p_C_OrderLine_ID = recordId
    } // 	prepare

    /**
     * Create Production Header and Plan for single ordered product
     *
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun doIt(): String {
        if (log.isLoggable(Level.INFO)) log.info("C_OrderLine_ID=$p_C_OrderLine_ID")
        if (p_C_OrderLine_ID == 0)
            throw IllegalArgumentException("No OrderLine")
        //
        val line = MOrderLine(p_C_OrderLine_ID)
        if (line.id == 0)
            throw IllegalArgumentException("Order line not found")
        val order: I_C_Order = MOrder(line.orderId)
        if (DOCSTATUS_Completed != order.docStatus)
            throw IllegalArgumentException("Order not completed")

        val doc = MDocType(order.documentTypeId)

        if (line.qtyOrdered.subtract(line.qtyDelivered).compareTo(Env.ZERO) <= 0) {
            if (doc.docSubTypeSO != "ON")
            // Consignment and stock orders both have subtype of ON
            {
                return "Ordered quantity already shipped"
            }
        }

        // If we don't ignore previous production, and there has been a previous one,
        // throw an exception
        if (!ignorePrevProduction) {
            val docNo = getSQLValueString(
                "SELECT max(DocumentNo) " + "FROM M_Production WHERE C_OrderLine_ID = ?",
                p_C_OrderLine_ID
            )
            if (docNo != null) {
                throw IllegalArgumentException("Production has already been created: $docNo")
            }
        }

        val production: I_M_Production = MProduction(line)
        val product = MProduct(line.productId)

        production.productId = line.productId
        production.setProductionQty(line.qtyOrdered.subtract(line.qtyDelivered))
        production.setDatePromised(line.datePromised)
        if (product.locatorId > 0)
            production.setLocatorId(product.locatorId)
        production.setOrderLineId(p_C_OrderLine_ID)

        var locator = product.locatorId
        if (locator == 0)
            locator = MWarehouse.get(line.warehouseId).defaultLocator.id
        production.setLocatorId(locator)

        if (line.businessPartnerId > 0) {
            production.setBusinessPartnerId(order.businessPartnerId)
        }

        if (line.projectId > 0) {
            production.setProjectId(line.projectId)
        } else {
            production.setProjectId(order.projectId)
        }

        if (line.campaignId > 0) {
            production.setCampaignId(line.campaignId)
        } else {
            production.setCampaignId(order.campaignId)
        }

        if (line.businessActivityId > 0) {
            production.setBusinessActivityId(line.businessActivityId)
        } else {
            production.setBusinessActivityId(order.businessActivityId)
        }

        if (line.user1Id > 0) {
            production.setUser1Id(line.user1Id)
        } else {
            production.setUser1Id(order.user1Id)
        }

        if (line.user2Id > 0) {
            production.setUser2Id(line.user2Id)
        } else {
            production.setUser2Id(order.user2Id)
        }

        if (line.transactionOrganizationId > 0) {
            production.setTransactionOrganizationId(line.transactionOrganizationId)
        } else {
            production.setTransactionOrganizationId(order.transactionOrganizationId)
        }

        production.saveEx()

        production.createLines(false)
        production.isCreated = "Y"
        production.saveEx()

        val msg = parseTranslation("@M_Production_ID@ @Created@ " + production.getDocumentNo())
        addLog(production.productionId, null, null, msg, MProduction.Table_ID, production.productionId)
        return "@OK@"
    } // 	OrderLineCreateShipment
} // 	OrderLineCreateShipment
