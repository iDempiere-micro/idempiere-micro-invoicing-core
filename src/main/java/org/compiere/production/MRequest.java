package org.compiere.production;

import kotliquery.Row;
import org.compiere.crm.MBPGroup;
import org.compiere.crm.MBPGroupKt;
import org.compiere.crm.X_C_BP_Group;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_R_Request;
import org.compiere.model.I_R_RequestType;
import org.compiere.model.I_R_RequestUpdate;
import org.compiere.orm.Query;
import org.compiere.orm.TimeUtil;
import org.idempiere.common.util.Env;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Request Model
 *
 * @author Jorg Janke
 * @version $Id: MRequest.java,v 1.2 2006/07/30 00:51:03 jjanke Exp $
 */
public class MRequest extends X_R_Request {
    /**
     *
     */
    private static final long serialVersionUID = -6049674214655497548L;

    /**
     * Request Type
     */
    private I_R_RequestType m_requestType = null;
    /**
     * BPartner
     */
    private I_C_BPartner m_partner = null;

    /**
     * ************************************************************************ Constructor
     *
     * @param R_Request_ID request or 0 for new
     */
    public MRequest(int R_Request_ID) {
        super(R_Request_ID);
        if (R_Request_ID == 0) {
            setDueType(X_R_Request.DUETYPE_Due);
            setConfidentialType(X_R_Request.CONFIDENTIALTYPE_PublicInformation); // A
            setConfidentialTypeEntry(X_R_Request.CONFIDENTIALTYPEENTRY_PublicInformation); // A
            setProcessed(false);
            setRequestAmt(Env.ZERO);
            setPriorityUser(X_R_Request.PRIORITY_Low);
            setIsEscalated(false);
            setIsSelfService(false);
            setIsInvoiced(false);
        }
    } //	MRequest

    /**
     * SelfService Constructor
     *  @param SalesRep_ID      SalesRep
     * @param R_RequestType_ID request type
     * @param Summary          summary
     * @param isSelfService    self service
     */
    public MRequest(

            int SalesRep_ID,
            int R_RequestType_ID,
            String Summary,
            boolean isSelfService) {
        this(0);
        setValue("SalesRep_ID", SalesRep_ID); // 	could be 0
        setValue("R_RequestType_ID", R_RequestType_ID);
        setSummary(Summary);
        setIsSelfService(isSelfService);
        getRequestType();
        if (m_requestType != null) {
            String ct = m_requestType.getConfidentialType();
            if (ct != null) {
                setConfidentialType(ct);
                setConfidentialTypeEntry(ct);
            }
        }
    } //	MRequest

    /**
     * Load Constructor
     *
     */
    public MRequest(Row row) {
        super(row);
    } //	MRequest

    /**
     * ************************************************************************ Set Default Request
     * Type.
     */
    public void setRequestTypeId() {
        m_requestType = MRequestType.getDefault();
        if (m_requestType == null) log.warning("No default found");
        else super.setRequestTypeId(m_requestType.getRequestTypeId());
    } //	setRequestTypeId

    /**
     * Set Default Request Status.
     */
    public void setStatusId() {
        MStatus status = MStatus.getDefault(getRequestTypeId());
        if (status == null) {
            log.warning("No default found");
            if (getStatusId() != 0) setStatusId(0);
        } else setStatusId(status.getStatusId());
    } //	setStatusId

    /**
     * Set DueType based on Date Next Action
     */
    public void setDueType() {
        Timestamp due = getDateNextAction();
        if (due == null) return;
        //
        Timestamp overdue = TimeUtil.addDays(due, getRequestType().getDueDateTolerance());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        //
        String DueType = X_R_Request.DUETYPE_Due;
        if (now.before(due)) DueType = X_R_Request.DUETYPE_Scheduled;
        else if (now.after(overdue)) DueType = X_R_Request.DUETYPE_Overdue;
        super.setDueType(DueType);
    } //	setDueType

