package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_MovementLineConfirm;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

public class X_M_MovementLineConfirm extends PO implements I_M_MovementLineConfirm {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_MovementLineConfirm(int M_MovementLineConfirm_ID) {
        super(M_MovementLineConfirm_ID);
        /*
         * if (M_MovementLineConfirm_ID == 0) { setConfirmedQty (Env.ZERO); setDifferenceQty (Env.ZERO);
         * setMovementConfirmId (0); setMovementLineConfirm_ID (0); setMovementLine_ID (0);
         * setProcessed (false); setScrappedQty (Env.ZERO); setTargetQty (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_M_MovementLineConfirm(Row row) {
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
        return "X_M_MovementLineConfirm[" + getId() + "]";
    }

    /**
     * Get Confirmed Quantity.
     *
     * @return Confirmation of a received quantity
     */
    public BigDecimal getConfirmedQty() {
        BigDecimal bd = getValue(COLUMNNAME_ConfirmedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Confirmed Quantity.
     *
     * @param ConfirmedQty Confirmation of a received quantity
     */
    public void setConfirmedQty(BigDecimal ConfirmedQty) {
        setValue(COLUMNNAME_ConfirmedQty, ConfirmedQty);
    }

    /**
     * Get Difference.
     *
     * @return Difference Quantity
     */
    public BigDecimal getDifferenceQty() {
        BigDecimal bd = getValue(COLUMNNAME_DifferenceQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Difference.
     *
     * @param DifferenceQty Difference Quantity
     */
    public void setDifferenceQty(BigDecimal DifferenceQty) {
        setValue(COLUMNNAME_DifferenceQty, DifferenceQty);
    }

    /**
     * Set Phys.Inventory Line.
     *
     * @param M_InventoryLine_ID Unique line in an Inventory document
     */
    public void setInventoryLineId(int M_InventoryLine_ID) {
        if (M_InventoryLine_ID < 1) setValue(COLUMNNAME_M_InventoryLine_ID, null);
        else setValue(COLUMNNAME_M_InventoryLine_ID, Integer.valueOf(M_InventoryLine_ID));
    }

    /**
     * Set Move Confirm.
     *
     * @param M_MovementConfirm_ID Inventory Move Confirmation
     */
    public void setMovementConfirmId(int M_MovementConfirm_ID) {
        if (M_MovementConfirm_ID < 1) setValueNoCheck(COLUMNNAME_M_MovementConfirm_ID, null);
        else setValueNoCheck(COLUMNNAME_M_MovementConfirm_ID, Integer.valueOf(M_MovementConfirm_ID));
    }

    /**
     * Get Move Line.
     *
     * @return Inventory Move document Line
     */
    public int getMovementLineId() {
        Integer ii = getValue(COLUMNNAME_M_MovementLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Move Line.
     *
     * @param M_MovementLine_ID Inventory Move document Line
     */
    public void setMovementLineId(int M_MovementLine_ID) {
        if (M_MovementLine_ID < 1) setValue(COLUMNNAME_M_MovementLine_ID, null);
        else setValue(COLUMNNAME_M_MovementLine_ID, Integer.valueOf(M_MovementLine_ID));
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
     * Get Scrapped Quantity.
     *
     * @return The Quantity scrapped due to QA issues
     */
    public BigDecimal getScrappedQty() {
        BigDecimal bd = getValue(COLUMNNAME_ScrappedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Scrapped Quantity.
     *
     * @param ScrappedQty The Quantity scrapped due to QA issues
     */
    public void setScrappedQty(BigDecimal ScrappedQty) {
        setValue(COLUMNNAME_ScrappedQty, ScrappedQty);
    }

    /**
     * Get Target Quantity.
     *
     * @return Target Movement Quantity
     */
    public BigDecimal getTargetQty() {
        BigDecimal bd = getValue(COLUMNNAME_TargetQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Target Quantity.
     *
     * @param TargetQty Target Movement Quantity
     */
    public void setTargetQty(BigDecimal TargetQty) {
        setValue(COLUMNNAME_TargetQty, TargetQty);
    }
}
