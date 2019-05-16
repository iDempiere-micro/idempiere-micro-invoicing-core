package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MConversionRate;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.AccountSchemaElement;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_ValidCombination;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Post Allocation Documents.
 *
 * <pre>
 *  Table:              C_AllocationHdr
 *  Document Types:     CMA
 *  </pre>
 *
 * @author Jorg Janke
 * @author phib BF [ 2019262 ] Allocation posting currency gain/loss omits line reference
 * @version $Id: Doc_Allocation.java,v 1.6 2006/07/30 00:53:33 jjanke Exp $
 * <p>FR [ 1840016 ] Avoid usage of clearing accounts - subject to
 * C_AcctSchema.IsPostIfClearingEqual Avoid posting if Receipt and both accounts Unallocated
 * Cash and Receivable are equal Avoid posting if Payment and both accounts Payment Select and
 * Liability are equal
 */
public class Doc_AllocationHdr extends Doc {
    /**
     * Tolerance G&L
     */
    private static final BigDecimal TOLERANCE = BigDecimal.valueOf(0.02);
    /**
     * Facts
     */
    private ArrayList<IFact> m_facts = null;

    /**
     * Constructor
     *
     * @param as accounting schema
     * @param rs record
     */
    public Doc_AllocationHdr(AccountingSchema as, Row rs) {
        super(as, MAllocationHdr.class, rs, DOCTYPE_Allocation);
    } //  Doc_Allocation

    @Override
    protected IPODoc createNewInstance(Row rs) {
        return new MAllocationHdr(rs, -1);
    }

    /**
     * Load Specific Document Details
     *
     * @return error message or null
     */
    protected String loadDocumentDetails() {
        MAllocationHdr alloc = (MAllocationHdr) getPO();
        setDateDoc(alloc.getDateTrx());
        //	Contained Objects
        p_lines = loadLines(alloc);
        return null;
    } //  loadDocumentDetails

    /**
     * Load Invoice Line
     *
     * @param alloc header
     * @return DocLine Array
     */
    private DocLine[] loadLines(MAllocationHdr alloc) {
        ArrayList<DocLine> list = new ArrayList<>();
        MAllocationLine[] lines = alloc.getLines(false);
        for (MAllocationLine line : lines) {
            DocLine_Allocation docLine = new DocLine_Allocation(line, this);
            //
            if (log.isLoggable(Level.FINE)) log.fine(docLine.toString());
            list.add(docLine);
        }

        //	Return Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);
        return dls;
    } //	loadLines

    /**
     * ************************************************************************ Get Source Currency
     * Balance - subtracts line and tax amounts from total - no rounding
     *
     * @return positive amount, if total invoice is bigger than lines
     */
    public BigDecimal getBalance() {
        return Env.ZERO;
    } //  getBalance

