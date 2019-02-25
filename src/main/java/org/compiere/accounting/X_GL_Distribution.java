package org.compiere.accounting;

import org.compiere.model.I_GL_Distribution;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for GL_Distribution
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_GL_Distribution extends BasePOName implements I_GL_Distribution {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_GL_Distribution(Properties ctx, int GL_Distribution_ID) {
        super(ctx, GL_Distribution_ID);
    }

    /**
     * Load Constructor
     */
    public X_GL_Distribution(Properties ctx, ResultSet rs) {
        super(ctx, rs);
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
        StringBuffer sb = new StringBuffer("X_GL_Distribution[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Account.
     *
     * @return Account used
     */
    public int getAccount_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_Account_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Account.
     *
     * @param Account_ID Account used
     */
    public void setAccount_ID(int Account_ID) {
        if (Account_ID < 1) set_Value(COLUMNNAME_Account_ID, null);
        else set_Value(COLUMNNAME_Account_ID, Integer.valueOf(Account_ID));
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getAD_OrgTrx_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setAD_OrgTrx_ID(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) set_Value(COLUMNNAME_AD_OrgTrx_ID, null);
        else set_Value(COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
    }

    /**
     * Get Any Account.
     *
     * @return Match any value of the Account segment
     */
    public boolean isAnyAcct() {
        Object oo = getValue(COLUMNNAME_AnyAcct);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Account.
     *
     * @param AnyAcct Match any value of the Account segment
     */
    public void setAnyAcct(boolean AnyAcct) {
        set_Value(COLUMNNAME_AnyAcct, Boolean.valueOf(AnyAcct));
    }

    /**
     * Get Any Activity.
     *
     * @return Match any value of the Activity segment
     */
    public boolean isAnyActivity() {
        Object oo = getValue(COLUMNNAME_AnyActivity);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Activity.
     *
     * @param AnyActivity Match any value of the Activity segment
     */
    public void setAnyActivity(boolean AnyActivity) {
        set_Value(COLUMNNAME_AnyActivity, Boolean.valueOf(AnyActivity));
    }

    /**
     * Get Any Bus.Partner.
     *
     * @return Match any value of the Business Partner segment
     */
    public boolean isAnyBPartner() {
        Object oo = getValue(COLUMNNAME_AnyBPartner);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Bus.Partner.
     *
     * @param AnyBPartner Match any value of the Business Partner segment
     */
    public void setAnyBPartner(boolean AnyBPartner) {
        set_Value(COLUMNNAME_AnyBPartner, Boolean.valueOf(AnyBPartner));
    }

    /**
     * Get Any Campaign.
     *
     * @return Match any value of the Campaign segment
     */
    public boolean isAnyCampaign() {
        Object oo = getValue(COLUMNNAME_AnyCampaign);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Campaign.
     *
     * @param AnyCampaign Match any value of the Campaign segment
     */
    public void setAnyCampaign(boolean AnyCampaign) {
        set_Value(COLUMNNAME_AnyCampaign, Boolean.valueOf(AnyCampaign));
    }

    /**
     * Get Any Location From.
     *
     * @return Match any value of the Location From segment
     */
    public boolean isAnyLocFrom() {
        Object oo = getValue(COLUMNNAME_AnyLocFrom);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Location From.
     *
     * @param AnyLocFrom Match any value of the Location From segment
     */
    public void setAnyLocFrom(boolean AnyLocFrom) {
        set_Value(COLUMNNAME_AnyLocFrom, Boolean.valueOf(AnyLocFrom));
    }

    /**
     * Get Any Location To.
     *
     * @return Match any value of the Location To segment
     */
    public boolean isAnyLocTo() {
        Object oo = getValue(COLUMNNAME_AnyLocTo);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Location To.
     *
     * @param AnyLocTo Match any value of the Location To segment
     */
    public void setAnyLocTo(boolean AnyLocTo) {
        set_Value(COLUMNNAME_AnyLocTo, Boolean.valueOf(AnyLocTo));
    }

    /**
     * Get Any Organization.
     *
     * @return Match any value of the Organization segment
     */
    public boolean isAnyOrg() {
        Object oo = getValue(COLUMNNAME_AnyOrg);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Organization.
     *
     * @param AnyOrg Match any value of the Organization segment
     */
    public void setAnyOrg(boolean AnyOrg) {
        set_Value(COLUMNNAME_AnyOrg, Boolean.valueOf(AnyOrg));
    }

    /**
     * Get Any Trx Organization.
     *
     * @return Match any value of the Transaction Organization segment
     */
    public boolean isAnyOrgTrx() {
        Object oo = getValue(COLUMNNAME_AnyOrgTrx);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Trx Organization.
     *
     * @param AnyOrgTrx Match any value of the Transaction Organization segment
     */
    public void setAnyOrgTrx(boolean AnyOrgTrx) {
        set_Value(COLUMNNAME_AnyOrgTrx, Boolean.valueOf(AnyOrgTrx));
    }

    /**
     * Get Any Product.
     *
     * @return Match any value of the Product segment
     */
    public boolean isAnyProduct() {
        Object oo = getValue(COLUMNNAME_AnyProduct);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Product.
     *
     * @param AnyProduct Match any value of the Product segment
     */
    public void setAnyProduct(boolean AnyProduct) {
        set_Value(COLUMNNAME_AnyProduct, Boolean.valueOf(AnyProduct));
    }

    /**
     * Get Any Project.
     *
     * @return Match any value of the Project segment
     */
    public boolean isAnyProject() {
        Object oo = getValue(COLUMNNAME_AnyProject);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Project.
     *
     * @param AnyProject Match any value of the Project segment
     */
    public void setAnyProject(boolean AnyProject) {
        set_Value(COLUMNNAME_AnyProject, Boolean.valueOf(AnyProject));
    }

    /**
     * Get Any Sales Region.
     *
     * @return Match any value of the Sales Region segment
     */
    public boolean isAnySalesRegion() {
        Object oo = getValue(COLUMNNAME_AnySalesRegion);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any Sales Region.
     *
     * @param AnySalesRegion Match any value of the Sales Region segment
     */
    public void setAnySalesRegion(boolean AnySalesRegion) {
        set_Value(COLUMNNAME_AnySalesRegion, Boolean.valueOf(AnySalesRegion));
    }

    /**
     * Get Any User 1.
     *
     * @return Match any value of the User 1 segment
     */
    public boolean isAnyUser1() {
        Object oo = getValue(COLUMNNAME_AnyUser1);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any User 1.
     *
     * @param AnyUser1 Match any value of the User 1 segment
     */
    public void setAnyUser1(boolean AnyUser1) {
        set_Value(COLUMNNAME_AnyUser1, Boolean.valueOf(AnyUser1));
    }

    /**
     * Get Any User 2.
     *
     * @return Match any value of the User 2 segment
     */
    public boolean isAnyUser2() {
        Object oo = getValue(COLUMNNAME_AnyUser2);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Any User 2.
     *
     * @param AnyUser2 Match any value of the User 2 segment
     */
    public void setAnyUser2(boolean AnyUser2) {
        set_Value(COLUMNNAME_AnyUser2, Boolean.valueOf(AnyUser2));
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getC_AcctSchema_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getC_Activity_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setC_Activity_ID(int C_Activity_ID) {
        if (C_Activity_ID < 1) set_Value(COLUMNNAME_C_Activity_ID, null);
        else set_Value(COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getC_BPartner_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setC_BPartner_ID(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) set_Value(COLUMNNAME_C_BPartner_ID, null);
        else set_Value(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getC_Campaign_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setC_Campaign_ID(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) set_Value(COLUMNNAME_C_Campaign_ID, null);
        else set_Value(COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
    }

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getC_DocType_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Location From.
     *
     * @return Location that inventory was moved from
     */
    public int getC_LocFrom_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_LocFrom_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Location From.
     *
     * @param C_LocFrom_ID Location that inventory was moved from
     */
    public void setC_LocFrom_ID(int C_LocFrom_ID) {
        if (C_LocFrom_ID < 1) set_Value(COLUMNNAME_C_LocFrom_ID, null);
        else set_Value(COLUMNNAME_C_LocFrom_ID, Integer.valueOf(C_LocFrom_ID));
    }

    /**
     * Get Location To.
     *
     * @return Location that inventory was moved to
     */
    public int getC_LocTo_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_LocTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Location To.
     *
     * @param C_LocTo_ID Location that inventory was moved to
     */
    public void setC_LocTo_ID(int C_LocTo_ID) {
        if (C_LocTo_ID < 1) set_Value(COLUMNNAME_C_LocTo_ID, null);
        else set_Value(COLUMNNAME_C_LocTo_ID, Integer.valueOf(C_LocTo_ID));
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getC_Project_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setC_Project_ID(int C_Project_ID) {
        if (C_Project_ID < 1) set_Value(COLUMNNAME_C_Project_ID, null);
        else set_Value(COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
    }

    /**
     * Get Sales Region.
     *
     * @return Sales coverage region
     */
    public int getC_SalesRegion_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_SalesRegion_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Region.
     *
     * @param C_SalesRegion_ID Sales coverage region
     */
    public void setC_SalesRegion_ID(int C_SalesRegion_ID) {
        if (C_SalesRegion_ID < 1) set_Value(COLUMNNAME_C_SalesRegion_ID, null);
        else set_Value(COLUMNNAME_C_SalesRegion_ID, Integer.valueOf(C_SalesRegion_ID));
    }

    /**
     * Get GL Distribution.
     *
     * @return General Ledger Distribution
     */
    public int getGL_Distribution_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_GL_Distribution_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Create Reversal.
     *
     * @return Indicates that reversal movement will be created, if disabled the original movement
     * will be deleted.
     */
    public boolean isCreateReversal() {
        Object oo = getValue(COLUMNNAME_IsCreateReversal);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Valid.
     *
     * @param IsValid Element is valid
     */
    public void setIsValid(boolean IsValid) {
        set_Value(COLUMNNAME_IsValid, Boolean.valueOf(IsValid));
    }

    /**
     * Get Valid.
     *
     * @return Element is valid
     */
    public boolean isValid() {
        Object oo = getValue(COLUMNNAME_IsValid);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getM_Product_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setM_Product_ID(int M_Product_ID) {
        if (M_Product_ID < 1) set_Value(COLUMNNAME_M_Product_ID, null);
        else set_Value(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Get Organization.
     *
     * @return Organizational entity within client
     */
    public int getOrg_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_Org_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Organization.
     *
     * @param Org_ID Organizational entity within client
     */
    public void setOrg_ID(int Org_ID) {
        if (Org_ID < 1) set_Value(COLUMNNAME_Org_ID, null);
        else set_Value(COLUMNNAME_Org_ID, Integer.valueOf(Org_ID));
    }

    /**
     * Get Total Percent.
     *
     * @return Sum of the Percent details
     */
    public BigDecimal getPercentTotal() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_PercentTotal);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Total Percent.
     *
     * @param PercentTotal Sum of the Percent details
     */
    public void setPercentTotal(BigDecimal PercentTotal) {
        set_Value(COLUMNNAME_PercentTotal, PercentTotal);
    }

    /**
     * Get PostingType.
     *
     * @return The type of posted amount for the transaction
     */
    public String getPostingType() {
        return (String) getValue(COLUMNNAME_PostingType);
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1_ID(int User1_ID) {
        if (User1_ID < 1) set_Value(COLUMNNAME_User1_ID, null);
        else set_Value(COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2_ID(int User2_ID) {
        if (User2_ID < 1) set_Value(COLUMNNAME_User2_ID, null);
        else set_Value(COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
    }

    @Override
    public int getTableId() {
        return I_GL_Distribution.Table_ID;
    }
}
