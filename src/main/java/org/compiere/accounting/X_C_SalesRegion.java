package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_SalesRegion;
import org.compiere.orm.BasePONameValue;

/**
 * Generated Model for C_SalesRegion
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_SalesRegion extends BasePONameValue implements I_C_SalesRegion {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_SalesRegion(int C_SalesRegion_ID) {
        super(C_SalesRegion_ID);
        /**
         * if (C_SalesRegion_ID == 0) { setSalesRegionId (0); setIsDefault (false); setIsSummary
         * (false); setName (null); setValue (null); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_SalesRegion(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 3 - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_SalesRegion[").append(getId()).append("]");
        return sb.toString();
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

    @Override
    public int getTableId() {
        return I_C_SalesRegion.Table_ID;
    }
}