    /**
     * Create Facts (the accounting logic) for CMA.
     *
     * <pre>
     *  AR_Invoice_Payment
     *      UnAllocatedCash DR
     *      or C_Prepayment
     *      DiscountExp     DR
     *      WriteOff        DR
     *      Receivables             CR
     *  AR_Invoice_Cash
     *      CashTransfer    DR
     *      DiscountExp     DR
     *      WriteOff        DR
     *      Receivables             CR
     *
     *  AP_Invoice_Payment
     *      Liability       DR
     *      DiscountRev             CR
     *      WriteOff                CR
     *      PaymentSelect           CR
     *      or V_Prepayment
     *  AP_Invoice_Cash
     *      Liability       DR
     *      DiscountRev             CR
     *      WriteOff                CR
     *      CashTransfer            CR
     *  CashBankTransfer
     *      -
     *  ==============================
     *  Realized Gain & Loss
     * 		AR/AP			DR		CR
     * 		Realized G/L	DR		CR
     *
     *
     *  </pre>
     * <p>
     * Tax needs to be corrected for discount & write-off; Currency gain & loss is realized here.
     *
     * @param as accounting schema
     * @return Fact
     */
    public ArrayList<IFact> createFacts(AccountingSchema as) {
        m_facts = new ArrayList<>();

        //  create Fact Header
        Fact fact = new Fact(this, as, Fact.POST_Actual);
        Fact factForRGL =
                new Fact(
                        this,
                        as,
                        Fact.POST_Actual); // dummy fact (not posted) to calculate Realized Gain & Loss
        boolean isInterOrg = isInterOrg(as);

        for (DocLine p_line : p_lines) {
            DocLine_Allocation line = (DocLine_Allocation) p_line;
            setBusinessPartnerId(line.getBusinessPartnerId());

            //  CashBankTransfer - all references null and Discount/WriteOff = 0
            if (line.getPaymentId() != 0
                    && line.getInvoiceId() == 0
                    && line.getOrderId() == 0
                    && line.getCashLineId() == 0
                    && line.getBusinessPartnerId() == 0
                    && Env.ZERO.compareTo(line.getDiscountAmt()) == 0
                    && Env.ZERO.compareTo(line.getWriteOffAmt()) == 0) continue;

            //	Receivables/Liability Amt
            BigDecimal allocationSource =
                    line.getAmtSource().add(line.getDiscountAmt()).add(line.getWriteOffAmt());
            BigDecimal allocationSourceForRGL = allocationSource; // for realized gain & loss purposes
            BigDecimal allocationAccounted = Env.ZERO; // AR/AP balance corrected
            @SuppressWarnings("unused")
            BigDecimal allocationAccountedForRGL = Env.ZERO; // for realized gain & loss purposes

            FactLine fl;
            FactLine flForRGL;
            MAccount bpAcct = null; // 	Liability/Receivables
            //
            MPayment payment = null;
            if (line.getPaymentId() != 0)
                payment = new MPayment(line.getPaymentId());
            MInvoice invoice = null;
            if (line.getInvoiceId() != 0)
                invoice = new MInvoice(null, line.getInvoiceId());

            //	No Invoice
            if (invoice == null) {
                //	adaxa-pb: allocate to charges
                // Charge Only
                if (line.getInvoiceId() == 0
                        && line.getPaymentId() == 0
                        && line.getChargeId() != 0) {
                    fact.createLine(
                            line,
                            line.getChargeAccount(as),
                            getCurrencyId(),
                            line.getAmtSource());
                }
                //	Payment Only
                else if (line.getInvoiceId() == 0 && line.getPaymentId() != 0) {
                    fl =
                            fact.createLine(
                                    line,
                                    getPaymentAcct(as, line.getPaymentId()),
                                    getCurrencyId(),
                                    line.getAmtSource(),
                                    null);
                    if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                } else {
                    p_Error = "Cannot determine SO/PO";
                    log.log(Level.SEVERE, p_Error);
                    return null;
                }
            }
            //	Sales Invoice
            else if (invoice.isSOTrx()) {

                // Avoid usage of clearing accounts
                // If both accounts Unallocated Cash and Receivable are equal
                // then don't post

                MAccount acct_unallocated_cash = null;
                if (line.getPaymentId() != 0)
                    acct_unallocated_cash = getPaymentAcct(as, line.getPaymentId());
                else if (line.getCashLineId() != 0)
                    acct_unallocated_cash = getCashAcct(as, line.getCashLineId());
                MAccount acct_receivable = getAccount(Doc.ACCTTYPE_C_Receivable, as);

                if ((!as.isPostIfClearingEqual())
                        && acct_unallocated_cash != null
                        && acct_unallocated_cash.equals(acct_receivable)
                        && (!isInterOrg)) {

                    // if not using clearing accounts, then don't post amtsource
                    // change the allocationsource to be writeoff + discount
                    allocationSource = line.getDiscountAmt().add(line.getWriteOffAmt());

                } else {

                    // Normal behavior -- unchanged if using clearing accounts

                    //	Payment/Cash	DR
                    if (line.getPaymentId() != 0) {
                        fl =
                                fact.createLine(
                                        line,
                                        getPaymentAcct(as, line.getPaymentId()),
                                        getCurrencyId(),
                                        line.getAmtSource(),
                                        null);
                        if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                    } else if (line.getCashLineId() != 0) {
                        fl =
                                fact.createLine(
                                        line,
                                        getCashAcct(as, line.getCashLineId()),
                                        getCurrencyId(),
                                        line.getAmtSource(),
                                        null);
                        MCashLine cashLine = new MCashLine(line.getCashLineId());
                        if (fl != null && cashLine.getId() != 0) fl.setOrgId(cashLine.getOrgId());
                    }
                }
                // End Avoid usage of clearing accounts

                //	Discount		DR
                if (Env.ZERO.compareTo(line.getDiscountAmt()) != 0) {
                    fl =
                            fact.createLine(
                                    line,
                                    getAccount(Doc.ACCTTYPE_DiscountExp, as),
                                    getCurrencyId(),
                                    line.getDiscountAmt(),
                                    null);
                    if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                }
                //	Write off		DR
                if (Env.ZERO.compareTo(line.getWriteOffAmt()) != 0) {
                    fl =
                            fact.createLine(
                                    line,
                                    getAccount(Doc.ACCTTYPE_WriteOff, as),
                                    getCurrencyId(),
                                    line.getWriteOffAmt(),
                                    null);
                    if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                }

                //	AR Invoice Amount	CR
                if (as.isAccrual()) {
                    bpAcct = getAccount(Doc.ACCTTYPE_C_Receivable, as);
                    fl =
                            fact.createLine(
                                    line, bpAcct, getCurrencyId(), null, allocationSource); // 	payment currency
                    if (fl != null) allocationAccounted = fl.getAcctBalance().negate();
                    if (fl != null) fl.setOrgId(invoice.getOrgId());

                    // for Realized Gain & Loss
                    flForRGL =
                            factForRGL.createLine(
                                    line,
                                    bpAcct,
                                    getCurrencyId(),
                                    null,
                                    allocationSourceForRGL); //	payment currency
                    if (flForRGL != null) flForRGL.getAcctBalance().negate();
                } else //	Cash Based
                {
                    allocationAccounted = createCashBasedAcct(as, fact, invoice, allocationSource);
                }
            }
            //	Purchase Invoice
            else {
                // Avoid usage of clearing accounts
                // If both accounts Payment Select and Liability are equal
                // then don't post

                MAccount acct_payment_select = null;
                if (line.getPaymentId() != 0)
                    acct_payment_select = getPaymentAcct(as, line.getPaymentId());
                else if (line.getCashLineId() != 0)
                    acct_payment_select = getCashAcct(as, line.getCashLineId());
                MAccount acct_liability = getAccount(Doc.ACCTTYPE_V_Liability, as);
                boolean isUsingClearing = true;

                // Save original allocation source for realized gain & loss purposes
                allocationSourceForRGL = allocationSourceForRGL.negate();

                if ((!as.isPostIfClearingEqual())
                        && acct_payment_select != null
                        && acct_payment_select.equals(acct_liability)
                        && (!isInterOrg)) {

                    // if not using clearing accounts, then don't post amtsource
                    // change the allocationsource to be writeoff + discount
                    allocationSource = line.getDiscountAmt().add(line.getWriteOffAmt());
                    isUsingClearing = false;
                }
                // End Avoid usage of clearing accounts

                allocationSource = allocationSource.negate(); // 	allocation is negative
                //	AP Invoice Amount	DR
                if (as.isAccrual()) {
                    bpAcct = getAccount(Doc.ACCTTYPE_V_Liability, as);
                    fl =
                            fact.createLine(
                                    line, bpAcct, getCurrencyId(), allocationSource, null); // 	payment currency
                    if (fl != null) allocationAccounted = fl.getAcctBalance();
                    if (fl != null) fl.setOrgId(invoice.getOrgId());

                    // for Realized Gain & Loss
                    flForRGL =
                            factForRGL.createLine(
                                    line,
                                    bpAcct,
                                    getCurrencyId(),
                                    allocationSourceForRGL,
                                    null); //	payment currency
                    if (flForRGL != null) flForRGL.getAcctBalance();
                } else //	Cash Based
                {
                    allocationAccounted = createCashBasedAcct(as, fact, invoice, allocationSource);
                }

                //	Discount		CR
                if (Env.ZERO.compareTo(line.getDiscountAmt()) != 0) {
                    fl =
                            fact.createLine(
                                    line,
                                    getAccount(Doc.ACCTTYPE_DiscountRev, as),
                                    getCurrencyId(),
                                    null,
                                    line.getDiscountAmt().negate());
                    if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                }
                //	Write off		CR
                if (Env.ZERO.compareTo(line.getWriteOffAmt()) != 0) {
                    fl =
                            fact.createLine(
                                    line,
                                    getAccount(Doc.ACCTTYPE_WriteOff, as),
                                    getCurrencyId(),
                                    null,
                                    line.getWriteOffAmt().negate());
                    if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                }
                //	Payment/Cash	CR
                if (isUsingClearing && line.getPaymentId() != 0) // Avoid usage of clearing accounts
                {
                    fl =
                            fact.createLine(
                                    line,
                                    getPaymentAcct(as, line.getPaymentId()),
                                    getCurrencyId(),
                                    null,
                                    line.getAmtSource().negate());
                    if (fl != null && payment != null) fl.setOrgId(payment.getOrgId());
                } else if (isUsingClearing
                        && line.getCashLineId() != 0) // Avoid usage of clearing accounts
                {
                    fl =
                            fact.createLine(
                                    line,
                                    getCashAcct(as, line.getCashLineId()),
                                    getCurrencyId(),
                                    null,
                                    line.getAmtSource().negate());
                    MCashLine cashLine = new MCashLine(line.getCashLineId());
                    if (fl != null && cashLine.getId() != 0) fl.setOrgId(cashLine.getOrgId());
                }
            }

            //	VAT Tax Correction
            if (invoice != null && as.isTaxCorrection()) {
                BigDecimal taxCorrectionAmt = Env.ZERO;
                if (as.isTaxCorrectionDiscount()) taxCorrectionAmt = line.getDiscountAmt();
                if (as.isTaxCorrectionWriteOff())
                    taxCorrectionAmt = taxCorrectionAmt.add(line.getWriteOffAmt());
                //
                if (taxCorrectionAmt.signum() != 0) {
                    if (!createTaxCorrection(
                            as,
                            fact,
                            line,
                            getAccount(
                                    invoice.isSOTrx() ? Doc.ACCTTYPE_DiscountExp : Doc.ACCTTYPE_DiscountRev, as),
                            getAccount(Doc.ACCTTYPE_WriteOff, as),
                            invoice.isSOTrx())) {
                        p_Error = "Cannot create Tax correction";
                        return null;
                    }
                }
            }

            //	Realized Gain & Loss
            if (invoice != null
                    && (getCurrencyId() != as.getCurrencyId() // 	payment allocation in foreign currency
                    || getCurrencyId()
                    != line.getInvoiceC_CurrencyId())) // 	allocation <> invoice currency
            {
                p_Error =
                        createRealizedGainLoss(
                                line, as, fact, bpAcct, invoice, allocationSource, allocationAccounted);
                if (p_Error != null) return null;
            }
        } //	for all lines

        // FR [ 1840016 ] Avoid usage of clearing accounts - subject to
        // C_AcctSchema.IsPostIfClearingEqual
        if ((!as.isPostIfClearingEqual()) && p_lines.length > 0 && (!isInterOrg)) {
            boolean allEquals = true;
            // more than one line (i.e. crossing one payment+ with a payment-, or an invoice against a
            // credit memo)
            // verify if the sum of all facts is zero net
            FactLine[] factlines = fact.getLines();
            BigDecimal netBalance = Env.ZERO;
            FactLine prevFactLine = null;
            for (FactLine factLine : factlines) {
                netBalance = netBalance.add(factLine.getAmtSourceDr()).subtract(factLine.getAmtSourceCr());
                if (prevFactLine != null) {
                    if (!equalFactLineIDs(prevFactLine, factLine)) {
                        allEquals = false;
                        break;
                    }
                }
                prevFactLine = factLine;
            }
            if (netBalance.compareTo(Env.ZERO) == 0 && allEquals) {
                // delete the postings
                for (FactLine factline : factlines) fact.remove(factline);
            }
        }

        //	reset line info
        setBusinessPartnerId(0);
        //
        m_facts.add(fact);
        return m_facts;
    } //  createFact

