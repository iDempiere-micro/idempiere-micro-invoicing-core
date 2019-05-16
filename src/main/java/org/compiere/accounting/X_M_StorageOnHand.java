package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_StorageOnHand;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for M_StorageOnHand
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_StorageOnHand extends PO implements I_M_StorageOnHand {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_StorageOnHand(int M_StorageOnHand_ID) {
        super(M_StorageOnHand_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_StorageOnHand(Row row) {
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
        return "X_M_StorageOnHand[" + getId() + "]";
    }

    /**
     * Set Date last inventory count.
     *
     * @param DateLastInventory Date of Last Inventory Count
     */
    public void setDateLastInventory(Timestamp DateLastInventory) {
        setValue(COLUMNNAME_DateLastInventory, DateLastInventory);
    }

    /**
     * Get Date Material Policy.
     *
     * @return Time used for LIFO and FIFO Material Policy
     */
    public Timestamp getDateMaterialPolicy() {
        return (Timestamp) getValue(COLUMNNAME_DateMaterialPolicy);
    }

    /**
     * Set Date Material Policy.
     *
     * @param DateMaterialPolicy Time used for LIFO and FIFO Material Policy
     */
    public void setDateMaterialPolicy(Timestamp DateMaterialPolicy) {
        setValueNoCheck(COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
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
        if (M_Locator_ID < 1) setValueNoCheck(COLUMNNAME_M_Locator_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
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
        else setValueNoCheck(COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Get On Hand Quantity.
     *
     * @return On Hand Quantity
     */
    public BigDecimal getQtyOnHand() {
        BigDecimal bd = getValue(COLUMNNAME_QtyOnHand);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set On Hand Quantity.
     *
     * @param QtyOnHand On Hand Quantity
     */
    public void setQtyOnHand(BigDecimal QtyOnHand) {
        setValueNoCheck(COLUMNNAME_QtyOnHand, QtyOnHand);
    }

    @Override
    public String getDateUpdatedISOFormat() {
        Timestamp updated = getUpdated();
        return updated == null ? null : updated.toInstant().toString();
    }
}
