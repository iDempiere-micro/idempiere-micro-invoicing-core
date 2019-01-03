package org.compiere.wf;

import java.util.logging.Level;
import org.compiere.crm.MUser;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;

/**
 * Manage Workflow Activity
 *
 * @author Jorg Janke
 * @version $Id: WFActivityManage.java,v 1.2 2006/07/30 00:51:05 jjanke Exp $
 */
public class WFActivityManage extends SvrProcess {
  /** Abort It */
  private boolean p_IsAbort = false;
  /** New User */
  private int p_AD_User_ID = 0;
  /** New Responsible */
  private int p_AD_WF_Responsible_ID = 0;
  /** Record */
  private int p_AD_WF_Activity_ID = 0;

  /** Prepare - e.g., get Parameters. */
  protected void prepare() {
    IProcessInfoParameter[] para = getParameter();
    for (int i = 0; i < para.length; i++) {
      String name = para[i].getParameterName();
      if (para[i].getParameter() == null) ;
      else if (name.equals("IsAbort")) p_IsAbort = "Y".equals(para[i].getParameter());
      else if (name.equals("AD_User_ID")) p_AD_User_ID = para[i].getParameterAsInt();
      else if (name.equals("AD_WF_Responsible_ID"))
        p_AD_WF_Responsible_ID = para[i].getParameterAsInt();
      else log.log(Level.SEVERE, "Unknown Parameter: " + name);
    }
    p_AD_WF_Activity_ID = getRecord_ID();
  } //	prepare

  /**
   * Perform process.
   *
   * @return Message (variables are parsed)
   * @throws Exception if not successful
   */
  protected String doIt() throws Exception {
    MWFActivity activity = new MWFActivity(getCtx(), p_AD_WF_Activity_ID, null);
    if (log.isLoggable(Level.INFO)) log.info("doIt - " + activity);

    MUser user = MUser.get(getCtx(), getAD_User_ID());
    //	Abort
    if (p_IsAbort) {
      String msg = user.getName() + ": Abort";
      activity.setTextMsg(msg);
      activity.setAD_User_ID(getAD_User_ID());
      // 2007-06-14, matthiasO.
      // Set the 'processed'-flag when an activity is aborted; not setting this flag
      // will leave the activity in an "unmanagable" state
      activity.setProcessed(true);
      activity.setWFState(StateEngine.STATE_Aborted);
      return msg;
    }
    String msg = null;
    //	Change User
    if (p_AD_User_ID != 0 && activity.getAD_User_ID() != p_AD_User_ID) {
      MUser from = MUser.get(getCtx(), activity.getAD_User_ID());
      MUser to = MUser.get(getCtx(), p_AD_User_ID);
      msg = user.getName() + ": " + from.getName() + " -> " + to.getName();
      activity.setTextMsg(msg);
      activity.setAD_User_ID(p_AD_User_ID);
    }
    //	Change Responsible
    if (p_AD_WF_Responsible_ID != 0
        && activity.getAD_WF_Responsible_ID() != p_AD_WF_Responsible_ID) {
      MWFResponsible from = MWFResponsible.get(getCtx(), activity.getAD_WF_Responsible_ID());
      MWFResponsible to = MWFResponsible.get(getCtx(), p_AD_WF_Responsible_ID);
      String msg1 = user.getName() + ": " + from.getName() + " -> " + to.getName();
      activity.setTextMsg(msg1);
      activity.setAD_WF_Responsible_ID(p_AD_WF_Responsible_ID);
      if (msg == null) msg = msg1;
      else msg += " - " + msg1;
    }
    //
    activity.saveEx();

    return msg;
  } //	doIt
} //	WFActivityManage