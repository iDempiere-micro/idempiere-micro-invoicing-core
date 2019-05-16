package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingElementValue;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.orm.PO;
import software.hsharp.core.orm.MBaseTableKt;

/**
 * Generated Model for C_ValidCombination
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_ValidCombination extends PO {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_ValidCombination(int C_ValidCombination_ID) {
        super(C_ValidCombination_ID);
        /**
         * if (C_ValidCombination_ID == 0) { setAccountId (0); setAccountingSchemaId (0);
         * setValidCombination_ID (0); setIsFullyQualified (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_ValidCombination(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return I_C_ValidCombination.accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_ValidCombination[").append(getId()).append("]");
        return sb.toString();
    }

    public AccountingElementValue getAccount() throws RuntimeException {
        return (AccountingElementValue)
                MBaseTableKt.getTable(AccountingElementValue.Table_Name)
                        .getPO(getAccountId());
    }

    /**
     * Get Account.
     *
     * @return Account used
     */
    public int getAccountId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_Account_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Account.
     *
     * @param Account_ID Account used
     */
    public void setAccountId(int Account_ID) {
        if (Account_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_Account_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_Account_ID, Account_ID);
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getTransactionOrganizationId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setTransactionOrganizationId(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_AD_OrgTrx_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
    }

    /**
     * Get Alias.
     *
     * @return Defines an alternate method of indicating an account combination.
     */
    public String getAlias() {
        return getValue(I_C_ValidCombination.COLUMNNAME_Alias);
    }

    /**
     * Set Alias.
     *
     * @param Alias Defines an alternate method of indicating an account combination.
     */
    public void setAlias(String Alias) {
        setValue(I_C_ValidCombination.COLUMNNAME_Alias, Alias);
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_AcctSchema_ID, C_AcctSchema_ID);
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_Activity_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_Activity_ID, C_Activity_ID);
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_BPartner_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_BPartner_ID, C_BPartner_ID);
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_Campaign_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_Campaign_ID, C_Campaign_ID);
    }

    /**
     * Get Location From.
     *
     * @return Location that inventory was moved from
     */
    public int getLocationFromId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_LocFrom_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Location From.
     *
     * @param C_LocFrom_ID Location that inventory was moved from
     */
    public void setLocationFromId(int C_LocFrom_ID) {
        if (C_LocFrom_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_LocFrom_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_LocFrom_ID, C_LocFrom_ID);
    }

    /**
     * Get Location To.
     *
     * @return Location that inventory was moved to
     */
    public int getLocationToId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_LocTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Location To.
     *
     * @param C_LocTo_ID Location that inventory was moved to
     */
    public void setLocationToId(int C_LocTo_ID) {
        if (C_LocTo_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_LocTo_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_LocTo_ID, C_LocTo_ID);
    }

    /**
     * Get Combination.
     *
     * @return Unique combination of account elements
     */
    public String getCombination() {
        return getValue(I_C_ValidCombination.COLUMNNAME_Combination);
    }

    /**
     * Set Combination.
     *
     * @param Combination Unique combination of account elements
     */
    public void setCombination(String Combination) {
        setValueNoCheck(I_C_ValidCombination.COLUMNNAME_Combination, Combination);
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_Project_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_Project_ID, C_Project_ID);
    }

    /**
     * Get Sales Region.
     *
     * @return Sales coverage region
     */
    public int getSalesRegionId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_SalesRegion_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Region.
     *
     * @param C_SalesRegion_ID Sales coverage region
     */
    public void setSalesRegionId(int C_SalesRegion_ID) {
        if (C_SalesRegion_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_SalesRegion_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_SalesRegion_ID, C_SalesRegion_ID);
    }

    /**
     * Get Sub Account.
     *
     * @return Sub account for Element Value
     */
    public int getSubAccountId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_SubAcct_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sub Account.
     *
     * @param C_SubAcct_ID Sub account for Element Value
     */
    public void setSubAccountId(int C_SubAcct_ID) {
        if (C_SubAcct_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_SubAcct_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_C_SubAcct_ID, C_SubAcct_ID);
    }

    /**
     * Get Combination.
     *
     * @return Valid Account Combination
     */
    public int getValidAccountCombinationId() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_C_ValidCombination_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(I_C_ValidCombination.COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValueNoCheck(I_C_ValidCombination.COLUMNNAME_Description, Description);
    }

    /**
     * Set Fully Qualified.
     *
     * @param IsFullyQualified This account is fully qualified
     */
    public void setIsFullyQualified(boolean IsFullyQualified) {
        setValueNoCheck(I_C_ValidCombination.COLUMNNAME_IsFullyQualified, IsFullyQualified);
    }

    /**
     * Get Fully Qualified.
     *
     * @return This account is fully qualified
     */
    public boolean isFullyQualified() {
        Object oo = getValue(I_C_ValidCombination.COLUMNNAME_IsFullyQualified);
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
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_M_Product_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1Id() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_User1_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_User1_ID, User1_ID);
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValueNoCheck(I_C_ValidCombination.COLUMNNAME_User2_ID, null);
        else setValueNoCheck(I_C_ValidCombination.COLUMNNAME_User2_ID, User2_ID);
    }

    /**
     * Get User Column 1.
     *
     * @return User defined accounting Element
     */
    public int getUserElement1Id() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_UserElement1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Column 1.
     *
     * @param UserElement1_ID User defined accounting Element
     */
    public void setUserElement1Id(int UserElement1_ID) {
        if (UserElement1_ID < 1) setValue(I_C_ValidCombination.COLUMNNAME_UserElement1_ID, null);
        else setValue(I_C_ValidCombination.COLUMNNAME_UserElement1_ID, UserElement1_ID);
    }

    /**
     * Get User Column 2.
     *
     * @return User defined accounting Element
     */
    public int getUserElement2Id() {
        Integer ii = getValue(I_C_ValidCombination.COLUMNNAME_UserElement2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Column 2.
     *
     * @param UserElement2_ID User defined accounting Element
     */
    public void setUserElement2Id(int UserElement2_ID) {
        if (UserElement2_ID < 1) setValue(I_C_ValidCombination.COLUMNNAME_UserElement2_ID, null);
        else setValue(I_C_ValidCombination.COLUMNNAME_UserElement2_ID, UserElement2_ID);
    }

    @Override
    public int getTableId() {
        return I_C_ValidCombination.Table_ID;
    }
}
