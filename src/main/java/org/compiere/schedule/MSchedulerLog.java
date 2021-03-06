package org.compiere.schedule;

import kotliquery.Row;
import org.compiere.model.AdempiereProcessorLog;

/**
 * Scheduler Log
 *
 * @author Jorg Janke
 * @version $Id: MSchedulerLog.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class MSchedulerLog extends X_AD_SchedulerLog implements AdempiereProcessorLog {
    /**
     *
     */
    private static final long serialVersionUID = -8105976307507562851L;

    /**
     * Standard Constructor
     *
     * @param ctx                context
     * @param AD_SchedulerLog_ID id
     */
    public MSchedulerLog(int AD_SchedulerLog_ID) {
        super(AD_SchedulerLog_ID);
        if (AD_SchedulerLog_ID == 0) setIsError(false);
    } //	MSchedulerLog

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MSchedulerLog(Row row) {
        super(row);
    } //	MSchedulerLog

    /**
     * Parent Constructor
     *
     * @param parent  parent
     * @param summary summary
     */
    public MSchedulerLog(MScheduler parent, String summary) {
        this(0);
        setClientOrg(parent);
        setSchedulerId(parent.getSchedulerId());
        setSummary(summary);
    } //	MSchedulerLog
} //	MSchedulerLog
