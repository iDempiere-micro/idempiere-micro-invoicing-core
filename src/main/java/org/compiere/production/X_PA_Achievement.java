package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_PA_Achievement;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for PA_Achievement
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_Achievement extends BasePOName implements I_PA_Achievement {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_Achievement(int PA_Achievement_ID) {
        super(PA_Achievement_ID);
    }

    /**
     * Load Constructor
     */
    public X_PA_Achievement(Row row) {
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


    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_PA_Achievement[" + getId() + "]";
    }

    /**
     * Get Document Date.
     *
     * @return Date of the Document
     */
    public Timestamp getDateDoc() {
        return (Timestamp) getValue(COLUMNNAME_DateDoc);
    }

    /**
     * Set Document Date.
     *
     * @param DateDoc Date of the Document
     */
    public void setDateDoc(Timestamp DateDoc) {
        setValue(COLUMNNAME_DateDoc, DateDoc);
    }

    /**
     * Get Achieved.
     *
     * @return The goal is achieved
     */
    public boolean isAchieved() {
        Object oo = getValue(COLUMNNAME_IsAchieved);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Set Manual Actual.
     *
     * @param ManualActual Manually entered actual value
     */
    public void setManualActual(BigDecimal ManualActual) {
        setValue(COLUMNNAME_ManualActual, ManualActual);
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

}
