package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_M_ProductionPlan;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for M_ProductionPlan
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_ProductionPlan extends PO implements I_M_ProductionPlan {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_ProductionPlan(int M_ProductionPlan_ID) {
        super(M_ProductionPlan_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_ProductionPlan(Row row) {
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
        return "X_M_ProductionPlan[" + getId() + "]";
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
     * Set Production.
     *
     * @param M_Production_ID Plan for producing a product
     */
    public void setProductionId(int M_Production_ID) {
        if (M_Production_ID < 1) setValueNoCheck(COLUMNNAME_M_Production_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Production_ID, Integer.valueOf(M_Production_ID));
    }

    /**
     * Get Production Plan.
     *
     * @return Plan for how a product is produced
     */
    public int getProductionPlanId() {
        Integer ii = getValue(COLUMNNAME_M_ProductionPlan_ID);
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
     * Get Production Quantity.
     *
     * @return Quantity of products to produce
     */
    public BigDecimal getProductionQty() {
        BigDecimal bd = getValue(COLUMNNAME_ProductionQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Production Quantity.
     *
     * @param ProductionQty Quantity of products to produce
     */
    public void setProductionQty(BigDecimal ProductionQty) {
        setValue(COLUMNNAME_ProductionQty, ProductionQty);
    }

    @Override
    public int getTableId() {
        return I_M_ProductionPlan.Table_ID;
    }
}
