package org.idempiere.process;

import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MOrder;
import org.compiere.bo.MCurrency;
import org.compiere.bo.MCurrencyKt;
import org.compiere.crm.MBPartner;
import org.compiere.crm.MLocation;
import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.invoicing.MInvoicePaySchedule;
import org.compiere.invoicing.MInvoiceSchedule;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_InOut;
import org.compiere.model.I_M_InOutLine;
import org.compiere.order.MOrderPaySchedule;
import org.compiere.order.OrderConstants;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.PO;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Language;

import java.math.BigDecimal;
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
    private I_M_InOut m_ship = null;
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
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            switch (name) {
                case "Selection":
                    p_Selection = "Y".equals(iProcessInfoParameter.getParameter());
                    break;
                case "DateInvoiced":
                    p_DateInvoiced = (Timestamp) iProcessInfoParameter.getParameter();
                    break;
                case "AD_Org_ID":
                    p_AD_Org_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "C_BPartner_ID":
                    p_C_BPartner_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "C_Order_ID":
                    p_C_Order_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "ConsolidateDocument":
                    p_ConsolidateDocument = "Y".equals(iProcessInfoParameter.getParameter());
                    break;
                case "DocAction":
                    p_docAction = (String) iProcessInfoParameter.getParameter();
                    break;
                case "MinimumAmt":
                    p_MinimumAmt = iProcessInfoParameter.getParameterAsBigDecimal();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }
        }

        //	Login Date
        if (p_DateInvoiced == null) p_DateInvoiced = Env.getContextAsDate();

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
        StringBuilder sql;
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
        MOrder[] orders = BaseInvoiceGenerateKt.getOrdersForInvoiceGeneration(sql, getProcessInstanceId(),
                p_Selection, p_AD_Org_ID, p_C_BPartner_ID, p_C_Order_ID);

        for (MOrder order : orders) {
            p_MinimumAmtInvSched = null;
            String msgsup = MsgKt.getMsg("Processing") +
                    " " +
                    order.getDocumentInfo();
            statusUpdate(msgsup);

            //	New Invoice Location
            if (!p_ConsolidateDocument
                    || (m_invoice != null
                    && m_invoice.getBusinessPartnerLocationId() != order.getBusinessPartnerInvoicingLocationId()))
                completeInvoice();
            boolean completeOrder =
                    OrderConstants.INVOICERULE_AfterOrderDelivered.equals(order.getInvoiceRule());

            //	Schedule After Delivery
            boolean doInvoice = false;
            if (OrderConstants.INVOICERULE_CustomerScheduleAfterDelivery.equals(order.getInvoiceRule())) {
                m_bp = new MBPartner(order.getInvoiceBusinessPartnerId());
                if (m_bp.getInvoiceScheduleId() == 0) {
                    log.warning("BPartner has no Schedule - set to After Delivery");
                    order.setInvoiceRule(OrderConstants.INVOICERULE_AfterDelivery);
                    order.saveEx();
                } else {
                    MInvoiceSchedule is =
                            MInvoiceSchedule.get(m_bp.getInvoiceScheduleId());
                    if (is.canInvoice(order.getDateOrdered())) {
                        if (is.isAmount() && is.getAmt() != null) p_MinimumAmtInvSched = is.getAmt();
                        doInvoice = true;
                    } else {
                        continue;
                    }
                }
            } //	Schedule

            //	After Delivery
            if (doInvoice || OrderConstants.INVOICERULE_AfterDelivery.equals(order.getInvoiceRule())) {
                I_M_InOut[] shipments = order.getShipments();
                for (I_M_InOut ship : shipments) {
                    createLines(order, ship);
                }
            }
            //	After Order Delivered, Immediate
            else {
                I_C_OrderLine[] oLines = order.getLines(true, null).toArray(new I_C_OrderLine[0]);
                for (I_C_OrderLine oLine : oLines) {
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
                    else if (OrderConstants.INVOICERULE_Immediate.equals(order.getInvoiceRule())) {
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
                if (OrderConstants.INVOICERULE_Immediate.equals(order.getInvoiceRule())) m_line += 1000;
            }

            //	Complete Order successful
            if (completeOrder
                    && OrderConstants.INVOICERULE_AfterOrderDelivered.equals(order.getInvoiceRule())) {
                I_M_InOut[] shipments = order.getShipments();
                for (I_M_InOut ship : shipments) {
                    createLines(order, ship);
                }
            } //	complete Order
        } //	for all orders

        completeInvoice();
        return "@Created@ = " + m_created;
    } //	generate

    private void createLines(MOrder order, I_M_InOut ship) {
        if (!ship.isComplete() // 	ignore incomplete or reversals
                || ship.getDocStatus().equals(MInOut.DOCSTATUS_Reversed)) return;
        I_M_InOutLine[] shipLines = ship.getLines(false);
        for (I_M_InOutLine shipLine : shipLines) {
            if (!order.isOrderLine(shipLine.getOrderLineId())) continue;
            if (!shipLine.isInvoiced()) createLine(order, ship, shipLine);
        }
        m_line += 1000;
    }

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
            MOrder order, I_C_OrderLine orderLine, BigDecimal qtyInvoiced, BigDecimal qtyEntered) {
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
    private void createLine(MOrder order, I_M_InOut ship, I_M_InOutLine sLine) {
        if (m_invoice == null) {
            m_invoice = new MInvoice(order, 0, p_DateInvoiced);
            if (!m_invoice.save()) throw new IllegalStateException("Could not create Invoice (s)");
        }
        //	Create Shipment Comment Line
        if (m_ship == null || m_ship.getInOutId() != ship.getInOutId()) {
            MDocType dt = MDocTypeKt.getDocumentType(ship.getDocumentTypeId());
            if (m_bp == null || m_bp.getBusinessPartnerId() != ship.getBusinessPartnerId())
                m_bp = new MBPartner(ship.getBusinessPartnerId());

            //	Reference: Delivery: 12345 - 12.12.12
            ClientWithAccounting client = MClientKt.getClientWithAccounting(order.getClientId());
            String AD_Language = client.getADLanguage();
            if (client.isMultiLingualDocument() && m_bp.getADLanguage() != null)
                AD_Language = m_bp.getADLanguage();
            if (AD_Language == null) AD_Language = Language.getBaseLanguageCode();
            java.text.SimpleDateFormat format =
                    DisplayType.getDateFormat(DisplayType.Date, Language.getLanguage(AD_Language));
            m_ship = ship;
            //
            MInvoiceLine line = new MInvoiceLine(m_invoice);
            line.setIsDescription(true);
            String reference = dt.getPrintName(m_bp.getADLanguage()) +
                    ": " +
                    ship.getDocumentNo() +
                    " - " +
                    format.format(ship.getMovementDate());
            line.setDescription(reference);
            line.setLine(m_line + sLine.getLine() - 2);
            if (!line.save())
                throw new IllegalStateException("Could not create Invoice Comment Line (sh)");
            //	Optional Ship Address if not Bill Address
            if (order.getBusinessPartnerInvoicingLocationId() != ship.getBusinessPartnerLocationId()) {
                MLocation addr = getBPLocation(ship.getBusinessPartnerLocationId());
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
            MOrder order = new MOrder(m_invoice.getOrderId());
            m_invoice.setPaymentRule(order.getPaymentRule());
            m_invoice.setPaymentTermId(order.getPaymentTermId());
            m_invoice.saveEx();
            m_invoice.load(); // refresh from DB
            // copy payment schedule from order if invoice doesn't have a current payment schedule
            MOrderPaySchedule[] opss =
                    MOrderPaySchedule.getOrderPaySchedule(
                            order.getOrderId(), 0);
            MInvoicePaySchedule[] ipss =
                    MInvoicePaySchedule.getInvoicePaySchedule(
                            m_invoice.getInvoiceId(), 0);
            if (ipss.length == 0 && opss.length > 0) {
                BigDecimal ogt = order.getGrandTotal();
                BigDecimal igt = m_invoice.getGrandTotal();
                BigDecimal percent = Env.ONE;
                if (ogt.compareTo(igt) != 0) percent = igt.divide(ogt, 10, BigDecimal.ROUND_HALF_UP);
                MCurrency cur = MCurrencyKt.getCurrency(order.getCurrencyId());
                int scale = cur.getStdPrecision();

                for (MOrderPaySchedule ops : opss) {
                    MInvoicePaySchedule ips = new MInvoicePaySchedule(0);
                    PO.copyValues(ops, ips);
                    if (!percent.equals(Env.ONE)) {
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

            if ((p_MinimumAmt != null
                    && p_MinimumAmt.signum() != 0
                    && m_invoice.getGrandTotal().compareTo(p_MinimumAmt) < 0)
                    || (p_MinimumAmtInvSched != null
                    && m_invoice.getGrandTotal().compareTo(p_MinimumAmtInvSched) < 0)) {

                // minimum amount not reached
                DecimalFormat format = DisplayType.getNumberFormat(DisplayType.Amount);
                String amt = format.format(m_invoice.getGrandTotal().doubleValue());
                String message =
                        MsgKt.parseTranslation(
                                "@NotInvoicedAmt@ " + amt + " - " + m_invoice.getBPartner().getName());
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
                        MsgKt.parseTranslation("@InvoiceProcessed@ " + m_invoice.getDocumentNo());
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
