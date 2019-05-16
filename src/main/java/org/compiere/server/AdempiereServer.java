package org.compiere.server;

import org.compiere.model.AdempiereProcessor;
import org.compiere.model.AdempiereProcessor2;
import org.compiere.orm.MSystem;
import org.compiere.orm.PO;
import org.compiere.orm.TimeUtil;
import org.compiere.schedule.MSchedule;
import org.idempiere.common.util.CLogger;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Adempiere Server Base
 *
 * @author Jorg Janke
 * @version $Id: AdempiereServer.java,v 1.3 2006/10/09 00:23:26 jjanke Exp $
 */
public abstract class AdempiereServer implements Runnable {

    /**
     * System
     */
    protected static volatile MSystem p_system = null;
    /**
     * The Processor Model
     */
    protected volatile AdempiereProcessor p_model;
    /**
     * Milliseconds to sleep - 0 Sec default
     */
    protected long m_sleepMS = 0;
    /**
     * Server start time
     */
    protected long m_start = 0;
    /**
     * Number of Work executions
     */
    protected int p_runCount = 0;
    /**
     * Tine start of work
     */
    protected long p_startWork = 0;
    /**
     * Logger
     */
    protected CLogger log = CLogger.getCLogger(getClass());
    /**
     * Sleeping
     */
    private volatile boolean m_sleeping = true;
    /**
     * Number MS of last Run
     */
    private long m_runLastMS = 0;
    /**
     * Number of MS total
     */
    private long m_runTotalMS = 0;
    /**
     * When to run next
     */
    private long m_nextWork = 0;

    public void run() {
        final Thread currentThread = Thread.currentThread();
        final String oldThreadName = currentThread.getName();
        String newThreadName = getName();
        boolean renamed = false;
        if (!oldThreadName.equals(newThreadName)) {
            try {
                currentThread.setName(newThreadName);
                renamed = true;
            } catch (SecurityException e) {
            }
        }

        Properties context = new Properties();
        // DAP: Env.setContext("#clientId", p_model.getClientId());
        if (p_model instanceof PO) {
            PO po = (PO) p_model;
            if (po.getColumnIndex("AD_Org_ID") >= 0) ;
                // DAP: Env.setContext("#orgId", po.getValueAsInt("AD_Org_ID"));
            if (po.getColumnIndex("AD_User_ID") >= 0) ;
            // DAP: Env.setContext("#AD_User_ID", po.getValueAsInt("AD_User_ID"));
        }

        try {
            // DAP ServerContext.setCurrentInstance(context);
            m_sleeping = false;
            doRun();
        } finally {
            m_sleeping = true;
            // DAP ServerContext.dispose();
            if (renamed) {
                // Revert the name back if the current thread was renamed.
                // We do not check the exception here because we know it works.
                currentThread.setName(oldThreadName);
            }
        }
    }

    /**
     * *********************************************************************** Run async
     */
    protected void doRun() {
        if (m_start == 0) m_start = System.currentTimeMillis();

        //	---------------
        p_startWork = System.currentTimeMillis();
        doWork();
        long now = System.currentTimeMillis();
        //	---------------

        p_runCount++;
        m_runLastMS = now - p_startWork;
        m_runTotalMS += m_runLastMS;

        // Finished work - calculate datetime for next run
        Timestamp lastRun = new Timestamp(now);
        if (p_model instanceof AdempiereProcessor2) {
            AdempiereProcessor2 ap = (AdempiereProcessor2) p_model;
            if (ap.isIgnoreProcessingTime()) {
                lastRun = new Timestamp(p_startWork);
            }
        }

        m_nextWork =
                MSchedule.getNextRunMS(
                        lastRun.getTime(),
                        p_model.getScheduleType(),
                        p_model.getFrequencyType(),
                        p_model.getFrequency(),
                        p_model.getCronPattern());

        m_sleepMS = m_nextWork - now;
        if (log.isLoggable(Level.INFO))
            log.info(" Next run: " + new Timestamp(m_nextWork) + " sleep " + m_sleepMS);
        //
        p_model.setDateLastRun(lastRun);
        p_model.setDateNextRun(new Timestamp(m_nextWork));
        p_model.saveEx();
    } //	run

    /**
     * Get Run Statistics
     *
     * @return Statistic info
     */
    public String getStatistics() {
        return "Run #"
                + p_runCount
                + " - Last="
                + TimeUtil.formatElapsed(m_runLastMS)
                + " - Total="
                + TimeUtil.formatElapsed(m_runTotalMS)
                + " - Next "
                + TimeUtil.formatElapsed(m_nextWork - System.currentTimeMillis());
    } //	getStatistics

    /**
     * Do the actual Work
     */
    protected abstract void doWork();

    /**
     * Get the date Next run
     *
     * @param requery requery database
     * @return date next run
     */
    public Timestamp getDateNextRun(boolean requery) {
        return p_model.getDateNextRun(requery);
    } //	getDateNextRun

    /**
     * Get the date Last run
     *
     * @return date lext run
     */
    public Timestamp getDateLastRun() {
        return p_model.getDateLastRun();
    } //	getDateLastRun

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuffer sb =
                new StringBuffer()
                        .append("Sleeping=")
                        .append(m_sleeping)
                        .append(",Last=")
                        .append(getDateLastRun());
        if (m_sleeping) sb.append(",Next=").append(getDateNextRun(false));
        return sb.toString();
    } //	toString

    public String getName() {
        return p_model.getName();
    }

} //	AdempiereServer
