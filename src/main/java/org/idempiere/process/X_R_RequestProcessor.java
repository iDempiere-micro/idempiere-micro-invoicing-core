package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_R_RequestProcessor;
import org.compiere.orm.BasePOName;

import java.sql.Timestamp;

public class X_R_RequestProcessor extends BasePOName implements I_R_RequestProcessor {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_RequestProcessor(int R_RequestProcessor_ID) {
        super(R_RequestProcessor_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_RequestProcessor(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_R_RequestProcessor[" + getId() + "]";
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
     * Set Schedule.
     *
     * @param AD_Schedule_ID Schedule
     */
    public void setScheduleId(int AD_Schedule_ID) {
        if (AD_Schedule_ID < 1) setValue(COLUMNNAME_AD_Schedule_ID, null);
        else setValue(COLUMNNAME_AD_Schedule_ID, AD_Schedule_ID);
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
     * Set Days to keep Log.
     *
     * @param KeepLogDays Number of days to keep the log entries
     */
    public void setKeepLogDays(int KeepLogDays) {
        setValue(COLUMNNAME_KeepLogDays, Integer.valueOf(KeepLogDays));
    }

    /**
     * Set Alert after Days Due.
     *
     * @param OverdueAlertDays Send email alert after number of days due (0=no alerts)
     */
    public void setOverdueAlertDays(int OverdueAlertDays) {
        setValue(COLUMNNAME_OverdueAlertDays, Integer.valueOf(OverdueAlertDays));
    }

    /**
     * Set Escalate after Days Due.
     *
     * @param OverdueAssignDays Escalation to superior after number of due days (0 = no)
     */
    public void setOverdueAssignDays(int OverdueAssignDays) {
        setValue(COLUMNNAME_OverdueAssignDays, Integer.valueOf(OverdueAssignDays));
    }

    /**
     * Set Reminder Days.
     *
     * @param RemindDays Days between sending Reminder Emails for a due or inactive Document
     */
    public void setRemindDays(int RemindDays) {
        setValue(COLUMNNAME_RemindDays, Integer.valueOf(RemindDays));
    }

    /**
     * Get Request Processor.
     *
     * @return Processor for Requests
     */
    public int getRequestProcessorId() {
        Integer ii = getValue(COLUMNNAME_R_RequestProcessor_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Supervisor.
     *
     * @param Supervisor_ID Supervisor for this user/organization - used for escalation and approval
     */
    public void setSupervisorId(int Supervisor_ID) {
        if (Supervisor_ID < 1) setValue(COLUMNNAME_Supervisor_ID, null);
        else setValue(COLUMNNAME_Supervisor_ID, Integer.valueOf(Supervisor_ID));
    }
}
