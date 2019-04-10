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

import org.compiere.accounting.MPayment;
import org.compiere.crm.MBPartner;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_BPartner;
import org.compiere.orm.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.AdempiereUserError;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

/**
 * Validate Business Partner
 *
 * @author Jorg Janke
 * @version $Id: BPartnerValidate.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $ FR: [ 2214883 ] Remove
 * SQL code and Replace for Query - red1, teo_sarca
 */
public class BPartnerValidate extends SvrProcess {
    /**
     * BPartner ID
     */
    int p_C_BPartner_ID = 0;
    /**
     * BPartner Group
     */
    int p_C_BP_Group_ID = 0;

    /**
     * Prepare
     */
    protected void prepare() {
        p_C_BPartner_ID = getRecordId();
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("C_BPartner_ID")) p_C_BPartner_ID = iProcessInfoParameter.getParameterAsInt();
            else if (name.equals("C_BP_Group_ID")) p_C_BP_Group_ID = iProcessInfoParameter.getParameterAsInt();
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
            log.info("C_BPartner_ID=" + p_C_BPartner_ID + ", C_BP_Group_ID=" + p_C_BP_Group_ID);
        if (p_C_BPartner_ID == 0 && p_C_BP_Group_ID == 0)
            throw new AdempiereUserError("No Business Partner/Group selected");

        if (p_C_BP_Group_ID == 0) {
            MBPartner bp = new MBPartner(p_C_BPartner_ID);
            if (bp.getId() == 0)
                throw new AdempiereUserError(
                        "Business Partner not found - C_BPartner_ID=" + p_C_BPartner_ID);
            checkBP(bp);
        } else {
            final String whereClause = "C_BP_Group_ID=?";
            List<MBPartner> it =
                    new Query(I_C_BPartner.Table_Name, whereClause)
                            .setParameters(p_C_BP_Group_ID)
                            .setOnlyActiveRecords(true)
                            .list();
            for (MBPartner partner : it) {
                checkBP(partner);
            }
        }
        //
        return "OK";
    } //	doIt

    /**
     * Check BP
     *
     * @param bp bp
     * @throws SQLException
     */
    private void checkBP(MBPartner bp) throws SQLException {
        addLog(0, null, null, bp.getName() + ":");
        //	See also VMerge.postMerge
        checkPayments(bp);
        checkInvoices(bp);
        //
        bp.setTotalOpenBalance();
        bp.setActualLifeTimeValue();
        bp.saveEx();
        //
        //	if (bp.getSalesOrderCreditUsed().signum() != 0)
        addLog(0, null, bp.getSalesOrderCreditUsed(), MsgKt.getElementTranslation("SO_CreditUsed"));
        addLog(0, null, bp.getTotalOpenBalance(), MsgKt.getElementTranslation("TotalOpenBalance"));
        addLog(0, null, bp.getActualLifeTimeValue(), MsgKt.getElementTranslation("ActualLifeTimeValue"));
        //
    } //	checkBP

    /**
     * Check Payments
     *
     * @param bp business partner
     */
    private void checkPayments(MBPartner bp) {
        //	See also VMerge.postMerge
        int changed = 0;
        MPayment[] payments = MPayment.getOfBPartner(bp.getBusinessPartnerId());
        for (int i = 0; i < payments.length; i++) {
            MPayment payment = payments[i];
            if (payment.testAllocation()) {
                payment.saveEx();
                changed++;
            }
        }
        if (changed != 0)
            addLog(
                    0,
                    null,
                    new BigDecimal(payments.length),
                    MsgKt.getElementTranslation("C_Payment_ID") + " - #" + changed);
    } //	checkPayments

    /**
     * Check Invoices
     *
     * @param bp business partner
     */
    private void checkInvoices(MBPartner bp) {
        //	See also VMerge.postMerge
        int changed = 0;
        MInvoice[] invoices = MInvoice.getOfBPartner(bp.getBusinessPartnerId());
        for (int i = 0; i < invoices.length; i++) {
            MInvoice invoice = invoices[i];
            if (invoice.testAllocation()) {
                invoice.saveEx();
                changed++;
            }
        }
        if (changed != 0)
            addLog(
                    0,
                    null,
                    new BigDecimal(invoices.length),
                    MsgKt.getElementTranslation("C_Invoice_ID") + " - #" + changed);
    } //	checkInvoices
} //	BPartnerValidate
