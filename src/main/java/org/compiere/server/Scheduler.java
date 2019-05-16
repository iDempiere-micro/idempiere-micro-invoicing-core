package org.compiere.server;

import org.compiere.accounting.MClientKt;
import org.compiere.crm.MUser;
import org.compiere.crm.MUserKt;
import org.compiere.model.ClientWithAccounting;
import org.compiere.orm.MOrgInfo;
import org.compiere.orm.MOrgInfoKt;
import org.compiere.orm.MRole;
import org.compiere.orm.TimeUtil;
import org.compiere.process.MPInstance;
import org.compiere.process.MPInstancePara;
import org.compiere.process.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.schedule.MScheduler;
import org.compiere.schedule.MSchedulerLog;
import org.compiere.schedule.MSchedulerPara;
import org.compiere.util.DisplayType;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Scheduler
 *
 * @author Jorg Janke
 * @version $Id: Scheduler.java,v 1.5 2006/07/30 00:53:33 jjanke Exp $
 * <p>Contributors: Carlos Ruiz - globalqss - FR [3135351] - Enable Scheduler for buttons
 */
public class Scheduler extends AdempiereServer {

    /**
     * The Concrete Model
     */
    protected MScheduler m_model = null;
    /**
     * Last Summary
     */
    protected StringBuffer m_summary = new StringBuffer();

    /**
     * Work
     */
    protected void doWork() {
        m_summary = new StringBuffer(m_model.toString()).append(" - ");

        // Prepare a ctx for the report/process - BF [1966880]
        ClientWithAccounting schedclient = MClientKt.getClientWithAccounting(m_model.getClientId());
        // DAP: Env.setContext("#clientId", schedclient.getClientId());
        // DAP: Env.setContext("#AD_Language", schedclient.getADLanguage());
        // DAP: Env.setContext("#orgId", m_model.getOrgId());
        if (m_model.getOrgId() != 0) {
            MOrgInfo schedorg = MOrgInfoKt.getOrganizationInfo(m_model.getOrgId());
            if (schedorg.getWarehouseId() > 0);
            // DAP: Env.setContext("#M_Warehouse_ID", schedorg.getWarehouseId());
        }
        // DAP: Env.setContext("#AD_User_ID", getUserId());
        // DAP: Env.setContext("#SalesRep_ID", getUserId());
        // TODO: It can be convenient to add  AD_Scheduler.AD_Role_ID
        MUser scheduser = MUserKt.getUser(getUserId());
        MRole[] schedroles = scheduser.getRoles(m_model.getOrgId());
        if (schedroles.length > 0) {
            // DAP: Env.setContext(
            // DAP:         "#AD_Role_ID",
            // DAP:         schedroles[0].getRoleId()); // first role, ordered by AD_Role_ID
        }
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat4Timestamp = new SimpleDateFormat("yyyy-MM-dd");
        // DAP: Env.setContext(
        // DAP:         "#Date", dateFormat4Timestamp.format(ts) + " 00:00:00"); //  JDBC format

        MProcess process = new MProcess(m_model.getProcessId());
        try {
            m_summary.append(runProcess(process));
        } catch (Throwable e) {
            log.log(Level.WARNING, process.toString(), e);
            m_summary.append(e.toString());
            throw new Error(e.toString());
        }

        //
        int no = m_model.deleteLog();
        m_summary.append(" Logs deleted=").append(no);
        //
        MSchedulerLog pLog = new MSchedulerLog(m_model, m_summary.toString());
        pLog.setReference(
                "#"
                        + p_runCount
                        + " - "
                        + TimeUtil.formatElapsed(new Timestamp(p_startWork)));
        pLog.saveEx();
    } //	doWork

    /**
     * Run Process or Report
     *
     * @param process process
     * @return summary
     * @throws Exception
     */
    protected String runProcess(MProcess process) throws Exception {
        if (log.isLoggable(Level.INFO)) log.info(process.toString());

        //	Process (see also MWFActivity.performWork
        int AD_Table_ID = m_model.getDBTableId();
        int Record_ID = m_model.getRecordId();
        //
        MPInstance pInstance = new MPInstance(process, Record_ID);
        fillParameter(pInstance);
        //
        ProcessInfo pi =
                new ProcessInfo(process.getName(), process.getProcessId(), AD_Table_ID, Record_ID);
        pi.setUserId(getUserId());
        pi.setADClientID(m_model.getClientId());
        pi.setProcessInstanceId(pInstance.getPInstanceId());
        pi.setIsBatch(true);
        pi.setPrintPreview(true);

        pi.setTransactionName(null);
        ServerProcessCtl.process(pi);

        return pi.getSummary();
    } //	runProcess

