package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_POSPayment;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_POSPayment
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_POSPayment extends PO implements I_C_POSPayment {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_POSPayment(int C_POSPayment_ID) {
        super(C_POSPayment_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_POSPayment(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 3 - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }


    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_C_POSPayment[" + getId() + "]";
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
     * Get Account Name.
     *
     * @return Name on Credit Card or Account holder
     */
    public String getAccountName() {
        return getValue(COLUMNNAME_A_Name);
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
     * Set Payment.
     *
     * @param C_Payment_ID Payment identifier
     */
    public void setPaymentId(int C_Payment_ID) {
        if (C_Payment_ID < 1) setValue(COLUMNNAME_C_Payment_ID, null);
        else setValue(COLUMNNAME_C_Payment_ID, Integer.valueOf(C_Payment_ID));
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
     * Get IBAN.
     *
     * @return International Bank Account Number
     */
    public String getIBAN() {
        return getValue(COLUMNNAME_IBAN);
    }

    /**
     * Get Post Dated.
     *
     * @return Post Dated
     */
    public boolean isPostDated() {
        Object oo = getValue(COLUMNNAME_IsPostDated);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
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
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Boolean.valueOf(Processed));
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
     * Get Swift code.
     *
     * @return Swift Code or BIC
     */
    public String getSwiftCode() {
        return getValue(COLUMNNAME_SwiftCode);
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
     * Get Voice authorization code.
     *
     * @return Voice Authorization Code from credit card company
     */
    public String getVoiceAuthCode() {
        return getValue(COLUMNNAME_VoiceAuthCode);
    }
}
