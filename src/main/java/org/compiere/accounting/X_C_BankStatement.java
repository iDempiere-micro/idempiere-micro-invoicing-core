package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_BankStatement;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_BankStatement
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_BankStatement extends BasePOName implements I_C_BankStatement {

    /**
     * Complete = CO
     */
    public static final String DOCACTION_Complete = "CO";
    /**
     * Close = CL
     */
    public static final String DOCACTION_Close = "CL";
    /**
     * <None> = --
     */
    public static final String DOCACTION_None = "--";
    /**
     * Prepare = PR
     */
    public static final String DOCACTION_Prepare = "PR";
    /**
     * Drafted = DR
     */
    public static final String DOCSTATUS_Drafted = "DR";
    /**
     * Completed = CO
     */
    public static final String DOCSTATUS_Completed = "CO";
    /**
     * Approved = AP
     */
    public static final String DOCSTATUS_Approved = "AP";
    /**
     * Not Approved = NA
     */
    public static final String DOCSTATUS_NotApproved = "NA";
    /**
     * Voided = VO
     */
    public static final String DOCSTATUS_Voided = "VO";
    /**
     * Invalid = IN
     */
    public static final String DOCSTATUS_Invalid = "IN";
    /**
     * Reversed = RE
     */
    public static final String DOCSTATUS_Reversed = "RE";
    /**
     * Closed = CL
     */
    public static final String DOCSTATUS_Closed = "CL";
    /**
     * In Progress = IP
     */
    public static final String DOCSTATUS_InProgress = "IP";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_BankStatement(int C_BankStatement_ID) {
        super(C_BankStatement_ID);
        /**
         * if (C_BankStatement_ID == 0) { setBankAccountId (0); setBankStatementId (0);
         * setDateAcct (new Timestamp( System.currentTimeMillis() )); setDocAction (null); // CO
         * setDocStatus (null); // DR setEndingBalance (Env.ZERO); setIsApproved (false); // N
         * setIsManual (true); // Y setName (null); // @#Date@ setPosted (false); // N setProcessed
         * (false); setStatementDate (new Timestamp( System.currentTimeMillis() )); // @Date@ }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_BankStatement(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 3 - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_BankStatement[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Beginning Balance.
     *
     * @return Balance prior to any transactions
     */
    public BigDecimal getBeginningBalance() {
        BigDecimal bd = getValue(COLUMNNAME_BeginningBalance);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Beginning Balance.
     *
     * @param BeginningBalance Balance prior to any transactions
     */
    public void setBeginningBalance(BigDecimal BeginningBalance) {
        setValue(COLUMNNAME_BeginningBalance, BeginningBalance);
    }

    /**
     * Get Bank Account.
     *
     * @return Account at the Bank
     */
    public int getBankAccountId() {
        Integer ii = getValue(COLUMNNAME_C_BankAccount_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Bank Account.
     *
     * @param C_BankAccount_ID Account at the Bank
     */
    public void setBankAccountId(int C_BankAccount_ID) {
        if (C_BankAccount_ID < 1) setValue(COLUMNNAME_C_BankAccount_ID, null);
        else setValue(COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
    }

    /**
     * Get Bank Statement.
     *
     * @return Bank Statement of account
     */
    public int getBankStatementId() {
        Integer ii = getValue(COLUMNNAME_C_BankStatement_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(COLUMNNAME_DateAcct);
    }

    /**
     * Set Account Date.
     *
     * @param DateAcct Accounting Date
     */
    public void setDateAcct(Timestamp DateAcct) {
        setValue(COLUMNNAME_DateAcct, DateAcct);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
    }

    /**
     * Get Document Action.
     *
     * @return The targeted status of the document
     */
    public String getDocAction() {
        return getValue(COLUMNNAME_DocAction);
    }

    /**
     * Set Document Action.
     *
     * @param DocAction The targeted status of the document
     */
    public void setDocAction(String DocAction) {

        setValue(COLUMNNAME_DocAction, DocAction);
    }

    /**
     * Get Document Status.
     *
     * @return The current status of the document
     */
    public String getDocStatus() {
        return getValue(COLUMNNAME_DocStatus);
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(String DocStatus) {

        setValue(COLUMNNAME_DocStatus, DocStatus);
    }

    /**
     * Set EFT Statement Date.
     *
     * @param EftStatementDate Electronic Funds Transfer Statement Date
     */
    public void setEftStatementDate(Timestamp EftStatementDate) {
        setValue(COLUMNNAME_EftStatementDate, EftStatementDate);
    }

    /**
     * Get EFT Statement Reference.
     *
     * @return Electronic Funds Transfer Statement Reference
     */
    public String getEftStatementReference() {
        return getValue(COLUMNNAME_EftStatementReference);
    }

    /**
     * Set EFT Statement Reference.
     *
     * @param EftStatementReference Electronic Funds Transfer Statement Reference
     */
    public void setEftStatementReference(String EftStatementReference) {
        setValue(COLUMNNAME_EftStatementReference, EftStatementReference);
    }

    /**
     * Set Ending balance.
     *
     * @param EndingBalance Ending or closing balance
     */
    public void setEndingBalance(BigDecimal EndingBalance) {
        setValue(COLUMNNAME_EndingBalance, EndingBalance);
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValue(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
    }

    /**
     * Get Approved.
     *
     * @return Indicates if this document requires approval
     */
    public boolean isApproved() {
        Object oo = getValue(COLUMNNAME_IsApproved);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Manual.
     *
     * @param IsManual This is a manual process
     */
    public void setIsManual(boolean IsManual) {
        setValue(COLUMNNAME_IsManual, Boolean.valueOf(IsManual));
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValue(COLUMNNAME_Posted, Boolean.valueOf(Posted));
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Boolean.valueOf(Processing));
    }

    /**
     * Get Statement date.
     *
     * @return Date of the statement
     */
    public Timestamp getStatementDate() {
        return (Timestamp) getValue(COLUMNNAME_StatementDate);
    }

    /**
     * Set Statement date.
     *
     * @param StatementDate Date of the statement
     */
    public void setStatementDate(Timestamp StatementDate) {
        setValue(COLUMNNAME_StatementDate, StatementDate);
    }

    /**
     * Get Statement difference.
     *
     * @return Difference between statement ending balance and actual ending balance
     */
    public BigDecimal getStatementDifference() {
        BigDecimal bd = getValue(COLUMNNAME_StatementDifference);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Statement difference.
     *
     * @param StatementDifference Difference between statement ending balance and actual ending
     *                            balance
     */
    public void setStatementDifference(BigDecimal StatementDifference) {
        setValue(COLUMNNAME_StatementDifference, StatementDifference);
    }

    @Override
    public int getTableId() {
        return I_C_BankStatement.Table_ID;
    }
}
