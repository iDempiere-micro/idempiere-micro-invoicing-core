package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_GL_DistributionLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for GL_DistributionLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_GL_DistributionLine extends PO implements I_GL_DistributionLine {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_GL_DistributionLine(int GL_DistributionLine_ID) {
        super(GL_DistributionLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_GL_DistributionLine(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_GL_DistributionLine[" + getId() + "]";
    }

    /**
     * Get Account.
     *
     * @return Account used
     */
    public int getAccountId() {
        Integer ii = getValue(COLUMNNAME_Account_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Account.
     *
     * @param Account_ID Account used
     */
    public void setAccountId(int Account_ID) {
        if (Account_ID < 1) setValue(COLUMNNAME_Account_ID, null);
        else setValue(COLUMNNAME_Account_ID, Account_ID);
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
        else setValue(COLUMNNAME_AD_OrgTrx_ID, AD_OrgTrx_ID);
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

    /**
     * Get Location From.
     *
     * @return Location that inventory was moved from
     */
    public int getLocationFromId() {
        Integer ii = getValue(COLUMNNAME_C_LocFrom_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Location From.
     *
     * @param C_LocFrom_ID Location that inventory was moved from
     */
    public void setLocationFromId(int C_LocFrom_ID) {
        if (C_LocFrom_ID < 1) setValue(COLUMNNAME_C_LocFrom_ID, null);
        else setValue(COLUMNNAME_C_LocFrom_ID, C_LocFrom_ID);
    }

    /**
     * Get Location To.
     *
     * @return Location that inventory was moved to
     */
    public int getLocationToId() {
        Integer ii = getValue(COLUMNNAME_C_LocTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Location To.
     *
     * @param C_LocTo_ID Location that inventory was moved to
     */
    public void setLocationToId(int C_LocTo_ID) {
        if (C_LocTo_ID < 1) setValue(COLUMNNAME_C_LocTo_ID, null);
        else setValue(COLUMNNAME_C_LocTo_ID, C_LocTo_ID);
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
        else setValue(COLUMNNAME_C_Project_ID, C_Project_ID);
    }

    /**
     * Get Sales Region.
     *
     * @return Sales coverage region
     */
    public int getSalesRegionId() {
        Integer ii = getValue(COLUMNNAME_C_SalesRegion_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Region.
     *
     * @param C_SalesRegion_ID Sales coverage region
     */
    public void setSalesRegionId(int C_SalesRegion_ID) {
        if (C_SalesRegion_ID < 1) setValue(COLUMNNAME_C_SalesRegion_ID, null);
        else setValue(COLUMNNAME_C_SalesRegion_ID, C_SalesRegion_ID);
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
     * Get GL Distribution.
     *
     * @return General Ledger Distribution
     */
    public int getGLDistributionId() {
        Integer ii = getValue(COLUMNNAME_GL_Distribution_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Organization.
     *
     * @return Organizational entity within client
     */
    public int getOrgId() {
        Integer ii = getValue(COLUMNNAME_Org_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Organization.
     *
     * @param Org_ID Organizational entity within client
     */
    public void setOrgId(int Org_ID) {
        if (Org_ID < 1) setValue(COLUMNNAME_Org_ID, null);
        else setValue(COLUMNNAME_Org_ID, Org_ID);
    }

    /**
     * Get Overwrite Account.
     *
     * @return Overwrite the account segment Account with the value specified
     */
    public boolean isOverwriteAcct() {
        Object oo = getValue(COLUMNNAME_OverwriteAcct);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Account.
     *
     * @param OverwriteAcct Overwrite the account segment Account with the value specified
     */
    public void setOverwriteAcct(boolean OverwriteAcct) {
        setValue(COLUMNNAME_OverwriteAcct, OverwriteAcct);
    }

    /**
     * Get Overwrite Activity.
     *
     * @return Overwrite the account segment Activity with the value specified
     */
    public boolean isOverwriteActivity() {
        Object oo = getValue(COLUMNNAME_OverwriteActivity);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Activity.
     *
     * @param OverwriteActivity Overwrite the account segment Activity with the value specified
     */
    public void setOverwriteActivity(boolean OverwriteActivity) {
        setValue(COLUMNNAME_OverwriteActivity, OverwriteActivity);
    }

    /**
     * Get Overwrite Bus.Partner.
     *
     * @return Overwrite the account segment Business Partner with the value specified
     */
    public boolean isOverwriteBPartner() {
        Object oo = getValue(COLUMNNAME_OverwriteBPartner);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Bus.Partner.
     *
     * @param OverwriteBPartner Overwrite the account segment Business Partner with the value
     *                          specified
     */
    public void setOverwriteBPartner(boolean OverwriteBPartner) {
        setValue(COLUMNNAME_OverwriteBPartner, OverwriteBPartner);
    }

    /**
     * Get Overwrite Campaign.
     *
     * @return Overwrite the account segment Campaign with the value specified
     */
    public boolean isOverwriteCampaign() {
        Object oo = getValue(COLUMNNAME_OverwriteCampaign);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Campaign.
     *
     * @param OverwriteCampaign Overwrite the account segment Campaign with the value specified
     */
    public void setOverwriteCampaign(boolean OverwriteCampaign) {
        setValue(COLUMNNAME_OverwriteCampaign, OverwriteCampaign);
    }

    /**
     * Get Overwrite Location From.
     *
     * @return Overwrite the account segment Location From with the value specified
     */
    public boolean isOverwriteLocFrom() {
        Object oo = getValue(COLUMNNAME_OverwriteLocFrom);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Location From.
     *
     * @param OverwriteLocFrom Overwrite the account segment Location From with the value specified
     */
    public void setOverwriteLocFrom(boolean OverwriteLocFrom) {
        setValue(COLUMNNAME_OverwriteLocFrom, OverwriteLocFrom);
    }

    /**
     * Get Overwrite Location To.
     *
     * @return Overwrite the account segment Location From with the value specified
     */
    public boolean isOverwriteLocTo() {
        Object oo = getValue(COLUMNNAME_OverwriteLocTo);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Location To.
     *
     * @param OverwriteLocTo Overwrite the account segment Location From with the value specified
     */
    public void setOverwriteLocTo(boolean OverwriteLocTo) {
        setValue(COLUMNNAME_OverwriteLocTo, OverwriteLocTo);
    }

    /**
     * Get Overwrite Organization.
     *
     * @return Overwrite the account segment Organization with the value specified
     */
    public boolean isOverwriteOrg() {
        Object oo = getValue(COLUMNNAME_OverwriteOrg);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Organization.
     *
     * @param OverwriteOrg Overwrite the account segment Organization with the value specified
     */
    public void setOverwriteOrg(boolean OverwriteOrg) {
        setValue(COLUMNNAME_OverwriteOrg, OverwriteOrg);
    }

    /**
     * Get Overwrite Trx Organuzation.
     *
     * @return Overwrite the account segment Transaction Organization with the value specified
     */
    public boolean isOverwriteOrgTrx() {
        Object oo = getValue(COLUMNNAME_OverwriteOrgTrx);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Trx Organuzation.
     *
     * @param OverwriteOrgTrx Overwrite the account segment Transaction Organization with the value
     *                        specified
     */
    public void setOverwriteOrgTrx(boolean OverwriteOrgTrx) {
        setValue(COLUMNNAME_OverwriteOrgTrx, OverwriteOrgTrx);
    }

    /**
     * Get Overwrite Product.
     *
     * @return Overwrite the account segment Product with the value specified
     */
    public boolean isOverwriteProduct() {
        Object oo = getValue(COLUMNNAME_OverwriteProduct);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Product.
     *
     * @param OverwriteProduct Overwrite the account segment Product with the value specified
     */
    public void setOverwriteProduct(boolean OverwriteProduct) {
        setValue(COLUMNNAME_OverwriteProduct, Boolean.valueOf(OverwriteProduct));
    }

    /**
     * Get Overwrite Project.
     *
     * @return Overwrite the account segment Project with the value specified
     */
    public boolean isOverwriteProject() {
        Object oo = getValue(COLUMNNAME_OverwriteProject);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Project.
     *
     * @param OverwriteProject Overwrite the account segment Project with the value specified
     */
    public void setOverwriteProject(boolean OverwriteProject) {
        setValue(COLUMNNAME_OverwriteProject, Boolean.valueOf(OverwriteProject));
    }

    /**
     * Get Overwrite Sales Region.
     *
     * @return Overwrite the account segment Sales Region with the value specified
     */
    public boolean isOverwriteSalesRegion() {
        Object oo = getValue(COLUMNNAME_OverwriteSalesRegion);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite Sales Region.
     *
     * @param OverwriteSalesRegion Overwrite the account segment Sales Region with the value specified
     */
    public void setOverwriteSalesRegion(boolean OverwriteSalesRegion) {
        setValue(COLUMNNAME_OverwriteSalesRegion, Boolean.valueOf(OverwriteSalesRegion));
    }

    /**
     * Get Overwrite User1.
     *
     * @return Overwrite the account segment User 1 with the value specified
     */
    public boolean isOverwriteUser1() {
        Object oo = getValue(COLUMNNAME_OverwriteUser1);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite User1.
     *
     * @param OverwriteUser1 Overwrite the account segment User 1 with the value specified
     */
    public void setOverwriteUser1(boolean OverwriteUser1) {
        setValue(COLUMNNAME_OverwriteUser1, Boolean.valueOf(OverwriteUser1));
    }

    /**
     * Get Overwrite User2.
     *
     * @return Overwrite the account segment User 2 with the value specified
     */
    public boolean isOverwriteUser2() {
        Object oo = getValue(COLUMNNAME_OverwriteUser2);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Overwrite User2.
     *
     * @param OverwriteUser2 Overwrite the account segment User 2 with the value specified
     */
    public void setOverwriteUser2(boolean OverwriteUser2) {
        setValue(COLUMNNAME_OverwriteUser2, Boolean.valueOf(OverwriteUser2));
    }

    /**
     * Get Percent.
     *
     * @return Percentage
     */
    public BigDecimal getPercent() {
        BigDecimal bd = getValue(COLUMNNAME_Percent);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Percent.
     *
     * @param Percent Percentage
     */
    public void setPercent(BigDecimal Percent) {
        setValue(COLUMNNAME_Percent, Percent);
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

    @Override
    public int getTableId() {
        return I_GL_DistributionLine.Table_ID;
    }
}
