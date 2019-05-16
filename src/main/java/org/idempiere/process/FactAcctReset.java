package org.idempiere.process;

import org.compiere.accounting.MAllocationHdr;
import org.compiere.accounting.MBankStatement;
import org.compiere.accounting.MCash;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MJournal;
import org.compiere.accounting.MMatchInv;
import org.compiere.accounting.MMatchPO;
import org.compiere.accounting.MOrder;
import org.compiere.accounting.MPayment;
import org.compiere.accounting.MPeriodControl;
import org.compiere.accounting.MRequisition;
import org.compiere.invoicing.MInventory;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_M_Production;
import org.compiere.order.MInOut;
import org.compiere.orm.TimeUtil;
import org.compiere.process.SvrProcess;
import org.compiere.production.MProjectIssue;
import org.eevolution.model.I_HR_Process;
import org.eevolution.model.I_PP_Cost_Collector;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.convertDate;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Accounting Fact Reset
 *
 * @author Jorg Janke
 * @version $Id: FactAcctReset.java,v 1.5 2006/09/21 21:05:02 jjanke Exp $
 */
public class FactAcctReset extends SvrProcess {
    /**
     * Client Parameter
     */
    private int p_AD_Client_ID = 0;
    /**
     * Table Parameter
     */
    private int p_AD_Table_ID = 0;
    /**
     * Delete Parameter
     */
    private boolean p_DeletePosting = false;

