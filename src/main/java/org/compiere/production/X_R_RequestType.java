package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_R_RequestType;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for R_RequestType
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_RequestType extends BasePOName implements I_R_RequestType {

    /**
     * Public Information = A
     */
    public static final String CONFIDENTIALTYPE_PublicInformation = "A";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_RequestType(int R_RequestType_ID) {
        super(R_RequestType_ID);
        /**
         * if (R_RequestType_ID == 0) { setConfidentialType (null); // C setDueDateTolerance (0); // 7
         * setIsAutoChangeRequest (false); setIsConfidentialInfo (false); // N setIsDefault (false); //
         * N setIsEMailWhenDue (false); setIsEMailWhenOverdue (false); setIsIndexed (false);
         * setIsSelfService (true); // Y setName (null); setRequestTypeId (0); setStatusCategoryId
         * (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_R_RequestType(Row row) {
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
        StringBuffer sb = new StringBuffer("X_R_RequestType[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Auto Due Date Days.
     *
     * @return Automatic Due Date Days
     */
    public int getAutoDueDateDays() {
        Integer ii = getValue(COLUMNNAME_AutoDueDateDays);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Auto Due Date Days.
     *
     * @param AutoDueDateDays Automatic Due Date Days
     */
    public void setAutoDueDateDays(int AutoDueDateDays) {
        setValue(COLUMNNAME_AutoDueDateDays, Integer.valueOf(AutoDueDateDays));
    }

    /**
     * Get Confidentiality.
     *
     * @return Type of Confidentiality
     */
    public String getConfidentialType() {
        return getValue(COLUMNNAME_ConfidentialType);
    }

    /**
     * Set Confidentiality.
     *
     * @param ConfidentialType Type of Confidentiality
     */
    public void setConfidentialType(String ConfidentialType) {

        setValue(COLUMNNAME_ConfidentialType, ConfidentialType);
    }

    /**
     * Get Due Date Tolerance.
     *
     * @return Tolerance in days between the Date Next Action and the date the request is regarded as
     * overdue
     */
    public int getDueDateTolerance() {
        Integer ii = getValue(COLUMNNAME_DueDateTolerance);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Due Date Tolerance.
     *
     * @param DueDateTolerance Tolerance in days between the Date Next Action and the date the request
     *                         is regarded as overdue
     */
    public void setDueDateTolerance(int DueDateTolerance) {
        setValue(COLUMNNAME_DueDateTolerance, Integer.valueOf(DueDateTolerance));
    }

    /**
     * Set Create Change Request.
     *
     * @param IsAutoChangeRequest Automatically create BOM (Engineering) Change Request
     */
    public void setIsAutoChangeRequest(boolean IsAutoChangeRequest) {
        setValue(COLUMNNAME_IsAutoChangeRequest, Boolean.valueOf(IsAutoChangeRequest));
    }

    /**
     * Set Confidential Info.
     *
     * @param IsConfidentialInfo Can enter confidential information
     */
    public void setIsConfidentialInfo(boolean IsConfidentialInfo) {
        setValue(COLUMNNAME_IsConfidentialInfo, Boolean.valueOf(IsConfidentialInfo));
    }

    /**
     * Set Default.
     *
     * @param IsDefault Default value
     */
    public void setIsDefault(boolean IsDefault) {
        setValue(COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
    }

    /**
     * Get Default.
     *
     * @return Default value
     */
    public boolean isDefault() {
        Object oo = getValue(COLUMNNAME_IsDefault);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set EMail when Due.
     *
     * @param IsEMailWhenDue Send EMail when Request becomes due
     */
    public void setIsEMailWhenDue(boolean IsEMailWhenDue) {
        setValue(COLUMNNAME_IsEMailWhenDue, Boolean.valueOf(IsEMailWhenDue));
    }

    /**
     * Set EMail when Overdue.
     *
     * @param IsEMailWhenOverdue Send EMail when Request becomes overdue
     */
    public void setIsEMailWhenOverdue(boolean IsEMailWhenOverdue) {
        setValue(COLUMNNAME_IsEMailWhenOverdue, Boolean.valueOf(IsEMailWhenOverdue));
    }

    /**
     * Set Indexed.
     *
     * @param IsIndexed Index the document for the internal search engine
     */
    public void setIsIndexed(boolean IsIndexed) {
        setValue(COLUMNNAME_IsIndexed, Boolean.valueOf(IsIndexed));
    }

    /**
     * Set Invoiced.
     *
     * @param IsInvoiced Is this invoiced?
     */
    public void setIsInvoiced(boolean IsInvoiced) {
        setValue(COLUMNNAME_IsInvoiced, Boolean.valueOf(IsInvoiced));
    }

    /**
     * Get Invoiced.
     *
     * @return Is this invoiced?
     */
    public boolean isInvoiced() {
        Object oo = getValue(COLUMNNAME_IsInvoiced);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Self-Service.
     *
     * @param IsSelfService This is a Self-Service entry or this entry can be changed via Self-Service
     */
    public void setIsSelfService(boolean IsSelfService) {
        setValue(COLUMNNAME_IsSelfService, Boolean.valueOf(IsSelfService));
    }

    /**
     * Get Request Type.
     *
     * @return Type of request (e.g. Inquiry, Complaint, ..)
     */
    public int getRequestTypeId() {
        Integer ii = getValue(COLUMNNAME_R_RequestType_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Status Category.
     *
     * @param R_StatusCategory_ID Request Status Category
     */
    public void setStatusCategoryId(int R_StatusCategory_ID) {
        if (R_StatusCategory_ID < 1) setValue(COLUMNNAME_R_StatusCategory_ID, null);
        else setValue(COLUMNNAME_R_StatusCategory_ID, Integer.valueOf(R_StatusCategory_ID));
    }

    @Override
    public int getTableId() {
        return I_R_RequestType.Table_ID;
    }
}
