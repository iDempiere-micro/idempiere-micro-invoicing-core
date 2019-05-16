package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_Product_PO;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for M_Product_PO
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_Product_PO extends PO implements I_M_Product_PO {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Product_PO(int M_Product_PO_ID) {
        super(M_Product_PO_ID);
        /*
         * if (M_Product_PO_ID == 0) { setBusinessPartnerId (0); setIsCurrentVendor (true); // Y
         * setProductId (0); // @M_Product_ID@ setVendorProductNo (null); // @Value@ }
         */
    }

    /**
     * Load Constructor
     */
    public X_M_Product_PO(Row row) {
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
        return "X_M_Product_PO[" + getId() + "]";
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
     * Set Current vendor.
     *
     * @param IsCurrentVendor Use this Vendor for pricing and stock replenishment
     */
    public void setIsCurrentVendor(boolean IsCurrentVendor) {
        setValue(COLUMNNAME_IsCurrentVendor, Boolean.valueOf(IsCurrentVendor));
    }

    /**
     * Get Current vendor.
     *
     * @return Use this Vendor for pricing and stock replenishment
     */
    public boolean isCurrentVendor() {
        Object oo = getValue(COLUMNNAME_IsCurrentVendor);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Get Last PO Price.
     *
     * @return Price of the last purchase order for the product
     */
    public BigDecimal getPriceLastPO() {
        BigDecimal bd = getValue(COLUMNNAME_PriceLastPO);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get List Price.
     *
     * @return List Price
     */
    public BigDecimal getPriceList() {
        BigDecimal bd = getValue(COLUMNNAME_PriceList);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get PO Price.
     *
     * @return Price based on a purchase order
     */
    public BigDecimal getPricePO() {
        BigDecimal bd = getValue(COLUMNNAME_PricePO);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    @Override
    public int getTableId() {
        return I_M_Product_PO.Table_ID;
    }
}
