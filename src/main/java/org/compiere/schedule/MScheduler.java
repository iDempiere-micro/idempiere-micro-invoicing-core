package org.compiere.schedule;

import org.compiere.model.*;
import org.compiere.orm.MColumn;
import org.compiere.orm.MTable;
import org.compiere.orm.MUserRoles;
import org.compiere.orm.Query;
import org.compiere.process.MProcess;
import org.compiere.util.DisplayType;
import org.compiere.util.Msg;
import org.idempiere.icommon.model.IPO;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import static software.hsharp.core.util.DBKt.executeUpdateEx;

/**
 * Scheduler Model
 *
 * @author Jorg Janke
 * @version $Id: MScheduler.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 *     <p>Contributors: Carlos Ruiz - globalqss - FR [3135351] - Enable Scheduler for buttons
 */
public class MScheduler extends X_AD_Scheduler implements AdempiereProcessor, AdempiereProcessor2 {
  /** */
  private static final long serialVersionUID = 5106574386025319255L;

  /**
   * Get Active
   *
   * @param ctx context
   * @return active processors
   */
  public static MScheduler[] getActive(Properties ctx) {
    List<MScheduler> list =
        new Query(ctx, I_AD_Scheduler.Table_Name, null, null).setOnlyActiveRecords(true).list();
    MScheduler[] retValue = new MScheduler[list.size()];
    list.toArray(retValue);
    return retValue;
  } //	getActive

  /**
   * Standard Constructor
   *
   * @param ctx context
   * @param AD_Scheduler_ID id
   * @param trxName transaction
   */
  public MScheduler(Properties ctx, int AD_Scheduler_ID, String trxName) {
    super(ctx, AD_Scheduler_ID, trxName);
    if (AD_Scheduler_ID == 0) {
      setKeepLogDays(7);
    }
  } //	MScheduler

  /**
   * Load Constructor
   *
   * @param ctx context
   * @param rs result set
   * @param trxName transaction
   */
  public MScheduler(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  } //	MScheduler

  /** Process Parameter */
  private MSchedulerPara[] m_parameter = null;
  /** Process Recipients */
  private MSchedulerRecipient[] m_recipients = null;

  /**
   * Get Server ID
   *
   * @return id
   */
  public String getServerID() {
    return "Scheduler" + getId();
  } //	getServerID

  /**
   * Get Date Next Run
   *
   * @param requery requery
   * @return date next run
   */
  public Timestamp getDateNextRun(boolean requery) {
    if (requery) load((HashMap)null);
    return getDateNextRun();
  } //	getDateNextRun

  /**
   * Get Logs
   *
   * @return logs
   */
  public AdempiereProcessorLog[] getLogs() {
    final String whereClause = MSchedulerLog.COLUMNNAME_AD_Scheduler_ID + "=?";
    List<MSchedulerLog> list =
        new Query(getCtx(), I_AD_SchedulerLog.Table_Name, whereClause, null)
            .setParameters(getAD_Scheduler_ID())
            .setOrderBy("Created DESC")
            .list();
    MSchedulerLog[] retValue = new MSchedulerLog[list.size()];
    list.toArray(retValue);
    return retValue;
  } //	getLogs

  /**
   * Delete old Request Log
   *
   * @return number of records
   */
  public int deleteLog() {
    if (getKeepLogDays() < 1) return 0;
    String sql =
        "DELETE AD_SchedulerLog "
            + "WHERE AD_Scheduler_ID="
            + getAD_Scheduler_ID()
            + " AND (Created+"
            + getKeepLogDays()
            + ") < SysDate";
    int no = executeUpdateEx(sql, null);
    return no;
  } //	deleteLog

  /**
   * Get Process
   *
   * @return process
   */
  public MProcess getProcess() {
    return MProcess.get(getCtx(), getAD_Process_ID());
  } //	getProcess

  /**
   * Get Parameters
   *
   * @param reload reload
   * @return parameter
   */
  public MSchedulerPara[] getParameters(boolean reload) {
    if (!reload && m_parameter != null) return m_parameter;
    //
    final String whereClause = MSchedulerPara.COLUMNNAME_AD_Scheduler_ID + "=?";
    List<MSchedulerPara> list =
        new Query(getCtx(), I_AD_Scheduler_Para.Table_Name, whereClause, null)
            .setParameters(getAD_Scheduler_ID())
            .setOnlyActiveRecords(true)
            .list();
    m_parameter = new MSchedulerPara[list.size()];
    list.toArray(m_parameter);
    return m_parameter;
  } //	getParameter

