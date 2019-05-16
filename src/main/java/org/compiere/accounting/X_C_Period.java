package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_Period;
import org.compiere.orm.BasePOName;
import software.hsharp.core.orm.MBaseTableKt;

import java.sql.Timestamp;

/**
 * Generated Model for C_Period
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_Period extends BasePOName implements I_C_Period {

    /**
     * Standard Calendar Period = S
     */
    public static final String PERIODTYPE_StandardCalendarPeriod = "S";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Period(int C_Period_ID) {
        super(C_Period_ID);
        /**
         * if (C_Period_ID == 0) { setPeriodId (0); setYearId (0); setName (null); setPeriodNo
         * (0); setPeriodType (null); // S setStartDate (new Timestamp( System.currentTimeMillis() )); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_Period(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_Period[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Period.
     *
     * @return Period of the Calendar
     */
    public int getPeriodId() {
        Integer ii = getValue(COLUMNNAME_C_Period_ID);
        if (ii == null) return 0;
        return ii;
    }

    public org.compiere.model.I_C_Year getYear() throws RuntimeException {
        return (org.compiere.model.I_C_Year)
                MBaseTableKt.getTable(org.compiere.model.I_C_Year.Table_Name)
                        .getPO(getYearId());
    }

    /**
     * Get Year.
     *
     * @return Calendar Year
     */
    public int getYearId() {
        Integer ii = getValue(COLUMNNAME_C_Year_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Year.
     *
     * @param C_Year_ID Calendar Year
     */
    public void setYearId(int C_Year_ID) {
        if (C_Year_ID < 1) setValueNoCheck(COLUMNNAME_C_Year_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Year_ID, C_Year_ID);
    }

    /**
     * Get End Date.
     *
     * @return Last effective date (inclusive)
     */
    public Timestamp getEndDate() {
        return (Timestamp) getValue(COLUMNNAME_EndDate);
    }

    /**
     * Set End Date.
     *
     * @param EndDate Last effective date (inclusive)
     */
    public void setEndDate(Timestamp EndDate) {
        setValue(COLUMNNAME_EndDate, EndDate);
    }

    /**
     * Set Period No.
     *
     * @param PeriodNo Unique Period Number
     */
    public void setPeriodNo(int PeriodNo) {
        setValue(COLUMNNAME_PeriodNo, Integer.valueOf(PeriodNo));
    }

    /**
     * Get Period Type.
     *
     * @return Period Type
     */
    public String getPeriodType() {
        return getValue(COLUMNNAME_PeriodType);
    }

    /**
     * Set Period Type.
     *
     * @param PeriodType Period Type
     */
    public void setPeriodType(String PeriodType) {

        setValueNoCheck(COLUMNNAME_PeriodType, PeriodType);
    }

    /**
     * Get Start Date.
     *
     * @return First effective day (inclusive)
     */
    public Timestamp getStartDate() {
        return (Timestamp) getValue(COLUMNNAME_StartDate);
    }

    /**
     * Set Start Date.
     *
     * @param StartDate First effective day (inclusive)
     */
    public void setStartDate(Timestamp StartDate) {
        setValue(COLUMNNAME_StartDate, StartDate);
    }

    @Override
    public int getTableId() {
        return I_C_Period.Table_ID;
    }
}
