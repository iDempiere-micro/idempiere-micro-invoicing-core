package org.compiere.accounting;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static software.hsharp.core.util.DBKt.getSQLValue;


/**
 * Allocation Line
 *
 * @author Jorg Janke
 * @version $Id: DocLine_Allocation.java,v 1.2 2006/07/30 00:53:33 jjanke Exp $
 */
public class DocLine_Allocation extends DocLine {
    private int m_C_Invoice_ID;
    private int m_C_Payment_ID;
    private int m_C_CashLine_ID;
    private int m_C_Order_ID;
    private int m_C_Charge_ID; // adaxa-pb
    private BigDecimal m_DiscountAmt;
    private BigDecimal m_WriteOffAmt;
    private BigDecimal m_OverUnderAmt;

    /**
     * DocLine_Allocation
     *
     * @param line allocation line
     * @param doc  header
     */
    public DocLine_Allocation(MAllocationLine line, Doc doc) {
        super(line, doc);
        m_C_Payment_ID = line.getPaymentId();
        m_C_CashLine_ID = line.getCashLineId();
        m_C_Invoice_ID = line.getInvoiceId();
        m_C_Order_ID = line.getOrderId();
        // adaxa-pb
        Object obj = line.getValue("C_Charge_ID");
        if (obj != null) m_C_Charge_ID = (Integer) line.getValue("C_Charge_ID");
        else m_C_Charge_ID = 0;
        // end adaxa-pb
        setAmount(line.getAmount());
        m_DiscountAmt = line.getDiscountAmt();
        m_WriteOffAmt = line.getWriteOffAmt();
        m_OverUnderAmt = line.getOverUnderAmt();

        //	Get Payment Conversion Rate
        if (line.getPaymentId() != 0) {
            MPayment payment = new MPayment(line.getPaymentId());
            int C_ConversionType_ID = payment.getConversionTypeId();
            this.setConversionTypeId(C_ConversionType_ID);
        }
    } //	DocLine_Allocation

    /**
     * Get Invoice C_Currency_ID
     *
     * @return 0 if no invoice -1 if not found
     */
    public int getInvoiceC_CurrencyId() {
        if (m_C_Invoice_ID == 0) return 0;
        String sql = "SELECT C_Currency_ID " + "FROM C_Invoice " + "WHERE C_Invoice_ID=?";
        return getSQLValue(sql, m_C_Invoice_ID);
    } //	getInvoiceC_Currency_ID

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("DocLine_Allocation[");
        sb.append(getId())
                .append(",Amt=")
                .append(getAmtSource())
                .append(",Discount=")
                .append(getDiscountAmt())
                .append(",WriteOff=")
                .append(getWriteOffAmt())
                .append(",OverUnderAmt=")
                .append(getOverUnderAmt())
                .append(" - C_Payment_ID=")
                .append(m_C_Payment_ID)
                .append(",C_CashLine_ID=")
                .append(m_C_CashLine_ID)
                .append(",C_Invoice_ID=")
                .append(m_C_Invoice_ID)
                .append("]");
        return sb.toString();
    } //	toString

    /**
     * @return Returns the c_Order_ID.
     */
    public int getOrderId() {
        return m_C_Order_ID;
    }

    /**
     * @return Returns the discountAmt.
     */
    public BigDecimal getDiscountAmt() {
        return m_DiscountAmt;
    }

    /**
     * @return Returns the overUnderAmt.
     */
    public BigDecimal getOverUnderAmt() {
        return m_OverUnderAmt;
    }

    /**
     * @return Returns the writeOffAmt.
     */
    public BigDecimal getWriteOffAmt() {
        return m_WriteOffAmt;
    }

    /**
     * @return Returns the c_CashLine_ID.
     */
    public int getCashLineId() {
        return m_C_CashLine_ID;
    }

    /**
     * @return Returns the c_Invoice_ID.
     */
    public int getInvoiceId() {
        return m_C_Invoice_ID;
    }

    /**
     * @return Returns the c_Payment_ID.
     */
    public int getPaymentId() {
        return m_C_Payment_ID;
    }

    /**
     * adaxa-pb
     *
     * @return Returns the C_Charge_ID.
     */
    public int getChargeId() {
        return m_C_Charge_ID;
    }

    @Override
    public Timestamp getDateConv() {
        if (getPaymentId() > 0) {
            MPayment payment = new MPayment(getPaymentId());
            return payment.getDateAcct(); // use payment date
        }
        return super.getDateConv();
    }
} //	DocLine_Allocation
