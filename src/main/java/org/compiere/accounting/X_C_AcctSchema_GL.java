package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_AcctSchema_GL;
import org.compiere.orm.PO;

/**
 * Generated Model for C_AcctSchema_GL
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_AcctSchema_GL extends PO implements I_C_AcctSchema_GL {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_AcctSchema_GL(int C_AcctSchema_GL_ID) {
        super(C_AcctSchema_GL_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_AcctSchema_GL(Row row) {
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

    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_AcctSchema_GL[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
    }

    /**
     * Get Currency Balancing Acct.
     *
     * @return Account used when a currency is out of balance
     */
    public int getCurrencyBalancingAccount() {
        Integer ii = getValue(COLUMNNAME_CurrencyBalancing_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Intercompany Due From Acct.
     *
     * @return Intercompany Due From / Receivables Account
     */
    public int getIntercompanyDueFromAccount() {
        Integer ii = getValue(COLUMNNAME_IntercompanyDueFrom_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Intercompany Due To Acct.
     *
     * @return Intercompany Due To / Payable Account
     */
    public int getIntercompanyDueToAccount() {
        Integer ii = getValue(COLUMNNAME_IntercompanyDueTo_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Suspense Balancing Acct.
     *
     * @return Suspense Balancing Acct
     */
    public int getSuspenseBalancingAccount() {
        Integer ii = getValue(COLUMNNAME_SuspenseBalancing_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Use Currency Balancing.
     *
     * @return Use Currency Balancing
     */
    public boolean isUseCurrencyBalancing() {
        Object oo = getValue(COLUMNNAME_UseCurrencyBalancing);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Use Currency Balancing.
     *
     * @param UseCurrencyBalancing Use Currency Balancing
     */
    public void setUseCurrencyBalancing(boolean UseCurrencyBalancing) {
        setValue(COLUMNNAME_UseCurrencyBalancing, Boolean.valueOf(UseCurrencyBalancing));
    }

    /**
     * Get Use Suspense Balancing.
     *
     * @return Use Suspense Balancing
     */
    public boolean isUseSuspenseBalancing() {
        Object oo = getValue(COLUMNNAME_UseSuspenseBalancing);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Use Suspense Balancing.
     *
     * @param UseSuspenseBalancing Use Suspense Balancing
     */
    public void setUseSuspenseBalancing(boolean UseSuspenseBalancing) {
        setValue(COLUMNNAME_UseSuspenseBalancing, Boolean.valueOf(UseSuspenseBalancing));
    }

    /**
     * Set Use Suspense Error.
     *
     * @param UseSuspenseError Use Suspense Error
     */
    public void setUseSuspenseError(boolean UseSuspenseError) {
        setValue(COLUMNNAME_UseSuspenseError, Boolean.valueOf(UseSuspenseError));
    }

    @Override
    public int getTableId() {
        return I_C_AcctSchema_GL.Table_ID;
    }
}
