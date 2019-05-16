package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_Replenish;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

public class X_M_Replenish extends PO implements I_M_Replenish {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Replenish(int M_Replenish_ID) {
        super(M_Replenish_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_Replenish(Row row) {
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
        return "X_M_Replenish[" + getId() + "]";
    }

    /**
     * Get Warehouse.
     *
     * @return Storage Warehouse and Service Point
     */
    public int getWarehouseId() {
        Integer ii = getValue(COLUMNNAME_M_Warehouse_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Qty Batch Size.
     *
     * @return Qty Batch Size
     */
    public BigDecimal getQtyBatchSize() {
        BigDecimal bd = getValue(COLUMNNAME_QtyBatchSize);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }
}
