package org.compiere.accounting

import kotliquery.Row
import software.hsharp.core.util.DB
import software.hsharp.core.util.queryOf

/**
 * Get Allocations of Payment
 *
 * @param ctx context
 * @param C_Payment_ID payment
 * @return allocations of payment
 */
fun getAllocationsOfPayment(C_Payment_ID: Int): Array<MAllocationHdr> {
    val sql = ("SELECT * FROM C_AllocationHdr h " +
            "WHERE IsActive='Y'" +
            " AND EXISTS (SELECT * FROM C_AllocationLine l " +
            "WHERE h.C_AllocationHdr_ID=l.C_AllocationHdr_ID AND l.C_Payment_ID=?)")
    val query = queryOf(sql, listOf(C_Payment_ID)).map { row -> MAllocationHdr(row, -1) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getOfPayment

/**
 * Get Allocations of Invoice
 *
 * @param ctx context
 * @param C_Invoice_ID payment
 * @return allocations of payment
 */
fun getAllocationsOfInvoice(C_Invoice_ID: Int): Array<MAllocationHdr> {
    val sql = ("SELECT * FROM C_AllocationHdr h " +
            "WHERE IsActive='Y'" +
            " AND EXISTS (SELECT * FROM C_AllocationLine l " +
            "WHERE h.C_AllocationHdr_ID=l.C_AllocationHdr_ID AND l.C_Invoice_ID=?)")
    val query = queryOf(sql, listOf(C_Invoice_ID)).map { row -> MAllocationHdr(row, -1) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getOfInvoice

/**
 * Get Lines
 *
 * @param requery if true requery
 * @return lines
 */
fun getAllocationLines(allocationHeaderId: Int, parent: MAllocationHdr): Array<MAllocationLine> {
    //
    val sql = "SELECT * FROM C_AllocationLine WHERE C_AllocationHdr_ID=?"
    fun load(row: Row): MAllocationLine {
        val line = MAllocationLine(row)
        line.parent = parent
        return line
    }

    val query = queryOf(sql, listOf(allocationHeaderId)).map { row -> load(row) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getLines
