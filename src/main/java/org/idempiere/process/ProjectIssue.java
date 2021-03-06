package org.idempiere.process;

import org.compiere.accounting.MStorageOnHand;
import org.compiere.accounting.MTimeExpense;
import org.compiere.accounting.MTimeExpenseLine;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_ProjectLine;
import org.compiere.model.I_M_InOutLine;
import org.compiere.order.MInOut;
import org.compiere.process.SvrProcess;
import org.compiere.production.MProject;
import org.compiere.production.MProjectIssue;
import org.compiere.production.MProjectLine;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

/**
 * Issue to Project.
 *
 * @author Jorg Janke
 * @version $Id: ProjectIssue.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class ProjectIssue extends SvrProcess {
    /**
     * Project - Mandatory Parameter
     */
    private int m_C_Project_ID = 0;
    /**
     * Receipt - Option 1
     */
    private int m_M_InOut_ID = 0;
    /**
     * Expenses - Option 2
     */
    private int m_S_TimeExpense_ID = 0;
    /**
     * Locator - Option 3,4
     */
    private int m_M_Locator_ID = 0;
    /**
     * Project Line - Option 3
     */
    private int m_C_ProjectLine_ID = 0;
    /**
     * Product - Option 4
     */
    private int m_M_Product_ID = 0;
    /**
     * Attribute - Option 4
     */
    @SuppressWarnings("unused")
    private int m_M_AttributeSetInstance_ID = 0;
    /**
     * Qty - Option 4
     */
    private BigDecimal m_MovementQty = null;
    /**
     * Date - Option
     */
    private Timestamp m_MovementDate = null;
    /**
     * Description - Option
     */
    private String m_Description = null;

    /**
     * The Project to be received
     */
    private MProject m_project = null;
    /**
     * The Project to be received
     */
    private MProjectIssue[] m_projectIssues = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            switch (name) {
                case "C_Project_ID":
                    m_C_Project_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "M_InOut_ID":
                    m_M_InOut_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "S_TimeExpense_ID":
                    m_S_TimeExpense_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "M_Locator_ID":
                    m_M_Locator_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "C_ProjectLine_ID":
                    m_C_ProjectLine_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "M_Product_ID":
                    m_M_Product_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "M_AttributeSetInstance_ID":
                    m_M_AttributeSetInstance_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                    break;
                case "MovementQty":
                    m_MovementQty = (BigDecimal) iProcessInfoParameter.getParameter();
                    break;
                case "MovementDate":
                    m_MovementDate = (Timestamp) iProcessInfoParameter.getParameter();
                    break;
                case "Description":
                    m_Description = (String) iProcessInfoParameter.getParameter();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }
        }
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        //	Check Parameter
        m_project = new MProject(m_C_Project_ID);
        if (!(MProject.PROJECTCATEGORY_WorkOrderJob.equals(m_project.getProjectCategory())
                || MProject.PROJECTCATEGORY_AssetProject.equals(m_project.getProjectCategory())))
            throw new IllegalArgumentException(
                    "Project not Work Order or Asset =" + m_project.getProjectCategory());
        if (log.isLoggable(Level.INFO)) log.info(m_project.toString());
        //
        if (m_M_InOut_ID != 0) return issueReceipt();
        if (m_S_TimeExpense_ID != 0) return issueExpense();
        if (m_M_Locator_ID == 0) throw new IllegalArgumentException("Locator missing");
        if (m_C_ProjectLine_ID != 0) return issueProjectLine();
        return issueInventory();
    } //	doIt

    /**
     * Issue Receipt
     *
     * @return Message (clear text)
     */
    private String issueReceipt() {
        MInOut inOut = new MInOut(m_M_InOut_ID);
        if (inOut.isSOTrx()
                || !inOut.isProcessed()
                || !(MInOut.DOCSTATUS_Completed.equals(inOut.getDocStatus())
                || MInOut.DOCSTATUS_Closed.equals(inOut.getDocStatus())))
            throw new IllegalArgumentException("Receipt not valid - " + inOut);
        if (log.isLoggable(Level.INFO)) log.info(inOut.toString());
        //	Set Project of Receipt
        if (inOut.getProjectId() == 0) {
            inOut.setProjectId(m_project.getProjectId());
            inOut.saveEx();
        } else if (inOut.getProjectId() != m_project.getProjectId())
            throw new IllegalArgumentException(
                    "Receipt for other Project (" + inOut.getProjectId() + ")");

        I_M_InOutLine[] inOutLines = inOut.getLines(false);
        int counter = 0;
        for (I_M_InOutLine inOutLine : inOutLines) {
            //	Need to have a Product
            if (inOutLine.getProductId() == 0) continue;
            //	Need to have Quantity
            if (inOutLine.getMovementQty() == null || inOutLine.getMovementQty().signum() == 0)
                continue;
            //	not issued yet
            if (projectIssueHasReceipt(inOutLine.getInOutLineId())) continue;
            //	Create Issue
            MProjectIssue pi = new MProjectIssue(m_project);
            pi.setMandatory(
                    inOutLine.getLocatorId(),
                    inOutLine.getProductId(),
                    inOutLine.getMovementQty());
            if (m_MovementDate != null) // 	default today
                pi.setMovementDate(m_MovementDate);
            if (m_Description != null && m_Description.length() > 0) pi.setDescription(m_Description);
            else if (inOutLine.getDescription() != null)
                pi.setDescription(inOutLine.getDescription());
            else if (inOut.getDescription() != null) pi.setDescription(inOut.getDescription());
            pi.setInOutLineId(inOutLine.getInOutLineId());
            pi.process();

            //	Find/Create Project Line
            I_C_ProjectLine pl = null;
            I_C_ProjectLine[] pls = m_project.getLines();
            for (I_C_ProjectLine i_c_projectLine : pls) {
                //	The Order we generated is the same as the Order of the receipt
                if (i_c_projectLine.getOrderPOId() == inOut.getOrderId()
                        && i_c_projectLine.getProductId() == inOutLine.getProductId()
                        && i_c_projectLine.getProjectIssueId() == 0) // 	not issued
                {
                    pl = i_c_projectLine;
                    break;
                }
            }
            if (pl == null) pl = new MProjectLine(m_project);
            pl.setMProjectIssue(pi); // 	setIssue
            pl.saveEx();
            addLog(pi.getLine(), pi.getMovementDate(), pi.getMovementQty(), null);
            counter++;
        } //	all InOutLines
        return "@Created@ " + counter;
    } //	issueReceipt

    /**
     * Issue Expense Report
     *
     * @return Message (clear text)
     */
    private String issueExpense() {
        //	Get Expense Report
        MTimeExpense expense = new MTimeExpense(m_S_TimeExpense_ID);
        if (!expense.isProcessed())
            throw new IllegalArgumentException("Time+Expense not processed - " + expense);

        //	for all expense lines
        MTimeExpenseLine[] expenseLines = expense.getLines(false);
        int counter = 0;
        for (MTimeExpenseLine expenseLine : expenseLines) {
            //	Need to have a Product
            if (expenseLine.getProductId() == 0) continue;
            //	Need to have Quantity
            if (expenseLine.getQty() == null || expenseLine.getQty().signum() == 0) continue;
            //	Need to the same project
            if (expenseLine.getProjectId() != m_project.getProjectId()) continue;
            //	not issued yet
            if (projectIssueHasExpense(expenseLine.getTimeExpenseLineId())) continue;

            //	Find Location
            int M_Locator_ID;
            //	MProduct product = new MProduct (expenseLines[i].getProductId());
            //	if (product.isStocked())
            M_Locator_ID =
                    MStorageOnHand.getLocatorId(
                            expense.getWarehouseId(),
                            expenseLine.getProductId(),
                            0, //	no ASI
                            expenseLine.getQty()
                    );
            if (M_Locator_ID == 0) // 	Service/Expense - get default (and fallback)
                M_Locator_ID = expense.getLocatorId();

            //	Create Issue
            MProjectIssue pi = new MProjectIssue(m_project);
            pi.setMandatory(M_Locator_ID, expenseLine.getProductId(), expenseLine.getQty());
            if (m_MovementDate != null) // 	default today
                pi.setMovementDate(m_MovementDate);
            if (m_Description != null && m_Description.length() > 0) pi.setDescription(m_Description);
            else if (expenseLine.getDescription() != null)
                pi.setDescription(expenseLine.getDescription());
            pi.setTimeExpenseLineId(expenseLine.getTimeExpenseLineId());
            pi.process();
            //	Find/Create Project Line
            MProjectLine pl = new MProjectLine(m_project);
            pl.setMProjectIssue(pi); // 	setIssue
            pl.saveEx();
            addLog(pi.getLine(), pi.getMovementDate(), pi.getMovementQty(), null);
            counter++;
        } //	allExpenseLines

        return "@Created@ " + counter;
    } //	issueExpense

    /**
     * Issue Project Line
     *
     * @return Message (clear text)
     */
    private String issueProjectLine() {
        MProjectLine pl = new MProjectLine(m_C_ProjectLine_ID);
        if (pl.getProductId() == 0) throw new IllegalArgumentException("Projet Line has no Product");
        if (pl.getProjectIssueId() != 0)
            throw new IllegalArgumentException("Projet Line already been issued");
        if (m_M_Locator_ID == 0) throw new IllegalArgumentException("No Locator");
        //	Set to Qty 1
        if (pl.getPlannedQty() == null || pl.getPlannedQty().signum() == 0) pl.setPlannedQty(Env.ONE);
        //
        MProjectIssue pi = new MProjectIssue(m_project);
        pi.setMandatory(m_M_Locator_ID, pl.getProductId(), pl.getPlannedQty());
        if (m_MovementDate != null) // 	default today
            pi.setMovementDate(m_MovementDate);
        if (m_Description != null && m_Description.length() > 0) pi.setDescription(m_Description);
        else if (pl.getDescription() != null) pi.setDescription(pl.getDescription());
        pi.process();

        //	Update Line
        pl.setMProjectIssue(pi);
        pl.saveEx();
        addLog(pi.getLine(), pi.getMovementDate(), pi.getMovementQty(), null);
        return "@Created@ 1";
    } //	issueProjectLine

    /**
     * Issue from Inventory
     *
     * @return Message (clear text)
     */
    private String issueInventory() {
        if (m_M_Locator_ID == 0) throw new IllegalArgumentException("No Locator");
        if (m_M_Product_ID == 0) throw new IllegalArgumentException("No Product");
        //	Set to Qty 1
        if (m_MovementQty == null || m_MovementQty.signum() == 0) m_MovementQty = Env.ONE;
        //
        MProjectIssue pi = new MProjectIssue(m_project);
        pi.setMandatory(m_M_Locator_ID, m_M_Product_ID, m_MovementQty);
        if (m_MovementDate != null) // 	default today
            pi.setMovementDate(m_MovementDate);
        if (m_Description != null && m_Description.length() > 0) pi.setDescription(m_Description);
        pi.process();

        //	Create Project Line
        MProjectLine pl = new MProjectLine(m_project);
        pl.setMProjectIssue(pi);
        pl.saveEx();
        addLog(pi.getLine(), pi.getMovementDate(), pi.getMovementQty(), null);
        return "@Created@ 1";
    } //	issueInventory

    /**
     * Check if Project Issue already has Expense
     *
     * @param S_TimeExpenseLine_ID line
     * @return true if exists
     */
    private boolean projectIssueHasExpense(int S_TimeExpenseLine_ID) {
        if (m_projectIssues == null) m_projectIssues = m_project.getIssues();
        for (MProjectIssue m_projectIssue : m_projectIssues) {
            if (m_projectIssue.getTimeExpenseLineId() == S_TimeExpenseLine_ID) return true;
        }
        return false;
    } //	projectIssueHasExpense

    /**
     * Check if Project Issue already has Receipt
     *
     * @param M_InOutLine_ID line
     * @return true if exists
     */
    private boolean projectIssueHasReceipt(int M_InOutLine_ID) {
        if (m_projectIssues == null) m_projectIssues = m_project.getIssues();
        for (MProjectIssue m_projectIssue : m_projectIssues) {
            if (m_projectIssue.getInOutLineId() == M_InOutLine_ID) return true;
        }
        return false;
    } //	projectIssueHasReceipt
} //	ProjectIssue
