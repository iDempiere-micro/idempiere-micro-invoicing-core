package org.idempiere.process;

import org.compiere.model.I_C_AcctProcessor;
import org.compiere.orm.BasePOName;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

public class X_C_AcctProcessor extends BasePOName implements I_C_AcctProcessor {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_AcctProcessor(Properties ctx, int C_AcctProcessor_ID) {
        super(ctx, C_AcctProcessor_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_AcctProcessor(Properties ctx, ResultSet rs) {
        super(ctx, rs);
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
        StringBuffer sb = new StringBuffer("X_C_AcctProcessor[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Schedule.
     *
     * @return Schedule
     */
    public int getScheduleId() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_Schedule_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Schedule.
     *
     * @param AD_Schedule_ID Schedule
     */
    public void setScheduleId(int AD_Schedule_ID) {
        if (AD_Schedule_ID < 1) set_Value(COLUMNNAME_AD_Schedule_ID, null);
        else set_Value(COLUMNNAME_AD_Schedule_ID, Integer.valueOf(AD_Schedule_ID));
    }

    /**
     * Get Accounting Processor.
     *
     * @return Accounting Processor/Server Parameters
     */
    public int getAccountingProcessorId() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_AcctProcessor_ID);
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
        set_Value(COLUMNNAME_DateNextRun, DateNextRun);
    }

    /**
     * Set Days to keep Log.
     *
     * @param KeepLogDays Number of days to keep the log entries
     */
    public void setKeepLogDays(int KeepLogDays) {
        set_Value(COLUMNNAME_KeepLogDays, Integer.valueOf(KeepLogDays));
    }

    /**
     * Set Supervisor.
     *
     * @param Supervisor_ID Supervisor for this user/organization - used for escalation and approval
     */
    public void setSupervisorId(int Supervisor_ID) {
        if (Supervisor_ID < 1) set_Value(COLUMNNAME_Supervisor_ID, null);
        else set_Value(COLUMNNAME_Supervisor_ID, Integer.valueOf(Supervisor_ID));
    }
}
