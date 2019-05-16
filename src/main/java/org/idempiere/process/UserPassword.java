package org.idempiere.process;

import org.compiere.crm.MUser;
import org.compiere.crm.MUserKt;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.MSysConfig;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.Util;

import java.util.logging.Level;

/**
 * Reset Password
 *
 * @author Jorg Janke
 * @version $Id: UserPassword.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class UserPassword extends SvrProcess {
    private int p_AD_User_ID = -1;
    private String p_OldPassword = null;
    private String p_NewPassword = null;
    private String p_NewPasswordConfirm = null;
    private String p_NewEMail = null;
    private String p_NewEMailConfirm = null;
    private String p_NewEMailUser = null;
    private String p_NewEMailUserPW = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            switch (name) {
                case "AD_User_ID":
                    p_AD_User_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "OldPassword":
                    p_OldPassword = (String) iProcessInfoParameter.getParameter();
                    break;
                case "NewPassword":
                    p_NewPassword = (String) iProcessInfoParameter.getParameter();
                    break;
                case "NewEMail":
                    p_NewEMail = (String) iProcessInfoParameter.getParameter();
                    break;
                case "NewEMailUser":
                    p_NewEMailUser = (String) iProcessInfoParameter.getParameter();
                    break;
                case "NewEMailUserPW":
                    p_NewEMailUserPW = (String) iProcessInfoParameter.getParameter();
                    break;
                case "NewPasswordConfirm":
                    p_NewPasswordConfirm = (String) iProcessInfoParameter.getParameter();
                    break;
                case "NewEMailConfirm":
                    p_NewEMailConfirm = (String) iProcessInfoParameter.getParameter();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info("AD_User_ID=" + p_AD_User_ID + " from " + getUserId());

        MUser user = MUserKt.getUser(p_AD_User_ID);
        user.load();
        MUser operator = MUserKt.getUser(getUserId());
        if (log.isLoggable(Level.FINE)) log.fine("User=" + user + ", Operator=" + operator);

        boolean hash_password = MSysConfig.getBooleanValue(MSysConfig.USER_PASSWORD_HASH, false);

        //	Do we need a password ?
        if (Util.isEmpty(p_OldPassword)) // 	Password required
        {
            if (p_AD_User_ID == 0 // 	change of System
                    || p_AD_User_ID == 100 // 	change of SuperUser
                    || !operator.isAdministrator())
                throw new IllegalArgumentException("@OldPasswordMandatory@");
        } else {
            //	is entered Password correct ?
            if (hash_password) {
                if (!user.authenticateHash(p_OldPassword))
                    throw new IllegalArgumentException("@OldPasswordNoMatch@");
            } else {
                if (!p_OldPassword.equals(user.getPassword()))
                    throw new IllegalArgumentException("@OldPasswordNoMatch@");
            }
        }

        // new password confirm
        if (!Util.isEmpty(p_NewPassword)) {
            if (Util.isEmpty(p_NewPasswordConfirm)) {
                throw new IllegalArgumentException("@NewPasswordConfirmMandatory@");
            } else {
                if (!p_NewPassword.equals(p_NewPasswordConfirm)) {
                    throw new IllegalArgumentException("@PasswordNotMatch@");
                }
            }
        }

        if (!Util.isEmpty(p_NewEMailUserPW)) {
            if (Util.isEmpty(p_NewEMailConfirm)) {
                throw new IllegalArgumentException("@NewEmailConfirmMandatory@");
            } else {
                if (!p_NewEMailUserPW.equals(p_NewEMailConfirm)) {
                    throw new IllegalArgumentException("@NewEmailNotMatch@");
                }
            }
        }

        if (!Util.isEmpty(p_NewPassword))
            user.setValueOfColumn("Password", p_NewPassword); // will be hashed and validate on saveEx
        if (!Util.isEmpty(p_NewEMail)) user.setEMail(p_NewEMail);
        if (!Util.isEmpty(p_NewEMailUser)) user.setEMailUser(p_NewEMailUser);
        if (!Util.isEmpty(p_NewEMailUserPW)) user.setEMailUserPW(p_NewEMailUserPW);
        user.saveEx();

        return "OK";
    } //	doIt
} //	UserPassword
