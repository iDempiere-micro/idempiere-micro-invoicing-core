package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.IDocLine;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;

/**
 * Allocation Line Model
 *
 * @author Jorg Janke
 * @version $Id: MAllocationLine.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MAllocationLine extends X_C_AllocationLine implements IDocLine {
    /**
     *
     */
    private static final long serialVersionUID = 5532305715886380749L;
    /**
     * Static Logger
     */
    @SuppressWarnings("unused")
    private static CLogger s_log = CLogger.getCLogger(MAllocationLine.class);
    /**
     * Invoice info
     */
    private MInvoice m_invoice = null;
    /**
     * Allocation Header
     */
    private MAllocationHdr m_parent = null;

    /**
     * Standard Constructor
     *
     * @param C_AllocationLine_ID id
     */
    public MAllocationLine(int C_AllocationLine_ID) {
        super(C_AllocationLine_ID);
        if (C_AllocationLine_ID == 0) {
            setAmount(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setWriteOffAmt(Env.ZERO);
            setOverUnderAmt(Env.ZERO);
        }
    } //	MAllocationLine

    /**
     * Load Constructor
     *
     */
    public MAllocationLine(Row row) {
        super(row);
    } //	MAllocationLine

    /**
     * Parent Constructor
     *
     * @param parent parent
     */
    public MAllocationLine(MAllocationHdr parent) {
        this(0);
        setClientOrg(parent);
        setPaymentAllocationHeaderId(parent.getPaymentAllocationHeaderId());
        m_parent = parent;
    } //	MAllocationLine

    /**
     * Parent Constructor
     *
     * @param parent       parent
     * @param Amount       amount
     * @param DiscountAmt  optional discount
     * @param WriteOffAmt  optional write off
     * @param OverUnderAmt over/underpayment
     */
    public MAllocationLine(
            MAllocationHdr parent,
            BigDecimal Amount,
            BigDecimal DiscountAmt,
            BigDecimal WriteOffAmt,
            BigDecimal OverUnderAmt) {
        this(parent);
        setAmount(Amount);
        setDiscountAmt(DiscountAmt == null ? Env.ZERO : DiscountAmt);
        setWriteOffAmt(WriteOffAmt == null ? Env.ZERO : WriteOffAmt);
        setOverUnderAmt(OverUnderAmt == null ? Env.ZERO : OverUnderAmt);
    } //	MAllocationLine

    /**
     * Get Parent
     *
     * @return parent
     */
    public MAllocationHdr getParent() {
        if (m_parent == null)
            m_parent = new MAllocationHdr(null, getPaymentAllocationHeaderId());
        return m_parent;
    } //	getParent

    /**
     * Set Parent
     *
     * @param parent parent
     */
    protected void setParent(MAllocationHdr parent) {
        m_parent = parent;
    } //	setParent

    /**
     * Set Document Info
     *
     * @param C_BPartner_ID partner
     * @param C_Order_ID    order
     * @param C_Invoice_ID  invoice
     */
    public void setDocInfo(int C_BPartner_ID, int C_Order_ID, int C_Invoice_ID) {
        setBusinessPartnerId(C_BPartner_ID);
        setOrderId(C_Order_ID);
        setInvoiceId(C_Invoice_ID);
    } //	setDocInfo

    /**
     * Set Payment Info
     *
     * @param C_Payment_ID  payment
     * @param C_CashLine_ID cash line
     */
    public void setPaymentInfo(int C_Payment_ID, int C_CashLine_ID) {
        if (C_Payment_ID != 0) setPaymentId(C_Payment_ID);
        if (C_CashLine_ID != 0) setCashLineId(C_CashLine_ID);
    } //	setPaymentInfo

    /**
     * Get Invoice
     *
     * @return invoice or null
     */
    public MInvoice getInvoice() {
        if (m_invoice == null && getInvoiceId() != 0)
            m_invoice = new MInvoice(null, getInvoiceId());
        return m_invoice;
    } //	getInvoice

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord
     * @return save
     */
    protected boolean beforeSave(boolean newRecord) {
        if (newRecord && getParent().isComplete()) {
            log.saveError("ParentComplete", MsgKt.translate("C_AllocationLine"));
            return false;
        }
        if (!newRecord && (isValueChanged("C_BPartner_ID") || isValueChanged("C_Invoice_ID"))) {
            log.severe("Cannot Change Business Partner or Invoice");
            return false;
        }

        //	Set BPartner/Order from Invoice
        if (getBusinessPartnerId() == 0 && getInvoice() != null)
            setBusinessPartnerId(getInvoice().getBusinessPartnerId());
        if (getOrderId() == 0 && getInvoice() != null) setOrderId(getInvoice().getOrderId());
        //
        return true;
    } //	beforeSave

    /**
     * Before Delete
     *
     * @return true if reversed
     */
    protected boolean beforeDelete() {
        setIsActive(false);
        processIt(true);
        return true;
    } //	beforeDelete

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MAllocationLine[");
        sb.append(getId());
        if (getPaymentId() != 0) sb.append(",C_Payment_ID=").append(getPaymentId());
        if (getCashLineId() != 0) sb.append(",C_CashLine_ID=").append(getCashLineId());
        if (getInvoiceId() != 0) sb.append(",C_Invoice_ID=").append(getInvoiceId());
        if (getBusinessPartnerId() != 0) sb.append(",C_BPartner_ID=").append(getBusinessPartnerId());
        sb.append(", Amount=")
                .append(getAmount())
                .append(",Discount=")
                .append(getDiscountAmt())
                .append(",WriteOff=")
                .append(getWriteOffAmt())
                .append(",OverUnder=")
                .append(getOverUnderAmt());
        sb.append("]");
        return sb.toString();
    } //	toString

    /**
     * ************************************************************************ Process Allocation
     * (does not update line). - Update and Link Invoice/Payment/Cash
     *
     * @param reverse if true allocation is reversed
     * @return C_BPartner_ID
     */
    protected int processIt(boolean reverse) {
        if (log.isLoggable(Level.FINE)) log.fine("Reverse=" + reverse + " - " + toString());
        int C_Invoice_ID = getInvoiceId();
        MInvoice invoice = getInvoice();
        if (invoice != null && getBusinessPartnerId() != invoice.getBusinessPartnerId())
            setBusinessPartnerId(invoice.getBusinessPartnerId());
        //
        int C_Payment_ID = getPaymentId();
        int C_CashLine_ID = getCashLineId();

        //	Update Payment
        if (C_Payment_ID != 0) {
            MPayment payment = new MPayment(C_Payment_ID);
            if (getBusinessPartnerId() != payment.getBusinessPartnerId())
                log.warning(
                        "C_BPartner_ID different - Invoice="
                                + getBusinessPartnerId()
                                + " - Payment="
                                + payment.getBusinessPartnerId());
            if (reverse) {
                if (!payment.isCashbookTrx()) {
                    payment.setIsAllocated(false);
                    payment.saveEx();
                }
            } else {
                if (payment.testAllocation()) payment.saveEx();
            }
        }

        //	Payment - Invoice
        if (C_Payment_ID != 0 && invoice != null) {
            //	Link to Invoice
            if (reverse) {
                invoice.setPaymentId(0);
                if (log.isLoggable(Level.FINE))
                    log.fine("C_Payment_ID=" + C_Payment_ID + " Unlinked from C_Invoice_ID=" + C_Invoice_ID);
            } else if (invoice.isPaid()) {
                invoice.setPaymentId(C_Payment_ID);
                if (log.isLoggable(Level.FINE))
                    log.fine("C_Payment_ID=" + C_Payment_ID + " Linked to C_Invoice_ID=" + C_Invoice_ID);
            }

            //	Link to Order
            String update =
                    "UPDATE C_Order o "
                            + "SET C_Payment_ID="
                            + (reverse
                            ? "NULL "
                            : "(SELECT C_Payment_ID FROM C_Invoice WHERE C_Invoice_ID=" + C_Invoice_ID + ") ")
                            + "WHERE o.C_Order_ID = (SELECT i.C_Order_ID FROM C_Invoice i "
                            + "WHERE i.C_Invoice_ID="
                            + C_Invoice_ID
                            + ")";
            if (executeUpdate(update) > 0)
                if (log.isLoggable(Level.FINE))
                    log.fine(
                            "C_Payment_ID="
                                    + C_Payment_ID
                                    + (reverse ? " UnLinked from" : " Linked to")
                                    + " order of C_Invoice_ID="
                                    + C_Invoice_ID);
        }

        //	Cash - Invoice
        if (C_CashLine_ID != 0 && invoice != null) {
            //	Link to Invoice
            if (reverse) {
                invoice.setCashLineId(0);
                if (log.isLoggable(Level.FINE))
                    log.fine(
                            "C_CashLine_ID=" + C_CashLine_ID + " Unlinked from C_Invoice_ID=" + C_Invoice_ID);
            } else {
                invoice.setCashLineId(C_CashLine_ID);
                if (log.isLoggable(Level.FINE))
                    log.fine("C_CashLine_ID=" + C_CashLine_ID + " Linked to C_Invoice_ID=" + C_Invoice_ID);
            }

            //	Link to Order
            String update =
                    "UPDATE C_Order o "
                            + "SET C_CashLine_ID="
                            + (reverse
                            ? "NULL "
                            : "(SELECT C_CashLine_ID FROM C_Invoice WHERE C_Invoice_ID="
                            + C_Invoice_ID
                            + ") ")
                            + "WHERE o.C_Order_ID = (SELECT i.C_Order_ID FROM C_Invoice i "
                            + "WHERE i.C_Invoice_ID="
                            + C_Invoice_ID
                            + ")";
            if (executeUpdate(update) > 0)
                if (log.isLoggable(Level.FINE))
                    log.fine(
                            "C_CashLine_ID="
                                    + C_CashLine_ID
                                    + (reverse ? " UnLinked from" : " Linked to")
                                    + " order of C_Invoice_ID="
                                    + C_Invoice_ID);
        }

        //	Update Balance / Credit used - Counterpart of MInvoice.completeIt
        if (invoice != null) {
            if (invoice.testAllocation() && !invoice.save())
                log.log(Level.SEVERE, "Invoice not updated - " + invoice);
        }

        return getBusinessPartnerId();
    } //	processIt
} //	MAllocationLine
