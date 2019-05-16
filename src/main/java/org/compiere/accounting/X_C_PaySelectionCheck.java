package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_PaySelectionCheck;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for C_PaySelectionCheck
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_PaySelectionCheck extends PO implements I_C_PaySelectionCheck {

    /**
     * Cash = B
     */
    public static final String PAYMENTRULE_Cash = "B";
    /**
     * Credit Card = K
     */
    public static final String PAYMENTRULE_CreditCard = "K";
    /**
     * Direct Deposit = T
     */
    public static final String PAYMENTRULE_DirectDeposit = "T";
    /**
     * Check = S
     */
    public static final String PAYMENTRULE_Check = "S";
    /**
     * Direct Debit = D
     */
    public static final String PAYMENTRULE_DirectDebit = "D";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_PaySelectionCheck(int C_PaySelectionCheck_ID) {
        super(C_PaySelectionCheck_ID);
        /**
         * if (C_PaySelectionCheck_ID == 0) { setBusinessPartnerId (0); setPaySelectionCheck_ID (0);
         * setPaySelectionId (0); setDiscountAmt (Env.ZERO); setIsGeneratedDraft (false); // N
         * setIsPrinted (false); setIsReceipt (false); setPayAmt (Env.ZERO); setPaymentRule (null);
         * setProcessed (false); // N setQty (0); setWriteOffAmt (Env.ZERO); // 0 }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_PaySelectionCheck(Row row) {
        super(row);
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
        StringBuffer sb = new StringBuffer("X_C_PaySelectionCheck[").append(getId()).append("]");
        return sb.toString();
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
        else setValue(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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
        else setValue(COLUMNNAME_C_BP_BankAccount_ID, Integer.valueOf(C_BP_BankAccount_ID));
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
     * Set Payment.
     *
     * @param C_Payment_ID Payment identifier
     */
    public void setPaymentId(int C_Payment_ID) {
        if (C_Payment_ID < 1) setValue(COLUMNNAME_C_Payment_ID, null);
        else setValue(COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
    }

    /**
     * Get Pay Selection Check.
     *
     * @return Payment Selection Check
     */
    public int getPaySelectionCheckId() {
        Integer ii = getValue(COLUMNNAME_C_PaySelectionCheck_ID);
        if (ii == null) return 0;
        return ii;
    }

    public org.compiere.model.I_C_PaySelection getPaySelection() throws RuntimeException {
        return (org.compiere.model.I_C_PaySelection)
                MBaseTableKt.getTable(org.compiere.model.I_C_PaySelection.Table_Name)
                        .getPO(getPaySelectionId());
    }

    /**
     * Get Payment Selection.
     *
     * @return Payment Selection
     */
    public int getPaySelectionId() {
        Integer ii = getValue(COLUMNNAME_C_PaySelection_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Payment Selection.
     *
     * @param C_PaySelection_ID Payment Selection
     */
    public void setPaySelectionId(int C_PaySelection_ID) {
        if (C_PaySelection_ID < 1) setValueNoCheck(COLUMNNAME_C_PaySelection_ID, null);
        else setValueNoCheck(COLUMNNAME_C_PaySelection_ID, C_PaySelection_ID);
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
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Get Generated Draft.
     *
     * @return Generated Draft
     */
    public boolean isGeneratedDraft() {
        Object oo = getValue(COLUMNNAME_IsGeneratedDraft);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
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
        setValue(COLUMNNAME_IsPrinted, Boolean.valueOf(IsPrinted));
    }

    /**
     * Set Receipt.
     *
     * @param IsReceipt This is a sales transaction (receipt)
     */
    public void setIsReceipt(boolean IsReceipt) {
        setValue(COLUMNNAME_IsReceipt, Boolean.valueOf(IsReceipt));
    }

    /**
     * Get Receipt.
     *
     * @return This is a sales transaction (receipt)
     */
    public boolean isReceipt() {
        Object oo = getValue(COLUMNNAME_IsReceipt);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Get Payment Rule.
     *
     * @return How you pay the invoice
     */
    public String getPaymentRule() {
        return getValue(COLUMNNAME_PaymentRule);
    }

    /**
     * Set Payment Rule.
     *
     * @param PaymentRule How you pay the invoice
     */
    public void setPaymentRule(String PaymentRule) {

        setValue(COLUMNNAME_PaymentRule, PaymentRule);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
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
        setValue(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Quantity.
     *
     * @return Quantity
     */
    public int getQty() {
        Integer ii = getValue(COLUMNNAME_Qty);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(int Qty) {
        setValue(COLUMNNAME_Qty, Integer.valueOf(Qty));
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
        return I_C_PaySelectionCheck.Table_ID;
    }
}
