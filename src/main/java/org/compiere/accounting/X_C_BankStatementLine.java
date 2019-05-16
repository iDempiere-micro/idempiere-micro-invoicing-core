package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_BankStatementLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_BankStatementLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_BankStatementLine extends PO implements I_C_BankStatementLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_BankStatementLine(int C_BankStatementLine_ID) {
        super(C_BankStatementLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_BankStatementLine(Row row) {
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
        return "X_C_BankStatementLine[" + getId() + "]";
    }

    /**
     * Get Bank Statement.
     *
     * @return Bank Statement of account
     */
    public int getBankStatementId() {
        Integer ii = getValue(COLUMNNAME_C_BankStatement_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Bank Statement.
     *
     * @param C_BankStatement_ID Bank Statement of account
     */
    public void setBankStatementId(int C_BankStatement_ID) {
        if (C_BankStatement_ID < 1) setValueNoCheck(COLUMNNAME_C_BankStatement_ID, null);
        else setValueNoCheck(COLUMNNAME_C_BankStatement_ID, Integer.valueOf(C_BankStatement_ID));
    }

    /**
     * Get Bank statement line.
     *
     * @return Line on a statement from this Bank
     */
    public int getBankStatementLineId() {
        Integer ii = getValue(COLUMNNAME_C_BankStatementLine_ID);
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
        if (C_BPartner_ID < 1) setValue(COLUMNNAME_C_BPartner_ID, null);
        else setValue(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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
     * Set Charge.
     *
     * @param C_Charge_ID Additional document charges
     */
    public void setChargeId(int C_Charge_ID) {
        if (C_Charge_ID < 1) setValue(COLUMNNAME_C_Charge_ID, null);
        else setValue(COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
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
        if (C_Currency_ID < 1) setValue(COLUMNNAME_C_Currency_ID, null);
        else setValue(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
    }

    /**
     * Get Charge amount.
     *
     * @return Charge Amount
     */
    public BigDecimal getChargeAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ChargeAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Charge amount.
     *
     * @param ChargeAmt Charge Amount
     */
    public void setChargeAmt(BigDecimal ChargeAmt) {
        setValue(COLUMNNAME_ChargeAmt, ChargeAmt);
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
        if (C_Invoice_ID < 1) setValue(COLUMNNAME_C_Invoice_ID, null);
        else setValue(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
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
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(COLUMNNAME_DateAcct);
    }

    /**
     * Set Account Date.
     *
     * @param DateAcct Accounting Date
     */
    public void setDateAcct(Timestamp DateAcct) {
        setValue(COLUMNNAME_DateAcct, DateAcct);
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
     * Set EFT Amount.
     *
     * @param EftAmt Electronic Funds Transfer Amount
     */
    public void setEftAmt(BigDecimal EftAmt) {
        setValue(COLUMNNAME_EftAmt, EftAmt);
    }

    /**
     * Set EFT Check No.
     *
     * @param EftCheckNo Electronic Funds Transfer Check No
     */
    public void setEftCheckNo(String EftCheckNo) {
        setValue(COLUMNNAME_EftCheckNo, EftCheckNo);
    }

    /**
     * Set EFT Currency.
     *
     * @param EftCurrency Electronic Funds Transfer Currency
     */
    public void setEftCurrency(String EftCurrency) {
        setValue(COLUMNNAME_EftCurrency, EftCurrency);
    }

    /**
     * Set EFT Memo.
     *
     * @param EftMemo Electronic Funds Transfer Memo
     */
    public void setEftMemo(String EftMemo) {
        setValue(COLUMNNAME_EftMemo, EftMemo);
    }

    /**
     * Set EFT Payee.
     *
     * @param EftPayee Electronic Funds Transfer Payee information
     */
    public void setEftPayee(String EftPayee) {
        setValue(COLUMNNAME_EftPayee, EftPayee);
    }

    /**
     * Set EFT Payee Account.
     *
     * @param EftPayeeAccount Electronic Funds Transfer Payee Account Information
     */
    public void setEftPayeeAccount(String EftPayeeAccount) {
        setValue(COLUMNNAME_EftPayeeAccount, EftPayeeAccount);
    }

    /**
     * Set EFT Reference.
     *
     * @param EftReference Electronic Funds Transfer Reference
     */
    public void setEftReference(String EftReference) {
        setValue(COLUMNNAME_EftReference, EftReference);
    }

    /**
     * Set EFT Statement Line Date.
     *
     * @param EftStatementLineDate Electronic Funds Transfer Statement Line Date
     */
    public void setEftStatementLineDate(Timestamp EftStatementLineDate) {
        setValue(COLUMNNAME_EftStatementLineDate, EftStatementLineDate);
    }

    /**
     * Set EFT Trx ID.
     *
     * @param EftTrxID Electronic Funds Transfer Transaction ID
     */
    public void setEftTrxID(String EftTrxID) {
        setValue(COLUMNNAME_EftTrxID, EftTrxID);
    }

    /**
     * Set EFT Trx Type.
     *
     * @param EftTrxType Electronic Funds Transfer Transaction Type
     */
    public void setEftTrxType(String EftTrxType) {
        setValue(COLUMNNAME_EftTrxType, EftTrxType);
    }

    /**
     * Set EFT Effective Date.
     *
     * @param EftValutaDate Electronic Funds Transfer Valuta (effective) Date
     */
    public void setEftValutaDate(Timestamp EftValutaDate) {
        setValue(COLUMNNAME_EftValutaDate, EftValutaDate);
    }

    /**
     * Get Interest Amount.
     *
     * @return Interest Amount
     */
    public BigDecimal getInterestAmt() {
        BigDecimal bd = getValue(COLUMNNAME_InterestAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Interest Amount.
     *
     * @param InterestAmt Interest Amount
     */
    public void setInterestAmt(BigDecimal InterestAmt) {
        setValue(COLUMNNAME_InterestAmt, InterestAmt);
    }

    /**
     * Set Reversal.
     *
     * @param IsReversal This is a reversing transaction
     */
    public void setIsReversal(boolean IsReversal) {
        setValue(COLUMNNAME_IsReversal, Boolean.valueOf(IsReversal));
    }

    /**
     * Get Reversal.
     *
     * @return This is a reversing transaction
     */
    public boolean isReversal() {
        Object oo = getValue(COLUMNNAME_IsReversal);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Set Memo.
     *
     * @param Memo Memo Text
     */
    public void setMemo(String Memo) {
        setValue(COLUMNNAME_Memo, Memo);
    }

    /**
     * Set Reference No.
     *
     * @param ReferenceNo Your customer or vendor number at the Business Partner's site
     */
    public void setReferenceNo(String ReferenceNo) {
        setValue(COLUMNNAME_ReferenceNo, ReferenceNo);
    }

    /**
     * Get Statement Line Date.
     *
     * @return Date of the Statement Line
     */
    public Timestamp getStatementLineDate() {
        return (Timestamp) getValue(COLUMNNAME_StatementLineDate);
    }

    /**
     * Set Statement Line Date.
     *
     * @param StatementLineDate Date of the Statement Line
     */
    public void setStatementLineDate(Timestamp StatementLineDate) {
        setValue(COLUMNNAME_StatementLineDate, StatementLineDate);
    }

    /**
     * Get Statement amount.
     *
     * @return Statement Amount
     */
    public BigDecimal getStmtAmt() {
        BigDecimal bd = getValue(COLUMNNAME_StmtAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Statement amount.
     *
     * @param StmtAmt Statement Amount
     */
    public void setStmtAmt(BigDecimal StmtAmt) {
        setValue(COLUMNNAME_StmtAmt, StmtAmt);
    }

    /**
     * Get Transaction Amount.
     *
     * @return Amount of a transaction
     */
    public BigDecimal getTrxAmt() {
        BigDecimal bd = getValue(COLUMNNAME_TrxAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Transaction Amount.
     *
     * @param TrxAmt Amount of a transaction
     */
    public void setTrxAmt(BigDecimal TrxAmt) {
        setValue(COLUMNNAME_TrxAmt, TrxAmt);
    }

    /**
     * Get Effective date.
     *
     * @return Date when money is available
     */
    public Timestamp getValutaDate() {
        return (Timestamp) getValue(COLUMNNAME_ValutaDate);
    }

    /**
     * Set Effective date.
     *
     * @param ValutaDate Date when money is available
     */
    public void setValutaDate(Timestamp ValutaDate) {
        setValue(COLUMNNAME_ValutaDate, ValutaDate);
    }

    @Override
    public int getTableId() {
        return I_C_BankStatementLine.Table_ID;
    }
}
