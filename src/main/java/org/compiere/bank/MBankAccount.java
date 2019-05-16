package org.compiere.bank;

import kotliquery.Row;
import org.compiere.model.I_C_BankAccount;
import org.compiere.orm.MSysConfig;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;
import org.jetbrains.annotations.NotNull;

/**
 * Bank Account Model
 *
 * @author Jorg Janke
 * @version $Id: MBankAccount.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MBankAccount extends X_C_BankAccount {
    /**
     *
     */
    private static final long serialVersionUID = -110709935374907275L;
    /**
     * Cache
     */
    private static CCache<Integer, MBankAccount> s_cache =
            new CCache<>(I_C_BankAccount.Table_Name, 5);

    /**
     * Bank Account Model
     *
     * @param C_BankAccount_ID bank account
     */
    public MBankAccount(int C_BankAccount_ID) {
        super(C_BankAccount_ID);
        if (C_BankAccount_ID == 0) {
            setIsDefault(false);
            setBankAccountType(X_C_BankAccount.BANKACCOUNTTYPE_Checking);
            setCurrentBalance(Env.ZERO);
            //	setCurrencyId (0);
            setCreditLimit(Env.ZERO);
            //	setBankAccountId (0);
        }
    } //	MBankAccount

    /**
     * Bank Account Model
     */
    public MBankAccount(Row row) {
        super(row);
    } //	MBankAccount

    /**
     * Get BankAccount from Cache
     *
     * @param C_BankAccount_ID id
     * @return MBankAccount
     */
    public static MBankAccount get(int C_BankAccount_ID) {
        Integer key = C_BankAccount_ID;
        MBankAccount retValue = s_cache.get(key);
        if (retValue != null) return retValue;
        retValue = new MBankAccount(C_BankAccount_ID);
        if (retValue.getId() != 0) s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * String representation
     *
     * @return info
     */
    public String toString() {
        return "MBankAccount[" +
                getId() +
                "-" +
                getAccountNo() +
                "]";
    } //	toString

    /**
     * Get Bank
     *
     * @return bank parent
     */
    public MBank getBank() {
        return MBank.get(getBankId());
    } //	getBank

    /**
     * Get Bank Name and Account No
     *
     * @return Bank/Account
     */
    @NotNull
    public String getName() {
        return getBank().getName() + " " + getAccountNo();
    } //	getName

    /**
     * Before Save
     *
     * @param newRecord new record
     * @return success
     */
    protected boolean beforeSave(boolean newRecord) {

        if (MSysConfig.getBooleanValue(
                MSysConfig.IBAN_VALIDATION, true, Env.getClientId())) {
            if (!Util.isEmpty(getIBAN())) {
                setIBAN(IBAN.normalizeIBAN(getIBAN()));
                if (!IBAN.isValid(getIBAN())) {
                    log.saveError("Error", "IBAN is invalid");
                    return false;
                }
            }
        }

        return true;
    } // beforeSave

    /**
     * After Save
     *
     * @param newRecord new record
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (newRecord && success)
            return insertAccounting("C_BankAccount_Acct", "C_AcctSchema_Default", null);
        return success;
    } //	afterSave
} //	MBankAccount
