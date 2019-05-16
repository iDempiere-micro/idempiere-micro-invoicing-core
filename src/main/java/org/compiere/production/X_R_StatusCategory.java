package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_R_StatusCategory;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for R_StatusCategory
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_StatusCategory extends BasePOName implements I_R_StatusCategory {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_StatusCategory(int R_StatusCategory_ID) {
        super(R_StatusCategory_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_StatusCategory(Row row) {
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
        return "X_R_StatusCategory[" + getId() + "]";
    }

    /**
     * Set Default.
     *
     * @param IsDefault Default value
     */
    public void setIsDefault(boolean IsDefault) {
        setValue(COLUMNNAME_IsDefault, IsDefault);
    }

    /**
     * Get Status Category.
     *
     * @return Request Status Category
     */
    public int getStatusCategoryId() {
        Integer ii = getValue(COLUMNNAME_R_StatusCategory_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_R_StatusCategory.Table_ID;
    }
}
