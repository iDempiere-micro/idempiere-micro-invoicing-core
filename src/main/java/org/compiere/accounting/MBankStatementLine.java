package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static software.hsharp.core.orm.POKt.I_ZERO;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;

/**
 * Bank Statement Line Model
 *
 * @author Eldir Tomassen/Jorg Janke
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * <li>BF [ 1896880 ] Unlink Payment if TrxAmt is zero
 * <li>BF [ 1896885 ] BS Line: don't update header if after save/delete fails
 * @version $Id: MBankStatementLine.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 * <p>Carlos Ruiz - globalqss - integrate bug fixing from Teo Sarca [ 1619076 ] Bank statement's
 * StatementDifference becames NULL
 */
public class MBankStatementLine extends X_C_BankStatementLine implements IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = 1914411222159254809L;
    /**
     * Parent
     */
    private MBankStatement m_parent = null;

    /**
     * Standard Constructor
     *
     * @param C_BankStatementLine_ID id
     */
    public MBankStatementLine(int C_BankStatementLine_ID) {
        super(C_BankStatementLine_ID);
        if (C_BankStatementLine_ID == 0) {
            setStmtAmt(Env.ZERO);
            setTrxAmt(Env.ZERO);
            setInterestAmt(Env.ZERO);
            setChargeAmt(Env.ZERO);
            setIsReversal(false);
        }
    } //	MBankStatementLine

    /**
     * Load Constructor
     */
    public MBankStatementLine(Row row) {
        super(row);
    } //	MBankStatementLine

    /**
     * Parent Constructor
     *
     * @param statement Bank Statement that the line is part of
     */
    public MBankStatementLine(MBankStatement statement) {
        this(0);
        setClientOrg(statement);
        setBankStatementId(statement.getBankStatementId());
        setStatementLineDate(statement.getStatementDate());
    } //	MBankStatementLine

    /**
     * Parent Constructor
     *
     * @param statement Bank Statement that the line is part of
     * @param lineNo    position of the line within the statement
     */
    public MBankStatementLine(MBankStatement statement, int lineNo) {
        this(statement);
        setLine(lineNo);
    } //	MBankStatementLine

    /**
     * Set Statement Line Date and all other dates (Valuta, Acct)
     *
     * @param StatementLineDate date
     */
    public void setStatementLineDate(Timestamp StatementLineDate) {
        super.setStatementLineDate(StatementLineDate);
        setValutaDate(StatementLineDate);
        setDateAcct(StatementLineDate);
    } //	setStatementLineDate

    /**
     * Set Payment
     *
     * @param payment payment
     */
    public void setPayment(MPayment payment) {
        setPaymentId(payment.getPaymentId());
        setCurrencyId(payment.getCurrencyId());
        //
        BigDecimal amt = payment.getPayAmt(true);
        BigDecimal chargeAmt = getChargeAmt();
        if (chargeAmt == null) chargeAmt = Env.ZERO;
        BigDecimal interestAmt = getInterestAmt();
        if (interestAmt == null) interestAmt = Env.ZERO;
        setTrxAmt(amt);
        setStmtAmt(amt.add(chargeAmt).add(interestAmt));
        //
        setDescription(payment.getDescription());
    } //	setPayment

    /**
     * Add to Description
     *
     * @param description text
     */
    public void addDescription(String description) {
        String desc = getDescription();
        if (desc == null) setDescription(description);
        else {
            StringBuilder msgsd = new StringBuilder(desc).append(" | ").append(description);
            setDescription(msgsd.toString());
        }
    } //	addDescription

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (newRecord && getParent().isComplete()) {
            log.saveError("ParentComplete", MsgKt.translate("C_BankStatementLine"));
            return false;
        }
        //	Calculate Charge = Statement - trx - Interest
        BigDecimal amt = getStmtAmt();
        amt = amt.subtract(getTrxAmt());
        amt = amt.subtract(getInterestAmt());
        if (amt.compareTo(getChargeAmt()) != 0) setChargeAmt(amt);
        //
        if (getChargeAmt().signum() != 0 && getChargeId() == 0) {
            log.saveError("FillMandatory", MsgKt.getElementTranslation("C_Charge_ID"));
            return false;
        }
        // Un-link Payment if TrxAmt is zero - teo_sarca BF [ 1896880 ]
        if (getTrxAmt().signum() == 0 && getPaymentId() > 0) {
            setPaymentId(I_ZERO);
            setInvoiceId(I_ZERO);
        }
        //	Set Line No
        if (getLine() == 0) {
            String sql =
                    "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM C_BankStatementLine WHERE C_BankStatement_ID=?";
            int ii = getSQLValue(sql, getBankStatementId());
            setLine(ii);
        }

        //	Set References
        if (getPaymentId() != 0 && getBusinessPartnerId() == 0) {
            MPayment payment = new MPayment(getPaymentId());
            setBusinessPartnerId(payment.getBusinessPartnerId());
            if (payment.getInvoiceId() != 0) setInvoiceId(payment.getInvoiceId());
        }
        if (getInvoiceId() != 0 && getBusinessPartnerId() == 0) {
            MInvoice invoice = new MInvoice(null, getInvoiceId());
            setBusinessPartnerId(invoice.getBusinessPartnerId());
        }

        return true;
    } //	beforeSave

    /**
     * Get Parent
     *
     * @return parent
     */
    public MBankStatement getParent() {
        if (m_parent == null)
            m_parent = new MBankStatement(getBankStatementId());
        return m_parent;
    } //	getParent

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;
        return updateHeader();
    } //	afterSave

    /**
     * After Delete
     *
     * @param success success
     * @return success
     */
    protected boolean afterDelete(boolean success) {
        if (!success) return success;
        return updateHeader();
    } //	afterSave

    /**
     * Update Header
     */
    private boolean updateHeader() {
        StringBuilder sql =
                new StringBuilder("UPDATE C_BankStatement bs")
                        .append(
                                " SET StatementDifference=(SELECT COALESCE(SUM(StmtAmt),0) FROM C_BankStatementLine bsl ")
                        .append("WHERE bsl.C_BankStatement_ID=bs.C_BankStatement_ID AND bsl.IsActive='Y') ")
                        .append("WHERE C_BankStatement_ID=")
                        .append(getBankStatementId());
        int no = executeUpdate(sql.toString());
        if (no != 1) {
            log.warning("StatementDifference #" + no);
            return false;
        }
        sql =
                new StringBuilder("UPDATE C_BankStatement bs")
                        .append(" SET EndingBalance=BeginningBalance+StatementDifference ")
                        .append("WHERE C_BankStatement_ID=")
                        .append(getBankStatementId());
        no = executeUpdate(sql.toString());
        if (no != 1) {
            log.warning("Balance #" + no);
            return false;
        }
        return true;
    } //	updateHeader

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }
} //	MBankStatementLine
