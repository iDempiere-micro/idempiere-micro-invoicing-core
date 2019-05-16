package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_UOM_Conversion;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_UOM_Conversion
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_UOM_Conversion extends PO implements I_C_UOM_Conversion {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_UOM_Conversion(int C_UOM_Conversion_ID) {
        super(C_UOM_Conversion_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_UOM_Conversion(Row row) {
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
        return "X_C_UOM_Conversion[" + getId() + "]";
    }

    /**
     * Get UOM.
     *
     * @return Unit of Measure
     */
    public int getUOMId() {
        Integer ii = getValue(COLUMNNAME_C_UOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set UOM.
     *
     * @param C_UOM_ID Unit of Measure
     */
    public void setUOMId(int C_UOM_ID) {
        if (C_UOM_ID < 1) setValue(COLUMNNAME_C_UOM_ID, null);
        else setValue(COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
    }

    /**
     * Get UoM To.
     *
     * @return Target or destination Unit of Measure
     */
    public int getTargetUOMId() {
        Integer ii = getValue(COLUMNNAME_C_UOM_To_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set UoM To.
     *
     * @param C_UOM_To_ID Target or destination Unit of Measure
     */
    public void setTargetUOMId(int C_UOM_To_ID) {
        if (C_UOM_To_ID < 1) setValue(COLUMNNAME_C_UOM_To_ID, null);
        else setValue(COLUMNNAME_C_UOM_To_ID, Integer.valueOf(C_UOM_To_ID));
    }

    /**
     * Get Divide Rate.
     *
     * @return To convert Source number to Target number, the Source is divided
     */
    public BigDecimal getDivideRate() {
        BigDecimal bd = getValue(COLUMNNAME_DivideRate);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Divide Rate.
     *
     * @param DivideRate To convert Source number to Target number, the Source is divided
     */
    public void setDivideRate(BigDecimal DivideRate) {
        setValue(COLUMNNAME_DivideRate, DivideRate);
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
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Get Multiply Rate.
     *
     * @return Rate to multiple the source by to calculate the target.
     */
    public BigDecimal getMultiplyRate() {
        BigDecimal bd = getValue(COLUMNNAME_MultiplyRate);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Multiply Rate.
     *
     * @param MultiplyRate Rate to multiple the source by to calculate the target.
     */
    public void setMultiplyRate(BigDecimal MultiplyRate) {
        setValue(COLUMNNAME_MultiplyRate, MultiplyRate);
    }
}
