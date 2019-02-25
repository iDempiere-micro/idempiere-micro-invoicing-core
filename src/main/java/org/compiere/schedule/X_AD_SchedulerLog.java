package org.compiere.schedule;

import org.compiere.model.I_AD_SchedulerLog;
import org.compiere.orm.PO;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for AD_SchedulerLog
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_SchedulerLog extends PO implements I_AD_SchedulerLog {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_SchedulerLog(Properties ctx, int AD_SchedulerLog_ID) {
        super(ctx, AD_SchedulerLog_ID);
        /**
         * if (AD_SchedulerLog_ID == 0) { setAD_Scheduler_ID (0); setAD_SchedulerLog_ID (0); setIsError
         * (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_AD_SchedulerLog(Properties ctx, ResultSet rs) {
        super(ctx, rs);
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
        StringBuffer sb = new StringBuffer("X_AD_SchedulerLog[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Set Scheduler.
     *
     * @param AD_Scheduler_ID Schedule Processes
     */
    public void setSchedulerId(int AD_Scheduler_ID) {
        if (AD_Scheduler_ID < 1) set_ValueNoCheck(COLUMNNAME_AD_Scheduler_ID, null);
        else set_ValueNoCheck(COLUMNNAME_AD_Scheduler_ID, Integer.valueOf(AD_Scheduler_ID));
    }

    /**
     * Set Error.
     *
     * @param IsError An Error occurred in the execution
     */
    public void setIsError(boolean IsError) {
        set_Value(COLUMNNAME_IsError, Boolean.valueOf(IsError));
    }

    /**
     * Set Reference.
     *
     * @param Reference Reference for this record
     */
    public void setReference(String Reference) {
        set_Value(COLUMNNAME_Reference, Reference);
    }

    /**
     * Set Summary.
     *
     * @param Summary Textual summary of this request
     */
    public void setSummary(String Summary) {
        set_Value(COLUMNNAME_Summary, Summary);
    }

    @Override
    public int getTableId() {
        return I_AD_SchedulerLog.Table_ID;
    }
}
