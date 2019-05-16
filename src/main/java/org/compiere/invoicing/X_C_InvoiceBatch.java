package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_InvoiceBatch;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_InvoiceBatch
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_InvoiceBatch extends PO implements I_C_InvoiceBatch {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_InvoiceBatch(int C_InvoiceBatch_ID) {
        super(C_InvoiceBatch_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_InvoiceBatch(Row row) {
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
        return "X_C_InvoiceBatch[" + getId() + "]";
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
     * Get Invoice Batch.
     *
     * @return Expense Invoice Batch Header
     */
    public int getInvoiceBatchId() {
        Integer ii = getValue(COLUMNNAME_C_InvoiceBatch_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Control Amount.
     *
     * @return If not zero, the Debit amount of the document must be equal this amount
     */
    public BigDecimal getControlAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ControlAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Control Amount.
     *
     * @param ControlAmt If not zero, the Debit amount of the document must be equal this amount
     */
    public void setControlAmt(BigDecimal ControlAmt) {
        setValue(COLUMNNAME_ControlAmt, ControlAmt);
    }

    /**
     * Set Document Date.
     *
     * @param DateDoc Date of the Document
     */
    public void setDateDoc(Timestamp DateDoc) {
        setValue(COLUMNNAME_DateDoc, DateDoc);
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
     * Get Document Amt.
     *
     * @return Document Amount
     */
    public BigDecimal getDocumentAmt() {
        BigDecimal bd = getValue(COLUMNNAME_DocumentAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Document Amt.
     *
     * @param DocumentAmt Document Amount
     */
    public void setDocumentAmt(BigDecimal DocumentAmt) {
        setValueNoCheck(COLUMNNAME_DocumentAmt, DocumentAmt);
    }

    /**
     * Set Sales Transaction.
     *
     * @param IsSOTrx This is a Sales Transaction
     */
    public void setIsSOTrx(boolean IsSOTrx) {
        setValue(COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
    }

    /**
     * Get Sales Transaction.
     *
     * @return This is a Sales Transaction
     */
    public boolean isSOTrx() {
        Object oo = getValue(COLUMNNAME_IsSOTrx);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Get Sales Representative.
     *
     * @return Sales Representative or Company Agent
     */
    public int getSalesRepresentativeId() {
        Integer ii = getValue(COLUMNNAME_SalesRep_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_C_InvoiceBatch.Table_ID;
    }
}
