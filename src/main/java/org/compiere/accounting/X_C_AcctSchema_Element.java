package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountSchemaElement;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for C_AcctSchema_Element
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_AcctSchema_Element extends BasePOName {

    /**
     * Organization = OO
     */
    public static final String ELEMENTTYPE_Organization = "OO";
    /**
     * Account = AC
     */
    public static final String ELEMENTTYPE_Account = "AC";
    /**
     * Product = PR
     */
    public static final String ELEMENTTYPE_Product = "PR";
    /**
     * BPartner = BP
     */
    public static final String ELEMENTTYPE_BPartner = "BP";
    /**
     * Org Trx = OT
     */
    public static final String ELEMENTTYPE_OrgTrx = "OT";
    /**
     * Location From = LF
     */
    public static final String ELEMENTTYPE_LocationFrom = "LF";
    /**
     * Location To = LT
     */
    public static final String ELEMENTTYPE_LocationTo = "LT";
    /**
     * Sales Region = SR
     */
    public static final String ELEMENTTYPE_SalesRegion = "SR";
    /**
     * Project = PJ
     */
    public static final String ELEMENTTYPE_Project = "PJ";
    /**
     * Campaign = MC
     */
    public static final String ELEMENTTYPE_Campaign = "MC";
    /**
     * User Element List 1 = U1
     */
    public static final String ELEMENTTYPE_UserElementList1 = "U1";
    /**
     * User Element List 2 = U2
     */
    public static final String ELEMENTTYPE_UserElementList2 = "U2";
    /**
     * Activity = AY
     */
    public static final String ELEMENTTYPE_Activity = "AY";
    /**
     * Sub Account = SA
     */
    public static final String ELEMENTTYPE_SubAccount = "SA";
    /**
     * User Column 1 = X1
     */
    public static final String ELEMENTTYPE_UserColumn1 = "X1";
    /**
     * User Column 2 = X2
     */
    public static final String ELEMENTTYPE_UserColumn2 = "X2";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_AcctSchema_Element(int C_AcctSchema_Element_ID) {
        super(C_AcctSchema_Element_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_AcctSchema_Element(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return AccountSchemaElement.accessLevel.intValue();
    }

    public String toString() {
        return "X_C_AcctSchema_Element[" + getId() + "]";
    }

    /**
     * Get Column.
     *
     * @return Column in the table
     */
    public int getTableColumnId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_AD_Column_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(AccountSchemaElement.COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(AccountSchemaElement.COLUMNNAME_C_AcctSchema_ID, C_AcctSchema_ID);
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Element.
     *
     * @return Accounting Element
     */
    public int getElementId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_Element_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Account Element.
     *
     * @return Account Element
     */
    public int getElementValueId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_ElementValue_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Address.
     *
     * @return Location or Address
     */
    public int getLocationId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_Location_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Sales Region.
     *
     * @return Sales coverage region
     */
    public int getSalesRegionId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_C_SalesRegion_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Type.
     *
     * @return Element Type (account or user defined)
     */
    public String getElementType() {
        return getValue(AccountSchemaElement.COLUMNNAME_ElementType);
    }

    /**
     * Set Balanced.
     *
     * @param IsBalanced Balanced
     */
    public void setIsBalanced(boolean IsBalanced) {
        setValue(AccountSchemaElement.COLUMNNAME_IsBalanced, IsBalanced);
    }

    /**
     * Get Balanced.
     *
     * @return Balanced
     */
    public boolean isBalanced() {
        Object oo = getValue(AccountSchemaElement.COLUMNNAME_IsBalanced);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Mandatory.
     *
     * @param IsMandatory Data entry is required in this column
     */
    public void setIsMandatory(boolean IsMandatory) {
        setValue(AccountSchemaElement.COLUMNNAME_IsMandatory, IsMandatory);
    }

    /**
     * Get Mandatory.
     *
     * @return Data entry is required in this column
     */
    public boolean isMandatory() {
        Object oo = getValue(AccountSchemaElement.COLUMNNAME_IsMandatory);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Organization.
     *
     * @return Organizational entity within client
     */
    public int getOrgId() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_Org_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Sequence.
     *
     * @return Method of ordering records; lowest number comes first
     */
    public int getSeqNo() {
        Integer ii = getValue(AccountSchemaElement.COLUMNNAME_SeqNo);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return AccountSchemaElement.Table_ID;
    }
}
