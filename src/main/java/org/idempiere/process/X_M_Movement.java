package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_Movement;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_M_Movement extends PO implements I_M_Movement {

    /**
     * Complete = CO
     */
    public static final String DOCACTION_Complete = "CO";
    /**
     * Void = VO
     */
    public static final String DOCACTION_Void = "VO";
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
    public X_M_Movement(int M_Movement_ID) {
        super(M_Movement_ID);
        /*
         * if (M_Movement_ID == 0) { setDocumentTypeId (0); setDocAction (null); // CO setDocStatus
         * (null); // DR setDocumentNo (null); setIsApproved (false); setIsInTransit (false);
         * setMovementId (0); setMovementDate (new Timestamp( System.currentTimeMillis() ));
         * // @#Date@ setPosted (false); setProcessed (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_M_Movement(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_M_Movement[" + getId() + "]";
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
        setValue(COLUMNNAME_DocumentNo, DocumentNo);
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
     * Set In Transit.
     *
     * @param IsInTransit Movement is in transit
     */
    public void setIsInTransit(boolean IsInTransit) {
        setValue(COLUMNNAME_IsInTransit, Boolean.valueOf(IsInTransit));
    }

    /**
     * Get Inventory Move.
     *
     * @return Movement of Inventory
     */
    public int getMovementId() {
        Integer ii = getValue(COLUMNNAME_M_Movement_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Movement Date.
     *
     * @return Date a product was moved in or out of inventory
     */
    public Timestamp getMovementDate() {
        return (Timestamp) getValue(COLUMNNAME_MovementDate);
    }

    /**
     * Set Movement Date.
     *
     * @param MovementDate Date a product was moved in or out of inventory
     */
    public void setMovementDate(Timestamp MovementDate) {
        setValue(COLUMNNAME_MovementDate, MovementDate);
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

}
