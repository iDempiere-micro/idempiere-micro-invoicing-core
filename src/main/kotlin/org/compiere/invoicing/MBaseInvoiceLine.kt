package org.compiere.invoicing

import software.hsharp.core.util.DB
import software.hsharp.core.util.queryOf

/**
 * Get LandedCost of InvoiceLine
 *
 * @param whereClause starting with AND
 * @return landedCost
 */
fun getInvoiceLineLandedCost(invoiceLineId: Int, whereClause: String?): Array<MLandedCost> {
    var sql = "SELECT * FROM C_LandedCost WHERE C_InvoiceLine_ID=? "
    if (whereClause != null) sql += whereClause

    val query = queryOf(sql, listOf(invoiceLineId)).map { row -> MLandedCost(row) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getLandedCost