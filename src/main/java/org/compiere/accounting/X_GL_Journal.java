package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingSchema;
import org.compiere.model.I_GL_Journal;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for GL_Journal
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_GL_Journal extends PO implements I_GL_Journal {

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
     * Drafted = DR
     */
    public static final String DOCSTATUS_Drafted = "DR";
    /**
     * Completed = CO
     */
    public static final String DOCSTATUS_Completed = "CO";
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
     * Actual = A
     */
    public static final String POSTINGTYPE_Actual = "A";
    /**
     * Budget = B
     */
    public static final String POSTINGTYPE_Budget = "B";
    /**
     * Commitment = E
     */
    public static final String POSTINGTYPE_Commitment = "E";
    /**
     * Statistical = S
     */
    public static final String POSTINGTYPE_Statistical = "S";
    /**
     * Reservation = R
     */
    public static final String POSTINGTYPE_Reservation = "R";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_GL_Journal(int GL_Journal_ID) {
        super(GL_Journal_ID);
        /**
         * if (GL_Journal_ID == 0) { setAccountingSchemaId (0); // @$C_AcctSchema_ID@
         * setConversionTypeId (0); setCurrencyId (0); // @C_Currency_ID@ setDocumentTypeId (0);
         * // @C_DocType_ID@ setPeriodId (0); // @C_Period_ID@ setCurrencyRate (Env.ZERO); // 1
         * setDateAcct (new Timestamp( System.currentTimeMillis() )); // @DateAcct@ setDateDoc (new
         * Timestamp( System.currentTimeMillis() )); // @DateDoc@ setDescription (null); setDocAction
         * (null); // CO setDocStatus (null); // DR setDocumentNo (null); setGLCategoryId (0);
         * // @GL_Category_ID@ setGLJournal_ID (0); setIsApproved (true); // Y setIsPrinted (false); //
         * N setPosted (false); // N setPostingType (null); // @PostingType@ setTotalCr (Env.ZERO); // 0
         * setTotalDr (Env.ZERO); // 0 }
         */
    }

    /**
     * Load Constructor
     */
    public X_GL_Journal(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_GL_Journal[").append(getId()).append("]");
        return sb.toString();
    }

    public AccountingSchema getAccountingSchema() throws RuntimeException {
        return (AccountingSchema)
                MBaseTableKt.getTable(AccountingSchema.Table_Name)
                        .getPO(getAccountingSchemaId());
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
    }

    /**
     * Get Currency Type.
     *
     * @return Currency Conversion Rate Type
     */
    public int getConversionTypeId() {
        Integer ii = getValue(COLUMNNAME_C_ConversionType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency Type.
     *
     * @param C_ConversionType_ID Currency Conversion Rate Type
     */
    public void setConversionTypeId(int C_ConversionType_ID) {
        if (C_ConversionType_ID < 1) setValue(COLUMNNAME_C_ConversionType_ID, null);
        else setValue(COLUMNNAME_C_ConversionType_ID, Integer.valueOf(C_ConversionType_ID));
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(COLUMNNAME_C_Currency_ID, null);
        else setValue(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getDocumentTypeId() {
        Integer ii = getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Document Type.
     *
     * @param C_DocType_ID Document type or rules
     */
    public void setDocumentTypeId(int C_DocType_ID) {
        if (C_DocType_ID < 0) setValue(COLUMNNAME_C_DocType_ID, null);
        else setValue(COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
    }

    /**
     * Get Control Amount.
     *
     * @return If not zero, the Debit amount of the document must be equal this amount
     */
    public BigDecimal getControlAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ControlAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Control Amount.
     *
     * @param ControlAmt If not zero, the Debit amount of the document must be equal this amount
     */
    public void setControlAmt(BigDecimal ControlAmt) {
        setValue(COLUMNNAME_ControlAmt, ControlAmt);
    }

    public org.compiere.model.I_C_Period getPeriod() throws RuntimeException {
        return (org.compiere.model.I_C_Period)
                MBaseTableKt.getTable(org.compiere.model.I_C_Period.Table_Name)
                        .getPO(getPeriodId());
    }

    /**
     * Get Period.
     *
     * @return Period of the Calendar
     */
    public int getPeriodId() {
        Integer ii = getValue(COLUMNNAME_C_Period_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Period.
     *
     * @param C_Period_ID Period of the Calendar
     */
    public void setPeriodId(int C_Period_ID) {
        if (C_Period_ID < 1) setValue(COLUMNNAME_C_Period_ID, null);
        else setValue(COLUMNNAME_C_Period_ID, C_Period_ID);
    }

    /**
     * Get Rate.
     *
     * @return Currency Conversion Rate
     */
    public BigDecimal getCurrencyRate() {
        BigDecimal bd = getValue(COLUMNNAME_CurrencyRate);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Rate.
     *
     * @param CurrencyRate Currency Conversion Rate
     */
    public void setCurrencyRate(BigDecimal CurrencyRate) {
        setValue(COLUMNNAME_CurrencyRate, CurrencyRate);
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
     * Get Document Date.
     *
     * @return Date of the Document
     */
    public Timestamp getDateDoc() {
        return (Timestamp) getValue(COLUMNNAME_DateDoc);
    }

    /**
     * Set Document Date.
     *
     * @param DateDoc Date of the Document
     */
    public void setDateDoc(Timestamp DateDoc) {
        setValue(COLUMNNAME_DateDoc, DateDoc);
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
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Document No.
     *
     * @param DocumentNo Document sequence number of the document
     */
    public void setDocumentNo(String DocumentNo) {
        setValueNoCheck(COLUMNNAME_DocumentNo, DocumentNo);
    }

    /**
     * Get Budget.
     *
     * @return General Ledger Budget
     */
    public int getGLBudgetId() {
        Integer ii = getValue(COLUMNNAME_GL_Budget_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Budget.
     *
     * @param GL_Budget_ID General Ledger Budget
     */
    public void setGLBudgetId(int GL_Budget_ID) {
        if (GL_Budget_ID < 1) setValue(COLUMNNAME_GL_Budget_ID, null);
        else setValue(COLUMNNAME_GL_Budget_ID, GL_Budget_ID);
    }

    /**
     * Get GL Category.
     *
     * @return General Ledger Category
     */
    public int getGLCategoryId() {
        Integer ii = getValue(COLUMNNAME_GL_Category_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set GL Category.
     *
     * @param GL_Category_ID General Ledger Category
     */
    public void setGLCategoryId(int GL_Category_ID) {
        if (GL_Category_ID < 1) setValue(COLUMNNAME_GL_Category_ID, null);
        else setValue(COLUMNNAME_GL_Category_ID, GL_Category_ID);
    }

    /**
     * Get Journal Batch.
     *
     * @return General Ledger Journal Batch
     */
    public int getGLJournalBatchId() {
        Integer ii = getValue(COLUMNNAME_GL_JournalBatch_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Journal Batch.
     *
     * @param GL_JournalBatch_ID General Ledger Journal Batch
     */
    public void setGLJournalBatchId(int GL_JournalBatch_ID) {
        if (GL_JournalBatch_ID < 1) setValueNoCheck(COLUMNNAME_GL_JournalBatch_ID, null);
        else setValueNoCheck(COLUMNNAME_GL_JournalBatch_ID, Integer.valueOf(GL_JournalBatch_ID));
    }

    /**
     * Get Journal.
     *
     * @return General Ledger Journal
     */
    public int getGLJournalId() {
        Integer ii = getValue(COLUMNNAME_GL_Journal_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValueNoCheck(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
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
     * Set Printed.
     *
     * @param IsPrinted Indicates if this document / line is printed
     */
    public void setIsPrinted(boolean IsPrinted) {
        setValueNoCheck(COLUMNNAME_IsPrinted, Boolean.valueOf(IsPrinted));
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValueNoCheck(COLUMNNAME_Posted, Boolean.valueOf(Posted));
    }

    /**
     * Get PostingType.
     *
     * @return The type of posted amount for the transaction
     */
    public String getPostingType() {
        return getValue(COLUMNNAME_PostingType);
    }

    /**
     * Set PostingType.
     *
     * @param PostingType The type of posted amount for the transaction
     */
    public void setPostingType(String PostingType) {

        setValue(COLUMNNAME_PostingType, PostingType);
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
     * Get Processed On.
     *
     * @return The date+time (expressed in decimal format) when the document has been processed
     */
    public BigDecimal getProcessedOn() {
        BigDecimal bd = getValue(COLUMNNAME_ProcessedOn);
        if (bd == null) return Env.ZERO;
        return bd;
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
     * Set Reversal ID.
     *
     * @param Reversal_ID ID of document reversal
     */
    public void setReversalId(int Reversal_ID) {
        if (Reversal_ID < 1) setValue(COLUMNNAME_Reversal_ID, null);
        else setValue(COLUMNNAME_Reversal_ID, Integer.valueOf(Reversal_ID));
    }

    /**
     * Get Total Credit.
     *
     * @return Total Credit in document currency
     */
    public BigDecimal getTotalCr() {
        BigDecimal bd = getValue(COLUMNNAME_TotalCr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Total Credit.
     *
     * @param TotalCr Total Credit in document currency
     */
    public void setTotalCr(BigDecimal TotalCr) {
        setValueNoCheck(COLUMNNAME_TotalCr, TotalCr);
    }

    /**
     * Get Total Debit.
     *
     * @return Total debit in document currency
     */
    public BigDecimal getTotalDr() {
        BigDecimal bd = getValue(COLUMNNAME_TotalDr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Total Debit.
     *
     * @param TotalDr Total debit in document currency
     */
    public void setTotalDr(BigDecimal TotalDr) {
        setValueNoCheck(COLUMNNAME_TotalDr, TotalDr);
    }

    @Override
    public int getTableId() {
        return I_GL_Journal.Table_ID;
    }
}
