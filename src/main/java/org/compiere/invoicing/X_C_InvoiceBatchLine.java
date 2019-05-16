package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_InvoiceBatchLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_InvoiceBatchLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_InvoiceBatchLine extends PO implements I_C_InvoiceBatchLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_InvoiceBatchLine(int C_InvoiceBatchLine_ID) {
        super(C_InvoiceBatchLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_InvoiceBatchLine(Row row) {
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
        return "X_C_InvoiceBatchLine[" + getId() + "]";
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getTransactionOrganizationId() {
        Integer ii = getValue(COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getUserId() {
        Integer ii = getValue(COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Partner Location.
     *
     * @return Identifies the (ship to) address for this Business Partner
     */
    public int getBusinessPartnerLocationId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_Location_ID);
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
        if (C_Invoice_ID < 1) setValueNoCheck(COLUMNNAME_C_Invoice_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    /**
     * Get Invoice Line.
     *
     * @return Invoice Detail Line
     */
    public int getInvoiceLineId() {
        Integer ii = getValue(COLUMNNAME_C_InvoiceLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice Line.
     *
     * @param C_InvoiceLine_ID Invoice Detail Line
     */
    public void setInvoiceLineId(int C_InvoiceLine_ID) {
        if (C_InvoiceLine_ID < 1) setValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, null);
        else setValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
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
     * Get Tax.
     *
     * @return Tax identifier
     */
    public int getTaxId() {
        Integer ii = getValue(COLUMNNAME_C_Tax_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Date Invoiced.
     *
     * @return Date printed on Invoice
     */
    public Timestamp getDateInvoiced() {
        return (Timestamp) getValue(COLUMNNAME_DateInvoiced);
    }

    /**
     * Set Date Invoiced.
     *
     * @param DateInvoiced Date printed on Invoice
     */
    public void setDateInvoiced(Timestamp DateInvoiced) {
        setValue(COLUMNNAME_DateInvoiced, DateInvoiced);
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
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Price includes Tax.
     *
     * @param IsTaxIncluded Tax is included in the price
     */
    public void setIsTaxIncluded(boolean IsTaxIncluded) {
        setValue(COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
    }

    /**
     * Get Price includes Tax.
     *
     * @return Tax is included in the price
     */
    public boolean isTaxIncluded() {
        Object oo = getValue(COLUMNNAME_IsTaxIncluded);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = getValue(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Line Amount.
     *
     * @return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public BigDecimal getLineNetAmt() {
        BigDecimal bd = getValue(COLUMNNAME_LineNetAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Amount.
     *
     * @param LineNetAmt Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public void setLineNetAmt(BigDecimal LineNetAmt) {
        setValueNoCheck(COLUMNNAME_LineNetAmt, LineNetAmt);
    }

    /**
     * Get Line Total.
     *
     * @return Total line amount incl. Tax
     */
    public BigDecimal getLineTotalAmt() {
        BigDecimal bd = getValue(COLUMNNAME_LineTotalAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Total.
     *
     * @param LineTotalAmt Total line amount incl. Tax
     */
    public void setLineTotalAmt(BigDecimal LineTotalAmt) {
        setValueNoCheck(COLUMNNAME_LineTotalAmt, LineTotalAmt);
    }

    /**
     * Get Price.
     *
     * @return Price Entered - the price based on the selected/base UoM
     */
    public BigDecimal getPriceEntered() {
        BigDecimal bd = getValue(COLUMNNAME_PriceEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Price.
     *
     * @param PriceEntered Price Entered - the price based on the selected/base UoM
     */
    public void setPriceEntered(BigDecimal PriceEntered) {
        setValue(COLUMNNAME_PriceEntered, PriceEntered);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValueNoCheck(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Quantity.
     *
     * @return The Quantity Entered is based on the selected UoM
     */
    public BigDecimal getQtyEntered() {
        BigDecimal bd = getValue(COLUMNNAME_QtyEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param QtyEntered The Quantity Entered is based on the selected UoM
     */
    public void setQtyEntered(BigDecimal QtyEntered) {
        setValue(COLUMNNAME_QtyEntered, QtyEntered);
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
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_C_InvoiceBatchLine.Table_ID;
    }
}
