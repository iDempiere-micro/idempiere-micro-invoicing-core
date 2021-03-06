package org.compiere.accounting;

import org.compiere.model.I_C_BankStatementLine;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

import static software.hsharp.core.util.DBKt.getSQLValue;

/**
 * Bank Statement Line
 *
 * @author Jorg Janke
 * @version $Id: DocLine_Bank.java,v 1.2 2006/07/30 00:53:33 jjanke Exp $
 */
public class DocLine_Bank extends DocLine {
    /**
     * Payment
     */
    private int m_C_Payment_ID = 0;
    private BigDecimal m_TrxAmt = Env.ZERO;
    private BigDecimal m_StmtAmt = Env.ZERO;
    private BigDecimal m_InterestAmt = Env.ZERO;

    /**
     * Constructor
     *
     * @param line statement line
     * @param doc  header
     */
    public DocLine_Bank(I_C_BankStatementLine line, Doc_BankStatement doc) {
        super(line, doc);
        m_C_Payment_ID = line.getPaymentId();
        //
        m_StmtAmt = line.getStmtAmt();
        m_InterestAmt = line.getInterestAmt();
        m_TrxAmt = line.getTrxAmt();
        //
        setDateDoc(line.getValutaDate());
        setDateAcct(doc.getDateAcct()); // adaxa-pb use statement date
        setBusinessPartnerId(line.getBusinessPartnerId());
    } //  DocLine_Bank

    /**
     * Get Payment
     *
     * @return C_Paymnet_ID
     */
    public int getPaymentId() {
        return m_C_Payment_ID;
    } //  getPaymentId

    /**
     * Get orgId
     *
     * @param payment if true get Org from payment
     * @return org
     */
    public int getOrgId(boolean payment) {
        if (payment && getPaymentId() != 0) {
            String sql = "SELECT AD_Org_ID FROM C_Payment WHERE C_Payment_ID=?";
            int id = getSQLValue(sql, getPaymentId());
            if (id > 0) return id;
        }
        return super.getOrgId();
    } //	getOrgId

    /**
     * Get Interest
     *
     * @return InterestAmount
     */
    public BigDecimal getInterestAmt() {
        return m_InterestAmt;
    } //  getInterestAmt

    /**
     * Get Statement
     *
     * @return Starement Amount
     */
    public BigDecimal getStmtAmt() {
        return m_StmtAmt;
    } //  getStrmtAmt

    /**
     * Get Transaction
     *
     * @return transaction amount
     */
    public BigDecimal getTrxAmt() {
        return m_TrxAmt;
    } //  getTrxAmt
} //  DocLine_Bank
