package org.compiere.production;

import org.compiere.model.I_PA_Measure;
import org.compiere.orm.BasePOName;
import org.compiere.orm.MTable;
import org.idempiere.common.util.Env;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for PA_Measure
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_Measure extends BasePOName implements I_PA_Measure, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_PA_Measure(Properties ctx, int PA_Measure_ID, String trxName) {
    super(ctx, PA_Measure_ID, trxName);
    /**
     * if (PA_Measure_ID == 0) { setMeasureDataType (null); // T setMeasureType (null); // M setName
     * (null); setPA_Measure_ID (0); }
     */
  }

  /** Load Constructor */
  public X_PA_Measure(Properties ctx, ResultSet rs, String trxName) {
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

  public String toString() {
    StringBuffer sb = new StringBuffer("X_PA_Measure[").append(getId()).append("]");
    return sb.toString();
  }

  /**
   * Set Calculation Class.
   *
   * @param CalculationClass Java Class for calculation, implementing Interface Measure
   */
  public void setCalculationClass(String CalculationClass) {
    set_Value(COLUMNNAME_CalculationClass, CalculationClass);
  }

  /**
   * Get Calculation Class.
   *
   * @return Java Class for calculation, implementing Interface Measure
   */
  public String getCalculationClass() {
    return (String) get_Value(COLUMNNAME_CalculationClass);
  }

  public org.compiere.model.I_C_ProjectType getC_ProjectType() throws RuntimeException {
    return (org.compiere.model.I_C_ProjectType)
        MTable.get(getCtx(), org.compiere.model.I_C_ProjectType.Table_Name)
            .getPO(getC_ProjectType_ID(), null);
  }

  /**
   * Set Project Type.
   *
   * @param C_ProjectType_ID Type of the project
   */
  public void setC_ProjectType_ID(int C_ProjectType_ID) {
    if (C_ProjectType_ID < 1) set_Value(COLUMNNAME_C_ProjectType_ID, null);
    else set_Value(COLUMNNAME_C_ProjectType_ID, Integer.valueOf(C_ProjectType_ID));
  }

  /**
   * Get Project Type.
   *
   * @return Type of the project
   */
  public int getC_ProjectType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_ProjectType_ID);
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
   * Set Manual Actual.
   *
   * @param ManualActual Manually entered actual value
   */
  public void setManualActual(BigDecimal ManualActual) {
    set_Value(COLUMNNAME_ManualActual, ManualActual);
  }

  /**
   * Get Manual Actual.
   *
   * @return Manually entered actual value
   */
  public BigDecimal getManualActual() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ManualActual);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Note.
   *
   * @param ManualNote Note for manual entry
   */
  public void setManualNote(String ManualNote) {
    set_Value(COLUMNNAME_ManualNote, ManualNote);
  }

  /**
   * Get Note.
   *
   * @return Note for manual entry
   */
  public String getManualNote() {
    return (String) get_Value(COLUMNNAME_ManualNote);
  }

  /** MeasureDataType AD_Reference_ID=369 */
  public static final int MEASUREDATATYPE_AD_Reference_ID = 369;
  /** Qty/Amount in Time = T */
  public static final String MEASUREDATATYPE_QtyAmountInTime = "T";
  /** Status Qty/Amount = S */
  public static final String MEASUREDATATYPE_StatusQtyAmount = "S";
  /**
   * Set Measure Data Type.
   *
   * @param MeasureDataType Type of data - Status or in Time
   */
  public void setMeasureDataType(String MeasureDataType) {

    set_Value(COLUMNNAME_MeasureDataType, MeasureDataType);
  }

  /**
   * Get Measure Data Type.
   *
   * @return Type of data - Status or in Time
   */
  public String getMeasureDataType() {
    return (String) get_Value(COLUMNNAME_MeasureDataType);
  }

  /** MeasureType AD_Reference_ID=231 */
  public static final int MEASURETYPE_AD_Reference_ID = 231;
  /** Manual = M */
  public static final String MEASURETYPE_Manual = "M";
  /** Calculated = C */
  public static final String MEASURETYPE_Calculated = "C";
  /** Achievements = A */
  public static final String MEASURETYPE_Achievements = "A";
  /** User defined = U */
  public static final String MEASURETYPE_UserDefined = "U";
  /** Ratio = R */
  public static final String MEASURETYPE_Ratio = "R";
  /** Request = Q */
  public static final String MEASURETYPE_Request = "Q";
  /** Project = P */
  public static final String MEASURETYPE_Project = "P";
  /**
   * Set Measure Type.
   *
   * @param MeasureType Determines how the actual performance is derived
   */
  public void setMeasureType(String MeasureType) {

    set_Value(COLUMNNAME_MeasureType, MeasureType);
  }

  /**
   * Get Measure Type.
   *
   * @return Determines how the actual performance is derived
   */
  public String getMeasureType() {
    return (String) get_Value(COLUMNNAME_MeasureType);
  }

  public org.compiere.model.I_PA_Benchmark getPA_Benchmark() throws RuntimeException {
    return (org.compiere.model.I_PA_Benchmark)
        MTable.get(getCtx(), org.compiere.model.I_PA_Benchmark.Table_Name)
            .getPO(getPA_Benchmark_ID(), null);
  }

  /**
   * Set Benchmark.
   *
   * @param PA_Benchmark_ID Performance Benchmark
   */
  public void setPA_Benchmark_ID(int PA_Benchmark_ID) {
    if (PA_Benchmark_ID < 1) set_Value(COLUMNNAME_PA_Benchmark_ID, null);
    else set_Value(COLUMNNAME_PA_Benchmark_ID, Integer.valueOf(PA_Benchmark_ID));
  }

  /**
   * Get Benchmark.
   *
   * @return Performance Benchmark
   */
  public int getPA_Benchmark_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PA_Benchmark_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_PA_Hierarchy getPA_Hierarchy() throws RuntimeException {
    return (org.compiere.model.I_PA_Hierarchy)
        MTable.get(getCtx(), org.compiere.model.I_PA_Hierarchy.Table_Name)
            .getPO(getPA_Hierarchy_ID(), null);
  }

  /**
   * Set Reporting Hierarchy.
   *
   * @param PA_Hierarchy_ID Optional Reporting Hierarchy - If not selected the default hierarchy
   *     trees are used.
   */
  public void setPA_Hierarchy_ID(int PA_Hierarchy_ID) {
    if (PA_Hierarchy_ID < 1) set_Value(COLUMNNAME_PA_Hierarchy_ID, null);
    else set_Value(COLUMNNAME_PA_Hierarchy_ID, Integer.valueOf(PA_Hierarchy_ID));
  }

  /**
   * Get Reporting Hierarchy.
   *
   * @return Optional Reporting Hierarchy - If not selected the default hierarchy trees are used.
   */
  public int getPA_Hierarchy_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PA_Hierarchy_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_PA_MeasureCalc getPA_MeasureCalc() throws RuntimeException {
    return (org.compiere.model.I_PA_MeasureCalc)
        MTable.get(getCtx(), org.compiere.model.I_PA_MeasureCalc.Table_Name)
            .getPO(getPA_MeasureCalc_ID(), null);
  }

  /**
   * Set Measure Calculation.
   *
   * @param PA_MeasureCalc_ID Calculation method for measuring performance
   */
  public void setPA_MeasureCalc_ID(int PA_MeasureCalc_ID) {
    if (PA_MeasureCalc_ID < 1) set_Value(COLUMNNAME_PA_MeasureCalc_ID, null);
    else set_Value(COLUMNNAME_PA_MeasureCalc_ID, Integer.valueOf(PA_MeasureCalc_ID));
  }

  /**
   * Get Measure Calculation.
   *
   * @return Calculation method for measuring performance
   */
  public int getPA_MeasureCalc_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PA_MeasureCalc_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Measure.
   *
   * @param PA_Measure_ID Concrete Performance Measurement
   */
  public void setPA_Measure_ID(int PA_Measure_ID) {
    if (PA_Measure_ID < 1) set_ValueNoCheck(COLUMNNAME_PA_Measure_ID, null);
    else set_ValueNoCheck(COLUMNNAME_PA_Measure_ID, Integer.valueOf(PA_Measure_ID));
  }

  /**
   * Get Measure.
   *
   * @return Concrete Performance Measurement
   */
  public int getPA_Measure_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PA_Measure_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set PA_Measure_UU.
   *
   * @param PA_Measure_UU PA_Measure_UU
   */
  public void setPA_Measure_UU(String PA_Measure_UU) {
    set_Value(COLUMNNAME_PA_Measure_UU, PA_Measure_UU);
  }

  /**
   * Get PA_Measure_UU.
   *
   * @return PA_Measure_UU
   */
  public String getPA_Measure_UU() {
    return (String) get_Value(COLUMNNAME_PA_Measure_UU);
  }

  public org.compiere.model.I_PA_Ratio getPA_Ratio() throws RuntimeException {
    return (org.compiere.model.I_PA_Ratio)
        MTable.get(getCtx(), org.compiere.model.I_PA_Ratio.Table_Name)
            .getPO(getPA_Ratio_ID(), null);
  }

  /**
   * Set Ratio.
   *
   * @param PA_Ratio_ID Performance Ratio
   */
  public void setPA_Ratio_ID(int PA_Ratio_ID) {
    if (PA_Ratio_ID < 1) set_Value(COLUMNNAME_PA_Ratio_ID, null);
    else set_Value(COLUMNNAME_PA_Ratio_ID, Integer.valueOf(PA_Ratio_ID));
  }

  /**
   * Get Ratio.
   *
   * @return Performance Ratio
   */
  public int getPA_Ratio_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PA_Ratio_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_R_RequestType getR_RequestType() throws RuntimeException {
    return (org.compiere.model.I_R_RequestType)
        MTable.get(getCtx(), org.compiere.model.I_R_RequestType.Table_Name)
            .getPO(getR_RequestType_ID(), null);
  }

  /**
   * Set Request Type.
   *
   * @param R_RequestType_ID Type of request (e.g. Inquiry, Complaint, ..)
   */
  public void setR_RequestType_ID(int R_RequestType_ID) {
    if (R_RequestType_ID < 1) set_Value(COLUMNNAME_R_RequestType_ID, null);
    else set_Value(COLUMNNAME_R_RequestType_ID, Integer.valueOf(R_RequestType_ID));
  }

  /**
   * Get Request Type.
   *
   * @return Type of request (e.g. Inquiry, Complaint, ..)
   */
  public int getR_RequestType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_R_RequestType_ID);
    if (ii == null) return 0;
    return ii;
  }

  @Override
  public int getTableId() {
    return I_PA_Measure.Table_ID;
  }
}