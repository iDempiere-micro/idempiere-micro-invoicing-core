/**
 * **************************************************************************** Product: Adempiere
 * ERP & CRM Smart Business Solution * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved. *
 * This program is free software; you can redistribute it and/or modify it * under the terms version
 * 2 of the GNU General Public License as published * by the Free Software Foundation. This program
 * is distributed in the hope * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. * See the GNU General
 * Public License for more details. * You should have received a copy of the GNU General Public
 * License along * with this program; if not, write to the Free Software Foundation, Inc., * 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA. * For the text or an alternative of this
 * public license, you may reach us * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA
 * 95054, USA * or via info@compiere.org or http://www.compiere.org/license.html *
 * ***************************************************************************
 */
package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.MRole;
import org.compiere.orm.Query;
import org.compiere.process.SvrProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Update Role Access
 *
 * @author Jorg Janke
 * @author Teo Sarca, teo.sarca@gmail.com
 * <li>BF [ 3018005 ] Role Access Update: updates all roles if I log in as System
 * https://sourceforge.net/tracker/?func=detail&aid=3018005&group_id=176962&atid=879332
 * @version $Id: RoleAccessUpdate.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class RoleAccessUpdate extends SvrProcess {

    /**
     * Update Role
     */
    private int p_AD_Role_ID = -1;
    /**
     * Update Roles of Client
     */
    private int p_AD_Client_ID = -1;
    /**
     * Reset Existing Access
     */
    private boolean p_IsReset = true;

    /**
     * Prepare
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("AD_Role_ID")) p_AD_Role_ID = iProcessInfoParameter.getParameterAsInt();
            else if (name.equals("AD_Client_ID")) p_AD_Client_ID = iProcessInfoParameter.getParameterAsInt();
            else if (name.equals("ResetAccess")) p_IsReset = "Y".equals(iProcessInfoParameter.getParameter());
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info("AD_Client_ID=" + p_AD_Client_ID + ", AD_Role_ID=" + p_AD_Role_ID);
        //
        if (p_AD_Role_ID > 0) updateRole(new MRole(p_AD_Role_ID));
        else {
            List<Object> params = new ArrayList<Object>();
            StringBuilder whereClause = new StringBuilder("1=1");
            if (p_AD_Client_ID > 0) {
                whereClause.append(" AND AD_Client_ID=? ");
                params.add(p_AD_Client_ID);
            }
            if (p_AD_Role_ID == 0) // System Role
            {
                whereClause.append(" AND AD_Role_ID=?");
                params.add(p_AD_Role_ID);
            }
            // sql += "ORDER BY AD_Client_ID, Name";

            List<MRole> roles =
                    new Query(MRole.Table_Name, whereClause.toString())
                            .setOnlyActiveRecords(true)
                            .setParameters(params)
                            .setOrderBy("AD_Client_ID, Name")
                            .list();

            for (MRole role : roles) {
                updateRole(role);
            }
        }

        return "";
    } //	doIt

    /**
     * Update Role
     *
     * @param role role
     */
    private void updateRole(MRole role) {
        StringBuilder msglog =
                new StringBuilder(role.getName()).append(": ").append(role.updateAccessRecords(p_IsReset));
        addLog(0, null, null, msglog.toString());
    } //	updateRole
} //	RoleAccessUpdate
