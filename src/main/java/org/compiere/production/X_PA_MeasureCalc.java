package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_PA_MeasureCalc;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for PA_MeasureCalc
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_MeasureCalc extends BasePOName implements I_PA_MeasureCalc {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_MeasureCalc(int PA_MeasureCalc_ID) {
        super(PA_MeasureCalc_ID);
        /*
         * if (PA_MeasureCalc_ID == 0) { setColumnTableId (0); setDateColumn (null); // x.Date
         * setEntityType (null); // @SQL=select get_sysconfig('DEFAULT_ENTITYTYPE','U',0,0) from dual
         * setKeyColumn (null); setName (null); setOrgColumn (null); // x.orgId setPA_MeasureCalc_ID
         * (0); setSelectClause (null); // SELECT ... FROM ... setWhereClause (null); // WHERE ... }
         */
    }

    /**
     * Load Constructor
     */
    public X_PA_MeasureCalc(Row row) {
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
        StringBuffer sb = new StringBuffer("X_PA_MeasureCalc[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Table.
     *
     * @return Database Table information
     */
    public int getRowTableId() {
        Integer ii = getValue(COLUMNNAME_AD_Table_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get B.Partner Column.
     *
     * @return Fully qualified Business Partner key column (C_BPartner_ID)
     */
    public String getBPartnerColumn() {
        return getValue(COLUMNNAME_BPartnerColumn);
    }

    /**
     * Get Date Column.
     *
     * @return Fully qualified date column
     */
    public String getDateColumn() {
        return getValue(COLUMNNAME_DateColumn);
    }

    /**
     * Get Org Column.
     *
     * @return Fully qualified Organization column (orgId)
     */
    public String getOrgColumn() {
        return getValue(COLUMNNAME_OrgColumn);
    }

    /**
     * Get Product Column.
     *
     * @return Fully qualified Product column (M_Product_ID)
     */
    public String getProductColumn() {
        return getValue(COLUMNNAME_ProductColumn);
    }

    /**
     * Get Sql SELECT.
     *
     * @return SQL SELECT clause
     */
    public String getSelectClause() {
        return getValue(COLUMNNAME_SelectClause);
    }

    /**
     * Get Sql WHERE.
     *
     * @return Fully qualified SQL WHERE clause
     */
    public String getWhereClause() {
        return getValue(COLUMNNAME_WhereClause);
    }

    @Override
    public int getTableId() {
        return I_PA_MeasureCalc.Table_ID;
    }
}
