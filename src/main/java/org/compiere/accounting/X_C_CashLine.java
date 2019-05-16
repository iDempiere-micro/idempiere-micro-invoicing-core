package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_CashLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_CashLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_CashLine extends PO implements I_C_CashLine {

    /**
     * Bank Account Transfer = T
     */
    public static final String CASHTYPE_BankAccountTransfer = "T";
    /**
     * Invoice = I
     */
    public static final String CASHTYPE_Invoice = "I";
    /**
     * General Expense = E
     */
    public static final String CASHTYPE_GeneralExpense = "E";
    /**
     * General Receipts = R
     */
    public static final String CASHTYPE_GeneralReceipts = "R";
    /**
     * Charge = C
     */
    public static final String CASHTYPE_Charge = "C";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_CashLine(int C_CashLine_ID) {
        super(C_CashLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_CashLine(Row row) {
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
        return "X_C_CashLine[" + getId() + "]";
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
        setValue(COLUMNNAME_Amount, Amount);
    }

    /**
     * Get Cash Type.
     *
     * @return Source of Cash
     */
    public String getCashType() {
        return getValue(COLUMNNAME_CashType);
    }

    /**
     * Set Cash Type.
     *
     * @param CashType Source of Cash
     */
    public void setCashType(String CashType) {

        setValueNoCheck(COLUMNNAME_CashType, CashType);
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
     * Set Bank Account.
     *
     * @param C_BankAccount_ID Account at the Bank
     */
    public void setBankAccountId(int C_BankAccount_ID) {
        if (C_BankAccount_ID < 1) setValue(COLUMNNAME_C_BankAccount_ID, null);
        else setValue(COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
    }

    /**
     * Get Cash Journal.
     *
     * @return Cash Journal
     */
    public int getCashId() {
        Integer ii = getValue(COLUMNNAME_C_Cash_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cash Journal.
     *
     * @param C_Cash_ID Cash Journal
     */
    public void setCashId(int C_Cash_ID) {
        if (C_Cash_ID < 1) setValueNoCheck(COLUMNNAME_C_Cash_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Cash_ID, Integer.valueOf(C_Cash_ID));
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
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getChargeId() {
        Integer ii = getValue(COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValueNoCheck(COLUMNNAME_C_Currency_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
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
     * Set Generated.
     *
     * @param IsGenerated This Line is generated
     */
    public void setIsGenerated(boolean IsGenerated) {
        setValueNoCheck(COLUMNNAME_IsGenerated, Boolean.valueOf(IsGenerated));
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
        setValue(COLUMNNAME_WriteOffAmt, WriteOffAmt);
    }

    @Override
    public int getTableId() {
        return I_C_CashLine.Table_ID;
    }
}
