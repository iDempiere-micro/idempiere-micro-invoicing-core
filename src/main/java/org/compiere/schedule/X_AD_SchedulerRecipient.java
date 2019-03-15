package org.compiere.schedule;

import kotliquery.Row;
import org.compiere.model.I_AD_SchedulerRecipient;
import org.compiere.orm.PO;

import java.util.Properties;

/**
 * Generated Model for AD_SchedulerRecipient
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_SchedulerRecipient extends PO implements I_AD_SchedulerRecipient {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_SchedulerRecipient(Properties ctx, int AD_SchedulerRecipient_ID) {
        super(ctx, AD_SchedulerRecipient_ID);
        /*
         * if (AD_SchedulerRecipient_ID == 0) { setAD_Scheduler_ID (0); setAD_SchedulerRecipient_ID (0);
         * }
         */
    }

    /**
     * Load Constructor
     */
    public X_AD_SchedulerRecipient(Properties ctx, Row row) {
        super(ctx, row);
    }

    /**
     * AccessLevel
     *
     * @return 6 - System - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_AD_SchedulerRecipient[" + getId() + "]";
    }

    /**
     * Get Role.
     *
     * @return Responsibility Role
     */
    public int getRoleId() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_Role_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getUserId() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_AD_SchedulerRecipient.Table_ID;
    }
}
