package org.idempiere.process;

import org.compiere.accounting.MOrder;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereUserError;
import software.hsharp.core.util.DBKt;

import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.convertString;

/**
 * Order Batch Processing
 *
 * @author Jorg Janke
 * @version $Id: OrderBatchProcess.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class OrderBatchProcess extends SvrProcess {
    private int p_C_DocTypeTarget_ID = 0;
    private String p_DocStatus = null;
    private int p_C_BPartner_ID = 0;
    private String p_IsSelfService = null;
    private Timestamp p_DateOrdered_From = null;
    private Timestamp p_DateOrdered_To = null;
    private String p_DocAction = null;
    private String p_IsDelivered = null;
    private String p_IsInvoiced = null;

    /**
     * Prepare
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (para[i].getParameter() == null && para[i].getParameterTo() == null) ;
            else if (name.equals("C_DocTypeTarget_ID"))
                p_C_DocTypeTarget_ID = para[i].getParameterAsInt();
            else if (name.equals("DocStatus")) p_DocStatus = (String) para[i].getParameter();
            else if (name.equals("IsSelfService")) p_IsSelfService = (String) para[i].getParameter();
            else if (name.equals("C_BPartner_ID")) p_C_BPartner_ID = para[i].getParameterAsInt();
            else if (name.equals("DateOrdered")) {
                p_DateOrdered_From = (Timestamp) para[i].getParameter();
                p_DateOrdered_To = (Timestamp) para[i].getParameterTo();
            } else if (name.equals("DocAction")) p_DocAction = (String) para[i].getParameter();
            else if (name.equals("IsDelivered")) {
                p_IsDelivered = (String) para[i].getParameter();
            } else if (name.equals("IsInvoiced")) {
                p_IsInvoiced = (String) para[i].getParameter();
            } else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
    } //	prepare

    /**
     * Process
     *
     * @return msg
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "C_DocTypeTarget_ID="
                            + p_C_DocTypeTarget_ID
                            + ", DocStatus="
                            + p_DocStatus
                            + ", IsSelfService="
                            + p_IsSelfService
                            + ", C_BPartner_ID="
                            + p_C_BPartner_ID
                            + ", DateOrdered="
                            + p_DateOrdered_From
                            + "->"
                            + p_DateOrdered_To
                            + ", DocAction="
                            + p_DocAction
                            + ", IsDelivered="
                            + p_IsDelivered
                            + ", IsInvoiced="
                            + p_IsInvoiced);

        if (p_C_DocTypeTarget_ID == 0) throw new AdempiereUserError("@NotFound@: @C_DocTypeTarget_ID@");
        if (p_DocStatus == null || p_DocStatus.length() != 2)
            throw new AdempiereUserError("@NotFound@: @DocStatus@");
        if (p_DocAction == null || p_DocAction.length() != 2)
            throw new AdempiereUserError("@NotFound@: @DocAction@");

        //
        StringBuilder sql =
                new StringBuilder("SELECT * FROM C_Order o ")
                        .append(" WHERE o.C_DocTypeTarget_ID=? AND o.DocStatus=? ");
        if (p_IsSelfService != null && p_IsSelfService.length() == 1)
            sql.append(" AND o.IsSelfService=").append(convertString(p_IsSelfService));
        if (p_C_BPartner_ID != 0) sql.append(" AND o.C_BPartner_ID=").append(p_C_BPartner_ID);
        if (p_DateOrdered_From != null)
            sql.append(" AND TRUNC(o.DateOrdered) >= ").append(DBKt.convertDate(p_DateOrdered_From, true));
        if (p_DateOrdered_To != null)
            sql.append(" AND TRUNC(o.DateOrdered) <= ").append(DBKt.convertDate(p_DateOrdered_To, true));
        if ("Y".equals(p_IsDelivered))
            sql.append(" AND NOT EXISTS (SELECT l.C_OrderLine_ID FROM C_OrderLine l ")
                    .append(" WHERE l.C_Order_ID=o.C_Order_ID AND l.QtyOrdered>l.QtyDelivered) ");
        else if ("N".equals(p_IsDelivered))
            sql.append(" AND EXISTS (SELECT l.C_OrderLine_ID FROM C_OrderLine l ")
                    .append(" WHERE l.C_Order_ID=o.C_Order_ID AND l.QtyOrdered>l.QtyDelivered) ");
        if ("Y".equals(p_IsInvoiced))
            sql.append(" AND NOT EXISTS (SELECT l.C_OrderLine_ID FROM C_OrderLine l ")
                    .append(" WHERE l.C_Order_ID=o.C_Order_ID AND l.QtyOrdered>l.QtyInvoiced) ");
        else if ("N".equals(p_IsInvoiced))
            sql.append(" AND EXISTS (SELECT l.C_OrderLine_ID FROM C_OrderLine l ")
                    .append(" WHERE l.C_Order_ID=o.C_Order_ID AND l.QtyOrdered>l.QtyInvoiced) ");

        MOrder[] items = BaseOrderBatchProcessKt.getOrdersToBatchProcess(sql.toString(), p_C_DocTypeTarget_ID, p_DocStatus);

        int counter = 0;
        int errCounter = 0;
        for (MOrder order : items) {
            if (process(order)) counter++;
            else errCounter++;
        }

        StringBuilder msgreturn =
                new StringBuilder("@Updated@=").append(counter).append(", @Errors@=").append(errCounter);
        return msgreturn.toString();
    } //	doIt

    /**
     * Process Order
     *
     * @param order order
     * @return true if ok
     */
    private boolean process(MOrder order) {
        if (log.isLoggable(Level.INFO)) log.info(order.toString());
        //
        order.setDocAction(p_DocAction);
        if (order.processIt(p_DocAction)) {
            order.saveEx();
            addLog(0, null, null, order.getDocumentNo() + ": OK");
            return true;
        } else {
            log.warning("Order Process Failed: " + order + " - " + order.getProcessMsg());
            throw new IllegalStateException(
                    "Order Process Failed: " + order + " - " + order.getProcessMsg());
        }
        // commented by zuhri - unreachable code
        // addLog (0, null, null, order.getDocumentNo() + ": Error " + order.getProcessMsg());
        // return false;
        // end commented out by zuhri
    } //	process
} //	OrderBatchProcess