    /**
     * Get Updates
     *
     * @param confidentialType maximum confidential type - null = all
     * @return updates
     */
    public I_R_RequestUpdate[] getUpdates(String confidentialType) {
        final String whereClause = MRequestUpdate.COLUMNNAME_R_Request_ID + "=?";
        List<I_R_RequestUpdate> listUpdates =
                new Query<I_R_RequestUpdate>(I_R_RequestUpdate.Table_Name, whereClause)
                        .setParameters(getId())
                        .setOrderBy("Created DESC")
                        .list();
        ArrayList<I_R_RequestUpdate> list = new ArrayList<>();
        for (I_R_RequestUpdate ru : listUpdates) {
            if (confidentialType != null) {
                //	Private only if private
                if (ru.getConfidentialTypeEntry()
                        .equals(X_R_Request.CONFIDENTIALTYPEENTRY_PrivateInformation)
                        && !confidentialType.equals(X_R_Request.CONFIDENTIALTYPEENTRY_PrivateInformation))
                    continue;
                //	Internal not if Customer/Public
                if (ru.getConfidentialTypeEntry().equals(X_R_Request.CONFIDENTIALTYPEENTRY_Internal)
                        && (confidentialType.equals(X_R_Request.CONFIDENTIALTYPEENTRY_PartnerConfidential)
                        || confidentialType.equals(X_R_Request.CONFIDENTIALTYPEENTRY_PublicInformation)))
                    continue;
                //	No Customer if public
                if (ru.getConfidentialTypeEntry()
                        .equals(X_R_Request.CONFIDENTIALTYPEENTRY_PartnerConfidential)
                        && confidentialType.equals(X_R_Request.CONFIDENTIALTYPEENTRY_PublicInformation))
                    continue;
            }
            list.add(ru);
        }
        //
        I_R_RequestUpdate[] retValue = new I_R_RequestUpdate[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getUpdates

    /**
     * Get Request Type
     *
     * @return Request Type
     */
    public I_R_RequestType getRequestType() {
        if (m_requestType == null) {
            int R_RequestType_ID = getRequestTypeId();
            if (R_RequestType_ID == 0) {
                setRequestTypeId();
                R_RequestType_ID = getRequestTypeId();
            }
            m_requestType = MRequestType.get(R_RequestType_ID);
        }
        return m_requestType;
    } //	getRequestType

    /**
     * Get BPartner (may be not defined)
     *
     * @return Sales Rep User
     */
    public I_C_BPartner getBPartner() {
        if (getBusinessPartnerId() == 0) return null;
        if (m_partner != null && m_partner.getBusinessPartnerId() != getBusinessPartnerId()) m_partner = null;
        if (m_partner == null) m_partner = getBusinessPartnerService().getById(getBusinessPartnerId());
        return m_partner;
    } //	getBPartner

    /**
     * Set Priority
     */
    private void setPriority() {
        if (getPriorityUser() == null) setPriorityUser(X_R_Request.PRIORITYUSER_Low);
        //
        if (getBPartner() != null) {
            MBPGroup bpg = MBPGroupKt.getBusinessPartnerGroup(getBPartner().getBPGroupId());
            String prioBase = bpg.getPriorityBase();
            if (prioBase != null && !prioBase.equals(X_C_BP_Group.PRIORITYBASE_Same)) {
                char targetPrio = getPriorityUser().charAt(0);
                if (prioBase.equals(X_C_BP_Group.PRIORITYBASE_Lower)) targetPrio += 2;
                else targetPrio -= 2;
                if (targetPrio < X_R_Request.PRIORITY_High.charAt(0)) // 	1
                    targetPrio = X_R_Request.PRIORITY_High.charAt(0);
                if (targetPrio > X_R_Request.PRIORITY_Low.charAt(0)) // 	9
                    targetPrio = X_R_Request.PRIORITY_Low.charAt(0);
                if (getPriority() == null) setPriority(String.valueOf(targetPrio));
                else //	previous priority
                {
                    if (targetPrio < getPriority().charAt(0)) setPriority(String.valueOf(targetPrio));
                }
            }
        }
        //	Same if nothing else
        if (getPriority() == null) setPriority(getPriorityUser());
    } //	setPriority

    /**
     * Set Confidential Type Entry
     *
     * @param ConfidentialTypeEntry confidentiality
     */
    public void setConfidentialTypeEntry(String ConfidentialTypeEntry) {
        if (ConfidentialTypeEntry == null) ConfidentialTypeEntry = getConfidentialType();
        //
        if (X_R_Request.CONFIDENTIALTYPE_Internal.equals(getConfidentialType()))
            super.setConfidentialTypeEntry(X_R_Request.CONFIDENTIALTYPE_Internal);
        else if (X_R_Request.CONFIDENTIALTYPE_PrivateInformation.equals(getConfidentialType())) {
            if (X_R_Request.CONFIDENTIALTYPE_Internal.equals(ConfidentialTypeEntry)
                    || X_R_Request.CONFIDENTIALTYPE_PrivateInformation.equals(ConfidentialTypeEntry))
                super.setConfidentialTypeEntry(ConfidentialTypeEntry);
            else super.setConfidentialTypeEntry(X_R_Request.CONFIDENTIALTYPE_PrivateInformation);
        } else if (X_R_Request.CONFIDENTIALTYPE_PartnerConfidential.equals(getConfidentialType())) {
            if (X_R_Request.CONFIDENTIALTYPE_Internal.equals(ConfidentialTypeEntry)
                    || X_R_Request.CONFIDENTIALTYPE_PrivateInformation.equals(ConfidentialTypeEntry)
                    || X_R_Request.CONFIDENTIALTYPE_PartnerConfidential.equals(ConfidentialTypeEntry))
                super.setConfidentialTypeEntry(ConfidentialTypeEntry);
            else super.setConfidentialTypeEntry(X_R_Request.CONFIDENTIALTYPE_PartnerConfidential);
        } else if (X_R_Request.CONFIDENTIALTYPE_PublicInformation.equals(getConfidentialType()))
            super.setConfidentialTypeEntry(ConfidentialTypeEntry);
    } //	setConfidentialTypeEntry

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MRequest[" + getId() + "-" + getDocumentNo() + "]";
    } //	toString

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        //	Request Type
        getRequestType();
        if (newRecord || isValueChanged("R_RequestType_ID")) {
            if (m_requestType != null) {
                if (isInvoiced() != m_requestType.isInvoiced()) setIsInvoiced(m_requestType.isInvoiced());
                if (getDateNextAction() == null && m_requestType.getAutoDueDateDays() > 0)
                    setDateNextAction(
                            TimeUtil.addDays(
                                    new Timestamp(System.currentTimeMillis()), m_requestType.getAutoDueDateDays()));
            }
            //	Is Status Valid
            if (getStatusId() != 0) {
                MStatus sta = MStatus.get(getStatusId());
                MRequestType rt = MRequestType.get(getRequestTypeId());
                if (sta.getStatusCategoryId() != rt.getStatusCategoryId())
                    setStatusId(); //	set to default
            }
        }

        //	Request Status
        if (getStatusId() == 0) setStatusId();
        //	Validate/Update Due Type
        setDueType();
        MStatus status = MStatus.get(getStatusId());
        //	Close/Open
        if (status != null) {
            if (status.isOpen()) {
                if (getStartDate() == null) setStartDate(new Timestamp(System.currentTimeMillis()));
                if (getCloseDate() != null) setCloseDate(null);
            }
            if (status.isClosed() && getCloseDate() == null)
                setCloseDate(new Timestamp(System.currentTimeMillis()));
            if (status.isFinalClose()) setProcessed(true);
        }

        //	Confidential Info
        if (getConfidentialType() == null) {
            getRequestType();
            if (m_requestType != null) {
                String ct = m_requestType.getConfidentialType();
                if (ct != null) setConfidentialType(ct);
            }
            if (getConfidentialType() == null)
                setConfidentialType(X_R_Request.CONFIDENTIALTYPEENTRY_PublicInformation);
        }
        if (getConfidentialTypeEntry() == null) setConfidentialTypeEntry(getConfidentialType());
        else setConfidentialTypeEntry(getConfidentialTypeEntry());

        //	Importance / Priority
        setPriority();

        return true;
    } //	beforeSave

