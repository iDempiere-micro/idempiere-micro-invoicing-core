package org.idempiere.process

import org.compiere.accounting.DocManager
import org.compiere.model.AccountingSchema
import org.idempiere.common.util.Env
import software.hsharp.core.util.DB
import software.hsharp.core.util.queryOf
import java.math.BigDecimal

fun postDocumentsInPostSession(
    sql: String,
    clientId: Int,
    processedOn: BigDecimal,
    m_ass: Array<AccountingSchema>,
    AD_Table_ID: Int
) {
    val parameters =
        listOf(clientId) +
        (if (processedOn.compareTo(Env.ZERO) != 0) listOf(processedOn) else emptyList())

    val query =
        queryOf(sql, parameters)
        .map {
            DocManager.postDocument(
                m_ass, AD_Table_ID, it, false, false
            )
        }.asList
    DB.current.run(query)
}