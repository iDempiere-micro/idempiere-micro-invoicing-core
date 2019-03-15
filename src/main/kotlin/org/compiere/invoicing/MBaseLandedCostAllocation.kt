package org.compiere.invoicing

import software.hsharp.core.util.DB
import software.hsharp.core.util.queryOf
import java.util.Properties

/**
 * Get Cost Allocations for invoice Line
 *
 * @param ctx context
 * @param C_InvoiceLine_ID invoice line
 * @return landed cost alloc
 */
fun getCostAllocationsforInvoiceLine(
    ctx: Properties,
    C_InvoiceLine_ID: Int
): Array<MLandedCostAllocation> {
    val sql = "SELECT * FROM C_LandedCostAllocation WHERE C_InvoiceLine_ID=?"

    val query = queryOf(sql, listOf(C_InvoiceLine_ID)).map { row -> MLandedCostAllocation(ctx, row) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getOfInvliceLine