    /**
     * Set SalesRep_ID
     *
     * @param SalesRep_ID id
     */
    public void setSalesRepresentativeId(int SalesRep_ID) {
        if (SalesRep_ID != 0) super.setSalesRepresentativeId(SalesRep_ID);
        else if (getSalesRepresentativeId() != 0)
            log.warning("Ignored - Tried to set SalesRep_ID to 0 from " + getSalesRepresentativeId());
    } //	setSalesRepresentativeId

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;

        //	Create Update
        if (newRecord && getResult() != null) {
            MRequestUpdate update = new MRequestUpdate(this);
            update.saveEx();
        }

        //	ChangeRequest - created in Request Processor
        if (getChangeRequestId() != 0
                && isValueChanged(I_R_Request.COLUMNNAME_R_Group_ID)) // 	different ECN assignment?
        {
            int oldID = getValueOldAsInt(I_R_Request.COLUMNNAME_R_Group_ID);
            if (getGroupId() == 0) {
                setChangeRequestId(0); // 	not effective as in afterSave
            } else {
                MGroup oldG = MGroup.get(oldID);
                MGroup newG = MGroup.get(getGroupId());
                if (oldG.getPPProductBOMId() != newG.getPPProductBOMId()
                        || oldG.getChangeNoticeId() != newG.getChangeNoticeId()) {
                    MChangeRequest ecr = new MChangeRequest(getChangeRequestId());
                    if (!ecr.isProcessed() || ecr.getFixChangeNoticeId() == 0) {
                        ecr.setProductBOMId(newG.getPPProductBOMId());
                        ecr.setChangeNoticeId(newG.getChangeNoticeId());
                        ecr.saveEx();
                    }
                }
            }
        }

        return success;
    } //	afterSave

} //	MRequest
