package org.idempiere.process;

import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInOutConfirm;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;

import java.util.logging.Level;

/**
 * Create Confirmation From Shipment
 *
 * @author Jorg Janke
 * @version $Id: InOutCreateConfirm.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class InOutCreateConfirm extends SvrProcess {
    /**
     * Shipment
     */
    private int p_M_InOut_ID = 0;
    /**
     * Confirmation Type
     */
    private String p_ConfirmType = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (para[i].getParameter() == null) ;
            else if (name.equals("ConfirmType")) p_ConfirmType = (String) para[i].getParameter();
            else log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }
        p_M_InOut_ID = getRecord_ID();
    } //	prepare

    /**
     * Create Confirmation
     *
     * @return document no
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info("M_InOut_ID=" + p_M_InOut_ID + ", Type=" + p_ConfirmType);
        MInOut shipment = new MInOut(getCtx(), p_M_InOut_ID);
        if (shipment.getId() == 0)
            throw new IllegalArgumentException("Not found M_InOut_ID=" + p_M_InOut_ID);
        //
        MInOutConfirm confirm = MInOutConfirm.create(shipment, p_ConfirmType, true);
        if (confirm == null)
            throw new Exception("Cannot create Confirmation for " + shipment.getDocumentNo());
        //

        addLog(
                confirm.getM_InOutConfirm_ID(),
                null,
                null,
                confirm.getDocumentNo(),
                confirm.getTableId(),
                confirm.getM_InOutConfirm_ID());

        return confirm.getDocumentNo();
    } //	doIt
} //	InOutCreateConfirm
