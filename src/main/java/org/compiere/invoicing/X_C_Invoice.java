package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.AccountingPO;
import org.compiere.model.DocumentType;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Currency;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_M_PriceList;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_Invoice
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_Invoice extends AccountingPO {

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
     * Cash = B
     */
    public static final String PAYMENTRULE_Cash = "B";
    /**
     * On Credit = P
     */
    public static final String PAYMENTRULE_OnCredit = "P";
    /**
     * Direct Debit = D
     */
    public static final String PAYMENTRULE_DirectDebit = "D";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard/Load  Constructor
     */
    public X_C_Invoice(Row row, int C_Invoice_ID) {
        super(row, C_Invoice_ID);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return I_C_Invoice.accessLevel.intValue();
    }

    public String toString() {
        return "X_C_Invoice[" + getId() + "]";
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getTransactionOrganizationId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setTransactionOrganizationId(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) setValue(I_C_Invoice.COLUMNNAME_AD_OrgTrx_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_AD_OrgTrx_ID, AD_OrgTrx_ID);
    }

    /**
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getUserId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User/Contact.
     *
     * @param AD_User_ID User within the system - Internal or Business Partner Contact
     */
    public void setUserId(int AD_User_ID) {
        if (AD_User_ID < 1) setValue(I_C_Invoice.COLUMNNAME_AD_User_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_AD_User_ID, AD_User_ID);
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_Activity_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_Activity_ID, C_Activity_ID);
    }

    public I_C_BPartner getBPartner() throws RuntimeException {
        return (I_C_BPartner)
                MBaseTableKt.getTable(I_C_BPartner.Table_Name)
                        .getPO(getBusinessPartnerId());
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_BPartner_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_BPartner_ID, C_BPartner_ID);
    }

    /**
     * Get Partner Location.
     *
     * @return Identifies the (ship to) address for this Business Partner
     */
    public int getBusinessPartnerLocationId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_BPartner_Location_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Partner Location.
     *
     * @param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
     */
    public void setBusinessPartnerLocationId(int C_BPartner_Location_ID) {
        if (C_BPartner_Location_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_BPartner_Location_ID, null);
        else
            setValue(
                    I_C_Invoice.COLUMNNAME_C_BPartner_Location_ID, C_BPartner_Location_ID);
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_Campaign_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_Campaign_ID, C_Campaign_ID);
    }

    /**
     * Get Cash Journal Line.
     *
     * @return Cash Journal Line
     */
    public int getCashLineId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_CashLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cash Journal Line.
     *
     * @param C_CashLine_ID Cash Journal Line
     */
    public void setCashLineId(int C_CashLine_ID) {
        if (C_CashLine_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_CashLine_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_CashLine_ID, C_CashLine_ID);
    }

    /**
     * Get Cash Plan Line.
     *
     * @return Cash Plan Line
     */
    public int getCashPlanLineId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_CashPlanLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cash Plan Line.
     *
     * @param C_CashPlanLine_ID Cash Plan Line
     */
    public void setCashPlanLineId(int C_CashPlanLine_ID) {
        if (C_CashPlanLine_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_CashPlanLine_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_CashPlanLine_ID, C_CashPlanLine_ID);
    }

    /**
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getChargeId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency Type.
     *
     * @return Currency Conversion Rate Type
     */
    public int getConversionTypeId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_ConversionType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency Type.
     *
     * @param C_ConversionType_ID Currency Conversion Rate Type
     */
    public void setConversionTypeId(int C_ConversionType_ID) {
        if (C_ConversionType_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_ConversionType_ID, null);
        else
            setValue(I_C_Invoice.COLUMNNAME_C_ConversionType_ID, C_ConversionType_ID);
    }

    public I_C_Currency getCurrency() throws RuntimeException {
        return (I_C_Currency)
                MBaseTableKt.getTable(I_C_Currency.Table_Name)
                        .getPO(getCurrencyId());
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_Currency_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_Currency_ID, C_Currency_ID);
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getDocumentTypeId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Document Type.
     *
     * @param C_DocType_ID Document type or rules
     */
    public void setDocumentTypeId(int C_DocType_ID) {
        if (C_DocType_ID < 0) setValueNoCheck(I_C_Invoice.COLUMNNAME_C_DocType_ID, null);
        else setValueNoCheck(I_C_Invoice.COLUMNNAME_C_DocType_ID, C_DocType_ID);
    }

    public DocumentType getDocTypeTarget() throws RuntimeException {
        return (DocumentType)
                MBaseTableKt.getTable(DocumentType.Table_Name)
                        .getPO(getTargetDocumentTypeId());
    }

    /**
     * Get Target Document Type.
     *
     * @return Target document type for conversing documents
     */
    public int getTargetDocumentTypeId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_DocTypeTarget_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Target Document Type.
     *
     * @param C_DocTypeTarget_ID Target document type for conversing documents
     */
    public void setTargetDocumentTypeId(int C_DocTypeTarget_ID) {
        if (C_DocTypeTarget_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_DocTypeTarget_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_DocTypeTarget_ID, C_DocTypeTarget_ID);
    }

    /**
     * Get Charge amount.
     *
     * @return Charge Amount
     */
    public BigDecimal getChargeAmt() {
        BigDecimal bd = getValue(I_C_Invoice.COLUMNNAME_ChargeAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Charge amount.
     *
     * @param ChargeAmt Charge Amount
     */
    public void setChargeAmt(BigDecimal ChargeAmt) {
        setValue(I_C_Invoice.COLUMNNAME_ChargeAmt, ChargeAmt);
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    public I_C_Order getOrder() throws RuntimeException {
        return (I_C_Order)
                MBaseTableKt.getTable(I_C_Order.Table_Name)
                        .getPO(getOrderId());
    }

    /**
     * Get Order.
     *
     * @return Order
     */
    public int getOrderId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Order.
     *
     * @param C_Order_ID Order
     */
    public void setOrderId(int C_Order_ID) {
        if (C_Order_ID < 1) setValueNoCheck(I_C_Invoice.COLUMNNAME_C_Order_ID, null);
        else setValueNoCheck(I_C_Invoice.COLUMNNAME_C_Order_ID, C_Order_ID);
    }

    /**
     * Get Payment.
     *
     * @return Payment identifier
     */
    public int getPaymentId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Payment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment.
     *
     * @param C_Payment_ID Payment identifier
     */
    public void setPaymentId(int C_Payment_ID) {
        if (C_Payment_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_Payment_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_Payment_ID, C_Payment_ID);
    }

    /**
     * Get Payment Term.
     *
     * @return The terms of Payment (timing, discount)
     */
    public int getPaymentTermId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_PaymentTerm_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment Term.
     *
     * @param C_PaymentTerm_ID The terms of Payment (timing, discount)
     */
    public void setPaymentTermId(int C_PaymentTerm_ID) {
        if (C_PaymentTerm_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_PaymentTerm_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_PaymentTerm_ID, C_PaymentTerm_ID);
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValue(I_C_Invoice.COLUMNNAME_C_Project_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_C_Project_ID, C_Project_ID);
    }

    /**
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(I_C_Invoice.COLUMNNAME_DateAcct);
    }

    /**
     * Set Account Date.
     *
     * @param DateAcct Accounting Date
     */
    public void setDateAcct(Timestamp DateAcct) {
        setValue(I_C_Invoice.COLUMNNAME_DateAcct, DateAcct);
    }

    /**
     * Get Date Invoiced.
     *
     * @return Date printed on Invoice
     */
    public Timestamp getDateInvoiced() {
        return (Timestamp) getValue(I_C_Invoice.COLUMNNAME_DateInvoiced);
    }

    /**
     * Set Date Invoiced.
     *
     * @param DateInvoiced Date printed on Invoice
     */
    public void setDateInvoiced(Timestamp DateInvoiced) {
        setValue(I_C_Invoice.COLUMNNAME_DateInvoiced, DateInvoiced);
    }

    /**
     * Get Date Ordered.
     *
     * @return Date of Order
     */
    public Timestamp getDateOrdered() {
        return (Timestamp) getValue(I_C_Invoice.COLUMNNAME_DateOrdered);
    }

    /**
     * Set Date Ordered.
     *
     * @param DateOrdered Date of Order
     */
    public void setDateOrdered(Timestamp DateOrdered) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_DateOrdered, DateOrdered);
    }

    /**
     * Set Date printed.
     *
     * @param DatePrinted Date the document was printed.
     */
    public void setDatePrinted(Timestamp DatePrinted) {
        setValue(I_C_Invoice.COLUMNNAME_DatePrinted, DatePrinted);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(I_C_Invoice.COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(I_C_Invoice.COLUMNNAME_Description, Description);
    }

    /**
     * Get Document Action.
     *
     * @return The targeted status of the document
     */
    public String getDocAction() {
        return getValue(I_C_Invoice.COLUMNNAME_DocAction);
    }

    /**
     * Set Document Action.
     *
     * @param DocAction The targeted status of the document
     */
    public void setDocAction(String DocAction) {

        setValue(I_C_Invoice.COLUMNNAME_DocAction, DocAction);
    }

    /**
     * Get Document Status.
     *
     * @return The current status of the document
     */
    public String getDocStatus() {
        return getValue(I_C_Invoice.COLUMNNAME_DocStatus);
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(String DocStatus) {

        setValue(I_C_Invoice.COLUMNNAME_DocStatus, DocStatus);
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(I_C_Invoice.COLUMNNAME_DocumentNo);
    }

    /**
     * Set Document No.
     *
     * @param DocumentNo Document sequence number of the document
     */
    public void setDocumentNo(String DocumentNo) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_DocumentNo, DocumentNo);
    }

    /**
     * Get Grand Total.
     *
     * @return Total amount of document
     */
    public BigDecimal getGrandTotal() {
        BigDecimal bd = getValue(I_C_Invoice.COLUMNNAME_GrandTotal);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Grand Total.
     *
     * @param GrandTotal Total amount of document
     */
    public void setGrandTotal(BigDecimal GrandTotal) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_GrandTotal, GrandTotal);
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_IsApproved, IsApproved);
    }

    /**
     * Get Approved.
     *
     * @return Indicates if this document requires approval
     */
    public boolean isApproved() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_IsApproved);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Discount Printed.
     *
     * @param IsDiscountPrinted Print Discount on Invoice and Order
     */
    public void setIsDiscountPrinted(boolean IsDiscountPrinted) {
        setValue(I_C_Invoice.COLUMNNAME_IsDiscountPrinted, IsDiscountPrinted);
    }

    /**
     * Set In Dispute.
     *
     * @param IsInDispute Document is in dispute
     */
    public void setIsInDispute(boolean IsInDispute) {
        setValue(I_C_Invoice.COLUMNNAME_IsInDispute, IsInDispute);
    }

    /**
     * Set Paid.
     *
     * @param IsPaid The document is paid
     */
    public void setIsPaid(boolean IsPaid) {
        setValue(I_C_Invoice.COLUMNNAME_IsPaid, IsPaid);
    }

    /**
     * Get Paid.
     *
     * @return The document is paid
     */
    public boolean isPaid() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_IsPaid);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Pay Schedule valid.
     *
     * @param IsPayScheduleValid Is the Payment Schedule is valid
     */
    public void setIsPayScheduleValid(boolean IsPayScheduleValid) {
        setValueNoCheck(
                I_C_Invoice.COLUMNNAME_IsPayScheduleValid, IsPayScheduleValid);
    }

    /**
     * Get Pay Schedule valid.
     *
     * @return Is the Payment Schedule is valid
     */
    public boolean isPayScheduleValid() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_IsPayScheduleValid);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Printed.
     *
     * @param IsPrinted Indicates if this document / line is printed
     */
    public void setIsPrinted(boolean IsPrinted) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_IsPrinted, IsPrinted);
    }

    /**
     * Set Self-Service.
     *
     * @param IsSelfService This is a Self-Service entry or this entry can be changed via Self-Service
     */
    public void setIsSelfService(boolean IsSelfService) {
        setValue(I_C_Invoice.COLUMNNAME_IsSelfService, IsSelfService);
    }

    /**
     * Set Sales Transaction.
     *
     * @param IsSOTrx This is a Sales Transaction
     */
    public void setIsSOTrx(boolean IsSOTrx) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_IsSOTrx, IsSOTrx);
    }

    /**
     * Get Sales Transaction.
     *
     * @return This is a Sales Transaction
     */
    public boolean isSOTrx() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_IsSOTrx);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Price includes Tax.
     *
     * @param IsTaxIncluded Tax is included in the price
     */
    public void setIsTaxIncluded(boolean IsTaxIncluded) {
        setValue(I_C_Invoice.COLUMNNAME_IsTaxIncluded, IsTaxIncluded);
    }

    /**
     * Get Price includes Tax.
     *
     * @return Tax is included in the price
     */
    public boolean isTaxIncluded() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_IsTaxIncluded);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Transferred.
     *
     * @param IsTransferred Transferred to General Ledger (i.e. accounted)
     */
    public void setIsTransferred(boolean IsTransferred) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_IsTransferred, IsTransferred);
    }

    public I_M_PriceList getPriceList() throws RuntimeException {
        return (I_M_PriceList)
                MBaseTableKt.getTable(I_M_PriceList.Table_Name)
                        .getPO(getPriceListId());
    }

    /**
     * Get Price List.
     *
     * @return Unique identifier of a Price List
     */
    public int getPriceListId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_M_PriceList_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Price List.
     *
     * @param M_PriceList_ID Unique identifier of a Price List
     */
    public void setPriceListId(int M_PriceList_ID) {
        if (M_PriceList_ID < 1) setValue(I_C_Invoice.COLUMNNAME_M_PriceList_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_M_PriceList_ID, M_PriceList_ID);
    }

    /**
     * Get RMA.
     *
     * @return Return Material Authorization
     */
    public int getRMAId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_M_RMA_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set RMA.
     *
     * @param M_RMA_ID Return Material Authorization
     */
    public void setRMAId(int M_RMA_ID) {
        if (M_RMA_ID < 1) setValue(I_C_Invoice.COLUMNNAME_M_RMA_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_M_RMA_ID, M_RMA_ID);
    }

    /**
     * Get Payment Rule.
     *
     * @return How you pay the invoice
     */
    public String getPaymentRule() {
        return getValue(I_C_Invoice.COLUMNNAME_PaymentRule);
    }

    /**
     * Set Payment Rule.
     *
     * @param PaymentRule How you pay the invoice
     */
    public void setPaymentRule(String PaymentRule) {

        setValue(I_C_Invoice.COLUMNNAME_PaymentRule, PaymentRule);
    }

    /**
     * Get Order Reference.
     *
     * @return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
     */
    public String getPOReference() {
        return getValue(I_C_Invoice.COLUMNNAME_POReference);
    }

    /**
     * Set Order Reference.
     *
     * @param POReference Transaction Reference Number (Sales Order, Purchase Order) of your Business
     *                    Partner
     */
    public void setPOReference(String POReference) {
        setValue(I_C_Invoice.COLUMNNAME_POReference, POReference);
    }

    /**
     * Get Posted.
     *
     * @return Posting status
     */
    public boolean isPosted() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_Posted);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValue(I_C_Invoice.COLUMNNAME_Posted, Posted);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(I_C_Invoice.COLUMNNAME_Processed);
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
        setValueNoCheck(I_C_Invoice.COLUMNNAME_Processed, Processed);
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(I_C_Invoice.COLUMNNAME_Processing, Processing);
    }

    /**
     * Get Referenced Invoice.
     *
     * @return Referenced Invoice
     */
    public int getRef_InvoiceId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_Ref_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Referenced Invoice.
     *
     * @param Ref_Invoice_ID Referenced Invoice
     */
    public void setRef_InvoiceId(int Ref_Invoice_ID) {
        if (Ref_Invoice_ID < 1) setValue(I_C_Invoice.COLUMNNAME_Ref_Invoice_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_Ref_Invoice_ID, Ref_Invoice_ID);
    }

    /**
     * Set Reversal ID.
     *
     * @param Reversal_ID ID of document reversal
     */
    public void setReversalId(int Reversal_ID) {
        if (Reversal_ID < 1) setValue(I_C_Invoice.COLUMNNAME_Reversal_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_Reversal_ID, Reversal_ID);
    }

    /**
     * Get Sales Representative.
     *
     * @return Sales Representative or Company Agent
     */
    public int getSalesRepresentativeId() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_SalesRep_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Representative.
     *
     * @param SalesRep_ID Sales Representative or Company Agent
     */
    public void setSalesRepresentativeId(int SalesRep_ID) {
        if (SalesRep_ID < 1) setValue(I_C_Invoice.COLUMNNAME_SalesRep_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_SalesRep_ID, SalesRep_ID);
    }

    /**
     * Set Send EMail.
     *
     * @param SendEMail Enable sending Document EMail
     */
    public void setSendEMail(boolean SendEMail) {
        setValue(I_C_Invoice.COLUMNNAME_SendEMail, SendEMail);
    }

    /**
     * Get Total Lines.
     *
     * @return Total of all document lines
     */
    public BigDecimal getTotalLines() {
        BigDecimal bd = getValue(I_C_Invoice.COLUMNNAME_TotalLines);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Total Lines.
     *
     * @param TotalLines Total of all document lines
     */
    public void setTotalLines(BigDecimal TotalLines) {
        setValueNoCheck(I_C_Invoice.COLUMNNAME_TotalLines, TotalLines);
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1Id() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValue(I_C_Invoice.COLUMNNAME_User1_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_User1_ID, User1_ID);
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(I_C_Invoice.COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValue(I_C_Invoice.COLUMNNAME_User2_ID, null);
        else setValue(I_C_Invoice.COLUMNNAME_User2_ID, User2_ID);
    }

    @Override
    public int getTableId() {
        return I_C_Invoice.Table_ID;
    }
}
