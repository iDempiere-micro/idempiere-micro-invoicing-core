package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_R_Resolution;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for R_Resolution
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_Resolution extends BasePOName implements I_R_Resolution {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_Resolution(int R_Resolution_ID) {
        super(R_Resolution_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_Resolution(Row row) {
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
        return "X_R_Resolution[" + getId() + "]";
    }

    @Override
    public int getTableId() {
        return I_R_Resolution.Table_ID;
    }
}
