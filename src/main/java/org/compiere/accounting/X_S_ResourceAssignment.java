package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_S_ResourceAssignment;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for S_ResourceAssignment
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_S_ResourceAssignment extends BasePOName
        implements I_S_ResourceAssignment {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_S_ResourceAssignment(int S_ResourceAssignment_ID) {
        super(S_ResourceAssignment_ID);
    }

    /**
     * Load Constructor
     */
    public X_S_ResourceAssignment(Row row) {
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
        return "X_S_ResourceAssignment[" + getId() + "]";
    }

    /**
     * Get Assign From.
     *
     * @return Assign resource from
     */
    public Timestamp getAssignDateFrom() {
        return (Timestamp) getValue(COLUMNNAME_AssignDateFrom);
    }

    /**
     * Set Assign From.
     *
     * @param AssignDateFrom Assign resource from
     */
    public void setAssignDateFrom(Timestamp AssignDateFrom) {
        setValueNoCheck(COLUMNNAME_AssignDateFrom, AssignDateFrom);
    }

    /**
     * Get Assign To.
     *
     * @return Assign resource until
     */
    public Timestamp getAssignDateTo() {
        return (Timestamp) getValue(COLUMNNAME_AssignDateTo);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
    }

    /**
     * Set Confirmed.
     *
     * @param IsConfirmed Assignment is confirmed
     */
    public void setIsConfirmed(boolean IsConfirmed) {
        setValueNoCheck(COLUMNNAME_IsConfirmed, Boolean.valueOf(IsConfirmed));
    }

    /**
     * Get Confirmed.
     *
     * @return Assignment is confirmed
     */
    public boolean isConfirmed() {
        Object oo = getValue(COLUMNNAME_IsConfirmed);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Quantity.
     *
     * @return Quantity
     */
    public BigDecimal getQty() {
        BigDecimal bd = getValue(COLUMNNAME_Qty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        setValueNoCheck(COLUMNNAME_Qty, Qty);
    }

    /**
     * Get Resource.
     *
     * @return Resource
     */
    public int getResourceID() {
        Integer ii = getValue(COLUMNNAME_S_Resource_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_S_ResourceAssignment.Table_ID;
    }
}
