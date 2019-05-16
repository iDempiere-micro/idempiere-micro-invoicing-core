package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.crm.MUserKt;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.User;
import org.compiere.orm.MDocType;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.product.MPriceList;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.prepareStatement;

public class MTimeExpense extends X_S_TimeExpense implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = 1567303438502090279L;
    /**
     * Default Locator
     */
    private int m_M_Locator_ID = 0;
    /**
     * Lines
     */
    private MTimeExpenseLine[] m_lines = null;
    /**
     * Cached User
     */
    private int m_AD_User_ID = 0;
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;

    /**
     * Default Constructor
     *
     * @param ctx              context
     * @param S_TimeExpense_ID id
     */
    public MTimeExpense(int S_TimeExpense_ID) {
        super(S_TimeExpense_ID);
        if (S_TimeExpense_ID == 0) {
            //	setBusinessPartnerId (0);
            setDateReport(new Timestamp(System.currentTimeMillis()));
            //	setDocumentNo (null);
            setIsApproved(false);
            //	setPriceListId (0);
            //	setWarehouseId (0);
            super.setProcessed(false);
            setProcessing(false);
        }
    } //	MTimeExpense

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MTimeExpense(Row row) {
        super(row);
    } //	MTimeExpense

    /**
     * Get Lines
     *
     * @param requery true requeries
     * @return array of lines
     */
    public MTimeExpenseLine[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        //
        m_lines = MBaseTimeExpenseKt.getBaseExpenseLines(getTimeExpenseId(), getCurrencyId());
        return m_lines;
    } //	getLines

    /**
     * Get Default Locator (from Warehouse)
     *
     * @return locator
     */
    public int getLocatorId() {
        if (m_M_Locator_ID != 0) return m_M_Locator_ID;
        //
        String sql =
                "SELECT M_Locator_ID FROM M_Locator "
                        + "WHERE M_Warehouse_ID=? AND IsActive='Y' ORDER BY IsDefault DESC, Created";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getWarehouseId());
            rs = pstmt.executeQuery();
            if (rs.next()) m_M_Locator_ID = rs.getInt(1);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "getLocatorId", ex);
        } finally {

            rs = null;
            pstmt = null;
        }
        //
        return m_M_Locator_ID;
    } //	getLocatorId

    /**
     * Set Processed. Propergate to Lines/Taxes
     *
     * @param processed processed
     */
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        if (getId() == 0) return;
        String sql =
                "UPDATE S_TimeExpenseLine SET Processed='"
                        + (processed ? "Y" : "N")
                        + "' WHERE S_TimeExpense_ID="
                        + getTimeExpenseId();
        int noLine = executeUpdate(sql);
        m_lines = null;
        if (log.isLoggable(Level.FINE)) log.fine(processed + " - Lines=" + noLine);
    } //	setProcessed

    /**
     * Get Document Info
     *
     * @return document info
     */
    @NotNull
    public String getDocumentInfo() {
        return MsgKt.getElementTranslation("S_TimeExpense_ID") + " " + getDocumentNo();
    } //	getDocumentInfo

    /**
     * ************************************************************************ Process document
     *
     * @param processAction document action
     * @return true if performed
     */
    public boolean processIt(@NotNull String processAction) {
        m_processMsg = null;
        DocumentEngine engine = new DocumentEngine(this, getDocStatus());
        return engine.processIt(processAction, getDocAction());
    } //	processIt

    /**
     * Unlock Document.
     *
     * @return true if success
     */
    public boolean unlockIt() {
        if (log.isLoggable(Level.INFO)) log.info("unlockIt - " + toString());
        setProcessing(false);
        return true;
    } //	unlockIt

    /**
     * Invalidate Document
     *
     * @return true if success
     */
    public boolean invalidateIt() {
        if (log.isLoggable(Level.INFO)) log.info("invalidateIt - " + toString());
        setDocAction(DOCACTION_Prepare);
        return true;
    } //	invalidateIt

    /**
     * Prepare Document
     *
     * @return new status (In Progress or Invalid)
     */
    @NotNull
    public String prepareIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        //	Std Period open? - AP (Reimbursement) Invoice
        if (!MPeriod.isOpen(
                getDateReport(), MDocType.DOCBASETYPE_APInvoice, getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        MTimeExpenseLine[] lines = getLines(false);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        //	Add up Amounts
        BigDecimal amt = Env.ZERO;
        for (int i = 0; i < lines.length; i++) {
            MTimeExpenseLine line = lines[i];
            amt = amt.add(line.getApprovalAmt());
        }
        setApprovalAmt(amt);

        //	Invoiced but no BP
        for (int i = 0; i < lines.length; i++) {
            MTimeExpenseLine line = lines[i];
            if (line.isInvoiced() && line.getBusinessPartnerId() == 0) {
                m_processMsg = "@Line@ " + line.getLine() + ": Invoiced, but no Business Partner";
                return DocAction.Companion.getSTATUS_Invalid();
            }
        }

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        if (!DOCACTION_Complete.equals(getDocAction())) setDocAction(DOCACTION_Complete);
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

    /**
     * Approve Document
     *
     * @return true if success
     */
    public boolean approveIt() {
        if (log.isLoggable(Level.INFO)) log.info("approveIt - " + toString());
        setIsApproved(true);
        return true;
    } //	approveIt

    /**
     * Reject Approval
     *
     * @return true if success
     */
    public boolean rejectIt() {
        if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + toString());
        setIsApproved(false);
        return true;
    } //	rejectIt

    /**
     * Complete Document
     *
     * @return new status (Complete, In Progress, Invalid, Waiting ..)
     */
    @NotNull
    public CompleteActionResult completeIt() {
        //	Re-Check
        if (!m_justPrepared) {
            String status = prepareIt();
            m_justPrepared = false;
            if (!DocAction.Companion.getSTATUS_InProgress().equals(status))
                return new CompleteActionResult(status);
        }

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
        if (m_processMsg != null)
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());

        //	Implicit Approval
        if (!isApproved()) approveIt();
        if (log.isLoggable(Level.INFO)) log.info("completeIt - " + toString());

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        //
        setProcessed(true);
        setDocAction(DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Void Document. Same as Close.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info("voidIt - " + toString());

        // Before Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
        if (m_processMsg != null) return false;

        if (!closeIt()) return false;

        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        return m_processMsg == null;
    } //	voidIt

    /**
     * Close Document. Cancel not delivered Qunatities
     *
     * @return true if success
     */
    public boolean closeIt() {
        if (log.isLoggable(Level.INFO)) log.info("closeIt - " + toString());
        // Before Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_CLOSE);
        if (m_processMsg != null) return false;
        // After Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE);
        return m_processMsg == null;//	Close Not delivered Qty
        //	setDocAction(DOCACTION_None);
    } //	closeIt

    /**
     * Reverse Correction
     *
     * @return false
     */
    public boolean reverseCorrectIt() {
        if (log.isLoggable(Level.INFO)) log.info("reverseCorrectIt - " + toString());
        // Before reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSECORRECT);
        if (m_processMsg != null) return false;

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        return false;
    } //	reverseCorrectionIt

    /**
     * Reverse Accrual - none
     *
     * @return false
     */
    public boolean reverseAccrualIt() {
        if (log.isLoggable(Level.INFO)) log.info("reverseAccrualIt - " + toString());
        // Before reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        // After reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        return false;
    } //	reverseAccrualIt

    /**
     * Re-activate
     *
     * @return true if success
     */
    public boolean reActivateIt() {
        if (log.isLoggable(Level.INFO)) log.info("reActivateIt - " + toString());
        // Before reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_REACTIVATE);
        if (m_processMsg != null) return false;

        // After reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_REACTIVATE);
        if (m_processMsg != null) return false;

        //	setProcessed(false);
        return false;
    } //	reActivateIt

    /**
     * *********************************************************************** Get Summary
     *
     * @return Summary of Document
     */
    @NotNull
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDocumentNo());
        //	: Total Lines = 123.00 (#1)
        sb.append(": ")
                .append(MsgKt.translate("ApprovalAmt"))
                .append("=")
                .append(getApprovalAmt())
                .append(" (#")
                .append(getLines(false).length)
                .append(")");
        //	 - Description
        if (getDescription() != null && getDescription().length() > 0)
            sb.append(" - ").append(getDescription());
        return sb.toString();
    } //	getSummary

    /**
     * Get Process Message
     *
     * @return clear text error message
     */
    @NotNull
    public String getProcessMsg() {
        return m_processMsg;
    } //	getProcessMsg

    /**
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    public int getDocumentUserId() {
        if (m_AD_User_ID != 0) return m_AD_User_ID;
        if (getBusinessPartnerId() != 0) {
            User[] users = MUserKt.getBusinessPartnerUsers(getBusinessPartnerId());
            if (users.length > 0) {
                m_AD_User_ID = users[0].getUserId();
                return m_AD_User_ID;
            }
        }
        return getCreatedBy();
    } //	getDoc_User_ID

    /**
     * Get Document Currency
     *
     * @return C_Currency_ID
     */
    public int getCurrencyId() {
        I_M_PriceList pl = MPriceList.get(getPriceListId());
        return pl.getCurrencyId();
    } //	getCurrencyId

    /**
     * Document Status is Complete or Closed
     *
     * @return true if CO, CL or RE
     */
    public boolean isComplete() {
        String ds = getDocStatus();
        return DOCSTATUS_Completed.equals(ds)
                || DOCSTATUS_Closed.equals(ds)
                || DOCSTATUS_Reversed.equals(ds);
    } //	isComplete

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(@NotNull String DocStatus) {

        setValue(COLUMNNAME_DocStatus, DocStatus);
    }
} //	MTimeExpense
