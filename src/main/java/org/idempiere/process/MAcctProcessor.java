package org.idempiere.process;

import org.compiere.accounting.MClient;
import org.compiere.model.*;
import org.compiere.orm.Query;
import org.compiere.schedule.MSchedule;
import org.compiere.util.Msg;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static software.hsharp.core.util.DBKt.executeUpdate;


public class MAcctProcessor extends X_C_AcctProcessor
    implements AdempiereProcessor, AdempiereProcessor2 {
  /** */
  private static final long serialVersionUID = -4760475718973777369L;

  /**
   * Get Active
   *
   * @param ctx context
   * @return active processors
   */
  public static MAcctProcessor[] getActive(Properties ctx) {
    List<MAcctProcessor> list =
        new Query(ctx, I_C_AcctProcessor.Table_Name, null, null).setOnlyActiveRecords(true).list();
    return list.toArray(new MAcctProcessor[list.size()]);
  } //	getActive

  /**
   * Standard Construvtor
   *
   * @param ctx context
   * @param C_AcctProcessor_ID id
   * @param trxName transaction
   */
  public MAcctProcessor(Properties ctx, int C_AcctProcessor_ID, String trxName) {
    super(ctx, C_AcctProcessor_ID, trxName);
    if (C_AcctProcessor_ID == 0) {
      //	setName (null);
      //	setSupervisor_ID (0);
      //	setFrequencyType (FREQUENCYTYPE_Hour);
      //	setFrequency (1);
      setKeepLogDays(7); // 7
    }
  } //	MAcctProcessor

  /**
   * Load Constructor
   *
   * @param ctx context
   * @param rs result set
   * @param trxName transaction
   */
  public MAcctProcessor(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  } //	MAcctProcessor

  /**
   * Parent Constructor
   *
   * @param client parent
   * @param Supervisor_ID admin
   */
  public MAcctProcessor(MClient client, int Supervisor_ID) {
    this(client.getCtx(), 0, null);
    setClientOrg(client);
    StringBuilder msgset =
        new StringBuilder()
            .append(client.getName())
            .append(" - ")
            .append(Msg.translate(getCtx(), "C_AcctProcessor_ID"));
    setName(msgset.toString());
    setSupervisor_ID(Supervisor_ID);
  } //	MAcctProcessor

  /**
   * Before Save
   *
   * @param newRecord new
   * @return true
   */
  @Override
  protected boolean beforeSave(boolean newRecord) {
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
   * Get Server ID
   *
   * @return id
   */
  public String getServerID() {
    StringBuilder msgreturn = new StringBuilder("AcctProcessor").append(getId());
    return msgreturn.toString();
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
    String whereClause = "C_AcctProcessor_ID=? ";
    List<MAcctProcessor> list =
        new Query(getCtx(), I_C_AcctProcessorLog.Table_Name, whereClause, null)
            .setParameters(getC_AcctProcessor_ID())
            .setOrderBy("Created DESC")
            .list();
    return list.toArray(new MAcctProcessorLog[list.size()]);
  } //	getLogs

  /**
   * Delete old Request Log
   *
   * @return number of records
   */
  public int deleteLog() {
    if (getKeepLogDays() < 1) return 0;
    StringBuilder sql =
        new StringBuilder("DELETE C_AcctProcessorLog ")
            .append("WHERE C_AcctProcessor_ID=")
            .append(getC_AcctProcessor_ID())
            .append(" AND (Created+")
            .append(getKeepLogDays())
            .append(") < SysDate");
    int no = executeUpdate(sql.toString(), null);
    return no;
  } //	deleteLog

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
} //	MAcctProcessor
