package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_PA_GoalRestriction;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for PA_GoalRestriction
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_GoalRestriction extends BasePOName implements I_PA_GoalRestriction {

    /**
     * Organization = O
     */
    public static final String GOALRESTRICTIONTYPE_Organization = "O";
    /**
     * Business Partner = B
     */
    public static final String GOALRESTRICTIONTYPE_BusinessPartner = "B";
    /**
     * Product = P
     */
    public static final String GOALRESTRICTIONTYPE_Product = "P";
    /**
     * Bus.Partner Group = G
     */
    public static final String GOALRESTRICTIONTYPE_BusPartnerGroup = "G";
    /**
     * Product Category = C
     */
    public static final String GOALRESTRICTIONTYPE_ProductCategory = "C";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_GoalRestriction(int PA_GoalRestriction_ID) {
        super(PA_GoalRestriction_ID);
    }

    /**
     * Load Constructor
     */
    public X_PA_GoalRestriction(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 6 - System - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_PA_GoalRestriction[").append(getId()).append("]");
        return sb.toString();
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
     * Get Business Partner Group.
     *
     * @return Business Partner Group
     */
    public int getBPGroupId() {
        Integer ii = getValue(COLUMNNAME_C_BP_Group_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Restriction Type.
     *
     * @return Goal Restriction Type
     */
    public String getGoalRestrictionType() {
        return getValue(COLUMNNAME_GoalRestrictionType);
    }

    /**
     * Get Product Category.
     *
     * @return Category of a Product
     */
    public int getProductCategoryId() {
        Integer ii = getValue(COLUMNNAME_M_Product_Category_ID);
        if (ii == null) return 0;
        return ii;
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
     * Get Organization.
     *
     * @return Organizational entity within client
     */
    public int getOrgId() {
        Integer ii = getValue(COLUMNNAME_Org_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_PA_GoalRestriction.Table_ID;
    }
}
