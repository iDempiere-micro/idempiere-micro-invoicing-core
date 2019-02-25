/**
 * ******************************************************************** This file is part of
 * iDempiere ERP Open Source * http://www.idempiere.org * * Copyright (C) Contributors * * This
 * program is free software; you can redistribute it and/or * modify it under the terms of the GNU
 * General Public License * as published by the Free Software Foundation; either version 2 * of the
 * License, or (at your option) any later version. * * This program is distributed in the hope that
 * it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the * GNU General Public License for
 * more details. * * You should have received a copy of the GNU General Public License * along with
 * this program; if not, write to the Free Software * Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, * MA 02110-1301, USA. * * Contributors: * - Thomas Bayen * - Carlos Ruiz *
 * ********************************************************************
 */
package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.MSequence;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.Util;
import org.idempiere.common.util.ValueNamePair;
import org.idempiere.icommon.model.IPO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * IDEMPIERE-2100 Automate Recurring Run
 *
 * @author Carlos Ruiz - globalqss
 */
public class RecurringRun extends SvrProcess {
    /* Tag Parameters to replace */
    ArrayList<ValueNamePair> prms = new ArrayList<ValueNamePair>();
    /* The recurring group */
    private int p_C_RecurringGroup_ID = 0;
    /* Cut Date */
    private Timestamp p_Cut_Date = null;
    /* Document Action */
    private String p_DocAction = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        for (IProcessInfoParameter para : getParameter()) {
            String name = para.getParameterName();
            if ("C_RecurringGroup_ID".equals(name)) {
                p_C_RecurringGroup_ID = para.getParameterAsInt();
            } else if ("Cut_Date".equals(name)) {
                p_Cut_Date = para.getParameterAsTimestamp();
            } else if ("DocAction".equals(name)) {
                p_DocAction = para.getParameterAsString();
            } else if (name.startsWith("Prm_")) {
                String prm = para.getParameterAsString();
                if (!Util.isEmpty(prm, true)) {
                    prms.add(new ValueNamePair(name, prm));
                }
            } else {
                log.log(Level.SEVERE, "Unknown Parameter: " + name);
            }
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception
     */
    protected String doIt() throws Exception {
        throw new NotImplementedException();
    /*

    if (log.isLoggable(Level.INFO)) log.info("C_RecurringGroup_ID=" + p_C_RecurringGroup_ID
    		+ ", Cut_Date=" + p_Cut_Date + ", DocAction=" + p_DocAction);

    ArrayList<Object> parameters = new ArrayList<Object>();
    StringBuffer whereClause = new StringBuffer("RunsMax>(SELECT COUNT(*) FROM C_Recurring_Run WHERE C_Recurring_Run.C_Recurring_ID=C_Recurring.C_Recurring_ID)");
    if (p_C_RecurringGroup_ID > 0) {
    	whereClause.append(" AND C_RecurringGroup_ID=?");
    	parameters.add(p_C_RecurringGroup_ID);
    }
    if (p_Cut_Date != null) {
    	whereClause.append(" AND DateNextRun<=?");
    	parameters.add(p_Cut_Date);
    }

    List<MRecurring> recs = new Query(getCtx(), MRecurring.Table_Name, whereClause.toString(), null)
    	.setOnlyActiveRecords(true)
    	.setClient_ID()
    	.setParameters(parameters)
    	.setOrderBy("Name, C_Recurring_ID")
    	.list();

    int cnt = 0;
    for (MRecurring rec : recs) {
    	if (log.isLoggable(Level.INFO)) log.info(rec.toString());
    	int percent = cnt * 100 / recs.size();
    	statusUpdate(Msg.getMsg(getCtx(), "Processing") + percent + "% - " + rec.toString());

    	Timestamp currdate = rec.getDateNextRun();
    	String msg = rec.executeRun();
    	IPO po = rec.getLastPO();
    	if (po != null) {

    		replaceTagsInDescription(po);

    		// replace tags on lines of documents
    		IPO[] polines = null;
    		if (po instanceof MInvoice) {
    			polines = ((MInvoice)po).getLines();
    		} else if (po instanceof MOrder) {
    			polines = ((MOrder)po).getLines();
    		}
    		if (polines != null) {
    			for (IPO poline : polines) {
    				replaceTagsInDescription(poline);
    			}
    		}
    		// replace for journals
    		if (po instanceof MJournalBatch) {
    			for (MJournal journal : ((MJournalBatch)po).getJournals(false)) {
    				polines = journal.getLines(false);
    				if (polines != null) {
    					for (IPO poline : polines) {
    						replaceTagsInDescription(poline);
    					}
    				}
    			}
    		}

    		// Complete/Prepare the document
    		if (p_DocAction != null && po instanceof DocAction) {
    			if (!((DocAction) po).processIt(p_DocAction))
    			{
    				log.warning("completePO - failed: " + po);
    				throw new IllegalStateException("PO Process Failed: " + po + " - " + ((DocAction) po).getProcessMsg());
    			}
    			if( po instanceof org.compiere.orm.PO ) {
    				((org.compiere.orm.PO)po).saveEx();
    			}
    		}

    		msg = Msg.parseTranslation(getCtx(), msg);
    		addBufferLog(po.getId(), currdate, null, msg, po.getTableId(), po.getId());
    	}
    }

    return "@OK@";
    */
    } //	doIt

    private void replaceTagsInDescription(IPO _po) {
        if (_po instanceof org.compiere.orm.PO) {
            org.compiere.orm.PO po = (org.compiere.orm.PO) _po;
            /* Parse context and prm tags on description */
            if (po.getColumnIndex("Description") >= 0) {
                String description = po.get_ValueAsString("Description");
                String description_org = description;
                description = MSequence.parseVariable(description, po, null, true);

                if (prms.size() > 0) {
                    for (ValueNamePair prm : prms) {
                        String prmName = prm.getValue();
                        String prmValue = prm.getName();
                        String tag = "@" + prmName + "@";
                        description = description.replaceAll(tag, prmValue);
                    }
                }
                if (description_org != null && !description_org.equals(description)) {
                    po.set_ValueOfColumn("Description", description);
                    po.saveEx();
                }
            }
        }
    }
} //	RecurringRun