    /**
     * Verify if the posting involves two or more organizations
     *
     * @return true if there are more than one org involved on the posting
     */
    private boolean isInterOrg(AccountingSchema as) {
        AccountSchemaElement elementorg =
                as.getAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_Organization);
        if (elementorg == null || !elementorg.isBalanced()) {
            // no org element or not need to be balanced
            return false;
        }

        if (p_lines.length <= 0) {
            // no lines
            return false;
        }

        int startorg = p_lines[0].getOrgId();
        // validate if the allocation involves more than one org
        for (DocLine p_line : p_lines) {
            DocLine_Allocation line = (DocLine_Allocation) p_line;
            int orgpayment = startorg;
            MPayment payment;
            if (line.getPaymentId() != 0) {
                payment = new MPayment(line.getPaymentId());
                orgpayment = payment.getOrgId();
            }
            int orginvoice = startorg;
            MInvoice invoice;
            if (line.getInvoiceId() != 0) {
                invoice = new MInvoice(null, line.getInvoiceId());
                orginvoice = invoice.getOrgId();
            }
            int orgcashline = startorg;
            MCashLine cashline;
            if (line.getCashLineId() != 0) {
                cashline = new MCashLine(line.getCashLineId());
                orgcashline = cashline.getOrgId();
            }
            int orgorder = startorg;
            MOrder order;
            if (line.getOrderId() != 0) {
                order = new MOrder(line.getOrderId());
                orgorder = order.getOrgId();
            }

            if (line.getOrgId() != startorg
                    || orgpayment != startorg
                    || orginvoice != startorg
                    || orgcashline != startorg
                    || orgorder != startorg) return true;
        }

