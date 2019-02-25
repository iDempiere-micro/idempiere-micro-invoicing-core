package org.compiere.invoicing;

import org.compiere.model.I_C_InvoiceBatchLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

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
    public X_C_InvoiceBatchLine(Properties ctx, int C_InvoiceBatchLine_ID) {
        super(ctx, C_InvoiceBatchLine_ID);
        /**
         * if (C_InvoiceBatchLine_ID == 0) { setC_BPartner_ID (0); // @C_BPartner_ID@
         * setC_BPartner_Location_ID (0); // @C_BPartner_Location_ID@ setC_Charge_ID (0);
         * setC_DocType_ID (0); // @C_DocType_ID@ setC_InvoiceBatch_ID (0); setC_InvoiceBatchLine_ID
         * (0); setC_Tax_ID (0); setDateAcct (new Timestamp( System.currentTimeMillis() ));
         * // @DateAcct@;@DateDoc@ setDateInvoiced (new Timestamp( System.currentTimeMillis() ));
         * // @DateInvoiced@;@DateDoc@ setDocumentNo (null); // @DocumentNo@ setIsTaxIncluded (false);
         * // @IsTaxIncluded@ setLine (0); // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM
         * C_InvoiceBatchLine WHERE C_InvoiceBatch_ID=@C_InvoiceBatch_ID@ setLineNetAmt (Env.ZERO);
         * setLineTotalAmt (Env.ZERO); setPriceEntered (Env.ZERO); setProcessed (false); setQtyEntered
         * (Env.ZERO); // 1 setTaxAmt (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_InvoiceBatchLine(Properties ctx, ResultSet rs) {
        super(ctx, rs);
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
        StringBuffer sb = new StringBuffer("X_C_InvoiceBatchLine[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getAD_OrgTrx_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getAD_User_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getC_Activity_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getC_BPartner_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Partner Location.
     *
     * @return Identifies the (ship to) address for this Business Partner
     */
    public int getC_BPartner_Location_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_BPartner_Location_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getC_Charge_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getC_DocType_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Invoice Batch.
     *
     * @return Expense Invoice Batch Header
     */
    public int getC_InvoiceBatch_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_InvoiceBatch_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getC_Invoice_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setC_Invoice_ID(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) set_ValueNoCheck(COLUMNNAME_C_Invoice_ID, null);
        else set_ValueNoCheck(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    /**
     * Get Invoice Line.
     *
     * @return Invoice Detail Line
     */
    public int getC_InvoiceLine_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_InvoiceLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice Line.
     *
     * @param C_InvoiceLine_ID Invoice Detail Line
     */
    public void setC_InvoiceLine_ID(int C_InvoiceLine_ID) {
        if (C_InvoiceLine_ID < 1) set_ValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, null);
        else set_ValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getC_Project_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Tax.
     *
     * @return Tax identifier
     */
    public int getC_Tax_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Tax_ID);
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
        set_Value(COLUMNNAME_DateAcct, DateAcct);
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
        set_Value(COLUMNNAME_DateInvoiced, DateInvoiced);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return (String) getValue(COLUMNNAME_Description);
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return (String) getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Price includes Tax.
     *
     * @param IsTaxIncluded Tax is included in the price
     */
    public void setIsTaxIncluded(boolean IsTaxIncluded) {
        set_Value(COLUMNNAME_IsTaxIncluded, Boolean.valueOf(IsTaxIncluded));
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
        Integer ii = (Integer) getValue(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Line Amount.
     *
     * @return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public BigDecimal getLineNetAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_LineNetAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Amount.
     *
     * @param LineNetAmt Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public void setLineNetAmt(BigDecimal LineNetAmt) {
        set_ValueNoCheck(COLUMNNAME_LineNetAmt, LineNetAmt);
    }

    /**
     * Get Line Total.
     *
     * @return Total line amount incl. Tax
     */
    public BigDecimal getLineTotalAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_LineTotalAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Total.
     *
     * @param LineTotalAmt Total line amount incl. Tax
     */
    public void setLineTotalAmt(BigDecimal LineTotalAmt) {
        set_ValueNoCheck(COLUMNNAME_LineTotalAmt, LineTotalAmt);
    }

    /**
     * Get Price.
     *
     * @return Price Entered - the price based on the selected/base UoM
     */
    public BigDecimal getPriceEntered() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_PriceEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Price.
     *
     * @param PriceEntered Price Entered - the price based on the selected/base UoM
     */
    public void setPriceEntered(BigDecimal PriceEntered) {
        set_Value(COLUMNNAME_PriceEntered, PriceEntered);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        set_ValueNoCheck(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Quantity.
     *
     * @return The Quantity Entered is based on the selected UoM
     */
    public BigDecimal getQtyEntered() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_QtyEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param QtyEntered The Quantity Entered is based on the selected UoM
     */
    public void setQtyEntered(BigDecimal QtyEntered) {
        set_Value(COLUMNNAME_QtyEntered, QtyEntered);
    }

    /**
     * Get Tax Amount.
     *
     * @return Tax Amount for a document
     */
    public BigDecimal getTaxAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_TaxAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Tax Amount.
     *
     * @param TaxAmt Tax Amount for a document
     */
    public void setTaxAmt(BigDecimal TaxAmt) {
        set_Value(COLUMNNAME_TaxAmt, TaxAmt);
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_C_InvoiceBatchLine.Table_ID;
    }
}
