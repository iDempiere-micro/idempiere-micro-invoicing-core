package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_BP_BankAccount;
import org.compiere.orm.PO;

/**
 * Generated Model for C_BP_BankAccount
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_BP_BankAccount extends PO implements I_C_BP_BankAccount {

    /**
     * Both = B
     */
    public static final String BPBANKACCTUSE_Both = "B";
    /**
     * Direct Debit = D
     */
    public static final String BPBANKACCTUSE_DirectDebit = "D";
    /**
     * Direct Deposit = T
     */
    public static final String BPBANKACCTUSE_DirectDeposit = "T";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_BP_BankAccount(int C_BP_BankAccount_ID) {
        super(C_BP_BankAccount_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_BP_BankAccount(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_BP_BankAccount[").append(getId()).append("]");
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
     * Set Account City.
     *
     * @param A_City City or the Credit Card or Account Holder
     */
    public void setAccountCity(String A_City) {
        setValue(COLUMNNAME_A_City, A_City);
    }

    /**
     * Set Account Country.
     *
     * @param A_Country Country
     */
    public void setAccountCountry(String A_Country) {
        setValue(COLUMNNAME_A_Country, A_Country);
    }

    /**
     * Set Account EMail.
     *
     * @param A_EMail Email Address
     */
    public void setAccountEMail(String A_EMail) {
        setValue(COLUMNNAME_A_EMail, A_EMail);
    }

    /**
     * Get Account Name.
     *
     * @return Name on Credit Card or Account holder
     */
    public String getAccountName() {
        return getValue(COLUMNNAME_A_Name);
    }

    /**
     * Set Account Name.
     *
     * @param A_Name Name on Credit Card or Account holder
     */
    public void setAccountName(String A_Name) {
        setValue(COLUMNNAME_A_Name, A_Name);
    }

    /**
     * Set Account State.
     *
     * @param A_State State of the Credit Card or Account holder
     */
    public void setAccountState(String A_State) {
        setValue(COLUMNNAME_A_State, A_State);
    }

    /**
     * Set Account Street.
     *
     * @param A_Street Street address of the Credit Card or Account holder
     */
    public void setAccountStreet(String A_Street) {
        setValue(COLUMNNAME_A_Street, A_Street);
    }

    /**
     * Set Account Zip/Postal.
     *
     * @param A_Zip Zip Code of the Credit Card or Account Holder
     */
    public void setAccountZip(String A_Zip) {
        setValue(COLUMNNAME_A_Zip, A_Zip);
    }

    /**
     * Get Account Usage.
     *
     * @return Business Partner Bank Account usage
     */
    public String getBPBankAcctUse() {
        return getValue(COLUMNNAME_BPBankAcctUse);
    }

    /**
     * Set Account Usage.
     *
     * @param BPBankAcctUse Business Partner Bank Account usage
     */
    public void setBPBankAcctUse(String BPBankAcctUse) {

        setValue(COLUMNNAME_BPBankAcctUse, BPBankAcctUse);
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
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValueNoCheck(COLUMNNAME_C_BPartner_ID, null);
        else setValueNoCheck(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
    }

    /**
     * Get Partner Bank Account.
     *
     * @return Bank Account of the Business Partner
     */
    public int getBusinessPartnerBankAccountId() {
        Integer ii = getValue(COLUMNNAME_C_BP_BankAccount_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Number.
     *
     * @return Credit Card Number
     */
    public String getCreditCardNumber() {
        return getValue(COLUMNNAME_CreditCardNumber);
    }

    /**
     * Set Number.
     *
     * @param CreditCardNumber Credit Card Number
     */
    public void setCreditCardNumber(String CreditCardNumber) {
        setValue(COLUMNNAME_CreditCardNumber, CreditCardNumber);
    }

    /**
     * Get Verification Code.
     *
     * @return Credit Card Verification code on credit card
     */
    public String getCreditCardVV() {
        return getValue(COLUMNNAME_CreditCardVV);
    }

    /**
     * Set Verification Code.
     *
     * @param CreditCardVV Credit Card Verification code on credit card
     */
    public void setCreditCardVV(String CreditCardVV) {
        setValue(COLUMNNAME_CreditCardVV, CreditCardVV);
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
     * Set ACH.
     *
     * @param IsACH Automatic Clearing House
     */
    public void setIsACH(boolean IsACH) {
        setValue(COLUMNNAME_IsACH, Boolean.valueOf(IsACH));
    }

    /**
     * Get ACH.
     *
     * @return Automatic Clearing House
     */
    public boolean isACH() {
        Object oo = getValue(COLUMNNAME_IsACH);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Routing No.
     *
     * @return Bank Routing Number
     */
    public String getRoutingNo() {
        return getValue(COLUMNNAME_RoutingNo);
    }

    /**
     * Set Routing No.
     *
     * @param RoutingNo Bank Routing Number
     */
    public void setRoutingNo(String RoutingNo) {
        setValue(COLUMNNAME_RoutingNo, RoutingNo);
    }

    @Override
    public int getTableId() {
        return I_C_BP_BankAccount.Table_ID;
    }
}
