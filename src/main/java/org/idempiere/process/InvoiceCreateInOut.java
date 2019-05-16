package org.idempiere.process;

import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInOutLine;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.X_C_Invoice;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.process.SvrProcess;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.exceptions.FillMandatoryException;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Create (Generate) Shipment from Invoice
 *
 * @author Jorg Janke
 * @author Teo Sarca, www.arhipac.ro
 * <li>FR [ 1895317 ] InvoiceCreateInOut: you can create many receipts
 * @version $Id: InvoiceCreateInOut.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class InvoiceCreateInOut extends SvrProcess {
    public static final String PARAM_M_Warehouse_ID = MInOut.COLUMNNAME_M_Warehouse_ID;

    /**
     * Warehouse
     */
    private int p_M_Warehouse_ID = 0;
    /**
     * Invoice
     */
    private int p_C_Invoice_ID = 0;
    /**
     * Receipt
     */
    private MInOut m_inout = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        for (IProcessInfoParameter para : getParameter()) {
            String name = para.getParameterName();
            if (para.getParameter() != null) {
                if (name.equals(PARAM_M_Warehouse_ID)) p_M_Warehouse_ID = para.getParameterAsInt();
                else log.log(Level.SEVERE, "Unknown Parameter: " + name);
            }
        }
        p_C_Invoice_ID = getRecordId();
    } //	prepare

    /**
     * Create Shipment
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info("C_Invoice_ID=" + p_C_Invoice_ID + ", M_Warehouse_ID=" + p_M_Warehouse_ID);
        if (p_C_Invoice_ID <= 0) throw new FillMandatoryException("C_Invoice_ID");
        if (p_M_Warehouse_ID == 0) throw new FillMandatoryException(PARAM_M_Warehouse_ID);
        //
        MInvoice invoice = new MInvoice(null, p_C_Invoice_ID);
        if (invoice.getId() <= 0) throw new AdempiereException("@NotFound@ @C_Invoice_ID@");
        if (!X_C_Invoice.DOCSTATUS_Completed.equals(invoice.getDocStatus()))
            throw new AdempiereException("@InvoiceCreateDocNotCompleted@");
        //
        for (I_C_InvoiceLine invoiceLine : invoice.getLines(false)) {
            createLine(invoice, invoiceLine);
        }
        if (m_inout == null) throw new InvoiceFullyMatchedException();

        addLog(
                m_inout.getInOutId(),
                m_inout.getMovementDate(),
                null,
                m_inout.getDocumentNo(),
                m_inout.getTableId(),
                m_inout.getInOutId());
        //
        return m_inout.getDocumentNo();
    } //	doIt

    /**
     * Create Shipment/Receipt header
     *
     * @param invoice
     * @return Shipment/Receipt header
     */
    private MInOut getCreateHeader(MInvoice invoice) {
        if (m_inout != null) return m_inout;
        m_inout = new MInOut(invoice, 0, null, p_M_Warehouse_ID);
        m_inout.saveEx();
        return m_inout;
    }

    /**
     * Create shipment/receipt line
     *
     * @param invoice
     * @param invoiceLine
     * @return shipment/receipt line
     */
    private MInOutLine createLine(MInvoice invoice, I_C_InvoiceLine invoiceLine) {
        BigDecimal qtyMatched = invoiceLine.getMatchedQty();
        BigDecimal qtyInvoiced = invoiceLine.getQtyInvoiced();
        BigDecimal qtyNotMatched = qtyInvoiced.subtract(qtyMatched);
        // If is fully matched don't create anything
        if (qtyNotMatched.signum() == 0) {
            return null;
        }
        MInOut inout = getCreateHeader(invoice);
        MInOutLine sLine = new MInOutLine(inout);
        sLine.setInvoiceLine(
                invoiceLine,
                0, //	Locator
                invoice.isSOTrx() ? qtyNotMatched : Env.ZERO);
        sLine.setQtyEntered(qtyNotMatched);
        sLine.setMovementQty(qtyNotMatched);
        if (invoice.isCreditMemo()) {
            sLine.setQtyEntered(sLine.getQtyEntered().negate());
            sLine.setMovementQty(sLine.getMovementQty().negate());
        }
        sLine.saveEx();
        //
        invoiceLine.setInOutLineId(sLine.getInOutLineId());
        invoiceLine.saveEx();
        //
        return sLine;
    }
} //	InvoiceCreateInOut
