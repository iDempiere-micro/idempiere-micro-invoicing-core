package org.compiere.server;

import org.compiere.model.Column;
import org.compiere.model.IProcessInfo;
import org.compiere.model.Server;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.compiere.process.DocAction;
import org.compiere.process.MPInstance;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessUtil;
import org.compiere.rule.MRule;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.io.InvalidClassException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

public class ServerProcessCtl implements Runnable {

    /**
     * Static Logger
     */
    private static CLogger log = CLogger.getCLogger(ServerProcessCtl.class);

    /**
     * Process Info
     */
    IProcessInfo m_pi;

    private boolean m_IsServerProcess = false;

    /**
     * ************************************************************************ Constructor
     *
     * @param pi Process info
     */
    public ServerProcessCtl(ProcessInfo pi) {
        m_pi = pi;
    } //  ProcessCtl

    /**
     * Process Control <code>
     * - Get Instance ID
     * - Get Parameters
     * - execute (lock - start process - unlock)
     * </code> Creates a ProcessCtl instance, which calls lockUI and unlockUI if parent is a
     * ASyncProcess <br>
     * Called from APanel.cmd_print, APanel.actionButton and VPaySelect.cmd_generate
     *
     * @param pi ProcessInfo process info
     * @return worker started ProcessCtl instance or null for workflow
     */
    public static ServerProcessCtl process(ProcessInfo pi) {
        if (log.isLoggable(Level.FINE)) log.fine("ServerProcess - " + pi);

        MPInstance instance = null;
        if (pi.getPInstanceId() <= 0) {
            try {
                instance = new MPInstance(pi.getProcessId(), pi.getRecordId());
            } catch (Exception e) {
                pi.setSummary(e.getLocalizedMessage());
                pi.setError(true);
                log.warning(pi.toString());
                return null;
            } catch (Error e) {
                pi.setSummary(e.getLocalizedMessage());
                pi.setError(true);
                log.warning(pi.toString());
                return null;
            }
            if (!instance.save()) {
                pi.setSummary(MsgKt.getMsg("ProcessNoInstance"));
                pi.setError(true);
                return null;
            }
            pi.setProcessInstanceId(instance.getPInstanceId());
        } else {
            instance = new MPInstance(pi.getPInstanceId(), null);
        }

        //	execute
        ServerProcessCtl worker = new ServerProcessCtl(pi);
        worker.run();

        return worker;
    } //	execute

    /**
     * Get Server
     *
     * @return Server
     */
    public static Server getServer() {
        return new ServerBean();
    } //	getServer

    /**
     * @param po
     * @param docAction
     * @return ProcessInfo
     */
    public static ProcessInfo runDocumentActionWorkflow(PO po, String docAction) {
        int AD_Table_ID = po.getTableId();
        MTable table = MBaseTableKt.getTable(AD_Table_ID);
        Column column = table.getColumn("DocAction");
        if (column == null) return null;
        if (!docAction.equals(po.getValue(column.getColumnName()))) {
            po.setValueOfColumn(column.getColumnName(), docAction);
            po.saveEx();
        }
        ProcessInfo processInfo =
                new ProcessInfo(
                        ((DocAction) po).getDocumentInfo(),
                        column.getProcessId(),
                        po.getTableId(),
                        po.getId());
        processInfo.setTransactionName(null);
        processInfo.setPO(po);
        ServerProcessCtl.process(processInfo);
        return processInfo;
    }

    /**
     * Execute Process Instance and Lock UI. Calls lockUI and unlockUI if parent is a ASyncProcess
     *
     * <pre>
     * 	- Get Process Information
     *      - Call Class
     * 	- Submit SQL Procedure
     * 	- Run SQL Procedure
     * </pre>
     */
    public void run() {
        if (log.isLoggable(Level.FINE))
            log.fine(
                    "AD_PInstance_ID=" + m_pi.getPInstanceId() + ", Record_ID=" + m_pi.getRecordId());

        //	Get Process Information: Name, Procedure Name, ClassName, IsReport, IsDirectPrint
        String ProcedureName = "";
        String JasperReport = "";
        int AD_ReportView_ID = 0;
        int AD_Workflow_ID = 0;
        boolean IsReport = false;

        //
        String sql =
                "SELECT p.Name, p.ProcedureName,p.ClassName, p.AD_Process_ID," //	1..4
                        + " p.isReport,p.IsDirectPrint,p.AD_ReportView_ID,p.AD_Workflow_ID," //	5..8
                        + " CASE WHEN COALESCE(p.Statistic_Count,0)=0 THEN 0 ELSE p.Statistic_Seconds/p.Statistic_Count END CASE,"
                        + " p.IsServerProcess, p.JasperReport "
                        + "FROM AD_Process p"
                        + " INNER JOIN AD_PInstance i ON (p.AD_Process_ID=i.AD_Process_ID) "
                        + "WHERE p.IsActive='Y'"
                        + " AND i.AD_PInstance_ID=?";
        if (!Env.isBaseLanguage())
            sql =
                    "SELECT t.Name, p.ProcedureName,p.ClassName, p.AD_Process_ID," //	1..4
                            + " p.isReport, p.IsDirectPrint,p.AD_ReportView_ID,p.AD_Workflow_ID," //	5..8
                            + " CASE WHEN COALESCE(p.Statistic_Count,0)=0 THEN 0 ELSE p.Statistic_Seconds/p.Statistic_Count END CASE,"
                            + " p.IsServerProcess, p.JasperReport "
                            + "FROM AD_Process p"
                            + " INNER JOIN AD_PInstance i ON (p.AD_Process_ID=i.AD_Process_ID) "
                            + " INNER JOIN AD_Process_Trl t ON (p.AD_Process_ID=t.AD_Process_ID"
                            + " AND t.AD_Language='"
                            + Env.getADLanguage()
                            + "') "
                            + "WHERE p.IsActive='Y'"
                            + " AND i.AD_PInstance_ID=?";
        //
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt =
                    prepareStatement(sql);
            pstmt.setInt(1, m_pi.getPInstanceId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                m_pi.setTitle(rs.getString(1));
                ProcedureName = rs.getString(2);
                m_pi.setClassName(rs.getString(3));
                m_pi.setProcessId(rs.getInt(4));
                //	Report
                if ("Y".equals(rs.getString(5))) {
                    IsReport = true;
                }
                AD_ReportView_ID = rs.getInt(7);
                AD_Workflow_ID = rs.getInt(8);
                //
                int estimate = rs.getInt(9);
                if (estimate != 0) {
                    m_pi.setEstSeconds(estimate + 1); //  admin overhead
                }
                m_IsServerProcess = "Y".equals(rs.getString(10));
                JasperReport = rs.getString(11);
            } else log.log(Level.SEVERE, "No AD_PInstance_ID=" + m_pi.getPInstanceId());
        } catch (Throwable e) {
            m_pi.setSummary(
                    MsgKt.getMsg("ProcessNoProcedure") + " " + e.getLocalizedMessage(), true);
            log.log(Level.SEVERE, "run", e);
            return;
        }

