package org.compiere.wf;

import org.compiere.model.I_AD_WorkflowProcessor;
import org.compiere.orm.BasePOName;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Generated Model for AD_WorkflowProcessor
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_WorkflowProcessor extends BasePOName
        implements I_AD_WorkflowProcessor {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_WorkflowProcessor(Properties ctx, int AD_WorkflowProcessor_ID) {
        super(ctx, AD_WorkflowProcessor_ID);
        /**
         * if (AD_WorkflowProcessor_ID == 0) { setScheduleId (0); setAD_WorkflowProcessor_ID (0);
         * setKeepLogDays (0); // 7 setName (null); setSupervisorId (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_AD_WorkflowProcessor(Properties ctx, ResultSet rs) {
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
        StringBuffer sb = new StringBuffer("X_AD_WorkflowProcessor[").append(getId()).append("]");
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
     * Get Workflow Processor.
     *
     * @return Workflow Processor Server
     */
    public int getWorkflowProcessorId() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_WorkflowProcessor_ID);
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

    @Override
    public int getTableId() {
        return I_AD_WorkflowProcessor.Table_ID;
    }
}
