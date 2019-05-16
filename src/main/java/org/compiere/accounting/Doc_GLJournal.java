package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_GL_JournalLine;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Post GL Journal Documents.
 *
 * <pre>
 *  Table:              GL_Journal (224)
 *  Document Types:     GLJ
 *  </pre>
 *
 * @author Jorg Janke
 * @version $Id: Doc_GLJournal.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_GLJournal extends Doc {
    /**
     * Posting Type
     */
    protected String m_PostingType = null;
    protected int m_C_AcctSchema_ID = 0;

    /**
     * Constructor
     *
     * @param as      accounting schema
     * @param rs      record
     */
    public Doc_GLJournal(MAcctSchema as, Row rs) {
        super(as, MJournal.class, rs, null);
    } //	Doc_GL_Journal

    @Override
    protected IPODoc createNewInstance(Row rs) {
        return new MJournal(rs);
    }

    /**
     * Load Specific Document Details
     *
     * @return error message or null
     */
    protected String loadDocumentDetails() {
        MJournal journal = (MJournal) getPO();
        m_PostingType = journal.getPostingType();
        m_C_AcctSchema_ID = journal.getAccountingSchemaId();

        //	Contained Objects
        p_lines = loadLines(journal);
        if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length);
        return null;
    } //  loadDocumentDetails

    /**
     * Load Invoice Line
     *
     * @param journal journal
     * @return DocLine Array
     */
    protected DocLine[] loadLines(MJournal journal) {
        ArrayList<DocLine> list = new ArrayList<>();
        I_GL_JournalLine[] lines = journal.getLines(false);
        for (I_GL_JournalLine line : lines) {
            DocLine docLine = new DocLine(line, this);
            // -- Quantity
            docLine.setQty(line.getQty(), false);
            //  --  Source Amounts
            docLine.setAmount(line.getAmtSourceDr(), line.getAmtSourceCr());
            //  --  Converted Amounts
            docLine.setConvertedAmt(m_C_AcctSchema_ID, line.getAmtAcctDr(), line.getAmtAcctCr());
            //  --  Account
            I_C_ValidCombination account = line.getAccount_Combi();
            docLine.setAccount(account);
            //	--	Organization of Line was set to Org of Account
            list.add(docLine);
        }
        //	Return Array
        int size = list.size();
        DocLine[] dls = new DocLine[size];
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
        BigDecimal retValue = Env.ZERO;
        StringBuilder sb = new StringBuilder(" [");
        //  Lines
        for (DocLine p_line : p_lines) {
            retValue = retValue.add(p_line.getAmtSource());
            sb.append("+").append(p_line.getAmtSource());
        }
        sb.append("]");
        //
        if (log.isLoggable(Level.FINE)) log.fine(toString() + " Balance=" + retValue + sb.toString());
        return retValue;
    } //  getBalance

    /**
     * Create Facts (the accounting logic) for GLJ. (only for the accounting scheme, it was created)
     *
     * <pre>
     *      account     DR          CR
     *  </pre>
     *
     * @param as acct schema
     * @return Fact
     */
    public ArrayList<IFact> createFacts(AccountingSchema as) {
        ArrayList<IFact> facts = new ArrayList<>();
        //	Other Acct Schema
        if (as.getAccountingSchemaId() != m_C_AcctSchema_ID) return facts;

        //  create Fact Header
        Fact fact = new Fact(this, as, m_PostingType);

        //  GLJ
        if (getDocumentType().equals(DOCTYPE_GLJournal)) {
            //  account     DR      CR
            for (DocLine p_line : p_lines) {
                if (p_line.getAccountingSchemaId() == as.getAccountingSchemaId()) {
                    @SuppressWarnings("unused")
                    FactLine line =
                            fact.createLine(
                                    p_line,
                                    p_line.getAccount(),
                                    getCurrencyId(),
                                    p_line.getAmtSourceDr(),
                                    p_line.getAmtSourceCr());
                }
            } //	for all lines
        } else {
            p_Error = "DocumentType unknown: " + getDocumentType();
            log.log(Level.SEVERE, p_Error);
            fact = null;
        }
        //
        facts.add(fact);
        return facts;
    } //  createFact
} //  Doc_GLJournal
