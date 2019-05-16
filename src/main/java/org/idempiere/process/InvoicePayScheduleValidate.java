package org.idempiere.process;

import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoicePaySchedule;
import org.compiere.process.SvrProcess;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Validate Invoice Payment Schedule
 *
 * @author Jorg Janke
 * @version $Id: InvoicePayScheduleValidate.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class InvoicePayScheduleValidate extends SvrProcess {
    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO)) log.info("C_InvoicePaySchedule_ID=" + getRecordId());
        MInvoicePaySchedule[] schedule =
                MInvoicePaySchedule.getInvoicePaySchedule(0, getRecordId());
        if (schedule.length == 0)
            throw new IllegalArgumentException("InvoicePayScheduleValidate - No Schedule");
        //	Get Invoice
        MInvoice invoice = new MInvoice(null, schedule[0].getInvoiceId());
        if (invoice.getId() == 0)
            throw new IllegalArgumentException("InvoicePayScheduleValidate - No Invoice");
        //
        BigDecimal total = Env.ZERO;
        for (MInvoicePaySchedule mInvoicePaySchedule : schedule) {
            BigDecimal due = mInvoicePaySchedule.getDueAmt();
            if (due != null) total = total.add(due);
        }
        boolean valid = invoice.getGrandTotal().compareTo(total) == 0;
        invoice.setIsPayScheduleValid(valid);
        invoice.saveEx();
        //	Schedule
        for (MInvoicePaySchedule mInvoicePaySchedule : schedule) {
            if (mInvoicePaySchedule.isValid() != valid) {
                mInvoicePaySchedule.setIsValid(valid);
                mInvoicePaySchedule.saveEx();
            }
        }
        StringBuilder msg = new StringBuilder("@OK@");
        if (!valid)
            msg =
                    new StringBuilder("@GrandTotal@ = ")
                            .append(invoice.getGrandTotal())
                            .append(" <> @Total@ = ")
                            .append(total)
                            .append("  - @Difference@ = ")
                            .append(invoice.getGrandTotal().subtract(total));
        return MsgKt.parseTranslation(msg.toString());
    } //	doIt
} //	InvoicePayScheduleValidate
