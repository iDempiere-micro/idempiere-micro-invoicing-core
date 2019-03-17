package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_PaymentAllocate;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * Generated Model for C_PaymentAllocate
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_PaymentAllocate extends PO implements I_C_PaymentAllocate {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_PaymentAllocate(Properties ctx, int C_PaymentAllocate_ID) {
        super(ctx, C_PaymentAllocate_ID);
        /**
         * if (C_PaymentAllocate_ID == 0) { setAmount (Env.ZERO); setInvoiceId (0);
         * setPaymentAllocate_ID (0); setPaymentId (0); setDiscountAmt (Env.ZERO); setOverUnderAmt
         * (Env.ZERO); setWriteOffAmt (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_PaymentAllocate(Properties ctx, Row row) {
        super(ctx, row);
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
        StringBuffer sb = new StringBuffer("X_C_PaymentAllocate[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Amount.
     *
     * @return Amount in a defined currency
     */
    public BigDecimal getAmount() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_Amount);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Amount.
     *
     * @param Amount Amount in a defined currency
     */
    public void setAmount(BigDecimal Amount) {
        setValue(COLUMNNAME_Amount, Amount);
    }

    /**
     * Set Allocation Line.
     *
     * @param C_AllocationLine_ID Allocation Line
     */
    public void setAllocationLineId(int C_AllocationLine_ID) {
        if (C_AllocationLine_ID < 1) setValue(COLUMNNAME_C_AllocationLine_ID, null);
        else setValue(COLUMNNAME_C_AllocationLine_ID, Integer.valueOf(C_AllocationLine_ID));
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValue(COLUMNNAME_C_Invoice_ID, null);
        else setValue(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    /**
     * Get Payment.
     *
     * @return Payment identifier
     */
    public int getPaymentId() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Payment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Discount Amount.
     *
     * @return Calculated amount of discount
     */
    public BigDecimal getDiscountAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_DiscountAmt);
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
     * Get Invoice Amt.
     *
     * @return Invoice Amt
     */
    public BigDecimal getInvoiceAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_InvoiceAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Invoice Amt.
     *
     * @param InvoiceAmt Invoice Amt
     */
    public void setInvoiceAmt(BigDecimal InvoiceAmt) {
        setValue(COLUMNNAME_InvoiceAmt, InvoiceAmt);
    }

    /**
     * Get Over/Under Payment.
     *
     * @return Over-Payment (unallocated) or Under-Payment (partial payment) Amount
     */
    public BigDecimal getOverUnderAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_OverUnderAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Over/Under Payment.
     *
     * @param OverUnderAmt Over-Payment (unallocated) or Under-Payment (partial payment) Amount
     */
    public void setOverUnderAmt(BigDecimal OverUnderAmt) {
        setValue(COLUMNNAME_OverUnderAmt, OverUnderAmt);
    }

    /**
     * Get Write-off Amount.
     *
     * @return Amount to write-off
     */
    public BigDecimal getWriteOffAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_WriteOffAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Write-off Amount.
     *
     * @param WriteOffAmt Amount to write-off
     */
    public void setWriteOffAmt(BigDecimal WriteOffAmt) {
        setValue(COLUMNNAME_WriteOffAmt, WriteOffAmt);
    }

    @Override
    public int getTableId() {
        return I_C_PaymentAllocate.Table_ID;
    }
}
