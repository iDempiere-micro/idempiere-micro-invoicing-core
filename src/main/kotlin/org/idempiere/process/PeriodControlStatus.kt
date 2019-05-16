package org.idempiere.process

import org.compiere.accounting.MPeriodControl
import org.compiere.process.SvrProcess
import org.idempiere.common.util.AdempiereUserError
import java.util.logging.Level

/**
 * Open/Close Period Control
 *
 * @author Jorg Janke
 * @version $Id: PeriodControlStatus.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
class PeriodControlStatus(var p_C_PeriodControl_ID: Int = 0) : SvrProcess() {
    /**
     * Prepare - e.g., get Parameters.
     */
    override fun prepare() {
        val para = parameter
        for (i in para.indices) {
            val name = para[i].parameterName
            if (para[i].parameter == null)
            else
                log.log(Level.SEVERE, "Unknown Parameter: $name")
        }
        if (p_C_PeriodControl_ID == 0) p_C_PeriodControl_ID = recordId
    } // 	prepare

    /**
     * Process
     * @return message
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun doIt(): String {
        if (log.isLoggable(Level.INFO)) log.info("C_PeriodControl_ID=$p_C_PeriodControl_ID")
        val pc = MPeriodControl(p_C_PeriodControl_ID)
        if (pc.id == 0)
            throw AdempiereUserError("@NotFound@  @C_PeriodControl_ID@=$p_C_PeriodControl_ID")
        // 	Permanently closed
        if (MPeriodControl.PERIODACTION_PermanentlyClosePeriod == pc.periodStatus)
            throw AdempiereUserError("@PeriodStatus@ = " + pc.periodStatus)
        // 	No Action
        if (MPeriodControl.PERIODACTION_NoAction == pc.periodAction)
            return "@OK@"

        // 	Open
        if (MPeriodControl.PERIODACTION_OpenPeriod == pc.periodAction)
            pc.periodStatus = MPeriodControl.PERIODSTATUS_Open
        // 	Close
        if (MPeriodControl.PERIODACTION_ClosePeriod == pc.periodAction)
            pc.periodStatus = MPeriodControl.PERIODSTATUS_Closed
        // 	Close Permanently
        if (MPeriodControl.PERIODACTION_PermanentlyClosePeriod == pc.periodAction)
            pc.periodStatus = MPeriodControl.PERIODSTATUS_PermanentlyClosed
        pc.periodAction = MPeriodControl.PERIODACTION_NoAction
        //
        val ok = pc.save()

        // 	Reset Cache
        // DAP: CacheMgt.get().reset()
        // DAP: CacheMgt.get().reset()

        return if (!ok) "@Error@" else "@OK@"
    } // 	doIt
} // 	PeriodControlStatus
