package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_C_City;
import org.compiere.orm.BasePOName;

public class X_C_City extends BasePOName implements I_C_City {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_City(int C_City_ID) {
        super(C_City_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_City(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_C_City[" + getId() + "]";
    }

    /**
     * Get City.
     *
     * @return City
     */
    public int getCityId() {
        Integer ii = getValue(COLUMNNAME_C_City_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Region.
     *
     * @param C_Region_ID Identifies a geographical Region
     */
    public void setRegionId(int C_Region_ID) {
        if (C_Region_ID < 1) setValue(COLUMNNAME_C_Region_ID, null);
        else setValue(COLUMNNAME_C_Region_ID, Integer.valueOf(C_Region_ID));
    }

}
