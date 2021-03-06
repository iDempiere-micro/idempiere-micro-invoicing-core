package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.production.MRequest;
import org.idempiere.common.util.AdempiereUserError;

import java.util.logging.Level;

/**
 * Re-Open Request
 *
 * @author Jorg Janke
 * @version $Id: RequestReOpen.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class RequestReOpen extends SvrProcess {
    /**
     * Request
     */
    private int p_R_Request_ID = 0;

    /**
     * Prepare
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("R_Request_ID")) p_R_Request_ID = iProcessInfoParameter.getParameterAsInt();
            else log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }
    } //	prepare

    /**
     * Process It
     *
     * @return message
     * @throws Exception
     */
    protected String doIt() throws Exception {
        MRequest request = new MRequest(p_R_Request_ID);
        if (log.isLoggable(Level.INFO)) log.info(request.toString());
        if (request.getId() == 0)
            throw new AdempiereUserError("@NotFound@ @R_Request_ID@ " + p_R_Request_ID);

        request.setStatusId(); // 	set default status
        request.setProcessed(false);
        if (request.save() && !request.isProcessed()) return "@OK@";
        return "@Error@";
    } //	doUt
} //	RequestReOpen
