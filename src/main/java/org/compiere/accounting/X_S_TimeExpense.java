package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_S_TimeExpense;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_S_TimeExpense extends PO implements I_S_TimeExpense {

    /**
     * Complete = CO
     */
    public static final String DOCACTION_Complete = "CO";
    /**
     * Close = CL
     */
    public static final String DOCACTION_Close = "CL";
    /**
     * Prepare = PR
     */
    public static final String DOCACTION_Prepare = "PR";
    /**
     * Completed = CO
     */
    public static final String DOCSTATUS_Completed = "CO";
    /**
     * Reversed = RE
     */
    public static final String DOCSTATUS_Reversed = "RE";
    /**
     * Closed = CL
     */
    public static final String DOCSTATUS_Closed = "CL";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_S_TimeExpense(int S_TimeExpense_ID) {
        super(S_TimeExpense_ID);
        /**
         * if (S_TimeExpense_ID == 0) { setBusinessPartnerId (0); setDateReport (new Timestamp(
         * System.currentTimeMillis() )); // @#Date@ setDocAction (null); // CO setDocStatus (null); //
         * DR setDocumentNo (null); setIsApproved (false); setPriceListId (0); setWarehouseId (0);
         * setProcessed (false); setS_TimeExpense_ID (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_S_TimeExpense(Row row) {
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
        StringBuffer sb = new StringBuffer("X_S_TimeExpense[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Approval Amount.
     *
     * @return Document Approval Amount
     */
    public BigDecimal getApprovalAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ApprovalAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Approval Amount.
     *
     * @param ApprovalAmt Document Approval Amount
     */
    public void setApprovalAmt(BigDecimal ApprovalAmt) {
        setValue(COLUMNNAME_ApprovalAmt, ApprovalAmt);
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Report Date.
     *
     * @return Expense/Time Report Date
     */
    public Timestamp getDateReport() {
        return (Timestamp) getValue(COLUMNNAME_DateReport);
    }

    /**
     * Set Report Date.
     *
     * @param DateReport Expense/Time Report Date
     */
    public void setDateReport(Timestamp DateReport) {
        setValue(COLUMNNAME_DateReport, DateReport);
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
     * Get Price List.
     *
     * @return Unique identifier of a Price List
     */
    public int getPriceListId() {
        Integer ii = getValue(COLUMNNAME_M_PriceList_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Warehouse.
     *
     * @return Storage Warehouse and Service Point
     */
    public int getWarehouseId() {
        Integer ii = getValue(COLUMNNAME_M_Warehouse_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Expense Report.
     *
     * @return Time and Expense Report
     */
    public int getTimeExpenseId() {
        Integer ii = getValue(COLUMNNAME_S_TimeExpense_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_S_TimeExpense.Table_ID;
    }

}
