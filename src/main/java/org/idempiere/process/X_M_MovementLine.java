package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_MovementLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

public abstract class X_M_MovementLine extends PO implements I_M_MovementLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_MovementLine(int M_MovementLine_ID) {
        super(M_MovementLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_MovementLine(Row row) {
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
        return "X_M_MovementLine[" + getId() + "]";
    }

    /**
     * Set Confirmed Quantity.
     *
     * @param ConfirmedQty Confirmation of a received quantity
     */
    public void setConfirmedQty(BigDecimal ConfirmedQty) {
        setValue(COLUMNNAME_ConfirmedQty, ConfirmedQty);
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

    /**
     * Set Attribute Set Instance To.
     *
     * @param M_AttributeSetInstanceTo_ID Target Product Attribute Set Instance
     */
    public void setAttributeSetInstanceToId(int M_AttributeSetInstanceTo_ID) {
        if (M_AttributeSetInstanceTo_ID < 1)
            setValueNoCheck(COLUMNNAME_M_AttributeSetInstanceTo_ID, null);
        else
            setValueNoCheck(
                    COLUMNNAME_M_AttributeSetInstanceTo_ID, M_AttributeSetInstanceTo_ID);
    }

    /**
     * Get Attribute Set Instance To.
     *
     * @return Target Product Attribute Set Instance
     */
    public int getMAttributeSetInstanceToId() {
        Integer ii = getValue(COLUMNNAME_M_AttributeSetInstanceTo_ID);
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

    /**
     * Get Locator To.
     *
     * @return Location inventory is moved to
     */
    public int getLocatorToId() {
        Integer ii = getValue(COLUMNNAME_M_LocatorTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Locator To.
     *
     * @param M_LocatorTo_ID Location inventory is moved to
     */
    public void setLocatorToId(int M_LocatorTo_ID) {
        if (M_LocatorTo_ID < 1) setValue(COLUMNNAME_M_LocatorTo_ID, null);
        else setValue(COLUMNNAME_M_LocatorTo_ID, M_LocatorTo_ID);
    }

    public org.compiere.model.I_M_Movement getMovement() throws RuntimeException {
        return (org.compiere.model.I_M_Movement)
                MBaseTableKt.getTable(org.compiere.model.I_M_Movement.Table_Name)
                        .getPO(getMovementId());
    }

    /**
     * Get Inventory Move.
     *
     * @return Movement of Inventory
     */
    public int getMovementId() {
        Integer ii = getValue(COLUMNNAME_M_Movement_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Inventory Move.
     *
     * @param M_Movement_ID Movement of Inventory
     */
    public void setMovementId(int M_Movement_ID) {
        if (M_Movement_ID < 1) setValueNoCheck(COLUMNNAME_M_Movement_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Movement_ID, M_Movement_ID);
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
     * Get Movement Quantity.
     *
     * @return Quantity of a product moved.
     */
    public BigDecimal getMovementQty() {
        BigDecimal bd = getValue(COLUMNNAME_MovementQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Movement Quantity.
     *
     * @param MovementQty Quantity of a product moved.
     */
    public void setMovementQty(BigDecimal MovementQty) {
        setValue(COLUMNNAME_MovementQty, MovementQty);
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
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Processed);
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

    /**
     * Set Scrapped Quantity.
     *
     * @param ScrappedQty The Quantity scrapped due to QA issues
     */
    public void setScrappedQty(BigDecimal ScrappedQty) {
        setValue(COLUMNNAME_ScrappedQty, ScrappedQty);
    }

    /**
     * Set Target Quantity.
     *
     * @param TargetQty Target Movement Quantity
     */
    public void setTargetQty(BigDecimal TargetQty) {
        setValue(COLUMNNAME_TargetQty, TargetQty);
    }

}
