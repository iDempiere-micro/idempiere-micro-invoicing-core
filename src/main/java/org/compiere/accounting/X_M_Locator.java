package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_Locator;
import org.compiere.orm.PO;

/**
 * Generated Model for M_Locator
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_Locator extends PO implements I_M_Locator {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Locator(int M_Locator_ID) {
        super(M_Locator_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_Locator(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 7 - System - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_M_Locator[" + getId() + "]";
    }

    /**
     * Set Default.
     *
     * @param IsDefault Default value
     */
    public void setIsDefault(boolean IsDefault) {
        setValue(COLUMNNAME_IsDefault, IsDefault);
    }

    /**
     * Get Default.
     *
     * @return Default value
     */
    public boolean isDefault() {
        Object oo = getValue(COLUMNNAME_IsDefault);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Get Locator Type.
     *
     * @return Locator Type
     */
    public int getLocatorTypeId() {
        Integer ii = getValue(COLUMNNAME_M_LocatorType_ID);
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
     * Set Warehouse.
     *
     * @param M_Warehouse_ID Storage Warehouse and Service Point
     */
    public void setWarehouseId(int M_Warehouse_ID) {
        if (M_Warehouse_ID < 1) setValueNoCheck(COLUMNNAME_M_Warehouse_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
    }

    /**
     * Set Relative Priority.
     *
     * @param PriorityNo Where inventory should be picked from first
     */
    public void setPriorityNo(int PriorityNo) {
        setValue(COLUMNNAME_PriorityNo, Integer.valueOf(PriorityNo));
    }

    /**
     * Get Search Key.
     *
     * @return Search key for the record in the format required - must be unique
     */
    public String getValue() {
        return getValue(COLUMNNAME_Value);
    }

    /**
     * Set Search Key.
     *
     * @param Value Search key for the record in the format required - must be unique
     */
    public void setValue(String Value) {
        setValue(COLUMNNAME_Value, Value);
    }

    /**
     * Set Aisle (X).
     *
     * @param X X dimension, e.g., Aisle
     */
    public void setX(String X) {
        setValue(COLUMNNAME_X, X);
    }

    /**
     * Set Bin (Y).
     *
     * @param Y Y dimension, e.g., Bin
     */
    public void setY(String Y) {
        setValue(COLUMNNAME_Y, Y);
    }

    /**
     * Set Level (Z).
     *
     * @param Z Z dimension, e.g., Level
     */
    public void setZ(String Z) {
        setValue(COLUMNNAME_Z, Z);
    }

}
