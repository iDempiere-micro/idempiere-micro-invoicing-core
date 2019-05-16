package org.compiere.accounting

import org.compiere.bo.getCurrencyStdPrecision
import org.compiere.tax.MTax
import org.idempiere.common.exceptions.AdempiereException
import software.hsharp.core.util.DB
import software.hsharp.core.util.asResource
import software.hsharp.core.util.queryOf
import java.math.BigDecimal
import java.util.logging.Level
import kotlin.collections.set

/**
 * Get Commitments Sales
 *
 * @param doc document
 * @param maxQuantity Qty invoiced/matched
 * @return commitments (order lines)
 */
fun getCommitmentsSales(doc: Doc, maxQuantity: BigDecimal, M_InOutLine_ID: Int): Array<DocLine> {
    var precision = -1
    var maxQty = maxQuantity

    return "/sql/getBPLocation.sql".asResource { sql ->
        val query = queryOf(sql, listOf(M_InOutLine_ID)).map { row -> MOrderLine(row) }.asList
        val items = DB.current.run(query)

        items.map { line ->

            val docLine = DocLine(line, doc)
            // 	Currency
            if (precision == -1) {
                doc.currencyId = docLine.currencyId
                precision = getCurrencyStdPrecision(docLine.currencyId)
            }
            // 	Qty
            val qty = line.qtyOrdered.max(maxQty)
            docLine.setQty(qty, false)
            //
            val priceActual = line.priceActual
            val priceCost = line.priceCost
            var lineNetAmt: BigDecimal?
            lineNetAmt = if (priceCost != null && priceCost.signum() != 0)
                qty.multiply(priceCost)
            else if (qty == maxQty)
                line.lineNetAmt
            else
                qty.multiply(priceActual)
            maxQty = maxQty.subtract(qty)

            docLine.setAmount(lineNetAmt) // 	DR
            var priceList = line.priceList
            val taxId = docLine.taxId
            // 	Correct included Tax
            if (taxId != 0 && line.parent.isTaxIncluded) {
                val tax = MTax.get(taxId)
                if (!tax.isZeroTax) {
                    val lineNetAmtTax = tax.calculateTax(lineNetAmt, true, precision)
                    if (Doc.s_log.isLoggable(Level.FINE))
                        Doc.s_log.fine("LineNetAmt=$lineNetAmt - Tax=$lineNetAmtTax")
                    lineNetAmt = lineNetAmt.subtract(lineNetAmtTax)
                    val priceListTax = tax.calculateTax(priceList, true, precision)
                    priceList = priceList.subtract(priceListTax)
                }
            } // 	correct included Tax

            docLine.setAmount(lineNetAmt, priceList, qty)
            docLine
        }.toTypedArray()
    }
} // 	getCommitmentsSales

/**
 * Load Requisitions
 *
 * @return requisition lines of Order
 */
fun loadRequisitions(order: MOrder, docOrder: Doc_Order): Array<DocLine> {
    val oLines = order.lines
    val quantities = HashMap<Int, BigDecimal>()
    for (i in oLines.indices) {
        val line = oLines[i]
        quantities[line.orderLineId] = line.qtyOrdered
    }

    return "/sql/loadRequisitions.sql".asResource { sql ->

        val query = queryOf(sql, listOf(order.orderId)).map { row -> MRequisitionLine(row) }.asList
        val items = DB.current.run(query)
        items.mapNotNull { line ->
            val docLine = DocLine(line, docOrder)
            // 	Quantity - not more then OrderLine
            // 	Issue: Split of Requisition to multiple POs & different price
            val key = line.orderLineId
            val maxQty = quantities[key] ?: throw AdempiereException("qty must be not null")
            val qty = line.qty.max(maxQty)
            if (qty.signum() != 0) {
                docLine.setQty(qty, false)
                quantities[key] = maxQty.subtract(qty)
                //
                val priceActual = line.priceActual
                var lineNetAmt = line.lineNetAmt
                if (line.qty.compareTo(qty) != 0) lineNetAmt = priceActual.multiply(qty)
                docLine.setAmount(lineNetAmt) // DR
                docLine
            } else null
        }.toTypedArray()
    }
} // loadRequisitions

/**
 * Get Commitments
 *
 * @param doc document
 * @param QtyInvoicedOrMatched Qty invoiced/matched
 * @param C_InvoiceLine_ID invoice line
 * @return commitments (order lines)
 */
fun getCommitments(doc: Doc, QtyInvoicedOrMatched: BigDecimal, C_InvoiceLine_ID: Int): Array<DocLine> {
    var maxQty = QtyInvoicedOrMatched
    var precision = -1
    //
    return "/sql/getBPLocation.sql".asResource { sql ->
        val query =
            queryOf(sql, listOf(C_InvoiceLine_ID, C_InvoiceLine_ID)).map { row -> MOrderLine(row) }
                .asList
        val items = DB.current.run(query)
        items.map { line ->
            val docLine = DocLine(line, doc)
            // 	Currency
            if (precision == -1) {
                doc.currencyId = docLine.currencyId
                precision = getCurrencyStdPrecision(docLine.currencyId)
            }
            // 	Qty
            val qty = line.qtyOrdered.max(maxQty)
            docLine.setQty(qty, false)
            //
            val priceActual = line.priceActual
            val priceCost = line.priceCost
            var lineNetAmt: BigDecimal?
            lineNetAmt = if (priceCost != null && priceCost.signum() != 0)
                qty.multiply(priceCost)
            else if (qty == maxQty)
                line.lineNetAmt
            else
                qty.multiply(priceActual)
            maxQty = maxQty.subtract(qty)

            docLine.setAmount(lineNetAmt) // 	DR
            var priceList = line.priceList
            val taxId = docLine.taxId
            // 	Correct included Tax
            if (taxId != 0 && line.parent.isTaxIncluded) {
                val tax = MTax.get(taxId)
                if (!tax.isZeroTax) {
                    val lineNetAmtTax = tax.calculateTax(lineNetAmt, true, precision)
                    if (Doc.s_log.isLoggable(Level.FINE))
                        Doc.s_log.fine("LineNetAmt=$lineNetAmt - Tax=$lineNetAmtTax")
                    lineNetAmt = lineNetAmt.subtract(lineNetAmtTax)
                    val priceListTax = tax.calculateTax(priceList, true, precision)
                    priceList = priceList.subtract(priceListTax)
                }
            } // 	correct included Tax

            docLine.setAmount(lineNetAmt, priceList, qty)
            docLine
        }.toTypedArray()
    }
} // 	getCommitments