    private int m_countReset = 0;
    private int m_countDelete = 0;
    private Timestamp p_DateAcct_From = null;
    private Timestamp p_DateAcct_To = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            if (iProcessInfoParameter.getParameter() != null || iProcessInfoParameter.getParameterTo() != null) {
                if (name.equals("AD_Client_ID"))
                    p_AD_Client_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                else if (name.equals("AD_Table_ID"))
                    p_AD_Table_ID = ((BigDecimal) iProcessInfoParameter.getParameter()).intValue();
                else if (name.equals("DeletePosting"))
                    p_DeletePosting = "Y".equals(iProcessInfoParameter.getParameter());
                else if (name.equals("DateAcct")) {
                    p_DateAcct_From = (Timestamp) iProcessInfoParameter.getParameter();
                    p_DateAcct_To = (Timestamp) iProcessInfoParameter.getParameterTo();
                } else log.log(Level.SEVERE, "Unknown Parameter: " + name);
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
        if (log.isLoggable(Level.INFO))
            log.info(
                    "AD_Client_ID="
                            + p_AD_Client_ID
                            + ", AD_Table_ID="
                            + p_AD_Table_ID
                            + ", DeletePosting="
                            + p_DeletePosting);
        //	List of Tables with Accounting Consequences
        String sql = "SELECT AD_Table_ID, TableName " + "FROM AD_Table t " + "WHERE t.IsView='N'";
        if (p_AD_Table_ID > 0) sql += " AND t.AD_Table_ID=" + p_AD_Table_ID;
        sql +=
                " AND EXISTS (SELECT * FROM AD_Column c "
                        + "WHERE t.AD_Table_ID=c.AD_Table_ID AND c.ColumnName='Posted' AND c.IsActive='Y')";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int AD_Table_ID = rs.getInt(1);
                String TableName = rs.getString(2);
                if (p_DeletePosting) delete(TableName, AD_Table_ID);
                else reset(TableName);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        } finally {

            rs = null;
            pstmt = null;
        }
        //
        return "@Updated@ = " + m_countReset + ", @Deleted@ = " + m_countDelete;
    } //	doIt

    /**
     * Reset Accounting Table and update count
     *
     * @param TableName table
     */
    private void reset(String TableName) {
        String sql =
                "UPDATE "
                        + TableName
                        + " SET Processing='N' WHERE AD_Client_ID="
                        + p_AD_Client_ID
                        + " AND (Processing<>'N' OR Processing IS NULL)";
        int unlocked = executeUpdate(sql);
        //
        sql =
                "UPDATE "
                        + TableName
                        + " SET Posted='N' WHERE AD_Client_ID="
                        + p_AD_Client_ID
                        + " AND (Posted NOT IN ('Y','N') OR Posted IS NULL) AND Processed='Y'";
        int invalid = executeUpdate(sql);
        //
        if (unlocked + invalid != 0)
            if (log.isLoggable(Level.FINE))
                log.fine(TableName + " - Unlocked=" + unlocked + " - Invalid=" + invalid);
        m_countReset += unlocked + invalid;
    } //	reset

    /**
     * Delete Accounting Table where period status is open and update count.
     *
     * @param TableName   table name
     * @param AD_Table_ID table
     */
    private void delete(String TableName, int AD_Table_ID) {
        Timestamp today = TimeUtil.trunc(new Timestamp(System.currentTimeMillis()), TimeUtil.TRUNC_DAY);

        AccountingSchema as = MClientKt.getClientWithAccounting(getClientId()).getAcctSchema();
        boolean autoPeriod = as != null && as.isAutoPeriodControl();
        if (autoPeriod) {
            Timestamp temp = TimeUtil.addDays(today, -as.getPeriodOpenHistory());
            if (p_DateAcct_From == null || p_DateAcct_From.before(temp)) {
                p_DateAcct_From = temp;
                if (log.isLoggable(Level.INFO)) log.info("DateAcct From set to: " + p_DateAcct_From);
            }
            temp = TimeUtil.addDays(today, as.getPeriodOpenFuture());
            if (p_DateAcct_To == null || p_DateAcct_To.after(temp)) {
                p_DateAcct_To = temp;
                if (log.isLoggable(Level.INFO)) log.info("DateAcct To set to: " + p_DateAcct_To);
            }
        }

        reset(TableName);
        m_countReset = 0;
        //
        String docBaseType = null;
        if (AD_Table_ID == MInvoice.Table_ID)
            docBaseType =
                    "IN ('"
                            + MPeriodControl.DOCBASETYPE_APInvoice
                            + "','"
                            + MPeriodControl.DOCBASETYPE_APCreditMemo
                            + "','"
                            + MPeriodControl.DOCBASETYPE_ARInvoice
                            + "','"
                            + MPeriodControl.DOCBASETYPE_ARCreditMemo
                            + "','"
                            + MPeriodControl.DOCBASETYPE_ARProFormaInvoice
                            + "')";
        else if (AD_Table_ID == MInOut.Table_ID)
            docBaseType =
                    "IN ('"
                            + MPeriodControl.DOCBASETYPE_MaterialDelivery
                            + "','"
                            + MPeriodControl.DOCBASETYPE_MaterialReceipt
                            + "')";
        else if (AD_Table_ID == MPayment.Table_ID)
            docBaseType =
                    "IN ('"
                            + MPeriodControl.DOCBASETYPE_APPayment
                            + "','"
                            + MPeriodControl.DOCBASETYPE_ARReceipt
                            + "')";
        else if (AD_Table_ID == MOrder.Table_ID)
            docBaseType =
                    "IN ('"
                            + MPeriodControl.DOCBASETYPE_SalesOrder
                            + "','"
                            + MPeriodControl.DOCBASETYPE_PurchaseOrder
                            + "')";
        else if (AD_Table_ID == MProjectIssue.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_ProjectIssue + "'";
        else if (AD_Table_ID == MBankStatement.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_BankStatement + "'";
        else if (AD_Table_ID == MCash.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_CashJournal + "'";
        else if (AD_Table_ID == MAllocationHdr.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_PaymentAllocation + "'";
        else if (AD_Table_ID == MJournal.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_GLJournal + "'";
            //	else if (AD_Table_ID == M.Table_ID)
            //		docBaseType = "= '" + MPeriodControl.DOCBASETYPE_GLDocument + "'";
        else if (AD_Table_ID == MMovement.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MaterialMovement + "'";
        else if (AD_Table_ID == MRequisition.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_PurchaseRequisition + "'";
        else if (AD_Table_ID == MInventory.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MaterialPhysicalInventory + "'";
        else if (AD_Table_ID == I_M_Production.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MaterialProduction + "'";
        else if (AD_Table_ID == MMatchInv.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MatchInvoice + "'";
        else if (AD_Table_ID == MMatchPO.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_MatchPO + "'";
        else if (AD_Table_ID == X_PP_Order.Table_ID)
            docBaseType =
                    "IN ('"
                            + MPeriodControl.DOCBASETYPE_ManufacturingOrder
                            + "','"
                            + MPeriodControl.DOCBASETYPE_MaintenanceOrder
                            + "','"
                            + MPeriodControl.DOCBASETYPE_QualityOrder
                            + "')";
        else if (AD_Table_ID == X_DD_Order.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_DistributionOrder + "'";
        else if (AD_Table_ID == I_HR_Process.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_Payroll + "'";
        else if (AD_Table_ID == I_PP_Cost_Collector.Table_ID)
            docBaseType = "= '" + MPeriodControl.DOCBASETYPE_ManufacturingCostCollector + "'";
        //
        if (docBaseType == null) {
            String s = TableName + ": Unknown DocBaseType";
            log.severe(s);
            addLog(s);
            docBaseType = "";
            return;
        } else docBaseType = " AND pc.DocBaseType " + docBaseType;

        //	Doc
        String sql1 =
                "UPDATE "
                        + TableName
                        + " SET Posted='N', Processing='N' "
                        + "WHERE AD_Client_ID="
                        + p_AD_Client_ID
                        + " AND (Posted<>'N' OR Posted IS NULL OR Processing<>'N' OR Processing IS NULL)"
                        + " AND EXISTS (SELECT 1 FROM C_PeriodControl pc"
                        + " INNER JOIN Fact_Acct fact ON (fact.C_Period_ID=pc.C_Period_ID) "
                        + " WHERE fact.AD_Table_ID="
                        + AD_Table_ID
                        + " AND fact.Record_ID="
                        + TableName
                        + "."
                        + TableName
                        + "_ID";
        if (!autoPeriod) sql1 += " AND pc.PeriodStatus = 'O'" + docBaseType;
        if (p_DateAcct_From != null)
            sql1 += " AND TRUNC(fact.DateAcct) >= " + convertDate(p_DateAcct_From);
        if (p_DateAcct_To != null) sql1 += " AND TRUNC(fact.DateAcct) <= " + convertDate(p_DateAcct_To);
        sql1 += ")";

        if (log.isLoggable(Level.FINE)) log.log(Level.FINE, sql1);

        int reset = executeUpdate(sql1);
        //	Fact
        String sql2 =
                "DELETE Fact_Acct "
                        + "WHERE AD_Client_ID="
                        + p_AD_Client_ID
                        + " AND AD_Table_ID="
                        + AD_Table_ID;
        if (!autoPeriod)
            sql2 +=
                    " AND EXISTS (SELECT 1 FROM C_PeriodControl pc "
                            + "WHERE pc.PeriodStatus = 'O'"
                            + docBaseType
                            + " AND Fact_Acct.C_Period_ID=pc.C_Period_ID)";
        else
            sql2 +=
                    " AND EXISTS (SELECT 1 FROM C_PeriodControl pc "
                            + "WHERE Fact_Acct.C_Period_ID=pc.C_Period_ID)";
        if (p_DateAcct_From != null)
            sql2 += " AND TRUNC(Fact_Acct.DateAcct) >= " + convertDate(p_DateAcct_From);
        if (p_DateAcct_To != null)
            sql2 += " AND TRUNC(Fact_Acct.DateAcct) <= " + convertDate(p_DateAcct_To);

        if (log.isLoggable(Level.FINE)) log.log(Level.FINE, sql2);

        int deleted = executeUpdate(sql2);
        //
        if (log.isLoggable(Level.INFO))
            log.info(TableName + "(" + AD_Table_ID + ") - Reset=" + reset + " - Deleted=" + deleted);
        String s = TableName + " - Reset=" + reset + " - Deleted=" + deleted;
        addLog(s);
        //
        m_countReset += reset;
        m_countDelete += deleted;
    } //	delete
} //	FactAcctReset