        return false;
    }

    /**
     * Compare the dimension ID's from two factlines
     *
     * @param prevFactLine
     * @param factLine
     * @return boolean indicating if both dimension ID's are equal
     */
    private boolean equalFactLineIDs(FactLine prevFactLine, FactLine factLine) {
        return (factLine.getAssetId() == prevFactLine.getAssetId()
                && factLine.getAccountId() == prevFactLine.getAccountId()
                && factLine.getClientId() == prevFactLine.getClientId()
                && factLine.getOrgId() == prevFactLine.getOrgId()
                && factLine.getTransactionOrganizationId() == prevFactLine.getTransactionOrganizationId()
                && factLine.getAccountingSchemaId() == prevFactLine.getAccountingSchemaId()
                && factLine.getBusinessActivityId() == prevFactLine.getBusinessActivityId()
                && factLine.getBusinessPartnerId() == prevFactLine.getBusinessPartnerId()
                && factLine.getCampaignId() == prevFactLine.getCampaignId()
                && factLine.getCurrencyId() == prevFactLine.getCurrencyId()
                && factLine.getLocationFromId() == prevFactLine.getLocationFromId()
                && factLine.getLocationToId() == prevFactLine.getLocationToId()
                && factLine.getPeriodId() == prevFactLine.getPeriodId()
                && factLine.getProjectId() == prevFactLine.getProjectId()
                && factLine.getProjectPhaseId() == prevFactLine.getProjectPhaseId()
                && factLine.getProjectTaskId() == prevFactLine.getProjectTaskId()
                && factLine.getSalesRegionId() == prevFactLine.getSalesRegionId()
                && factLine.getSubAccountId() == prevFactLine.getSubAccountId()
                && factLine.getTaxId() == prevFactLine.getTaxId()
                && factLine.getUOMId() == prevFactLine.getUOMId()
                && factLine.getGLBudgetId() == prevFactLine.getGLBudgetId()
                && factLine.getGLCategoryId() == prevFactLine.getGLCategoryId()
                && factLine.getLocatorId() == prevFactLine.getLocatorId()
                && factLine.getProductId() == prevFactLine.getProductId()
                && factLine.getUserElement1Id() == prevFactLine.getUserElement1Id()
                && factLine.getUserElement2Id() == prevFactLine.getUserElement2Id()
                && factLine.getUser1Id() == prevFactLine.getUser1Id()
                && factLine.getUser2Id() == prevFactLine.getUser2Id());
    }

    /**
     * Create Cash Based Acct
     *
     * @param as               accounting schema
     * @param fact             fact
     * @param invoice          invoice
     * @param allocationSource allocation amount (incl discount, writeoff)
     * @return Accounted Amt
     */
    private BigDecimal createCashBasedAcct(
            AccountingSchema as, Fact fact, MInvoice invoice, BigDecimal allocationSource) {
        BigDecimal allocationAccounted;
        //	Multiplier
        double percent = invoice.getGrandTotal().doubleValue() / allocationSource.doubleValue();
        if (percent > 0.99 && percent < 1.01) percent = 1.0;
        if (log.isLoggable(Level.CONFIG))
            log.config(
                    "Multiplier="
                            + percent
                            + " - GrandTotal="
                            + invoice.getGrandTotal()
                            + " - Allocation Source="
                            + allocationSource);

        //	Get Invoice Postings
        Doc_Invoice docInvoice =
                (Doc_Invoice) Doc.get(as, MInvoice.Table_ID, invoice.getInvoiceId());
        docInvoice.loadDocumentDetails();
        allocationAccounted = docInvoice.createFactCash(as, fact, BigDecimal.valueOf(percent));
        if (log.isLoggable(Level.CONFIG)) log.config("Allocation Accounted=" + allocationAccounted);

        //	Cash Based Commitment Release
        if (as.isCreatePOCommitment() && !invoice.isSOTrx()) {
            I_C_InvoiceLine[] lines = invoice.getLines();
            for (I_C_InvoiceLine line : lines) {
                Fact factC =
                        Doc_Order.getCommitmentRelease(
                                as,
                                this,
                                line.getQtyInvoiced(),
                                line.getInvoiceLineId(),
                                BigDecimal.valueOf(percent));
                if (factC == null) return null;
                m_facts.add(factC);
            }
        } //	Commitment

        return allocationAccounted;
    } //	createCashBasedAcct

    /**
     * Get Payment (Unallocated Payment or Payment Selection) Acct of Bank Account
     *
     * @param as           accounting schema
     * @param C_Payment_ID payment
     * @return acct
     */
    private MAccount getPaymentAcct(AccountingSchema as, int C_Payment_ID) {
        setBankAccountId(0);
        //	Doc.ACCTTYPE_UnallocatedCash (AR) or C_Prepayment
        //	or Doc.ACCTTYPE_PaymentSelect (AP) or V_Prepayment
        int accountType = Doc.ACCTTYPE_UnallocatedCash;
        //
        String sql =
                "SELECT p.C_BankAccount_ID, d.DocBaseType, p.IsReceipt, p.IsPrepayment "
                        + "FROM C_Payment p INNER JOIN C_DocType d ON (p.C_DocType_ID=d.C_DocType_ID) "
                        + "WHERE C_Payment_ID=?";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, C_Payment_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                setBankAccountId(rs.getInt(1));
                if (DOCTYPE_APPayment.equals(rs.getString(2))) accountType = Doc.ACCTTYPE_PaymentSelect;
                //	Prepayment
                if ("Y".equals(rs.getString(4))) // 	Prepayment
                {
                    if ("Y".equals(rs.getString(3))) // 	Receipt
                        accountType = Doc.ACCTTYPE_C_Prepayment;
                    else accountType = Doc.ACCTTYPE_V_Prepayment;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        }
        //
        if (getBankAccountId() <= 0) {
            log.log(Level.SEVERE, "NONE for C_Payment_ID=" + C_Payment_ID);
            return null;
        }
        return getAccount(accountType, as);
    } //	getPaymentAcct

    /**
     * Get Cash (Transfer) Acct of CashBook
     *
     * @param as            accounting schema
     * @param C_CashLine_ID
     * @return acct
     */
    private MAccount getCashAcct(AccountingSchema as, int C_CashLine_ID) {
        String sql =
                "SELECT c.C_CashBook_ID "
                        + "FROM C_Cash c, C_CashLine cl "
                        + "WHERE c.C_Cash_ID=cl.C_Cash_ID AND cl.C_CashLine_ID=?";
        setCashBookId(getSQLValue(sql, C_CashLine_ID));

        if (getCashBookId() <= 0) {
            log.log(Level.SEVERE, "NONE for C_CashLine_ID=" + C_CashLine_ID);
            return null;
        }
        return getAccount(Doc.ACCTTYPE_CashTransfer, as);
    } //	getCashAcct

    /**
     * ************************************************************************ Create Realized Gain &
     * Loss. Compares the Accounted Amount of the Invoice to the Accounted Amount of the Allocation
     *
     * @param as                  accounting schema
     * @param fact                fact
     * @param acct                account
     * @param invoice             invoice
     * @param allocationSource    source amt
     * @param allocationAccounted acct amt
     * @return Error Message or null if OK
     */
    private String createRealizedGainLoss(
            DocLine line,
            AccountingSchema as,
            Fact fact,
            MAccount acct,
            MInvoice invoice,
            BigDecimal allocationSource,
            BigDecimal allocationAccounted) {
        BigDecimal invoiceSource = null;
        BigDecimal invoiceAccounted = null;
        //
        StringBuilder sql =
                new StringBuilder("SELECT ")
                        .append(
                                invoice.isSOTrx()
                                        ? "SUM(AmtSourceDr), SUM(AmtAcctDr)" //	so
                                        : "SUM(AmtSourceCr), SUM(AmtAcctCr)") //	po
                        .append(" FROM Fact_Acct ")
                        .append("WHERE AD_Table_ID=318 AND Record_ID=?") // 	Invoice
                        .append(" AND C_AcctSchema_ID=?")
                        .append(" AND PostingType='A'");
        // AND C_Currency_ID=102
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql.toString());
            pstmt.setInt(1, invoice.getInvoiceId());
            pstmt.setInt(2, as.getAccountingSchemaId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                invoiceSource = rs.getBigDecimal(1);
                invoiceAccounted = rs.getBigDecimal(2);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, sql.toString(), e);
        }
        // 	Requires that Invoice is Posted
        if (invoiceSource == null || invoiceAccounted == null)
            return "Gain/Loss - Invoice not posted yet";
        //
        StringBuilder description =
                new StringBuilder("Invoice=(")
                        .append(invoice.getCurrencyId())
                        .append(")")
                        .append(invoiceSource)
                        .append("/")
                        .append(invoiceAccounted)
                        .append(" - Allocation=(")
                        .append(getCurrencyId())
                        .append(")")
                        .append(allocationSource)
                        .append("/")
                        .append(allocationAccounted);
        if (log.isLoggable(Level.FINE)) log.fine(description.toString());
        //	Allocation not Invoice Currency
        if (getCurrencyId() != invoice.getCurrencyId()) {
            BigDecimal allocationSourceNew =
                    MConversionRate.convert(
                            allocationSource,
                            getCurrencyId(),
                            invoice.getCurrencyId(),
                            getDateAcct(),
                            invoice.getConversionTypeId(),
                            invoice.getClientId(),
                            invoice.getOrgId());
            if (allocationSourceNew == null) return "Gain/Loss - No Conversion from Allocation->Invoice";
            StringBuilder d2 =
                    new StringBuilder("Allocation=(")
                            .append(getCurrencyId())
                            .append(")")
                            .append(allocationSource)
                            .append("->(")
                            .append(invoice.getCurrencyId())
                            .append(")")
                            .append(allocationSourceNew);
            if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
            description.append(" - ").append(d2);
            allocationSource = allocationSourceNew;
        }

        BigDecimal acctDifference; // 	gain is negative
        //	Full Payment in currency
        if (allocationSource.compareTo(invoiceSource) == 0) {
            acctDifference = invoiceAccounted.subtract(allocationAccounted); // 	gain is negative
            StringBuilder d2 = new StringBuilder("(full) = ").append(acctDifference);
            if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
            description.append(" - ").append(d2);
        } else //	partial or MC
        {
            //	percent of total payment
            double multiplier = allocationSource.doubleValue() / invoiceSource.doubleValue();
            //	Reduce Orig Invoice Accounted
            invoiceAccounted = invoiceAccounted.multiply(BigDecimal.valueOf(multiplier));
            //	Difference based on percentage of Orig Invoice
            acctDifference = invoiceAccounted.subtract(allocationAccounted); // 	gain is negative
            //	ignore Tolerance
            if (acctDifference.abs().compareTo(TOLERANCE) < 0) acctDifference = Env.ZERO;
            //	Round
            int precision = as.getStdPrecision();
            if (acctDifference.scale() > precision)
                acctDifference = acctDifference.setScale(precision, BigDecimal.ROUND_HALF_UP);
            StringBuilder d2 =
                    new StringBuilder("(partial) = ")
                            .append(acctDifference)
                            .append(" - Multiplier=")
                            .append(multiplier);
            if (log.isLoggable(Level.FINE)) log.fine(d2.toString());
            description.append(" - ").append(d2);
        }

        if (acctDifference.signum() == 0) {
            log.fine("No Difference");
            return null;
        }

        MAccount gain = MAccount.get(as.getAcctSchemaDefault().getRealizedGainAccount());
        MAccount loss = MAccount.get(as.getAcctSchemaDefault().getRealizedLossAccount());
        //
        if (invoice.isSOTrx()) {
            FactLine fl = fact.createLine(line, loss, gain, as.getCurrencyId(), acctDifference);
            fl.setDescription(description.toString());
            fact.createLine(line, acct, as.getCurrencyId(), acctDifference.negate());
            fl.setDescription(description.toString());
        } else {
            fact.createLine(line, acct, as.getCurrencyId(), acctDifference);
            @SuppressWarnings("unused")
            FactLine fl =
                    fact.createLine(line, loss, gain, as.getCurrencyId(), acctDifference.negate());
        }
        return null;
    } //	createRealizedGainLoss

    /**
     * ************************************************************************ Create Tax Correction.
     * Requirement: Adjust the tax amount, if you did not receive the full amount of the invoice
     * (payment discount, write-off). Applies to many countries with VAT. Example: Invoice: Net $100 +
     * Tax1 $15 + Tax2 $5 = Total $120 Payment: $115 (i.e. $5 underpayment) Tax Adjustment = Tax1 =
     * 0.63 (15/120*5) Tax2 = 0.21 (5/120/5)
     *
     * @param as              accounting schema
     * @param fact            fact
     * @param line            Allocation line
     * @param DiscountAccount discount acct
     * @param WriteOffAccoint write off acct
     * @return true if created
     */
    private boolean createTaxCorrection(
            AccountingSchema as,
            Fact fact,
            DocLine_Allocation line,
            MAccount DiscountAccount,
            MAccount WriteOffAccoint,
            boolean isSOTrx) {
        if (log.isLoggable(Level.INFO)) log.info(line.toString());
        BigDecimal discount = Env.ZERO;
        if (as.isTaxCorrectionDiscount())
            discount = line.getDiscountAmt();
        BigDecimal writeOff = Env.ZERO;
        if (as.isTaxCorrectionWriteOff())
            writeOff = line.getWriteOffAmt();

        Doc_AllocationTax tax = new Doc_AllocationTax(
                DiscountAccount, discount, WriteOffAccoint, writeOff, isSOTrx);

        return BasePostAllocationDocumentsKt.createTaxCorrection(as,
                fact,
                line,
                tax);
    } //	createTaxCorrection
} //  Doc_Allocation

