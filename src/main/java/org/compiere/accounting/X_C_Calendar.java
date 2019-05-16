package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_Calendar;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for C_Calendar
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_Calendar extends BasePOName implements I_C_Calendar {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Calendar(int C_Calendar_ID) {
        super(C_Calendar_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_Calendar(Row row) {
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
        return "X_C_Calendar[" + getId() + "]";
    }

    /**
     * Get Calendar.
     *
     * @return Accounting Calendar Name
     */
    public int getCalendarId() {
        Integer ii = getValue(COLUMNNAME_C_Calendar_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_C_Calendar.Table_ID;
    }
}
