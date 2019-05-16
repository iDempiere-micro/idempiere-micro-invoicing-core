package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_I_InOutLineConfirm;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

public class X_I_InOutLineConfirm extends PO implements I_I_InOutLineConfirm {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_InOutLineConfirm(int I_InOutLineConfirm_ID) {
        super(I_InOutLineConfirm_ID);
    }

    /**
     * Load Constructor
     */
    public X_I_InOutLineConfirm(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_I_InOutLineConfirm[" + getId() + "]";
    }

    /**
     * Get Confirmation No.
     *
     * @return Confirmation Number
     */
    public String getConfirmationNo() {
        return getValue(COLUMNNAME_ConfirmationNo);
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
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
     * Set Import Error Message.
     *
     * @param I_ErrorMsg Messages generated from import process
     */
    public void setImportErrorMsg(String I_ErrorMsg) {
        setValue(COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
    }

    /**
     * Set Imported.
     *
     * @param I_IsImported Has this import been processed
     */
    public void setIsImported(boolean I_IsImported) {
        setValue(COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
    }

    /**
     * Get Ship/Receipt Confirmation Line.
     *
     * @return Material Shipment or Receipt Confirmation Line
     */
    public int getInOutLineConfirmId() {
        Integer ii = getValue(COLUMNNAME_M_InOutLineConfirm_ID);
        if (ii == null) return 0;
        return ii;
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
}
