package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_ProjectLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for C_ProjectLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_ProjectLine extends PO implements I_C_ProjectLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_ProjectLine(int C_ProjectLine_ID) {
        super(C_ProjectLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_ProjectLine(Row row) {
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

    public String toString() {
        return "X_C_ProjectLine[" + getId() + "]";
    }

    /**
     * Set Committed Quantity.
     *
     * @param CommittedQty The (legal) commitment Quantity
     */
    public void setCommittedQty(BigDecimal CommittedQty) {
        setValue(COLUMNNAME_CommittedQty, CommittedQty);
    }

    /**
     * Set Order.
     *
     * @param C_Order_ID Order
     */
    public void setOrderId(int C_Order_ID) {
        if (C_Order_ID < 1) setValueNoCheck(COLUMNNAME_C_Order_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Order_ID, C_Order_ID);
    }

    /**
     * Get Purchase Order.
     *
     * @return Purchase Order
     */
    public int getOrderPOId() {
        Integer ii = getValue(COLUMNNAME_C_OrderPO_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Purchase Order.
     *
     * @param C_OrderPO_ID Purchase Order
     */
    public void setOrderPOId(int C_OrderPO_ID) {
        if (C_OrderPO_ID < 1) setValueNoCheck(COLUMNNAME_C_OrderPO_ID, null);
        else setValueNoCheck(COLUMNNAME_C_OrderPO_ID, C_OrderPO_ID);
    }

    public org.compiere.model.I_C_Project getProject() throws RuntimeException {
        return (org.compiere.model.I_C_Project)
                MBaseTableKt.getTable(org.compiere.model.I_C_Project.Table_Name)
                        .getPO(getProjectId());
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
        if (C_Project_ID < 1) setValueNoCheck(COLUMNNAME_C_Project_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Project_ID, C_Project_ID);
    }

    /**
     * Get Project Issue.
     *
     * @return Project Issues (Material, Labor)
     */
    public int getProjectIssueId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectIssue_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project Issue.
     *
     * @param C_ProjectIssue_ID Project Issues (Material, Labor)
     */
    public void setProjectIssueId(int C_ProjectIssue_ID) {
        if (C_ProjectIssue_ID < 1) setValueNoCheck(COLUMNNAME_C_ProjectIssue_ID, null);
        else setValueNoCheck(COLUMNNAME_C_ProjectIssue_ID, C_ProjectIssue_ID);
    }

    /**
     * Get Project Line.
     *
     * @return Task or step in a project
     */
    public int getProjectLineId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectLine_ID);
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
     * Set Project Phase.
     *
     * @param C_ProjectPhase_ID Phase of a Project
     */
    public void setProjectPhaseId(int C_ProjectPhase_ID) {
        if (C_ProjectPhase_ID < 1) setValue(COLUMNNAME_C_ProjectPhase_ID, null);
        else setValue(COLUMNNAME_C_ProjectPhase_ID, C_ProjectPhase_ID);
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
     * Set Project Task.
     *
     * @param C_ProjectTask_ID Actual Project Task in a Phase
     */
    public void setProjectTaskId(int C_ProjectTask_ID) {
        if (C_ProjectTask_ID < 1) setValue(COLUMNNAME_C_ProjectTask_ID, null);
        else setValue(COLUMNNAME_C_ProjectTask_ID, C_ProjectTask_ID);
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
     * Set Invoiced Amount.
     *
     * @param InvoicedAmt The amount invoiced
     */
    public void setInvoicedAmt(BigDecimal InvoicedAmt) {
        setValue(COLUMNNAME_InvoicedAmt, InvoicedAmt);
    }

    /**
     * Get Quantity Invoiced .
     *
     * @return The quantity invoiced
     */
    public BigDecimal getInvoicedQty() {
        BigDecimal bd = getValue(COLUMNNAME_InvoicedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Invoiced .
     *
     * @param InvoicedQty The quantity invoiced
     */
    public void setInvoicedQty(BigDecimal InvoicedQty) {
        setValue(COLUMNNAME_InvoicedQty, InvoicedQty);
    }

    /**
     * Set Printed.
     *
     * @param IsPrinted Indicates if this document / line is printed
     */
    public void setIsPrinted(boolean IsPrinted) {
        setValue(COLUMNNAME_IsPrinted, IsPrinted);
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
        setValue(COLUMNNAME_Line, Line);
    }

    /**
     * Get Product Category.
     *
     * @return Category of a Product
     */
    public int getProductCategoryId() {
        Integer ii = getValue(COLUMNNAME_M_Product_Category_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Get Production.
     *
     * @return Plan for producing a product
     */
    public int getProductionId() {
        Integer ii = getValue(COLUMNNAME_M_Production_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Production.
     *
     * @param M_Production_ID Plan for producing a product
     */
    public void setProductionId(int M_Production_ID) {
        if (M_Production_ID < 1) setValueNoCheck(COLUMNNAME_M_Production_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Production_ID, M_Production_ID);
    }

    /**
     * Set Planned Amount.
     *
     * @param PlannedAmt Planned amount for this project
     */
    public void setPlannedAmt(BigDecimal PlannedAmt) {
        setValue(COLUMNNAME_PlannedAmt, PlannedAmt);
    }

    /**
     * Set Planned Margin.
     *
     * @param PlannedMarginAmt Project's planned margin amount
     */
    public void setPlannedMarginAmt(BigDecimal PlannedMarginAmt) {
        setValue(COLUMNNAME_PlannedMarginAmt, PlannedMarginAmt);
    }

    /**
     * Get Planned Price.
     *
     * @return Planned price for this project line
     */
    public BigDecimal getPlannedPrice() {
        BigDecimal bd = getValue(COLUMNNAME_PlannedPrice);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Planned Price.
     *
     * @param PlannedPrice Planned price for this project line
     */
    public void setPlannedPrice(BigDecimal PlannedPrice) {
        setValue(COLUMNNAME_PlannedPrice, PlannedPrice);
    }

    /**
     * Get Planned Quantity.
     *
     * @return Planned quantity for this project
     */
    public BigDecimal getPlannedQty() {
        BigDecimal bd = getValue(COLUMNNAME_PlannedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Planned Quantity.
     *
     * @param PlannedQty Planned quantity for this project
     */
    public void setPlannedQty(BigDecimal PlannedQty) {
        setValue(COLUMNNAME_PlannedQty, PlannedQty);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Processed);
    }

    @Override
    public int getTableId() {
        return I_C_ProjectLine.Table_ID;
    }
}
