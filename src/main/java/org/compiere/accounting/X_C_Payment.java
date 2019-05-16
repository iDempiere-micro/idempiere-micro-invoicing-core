package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_Payment;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_Payment
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_Payment extends AccountingPO implements I_C_Payment {

    /**
     * Amex = A
     */
    public static final String CREDITCARDTYPE_Amex = "A";
    /**
     * MasterCard = M
     */
    public static final String CREDITCARDTYPE_MasterCard = "M";
    /**
     * Visa = V
     */
    public static final String CREDITCARDTYPE_Visa = "V";
    /**
     * ATM = C
     */
    public static final String CREDITCARDTYPE_ATM = "C";
    /**
     * Diners = D
     */
    public static final String CREDITCARDTYPE_Diners = "D";
    /**
     * Discover = N
     */
    public static final String CREDITCARDTYPE_Discover = "N";
    /**
     * Purchase Card = P
     */
    public static final String CREDITCARDTYPE_PurchaseCard = "P";
    /**
     * Complete = CO
     */
    public static final String DOCACTION_Complete = "CO";
    /**
     * Close = CL
     */
    public static final String DOCACTION_Close = "CL";
    /**
     * <None> = --
     */
    public static final String DOCACTION_None = "--";
    /**
     * Prepare = PR
     */
    public static final String DOCACTION_Prepare = "PR";
    /**
     * Drafted = DR
     */
    public static final String DOCSTATUS_Drafted = "DR";
    /**
     * Completed = CO
     */
    public static final String DOCSTATUS_Completed = "CO";
    /**
     * Approved = AP
     */
    public static final String DOCSTATUS_Approved = "AP";
    /**
     * Not Approved = NA
     */
    public static final String DOCSTATUS_NotApproved = "NA";
    /**
     * Voided = VO
     */
    public static final String DOCSTATUS_Voided = "VO";
    /**
     * Invalid = IN
     */
    public static final String DOCSTATUS_Invalid = "IN";
    /**
     * Reversed = RE
     */
    public static final String DOCSTATUS_Reversed = "RE";
    /**
     * Closed = CL
     */
    public static final String DOCSTATUS_Closed = "CL";
    /**
     * In Progress = IP
     */
    public static final String DOCSTATUS_InProgress = "IP";
    /**
     * Waiting Payment = WP
     */
    public static final String DOCSTATUS_WaitingPayment = "WP";
    /**
     * Unavailable = X
     */
    public static final String R_AVSZIP_Unavailable = "X";
    /**
     * Credit Card = C
     */
    public static final String TENDERTYPE_CreditCard = "C";
    /**
     * Check = K
     */
    public static final String TENDERTYPE_Check = "K";
    /**
     * Direct Deposit = A
     */
    public static final String TENDERTYPE_DirectDeposit = "A";
    /**
     * Direct Debit = D
     */
    public static final String TENDERTYPE_DirectDebit = "D";
    /**
     * Account = T
     */
    public static final String TENDERTYPE_Account = "T";
    /**
     * Cash = X
     */
    public static final String TENDERTYPE_Cash = "X";
    /**
     * Sales = S
     */
    public static final String TRXTYPE_Sales = "S";
    /**
     * Delayed Capture = D
     */
    public static final String TRXTYPE_DelayedCapture = "D";
    /**
     * Credit (Payment) = C
     */
    public static final String TRXTYPE_CreditPayment = "C";
    /**
     * Void = V
     */
    public static final String TRXTYPE_Void = "V";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Payment(Row row, int C_Payment_ID) {
        super(row, C_Payment_ID);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_C_Payment[" + getId() + "]";
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
     * Set Account No.
     *
     * @param AccountNo Account Number
     */
    public void setAccountNo(String AccountNo) {
        setValue(COLUMNNAME_AccountNo, AccountNo);
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
     * Set Account City.
     *
     * @param A_City City or the Credit Card or Account Holder
     */
    public void setAccountCity(String A_City) {
        setValue(COLUMNNAME_A_City, A_City);
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
     * Set Account Country.
     *
     * @param A_Country Country
     */
    public void setAccountCountry(String A_Country) {
        setValue(COLUMNNAME_A_Country, A_Country);
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
     * Set Account EMail.
     *
     * @param A_EMail Email Address
     */
    public void setAccountEMail(String A_EMail) {
        setValue(COLUMNNAME_A_EMail, A_EMail);
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
     * Set Driver License.
     *
     * @param A_Ident_DL Payment Identification - Driver License
     */
    public void setPaymentIdentificationDriverLicence(String A_Ident_DL) {
        setValue(COLUMNNAME_A_Ident_DL, A_Ident_DL);
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
     * Set Social Security No.
     *
     * @param A_Ident_SSN Payment Identification - Social Security No
     */
    public void setSocialSecurityNoPaymentIdentification(String A_Ident_SSN) {
        setValue(COLUMNNAME_A_Ident_SSN, A_Ident_SSN);
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
     * Set Account Name.
     *
     * @param A_Name Name on Credit Card or Account holder
     */
    public void setAccountName(String A_Name) {
        setValue(COLUMNNAME_A_Name, A_Name);
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
     * Set Account State.
     *
     * @param A_State State of the Credit Card or Account holder
     */
    public void setAccountState(String A_State) {
        setValue(COLUMNNAME_A_State, A_State);
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
     * Set Account Street.
     *
     * @param A_Street Street address of the Credit Card or Account holder
     */
    public void setAccountStreet(String A_Street) {
        setValue(COLUMNNAME_A_Street, A_Street);
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
     * Set Account Zip/Postal.
     *
     * @param A_Zip Zip Code of the Credit Card or Account Holder
     */
    public void setAccountZip(String A_Zip) {
        setValue(COLUMNNAME_A_Zip, A_Zip);
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValue(COLUMNNAME_C_Activity_ID, null);
        else setValue(COLUMNNAME_C_Activity_ID, C_Activity_ID);
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
     * Set Bank Account.
     *
     * @param C_BankAccount_ID Account at the Bank
     */
    public void setBankAccountId(int C_BankAccount_ID) {
        if (C_BankAccount_ID < 1) setValue(COLUMNNAME_C_BankAccount_ID, null);
        else setValue(COLUMNNAME_C_BankAccount_ID, C_BankAccount_ID);
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
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValue(COLUMNNAME_C_BPartner_ID, null);
        else setValue(COLUMNNAME_C_BPartner_ID, C_BPartner_ID);
    }

    /**
     * Get Partner Bank Account.
     *
     * @return Bank Account of the Business Partner
     */
    public int getBusinessPartnerBankAccountId() {
        Integer ii = getValue(COLUMNNAME_C_BP_BankAccount_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Partner Bank Account.
     *
     * @param C_BP_BankAccount_ID Bank Account of the Business Partner
     */
    public void setBusinessPartnerBankAccountId(int C_BP_BankAccount_ID) {
        if (C_BP_BankAccount_ID < 1) setValue(COLUMNNAME_C_BP_BankAccount_ID, null);
        else setValue(COLUMNNAME_C_BP_BankAccount_ID, C_BP_BankAccount_ID);
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValue(COLUMNNAME_C_Campaign_ID, null);
        else setValue(COLUMNNAME_C_Campaign_ID, C_Campaign_ID);
    }

    /**
     * Get Cash Book.
     *
     * @return Cash Book for recording petty cash transactions
     */
    public int getCashBookId() {
        Integer ii = getValue(COLUMNNAME_C_CashBook_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cash Book.
     *
     * @param C_CashBook_ID Cash Book for recording petty cash transactions
     */
    public void setCashBookId(int C_CashBook_ID) {
        if (C_CashBook_ID < 1) setValue(COLUMNNAME_C_CashBook_ID, null);
        else setValue(COLUMNNAME_C_CashBook_ID, C_CashBook_ID);
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
     * Set Charge.
     *
     * @param C_Charge_ID Additional document charges
     */
    public void setChargeId(int C_Charge_ID) {
        if (C_Charge_ID < 1) setValue(COLUMNNAME_C_Charge_ID, null);
        else setValue(COLUMNNAME_C_Charge_ID, C_Charge_ID);
    }

    /**
     * Get Currency Type.
     *
     * @return Currency Conversion Rate Type
     */
    public int getConversionTypeId() {
        Integer ii = getValue(COLUMNNAME_C_ConversionType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency Type.
     *
     * @param C_ConversionType_ID Currency Conversion Rate Type
     */
    public void setConversionTypeId(int C_ConversionType_ID) {
        if (C_ConversionType_ID < 1) setValue(COLUMNNAME_C_ConversionType_ID, null);
        else setValue(COLUMNNAME_C_ConversionType_ID, C_ConversionType_ID);
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
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(COLUMNNAME_C_Currency_ID, null);
        else setValue(COLUMNNAME_C_Currency_ID, C_Currency_ID);
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
     * Set Document Type.
     *
     * @param C_DocType_ID Document type or rules
     */
    public void setDocumentTypeId(int C_DocType_ID) {
        if (C_DocType_ID < 0) setValue(COLUMNNAME_C_DocType_ID, null);
        else setValue(COLUMNNAME_C_DocType_ID, C_DocType_ID);
    }

    /**
     * Set Charge amount.
     *
     * @param ChargeAmt Charge Amount
     */
    public void setChargeAmt(BigDecimal ChargeAmt) {
        setValue(COLUMNNAME_ChargeAmt, ChargeAmt);
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
     * Set Check No.
     *
     * @param CheckNo Check Number
     */
    public void setCheckNo(String CheckNo) {
        setValue(COLUMNNAME_CheckNo, CheckNo);
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
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValue(COLUMNNAME_C_Invoice_ID, null);
        else setValue(COLUMNNAME_C_Invoice_ID, C_Invoice_ID);
    }

    /**
     * Get Order.
     *
     * @return Order
     */
    public int getOrderId() {
        Integer ii = getValue(COLUMNNAME_C_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Order.
     *
     * @param C_Order_ID Order
     */
    public void setOrderId(int C_Order_ID) {
        if (C_Order_ID < 1) setValue(COLUMNNAME_C_Order_ID, null);
        else setValue(COLUMNNAME_C_Order_ID, C_Order_ID);
    }

    /**
     * Set Payment Batch.
     *
     * @param C_PaymentBatch_ID Payment batch for EFT
     */
    public void setPaymentBatchId(int C_PaymentBatch_ID) {
        if (C_PaymentBatch_ID < 1) setValue(COLUMNNAME_C_PaymentBatch_ID, null);
        else setValue(COLUMNNAME_C_PaymentBatch_ID, C_PaymentBatch_ID);
    }

    /**
     * Get Payment.
     *
     * @return Payment identifier
     */
    public int getPaymentId() {
        Integer ii = getValue(COLUMNNAME_C_Payment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Payment Processor.
     *
     * @return Payment processor for electronic payments
     */
    public int getPaymentProcessorId() {
        Integer ii = getValue(COLUMNNAME_C_PaymentProcessor_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment Processor.
     *
     * @param C_PaymentProcessor_ID Payment processor for electronic payments
     */
    public void setPaymentProcessorId(int C_PaymentProcessor_ID) {
        if (C_PaymentProcessor_ID < 1) setValue(COLUMNNAME_C_PaymentProcessor_ID, null);
        else setValue(COLUMNNAME_C_PaymentProcessor_ID, C_PaymentProcessor_ID);
    }

    /**
     * Get POS Tender Type.
     *
     * @return POS Tender Type
     */
    public int getPOSTenderTypeId() {
        Integer ii = getValue(COLUMNNAME_C_POSTenderType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set POS Tender Type.
     *
     * @param C_POSTenderType_ID POS Tender Type
     */
    public void setPOSTenderTypeId(int C_POSTenderType_ID) {
        if (C_POSTenderType_ID < 1) setValue(COLUMNNAME_C_POSTenderType_ID, null);
        else setValue(COLUMNNAME_C_POSTenderType_ID, C_POSTenderType_ID);
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValue(COLUMNNAME_C_Project_ID, null);
        else setValue(COLUMNNAME_C_Project_ID, C_Project_ID);
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
     * Set Exp. Month.
     *
     * @param CreditCardExpMM Expiry Month
     */
    public void setCreditCardExpMM(int CreditCardExpMM) {
        setValue(COLUMNNAME_CreditCardExpMM, CreditCardExpMM);
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
     * Set Exp. Year.
     *
     * @param CreditCardExpYY Expiry Year
     */
    public void setCreditCardExpYY(int CreditCardExpYY) {
        setValue(COLUMNNAME_CreditCardExpYY, CreditCardExpYY);
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
     * Set Number.
     *
     * @param CreditCardNumber Credit Card Number
     */
    public void setCreditCardNumber(String CreditCardNumber) {
        setValue(COLUMNNAME_CreditCardNumber, CreditCardNumber);
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
     * Set Credit Card.
     *
     * @param CreditCardType Credit Card (Visa, MC, AmEx)
     */
    public void setCreditCardType(String CreditCardType) {

        setValue(COLUMNNAME_CreditCardType, CreditCardType);
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
     * Set Verification Code.
     *
     * @param CreditCardVV Credit Card Verification code on credit card
     */
    public void setCreditCardVV(String CreditCardVV) {
        setValue(COLUMNNAME_CreditCardVV, CreditCardVV);
    }

    /**
     * Get Customer Address ID.
     *
     * @return Customer Address ID
     */
    public String getCustomerAddressID() {
        return getValue(COLUMNNAME_CustomerAddressID);
    }

    /**
     * Set Customer Address ID.
     *
     * @param CustomerAddressID Customer Address ID
     */
    public void setCustomerAddressID(String CustomerAddressID) {
        setValue(COLUMNNAME_CustomerAddressID, CustomerAddressID);
    }

    /**
     * Get Customer Payment Profile ID.
     *
     * @return Customer Payment Profile ID
     */
    public String getCustomerPaymentProfileID() {
        return getValue(COLUMNNAME_CustomerPaymentProfileID);
    }

    /**
     * Set Customer Payment Profile ID.
     *
     * @param CustomerPaymentProfileID Customer Payment Profile ID
     */
    public void setCustomerPaymentProfileID(String CustomerPaymentProfileID) {
        setValue(COLUMNNAME_CustomerPaymentProfileID, CustomerPaymentProfileID);
    }

    /**
     * Get Customer Profile ID.
     *
     * @return Customer Profile ID
     */
    public String getCustomerProfileID() {
        return getValue(COLUMNNAME_CustomerProfileID);
    }

    /**
     * Set Customer Profile ID.
     *
     * @param CustomerProfileID Customer Profile ID
     */
    public void setCustomerProfileID(String CustomerProfileID) {
        setValue(COLUMNNAME_CustomerProfileID, CustomerProfileID);
    }

    /**
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(COLUMNNAME_DateAcct);
    }

    /**
     * Set Account Date.
     *
     * @param DateAcct Accounting Date
     */
    public void setDateAcct(Timestamp DateAcct) {
        setValue(COLUMNNAME_DateAcct, DateAcct);
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
     * Set Transaction Date.
     *
     * @param DateTrx Transaction Date
     */
    public void setDateTrx(Timestamp DateTrx) {
        setValue(COLUMNNAME_DateTrx, DateTrx);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
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
     * Set Discount Amount.
     *
     * @param DiscountAmt Calculated amount of discount
     */
    public void setDiscountAmt(BigDecimal DiscountAmt) {
        setValue(COLUMNNAME_DiscountAmt, DiscountAmt);
    }

    /**
     * Get Document Action.
     *
     * @return The targeted status of the document
     */
    public String getDocAction() {
        return getValue(COLUMNNAME_DocAction);
    }

    /**
     * Set Document Action.
     *
     * @param DocAction The targeted status of the document
     */
    public void setDocAction(String DocAction) {

        setValue(COLUMNNAME_DocAction, DocAction);
    }

    /**
     * Get Document Status.
     *
     * @return The current status of the document
     */
    public String getDocStatus() {
        return getValue(COLUMNNAME_DocStatus);
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(String DocStatus) {

        setValue(COLUMNNAME_DocStatus, DocStatus);
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
     * Set Document No.
     *
     * @param DocumentNo Document sequence number of the document
     */
    public void setDocumentNo(String DocumentNo) {
        setValue(COLUMNNAME_DocumentNo, DocumentNo);
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
     * Set IBAN.
     *
     * @param IBAN International Bank Account Number
     */
    public void setIBAN(String IBAN) {
        setValue(COLUMNNAME_IBAN, IBAN);
    }

    /**
     * Set Allocated.
     *
     * @param IsAllocated Indicates if the payment has been allocated
     */
    public void setIsAllocated(boolean IsAllocated) {
        setValue(COLUMNNAME_IsAllocated, IsAllocated);
    }

    /**
     * Get Allocated.
     *
     * @return Indicates if the payment has been allocated
     */
    public boolean isAllocated() {
        Object oo = getValue(COLUMNNAME_IsAllocated);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValueNoCheck(COLUMNNAME_IsApproved, IsApproved);
    }

    /**
     * Get Approved.
     *
     * @return Indicates if this document requires approval
     */
    public boolean isApproved() {
        Object oo = getValue(COLUMNNAME_IsApproved);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Delayed Capture.
     *
     * @param IsDelayedCapture Charge after Shipment
     */
    public void setIsDelayedCapture(boolean IsDelayedCapture) {
        setValue(COLUMNNAME_IsDelayedCapture, IsDelayedCapture);
    }

    /**
     * Get Delayed Capture.
     *
     * @return Charge after Shipment
     */
    public boolean isDelayedCapture() {
        Object oo = getValue(COLUMNNAME_IsDelayedCapture);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Online Access.
     *
     * @param IsOnline Can be accessed online
     */
    public void setIsOnline(boolean IsOnline) {
        setValue(COLUMNNAME_IsOnline, IsOnline);
    }

    /**
     * Get Online Access.
     *
     * @return Can be accessed online
     */
    public boolean isOnline() {
        Object oo = getValue(COLUMNNAME_IsOnline);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Over/Under Payment.
     *
     * @param IsOverUnderPayment Over-Payment (unallocated) or Under-Payment (partial payment)
     */
    public void setIsOverUnderPayment(boolean IsOverUnderPayment) {
        setValue(COLUMNNAME_IsOverUnderPayment, IsOverUnderPayment);
    }

    /**
     * Get Over/Under Payment.
     *
     * @return Over-Payment (unallocated) or Under-Payment (partial payment)
     */
    public boolean isOverUnderPayment() {
        Object oo = getValue(COLUMNNAME_IsOverUnderPayment);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Prepayment.
     *
     * @param IsPrepayment The Payment/Receipt is a Prepayment
     */
    public void setIsPrepayment(boolean IsPrepayment) {
        setValue(COLUMNNAME_IsPrepayment, IsPrepayment);
    }

    /**
     * Get Prepayment.
     *
     * @return The Payment/Receipt is a Prepayment
     */
    public boolean isPrepayment() {
        Object oo = getValue(COLUMNNAME_IsPrepayment);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Receipt.
     *
     * @param IsReceipt This is a sales transaction (receipt)
     */
    public void setIsReceipt(boolean IsReceipt) {
        setValue(COLUMNNAME_IsReceipt, IsReceipt);
    }

    /**
     * Get Receipt.
     *
     * @return This is a sales transaction (receipt)
     */
    public boolean isReceipt() {
        Object oo = getValue(COLUMNNAME_IsReceipt);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Reconciled.
     *
     * @param IsReconciled Payment is reconciled with bank statement
     */
    public void setIsReconciled(boolean IsReconciled) {
        setValue(COLUMNNAME_IsReconciled, IsReconciled);
    }

    /**
     * Set Self-Service.
     *
     * @param IsSelfService This is a Self-Service entry or this entry can be changed via Self-Service
     */
    public void setIsSelfService(boolean IsSelfService) {
        setValue(COLUMNNAME_IsSelfService, IsSelfService);
    }

    /**
     * Get Self-Service.
     *
     * @return This is a Self-Service entry or this entry can be changed via Self-Service
     */
    public boolean isSelfService() {
        Object oo = getValue(COLUMNNAME_IsSelfService);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Voided.
     *
     * @param IsVoided Voided
     */
    public void setIsVoided(boolean IsVoided) {
        setValue(COLUMNNAME_IsVoided, IsVoided);
    }

    /**
     * Get Voided.
     *
     * @return Voided
     */
    public boolean isVoided() {
        Object oo = getValue(COLUMNNAME_IsVoided);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
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
     * Set Micr.
     *
     * @param Micr Combination of routing no, account and check no
     */
    public void setMicr(String Micr) {
        setValue(COLUMNNAME_Micr, Micr);
    }

    /**
     * Set Online Processing.
     *
     * @param OProcessing This payment can be processed online
     */
    public void setOProcessing(String OProcessing) {
        setValue(COLUMNNAME_OProcessing, OProcessing);
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
     * Set Original Transaction ID.
     *
     * @param Orig_TrxID Original Transaction ID
     */
    public void setOriginalTransactionId(String Orig_TrxID) {
        setValue(COLUMNNAME_Orig_TrxID, Orig_TrxID);
    }

    /**
     * Get Over/Under Payment.
     *
     * @return Over-Payment (unallocated) or Under-Payment (partial payment) Amount
     */
    public BigDecimal getOverUnderAmt() {
        BigDecimal bd = getValue(COLUMNNAME_OverUnderAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Over/Under Payment.
     *
     * @param OverUnderAmt Over-Payment (unallocated) or Under-Payment (partial payment) Amount
     */
    public void setOverUnderAmt(BigDecimal OverUnderAmt) {
        setValue(COLUMNNAME_OverUnderAmt, OverUnderAmt);
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
     * Set Payment amount.
     *
     * @param PayAmt Amount being paid
     */
    public void setPayAmt(BigDecimal PayAmt) {
        setValue(COLUMNNAME_PayAmt, PayAmt);
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
     * Set PO Number.
     *
     * @param PONum Purchase Order Number
     */
    public void setPONum(String PONum) {
        setValue(COLUMNNAME_PONum, PONum);
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValue(COLUMNNAME_Posted, Posted);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Processed);
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Processing);
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
     * Set Authorization Code.
     *
     * @param R_AuthCode Authorization Code returned
     */
    public void setAuthorizationCode(String R_AuthCode) {
        setValueNoCheck(COLUMNNAME_R_AuthCode, R_AuthCode);
    }

    /**
     * Get Address verified.
     *
     * @return This address has been verified
     */
    public String getAddressVerified() {
        return getValue(COLUMNNAME_R_AvsAddr);
    }

    /**
     * Set Address verified.
     *
     * @param R_AvsAddr This address has been verified
     */
    public void setAddressVerified(String R_AvsAddr) {

        setValueNoCheck(COLUMNNAME_R_AvsAddr, R_AvsAddr);
    }

    /**
     * Get Zip verified.
     *
     * @return The Zip Code has been verified
     */
    public String getVerifiedZip() {
        return getValue(COLUMNNAME_R_AvsZip);
    }

    /**
     * Set Zip verified.
     *
     * @param R_AvsZip The Zip Code has been verified
     */
    public void setVerifiedZip(String R_AvsZip) {

        setValueNoCheck(COLUMNNAME_R_AvsZip, R_AvsZip);
    }

    /**
     * Get CVV Match.
     *
     * @return Credit Card Verification Code Match
     */
    public boolean isCVV2Match() {
        Object oo = getValue(COLUMNNAME_R_CVV2Match);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set CVV Match.
     *
     * @param R_CVV2Match Credit Card Verification Code Match
     */
    public void setCVV2Match(boolean R_CVV2Match) {
        setValueNoCheck(COLUMNNAME_R_CVV2Match, R_CVV2Match);
    }

    /**
     * Get Referenced Payment.
     *
     * @return Referenced Payment
     */
    public int getReferencedPaymentId() {
        Integer ii = getValue(COLUMNNAME_Ref_Payment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Referenced Payment.
     *
     * @param Ref_Payment_ID Referenced Payment
     */
    public void setReferencedPaymentId(int Ref_Payment_ID) {
        if (Ref_Payment_ID < 1) setValueNoCheck(COLUMNNAME_Ref_Payment_ID, null);
        else setValueNoCheck(COLUMNNAME_Ref_Payment_ID, Ref_Payment_ID);
    }

    public org.compiere.model.I_C_Payment getReversal() throws RuntimeException {
        return (org.compiere.model.I_C_Payment)
                MBaseTableKt.getTable(org.compiere.model.I_C_Payment.Table_Name)
                        .getPO(getReversalId());
    }

    /**
     * Get Reversal ID.
     *
     * @return ID of document reversal
     */
    public int getReversalId() {
        Integer ii = getValue(COLUMNNAME_Reversal_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Reversal ID.
     *
     * @param Reversal_ID ID of document reversal
     */
    public void setReversalId(int Reversal_ID) {
        if (Reversal_ID < 1) setValue(COLUMNNAME_Reversal_ID, null);
        else setValue(COLUMNNAME_Reversal_ID, Reversal_ID);
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
     * Set Info.
     *
     * @param R_Info Response info
     */
    public void setResponseInfo(String R_Info) {
        setValueNoCheck(COLUMNNAME_R_Info, R_Info);
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
     * Set Routing No.
     *
     * @param RoutingNo Bank Routing Number
     */
    public void setRoutingNo(String RoutingNo) {
        setValue(COLUMNNAME_RoutingNo, RoutingNo);
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
     * Set Reference.
     *
     * @param R_PnRef Payment reference
     */
    public void setPaymentReference(String R_PnRef) {
        setValueNoCheck(COLUMNNAME_R_PnRef, R_PnRef);
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
     * Set Response Message.
     *
     * @param R_RespMsg Response message
     */
    public void setResponseMessage(String R_RespMsg) {
        setValueNoCheck(COLUMNNAME_R_RespMsg, R_RespMsg);
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
     * Set Result.
     *
     * @param R_Result Result of transmission
     */
    public void setTransmissionResult(String R_Result) {
        setValueNoCheck(COLUMNNAME_R_Result, R_Result);
    }

    /**
     * Get Void Message.
     *
     * @return Void Message
     */
    public String getVoidMessage() {
        return getValue(COLUMNNAME_R_VoidMsg);
    }

    /**
     * Set Void Message.
     *
     * @param R_VoidMsg Void Message
     */
    public void setVoidMessage(String R_VoidMsg) {
        setValue(COLUMNNAME_R_VoidMsg, R_VoidMsg);
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
     * Set Swift code.
     *
     * @param SwiftCode Swift Code or BIC
     */
    public void setSwiftCode(String SwiftCode) {
        setValue(COLUMNNAME_SwiftCode, SwiftCode);
    }

    /**
     * Set Swipe.
     *
     * @param Swipe Track 1 and 2 of the Credit Card
     */
    public void setSwipe(String Swipe) {
        setValueNoCheck(COLUMNNAME_Swipe, Swipe);
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
     * Set Tax Amount.
     *
     * @param TaxAmt Tax Amount for a document
     */
    public void setTaxAmt(BigDecimal TaxAmt) {
        setValue(COLUMNNAME_TaxAmt, TaxAmt);
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
     * Set Tender type.
     *
     * @param TenderType Method of Payment
     */
    public void setTenderType(String TenderType) {

        setValue(COLUMNNAME_TenderType, TenderType);
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
     * Set Transaction Type.
     *
     * @param TrxType Type of credit card transaction
     */
    public void setTrxType(String TrxType) {

        setValue(COLUMNNAME_TrxType, TrxType);
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1Id() {
        Integer ii = getValue(COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValue(COLUMNNAME_User1_ID, null);
        else setValue(COLUMNNAME_User1_ID, User1_ID);
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValue(COLUMNNAME_User2_ID, null);
        else setValue(COLUMNNAME_User2_ID, User2_ID);
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
     * Set Voice authorization code.
     *
     * @param VoiceAuthCode Voice Authorization Code from credit card company
     */
    public void setVoiceAuthCode(String VoiceAuthCode) {
        setValue(COLUMNNAME_VoiceAuthCode, VoiceAuthCode);
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

    /**
     * Set Write-off Amount.
     *
     * @param WriteOffAmt Amount to write-off
     */
    public void setWriteOffAmt(BigDecimal WriteOffAmt) {
        setValue(COLUMNNAME_WriteOffAmt, WriteOffAmt);
    }

    @Override
    public int getTableId() {
        return I_C_Payment.Table_ID;
    }
}
