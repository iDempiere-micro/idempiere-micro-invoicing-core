package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MBPBankAccount;
import org.compiere.order.X_C_Order;
import org.idempiere.common.util.Env;

/**
 * Payment Print/Export model.
 *
 * @author Jorg Janke
 * @version $Id: MPaySelectionCheck.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class MPaySelectionCheck extends X_C_PaySelectionCheck {
    /**
     *
     */
    private static final long serialVersionUID = 2130445794890189020L;
    /**
     * Parent
     */
    private MPaySelection m_parent = null;
    /**
     * Payment Selection lines of this check
     */
    private MPaySelectionLine[] m_lines = null;

    /**
     * ************************************************************************ Constructor
     *
     * @param ctx                    context
     * @param C_PaySelectionCheck_ID C_PaySelectionCheck_ID
     */
    public MPaySelectionCheck(int C_PaySelectionCheck_ID) {
        super(C_PaySelectionCheck_ID);
        if (C_PaySelectionCheck_ID == 0) {
            //	setPaySelectionId (0);
            //	setBusinessPartnerId (0);
            //	setPaymentRule (null);
            setPayAmt(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setWriteOffAmt(Env.ZERO);
            setIsPrinted(false);
            setIsReceipt(false);
            setQty(0);
        }
    } //  MPaySelectionCheck

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MPaySelectionCheck(Row row) {
        super(row);
    } //  MPaySelectionCheck

    /**
     * Create from Line
     *
     * @param line        payment selection
     * @param PaymentRule payment rule
     */
    public MPaySelectionCheck(MPaySelectionLine line, String PaymentRule) {
        this(0);
        setClientOrg(line);
        setPaySelectionId(line.getPaySelectionId());
        int C_BPartner_ID = line.getInvoice().getBusinessPartnerId();
        setBusinessPartnerId(C_BPartner_ID);
        //
        if (X_C_Order.PAYMENTRULE_DirectDebit.equals(PaymentRule)) {
            MBPBankAccount[] bas = MBPBankAccount.getOfBPartner(C_BPartner_ID);
            for (int i = 0; i < bas.length; i++) {
                MBPBankAccount account = bas[i];
                if (account.isDirectDebit()) {
                    setBusinessPartnerBankAccountId(account.getBusinessPartnerBankAccountId());
                    break;
                }
            }
        } else if (X_C_Order.PAYMENTRULE_DirectDeposit.equals(PaymentRule)) {
            MBPBankAccount[] bas = MBPBankAccount.getOfBPartner(C_BPartner_ID);
            for (int i = 0; i < bas.length; i++) {
                MBPBankAccount account = bas[i];
                if (account.isDirectDeposit()) {
                    setBusinessPartnerBankAccountId(account.getBusinessPartnerBankAccountId());
                    break;
                }
            }
        }
        setPaymentRule(PaymentRule);
        //
        setIsReceipt(line.isSOTrx());
        setPayAmt(line.getPayAmt());
        setDiscountAmt(line.getDiscountAmt());
        setWriteOffAmt(line.getWriteOffAmt());
        setQty(1);
    } //	MPaySelectionCheck

    /**
     * Create from Pay Selection
     *
     * @param ps          payment selection
     * @param PaymentRule payment rule
     */
    public MPaySelectionCheck(MPaySelection ps, String PaymentRule) {
        this(0);
        setClientOrg(ps);
        setPaySelectionId(ps.getPaySelectionId());
        setPaymentRule(PaymentRule);
    } //	MPaySelectionCheck

    /**
     * Get Check for Payment
     *
     * @param ctx          context
     * @param C_Payment_ID id
     * @return pay selection check for payment or null
     */
    public static MPaySelectionCheck getOfPayment(int C_Payment_ID) {
        return MBasePaySelectionCheckKt.getCheckforPayment(C_Payment_ID);
    } //	getOfPayment

    /**
     * Delete Payment Selection when generated as Draft (Print Preview)
     *
     * @param ctx          context
     * @param C_Payment_ID id
     * @return
     */
    public static boolean deleteGeneratedDraft(int C_Payment_ID) {

        MPaySelectionCheck mpsc = MPaySelectionCheck.getOfPayment(C_Payment_ID);

        if (mpsc != null && mpsc.isGeneratedDraft()) {
            MPaySelection mps = new MPaySelection(mpsc.getPaySelectionId());
            MPaySelectionLine[] mpsl = mps.getLines(true);

            // Delete Pay Selection lines
            for (int i = 0; i < mpsl.length; i++) {
                if (!mpsl[i].delete(true)) return false;
            }
            // Delete Pay Selection Check
            if (!mpsc.delete(true)) return false;

            // Delete Pay Selection
            return mps.delete(true);
        }
        return true;
    }

    /**
     * Add Payment Selection Line
     *
     * @param line line
     */
    public void addLine(MPaySelectionLine line) {
        if (getBusinessPartnerId() != line.getInvoice().getBusinessPartnerId())
            throw new IllegalArgumentException("Line for different BPartner");
        //
        if (isReceipt() == line.isSOTrx()) {
            setPayAmt(getPayAmt().add(line.getPayAmt()));
            setDiscountAmt(getDiscountAmt().add(line.getDiscountAmt()));
            setWriteOffAmt(getWriteOffAmt().add(line.getWriteOffAmt()));
        } else {
            setPayAmt(getPayAmt().subtract(line.getPayAmt()));
            setDiscountAmt(getDiscountAmt().subtract(line.getDiscountAmt()));
            setWriteOffAmt(getWriteOffAmt().subtract(line.getWriteOffAmt()));
        }
        setQty(getQty() + 1);
    } //	addLine

    /**
     * Get Parent
     *
     * @return parent
     */
    public MPaySelection getParent() {
        if (m_parent == null)
            m_parent = new MPaySelection(getPaySelectionId());
        return m_parent;
    } //	getParent

    /**
     * Is this a valid Prepared Payment
     *
     * @return true if valid
     */
    public boolean isValid() {
        if (getBusinessPartnerBankAccountId() != 0) return true;
        return !isDirect();
    } //	isValid

    /**
     * Is this a direct Debit or Deposit
     *
     * @return true if direct
     */
    public boolean isDirect() {
        return (X_C_Order.PAYMENTRULE_DirectDeposit.equals(getPaymentRule())
                || X_C_Order.PAYMENTRULE_DirectDebit.equals(getPaymentRule()));
    } //	isDirect

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        String sb = "MPaymentCheck[" + getId() +
                "-" +
                getDocumentNo() +
                "-" +
                getPayAmt() +
                ",PaymetRule=" +
                getPaymentRule() +
                ",Qty=" +
                getQty() +
                "]";
        return sb;
    } //	toString

    /**
     * Get Payment Selection Lines of this check
     *
     * @param requery requery
     * @return array of payment selection lines
     */
    public MPaySelectionLine[] getPaySelectionLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        m_lines = MBasePaySelectionCheckKt.getPaySelectionLines(getPaySelectionCheckId());
        return m_lines;
    } //	getPaySelectionLines
} //  MPaySelectionCheck
