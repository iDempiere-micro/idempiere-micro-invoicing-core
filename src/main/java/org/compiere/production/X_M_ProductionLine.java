package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.IDocLine;
import org.compiere.model.I_M_ProductionLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for M_ProductionLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_ProductionLine extends PO implements I_M_ProductionLine, IDocLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_ProductionLine(int M_ProductionLine_ID) {
        super(M_ProductionLine_ID);
        /*
         * if (M_ProductionLine_ID == 0) { setLine (0); // @SQL=SELECT NVL(MAX(Line),0)+10 AS
         * DefaultValue FROM M_ProductionLine WHERE M_Production_ID=@M_Production_ID@
         * setAttributeSetInstanceId (0); setLocatorId (0); // @M_Locator_ID@ setMovementQty
         * (Env.ZERO); setProductId (0); setProductionLineId (0); setProcessed (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_M_ProductionLine(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return I_M_ProductionLine.accessLevel.intValue();
    }

    public String toString() {
        return "X_M_ProductionLine[" + getId() + "]";
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(I_M_ProductionLine.COLUMNNAME_Description, Description);
    }

    /**
     * Set End Product.
     *
     * @param IsEndProduct End Product of production
     */
    public void setIsEndProduct(boolean IsEndProduct) {
        setValue(I_M_ProductionLine.COLUMNNAME_IsEndProduct, Boolean.valueOf(IsEndProduct));
    }

    /**
     * Get End Product.
     *
     * @return End Product of production
     */
    public boolean isEndProduct() {
        Object oo = getValue(I_M_ProductionLine.COLUMNNAME_IsEndProduct);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(I_M_ProductionLine.COLUMNNAME_Line, Integer.valueOf(Line));
    }

    /**
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getAttributeSetInstanceId() {
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Attribute Set Instance.
     *
     * @param M_AttributeSetInstance_ID Product Attribute Set Instance
     */
    public void setAttributeSetInstanceId(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0)
            setValue(I_M_ProductionLine.COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValue(
                    I_M_ProductionLine.COLUMNNAME_M_AttributeSetInstance_ID,
                    Integer.valueOf(M_AttributeSetInstance_ID));
    }

    /**
     * Get Locator.
     *
     * @return Warehouse Locator
     */
    public int getLocatorId() {
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_M_Locator_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Locator.
     *
     * @param M_Locator_ID Warehouse Locator
     */
    public void setLocatorId(int M_Locator_ID) {
        if (M_Locator_ID < 1) setValue(I_M_ProductionLine.COLUMNNAME_M_Locator_ID, null);
        else setValue(I_M_ProductionLine.COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
    }

    /**
     * Get Movement Quantity.
     *
     * @return Quantity of a product moved.
     */
    public BigDecimal getMovementQty() {
        BigDecimal bd = getValue(I_M_ProductionLine.COLUMNNAME_MovementQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Movement Quantity.
     *
     * @param MovementQty Quantity of a product moved.
     */
    public void setMovementQty(BigDecimal MovementQty) {
        setValue(I_M_ProductionLine.COLUMNNAME_MovementQty, MovementQty);
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
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(I_M_ProductionLine.COLUMNNAME_M_Product_ID, null);
        else setValue(I_M_ProductionLine.COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    public org.compiere.model.I_M_Production getProduction() throws RuntimeException {
        return (org.compiere.model.I_M_Production)
                MBaseTableKt.getTable(org.compiere.model.I_M_Production.Table_Name)
                        .getPO(getProductionId());
    }

    /**
     * Get Production.
     *
     * @return Plan for producing a product
     */
    public int getProductionId() {
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_M_Production_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Production.
     *
     * @param M_Production_ID Plan for producing a product
     */
    public void setProductionId(int M_Production_ID) {
        if (M_Production_ID < 1) setValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_Production_ID, null);
        else
            setValueNoCheck(
                    I_M_ProductionLine.COLUMNNAME_M_Production_ID, Integer.valueOf(M_Production_ID));
    }

    /**
     * Get Production Line.
     *
     * @return Document Line representing a production
     */
    public int getProductionLineId() {
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_M_ProductionLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Production Line.
     *
     * @param M_ProductionLine_ID Document Line representing a production
     */
    public void setProductionLineId(int M_ProductionLine_ID) {
        if (M_ProductionLine_ID < 1)
            setValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_ProductionLine_ID, null);
        else
            setValueNoCheck(
                    I_M_ProductionLine.COLUMNNAME_M_ProductionLine_ID, Integer.valueOf(M_ProductionLine_ID));
    }

    public org.compiere.model.I_M_ProductionPlan getProductionPlan() throws RuntimeException {
        return (org.compiere.model.I_M_ProductionPlan)
                MBaseTableKt.getTable(org.compiere.model.I_M_ProductionPlan.Table_Name)
                        .getPO(getProductionPlanId());
    }

    /**
     * Get Production Plan.
     *
     * @return Plan for how a product is produced
     */
    public int getProductionPlanId() {
        Integer ii = getValue(I_M_ProductionLine.COLUMNNAME_M_ProductionPlan_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Production Plan.
     *
     * @param M_ProductionPlan_ID Plan for how a product is produced
     */
    public void setProductionPlanId(int M_ProductionPlan_ID) {
        if (M_ProductionPlan_ID < 1)
            setValueNoCheck(I_M_ProductionLine.COLUMNNAME_M_ProductionPlan_ID, null);
        else
            setValueNoCheck(
                    I_M_ProductionLine.COLUMNNAME_M_ProductionPlan_ID, M_ProductionPlan_ID);
    }

    /**
     * Get Planned Quantity.
     *
     * @return Planned quantity for this project
     */
    public BigDecimal getPlannedQty() {
        BigDecimal bd = getValue(I_M_ProductionLine.COLUMNNAME_PlannedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Planned Quantity.
     *
     * @param PlannedQty Planned quantity for this project
     */
    public void setPlannedQty(BigDecimal PlannedQty) {
        setValue(I_M_ProductionLine.COLUMNNAME_PlannedQty, PlannedQty);
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(I_M_ProductionLine.COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Quantity Used.
     *
     * @return Quantity Used
     */
    public BigDecimal getQtyUsed() {
        BigDecimal bd = getValue(I_M_ProductionLine.COLUMNNAME_QtyUsed);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Used.
     *
     * @param QtyUsed Quantity Used
     */
    public void setQtyUsed(BigDecimal QtyUsed) {
        setValue(I_M_ProductionLine.COLUMNNAME_QtyUsed, QtyUsed);
    }

    @Override
    public int getTableId() {
        return I_M_ProductionLine.Table_ID;
    }
}
