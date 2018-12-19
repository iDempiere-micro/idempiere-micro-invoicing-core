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
 * 95054, USA * or via info@compiere.org or http://www.compiere.org/license.html * Contributor(s):
 * Carlos Ruiz (globalqss)
 * ***************************************************************************
 */
package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;

import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;

/**
 * Title: Set Current Format as Default
 *
 * @author Carlos Ruiz (globalqss)
 * @version $Id: AD_PrintPaper_Default.java,v 1.0 2005/09/14 22:29:00 globalqss Exp $
 */
public class AD_PrintPaper_Default extends SvrProcess {

  /** The Client */
  private int p_AD_Client_ID = -1;
  /** The Record */
  private int p_Record_ID = 0;

  /** Prepare - e.g., get Parameters. */
  protected void prepare() {
    IProcessInfoParameter[] para = getParameter();
    for (int i = 0; i < para.length; i++) {
      String name = para[i].getParameterName();
      if (para[i].getParameter() == null) ;
      else if (name.equals("AD_Client_ID")) p_AD_Client_ID = para[i].getParameterAsInt();
      else log.log(Level.SEVERE, "Unknown Parameter: " + name);
    }
    p_Record_ID = getRecord_ID();
  } //	prepare

  /**
   * Process
   *
   * @return message
   * @throws Exception
   */
  protected String doIt() throws Exception {
    StringBuilder sql = new StringBuilder();
    int cnt = 0;

    log.info("Set Print Format");

    try {
      sql.append("UPDATE AD_PrintFormat pf ")
          .append("SET AD_PrintPaper_ID = ")
          .append(p_Record_ID)
          .append(" ")
          .append("WHERE EXISTS (SELECT * FROM AD_PrintPaper pp ")
          .append("WHERE pf.AD_PrintPaper_ID=pp.AD_PrintPaper_ID ")
          .append("AND IsLandscape = (SELECT IsLandscape FROM AD_PrintPaper ")
          .append("WHERE AD_PrintPaper_ID=")
          .append(p_Record_ID)
          .append("))");
      if (p_AD_Client_ID != -1) {
        sql.append(" AND clientId = ").append(p_AD_Client_ID);
      }
      cnt = executeUpdate(sql.toString(), null);
      if (log.isLoggable(Level.INFO)) log.info("Updated " + cnt + " columns");
    } catch (Exception e) {
      log.log(Level.SEVERE, "set print format", e);
    }

    StringBuilder msgreturn = new StringBuilder("@Copied@=").append(cnt);
    return msgreturn.toString();
  } //	doIt
} //	AD_PrintPaper_Default
