package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_Project;
import org.compiere.orm.BasePONameValue;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_Project
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_Project extends BasePONameValue implements I_C_Project {

    /**
     * Asset Project = A
     */
    public static final String PROJECTCATEGORY_AssetProject = "A";
    /**
     * Work Order (Job) = W
     */
    public static final String PROJECTCATEGORY_WorkOrderJob = "W";
    /**
     * Service (Charge) Project = S
     */
    public static final String PROJECTCATEGORY_ServiceChargeProject = "S";
    /**
     * Project = P
     */
    public static final String PROJECTLINELEVEL_Project = "P";
    /**
     * None = -
     */
    public static final String PROJINVOICERULE_None = "-";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Project(int C_Project_ID) {
        super(C_Project_ID);
        /**
         * if (C_Project_ID == 0) { setCurrencyId (0); setCommittedAmt (Env.ZERO); setCommittedQty
         * (Env.ZERO); setProjectId (0); setInvoicedAmt (Env.ZERO); setInvoicedQty (Env.ZERO);
         * setIsCommitCeiling (false); setIsCommitment (false); setIsSummary (false); setName (null);
         * setPlannedAmt (Env.ZERO); setPlannedMarginAmt (Env.ZERO); setPlannedQty (Env.ZERO);
         * setProcessed (false); setProjectBalanceAmt (Env.ZERO); setProjectLineLevel (null); // P
         * setProjInvoiceRule (null); // - setValue (null); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_Project(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_Project[").append(getId()).append("]");
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
        else setValue(COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
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
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(COLUMNNAME_C_Currency_ID, null);
        else setValue(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
    }

    /**
     * Set Committed Amount.
     *
     * @param CommittedAmt The (legal) commitment amount
     */
    public void setCommittedAmt(BigDecimal CommittedAmt) {
        setValue(COLUMNNAME_CommittedAmt, CommittedAmt);
    }

    /**
     * Set Committed Quantity.
     *
     * @param CommittedQty The (legal) commitment Quantity
     */
    public void setCommittedQty(BigDecimal CommittedQty) {
        setValue(COLUMNNAME_CommittedQty, CommittedQty);
    }

    /**
     * Get Payment Term.
     *
     * @return The terms of Payment (timing, discount)
     */
    public int getPaymentTermId() {
        Integer ii = getValue(COLUMNNAME_C_PaymentTerm_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Project Type.
     *
     * @return Type of the project
     */
    public String getProjectTypeId() {
        return getValue(COLUMNNAME_C_ProjectType_ID);
    }

    /**
     * Set Project Type.
     *
     * @param C_ProjectType_ID Type of the project
     */
    public void setProjectTypeId(String C_ProjectType_ID) {
        setValue(COLUMNNAME_C_ProjectType_ID, C_ProjectType_ID);
    }

    /**
     * Get Contract Date.
     *
     * @return The (planned) effective date of this document.
     */
    public Timestamp getDateContract() {
        return (Timestamp) getValue(COLUMNNAME_DateContract);
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Get Invoiced Amount.
     *
     * @return The amount invoiced
     */
    public BigDecimal getInvoicedAmt() {
        BigDecimal bd = getValue(COLUMNNAME_InvoicedAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Invoiced Amount.
     *
     * @param InvoicedAmt The amount invoiced
     */
    public void setInvoicedAmt(BigDecimal InvoicedAmt) {
        setValueNoCheck(COLUMNNAME_InvoicedAmt, InvoicedAmt);
    }

    /**
     * Set Quantity Invoiced .
     *
     * @param InvoicedQty The quantity invoiced
     */
    public void setInvoicedQty(BigDecimal InvoicedQty) {
        setValueNoCheck(COLUMNNAME_InvoicedQty, InvoicedQty);
    }

    /**
     * Set Commitment is Ceiling.
     *
     * @param IsCommitCeiling The commitment amount/quantity is the chargeable ceiling
     */
    public void setIsCommitCeiling(boolean IsCommitCeiling) {
        setValue(COLUMNNAME_IsCommitCeiling, Boolean.valueOf(IsCommitCeiling));
    }

    /**
     * Set Commitment.
     *
     * @param IsCommitment Is this document a (legal) commitment?
     */
    public void setIsCommitment(boolean IsCommitment) {
        setValue(COLUMNNAME_IsCommitment, Boolean.valueOf(IsCommitment));
    }

    /**
     * Set Summary Level.
     *
     * @param IsSummary This is a summary entity
     */
    public void setIsSummary(boolean IsSummary) {
        setValue(COLUMNNAME_IsSummary, Boolean.valueOf(IsSummary));
    }

    /**
     * Get Price List Version.
     *
     * @return Identifies a unique instance of a Price List
     */
    public int getPriceListVersionId() {
        Integer ii = getValue(COLUMNNAME_M_PriceList_Version_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Price List Version.
     *
     * @param M_PriceList_Version_ID Identifies a unique instance of a Price List
     */
    public void setPriceListVersionId(int M_PriceList_Version_ID) {
        if (M_PriceList_Version_ID < 1) setValue(COLUMNNAME_M_PriceList_Version_ID, null);
        else setValue(COLUMNNAME_M_PriceList_Version_ID, Integer.valueOf(M_PriceList_Version_ID));
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
     * Set Planned Amount.
     *
     * @param PlannedAmt Planned amount for this project
     */
    public void setPlannedAmt(BigDecimal PlannedAmt) {
        setValue(COLUMNNAME_PlannedAmt, PlannedAmt);
    }

    /**
     * Set Planned Margin.
     *
     * @param PlannedMarginAmt Project's planned margin amount
     */
    public void setPlannedMarginAmt(BigDecimal PlannedMarginAmt) {
        setValue(COLUMNNAME_PlannedMarginAmt, PlannedMarginAmt);
    }

    /**
     * Set Planned Quantity.
     *
     * @param PlannedQty Planned quantity for this project
     */
    public void setPlannedQty(BigDecimal PlannedQty) {
        setValue(COLUMNNAME_PlannedQty, PlannedQty);
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
     * Set Project Balance.
     *
     * @param ProjectBalanceAmt Total Project Balance
     */
    public void setProjectBalanceAmt(BigDecimal ProjectBalanceAmt) {
        setValueNoCheck(COLUMNNAME_ProjectBalanceAmt, ProjectBalanceAmt);
    }

    /**
     * Get Project Category.
     *
     * @return Project Category
     */
    public String getProjectCategory() {
        return getValue(COLUMNNAME_ProjectCategory);
    }

    /**
     * Set Project Category.
     *
     * @param ProjectCategory Project Category
     */
    public void setProjectCategory(String ProjectCategory) {

        setValue(COLUMNNAME_ProjectCategory, ProjectCategory);
    }

    /**
     * Set Line Level.
     *
     * @param ProjectLineLevel Project Line Level
     */
    public void setProjectLineLevel(String ProjectLineLevel) {

        setValue(COLUMNNAME_ProjectLineLevel, ProjectLineLevel);
    }

    /**
     * Set Invoice Rule.
     *
     * @param ProjInvoiceRule Invoice Rule for the project
     */
    public void setProjInvoiceRule(String ProjInvoiceRule) {

        setValue(COLUMNNAME_ProjInvoiceRule, ProjInvoiceRule);
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

    @Override
    public int getTableId() {
        return I_C_Project.Table_ID;
    }
}
