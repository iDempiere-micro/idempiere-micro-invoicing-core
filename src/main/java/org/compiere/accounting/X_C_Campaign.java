package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_Campaign;
import org.compiere.orm.BasePONameValue;

import java.util.Properties;

/**
 * Generated Model for C_Campaign
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_Campaign extends BasePONameValue implements I_C_Campaign {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Campaign(Properties ctx, int C_Campaign_ID) {
        super(ctx, C_Campaign_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_Campaign(Properties ctx, Row row) {
        super(ctx, row);
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
        return "X_C_Campaign[" + getId() + "]";
    }

}
