package org.idempiere.process

import kotliquery.Row
import org.compiere.accounting.MPayment
import org.compiere.invoicing.MInvoice
import software.hsharp.core.util.DB
import software.hsharp.core.util.queryOf

private const val ONLY_AP = "P"
private const val ONLY_AR = "R"

/**
 * Get Payments of BP
 *
 * @param C_BPartner_ID id
 * @return unallocated payments
 */
fun getBusinessPartnerAllocationPayments(C_BPartner_ID: Int, p_APAR: String): Array<MPayment> {
    val sql = StringBuilder("SELECT * FROM C_Payment ")
        .append("WHERE IsAllocated='N' AND Processed='Y' AND C_BPartner_ID=?")
        .append(" AND IsPrepayment='N' AND C_Charge_ID IS NULL ")
    if (ONLY_AP == p_APAR)
        sql.append("AND IsReceipt='N' ")
    else if (ONLY_AR == p_APAR) sql.append("AND IsReceipt='Y' ")
    sql.append("ORDER BY DateTrx")

    fun load(row: Row): MPayment? {
        val payment = MPayment(row)
        val allocated = payment.getAllocatedAmt()
        if (allocated != null && allocated.compareTo(payment.getPayAmt()) == 0) {
            payment.setIsAllocated(true)
            payment.saveEx()
            return null
        } else
            return payment
    }

    val query = queryOf(sql.toString(), listOf(C_BPartner_ID)).map { row -> load(row) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getPayments

fun getBusinessPartnerAllocationInvoices(C_BPartner_ID: Int, p_APAR: String): Array<MInvoice> {
    val sql = StringBuilder("SELECT * FROM C_Invoice ")
        .append("WHERE IsPaid='N' AND Processed='Y' AND C_BPartner_ID=? ")
    if (ONLY_AP.equals(p_APAR))
        sql.append("AND IsSOTrx='N' ")
    else if (ONLY_AR.equals(p_APAR)) sql.append("AND IsSOTrx='Y' ")
    sql.append("ORDER BY DateInvoiced")

    fun load(row: Row): MInvoice? {
        val invoice = MInvoice(row, -1)
        if (invoice.getOpenAmt(false, null).signum() == 0) {
            invoice.setIsPaid(true)
            invoice.saveEx()
            return null
        } else
            return invoice
    }

    val query = queryOf(sql.toString(), listOf(C_BPartner_ID)).map { row -> load(row) }.asList
    return DB.current.run(query).toTypedArray()
}