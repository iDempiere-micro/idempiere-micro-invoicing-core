package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_PA_Hierarchy;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for PA_Hierarchy
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_Hierarchy extends BasePOName implements I_PA_Hierarchy {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_Hierarchy(int PA_Hierarchy_ID) {
        super(PA_Hierarchy_ID);
    }

    /**
     * Load Constructor
     */
    public X_PA_Hierarchy(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer("X_PA_Hierarchy[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Account Tree.
     *
     * @return Tree for Natural Account Tree
     */
    public int getTreeAccountId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_Account_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Activity Tree.
     *
     * @return Trees are used for (financial) reporting
     */
    public int getTreeActivityId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get BPartner Tree.
     *
     * @return Trees are used for (financial) reporting
     */
    public int getTreeBPartnerId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Campaign Tree.
     *
     * @return Trees are used for (financial) reporting
     */
    public int getTreeCampaignId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Organization Tree.
     *
     * @return Trees are used for (financial) reporting and security access (via role)
     */
    public int getTreeOrgId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_Org_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Product Tree.
     *
     * @return Trees are used for (financial) reporting
     */
    public int getTreeProductId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project Tree.
     *
     * @return Trees are used for (financial) reporting
     */
    public int getTreeProjectId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Sales Region Tree.
     *
     * @return Trees are used for (financial) reporting
     */
    public int getTreeSalesRegionId() {
        Integer ii = getValue(COLUMNNAME_AD_Tree_SalesRegion_ID);
        if (ii == null) return 0;
        return ii;
    }

}
