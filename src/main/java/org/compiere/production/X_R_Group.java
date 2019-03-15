package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_R_Group;
import org.compiere.orm.BasePOName;

import java.util.Properties;

/**
 * Generated Model for R_Group
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_Group extends BasePOName implements I_R_Group {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_Group(Properties ctx, int R_Group_ID) {
        super(ctx, R_Group_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_Group(Properties ctx, Row row) {
        super(ctx, row);
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
        return "X_R_Group[" + getId() + "]";
    }

    /**
     * Get Change Notice.
     *
     * @return Bill of Materials (Engineering) Change Notice (Version)
     */
    public int getM_ChangeNotice_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_M_ChangeNotice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get BOM & Formula.
     *
     * @return BOM & Formula
     */
    public int getPP_Product_BOM_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_PP_Product_BOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_R_Group.Table_ID;
    }
}
