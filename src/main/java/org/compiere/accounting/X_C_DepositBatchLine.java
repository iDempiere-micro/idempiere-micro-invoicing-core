package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_DepositBatchLine;
import org.compiere.orm.PO;

import java.math.BigDecimal;

/**
 * Generated Model for C_DepositBatchLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_DepositBatchLine extends PO implements I_C_DepositBatchLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_DepositBatchLine(int C_DepositBatchLine_ID) {
        super(C_DepositBatchLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_DepositBatchLine(Row row) {
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
        return "X_C_DepositBatchLine[" + getId() + "]";
    }

    /**
     * Get Deposit Batch.
     *
     * @return Deposit Batch
     */
    public int getDepositBatchId() {
        Integer ii = getValue(COLUMNNAME_C_DepositBatch_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Deposit Batch.
     *
     * @param C_DepositBatch_ID Deposit Batch
     */
    public void setDepositBatchId(int C_DepositBatch_ID) {
        if (C_DepositBatch_ID < 1) setValueNoCheck(COLUMNNAME_C_DepositBatch_ID, null);
        else setValueNoCheck(COLUMNNAME_C_DepositBatch_ID, Integer.valueOf(C_DepositBatch_ID));
    }

    /**
     * Get Payment.
     *
     * @return Payment identifier
     */
    public int getPaymentId() {
        Integer ii = getValue(COLUMNNAME_C_Payment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment.
     *
     * @param C_Payment_ID Payment identifier
     */
    public void setPaymentId(int C_Payment_ID) {
        if (C_Payment_ID < 1) setValue(COLUMNNAME_C_Payment_ID, null);
        else setValue(COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = getValue(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(COLUMNNAME_Line, Integer.valueOf(Line));
    }

    /**
     * Set Payment amount.
     *
     * @param PayAmt Amount being paid
     */
    public void setPayAmt(BigDecimal PayAmt) {
        setValue(COLUMNNAME_PayAmt, PayAmt);
    }

    @Override
    public int getTableId() {
        return I_C_DepositBatchLine.Table_ID;
    }
}
