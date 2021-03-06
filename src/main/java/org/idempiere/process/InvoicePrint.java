package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereUserError;
import software.hsharp.core.util.DBKt;

import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.convertString;

/**
 * Print Invoices on Paper or send PDFs
 *
 * @author Jorg Janke
 * @version $Id: InvoicePrint.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class InvoicePrint extends SvrProcess {
    /**
     * Mail PDF
     */
    private boolean p_EMailPDF = false;
    /**
     * Mail Template
     */
    private int p_R_MailText_ID = 0;

    private Timestamp m_dateInvoiced_From = null;
    private Timestamp m_dateInvoiced_To = null;
    private int m_C_BPartner_ID = 0;
    private int m_C_Invoice_ID = 0;
    private String m_DocumentNo_From = null;
    private String m_DocumentNo_To = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            if (iProcessInfoParameter.getParameter() != null || iProcessInfoParameter.getParameterTo() != null) {
                if (!name.equals("DateInvoiced")) {
                    switch (name) {
                        case "EMailPDF":
                            p_EMailPDF = "Y".equals(iProcessInfoParameter.getParameter());
                            break;
                        case "R_MailText_ID":
                            p_R_MailText_ID = iProcessInfoParameter.getParameterAsInt();
                            break;
                        case "C_BPartner_ID":
                            m_C_BPartner_ID = iProcessInfoParameter.getParameterAsInt();
                            break;
                        case "C_Invoice_ID":
                            m_C_Invoice_ID = iProcessInfoParameter.getParameterAsInt();
                            break;
                        case "DocumentNo":
                            m_DocumentNo_From = (String) iProcessInfoParameter.getParameter();
                            m_DocumentNo_To = (String) iProcessInfoParameter.getParameterTo();
                            break;
                        default:
                            log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
                            break;
                    }
                } else {
                    m_dateInvoiced_From = ((Timestamp) iProcessInfoParameter.getParameter());
                    m_dateInvoiced_To = ((Timestamp) iProcessInfoParameter.getParameterTo());
                }
            }
        }
        if (m_DocumentNo_From != null && m_DocumentNo_From.length() == 0) m_DocumentNo_From = null;
        if (m_DocumentNo_To != null && m_DocumentNo_To.length() == 0) m_DocumentNo_To = null;
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception
     */
    protected String doIt() throws java.lang.Exception {
        //	Need to have Template
        if (p_EMailPDF && p_R_MailText_ID == 0)
            throw new AdempiereUserError("@NotFound@: @R_MailText_ID@");
        if (log.isLoggable(Level.INFO))
            log.info(
                    "C_BPartner_ID="
                            + m_C_BPartner_ID
                            + ", C_Invoice_ID="
                            + m_C_Invoice_ID
                            + ", EmailPDF="
                            + p_EMailPDF
                            + ",R_MailText_ID="
                            + p_R_MailText_ID
                            + ", DateInvoiced="
                            + m_dateInvoiced_From
                            + "-"
                            + m_dateInvoiced_To
                            + ", DocumentNo="
                            + m_DocumentNo_From
                            + "-"
                            + m_DocumentNo_To);

        //	Too broad selection
        if (m_C_BPartner_ID == 0
                && m_C_Invoice_ID == 0
                && m_dateInvoiced_From == null
                && m_dateInvoiced_To == null
                && m_DocumentNo_From == null
                && m_DocumentNo_To == null) throw new AdempiereUserError("@RestrictSelection@");

        //	Get Info
        StringBuilder sql =
                new StringBuilder("SELECT i.C_Invoice_ID,bp.AD_Language,c.IsMultiLingualDocument,") // 	1..3
                        //	Prio: 1. BPartner 2. DocType, 3. PrintFormat (Org)	//	see ReportCtl+MInvoice
                        .append(
                                " COALESCE(bp.Invoice_PrintFormat_ID, dt.AD_PrintFormat_ID, pf.Invoice_PrintFormat_ID),") //	4
                        .append(" dt.DocumentCopies+bp.DocumentCopies,") // 	5
                        .append(" bpc.AD_User_ID, i.DocumentNo,") // 	6..7
                        .append(" bp.C_BPartner_ID ") // 	8
                        .append("FROM C_Invoice i")
                        .append(" INNER JOIN C_BPartner bp ON (i.C_BPartner_ID=bp.C_BPartner_ID)")
                        .append(" LEFT OUTER JOIN AD_User bpc ON (i.AD_User_ID=bpc.AD_User_ID)")
                        .append(" INNER JOIN AD_Client c ON (i.AD_Client_ID=c.AD_Client_ID)")
                        .append(" INNER JOIN AD_PrintForm pf ON (i.AD_Client_ID=pf.AD_Client_ID)")
                        .append(" INNER JOIN C_DocType dt ON (i.C_DocType_ID=dt.C_DocType_ID)")
                        .append(" WHERE i.AD_Client_ID=? AND i.AD_Org_ID=? AND i.isSOTrx='Y' AND ")
                        .append("       pf.orgId IN (0,i.orgId) AND "); // 	more them 1 PF
        boolean needAnd = false;
        if (m_C_Invoice_ID != 0) sql.append("i.C_Invoice_ID=").append(m_C_Invoice_ID);
        else {
            if (m_C_BPartner_ID != 0) {
                sql.append("i.C_BPartner_ID=").append(m_C_BPartner_ID);
                needAnd = true;
            }
            if (m_dateInvoiced_From != null && m_dateInvoiced_To != null) {
                if (needAnd) sql.append(" AND ");
                sql.append("TRUNC(i.DateInvoiced) BETWEEN ")
                        .append(DBKt.convertDate(m_dateInvoiced_From, true))
                        .append(" AND ")
                        .append(DBKt.convertDate(m_dateInvoiced_To, true));
                needAnd = true;
            } else if (m_dateInvoiced_From != null) {
                if (needAnd) sql.append(" AND ");
                sql.append("TRUNC(i.DateInvoiced) >= ").append(DBKt.convertDate(m_dateInvoiced_From, true));
                needAnd = true;
            } else if (m_dateInvoiced_To != null) {
                if (needAnd) sql.append(" AND ");
                sql.append("TRUNC(i.DateInvoiced) <= ").append(DBKt.convertDate(m_dateInvoiced_To, true));
                needAnd = true;
            } else if (m_DocumentNo_From != null && m_DocumentNo_To != null) {
                if (needAnd) sql.append(" AND ");
                sql.append("i.DocumentNo BETWEEN ")
                        .append(convertString(m_DocumentNo_From))
                        .append(" AND ")
                        .append(convertString(m_DocumentNo_To));
            } else if (m_DocumentNo_From != null) {
                if (needAnd) sql.append(" AND ");
                if (m_DocumentNo_From.indexOf('%') == -1)
                    sql.append("i.DocumentNo >= ").append(convertString(m_DocumentNo_From));
                else sql.append("i.DocumentNo LIKE ").append(convertString(m_DocumentNo_From));
            }

            if (p_EMailPDF) {
                if (needAnd) {
                    sql.append(" AND ");
                }
                /* if emailed to customer only select COmpleted & CLosed invoices */
                sql.append("i.DocStatus IN ('CO','CL') ");
            }
        }
        sql.append(" ORDER BY i.C_Invoice_ID, pf.orgId DESC"); // 	more than 1 PF record
        if (log.isLoggable(Level.FINE)) log.fine(sql.toString());

    /*MPrintFormat format = null;
    int old_AD_PrintFormat_ID = -1;
    int old_C_Invoice_ID = -1;
    int C_BPartner_ID = 0;
    int count = 0;
    int errors = 0;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try
    {
    	pstmt = prepareStatement(sql.toString(), null);
    	pstmt.setInt(1, Env.getClientId(Env.getCtx()));
    	pstmt.setInt(2, Env.getOrgId(Env.getCtx()));
    	rs = pstmt.executeQuery();

    	while (rs.next())
    	{
    		int C_Invoice_ID = rs.getInt(1);
    		if (C_Invoice_ID == old_C_Invoice_ID)	//	multiple pf records
    			continue;
    		old_C_Invoice_ID = C_Invoice_ID;
    		//	Set Language when enabled
    		Language language = Language.getLoginLanguage();		//	Base Language
    		String AD_Language = rs.getString(2);
    		if (AD_Language != null && "Y".equals(rs.getString(3)))
    			language = Language.getLanguage(AD_Language);
    		//
    		int AD_PrintFormat_ID = rs.getInt(4);
    		int copies = rs.getInt(5);
    		if (copies == 0)
    			copies = 1;
    		int AD_User_ID = rs.getInt(6);
    		MUser to = new MUser (AD_User_ID, null);
    		String DocumentNo = rs.getString(7);
    		C_BPartner_ID = rs.getInt(8);
    		//
    		String documentDir = client.getDocumentDir();
    		if (documentDir == null || documentDir.length() == 0)
    			documentDir = ".";
    		//
    		if (p_EMailPDF && (to.getId() == 0 || to.getEMail() == null || to.getEMail().length() == 0))
    		{
    			addLog (C_Invoice_ID, null, null, DocumentNo + " @RequestActionEMailNoTo@");
    			errors++;
    			continue;
    		}
    		if (AD_PrintFormat_ID == 0)
    		{
    			addLog (C_Invoice_ID, null, null, DocumentNo + " No Print Format");
    			errors++;
    			continue;
    		}
    		//	Get Format & Data
    		if (AD_PrintFormat_ID != old_AD_PrintFormat_ID)
    		{
    			format = MPrintFormat.get (AD_PrintFormat_ID, false);
    			old_AD_PrintFormat_ID = AD_PrintFormat_ID;
    		}
    		format.setLanguage(language);
    		format.setTranslationLanguage(language);
    		//	query
    		MQuery query = new MQuery("C_Invoice_Header_v");
    		query.addRestriction("C_Invoice_ID", MQuery.EQUAL, new Integer(C_Invoice_ID));

    		//	Engine
    		PrintInfo info = new PrintInfo(
    			DocumentNo,
    			X_C_Invoice.Table_ID,
    			C_Invoice_ID,
    			C_BPartner_ID);
    		info.setCopies(copies);
    		ReportEngine re = new ReportEngine(format, query, info);
    		boolean printed = false;
    		if (p_EMailPDF)
    		{
    			StringBuilder subject =new StringBuilder(mText.getMailHeader()).append(" - ").append(DocumentNo);
    			EMail email = client.createEMail(to.getEMail(), subject.toString(), null);
    			if (!email.isValid())
    			{
    				addLog (C_Invoice_ID, null, null,
    				  DocumentNo + " @RequestActionEMailError@ Invalid EMail: " + to);
    				errors++;
    				continue;
    			}
    			mText.setUser(to);					//	Context
    			mText.setBPartner(C_BPartner_ID);	//	Context
    			mText.setPO(new MInvoice(C_Invoice_ID, null));
    			String message = mText.getMailText(true);
    			if (mText.isHtml())
    				email.setMessageInHTML(subject.toString(), message);
    			else
    			{
    				email.setSubject (subject.toString());
    				email.setMessageText (message);
    			}
    			//
    			File invoice = null;
    			if (!Ini.isClient())
    				invoice = new File(MInvoice.getPDFFileName(documentDir, C_Invoice_ID));
    			File attachment = re.getPDF(invoice);
    			if (log.isLoggable(Level.FINE)) log.fine(to + " - " + attachment);
    			email.addAttachment(attachment);
    			//
    			String msg = email.send();
    			MUserMail um = new MUserMail(mText, getUserId(), email);
    			um.saveEx();
    			if (msg.equals(EMail.SENT_OK))
    			{
    				addLog (C_Invoice_ID, null, null,
    				  DocumentNo + " @RequestActionEMailOK@ - " + to.getEMail());
    				count++;
    				printed = true;
    			}
    			else
    			{
    				addLog (C_Invoice_ID, null, null,
    				  DocumentNo + " @RequestActionEMailError@ " + msg
    				  + " - " + to.getEMail());
    				errors++;
    			}
    		}
    		else
    		{
    			ServerReportCtl.startDocumentPrint(ReportEngine.INVOICE,
    											   null, // No custom print format
    											   C_Invoice_ID,
    											   null  // No custom printer
    											   );
    			count++;
    			printed = true;
    		}
    		//	Print Confirm
    		if (printed)
    		{
    			StringBuilder sb = new StringBuilder ("UPDATE C_Invoice ")
    				.append("SET DatePrinted=SysDate, IsPrinted='Y' WHERE C_Invoice_ID=")
    				.append (C_Invoice_ID);
    			@SuppressWarnings("unused")
    			int no = executeUpdate(sb.toString(), null);
    		}
    	}	//	for all entries
    }
    catch (Exception e)
    {
    	log.log(Level.SEVERE, "doIt - " + sql, e);
    	throw new Exception (e);
    }
    finally {

    }
    //
    if (p_EMailPDF){
    	StringBuilder msgreturn = new StringBuilder("@Sent@=").append(count).append(" - @Errors@=").append(errors);
    	return msgreturn.toString();
    }
    StringBuilder msgreturn = new StringBuilder("@Printed@=").append(count);
    return msgreturn.toString();*/
        return null;
    } //	doIt
} //	InvoicePrint