        //  No PL/SQL Procedure
        if (ProcedureName == null) ProcedureName = "";

        /* ******************************************************************** Workflow */
        if (AD_Workflow_ID > 0) {
            //startWorkflow(AD_Workflow_ID);
            throw new AdempiereException("Not implemented");
        }

        // Clear Jasper Report class if default - to be executed later
        boolean isJasper = false;
        if (JasperReport != null && JasperReport.trim().length() > 0) {
            isJasper = true;
            if (ProcessUtil.JASPER_STARTER_CLASS.equals(m_pi.getClassName())) {
                m_pi.setClassName(null);
            }
        }

        /* ******************************************************************** Start Optional Class */
        if (m_pi.getClassName() != null) {
            if (isJasper) {
                m_pi.setReportingProcess(true);
            }

            //	Run Class
            if (!startProcess()) {
                return;
            }

            //  No Optional SQL procedure ... done
            if (!IsReport && ProcedureName.length() == 0) {
                return;
            }
            //  No Optional Report ... done
            if (IsReport && AD_ReportView_ID == 0 && !isJasper) {
                return;
            }
        }

        if (isJasper) {
            m_pi.setReportingProcess(true);
            m_pi.setClassName(ProcessUtil.JASPER_STARTER_CLASS);
            startProcess();
        }

        //	log.fine(Log.l3_Util, "ProcessCtl.run - done");
    } //  run

    /**
     * ************************************************************************ Start Java Process
     * Class. instanciate the class implementing the interface ProcessCall. The class can be a
     * Server/Client class (when in Package org adempiere.process or org.compiere.model) or a client
     * only class (e.g. in org.compiere.report)
     *
     * @return true if success
     */
    protected boolean startProcess() {
        if (log.isLoggable(Level.FINE)) log.fine(m_pi.toString());
        boolean started = false;

        // hengsin, bug [ 1633995 ]
        boolean clientOnly = false;
        if (!m_pi.getClassName().toLowerCase().startsWith(MRule.SCRIPT_PREFIX)) {
            try {
                Class<?> processClass = Class.forName(m_pi.getClassName());
                if (ClientProcess.class.isAssignableFrom(processClass)) clientOnly = true;
            } catch (Exception ignored) {
            }
        }

        if (m_IsServerProcess && !clientOnly) {
            Server server = getServer();
            try {
                //	See ServerBean
                m_pi = server.process(m_pi);
                if (log.isLoggable(Level.FINEST)) log.finest("server => " + m_pi);
                started = true;
            } catch (UndeclaredThrowableException ex) {
                Throwable cause = ex.getCause();
                if (cause != null) {
                    if (cause instanceof InvalidClassException)
                        log.log(
                                Level.SEVERE, "Version Server <> Client: " + cause.toString() + " - " + m_pi, ex);
                    else
                        log.log(Level.SEVERE, "AppsServer error(1b): " + cause.toString() + " - " + m_pi, ex);
                } else log.log(Level.SEVERE, " AppsServer error(1) - " + m_pi, ex);
                started = false;
            } catch (Exception ex) {
                Throwable cause = ex.getCause();
                if (cause == null) cause = ex;
                log.log(Level.SEVERE, "AppsServer error - " + m_pi, cause);
                started = false;
            }
        }
        //	Run locally
        if (!started && (!m_IsServerProcess || clientOnly)) {
            if (m_pi.getClassName().toLowerCase().startsWith(MRule.SCRIPT_PREFIX)) {
                return ProcessUtil.startScriptProcess(m_pi);
            } else {
                return ProcessUtil.startJavaProcess(m_pi);
            }
        }
        return !m_pi.isError();
    } //  startProcess
}
