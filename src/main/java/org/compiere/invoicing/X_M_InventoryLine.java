package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_M_InventoryLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for M_InventoryLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_InventoryLine extends PO implements I_M_InventoryLine {

    /**
     * Inventory Difference = D
     */
    public static final String INVENTORYTYPE_InventoryDifference = "D";
    /**
     * Charge Account = C
     */
    public static final String INVENTORYTYPE_ChargeAccount = "C";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_InventoryLine(int M_InventoryLine_ID) {
        super(M_InventoryLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_InventoryLine(Row row) {
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
        return "X_M_InventoryLine[" + getId() + "]";
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
     * Set Charge.
     *
     * @param C_Charge_ID Additional document charges
     */
    public void setChargeId(int C_Charge_ID) {
        if (C_Charge_ID < 1) setValue(COLUMNNAME_C_Charge_ID, null);
        else setValue(COLUMNNAME_C_Charge_ID, C_Charge_ID);
    }

    /**
     * Get Current Cost Price.
     *
     * @return The currently used cost price
     */
    public BigDecimal getCurrentCostPrice() {
        BigDecimal bd = getValue(COLUMNNAME_CurrentCostPrice);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Current Cost Price.
     *
     * @param CurrentCostPrice The currently used cost price
     */
    public void setCurrentCostPrice(BigDecimal CurrentCostPrice) {
        setValueNoCheck(COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
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
     * Get Inventory Type.
     *
     * @return Type of inventory difference
     */
    public String getInventoryType() {
        return getValue(COLUMNNAME_InventoryType);
    }

    /**
     * Set Inventory Type.
     *
     * @param InventoryType Type of inventory difference
     */
    public void setInventoryType(String InventoryType) {

        setValue(COLUMNNAME_InventoryType, InventoryType);
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
        if (M_AttributeSetInstance_ID < 0) setValue(COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValue(COLUMNNAME_M_AttributeSetInstance_ID, M_AttributeSetInstance_ID);
    }

    public org.compiere.model.I_M_Inventory getInventory() throws RuntimeException {
        return (org.compiere.model.I_M_Inventory)
                MBaseTableKt.getTable(org.compiere.model.I_M_Inventory.Table_Name)
                        .getPO(getInventoryId());
    }

    /**
     * Get Phys.Inventory.
     *
     * @return Parameters for a Physical Inventory
     */
    public int getInventoryId() {
        Integer ii = getValue(COLUMNNAME_M_Inventory_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Phys.Inventory.
     *
     * @param M_Inventory_ID Parameters for a Physical Inventory
     */
    public void setInventoryId(int M_Inventory_ID) {
        if (M_Inventory_ID < 1) setValueNoCheck(COLUMNNAME_M_Inventory_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Inventory_ID, M_Inventory_ID);
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
     * Get Locator.
     *
     * @return Warehouse Locator
     */
    public int getLocatorId() {
        Integer ii = getValue(COLUMNNAME_M_Locator_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Locator.
     *
     * @param M_Locator_ID Warehouse Locator
     */
    public void setLocatorId(int M_Locator_ID) {
        if (M_Locator_ID < 1) setValue(COLUMNNAME_M_Locator_ID, null);
        else setValue(COLUMNNAME_M_Locator_ID, M_Locator_ID);
    }

    public org.compiere.model.I_M_Product getProduct() throws RuntimeException {
        return (org.compiere.model.I_M_Product)
                MBaseTableKt.getTable(org.compiere.model.I_M_Product.Table_Name)
                        .getPO(getProductId());
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
     * Get New Cost Price.
     *
     * @return New current cost price after processing of M_CostDetail
     */
    public BigDecimal getNewCostPrice() {
        BigDecimal bd = getValue(COLUMNNAME_NewCostPrice);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set New Cost Price.
     *
     * @param NewCostPrice New current cost price after processing of M_CostDetail
     */
    public void setNewCostPrice(BigDecimal NewCostPrice) {
        setValue(COLUMNNAME_NewCostPrice, NewCostPrice);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Processed);
    }

    /**
     * Get Quantity book.
     *
     * @return Book Quantity
     */
    public BigDecimal getQtyBook() {
        BigDecimal bd = getValue(COLUMNNAME_QtyBook);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity book.
     *
     * @param QtyBook Book Quantity
     */
    public void setQtyBook(BigDecimal QtyBook) {
        setValueNoCheck(COLUMNNAME_QtyBook, QtyBook);
    }

    /**
     * Get Quantity count.
     *
     * @return Counted Quantity
     */
    public BigDecimal getQtyCount() {
        BigDecimal bd = getValue(COLUMNNAME_QtyCount);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity count.
     *
     * @param QtyCount Counted Quantity
     */
    public void setQtyCount(BigDecimal QtyCount) {
        setValue(COLUMNNAME_QtyCount, QtyCount);
    }

    /**
     * Get Internal Use Qty.
     *
     * @return Internal Use Quantity removed from Inventory
     */
    public BigDecimal getQtyInternalUse() {
        BigDecimal bd = getValue(COLUMNNAME_QtyInternalUse);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Internal Use Qty.
     *
     * @param QtyInternalUse Internal Use Quantity removed from Inventory
     */
    public void setQtyInternalUse(BigDecimal QtyInternalUse) {
        setValue(COLUMNNAME_QtyInternalUse, QtyInternalUse);
    }

    /**
     * Set Reversal Line.
     *
     * @param ReversalLine_ID Use to keep the reversal line ID for reversing costing purpose
     */
    public void setReversalLineId(int ReversalLine_ID) {
        if (ReversalLine_ID < 1) setValue(COLUMNNAME_ReversalLine_ID, null);
        else setValue(COLUMNNAME_ReversalLine_ID, ReversalLine_ID);
    }

    @Override
    public int getTableId() {
        return I_M_InventoryLine.Table_ID;
    }
}
