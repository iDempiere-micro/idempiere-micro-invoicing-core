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

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Copy Line Set at the end of the Line Set
 *
 * @author Jorg Janke
 * @version $Id: ReportLineSet_Copy.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class ReportLineSet_Copy extends SvrProcess {

    /**
     * Source Line Set
     */
    private int m_PA_ReportLineSet_ID = 0;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (para[i].getParameter() == null) ;
            else if (name.equals("PA_ReportLineSet_ID"))
                m_PA_ReportLineSet_ID = ((BigDecimal) para[i].getParameter()).intValue();
            else log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception
     */
    protected String doIt() throws Exception {
        int to_ID = super.getRecord_ID();
        if (log.isLoggable(Level.INFO))
            log.info("From PA_ReportLineSet_ID=" + m_PA_ReportLineSet_ID + ", To=" + to_ID);
        if (to_ID < 1) throw new Exception(MSG_SaveErrorRowNotFound);
        throw new Exception("NotImplementedException");
        //
    /*
    MReportLineSet to = new MReportLineSet(getCtx(), to_ID, null);
    MReportLineSet rlSet = new MReportLineSet(getCtx(), m_PA_ReportLineSet_ID, null);
    MReportLine[] rls = rlSet.getLiness();
    for (int i = 0; i < rls.length; i++)
    {
    	MReportLine rl = MReportLine.copy (getCtx(), to.getClientId(), to.getOrgId(), to_ID, rls[i], null);
    	rl.saveEx();
    	MReportSource[] rss = rls[i].getSources();
    	if (rss != null)
    	{
    		for (int ii = 0; ii < rss.length; ii++)
    		{
    			MReportSource rs = MReportSource.copy (getCtx(), to.getClientId(), to.getOrgId(), rl.getId(), rss[ii], null);
    			rs.saveEx();
    		}
    	}
    	//	Oper 1/2 were set to Null !
    }
    StringBuilder msgreturn = new StringBuilder("@Copied@=").append(rls.length);
    return msgreturn.toString();*/
    } //	doIt
} //	ReportLineSet_Copy
