package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_S_TimeExpenseLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_S_TimeExpenseLine extends PO implements I_S_TimeExpenseLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_S_TimeExpenseLine(int S_TimeExpenseLine_ID) {
        super(S_TimeExpenseLine_ID);
        /**
         * if (S_TimeExpenseLine_ID == 0) { setDateExpense (new Timestamp( System.currentTimeMillis()
         * )); // @DateExpense@;@DateReport@ setIsInvoiced (false); setIsTimeReport (false); setLine
         * (0); // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM S_TimeExpenseLine WHERE
         * S_TimeExpense_ID=@S_TimeExpense_ID@ setProcessed (false); setS_TimeExpense_ID (0);
         * setTimeExpenseLineId (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_S_TimeExpenseLine(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_S_TimeExpenseLine[").append(getId()).append("]");
        return sb.toString();
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
     * Get Converted Amount.
     *
     * @return Converted Amount
     */
    public BigDecimal getConvertedAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ConvertedAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Converted Amount.
     *
     * @param ConvertedAmt Converted Amount
     */
    public void setConvertedAmt(BigDecimal ConvertedAmt) {
        setValue(COLUMNNAME_ConvertedAmt, ConvertedAmt);
    }

    /**
     * Set Sales Order Line.
     *
     * @param C_OrderLine_ID Sales Order Line
     */
    public void setOrderLineId(int C_OrderLine_ID) {
        if (C_OrderLine_ID < 1) setValueNoCheck(COLUMNNAME_C_OrderLine_ID, null);
        else setValueNoCheck(COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
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
     * Get Project Phase.
     *
     * @return Phase of a Project
     */
    public int getProjectPhaseId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectPhase_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project Task.
     *
     * @return Actual Project Task in a Phase
     */
    public int getProjectTaskId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectTask_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get UOM.
     *
     * @return Unit of Measure
     */
    public int getUOMId() {
        Integer ii = getValue(COLUMNNAME_C_UOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Expense Date.
     *
     * @return Date of expense
     */
    public Timestamp getDateExpense() {
        return (Timestamp) getValue(COLUMNNAME_DateExpense);
    }

    /**
     * Set Expense Date.
     *
     * @param DateExpense Date of expense
     */
    public void setDateExpense(Timestamp DateExpense) {
        setValue(COLUMNNAME_DateExpense, DateExpense);
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
     * Get Expense Amount.
     *
     * @return Amount for this expense
     */
    public BigDecimal getExpenseAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ExpenseAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Expense Amount.
     *
     * @param ExpenseAmt Amount for this expense
     */
    public void setExpenseAmt(BigDecimal ExpenseAmt) {
        setValue(COLUMNNAME_ExpenseAmt, ExpenseAmt);
    }

    /**
     * Get Invoice Price.
     *
     * @return Unit price to be invoiced or 0 for default price
     */
    public BigDecimal getInvoicePrice() {
        BigDecimal bd = getValue(COLUMNNAME_InvoicePrice);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Invoice Price.
     *
     * @param InvoicePrice Unit price to be invoiced or 0 for default price
     */
    public void setInvoicePrice(BigDecimal InvoicePrice) {
        setValue(COLUMNNAME_InvoicePrice, InvoicePrice);
    }

    /**
     * Set Invoiced.
     *
     * @param IsInvoiced Is this invoiced?
     */
    public void setIsInvoiced(boolean IsInvoiced) {
        setValue(COLUMNNAME_IsInvoiced, Boolean.valueOf(IsInvoiced));
    }

    /**
     * Get Invoiced.
     *
     * @return Is this invoiced?
     */
    public boolean isInvoiced() {
        Object oo = getValue(COLUMNNAME_IsInvoiced);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Time Report.
     *
     * @param IsTimeReport Line is a time report only (no expense)
     */
    public void setIsTimeReport(boolean IsTimeReport) {
        setValue(COLUMNNAME_IsTimeReport, Boolean.valueOf(IsTimeReport));
    }

    /**
     * Get Time Report.
     *
     * @return Line is a time report only (no expense)
     */
    public boolean isTimeReport() {
        Object oo = getValue(COLUMNNAME_IsTimeReport);
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
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(COLUMNNAME_Line, Integer.valueOf(Line));
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Price Invoiced.
     *
     * @return The priced invoiced to the customer (in the currency of the customer's AR price list) -
     * 0 for default price
     */
    public BigDecimal getPriceInvoiced() {
        BigDecimal bd = getValue(COLUMNNAME_PriceInvoiced);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Price Invoiced.
     *
     * @param PriceInvoiced The priced invoiced to the customer (in the currency of the customer's AR
     *                      price list) - 0 for default price
     */
    public void setPriceInvoiced(BigDecimal PriceInvoiced) {
        setValue(COLUMNNAME_PriceInvoiced, PriceInvoiced);
    }

    /**
     * Get Price Reimbursed.
     *
     * @return The reimbursed price (in currency of the employee's AP price list)
     */
    public BigDecimal getPriceReimbursed() {
        BigDecimal bd = getValue(COLUMNNAME_PriceReimbursed);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Price Reimbursed.
     *
     * @param PriceReimbursed The reimbursed price (in currency of the employee's AP price list)
     */
    public void setPriceReimbursed(BigDecimal PriceReimbursed) {
        setValue(COLUMNNAME_PriceReimbursed, PriceReimbursed);
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
    public BigDecimal getQty() {
        BigDecimal bd = getValue(COLUMNNAME_Qty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        setValue(COLUMNNAME_Qty, Qty);
    }

    /**
     * Get Quantity Invoiced.
     *
     * @return Invoiced Quantity
     */
    public BigDecimal getQtyInvoiced() {
        BigDecimal bd = getValue(COLUMNNAME_QtyInvoiced);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Invoiced.
     *
     * @param QtyInvoiced Invoiced Quantity
     */
    public void setQtyInvoiced(BigDecimal QtyInvoiced) {
        setValue(COLUMNNAME_QtyInvoiced, QtyInvoiced);
    }

    /**
     * Get Quantity Reimbursed.
     *
     * @return The reimbursed quantity
     */
    public BigDecimal getQtyReimbursed() {
        BigDecimal bd = getValue(COLUMNNAME_QtyReimbursed);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Reimbursed.
     *
     * @param QtyReimbursed The reimbursed quantity
     */
    public void setQtyReimbursed(BigDecimal QtyReimbursed) {
        setValue(COLUMNNAME_QtyReimbursed, QtyReimbursed);
    }

    /**
     * Get Resource Assignment.
     *
     * @return Resource Assignment
     */
    public int getResourceAssignmentId() {
        Integer ii = getValue(COLUMNNAME_S_ResourceAssignment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Expense Report.
     *
     * @return Time and Expense Report
     */
    public int getTimeExpenseId() {
        Integer ii = getValue(COLUMNNAME_S_TimeExpense_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Expense Line.
     *
     * @return Time and Expense Report Line
     */
    public int getTimeExpenseLineId() {
        Integer ii = getValue(COLUMNNAME_S_TimeExpenseLine_ID);
        if (ii == null) return 0;
        return ii;
    }

}
