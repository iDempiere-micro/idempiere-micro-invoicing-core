package org.compiere.production;

import kotliquery.Row;
import org.compiere.accounting.AccountingPO;
import org.compiere.model.I_R_Request;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for R_Request
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_Request extends AccountingPO implements I_R_Request {

    /**
     * ConfidentialType AD_Reference_ID=340
     */
    public static final int CONFIDENTIALTYPE_AD_Reference_ID = 340;
    /**
     * Public Information = A
     */
    public static final String CONFIDENTIALTYPE_PublicInformation = "A";
    /**
     * Partner Confidential = C
     */
    public static final String CONFIDENTIALTYPE_PartnerConfidential = "C";
    /**
     * Internal = I
     */
    public static final String CONFIDENTIALTYPE_Internal = "I";
    /**
     * Private Information = P
     */
    public static final String CONFIDENTIALTYPE_PrivateInformation = "P";
    /**
     * ConfidentialTypeEntry AD_Reference_ID=340
     */
    public static final int CONFIDENTIALTYPEENTRY_AD_Reference_ID = 340;
    /**
     * Public Information = A
     */
    public static final String CONFIDENTIALTYPEENTRY_PublicInformation = "A";
    /**
     * Partner Confidential = C
     */
    public static final String CONFIDENTIALTYPEENTRY_PartnerConfidential = "C";
    /**
     * Internal = I
     */
    public static final String CONFIDENTIALTYPEENTRY_Internal = "I";
    /**
     * Private Information = P
     */
    public static final String CONFIDENTIALTYPEENTRY_PrivateInformation = "P";
    /**
     * DueType AD_Reference_ID=222
     */
    public static final int DUETYPE_AD_Reference_ID = 222;
    /**
     * Overdue = 3
     */
    public static final String DUETYPE_Overdue = "3";
    /**
     * Due = 5
     */
    public static final String DUETYPE_Due = "5";
    /**
     * Scheduled = 7
     */
    public static final String DUETYPE_Scheduled = "7";
    /**
     * Priority AD_Reference_ID=154
     */
    public static final int PRIORITY_AD_Reference_ID = 154;
    /**
     * High = 3
     */
    public static final String PRIORITY_High = "3";
    /**
     * Medium = 5
     */
    public static final String PRIORITY_Medium = "5";
    /**
     * Low = 7
     */
    public static final String PRIORITY_Low = "7";
    /**
     * Urgent = 1
     */
    public static final String PRIORITY_Urgent = "1";
    /**
     * Minor = 9
     */
    public static final String PRIORITY_Minor = "9";
    /**
     * PriorityUser AD_Reference_ID=154
     */
    public static final int PRIORITYUSER_AD_Reference_ID = 154;
    /**
     * High = 3
     */
    public static final String PRIORITYUSER_High = "3";
    /**
     * Medium = 5
     */
    public static final String PRIORITYUSER_Medium = "5";
    /**
     * Low = 7
     */
    public static final String PRIORITYUSER_Low = "7";
    /**
     * Urgent = 1
     */
    public static final String PRIORITYUSER_Urgent = "1";
    /**
     * Minor = 9
     */
    public static final String PRIORITYUSER_Minor = "9";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_Request(int R_Request_ID) {
        super(null, R_Request_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_Request(Row row) {
        super(row, -1);
    }

    /**
     * AccessLevel
     *
     * @return 7 - System - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_R_Request[" + getId() + "]";
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Close Date.
     *
     * @return Close Date
     */
    public Timestamp getCloseDate() {
        return (Timestamp) getValue(COLUMNNAME_CloseDate);
    }

    /**
     * Set Close Date.
     *
     * @param CloseDate Close Date
     */
    public void setCloseDate(Timestamp CloseDate) {
        setValue(COLUMNNAME_CloseDate, CloseDate);
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
     * Get Entry Confidentiality.
     *
     * @return Confidentiality of the individual entry
     */
    public String getConfidentialTypeEntry() {
        return getValue(COLUMNNAME_ConfidentialTypeEntry);
    }

    /**
     * Set Entry Confidentiality.
     *
     * @param ConfidentialTypeEntry Confidentiality of the individual entry
     */
    public void setConfidentialTypeEntry(String ConfidentialTypeEntry) {

        setValue(COLUMNNAME_ConfidentialTypeEntry, ConfidentialTypeEntry);
    }

    /**
     * Get Date next action.
     *
     * @return Date that this request should be acted on
     */
    public Timestamp getDateNextAction() {
        return (Timestamp) getValue(COLUMNNAME_DateNextAction);
    }

    /**
     * Set Date next action.
     *
     * @param DateNextAction Date that this request should be acted on
     */
    public void setDateNextAction(Timestamp DateNextAction) {
        setValue(COLUMNNAME_DateNextAction, DateNextAction);
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Due type.
     *
     * @param DueType Status of the next action for this Request
     */
    public void setDueType(String DueType) {

        setValue(COLUMNNAME_DueType, DueType);
    }

    /**
     * Set Escalated.
     *
     * @param IsEscalated This request has been escalated
     */
    public void setIsEscalated(boolean IsEscalated) {
        setValue(COLUMNNAME_IsEscalated, IsEscalated);
    }

    /**
     * Set Invoiced.
     *
     * @param IsInvoiced Is this invoiced?
     */
    public void setIsInvoiced(boolean IsInvoiced) {
        setValue(COLUMNNAME_IsInvoiced, IsInvoiced);
    }

    /**
     * Get Invoiced.
     *
     * @return Is this invoiced?
     */
    public boolean isInvoiced() {
        Object oo = getValue(COLUMNNAME_IsInvoiced);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
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
        setValueNoCheck(COLUMNNAME_IsSelfService, IsSelfService);
    }

    /**
     * Get Change Request.
     *
     * @return BOM (Engineering) Change Request
     */
    public int getChangeRequestId() {
        Integer ii = getValue(COLUMNNAME_M_ChangeRequest_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Change Request.
     *
     * @param M_ChangeRequest_ID BOM (Engineering) Change Request
     */
    public void setChangeRequestId(int M_ChangeRequest_ID) {
        if (M_ChangeRequest_ID < 1) setValue(COLUMNNAME_M_ChangeRequest_ID, null);
        else setValue(COLUMNNAME_M_ChangeRequest_ID, M_ChangeRequest_ID);
    }

    /**
     * Get Priority.
     *
     * @return Indicates if this request is of a high, medium or low priority.
     */
    public String getPriority() {
        return getValue(COLUMNNAME_Priority);
    }

    /**
     * Set Priority.
     *
     * @param Priority Indicates if this request is of a high, medium or low priority.
     */
    public void setPriority(String Priority) {

        setValue(COLUMNNAME_Priority, Priority);
    }

    /**
     * Get User Importance.
     *
     * @return Priority of the issue for the User
     */
    public String getPriorityUser() {
        return getValue(COLUMNNAME_PriorityUser);
    }

    /**
     * Set User Importance.
     *
     * @param PriorityUser Priority of the issue for the User
     */
    public void setPriorityUser(String PriorityUser) {

        setValue(COLUMNNAME_PriorityUser, PriorityUser);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Processed);
    }

    /**
     * Set Request Amount.
     *
     * @param RequestAmt Amount associated with this request
     */
    public void setRequestAmt(BigDecimal RequestAmt) {
        setValue(COLUMNNAME_RequestAmt, RequestAmt);
    }

    /**
     * Get Result.
     *
     * @return Result of the action taken
     */
    public String getResult() {
        return getValue(COLUMNNAME_Result);
    }

    /**
     * Get Group.
     *
     * @return Request Group
     */
    public int getGroupId() {
        Integer ii = getValue(COLUMNNAME_R_Group_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Request.
     *
     * @return Request from a Business Partner or Prospect
     */
    public int getRequestId() {
        Integer ii = getValue(COLUMNNAME_R_Request_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Request Type.
     *
     * @param R_RequestType_ID Type of request (e.g. Inquiry, Complaint, ..)
     */
    public void setRequestTypeId(int R_RequestType_ID) {
        if (R_RequestType_ID < 1) setValue(COLUMNNAME_R_RequestType_ID, null);
        else setValue(COLUMNNAME_R_RequestType_ID, R_RequestType_ID);
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
     * Set Status.
     *
     * @param R_Status_ID Request Status
     */
    public void setStatusId(int R_Status_ID) {
        if (R_Status_ID < 1) setValue(COLUMNNAME_R_Status_ID, null);
        else setValue(COLUMNNAME_R_Status_ID, R_Status_ID);
    }

    /**
     * Get Sales Representative.
     *
     * @return Sales Representative or Company Agent
     */
    public int getSalesRepresentativeId() {
        Integer ii = getValue(COLUMNNAME_SalesRep_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Representative.
     *
     * @param SalesRep_ID Sales Representative or Company Agent
     */
    public void setSalesRepresentativeId(int SalesRep_ID) {
        if (SalesRep_ID < 1) setValue(COLUMNNAME_SalesRep_ID, null);
        else setValue(COLUMNNAME_SalesRep_ID, SalesRep_ID);
    }

    /**
     * Get Start Date.
     *
     * @return First effective day (inclusive)
     */
    public Timestamp getStartDate() {
        return (Timestamp) getValue(COLUMNNAME_StartDate);
    }

    /**
     * Set Start Date.
     *
     * @param StartDate First effective day (inclusive)
     */
    public void setStartDate(Timestamp StartDate) {
        setValue(COLUMNNAME_StartDate, StartDate);
    }

    /**
     * Get Summary.
     *
     * @return Textual summary of this request
     */
    public String getSummary() {
        return getValue(COLUMNNAME_Summary);
    }

    /**
     * Set Summary.
     *
     * @param Summary Textual summary of this request
     */
    public void setSummary(String Summary) {
        setValue(COLUMNNAME_Summary, Summary);
    }

    @Override
    public int getTableId() {
        return I_R_Request.Table_ID;
    }
}
