package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_LandedCostAllocation;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for C_LandedCostAllocation
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_LandedCostAllocation extends PO implements I_C_LandedCostAllocation {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_LandedCostAllocation(int C_LandedCostAllocation_ID) {
        super(C_LandedCostAllocation_ID);
        /**
         * if (C_LandedCostAllocation_ID == 0) { setAmt (Env.ZERO); setBase (Env.ZERO);
         * setInvoiceLineId (0); setLandedCostAllocationId (0); setCostElementId (0);
         * setProductId (0); setQty (Env.ZERO); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_LandedCostAllocation(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_LandedCostAllocation[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Amount.
     *
     * @return Amount
     */
    public BigDecimal getAmt() {
        BigDecimal bd = getValue(COLUMNNAME_Amt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Amount.
     *
     * @param Amt Amount
     */
    public void setAmt(BigDecimal Amt) {
        setValue(COLUMNNAME_Amt, Amt);
    }

    /**
     * Get Base.
     *
     * @return Calculation Base
     */
    public BigDecimal getBase() {
        BigDecimal bd = getValue(COLUMNNAME_Base);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Base.
     *
     * @param Base Calculation Base
     */
    public void setBase(BigDecimal Base) {
        setValue(COLUMNNAME_Base, Base);
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
     * Get Landed Cost Allocation.
     *
     * @return Allocation for Land Costs
     */
    public int getLandedCostAllocationId() {
        Integer ii = getValue(COLUMNNAME_C_LandedCostAllocation_ID);
        if (ii == null) return 0;
        return ii;
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
        if (M_CostElement_ID < 1) setValue(COLUMNNAME_M_CostElement_ID, null);
        else setValue(COLUMNNAME_M_CostElement_ID, Integer.valueOf(M_CostElement_ID));
    }

    public org.compiere.model.I_M_InOutLine getInOutLine() throws RuntimeException {
        return (org.compiere.model.I_M_InOutLine)
                MBaseTableKt.getTable(org.compiere.model.I_M_InOutLine.Table_Name)
                        .getPO(getInOutLineId());
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
     * Set Shipment/Receipt Line.
     *
     * @param M_InOutLine_ID Line on Shipment or Receipt document
     */
    public void setInOutLineId(int M_InOutLine_ID) {
        if (M_InOutLine_ID < 1) setValueNoCheck(COLUMNNAME_M_InOutLine_ID, null);
        else setValueNoCheck(COLUMNNAME_M_InOutLine_ID, M_InOutLine_ID);
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
        else setValueNoCheck(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Get Quantity.
     *
     * @return Quantity
     */
    public BigDecimal getQty() {
        BigDecimal bd = getValue(COLUMNNAME_Qty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        setValue(COLUMNNAME_Qty, Qty);
    }

    @Override
    public int getTableId() {
        return I_C_LandedCostAllocation.Table_ID;
    }
}
