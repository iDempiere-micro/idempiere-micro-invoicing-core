package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_CostDetail;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for M_CostDetail
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_CostDetail extends PO implements I_M_CostDetail {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_CostDetail(int M_CostDetail_ID) {
        super(M_CostDetail_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_CostDetail(Row row) {
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
        return "X_M_CostDetail[" + getId() + "]";
    }

    /**
     * Get Amount.
     *
     * @return Amount
     */
    public BigDecimal getAmt() {
        BigDecimal bd = getValue(COLUMNNAME_Amt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Amount.
     *
     * @param Amt Amount
     */
    public void setAmt(BigDecimal Amt) {
        setValue(COLUMNNAME_Amt, Amt);
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
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
     * Get Sales Order Line.
     *
     * @return Sales Order Line
     */
    public int getOrderLineId() {
        Integer ii = getValue(COLUMNNAME_C_OrderLine_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Accumulated Amt.
     *
     * @param CumulatedAmt Total Amount
     */
    public void setCumulatedAmt(BigDecimal CumulatedAmt) {
        setValue(COLUMNNAME_CumulatedAmt, CumulatedAmt);
    }

    /**
     * Set Accumulated Qty.
     *
     * @param CumulatedQty Total Quantity
     */
    public void setCumulatedQty(BigDecimal CumulatedQty) {
        setValue(COLUMNNAME_CumulatedQty, CumulatedQty);
    }

    /**
     * Set Current Cost Price.
     *
     * @param CurrentCostPrice The currently used cost price
     */
    public void setCurrentCostPrice(BigDecimal CurrentCostPrice) {
        setValue(COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
    }

    /**
     * Set Current Quantity.
     *
     * @param CurrentQty Current Quantity
     */
    public void setCurrentQty(BigDecimal CurrentQty) {
        setValue(COLUMNNAME_CurrentQty, CurrentQty);
    }

    /**
     * Get Delta Amount.
     *
     * @return Difference Amount
     */
    public BigDecimal getDeltaAmt() {
        BigDecimal bd = getValue(COLUMNNAME_DeltaAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Delta Amount.
     *
     * @param DeltaAmt Difference Amount
     */
    public void setDeltaAmt(BigDecimal DeltaAmt) {
        setValue(COLUMNNAME_DeltaAmt, DeltaAmt);
    }

    /**
     * Get Delta Quantity.
     *
     * @return Quantity Difference
     */
    public BigDecimal getDeltaQty() {
        BigDecimal bd = getValue(COLUMNNAME_DeltaQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Delta Quantity.
     *
     * @param DeltaQty Quantity Difference
     */
    public void setDeltaQty(BigDecimal DeltaQty) {
        setValue(COLUMNNAME_DeltaQty, DeltaQty);
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
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getAttributeSetInstanceId() {
        Integer ii = getValue(COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Attribute Set Instance.
     *
     * @param M_AttributeSetInstance_ID Product Attribute Set Instance
     */
    public void setAttributeSetInstanceId(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) setValueNoCheck(COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValueNoCheck(
                    COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
    }

    /**
     * Get Cost Detail.
     *
     * @return Cost Detail Information
     */
    public int getCostDetailId() {
        Integer ii = getValue(COLUMNNAME_M_CostDetail_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cost Element.
     *
     * @return Product Cost Element
     */
    public int getCostElementId() {
        Integer ii = getValue(COLUMNNAME_M_CostElement_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cost Element.
     *
     * @param M_CostElement_ID Product Cost Element
     */
    public void setCostElementId(int M_CostElement_ID) {
        if (M_CostElement_ID < 1) setValueNoCheck(COLUMNNAME_M_CostElement_ID, null);
        else setValueNoCheck(COLUMNNAME_M_CostElement_ID, Integer.valueOf(M_CostElement_ID));
    }

    /**
     * Get Shipment/Receipt Line.
     *
     * @return Line on Shipment or Receipt document
     */
    public int getInOutLineId() {
        Integer ii = getValue(COLUMNNAME_M_InOutLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Shipment/Receipt Line.
     *
     * @param M_InOutLine_ID Line on Shipment or Receipt document
     */
    public void setInOutLineId(int M_InOutLine_ID) {
        if (M_InOutLine_ID < 1) setValueNoCheck(COLUMNNAME_M_InOutLine_ID, null);
        else setValueNoCheck(COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
    }

    public org.compiere.model.I_M_InventoryLine getInventoryLine() throws RuntimeException {
        return (org.compiere.model.I_M_InventoryLine)
                MBaseTableKt.getTable(org.compiere.model.I_M_InventoryLine.Table_Name)
                        .getPO(getInventoryLineId());
    }

    /**
     * Get Phys.Inventory Line.
     *
     * @return Unique line in an Inventory document
     */
    public int getInventoryLineId() {
        Integer ii = getValue(COLUMNNAME_M_InventoryLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Phys.Inventory Line.
     *
     * @param M_InventoryLine_ID Unique line in an Inventory document
     */
    public void setInventoryLineId(int M_InventoryLine_ID) {
        if (M_InventoryLine_ID < 1) setValue(COLUMNNAME_M_InventoryLine_ID, null);
        else setValue(COLUMNNAME_M_InventoryLine_ID, M_InventoryLine_ID);
    }

    /**
     * Get Match Invoice.
     *
     * @return Match Shipment/Receipt to Invoice
     */
    public int getMatchInvoiceId() {
        Integer ii = getValue(COLUMNNAME_M_MatchInv_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Move Line.
     *
     * @return Inventory Move document Line
     */
    public int getMovementLineId() {
        Integer ii = getValue(COLUMNNAME_M_MovementLine_ID);
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
        if (M_Product_ID < 1) setValueNoCheck(COLUMNNAME_M_Product_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Get Production Line.
     *
     * @return Document Line representing a production
     */
    public int getProductionLineId() {
        Integer ii = getValue(COLUMNNAME_M_ProductionLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Manufacturing Cost Collector.
     *
     * @return Manufacturing Cost Collector
     */
    public int getManufacturingCostCollectorId() {
        Integer ii = getValue(COLUMNNAME_PP_Cost_Collector_ID);
        if (ii == null) return 0;
        return ii;
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
}
