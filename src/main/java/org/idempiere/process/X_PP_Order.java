package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.AccountingElementValue;
import org.compiere.model.DocumentType;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.User;
import org.compiere.model.Workflow;
import org.compiere.orm.PO;
import org.eevolution.model.I_PP_Order;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_PP_Order extends PO implements I_PP_Order {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PP_Order(int PP_Order_ID) {
        super(PP_Order_ID);
    }

    /**
     * Load Constructor
     */
    public X_PP_Order(Row row) {
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
        StringBuffer sb = new StringBuffer("X_PP_Order[").append(getId()).append("]");
        return sb.toString();
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

    public Workflow getWorkflow() throws RuntimeException {
        return (Workflow)
                MBaseTableKt.getTable(Workflow.Table_Name)
                        .getPO(getWorkflowId());
    }

    /**
     * Get Workflow.
     *
     * @return Workflow or combination of tasks
     */
    public int getWorkflowId() {
        Integer ii = getValue(COLUMNNAME_AD_Workflow_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Workflow.
     *
     * @param AD_Workflow_ID Workflow or combination of tasks
     */
    public void setWorkflowId(int AD_Workflow_ID) {
        if (AD_Workflow_ID < 1) setValueNoCheck(COLUMNNAME_AD_Workflow_ID, null);
        else setValueNoCheck(COLUMNNAME_AD_Workflow_ID, Integer.valueOf(AD_Workflow_ID));
    }

    /**
     * Get Quantity Assay.
     *
     * @return Indicated the Quantity Assay to use into Quality Order
     */
    public BigDecimal getAssay() {
        BigDecimal bd = getValue(COLUMNNAME_Assay);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Assay.
     *
     * @param Assay Indicated the Quantity Assay to use into Quality Order
     */
    public void setAssay(BigDecimal Assay) {
        setValue(COLUMNNAME_Assay, Assay);
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

    public DocumentType getDocumentType() throws RuntimeException {
        return (DocumentType)
                MBaseTableKt.getTable(DocumentType.Table_Name)
                        .getPO(getDocumentTypeId());
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getDocumentTypeId() {
        Integer ii = getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Document Type.
     *
     * @param C_DocType_ID Document type or rules
     */
    public void setDocumentTypeId(int C_DocType_ID) {
        if (C_DocType_ID < 0) setValue(COLUMNNAME_C_DocType_ID, null);
        else setValue(COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
    }

    public DocumentType getDocTypeTarget() throws RuntimeException {
        return (DocumentType)
                MBaseTableKt.getTable(DocumentType.Table_Name)
                        .getPO(getTargetDocumentTypeId());
    }

    /**
     * Get Target Document Type.
     *
     * @return Target document type for conversing documents
     */
    public int getTargetDocumentTypeId() {
        Integer ii = getValue(COLUMNNAME_C_DocTypeTarget_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Target Document Type.
     *
     * @param C_DocTypeTarget_ID Target document type for conversing documents
     */
    public void setTargetDocumentTypeId(int C_DocTypeTarget_ID) {
        if (C_DocTypeTarget_ID < 1) setValueNoCheck(COLUMNNAME_C_DocTypeTarget_ID, null);
        else setValueNoCheck(COLUMNNAME_C_DocTypeTarget_ID, Integer.valueOf(C_DocTypeTarget_ID));
    }

    /**
     * Get Copy From.
     *
     * @return Copy From Record
     */
    public String getCopyFrom() {
        return getValue(COLUMNNAME_CopyFrom);
    }

    /**
     * Set Copy From.
     *
     * @param CopyFrom Copy From Record
     */
    public void setCopyFrom(String CopyFrom) {
        setValue(COLUMNNAME_CopyFrom, CopyFrom);
    }

    public org.compiere.model.I_C_OrderLine getOrderLine() throws RuntimeException {
        return (org.compiere.model.I_C_OrderLine)
                MBaseTableKt.getTable(org.compiere.model.I_C_OrderLine.Table_Name)
                        .getPO(getOrderLineId());
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
        else setValue(COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
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
     * Get Date Confirm.
     *
     * @return Date Confirm of this Order
     */
    public Timestamp getDateConfirm() {
        return (Timestamp) getValue(COLUMNNAME_DateConfirm);
    }

    /**
     * Set Date Confirm.
     *
     * @param DateConfirm Date Confirm of this Order
     */
    public void setDateConfirm(Timestamp DateConfirm) {
        setValueNoCheck(COLUMNNAME_DateConfirm, DateConfirm);
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
        setValueNoCheck(COLUMNNAME_DateDelivered, DateDelivered);
    }

    /**
     * Get Finish Date.
     *
     * @return Finish or (planned) completion date
     */
    public Timestamp getDateFinish() {
        return (Timestamp) getValue(COLUMNNAME_DateFinish);
    }

    /**
     * Set Finish Date.
     *
     * @param DateFinish Finish or (planned) completion date
     */
    public void setDateFinish(Timestamp DateFinish) {
        setValueNoCheck(COLUMNNAME_DateFinish, DateFinish);
    }

    /**
     * Get Date Finish Schedule.
     *
     * @return Scheduled Finish date for this Order
     */
    public Timestamp getDateFinishSchedule() {
        return (Timestamp) getValue(COLUMNNAME_DateFinishSchedule);
    }

    /**
     * Set Date Finish Schedule.
     *
     * @param DateFinishSchedule Scheduled Finish date for this Order
     */
    public void setDateFinishSchedule(Timestamp DateFinishSchedule) {
        setValue(COLUMNNAME_DateFinishSchedule, DateFinishSchedule);
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

    /**
     * Get Date Start.
     *
     * @return Date Start for this Order
     */
    public Timestamp getDateStart() {
        return (Timestamp) getValue(COLUMNNAME_DateStart);
    }

    /**
     * Set Date Start.
     *
     * @param DateStart Date Start for this Order
     */
    public void setDateStart(Timestamp DateStart) {
        setValueNoCheck(COLUMNNAME_DateStart, DateStart);
    }

    /**
     * Get Date Start Schedule.
     *
     * @return Scheduled start date for this Order
     */
    public Timestamp getDateStartSchedule() {
        return (Timestamp) getValue(COLUMNNAME_DateStartSchedule);
    }

    /**
     * Set Date Start Schedule.
     *
     * @param DateStartSchedule Scheduled start date for this Order
     */
    public void setDateStartSchedule(Timestamp DateStartSchedule) {
        setValue(COLUMNNAME_DateStartSchedule, DateStartSchedule);
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
     * Get Document Action.
     *
     * @return The targeted status of the document
     */
    public String getDocAction() {
        return getValue(COLUMNNAME_DocAction);
    }

    /**
     * Set Document Action.
     *
     * @param DocAction The targeted status of the document
     */
    public void setDocAction(String DocAction) {

        setValue(COLUMNNAME_DocAction, DocAction);
    }

    /**
     * Get Document Status.
     *
     * @return The current status of the document
     */
    public String getDocStatus() {
        return getValue(COLUMNNAME_DocStatus);
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(String DocStatus) {

        setValue(COLUMNNAME_DocStatus, DocStatus);
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Document No.
     *
     * @param DocumentNo Document sequence number of the document
     */
    public void setDocumentNo(String DocumentNo) {
        setValue(COLUMNNAME_DocumentNo, DocumentNo);
    }

    /**
     * Get Float After.
     *
     * @return Float After
     */
    public BigDecimal getFloatAfter() {
        BigDecimal bd = getValue(COLUMNNAME_FloatAfter);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Float After.
     *
     * @param FloatAfter Float After
     */
    public void setFloatAfter(BigDecimal FloatAfter) {
        setValue(COLUMNNAME_FloatAfter, FloatAfter);
    }

    /**
     * Get Float Befored.
     *
     * @return Float Befored
     */
    public BigDecimal getFloatBefored() {
        BigDecimal bd = getValue(COLUMNNAME_FloatBefored);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Float Befored.
     *
     * @param FloatBefored Float Befored
     */
    public void setFloatBefored(BigDecimal FloatBefored) {
        setValue(COLUMNNAME_FloatBefored, FloatBefored);
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValue(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
    }

    /**
     * Get Approved.
     *
     * @return Indicates if this document requires approval
     */
    public boolean isApproved() {
        Object oo = getValue(COLUMNNAME_IsApproved);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Printed.
     *
     * @param IsPrinted Indicates if this document / line is printed
     */
    public void setIsPrinted(boolean IsPrinted) {
        setValue(COLUMNNAME_IsPrinted, Boolean.valueOf(IsPrinted));
    }

    /**
     * Get Printed.
     *
     * @return Indicates if this document / line is printed
     */
    public boolean isPrinted() {
        Object oo = getValue(COLUMNNAME_IsPrinted);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Is Qty Percentage.
     *
     * @param IsQtyPercentage Indicate that this component is based in % Quantity
     */
    public void setIsQtyPercentage(boolean IsQtyPercentage) {
        setValue(COLUMNNAME_IsQtyPercentage, Boolean.valueOf(IsQtyPercentage));
    }

    /**
     * Get Is Qty Percentage.
     *
     * @return Indicate that this component is based in % Quantity
     */
    public boolean isQtyPercentage() {
        Object oo = getValue(COLUMNNAME_IsQtyPercentage);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Selected.
     *
     * @param IsSelected Selected
     */
    public void setIsSelected(boolean IsSelected) {
        setValue(COLUMNNAME_IsSelected, Boolean.valueOf(IsSelected));
    }

    /**
     * Get Selected.
     *
     * @return Selected
     */
    public boolean isSelected() {
        Object oo = getValue(COLUMNNAME_IsSelected);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Sales Transaction.
     *
     * @param IsSOTrx This is a Sales Transaction
     */
    public void setIsSOTrx(boolean IsSOTrx) {
        setValue(COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
    }

    /**
     * Get Sales Transaction.
     *
     * @return This is a Sales Transaction
     */
    public boolean isSOTrx() {
        Object oo = getValue(COLUMNNAME_IsSOTrx);
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
     * Get Lot No.
     *
     * @return Lot number (alphanumeric)
     */
    public String getLot() {
        return getValue(COLUMNNAME_Lot);
    }

    /**
     * Set Lot No.
     *
     * @param Lot Lot number (alphanumeric)
     */
    public void setLot(String Lot) {
        setValue(COLUMNNAME_Lot, Lot);
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
        else setValueNoCheck(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    public org.compiere.model.I_M_Warehouse getWarehouse() throws RuntimeException {
        return (org.compiere.model.I_M_Warehouse)
                MBaseTableKt.getTable(org.compiere.model.I_M_Warehouse.Table_Name)
                        .getPO(getWarehouseId());
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
     * Set Warehouse.
     *
     * @param M_Warehouse_ID Storage Warehouse and Service Point
     */
    public void setWarehouseId(int M_Warehouse_ID) {
        if (M_Warehouse_ID < 1) setValueNoCheck(COLUMNNAME_M_Warehouse_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
    }

    /**
     * Get Order Type.
     *
     * @return Type of Order: MRP records grouped by source (Sales Order, Purchase Order, Distribution
     * Order, Requisition)
     */
    public String getOrderType() {
        return getValue(COLUMNNAME_OrderType);
    }

    /**
     * Set Order Type.
     *
     * @param OrderType Type of Order: MRP records grouped by source (Sales Order, Purchase Order,
     *                  Distribution Order, Requisition)
     */
    public void setOrderType(String OrderType) {
        setValue(COLUMNNAME_OrderType, OrderType);
    }

    public User getPlanner() throws RuntimeException {
        return (User)
                MBaseTableKt.getTable(User.Table_Name)
                        .getPO(getPlannerId());
    }

    /**
     * Get Planner.
     *
     * @return Planner
     */
    public int getPlannerId() {
        Integer ii = getValue(COLUMNNAME_Planner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Planner.
     *
     * @param Planner_ID Planner
     */
    public void setPlannerId(int Planner_ID) {
        if (Planner_ID < 1) setValue(COLUMNNAME_Planner_ID, null);
        else setValue(COLUMNNAME_Planner_ID, Integer.valueOf(Planner_ID));
    }

    /**
     * Get Posted.
     *
     * @return Posting status
     */
    public boolean isPosted() {
        Object oo = getValue(COLUMNNAME_Posted);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValue(COLUMNNAME_Posted, Boolean.valueOf(Posted));
    }

    /**
     * Get Manufacturing Order.
     *
     * @return Manufacturing Order
     */
    public int getOrderId() {
        Integer ii = getValue(COLUMNNAME_PP_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Manufacturing Order.
     *
     * @param PP_Order_ID Manufacturing Order
     */
    public void setOrderId(int PP_Order_ID) {
        if (PP_Order_ID < 1) setValueNoCheck(COLUMNNAME_PP_Order_ID, null);
        else setValueNoCheck(COLUMNNAME_PP_Order_ID, Integer.valueOf(PP_Order_ID));
    }

    /**
     * Get PP_Order_UU.
     *
     * @return PP_Order_UU
     */
    public String getOrderUU() {
        return getValue(COLUMNNAME_PP_Order_UU);
    }

    /**
     * Set PP_Order_UU.
     *
     * @param PP_Order_UU PP_Order_UU
     */
    public void setOrderUU(String PP_Order_UU) {
        setValue(COLUMNNAME_PP_Order_UU, PP_Order_UU);
    }

    public org.eevolution.model.I_PP_Product_BOM getProductBOM() throws RuntimeException {
        return (org.eevolution.model.I_PP_Product_BOM)
                MBaseTableKt.getTable(org.eevolution.model.I_PP_Product_BOM.Table_Name)
                        .getPO(getProductBOMId());
    }

    /**
     * Get BOM & Formula.
     *
     * @return BOM & Formula
     */
    public int getProductBOMId() {
        Integer ii = getValue(COLUMNNAME_PP_Product_BOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set BOM & Formula.
     *
     * @param PP_Product_BOM_ID BOM & Formula
     */
    public void setProductBOMId(int PP_Product_BOM_ID) {
        if (PP_Product_BOM_ID < 1) setValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, null);
        else setValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, Integer.valueOf(PP_Product_BOM_ID));
    }

    /**
     * Get Priority.
     *
     * @return Priority of a document
     */
    public String getPriorityRule() {
        return getValue(COLUMNNAME_PriorityRule);
    }

    /**
     * Set Priority.
     *
     * @param PriorityRule Priority of a document
     */
    public void setPriorityRule(String PriorityRule) {

        setValue(COLUMNNAME_PriorityRule, PriorityRule);
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
     * Get Processed On.
     *
     * @return The date+time (expressed in decimal format) when the document has been processed
     */
    public BigDecimal getProcessedOn() {
        BigDecimal bd = getValue(COLUMNNAME_ProcessedOn);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Processed On.
     *
     * @param ProcessedOn The date+time (expressed in decimal format) when the document has been
     *                    processed
     */
    public void setProcessedOn(BigDecimal ProcessedOn) {
        setValue(COLUMNNAME_ProcessedOn, ProcessedOn);
    }

    /**
     * Get Process Now.
     *
     * @return Process Now
     */
    public boolean isProcessing() {
        Object oo = getValue(COLUMNNAME_Processing);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Boolean.valueOf(Processing));
    }

    /**
     * Get Qty Batchs.
     *
     * @return Qty Batchs
     */
    public BigDecimal getQtyBatchs() {
        BigDecimal bd = getValue(COLUMNNAME_QtyBatchs);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Qty Batchs.
     *
     * @param QtyBatchs Qty Batchs
     */
    public void setQtyBatchs(BigDecimal QtyBatchs) {
        setValueNoCheck(COLUMNNAME_QtyBatchs, QtyBatchs);
    }

    /**
     * Get Qty Batch Size.
     *
     * @return Qty Batch Size
     */
    public BigDecimal getQtyBatchSize() {
        BigDecimal bd = getValue(COLUMNNAME_QtyBatchSize);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Qty Batch Size.
     *
     * @param QtyBatchSize Qty Batch Size
     */
    public void setQtyBatchSize(BigDecimal QtyBatchSize) {
        setValueNoCheck(COLUMNNAME_QtyBatchSize, QtyBatchSize);
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
        setValueNoCheck(COLUMNNAME_QtyOrdered, QtyOrdered);
    }

    /**
     * Get Qty Reject.
     *
     * @return Qty Reject
     */
    public BigDecimal getQtyReject() {
        BigDecimal bd = getValue(COLUMNNAME_QtyReject);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Qty Reject.
     *
     * @param QtyReject Qty Reject
     */
    public void setQtyReject(BigDecimal QtyReject) {
        setValue(COLUMNNAME_QtyReject, QtyReject);
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
     * Get Scrap %.
     *
     * @return Scrap % Quantity for this componet
     */
    public BigDecimal getQtyScrap() {
        BigDecimal bd = getValue(COLUMNNAME_QtyScrap);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Scrap %.
     *
     * @param QtyScrap Scrap % Quantity for this componet
     */
    public void setQtyScrap(BigDecimal QtyScrap) {
        setValue(COLUMNNAME_QtyScrap, QtyScrap);
    }

    /**
     * Get Schedule Type.
     *
     * @return Type of schedule
     */
    public String getScheduleType() {
        return getValue(COLUMNNAME_ScheduleType);
    }

    /**
     * Set Schedule Type.
     *
     * @param ScheduleType Type of schedule
     */
    public void setScheduleType(String ScheduleType) {
        setValue(COLUMNNAME_ScheduleType, ScheduleType);
    }

    /**
     * Get Serial No.
     *
     * @return Product Serial Number
     */
    public String getSerNo() {
        return getValue(COLUMNNAME_SerNo);
    }

    /**
     * Set Serial No.
     *
     * @param SerNo Product Serial Number
     */
    public void setSerNo(String SerNo) {
        setValue(COLUMNNAME_SerNo, SerNo);
    }

    public org.compiere.model.I_S_Resource getResource() throws RuntimeException {
        return (org.compiere.model.I_S_Resource)
                MBaseTableKt.getTable(org.compiere.model.I_S_Resource.Table_Name)
                        .getPO(getResourceID());
    }

    /**
     * Get Resource.
     *
     * @return Resource
     */
    public int getResourceID() {
        Integer ii = getValue(COLUMNNAME_S_Resource_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Resource.
     *
     * @param S_Resource_ID Resource
     */
    public void setResourceID(int S_Resource_ID) {
        if (S_Resource_ID < 1) setValueNoCheck(COLUMNNAME_S_Resource_ID, null);
        else setValueNoCheck(COLUMNNAME_S_Resource_ID, Integer.valueOf(S_Resource_ID));
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

    /**
     * Get Yield %.
     *
     * @return The Yield is the percentage of a lot that is expected to be of acceptable wuality may
     * fall below 100 percent
     */
    public BigDecimal getYield() {
        BigDecimal bd = getValue(COLUMNNAME_Yield);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Yield %.
     *
     * @param Yield The Yield is the percentage of a lot that is expected to be of acceptable wuality
     *              may fall below 100 percent
     */
    public void setYield(BigDecimal Yield) {
        setValue(COLUMNNAME_Yield, Yield);
    }
}
