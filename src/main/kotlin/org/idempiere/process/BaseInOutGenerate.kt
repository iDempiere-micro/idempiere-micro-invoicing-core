package org.idempiere.process

import kotliquery.Row
import org.compiere.accounting.MOrder
import org.compiere.accounting.MStorageOnHand
import org.compiere.invoicing.MInOut
import org.compiere.invoicing.MInOutLine
import org.compiere.model.I_C_OrderLine
import org.compiere.order.OrderConstants.DELIVERYRULE_Availability
import org.compiere.order.OrderConstants.DELIVERYRULE_CompleteLine
import org.compiere.order.OrderConstants.DELIVERYRULE_CompleteOrder
import org.compiere.order.OrderConstants.DELIVERYRULE_Force
import org.compiere.order.OrderConstants.DELIVERYRULE_Manual
import org.compiere.orm.MClient.Companion.MMPOLICY_FiFo
import org.compiere.process.SvrProcess
import org.idempiere.common.util.AdempiereUserError
import org.idempiere.common.util.Env
import software.hsharp.core.util.DB
import software.hsharp.core.util.convertDate
import software.hsharp.core.util.queryOf
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.logging.Level

/**
 * Base for Shipments Generation. Manual or Automatic
 *
 */
abstract class BaseInOutGenerate : SvrProcess() {
    /**
     * Warehouse
     */
    protected var p_M_Warehouse_ID = 0

    /**
     * Manual Selection
     */
    protected var p_Selection = false

    /**
     * Promise Date
     */
    protected var p_DatePromised: Timestamp? = null

    /**
     * BPartner
     */
    protected var p_C_BPartner_ID = 0

    /**
     * Consolidate
     */
    protected var p_ConsolidateDocument = true

    /**
     * The current Shipment
     */
    protected var m_shipment: MInOut? = null

    /**
     * Movement Date
     */
    protected var m_movementDate: Timestamp? = null

    protected abstract fun completeShipment()

    /**
     * Include Orders w. unconfirmed Shipments
     */
    protected var p_IsUnconfirmedInOut = false

    protected abstract fun createLine(
        order: MOrder,
        orderLine: I_C_OrderLine,
        qty: BigDecimal,
        storages: Array<MStorageOnHand>?,
        force: Boolean
    )

    protected abstract fun getStorages(
        M_Warehouse_ID: Int,
        M_Product_ID: Int,
        M_AttributeSetInstance_ID: Int,
        minGuaranteeDate: Timestamp?,
        FiFo: Boolean
    ): Array<MStorageOnHand>

    /**
     * Line Number
     */
    protected var m_line = 0

    /**
     * Number of Shipments being created
     */
    protected var m_created = 0

