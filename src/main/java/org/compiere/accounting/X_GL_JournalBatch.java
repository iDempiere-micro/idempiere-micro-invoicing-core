package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_GL_JournalBatch;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for GL_JournalBatch
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_GL_JournalBatch extends PO implements I_GL_JournalBatch {

    /**
     * Complete = CO
     */
    public static final String DOCACTION_Complete = "CO";
    /**
     * Close = CL
     */
    public static final String DOCACTION_Close = "CL";
    /**
     * Re-activate = RE
     */
    public static final String DOCACTION_Re_Activate = "RE";
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
     * Actual = A
     */
    public static final String POSTINGTYPE_Actual = "A";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_GL_JournalBatch(int GL_JournalBatch_ID) {
        super(GL_JournalBatch_ID);
    }

    /**
     * Load Constructor
     */
    public X_GL_JournalBatch(Row row) {
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
        StringBuffer sb = new StringBuffer("X_GL_JournalBatch[").append(getId()).append("]");
        return sb.toString();
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
        else setValue(COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
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
        else setValue(COLUMNNAME_GL_Category_ID, Integer.valueOf(GL_Category_ID));
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
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValue(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
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
        return I_GL_JournalBatch.Table_ID;
    }
}
