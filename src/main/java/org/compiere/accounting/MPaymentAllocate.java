package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.I_C_PaymentAllocate;
import org.compiere.model.TypedQuery;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payment Allocate Model
 *
 * @author Jorg Janke
 * @version $Id: MPaymentAllocate.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MPaymentAllocate extends X_C_PaymentAllocate {
    /**
     *
     */
    private static final long serialVersionUID = 2894385378672375131L;
    /**
     * The Invoice
     */
    private MInvoice m_invoice = null;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_PaymentAllocate_ID id
     */
    public MPaymentAllocate(int C_PaymentAllocate_ID) {
        super(C_PaymentAllocate_ID);
        if (C_PaymentAllocate_ID == 0) {
            setAmount(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setOverUnderAmt(Env.ZERO);
            setWriteOffAmt(Env.ZERO);
            setInvoiceAmt(Env.ZERO);
        }
    } //	MPaymentAllocate

    /**
     * Load Constructor
     *
     */
    public MPaymentAllocate(Row row) {
        super(row);
    } //	MPaymentAllocate

    /**
     * Get active Payment Allocation of Payment
     *
     * @param parent payment
     * @return array of allocations
     */
    public static I_C_PaymentAllocate[] get(MPayment parent) {
        final String whereClause = "C_Payment_ID=?";
        TypedQuery<I_C_PaymentAllocate> query =
                MBaseTableKt.getTable(I_C_PaymentAllocate.Table_ID)
                        .createQuery(whereClause);
        query.setParameters(parent.getPaymentId()).setOnlyActiveRecords(true);
        List<I_C_PaymentAllocate> list = query.list();
        return list.toArray(new I_C_PaymentAllocate[0]);
    } //	get

    /**
     * Set C_Invoice_ID
     *
     * @param C_Invoice_ID id
     */
    public void setInvoiceId(int C_Invoice_ID) {
        super.setInvoiceId(C_Invoice_ID);
        m_invoice = null;
    } //	setInvoiceId

    /**
     * Get Invoice
     *
     * @return invoice
     */
    public MInvoice getInvoice() {
        if (m_invoice == null && getInvoiceId() != 0)
            m_invoice = new MInvoice(null, getInvoiceId());
        return m_invoice;
    } //	getInvoice

    /**
     * Get BPartner of Invoice
     *
     * @return bp
     */
    public int getBusinessPartnerId() {
        if (m_invoice == null) getInvoice();
        if (m_invoice == null) return 0;
        return m_invoice.getBusinessPartnerId();
    } //	getBusinessPartnerId

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        MPayment payment = new MPayment(getPaymentId());
        if ((newRecord || isValueChanged("C_Invoice_ID"))
                && (payment.getChargeId() != 0
                || payment.getInvoiceId() != 0
                || payment.getOrderId() != 0)) {
            log.saveError("PaymentIsAllocated", "");
            return false;
        }

        BigDecimal check =
                getAmount().add(getDiscountAmt()).add(getWriteOffAmt()).add(getOverUnderAmt());
        if (check.compareTo(getInvoiceAmt()) != 0) {
            log.saveError(
                    "Error",
                    MsgKt.parseTranslation(
                            "@InvoiceAmt@(" + getInvoiceAmt() + ") <> @Totals@(" + check + ")"));
            return false;
        }

        //	Org
        if (newRecord || isValueChanged("C_Invoice_ID")) {
            getInvoice();
            if (m_invoice != null) setOrgId(m_invoice.getOrgId());
        }

        return true;
    } //	beforeSave
} //	MPaymentAllocate
