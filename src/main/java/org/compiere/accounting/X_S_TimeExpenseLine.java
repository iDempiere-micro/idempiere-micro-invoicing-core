package org.compiere.accounting;

import org.compiere.model.I_S_TimeExpenseLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

public class X_S_TimeExpenseLine extends PO implements I_S_TimeExpenseLine, I_Persistent {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_S_TimeExpenseLine(Properties ctx, int S_TimeExpenseLine_ID) {
        super(ctx, S_TimeExpenseLine_ID);
        /**
         * if (S_TimeExpenseLine_ID == 0) { setDateExpense (new Timestamp( System.currentTimeMillis()
         * )); // @DateExpense@;@DateReport@ setIsInvoiced (false); setIsTimeReport (false); setLine
         * (0); // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM S_TimeExpenseLine WHERE
         * S_TimeExpense_ID=@S_TimeExpense_ID@ setProcessed (false); setS_TimeExpense_ID (0);
         * setS_TimeExpenseLine_ID (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_S_TimeExpenseLine(Properties ctx, ResultSet rs) {
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
    public int getC_Activity_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getC_BPartner_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getC_Campaign_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getC_Currency_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Invoice Line.
     *
     * @return Invoice Detail Line
     */
    public int getC_InvoiceLine_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_InvoiceLine_ID);
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
     * Get Converted Amount.
     *
     * @return Converted Amount
     */
    public BigDecimal getConvertedAmt() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ConvertedAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Converted Amount.
     *
     * @param ConvertedAmt Converted Amount
     */
    public void setConvertedAmt(BigDecimal ConvertedAmt) {
        set_Value(COLUMNNAME_ConvertedAmt, ConvertedAmt);
    }

    /**
     * Get Sales Order Line.
     *
     * @return Sales Order Line
     */
    public int getC_OrderLine_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_OrderLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Order Line.
     *
     * @param C_OrderLine_ID Sales Order Line
     */
    public void setC_OrderLine_ID(int C_OrderLine_ID) {
        if (C_OrderLine_ID < 1) set_ValueNoCheck(COLUMNNAME_C_OrderLine_ID, null);
        else set_ValueNoCheck(COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getC_Project_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project Phase.
     *
     * @return Phase of a Project
     */
    public int getC_ProjectPhase_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_ProjectPhase_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project Task.
     *
     * @return Actual Project Task in a Phase
     */
    public int getC_ProjectTask_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_ProjectTask_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get UOM.
     *
     * @return Unit of Measure
     */
    public int getC_UOM_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_C_UOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Expense Date.
     *
     * @return Date of expense
     */
    public Timestamp getDateExpense() {
        return (Timestamp) get_Value(COLUMNNAME_DateExpense);
    }

    /**
     * Set Expense Date.
     *
     * @param DateExpense Date of expense
     */
    public void setDateExpense(Timestamp DateExpense) {
        set_Value(COLUMNNAME_DateExpense, DateExpense);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return (String) get_Value(COLUMNNAME_Description);
    }

    /**
     * Get Expense Amount.
     *
     * @return Amount for this expense
     */
    public BigDecimal getExpenseAmt() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ExpenseAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Expense Amount.
     *
     * @param ExpenseAmt Amount for this expense
     */
    public void setExpenseAmt(BigDecimal ExpenseAmt) {
        set_Value(COLUMNNAME_ExpenseAmt, ExpenseAmt);
    }

    /**
     * Get Invoice Price.
     *
     * @return Unit price to be invoiced or 0 for default price
     */
    public BigDecimal getInvoicePrice() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_InvoicePrice);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Invoice Price.
     *
     * @param InvoicePrice Unit price to be invoiced or 0 for default price
     */
    public void setInvoicePrice(BigDecimal InvoicePrice) {
        set_Value(COLUMNNAME_InvoicePrice, InvoicePrice);
    }

    /**
     * Set Invoiced.
     *
     * @param IsInvoiced Is this invoiced?
     */
    public void setIsInvoiced(boolean IsInvoiced) {
        set_Value(COLUMNNAME_IsInvoiced, Boolean.valueOf(IsInvoiced));
    }

    /**
     * Get Invoiced.
     *
     * @return Is this invoiced?
     */
    public boolean isInvoiced() {
        Object oo = get_Value(COLUMNNAME_IsInvoiced);
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
        set_Value(COLUMNNAME_IsTimeReport, Boolean.valueOf(IsTimeReport));
    }

    /**
     * Get Time Report.
     *
     * @return Line is a time report only (no expense)
     */
    public boolean isTimeReport() {
        Object oo = get_Value(COLUMNNAME_IsTimeReport);
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
        Integer ii = (Integer) get_Value(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        set_Value(COLUMNNAME_Line, Integer.valueOf(Line));
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getM_Product_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_M_Product_ID);
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
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_PriceInvoiced);
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
        set_Value(COLUMNNAME_PriceInvoiced, PriceInvoiced);
    }

    /**
     * Get Price Reimbursed.
     *
     * @return The reimbursed price (in currency of the employee's AP price list)
     */
    public BigDecimal getPriceReimbursed() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_PriceReimbursed);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Price Reimbursed.
     *
     * @param PriceReimbursed The reimbursed price (in currency of the employee's AP price list)
     */
    public void setPriceReimbursed(BigDecimal PriceReimbursed) {
        set_Value(COLUMNNAME_PriceReimbursed, PriceReimbursed);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        set_Value(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Quantity.
     *
     * @return Quantity
     */
    public BigDecimal getQty() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Qty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        set_Value(COLUMNNAME_Qty, Qty);
    }

    /**
     * Get Quantity Invoiced.
     *
     * @return Invoiced Quantity
     */
    public BigDecimal getQtyInvoiced() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_QtyInvoiced);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Invoiced.
     *
     * @param QtyInvoiced Invoiced Quantity
     */
    public void setQtyInvoiced(BigDecimal QtyInvoiced) {
        set_Value(COLUMNNAME_QtyInvoiced, QtyInvoiced);
    }

    /**
     * Get Quantity Reimbursed.
     *
     * @return The reimbursed quantity
     */
    public BigDecimal getQtyReimbursed() {
        BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_QtyReimbursed);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Reimbursed.
     *
     * @param QtyReimbursed The reimbursed quantity
     */
    public void setQtyReimbursed(BigDecimal QtyReimbursed) {
        set_Value(COLUMNNAME_QtyReimbursed, QtyReimbursed);
    }

    /**
     * Get Resource Assignment.
     *
     * @return Resource Assignment
     */
    public int getS_ResourceAssignment_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_S_ResourceAssignment_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Expense Report.
     *
     * @return Time and Expense Report
     */
    public int getS_TimeExpense_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_S_TimeExpense_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Expense Line.
     *
     * @return Time and Expense Report Line
     */
    public int getS_TimeExpenseLine_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_S_TimeExpenseLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Time Type.
     *
     * @return Type of time recorded
     */
    public int getS_TimeType_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_S_TimeType_ID);
        if (ii == null) return 0;
        return ii;
    }
}
