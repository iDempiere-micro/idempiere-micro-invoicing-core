package org.compiere.schedule;

import kotliquery.Row;
import org.compiere.model.I_AD_SchedulerLog;
import org.compiere.orm.PO;

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
    }

    /**
     * Load Constructor
     */
    public X_AD_SchedulerLog(Properties ctx, Row row) {
        super(ctx, row);
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
        if (AD_Scheduler_ID < 1) setValueNoCheck(COLUMNNAME_AD_Scheduler_ID, null);
        else setValueNoCheck(COLUMNNAME_AD_Scheduler_ID, AD_Scheduler_ID);
    }

    /**
     * Set Error.
     *
     * @param IsError An Error occurred in the execution
     */
    public void setIsError(boolean IsError) {
        setValue(COLUMNNAME_IsError, IsError);
    }

    /**
     * Set Reference.
     *
     * @param Reference Reference for this record
     */
    public void setReference(String Reference) {
        setValue(COLUMNNAME_Reference, Reference);
    }

    /**
     * Set Summary.
     *
     * @param Summary Textual summary of this request
     */
    public void setSummary(String Summary) {
        setValue(COLUMNNAME_Summary, Summary);
    }

    @Override
    public int getTableId() {
        return I_AD_SchedulerLog.Table_ID;
    }
}
