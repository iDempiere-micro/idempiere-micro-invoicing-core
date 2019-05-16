package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingElement;
import org.compiere.model.AccountingElementValue;
import org.compiere.orm.BasePONameValue;
import software.hsharp.core.orm.MBaseTableKt;

/**
 * Generated Model for C_ElementValue
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_ElementValue extends BasePONameValue implements AccountingElementValue {

    /**
     * Natural = N
     */
    public static final String ACCOUNTSIGN_Natural = "N";
    /**
     * Asset = A
     */
    public static final String ACCOUNTTYPE_Asset = "A";
    /**
     * Liability = L
     */
    public static final String ACCOUNTTYPE_Liability = "L";
    /**
     * Expense = E
     */
    public static final String ACCOUNTTYPE_Expense = "E";
    /**
     * Owner's Equity = O
     */
    public static final String ACCOUNTTYPE_OwnerSEquity = "O";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_ElementValue(int C_ElementValue_ID) {
        super(C_ElementValue_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_ElementValue(Row row) {
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
        return "X_C_ElementValue[" + getId() + "]";
    }

    /**
     * Set Account Sign.
     *
     * @param AccountSign Indicates the Natural Sign of the Account as a Debit or Credit
     */
    public void setAccountSign(String AccountSign) {

        setValue(COLUMNNAME_AccountSign, AccountSign);
    }

    /**
     * Get Account Type.
     *
     * @return Indicates the type of account
     */
    public String getAccountType() {
        return getValue(COLUMNNAME_AccountType);
    }

    /**
     * Set Account Type.
     *
     * @param AccountType Indicates the type of account
     */
    public void setAccountType(String AccountType) {

        setValue(COLUMNNAME_AccountType, AccountType);
    }

    public AccountingElement getElement() throws RuntimeException {
        return (AccountingElement)
                MBaseTableKt.getTable(AccountingElement.Table_Name)
                        .getPO(getElementId());
    }

    /**
     * Get Element.
     *
     * @return Accounting Element
     */
    public int getElementId() {
        Integer ii = getValue(COLUMNNAME_C_Element_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Element.
     *
     * @param C_Element_ID Accounting Element
     */
    public void setElementId(int C_Element_ID) {
        if (C_Element_ID < 1) setValueNoCheck(COLUMNNAME_C_Element_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Element_ID, C_Element_ID);
    }

    /**
     * Get Account Element.
     *
     * @return Account Element
     */
    public int getElementValueId() {
        Integer ii = getValue(COLUMNNAME_C_ElementValue_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Bank Account.
     *
     * @param IsBankAccount Indicates if this is the Bank Account
     */
    public void setIsBankAccount(boolean IsBankAccount) {
        setValue(COLUMNNAME_IsBankAccount, IsBankAccount);
    }

    /**
     * Set Document Controlled.
     *
     * @param IsDocControlled Control account - If an account is controlled by a document, you cannot
     *                        post manually to it
     */
    public void setIsDocControlled(boolean IsDocControlled) {
        setValue(COLUMNNAME_IsDocControlled, IsDocControlled);
    }

    /**
     * Get Document Controlled.
     *
     * @return Control account - If an account is controlled by a document, you cannot post manually
     * to it
     */
    public boolean isDocControlled() {
        Object oo = getValue(COLUMNNAME_IsDocControlled);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Foreign Currency Account.
     *
     * @param IsForeignCurrency Balances in foreign currency accounts are held in the nominated
     *                          currency
     */
    public void setIsForeignCurrency(boolean IsForeignCurrency) {
        setValue(COLUMNNAME_IsForeignCurrency, IsForeignCurrency);
    }

    /**
     * Set Summary Level.
     *
     * @param IsSummary This is a summary entity
     */
    public void setIsSummary(boolean IsSummary) {
        setValue(COLUMNNAME_IsSummary, IsSummary);
    }

    /**
     * Get Summary Level.
     *
     * @return This is a summary entity
     */
    public boolean isSummary() {
        Object oo = getValue(COLUMNNAME_IsSummary);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Post Actual.
     *
     * @return Actual Values can be posted
     */
    public boolean isPostActual() {
        Object oo = getValue(COLUMNNAME_PostActual);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Post Actual.
     *
     * @param PostActual Actual Values can be posted
     */
    public void setPostActual(boolean PostActual) {
        setValue(COLUMNNAME_PostActual, PostActual);
    }

    /**
     * Get Post Budget.
     *
     * @return Budget values can be posted
     */
    public boolean isPostBudget() {
        Object oo = getValue(COLUMNNAME_PostBudget);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Post Budget.
     *
     * @param PostBudget Budget values can be posted
     */
    public void setPostBudget(boolean PostBudget) {
        setValue(COLUMNNAME_PostBudget, PostBudget);
    }

    /**
     * Set Post Encumbrance.
     *
     * @param PostEncumbrance Post commitments to this account
     */
    public void setPostEncumbrance(boolean PostEncumbrance) {
        setValue(COLUMNNAME_PostEncumbrance, PostEncumbrance);
    }

    /**
     * Get Post Statistical.
     *
     * @return Post statistical quantities to this account?
     */
    public boolean isPostStatistical() {
        Object oo = getValue(COLUMNNAME_PostStatistical);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Post Statistical.
     *
     * @param PostStatistical Post statistical quantities to this account?
     */
    public void setPostStatistical(boolean PostStatistical) {
        setValue(COLUMNNAME_PostStatistical, PostStatistical);
    }

    @Override
    public int getTableId() {
        return AccountingElementValue.Table_ID;
    }
}
