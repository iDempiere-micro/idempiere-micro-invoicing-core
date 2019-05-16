package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_I_FixedAsset;
import org.compiere.orm.BasePOName;

import java.sql.Timestamp;

/**
 * Generated Model for I_FixedAsset
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_I_FixedAsset extends BasePOName implements I_I_FixedAsset {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_FixedAsset(int I_FixedAsset_ID) {
        super(I_FixedAsset_ID);
    }

    /**
     * Load Constructor
     */
    public X_I_FixedAsset(Row row) {
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
        return "X_I_FixedAsset[" + getId() + "]";
    }

    /**
     * Get Asset Group.
     *
     * @return Group of Assets
     */
    public int getAssetGroupId() {
        Integer ii = getValue(COLUMNNAME_A_Asset_Group_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get In Service Date.
     *
     * @return Date when Asset was put into service
     */
    public Timestamp getAssetServiceDate() {
        return (Timestamp) getValue(COLUMNNAME_AssetServiceDate);
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Get Inventory No.
     *
     * @return Inventory No
     */
    public String getInventoryNo() {
        return getValue(COLUMNNAME_InventoryNo);
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
        else setValue(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Set Product Key.
     *
     * @param ProductValue Key of the Product
     */
    public void setProductValue(String ProductValue) {
        setValue(COLUMNNAME_ProductValue, ProductValue);
    }

    @Override
    public int getTableId() {
        return I_I_FixedAsset.Table_ID;
    }
}
