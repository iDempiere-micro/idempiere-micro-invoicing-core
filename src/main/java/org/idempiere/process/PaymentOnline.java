package org.idempiere.process;

// import org.compiere.process.*;

import org.compiere.accounting.MPayment;
import org.compiere.process.SvrProcess;

import java.util.logging.Level;

/**
 * Online Payment Process
 *
 * @author Jorg Janke
 * @version $Id: PaymentOnline.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class PaymentOnline extends SvrProcess {
    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO)) log.info("Record_ID=" + getRecordId());
        //	get Payment
        MPayment pp = new MPayment(getRecordId());

        //  Process it
        boolean ok = pp.processOnline();
        pp.saveEx();
        if (!ok) throw new Exception(pp.getErrorMessage());
        return "OK";
    } //	doIt
} //	PaymentOnline
