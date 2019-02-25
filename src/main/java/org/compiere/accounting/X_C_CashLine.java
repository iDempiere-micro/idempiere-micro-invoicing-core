package org.compiere.accounting;

import org.compiere.model.I_C_CashLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for C_CashLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_CashLine extends PO implements I_C_CashLine {

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
    public X_C_CashLine(Properties ctx, int C_CashLine_ID) {
        super(ctx, C_CashLine_ID);
        /**
         * if (C_CashLine_ID == 0) { setAmount (Env.ZERO); setCashType (null); // E setC_Cash_ID (0);
         * setC_CashLine_ID (0); setLine (0); // @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue
         * FROM C_CashLine WHERE C_Cash_ID=@C_Cash_ID@ setProcessed (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_CashLine(Properties ctx, ResultSet rs) {
        super(ctx, rs);
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
        StringBuffer sb = new StringBuffer("X_C_CashLine[").append(getId()).append("]");
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
        set_Value(COLUMNNAME_Amount, Amount);
    }

    /**
     * Get Cash Type.
     *
     * @return Source of Cash
     */
    public String getCashType() {
        return (String) getValue(COLUMNNAME_CashType);
    }

    /**
     * Set Cash Type.
     *
     * @param CashType Source of Cash
     */
    public void setCashType(String CashType) {

        set_ValueNoCheck(COLUMNNAME_CashType, CashType);
    }

    /**
     * Get Bank Account.
     *
     * @return Account at the Bank
     */
    public int getC_BankAccount_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_BankAccount_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Bank Account.
     *
     * @param C_BankAccount_ID Account at the Bank
     */
    public void setC_BankAccount_ID(int C_BankAccount_ID) {
        if (C_BankAccount_ID < 1) set_Value(COLUMNNAME_C_BankAccount_ID, null);
        else set_Value(COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
    }

    /**
     * Get Cash Journal.
     *
     * @return Cash Journal
     */
    public int getC_Cash_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Cash_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cash Journal.
     *
     * @param C_Cash_ID Cash Journal
     */
    public void setC_Cash_ID(int C_Cash_ID) {
        if (C_Cash_ID < 1) set_ValueNoCheck(COLUMNNAME_C_Cash_ID, null);
        else set_ValueNoCheck(COLUMNNAME_C_Cash_ID, Integer.valueOf(C_Cash_ID));
    }

    /**
     * Get Cash Journal Line.
     *
     * @return Cash Journal Line
     */
    public int getC_CashLine_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_CashLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getC_Charge_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getC_Currency_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setC_Currency_ID(int C_Currency_ID) {
        if (C_Currency_ID < 1) set_ValueNoCheck(COLUMNNAME_C_Currency_ID, null);
        else set_ValueNoCheck(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getC_Invoice_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Payment.
     *
     * @return Payment identifier
     */
    public int getC_Payment_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Payment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment.
     *
     * @param C_Payment_ID Payment identifier
     */
    public void setC_Payment_ID(int C_Payment_ID) {
        if (C_Payment_ID < 1) set_Value(COLUMNNAME_C_Payment_ID, null);
        else set_Value(COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return (String) getValue(COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        set_Value(COLUMNNAME_Description, Description);
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
        set_Value(COLUMNNAME_DiscountAmt, DiscountAmt);
    }

    /**
     * Set Generated.
     *
     * @param IsGenerated This Line is generated
     */
    public void setIsGenerated(boolean IsGenerated) {
        set_ValueNoCheck(COLUMNNAME_IsGenerated, Boolean.valueOf(IsGenerated));
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = (Integer) getValue(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        set_Value(COLUMNNAME_Line, Integer.valueOf(Line));
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
        set_Value(COLUMNNAME_WriteOffAmt, WriteOffAmt);
    }

    @Override
    public int getTableId() {
        return I_C_CashLine.Table_ID;
    }
}
