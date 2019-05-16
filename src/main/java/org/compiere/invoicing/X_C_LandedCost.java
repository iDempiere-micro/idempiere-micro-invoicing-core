package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_LandedCost;
import org.compiere.orm.PO;

/**
 * Generated Model for C_LandedCost
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_LandedCost extends PO implements I_C_LandedCost {

    /**
     * Quantity = Q
     */
    public static final String LANDEDCOSTDISTRIBUTION_Quantity = "Q";
    /**
     * Line = L
     */
    public static final String LANDEDCOSTDISTRIBUTION_Line = "L";
    /**
     * Volume = V
     */
    public static final String LANDEDCOSTDISTRIBUTION_Volume = "V";
    /**
     * Weight = W
     */
    public static final String LANDEDCOSTDISTRIBUTION_Weight = "W";
    /**
     * Costs = C
     */
    public static final String LANDEDCOSTDISTRIBUTION_Costs = "C";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_LandedCost(int C_LandedCost_ID) {
        super(C_LandedCost_ID);
        /**
         * if (C_LandedCost_ID == 0) { setInvoiceLineId (0); setLandedCost_ID (0);
         * setLandedCostDistribution (null); // Q setCostElementId (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_LandedCost(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_LandedCost[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Invoice Line.
     *
     * @return Invoice Detail Line
     */
    public int getInvoiceLineId() {
        Integer ii = getValue(COLUMNNAME_C_InvoiceLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice Line.
     *
     * @param C_InvoiceLine_ID Invoice Detail Line
     */
    public void setInvoiceLineId(int C_InvoiceLine_ID) {
        if (C_InvoiceLine_ID < 1) setValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, null);
        else setValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
    }

    /**
     * Get Cost Distribution.
     *
     * @return Landed Cost Distribution
     */
    public String getLandedCostDistribution() {
        return getValue(COLUMNNAME_LandedCostDistribution);
    }

    /**
     * Set Cost Distribution.
     *
     * @param LandedCostDistribution Landed Cost Distribution
     */
    public void setLandedCostDistribution(String LandedCostDistribution) {

        setValue(COLUMNNAME_LandedCostDistribution, LandedCostDistribution);
    }

    /**
     * Get Cost Element.
     *
     * @return Product Cost Element
     */
    public int getCostElementId() {
        Integer ii = getValue(COLUMNNAME_M_CostElement_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Shipment/Receipt.
     *
     * @return Material Shipment Document
     */
    public int getInOutId() {
        Integer ii = getValue(COLUMNNAME_M_InOut_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Shipment/Receipt Line.
     *
     * @return Line on Shipment or Receipt document
     */
    public int getInOutLineId() {
        Integer ii = getValue(COLUMNNAME_M_InOutLine_ID);
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
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    @Override
    public int getTableId() {
        return I_C_LandedCost.Table_ID;
    }
}
