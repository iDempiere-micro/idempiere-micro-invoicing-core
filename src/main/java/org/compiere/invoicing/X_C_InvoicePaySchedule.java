package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_InvoicePaySchedule;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_InvoicePaySchedule
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_InvoicePaySchedule extends PO implements I_C_InvoicePaySchedule {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_InvoicePaySchedule(int C_InvoicePaySchedule_ID) {
        super(C_InvoicePaySchedule_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_InvoicePaySchedule(Row row) {
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
        return "X_C_InvoicePaySchedule[" + getId() + "]";
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = getValue(COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValueNoCheck(COLUMNNAME_C_Invoice_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    /**
     * Set Payment Schedule.
     *
     * @param C_PaySchedule_ID Payment Schedule Template
     */
    public void setPayScheduleId(int C_PaySchedule_ID) {
        if (C_PaySchedule_ID < 1) setValueNoCheck(COLUMNNAME_C_PaySchedule_ID, null);
        else setValueNoCheck(COLUMNNAME_C_PaySchedule_ID, Integer.valueOf(C_PaySchedule_ID));
    }

    /**
     * Get Discount Amount.
     *
     * @return Calculated amount of discount
     */
    public BigDecimal getDiscountAmt() {
        BigDecimal bd = getValue(COLUMNNAME_DiscountAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Discount Amount.
     *
     * @param DiscountAmt Calculated amount of discount
     */
    public void setDiscountAmt(BigDecimal DiscountAmt) {
        setValue(COLUMNNAME_DiscountAmt, DiscountAmt);
    }

    /**
     * Get Discount Date.
     *
     * @return Last Date for payments with discount
     */
    public Timestamp getDiscountDate() {
        return (Timestamp) getValue(COLUMNNAME_DiscountDate);
    }

    /**
     * Set Discount Date.
     *
     * @param DiscountDate Last Date for payments with discount
     */
    public void setDiscountDate(Timestamp DiscountDate) {
        setValue(COLUMNNAME_DiscountDate, DiscountDate);
    }

    /**
     * Get Amount due.
     *
     * @return Amount of the payment due
     */
    public BigDecimal getDueAmt() {
        BigDecimal bd = getValue(COLUMNNAME_DueAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Amount due.
     *
     * @param DueAmt Amount of the payment due
     */
    public void setDueAmt(BigDecimal DueAmt) {
        setValue(COLUMNNAME_DueAmt, DueAmt);
    }

    /**
     * Get Due Date.
     *
     * @return Date when the payment is due
     */
    public Timestamp getDueDate() {
        return (Timestamp) getValue(COLUMNNAME_DueDate);
    }

    /**
     * Set Due Date.
     *
     * @param DueDate Date when the payment is due
     */
    public void setDueDate(Timestamp DueDate) {
        setValue(COLUMNNAME_DueDate, DueDate);
    }

    /**
     * Set Valid.
     *
     * @param IsValid Element is valid
     */
    public void setIsValid(boolean IsValid) {
        setValue(COLUMNNAME_IsValid, Boolean.valueOf(IsValid));
    }

    /**
     * Get Valid.
     *
     * @return Element is valid
     */
    public boolean isValid() {
        Object oo = getValue(COLUMNNAME_IsValid);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Boolean.valueOf(Processing));
    }

    @Override
    public int getTableId() {
        return I_C_InvoicePaySchedule.Table_ID;
    }
}
