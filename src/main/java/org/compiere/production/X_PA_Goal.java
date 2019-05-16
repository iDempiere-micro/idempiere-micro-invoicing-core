package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_PA_Goal;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for PA_Goal
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_Goal extends BasePOName implements I_PA_Goal {

    /**
     * MeasureDisplay AD_Reference_ID=367
     */
    public static final int MEASUREDISPLAY_AD_Reference_ID = 367;
    /**
     * Year = 1
     */
    public static final String MEASUREDISPLAY_Year = "1";
    /**
     * Quarter = 3
     */
    public static final String MEASUREDISPLAY_Quarter = "3";
    /**
     * Month = 5
     */
    public static final String MEASUREDISPLAY_Month = "5";
    /**
     * Total = 0
     */
    public static final String MEASUREDISPLAY_Total = "0";
    /**
     * Week = 7
     */
    public static final String MEASUREDISPLAY_Week = "7";
    /**
     * Day = 8
     */
    public static final String MEASUREDISPLAY_Day = "8";
    /**
     * Year = 1
     */
    public static final String MEASURESCOPE_Year = "1";
    /**
     * Quarter = 3
     */
    public static final String MEASURESCOPE_Quarter = "3";
    /**
     * Month = 5
     */
    public static final String MEASURESCOPE_Month = "5";
    /**
     * Total = 0
     */
    public static final String MEASURESCOPE_Total = "0";
    /**
     * Week = 7
     */
    public static final String MEASURESCOPE_Week = "7";
    /**
     * Day = 8
     */
    public static final String MEASURESCOPE_Day = "8";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_Goal(int PA_Goal_ID) {
        super(PA_Goal_ID);
    }

    /**
     * Load Constructor
     */
    public X_PA_Goal(Row row) {
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
        StringBuffer sb = new StringBuffer("X_PA_Goal[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Role.
     *
     * @return Responsibility Role
     */
    public int getRoleId() {
        Integer ii = getValue(COLUMNNAME_AD_Role_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Role.
     *
     * @param AD_Role_ID Responsibility Role
     */
    public void setRoleId(int AD_Role_ID) {
        if (AD_Role_ID < 0) setValue(COLUMNNAME_AD_Role_ID, null);
        else setValue(COLUMNNAME_AD_Role_ID, Integer.valueOf(AD_Role_ID));
    }

    /**
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getUserId() {
        Integer ii = getValue(COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Date last run.
     *
     * @return Date the process was last run.
     */
    public Timestamp getDateLastRun() {
        return (Timestamp) getValue(COLUMNNAME_DateLastRun);
    }

    /**
     * Set Date last run.
     *
     * @param DateLastRun Date the process was last run.
     */
    public void setDateLastRun(Timestamp DateLastRun) {
        setValueNoCheck(COLUMNNAME_DateLastRun, DateLastRun);
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
     * Get Performance Goal.
     *
     * @return Target achievement from 0..1
     */
    public BigDecimal getGoalPerformance() {
        BigDecimal bd = getValue(COLUMNNAME_GoalPerformance);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Performance Goal.
     *
     * @param GoalPerformance Target achievement from 0..1
     */
    public void setGoalPerformance(BigDecimal GoalPerformance) {
        setValueNoCheck(COLUMNNAME_GoalPerformance, GoalPerformance);
    }

    /**
     * Set Summary Level.
     *
     * @param IsSummary This is a summary entity
     */
    public void setIsSummary(boolean IsSummary) {
        setValue(COLUMNNAME_IsSummary, Boolean.valueOf(IsSummary));
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
     * Get Measure Actual.
     *
     * @return Actual value that has been measured.
     */
    public BigDecimal getMeasureActual() {
        BigDecimal bd = getValue(COLUMNNAME_MeasureActual);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Measure Actual.
     *
     * @param MeasureActual Actual value that has been measured.
     */
    public void setMeasureActual(BigDecimal MeasureActual) {
        setValueNoCheck(COLUMNNAME_MeasureActual, MeasureActual);
    }

    /**
     * Get Measure Display.
     *
     * @return Measure Scope initially displayed
     */
    public String getMeasureDisplay() {
        return getValue(COLUMNNAME_MeasureDisplay);
    }

    /**
     * Get Measure Scope.
     *
     * @return Performance Measure Scope
     */
    public String getMeasureScope() {
        return getValue(COLUMNNAME_MeasureScope);
    }

    /**
     * Set Measure Scope.
     *
     * @param MeasureScope Performance Measure Scope
     */
    public void setMeasureScope(String MeasureScope) {

        setValue(COLUMNNAME_MeasureScope, MeasureScope);
    }

    /**
     * Get Measure Target.
     *
     * @return Target value for measure
     */
    public BigDecimal getMeasureTarget() {
        BigDecimal bd = getValue(COLUMNNAME_MeasureTarget);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Measure Target.
     *
     * @param MeasureTarget Target value for measure
     */
    public void setMeasureTarget(BigDecimal MeasureTarget) {
        setValue(COLUMNNAME_MeasureTarget, MeasureTarget);
    }

    /**
     * Get Goal.
     *
     * @return Performance Goal
     */
    public int getGoalId() {
        Integer ii = getValue(COLUMNNAME_PA_Goal_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Measure.
     *
     * @return Concrete Performance Measurement
     */
    public int getMeasureId() {
        Integer ii = getValue(COLUMNNAME_PA_Measure_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Measure.
     *
     * @param PA_Measure_ID Concrete Performance Measurement
     */
    public void setMeasureId(int PA_Measure_ID) {
        if (PA_Measure_ID < 1) setValue(COLUMNNAME_PA_Measure_ID, null);
        else setValue(COLUMNNAME_PA_Measure_ID, Integer.valueOf(PA_Measure_ID));
    }

    /**
     * Set Relative Weight.
     *
     * @param RelativeWeight Relative weight of this step (0 = ignored)
     */
    public void setRelativeWeight(BigDecimal RelativeWeight) {
        setValue(COLUMNNAME_RelativeWeight, RelativeWeight);
    }

    /**
     * Set Sequence.
     *
     * @param SeqNo Method of ordering records; lowest number comes first
     */
    public void setSeqNo(int SeqNo) {
        setValue(COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
    }

    @Override
    public int getTableId() {
        return I_PA_Goal.Table_ID;
    }
}
