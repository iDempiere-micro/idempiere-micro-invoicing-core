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

import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.invoicing.MRMA;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_M_RMALine;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Generate invoice for Vendor RMA
 *
 * @author Ashley Ramdass
 * <p>Based on org.compiere.process.InvoiceGenerate
 */
public class InvoiceGenerateRMA extends SvrProcess {
    /**
     * Manual Selection
     */
    private boolean p_Selection = false;
    /**
     * Invoice Document Action
     */
    private String p_docAction = DocAction.Companion.getACTION_Complete();

    /**
     * Number of Invoices
     */
    private int m_created = 0;
    /**
     * Invoice Date
     */
    private Timestamp m_dateinvoiced = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {

        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("Selection")) p_Selection = "Y".equals(iProcessInfoParameter.getParameter());
            else if (name.equals("DocAction")) p_docAction = (String) iProcessInfoParameter.getParameter();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }

        m_dateinvoiced = Env.getContextAsDate();
        if (m_dateinvoiced == null) {
            m_dateinvoiced = new Timestamp(System.currentTimeMillis());
        }
    }

    protected String doIt() throws Exception {
        if (!p_Selection) {
            throw new IllegalStateException("Shipments can only be generated from selection");
        }

        String sql =
                "SELECT rma.M_RMA_ID FROM M_RMA rma, T_Selection "
                        + "WHERE rma.DocStatus='CO' AND rma.IsSOTrx='Y' AND rma.AD_Client_ID=? "
                        + "AND rma.M_RMA_ID = T_Selection.T_Selection_ID "
                        + "AND T_Selection.AD_PInstance_ID=? ";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, Env.getClientId());
            pstmt.setInt(2, getProcessInstanceId());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                generateInvoice(rs.getInt(1));
            }
        } catch (Exception ex) {
            throw new AdempiereException(ex);
        } finally {

            rs = null;
            pstmt = null;
        }
        StringBuilder msgreturn = new StringBuilder("@Created@ = ").append(m_created);
        return msgreturn.toString();
    }

    private int getInvoiceDocTypeId(int M_RMA_ID) {
        String docTypeSQl =
                "SELECT dt.C_DocTypeInvoice_ID FROM C_DocType dt "
                        + "INNER JOIN M_RMA rma ON dt.C_DocType_ID=rma.C_DocType_ID "
                        + "WHERE rma.M_RMA_ID=?";

        int docTypeId = getSQLValue(docTypeSQl, M_RMA_ID);

        return docTypeId;
    }

    private MInvoice createInvoice(MRMA rma) {
        int docTypeId = getInvoiceDocTypeId(rma.getId());

        if (docTypeId == -1) {
            throw new IllegalStateException("Could not get invoice document type for Vendor RMA");
        }

        MInvoice invoice = new MInvoice(null, 0);
        invoice.setRMA(rma);

        invoice.setTargetDocumentTypeId(docTypeId);
        if (!invoice.save()) {
            throw new IllegalStateException("Could not create invoice");
        }

        return invoice;
    }

    private MInvoiceLine[] createInvoiceLines(MRMA rma, MInvoice invoice) {
        ArrayList<MInvoiceLine> invLineList = new ArrayList<MInvoiceLine>();

        I_M_RMALine[] rmaLines = rma.getLines(true);

        for (I_M_RMALine rmaLine : rmaLines) {
            if (rmaLine.getInOutLineId() == 0 && rmaLine.getChargeId() == 0) {
                StringBuilder msgiste =
                        new StringBuilder("No customer return line - RMA = ")
                                .append(rma.getDocumentNo())
                                .append(", Line = ")
                                .append(rmaLine.getLine());
                throw new IllegalStateException(msgiste.toString());
            }

            MInvoiceLine invLine = new MInvoiceLine(invoice);
            invLine.setRMALine(rmaLine);

            if (!invLine.save()) {
                throw new IllegalStateException("Could not create invoice line");
            }

            invLineList.add(invLine);
        }

        MInvoiceLine[] invLines = new MInvoiceLine[invLineList.size()];
        invLineList.toArray(invLines);

        return invLines;
    }

    private void generateInvoice(int M_RMA_ID) {
        MRMA rma = new MRMA(M_RMA_ID);
        statusUpdate(MsgKt.getMsg("Processing") + " " + rma.getDocumentInfo());

        MInvoice invoice = createInvoice(rma);
        MInvoiceLine[] invoiceLines = createInvoiceLines(rma, invoice);

        if (invoiceLines.length == 0) {
            StringBuilder msglog =
                    new StringBuilder("No invoice lines created: M_RMA_ID=")
                            .append(M_RMA_ID)
                            .append(", M_Invoice_ID=")
                            .append(invoice.getId());
            log.log(Level.WARNING, msglog.toString());
        }

        StringBuilder processMsg = new StringBuilder().append(invoice.getDocumentNo());

        if (!invoice.processIt(p_docAction)) {
            processMsg.append(" (NOT Processed)");
            StringBuilder msg =
                    new StringBuilder("Invoice Processing failed: ")
                            .append(invoice)
                            .append(" - ")
                            .append(invoice.getProcessMsg());
            log.warning(msg.toString());
            throw new IllegalStateException(msg.toString());
        }

        if (!invoice.save()) {
            throw new IllegalStateException("Could not update invoice");
        }

        // Add processing information to process log
        String message = MsgKt.parseTranslation("@InvoiceProcessed@ " + processMsg.toString());
        addBufferLog(
                invoice.getInvoiceId(),
                invoice.getDateInvoiced(),
                null,
                message,
                invoice.getTableId(),
                invoice.getInvoiceId());
        m_created++;
    }
}
