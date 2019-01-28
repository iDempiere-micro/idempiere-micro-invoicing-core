package org.compiere.invoicing;

import org.compiere.model.I_C_DocTypeCounter;
import org.compiere.orm.BasePOName;
import org.idempiere.orm.I_Persistent;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for C_DocTypeCounter
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_DocTypeCounter extends BasePOName implements I_C_DocTypeCounter, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_C_DocTypeCounter(Properties ctx, int C_DocTypeCounter_ID, String trxName) {
    super(ctx, C_DocTypeCounter_ID, trxName);
  }

  /** Load Constructor */
  public X_C_DocTypeCounter(Properties ctx, ResultSet rs, String trxName) {
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
    return "X_C_DocTypeCounter[" + getId() + "]";
  }

    /**
   * Set Document Type.
   *
   * @param C_DocType_ID Document type or rules
   */
  public void setC_DocType_ID(int C_DocType_ID) {
    if (C_DocType_ID < 0) set_Value(COLUMNNAME_C_DocType_ID, null);
    else set_Value(COLUMNNAME_C_DocType_ID, C_DocType_ID);
  }

  /**
   * Get Document Type.
   *
   * @return Document type or rules
   */
  public int getC_DocType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_DocType_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Counter Document Type.
   *
   * @param Counter_C_DocType_ID Generated Counter Document Type (To)
   */
  public void setCounter_C_DocType_ID(int Counter_C_DocType_ID) {
    if (Counter_C_DocType_ID < 1) set_Value(COLUMNNAME_Counter_C_DocType_ID, null);
    else set_Value(COLUMNNAME_Counter_C_DocType_ID, Counter_C_DocType_ID);
  }

  /**
   * Get Counter Document Type.
   *
   * @return Generated Counter Document Type (To)
   */
  public int getCounter_C_DocType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_Counter_C_DocType_ID);
    if (ii == null) return 0;
    return ii;
  }

    /** DocAction AD_Reference_ID=135 */
  public static final int DOCACTION_AD_Reference_ID = 135;
  /** Complete = CO */
  public static final String DOCACTION_Complete = "CO";
  /** Approve = AP */
  public static final String DOCACTION_Approve = "AP";
  /** Reject = RJ */
  public static final String DOCACTION_Reject = "RJ";
  /** Post = PO */
  public static final String DOCACTION_Post = "PO";
  /** Void = VO */
  public static final String DOCACTION_Void = "VO";
  /** Close = CL */
  public static final String DOCACTION_Close = "CL";
  /** Reverse - Correct = RC */
  public static final String DOCACTION_Reverse_Correct = "RC";
  /** Reverse - Accrual = RA */
  public static final String DOCACTION_Reverse_Accrual = "RA";
  /** Invalidate = IN */
  public static final String DOCACTION_Invalidate = "IN";
  /** Re-activate = RE */
  public static final String DOCACTION_Re_Activate = "RE";
  /** <None> = -- */
  public static final String DOCACTION_None = "--";
  /** Prepare = PR */
  public static final String DOCACTION_Prepare = "PR";
  /** Unlock = XL */
  public static final String DOCACTION_Unlock = "XL";
  /** Wait Complete = WC */
  public static final String DOCACTION_WaitComplete = "WC";

    /**
   * Get Document Action.
   *
   * @return The targeted status of the document
   */
  public String getDocAction() {
    return (String) get_Value(COLUMNNAME_DocAction);
  }

  /**
   * Set Create Counter Document.
   *
   * @param IsCreateCounter Create Counter Document
   */
  public void setIsCreateCounter(boolean IsCreateCounter) {
    set_Value(COLUMNNAME_IsCreateCounter, IsCreateCounter);
  }

  /**
   * Get Create Counter Document.
   *
   * @return Create Counter Document
   */
  public boolean isCreateCounter() {
    Object oo = get_Value(COLUMNNAME_IsCreateCounter);
    if (oo != null) {
      if (oo instanceof Boolean) return (Boolean) oo;
      return "Y".equals(oo);
    }
    return false;
  }

  /**
   * Set Valid.
   *
   * @param IsValid Element is valid
   */
  public void setIsValid(boolean IsValid) {
    set_Value(COLUMNNAME_IsValid, IsValid);
  }

  /**
   * Get Valid.
   *
   * @return Element is valid
   */
  public boolean isValid() {
    Object oo = get_Value(COLUMNNAME_IsValid);
    if (oo != null) {
      if (oo instanceof Boolean) return (Boolean) oo;
      return "Y".equals(oo);
    }
    return false;
  }

    @Override
  public int getTableId() {
    return I_C_DocTypeCounter.Table_ID;
  }
}
