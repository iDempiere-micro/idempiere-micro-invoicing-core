package org.compiere.wf;

import org.compiere.model.I_AD_Workflow_Access;
import org.compiere.orm.PO;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for AD_Workflow_Access
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_Workflow_Access extends PO implements I_AD_Workflow_Access {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_Workflow_Access(Properties ctx, int AD_Workflow_Access_ID) {
        super(ctx, AD_Workflow_Access_ID);
        /**
         * if (AD_Workflow_Access_ID == 0) { setRoleId (0); setAD_Workflow_ID (0); setIsReadWrite
         * (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_AD_Workflow_Access(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    }

    /**
     * AccessLevel
     *
     * @return 6 - System - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }


    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_AD_Workflow_Access[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Set Role.
     *
     * @param AD_Role_ID Responsibility Role
     */
    public void setRoleId(int AD_Role_ID) {
        if (AD_Role_ID < 0) set_ValueNoCheck(COLUMNNAME_AD_Role_ID, null);
        else set_ValueNoCheck(COLUMNNAME_AD_Role_ID, Integer.valueOf(AD_Role_ID));
    }

    /**
     * Set Workflow.
     *
     * @param AD_Workflow_ID Workflow or combination of tasks
     */
    public void setWorkflowId(int AD_Workflow_ID) {
        if (AD_Workflow_ID < 1) set_ValueNoCheck(COLUMNNAME_AD_Workflow_ID, null);
        else set_ValueNoCheck(COLUMNNAME_AD_Workflow_ID, Integer.valueOf(AD_Workflow_ID));
    }

    /**
     * Set Read Write.
     *
     * @param IsReadWrite Field is read / write
     */
    public void setIsReadWrite(boolean IsReadWrite) {
        set_Value(COLUMNNAME_IsReadWrite, Boolean.valueOf(IsReadWrite));
    }

}
