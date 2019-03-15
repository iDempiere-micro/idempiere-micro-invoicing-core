package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_MovementLineMA;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Properties;

public class X_M_MovementLineMA extends PO implements I_M_MovementLineMA {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_MovementLineMA(Properties ctx, int M_MovementLineMA_ID) {
        super(ctx, M_MovementLineMA_ID);
        /*
         * if (M_MovementLineMA_ID == 0) { setM_AttributeSetInstance_ID (0); setM_MovementLine_ID (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_M_MovementLineMA(Properties ctx, Row row) {
        super(ctx, row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_M_MovementLineMA[" + getId() + "]";
    }

    /**
     * Get Date Material Policy.
     *
     * @return Time used for LIFO and FIFO Material Policy
     */
    public Timestamp getDateMaterialPolicy() {
        return (Timestamp) getValue(COLUMNNAME_DateMaterialPolicy);
    }

    /**
     * Set Date Material Policy.
     *
     * @param DateMaterialPolicy Time used for LIFO and FIFO Material Policy
     */
    public void setDateMaterialPolicy(Timestamp DateMaterialPolicy) {
        setValueNoCheck(COLUMNNAME_DateMaterialPolicy, DateMaterialPolicy);
    }

    /**
     * Set Auto Generated.
     *
     * @param IsAutoGenerated Auto Generated
     */
    public void setIsAutoGenerated(boolean IsAutoGenerated) {
        setValueNoCheck(COLUMNNAME_IsAutoGenerated, Boolean.valueOf(IsAutoGenerated));
    }

    /**
     * Get Auto Generated.
     *
     * @return Auto Generated
     */
    public boolean isAutoGenerated() {
        Object oo = getValue(COLUMNNAME_IsAutoGenerated);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Attribute Set Instance.
     *
     * @param M_AttributeSetInstance_ID Product Attribute Set Instance
     */
    public void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) setValueNoCheck(COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValueNoCheck(
                    COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
    }

    /**
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getMAttributeSetInstance_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
    }

    public org.compiere.model.I_M_MovementLine getM_MovementLine() throws RuntimeException {
        return (org.compiere.model.I_M_MovementLine)
                MTable.get(getCtx(), org.compiere.model.I_M_MovementLine.Table_Name)
                        .getPO(getM_MovementLine_ID());
    }

    /**
     * Get Move Line.
     *
     * @return Inventory Move document Line
     */
    public int getM_MovementLine_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_M_MovementLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Move Line.
     *
     * @param M_MovementLine_ID Inventory Move document Line
     */
    public void setM_MovementLine_ID(int M_MovementLine_ID) {
        if (M_MovementLine_ID < 1) setValueNoCheck(COLUMNNAME_M_MovementLine_ID, null);
        else setValueNoCheck(COLUMNNAME_M_MovementLine_ID, Integer.valueOf(M_MovementLine_ID));
    }

    /**
     * Get Movement Quantity.
     *
     * @return Quantity of a product moved.
     */
    public BigDecimal getMovementQty() {
        BigDecimal bd = (BigDecimal) getValue(COLUMNNAME_MovementQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Movement Quantity.
     *
     * @param MovementQty Quantity of a product moved.
     */
    public void setMovementQty(BigDecimal MovementQty) {
        setValue(COLUMNNAME_MovementQty, MovementQty);
    }
}
