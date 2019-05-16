package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_M_ProductionLineMA;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for M_ProductionLineMA
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_ProductionLineMA extends PO implements I_M_ProductionLineMA {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_ProductionLineMA(int M_ProductionLineMA_ID) {
        super(M_ProductionLineMA_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_ProductionLineMA(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_M_ProductionLineMA[" + getId() + "]";
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
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getAttributeSetInstanceId() {
        Integer ii = getValue(COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Attribute Set Instance.
     *
     * @param M_AttributeSetInstance_ID Product Attribute Set Instance
     */
    public void setAttributeSetInstanceId(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) setValueNoCheck(COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValueNoCheck(
                    COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
    }

    /**
     * Get Movement Quantity.
     *
     * @return Quantity of a product moved.
     */
    public BigDecimal getMovementQty() {
        BigDecimal bd = getValue(COLUMNNAME_MovementQty);
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

    /**
     * Set Production Line.
     *
     * @param M_ProductionLine_ID Document Line representing a production
     */
    public void setProductionLineId(int M_ProductionLine_ID) {
        if (M_ProductionLine_ID < 1) setValueNoCheck(COLUMNNAME_M_ProductionLine_ID, null);
        else setValueNoCheck(COLUMNNAME_M_ProductionLine_ID, Integer.valueOf(M_ProductionLine_ID));
    }

    @Override
    public int getTableId() {
        return I_M_ProductionLineMA.Table_ID;
    }
}
