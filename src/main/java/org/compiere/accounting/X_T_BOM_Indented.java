package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_T_BOM_Indented;
import org.compiere.orm.PO;

import java.math.BigDecimal;

public class X_T_BOM_Indented extends PO implements I_T_BOM_Indented {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_T_BOM_Indented(int T_BOM_Indented_ID) {
        super(T_BOM_Indented_ID);
    }

    /**
     * Load Constructor
     */
    public X_T_BOM_Indented(Row row) {
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
        return "X_T_BOM_Indented[" + getId() + "]";
    }

    /**
     * Set Process Instance.
     *
     * @param AD_PInstance_ID Instance of the process
     */
    public void setPInstanceId(int AD_PInstance_ID) {
        if (AD_PInstance_ID < 1) setValue(COLUMNNAME_AD_PInstance_ID, null);
        else setValue(COLUMNNAME_AD_PInstance_ID, AD_PInstance_ID);
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAcctSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValue(COLUMNNAME_C_AcctSchema_ID, null);
        else setValue(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
    }

    /**
     * Set Cost.
     *
     * @param Cost Cost information
     */
    public void setCost(BigDecimal Cost) {
        setValue(COLUMNNAME_Cost, Cost);
    }

    /**
     * Set Future Cost.
     *
     * @param CostFuture Cost information
     */
    public void setCostFuture(BigDecimal CostFuture) {
        setValue(COLUMNNAME_CostFuture, CostFuture);
    }

    /**
     * Set Current Cost Price.
     *
     * @param CurrentCostPrice The currently used cost price
     */
    public void setCurrentCostPrice(BigDecimal CurrentCostPrice) {
        setValue(COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
    }

    /**
     * Set Current Cost Price Lower Level.
     *
     * @param CurrentCostPriceLL Current Price Lower Level Is the sum of the costs of the components
     *                           of this product manufactured for this level.
     */
    public void setCurrentCostPriceLL(BigDecimal CurrentCostPriceLL) {
        setValue(COLUMNNAME_CurrentCostPriceLL, CurrentCostPriceLL);
    }

    /**
     * Set Future Cost Price.
     *
     * @param FutureCostPrice Future Cost Price
     */
    public void setFutureCostPrice(BigDecimal FutureCostPrice) {
        setValue(COLUMNNAME_FutureCostPrice, FutureCostPrice);
    }

    /**
     * Set Future Cost Price Lower Level.
     *
     * @param FutureCostPriceLL Future Cost Price Lower Level
     */
    public void setFutureCostPriceLL(BigDecimal FutureCostPriceLL) {
        setValue(COLUMNNAME_FutureCostPriceLL, FutureCostPriceLL);
    }

    /**
     * Set Level no.
     *
     * @param LevelNo Level no
     */
    public void setLevelNo(int LevelNo) {
        setValue(COLUMNNAME_LevelNo, Integer.valueOf(LevelNo));
    }

    /**
     * Set Levels.
     *
     * @param Levels Levels
     */
    public void setLevels(String Levels) {
        setValue(COLUMNNAME_Levels, Levels);
    }

    /**
     * Set Cost Element.
     *
     * @param M_CostElement_ID Product Cost Element
     */
    public void setCostElementId(int M_CostElement_ID) {
        if (M_CostElement_ID < 1) setValue(COLUMNNAME_M_CostElement_ID, null);
        else setValue(COLUMNNAME_M_CostElement_ID, Integer.valueOf(M_CostElement_ID));
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
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        setValue(COLUMNNAME_Qty, Qty);
    }

    /**
     * Set Quantity.
     *
     * @param QtyBOM Indicate the Quantity use in this BOM
     */
    public void setQtyBOM(BigDecimal QtyBOM) {
        setValue(COLUMNNAME_QtyBOM, QtyBOM);
    }

    /**
     * Set Selected Product.
     *
     * @param Sel_Product_ID Selected Product
     */
    public void setSelectedProductId(int Sel_Product_ID) {
        if (Sel_Product_ID < 1) setValue(COLUMNNAME_Sel_Product_ID, null);
        else setValue(COLUMNNAME_Sel_Product_ID, Integer.valueOf(Sel_Product_ID));
    }

    /**
     * Set Sequence.
     *
     * @param SeqNo Method of ordering records; lowest number comes first
     */
    public void setSeqNo(int SeqNo) {
        setValue(COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
    }

    @Override
    public int getTableId() {
        return I_T_BOM_Indented.Table_ID;
    }
}
