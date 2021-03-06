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

import org.compiere.process.SvrProcess;

import java.util.logging.Level;

/**
 * Create AP Invoices for Commission
 *
 * @author Jorg Janke
 * @version $Id: CommissionAPInvoice.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class CommissionAPInvoice extends SvrProcess {
    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (variables are parsed)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO)) log.info("doIt - C_CommissionRun_ID=" + getRecordId());

        throw new NotImplementedException();

    /*
    	//	Load Data
    	MCommissionRun comRun = new MCommissionRun (getRecordId(), null);
    	if (comRun.getId() == 0)
    		throw new IllegalArgumentException("CommissionAPInvoice - No Commission Run");
    	if (Env.ZERO.compareTo(comRun.getGrandTotal()) == 0)
    		throw new IllegalArgumentException("@GrandTotal@ = 0");
    	MCommission com = new MCommission (comRun.getCommissionId(), null);
    	if (com.getId() == 0)
    		throw new IllegalArgumentException("CommissionAPInvoice - No Commission");
    	if (com.getChargeId() == 0)
    		throw new IllegalArgumentException("CommissionAPInvoice - No Charge on Commission");
    	MBPartner bp = new MBPartner (com.getBusinessPartnerId(), null);
    	if (bp.getId() == 0)
    		throw new IllegalArgumentException("CommissionAPInvoice - No BPartner");

    	//	Create Invoice
    	MInvoice invoice = new MInvoice (0, null);
    	invoice.setClientOrg(com.getClientId(), com.getOrgId());
    	invoice.setTargetDocumentTypeId(MDocType.DOCBASETYPE_APInvoice);	//	API
    	invoice.setBPartner(bp);
    //	invoice.setDocumentNo (comRun.getDocumentNo());		//	may cause unique constraint
    	invoice.setSalesRepresentativeId(getUserId());	//	caller
    	//
    	if (com.getCurrencyId() != invoice.getCurrencyId())
    		throw new IllegalArgumentException("CommissionAPInvoice - Currency of PO Price List not Commission Currency");
    	//
    	if (!invoice.save())
    		throw new IllegalStateException("CommissionAPInvoice - cannot save Invoice");

    		//	Create Invoice Line
    		MInvoiceLine iLine = new MInvoiceLine(invoice);
    	iLine.setChargeId(com.getChargeId());
    		iLine.setQty(1);
    		iLine.setPrice(comRun.getGrandTotal());
    	iLine.setTax();
    	if (!iLine.save())
    		throw new IllegalStateException("CommissionAPInvoice - cannot save Invoice Line");
    	//
    	return "@C_Invoice_ID@ = " + invoice.getDocumentNo();
    	*/
    } //	doIt
} //	CommissionAPInvoice
