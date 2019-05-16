package org.compiere.schedule;

import kotliquery.Row;
import org.compiere.model.Scheduler;
import org.compiere.orm.BasePOName;

import java.sql.Timestamp;

/**
 * Generated Model for AD_Scheduler
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_Scheduler extends BasePOName implements Scheduler {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_Scheduler(int AD_Scheduler_ID) {
        super(AD_Scheduler_ID);
    }

    /**
     * Load Constructor
     */
    public X_AD_Scheduler(Row row) {
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
        return "X_AD_Scheduler[" + getId() + "]";
    }

    /**
     * Get Process.
     *
     * @return Process or Report
     */
    public int getProcessId() {
        Integer ii = getValue(COLUMNNAME_AD_Process_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Schedule.
     *
     * @return Schedule
     */
    public int getScheduleId() {
        Integer ii = getValue(COLUMNNAME_AD_Schedule_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Scheduler.
     *
     * @return Schedule Processes
     */
    public int getSchedulerId() {
        Integer ii = getValue(COLUMNNAME_AD_Scheduler_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Table.
     *
     * @return Database Table information
     */
    public int getDBTableId() {
        Integer ii = getValue(COLUMNNAME_AD_Table_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Date next run.
     *
     * @return Date the process will run next
     */
    public Timestamp getDateNextRun() {
        return (Timestamp) getValue(COLUMNNAME_DateNextRun);
    }

    /**
     * Set Date next run.
     *
     * @param DateNextRun Date the process will run next
     */
    public void setDateNextRun(Timestamp DateNextRun) {
        setValue(COLUMNNAME_DateNextRun, DateNextRun);
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
     * Get Days to keep Log.
     *
     * @return Number of days to keep the log entries
     */
    public int getKeepLogDays() {
        Integer ii = getValue(COLUMNNAME_KeepLogDays);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Days to keep Log.
     *
     * @param KeepLogDays Number of days to keep the log entries
     */
    public void setKeepLogDays(int KeepLogDays) {
        setValue(COLUMNNAME_KeepLogDays, Integer.valueOf(KeepLogDays));
    }

    /**
     * Get Record ID.
     *
     * @return Direct internal record ID
     */
    public int getRecordId() {
        Integer ii = getValue(COLUMNNAME_Record_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Record ID.
     *
     * @param Record_ID Direct internal record ID
     */
    public void setRecordId(int Record_ID) {
        if (Record_ID < 0) setValue(COLUMNNAME_Record_ID, null);
        else setValue(COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
    }

    /**
     * Get Mail Template.
     *
     * @return Text templates for mailings
     */
    public int getMailTextId() {
        Integer ii = getValue(COLUMNNAME_R_MailText_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Supervisor.
     *
     * @return Supervisor for this user/organization - used for escalation and approval
     */
    public int getSupervisorId() {
        Integer ii = getValue(COLUMNNAME_Supervisor_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return Scheduler.Table_ID;
    }
}
