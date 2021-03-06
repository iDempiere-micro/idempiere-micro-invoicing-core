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
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereUserError;

import java.util.logging.Level;

/**
 * Dunning Letter Print
 *
 * @author Jorg Janke
 * @version $Id: DunningPrint.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 * <p>FR 2872010 - Dunning Run for a complete Dunning (not just level) - Developer: Carlos Ruiz
 * - globalqss - Sponsor: Metas
 */
public class DunningPrint extends SvrProcess {
    /**
     * Mail PDF
     */
    private boolean p_EMailPDF = false;
    /**
     * Mail Template
     */
    private int p_R_MailText_ID = 0;
    /**
     * Dunning Run
     */
    private int p_C_DunningRun_ID = 0;
    /**
     * Print only Outstanding
     */
    private boolean p_IsOnlyIfBPBalance = true;
    /**
     * Print only unprocessed lines
     */
    private boolean p_PrintUnprocessedOnly = true;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("EMailPDF")) p_EMailPDF = "Y".equals(iProcessInfoParameter.getParameter());
            else if (name.equals("R_MailText_ID")) p_R_MailText_ID = iProcessInfoParameter.getParameterAsInt();
            else if (name.equals("C_DunningRun_ID")) p_C_DunningRun_ID = iProcessInfoParameter.getParameterAsInt();
            else if (name.equals("IsOnlyIfBPBalance"))
                p_IsOnlyIfBPBalance = "Y".equals(iProcessInfoParameter.getParameter());
            else if (name.equals("PrintUnprocessedOnly"))
                p_PrintUnprocessedOnly = "Y".equals(iProcessInfoParameter.getParameter());
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
            log.info(
                    "C_DunningRun_ID="
                            + p_C_DunningRun_ID
                            + ",R_MailText_ID="
                            + p_R_MailText_ID
                            + ", EmailPDF="
                            + p_EMailPDF
                            + ",IsOnlyIfBPBalance="
                            + p_IsOnlyIfBPBalance
                            + ",PrintUnprocessedOnly="
                            + p_PrintUnprocessedOnly);

        //	Need to have Template
        if (p_EMailPDF && p_R_MailText_ID == 0)
            throw new AdempiereUserError("@NotFound@: @R_MailText_ID@");

        throw new NotImplementedException();

    } //	doIt
} //	DunningPrint
