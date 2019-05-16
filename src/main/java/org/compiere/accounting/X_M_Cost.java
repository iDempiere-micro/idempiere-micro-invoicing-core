package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingSchema;
import org.compiere.model.I_M_Cost;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for M_Cost
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_Cost extends PO implements I_M_Cost {

    /**
     * Average PO = A
     */
    public static final String COSTINGMETHOD_AveragePO = "A";
    /**
     * Average Invoice = I
     */
    public static final String COSTINGMETHOD_AverageInvoice = "I";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Cost(int M_Cost_ID) {
        super(M_Cost_ID);
    }


    /**
     * Load Constructor
     */
    public X_M_Cost(Row row) {
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
        return "X_M_Cost[" + getId() + "]";
    }

    public AccountingSchema getAccountingSchema() throws RuntimeException {
        return (AccountingSchema)
                MBaseTableKt.getTable(AccountingSchema.Table_Name)
                        .getPO(getAccountingSchemaId());
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, C_AcctSchema_ID);
    }

    /**
     * Get Accumulated Amt.
     *
     * @return Total Amount
     */
    public BigDecimal getCumulatedAmt() {
        BigDecimal bd = getValue(COLUMNNAME_CumulatedAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Accumulated Amt.
     *
     * @param CumulatedAmt Total Amount
     */
    public void setCumulatedAmt(BigDecimal CumulatedAmt) {
        setValueNoCheck(COLUMNNAME_CumulatedAmt, CumulatedAmt);
    }

    /**
     * Get Accumulated Qty.
     *
     * @return Total Quantity
     */
    public BigDecimal getCumulatedQty() {
        BigDecimal bd = getValue(COLUMNNAME_CumulatedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Accumulated Qty.
     *
     * @param CumulatedQty Total Quantity
     */
    public void setCumulatedQty(BigDecimal CumulatedQty) {
        setValueNoCheck(COLUMNNAME_CumulatedQty, CumulatedQty);
    }

    /**
     * Get Current Cost Price.
     *
     * @return The currently used cost price
     */
    public BigDecimal getCurrentCostPrice() {
        BigDecimal bd = getValue(COLUMNNAME_CurrentCostPrice);
        if (bd == null) return Env.ZERO;
        return bd;
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
     * Get Current Cost Price Lower Level.
     *
     * @return Current Price Lower Level Is the sum of the costs of the components of this product
     * manufactured for this level.
     */
    public BigDecimal getCurrentCostPriceLL() {
        BigDecimal bd = getValue(COLUMNNAME_CurrentCostPriceLL);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Current Quantity.
     *
     * @return Current Quantity
     */
    public BigDecimal getCurrentQty() {
        BigDecimal bd = getValue(COLUMNNAME_CurrentQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Current Quantity.
     *
     * @param CurrentQty Current Quantity
     */
    public void setCurrentQty(BigDecimal CurrentQty) {
        setValue(COLUMNNAME_CurrentQty, CurrentQty);
    }

    /**
     * Get Future Cost Price.
     *
     * @return Future Cost Price
     */
    public BigDecimal getFutureCostPrice() {
        BigDecimal bd = getValue(COLUMNNAME_FutureCostPrice);
        if (bd == null) return Env.ZERO;
        return bd;
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
                    COLUMNNAME_M_AttributeSetInstance_ID, M_AttributeSetInstance_ID);
    }

    public org.compiere.model.I_M_CostElement getCostElement() throws RuntimeException {
        return (org.compiere.model.I_M_CostElement)
                MBaseTableKt.getTable(org.compiere.model.I_M_CostElement.Table_Name)
                        .getPO(getCostElementId());
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
     * Set Cost Element.
     *
     * @param M_CostElement_ID Product Cost Element
     */
    public void setCostElementId(int M_CostElement_ID) {
        if (M_CostElement_ID < 1) setValueNoCheck(COLUMNNAME_M_CostElement_ID, null);
        else setValueNoCheck(COLUMNNAME_M_CostElement_ID, M_CostElement_ID);
    }

    /**
     * Get Cost Type.
     *
     * @return Type of Cost (e.g. Current, Plan, Future)
     */
    public int getCostTypeId() {
        Integer ii = getValue(COLUMNNAME_M_CostType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cost Type.
     *
     * @param M_CostType_ID Type of Cost (e.g. Current, Plan, Future)
     */
    public void setCostTypeId(int M_CostType_ID) {
        if (M_CostType_ID < 1) setValueNoCheck(COLUMNNAME_M_CostType_ID, null);
        else setValueNoCheck(COLUMNNAME_M_CostType_ID, M_CostType_ID);
    }

    public org.compiere.model.I_M_Product getProduct() throws RuntimeException {
        return (org.compiere.model.I_M_Product)
                MBaseTableKt.getTable(org.compiere.model.I_M_Product.Table_Name)
                        .getPO(getProductId());
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
     * Get Percent.
     *
     * @return Percentage
     */
    public int getPercent() {
        Integer ii = getValue(COLUMNNAME_Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Percent.
     *
     * @param Percent Percentage
     */
    public void setPercent(int Percent) {
        setValue(COLUMNNAME_Percent, Integer.valueOf(Percent));
    }

    @Override
    public int getTableId() {
        return I_M_Cost.Table_ID;
    }
}
