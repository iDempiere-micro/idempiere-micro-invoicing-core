package org.idempiere.process;

import org.compiere.accounting.MOrder;
import org.compiere.bo.MCurrency;
import org.compiere.bo.MCurrencyKt;
import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.invoicing.MInvoicePaySchedule;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_M_InOutLine;
import org.compiere.order.MOrderPaySchedule;
import org.compiere.orm.PO;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Create (Generate) Invoice from Shipment
 *
 * @author Jorg Janke
 * @version $Id: InOutCreateInvoice.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class InOutCreateInvoice extends SvrProcess {
    /**
     * Shipment
     */
    private int p_M_InOut_ID = 0;
    /**
     * Price List Version
     */
    private int p_M_PriceList_ID = 0;
    /* Document No					*/
    private String p_InvoiceDocumentNo = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("M_PriceList_ID")) p_M_PriceList_ID = iProcessInfoParameter.getParameterAsInt();
            else if (name.equals("InvoiceDocumentNo"))
                p_InvoiceDocumentNo = (String) iProcessInfoParameter.getParameter();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_M_InOut_ID = getRecordId();
    } //	prepare

    /**
     * Create Invoice.
     *
     * @return document no
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "M_InOut_ID="
                            + p_M_InOut_ID
                            + ", M_PriceList_ID="
                            + p_M_PriceList_ID
                            + ", InvoiceDocumentNo="
                            + p_InvoiceDocumentNo);
        if (p_M_InOut_ID == 0) throw new IllegalArgumentException("No Shipment");
        //
        MInOut ship = new MInOut(p_M_InOut_ID);
        if (ship.getId() == 0) throw new IllegalArgumentException("Shipment not found");
        if (!MInOut.DOCSTATUS_Completed.equals(ship.getDocStatus()))
            throw new IllegalArgumentException("Shipment not completed");

        MInvoice invoice = new MInvoice(ship, null);
        // Should not override pricelist for RMA
        if (p_M_PriceList_ID != 0 && ship.getRMAId() == 0)
            invoice.setPriceListId(p_M_PriceList_ID);
        if (p_InvoiceDocumentNo != null && p_InvoiceDocumentNo.length() > 0)
            invoice.setDocumentNo(p_InvoiceDocumentNo);
        if (!invoice.save()) throw new IllegalArgumentException("Cannot save Invoice");
        I_M_InOutLine[] shipLines = ship.getLines(false);
        for (I_M_InOutLine sLine : shipLines) {
            MInvoiceLine line = new MInvoiceLine(invoice);
            line.setShipLine(sLine);
            if (sLine.sameOrderLineUOM()) line.setQtyEntered(sLine.getQtyEntered());
            else line.setQtyEntered(sLine.getMovementQty());
            line.setQtyInvoiced(sLine.getMovementQty());
            if (!line.save()) throw new IllegalArgumentException("Cannot save Invoice Line");
        }

        if (invoice.getOrderId() > 0) {
            MOrder order = new MOrder(invoice.getOrderId());
            invoice.setPaymentRule(order.getPaymentRule());
            invoice.setPaymentTermId(order.getPaymentTermId());
            invoice.saveEx();
            invoice.load(); // refresh from DB
            // copy payment schedule from order if invoice doesn't have a current payment schedule
            MOrderPaySchedule[] opss =
                    MOrderPaySchedule.getOrderPaySchedule(order.getOrderId(), 0);
            MInvoicePaySchedule[] ipss =
                    MInvoicePaySchedule.getInvoicePaySchedule(
                            invoice.getInvoiceId(), 0);
            if (ipss.length == 0 && opss.length > 0) {
                BigDecimal ogt = order.getGrandTotal();
                BigDecimal igt = invoice.getGrandTotal();
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
                    ips.setInvoiceId(invoice.getInvoiceId());
                    ips.setOrgId(ops.getOrgId());
                    ips.setProcessing(ops.isProcessing());
                    ips.setIsActive(ops.isActive());
                    ips.saveEx();
                }
            }
            invoice.validatePaySchedule();
            invoice.saveEx();
        }

        addLog(
                invoice.getInvoiceId(),
                invoice.getDateInvoiced(),
                invoice.getGrandTotal(),
                invoice.getDocumentNo(),
                invoice.getTableId(),
                invoice.getInvoiceId());

        return invoice.getDocumentNo();
    } //	InOutCreateInvoice
} //	InOutCreateInvoice