  /**
   * Get Recipients
   *
   * @param reload reload
   * @return Recipients
   */
  public MSchedulerRecipient[] getRecipients(boolean reload) {
    if (!reload && m_recipients != null) return m_recipients;
    //
    final String whereClause = MSchedulerRecipient.COLUMNNAME_AD_Scheduler_ID + "=?";
    List<MSchedulerRecipient> list =
        new Query(getCtx(), I_AD_SchedulerRecipient.Table_Name, whereClause, null)
            .setParameters(getAD_Scheduler_ID())
            .setOnlyActiveRecords(true)
            .list();
    m_recipients = new MSchedulerRecipient[list.size()];
    list.toArray(m_recipients);
    return m_recipients;
  } //	getRecipients

  /**
   * Get Recipient AD_User_IDs
   *
   * @return array of user IDs
   */
  public Integer[] getRecipientAD_User_IDs() {
    TreeSet<Integer> list = new TreeSet<Integer>();
    MSchedulerRecipient[] recipients = getRecipients(false);
    for (int i = 0; i < recipients.length; i++) {
      MSchedulerRecipient recipient = recipients[i];
      if (!recipient.isActive()) continue;
      if (recipient.getAD_User_ID() != 0) {
        list.add(recipient.getAD_User_ID());
      }
      if (recipient.getAD_Role_ID() != 0) {
        MUserRoles[] urs = MUserRoles.getOfRole(getCtx(), recipient.getAD_Role_ID());
        for (int j = 0; j < urs.length; j++) {
          MUserRoles ur = urs[j];
          if (!ur.isActive()) continue;
          if (!list.contains(ur.getAD_User_ID())) list.add(ur.getAD_User_ID());
        }
      }
    }
    //
    return list.toArray(new Integer[list.size()]);
  } //	getRecipientAD_User_IDs

  /**
   * Before Save
   *
   * @param newRecord new
   * @return true
   */
  @Override
  protected boolean beforeSave(boolean newRecord) {

    // FR [3135351] - Enable Scheduler for buttons
    if (getAD_Table_ID() > 0) {
      // Validate the table has any button referencing the process
      int colid =
          new Query(
                  getCtx(),
                  MColumn.Table_Name,
                  "AD_Table_ID=? AND AD_Reference_ID=? AND AD_Process_ID=?",
                  null)
              .setOnlyActiveRecords(true)
              .setParameters(getAD_Table_ID(), DisplayType.Button, getAD_Process_ID())
              .firstId();
      if (colid <= 0) {
        log.saveError("Error", Msg.getMsg(getCtx(), "TableMustHaveProcessButton"));
        return false;
      }
    } else {
      setRecord_ID(-1);
    }

    if (getRecord_ID() != 0) {
      // Validate AD_Table_ID must be set
      if (getAD_Table_ID() <= 0) {
        log.saveError("Error", Msg.getMsg(getCtx(), "MustFillTable"));
        return false;
      }
      // Validate the record must exists on the same client of the scheduler
      MTable table = MTable.get(getCtx(), getAD_Table_ID());
      IPO po = (IPO) table.getPO(getRecord_ID(), null);
      if (po == null || po.getId() <= 0 || po.getClientId() != getClientId()) {
        log.saveError("Error", Msg.getMsg(getCtx(), "NoRecordID"));
        return false;
      }
    }

    if (newRecord || is_ValueChanged("AD_Schedule_ID")) {
      long nextWork =
          MSchedule.getNextRunMS(
              System.currentTimeMillis(),
              getScheduleType(),
              getFrequencyType(),
              getFrequency(),
              getCronPattern());
      if (nextWork > 0) setDateNextRun(new Timestamp(nextWork));
    }

    return true;
  } //	beforeSave

  /**
   * String Representation
   *
   * @return info
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("MScheduler[");
    sb.append(getId()).append("-").append(getName()).append("]");
    return sb.toString();
  } //	toString

  @Override
  public String getFrequencyType() {
    return MSchedule.get(getCtx(), getAD_Schedule_ID()).getFrequencyType();
  }

  @Override
  public int getFrequency() {
    return MSchedule.get(getCtx(), getAD_Schedule_ID()).getFrequency();
  }

  @Override
  public boolean isIgnoreProcessingTime() {
    return MSchedule.get(getCtx(), getAD_Schedule_ID()).isIgnoreProcessingTime();
  }

  @Override
  public String getScheduleType() {
    return MSchedule.get(getCtx(), getAD_Schedule_ID()).getScheduleType();
  }

  @Override
  public String getCronPattern() {
    return MSchedule.get(getCtx(), getAD_Schedule_ID()).getCronPattern();
  }

} //	MScheduler