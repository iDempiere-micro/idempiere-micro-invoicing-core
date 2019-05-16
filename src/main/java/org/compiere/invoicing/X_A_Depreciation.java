package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.Depreciation;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for A_Depreciation
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_A_Depreciation extends BasePOName implements Depreciation {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_A_Depreciation(int A_Depreciation_ID) {
        super(A_Depreciation_ID);
    }

    /**
     * Load Constructor
     */
    public X_A_Depreciation(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 7 - System - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_A_Depreciation[" + getId() + "]";
    }

    /**
     * Get DepreciationType.
     *
     * @return DepreciationType
     */
    public String getDepreciationType() {
        return getValue(COLUMNNAME_DepreciationType);
    }

    @Override
    public int getTableId() {
        return Depreciation.Table_ID;
    }
}
