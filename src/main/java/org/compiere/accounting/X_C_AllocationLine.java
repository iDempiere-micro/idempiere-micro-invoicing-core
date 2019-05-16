package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_AllocationLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for C_AllocationLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_AllocationLine extends PO implements I_C_AllocationLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_AllocationLine(int C_AllocationLine_ID) {
        super(C_AllocationLine_ID);
        /**
         * if (C_AllocationLine_ID == 0) { setAmount (Env.ZERO); setPaymentAllocationHeaderId (0);
         * setAllocationLineId (0); setDiscountAmt (Env.ZERO); setWriteOffAmt (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_AllocationLine(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_AllocationLine[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Amount.
     *
     * @return Amount in a defined currency
     */
    public BigDecimal getAmount() {
        BigDecimal bd = getValue(COLUMNNAME_Amount);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Amount.
     *
     * @param Amount Amount in a defined currency
     */
    public void setAmount(BigDecimal Amount) {
        setValueNoCheck(COLUMNNAME_Amount, Amount);
    }

    /**
     * Get Allocation.
     *
     * @return Payment allocation
     */
    public int getPaymentAllocationHeaderId() {
        Integer ii = getValue(COLUMNNAME_C_AllocationHdr_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Allocation.
     *
     * @param C_AllocationHdr_ID Payment allocation
     */
    public void setPaymentAllocationHeaderId(int C_AllocationHdr_ID) {
        if (C_AllocationHdr_ID < 1) setValueNoCheck(COLUMNNAME_C_AllocationHdr_ID, null);
        else setValueNoCheck(COLUMNNAME_C_AllocationHdr_ID, Integer.valueOf(C_AllocationHdr_ID));
    }

    /**
     * Get Allocation Line.
     *
     * @return Allocation Line
     */
    public int getAllocationLineId() {
        Integer ii = getValue(COLUMNNAME_C_AllocationLine_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValueNoCheck(COLUMNNAME_C_BPartner_ID, null);
        else setValueNoCheck(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
    }

    /**
     * Get Cash Journal Line.
     *
     * @return Cash Journal Line
     */
    public int getCashLineId() {
        Integer ii = getValue(COLUMNNAME_C_CashLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cash Journal Line.
     *
     * @param C_CashLine_ID Cash Journal Line
     */
    public void setCashLineId(int C_CashLine_ID) {
        if (C_CashLine_ID < 1) setValueNoCheck(COLUMNNAME_C_CashLine_ID, null);
        else setValueNoCheck(COLUMNNAME_C_CashLine_ID, Integer.valueOf(C_CashLine_ID));
    }

    public org.compiere.model.I_C_Invoice getInvoice() throws RuntimeException {
        return (org.compiere.model.I_C_Invoice)
                MBaseTableKt.getTable(org.compiere.model.I_C_Invoice.Table_Name)
                        .getPO(getInvoiceId());
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
     * Get Order.
     *
     * @return Order
     */
    public int getOrderId() {
        Integer ii = getValue(COLUMNNAME_C_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Order.
     *
     * @param C_Order_ID Order
     */
    public void setOrderId(int C_Order_ID) {
        if (C_Order_ID < 1) setValueNoCheck(COLUMNNAME_C_Order_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
    }

    public org.compiere.model.I_C_Payment getPayment() throws RuntimeException {
        return (org.compiere.model.I_C_Payment)
                MBaseTableKt.getTable(org.compiere.model.I_C_Payment.Table_Name)
                        .getPO(getPaymentId());
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
        if (C_Payment_ID < 1) setValueNoCheck(COLUMNNAME_C_Payment_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Payment_ID, C_Payment_ID);
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
        setValueNoCheck(COLUMNNAME_DiscountAmt, DiscountAmt);
    }

    /**
     * Get Over/Under Payment.
     *
     * @return Over-Payment (unallocated) or Under-Payment (partial payment) Amount
     */
    public BigDecimal getOverUnderAmt() {
        BigDecimal bd = getValue(COLUMNNAME_OverUnderAmt);
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
        BigDecimal bd = getValue(COLUMNNAME_WriteOffAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Write-off Amount.
     *
     * @param WriteOffAmt Amount to write-off
     */
    public void setWriteOffAmt(BigDecimal WriteOffAmt) {
        setValueNoCheck(COLUMNNAME_WriteOffAmt, WriteOffAmt);
    }

    @Override
    public int getTableId() {
        return I_C_AllocationLine.Table_ID;
    }
}
