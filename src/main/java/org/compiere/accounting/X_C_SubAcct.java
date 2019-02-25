package org.compiere.accounting;

import org.compiere.model.I_C_SubAcct;
import org.compiere.orm.BasePONameValue;

import java.sql.ResultSet;
import java.util.Properties;

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
    public X_C_SubAcct(Properties ctx, int C_SubAcct_ID) {
        super(ctx, C_SubAcct_ID);
        /**
         * if (C_SubAcct_ID == 0) { setC_ElementValue_ID (0); setC_SubAcct_ID (0); setName (null);
         * setValue (null); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_SubAcct(Properties ctx, ResultSet rs) {
        super(ctx, rs);
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
        StringBuffer sb = new StringBuffer("X_C_SubAcct[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Account Element.
     *
     * @return Account Element
     */
    public int getC_ElementValue_ID() {
        Integer ii = (Integer) getValue(COLUMNNAME_C_ElementValue_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_C_SubAcct.Table_ID;
    }
}
