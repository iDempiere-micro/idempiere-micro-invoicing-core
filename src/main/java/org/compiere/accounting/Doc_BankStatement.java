package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bank.MBankAccount;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_BankStatementLine;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Post Invoice Documents.
 *
 * <pre>
 *  Table:              C_BankStatement (392)
 *  Document Types:     CMB
 *  </pre>
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @version $Id: Doc_Bank.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 * <p>FR [ 1840016 ] Avoid usage of clearing accounts - subject to
 * C_AcctSchema.IsPostIfClearingEqual Avoid posting if both accounts BankAsset and BankInTransit
 * are equal
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 */
public class Doc_BankStatement extends Doc {
    /**
     * Bank Account
     */
    protected int m_C_BankAccount_ID = 0;

    /**
     * Constructor
     *
     * @param as      accounting schema
     * @param rs      record
     */
    public Doc_BankStatement(MAcctSchema as, Row rs) {
        super(as, MBankStatement.class, rs, DOCTYPE_BankStatement);
    } //	Doc_Bank

    @Override
    protected IPODoc createNewInstance(Row rs) {
        return new MBankStatement(rs);
    }

    /**
     * Load Specific Document Details
     *
     * @return error message or null
     */
    protected String loadDocumentDetails() {
        MBankStatement bs = (MBankStatement) getPO();
        setDateDoc(bs.getStatementDate());
        setDateAcct(bs.getDateAcct());

        m_C_BankAccount_ID = bs.getBankAccountId();
        //	Amounts
        setAmount(AMTTYPE_Gross, bs.getStatementDifference());

        //  Set Bank Account Info (Currency)
        MBankAccount ba = MBankAccount.get(m_C_BankAccount_ID);
        setCurrencyId(ba.getCurrencyId());

        //	Contained Objects
        p_lines = loadLines(bs);
        if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length);
        return null;
    } //  loadDocumentDetails

    /**
     * Load Invoice Line.
     *
     * @param bs bank statement 4 amounts AMTTYPE_Payment AMTTYPE_Statement2 AMTTYPE_Charge
     *           AMTTYPE_Interest
     * @return DocLine Array
     */
    protected DocLine[] loadLines(MBankStatement bs) {
        ArrayList<DocLine> list = new ArrayList<DocLine>();
        I_C_BankStatementLine[] lines = bs.getLines(false);
        for (I_C_BankStatementLine line : lines) {
            if (line.isActive()) {
                DocLine_Bank docLine = new DocLine_Bank(line, this);
                list.add(docLine);
            }
        }

        //	Return Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);
        return dls;
    } //	loadLines

    /**
     * ************************************************************************ Get Source Currency
     * Balance - subtracts line amounts from total - no rounding
     *
     * @return positive amount, if total invoice is bigger than lines
     */
    public BigDecimal getBalance() {
        BigDecimal retValue = Env.ZERO;
        StringBuilder sb = new StringBuilder(" [");
        //  Total
        retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
        sb.append(getAmount(Doc.AMTTYPE_Gross));
        //  - Lines
        for (DocLine p_line : p_lines) {
            BigDecimal lineBalance = ((DocLine_Bank) p_line).getStmtAmt();
            retValue = retValue.subtract(lineBalance);
            sb.append("-").append(lineBalance);
        }
        sb.append("]");
        //
        if (log.isLoggable(Level.FINE)) log.fine(toString() + " Balance=" + retValue + sb.toString());
        return retValue;
    } //  getBalance

    /**
     * Create Facts (the accounting logic) for CMB.
     *
     * <pre>
     *      BankAsset       DR      CR  (Statement)
     *      BankInTransit   DR      CR              (Payment)
     *      Charge          DR          (Charge)
     *      Interest        DR      CR  (Interest)
     *  </pre>
     *
     * @param as accounting schema
     * @return Fact
     */
    public ArrayList<IFact> createFacts(AccountingSchema as) {
        //  create Fact Header
        Fact fact = new Fact(this, as, Fact.POST_Actual);
        // boolean isInterOrg = isInterOrg(as);

        //  Header -- there may be different currency amounts

        FactLine fl = null;
        int AD_Org_ID = getBankAccountOrganizationId(); // 	Bank Account Org
        //  Lines
        for (int i = 0; i < p_lines.length; i++) {
            DocLine_Bank line = (DocLine_Bank) p_lines[i];
            int C_BPartner_ID = line.getBusinessPartnerId();

            // Avoid usage of clearing accounts
            // If both accounts BankAsset and BankInTransit are equal
            // then remove the posting

            MAccount acct_bank_asset = getAccount(Doc.ACCTTYPE_BankAsset, as);
            MAccount acct_bank_in_transit = getAccount(Doc.ACCTTYPE_BankInTransit, as);

            // if ((!as.isPostIfClearingEqual()) && acct_bank_asset.equals(acct_bank_in_transit) &&
            // (!isInterOrg)) {
            // don't validate interorg on banks for this - normally banks are balanced by orgs
            if ((!as.isPostIfClearingEqual()) && acct_bank_asset.equals(acct_bank_in_transit)) {
                // Not using clearing accounts
                // just post the difference (if any)

                BigDecimal amt_stmt_minus_trx = line.getStmtAmt().subtract(line.getTrxAmt());
                if (amt_stmt_minus_trx.compareTo(Env.ZERO) != 0) {

                    //  BankAsset       DR      CR  (Statement minus Payment)
                    fl =
                            fact.createLine(
                                    line,
                                    getAccount(Doc.ACCTTYPE_BankAsset, as),
                                    line.getCurrencyId(),
                                    amt_stmt_minus_trx);
                    if (fl != null && AD_Org_ID != 0) fl.setOrgId(AD_Org_ID);
                    if (fl != null && C_BPartner_ID != 0) fl.setBusinessPartnerId(C_BPartner_ID);
                }

            } else {

                // Normal Adempiere behavior -- unchanged if using clearing accounts

                //  BankAsset       DR      CR  (Statement)
                fl =
                        fact.createLine(
                                line,
                                getAccount(Doc.ACCTTYPE_BankAsset, as),
                                line.getCurrencyId(),
                                line.getStmtAmt());
                if (fl != null && AD_Org_ID != 0) fl.setOrgId(AD_Org_ID);
                if (fl != null && C_BPartner_ID != 0) fl.setBusinessPartnerId(C_BPartner_ID);

                //  BankInTransit   DR      CR              (Payment)
                fl =
                        fact.createLine(
                                line,
                                getAccount(Doc.ACCTTYPE_BankInTransit, as),
                                line.getCurrencyId(),
                                line.getTrxAmt().negate());
                if (fl != null) {
                    if (C_BPartner_ID != 0) fl.setBusinessPartnerId(C_BPartner_ID);
                    if (AD_Org_ID != 0) fl.setOrgId(AD_Org_ID);
                    else fl.setOrgId(line.getOrgId(true)); // from payment
                }
            }
            // End Avoid usage of clearing accounts

            //  Charge          DR          (Charge)
            if (line.getChargeAmt().compareTo(Env.ZERO) > 0) {
                fl =
                        fact.createLine(
                                line,
                                line.getChargeAccount(as),
                                line.getCurrencyId(),
                                null,
                                line.getChargeAmt());
            } else {
                fl =
                        fact.createLine(
                                line,
                                line.getChargeAccount(as),
                                line.getCurrencyId(),
                                line.getChargeAmt().negate(),
                                null);
            }
            if (fl != null && C_BPartner_ID != 0) fl.setBusinessPartnerId(C_BPartner_ID);

            //  Interest        DR      CR  (Interest)
            if (line.getInterestAmt().signum() < 0)
                fl =
                        fact.createLine(
                                line,
                                getAccount(Doc.ACCTTYPE_InterestExp, as),
                                getAccount(Doc.ACCTTYPE_InterestExp, as),
                                line.getCurrencyId(),
                                line.getInterestAmt().negate());
            else
                fl =
                        fact.createLine(
                                line,
                                getAccount(Doc.ACCTTYPE_InterestRev, as),
                                getAccount(Doc.ACCTTYPE_InterestRev, as),
                                line.getCurrencyId(),
                                line.getInterestAmt().negate());
            if (fl != null && C_BPartner_ID != 0) fl.setBusinessPartnerId(C_BPartner_ID);
            //
            //	fact.createTaxCorrection();
        }
        //
        ArrayList<IFact> facts = new ArrayList<IFact>();
        facts.add(fact);
        return facts;
    } //  createFact

    /**
     * Verify if the posting involves two or more organizations
     *
     * @return true if there are more than one org involved on the posting private boolean
     *     isInterOrg(MAcctSchema as) { MAcctSchemaElement elementorg =
     *     as.getAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_Organization); if (elementorg ==
     *     null || !elementorg.isBalanced()) { // no org element or not need to be balanced return
     *     false; }
     *     <p>if (p_lines.length <= 0) { // no lines return false; }
     *     <p>int startorg = getBankAccountOrganizationId(); if (startorg == 0) startorg =
     *     p_lines[0]. getOrgId(); // validate if the allocation involves more than one org for
     *     (int i = 0; i < p_lines.length; i++) { if (p_lines[i]. getOrgId() != startorg) return
     *     true; }
     *     <p>return false; }
     */

    /**
     * Get orgId from Bank Account
     *
     * @return orgId or 0
     */
    protected int getBankAccountOrganizationId() {
        if (m_C_BankAccount_ID == 0) return 0;
        //
        MBankAccount ba = MBankAccount.get(m_C_BankAccount_ID);
        return ba.getOrgId();
    } //	getBank_Org_ID
} //  Doc_Bank
