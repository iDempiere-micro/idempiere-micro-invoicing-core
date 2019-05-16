package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_I_Inventory;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_I_Inventory extends PO implements I_I_Inventory {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_Inventory(int I_Inventory_ID) {
        super(I_Inventory_ID);
    }

    /**
     * Load Constructor
     */
    public X_I_Inventory(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer("X_I_Inventory[").append(getId()).append("]");
        return sb.toString();
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Set Import Error Message.
     *
     * @param I_ErrorMsg Messages generated from import process
     */
    public void setImportErrorMsg(String I_ErrorMsg) {
        setValue(COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
    }

    /**
     * Set Imported.
     *
     * @param I_IsImported Has this import been processed
     */
    public void setIsImported(boolean I_IsImported) {
        setValue(COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
    }

    /**
     * Get Lot No.
     *
     * @return Lot number (alphanumeric)
     */
    public String getLot() {
        return getValue(COLUMNNAME_Lot);
    }

    /**
     * Set Cost Adjustment Line.
     *
     * @param M_CostingLine_ID Unique line in an Inventory cost adjustment document
     */
    public void setCostingLineId(int M_CostingLine_ID) {
        if (M_CostingLine_ID < 1) setValue(COLUMNNAME_M_CostingLine_ID, null);
        else setValue(COLUMNNAME_M_CostingLine_ID, Integer.valueOf(M_CostingLine_ID));
    }

    /**
     * Set Phys.Inventory.
     *
     * @param M_Inventory_ID Parameters for a Physical Inventory
     */
    public void setInventoryId(int M_Inventory_ID) {
        if (M_Inventory_ID < 1) setValue(COLUMNNAME_M_Inventory_ID, null);
        else setValue(COLUMNNAME_M_Inventory_ID, Integer.valueOf(M_Inventory_ID));
    }

    /**
     * Set Phys.Inventory Line.
     *
     * @param M_InventoryLine_ID Unique line in an Inventory document
     */
    public void setInventoryLineId(int M_InventoryLine_ID) {
        if (M_InventoryLine_ID < 1) setValue(COLUMNNAME_M_InventoryLine_ID, null);
        else setValue(COLUMNNAME_M_InventoryLine_ID, Integer.valueOf(M_InventoryLine_ID));
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
     * Get Movement Date.
     *
     * @return Date a product was moved in or out of inventory
     */
    public Timestamp getMovementDate() {
        return (Timestamp) getValue(COLUMNNAME_MovementDate);
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
     * Get Warehouse.
     *
     * @return Storage Warehouse and Service Point
     */
    public int getWarehouseId() {
        Integer ii = getValue(COLUMNNAME_M_Warehouse_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Serial No.
     *
     * @return Product Serial Number
     */
    public String getSerNo() {
        return getValue(COLUMNNAME_SerNo);
    }

}
