package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_PaySelection;
import org.compiere.orm.BasePOName;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_PaySelection
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_PaySelection extends BasePOName implements I_C_PaySelection {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_PaySelection(int C_PaySelection_ID) {
        super(C_PaySelection_ID);
        /**
         * if (C_PaySelection_ID == 0) { setBankAccountId (0); setPaySelectionId (0);
         * setIsApproved (false); setName (null); // @#Date@ setPayDate (new Timestamp(
         * System.currentTimeMillis() )); // @#Date@ setProcessed (false); setProcessing (false);
         * setTotalAmt (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_PaySelection(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_PaySelection[").append(getId()).append("]");
        return sb.toString();
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
     * Get Payment Selection.
     *
     * @return Payment Selection
     */
    public int getPaySelectionId() {
        Integer ii = getValue(COLUMNNAME_C_PaySelection_ID);
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
     * Get Payment date.
     *
     * @return Date Payment made
     */
    public Timestamp getPayDate() {
        return (Timestamp) getValue(COLUMNNAME_PayDate);
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

    /**
     * Set Total Amount.
     *
     * @param TotalAmt Total Amount
     */
    public void setTotalAmt(BigDecimal TotalAmt) {
        setValue(COLUMNNAME_TotalAmt, TotalAmt);
    }

    @Override
    public int getTableId() {
        return I_C_PaySelection.Table_ID;
    }
}
