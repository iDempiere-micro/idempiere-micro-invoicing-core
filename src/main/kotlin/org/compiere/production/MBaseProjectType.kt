package org.compiere.production

import software.hsharp.core.util.DB
import software.hsharp.core.util.queryOf

/**
 * Get Project Type
 * Phases
 *
 * @return Array of phases
 */
fun getProjectTypePhases(projectTypeId: Int): Array<MProjectTypePhase> {
    val sql = "SELECT * FROM C_Phase WHERE C_ProjectType_ID=? ORDER BY SeqNo"
    val query = queryOf(sql, listOf(projectTypeId)).map { row -> MProjectTypePhase(row) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getPhases
