package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_RequisitionLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for M_RequisitionLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_RequisitionLine extends PO implements I_M_RequisitionLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_RequisitionLine(int M_RequisitionLine_ID) {
        super(M_RequisitionLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_RequisitionLine(Row row) {
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
        return "X_M_RequisitionLine[" + getId() + "]";
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
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValue(COLUMNNAME_C_BPartner_ID, null);
        else setValue(COLUMNNAME_C_BPartner_ID, C_BPartner_ID);
    }

    /**
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getChargeId() {
        Integer ii = getValue(COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Charge.
     *
     * @param C_Charge_ID Additional document charges
     */
    public void setChargeId(int C_Charge_ID) {
        if (C_Charge_ID < 1) setValue(COLUMNNAME_C_Charge_ID, null);
        else setValue(COLUMNNAME_C_Charge_ID, C_Charge_ID);
    }

    /**
     * Get Sales Order Line.
     *
     * @return Sales Order Line
     */
    public int getOrderLineId() {
        Integer ii = getValue(COLUMNNAME_C_OrderLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Order Line.
     *
     * @param C_OrderLine_ID Sales Order Line
     */
    public void setOrderLineId(int C_OrderLine_ID) {
        if (C_OrderLine_ID < 1) setValue(COLUMNNAME_C_OrderLine_ID, null);
        else setValue(COLUMNNAME_C_OrderLine_ID, C_OrderLine_ID);
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
     * Set UOM.
     *
     * @param C_UOM_ID Unit of Measure
     */
    public void setUOMId(int C_UOM_ID) {
        if (C_UOM_ID < 1) setValueNoCheck(COLUMNNAME_C_UOM_ID, null);
        else setValueNoCheck(COLUMNNAME_C_UOM_ID, C_UOM_ID);
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
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
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
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(COLUMNNAME_Line, Line);
    }

    /**
     * Get Line Amount.
     *
     * @return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public BigDecimal getLineNetAmt() {
        BigDecimal bd = getValue(COLUMNNAME_LineNetAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Amount.
     *
     * @param LineNetAmt Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public void setLineNetAmt(BigDecimal LineNetAmt) {
        setValue(COLUMNNAME_LineNetAmt, LineNetAmt);
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
        if (M_AttributeSetInstance_ID < 0) setValue(COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValue(COLUMNNAME_M_AttributeSetInstance_ID, M_AttributeSetInstance_ID);
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
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Get Requisition.
     *
     * @return Material Requisition
     */
    public int getRequisitionId() {
        Integer ii = getValue(COLUMNNAME_M_Requisition_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Requisition.
     *
     * @param M_Requisition_ID Material Requisition
     */
    public void setRequisitionId(int M_Requisition_ID) {
        if (M_Requisition_ID < 1) setValueNoCheck(COLUMNNAME_M_Requisition_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Requisition_ID, M_Requisition_ID);
    }

    /**
     * Get Unit Price.
     *
     * @return Actual Price
     */
    public BigDecimal getPriceActual() {
        BigDecimal bd = getValue(COLUMNNAME_PriceActual);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Unit Price.
     *
     * @param PriceActual Actual Price
     */
    public void setPriceActual(BigDecimal PriceActual) {
        setValue(COLUMNNAME_PriceActual, PriceActual);
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
        return I_M_RequisitionLine.Table_ID;
    }
}
