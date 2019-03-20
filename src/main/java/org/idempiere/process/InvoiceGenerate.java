package org.idempiere.process;

import org.compiere.accounting.MClient;
import org.compiere.accounting.MOrder;
import org.compiere.accounting.MOrderLine;
import org.compiere.bo.MCurrency;
import org.compiere.crm.MBPartner;
import org.compiere.crm.MLocation;
import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInOutLine;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.invoicing.MInvoicePaySchedule;
import org.compiere.invoicing.MInvoiceSchedule;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.order.MOrderPaySchedule;
import org.compiere.orm.MDocType;
import org.compiere.orm.PO;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.Msg;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Language;

import java.math.BigDecimal;
import java.sql.Savepoint;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.logging.Level;

import static org.compiere.crm.MBaseLocationKt.getBPLocation;

/**
 * Generate Invoices
 *
 * @author Jorg Janke
 * @version $Id: InvoiceGenerate.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class InvoiceGenerate extends SvrProcess {
    /**
     * Manual Selection
     */
    private boolean p_Selection = false;
    /**
     * Date Invoiced
     */
    private Timestamp p_DateInvoiced = null;
    /**
     * Org
     */
    private int p_AD_Org_ID = 0;
    /**
     * BPartner
     */
    private int p_C_BPartner_ID = 0;
    /**
     * Order
     */
    private int p_C_Order_ID = 0;
    /**
     * Consolidate
     */
    private boolean p_ConsolidateDocument = true;
    /**
     * Invoice Document Action
     */
    private String p_docAction = DocAction.Companion.getACTION_Complete();

    /**
     * The current Invoice
     */
    private MInvoice m_invoice = null;
    /**
     * The current Shipment
     */
    private MInOut m_ship = null;
    /**
     * Numner of Invoices
     */
    private int m_created = 0;
    /**
     * Line Number
     */
    private int m_line = 0;
    /**
     * Business Partner
     */
    private MBPartner m_bp = null;
    /**
     * Minimum Amount to Invoice
     */
    private BigDecimal p_MinimumAmt = null;
    /**
     * Minimum Amount to Invoice according to Invoice Schedule
     */
    private BigDecimal p_MinimumAmtInvSched = null;
    /**
     * Per Invoice Savepoint
     */
    private Savepoint m_savepoint = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (para[i].getParameter() == null) ;
            else if (name.equals("Selection")) p_Selection = "Y".equals(para[i].getParameter());
            else if (name.equals("DateInvoiced")) p_DateInvoiced = (Timestamp) para[i].getParameter();
            else if (name.equals("AD_Org_ID")) p_AD_Org_ID = para[i].getParameterAsInt();
            else if (name.equals("C_BPartner_ID")) p_C_BPartner_ID = para[i].getParameterAsInt();
            else if (name.equals("C_Order_ID")) p_C_Order_ID = para[i].getParameterAsInt();
            else if (name.equals("ConsolidateDocument"))
                p_ConsolidateDocument = "Y".equals(para[i].getParameter());
            else if (name.equals("DocAction")) p_docAction = (String) para[i].getParameter();
            else if (name.equals("MinimumAmt")) p_MinimumAmt = para[i].getParameterAsBigDecimal();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }

        //	Login Date
        if (p_DateInvoiced == null) p_DateInvoiced = Env.getContextAsDate(getCtx(), "#Date");
        if (p_DateInvoiced == null) p_DateInvoiced = new Timestamp(System.currentTimeMillis());

        //	DocAction check
        if (!DocAction.Companion.getACTION_Complete().equals(p_docAction))
            p_docAction = DocAction.Companion.getACTION_Prepare();
    } //	prepare

    /**
     * Generate Invoices
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "Selection="
                            + p_Selection
                            + ", DateInvoiced="
                            + p_DateInvoiced
                            + ", AD_Org_ID="
                            + p_AD_Org_ID
                            + ", C_BPartner_ID="
                            + p_C_BPartner_ID
                            + ", C_Order_ID="
                            + p_C_Order_ID
                            + ", DocAction="
                            + p_docAction
                            + ", Consolidate="
                            + p_ConsolidateDocument);
        //
        StringBuilder sql = null;
        if (p_Selection) //	VInvoiceGen
        {
            sql =
                    new StringBuilder("SELECT C_Order.* FROM C_Order, T_Selection ")
                            .append("WHERE C_Order.DocStatus='CO' AND C_Order.IsSOTrx='Y' ")
                            .append("AND C_Order.C_Order_ID = T_Selection.T_Selection_ID ")
                            .append("AND T_Selection.AD_PInstance_ID=? ")
                            .append(
                                    "ORDER BY C_Order.M_Warehouse_ID, C_Order.PriorityRule, C_Order.C_BPartner_ID, C_Order.Bill_Location_ID, C_Order.C_Order_ID");
        } else {
            sql =
                    new StringBuilder("SELECT * FROM C_Order o ")
                            .append("WHERE DocStatus IN('CO','CL') AND IsSOTrx='Y'");
            if (p_AD_Org_ID != 0) sql.append(" AND AD_Org_ID=?");
            if (p_C_BPartner_ID != 0) sql.append(" AND C_BPartner_ID=?");
            if (p_C_Order_ID != 0) sql.append(" AND C_Order_ID=?");
            //
            sql.append(" AND EXISTS (SELECT * FROM C_OrderLine ol ")
                    .append("WHERE o.C_Order_ID=ol.C_Order_ID AND ol.QtyOrdered<>ol.QtyInvoiced) ")
                    .append("AND o.C_DocType_ID IN (SELECT C_DocType_ID FROM C_DocType ")
                    .append("WHERE DocBaseType='SOO' AND DocSubTypeSO NOT IN ('ON','OB','WR')) ")
                    .append(
                            "ORDER BY M_Warehouse_ID, PriorityRule, C_BPartner_ID, Bill_Location_ID, C_Order_ID");
        }
        //	sql += " FOR UPDATE";
        return generate(sql.toString());
    } //	doIt

    /**
     * Generate Shipments
     *
     * @return info
     */
    private String generate(String sql) {
        MOrder[] orders = BaseInvoiceGenerateKt.getOrdersForInvoiceGeneration(getCtx(), sql, getProcessInstanceId(),
                p_Selection, p_AD_Org_ID, p_C_BPartner_ID, p_C_Order_ID);

        for (MOrder order : orders) {
            p_MinimumAmtInvSched = null;
            StringBuilder msgsup =
                    new StringBuilder(Msg.getMsg(getCtx(), "Processing"))
                            .append(" ")
                            .append(order.getDocumentInfo());
            statusUpdate(msgsup.toString());

            //	New Invoice Location
            if (!p_ConsolidateDocument
                    || (m_invoice != null
                    && m_invoice.getBusinessPartnerLocationId() != order.getBusinessPartnerInvoicingLocationId()))
                completeInvoice();
            boolean completeOrder =
                    MOrder.INVOICERULE_AfterOrderDelivered.equals(order.getInvoiceRule());

            //	Schedule After Delivery
            boolean doInvoice = false;
            if (MOrder.INVOICERULE_CustomerScheduleAfterDelivery.equals(order.getInvoiceRule())) {
                m_bp = new MBPartner(getCtx(), order.getBill_BPartnerId());
                if (m_bp.getInvoiceScheduleId() == 0) {
                    log.warning("BPartner has no Schedule - set to After Delivery");
                    order.setInvoiceRule(MOrder.INVOICERULE_AfterDelivery);
                    order.saveEx();
                } else {
                    MInvoiceSchedule is =
                            MInvoiceSchedule.get(getCtx(), m_bp.getInvoiceScheduleId());
                    if (is.canInvoice(order.getDateOrdered())) {
                        if (is.isAmount() && is.getAmt() != null) p_MinimumAmtInvSched = is.getAmt();
                        doInvoice = true;
                    } else {
                        continue;
                    }
                }
            } //	Schedule

            //	After Delivery
            if (doInvoice || MOrder.INVOICERULE_AfterDelivery.equals(order.getInvoiceRule())) {
                MInOut[] shipments = order.getShipments();
                for (int i = 0; i < shipments.length; i++) {
                    MInOut ship = shipments[i];
                    if (!ship.isComplete() // 	ignore incomplete or reversals
                            || ship.getDocStatus().equals(MInOut.DOCSTATUS_Reversed)) continue;
                    MInOutLine[] shipLines = ship.getLines(false);
                    for (int j = 0; j < shipLines.length; j++) {
                        MInOutLine shipLine = shipLines[j];
                        if (!order.isOrderLine(shipLine.getOrderLineId())) continue;
                        if (!shipLine.isInvoiced()) createLine(order, ship, shipLine);
                    }
                    m_line += 1000;
                }
            }
            //	After Order Delivered, Immediate
            else {
                MOrderLine[] oLines = order.getLines(true, null);
                for (int i = 0; i < oLines.length; i++) {
                    MOrderLine oLine = oLines[i];
                    BigDecimal toInvoice = oLine.getQtyOrdered().subtract(oLine.getQtyInvoiced());
                    if (toInvoice.compareTo(Env.ZERO) == 0 && oLine.getProductId() != 0) continue;
                    @SuppressWarnings("unused")
                    BigDecimal notInvoicedShipment =
                            oLine.getQtyDelivered().subtract(oLine.getQtyInvoiced());
                    //
                    boolean fullyDelivered = oLine.getQtyOrdered().compareTo(oLine.getQtyDelivered()) == 0;

                    //	Complete Order
                    if (completeOrder && !fullyDelivered) {
                        if (log.isLoggable(Level.FINE)) log.fine("Failed CompleteOrder - " + oLine);
                        addBufferLog(
                                0,
                                null,
                                null,
                                "Failed CompleteOrder - " + oLine,
                                oLine.getTableId(),
                                oLine.getOrderLineId()); // Elaine 2008/11/25
                        completeOrder = false;
                        break;
                    }
                    //	Immediate
                    else if (MOrder.INVOICERULE_Immediate.equals(order.getInvoiceRule())) {
                        if (log.isLoggable(Level.FINE))
                            log.fine("Immediate - ToInvoice=" + toInvoice + " - " + oLine);
                        BigDecimal qtyEntered = toInvoice;
                        //	Correct UOM for QtyEntered
                        if (oLine.getQtyEntered().compareTo(oLine.getQtyOrdered()) != 0)
                            qtyEntered =
                                    toInvoice
                                            .multiply(oLine.getQtyEntered())
                                            .divide(oLine.getQtyOrdered(), 12, BigDecimal.ROUND_HALF_UP);
                        createLine(order, oLine, toInvoice, qtyEntered);
                    } else if (!completeOrder) {
                        if (log.isLoggable(Level.FINE))
                            log.fine(
                                    "Failed: "
                                            + order.getInvoiceRule()
                                            + " - ToInvoice="
                                            + toInvoice
                                            + " - "
                                            + oLine);
                        addBufferLog(
                                0,
                                null,
                                null,
                                "Failed: " + order.getInvoiceRule() + " - ToInvoice=" + toInvoice + " - " + oLine,
                                oLine.getTableId(),
                                oLine.getOrderLineId());
                    }
                } //	for all order lines
                if (MOrder.INVOICERULE_Immediate.equals(order.getInvoiceRule())) m_line += 1000;
            }

            //	Complete Order successful
            if (completeOrder
                    && MOrder.INVOICERULE_AfterOrderDelivered.equals(order.getInvoiceRule())) {
                MInOut[] shipments = order.getShipments();
                for (int i = 0; i < shipments.length; i++) {
                    MInOut ship = shipments[i];
                    if (!ship.isComplete() // 	ignore incomplete or reversals
                            || ship.getDocStatus().equals(MInOut.DOCSTATUS_Reversed)) continue;
                    MInOutLine[] shipLines = ship.getLines(false);
                    for (int j = 0; j < shipLines.length; j++) {
                        MInOutLine shipLine = shipLines[j];
                        if (!order.isOrderLine(shipLine.getOrderLineId())) continue;
                        if (!shipLine.isInvoiced()) createLine(order, ship, shipLine);
                    }
                    m_line += 1000;
                }
            } //	complete Order
        } //	for all orders

        completeInvoice();
        StringBuilder msgreturn = new StringBuilder("@Created@ = ").append(m_created);
        return msgreturn.toString();
    } //	generate

    /**
     * ************************************************************************ Create Invoice Line
     * from Order Line
     *
     * @param order       order
     * @param orderLine   line
     * @param qtyInvoiced qty
     * @param qtyEntered  qty
     */
    private void createLine(
            MOrder order, MOrderLine orderLine, BigDecimal qtyInvoiced, BigDecimal qtyEntered) {
        if (m_invoice == null) {
            m_invoice = new MInvoice(order, 0, p_DateInvoiced);
            if (!m_invoice.save()) throw new IllegalStateException("Could not create Invoice (o)");
        }
        //
        MInvoiceLine line = new MInvoiceLine(m_invoice);
        line.setOrderLine(orderLine);
        line.setQtyInvoiced(qtyInvoiced);
        line.setQtyEntered(qtyEntered);
        line.setLine(m_line + orderLine.getLine());
        if (!line.save()) throw new IllegalStateException("Could not create Invoice Line (o)");
        if (log.isLoggable(Level.FINE)) log.fine(line.toString());
    } //	createLine

    /**
     * Create Invoice Line from Shipment
     *
     * @param order order
     * @param ship  shipment header
     * @param sLine shipment line
     */
    private void createLine(MOrder order, MInOut ship, MInOutLine sLine) {
        if (m_invoice == null) {
            m_invoice = new MInvoice(order, 0, p_DateInvoiced);
            if (!m_invoice.save()) throw new IllegalStateException("Could not create Invoice (s)");
        }
        //	Create Shipment Comment Line
        if (m_ship == null || m_ship.getInOutId() != ship.getInOutId()) {
            MDocType dt = MDocType.get(getCtx(), ship.getDocumentTypeId());
            if (m_bp == null || m_bp.getBusinessPartnerId() != ship.getBusinessPartnerId())
                m_bp = new MBPartner(getCtx(), ship.getBusinessPartnerId());

            //	Reference: Delivery: 12345 - 12.12.12
            MClient client = MClient.get(getCtx(), order.getClientId());
            String AD_Language = client.getADLanguage();
            if (client.isMultiLingualDocument() && m_bp.getADLanguage() != null)
                AD_Language = m_bp.getADLanguage();
            if (AD_Language == null) AD_Language = Language.getBaseAD_Language();
            java.text.SimpleDateFormat format =
                    DisplayType.getDateFormat(DisplayType.Date, Language.getLanguage(AD_Language));
            StringBuilder reference =
                    new StringBuilder()
                            .append(dt.getPrintName(m_bp.getADLanguage()))
                            .append(": ")
                            .append(ship.getDocumentNo())
                            .append(" - ")
                            .append(format.format(ship.getMovementDate()));
            m_ship = ship;
            //
            MInvoiceLine line = new MInvoiceLine(m_invoice);
            line.setIsDescription(true);
            line.setDescription(reference.toString());
            line.setLine(m_line + sLine.getLine() - 2);
            if (!line.save())
                throw new IllegalStateException("Could not create Invoice Comment Line (sh)");
            //	Optional Ship Address if not Bill Address
            if (order.getBusinessPartnerInvoicingLocationId() != ship.getBusinessPartnerLocationId()) {
                MLocation addr = getBPLocation(getCtx(), ship.getBusinessPartnerLocationId());
                line = new MInvoiceLine(m_invoice);
                line.setIsDescription(true);
                line.setDescription(addr.toString());
                line.setLine(m_line + sLine.getLine() - 1);
                if (!line.save())
                    throw new IllegalStateException("Could not create Invoice Comment Line 2 (sh)");
            }
        }
        //
        MInvoiceLine line = new MInvoiceLine(m_invoice);
        line.setShipLine(sLine);
        if (sLine.sameOrderLineUOM()) line.setQtyEntered(sLine.getQtyEntered());
        else line.setQtyEntered(sLine.getMovementQty());
        line.setQtyInvoiced(sLine.getMovementQty());
        line.setLine(m_line + sLine.getLine());
        if (!line.save()) throw new IllegalStateException("Could not create Invoice Line (s)");
        //	Link
        sLine.setIsInvoiced(true);
        if (!sLine.save()) throw new IllegalStateException("Could not update Shipment Line");

        if (log.isLoggable(Level.FINE)) log.fine(line.toString());
    } //	createLine

    /**
     * Complete Invoice
     */
    private void completeInvoice() {
        if (m_invoice != null) {
            MOrder order = new MOrder(getCtx(), m_invoice.getOrderId());
            if (order != null) {
                m_invoice.setPaymentRule(order.getPaymentRule());
                m_invoice.setPaymentTermId(order.getPaymentTermId());
                m_invoice.saveEx();
                m_invoice.load(); // refresh from DB
                // copy payment schedule from order if invoice doesn't have a current payment schedule
                MOrderPaySchedule[] opss =
                        MOrderPaySchedule.getOrderPaySchedule(
                                getCtx(), order.getOrderId(), 0);
                MInvoicePaySchedule[] ipss =
                        MInvoicePaySchedule.getInvoicePaySchedule(
                                getCtx(), m_invoice.getInvoiceId(), 0);
                if (ipss.length == 0 && opss.length > 0) {
                    BigDecimal ogt = order.getGrandTotal();
                    BigDecimal igt = m_invoice.getGrandTotal();
                    BigDecimal percent = Env.ONE;
                    if (ogt.compareTo(igt) != 0) percent = igt.divide(ogt, 10, BigDecimal.ROUND_HALF_UP);
                    MCurrency cur = MCurrency.get(order.getCtx(), order.getCurrencyId());
                    int scale = cur.getStdPrecision();

                    for (MOrderPaySchedule ops : opss) {
                        MInvoicePaySchedule ips = new MInvoicePaySchedule(getCtx(), 0);
                        PO.copyValues(ops, ips);
                        if (percent != Env.ONE) {
                            BigDecimal propDueAmt = ops.getDueAmt().multiply(percent);
                            if (propDueAmt.scale() > scale)
                                propDueAmt = propDueAmt.setScale(scale, BigDecimal.ROUND_HALF_UP);
                            ips.setDueAmt(propDueAmt);
                        }
                        ips.setInvoiceId(m_invoice.getInvoiceId());
                        ips.setOrgId(ops.getOrgId());
                        ips.setProcessing(ops.isProcessing());
                        ips.setIsActive(ops.isActive());
                        ips.saveEx();
                    }
                    m_invoice.validatePaySchedule();
                    m_invoice.saveEx();
                }
            }

            if ((p_MinimumAmt != null
                    && p_MinimumAmt.signum() != 0
                    && m_invoice.getGrandTotal().compareTo(p_MinimumAmt) < 0)
                    || (p_MinimumAmtInvSched != null
                    && m_invoice.getGrandTotal().compareTo(p_MinimumAmtInvSched) < 0)) {

                // minimum amount not reached
                DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);
                String amt = format.format(m_invoice.getGrandTotal().doubleValue());
                String message =
                        Msg.parseTranslation(
                                getCtx(), "@NotInvoicedAmt@ " + amt + " - " + m_invoice.getBPartner().getName());
                addLog(message);
                throw new AdempiereException("No savepoint");

            } else {

                if (!m_invoice.processIt(p_docAction)) {
                    log.warning("completeInvoice - failed: " + m_invoice);
                    addBufferLog(
                            0,
                            null,
                            null,
                            "completeInvoice - failed: " + m_invoice,
                            m_invoice.getTableId(),
                            m_invoice.getInvoiceId()); // Elaine 2008/11/25
                    throw new IllegalStateException(
                            "Invoice Process Failed: " + m_invoice + " - " + m_invoice.getProcessMsg());
                }
                m_invoice.saveEx();

                String message =
                        Msg.parseTranslation(getCtx(), "@InvoiceProcessed@ " + m_invoice.getDocumentNo());
                addBufferLog(
                        m_invoice.getInvoiceId(),
                        m_invoice.getDateInvoiced(),
                        null,
                        message,
                        m_invoice.getTableId(),
                        m_invoice.getInvoiceId());
                m_created++;
            }
        }
        m_invoice = null;
        m_ship = null;
        m_line = 0;
    } //	completeInvoice
} //	InvoiceGenerate
