package org.idempiere.process;

import org.compiere.invoicing.MInvoice;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Copy Invoice Lines
 *
 * @author Jorg Janke
 * @version $Id: CopyFromInvoice.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class CopyFromInvoice extends SvrProcess {
    private int m_C_Invoice_ID = 0;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("C_Invoice_ID"))
                m_C_Invoice_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
            else log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        int To_C_Invoice_ID = getRecordId();
        if (log.isLoggable(Level.INFO))
            log.info("From C_Invoice_ID=" + m_C_Invoice_ID + " to " + To_C_Invoice_ID);
        if (To_C_Invoice_ID == 0) throw new IllegalArgumentException("Target C_Invoice_ID == 0");
        if (m_C_Invoice_ID == 0) throw new IllegalArgumentException("Source C_Invoice_ID == 0");
        MInvoice from = new MInvoice(null, m_C_Invoice_ID);
        MInvoice to = new MInvoice(null, To_C_Invoice_ID);
        //
        int no = to.copyLinesFrom(from, false, false);
        //
        return "@Copied@=" + no;
    } //	doIt
} //	CopyFromInvoice
