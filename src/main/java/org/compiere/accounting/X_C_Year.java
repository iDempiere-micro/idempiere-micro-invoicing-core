package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_Year;
import org.compiere.orm.PO;

import java.util.Properties;

/**
 * Generated Model for C_Year
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_Year extends PO implements I_C_Year {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Year(Properties ctx, int C_Year_ID) {
        super(ctx, C_Year_ID);
        /* if (C_Year_ID == 0) { setC_Calendar_ID (0); setC_Year_ID (0); setFiscalYear (null); } */
    }

    /**
     * Load Constructor
     */
    public X_C_Year(Properties ctx, Row row) {
        super(ctx, row);
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
        return "X_C_Year[" + getId() + "]";
    }

    /**
     * Get Calendar.
     *
     * @return Accounting Calendar Name
     */
    public int getC_Calendar_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Calendar_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Calendar.
     *
     * @param C_Calendar_ID Accounting Calendar Name
     */
    public void setC_Calendar_ID(int C_Calendar_ID) {
        if (C_Calendar_ID < 1) setValueNoCheck(COLUMNNAME_C_Calendar_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Calendar_ID, Integer.valueOf(C_Calendar_ID));
    }

    /**
     * Get Year.
     *
     * @return Calendar Year
     */
    public int getC_Year_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Year_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Year.
     *
     * @return The Fiscal Year
     */
    public String getFiscalYear() {
        return (String) getValue(COLUMNNAME_FiscalYear);
    }

    /**
     * Set Year.
     *
     * @param FiscalYear The Fiscal Year
     */
    public void setFiscalYear(String FiscalYear) {
        setValue(COLUMNNAME_FiscalYear, FiscalYear);
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Boolean.valueOf(Processing));
    }

    @Override
    public int getTableId() {
        return I_C_Year.Table_ID;
    }
}
