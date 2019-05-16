package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.AccountingElementValue;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.orm.PO;
import org.eevolution.model.I_DD_OrderLine;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_DD_OrderLine extends PO implements I_DD_OrderLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_DD_OrderLine(int DD_OrderLine_ID) {
        super(DD_OrderLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_DD_OrderLine(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_DD_OrderLine[" + getId() + "]";
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getTransactionOrganizationId() {
        Integer ii = getValue(COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setTransactionOrganizationId(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) setValue(COLUMNNAME_AD_OrgTrx_ID, null);
        else setValue(COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
    }

    public org.compiere.model.I_C_Activity getActivity() throws RuntimeException {
        return (org.compiere.model.I_C_Activity)
                MBaseTableKt.getTable(org.compiere.model.I_C_Activity.Table_Name)
                        .getPO(getBusinessActivityId());
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValue(COLUMNNAME_C_Activity_ID, null);
        else setValue(COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
    }

    public org.compiere.model.I_C_Campaign getCampaign() throws RuntimeException {
        return (org.compiere.model.I_C_Campaign)
                MBaseTableKt.getTable(org.compiere.model.I_C_Campaign.Table_Name)
                        .getPO(getCampaignId());
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValue(COLUMNNAME_C_Campaign_ID, null);
        else setValue(COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
    }

    public org.compiere.model.I_C_Charge getCharge() throws RuntimeException {
        return (org.compiere.model.I_C_Charge)
                MBaseTableKt.getTable(org.compiere.model.I_C_Charge.Table_Name)
                        .getPO(getChargeId());
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
        else setValue(COLUMNNAME_C_Charge_ID, Integer.valueOf(C_Charge_ID));
    }

    /**
     * Get Confirmed Quantity.
     *
     * @return Confirmation of a received quantity
     */
    public BigDecimal getConfirmedQty() {
        BigDecimal bd = getValue(COLUMNNAME_ConfirmedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Confirmed Quantity.
     *
     * @param ConfirmedQty Confirmation of a received quantity
     */
    public void setConfirmedQty(BigDecimal ConfirmedQty) {
        setValue(COLUMNNAME_ConfirmedQty, ConfirmedQty);
    }

    public org.compiere.model.I_C_Project getProject() throws RuntimeException {
        return (org.compiere.model.I_C_Project)
                MBaseTableKt.getTable(org.compiere.model.I_C_Project.Table_Name)
                        .getPO(getProjectId());
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValue(COLUMNNAME_C_Project_ID, null);
        else setValue(COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
    }

    public org.compiere.model.I_C_UOM getUOM() throws RuntimeException {
        return (org.compiere.model.I_C_UOM)
                MBaseTableKt.getTable(org.compiere.model.I_C_UOM.Table_Name)
                        .getPO(getUOMId());
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
        else setValueNoCheck(COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
    }

    /**
     * Get Date Delivered.
     *
     * @return Date when the product was delivered
     */
    public Timestamp getDateDelivered() {
        return (Timestamp) getValue(COLUMNNAME_DateDelivered);
    }

    /**
     * Set Date Delivered.
     *
     * @param DateDelivered Date when the product was delivered
     */
    public void setDateDelivered(Timestamp DateDelivered) {
        setValue(COLUMNNAME_DateDelivered, DateDelivered);
    }

    /**
     * Get Date Ordered.
     *
     * @return Date of Order
     */
    public Timestamp getDateOrdered() {
        return (Timestamp) getValue(COLUMNNAME_DateOrdered);
    }

    /**
     * Set Date Ordered.
     *
     * @param DateOrdered Date of Order
     */
    public void setDateOrdered(Timestamp DateOrdered) {
        setValue(COLUMNNAME_DateOrdered, DateOrdered);
    }

    /**
     * Get Date Promised.
     *
     * @return Date Order was promised
     */
    public Timestamp getDatePromised() {
        return (Timestamp) getValue(COLUMNNAME_DatePromised);
    }

    /**
     * Set Date Promised.
     *
     * @param DatePromised Date Order was promised
     */
    public void setDatePromised(Timestamp DatePromised) {
        setValue(COLUMNNAME_DatePromised, DatePromised);
    }

    public org.eevolution.model.I_DD_Order getDistributionOrder() throws RuntimeException {
        return (org.eevolution.model.I_DD_Order)
                MBaseTableKt.getTable(org.eevolution.model.I_DD_Order.Table_Name)
                        .getPO(getDistributionOrderId());
    }

    /**
     * Get Distribution Order.
     *
     * @return Distribution Order
     */
    public int getDistributionOrderId() {
        Integer ii = getValue(COLUMNNAME_DD_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Distribution Order.
     *
     * @param DD_Order_ID Distribution Order
     */
    public void setDistributionOrderId(int DD_Order_ID) {
        if (DD_Order_ID < 1) setValueNoCheck(COLUMNNAME_DD_Order_ID, null);
        else setValueNoCheck(COLUMNNAME_DD_Order_ID, Integer.valueOf(DD_Order_ID));
    }

    /**
     * Get Distribution Order Line.
     *
     * @return Distribution Order Line
     */
    public int getDistributionOrderLineId() {
        Integer ii = getValue(COLUMNNAME_DD_OrderLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Distribution Order Line.
     *
     * @param DD_OrderLine_ID Distribution Order Line
     */
    public void setDistributionOrderLineId(int DD_OrderLine_ID) {
        if (DD_OrderLine_ID < 1) setValueNoCheck(COLUMNNAME_DD_OrderLine_ID, null);
        else setValueNoCheck(COLUMNNAME_DD_OrderLine_ID, Integer.valueOf(DD_OrderLine_ID));
    }

    /**
     * Get DD_OrderLine_UU.
     *
     * @return DD_OrderLine_UU
     */
    public String getOrderLine_UU() {
        return getValue(COLUMNNAME_DD_OrderLine_UU);
    }

    /**
     * Set DD_OrderLine_UU.
     *
     * @param DD_OrderLine_UU DD_OrderLine_UU
     */
    public void setOrderLine_UU(String DD_OrderLine_UU) {
        setValue(COLUMNNAME_DD_OrderLine_UU, DD_OrderLine_UU);
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
     * Get Freight Amount.
     *
     * @return Freight Amount
     */
    public BigDecimal getFreightAmt() {
        BigDecimal bd = getValue(COLUMNNAME_FreightAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Freight Amount.
     *
     * @param FreightAmt Freight Amount
     */
    public void setFreightAmt(BigDecimal FreightAmt) {
        setValue(COLUMNNAME_FreightAmt, FreightAmt);
    }

    /**
     * Set Description Only.
     *
     * @param IsDescription if true, the line is just description and no transaction
     */
    public void setIsDescription(boolean IsDescription) {
        setValue(COLUMNNAME_IsDescription, Boolean.valueOf(IsDescription));
    }

    /**
     * Get Description Only.
     *
     * @return if true, the line is just description and no transaction
     */
    public boolean isDescription() {
        Object oo = getValue(COLUMNNAME_IsDescription);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Set Invoiced.
     *
     * @param IsInvoiced Is this invoiced?
     */
    public void setIsInvoiced(boolean IsInvoiced) {
        setValue(COLUMNNAME_IsInvoiced, Boolean.valueOf(IsInvoiced));
    }

    /**
     * Get Invoiced.
     *
     * @return Is this invoiced?
     */
    public boolean isInvoiced() {
        Object oo = getValue(COLUMNNAME_IsInvoiced);
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
        setValue(COLUMNNAME_Line, Integer.valueOf(Line));
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

    public I_M_AttributeSetInstance getMAttributeSetInstance() throws RuntimeException {
        return (I_M_AttributeSetInstance)
                MBaseTableKt.getTable(I_M_AttributeSetInstance.Table_Name)
                        .getPO(getAttributeSetInstanceId());
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
            setValue(COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
    }

    public I_M_AttributeSetInstance getMAttributeSetInstanceTo() throws RuntimeException {
        return (I_M_AttributeSetInstance)
                MBaseTableKt.getTable(I_M_AttributeSetInstance.Table_Name)
                        .getPO(getMAttributeSetInstanceToId());
    }

    /**
     * Set Attribute Set Instance To.
     *
     * @param M_AttributeSetInstanceTo_ID Target Product Attribute Set Instance
     */
    public void setAttributeSetInstanceToId(int M_AttributeSetInstanceTo_ID) {
        if (M_AttributeSetInstanceTo_ID < 1) setValue(COLUMNNAME_M_AttributeSetInstanceTo_ID, null);
        else
            setValue(
                    COLUMNNAME_M_AttributeSetInstanceTo_ID, Integer.valueOf(M_AttributeSetInstanceTo_ID));
    }

    /**
     * Get Attribute Set Instance To.
     *
     * @return Target Product Attribute Set Instance
     */
    public int getMAttributeSetInstanceToId() {
        Integer ii = getValue(COLUMNNAME_M_AttributeSetInstanceTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    public org.compiere.model.I_M_Locator getLocator() throws RuntimeException {
        return (org.compiere.model.I_M_Locator)
                MBaseTableKt.getTable(org.compiere.model.I_M_Locator.Table_Name)
                        .getPO(getLocatorId());
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
     * Set Locator.
     *
     * @param M_Locator_ID Warehouse Locator
     */
    public void setLocatorId(int M_Locator_ID) {
        if (M_Locator_ID < 1) setValue(COLUMNNAME_M_Locator_ID, null);
        else setValue(COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
    }

    public org.compiere.model.I_M_Locator getLocatorTo() throws RuntimeException {
        return (org.compiere.model.I_M_Locator)
                MBaseTableKt.getTable(org.compiere.model.I_M_Locator.Table_Name)
                        .getPO(getLocatorToId());
    }

    /**
     * Get Locator To.
     *
     * @return Location inventory is moved to
     */
    public int getLocatorToId() {
        Integer ii = getValue(COLUMNNAME_M_LocatorTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Locator To.
     *
     * @param M_LocatorTo_ID Location inventory is moved to
     */
    public void setLocatorToId(int M_LocatorTo_ID) {
        if (M_LocatorTo_ID < 1) setValue(COLUMNNAME_M_LocatorTo_ID, null);
        else setValue(COLUMNNAME_M_LocatorTo_ID, Integer.valueOf(M_LocatorTo_ID));
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
        else setValue(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    public org.compiere.model.I_M_Shipper getShipper() throws RuntimeException {
        return (org.compiere.model.I_M_Shipper)
                MBaseTableKt.getTable(org.compiere.model.I_M_Shipper.Table_Name)
                        .getPO(getShipperId());
    }

    /**
     * Get Shipper.
     *
     * @return Method or manner of product delivery
     */
    public int getShipperId() {
        Integer ii = getValue(COLUMNNAME_M_Shipper_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Shipper.
     *
     * @param M_Shipper_ID Method or manner of product delivery
     */
    public void setShipperId(int M_Shipper_ID) {
        if (M_Shipper_ID < 1) setValue(COLUMNNAME_M_Shipper_ID, null);
        else setValue(COLUMNNAME_M_Shipper_ID, Integer.valueOf(M_Shipper_ID));
    }

    /**
     * Get Picked Quantity.
     *
     * @return Picked Quantity
     */
    public BigDecimal getPickedQty() {
        BigDecimal bd = getValue(COLUMNNAME_PickedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Picked Quantity.
     *
     * @param PickedQty Picked Quantity
     */
    public void setPickedQty(BigDecimal PickedQty) {
        setValue(COLUMNNAME_PickedQty, PickedQty);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
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
     * Get Delivered Quantity.
     *
     * @return Delivered Quantity
     */
    public BigDecimal getQtyDelivered() {
        BigDecimal bd = getValue(COLUMNNAME_QtyDelivered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Delivered Quantity.
     *
     * @param QtyDelivered Delivered Quantity
     */
    public void setQtyDelivered(BigDecimal QtyDelivered) {
        setValue(COLUMNNAME_QtyDelivered, QtyDelivered);
    }

    /**
     * Get Quantity.
     *
     * @return The Quantity Entered is based on the selected UoM
     */
    public BigDecimal getQtyEntered() {
        BigDecimal bd = getValue(COLUMNNAME_QtyEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param QtyEntered The Quantity Entered is based on the selected UoM
     */
    public void setQtyEntered(BigDecimal QtyEntered) {
        setValue(COLUMNNAME_QtyEntered, QtyEntered);
    }

    /**
     * Get Qty In Transit.
     *
     * @return Qty In Transit
     */
    public BigDecimal getQtyInTransit() {
        BigDecimal bd = getValue(COLUMNNAME_QtyInTransit);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Qty In Transit.
     *
     * @param QtyInTransit Qty In Transit
     */
    public void setQtyInTransit(BigDecimal QtyInTransit) {
        setValue(COLUMNNAME_QtyInTransit, QtyInTransit);
    }

    /**
     * Get Ordered Quantity.
     *
     * @return Ordered Quantity
     */
    public BigDecimal getQtyOrdered() {
        BigDecimal bd = getValue(COLUMNNAME_QtyOrdered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Ordered Quantity.
     *
     * @param QtyOrdered Ordered Quantity
     */
    public void setQtyOrdered(BigDecimal QtyOrdered) {
        setValue(COLUMNNAME_QtyOrdered, QtyOrdered);
    }

    /**
     * Get Reserved Quantity.
     *
     * @return Reserved Quantity
     */
    public BigDecimal getQtyReserved() {
        BigDecimal bd = getValue(COLUMNNAME_QtyReserved);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Reserved Quantity.
     *
     * @param QtyReserved Reserved Quantity
     */
    public void setQtyReserved(BigDecimal QtyReserved) {
        setValue(COLUMNNAME_QtyReserved, QtyReserved);
    }

    /**
     * Get Scrapped Quantity.
     *
     * @return The Quantity scrapped due to QA issues
     */
    public BigDecimal getScrappedQty() {
        BigDecimal bd = getValue(COLUMNNAME_ScrappedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Scrapped Quantity.
     *
     * @param ScrappedQty The Quantity scrapped due to QA issues
     */
    public void setScrappedQty(BigDecimal ScrappedQty) {
        setValue(COLUMNNAME_ScrappedQty, ScrappedQty);
    }

    /**
     * Get Target Quantity.
     *
     * @return Target Movement Quantity
     */
    public BigDecimal getTargetQty() {
        BigDecimal bd = getValue(COLUMNNAME_TargetQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Target Quantity.
     *
     * @param TargetQty Target Movement Quantity
     */
    public void setTargetQty(BigDecimal TargetQty) {
        setValue(COLUMNNAME_TargetQty, TargetQty);
    }

    public AccountingElementValue getUser1() throws RuntimeException {
        return (AccountingElementValue)
                MBaseTableKt.getTable(AccountingElementValue.Table_Name)
                        .getPO(getUser1Id());
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1Id() {
        Integer ii = getValue(COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValue(COLUMNNAME_User1_ID, null);
        else setValue(COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
    }

    public AccountingElementValue getUser2() throws RuntimeException {
        return (AccountingElementValue)
                MBaseTableKt.getTable(AccountingElementValue.Table_Name)
                        .getPO(getUser2Id());
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValue(COLUMNNAME_User2_ID, null);
        else setValue(COLUMNNAME_User2_ID, User2_ID);
    }
}
