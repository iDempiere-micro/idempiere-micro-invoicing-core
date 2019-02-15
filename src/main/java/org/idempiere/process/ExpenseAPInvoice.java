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
import org.compiere.util.Msg;
import org.idempiere.common.util.Env;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

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

  /** Prepare - e.g., get Parameters. */
  protected void prepare() {
    IProcessInfoParameter[] para = getParameter();
    for (int i = 0; i < para.length; i++) {
      String name = para[i].getParameterName();
      if (para[i].getParameter() == null && para[i].getParameter_To() == null) ;
      else if (name.equals("C_BPartner_ID")) m_C_BPartner_ID = para[i].getParameterAsInt();
      else if (name.equals("DateReport")) {
        m_DateFrom = (Timestamp) para[i].getParameter();
        m_DateTo = (Timestamp) para[i].getParameter_To();
      } else log.log(Level.SEVERE, "Unknown Parameter: " + name);
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
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = prepareStatement(sql.toString());
      int par = 1;
      pstmt.setInt(par++, getClientId());
      if (m_C_BPartner_ID != 0) pstmt.setInt(par++, m_C_BPartner_ID);
      if (m_DateFrom != null) pstmt.setTimestamp(par++, m_DateFrom);
      if (m_DateTo != null) pstmt.setTimestamp(par++, m_DateTo);
      rs = pstmt.executeQuery();
      while (rs.next()) // 	********* Expense Line Loop
      {
        MTimeExpense te = new MTimeExpense(getCtx(), rs);

        //	New BPartner - New Order
        if (te.getC_BPartner_ID() != old_BPartner_ID) {
          completeInvoice(invoice);
          MBPartner bp = new MBPartner(getCtx(), te.getC_BPartner_ID());
          //
          if (log.isLoggable(Level.INFO)) log.info("New Invoice for " + bp);
          invoice = new MInvoice(getCtx(), 0);
          invoice.setClientOrg(te.getClientId(), te.getOrgId());
          invoice.setC_DocTypeTarget_ID(MDocType.DOCBASETYPE_APInvoice); // 	API
          invoice.setDocumentNo(te.getDocumentNo());
          //
          invoice.setBPartner(bp);
          if (invoice.getC_BPartner_Location_ID() == 0) {
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
          invoice.setM_PriceList_ID(te.getM_PriceList_ID());
          invoice.setSalesRep_ID(te.getDoc_User_ID());
          StringBuilder descr =
              new StringBuilder()
                  .append(Msg.translate(getCtx(), "S_TimeExpense_ID"))
                  .append(": ")
                  .append(te.getDocumentNo())
                  .append(" ")
                  .append(DisplayType.getDateFormat(DisplayType.Date).format(te.getDateReport()));
          invoice.setDescription(descr.toString());
          if (!invoice.save()) throw new IllegalStateException("Cannot save Invoice");
          old_BPartner_ID = bp.getC_BPartner_ID();
        }
        MTimeExpenseLine[] tel = te.getLines(false);
        for (int i = 0; i < tel.length; i++) {
          MTimeExpenseLine line = tel[i];

          //	Already Invoiced or nothing to be reimbursed
          if (line.getC_InvoiceLine_ID() != 0
              || Env.ZERO.compareTo(line.getQtyReimbursed()) == 0
              || Env.ZERO.compareTo(line.getPriceReimbursed()) == 0) continue;

          //	Update Header info
          if (line.getC_Activity_ID() != 0 && line.getC_Activity_ID() != invoice.getC_Activity_ID())
            invoice.setC_Activity_ID(line.getC_Activity_ID());
          if (line.getC_Campaign_ID() != 0 && line.getC_Campaign_ID() != invoice.getC_Campaign_ID())
            invoice.setC_Campaign_ID(line.getC_Campaign_ID());
          if (line.getC_Project_ID() != 0 && line.getC_Project_ID() != invoice.getC_Project_ID())
            invoice.setC_Project_ID(line.getC_Project_ID());
          if (!invoice.save()) throw new IllegalStateException("Cannot save Invoice");

          //	Create OrderLine
          MInvoiceLine il = new MInvoiceLine(invoice);
          //
          if (line.getM_Product_ID() != 0) il.setM_Product_ID(line.getM_Product_ID(), true);
          il.setQty(line.getQtyReimbursed()); // 	Entered/Invoiced
          il.setDescription(line.getDescription());
          //
          il.setC_Project_ID(line.getC_Project_ID());
          il.setC_ProjectPhase_ID(line.getC_ProjectPhase_ID());
          il.setC_ProjectTask_ID(line.getC_ProjectTask_ID());
          il.setC_Activity_ID(line.getC_Activity_ID());
          il.setC_Campaign_ID(line.getC_Campaign_ID());
          //
          //	il.setPrice();	//	not really a list/limit price for reimbursements
          il.setPrice(line.getPriceReimbursed()); //
          il.setTax();
          if (!il.save()) throw new IllegalStateException("Cannot save Invoice Line");
          //	Update TEL
          line.setC_InvoiceLine_ID(il.getC_InvoiceLine_ID());
          line.saveEx();
        } //	for all expense lines
      } //	********* Expense Line Loop
    } catch (Exception e) {
      log.log(Level.SEVERE, sql.toString(), e);
    } finally {

      rs = null;
      pstmt = null;
    }
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
        invoice.getC_Invoice_ID());
  } //	completeInvoice
} //	ExpenseAPInvoice
