package org.idempiere.process

import software.hsharp.core.util.DB
import software.hsharp.core.util.Environment
import software.hsharp.core.util.queryOf

/**
 * Get Default Category
 *
 * @param ctx context
 * @param CategoryType optional CategoryType (ignored, if not exists)
 * @return GL Category or null
 */
fun getDefault(CategoryType: String?): MGLCategory? {
    val sql = "SELECT * FROM GL_Category WHERE AD_Client_ID=? AND IsDefault='Y'"

    val query = queryOf(sql, listOf(Environment.current.clientId)).map { row -> MGLCategory(row) }.asList
    val items = DB.current.run(query)

    return items.firstOrNull { CategoryType != null && it.categoryType == CategoryType } ?: items.firstOrNull()
} // 	getDefault