    protected int getUserId() {
        int AD_User_ID;
        if (m_model.getSupervisorId() > 0) AD_User_ID = m_model.getSupervisorId();
        else if (m_model.getCreatedBy() > 0) AD_User_ID = m_model.getCreatedBy();
        else if (m_model.getUpdatedBy() > 0) AD_User_ID = m_model.getUpdatedBy();
        else AD_User_ID = 100; // fall back to SuperUser
        return AD_User_ID;
    }

    /**
     * Fill Parameter
     *
     * @param pInstance process instance
     */
    protected void fillParameter(MPInstance pInstance) {
        MSchedulerPara[] sParams = m_model.getParameters(false);
        MPInstancePara[] iParams = pInstance.getParameters();
        for (MPInstancePara iPara : iParams) {
            for (MSchedulerPara sPara : sParams) {
                if (iPara.getParameterName().equals(sPara.getColumnName())) {
                    String paraDesc = sPara.getDescription();
                    if (paraDesc != null && paraDesc.trim().length() > 0)
                        iPara.setInfo(sPara.getDescription());
                    String variable = sPara.getParameterDefault();
                    String toVariable = sPara.getParameterToDefault();
                    if (log.isLoggable(Level.FINE)) log.fine(sPara.getColumnName() + " = " + variable);
                    //	Value - Constant/Variable
                    Object value = parseVariable(sPara, variable);
                    Object toValue = toVariable != null ? parseVariable(sPara, toVariable) : null;

                    //	No Value
                    if (value == null && toValue == null) {
                        if (log.isLoggable(Level.FINE)) log.fine(sPara.getColumnName() + " - empty");
                        break;
                    }

                    //	Convert to Type
                    try {
                        if (DisplayType.isNumeric(sPara.getDisplayType())
                                || DisplayType.isID(sPara.getDisplayType())) {
                            DecimalFormat decimalFormat = DisplayType.getNumberFormat(sPara.getDisplayType());
                            BigDecimal bd = toBigDecimal(value);
                            iPara.setProcessNumber(bd);
                            if (toValue != null) {
                                bd = toBigDecimal(toValue);
                                iPara.setProcessNumberTo(bd);
                            }
                            if (Util.isEmpty(paraDesc)) {
                                String info = decimalFormat.format(iPara.getProcessNumber());
                                if (iPara.getProcessNumberTo() != null)
                                    info = info + " - " + decimalFormat.format(iPara.getProcessNumberTo());
                                iPara.setInfo(info);
                            }
                            if (log.isLoggable(Level.FINE))
                                log.fine(sPara.getColumnName() + " = " + variable + " (=" + bd + "=)");
                        } else if (DisplayType.isDate(sPara.getDisplayType())) {
                            SimpleDateFormat dateFormat = DisplayType.getDateFormat(sPara.getDisplayType());
                            Timestamp ts = toTimestamp(value);
                            iPara.setProcessDate(ts);
                            if (toValue != null) {
                                ts = toTimestamp(toValue);
                                iPara.setProcessDateTo(ts);
                            }
                            if (Util.isEmpty(paraDesc)) {
                                String info = dateFormat.format(iPara.getProcessDate());
                                if (iPara.getProcessDateTo() != null) {
                                    info = info + " - " + dateFormat.format(iPara.getProcessDateTo());
                                }
                                iPara.setInfo(info);
                            }
                            if (log.isLoggable(Level.FINE))
                                log.fine(sPara.getColumnName() + " = " + variable + " (=" + ts + "=)");
                        } else {
                            iPara.setProcessString(value.toString());
                            if (toValue != null) {
                                iPara.setProcessStringTo(toValue.toString());
                            }
                            if (Util.isEmpty(paraDesc)) {
                                String info = iPara.getProcessString();
                                if (iPara.getProcessStringTo() != null) {
                                    info = info + " - " + iPara.getProcessStringTo();
                                }
                                iPara.setInfo(info);
                            }
                            if (log.isLoggable(Level.FINE))
                                log.fine(
                                        sPara.getColumnName()
                                                + " = "
                                                + variable
                                                + " (="
                                                + value
                                                + "=) "
                                                + value.getClass().getName());
                        }
                        if (!iPara.save()) log.warning("Not Saved - " + sPara.getColumnName());
                    } catch (Exception e) {
                        log.warning(
                                sPara.getColumnName()
                                        + " = "
                                        + variable
                                        + " ("
                                        + value
                                        + ") "
                                        + value.getClass().getName()
                                        + " - "
                                        + e.getLocalizedMessage());
                    }
                    break;
                } //	parameter match
            } //	scheduler parameter loop
        } //	instance parameter loop
    } //	fillParameter

