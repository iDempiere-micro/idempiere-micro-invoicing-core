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

import org.compiere.accounting.MYear;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereUserError;

import java.sql.Timestamp;
import java.util.logging.Level;

/**
 * Create Periods of year
 *
 * @author Jorg Janke
 * @version $Id: YearCreatePeriods.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class YearCreatePeriods extends SvrProcess {
    private int p_C_Year_ID = 0;
    private Timestamp p_StartDate;
    private String p_DateFormat;

    /**
     * Prepare
     */
    protected void prepare() {

        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("StartDate")) p_StartDate = (Timestamp) iProcessInfoParameter.getParameter();
            else if (name.equals("DateFormat")) p_DateFormat = (String) iProcessInfoParameter.getParameter();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_C_Year_ID = getRecordId();
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        MYear year = new MYear(p_C_Year_ID);
        if (p_C_Year_ID == 0 || year.getId() != p_C_Year_ID)
            throw new AdempiereUserError("@NotFound@: @C_Year_ID@ - " + p_C_Year_ID);
        if (log.isLoggable(Level.INFO)) log.info(year.toString());
        //
        if (year.createStdPeriods(null, p_StartDate, p_DateFormat)) return "@OK@";
        return "@Error@";
    } //	doIt
} //	YearCreatePeriods
