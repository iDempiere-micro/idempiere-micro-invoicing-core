package org.idempiere.process;

import org.compiere.model.I_I_Conversion_Rate;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.KeyNamePair;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

public class X_I_Conversion_Rate extends PO implements I_I_Conversion_Rate, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_I_Conversion_Rate(Properties ctx, int I_Conversion_Rate_ID, String trxName) {
    super(ctx, I_Conversion_Rate_ID, trxName);
    /** if (I_Conversion_Rate_ID == 0) { setI_Conversion_Rate_ID (0); } */
  }

  /** Load Constructor */
  public X_I_Conversion_Rate(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }

  /**
   * AccessLevel
   *
   * @return 6 - System - Client
   */
  protected int getAccessLevel() {
    return accessLevel.intValue();
  }

  @Override
  public int getTableId() {
    return Table_ID;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("X_I_Conversion_Rate[").append(getId()).append("]");
    return sb.toString();
  }

    /**
   * Set Conversion Rate.
   *
   * @param C_Conversion_Rate_ID Rate used for converting currencies
   */
  public void setC_Conversion_Rate_ID(int C_Conversion_Rate_ID) {
    if (C_Conversion_Rate_ID < 1) set_Value(COLUMNNAME_C_Conversion_Rate_ID, null);
    else set_Value(COLUMNNAME_C_Conversion_Rate_ID, Integer.valueOf(C_Conversion_Rate_ID));
  }

  /**
   * Get Conversion Rate.
   *
   * @return Rate used for converting currencies
   */
  public int getC_Conversion_Rate_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_Conversion_Rate_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Get Currency Type.
   *
   * @return Currency Conversion Rate Type
   */
  public int getC_ConversionType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_ConversionType_ID);
    if (ii == null) return 0;
    return ii;
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

    /**
   * Get Currency To.
   *
   * @return Target currency
   */
  public int getC_Currency_ID_To() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_Currency_ID_To);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Get Create Reciprocal Rate.
   *
   * @return Create Reciprocal Rate from current information
   */
  public boolean isCreateReciprocalRate() {
    Object oo = get_Value(COLUMNNAME_CreateReciprocalRate);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
  }

    /**
   * Get Divide Rate.
   *
   * @return To convert Source number to Target number, the Source is divided
   */
  public BigDecimal getDivideRate() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_DivideRate);
    if (bd == null) return Env.ZERO;
    return bd;
  }

    /**
   * Get Import Conversion Rate.
   *
   * @return Import Currency Conversion Rate
   */
  public int getI_Conversion_Rate_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_I_Conversion_Rate_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Get Record ID/ColumnName
   *
   * @return ID/ColumnName pair
   */
  public KeyNamePair getKeyNamePair() {
    return new KeyNamePair(getId(), String.valueOf(getI_Conversion_Rate_ID()));
  }

    /**
   * Set Imported.
   *
   * @param I_IsImported Has this import been processed
   */
  public void setI_IsImported(boolean I_IsImported) {
    set_Value(COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
  }

    /**
   * Get Multiply Rate.
   *
   * @return Rate to multiple the source by to calculate the target.
   */
  public BigDecimal getMultiplyRate() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_MultiplyRate);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Processed.
   *
   * @param Processed The document has been processed
   */
  public void setProcessed(boolean Processed) {
    set_Value(COLUMNNAME_Processed, Boolean.valueOf(Processed));
  }

    /**
   * Get Valid from.
   *
   * @return Valid from including this date (first day)
   */
  public Timestamp getValidFrom() {
    return (Timestamp) get_Value(COLUMNNAME_ValidFrom);
  }

    /**
   * Get Valid to.
   *
   * @return Valid to including this date (last day)
   */
  public Timestamp getValidTo() {
    return (Timestamp) get_Value(COLUMNNAME_ValidTo);
  }
}
