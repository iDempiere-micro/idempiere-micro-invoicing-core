package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_BOM;
import org.compiere.orm.BasePOName;

public class X_M_BOM extends BasePOName implements I_M_BOM {

    /**
     * Current Active = A
     */
    public static final String BOMTYPE_CurrentActive = "A";
    /**
     * Make-To-Order = O
     */
    public static final String BOMTYPE_Make_To_Order = "O";
    /**
     * Master = A
     */
    public static final String BOMUSE_Master = "A";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_BOM(int M_BOM_ID) {
        super(M_BOM_ID);
    }


    /**
     * Load Constructor
     */
    public X_M_BOM(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_M_BOM[" + getId() + "]";
    }

    /**
     * Get BOM Type.
     *
     * @return Type of BOM
     */
    public String getBOMType() {
        return getValue(COLUMNNAME_BOMType);
    }

    /**
     * Set BOM Type.
     *
     * @param BOMType Type of BOM
     */
    public void setBOMType(String BOMType) {

        setValue(COLUMNNAME_BOMType, BOMType);
    }

    /**
     * Get BOM Use.
     *
     * @return The use of the Bill of Material
     */
    public String getBOMUse() {
        return getValue(COLUMNNAME_BOMUse);
    }

    /**
     * Set BOM Use.
     *
     * @param BOMUse The use of the Bill of Material
     */
    public void setBOMUse(String BOMUse) {

        setValue(COLUMNNAME_BOMUse, BOMUse);
    }

    /**
     * Get BOM.
     *
     * @return Bill of Material
     */
    public int getBOMId() {
        Integer ii = getValue(COLUMNNAME_M_BOM_ID);
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

}
