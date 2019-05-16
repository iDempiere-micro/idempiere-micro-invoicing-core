package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MInvoice;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

import static software.hsharp.core.util.DBKt.executeUpdate;

/**
 * Payment Selection Line Model
 *
 * @author Jorg Janke
 * @version $Id: MPaySelectionLine.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MPaySelectionLine extends X_C_PaySelectionLine {
    /**
     *
     */
    private static final long serialVersionUID = -1880961891234637133L;
    /**
     * Invoice
     */
    private MInvoice m_invoice = null;

    /**
     * Standard Constructor
     *
     * @param ctx                   context
     * @param C_PaySelectionLine_ID id
     */
    public MPaySelectionLine(int C_PaySelectionLine_ID) {
        super(C_PaySelectionLine_ID);
        if (C_PaySelectionLine_ID == 0) {
            setIsSOTrx(false);
            setOpenAmt(Env.ZERO);
            setPayAmt(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setWriteOffAmt(Env.ZERO);
            setDifferenceAmt(Env.ZERO);
            setIsManual(false);
        }
    } //	MPaySelectionLine

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MPaySelectionLine(Row row) {
        super(row);
    } //	MPaySelectionLine

    /**
     * Parent Constructor
     *
     * @param ps          parent
     * @param Line        line
     * @param PaymentRule payment rule
     */
    public MPaySelectionLine(MPaySelection ps, int Line, String PaymentRule) {
        this(0);
        setClientOrg(ps);
        setPaySelectionId(ps.getPaySelectionId());
        setLine(Line);
        setPaymentRule(PaymentRule);
    } //	MPaySelectionLine

    /**
     * Set Invoice Info
     *
     * @param C_Invoice_ID invoice
     * @param isSOTrx      sales trx
     * @param PayAmt       payment
     * @param OpenAmt      open
     * @param DiscountAmt  discount
     * @param WriteOffAmt  writeoff
     */
    public void setInvoice(
            int C_Invoice_ID,
            boolean isSOTrx,
            BigDecimal OpenAmt,
            BigDecimal PayAmt,
            BigDecimal DiscountAmt,
            BigDecimal WriteOffAmt) {
        setInvoiceId(C_Invoice_ID);
        setIsSOTrx(isSOTrx);
        setOpenAmt(OpenAmt);
        setPayAmt(PayAmt);
        setDiscountAmt(DiscountAmt);
        setWriteOffAmt(WriteOffAmt);
        setDifferenceAmt(OpenAmt.subtract(PayAmt).subtract(DiscountAmt).subtract(WriteOffAmt));
    } //	setInvoice

    /**
     * Get Invoice
     *
     * @return invoice
     */
    public MInvoice getInvoice() {
        if (m_invoice == null) m_invoice = new MInvoice(null, getInvoiceId());
        return m_invoice;
    } //	getInvoice

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        setDifferenceAmt(
                getOpenAmt().subtract(getPayAmt()).subtract(getDiscountAmt()).subtract(getWriteOffAmt()));
        return true;
    } //	beforeSave

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;
        setHeader();
        return success;
    } //	afterSave

    /**
     * After Delete
     *
     * @param success success
     * @return sucess
     */
    protected boolean afterDelete(boolean success) {
        if (!success) return success;
        setHeader();
        return success;
    } //	afterDelete

    /**
     * Recalculate Header Sum
     */
    private void setHeader() {
        //	Update Header
        String sql =
                "UPDATE C_PaySelection ps "
                        + "SET TotalAmt = (SELECT COALESCE(SUM(psl.PayAmt),0) "
                        + "FROM C_PaySelectionLine psl "
                        + "WHERE ps.C_PaySelection_ID=psl.C_PaySelection_ID AND psl.IsActive='Y') "
                        + "WHERE C_PaySelection_ID="
                        + getPaySelectionId();
        executeUpdate(sql);
    } //	setHeader

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MPaySelectionLine[" + getId() +
                ",C_Invoice_ID=" +
                getInvoiceId() +
                ",PayAmt=" +
                getPayAmt() +
                ",DifferenceAmt=" +
                getDifferenceAmt() +
                "]";
    } //	toString
} //	MPaySelectionLine
