package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_M_Production;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for M_Production
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_Production extends BasePOName {

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
     * Approved = AP
     */
    public static final String DOCSTATUS_Approved = "AP";
    /**
     * Not Approved = NA
     */
    public static final String DOCSTATUS_NotApproved = "NA";
    /**
     * Voided = VO
     */
    public static final String DOCSTATUS_Voided = "VO";
    /**
     * Invalid = IN
     */
    public static final String DOCSTATUS_Invalid = "IN";
    /**
     * Reversed = RE
     */
    public static final String DOCSTATUS_Reversed = "RE";
    /**
     * Closed = CL
     */
    public static final String DOCSTATUS_Closed = "CL";
    /**
     * In Progress = IP
     */
    public static final String DOCSTATUS_InProgress = "IP";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Production(int M_Production_ID) {
        super(M_Production_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_Production(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return I_M_Production.accessLevel.intValue();
    }

    public String toString() {
        return "X_M_Production[" + getId() + "]";
    }

    /**
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setTransactionOrganizationId(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) setValue(I_M_Production.COLUMNNAME_AD_OrgTrx_ID, null);
        else setValue(I_M_Production.COLUMNNAME_AD_OrgTrx_ID, AD_OrgTrx_ID);
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValue(I_M_Production.COLUMNNAME_C_Activity_ID, null);
        else setValue(I_M_Production.COLUMNNAME_C_Activity_ID, C_Activity_ID);
    }

    /**
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValueNoCheck(I_M_Production.COLUMNNAME_C_BPartner_ID, null);
        else setValueNoCheck(I_M_Production.COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValue(I_M_Production.COLUMNNAME_C_Campaign_ID, null);
        else setValue(I_M_Production.COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValue(I_M_Production.COLUMNNAME_C_Project_ID, null);
        else setValue(I_M_Production.COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
    }

    /**
     * Set Project Phase.
     *
     * @param C_ProjectPhase_ID Phase of a Project
     */
    public void setProjectPhaseId(int C_ProjectPhase_ID) {
        if (C_ProjectPhase_ID < 1) setValueNoCheck(I_M_Production.COLUMNNAME_C_ProjectPhase_ID, null);
        else
            setValueNoCheck(
                    I_M_Production.COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
    }

    /**
     * Set Project Task.
     *
     * @param C_ProjectTask_ID Actual Project Task in a Phase
     */
    public void setProjectTaskId(int C_ProjectTask_ID) {
        if (C_ProjectTask_ID < 1) setValueNoCheck(I_M_Production.COLUMNNAME_C_ProjectTask_ID, null);
        else
            setValueNoCheck(
                    I_M_Production.COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
    }

    /**
     * Set Sales Order Line.
     *
     * @param C_OrderLine_ID Sales Order Line
     */
    public void setOrderLineId(int C_OrderLine_ID) {
        if (C_OrderLine_ID < 1) setValue(I_M_Production.COLUMNNAME_C_OrderLine_ID, null);
        else setValue(I_M_Production.COLUMNNAME_C_OrderLine_ID, C_OrderLine_ID);
    }

    /**
     * Set Date Promised.
     *
     * @param DatePromised Date Order was promised
     */
    public void setDatePromised(Timestamp DatePromised) {
        setValue(I_M_Production.COLUMNNAME_DatePromised, DatePromised);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(I_M_Production.COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(I_M_Production.COLUMNNAME_Description, Description);
    }

    /**
     * Get Document Action.
     *
     * @return The targeted status of the document
     */
    public String getDocAction() {
        return getValue(I_M_Production.COLUMNNAME_DocAction);
    }

    /**
     * Set Document Action.
     *
     * @param DocAction The targeted status of the document
     */
    public void setDocAction(String DocAction) {

        setValue(I_M_Production.COLUMNNAME_DocAction, DocAction);
    }

    /**
     * Get Document Status.
     *
     * @return The current status of the document
     */
    public String getDocStatus() {
        return getValue(I_M_Production.COLUMNNAME_DocStatus);
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(String DocStatus) {

        setValue(I_M_Production.COLUMNNAME_DocStatus, DocStatus);
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(I_M_Production.COLUMNNAME_DocumentNo);
    }

    /**
     * Set Complete.
     *
     * @param IsComplete It is complete
     */
    public void setIsComplete(boolean IsComplete) {
        setValue(I_M_Production.COLUMNNAME_IsComplete, Boolean.valueOf(IsComplete));
    }

    /**
     * Get Records created.
     *
     * @return Records created
     */
    public String getIsCreated() {
        return getValue(I_M_Production.COLUMNNAME_IsCreated);
    }

    /**
     * Set Records created.
     *
     * @param IsCreated Records created
     */
    public void setIsCreated(String IsCreated) {

        setValue(I_M_Production.COLUMNNAME_IsCreated, IsCreated);
    }

    /**
     * Set Use Production Plan.
     *
     * @param IsUseProductionPlan Use Production Plan
     */
    public void setIsUseProductionPlan(boolean IsUseProductionPlan) {
        setValue(I_M_Production.COLUMNNAME_IsUseProductionPlan, IsUseProductionPlan);
    }

    /**
     * Get Use Production Plan.
     *
     * @return Use Production Plan
     */
    public boolean isUseProductionPlan() {
        Object oo = getValue(I_M_Production.COLUMNNAME_IsUseProductionPlan);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Locator.
     *
     * @return Warehouse Locator
     */
    public int getLocatorId() {
        Integer ii = getValue(I_M_Production.COLUMNNAME_M_Locator_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Locator.
     *
     * @param M_Locator_ID Warehouse Locator
     */
    public void setLocatorId(int M_Locator_ID) {
        if (M_Locator_ID < 1) setValue(I_M_Production.COLUMNNAME_M_Locator_ID, null);
        else setValue(I_M_Production.COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
    }

    /**
     * Get Movement Date.
     *
     * @return Date a product was moved in or out of inventory
     */
    public Timestamp getMovementDate() {
        return (Timestamp) getValue(I_M_Production.COLUMNNAME_MovementDate);
    }

    /**
     * Set Movement Date.
     *
     * @param MovementDate Date a product was moved in or out of inventory
     */
    public void setMovementDate(Timestamp MovementDate) {
        setValue(I_M_Production.COLUMNNAME_MovementDate, MovementDate);
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(I_M_Production.COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(I_M_Production.COLUMNNAME_M_Product_ID, null);
        else setValue(I_M_Production.COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Get Production.
     *
     * @return Plan for producing a product
     */
    public int getProductionId() {
        Integer ii = getValue(I_M_Production.COLUMNNAME_M_Production_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(I_M_Production.COLUMNNAME_Processed);
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
        setValue(I_M_Production.COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(I_M_Production.COLUMNNAME_Processing, Boolean.valueOf(Processing));
    }

    /**
     * Get Production Quantity.
     *
     * @return Quantity of products to produce
     */
    public BigDecimal getProductionQty() {
        BigDecimal bd = getValue(I_M_Production.COLUMNNAME_ProductionQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Production Quantity.
     *
     * @param ProductionQty Quantity of products to produce
     */
    public void setProductionQty(BigDecimal ProductionQty) {
        setValue(I_M_Production.COLUMNNAME_ProductionQty, ProductionQty);
    }

    /**
     * Set Reversal ID.
     *
     * @param Reversal_ID ID of document reversal
     */
    public void setReversalId(int Reversal_ID) {
        if (Reversal_ID < 1) setValue(I_M_Production.COLUMNNAME_Reversal_ID, null);
        else setValue(I_M_Production.COLUMNNAME_Reversal_ID, Reversal_ID);
    }

    @Override
    public int getTableId() {
        return I_M_Production.Table_ID;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValue(I_M_Production.COLUMNNAME_User1_ID, null);
        else setValue(I_M_Production.COLUMNNAME_User1_ID, User1_ID);
    }


    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValue(I_M_Production.COLUMNNAME_User2_ID, null);
        else setValue(I_M_Production.COLUMNNAME_User2_ID, User2_ID);
    }
}
