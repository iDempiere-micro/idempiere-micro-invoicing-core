package org.compiere.accounting;

import org.compiere.model.I_C_ElementValue;
import org.compiere.orm.BasePONameValue;
import org.compiere.orm.MTable;
import org.idempiere.orm.I_Persistent;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for C_ElementValue
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_ElementValue extends BasePONameValue implements I_C_ElementValue, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_C_ElementValue(Properties ctx, int C_ElementValue_ID, String trxName) {
    super(ctx, C_ElementValue_ID, trxName);
  }

  /** Load Constructor */
  public X_C_ElementValue(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
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

  /** AccountSign AD_Reference_ID=118 */
  public static final int ACCOUNTSIGN_AD_Reference_ID = 118;
  /** Natural = N */
  public static final String ACCOUNTSIGN_Natural = "N";
  /** Debit = D */
  public static final String ACCOUNTSIGN_Debit = "D";
  /** Credit = C */
  public static final String ACCOUNTSIGN_Credit = "C";
  /**
   * Set Account Sign.
   *
   * @param AccountSign Indicates the Natural Sign of the Account as a Debit or Credit
   */
  public void setAccountSign(String AccountSign) {

    set_Value(COLUMNNAME_AccountSign, AccountSign);
  }

    /** AccountType AD_Reference_ID=117 */
  public static final int ACCOUNTTYPE_AD_Reference_ID = 117;
  /** Asset = A */
  public static final String ACCOUNTTYPE_Asset = "A";
  /** Liability = L */
  public static final String ACCOUNTTYPE_Liability = "L";
  /** Revenue = R */
  public static final String ACCOUNTTYPE_Revenue = "R";
  /** Expense = E */
  public static final String ACCOUNTTYPE_Expense = "E";
  /** Owner's Equity = O */
  public static final String ACCOUNTTYPE_OwnerSEquity = "O";
  /** Memo = M */
  public static final String ACCOUNTTYPE_Memo = "M";
  /**
   * Set Account Type.
   *
   * @param AccountType Indicates the type of account
   */
  public void setAccountType(String AccountType) {

    set_Value(COLUMNNAME_AccountType, AccountType);
  }

  /**
   * Get Account Type.
   *
   * @return Indicates the type of account
   */
  public String getAccountType() {
    return (String) get_Value(COLUMNNAME_AccountType);
  }

  /** BPartnerType AD_Reference_ID=200076 */
  public static final int BPARTNERTYPE_AD_Reference_ID = 200076;
  /** Customer = C */
  public static final String BPARTNERTYPE_Customer = "C";
  /** Vendor = V */
  public static final String BPARTNERTYPE_Vendor = "V";
  /** Employee = E */
  public static final String BPARTNERTYPE_Employee = "E";

    public org.compiere.model.I_C_BankAccount getC_BankAccount() throws RuntimeException {
    return (org.compiere.model.I_C_BankAccount)
        MTable.get(getCtx(), org.compiere.model.I_C_BankAccount.Table_Name)
            .getPO(getC_BankAccount_ID(), null);
  }

    /**
   * Get Bank Account.
   *
   * @return Account at the Bank
   */
  public int getC_BankAccount_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_BankAccount_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException {
    return (org.compiere.model.I_C_Currency)
        MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_Name)
            .getPO(getC_Currency_ID(), null);
  }

    /**
   * Get Currency.
   *
   * @return The Currency for this record
   */
  public int getC_Currency_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_Currency_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_C_Element getC_Element() throws RuntimeException {
    return (org.compiere.model.I_C_Element)
        MTable.get(getCtx(), org.compiere.model.I_C_Element.Table_Name)
            .getPO(getC_Element_ID(), null);
  }

  /**
   * Set Element.
   *
   * @param C_Element_ID Accounting Element
   */
  public void setC_Element_ID(int C_Element_ID) {
    if (C_Element_ID < 1) set_ValueNoCheck(COLUMNNAME_C_Element_ID, null);
    else set_ValueNoCheck(COLUMNNAME_C_Element_ID, C_Element_ID);
  }

  /**
   * Get Element.
   *
   * @return Accounting Element
   */
  public int getC_Element_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_Element_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Get Account Element.
   *
   * @return Account Element
   */
  public int getC_ElementValue_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_ElementValue_ID);
    if (ii == null) return 0;
    return ii;
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
   * Get Description.
   *
   * @return Optional short description of the record
   */
  public String getDescription() {
    return (String) get_Value(COLUMNNAME_Description);
  }

  /**
   * Set Bank Account.
   *
   * @param IsBankAccount Indicates if this is the Bank Account
   */
  public void setIsBankAccount(boolean IsBankAccount) {
    set_Value(COLUMNNAME_IsBankAccount, IsBankAccount);
  }

    /**
   * Set Document Controlled.
   *
   * @param IsDocControlled Control account - If an account is controlled by a document, you cannot
   *     post manually to it
   */
  public void setIsDocControlled(boolean IsDocControlled) {
    set_Value(COLUMNNAME_IsDocControlled, IsDocControlled);
  }

  /**
   * Get Document Controlled.
   *
   * @return Control account - If an account is controlled by a document, you cannot post manually
   *     to it
   */
  public boolean isDocControlled() {
    Object oo = get_Value(COLUMNNAME_IsDocControlled);
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
   *     currency
   */
  public void setIsForeignCurrency(boolean IsForeignCurrency) {
    set_Value(COLUMNNAME_IsForeignCurrency, IsForeignCurrency);
  }

    /**
   * Set Summary Level.
   *
   * @param IsSummary This is a summary entity
   */
  public void setIsSummary(boolean IsSummary) {
    set_Value(COLUMNNAME_IsSummary, IsSummary);
  }

  /**
   * Get Summary Level.
   *
   * @return This is a summary entity
   */
  public boolean isSummary() {
    Object oo = get_Value(COLUMNNAME_IsSummary);
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
    set_Value(COLUMNNAME_PostActual, PostActual);
  }

  /**
   * Get Post Actual.
   *
   * @return Actual Values can be posted
   */
  public boolean isPostActual() {
    Object oo = get_Value(COLUMNNAME_PostActual);
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
    set_Value(COLUMNNAME_PostBudget, PostBudget);
  }

  /**
   * Get Post Budget.
   *
   * @return Budget values can be posted
   */
  public boolean isPostBudget() {
    Object oo = get_Value(COLUMNNAME_PostBudget);
    if (oo != null) {
      if (oo instanceof Boolean) return (Boolean) oo;
      return "Y".equals(oo);
    }
    return false;
  }

  /**
   * Set Post Encumbrance.
   *
   * @param PostEncumbrance Post commitments to this account
   */
  public void setPostEncumbrance(boolean PostEncumbrance) {
    set_Value(COLUMNNAME_PostEncumbrance, PostEncumbrance);
  }

    /**
   * Set Post Statistical.
   *
   * @param PostStatistical Post statistical quantities to this account?
   */
  public void setPostStatistical(boolean PostStatistical) {
    set_Value(COLUMNNAME_PostStatistical, PostStatistical);
  }

  /**
   * Get Post Statistical.
   *
   * @return Post statistical quantities to this account?
   */
  public boolean isPostStatistical() {
    Object oo = get_Value(COLUMNNAME_PostStatistical);
    if (oo != null) {
      if (oo instanceof Boolean) return (Boolean) oo;
      return "Y".equals(oo);
    }
    return false;
  }

    @Override
  public int getTableId() {
    return I_C_ElementValue.Table_ID;
  }
}