    private Timestamp toTimestamp(Object value) {
        Timestamp ts;
        if (value instanceof Timestamp) ts = (Timestamp) value;
        else ts = Timestamp.valueOf(value.toString());
        return ts;
    }

    private BigDecimal toBigDecimal(Object value) {
        BigDecimal bd;
        if (value instanceof BigDecimal) bd = (BigDecimal) value;
        else if (value instanceof Integer) bd = new BigDecimal((Integer) value);
        else bd = new BigDecimal(value.toString());
        return bd;
    }

    private Object parseVariable(MSchedulerPara sPara, String variable) {
        Object value = variable;
        if (variable == null || variable.length() == 0) value = null;
        else if (variable.startsWith("@SQL=")) {
            String defStr = "";
            String sql = variable.substring(5); // 	w/o tag
            sql = Env.parseContext(0, sql, false, false); // 	replace variables
            if (sql.equals(""))
                log.log(
                        Level.WARNING,
                        "(" + sPara.getColumnName() + ") - Default SQL variable parse failed: " + variable);
            else {
                PreparedStatement stmt;
                ResultSet rs;
                try {
                    stmt = prepareStatement(sql);
                    rs = stmt.executeQuery();
                    if (rs.next()) defStr = rs.getString(1);
                    else {
                        if (log.isLoggable(Level.INFO))
                            log.log(Level.INFO, "(" + sPara.getColumnName() + ") - no Result: " + sql);
                    }
                } catch (SQLException e) {
                    log.log(Level.WARNING, "(" + sPara.getColumnName() + ") " + sql, e);
                }
            }
            if (!Util.isEmpty(defStr)) value = defStr;
        } //	SQL Statement
        else if (variable.indexOf('@') != -1
                && variable.indexOf('@') != variable.lastIndexOf('@')) // 	we have a variable / BF [1926032]
        {
            //	Strip
            int index = variable.indexOf('@');
            String columnName = variable.substring(index + 1);
            index = columnName.indexOf('@');
            if (index == -1) {
                log.warning(sPara.getColumnName() + " - cannot evaluate=" + variable);
                return null;
            }
            String tail = index < (columnName.length() - 1) ? columnName.substring(index + 1) : null;
            columnName = columnName.substring(0, index);
            //	try Env
            String env = Env.getContext(columnName);
            if (env == null || env.length() == 0) env = Env.getContext(columnName);
            if (env.length() == 0) {
                log.warning(
                        sPara.getColumnName() + " - not in environment =" + columnName + "(" + variable + ")");
                return null;
            } else value = env;

            if (tail != null && columnName.equals("#Date")) {
                tail = tail.trim();
                if (tail.startsWith("-") || tail.startsWith("+")) {
                    boolean negate = tail.startsWith("-");
                    int type = Calendar.DATE;
                    tail = tail.substring(1);
                    if (tail.endsWith("d")) {
                        tail = tail.substring(0, tail.length() - 1);
                    } else if (tail.endsWith("m")) {
                        type = Calendar.MONTH;
                        tail = tail.substring(0, tail.length() - 1);
                    } else if (tail.endsWith("y")) {
                        type = Calendar.YEAR;
                        tail = tail.substring(0, tail.length() - 1);
                    }

                    int toApply = 0;
                    try {
                        toApply = Integer.parseInt(tail);
                    } catch (Exception ignored) {
                    }
                    if (toApply > 0) {
                        if (negate) toApply = toApply * -1;
                        Timestamp ts = toTimestamp(value);
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(ts.getTime());
                        cal.add(type, toApply);
                        value = new Timestamp(cal.getTimeInMillis());
                    }
                }
            }
        } //	@variable@
        return value;
    }

} //	Scheduler
