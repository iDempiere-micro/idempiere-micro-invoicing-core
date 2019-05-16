/**
 * **************************************************************************** Product: Adempiere
 * ERP & CRM Smart Business Solution * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved. *
 * This program is free software; you can redistribute it and/or modify it * under the terms version
 * 2 of the GNU General Public License as published * by the Free Software Foundation. This program
 * is distributed in the hope * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. * See the GNU General
 * Public License for more details. * You should have received a copy of the GNU General Public
 * License along * with this program; if not, write to the Free Software Foundation, Inc., * 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA. * For the text or an alternative of this
 * public license, you may reach us * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA
 * 95054, USA * or via info@compiere.org or http://www.compiere.org/license.html *
 * ***************************************************************************
 */
package org.idempiere.process;

import org.compiere.accounting.MAllocationHdr;
import org.compiere.accounting.MAllocationLine;
import org.compiere.accounting.MPayment;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereUserError;
import org.idempiere.common.util.Env;
import software.hsharp.core.util.DBKt;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Write-off Open Invoices
 *
 * @author Jorg Janke
 * @version $Id: InvoiceWriteOff.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class InvoiceWriteOff extends SvrProcess {
    private static String ONLY_AP = "P";
    private static String ONLY_AR = "R";
    /**
     * BPartner
     */
    private int p_C_BPartner_ID = 0;
    /**
     * BPartner Group
     */
    private int p_C_BP_Group_ID = 0;
    /**
     * Invoice
     */
    private int p_C_Invoice_ID = 0;
    /**
     * Max Amt
     */
    private BigDecimal p_MaxInvWriteOffAmt = Env.ZERO;
    /**
     * AP or AR
     */
    private String p_APAR = "R";
    /**
     * Invoice Date From
     */
    private Timestamp p_DateInvoiced_From = null;
    /**
     * Invoice Date To
     */
    private Timestamp p_DateInvoiced_To = null;
    /**
     * Accounting Date
     */
    private Timestamp p_DateAcct = null;
    /**
     * Create Payment
     */
    private boolean p_CreatePayment = false;
    /**
     * Bank Account
     */
    private int p_C_BankAccount_ID = 0;
    /**
     * Simulation
     */
    private boolean p_IsSimulation = true;

    /**
     * Allocation Hdr
     */
    private MAllocationHdr m_alloc = null;
    /**
     * Payment
     */
    private MPayment m_payment = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            if (iProcessInfoParameter.getParameter() != null || iProcessInfoParameter.getParameterTo() != null) {
                switch (name) {
                    case "C_BPartner_ID":
                        p_C_BPartner_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "C_BP_Group_ID":
                        p_C_BP_Group_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "C_Invoice_ID":
                        p_C_Invoice_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    //
                    case "MaxInvWriteOffAmt":
                        p_MaxInvWriteOffAmt = (BigDecimal) iProcessInfoParameter.getParameter();
                        break;
                    case "APAR":
                        p_APAR = (String) iProcessInfoParameter.getParameter();
                        break;
                    //
                    case "DateInvoiced":
                        p_DateInvoiced_From = (Timestamp) iProcessInfoParameter.getParameter();
                        p_DateInvoiced_To = (Timestamp) iProcessInfoParameter.getParameterTo();
                        break;
                    case "DateAcct":
                        p_DateAcct = (Timestamp) iProcessInfoParameter.getParameter();
                        break;
                    //
                    case "CreatePayment":
                        p_CreatePayment = "Y".equals(iProcessInfoParameter.getParameter());
                        break;
                    case "C_BankAccount_ID":
                        p_C_BankAccount_ID = iProcessInfoParameter.getParameterAsInt();
                        break;
                    //
                    case "IsSimulation":
                        p_IsSimulation = "Y".equals(iProcessInfoParameter.getParameter());
                        break;
                    default:
                        log.log(Level.SEVERE, "Unknown Parameter: " + name);
                        break;
                }
            }
        }
    } //	prepare

    /**
     * Execute
     *
     * @return message
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "C_BPartner_ID="
                            + p_C_BPartner_ID
                            + ", C_BP_Group_ID="
                            + p_C_BP_Group_ID
                            + ", C_Invoice_ID="
                            + p_C_Invoice_ID
                            + "; APAR="
                            + p_APAR
                            + ", "
                            + p_DateInvoiced_From
                            + " - "
                            + p_DateInvoiced_To
                            + "; CreatePayment="
                            + p_CreatePayment
                            + ", C_BankAccount_ID="
                            + p_C_BankAccount_ID);
        //
        if (p_C_BPartner_ID == 0 && p_C_Invoice_ID == 0 && p_C_BP_Group_ID == 0)
            throw new AdempiereUserError("@FillMandatory@ @C_Invoice_ID@ / @C_BPartner_ID@ / ");
        //
        if (p_CreatePayment && p_C_BankAccount_ID == 0)
            throw new AdempiereUserError("@FillMandatory@  @C_BankAccount_ID@");
        //
        StringBuilder sql =
                new StringBuilder("SELECT C_Invoice_ID,DocumentNo,DateInvoiced,")
                        .append(" C_Currency_ID,GrandTotal, invoiceOpen(C_Invoice_ID, 0) AS OpenAmt ")
                        .append("FROM C_Invoice WHERE ");
        if (p_C_Invoice_ID != 0) sql.append("C_Invoice_ID=").append(p_C_Invoice_ID);
        else {
            if (p_C_BPartner_ID != 0) sql.append("C_BPartner_ID=").append(p_C_BPartner_ID);
            else
                sql.append(
                        "EXISTS (SELECT * FROM C_BPartner bp WHERE C_Invoice.C_BPartner_ID=bp.C_BPartner_ID AND bp.C_BP_Group_ID=")
                        .append(p_C_BP_Group_ID)
                        .append(")");
            //
            if (ONLY_AR.equals(p_APAR)) sql.append(" AND IsSOTrx='Y'");
            else if (ONLY_AP.equals(p_APAR)) sql.append(" AND IsSOTrx='N'");
            //
            if (p_DateInvoiced_From != null && p_DateInvoiced_To != null)
                sql.append(" AND TRUNC(DateInvoiced) BETWEEN ")
                        .append(DBKt.convertDate(p_DateInvoiced_From, true))
                        .append(" AND ")
                        .append(DBKt.convertDate(p_DateInvoiced_To, true));
            else if (p_DateInvoiced_From != null)
                sql.append(" AND TRUNC(DateInvoiced) >= ").append(DBKt.convertDate(p_DateInvoiced_From, true));
            else if (p_DateInvoiced_To != null)
                sql.append(" AND TRUNC(DateInvoiced) <= ").append(DBKt.convertDate(p_DateInvoiced_To, true));
        }
        sql.append(" AND IsPaid='N' ORDER BY C_Currency_ID, C_BPartner_ID, DateInvoiced");
        if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
        //
        int counter = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (writeOff(
                        rs.getInt(1), rs.getString(2), rs.getTimestamp(3), rs.getInt(4), rs.getBigDecimal(6)))
                    counter++;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, sql.toString(), e);
        } finally {

            rs = null;
            pstmt = null;
        }
        //	final
        processPayment();
        processAllocation();
        StringBuilder msgreturn = new StringBuilder("#").append(counter);
        return msgreturn.toString();
    } //	doIt

    /**
     * Write Off
     *
     * @param C_Invoice_ID  invoice
     * @param DocumentNo    doc no
     * @param DateInvoiced  date
     * @param C_Currency_ID currency
     * @param OpenAmt       open amt
     * @return true if written off
     */
    private boolean writeOff(
            int C_Invoice_ID,
            String DocumentNo,
            Timestamp DateInvoiced,
            int C_Currency_ID,
            BigDecimal OpenAmt) {
        //	Nothing to do
        if (OpenAmt == null || OpenAmt.signum() == 0) return false;
        if (OpenAmt.abs().compareTo(p_MaxInvWriteOffAmt) >= 0) return false;
        //
        if (p_IsSimulation) {
            addLog(C_Invoice_ID, DateInvoiced, OpenAmt, DocumentNo);
            return true;
        }

        //	Invoice
        MInvoice invoice = new MInvoice(null, C_Invoice_ID);
        if (!invoice.isSOTrx()) OpenAmt = OpenAmt.negate();

        //	Allocation
        if (m_alloc == null || C_Currency_ID != m_alloc.getCurrencyId()) {
            processAllocation();
            m_alloc =
                    new MAllocationHdr(
                            true,
                            p_DateAcct,
                            C_Currency_ID,
                            getProcessInfo().getTitle() + " #" + getProcessInstanceId()
                    );
            m_alloc.setOrgId(invoice.getOrgId());
            if (!m_alloc.save()) {
                log.log(Level.SEVERE, "Cannot create allocation header");
                return false;
            }
        }
        //	Payment
        if (p_CreatePayment
                && (m_payment == null
                || invoice.getBusinessPartnerId() != m_payment.getBusinessPartnerId()
                || C_Currency_ID != m_payment.getCurrencyId())) {
            processPayment();
            m_payment = new MPayment(0);
            m_payment.setOrgId(invoice.getOrgId());
            m_payment.setBankAccountId(p_C_BankAccount_ID);
            m_payment.setTenderType(MPayment.TENDERTYPE_Check);
            m_payment.setDateTrx(p_DateAcct);
            m_payment.setDateAcct(p_DateAcct);
            m_payment.setDescription(getProcessInfo().getTitle() + " #" + getProcessInstanceId());
            m_payment.setBusinessPartnerId(invoice.getBusinessPartnerId());
            m_payment.setIsReceipt(true); // 	payments are negative
            m_payment.setCurrencyId(C_Currency_ID);
            if (!m_payment.save()) {
                log.log(Level.SEVERE, "Cannot create payment");
                return false;
            }
        }

        //	Line
        MAllocationLine aLine = null;
        if (p_CreatePayment) {
            aLine = new MAllocationLine(m_alloc, OpenAmt, Env.ZERO, Env.ZERO, Env.ZERO);
            m_payment.setPayAmt(m_payment.getPayAmt().add(OpenAmt));
            aLine.setPaymentId(m_payment.getPaymentId());
        } else aLine = new MAllocationLine(m_alloc, Env.ZERO, Env.ZERO, OpenAmt, Env.ZERO);
        aLine.setInvoiceId(C_Invoice_ID);
        if (aLine.save()) {
            addLog(C_Invoice_ID, DateInvoiced, OpenAmt, DocumentNo);
            return true;
        }
        //	Error
        log.log(Level.SEVERE, "Cannot create allocation line for C_Invoice_ID=" + C_Invoice_ID);
        return false;
    } //	writeOff

    /**
     * Process Allocation
     *
     * @return true if processed
     */
    private boolean processAllocation() {
        if (m_alloc == null) return true;
        processPayment();
        //	Process It
        if (!m_alloc.processIt(DocAction.Companion.getACTION_Complete())) {
            log.warning("Allocation Process Failed: " + m_alloc + " - " + m_alloc.getProcessMsg());
            throw new IllegalStateException(
                    "Allocation Process Failed: " + m_alloc + " - " + m_alloc.getProcessMsg());
        }
        if (m_alloc.save()) {
            m_alloc = null;
            return true;
        }
        //
        m_alloc = null;
        return false;
    } //	processAllocation

    /**
     * Process Payment
     *
     * @return true if processed
     */
    private boolean processPayment() {
        if (m_payment == null) return true;
        //	Process It
        if (!m_payment.processIt(DocAction.Companion.getACTION_Complete())) {
            log.warning("Payment Process Failed: " + m_payment + " - " + m_payment.getProcessMsg());
            throw new IllegalStateException(
                    "Payment Process Failed: " + m_payment + " - " + m_payment.getProcessMsg());
        }

        if (m_payment.save()) {
            m_payment = null;
            return true;
        }
        //
        m_payment = null;
        return false;
    } //	processPayment
} //	InvoiceWriteOff
