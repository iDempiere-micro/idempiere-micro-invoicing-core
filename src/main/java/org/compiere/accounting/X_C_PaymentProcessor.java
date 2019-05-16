package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_PaymentProcessor;
import org.compiere.orm.BasePOName;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for C_PaymentProcessor
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_PaymentProcessor extends BasePOName implements I_C_PaymentProcessor {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_PaymentProcessor(int C_PaymentProcessor_ID) {
        super(C_PaymentProcessor_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_PaymentProcessor(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 6 - System - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_C_PaymentProcessor[" + getId() + "]";
    }

    /**
     * Get Accept AMEX.
     *
     * @return Accept American Express Card
     */
    public boolean isAcceptAMEX() {
        Object oo = getValue(COLUMNNAME_AcceptAMEX);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept AMEX.
     *
     * @param AcceptAMEX Accept American Express Card
     */
    public void setAcceptAMEX(boolean AcceptAMEX) {
        setValue(COLUMNNAME_AcceptAMEX, Boolean.valueOf(AcceptAMEX));
    }

    /**
     * Get Accept ATM.
     *
     * @return Accept Bank ATM Card
     */
    public boolean isAcceptATM() {
        Object oo = getValue(COLUMNNAME_AcceptATM);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept ATM.
     *
     * @param AcceptATM Accept Bank ATM Card
     */
    public void setAcceptATM(boolean AcceptATM) {
        setValue(COLUMNNAME_AcceptATM, Boolean.valueOf(AcceptATM));
    }

    /**
     * Get Accept Electronic Check.
     *
     * @return Accept ECheck (Electronic Checks)
     */
    public boolean isAcceptCheck() {
        Object oo = getValue(COLUMNNAME_AcceptCheck);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Electronic Check.
     *
     * @param AcceptCheck Accept ECheck (Electronic Checks)
     */
    public void setAcceptCheck(boolean AcceptCheck) {
        setValue(COLUMNNAME_AcceptCheck, Boolean.valueOf(AcceptCheck));
    }

    /**
     * Get Accept Corporate.
     *
     * @return Accept Corporate Purchase Cards
     */
    public boolean isAcceptCorporate() {
        Object oo = getValue(COLUMNNAME_AcceptCorporate);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Corporate.
     *
     * @param AcceptCorporate Accept Corporate Purchase Cards
     */
    public void setAcceptCorporate(boolean AcceptCorporate) {
        setValue(COLUMNNAME_AcceptCorporate, Boolean.valueOf(AcceptCorporate));
    }

    /**
     * Get Accept Diners.
     *
     * @return Accept Diner's Club
     */
    public boolean isAcceptDiners() {
        Object oo = getValue(COLUMNNAME_AcceptDiners);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Diners.
     *
     * @param AcceptDiners Accept Diner's Club
     */
    public void setAcceptDiners(boolean AcceptDiners) {
        setValue(COLUMNNAME_AcceptDiners, Boolean.valueOf(AcceptDiners));
    }

    /**
     * Get Accept Direct Debit.
     *
     * @return Accept Direct Debits (vendor initiated)
     */
    public boolean isAcceptDirectDebit() {
        Object oo = getValue(COLUMNNAME_AcceptDirectDebit);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Direct Debit.
     *
     * @param AcceptDirectDebit Accept Direct Debits (vendor initiated)
     */
    public void setAcceptDirectDebit(boolean AcceptDirectDebit) {
        setValue(COLUMNNAME_AcceptDirectDebit, Boolean.valueOf(AcceptDirectDebit));
    }

    /**
     * Get Accept Direct Deposit.
     *
     * @return Accept Direct Deposit (payee initiated)
     */
    public boolean isAcceptDirectDeposit() {
        Object oo = getValue(COLUMNNAME_AcceptDirectDeposit);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Direct Deposit.
     *
     * @param AcceptDirectDeposit Accept Direct Deposit (payee initiated)
     */
    public void setAcceptDirectDeposit(boolean AcceptDirectDeposit) {
        setValue(COLUMNNAME_AcceptDirectDeposit, Boolean.valueOf(AcceptDirectDeposit));
    }

    /**
     * Get Accept Discover.
     *
     * @return Accept Discover Card
     */
    public boolean isAcceptDiscover() {
        Object oo = getValue(COLUMNNAME_AcceptDiscover);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Discover.
     *
     * @param AcceptDiscover Accept Discover Card
     */
    public void setAcceptDiscover(boolean AcceptDiscover) {
        setValue(COLUMNNAME_AcceptDiscover, Boolean.valueOf(AcceptDiscover));
    }

    /**
     * Get Accept MasterCard.
     *
     * @return Accept Master Card
     */
    public boolean isAcceptMC() {
        Object oo = getValue(COLUMNNAME_AcceptMC);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept MasterCard.
     *
     * @param AcceptMC Accept Master Card
     */
    public void setAcceptMC(boolean AcceptMC) {
        setValue(COLUMNNAME_AcceptMC, Boolean.valueOf(AcceptMC));
    }

    /**
     * Get Accept Visa.
     *
     * @return Accept Visa Cards
     */
    public boolean isAcceptVisa() {
        Object oo = getValue(COLUMNNAME_AcceptVisa);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Accept Visa.
     *
     * @param AcceptVisa Accept Visa Cards
     */
    public void setAcceptVisa(boolean AcceptVisa) {
        setValue(COLUMNNAME_AcceptVisa, Boolean.valueOf(AcceptVisa));
    }

    public org.compiere.model.I_C_BankAccount getBankAccount() throws RuntimeException {
        return (org.compiere.model.I_C_BankAccount)
                MBaseTableKt.getTable(org.compiere.model.I_C_BankAccount.Table_Name)
                        .getPO(getBankAccountId());
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
        if (C_BankAccount_ID < 1) setValueNoCheck(COLUMNNAME_C_BankAccount_ID, null);
        else setValueNoCheck(COLUMNNAME_C_BankAccount_ID, Integer.valueOf(C_BankAccount_ID));
    }

    /**
     * Set Commission %.
     *
     * @param Commission Commission stated as a percentage
     */
    public void setCommission(BigDecimal Commission) {
        setValue(COLUMNNAME_Commission, Commission);
    }

    /**
     * Set Cost per transaction.
     *
     * @param CostPerTrx Fixed cost per transaction
     */
    public void setCostPerTrx(BigDecimal CostPerTrx) {
        setValue(COLUMNNAME_CostPerTrx, CostPerTrx);
    }

    /**
     * Get Payment Processor Class.
     *
     * @return Payment Processor Java Class
     */
    public String getPayProcessorClass() {
        return getValue(COLUMNNAME_PayProcessorClass);
    }

    /**
     * Set Require CreditCard Verification Code.
     *
     * @param RequireVV Require 3/4 digit Credit Verification Code
     */
    public void setRequireVV(boolean RequireVV) {
        setValue(COLUMNNAME_RequireVV, Boolean.valueOf(RequireVV));
    }

    @Override
    public int getTableId() {
        return I_C_PaymentProcessor.Table_ID;
    }
}
