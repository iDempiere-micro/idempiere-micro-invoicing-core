package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_MovementConfirm;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

public class X_M_MovementConfirm extends PO implements I_M_MovementConfirm {

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
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_MovementConfirm(int M_MovementConfirm_ID) {
        super(M_MovementConfirm_ID);
        /*
         * if (M_MovementConfirm_ID == 0) { setDocAction (null); setDocStatus (null); setDocumentNo
         * (null); setIsApproved (false); // N setMovementConfirmId (0); setMovementId (0);
         * setProcessed (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_M_MovementConfirm(Row row) {
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
        return "X_M_MovementConfirm[" + getId() + "]";
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
     * Get Phys.Inventory.
     *
     * @return Parameters for a Physical Inventory
     */
    public int getInventoryId() {
        Integer ii = getValue(COLUMNNAME_M_Inventory_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Phys.Inventory.
     *
     * @param M_Inventory_ID Parameters for a Physical Inventory
     */
    public void setInventoryId(int M_Inventory_ID) {
        if (M_Inventory_ID < 1) setValue(COLUMNNAME_M_Inventory_ID, null);
        else setValue(COLUMNNAME_M_Inventory_ID, Integer.valueOf(M_Inventory_ID));
    }

    /**
     * Get Move Confirm.
     *
     * @return Inventory Move Confirmation
     */
    public int getMovementConfirmId() {
        Integer ii = getValue(COLUMNNAME_M_MovementConfirm_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Inventory Move.
     *
     * @param M_Movement_ID Movement of Inventory
     */
    public void setMovementId(int M_Movement_ID) {
        if (M_Movement_ID < 1) setValue(COLUMNNAME_M_Movement_ID, null);
        else setValue(COLUMNNAME_M_Movement_ID, Integer.valueOf(M_Movement_ID));
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

}
