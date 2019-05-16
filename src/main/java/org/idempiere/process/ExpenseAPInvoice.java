package org.idempiere.process;

import org.compiere.accounting.MTimeExpense;
import org.compiere.accounting.MTimeExpenseLine;
import org.compiere.crm.MBPartner;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.MDocType;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;

import java.sql.Timestamp;
import java.util.logging.Level;

/**
 * Create AP Invoices from Expense Reports
 *
 * @author Jorg Janke
 * @version $Id: ExpenseAPInvoice.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class ExpenseAPInvoice extends SvrProcess {
    private int m_C_BPartner_ID = 0;
    private Timestamp m_DateFrom = null;
    private Timestamp m_DateTo = null;
    private int m_noInvoices = 0;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            if (iProcessInfoParameter.getParameter() != null || iProcessInfoParameter.getParameterTo() != null) {
                if (name.equals("C_BPartner_ID")) m_C_BPartner_ID = iProcessInfoParameter.getParameterAsInt();
                else if (name.equals("DateReport")) {
                    m_DateFrom = (Timestamp) iProcessInfoParameter.getParameter();
                    m_DateTo = (Timestamp) iProcessInfoParameter.getParameterTo();
                } else log.log(Level.SEVERE, "Unknown Parameter: " + name);
            }
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws java.lang.Exception {
        StringBuilder sql =
                new StringBuilder("SELECT * ")
                        .append("FROM S_TimeExpense e ")
                        .append("WHERE e.Processed='Y'")
                        .append(" AND e.AD_Client_ID=?"); // 	#1
        if (m_C_BPartner_ID != 0) sql.append(" AND e.C_BPartner_ID=?"); // 	#2
        if (m_DateFrom != null) sql.append(" AND e.DateReport >= ?"); // 	#3
        if (m_DateTo != null) sql.append(" AND e.DateReport <= ?"); // 	#4
        sql.append(" AND EXISTS (SELECT * FROM S_TimeExpenseLine el ")
                .append("WHERE e.S_TimeExpense_ID=el.S_TimeExpense_ID")
                .append(" AND el.C_InvoiceLine_ID IS NULL")
                .append(" AND el.ConvertedAmt<>0) ")
                .append("ORDER BY e.C_BPartner_ID, e.S_TimeExpense_ID");
        //
        int old_BPartner_ID = -1;
        MInvoice invoice = null;
        //
        MTimeExpense[] items =
                BaseCreateAPInvoicesFromExpenseReportsKt.getExpensesToBeInvoices(
                        sql.toString(), getClientId(), m_C_BPartner_ID, m_DateFrom, m_DateTo
                );
        for (MTimeExpense te : items) // 	********* Expense Line Loop
        {
            //	New BPartner - New Order
            if (te.getBusinessPartnerId() != old_BPartner_ID) {
                completeInvoice(invoice);
                MBPartner bp = new MBPartner(te.getBusinessPartnerId());
                //
                if (log.isLoggable(Level.INFO)) log.info("New Invoice for " + bp);
                invoice = new MInvoice(null, 0);
                invoice.setClientOrg(te.getClientId(), te.getOrgId());
                invoice.setTargetDocumentTypeId(MDocType.DOCBASETYPE_APInvoice); // 	API
                invoice.setDocumentNo(te.getDocumentNo());
                //
                invoice.setBPartner(bp);
                if (invoice.getBusinessPartnerLocationId() == 0) {
                    StringBuilder msglog = new StringBuilder("No BP Location: ").append(bp);
                    log.log(Level.SEVERE, msglog.toString());
                    msglog =
                            new StringBuilder("No Location: ")
                                    .append(te.getDocumentNo())
                                    .append(" ")
                                    .append(bp.getName());
                    addLog(0, te.getDateReport(), null, msglog.toString());
                    invoice = null;
                    break;
                }
                invoice.setPriceListId(te.getPriceListId());
                invoice.setSalesRepresentativeId(te.getDocumentUserId());
                StringBuilder descr =
                        new StringBuilder()
                                .append(MsgKt.translate("S_TimeExpense_ID"))
                                .append(": ")
                                .append(te.getDocumentNo())
                                .append(" ")
                                .append(DisplayType.getDateFormat(DisplayType.Date).format(te.getDateReport()));
                invoice.setDescription(descr.toString());
                if (!invoice.save()) throw new IllegalStateException("Cannot save Invoice");
                old_BPartner_ID = bp.getBusinessPartnerId();
            }
            MTimeExpenseLine[] tel = te.getLines(false);
            for (int i = 0; i < tel.length; i++) {
                MTimeExpenseLine line = tel[i];

                //	Already Invoiced or nothing to be reimbursed
                if (line.getInvoiceLineId() != 0
                        || Env.ZERO.compareTo(line.getQtyReimbursed()) == 0
                        || Env.ZERO.compareTo(line.getPriceReimbursed()) == 0) continue;

                //	Update Header info
                if (line.getBusinessActivityId() != 0 && line.getBusinessActivityId() != invoice.getBusinessActivityId())
                    invoice.setBusinessActivityId(line.getBusinessActivityId());
                if (line.getCampaignId() != 0 && line.getCampaignId() != invoice.getCampaignId())
                    invoice.setCampaignId(line.getCampaignId());
                if (line.getProjectId() != 0 && line.getProjectId() != invoice.getProjectId())
                    invoice.setProjectId(line.getProjectId());
                if (!invoice.save()) throw new IllegalStateException("Cannot save Invoice");

                //	Create OrderLine
                MInvoiceLine il = new MInvoiceLine(invoice);
                //
                if (line.getProductId() != 0) il.setProductId(line.getProductId(), true);
                il.setQty(line.getQtyReimbursed()); // 	Entered/Invoiced
                il.setDescription(line.getDescription());
                //
                il.setProjectId(line.getProjectId());
                il.setProjectPhaseId(line.getProjectPhaseId());
                il.setProjectTaskId(line.getProjectTaskId());
                il.setBusinessActivityId(line.getBusinessActivityId());
                il.setCampaignId(line.getCampaignId());
                //
                //	il.setPrice();	//	not really a list/limit price for reimbursements
                il.setPrice(line.getPriceReimbursed()); //
                il.setTax();
                if (!il.save()) throw new IllegalStateException("Cannot save Invoice Line");
                //	Update TEL
                line.setInvoiceLineId(il.getInvoiceLineId());
                line.saveEx();
            } //	for all expense lines
        } //	********* Expense Line Loop
        completeInvoice(invoice);
        StringBuilder msgreturn = new StringBuilder("@Created@=").append(m_noInvoices);
        return msgreturn.toString();
    } //	doIt

    /**
     * Complete Invoice
     *
     * @param invoice invoice
     */
    private void completeInvoice(MInvoice invoice) {
        if (invoice == null) return;
        invoice.setDocAction(DocAction.Companion.getACTION_Prepare());
        if (!invoice.processIt(DocAction.Companion.getACTION_Prepare())) {
            StringBuilder msglog =
                    new StringBuilder("Invoice Process Failed: ")
                            .append(invoice)
                            .append(" - ")
                            .append(invoice.getProcessMsg());
            log.warning(msglog.toString());
            throw new IllegalStateException(msglog.toString());
        }
        if (!invoice.save()) throw new IllegalStateException("Cannot save Invoice");
        //
        m_noInvoices++;
        addBufferLog(
                invoice.getId(),
                invoice.getDateInvoiced(),
                invoice.getGrandTotal(),
                invoice.getDocumentNo(),
                invoice.getTableId(),
                invoice.getInvoiceId());
    } //	completeInvoice
} //	ExpenseAPInvoice
