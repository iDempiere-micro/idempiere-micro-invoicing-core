package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.DepreciationEntry;
import org.compiere.orm.PO;

import java.sql.Timestamp;

/**
 * Generated Model for A_Depreciation_Entry
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_A_Depreciation_Entry extends PO implements DepreciationEntry {

    /**
     * Depreciation = DEP
     */
    public static final String A_ENTRY_TYPE_Depreciation = "DEP";
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
    public X_A_Depreciation_Entry(int A_Depreciation_Entry_ID) {
        super(A_Depreciation_Entry_ID);
    }

    /**
     * Load Constructor
     */
    public X_A_Depreciation_Entry(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_A_Depreciation_Entry[" + getId() + "]";
    }

    /**
     * Set Entry Type.
     *
     * @param A_Entry_Type Entry Type
     */
    public void setEntryType(String A_Entry_Type) {

        setValue(COLUMNNAME_A_Entry_Type, A_Entry_Type);
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAcctSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValue(COLUMNNAME_C_AcctSchema_ID, null);
        else setValue(COLUMNNAME_C_AcctSchema_ID, C_AcctSchema_ID);
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(COLUMNNAME_C_Currency_ID, null);
        else setValue(COLUMNNAME_C_Currency_ID, C_Currency_ID);
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getDocTypeId() {
        Integer ii = getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(COLUMNNAME_DateAcct);
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
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValue(COLUMNNAME_IsApproved, IsApproved);
    }

    /**
     * Get Approved.
     *
     * @return Indicates if this document requires approval
     */
    public boolean isApproved() {
        Object oo = getValue(COLUMNNAME_IsApproved);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValue(COLUMNNAME_Posted, Posted);
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
            if (oo instanceof Boolean) return (Boolean) oo;
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
        setValue(COLUMNNAME_Processed, Processed);
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Processing);
    }

}