/**
 * Allocation Document Tax Handing
 *
 * @author Jorg Janke
 * @version $Id: Doc_Allocation.java,v 1.6 2006/07/30 00:53:33 jjanke Exp $
 */
class Doc_AllocationTax implements DocAllocationTax {
    private CLogger log = CLogger.getCLogger(getClass());
    private MAccount m_DiscountAccount;
    private BigDecimal m_DiscountAmt;
    private MAccount m_WriteOffAccount;
    private BigDecimal m_WriteOffAmt;
    private boolean m_IsSOTrx;
    private ArrayList<MFactAcct> m_facts = new ArrayList<>();
    private int m_totalIndex = 0;

    /**
     * Allocation Tax Adjustment
     *
     * @param DiscountAccount discount acct
     * @param DiscountAmt     discount amt
     * @param WriteOffAccount write off acct
     * @param WriteOffAmt     write off amt
     */
    public Doc_AllocationTax(
            MAccount DiscountAccount,
            BigDecimal DiscountAmt,
            MAccount WriteOffAccount,
            BigDecimal WriteOffAmt,
            boolean isSOTrx) {
        m_DiscountAccount = DiscountAccount;
        m_DiscountAmt = DiscountAmt;
        m_WriteOffAccount = WriteOffAccount;
        m_WriteOffAmt = WriteOffAmt;
        m_IsSOTrx = isSOTrx;
    } //	Doc_AllocationTax

