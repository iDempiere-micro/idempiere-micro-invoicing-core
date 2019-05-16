package org.compiere.production;

import kotliquery.Row;
import org.compiere.accounting.MAccount;
import org.compiere.model.I_C_ProjectIssue;
import org.compiere.model.I_C_ProjectLine;
import org.compiere.model.I_C_ProjectPhase;
import org.compiere.model.I_M_PriceList;
import org.compiere.orm.MTree_Base;
import org.compiere.orm.PO;
import org.compiere.orm.Query;
import org.compiere.product.MPriceList;
import org.idempiere.common.util.Env;

import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValue;

/**
 * Project Model
 *
 * @author Jorg Janke
 * @version $Id: MProject.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class MProject extends X_C_Project {
    /**
     *
     */
    private static final long serialVersionUID = 8631795136761641303L;
    /**
     * Cached PL
     */
    private int m_M_PriceList_ID = 0;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_Project_ID id
     */
    public MProject(int C_Project_ID) {
        super(C_Project_ID);
        if (C_Project_ID == 0) {
            setCommittedAmt(Env.ZERO);
            setCommittedQty(Env.ZERO);
            setInvoicedAmt(Env.ZERO);
            setInvoicedQty(Env.ZERO);
            setPlannedAmt(Env.ZERO);
            setPlannedMarginAmt(Env.ZERO);
            setPlannedQty(Env.ZERO);
            setProjectBalanceAmt(Env.ZERO);
            setProjInvoiceRule(PROJINVOICERULE_None);
            setProjectLineLevel(PROJECTLINELEVEL_Project);
            setIsCommitCeiling(false);
            setIsCommitment(false);
            setIsSummary(false);
            setProcessed(false);
        }
    } //	MProject

    /**
     * Load Constructor
     *
     */
    public MProject(Row row) {
        super(row);
    } //	MProject

    /**
     * Get Project Type as Int (is Button).
     *
     * @return C_ProjectType_ID id
     */
    public int getProjectTypeIdAsInt() {
        String pj = super.getProjectTypeId();
        if (pj == null) return 0;
        int C_ProjectType_ID = 0;
        try {
            C_ProjectType_ID = Integer.parseInt(pj);
        } catch (Exception ex) {
            log.log(Level.SEVERE, pj, ex);
        }
        return C_ProjectType_ID;
    } //	getProjectTypeId_Int

    /**
     * Set Project Type (overwrite r/o)
     *
     * @param C_ProjectType_ID id
     */
    public void setProjectTypeId(int C_ProjectType_ID) {
        if (C_ProjectType_ID == 0) super.setProjectTypeId(null);
        else super.setValue("C_ProjectType_ID", C_ProjectType_ID);
    } //	setProjectTypeId

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MProject[" +
                getId() +
                "-" +
                getSearchKey() +
                ",ProjectCategory=" +
                getProjectCategory() +
                "]";
    } //	toString

    /**
     * Get Price List from Price List Version
     *
     * @return price list or 0
     */
    public int getPriceListId() {
        if (getPriceListVersionId() == 0) return 0;
        if (m_M_PriceList_ID > 0) return m_M_PriceList_ID;
        //
        String sql = "SELECT M_PriceList_ID FROM M_PriceList_Version WHERE M_PriceList_Version_ID=?";
        m_M_PriceList_ID = getSQLValue(sql, getPriceListVersionId());
        return m_M_PriceList_ID;
    } //	getPriceListId

    /**
     * Set PL Version
     *
     * @param M_PriceList_Version_ID id
     */
    public void setPriceListVersionId(int M_PriceList_Version_ID) {
        super.setPriceListVersionId(M_PriceList_Version_ID);
        m_M_PriceList_ID = 0; // 	reset
    } //	setPriceListVersionId

    /**
     * ************************************************************************ Get Project Lines
     *
     * @return Array of lines
     */
    public I_C_ProjectLine[] getLines() {
        // FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
        final String whereClause = "C_Project_ID=?";
        List<I_C_ProjectLine> list =
                new Query<I_C_ProjectLine>(I_C_ProjectLine.Table_Name, whereClause)
                        .setParameters(getProjectId())
                        .setOrderBy("Line")
                        .list();
        //
        I_C_ProjectLine[] retValue = new I_C_ProjectLine[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getLines

    /**
     * ************************************************************************ Get Project Lines from
     * a Phase
     *
     * @return Array of lines from a Phase
     */
    public I_C_ProjectLine[] getPhaseLines(int phase) {
        final String whereClause = "C_Project_ID=? and C_ProjectPhase_ID=?";
        List<I_C_ProjectLine> list =
                new Query<I_C_ProjectLine>(I_C_ProjectLine.Table_Name, whereClause)
                        .setParameters(getProjectId(), phase)
                        .setOrderBy("Line")
                        .list();
        //
        I_C_ProjectLine[] retValue = new I_C_ProjectLine[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getPhaseLines

    /**
     * Get Project Issues
     *
     * @return Array of issues
     */
    public MProjectIssue[] getIssues() {
        // FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
        String whereClause = "C_Project_ID=?";
        List<MProjectIssue> list =
                new Query(I_C_ProjectIssue.Table_Name, whereClause)
                        .setParameters(getProjectId())
                        .setOrderBy("Line")
                        .list();
        //
        MProjectIssue[] retValue = new MProjectIssue[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getIssues

    /**
     * Get Project Phases
     *
     * @return Array of phases
     */
    public MProjectPhase[] getPhases() {
        // FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
        String whereClause = "C_Project_ID=?";
        List<MProjectPhase> list =
                new Query(I_C_ProjectPhase.Table_Name, whereClause)
                        .setParameters(getProjectId())
                        .setOrderBy("SeqNo")
                        .list();
        //
        MProjectPhase[] retValue = new MProjectPhase[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getPhases

    /**
     * ************************************************************************ Copy Lines/Phase/Task
     * from other Project
     *
     * @param project project
     * @return number of total lines copied
     */
    public int copyDetailsFrom(MProject project) {
        if (isProcessed() || project == null) return 0;
        int count = copyLinesFrom(project) + copyPhasesFrom(project);
        return count;
    } //	copyDetailsFrom

    /**
     * Copy Lines From other Project
     *
     * @param project project
     * @return number of lines copied
     */
    public int copyLinesFrom(MProject project) {
        if (isProcessed() || project == null) return 0;
        int count = 0;
        I_C_ProjectLine[] fromLines = project.getLines();
        for (I_C_ProjectLine fromLine : fromLines) {
            // BF 3067850 - monhate
            if ((fromLine.getProjectPhaseId() != 0) || (fromLine.getProjectTaskId() != 0))
                continue;

            MProjectLine line = new MProjectLine(0);
            copyValues((PO)fromLine, line, getClientId(), getOrgId());
            line.setProjectId(getProjectId());
            line.setInvoicedAmt(Env.ZERO);
            line.setInvoicedQty(Env.ZERO);
            line.setOrderPOId(0);
            line.setOrderId(0);
            line.setProcessed(false);
            if (line.save()) count++;
        }
        if (fromLines.length != count)
            log.log(
                    Level.SEVERE, "Lines difference - Project=" + fromLines.length + " <> Saved=" + count);

        return count;
    } //	copyLinesFrom

    /**
     * Copy Phases/Tasks from other Project
     *
     * @param fromProject project
     * @return number of items copied
     */
    public int copyPhasesFrom(MProject fromProject) {
        if (isProcessed() || fromProject == null) return 0;
        int count = 0;
        int taskCount = 0, lineCount = 0;
        //	Get Phases
        MProjectPhase[] myPhases = getPhases();
        MProjectPhase[] fromPhases = fromProject.getPhases();
        //	Copy Phases
        for (int i = 0; i < fromPhases.length; i++) {
            //	Check if Phase already exists
            int C_Phase_ID = fromPhases[i].getPhaseId();
            boolean exists = false;
            if (C_Phase_ID == 0) exists = false;
            else {
                for (int ii = 0; ii < myPhases.length; ii++) {
                    if (myPhases[ii].getPhaseId() == C_Phase_ID) {
                        exists = true;
                        break;
                    }
                }
            }
            //	Phase exist
            if (exists) {
                if (log.isLoggable(Level.INFO))
                    log.info("Phase already exists here, ignored - " + fromPhases[i]);
            } else {
                MProjectPhase toPhase = new MProjectPhase(0);
                copyValues(fromPhases[i], toPhase, getClientId(), getOrgId());
                toPhase.setProjectId(getProjectId());
                toPhase.setOrderId(0);
                toPhase.setIsComplete(false);
                toPhase.saveEx();
                count++;
                taskCount += toPhase.copyTasksFrom(fromPhases[i]);
                // BF 3067850 - monhate
                lineCount += toPhase.copyLinesFrom(fromPhases[i]);
            }
        }
        if (fromPhases.length != count)
            log.warning("Count difference - Project=" + fromPhases.length + " <> Saved=" + count);

        return count + taskCount + lineCount;
    } //	copyPhasesFrom

    /**
     * Set Project Type and Category. If Service Project copy Projet Type Phase/Tasks
     *
     * @param type project type
     */
    public void setProjectType(MProjectType type) {
        if (type == null) return;
        setProjectTypeId(type.getProjectTypeId());
        setProjectCategory(type.getProjectCategory());
        if (PROJECTCATEGORY_ServiceChargeProject.equals(getProjectCategory())) copyPhasesFrom(type);
    } //	setProjectType

    /**
     * Copy Phases from Type
     *
     * @param type Project Type
     * @return count
     */
    public int copyPhasesFrom(MProjectType type) {
        //	create phases
        int count = 0;
        int taskCount = 0;
        MProjectTypePhase[] typePhases = type.getPhases();
        for (int i = 0; i < typePhases.length; i++) {
            MProjectPhase toPhase = new MProjectPhase(this, typePhases[i]);
            if (toPhase.save()) {
                count++;
                taskCount += toPhase.copyTasksFrom(typePhases[i]);
            }
        }
        if (log.isLoggable(Level.FINE)) log.fine("#" + count + "/" + taskCount + " - " + type);
        if (typePhases.length != count)
            log.log(Level.SEVERE, "Count difference - Type=" + typePhases.length + " <> Saved=" + count);
        return count;
    } //	copyPhasesFrom

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (getUserId() == -1) // 	Summary Project in Dimensions
            setUserId(0);

        //	Set Currency
        if (isValueChanged("M_PriceList_Version_ID") && getPriceListVersionId() != 0) {
            I_M_PriceList pl = MPriceList.get(getPriceListId());
            if (pl != null && pl.getId() != 0) setCurrencyId(pl.getCurrencyId());
        }

        return true;
    } //	beforeSave

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;
        if (newRecord) {
            insertAccounting("C_Project_Acct", "C_AcctSchema_Default", null);
            insertTree(MTree_Base.TREETYPE_Project);
        }
        if (newRecord || isValueChanged(COLUMNNAME_Value)) updateTree(MTree_Base.TREETYPE_Project);

        //	Value/Name change
        if (!newRecord && (isValueChanged("Value") || isValueChanged("Name")))
            MAccount.updateValueDescription("C_Project_ID=" + getProjectId());

        return success;
    } //	afterSave

    /**
     * After Delete
     *
     * @param success
     * @return deleted
     */
    protected boolean afterDelete(boolean success) {
        if (success) deleteTree(MTree_Base.TREETYPE_Project);
        return success;
    } //	afterDelete

} //	MProject