    /*
     * Generate Shipments
     *
     * @param pstmt Order Query
     * @return info
     */
    private fun generate(sql: String, parameters: List<Any?>): String {
        fun load(row: Row): Int {
            val order = MOrder(row)

            val shipment = m_shipment

            // 	New Header different Shipper, Shipment Location
            if (!p_ConsolidateDocument || shipment != null && (shipment.businessPartnerLocationId != order.businessPartnerLocationId || shipment.shipperId != order.shipperId))
                completeShipment()
            if (log.isLoggable(Level.FINE))
                log.fine("check: " + order + " - DeliveryRule=" + order.deliveryRule)
            //
            val minGuaranteeDate = m_movementDate
            var completeOrder = DELIVERYRULE_CompleteOrder == order.deliveryRule
            // 	OrderLine WHERE
            val where = StringBuilder(" AND M_Warehouse_ID=").append(p_M_Warehouse_ID)
            if (p_DatePromised != null)
                where
                    .append(" AND (TRUNC(DatePromised)<=")
                    .append(convertDate(p_DatePromised, true))
                    .append(" OR DatePromised IS NULL)")
            // 	Exclude Auto Delivery if not Force
            if (DELIVERYRULE_Force != order.deliveryRule)
                where
                    .append(" AND (C_OrderLine.M_Product_ID IS NULL")
                    .append(" OR EXISTS (SELECT * FROM M_Product p ")
                    .append("WHERE C_OrderLine.M_Product_ID=p.M_Product_ID")
                    .append(" AND IsExcludeAutoDelivery='N'))")
            // 	Exclude Unconfirmed
            if (!p_IsUnconfirmedInOut)
                where
                    .append(" AND NOT EXISTS (SELECT * FROM M_InOutLine iol")
                    .append(" INNER JOIN M_InOut io ON (iol.M_InOut_ID=io.M_InOut_ID) ")
                    .append(
                        "WHERE iol.C_OrderLine_ID=C_OrderLine.C_OrderLine_ID AND io.DocStatus IN ('DR','IN','IP','WC'))"
                    )
            // 	Deadlock Prevention - Order by M_Product_ID
            val lines = order.getLines(where.toString(), "C_BPartner_Location_ID, M_Product_ID")
            for (i in lines.indices) {
                val line = lines[i]
                if (line.warehouseId != p_M_Warehouse_ID) continue
                if (log.isLoggable(Level.FINE)) log.fine("check: $line")
                var onHand = Env.ZERO
                var toDeliver = line.qtyOrdered.subtract(line.qtyDelivered)
                val product = line.product
                // 	Nothing to Deliver
                if (product != null && toDeliver.signum() == 0) continue

                // or it's a charge - Bug#: 1603966
                if (line.chargeId != 0 && toDeliver.signum() == 0) continue

                // 	Check / adjust for confirmations
                var unconfirmedShippedQty = Env.ZERO
                if (p_IsUnconfirmedInOut && product != null && toDeliver.signum() != 0) {
                    val where2 =
                        "EXISTS (SELECT * FROM M_InOut io WHERE io.M_InOut_ID=M_InOutLine.M_InOut_ID AND io.DocStatus IN ('DR','IN','IP','WC'))"
                    val iols = MInOutLine.getOfOrderLine(line.orderLineId, where2)
                    for (j in iols.indices)
                        unconfirmedShippedQty = unconfirmedShippedQty.add(iols[j].movementQty)
                    val logInfo = StringBuilder("Unconfirmed Qty=")
                        .append(unconfirmedShippedQty)
                        .append(" - ToDeliver=")
                        .append(toDeliver)
                        .append("->")
                    toDeliver = toDeliver.subtract(unconfirmedShippedQty)
                    logInfo.append(toDeliver)
                    if (toDeliver.signum() < 0) {
                        toDeliver = Env.ZERO
                        logInfo.append(" (set to 0)")
                    }
                    // 	Adjust On Hand
                    onHand = onHand.subtract(unconfirmedShippedQty)
                    if (log.isLoggable(Level.FINE)) log.fine(logInfo.toString())
                }

                // 	Comments & lines w/o product & services
                if ((product == null || !product.isStocked) && (line.qtyOrdered.signum() == 0 || // 	comments
                            toDeliver.signum() != 0)
                )
                // 	lines w/o product
                {
                    if (DELIVERYRULE_CompleteOrder != order.deliveryRule)
                    // 	printed later
                        createLine(order, line, toDeliver, null, false)
                    continue
                }

                // 	Stored Product
                val MMPolicy = product.mmPolicy

                val storages = getStorages(
                    line.warehouseId,
                    line.productId,
                    line.attributeSetInstanceId,
                    minGuaranteeDate,
                    MMPOLICY_FiFo == MMPolicy
                )

                for (j in storages.indices) {
                    val storage = storages[j]
                    onHand = onHand.add(storage.qtyOnHand)
                }
                val fullLine = onHand >= toDeliver || toDeliver.signum() < 0

                // 	Complete Order
                if (completeOrder && !fullLine) {
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            "Failed CompleteOrder - OnHand=" +
                                    onHand +
                                    " (Unconfirmed=" +
                                    unconfirmedShippedQty +
                                    "), ToDeliver=" +
                                    toDeliver +
                                    " - " +
                                    line
                        )
                    completeOrder = false
                    break
                } else if (fullLine && DELIVERYRULE_CompleteLine == order.deliveryRule) {
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            ("CompleteLine - OnHand=" +
                                    onHand +
                                    " (Unconfirmed=" +
                                    unconfirmedShippedQty +
                                    ", ToDeliver=" +
                                    toDeliver +
                                    " - " +
                                    line)
                        )
                    //
                    createLine(order, line, toDeliver, storages, false)
                } else if ((DELIVERYRULE_Availability == order.deliveryRule && (onHand.signum() > 0 || toDeliver.signum() < 0))) {
                    var deliver = toDeliver
                    if (deliver > onHand) deliver = onHand
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            ("Available - OnHand=" +
                                    onHand +
                                    " (Unconfirmed=" +
                                    unconfirmedShippedQty +
                                    "), ToDeliver=" +
                                    toDeliver +
                                    ", Delivering=" +
                                    deliver +
                                    " - " +
                                    line)
                        )
                    //
                    createLine(order, line, deliver, storages, false)
                } else if (DELIVERYRULE_Force == order.deliveryRule) {
                    val deliver = toDeliver
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            ("Force - OnHand=" +
                                    onHand +
                                    " (Unconfirmed=" +
                                    unconfirmedShippedQty +
                                    "), ToDeliver=" +
                                    toDeliver +
                                    ", Delivering=" +
                                    deliver +
                                    " - " +
                                    line)
                        )
                    //
                    createLine(order, line, deliver, storages, true)
                } else if (DELIVERYRULE_Manual == order.deliveryRule) {
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            ("Manual - OnHand=" +
                                    onHand +
                                    " (Unconfirmed=" +
                                    unconfirmedShippedQty +
                                    ") - " +
                                    line)
                        )
                } else {
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                            ("Failed: " +
                                    order.deliveryRule +
                                    " - OnHand=" +
                                    onHand +
                                    " (Unconfirmed=" +
                                    unconfirmedShippedQty +
                                    "), ToDeliver=" +
                                    toDeliver +
                                    " - " +
                                    line)
                        )
                } // 	Manual
                // 	Force
                // 	Availability
                // 	Complete Line
            } // 	for all order lines

            // 	Complete Order successful
            if (completeOrder && DELIVERYRULE_CompleteOrder == order.deliveryRule) {
                for (i in lines.indices) {
                    val line = lines[i]
                    if (line.warehouseId != p_M_Warehouse_ID) continue
                    val product = line.product
                    val toDeliver = line.qtyOrdered.subtract(line.qtyDelivered)
                    //
                    var storages: Array<MStorageOnHand>? = null
                    if (product != null && product.isStocked) {
                        val MMPolicy = product.mmPolicy
                        storages = getStorages(
                            line.warehouseId,
                            line.productId,
                            line.attributeSetInstanceId,
                            minGuaranteeDate,
                            MMPOLICY_FiFo == MMPolicy
                        )
                    }
                    //
                    createLine(order, line, toDeliver, storages, false)
                }
            }
            m_line += 1000

            return 0
        }

        val query = queryOf(sql, parameters).map { row -> load(row) }.asList
        DB.current.run(query).min()

        completeShipment()
        val msgreturn = StringBuilder("@Created@ = ").append(m_created)
        return msgreturn.toString()
    } // 	generate

    /**
     * Generate Shipments
     *
     * @return info
     * @throws Exception
     */
    override fun doIt(): String {
        if (p_M_Warehouse_ID == 0) throw AdempiereUserError("@NotFound@ @M_Warehouse_ID@")
        val sql: StringBuffer

        if (p_Selection)
        // 	VInOutGen
        {
            sql = StringBuffer("SELECT C_Order.* FROM C_Order, T_Selection ")
                .append(
                    "WHERE C_Order.DocStatus='CO' AND C_Order.IsSOTrx='Y' AND C_Order.AD_Client_ID=? "
                )
                .append("AND C_Order.C_Order_ID = T_Selection.T_Selection_ID ")
                .append("AND T_Selection.AD_PInstance_ID=? ")
        } else {
            sql = StringBuffer("SELECT * FROM C_Order o ")
                .append("WHERE DocStatus='CO' AND IsSOTrx='Y'")
                // 	No Offer,POS
                .append(" AND o.C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType ")
                .append("WHERE DocBaseType='SOO' AND DocSubTypeSO NOT IN ('ON','OB','WR'))")
                .append("	AND o.IsDropShip='N'")
                // 	No Manual
                .append(" AND o.DeliveryRule<>'M'")
                // 	Open Order Lines with Warehouse
                .append(" AND EXISTS (SELECT * FROM C_OrderLine ol ")
                .append("WHERE ol.M_Warehouse_ID=?") // 	#1
            if (p_DatePromised != null) sql.append(" AND TRUNC(ol.DatePromised)<=?") // 	#2
            sql.append(" AND o.C_Order_ID=ol.C_Order_ID AND ol.QtyOrdered<>ol.QtyDelivered)")
            //
            if (p_C_BPartner_ID != 0) sql.append(" AND o.C_BPartner_ID=?") // 	#3
        }
        sql.append(
            " ORDER BY M_Warehouse_ID, PriorityRule, M_Shipper_ID, C_BPartner_ID, C_BPartner_Location_ID, C_Order_ID"
        )
        // 	m_sql += " FOR UPDATE";

        val parameters =
            if (p_Selection) {
                listOf(Env.getClientId(), processInstanceId)
            } else {
                listOf(p_M_Warehouse_ID) +
                        (if (p_DatePromised != null) listOf(p_DatePromised) else emptyList()) +
                        (if (p_C_BPartner_ID != 0) listOf(p_C_BPartner_ID) else emptyList())
            }

        return generate(sql.toString(), parameters)
    } // 	doIt
}