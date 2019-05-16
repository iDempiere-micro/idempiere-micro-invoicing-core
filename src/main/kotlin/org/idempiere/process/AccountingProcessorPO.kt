package org.idempiere.process

import kotliquery.Row
import org.compiere.model.AccountingProcessor
import org.compiere.orm.BasePOName
import org.idempiere.common.util.AdempiereSystemError

import java.sql.Timestamp

open class AccountingProcessorPO : BasePOName, AccountingProcessor {

    override val tableId: Int
        get() = AccountingProcessor.Table_ID

    /**
     * Standard Constructor
     */
    constructor(C_AcctProcessor_ID: Int) : super(C_AcctProcessor_ID) {}

    /**
     * Load Constructor
     */
    constructor(row: Row) : super(row) {}

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    override fun getAccessLevel(): Int {
        return AccountingProcessor.accessLevel.toInt()
    }

    override fun toString(): String {
        return "AccountingProcessorPO[$id]"
    }

    /**
     * Get Schedule.
     *
     * @return Schedule
     */
    override fun getScheduleId(): Int {
        return getValue<Int>(AccountingProcessor.COLUMNNAME_AD_Schedule_ID) ?: return 0
    }

    /**
     * Set Schedule.
     *
     * @param AD_Schedule_ID Schedule
     */
    override fun setScheduleId(AD_Schedule_ID: Int) {
        if (AD_Schedule_ID < 1)
            setValue(AccountingProcessor.COLUMNNAME_AD_Schedule_ID, null)
        else
            setValue(AccountingProcessor.COLUMNNAME_AD_Schedule_ID, AD_Schedule_ID)
    }

    /**
     * Get Accounting Processor.
     *
     * @return Accounting Processor/Server Parameters
     */
    override fun getAccountingProcessorId(): Int {
        return getValue<Int>(AccountingProcessor.COLUMNNAME_C_AcctProcessor_ID) ?: return 0
    }

    /**
     * Get Date next run.
     *
     * @return Date the process will run next
     */
    override fun getDateNextRun(): Timestamp {
        return getValue<Timestamp>(AccountingProcessor.COLUMNNAME_DateNextRun) ?: throw AdempiereSystemError("Date the process will run next not set")
    }

    /**
     * Set Date next run.
     *
     * @param DateNextRun Date the process will run next
     */
    override fun setDateNextRun(DateNextRun: Timestamp) {
        setValue(AccountingProcessor.COLUMNNAME_DateNextRun, DateNextRun)
    }

    /**
     * Set Days to keep Log.
     *
     * @param KeepLogDays Number of days to keep the log entries
     */
    override fun setKeepLogDays(KeepLogDays: Int) {
        setValue(AccountingProcessor.COLUMNNAME_KeepLogDays, KeepLogDays)
    }

    /**
     * Set Supervisor.
     *
     * @param Supervisor_ID Supervisor for this user/organization - used for escalation and approval
     */
    override fun setSupervisorId(Supervisor_ID: Int) {
        if (Supervisor_ID < 1)
            setValue(AccountingProcessor.COLUMNNAME_Supervisor_ID, null)
        else
            setValue(AccountingProcessor.COLUMNNAME_Supervisor_ID, Supervisor_ID)
    }

    companion object {
        private const val serialVersionUID = 20171031L
    }
}
