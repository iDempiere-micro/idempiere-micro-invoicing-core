package org.idempiere.process;

public class BankStatementMatchInfo {

    private int m_C_BPartner_ID = 0;
    private int m_C_Payment_ID = 0;
    private int m_C_Invoice_ID = 0;

    /**
     * Do we have a match?
     *
     * @return true if something could be matched
     */
    public boolean isMatched() {
        return m_C_BPartner_ID > 0 || m_C_Payment_ID > 0 || m_C_Invoice_ID > 0;
    } //	isValid

    /**
     * Get matched BPartner
     *
     * @return BPartner
     */
    public int getBusinessPartnerId() {
        return m_C_BPartner_ID;
    }

    /**
     * Get matched Payment
     *
     * @return Payment
     */
    public int getPaymentId() {
        return m_C_Payment_ID;
    }

    /**
     * Get matched Invoice
     *
     * @return invoice
     */
    public int getInvoiceId() {
        return m_C_Invoice_ID;
    }
} //	BankStatementMatchInfo
