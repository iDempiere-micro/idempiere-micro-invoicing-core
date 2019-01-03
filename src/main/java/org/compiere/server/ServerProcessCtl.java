package org.compiere.server;

import org.compiere.model.IProcessInfo;
import org.compiere.model.Server;
import org.compiere.orm.MColumn;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.compiere.process.DocAction;
import org.compiere.process.MPInstance;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessUtil;
import org.compiere.rule.MRule;
import org.compiere.util.Msg;
import org.compiere.wf.MWFProcess;
import org.compiere.wf.MWorkflow;
import org.idempiere.common.base.Service;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.io.InvalidClassException;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.close;
import static software.hsharp.core.util.DBKt.prepareStatement;

public class ServerProcessCtl implements Runnable {

  /** Static Logger */
  private static CLogger log = CLogger.getCLogger(ServerProcessCtl.class);

  private static Server m_server;

  /** Process Info */
  IProcessInfo m_pi;

  private boolean m_IsServerProcess = false;

  /**
   * ************************************************************************ Constructor
   *
   * @param pi Process info
   * @param trx Transaction
   */
  public ServerProcessCtl(ProcessInfo pi) {
    m_pi = pi;
  } //  ProcessCtl

  /**
   * Process Control <code>
   * - Get Instance ID
   * - Get Parameters
   * - execute (lock - start process - unlock)
   *  </code> Creates a ProcessCtl instance, which calls lockUI and unlockUI if parent is a
   * ASyncProcess <br>
   * Called from APanel.cmd_print, APanel.actionButton and VPaySelect.cmd_generate
   *
   * @param pi ProcessInfo process info
   * @param trx Transaction
   * @return worker started ProcessCtl instance or null for workflow
   */
  public static ServerProcessCtl process(ProcessInfo pi) {
    if (log.isLoggable(Level.FINE)) log.fine("ServerProcess - " + pi);

    MPInstance instance = null;
    if (pi.getAD_PInstance_ID() <= 0) {
      try {
        instance = new MPInstance(Env.getCtx(), pi.getAD_Process_ID(), pi.getRecord_ID());
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
        pi.setSummary(Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
        pi.setError(true);
        return null;
      }
      pi.setAD_PInstance_ID(instance.getAD_PInstance_ID());
    } else {
      instance = new MPInstance(Env.getCtx(), pi.getAD_PInstance_ID(), null);
    }

    //	execute
    ServerProcessCtl worker = new ServerProcessCtl(pi);
    worker.run();

    return worker;
  } //	execute

  /** Run this process in a new thread */
  public void start() {
    Thread thread = new Thread(this);
    // Set thread name
    if (m_pi != null) thread.setName(m_pi.getTitle() + "-" + m_pi.getAD_PInstance_ID());
    thread.start();
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
          "AD_PInstance_ID=" + m_pi.getAD_PInstance_ID() + ", Record_ID=" + m_pi.getRecord_ID());

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
    if (!Env.isBaseLanguage(Env.getCtx(), "AD_Process"))
      sql =
          "SELECT t.Name, p.ProcedureName,p.ClassName, p.AD_Process_ID," //	1..4
              + " p.isReport, p.IsDirectPrint,p.AD_ReportView_ID,p.AD_Workflow_ID," //	5..8
              + " CASE WHEN COALESCE(p.Statistic_Count,0)=0 THEN 0 ELSE p.Statistic_Seconds/p.Statistic_Count END CASE,"
              + " p.IsServerProcess, p.JasperReport "
              + "FROM AD_Process p"
              + " INNER JOIN AD_PInstance i ON (p.AD_Process_ID=i.AD_Process_ID) "
              + " INNER JOIN AD_Process_Trl t ON (p.AD_Process_ID=t.AD_Process_ID"
              + " AND t.AD_Language='"
              + Env.getADLanguage(Env.getCtx())
              + "') "
              + "WHERE p.IsActive='Y'"
              + " AND i.AD_PInstance_ID=?";
    //
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt =
          prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, null);
      pstmt.setInt(1, m_pi.getAD_PInstance_ID());
      rs = pstmt.executeQuery();
      if (rs.next()) {
        m_pi.setTitle(rs.getString(1));
        ProcedureName = rs.getString(2);
        m_pi.setClassName(rs.getString(3));
        m_pi.setAD_Process_ID(rs.getInt(4));
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
      } else log.log(Level.SEVERE, "No AD_PInstance_ID=" + m_pi.getAD_PInstance_ID());
    } catch (Throwable e) {
      m_pi.setSummary(
          Msg.getMsg(Env.getCtx(), "ProcessNoProcedure") + " " + e.getLocalizedMessage(), true);
      log.log(Level.SEVERE, "run", e);
      return;
    } finally {
      close(rs, pstmt);
      rs = null;
      pstmt = null;
    }

    //  No PL/SQL Procedure
    if (ProcedureName == null) ProcedureName = "";

    /** ******************************************************************** Workflow */
    if (AD_Workflow_ID > 0) {
      startWorkflow(AD_Workflow_ID);
      return;
    }

    // Clear Jasper Report class if default - to be executed later
    boolean isJasper = false;
    if (JasperReport != null && JasperReport.trim().length() > 0) {
      isJasper = true;
      if (ProcessUtil.JASPER_STARTER_CLASS.equals(m_pi.getClassName())) {
        m_pi.setClassName(null);
      }
    }

    /** ******************************************************************** Start Optional Class */
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
      return;
    }

    //	log.fine(Log.l3_Util, "ProcessCtl.run - done");
  } //  run

  /**
   * Get Server
   *
   * @return Server
   */
  public static Server getServer() {
    if (m_server == null) {
      m_server = Service.Companion.locator().locate(Server.class).getService();
    }
    return m_server;
  } //	getServer

  /**
   * ************************************************************************ Start Workflow.
   *
   * @param AD_Workflow_ID workflow
   * @return true if started
   */
  protected boolean startWorkflow(int AD_Workflow_ID) {
    if (log.isLoggable(Level.FINE)) log.fine(AD_Workflow_ID + " - " + m_pi);
    boolean started = false;
    if (m_IsServerProcess) {
      Server server = getServer();
      try {
        if (server != null) { // 	See ServerBean
          m_pi = server.workflow(Env.getRemoteCallCtx(Env.getCtx()), m_pi, AD_Workflow_ID);
          if (log.isLoggable(Level.FINEST)) log.finest("server => " + m_pi);
          started = true;
        }
      } catch (Exception ex) {
        log.log(Level.SEVERE, "AppsServer error", ex);
        started = false;
      }
    }
    //	Run locally
    if (!started && !m_IsServerProcess) {
      MWFProcess wfProcess = startWorkFlow(Env.getCtx(), m_pi, AD_Workflow_ID);
      started = wfProcess != null;
    }
    return started;
  } //  startWorkflow

  public static MWFProcess startWorkFlow(Properties ctx, IProcessInfo pi, int AD_Workflow_ID) {
    MWorkflow wf = MWorkflow.get(ctx, AD_Workflow_ID);
    MWFProcess wfProcess = wf.start(pi, pi.getTransactionName());
    if (log.isLoggable(Level.FINE)) log.fine(pi.toString());
    return wfProcess;
  }

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
      } catch (Exception e) {
      }
    }

    if (m_IsServerProcess && !clientOnly) {
      Server server = getServer();
      try {
        if (server != null) {
          //	See ServerBean
          m_pi = server.process(Env.getRemoteCallCtx(Env.getCtx()), m_pi);
          if (log.isLoggable(Level.FINEST)) log.finest("server => " + m_pi);
          started = true;
        }
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
        return ProcessUtil.startScriptProcess(Env.getCtx(), m_pi);
      } else {
        return ProcessUtil.startJavaProcess(Env.getCtx(), m_pi);
      }
    }
    return !m_pi.isError();
  } //  startProcess

  /**
   * @param po
   * @param docAction
   * @return ProcessInfo
   */
  public static ProcessInfo runDocumentActionWorkflow(PO po, String docAction) {
    int AD_Table_ID = po.getTableId();
    MTable table = MTable.get(Env.getCtx(), AD_Table_ID);
    MColumn column = table.getColumn("DocAction");
    if (column == null) return null;
    if (!docAction.equals(po.get_Value(column.getColumnName()))) {
      po.set_ValueOfColumn(column.getColumnName(), docAction);
      po.saveEx();
    }
    ProcessInfo processInfo =
        new ProcessInfo(
            ((DocAction) po).getDocumentInfo(),
            column.getAD_Process_ID(),
            po.getTableId(),
            po.getId());
    processInfo.setTransactionName(null);
    processInfo.setPO(po);
    ServerProcessCtl.process(processInfo);
    return processInfo;
  }
}