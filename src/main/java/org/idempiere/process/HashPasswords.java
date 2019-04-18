package org.idempiere.process;

import org.compiere.crm.MUser;
import org.compiere.model.I_AD_User;
import org.compiere.model.I_Query;
import org.compiere.orm.MSysConfig;
import software.hsharp.core.orm.MBaseTableKt;
import org.compiere.process.SvrProcess;
import org.compiere.util.SystemIDs;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.CacheMgt;

import java.util.List;

/**
 * Hash existing passwords
 */
public class HashPasswords extends SvrProcess {
    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
    } // 	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        boolean hash_password = MSysConfig.getBooleanValue(MSysConfig.USER_PASSWORD_HASH, false);
        if (hash_password) throw new AdempiereException("Passwords already hashed");

        String where = " Password IS NOT NULL AND Salt IS NULL ";

        // update the sysconfig key to Y out of trx and reset the cache
        MSysConfig conf = new MSysConfig(SystemIDs.SYSCONFIG_USER_HASH_PASSWORD);
        conf.setSearchKey("Y");
        conf.saveEx();
        CacheMgt.get().reset(MSysConfig.Table_Name);

        int count = 0;
        try {
            I_Query<I_AD_User> query = MBaseTableKt.getTable(MUser.Table_ID).createQuery(where);
            List<I_AD_User> users = query.list();
            for (I_AD_User user : users) {
                user.setPassword(user.getPassword());
                count++;
                user.saveEx();
            }
        } catch (Exception e) {
            // reset to N on exception
            conf.setSearchKey("N");
            conf.saveEx();
            throw e;
        }

        return "@Updated@ " + count;
    } //	doIt
} //	HashPasswords
