package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.order.MPaySchedule;
import org.compiere.orm.TimeUtil;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Invoice Payment Schedule Model
 *
 * @author Jorg Janke
 * @version $Id: MInvoicePaySchedule.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MInvoicePaySchedule extends X_C_InvoicePaySchedule {
    /**
     *
     */
    private static final long serialVersionUID = 4613382619117842586L;
    /**
     * Parent
     */
    private MInvoice m_parent = null;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_InvoicePaySchedule_ID id
     */
    public MInvoicePaySchedule(int C_InvoicePaySchedule_ID) {
        super(C_InvoicePaySchedule_ID);
        if (C_InvoicePaySchedule_ID == 0) {
            setIsValid(false);
        }
    } //	MInvoicePaySchedule

    /**
     * Load Constructor
     */
    public MInvoicePaySchedule(Row row) {
        super(row);
    } //	MInvoicePaySchedule

    /**
     * Parent Constructor
     *
     * @param invoice     invoice
     * @param paySchedule payment schedule
     */
    public MInvoicePaySchedule(MInvoice invoice, MPaySchedule paySchedule) {
        super(0);
        m_parent = invoice;
        setClientOrg(invoice);
        setInvoiceId(invoice.getInvoiceId());
        setPayScheduleId(paySchedule.getPayScheduleId());

        //	Amounts
        int scale = MCurrencyKt.getCurrencyStdPrecision(invoice.getCurrencyId());
        BigDecimal due = invoice.getGrandTotal();
        if (due.compareTo(Env.ZERO) == 0) {
            setDueAmt(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setIsValid(false);
        } else {
            due =
                    due.multiply(paySchedule.getPercentage())
                            .divide(Env.ONEHUNDRED, scale, BigDecimal.ROUND_HALF_UP);
            setDueAmt(due);
            BigDecimal discount =
                    due.multiply(paySchedule.getDiscount())
                            .divide(Env.ONEHUNDRED, scale, BigDecimal.ROUND_HALF_UP);
            setDiscountAmt(discount);
            setIsValid(true);
        }

        //	Dates
        Timestamp dueDate = TimeUtil.addDays(invoice.getDateInvoiced(), paySchedule.getNetDays());
        setDueDate(dueDate);
        Timestamp discountDate =
                TimeUtil.addDays(invoice.getDateInvoiced(), paySchedule.getDiscountDays());
        setDiscountDate(discountDate);
    } //	MInvoicePaySchedule

    /**
     * Get Payment Schedule of the invoice
     *
     * @param C_Invoice_ID            invoice id (direct)
     * @param C_InvoicePaySchedule_ID id (indirect)
     * @return array of schedule
     */
    public static MInvoicePaySchedule[] getInvoicePaySchedule(
            int C_Invoice_ID, int C_InvoicePaySchedule_ID) {
        return MBaseInvoicePayScheduleKt.getInvoicePaySchedule(C_Invoice_ID, C_InvoicePaySchedule_ID);
    } //	getSchedule

    /**
     * @return Returns the parent.
     */
    public MInvoice getParent() {
        if (m_parent == null) m_parent = new MInvoice(null, getInvoiceId());
        return m_parent;
    } //	getParent

    /**
     * @param parent The parent to set.
     */
    public void setParent(MInvoice parent) {
        m_parent = parent;
    } //	setParent

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MInvoicePaySchedule[" + getId() +
                "-Due=" +
                getDueDate() +
                "/" +
                getDueAmt() +
                ";Discount=" +
                getDiscountDate() +
                "/" +
                getDiscountAmt() +
                "]";
    } //	toString

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (isValueChanged("DueAmt")) {
            log.fine("beforeSave");
            setIsValid(false);
        }
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
        if (isValueChanged("DueAmt") || isValueChanged("IsActive")) {
            log.fine("afterSave");
            getParent();
            m_parent.validatePaySchedule();
            m_parent.saveEx();
        }
        return success;
    } //	afterSave

    @Override
    protected boolean afterDelete(boolean success) {
        if (!success) return success;
        log.fine("afterDelete");
        getParent();
        m_parent.validatePaySchedule();
        m_parent.saveEx();
        return success;
    }
} //	MInvoicePaySchedule
