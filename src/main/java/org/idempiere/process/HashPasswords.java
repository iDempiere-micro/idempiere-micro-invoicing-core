package org.idempiere.process;

import org.compiere.crm.MUser;
import org.compiere.model.TypedQuery;
import org.compiere.model.User;
import org.compiere.orm.MSysConfig;
import org.compiere.process.SvrProcess;
import org.compiere.util.SystemIDs;
import org.idempiere.common.exceptions.AdempiereException;
import software.hsharp.core.orm.MBaseTableKt;

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
        // DAP: CacheMgt.get().reset();

        int count = 0;
        try {
            TypedQuery<User> query = MBaseTableKt.getTable(MUser.Table_ID).createQuery(where);
            List<User> users = query.list();
            for (User user : users) {
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
