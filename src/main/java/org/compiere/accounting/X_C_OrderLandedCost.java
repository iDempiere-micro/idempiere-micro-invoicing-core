package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_OrderLandedCost;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * Generated Model for C_OrderLandedCost
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_OrderLandedCost extends PO implements I_C_OrderLandedCost {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_OrderLandedCost(Properties ctx, int C_OrderLandedCost_ID) {
        super(ctx, C_OrderLandedCost_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_OrderLandedCost(Properties ctx, Row row) {
        super(ctx, row);
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
        return "X_C_OrderLandedCost[" + getId() + "]";
    }

    /**
     * Get Amount.
     *
     * @return Amount
     */
    public BigDecimal getAmt() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_Amt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    public org.compiere.model.I_C_Order getOrder() throws RuntimeException {
        return (org.compiere.model.I_C_Order)
                MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_Name)
                        .getPO(getOrderId());
    }

    /**
     * Get Order.
     *
     * @return Order
     */
    public int getOrderId() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Estimated Landed Cost.
     *
     * @return Estimated Landed Cost
     */
    public int getOrderLandedCostId() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_OrderLandedCost_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cost Distribution.
     *
     * @return Landed Cost Distribution
     */
    public String getLandedCostDistribution() {
        return (String) getValue(COLUMNNAME_LandedCostDistribution);
    }

    /**
     * Get Cost Element.
     *
     * @return Product Cost Element
     */
    public int getCostElementId() {
        Integer ii = (Integer) getValue(COLUMNNAME_M_CostElement_ID);
        if (ii == null) return 0;
        return ii;
    }

}
