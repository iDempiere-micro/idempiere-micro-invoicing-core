package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_SubAcct;
import org.compiere.orm.BasePONameValue;

/**
 * Generated Model for C_SubAcct
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_SubAcct extends BasePONameValue implements I_C_SubAcct {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_SubAcct(int C_SubAcct_ID) {
        super(C_SubAcct_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_SubAcct(Row row) {
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
        return "X_C_SubAcct[" + getId() + "]";
    }

    /**
     * Get Account Element.
     *
     * @return Account Element
     */
    public int getElementValueId() {
        Integer ii = getValue(COLUMNNAME_C_ElementValue_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_C_SubAcct.Table_ID;
    }
}
