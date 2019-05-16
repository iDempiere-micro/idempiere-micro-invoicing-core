package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_InvoiceTax;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_InvoiceTax
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_InvoiceTax extends PO {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_InvoiceTax(int C_InvoiceTax_ID) {
        super(C_InvoiceTax_ID);
        /**
         * if (C_InvoiceTax_ID == 0) { setInvoiceId (0); setTaxId (0); setIsTaxIncluded (false);
         * setProcessed (false); setTaxAmt (Env.ZERO); setTaxBaseAmt (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_InvoiceTax(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return I_C_InvoiceTax.accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_InvoiceTax[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = getValue(I_C_InvoiceTax.COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValueNoCheck(I_C_InvoiceTax.COLUMNNAME_C_Invoice_ID, null);
        else setValueNoCheck(I_C_InvoiceTax.COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    /**
     * Get Tax.
     *
     * @return Tax identifier
     */
    public int getTaxId() {
        Integer ii = getValue(I_C_InvoiceTax.COLUMNNAME_C_Tax_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Tax.
     *
     * @param C_Tax_ID Tax identifier
     */
    public void setTaxId(int C_Tax_ID) {
        if (C_Tax_ID < 1) setValueNoCheck(I_C_InvoiceTax.COLUMNNAME_C_Tax_ID, null);
        else setValueNoCheck(I_C_InvoiceTax.COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
    }

    /**
     * Set Price includes Tax.
     *
     * @param IsTaxIncluded Tax is included in the price
     */
    public void setIsTaxIncluded(boolean IsTaxIncluded) {
        setValue(I_C_InvoiceTax.COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
    }

    /**
     * Get Price includes Tax.
     *
     * @return Tax is included in the price
     */
    public boolean isTaxIncluded() {
        Object oo = getValue(I_C_InvoiceTax.COLUMNNAME_IsTaxIncluded);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Tax Amount.
     *
     * @return Tax Amount for a document
     */
    public BigDecimal getTaxAmt() {
        BigDecimal bd = getValue(I_C_InvoiceTax.COLUMNNAME_TaxAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Tax Amount.
     *
     * @param TaxAmt Tax Amount for a document
     */
    public void setTaxAmt(BigDecimal TaxAmt) {
        setValueNoCheck(I_C_InvoiceTax.COLUMNNAME_TaxAmt, TaxAmt);
    }

    /**
     * Get Tax base Amount.
     *
     * @return Base for calculating the tax amount
     */
    public BigDecimal getTaxBaseAmt() {
        BigDecimal bd = getValue(I_C_InvoiceTax.COLUMNNAME_TaxBaseAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Tax base Amount.
     *
     * @param TaxBaseAmt Base for calculating the tax amount
     */
    public void setTaxBaseAmt(BigDecimal TaxBaseAmt) {
        setValueNoCheck(I_C_InvoiceTax.COLUMNNAME_TaxBaseAmt, TaxBaseAmt);
    }

    @Override
    public int getTableId() {
        return I_C_InvoiceTax.Table_ID;
    }
}
