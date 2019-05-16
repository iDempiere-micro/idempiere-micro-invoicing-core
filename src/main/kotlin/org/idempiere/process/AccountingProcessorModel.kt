package org.idempiere.process

import kotliquery.Row
import org.compiere.accounting.MClient
import org.compiere.model.AccountingProcessor.COLUMNNAME_DateLastRun
import org.compiere.model.AdempiereProcessor
import org.compiere.model.AdempiereProcessor2
import org.compiere.schedule.MSchedule
import org.compiere.util.translate
import java.sql.Timestamp

class AccountingProcessorModel : AccountingProcessorPO, AdempiereProcessor, AdempiereProcessor2 {

    /**
     * Standard Constructor
     *
     * @param C_AcctProcessor_ID id
     */
    constructor(C_AcctProcessor_ID: Int) : super(C_AcctProcessor_ID) {
        if (C_AcctProcessor_ID == 0) {
            setKeepLogDays(7) // 7
        }
    } // 	AccountingProcessorModel

    /**
     * Load Constructor
     *
     */
    constructor(row: Row) : super(row) // 	AccountingProcessorModel

    /**
     * Parent Constructor
     *
     * @param client parent
     * @param Supervisor_ID admin
     */
    constructor(client: MClient, Supervisor_ID: Int) : this(0) {
        setClientOrg(client)
        val msgset = client.name +
                " - " +
                translate("C_AcctProcessor_ID")
        name = msgset
        setSupervisorId(Supervisor_ID)
    } // 	AccountingProcessorModel

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    override fun beforeSave(newRecord: Boolean): Boolean {
        if (newRecord || isValueChanged("AD_Schedule_ID")) {
            val nextWork = MSchedule.getNextRunMS(
                System.currentTimeMillis(),
                scheduleType,
                frequencyType,
                frequency,
                cronPattern
            )
            if (nextWork > 0) dateNextRun = Timestamp(nextWork)
        }

        return true
    } // 	beforeSave

    /**
     * Get Date Next Run
     *
     * @param requery requery
     * @return date next run
     */
    override fun getDateNextRun(requery: Boolean): Timestamp {
        if (requery) loadFromMap(null)
        return dateNextRun
    } // 	getDateNextRun

    override fun getFrequencyType(): String {
        return MSchedule.get(scheduleId).frequencyType
    }

    override fun getFrequency(): Int {
        return MSchedule.get(scheduleId).frequency
    }

    override fun isIgnoreProcessingTime(): Boolean {
        return MSchedule.get(scheduleId).isIgnoreProcessingTime
    }

    override fun getScheduleType(): String {
        return MSchedule.get(scheduleId).scheduleType
    }

    override fun getCronPattern(): String? {
        return MSchedule.get(scheduleId).cronPattern
    }

    /**
     * Get Date last run.
     *
     * @return Date the process was last run.
     */
    override fun getDateLastRun(): Timestamp? {
        return getValue(COLUMNNAME_DateLastRun)
    }

    /**
     * Set Date last run.
     *
     * @param DateLastRun Date the process was last run.
     */
    override fun setDateLastRun(DateLastRun: Timestamp) {
        setValue(COLUMNNAME_DateLastRun, DateLastRun)
    }

    companion object {
        private const val serialVersionUID = -4760475718973777369L
    }
} // 	AccountingProcessorModel
