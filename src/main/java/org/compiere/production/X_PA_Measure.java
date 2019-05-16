package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_PA_Measure;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for PA_Measure
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_Measure extends BasePOName implements I_PA_Measure {

    /**
     * Qty/Amount in Time = T
     */
    public static final String MEASUREDATATYPE_QtyAmountInTime = "T";
    /**
     * Status Qty/Amount = S
     */
    public static final String MEASUREDATATYPE_StatusQtyAmount = "S";
    /**
     * Manual = M
     */
    public static final String MEASURETYPE_Manual = "M";
    /**
     * Calculated = C
     */
    public static final String MEASURETYPE_Calculated = "C";
    /**
     * Achievements = A
     */
    public static final String MEASURETYPE_Achievements = "A";
    /**
     * User defined = U
     */
    public static final String MEASURETYPE_UserDefined = "U";
    /**
     * Ratio = R
     */
    public static final String MEASURETYPE_Ratio = "R";
    /**
     * Request = Q
     */
    public static final String MEASURETYPE_Request = "Q";
    /**
     * Project = P
     */
    public static final String MEASURETYPE_Project = "P";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_Measure(int PA_Measure_ID) {
        super(PA_Measure_ID);
    }

    /**
     * Load Constructor
     */
    public X_PA_Measure(Row row) {
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
        return "X_PA_Measure[" + getId() + "]";
    }

    /**
     * Get Calculation Class.
     *
     * @return Java Class for calculation, implementing Interface Measure
     */
    public String getCalculationClass() {
        return getValue(COLUMNNAME_CalculationClass);
    }

    /**
     * Get Project Type.
     *
     * @return Type of the project
     */
    public int getProjectTypeId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Manual Actual.
     *
     * @return Manually entered actual value
     */
    public BigDecimal getManualActual() {
        BigDecimal bd = getValue(COLUMNNAME_ManualActual);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Measure Data Type.
     *
     * @return Type of data - Status or in Time
     */
    public String getMeasureDataType() {
        return getValue(COLUMNNAME_MeasureDataType);
    }

    /**
     * Get Measure Type.
     *
     * @return Determines how the actual performance is derived
     */
    public String getMeasureType() {
        return getValue(COLUMNNAME_MeasureType);
    }

    /**
     * Get Measure Calculation.
     *
     * @return Calculation method for measuring performance
     */
    public int getMeasureCalcId() {
        Integer ii = getValue(COLUMNNAME_PA_MeasureCalc_ID);
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
     * Get Ratio.
     *
     * @return Performance Ratio
     */
    public int getRatioId() {
        Integer ii = getValue(COLUMNNAME_PA_Ratio_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Request Type.
     *
     * @return Type of request (e.g. Inquiry, Complaint, ..)
     */
    public int getRequestTypeId() {
        Integer ii = getValue(COLUMNNAME_R_RequestType_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_PA_Measure.Table_ID;
    }
}
