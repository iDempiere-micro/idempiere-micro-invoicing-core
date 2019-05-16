package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_T_Replenish;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

public class X_T_Replenish extends PO implements I_T_Replenish {

    /**
     * Custom = 9
     */
    public static final String REPLENISHTYPE_Custom = "9";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_T_Replenish(int T_Replenish_ID) {
        super(T_Replenish_ID);
    }

    /**
     * Load Constructor
     */
    public X_T_Replenish(Row row) {
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
        return "X_T_Replenish[" + getId() + "]";
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
     * Get Source Warehouse.
     *
     * @return Optional Warehouse to replenish from
     */
    public int getWarehouseSourceId() {
        Integer ii = getValue(COLUMNNAME_M_WarehouseSource_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Quantity to Order.
     *
     * @return Quantity to Order
     */
    public BigDecimal getQtyToOrder() {
        BigDecimal bd = getValue(COLUMNNAME_QtyToOrder);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity to Order.
     *
     * @param QtyToOrder Quantity to Order
     */
    public void setQtyToOrder(BigDecimal QtyToOrder) {
        setValue(COLUMNNAME_QtyToOrder, QtyToOrder);
    }

    /**
     * Get Replenish Type.
     *
     * @return Method for re-ordering a product
     */
    public String getReplenishType() {
        return getValue(COLUMNNAME_ReplenishType);
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }
}
