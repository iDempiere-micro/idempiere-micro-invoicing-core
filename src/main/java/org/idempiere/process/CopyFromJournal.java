package org.idempiere.process;

import org.compiere.accounting.MJournalBatch;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;

import java.math.BigDecimal;
import java.util.logging.Level;

/**
 * Copy GL Batch Journal/Lines
 *
 * @author Jorg Janke
 * @version $Id: CopyFromJournal.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class CopyFromJournal extends SvrProcess {
    private int m_GL_JournalBatch_ID = 0;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("GL_JournalBatch_ID"))
                m_GL_JournalBatch_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
            else log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        int To_GL_JournalBatch_ID = getRecordId();
        if (log.isLoggable(Level.INFO))
            log.info(
                    "doIt - From GL_JournalBatch_ID="
                            + m_GL_JournalBatch_ID
                            + " to "
                            + To_GL_JournalBatch_ID);
        if (To_GL_JournalBatch_ID == 0)
            throw new IllegalArgumentException("Target GL_JournalBatch_ID == 0");
        if (m_GL_JournalBatch_ID == 0)
            throw new IllegalArgumentException("Source GL_JournalBatch_ID == 0");
        MJournalBatch from = new MJournalBatch(m_GL_JournalBatch_ID);
        MJournalBatch to = new MJournalBatch(To_GL_JournalBatch_ID);
        //
        int no = to.copyDetailsFrom(from);
        //
        return "@Copied@=" + no;
    } //	doIt
}
