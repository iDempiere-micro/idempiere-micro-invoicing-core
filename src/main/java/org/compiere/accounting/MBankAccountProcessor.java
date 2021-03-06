package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;

import java.math.BigDecimal;


/**
 * Bank Account Payment Processor
 *
 * @author Elaine
 * @date October 17, 2012
 */
public class MBankAccountProcessor extends X_C_BankAccount_Processor {

    /**
     *
     */
    private static final long serialVersionUID = -9082774421123292838L;

    public MBankAccountProcessor(int ignored) {
        super(0);
        if (ignored != 0) throw new IllegalArgumentException("Multi-Key");
    }

    public MBankAccountProcessor(Row row) {
        super(row);
    }

    public MBankAccountProcessor(
            int C_BankAccount_ID, int C_PaymentProcessor_ID) {
        this(0);
        setBankAccountId(C_BankAccount_ID); // 	FK
        setPaymentProcessorId(C_PaymentProcessor_ID); // 	FK
    }

    /**
     * Get Bank Account Processor
     *
     * @param ctx           context
     * @param tender        optional Tender see TENDER_
     * @param CCType        optional CC Type see CC_
     * @param AD_Client_ID  Client
     * @param C_Currency_ID Currency (ignored)
     * @param Amt           Amount (ignored)
     * @return Array of BankAccount[0] & PaymentProcessor[1] or null
     */
    public static MBankAccountProcessor[] find(

            String tender,
            String CCType,
            int AD_Client_ID,
            int C_Currency_ID,
            BigDecimal Amt) {
        return MBaseBankAccountProcessorKt.findBankAccountProcessors(tender, CCType, AD_Client_ID, C_Currency_ID, Amt);
    } //  find

    @Override
    protected boolean beforeSave(boolean newRecord) {
        if (getPaymentProcessorId() > 0 && isActive()) {
            MPaymentProcessor pp =
                    new MPaymentProcessor(getPaymentProcessorId());
            if (!pp.isActive())
                throw new AdempiereException(
                        MsgKt.translate("InactivePaymentProcessor") + ". " + pp.toString());
        }

        return true;
    }

    /**
     * Does Payment Processor accepts tender / CC
     *
     * @param TenderType     tender type
     * @param CreditCardType credit card type
     * @return true if acceptes
     */
    public boolean accepts(String TenderType, String CreditCardType) {
        if (getPaymentProcessorId() > 0) {
            MPaymentProcessor pp =
                    new MPaymentProcessor(getPaymentProcessorId());

            return (MPayment.TENDERTYPE_DirectDeposit.equals(TenderType)
                    && isAcceptDirectDeposit()
                    && pp.isAcceptDirectDeposit())
                    || (MPayment.TENDERTYPE_DirectDebit.equals(TenderType)
                    && isAcceptDirectDebit()
                    && pp.isAcceptDirectDebit())
                    || (MPayment.TENDERTYPE_Check.equals(TenderType) && isAcceptCheck() && pp.isAcceptCheck())
                    //
                    || (MPayment.CREDITCARDTYPE_ATM.equals(CreditCardType)
                    && isAcceptATM()
                    && pp.isAcceptATM())
                    || (MPayment.CREDITCARDTYPE_Amex.equals(CreditCardType)
                    && isAcceptAMEX()
                    && pp.isAcceptAMEX())
                    || (MPayment.CREDITCARDTYPE_PurchaseCard.equals(CreditCardType)
                    && isAcceptCorporate()
                    && pp.isAcceptCorporate())
                    || (MPayment.CREDITCARDTYPE_Diners.equals(CreditCardType)
                    && isAcceptDiners()
                    && pp.isAcceptDiners())
                    || (MPayment.CREDITCARDTYPE_Discover.equals(CreditCardType)
                    && isAcceptDiscover()
                    && pp.isAcceptDiscover())
                    || (MPayment.CREDITCARDTYPE_MasterCard.equals(CreditCardType)
                    && isAcceptMC()
                    && pp.isAcceptMC())
                    || (MPayment.CREDITCARDTYPE_Visa.equals(CreditCardType)
                    && isAcceptVisa()
                    && pp.isAcceptVisa());
        } else {
            return (MPayment.TENDERTYPE_DirectDeposit.equals(TenderType) && isAcceptDirectDeposit())
                    || (MPayment.TENDERTYPE_DirectDebit.equals(TenderType) && isAcceptDirectDebit())
                    || (MPayment.TENDERTYPE_Check.equals(TenderType) && isAcceptCheck())
                    //
                    || (MPayment.CREDITCARDTYPE_ATM.equals(CreditCardType) && isAcceptATM())
                    || (MPayment.CREDITCARDTYPE_Amex.equals(CreditCardType) && isAcceptAMEX())
                    || (MPayment.CREDITCARDTYPE_PurchaseCard.equals(CreditCardType) && isAcceptCorporate())
                    || (MPayment.CREDITCARDTYPE_Diners.equals(CreditCardType) && isAcceptDiners())
                    || (MPayment.CREDITCARDTYPE_Discover.equals(CreditCardType) && isAcceptDiscover())
                    || (MPayment.CREDITCARDTYPE_MasterCard.equals(CreditCardType) && isAcceptMC())
                    || (MPayment.CREDITCARDTYPE_Visa.equals(CreditCardType) && isAcceptVisa());
        }
    } //	accepts

    public String toString() {
        return "MBankAccountProcessor[" +
                "C_BankAccount_ID=" +
                getBankAccountId() +
                ",C_PaymentProcessor_ID=" +
                getPaymentProcessorId() +
                "]";
    }

}
