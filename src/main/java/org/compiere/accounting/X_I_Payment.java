package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_I_Payment;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_I_Payment extends PO implements I_I_Payment {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_Payment(int I_Payment_ID) {
        super(I_Payment_ID);
    }

    /**
     * Load Constructor
     */
    public X_I_Payment(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_I_Payment[" + getId() + "]";
    }

    /**
     * Get Account No.
     *
     * @return Account Number
     */
    public String getAccountNo() {
        return getValue(COLUMNNAME_AccountNo);
    }

    /**
     * Get Account City.
     *
     * @return City or the Credit Card or Account Holder
     */
    public String getAccountCity() {
        return getValue(COLUMNNAME_A_City);
    }

    /**
     * Get Account Country.
     *
     * @return Country
     */
    public String getAccountCountry() {
        return getValue(COLUMNNAME_A_Country);
    }

    /**
     * Get Account EMail.
     *
     * @return Email Address
     */
    public String getAccountEMail() {
        return getValue(COLUMNNAME_A_EMail);
    }

    /**
     * Get Driver License.
     *
     * @return Payment Identification - Driver License
     */
    public String getPaymentIdentificationDriverLicence() {
        return getValue(COLUMNNAME_A_Ident_DL);
    }

    /**
     * Get Social Security No.
     *
     * @return Payment Identification - Social Security No
     */
    public String getSocialSecurityNoPaymentIdentification() {
        return getValue(COLUMNNAME_A_Ident_SSN);
    }

    /**
     * Get Account Name.
     *
     * @return Name on Credit Card or Account holder
     */
    public String getAccountName() {
        return getValue(COLUMNNAME_A_Name);
    }

    /**
     * Get Account State.
     *
     * @return State of the Credit Card or Account holder
     */
    public String getAccountState() {
        return getValue(COLUMNNAME_A_State);
    }

    /**
     * Get Account Street.
     *
     * @return Street address of the Credit Card or Account holder
     */
    public String getAccountStreet() {
        return getValue(COLUMNNAME_A_Street);
    }

    /**
     * Get Account Zip/Postal.
     *
     * @return Zip Code of the Credit Card or Account Holder
     */
    public String getAccountZip() {
        return getValue(COLUMNNAME_A_Zip);
    }

    /**
     * Get Bank Account.
     *
     * @return Account at the Bank
     */
    public int getBankAccountId() {
        Integer ii = getValue(COLUMNNAME_C_BankAccount_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getChargeId() {
        Integer ii = getValue(COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getDocumentTypeId() {
        Integer ii = getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Charge amount.
     *
     * @return Charge Amount
     */
    public BigDecimal getChargeAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ChargeAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Check No.
     *
     * @return Check Number
     */
    public String getCheckNo() {
        return getValue(COLUMNNAME_CheckNo);
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = getValue(COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment.
     *
     * @param C_Payment_ID Payment identifier
     */
    public void setPaymentId(int C_Payment_ID) {
        if (C_Payment_ID < 1) setValue(COLUMNNAME_C_Payment_ID, null);
        else setValue(COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
    }

    /**
     * Get Exp. Month.
     *
     * @return Expiry Month
     */
    public int getCreditCardExpMM() {
        Integer ii = getValue(COLUMNNAME_CreditCardExpMM);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Exp. Year.
     *
     * @return Expiry Year
     */
    public int getCreditCardExpYY() {
        Integer ii = getValue(COLUMNNAME_CreditCardExpYY);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Number.
     *
     * @return Credit Card Number
     */
    public String getCreditCardNumber() {
        return getValue(COLUMNNAME_CreditCardNumber);
    }

    /**
     * Get Credit Card.
     *
     * @return Credit Card (Visa, MC, AmEx)
     */
    public String getCreditCardType() {
        return getValue(COLUMNNAME_CreditCardType);
    }

    /**
     * Get Verification Code.
     *
     * @return Credit Card Verification code on credit card
     */
    public String getCreditCardVV() {
        return getValue(COLUMNNAME_CreditCardVV);
    }

    /**
     * Get Transaction Date.
     *
     * @return Transaction Date
     */
    public Timestamp getDateTrx() {
        return (Timestamp) getValue(COLUMNNAME_DateTrx);
    }

    /**
     * Get Discount Amount.
     *
     * @return Calculated amount of discount
     */
    public BigDecimal getDiscountAmt() {
        BigDecimal bd = getValue(COLUMNNAME_DiscountAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Get IBAN.
     *
     * @return International Bank Account Number
     */
    public String getIBAN() {
        return getValue(COLUMNNAME_IBAN);
    }

    /**
     * Set Imported.
     *
     * @param I_IsImported Has this import been processed
     */
    public void setIsImported(boolean I_IsImported) {
        setValue(COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
    }

    /**
     * Get Micr.
     *
     * @return Combination of routing no, account and check no
     */
    public String getMicr() {
        return getValue(COLUMNNAME_Micr);
    }

    /**
     * Get Original Transaction ID.
     *
     * @return Original Transaction ID
     */
    public String getOriginalTransactionId() {
        return getValue(COLUMNNAME_Orig_TrxID);
    }

    /**
     * Get Payment amount.
     *
     * @return Amount being paid
     */
    public BigDecimal getPayAmt() {
        BigDecimal bd = getValue(COLUMNNAME_PayAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get PO Number.
     *
     * @return Purchase Order Number
     */
    public String getPONum() {
        return getValue(COLUMNNAME_PONum);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Authorization Code.
     *
     * @return Authorization Code returned
     */
    public String getAuthorizationCode() {
        return getValue(COLUMNNAME_R_AuthCode);
    }

    /**
     * Get Info.
     *
     * @return Response info
     */
    public String getResponseInfo() {
        return getValue(COLUMNNAME_R_Info);
    }

    /**
     * Get Routing No.
     *
     * @return Bank Routing Number
     */
    public String getRoutingNo() {
        return getValue(COLUMNNAME_RoutingNo);
    }

    /**
     * Get Reference.
     *
     * @return Payment reference
     */
    public String getPaymentReference() {
        return getValue(COLUMNNAME_R_PnRef);
    }

    /**
     * Get Response Message.
     *
     * @return Response message
     */
    public String getResponseMessage() {
        return getValue(COLUMNNAME_R_RespMsg);
    }

    /**
     * Get Result.
     *
     * @return Result of transmission
     */
    public String getTransmissionResult() {
        return getValue(COLUMNNAME_R_Result);
    }

    /**
     * Get Swift code.
     *
     * @return Swift Code or BIC
     */
    public String getSwiftCode() {
        return getValue(COLUMNNAME_SwiftCode);
    }

    /**
     * Get Swipe.
     *
     * @return Track 1 and 2 of the Credit Card
     */
    public String getSwipe() {
        return getValue(COLUMNNAME_Swipe);
    }

    /**
     * Get Tax Amount.
     *
     * @return Tax Amount for a document
     */
    public BigDecimal getTaxAmt() {
        BigDecimal bd = getValue(COLUMNNAME_TaxAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Tender type.
     *
     * @return Method of Payment
     */
    public String getTenderType() {
        return getValue(COLUMNNAME_TenderType);
    }

    /**
     * Get Transaction Type.
     *
     * @return Type of credit card transaction
     */
    public String getTrxType() {
        return getValue(COLUMNNAME_TrxType);
    }

    /**
     * Get Voice authorization code.
     *
     * @return Voice Authorization Code from credit card company
     */
    public String getVoiceAuthCode() {
        return getValue(COLUMNNAME_VoiceAuthCode);
    }

    /**
     * Get Write-off Amount.
     *
     * @return Amount to write-off
     */
    public BigDecimal getWriteOffAmt() {
        BigDecimal bd = getValue(COLUMNNAME_WriteOffAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    @Override
    public int getTableId() {
        return I_I_Payment.Table_ID;
    }
}
