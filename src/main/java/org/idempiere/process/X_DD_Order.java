package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.AccountingElementValue;
import org.compiere.model.DocumentType;
import org.compiere.model.User;
import org.compiere.orm.PO;
import org.eevolution.model.I_DD_Order;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_DD_Order extends PO implements I_DD_Order {

    /**
     * Availability = A
     */
    public static final String DELIVERYRULE_Availability = "A";
    /**
     * Complete Order = O
     */
    public static final String DELIVERYRULE_CompleteOrder = "O";
    /**
     * Pickup = P
     */
    public static final String DELIVERYVIARULE_Pickup = "P";
    /**
     * Delivery = D
     */
    public static final String DELIVERYVIARULE_Delivery = "D";
    /**
     * Complete = CO
     */
    public static final String DOCACTION_Complete = "CO";
    /**
     * Close = CL
     */
    public static final String DOCACTION_Close = "CL";
    /**
     * <None> = --
     */
    public static final String DOCACTION_None = "--";
    /**
     * Prepare = PR
     */
    public static final String DOCACTION_Prepare = "PR";
    /**
     * Drafted = DR
     */
    public static final String DOCSTATUS_Drafted = "DR";
    /**
     * Completed = CO
     */
    public static final String DOCSTATUS_Completed = "CO";
    /**
     * Reversed = RE
     */
    public static final String DOCSTATUS_Reversed = "RE";
    /**
     * Closed = CL
     */
    public static final String DOCSTATUS_Closed = "CL";
    /**
     * Freight included = I
     */
    public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
    /**
     * Medium = 5
     */
    public static final String PRIORITYRULE_Medium = "5";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_DD_Order(int DD_Order_ID) {
        super(DD_Order_ID);
    }

    /**
     * Load Constructor
     */
    public X_DD_Order(Row row) {
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
        return "X_DD_Order[" + getId() + "]";
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

    public User getUser() throws RuntimeException {
        return (User)
                MBaseTableKt.getTable(User.Table_Name)
                        .getPO(getUserId());
    }

    /**
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getUserId() {
        Integer ii = getValue(COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User/Contact.
     *
     * @param AD_User_ID User within the system - Internal or Business Partner Contact
     */
    public void setUserId(int AD_User_ID) {
        if (AD_User_ID < 1) setValue(COLUMNNAME_AD_User_ID, null);
        else setValue(COLUMNNAME_AD_User_ID, AD_User_ID);
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
        else setValue(COLUMNNAME_C_Activity_ID, C_Activity_ID);
    }

    public org.compiere.model.I_C_BPartner getBPartner() throws RuntimeException {
        return (org.compiere.model.I_C_BPartner)
                MBaseTableKt.getTable(org.compiere.model.I_C_BPartner.Table_Name)
                        .getPO(getBusinessPartnerId());
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

    public org.compiere.model.I_C_BPartner_Location getBPartnerLocation() throws RuntimeException {
        return (org.compiere.model.I_C_BPartner_Location)
                MBaseTableKt.getTable(org.compiere.model.I_C_BPartner_Location.Table_Name)
                        .getPO(getBusinessPartnerLocationId());
    }

    /**
     * Get Partner Location.
     *
     * @return Identifies the (ship to) address for this Business Partner
     */
    public int getBusinessPartnerLocationId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_Location_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Partner Location.
     *
     * @param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
     */
    public void setBusinessPartnerLocationId(int C_BPartner_Location_ID) {
        if (C_BPartner_Location_ID < 1) setValue(COLUMNNAME_C_BPartner_Location_ID, null);
        else setValue(COLUMNNAME_C_BPartner_Location_ID, C_BPartner_Location_ID);
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
        else setValue(COLUMNNAME_C_Campaign_ID, C_Campaign_ID);
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
        else setValue(COLUMNNAME_C_Charge_ID, C_Charge_ID);
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
        if (C_DocType_ID < 0) setValueNoCheck(COLUMNNAME_C_DocType_ID, null);
        else setValueNoCheck(COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
    }

    /**
     * Get Charge amount.
     *
     * @return Charge Amount
     */
    public BigDecimal getChargeAmt() {
        BigDecimal bd = getValue(COLUMNNAME_ChargeAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Charge amount.
     *
     * @param ChargeAmt Charge Amount
     */
    public void setChargeAmt(BigDecimal ChargeAmt) {
        setValue(COLUMNNAME_ChargeAmt, ChargeAmt);
    }

    public org.compiere.model.I_C_Invoice getInvoice() throws RuntimeException {
        return (org.compiere.model.I_C_Invoice)
                MBaseTableKt.getTable(org.compiere.model.I_C_Invoice.Table_Name)
                        .getPO(getInvoiceId());
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = getValue(COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValueNoCheck(COLUMNNAME_C_Invoice_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    public org.compiere.model.I_C_Order getOrder() throws RuntimeException {
        return (org.compiere.model.I_C_Order)
                MBaseTableKt.getTable(org.compiere.model.I_C_Order.Table_Name)
                        .getPO(getOrderId());
    }

    /**
     * Get Order.
     *
     * @return Order
     */
    public int getOrderId() {
        Integer ii = getValue(COLUMNNAME_C_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Order.
     *
     * @param C_Order_ID Order
     */
    public void setOrderId(int C_Order_ID) {
        if (C_Order_ID < 1) setValueNoCheck(COLUMNNAME_C_Order_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Order_ID, C_Order_ID);
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

    /**
     * Get Create Confirm.
     *
     * @return Create Confirm
     */
    public String getCreateConfirm() {
        return getValue(COLUMNNAME_CreateConfirm);
    }

    /**
     * Set Create Confirm.
     *
     * @param CreateConfirm Create Confirm
     */
    public void setCreateConfirm(String CreateConfirm) {
        setValue(COLUMNNAME_CreateConfirm, CreateConfirm);
    }

    /**
     * Get Create lines from.
     *
     * @return Process which will generate a new document lines based on an existing document
     */
    public String getCreateFrom() {
        return getValue(COLUMNNAME_CreateFrom);
    }

    /**
     * Set Create lines from.
     *
     * @param CreateFrom Process which will generate a new document lines based on an existing
     *                   document
     */
    public void setCreateFrom(String CreateFrom) {
        setValue(COLUMNNAME_CreateFrom, CreateFrom);
    }

    /**
     * Get Create Package.
     *
     * @return Create Package
     */
    public String getCreatePackage() {
        return getValue(COLUMNNAME_CreatePackage);
    }

    /**
     * Set Create Package.
     *
     * @param CreatePackage Create Package
     */
    public void setCreatePackage(String CreatePackage) {
        setValue(COLUMNNAME_CreatePackage, CreatePackage);
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
        setValueNoCheck(COLUMNNAME_DateOrdered, DateOrdered);
    }

    /**
     * Get Date printed.
     *
     * @return Date the document was printed.
     */
    public Timestamp getDatePrinted() {
        return (Timestamp) getValue(COLUMNNAME_DatePrinted);
    }

    /**
     * Set Date printed.
     *
     * @param DatePrinted Date the document was printed.
     */
    public void setDatePrinted(Timestamp DatePrinted) {
        setValue(COLUMNNAME_DatePrinted, DatePrinted);
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
     * Get Date received.
     *
     * @return Date a product was received
     */
    public Timestamp getDateReceived() {
        return (Timestamp) getValue(COLUMNNAME_DateReceived);
    }

    /**
     * Set Date received.
     *
     * @param DateReceived Date a product was received
     */
    public void setDateReceived(Timestamp DateReceived) {
        setValue(COLUMNNAME_DateReceived, DateReceived);
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
     * Get DD_Order_UU.
     *
     * @return DD_Order_UU
     */
    public String getOrder_UU() {
        return getValue(COLUMNNAME_DD_Order_UU);
    }

    /**
     * Set DD_Order_UU.
     *
     * @param DD_Order_UU DD_Order_UU
     */
    public void setOrder_UU(String DD_Order_UU) {
        setValue(COLUMNNAME_DD_Order_UU, DD_Order_UU);
    }

    /**
     * Get Delivery Rule.
     *
     * @return Defines the timing of Delivery
     */
    public String getDeliveryRule() {
        return getValue(COLUMNNAME_DeliveryRule);
    }

    /**
     * Set Delivery Rule.
     *
     * @param DeliveryRule Defines the timing of Delivery
     */
    public void setDeliveryRule(String DeliveryRule) {

        setValue(COLUMNNAME_DeliveryRule, DeliveryRule);
    }

    /**
     * Get Delivery Via.
     *
     * @return How the order will be delivered
     */
    public String getDeliveryViaRule() {
        return getValue(COLUMNNAME_DeliveryViaRule);
    }

    /**
     * Set Delivery Via.
     *
     * @param DeliveryViaRule How the order will be delivered
     */
    public void setDeliveryViaRule(String DeliveryViaRule) {

        setValue(COLUMNNAME_DeliveryViaRule, DeliveryViaRule);
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
        setValueNoCheck(COLUMNNAME_DocumentNo, DocumentNo);
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
     * Get Freight Cost Rule.
     *
     * @return Method for charging Freight
     */
    public String getFreightCostRule() {
        return getValue(COLUMNNAME_FreightCostRule);
    }

    /**
     * Set Freight Cost Rule.
     *
     * @param FreightCostRule Method for charging Freight
     */
    public void setFreightCostRule(String FreightCostRule) {

        setValue(COLUMNNAME_FreightCostRule, FreightCostRule);
    }

    /**
     * Get Generate To.
     *
     * @return Generate To
     */
    public String getGenerateTo() {
        return getValue(COLUMNNAME_GenerateTo);
    }

    /**
     * Set Generate To.
     *
     * @param GenerateTo Generate To
     */
    public void setGenerateTo(String GenerateTo) {
        setValue(COLUMNNAME_GenerateTo, GenerateTo);
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
     * Set Delivered.
     *
     * @param IsDelivered Delivered
     */
    public void setIsDelivered(boolean IsDelivered) {
        setValue(COLUMNNAME_IsDelivered, Boolean.valueOf(IsDelivered));
    }

    /**
     * Get Delivered.
     *
     * @return Delivered
     */
    public boolean isDelivered() {
        Object oo = getValue(COLUMNNAME_IsDelivered);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Drop Shipment.
     *
     * @param IsDropShip Drop Shipments are sent from the Vendor directly to the Customer
     */
    public void setIsDropShip(boolean IsDropShip) {
        setValue(COLUMNNAME_IsDropShip, Boolean.valueOf(IsDropShip));
    }

    /**
     * Get Drop Shipment.
     *
     * @return Drop Shipments are sent from the Vendor directly to the Customer
     */
    public boolean isDropShip() {
        Object oo = getValue(COLUMNNAME_IsDropShip);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set In Dispute.
     *
     * @param IsInDispute Document is in dispute
     */
    public void setIsInDispute(boolean IsInDispute) {
        setValue(COLUMNNAME_IsInDispute, Boolean.valueOf(IsInDispute));
    }

    /**
     * Get In Dispute.
     *
     * @return Document is in dispute
     */
    public boolean isInDispute() {
        Object oo = getValue(COLUMNNAME_IsInDispute);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set In Transit.
     *
     * @param IsInTransit Movement is in transit
     */
    public void setIsInTransit(boolean IsInTransit) {
        setValue(COLUMNNAME_IsInTransit, Boolean.valueOf(IsInTransit));
    }

    /**
     * Get In Transit.
     *
     * @return Movement is in transit
     */
    public boolean isInTransit() {
        Object oo = getValue(COLUMNNAME_IsInTransit);
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
     * Get No Packages.
     *
     * @return Number of packages shipped
     */
    public int getNoPackages() {
        Integer ii = getValue(COLUMNNAME_NoPackages);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set No Packages.
     *
     * @param NoPackages Number of packages shipped
     */
    public void setNoPackages(int NoPackages) {
        setValue(COLUMNNAME_NoPackages, Integer.valueOf(NoPackages));
    }

    /**
     * Get Pick Date.
     *
     * @return Date/Time when picked for Shipment
     */
    public Timestamp getPickDate() {
        return (Timestamp) getValue(COLUMNNAME_PickDate);
    }

    /**
     * Set Pick Date.
     *
     * @param PickDate Date/Time when picked for Shipment
     */
    public void setPickDate(Timestamp PickDate) {
        setValue(COLUMNNAME_PickDate, PickDate);
    }

    /**
     * Get Order Reference.
     *
     * @return Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner
     */
    public String getPOReference() {
        return getValue(COLUMNNAME_POReference);
    }

    /**
     * Set Order Reference.
     *
     * @param POReference Transaction Reference Number (Sales Order, Purchase Order) of your Business
     *                    Partner
     */
    public void setPOReference(String POReference) {
        setValue(COLUMNNAME_POReference, POReference);
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

    public org.compiere.model.I_C_Order getReferencedOrder() throws RuntimeException {
        return (org.compiere.model.I_C_Order)
                MBaseTableKt.getTable(org.compiere.model.I_C_Order.Table_Name)
                        .getPO(getReferencedOrderId());
    }

    /**
     * Get Referenced Order.
     *
     * @return Reference to corresponding Sales/Purchase Order
     */
    public int getReferencedOrderId() {
        Integer ii = getValue(COLUMNNAME_Ref_Order_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Referenced Order.
     *
     * @param Ref_Order_ID Reference to corresponding Sales/Purchase Order
     */
    public void setReferencedOrderId(int Ref_Order_ID) {
        if (Ref_Order_ID < 1) setValue(COLUMNNAME_Ref_Order_ID, null);
        else setValue(COLUMNNAME_Ref_Order_ID, Integer.valueOf(Ref_Order_ID));
    }

    public User getSalesRep() throws RuntimeException {
        return (User)
                MBaseTableKt.getTable(User.Table_Name)
                        .getPO(getSalesRepresentativeId());
    }

    /**
     * Get Sales Representative.
     *
     * @return Sales Representative or Company Agent
     */
    public int getSalesRepresentativeId() {
        Integer ii = getValue(COLUMNNAME_SalesRep_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Representative.
     *
     * @param SalesRep_ID Sales Representative or Company Agent
     */
    public void setSalesRepresentativeId(int SalesRep_ID) {
        if (SalesRep_ID < 1) setValue(COLUMNNAME_SalesRep_ID, null);
        else setValue(COLUMNNAME_SalesRep_ID, Integer.valueOf(SalesRep_ID));
    }

    /**
     * Get Send EMail.
     *
     * @return Enable sending Document EMail
     */
    public boolean isSendEMail() {
        Object oo = getValue(COLUMNNAME_SendEMail);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Send EMail.
     *
     * @param SendEMail Enable sending Document EMail
     */
    public void setSendEMail(boolean SendEMail) {
        setValue(COLUMNNAME_SendEMail, Boolean.valueOf(SendEMail));
    }

    /**
     * Get Ship Date.
     *
     * @return Shipment Date/Time
     */
    public Timestamp getShipDate() {
        return (Timestamp) getValue(COLUMNNAME_ShipDate);
    }

    /**
     * Set Ship Date.
     *
     * @param ShipDate Shipment Date/Time
     */
    public void setShipDate(Timestamp ShipDate) {
        setValue(COLUMNNAME_ShipDate, ShipDate);
    }

    /**
     * Get Tracking No.
     *
     * @return Number to track the shipment
     */
    public String getTrackingNo() {
        return getValue(COLUMNNAME_TrackingNo);
    }

    /**
     * Set Tracking No.
     *
     * @param TrackingNo Number to track the shipment
     */
    public void setTrackingNo(String TrackingNo) {
        setValue(COLUMNNAME_TrackingNo, TrackingNo);
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
        else setValue(COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
    }

    /**
     * Get Volume.
     *
     * @return Volume of a product
     */
    public BigDecimal getVolume() {
        BigDecimal bd = getValue(COLUMNNAME_Volume);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Volume.
     *
     * @param Volume Volume of a product
     */
    public void setVolume(BigDecimal Volume) {
        setValue(COLUMNNAME_Volume, Volume);
    }

    /**
     * Get Weight.
     *
     * @return Weight of a product
     */
    public BigDecimal getWeight() {
        BigDecimal bd = getValue(COLUMNNAME_Weight);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Weight.
     *
     * @param Weight Weight of a product
     */
    public void setWeight(BigDecimal Weight) {
        setValue(COLUMNNAME_Weight, Weight);
    }
}
