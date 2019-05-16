package org.idempiere.process;

import org.compiere.accounting.MOrder;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.order.OrderConstants;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.process.SvrProcess;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

/**
 * Copy Order and optionally close
 *
 * @author Jorg Janke
 * @version $Id: CopyOrder.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class CopyOrder extends SvrProcess {
    /**
     * Order to Copy
     */
    private int p_C_Order_ID = 0;
    /**
     * Document Type of new Order
     */
    private int p_C_DocType_ID = 0;
    /**
     * New Doc Date
     */
    private Timestamp p_DateDoc = null;
    /**
     * Close/Process Old Order
     */
    private boolean p_IsCloseDocument = false;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            switch (name) {
                case "C_Order_ID":
                    p_C_Order_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "C_DocType_ID":
                    p_C_DocType_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "DateDoc":
                    p_DateDoc = (Timestamp) iProcessInfoParameter.getParameter();
                    break;
                case "IsCloseDocument":
                    p_IsCloseDocument = "Y".equals(iProcessInfoParameter.getParameter());
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "C_Order_ID="
                            + p_C_Order_ID
                            + ", C_DocType_ID="
                            + p_C_DocType_ID
                            + ", CloseDocument="
                            + p_IsCloseDocument);
        if (p_C_Order_ID == 0) throw new IllegalArgumentException("No Order");
        MDocType dt = MDocTypeKt.getDocumentType(p_C_DocType_ID);
        if (dt.getId() == 0) throw new IllegalArgumentException("No DocType");
        if (p_DateDoc == null) p_DateDoc = new Timestamp(System.currentTimeMillis());
        //
        MOrder from = new MOrder(p_C_Order_ID);
        MOrder newOrder =
                MOrder.Companion.copyFrom(
                        from,
                        p_DateDoc,
                        dt.getDocTypeId(),
                        dt.isSOTrx(),
                        false,
                        true
                ); //	copy ASI
        newOrder.setTargetDocumentTypeId(p_C_DocType_ID);
        newOrder.setQuotationOrderId(from.getOrderId()); // IDEMPIERE-475
        boolean OK = newOrder.save();
        if (!OK) throw new IllegalStateException("Could not create new Order");
        //
        if (p_IsCloseDocument) {
            MOrder original = new MOrder(p_C_Order_ID);
            original.setDocAction(OrderConstants.DOCACTION_Complete);
            if (!original.processIt(OrderConstants.DOCACTION_Complete)) {
                log.warning(
                        "Order Process Failed: " + original.getDocumentNo() + " " + original.getProcessMsg());
                throw new IllegalStateException(
                        "Order Process Failed: " + original.getDocumentNo() + " " + original.getProcessMsg());
            }
            original.saveEx();
            original.setDocAction(OrderConstants.DOCACTION_Close);
            if (!original.processIt(OrderConstants.DOCACTION_Close)) {
                log.warning(
                        "Order Process Failed: " + original.getDocumentNo() + " " + original.getProcessMsg());
                throw new IllegalStateException(
                        "Order Process Failed: " + original.getDocumentNo() + " " + original.getProcessMsg());
            }

            original.saveEx();
        }
        //
        //	Env.setSOTrx(newOrder.isSOTrx());
        //	return "@C_Order_ID@ " + newOrder.getDocumentNo();
        addLog(0, null, null, dt.getName() + ": " + newOrder.getDocumentNo(), newOrder.getTableId(), newOrder.getOrderId());
        return "@C_Order_ID@ @Created@";
    } //	doIt
} //	CopyOrder
