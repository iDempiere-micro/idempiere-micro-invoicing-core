package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_R_Status;
import org.compiere.orm.BasePONameValue;

/**
 * Generated Model for R_Status
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_Status extends BasePONameValue implements I_R_Status {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_Status(int R_Status_ID) {
        super(R_Status_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_Status(Row row) {
        super(row);
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
        return "X_R_Status[" + getId() + "]";
    }

    /**
     * Set Closed Status.
     *
     * @param IsClosed The status is closed
     */
    public void setIsClosed(boolean IsClosed) {
        setValue(COLUMNNAME_IsClosed, IsClosed);
    }

    /**
     * Get Closed Status.
     *
     * @return The status is closed
     */
    public boolean isClosed() {
        Object oo = getValue(COLUMNNAME_IsClosed);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Default.
     *
     * @param IsDefault Default value
     */
    public void setIsDefault(boolean IsDefault) {
        setValue(COLUMNNAME_IsDefault, IsDefault);
    }

    /**
     * Get Default.
     *
     * @return Default value
     */
    public boolean isDefault() {
        Object oo = getValue(COLUMNNAME_IsDefault);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Final Close.
     *
     * @param IsFinalClose Entries with Final Close cannot be re-opened
     */
    public void setIsFinalClose(boolean IsFinalClose) {
        setValue(COLUMNNAME_IsFinalClose, Boolean.valueOf(IsFinalClose));
    }

    /**
     * Get Final Close.
     *
     * @return Entries with Final Close cannot be re-opened
     */
    public boolean isFinalClose() {
        Object oo = getValue(COLUMNNAME_IsFinalClose);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Open Status.
     *
     * @param IsOpen The status is closed
     */
    public void setIsOpen(boolean IsOpen) {
        setValue(COLUMNNAME_IsOpen, Boolean.valueOf(IsOpen));
    }

    /**
     * Get Open Status.
     *
     * @return The status is closed
     */
    public boolean isOpen() {
        Object oo = getValue(COLUMNNAME_IsOpen);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Web Can Update.
     *
     * @param IsWebCanUpdate Entry can be updated from the Web
     */
    public void setIsWebCanUpdate(boolean IsWebCanUpdate) {
        setValue(COLUMNNAME_IsWebCanUpdate, Boolean.valueOf(IsWebCanUpdate));
    }

    /**
     * Get Web Can Update.
     *
     * @return Entry can be updated from the Web
     */
    public boolean isWebCanUpdate() {
        Object oo = getValue(COLUMNNAME_IsWebCanUpdate);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Next Status.
     *
     * @return Move to next status automatically after timeout
     */
    public int getNextStatusId() {
        Integer ii = getValue(COLUMNNAME_Next_Status_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Next Status.
     *
     * @param Next_Status_ID Move to next status automatically after timeout
     */
    public void setNextStatusId(int Next_Status_ID) {
        if (Next_Status_ID < 1) setValue(COLUMNNAME_Next_Status_ID, null);
        else setValue(COLUMNNAME_Next_Status_ID, Integer.valueOf(Next_Status_ID));
    }

    /**
     * Get Status Category.
     *
     * @return Request Status Category
     */
    public int getStatusCategoryId() {
        Integer ii = getValue(COLUMNNAME_R_StatusCategory_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Status.
     *
     * @return Request Status
     */
    public int getStatusId() {
        Integer ii = getValue(COLUMNNAME_R_Status_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Timeout in Days.
     *
     * @return Timeout in Days to change Status automatically
     */
    public int getTimeoutDays() {
        Integer ii = getValue(COLUMNNAME_TimeoutDays);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Update Status.
     *
     * @return Automatically change the status after entry from web
     */
    public int getUpdateStatusId() {
        Integer ii = getValue(COLUMNNAME_Update_Status_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Update Status.
     *
     * @param Update_Status_ID Automatically change the status after entry from web
     */
    public void setUpdateStatusId(int Update_Status_ID) {
        if (Update_Status_ID < 1) setValue(COLUMNNAME_Update_Status_ID, null);
        else setValue(COLUMNNAME_Update_Status_ID, Integer.valueOf(Update_Status_ID));
    }

    @Override
    public int getTableId() {
        return I_R_Status.Table_ID;
    }
}
