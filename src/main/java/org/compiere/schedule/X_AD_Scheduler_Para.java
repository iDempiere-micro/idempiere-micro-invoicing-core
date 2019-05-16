package org.compiere.schedule;

import kotliquery.Row;
import org.compiere.model.SchedulerParameter;
import org.compiere.orm.PO;

/**
 * Generated Model for AD_Scheduler_Para
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_Scheduler_Para extends PO implements SchedulerParameter {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_Scheduler_Para(int AD_Scheduler_Para_ID) {
        super(AD_Scheduler_Para_ID);
    }

    /**
     * Load Constructor
     */
    public X_AD_Scheduler_Para(Row row) {
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
        return "X_AD_Scheduler_Para[" + getId() + "]";
    }

    /**
     * Get Process Parameter.
     *
     * @return Process Parameter
     */
    public int getProcessParameterId() {
        Integer ii = getValue(COLUMNNAME_AD_Process_Para_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Get Default Parameter.
     *
     * @return Default value of the parameter
     */
    public String getParameterDefault() {
        return getValue(COLUMNNAME_ParameterDefault);
    }

    /**
     * Get Default To Parameter.
     *
     * @return Default value of the to parameter
     */
    public String getParameterToDefault() {
        return getValue(COLUMNNAME_ParameterToDefault);
    }

    @Override
    public int getTableId() {
        return SchedulerParameter.Table_ID;
    }
}
