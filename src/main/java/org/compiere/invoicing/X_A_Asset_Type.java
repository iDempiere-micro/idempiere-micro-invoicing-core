package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_A_Asset_Type;
import org.compiere.orm.BasePONameValue;

import java.util.Properties;

/**
 * Generated Model for A_Asset_Type
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_A_Asset_Type extends BasePONameValue implements I_A_Asset_Type {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_A_Asset_Type(Properties ctx, int A_Asset_Type_ID) {
        super(ctx, A_Asset_Type_ID);
    }

    /**
     * Load Constructor
     */
    public X_A_Asset_Type(Properties ctx, Row row) {
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

    public String toString() {
        StringBuffer sb = new StringBuffer("X_A_Asset_Type[").append(getId()).append("]");
        return sb.toString();
    }

    @Override
    public int getTableId() {
        return I_A_Asset_Type.Table_ID;
    }
}
