package org.compiere.accounting;

import java.math.BigDecimal;

import org.idempiere.common.util.Env;

/**
 * Bank Statement Line
 *
 * @author Jorg Janke
 * @version $Id: DocLine_Bank.java,v 1.2 2006/07/30 00:53:33 jjanke Exp $
 */
public class DocLine_Bank extends DocLine {
  /**
   * Constructor
   *
   * @param line statement line
   * @param doc header
   */
  public DocLine_Bank(MBankStatementLine line, Doc_BankStatement doc) {
    super(line, doc);
    m_C_Payment_ID = line.getC_Payment_ID();
    m_IsReversal = line.isReversal();
    //
    m_StmtAmt = line.getStmtAmt();
    m_InterestAmt = line.getInterestAmt();
    m_TrxAmt = line.getTrxAmt();
    //
    setDateDoc(line.getValutaDate());
    setDateAcct(doc.getDateAcct()); // adaxa-pb use statement date
    setC_BPartner_ID(line.getC_BPartner_ID());
  } //  DocLine_Bank

  /** Reversal Flag */
  private boolean m_IsReversal = false;
  /** Payment */
  private int m_C_Payment_ID = 0;

  private BigDecimal m_TrxAmt = Env.ZERO;
  private BigDecimal m_StmtAmt = Env.ZERO;
  private BigDecimal m_InterestAmt = Env.ZERO;

  /**
   * Get Payment
   *
   * @return C_Paymnet_ID
   */
  public int getC_Payment_ID() {
    return m_C_Payment_ID;
  } //  getC_Payment_ID

  /**
   * Get AD_Org_ID
   *
   * @param payment if true get Org from payment
   * @return org
   */
  public int getOrgId(boolean payment) {
    if (payment && getC_Payment_ID() != 0) {
      String sql = "SELECT AD_Org_ID FROM C_Payment WHERE C_Payment_ID=?";
      int id = getSQLValue(null, sql, getC_Payment_ID());
      if (id > 0) return id;
    }
    return super. getOrgId();
  } //	getAD_Org_ID

  /**
   * Is Reversal
   *
   * @return true if reversal
   */
  public boolean isReversal() {
    return m_IsReversal;
  } //  isReversal

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
