package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_I_ElementValue;
import org.compiere.orm.BasePONameValue;

/**
 * Generated Model for I_ElementValue
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_I_ElementValue extends BasePONameValue implements I_I_ElementValue {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_ElementValue(int I_ElementValue_ID) {
        super(I_ElementValue_ID);
        /** if (I_ElementValue_ID == 0) { setI_ElementValue_ID (0); setIsImported (false); } */
    }

    /**
     * Load Constructor
     */
    public X_I_ElementValue(Row row) {
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
        StringBuffer sb = new StringBuffer("X_I_ElementValue[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Account Sign.
     *
     * @return Indicates the Natural Sign of the Account as a Debit or Credit
     */
    public String getAccountSign() {
        return getValue(COLUMNNAME_AccountSign);
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
     * Set Account Element.
     *
     * @param C_ElementValue_ID Account Element
     */
    public void setElementValueId(int C_ElementValue_ID) {
        if (C_ElementValue_ID < 1) setValue(COLUMNNAME_C_ElementValue_ID, null);
        else setValue(COLUMNNAME_C_ElementValue_ID, Integer.valueOf(C_ElementValue_ID));
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
     * Get Import Account.
     *
     * @return Import Account Value
     */
    public int getIMportAccountElementValueId() {
        Integer ii = getValue(COLUMNNAME_I_ElementValue_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Document Controlled.
     *
     * @return Control account - If an account is controlled by a document, you cannot post manually
     * to it
     */
    public boolean isDocControlled() {
        Object oo = getValue(COLUMNNAME_IsDocControlled);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Summary Level.
     *
     * @return This is a summary entity
     */
    public boolean isSummary() {
        Object oo = getValue(COLUMNNAME_IsSummary);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
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
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Post Budget.
     *
     * @return Budget values can be posted
     */
    public boolean isPostBudget() {
        Object oo = getValue(COLUMNNAME_PostBudget);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Post Encumbrance.
     *
     * @return Post commitments to this account
     */
    public boolean isPostEncumbrance() {
        Object oo = getValue(COLUMNNAME_PostEncumbrance);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Post Statistical.
     *
     * @return Post statistical quantities to this account?
     */
    public boolean isPostStatistical() {
        Object oo = getValue(COLUMNNAME_PostStatistical);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    @Override
    public int getTableId() {
        return I_I_ElementValue.Table_ID;
    }
}
