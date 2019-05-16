package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.conversionrate.MConversionRate;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MInvoiceLine;
import org.compiere.invoicing.MLandedCostAllocation;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_OrderLandedCostAllocation;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_M_InOutLine;
import org.compiere.tax.MTax;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValueBD;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Post Invoice Documents.
 *
 * <pre>
 *  Table:              C_Invoice (318)
 *  Document Types:     ARI, ARC, ARF, API, APC
 *  </pre>
 *
 * @author Jorg Janke
 * @author Armen Rizal, Goodwill Consulting
 * <li>BF: 2797257 Landed Cost Detail is not using allocation qty
 * @version $Id: Doc_Invoice.java,v 1.2 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_Invoice extends Doc {
    /**
     * Contained Optional Tax Lines
     */
    protected DocTax[] m_taxes = null;
    /**
     * Currency Precision
     */
    protected int m_precision = -1;
    /**
     * All lines are Service
     */
    protected boolean m_allLinesService = true;
    /**
     * All lines are product item
     */
    protected boolean m_allLinesItem = true;

    /**
     * Constructor
     *
     * @param as accounting schemata
     * @param rs record
     */
    public Doc_Invoice(MAcctSchema as, Row rs) {
        super(as, MInvoice.class, rs, null);
    } //	Doc_Invoice

    @Override
    protected IPODoc createNewInstance(Row rs) {
        return new MInvoice(rs, -1);
    }

    /**
     * Load Specific Document Details
     *
     * @return error message or null
     */
    protected String loadDocumentDetails() {
        MInvoice invoice = (MInvoice) getPO();
        setDateDoc(invoice.getDateInvoiced());
        setIsTaxIncluded(invoice.isTaxIncluded());
        //	Amounts
        setAmount(Doc.AMTTYPE_Gross, invoice.getGrandTotal());
        setAmount(Doc.AMTTYPE_Net, invoice.getTotalLines());
        setAmount(Doc.AMTTYPE_Charge, invoice.getChargeAmt());

        //	Contained Objects
        m_taxes = loadTaxes();
        p_lines = loadLines(invoice);
        if (log.isLoggable(Level.FINE))
            log.fine("Lines=" + p_lines.length + ", Taxes=" + m_taxes.length);
        return null;
    } //  loadDocumentDetails

    /**
     * Load Invoice Taxes
     *
     * @return DocTax Array
     */
    private DocTax[] loadTaxes() {
        ArrayList<DocTax> list = new ArrayList<DocTax>();
        String sql =
                "SELECT it.C_Tax_ID, t.Name, t.Rate, it.TaxBaseAmt, it.TaxAmt, t.IsSalesTax "
                        + "FROM C_Tax t, C_InvoiceTax it "
                        + "WHERE t.C_Tax_ID=it.C_Tax_ID AND it.C_Invoice_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
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
                if (log.isLoggable(Level.FINE)) log.fine(taxLine.toString());
                list.add(taxLine);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
            return null;
        } finally {

            rs = null;
            pstmt = null;
        }

        //	Return Array
        DocTax[] tl = new DocTax[list.size()];
        list.toArray(tl);
        return tl;
    } //	loadTaxes

    /**
     * Load Invoice Line
     *
     * @param invoice invoice
     * @return DocLine Array
     */
    private DocLine[] loadLines(MInvoice invoice) {
        ArrayList<DocLine> list = new ArrayList<DocLine>();
        //
        I_C_InvoiceLine[] lines = invoice.getLines(false);
        for (I_C_InvoiceLine line : lines) {
            if (line.isDescription()) continue;
            DocLine docLine = new DocLine(line, this);
            //	Qty
            BigDecimal Qty = line.getQtyInvoiced();
            boolean cm =
                    getDocumentType().equals(DOCTYPE_ARCredit) || getDocumentType().equals(DOCTYPE_APCredit);
            docLine.setQty(cm ? Qty.negate() : Qty, invoice.isSOTrx());
            //
            BigDecimal LineNetAmt = line.getLineNetAmt();
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

                    if (tax.isSummary()) {
                        BigDecimal sumChildLineNetAmtTax = Env.ZERO;
                        DocTax taxToApplyDiff = null;
                        for (MTax childTax : tax.getChildTaxes(false)) {
                            if (!childTax.isZeroTax()) {
                                BigDecimal childLineNetAmtTax =
                                        childTax.calculateTax(LineNetAmt, false, getStdPrecision());
                                if (log.isLoggable(Level.FINE))
                                    log.fine("LineNetAmt=" + LineNetAmt + " - Child Tax=" + childLineNetAmtTax);
                                for (int t = 0; t < m_taxes.length; t++) {
                                    if (m_taxes[t].getTaxId() == childTax.getTaxId()) {
                                        m_taxes[t].addIncludedTax(childLineNetAmtTax);
                                        taxToApplyDiff = m_taxes[t];
                                        sumChildLineNetAmtTax = sumChildLineNetAmtTax.add(childLineNetAmtTax);
                                        break;
                                    }
                                }
                            }
                        }
                        BigDecimal diffChildVsSummary = LineNetAmtTax.subtract(sumChildLineNetAmtTax);
                        if (diffChildVsSummary.signum() != 0 && taxToApplyDiff != null) {
                            taxToApplyDiff.addIncludedTax(diffChildVsSummary);
                        }
                    } else {
                        for (int t = 0; t < m_taxes.length; t++) {
                            if (m_taxes[t].getTaxId() == C_Tax_ID) {
                                m_taxes[t].addIncludedTax(LineNetAmtTax);
                                break;
                            }
                        }
                    }

                    BigDecimal PriceListTax = tax.calculateTax(PriceList, true, getStdPrecision());
                    PriceList = PriceList.subtract(PriceListTax);
                }
            } //	correct included Tax

            docLine.setAmount(LineNetAmt, PriceList, Qty); // 	qty for discount calc
            if (docLine.isItem()) m_allLinesService = false;
            else m_allLinesItem = false;
            //
            if (log.isLoggable(Level.FINE)) log.fine(docLine.toString());
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        //	Included Tax - make sure that no difference
        if (isTaxIncluded()) {
            for (int i = 0; i < m_taxes.length; i++) {
                if (m_taxes[i].isIncludedTaxDifference()) {
                    BigDecimal diff = m_taxes[i].getIncludedTaxDifference();
                    for (int j = 0; j < dls.length; j++) {
                        MTax lineTax = MTax.get(dls[j].getTaxId());
                        MTax[] composingTaxes = null;
                        if (lineTax.isSummary()) {
                            composingTaxes = lineTax.getChildTaxes(false);
                        } else {
                            composingTaxes = new MTax[1];
                            composingTaxes[0] = lineTax;
                        }
                        for (MTax mTax : composingTaxes) {
                            if (mTax.getTaxId() == m_taxes[i].getTaxId()) {
                                dls[j].setLineNetAmtDifference(diff);
                                m_taxes[i].addIncludedTax(diff.negate());
                                diff = Env.ZERO;
                                break;
                            }
                        }
                        if (diff.signum() == 0) {
                            break;
                        }
                    } //	for all lines
                } //	tax difference
            } //	for all taxes
        } //	Included Tax difference

        //	Return Array
        return dls;
    } //	loadLines

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
        for (DocTax m_tax : m_taxes) {
            retValue = retValue.subtract(m_tax.getAmount());
            sb.append("-").append(m_tax.getAmount());
        }
        //  - Lines
        for (DocLine p_line : p_lines) {
            retValue = retValue.subtract(p_line.getAmtSource());
            sb.append("-").append(p_line.getAmtSource());
        }
        sb.append("]");
        //
        if (log.isLoggable(Level.FINE)) log.fine(toString() + " Balance=" + retValue + sb.toString());
        return retValue;
    } //  getBalance

    /**
     * Create Facts (the accounting logic) for ARI, ARC, ARF, API, APC.
     *
     * <pre>
     *  ARI, ARF
     *      Receivables     DR
     *      Charge                  CR
     *      TaxDue                  CR
     *      Revenue                 CR
     *
     *  ARC
     *      Receivables             CR
     *      Charge          DR
     *      TaxDue          DR
     *      Revenue         RR
     *
     *  API
     *      Payables                CR
     *      Charge          DR
     *      TaxCredit       DR
     *      Expense         DR
     *
     *  APC
     *      Payables        DR
     *      Charge                  CR
     *      TaxCredit               CR
     *      Expense                 CR
     *  </pre>
     *
     * @param as accounting schema
     * @return Fact
     */
    public ArrayList<IFact> createFacts(AccountingSchema as) {
        //
        ArrayList<IFact> facts = new ArrayList<>();
        //  create Fact Header
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        //  Cash based accounting
        if (!as.isAccrual()) return facts;

        //  ** ARI, ARF
        switch (getDocumentType()) {
            case DOCTYPE_ARInvoice:
            case DOCTYPE_ARProForma: {
                BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
                BigDecimal serviceAmt = Env.ZERO;

                //  Header Charge           CR
                BigDecimal amt = getAmount(Doc.AMTTYPE_Charge);
                if (amt != null && amt.signum() != 0)
                    fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as), getCurrencyId(), null, amt);
                //  TaxDue                  CR
                for (DocTax m_tax : m_taxes) {
                    amt = m_tax.getAmount();
                    if (amt != null && amt.signum() != 0) {
                        FactLine tl =
                                fact.createLine(
                                        null,
                                        m_tax.getAccount(DocTax.ACCTTYPE_TaxDue, as),
                                        getCurrencyId(),
                                        null,
                                        amt);
                        if (tl != null) tl.setTaxId(m_tax.getTaxId());
                    }
                }
                //  Revenue                 CR
                for (DocLine p_line : p_lines) {
                    amt = p_line.getAmtSource();
                    BigDecimal dAmt = null;
                    if (as.isTradeDiscountPosted()) {
                        BigDecimal discount = p_line.getDiscount();
                        if (discount != null && discount.signum() != 0) {
                            amt = amt.add(discount);
                            dAmt = discount;
                            fact.createLine(
                                    p_line,
                                    p_line.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as),
                                    getCurrencyId(),
                                    dAmt,
                                    null);
                        }
                    }
                    fact.createLine(
                            p_line,
                            p_line.getAccount(ProductCost.ACCTTYPE_P_Revenue, as),
                            getCurrencyId(),
                            null,
                            amt);
                    if (!p_line.isItem()) {
                        grossAmt = grossAmt.subtract(amt);
                        serviceAmt = serviceAmt.add(amt);
                    }
                }
                //  Set Locations
                FactLine[] fLines = fact.getLines();
                for (FactLine fLine : fLines) {
                    if (fLine != null) {
                        fLine.setLocationFromOrg(fLine.getOrgId(), true); //  from Loc
                        fLine.setLocationFromBPartner(getBusinessPartnerLocationId(), false); //  to Loc
                    }
                }

                //  Receivables     DR
                int receivables_ID = getValidCombinationId(Doc.ACCTTYPE_C_Receivable, as);
                int receivablesServices_ID = getValidCombinationId(Doc.ACCTTYPE_C_Receivable_Services, as);
                if (m_allLinesItem || !as.isPostServices() || receivables_ID == receivablesServices_ID) {
                    grossAmt = getAmount(Doc.AMTTYPE_Gross);
                    serviceAmt = Env.ZERO;
                } else if (m_allLinesService) {
                    serviceAmt = getAmount(Doc.AMTTYPE_Gross);
                    grossAmt = Env.ZERO;
                }
                if (grossAmt.signum() != 0)
                    fact.createLine(
                            null, MAccount.get(receivables_ID), getCurrencyId(), grossAmt, null);
                if (serviceAmt.signum() != 0)
                    fact.createLine(
                            null,
                            MAccount.get(receivablesServices_ID),
                            getCurrencyId(),
                            serviceAmt,
                            null);
                break;
            }
            //  ARC
            case DOCTYPE_ARCredit: {
                BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
                BigDecimal serviceAmt = Env.ZERO;

                //  Header Charge   DR
                BigDecimal amt = getAmount(Doc.AMTTYPE_Charge);
                if (amt != null && amt.signum() != 0)
                    fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as), getCurrencyId(), amt, null);
                //  TaxDue          DR
                for (DocTax m_tax : m_taxes) {
                    amt = m_tax.getAmount();
                    if (amt != null && amt.signum() != 0) {
                        FactLine tl =
                                fact.createLine(
                                        null,
                                        m_tax.getAccount(DocTax.ACCTTYPE_TaxDue, as),
                                        getCurrencyId(),
                                        amt,
                                        null);
                        if (tl != null) tl.setTaxId(m_tax.getTaxId());
                    }
                }
                //  Revenue         CR
                for (DocLine p_line : p_lines) {
                    amt = p_line.getAmtSource();
                    BigDecimal dAmt = null;
                    if (as.isTradeDiscountPosted()) {
                        BigDecimal discount = p_line.getDiscount();
                        if (discount != null && discount.signum() != 0) {
                            amt = amt.add(discount);
                            dAmt = discount;
                            fact.createLine(
                                    p_line,
                                    p_line.getAccount(ProductCost.ACCTTYPE_P_TDiscountGrant, as),
                                    getCurrencyId(),
                                    null,
                                    dAmt);
                        }
                    }
                    fact.createLine(
                            p_line,
                            p_line.getAccount(ProductCost.ACCTTYPE_P_Revenue, as),
                            getCurrencyId(),
                            amt,
                            null);
                    if (!p_line.isItem()) {
                        grossAmt = grossAmt.subtract(amt);
                        serviceAmt = serviceAmt.add(amt);
                    }
                }
                //  Set Locations
                FactLine[] fLines = fact.getLines();
                for (FactLine fLine : fLines) {
                    if (fLine != null) {
                        fLine.setLocationFromOrg(fLine.getOrgId(), true); //  from Loc
                        fLine.setLocationFromBPartner(getBusinessPartnerLocationId(), false); //  to Loc
                    }
                }
                //  Receivables             CR
                int receivables_ID = getValidCombinationId(Doc.ACCTTYPE_C_Receivable, as);
                int receivablesServices_ID = getValidCombinationId(Doc.ACCTTYPE_C_Receivable_Services, as);
                if (m_allLinesItem || !as.isPostServices() || receivables_ID == receivablesServices_ID) {
                    grossAmt = getAmount(Doc.AMTTYPE_Gross);
                    serviceAmt = Env.ZERO;
                } else if (m_allLinesService) {
                    serviceAmt = getAmount(Doc.AMTTYPE_Gross);
                    grossAmt = Env.ZERO;
                }
                if (grossAmt.signum() != 0)
                    fact.createLine(
                            null, MAccount.get(receivables_ID), getCurrencyId(), null, grossAmt);
                if (serviceAmt.signum() != 0)
                    fact.createLine(
                            null,
                            MAccount.get(receivablesServices_ID),
                            getCurrencyId(),
                            null,
                            serviceAmt);
                break;
            }

            //  ** API
            case DOCTYPE_APInvoice: {
                BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
                BigDecimal serviceAmt = Env.ZERO;

                //  Charge          DR
                fact.createLine(
                        null,
                        getAccount(Doc.ACCTTYPE_Charge, as),
                        getCurrencyId(),
                        getAmount(Doc.AMTTYPE_Charge),
                        null);
                //  TaxCredit       DR
                for (DocTax m_tax : m_taxes) {
                    FactLine tl =
                            fact.createLine(
                                    null,
                                    m_tax.getAccount(m_tax.getAPTaxType(), as),
                                    getCurrencyId(),
                                    m_tax.getAmount(),
                                    null);
                    if (tl != null) tl.setTaxId(m_tax.getTaxId());
                }
                //  Expense         DR
                for (DocLine line : p_lines) {
                    boolean landedCost = landedCost(as, fact, line, true);
                    if (landedCost && as.isExplicitCostAdjustment()) {
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
                                getCurrencyId(),
                                line.getAmtSource(),
                                null);
                        //
                        FactLine fl =
                                fact.createLine(
                                        line,
                                        line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
                                        getCurrencyId(),
                                        null,
                                        line.getAmtSource());
                        String desc = line.getDescription();
                        if (desc == null) desc = "100%";
                        else desc += " 100%";
                        fl.setDescription(desc);
                    }
                    if (!landedCost) {
                        I_C_ValidCombination expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
                        if (line.isItem())
                            expense = line.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);
                        BigDecimal amt = line.getAmtSource();
                        BigDecimal dAmt;
                        if (as.isTradeDiscountPosted() && !line.isItem()) {
                            BigDecimal discount = line.getDiscount();
                            if (discount != null && discount.signum() != 0) {
                                amt = amt.add(discount);
                                dAmt = discount;
                                I_C_ValidCombination tradeDiscountReceived =
                                        line.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
                                fact.createLine(line, tradeDiscountReceived, getCurrencyId(), null, dAmt);
                            }
                        }
                        fact.createLine(line, expense, getCurrencyId(), amt, null);
                        if (!line.isItem()) {
                            grossAmt = grossAmt.subtract(amt);
                            serviceAmt = serviceAmt.add(amt);
                        }
                        //
                        if (line.getProductId() != 0
                                && line.getProduct().isService()) // 	otherwise Inv Matching
                            MCostDetail.createInvoice(
                                    as,
                                    line.getOrgId(),
                                    line.getProductId(),
                                    line.getAttributeSetInstanceId(),
                                    line.getId(),
                                    0, //	No Cost Element
                                    line.getAmtSource(),
                                    line.getQty(),
                                    line.getDescription());
                    }
                }
                //  Set Locations
                FactLine[] fLines = fact.getLines();
                for (FactLine fLine : fLines) {
                    if (fLine != null) {
                        fLine.setLocationFromBPartner(getBusinessPartnerLocationId(), true); //  from Loc
                        fLine.setLocationFromOrg(fLine.getOrgId(), false); //  to Loc
                    }
                }

                //  Liability               CR
                int payables_ID = getValidCombinationId(Doc.ACCTTYPE_V_Liability, as);
                int payablesServices_ID = getValidCombinationId(Doc.ACCTTYPE_V_Liability_Services, as);
                if (m_allLinesItem || !as.isPostServices() || payables_ID == payablesServices_ID) {
                    grossAmt = getAmount(Doc.AMTTYPE_Gross);
                    serviceAmt = Env.ZERO;
                } else if (m_allLinesService) {
                    serviceAmt = getAmount(Doc.AMTTYPE_Gross);
                    grossAmt = Env.ZERO;
                }
                if (grossAmt.signum() != 0)
                    fact.createLine(
                            null, MAccount.get(payables_ID), getCurrencyId(), null, grossAmt);
                if (serviceAmt.signum() != 0)
                    fact.createLine(
                            null,
                            MAccount.get(payablesServices_ID),
                            getCurrencyId(),
                            null,
                            serviceAmt);
                //
                updateProductPO(as); // 	Only API

                break;
            }
            //  APC
            case DOCTYPE_APCredit: {
                BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);
                BigDecimal serviceAmt = Env.ZERO;
                //  Charge                  CR
                fact.createLine(
                        null,
                        getAccount(Doc.ACCTTYPE_Charge, as),
                        getCurrencyId(),
                        null,
                        getAmount(Doc.AMTTYPE_Charge));
                //  TaxCredit               CR
                for (DocTax m_tax : m_taxes) {
                    FactLine tl =
                            fact.createLine(
                                    null,
                                    m_tax.getAccount(m_tax.getAPTaxType(), as),
                                    getCurrencyId(),
                                    null,
                                    m_tax.getAmount());
                    if (tl != null) tl.setTaxId(m_tax.getTaxId());
                }
                //  Expense                 CR
                for (DocLine line : p_lines) {
                    boolean landedCost = landedCost(as, fact, line, false);
                    if (landedCost && as.isExplicitCostAdjustment()) {
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
                                getCurrencyId(),
                                null,
                                line.getAmtSource());
                        //
                        FactLine fl =
                                fact.createLine(
                                        line,
                                        line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
                                        getCurrencyId(),
                                        line.getAmtSource(),
                                        null);
                        String desc = line.getDescription();
                        if (desc == null) desc = "100%";
                        else desc += " 100%";
                        fl.setDescription(desc);
                    }
                    if (!landedCost) {
                        I_C_ValidCombination expense = line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
                        if (line.isItem())
                            expense = line.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);
                        BigDecimal amt = line.getAmtSource();
                        BigDecimal dAmt;
                        if (as.isTradeDiscountPosted() && !line.isItem()) {
                            BigDecimal discount = line.getDiscount();
                            if (discount != null && discount.signum() != 0) {
                                amt = amt.add(discount);
                                dAmt = discount;
                                I_C_ValidCombination tradeDiscountReceived =
                                        line.getAccount(ProductCost.ACCTTYPE_P_TDiscountRec, as);
                                fact.createLine(line, tradeDiscountReceived, getCurrencyId(), dAmt, null);
                            }
                        }
                        fact.createLine(line, expense, getCurrencyId(), null, amt);
                        if (!line.isItem()) {
                            grossAmt = grossAmt.subtract(amt);
                            serviceAmt = serviceAmt.add(amt);
                        }
                        //
                        if (line.getProductId() != 0
                                && line.getProduct().isService()) // 	otherwise Inv Matching
                            MCostDetail.createInvoice(
                                    as,
                                    line.getOrgId(),
                                    line.getProductId(),
                                    line.getAttributeSetInstanceId(),
                                    line.getId(),
                                    0, //	No Cost Element
                                    line.getAmtSource().negate(),
                                    line.getQty(),
                                    line.getDescription());
                    }
                }
                //  Set Locations
                FactLine[] fLines = fact.getLines();
                for (FactLine fLine : fLines) {
                    if (fLine != null) {
                        fLine.setLocationFromBPartner(getBusinessPartnerLocationId(), true); //  from Loc
                        fLine.setLocationFromOrg(fLine.getOrgId(), false); //  to Loc
                    }
                }
                //  Liability       DR
                int payables_ID = getValidCombinationId(Doc.ACCTTYPE_V_Liability, as);
                int payablesServices_ID = getValidCombinationId(Doc.ACCTTYPE_V_Liability_Services, as);
                if (m_allLinesItem || !as.isPostServices() || payables_ID == payablesServices_ID) {
                    grossAmt = getAmount(Doc.AMTTYPE_Gross);
                    serviceAmt = Env.ZERO;
                } else if (m_allLinesService) {
                    serviceAmt = getAmount(Doc.AMTTYPE_Gross);
                    grossAmt = Env.ZERO;
                }
                if (grossAmt.signum() != 0)
                    fact.createLine(
                            null, MAccount.get(payables_ID), getCurrencyId(), grossAmt, null);
                if (serviceAmt.signum() != 0)
                    fact.createLine(
                            null,
                            MAccount.get(payablesServices_ID),
                            getCurrencyId(),
                            serviceAmt,
                            null);
                break;
            }
            default:
                p_Error = "DocumentType unknown: " + getDocumentType();
                log.log(Level.SEVERE, p_Error);
                fact = null;
                break;
        }
        //
        facts.add(fact);
        return facts;
    } //  createFact

    /**
     * Create Fact Cash Based (i.e. only revenue/expense)
     *
     * @param as         accounting schema
     * @param fact       fact to add lines to
     * @param multiplier source amount multiplier
     * @return accounted amount
     */
    public BigDecimal createFactCash(AccountingSchema as, Fact fact, BigDecimal multiplier) {
        boolean creditMemo =
                getDocumentType().equals(DOCTYPE_ARCredit) || getDocumentType().equals(DOCTYPE_APCredit);
        boolean payables =
                getDocumentType().equals(DOCTYPE_APInvoice) || getDocumentType().equals(DOCTYPE_APCredit);
        BigDecimal acctAmt = Env.ZERO;
        FactLine fl;
        //	Revenue/Cost
        for (DocLine line : p_lines) {
            boolean landedCost = false;
            if (payables) landedCost = landedCost(as, fact, line, false);
            if (landedCost && as.isExplicitCostAdjustment()) {
                fact.createLine(
                        line,
                        line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
                        getCurrencyId(),
                        null,
                        line.getAmtSource());
                //
                fl =
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Expense, as),
                                getCurrencyId(),
                                line.getAmtSource(),
                                null);
                String desc = line.getDescription();
                if (desc == null) desc = "100%";
                else desc += " 100%";
                fl.setDescription(desc);
            }
            if (!landedCost) {
                I_C_ValidCombination acct =
                        line.getAccount(
                                payables ? ProductCost.ACCTTYPE_P_Expense : ProductCost.ACCTTYPE_P_Revenue, as);
                if (payables) {
                    //	if Fixed Asset
                    if (line.isItem()) acct = line.getAccount(ProductCost.ACCTTYPE_P_InventoryClearing, as);
                }
                BigDecimal amt = line.getAmtSource().multiply(multiplier);
                BigDecimal amt2 = null;
                if (creditMemo) {
                    amt2 = amt;
                    amt = null;
                }
                if (payables) //	Vendor = DR
                    fl = fact.createLine(line, acct, getCurrencyId(), amt, amt2);
                else //	Customer = CR
                    fl = fact.createLine(line, acct, getCurrencyId(), amt2, amt);
                if (fl != null) acctAmt = acctAmt.add(fl.getAcctBalance());
            }
        }
        //  Tax
        for (DocTax m_tax : m_taxes) {
            BigDecimal amt = m_tax.getAmount();
            BigDecimal amt2 = null;
            if (creditMemo) {
                amt2 = amt;
                amt = null;
            }
            FactLine tl;
            if (payables)
                tl =
                        fact.createLine(
                                null,
                                m_tax.getAccount(m_tax.getAPTaxType(), as),
                                getCurrencyId(),
                                amt,
                                amt2);
            else
                tl =
                        fact.createLine(
                                null,
                                m_tax.getAccount(DocTax.ACCTTYPE_TaxDue, as),
                                getCurrencyId(),
                                amt2,
                                amt);
            if (tl != null) tl.setTaxId(m_tax.getTaxId());
        }
        //  Set Locations
        FactLine[] fLines = fact.getLines();
        for (FactLine fLine : fLines) {
            if (fLine != null) {
                if (payables) {
                    fLine.setLocationFromBPartner(getBusinessPartnerLocationId(), true); //  from Loc
                    fLine.setLocationFromOrg(fLine.getOrgId(), false); //  to Loc
                } else {
                    fLine.setLocationFromOrg(fLine.getOrgId(), true); //  from Loc
                    fLine.setLocationFromBPartner(getBusinessPartnerLocationId(), false); //  to Loc
                }
            }
        }
        return acctAmt;
    } //	createFactCash

    /**
     * Create Landed Cost accounting & Cost lines
     *
     * @param as   accounting schema
     * @param fact fact
     * @param line document line
     * @param dr   DR entry (normal api)
     * @return true if landed costs were created
     */
    protected boolean landedCost(AccountingSchema as, Fact fact, DocLine line, boolean dr) {
        int C_InvoiceLine_ID = line.getId();
        MLandedCostAllocation[] lcas =
                MLandedCostAllocation.getOfInvoiceLine(C_InvoiceLine_ID);
        if (lcas.length == 0) return false;

        //	Calculate Total Base
        double totalBase = 0;
        for (MLandedCostAllocation lca1 : lcas) totalBase += lca1.getBase().doubleValue();

        Map<String, BigDecimal> costDetailAmtMap = new HashMap<>();

        //	Create New
        MInvoiceLine il = new MInvoiceLine(C_InvoiceLine_ID);
        for (MLandedCostAllocation lca : lcas) {
            if (lca.getBase().signum() == 0) continue;
            double percent = lca.getBase().doubleValue() / totalBase;
            String desc = il.getDescription();
            if (desc == null) desc = percent + "%";
            else desc += " - " + percent + "%";
            if (line.getDescription() != null) desc += " - " + line.getDescription();

            // Accounting
            BigDecimal drAmt = null;
            BigDecimal crAmt = null;
            MAccount account;
            ProductCost pc =
                    new ProductCost(
                            lca.getProductId(), lca.getAttributeSetInstanceId());
            String costingMethod = pc.getProduct().getCostingMethod(as);
            if (X_M_Cost.COSTINGMETHOD_AverageInvoice.equals(costingMethod)
                    || X_M_Cost.COSTINGMETHOD_AveragePO.equals(costingMethod)) {

                BigDecimal allocationAmt = lca.getAmt();
                BigDecimal estimatedAmt = BigDecimal.ZERO;
                int oCurrencyId = 0;
                boolean usesSchemaCurrency = false;
                Timestamp oDateAcct = getDateAcct();
                if (lca.getInOutLineId() > 0) {
                    I_M_InOutLine iol = lca.getInOutLine();
                    if (iol.getOrderLineId() > 0) {
                        oCurrencyId = iol.getOrderLine().getCurrencyId();
                        oDateAcct = iol.getOrderLine().getOrder().getDateAcct();
                        I_C_OrderLandedCostAllocation[] allocations =
                                MOrderLandedCostAllocation.getOfOrderLine(iol.getOrderLineId());
                        for (I_C_OrderLandedCostAllocation allocation : allocations) {
                            if (allocation.getOrderLandedCost().getCostElementId()
                                    != lca.getCostElementId()) continue;

                            BigDecimal amt = allocation.getAmt();
                            BigDecimal qty = allocation.getQty();
                            if (qty.compareTo(iol.getMovementQty()) != 0) {
                                amt = amt.multiply(iol.getMovementQty()).divide(qty, 12, BigDecimal.ROUND_HALF_UP);
                            }
                            estimatedAmt = estimatedAmt.add(amt);
                        }
                    }
                }

                if (estimatedAmt.scale() > as.getCostingPrecision()) {
                    estimatedAmt = estimatedAmt.setScale(as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
                }
                BigDecimal costAdjustmentAmt = allocationAmt;
                if (estimatedAmt.signum() > 0) {
                    // get other allocation amt
                    String sql = "SELECT Sum(Amt) FROM C_LandedCostAllocation WHERE M_InOutLine_ID=? " +
                            "AND C_LandedCostAllocation_ID<>? " +
                            "AND M_CostElement_ID=? " +
                            "AND AD_Client_ID=? ";
                    BigDecimal otherAmt =
                            getSQLValueBD(
                                    sql,
                                    lca.getInOutLineId(),
                                    lca.getLandedCostAllocationId(),
                                    lca.getCostElementId(),
                                    lca.getClientId());
                    if (otherAmt != null) {
                        estimatedAmt = estimatedAmt.subtract(otherAmt);
                        if (allocationAmt.signum() < 0) {
                            // add back since the sum above would include the original trx
                            estimatedAmt = estimatedAmt.add(allocationAmt.negate());
                        }
                    }
                    // added for IDEMPIERE-3014
                    // convert to accounting schema currency
                    if (estimatedAmt.signum() > 0 && oCurrencyId != getCurrencyId()) {
                        estimatedAmt =
                                MConversionRate.convert(
                                        estimatedAmt,
                                        oCurrencyId,
                                        as.getCurrencyId(),
                                        oDateAcct,
                                        getConversionTypeId(),
                                        getClientId(),
                                        getOrgId());

                        allocationAmt =
                                MConversionRate.convert(
                                        allocationAmt,
                                        getCurrencyId(),
                                        as.getCurrencyId(),
                                        getDateAcct(),
                                        getConversionTypeId(),
                                        getClientId(),
                                        getOrgId());
                        setCurrencyId(as.getCurrencyId());
                        usesSchemaCurrency = true;
                    }

                    if (estimatedAmt.signum() > 0) {
                        if (allocationAmt.signum() > 0)
                            costAdjustmentAmt = allocationAmt.subtract(estimatedAmt);
                        else if (allocationAmt.signum() < 0)
                            costAdjustmentAmt = allocationAmt.add(estimatedAmt);
                    }
                }

                if (!dr) costAdjustmentAmt = costAdjustmentAmt.negate();

                boolean zeroQty = false;
                if (costAdjustmentAmt.signum() != 0) {
                    try {
                        BigDecimal costDetailAmt = costAdjustmentAmt;
                        // convert to accounting schema currency
                        if (getCurrencyId() != as.getCurrencyId())
                            costDetailAmt =
                                    MConversionRate.convert(
                                            costDetailAmt,
                                            getCurrencyId(),
                                            as.getCurrencyId(),
                                            getDateAcct(),
                                            getConversionTypeId(),
                                            getClientId(),
                                            getOrgId());
                        if (costDetailAmt.scale() > as.getCostingPrecision())
                            costDetailAmt =
                                    costDetailAmt.setScale(as.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);

                        String key = lca.getProductId() + "_" + lca.getAttributeSetInstanceId();
                        BigDecimal prevAmt = costDetailAmtMap.remove(key);
                        if (prevAmt != null) {
                            costDetailAmt = costDetailAmt.add(prevAmt);
                        }
                        costDetailAmtMap.put(key, costDetailAmt);
                        if (!MCostDetail.createInvoice(
                                as,
                                lca.getOrgId(),
                                lca.getProductId(),
                                lca.getAttributeSetInstanceId(),
                                C_InvoiceLine_ID,
                                lca.getCostElementId(),
                                costDetailAmt,
                                lca.getQty(),
                                desc)) {
                            throw new RuntimeException("Failed to create cost detail record.");
                        }
                    } catch (AverageCostingZeroQtyException e) {
                        throw new AdempiereException(e);
                    }
                }

                boolean reversal = false;
                if (allocationAmt.signum() < 0) // reversal
                {
                    allocationAmt = allocationAmt.negate();
                    reversal = true;
                }

                if (allocationAmt.signum() > 0) {
                    if (allocationAmt.scale() > as.getStdPrecision()) {
                        allocationAmt = allocationAmt.setScale(as.getStdPrecision(), BigDecimal.ROUND_HALF_UP);
                    }
                    if (estimatedAmt.scale() > as.getStdPrecision()) {
                        estimatedAmt = estimatedAmt.setScale(as.getStdPrecision(), BigDecimal.ROUND_HALF_UP);
                    }
                    int compare = allocationAmt.compareTo(estimatedAmt);
                    if (compare > 0) {
                        drAmt = dr ? (reversal ? null : estimatedAmt) : (reversal ? estimatedAmt : null);
                        crAmt = dr ? (reversal ? estimatedAmt : null) : (reversal ? null : estimatedAmt);
                        account = pc.getAccount(ProductCost.ACCTTYPE_P_LandedCostClearing, as);
                        FactLine fl = fact.createLine(line, account, getCurrencyId(), drAmt, crAmt);
                        fl.setDescription(desc);
                        fl.setProductId(lca.getProductId());
                        fl.setQty(line.getQty());

                        BigDecimal overAmt = allocationAmt.subtract(estimatedAmt);
                        drAmt = dr ? (reversal ? null : overAmt) : (reversal ? overAmt : null);
                        crAmt = dr ? (reversal ? overAmt : null) : (reversal ? null : overAmt);
                        account =
                                zeroQty
                                        ? pc.getAccount(ProductCost.ACCTTYPE_P_AverageCostVariance, as)
                                        : pc.getAccount(ProductCost.ACCTTYPE_P_Asset, as);
                        fl = fact.createLine(line, account, getCurrencyId(), drAmt, crAmt);
                        fl.setDescription(desc);
                        fl.setProductId(lca.getProductId());
                        fl.setQty(line.getQty());
                    } else if (compare < 0) {
                        drAmt = dr ? (reversal ? null : estimatedAmt) : (reversal ? estimatedAmt : null);
                        crAmt = dr ? (reversal ? estimatedAmt : null) : (reversal ? null : estimatedAmt);
                        account = pc.getAccount(ProductCost.ACCTTYPE_P_LandedCostClearing, as);
                        FactLine fl = fact.createLine(line, account, getCurrencyId(), drAmt, crAmt);
                        fl.setDescription(desc);
                        fl.setProductId(lca.getProductId());
                        fl.setQty(line.getQty());

                        BigDecimal underAmt = estimatedAmt.subtract(allocationAmt);
                        drAmt = dr ? (reversal ? underAmt : null) : (reversal ? null : underAmt);
                        crAmt = dr ? (reversal ? null : underAmt) : (reversal ? underAmt : null);
                        account =
                                zeroQty
                                        ? pc.getAccount(ProductCost.ACCTTYPE_P_AverageCostVariance, as)
                                        : pc.getAccount(ProductCost.ACCTTYPE_P_Asset, as);
                        fl = fact.createLine(line, account, getCurrencyId(), drAmt, crAmt);
                        fl.setDescription(desc);
                        fl.setProductId(lca.getProductId());
                        fl.setQty(line.getQty());
                    } else {
                        drAmt = dr ? (reversal ? null : allocationAmt) : (reversal ? allocationAmt : null);
                        crAmt = dr ? (reversal ? allocationAmt : null) : (reversal ? null : allocationAmt);
                        account = pc.getAccount(ProductCost.ACCTTYPE_P_LandedCostClearing, as);
                        FactLine fl = fact.createLine(line, account, getCurrencyId(), drAmt, crAmt);
                        fl.setDescription(desc);
                        fl.setProductId(lca.getProductId());
                        fl.setQty(line.getQty());
                    }
                }
                if (usesSchemaCurrency) setCurrencyId(line.getCurrencyId());
            } else {
                if (dr) drAmt = lca.getAmt();
                else crAmt = lca.getAmt();
                account = pc.getAccount(ProductCost.ACCTTYPE_P_CostAdjustment, as);
                FactLine fl = fact.createLine(line, account, getCurrencyId(), drAmt, crAmt);
                fl.setDescription(desc);
                fl.setProductId(lca.getProductId());
                fl.setQty(line.getQty());
            }
        }

        if (log.isLoggable(Level.CONFIG)) log.config("Created #" + lcas.length);
        return true;
    } //	landedCosts

    /**
     * Update ProductPO PriceLastInv
     *
     * @param as accounting schema
     */
    protected void updateProductPO(AccountingSchema as) {
        MClientInfo ci = MClientInfo.get(as.getClientId());
        if (ci.getAcctSchema1Id() != as.getAccountingSchemaId()) return;

        // jz + " AND ROWNUM=1 AND i.C_Invoice_ID=").append(getId()).append(") ")
        String sql = "UPDATE M_Product_PO po " +
                "SET PriceLastInv = " +
                //	select
                "(SELECT currencyConvert(il.PriceActual,i.C_Currency_ID,po.C_Currency_ID,i.DateInvoiced,i.C_ConversionType_ID,i.AD_Client_ID,i.orgId) " +
                "FROM C_Invoice i, C_InvoiceLine il " +
                "WHERE i.C_Invoice_ID=il.C_Invoice_ID" +
                " AND po.M_Product_ID=il.M_Product_ID AND po.C_BPartner_ID=i.C_BPartner_ID" +
                " AND il.C_InvoiceLine_ID = (SELECT MIN(il1.C_InvoiceLine_ID) " +
                "FROM C_Invoice i1, C_InvoiceLine il1 " +
                "WHERE i1.C_Invoice_ID=il1.C_Invoice_ID" +
                " AND po.M_Product_ID=il1.M_Product_ID AND po.C_BPartner_ID=i1.C_BPartner_ID" +
                "  AND i1.C_Invoice_ID=" +
                getId() +
                ") " +
                "  AND i.C_Invoice_ID=" +
                getId() +
                ") " +
                //	update
                "WHERE EXISTS (SELECT * " +
                "FROM C_Invoice i, C_InvoiceLine il " +
                "WHERE i.C_Invoice_ID=il.C_Invoice_ID" +
                " AND po.M_Product_ID=il.M_Product_ID AND po.C_BPartner_ID=i.C_BPartner_ID" +
                " AND i.C_Invoice_ID=" +
                getId() +
                ")";
        int no = executeUpdate(sql);
        if (log.isLoggable(Level.FINE)) log.fine("Updated=" + no);
    } //	updateProductPO
} //  Doc_Invoice
