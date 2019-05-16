package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_I_BankStatement;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_I_BankStatement extends BasePOName implements I_I_BankStatement {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_BankStatement(int I_BankStatement_ID) {
        super(I_BankStatement_ID);
    }

    /**
     * Load Constructor
     */
    public X_I_BankStatement(Row row) {
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
        return "X_I_BankStatement[" + getId() + "]";
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
     * Set Bank Statement.
     *
     * @param C_BankStatement_ID Bank Statement of account
     */
    public void setBankStatementId(int C_BankStatement_ID) {
        if (C_BankStatement_ID < 1) setValue(COLUMNNAME_C_BankStatement_ID, null);
        else setValue(COLUMNNAME_C_BankStatement_ID, Integer.valueOf(C_BankStatement_ID));
    }

    /**
     * Set Bank statement line.
     *
     * @param C_BankStatementLine_ID Line on a statement from this Bank
     */
    public void setBankStatementLineId(int C_BankStatementLine_ID) {
        if (C_BankStatementLine_ID < 1) setValue(COLUMNNAME_C_BankStatementLine_ID, null);
        else setValue(COLUMNNAME_C_BankStatementLine_ID, Integer.valueOf(C_BankStatementLine_ID));
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Get EFT Amount.
     *
     * @return Electronic Funds Transfer Amount
     */
    public BigDecimal getEftAmt() {
        BigDecimal bd = getValue(COLUMNNAME_EftAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get EFT Check No.
     *
     * @return Electronic Funds Transfer Check No
     */
    public String getEftCheckNo() {
        return getValue(COLUMNNAME_EftCheckNo);
    }

    /**
     * Get EFT Currency.
     *
     * @return Electronic Funds Transfer Currency
     */
    public String getEftCurrency() {
        return getValue(COLUMNNAME_EftCurrency);
    }

    /**
     * Get EFT Memo.
     *
     * @return Electronic Funds Transfer Memo
     */
    public String getEftMemo() {
        return getValue(COLUMNNAME_EftMemo);
    }

    /**
     * Get EFT Payee.
     *
     * @return Electronic Funds Transfer Payee information
     */
    public String getEftPayee() {
        return getValue(COLUMNNAME_EftPayee);
    }

    /**
     * Get EFT Payee Account.
     *
     * @return Electronic Funds Transfer Payee Account Information
     */
    public String getEftPayeeAccount() {
        return getValue(COLUMNNAME_EftPayeeAccount);
    }

    /**
     * Get EFT Reference.
     *
     * @return Electronic Funds Transfer Reference
     */
    public String getEftReference() {
        return getValue(COLUMNNAME_EftReference);
    }

    /**
     * Get EFT Statement Date.
     *
     * @return Electronic Funds Transfer Statement Date
     */
    public Timestamp getEftStatementDate() {
        return (Timestamp) getValue(COLUMNNAME_EftStatementDate);
    }

    /**
     * Get EFT Statement Line Date.
     *
     * @return Electronic Funds Transfer Statement Line Date
     */
    public Timestamp getEftStatementLineDate() {
        return (Timestamp) getValue(COLUMNNAME_EftStatementLineDate);
    }

    /**
     * Get EFT Statement Reference.
     *
     * @return Electronic Funds Transfer Statement Reference
     */
    public String getEftStatementReference() {
        return getValue(COLUMNNAME_EftStatementReference);
    }

    /**
     * Get EFT Trx ID.
     *
     * @return Electronic Funds Transfer Transaction ID
     */
    public String getEftTrxID() {
        return getValue(COLUMNNAME_EftTrxID);
    }

    /**
     * Get EFT Trx Type.
     *
     * @return Electronic Funds Transfer Transaction Type
     */
    public String getEftTrxType() {
        return getValue(COLUMNNAME_EftTrxType);
    }

    /**
     * Get EFT Effective Date.
     *
     * @return Electronic Funds Transfer Valuta (effective) Date
     */
    public Timestamp getEftValutaDate() {
        return (Timestamp) getValue(COLUMNNAME_EftValutaDate);
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
     * Get Line Description.
     *
     * @return Description of the Line
     */
    public String getLineDescription() {
        return getValue(COLUMNNAME_LineDescription);
    }

    /**
     * Get Memo.
     *
     * @return Memo Text
     */
    public String getMemo() {
        return getValue(COLUMNNAME_Memo);
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
     * Get Reference No.
     *
     * @return Your customer or vendor number at the Business Partner's site
     */
    public String getReferenceNo() {
        return getValue(COLUMNNAME_ReferenceNo);
    }

    /**
     * Get Statement date.
     *
     * @return Date of the statement
     */
    public Timestamp getStatementDate() {
        return (Timestamp) getValue(COLUMNNAME_StatementDate);
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

}
