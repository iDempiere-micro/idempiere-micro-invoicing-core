package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_ProjectLine;
import org.compiere.orm.PO;
import org.compiere.orm.Query;
import org.idempiere.common.util.Env;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Project Phase Model
 *
 * @author Jorg Janke
 * @version $Id: MProjectPhase.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MProjectPhase extends X_C_ProjectPhase {
    /**
     *
     */
    private static final long serialVersionUID = 5824045445920353065L;

    /**
     * Standard Constructor
     *
     * @param ctx               context
     * @param C_ProjectPhase_ID id
     */
    public MProjectPhase(Properties ctx, int C_ProjectPhase_ID) {
        super(ctx, C_ProjectPhase_ID);
        if (C_ProjectPhase_ID == 0) {
            //	setProjectPhaseId (0);	//	PK
            //	setProjectId (0);		//	Parent
            //	setPhaseId (0);			//	FK
            setCommittedAmt(Env.ZERO);
            setIsCommitCeiling(false);
            setIsComplete(false);
            setSeqNo(0);
            //	setName (null);
            setQty(Env.ZERO);
        }
    } //	MProjectPhase

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MProjectPhase(Properties ctx, Row row) {
        super(ctx, row);
    } //	MProjectPhase

    /**
     * Parent Constructor
     *
     * @param project parent
     */
    public MProjectPhase(MProject project) {
        this(project.getCtx(), 0);
        setClientOrg(project);
        setProjectId(project.getProjectId());
    } //	MProjectPhase

    /**
     * Copy Constructor
     *
     * @param project parent
     * @param phase   copy
     */
    public MProjectPhase(MProject project, MProjectTypePhase phase) {
        this(project);
        //
        setPhaseId(phase.getPhaseId()); // 	FK
        setName(phase.getName());
        setSeqNo(phase.getSeqNo());
        setDescription(phase.getDescription());
        setHelp(phase.getHelp());
        if (phase.getProductId() != 0) setProductId(phase.getProductId());
        setQty(phase.getStandardQty());
    } //	MProjectPhase

    /**
     * Get Project Phase Tasks.
     *
     * @return Array of tasks
     */
    public MProjectTask[] getTasks() {
        return MBaseProjectPhaseKt.getProjectPhaseTasks(getCtx(), getProjectPhaseId());
    } //	getTasks

    /**
     * Copy Lines from other Phase BF 3067850 - monhate
     *
     * @param fromPhase from phase
     * @return number of tasks copied
     */
    public int copyLinesFrom(MProjectPhase fromPhase) {
        if (fromPhase == null) return 0;
        int count = 0;
        //
        MProjectLine[] fromLines = fromPhase.getLines();
        //	Copy Project Lines
        for (int i = 0; i < fromLines.length; i++) {
            if (fromLines[i].getProjectTaskId() != 0) continue;
            MProjectLine toLine = new MProjectLine(getCtx(), 0);
            PO.copyValues(fromLines[i], toLine, getClientId(), getOrgId());
            toLine.setProjectId(getProjectId());
            toLine.setProjectPhaseId(getProjectPhaseId());
            toLine.saveEx();
            count++;
        }
        if (fromLines.length != count)
            log.warning("Count difference - ProjectLine=" + fromLines.length + " <> Saved=" + count);

        return count;
    }

    /**
     * Copy Tasks from other Phase BF 3067850 - monhate
     *
     * @param fromPhase from phase
     * @return number of tasks copied
     */
    public int copyTasksFrom(MProjectPhase fromPhase) {
        if (fromPhase == null) return 0;
        int count = 0, countLine = 0;
        //
        MProjectTask[] myTasks = getTasks();
        MProjectTask[] fromTasks = fromPhase.getTasks();
        //	Copy Project Tasks
        for (int i = 0; i < fromTasks.length; i++) {
            //	Check if Task already exists
            int C_Task_ID = fromTasks[i].getTaskId();
            boolean exists = false;
            if (C_Task_ID == 0) exists = false;
            else {
                for (int ii = 0; ii < myTasks.length; ii++) {
                    if (myTasks[ii].getTaskId() == C_Task_ID) {
                        exists = true;
                        break;
                    }
                }
            }
            //	Phase exist
            if (exists) {
                if (log.isLoggable(Level.INFO))
                    log.info("Task already exists here, ignored - " + fromTasks[i]);
            } else {
                MProjectTask toTask = new MProjectTask(getCtx(), 0);
                PO.copyValues(fromTasks[i], toTask, getClientId(), getOrgId());
                toTask.setProjectPhaseId(getProjectPhaseId());
                toTask.saveEx();
                count++;
                // BF 3067850 - monhate
                countLine += toTask.copyLinesFrom(fromTasks[i]);
            }
        }
        if (fromTasks.length != count)
            log.warning("Count difference - ProjectPhase=" + fromTasks.length + " <> Saved=" + count);

        return count + countLine;
    } //	copyTasksFrom

    /**
     * Copy Tasks from other Phase
     *
     * @param fromPhase from phase
     * @return number of tasks copied
     */
    public int copyTasksFrom(MProjectTypePhase fromPhase) {
        if (fromPhase == null) return 0;
        int count = 0;
        //	Copy Type Tasks
        MProjectTypeTask[] fromTasks = fromPhase.getTasks();
        for (int i = 0; i < fromTasks.length; i++) {
            MProjectTask toTask = new MProjectTask(this, fromTasks[i]);
            if (toTask.save()) count++;
        }
        if (log.isLoggable(Level.FINE)) log.fine("#" + count + " - " + fromPhase);
        if (fromTasks.length != count)
            log.log(
                    Level.SEVERE, "Count difference - TypePhase=" + fromTasks.length + " <> Saved=" + count);

        return count;
    } //	copyTasksFrom

    /**
     * ************************************************************************ Get Project Lines BF
     * 3067850 - monhate
     *
     * @return Array of lines
     */
    public MProjectLine[] getLines() {
        final String whereClause = "C_Project_ID=? and C_ProjectPhase_ID=?";
        List<MProjectLine> list =
                new Query(getCtx(), I_C_ProjectLine.Table_Name, whereClause)
                        .setParameters(getProjectId(), getProjectPhaseId())
                        .setOrderBy("Line")
                        .list();
        //
        MProjectLine[] retValue = new MProjectLine[list.size()];
        list.toArray(retValue);
        return retValue;
    }

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MProjectPhase[");
        sb.append(getId()).append("-").append(getSeqNo()).append("-").append(getName()).append("]");
        return sb.toString();
    } //	toString
} //	MProjectPhase
