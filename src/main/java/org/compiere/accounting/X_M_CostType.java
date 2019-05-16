package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_CostType;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for M_CostType
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_CostType extends BasePOName implements I_M_CostType {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_CostType(int M_CostType_ID) {
        super(M_CostType_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_CostType(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_M_CostType[" + getId() + "]";
    }

    /**
     * Get Cost Type.
     *
     * @return Type of Cost (e.g. Current, Plan, Future)
     */
    public int getCostTypeId() {
        Integer ii = getValue(COLUMNNAME_M_CostType_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_M_CostType.Table_ID;
    }
}
