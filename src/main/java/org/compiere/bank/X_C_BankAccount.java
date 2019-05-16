package org.compiere.bank;

import kotliquery.Row;
import org.compiere.model.I_C_BankAccount;
import org.compiere.orm.BasePONameValue;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_BankAccount
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_BankAccount extends BasePONameValue implements I_C_BankAccount {

    /**
     * Checking = C
     */
    public static final String BANKACCOUNTTYPE_Checking = "C";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_BankAccount(int C_BankAccount_ID) {
        super(C_BankAccount_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_BankAccount(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_BankAccount[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Account No.
     *
     * @return Account Number
     */
    public String getAccountNo() {
        return getValue(COLUMNNAME_AccountNo);
    }

    /**
     * Set Account No.
     *
     * @param AccountNo Account Number
     */
    public void setAccountNo(String AccountNo) {
        setValue(COLUMNNAME_AccountNo, AccountNo);
    }

    /**
     * Set Bank Account Type.
     *
     * @param BankAccountType Bank Account Type
     */
    public void setBankAccountType(String BankAccountType) {

        setValue(COLUMNNAME_BankAccountType, BankAccountType);
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
     * Get Bank.
     *
     * @return Bank
     */
    public int getBankId() {
        Integer ii = getValue(COLUMNNAME_C_Bank_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Bank.
     *
     * @param C_Bank_ID Bank
     */
    public void setBankId(int C_Bank_ID) {
        if (C_Bank_ID < 1) setValueNoCheck(COLUMNNAME_C_Bank_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Bank_ID, C_Bank_ID);
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
     * Set Credit limit.
     *
     * @param CreditLimit Amount of Credit allowed
     */
    public void setCreditLimit(BigDecimal CreditLimit) {
        setValue(COLUMNNAME_CreditLimit, CreditLimit);
    }

    /**
     * Get Current balance.
     *
     * @return Current Balance
     */
    public BigDecimal getCurrentBalance() {
        BigDecimal bd = getValue(COLUMNNAME_CurrentBalance);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Current balance.
     *
     * @param CurrentBalance Current Balance
     */
    public void setCurrentBalance(BigDecimal CurrentBalance) {
        setValue(COLUMNNAME_CurrentBalance, CurrentBalance);
    }

    /**
     * Get IBAN.
     *
     * @return International Bank Account Number
     */
    public String getIBAN() {
        return getValue(COLUMNNAME_IBAN);
    }

    /**
     * Set IBAN.
     *
     * @param IBAN International Bank Account Number
     */
    public void setIBAN(String IBAN) {
        setValue(COLUMNNAME_IBAN, IBAN);
    }

    /**
     * Set Default.
     *
     * @param IsDefault Default value
     */
    public void setIsDefault(boolean IsDefault) {
        setValue(COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
    }

    @Override
    public int getTableId() {
        return I_C_BankAccount.Table_ID;
    }
}