    /**
     * Add Invoice Fact Line
     *
     * @param fact fact line
     */
    public void addInvoiceFact(@NotNull MFactAcct fact) {
        m_facts.add(fact);
    } //	addInvoiceLine

    /**
     * Get Line Count
     *
     * @return number of lines
     */
    public int getLineCount() {
        return m_facts.size();
    } //	getLineCount

    /**
     * Create Accounting Entries
     *
     * @param as   account schema
     * @param fact fact to add lines
     * @param line line
     * @return true if created
     */
    public boolean createEntries(@NotNull AccountingSchema as, @NotNull Fact fact, @NotNull DocLine line) {
        //	get total index (the Receivables/Liabilities line)
        BigDecimal total = Env.ZERO;
        for (int i = 0; i < m_facts.size(); i++) {
            MFactAcct factAcct = m_facts.get(i);
            if (factAcct.getAmtSourceDr().compareTo(total) > 0) {
                total = factAcct.getAmtSourceDr();
                m_totalIndex = i;
            }
            if (factAcct.getAmtSourceCr().compareTo(total) > 0) {
                total = factAcct.getAmtSourceCr();
                m_totalIndex = i;
            }
        }

        MFactAcct factAcct = m_facts.get(m_totalIndex);
        if (log.isLoggable(Level.INFO)) log.info("Total Invoice = " + total + " - " + factAcct);
        int precision = as.getStdPrecision();
        for (int i = 0; i < m_facts.size(); i++) {
            //	No Tax Line
            if (i == m_totalIndex) continue;

            factAcct = m_facts.get(i);
            if (log.isLoggable(Level.INFO)) log.info(i + ": " + factAcct);

            //	Create Tax Account
            I_C_ValidCombination taxAcct = factAcct.getMAccount();
            if (taxAcct == null || taxAcct.getId() == 0) {
                log.severe("Tax Account not found/created");
                return false;
            }

            //			Discount Amount
            if (m_DiscountAmt.signum() != 0) {
                //	Original Tax is DR - need to correct it CR
                if (Env.ZERO.compareTo(factAcct.getAmtSourceDr()) != 0) {
                    BigDecimal amount =
                            calcAmount(factAcct.getAmtSourceDr(), total, m_DiscountAmt, precision);
                    if (amount.signum() != 0) {
                        // for sales actions
                        if (m_IsSOTrx) {
                            fact.createLine(line, m_DiscountAccount, as.getCurrencyId(), amount, null);
                            fact.createLine(line, taxAcct, as.getCurrencyId(), null, amount);
                        } else {
                            // for purchase actions
                            fact.createLine(
                                    line, m_DiscountAccount, as.getCurrencyId(), amount.negate(), null);
                            fact.createLine(line, taxAcct, as.getCurrencyId(), null, amount.negate());
                        }
                    }
                }
                //	Original Tax is CR - need to correct it DR
                else {
                    BigDecimal amount =
                            calcAmount(factAcct.getAmtSourceCr(), total, m_DiscountAmt, precision);
                    if (amount.signum() != 0) {
                        //						for sales actions
                        if (m_IsSOTrx) {
                            fact.createLine(line, taxAcct, as.getCurrencyId(), amount, null);
                            fact.createLine(line, m_DiscountAccount, as.getCurrencyId(), null, amount);
                        } else {
                            fact.createLine(line, taxAcct, as.getCurrencyId(), amount.negate(), null);
                            fact.createLine(
                                    line, m_DiscountAccount, as.getCurrencyId(), null, amount.negate());
                        }
                    }
                }
            } //	Discount

            //	WriteOff Amount
            if (m_WriteOffAmt.signum() != 0) {
                //	Original Tax is DR - need to correct it CR
                if (Env.ZERO.compareTo(factAcct.getAmtSourceDr()) != 0) {
                    BigDecimal amount =
                            calcAmount(factAcct.getAmtSourceDr(), total, m_WriteOffAmt, precision);
                    if (amount.signum() != 0) {
                        if (m_IsSOTrx) {
                            fact.createLine(line, m_WriteOffAccount, as.getCurrencyId(), amount, null);
                            fact.createLine(line, taxAcct, as.getCurrencyId(), null, amount);
                        } else {
                            fact.createLine(
                                    line, m_WriteOffAccount, as.getCurrencyId(), amount.negate(), null);
                            fact.createLine(line, taxAcct, as.getCurrencyId(), null, amount.negate());
                        }
                    }
                }
                //	Original Tax is CR - need to correct it DR
                else {
                    BigDecimal amount =
                            calcAmount(factAcct.getAmtSourceCr(), total, m_WriteOffAmt, precision);
                    if (amount.signum() != 0) {
                        if (m_IsSOTrx) {
                            fact.createLine(line, taxAcct, as.getCurrencyId(), amount, null);
                            fact.createLine(line, m_WriteOffAccount, as.getCurrencyId(), null, amount);
                        } else {
                            fact.createLine(line, taxAcct, as.getCurrencyId(), amount.negate(), null);
                            fact.createLine(
                                    line, m_WriteOffAccount, as.getCurrencyId(), null, amount.negate());
                        }
                    }
                }
            } //	WriteOff
        } //	for all lines
        return true;
    } //	createEntries

    /**
     * Calc Amount tax / total * amt
     *
     * @param tax       tax
     * @param total     total
     * @param amt       reduction amt
     * @param precision precision
     * @return tax / total * amt
     */
    private BigDecimal calcAmount(BigDecimal tax, BigDecimal total, BigDecimal amt, int precision) {
        if (log.isLoggable(Level.FINE)) log.fine("Amt=" + amt + " - Total=" + total + ", Tax=" + tax);
        if (tax.signum() == 0 || total.signum() == 0 || amt.signum() == 0) return Env.ZERO;
        //
        BigDecimal multiplier = tax.divide(total, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal retValue = multiplier.multiply(amt);
        if (retValue.scale() > precision)
            retValue = retValue.setScale(precision, BigDecimal.ROUND_HALF_UP);
        if (log.isLoggable(Level.FINE))
            log.fine(retValue + " (Mult=" + multiplier + "(Prec=" + precision + ")");
        return retValue;
    } //	calcAmount
} //	Doc_AllocationTax
