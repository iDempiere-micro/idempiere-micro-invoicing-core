package org.idempiere.process;

import org.compiere.crm.MBPartner;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_R_RequestUpdate;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.production.MRequest;
import org.compiere.production.MRequestType;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.AdempiereSystemError;

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Create Invoices for Requests
 *
 * @author Jorg Janke
 * @version $Id: RequestInvoice.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class RequestInvoice extends SvrProcess {
    /**
     * Request Type
     */
    private int p_R_RequestType_ID = 0;
    /**
     * Request Group (opt)
     */
    private int p_R_Group_ID = 0;
    /**
     * Request Categpry (opt)
     */
    private int p_R_Category_ID = 0;
    /**
     * Business Partner (opt)
     */
    private int p_C_BPartner_ID = 0;
    /**
     * Default product
     */
    private int p_M_Product_ID = 0;

    /**
     * The invoice
     */
    private MInvoice m_invoice = null;
    /**
     * Line Count
     */
    private int m_linecount = 0;

    /**
     * Prepare
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            if (iProcessInfoParameter.getParameter() != null) {
                switch (name) {
                    case "R_RequestType_ID":
                        p_R_RequestType_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "R_Group_ID":
                        p_R_Group_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "R_Category_ID":
                        p_R_Category_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "C_BPartner_ID":
                        p_C_BPartner_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "M_Product_ID":
                        p_M_Product_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    default:
                        log.log(Level.SEVERE, "Unknown Parameter: " + name);
                        break;
                }
            }
        }
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "R_RequestType_ID="
                            + p_R_RequestType_ID
                            + ", R_Group_ID="
                            + p_R_Group_ID
                            + ", R_Category_ID="
                            + p_R_Category_ID
                            + ", C_BPartner_ID="
                            + p_C_BPartner_ID
                            + ", p_M_Product_ID="
                            + p_M_Product_ID);

        MRequestType type = MRequestType.get(p_R_RequestType_ID);
        if (type.getId() == 0)
            throw new AdempiereSystemError("@R_RequestType_ID@ @NotFound@ " + p_R_RequestType_ID);
        if (!type.isInvoiced()) throw new AdempiereSystemError("@R_RequestType_ID@ <> @IsInvoiced@");

        StringBuilder sql =
                new StringBuilder("SELECT * FROM R_Request r")
                        .append(" INNER JOIN R_Status s ON (r.R_Status_ID=s.R_Status_ID) ")
                        .append("WHERE s.IsClosed='Y'")
                        .append(" AND r.R_RequestType_ID=?");
        // globalqss -- avoid double invoicing
        // + " AND EXISTS (SELECT 1 FROM R_RequestUpdate ru " +
        //		"WHERE ru.R_Request_ID=r.R_Request_ID AND NVL(C_InvoiceLine_ID,0)=0";
        if (p_R_Group_ID != 0) sql.append(" AND r.R_Group_ID=?");
        if (p_R_Category_ID != 0) sql.append(" AND r.R_Category_ID=?");
        if (p_C_BPartner_ID != 0) sql.append(" AND r.C_BPartner_ID=?");
        sql.append(" AND r.IsInvoiced='Y' ").append("ORDER BY C_BPartner_ID");

        MRequest[] requests =
                BaseRequestInvoiceKt.getRequestsToBeInvoiced(
                        p_R_RequestType_ID,
                        p_R_Group_ID, p_R_Category_ID, p_C_BPartner_ID
                );

        int oldC_BPartner_ID = 0;
        for (MRequest request : requests) {
            if (!request.isInvoiced()) continue;
            if (oldC_BPartner_ID != request.getBusinessPartnerId()) invoiceDone();
            if (m_invoice == null) {
                invoiceNew(request);
                oldC_BPartner_ID = request.getBusinessPartnerId();
            }
            invoiceLine(request);
        }
        invoiceDone();
        //
        return null;
    } //	doIt

    /**
     * Done with Invoice
     */
    private void invoiceDone() {
        //	Close Old
        if (m_invoice != null) {
            if (m_linecount == 0) m_invoice.delete(false);
            else {
                if (!m_invoice.processIt(DocAction.Companion.getACTION_Prepare())) {
                    log.warning("Invoice Process Failed: " + m_invoice + " - " + m_invoice.getProcessMsg());
                    throw new IllegalStateException(
                            "Invoice Process Failed: " + m_invoice + " - " + m_invoice.getProcessMsg());
                }
                m_invoice.saveEx();
                String message =
                        MsgKt.parseTranslation("@InvoiceProcessed@ " + m_invoice.getDocumentNo());
                addBufferLog(
                        0,
                        null,
                        m_invoice.getGrandTotal(),
                        message,
                        m_invoice.getTableId(),
                        m_invoice.getInvoiceId());
            }
        }
        m_invoice = null;
    } //	invoiceDone

    /**
     * New Invoice
     *
     * @param request request
     */
    private void invoiceNew(MRequest request) {
        m_invoice = new MInvoice(null, 0);
        m_invoice.setIsSOTrx(true);

        MBPartner partner = new MBPartner(request.getBusinessPartnerId());
        m_invoice.setBPartner(partner);

        m_invoice.saveEx();
        m_linecount = 0;
    } //	invoiceNew

    /**
     * Invoice Line
     *
     * @param request request
     */
    private void invoiceLine(MRequest request) {
        I_R_RequestUpdate[] updates = request.getUpdates(null);
        for (I_R_RequestUpdate update : updates) {
            BigDecimal qty = update.getQtyInvoiced();
            if (qty == null || qty.signum() == 0) continue;

            MInvoiceLine il = new MInvoiceLine(m_invoice);
            m_linecount++;
            il.setLine(m_linecount * 10);
            //
            il.setQty(qty);
            //	Product
            int M_Product_ID = update.getProductSpentId();
            if (M_Product_ID == 0) M_Product_ID = p_M_Product_ID;
            il.setProductId(M_Product_ID);
            //
            il.setPrice();
            il.saveEx();
        }
    } //	invoiceLine
} //	RequestInvoice
