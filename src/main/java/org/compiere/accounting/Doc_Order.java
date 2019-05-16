package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.tax.MTax;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Post Order Documents.
 *
 * <pre>
 *  Table:              C_Order (259)
 *  Document Types:     SOO, POO
 *  </pre>
 *
 * @author Jorg Janke
 * @version $Id: Doc_Order.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_Order extends Doc {
    /**
     * Contained Optional Tax Lines
     */
    private DocTax[] m_taxes = null;
    /**
     * Requisitions
     */
    private DocLine[] m_requisitions = null;
    /**
     * Order Currency Precision
     */
    private int m_precision = -1;

    /**
     * Constructor
     *
     * @param as accounting schema
     * @param rs record
     */
    public Doc_Order(MAcctSchema as, Row rs) {
        super(as, MOrder.class, rs, null);
    } //	Doc_Order

    /**
     * Get Commitments
     *
     * @param doc              document
     * @param maxQty           Qty invoiced/matched
     * @param C_InvoiceLine_ID invoice line
     * @return commitments (order lines)
     */
    protected static DocLine[] getCommitments(Doc doc, BigDecimal maxQty, int C_InvoiceLine_ID) {
        return BaseDoc_OrderKt.getCommitments(doc, maxQty, C_InvoiceLine_ID);
    } //	getCommitments

    /**
     * Get Commitment Release. Called from MatchInv for accrual and Allocation for Cash Based
     *
     * @param as               accounting schema
     * @param doc              doc
     * @param Qty              qty invoiced/matched
     * @param C_InvoiceLine_ID line
     * @param multiplier       1 for accrual
     * @return Fact
     */
    public static Fact getCommitmentRelease(
            AccountingSchema as, Doc doc, BigDecimal Qty, int C_InvoiceLine_ID, BigDecimal multiplier) {
        Fact fact = new Fact(doc, as, Fact.POST_Commitment);
        DocLine[] commitments = Doc_Order.getCommitments(doc, Qty, C_InvoiceLine_ID);

        BigDecimal total = Env.ZERO;
        @SuppressWarnings("unused")
        FactLine fl = null;
        int C_Currency_ID = -1;
        for (DocLine line : commitments) {
            if (C_Currency_ID == -1) C_Currency_ID = line.getCurrencyId();
            else if (C_Currency_ID != line.getCurrencyId()) {
                doc.p_Error = "Different Currencies of Order Lines";
                s_log.log(Level.SEVERE, doc.p_Error);
                return null;
            }
            BigDecimal cost = line.getAmtSource().multiply(multiplier);
            total = total.add(cost);

            //	Account
            I_C_ValidCombination expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
            fact.createLine(line, expense, C_Currency_ID, null, cost);
        }
        //	Offset
        MAccount offset = doc.getAccount(ACCTTYPE_CommitmentOffset, as);
        if (offset == null) {
            doc.p_Error = "@NotFound@ @CommitmentOffset_Acct@";
            s_log.log(Level.SEVERE, doc.p_Error);
            return null;
        }
        fact.createLine(null, offset, C_Currency_ID, total, null);
        return fact;
    } //	getCommitmentRelease

    /**
     * Get Commitments Sales
     *
     * @param doc    document
     * @param maxQty Qty invoiced/matched
     * @return commitments (order lines)
     */
    protected static DocLine[] getCommitmentsSales(Doc doc, BigDecimal maxQty, int M_InOutLine_ID) {
        return BaseDoc_OrderKt.getCommitmentsSales(doc, maxQty, M_InOutLine_ID);
    } //	getCommitmentsSales

    /**
     * Get Commitment Sales Release. Called from InOut
     *
     * @param as         accounting schema
     * @param doc        doc
     * @param Qty        qty invoiced/matched
     * @param multiplier 1 for accrual
     * @return Fact
     */
    protected static Fact getCommitmentSalesRelease(
            AccountingSchema as, Doc doc, BigDecimal Qty, int M_InOutLine_ID, BigDecimal multiplier) {
        Fact fact = new Fact(doc, as, Fact.POST_Commitment);
        DocLine[] commitments = Doc_Order.getCommitmentsSales(doc, Qty, M_InOutLine_ID);

        BigDecimal total = Env.ZERO;
        @SuppressWarnings("unused")
        FactLine fl = null;
        int C_Currency_ID = -1;
        for (DocLine line : commitments) {
            if (C_Currency_ID == -1) C_Currency_ID = line.getCurrencyId();
            else if (C_Currency_ID != line.getCurrencyId()) {
                doc.p_Error = "Different Currencies of Order Lines";
                s_log.log(Level.SEVERE, doc.p_Error);
                return null;
            }
            BigDecimal cost = line.getAmtSource().multiply(multiplier);
            total = total.add(cost);

            //	Account
            I_C_ValidCombination revenue = line.getAccount(ProductCost.ACCTTYPE_P_Revenue, as);
            fact.createLine(line, revenue, C_Currency_ID, cost, null);
        }
        //	Offset
        MAccount offset = doc.getAccount(ACCTTYPE_CommitmentOffsetSales, as);
        if (offset == null) {
            doc.p_Error = "@NotFound@ @CommitmentOffsetSales_Acct@";
            s_log.log(Level.SEVERE, doc.p_Error);
            return null;
        }
        fact.createLine(null, offset, C_Currency_ID, null, total);
        return fact;
    } //	getCommitmentSalesRelease

    @Override
    protected IPODoc createNewInstance(Row rs) {
        return new MOrder(rs);
    }

    /**
     * Load Specific Document Details
     *
     * @return error message or null
     */
    protected String loadDocumentDetails() {
        MOrder order = (MOrder) getPO();
        setDateDoc(order.getDateOrdered());
        setIsTaxIncluded(order.isTaxIncluded());
        //	Amounts
        setAmount(AMTTYPE_Gross, order.getGrandTotal());
        setAmount(AMTTYPE_Net, order.getTotalLines());
        setAmount(AMTTYPE_Charge, order.getChargeAmt());

        //	Contained Objects
        m_taxes = loadTaxes();
        p_lines = loadLines(order);
        //	log.fine( "Lines=" + p_lines.length + ", Taxes=" + m_taxes.length);
        return null;
    } //  loadDocumentDetails

    /**
     * Load Invoice Line
     *
     * @param order order
     * @return DocLine Array
     */
    private DocLine[] loadLines(MOrder order) {
        ArrayList<DocLine> list = new ArrayList<>();
        I_C_OrderLine[] lines = order.getLines().toArray(new I_C_OrderLine[0]);
        for (I_C_OrderLine line : lines) {
            DocLine docLine = new DocLine(line, this);
            BigDecimal Qty = line.getQtyOrdered();
            docLine.setQty(Qty, order.isSOTrx());
            //
            BigDecimal PriceCost = null;
            if (getDocumentType().equals(DOCTYPE_POrder)) // 	PO
                PriceCost = line.getPriceCost();
            BigDecimal LineNetAmt;
            if (PriceCost != null && PriceCost.signum() != 0) LineNetAmt = Qty.multiply(PriceCost);
            else LineNetAmt = line.getLineNetAmt();
            docLine.setAmount(LineNetAmt); // 	DR
            BigDecimal PriceList = line.getPriceList();
            int C_Tax_ID = docLine.getTaxId();
            //	Correct included Tax
            if (isTaxIncluded() && C_Tax_ID != 0) {
                MTax tax = MTax.get(C_Tax_ID);
                if (!tax.isZeroTax()) {
                    BigDecimal LineNetAmtTax = tax.calculateTax(LineNetAmt, true, getStdPrecision());
                    if (log.isLoggable(Level.FINE))
                        log.fine("LineNetAmt=" + LineNetAmt + " - Tax=" + LineNetAmtTax);
                    LineNetAmt = LineNetAmt.subtract(LineNetAmtTax);
                    for (DocTax m_tax : m_taxes) {
                        if (m_tax.getTaxId() == C_Tax_ID) {
                            m_tax.addIncludedTax(LineNetAmtTax);
                            break;
                        }
                    }
                    BigDecimal PriceListTax = tax.calculateTax(PriceList, true, getStdPrecision());
                    PriceList = PriceList.subtract(PriceListTax);
                }
            } //	correct included Tax

            docLine.setAmount(LineNetAmt, PriceList, Qty);
            list.add(docLine);
        }

        //	Return Array
        DocLine[] dl = new DocLine[list.size()];
        list.toArray(dl);
        return dl;
    } //	loadLines

    /**
     * Load Requisitions
     *
     * @return requisition lines of Order
     */
    private DocLine[] loadRequisitions() {
        return BaseDoc_OrderKt.loadRequisitions((MOrder) getPO(), this);
    } // loadRequisitions

    /**
     * Get Currency Precision
     *
     * @return precision
     */
    private int getStdPrecision() {
        if (m_precision == -1) m_precision = MCurrencyKt.getCurrencyStdPrecision(getCurrencyId());
        return m_precision;
    } //	getPrecision

    /**
     * Load Invoice Taxes
     *
     * @return DocTax Array
     */
    private DocTax[] loadTaxes() {
        ArrayList<DocTax> list = new ArrayList<>();
        StringBuilder sql =
                new StringBuilder(
                        "SELECT it.C_Tax_ID, t.Name, t.Rate, it.TaxBaseAmt, it.TaxAmt, t.IsSalesTax ")
                        .append("FROM C_Tax t, C_OrderTax it ")
                        .append("WHERE t.C_Tax_ID=it.C_Tax_ID AND it.C_Order_ID=?");
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql.toString());
            pstmt.setInt(1, getId());
            rs = pstmt.executeQuery();
            //
            while (rs.next()) {
                int C_Tax_ID = rs.getInt(1);
                String name = rs.getString(2);
                BigDecimal rate = rs.getBigDecimal(3);
                BigDecimal taxBaseAmt = rs.getBigDecimal(4);
                BigDecimal amount = rs.getBigDecimal(5);
                boolean salesTax = "Y".equals(rs.getString(6));
                //
                DocTax taxLine = new DocTax(C_Tax_ID, name, rate, taxBaseAmt, amount, salesTax);
                list.add(taxLine);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql.toString(), e);
        }

        //	Return Array
        DocTax[] tl = new DocTax[list.size()];
        list.toArray(tl);
        return tl;
    } //	loadTaxes

    /**
     * ************************************************************************ Get Source Currency
     * Balance - subtracts line and tax amounts from total - no rounding
     *
     * @return positive amount, if total invoice is bigger than lines
     */
    public BigDecimal getBalance() {
        BigDecimal retValue = Env.ZERO;
        StringBuilder sb = new StringBuilder(" [");
        //  Total
        retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
        sb.append(getAmount(Doc.AMTTYPE_Gross));
        //  - Header Charge
        retValue = retValue.subtract(getAmount(Doc.AMTTYPE_Charge));
        sb.append("-").append(getAmount(Doc.AMTTYPE_Charge));
        //  - Tax
        if (m_taxes != null) {
            for (DocTax m_tax : m_taxes) {
                retValue = retValue.subtract(m_tax.getAmount());
                sb.append("-").append(m_tax.getAmount());
            }
        }
        //  - Lines
        if (p_lines != null) {
            for (DocLine p_line : p_lines) {
                retValue = retValue.subtract(p_line.getAmtSource());
                sb.append("-").append(p_line.getAmtSource());
            }
            sb.append("]");
        }
        //
        if (retValue.signum() != 0 // 	Sum of Cost(vs. Price) in lines may not add up
                && getDocumentType().equals(DOCTYPE_POrder)) // 	PO
        {
            if (log.isLoggable(Level.FINE))
                log.fine(toString() + " Balance=" + retValue + sb.toString() + " (ignored)");
            retValue = Env.ZERO;
        } else if (log.isLoggable(Level.FINE))
            log.fine(toString() + " Balance=" + retValue + sb.toString());
        return retValue;
    } //  getBalance

    /**
     * *********************************************************************** Create Facts (the
     * accounting logic) for SOO, POO.
     *
     * <pre>
     *  Reservation (release)
     * 		Expense			DR
     * 		Offset					CR
     *  Commitment
     *  (to be released by Invoice Matching)
     * 		Expense					CR
     * 		Offset			DR
     *  </pre>
     *
     * @param as accounting schema
     * @return Fact
     */
    public ArrayList<IFact> createFacts(AccountingSchema as) {
        ArrayList<IFact> facts = new ArrayList<>();
        //  Purchase Order
        if (getDocumentType().equals(DOCTYPE_POrder)) {
            updateProductPO(as);

            // BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

            //  Commitment
            @SuppressWarnings("unused")
            FactLine fl = null;
            if (as.isCreatePOCommitment()) {
                Fact fact = new Fact(this, as, Fact.POST_Commitment);
                BigDecimal total = Env.ZERO;
                for (DocLine line : p_lines) {
                    BigDecimal cost = line.getAmtSource();
                    total = total.add(cost);

                    //	Account
                    I_C_ValidCombination expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
                    fact.createLine(line, expense, getCurrencyId(), cost, null);
                }
                //	Offset
                MAccount offset = getAccount(ACCTTYPE_CommitmentOffset, as);
                if (offset == null) {
                    p_Error = "@NotFound@ @CommitmentOffset_Acct@";
                    log.log(Level.SEVERE, p_Error);
                    return null;
                }
                fact.createLine(null, offset, getCurrencyId(), null, total);
                //
                facts.add(fact);
            }

            //  Reverse Reservation
            if (as.isCreateReservation()) {
                Fact fact = new Fact(this, as, Fact.POST_Reservation);
                BigDecimal total = Env.ZERO;
                if (m_requisitions == null) m_requisitions = loadRequisitions();
                for (DocLine line : m_requisitions) {
                    BigDecimal cost = line.getAmtSource();
                    total = total.add(cost);

                    //	Account
                    I_C_ValidCombination expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
                    fact.createLine(line, expense, getCurrencyId(), null, cost);
                }
                //	Offset
                if (m_requisitions.length > 0) {
                    MAccount offset = getAccount(ACCTTYPE_CommitmentOffset, as);
                    if (offset == null) {
                        p_Error = "@NotFound@ @CommitmentOffset_Acct@";
                        log.log(Level.SEVERE, p_Error);
                        return null;
                    }
                    fact.createLine(null, offset, getCurrencyId(), total, null);
                }
                //
                facts.add(fact);
            } //	reservations
        }
        //	SO
        else if (getDocumentType().equals(DOCTYPE_SOrder)) {
            //  Commitment
            @SuppressWarnings("unused")
            FactLine fl = null;
            if (as.isCreateSOCommitment()) {
                Fact fact = new Fact(this, as, Fact.POST_Commitment);
                BigDecimal total = Env.ZERO;
                for (DocLine line : p_lines) {
                    BigDecimal cost = line.getAmtSource();
                    total = total.add(cost);

                    //	Account
                    I_C_ValidCombination revenue = line.getAccount(ProductCost.ACCTTYPE_P_Revenue, as);
                    fact.createLine(line, revenue, getCurrencyId(), null, cost);
                }
                //	Offset
                MAccount offset = getAccount(ACCTTYPE_CommitmentOffsetSales, as);
                if (offset == null) {
                    p_Error = "@NotFound@ @CommitmentOffsetSales_Acct@";
                    log.log(Level.SEVERE, p_Error);
                    return null;
                }
                fact.createLine(null, offset, getCurrencyId(), total, null);
                //
                facts.add(fact);
            }
        }
        return facts;
    } //  createFact

    /**
     * Update ProductPO PriceLastPO
     *
     * @param as accounting schema
     */
    private void updateProductPO(AccountingSchema as) {
        MClientInfo ci = MClientInfo.get(as.getClientId());
        if (ci.getAcctSchema1Id() != as.getAccountingSchemaId()) return;

        String sql = "UPDATE M_Product_PO po " +
                "SET PriceLastPO = (SELECT currencyConvert(ol.PriceActual,ol.C_Currency_ID,po.C_Currency_ID,o.DateOrdered,o.C_ConversionType_ID,o.AD_Client_ID,o.orgId) " +
                "FROM C_Order o, C_OrderLine ol " +
                "WHERE o.C_Order_ID=ol.C_Order_ID" +
                " AND po.M_Product_ID=ol.M_Product_ID AND po.C_BPartner_ID=o.C_BPartner_ID " +
                // jz + " AND ROWNUM=1 AND o.C_Order_ID=").append(getId()).append(") ")
                " AND ol.C_OrderLine_ID = (SELECT MIN(ol1.C_OrderLine_ID) " +
                "FROM C_Order o1, C_OrderLine ol1 " +
                "WHERE o1.C_Order_ID=ol1.C_Order_ID" +
                " AND po.M_Product_ID=ol1.M_Product_ID AND po.C_BPartner_ID=o1.C_BPartner_ID" +
                "  AND o1.C_Order_ID=" +
                getId() +
                ") " +
                "  AND o.C_Order_ID=" +
                getId() +
                ") " +
                "WHERE EXISTS (SELECT * " +
                "FROM C_Order o, C_OrderLine ol " +
                "WHERE o.C_Order_ID=ol.C_Order_ID" +
                " AND po.M_Product_ID=ol.M_Product_ID AND po.C_BPartner_ID=o.C_BPartner_ID" +
                " AND o.C_Order_ID=" +
                getId() +
                ")";
        int no = executeUpdate(sql);
        if (log.isLoggable(Level.FINE)) log.fine("Updated=" + no);
    } //	updateProductPO
} //  Doc_Order
