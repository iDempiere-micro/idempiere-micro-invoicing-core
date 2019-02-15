package org.compiere.accounting;

import org.compiere.model.I_C_CashBook;
import org.compiere.orm.BasePOName;
import org.idempiere.orm.I_Persistent;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for C_CashBook
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_CashBook extends BasePOName implements I_C_CashBook, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_C_CashBook(Properties ctx, int C_CashBook_ID) {
    super(ctx, C_CashBook_ID);
    /**
     * if (C_CashBook_ID == 0) { setC_CashBook_ID (0); setC_Currency_ID (0); setIsDefault (false);
     * setName (null); }
     */
  }

  /** Load Constructor */
  public X_C_CashBook(Properties ctx, ResultSet rs) {
    super(ctx, rs);
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
    StringBuffer sb = new StringBuffer("X_C_CashBook[").append(getId()).append("]");
    return sb.toString();
  }

    /**
   * Get Cash Book.
   *
   * @return Cash Book for recording petty cash transactions
   */
  public int getC_CashBook_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_CashBook_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Currency.
   *
   * @param C_Currency_ID The Currency for this record
   */
  public void setC_Currency_ID(int C_Currency_ID) {
    if (C_Currency_ID < 1) set_Value(COLUMNNAME_C_Currency_ID, null);
    else set_Value(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
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

    @Override
  public int getTableId() {
    return I_C_CashBook.Table_ID;
  }
}
