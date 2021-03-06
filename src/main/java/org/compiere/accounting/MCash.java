package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.docengine.DocumentEngine;
import org.compiere.invoicing.MConversionRate;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_AllocationHdr;
import org.compiere.model.I_C_Cash;
import org.compiere.model.I_C_CashBook;
import org.compiere.model.I_C_CashLine;
import org.compiere.model.I_C_Invoice;
import org.compiere.orm.MDocType;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.Query;
import org.compiere.orm.TimeUtil;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.util.DisplayType;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;

/**
 * Cash Journal Model
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 1866214 ]
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * <li>BF [ 1831997 ] Cash journal allocation reversed
 * <li>BF [ 1894524 ] Pay an reversed invoice
 * <li>BF [ 1899477 ] MCash.getLines should return only active lines
 * <li>BF [ 2588326 ] Cash Lines are not correctly updated on voiding
 * @version $Id: MCash.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 * @see http://sourceforge.net/tracker/index.php?func=detail&aid=1866214&group_id=176962&atid=879335
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 */
public class MCash extends X_C_Cash implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -1221144207418749593L;
    /**
     * Static Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MCash.class);
    /**
     * Lines
     */
    private I_C_CashLine[] m_lines = null;
    /**
     * CashBook
     */
    private I_C_CashBook m_book = null;
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_Cash_ID id
     */
    public MCash(int C_Cash_ID) {
        super(C_Cash_ID);
        if (C_Cash_ID == 0) {
            //	setCashBookId (0);		//	FK
            setBeginningBalance(Env.ZERO);
            setEndingBalance(Env.ZERO);
            setStatementDifference(Env.ZERO);
            setDocAction(X_C_Cash.DOCACTION_Complete);
            setDocStatus(X_C_Cash.DOCSTATUS_Drafted);
            //
            Timestamp today = TimeUtil.getDay(System.currentTimeMillis());
            setStatementDate(today); // @#Date@
            setDateAcct(today); // @#Date@

            MOrg org = MOrgKt.getOrg(getOrgId());
            String orgSearchKey = org.isSearchKeyNotNull() ? org.getSearchKey() : "unknown";

            String name = DisplayType.getDateFormat(DisplayType.Date).format(today) +
                    " " + orgSearchKey;
            setName(name);
            setIsApproved(false);
            setPosted(false); // N
            setProcessed(false);
        }
    } //	MCash

    /**
     * Load Constructor
     */
    public MCash(Row row) {
        super(row);
    } //	MCash

    /**
     * Parent Constructor
     *
     * @param cb    cash book
     * @param today date - if null today
     */
    public MCash(I_C_CashBook cb, Timestamp today) {
        this(0);
        setClientOrg(cb);
        setCashBookId(cb.getCashBookId());
        if (today != null) {
            setStatementDate(today);
            setDateAcct(today);
            String name = DisplayType.getDateFormat(DisplayType.Date).format(today) +
                    " " +
                    cb.getName();
            setName(name);
        }
        m_book = cb;
    } //	MCash

    /**
     * Get Cash Journal for currency, org and date
     *
     * @param C_Currency_ID currency
     * @param AD_Org_ID     org
     * @param dateAcct      date
     * @return cash
     */
    public static I_C_Cash get(
            int AD_Org_ID, Timestamp dateAcct, int C_Currency_ID) {
        //	Existing Journal
        final String whereClause =
                "C_Cash.AD_Org_ID=?" //	#1
                        + " AND TRUNC(C_Cash.StatementDate)=?" //	#2
                        + " AND C_Cash.Processed='N'"
                        + " AND EXISTS (SELECT * FROM C_CashBook cb "
                        + "WHERE C_Cash.C_CashBook_ID=cb.C_CashBook_ID AND cb.AD_Org_ID=C_Cash.orgId"
                        + " AND cb.C_Currency_ID=?)"; //	#3
        I_C_Cash retValue =
                new Query<I_C_Cash>(I_C_Cash.Table_Name, whereClause)
                        .setParameters(AD_Org_ID, TimeUtil.getDay(dateAcct), C_Currency_ID)
                        .first();

        if (retValue != null) return retValue;

        //	Get CashBook
        I_C_CashBook cb = MCashBook.get(AD_Org_ID, C_Currency_ID);
        if (cb == null) {
            s_log.warning("No CashBook for AD_Org_ID=" + AD_Org_ID + ", C_Currency_ID=" + C_Currency_ID);
            return null;
        }

        //	Create New Journal
        retValue = new MCash(cb, dateAcct);
        retValue.saveEx();
        return retValue;
    } //	get

    /**
     * Get Lines
     *
     * @param requery requery
     * @return lines
     */
    public I_C_CashLine[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }

        final String whereClause = MCashLine.COLUMNNAME_C_Cash_ID + "=?";
        List<I_C_CashLine> list =
                new Query<I_C_CashLine>(I_C_CashLine.Table_Name, whereClause)
                        .setParameters(getCashId())
                        .setOrderBy(I_C_CashLine.COLUMNNAME_Line)
                        .setOnlyActiveRecords(true)
                        .list();

        m_lines = list.toArray(new I_C_CashLine[list.size()]);
        return m_lines;
    } //	getLines

    /**
     * Get Cash Book
     *
     * @return cash book
     */
    public I_C_CashBook getCashBook() {
        if (m_book == null) m_book = MCashBook.get(getCashBookId());
        return m_book;
    } //	getCashBook

    /**
     * Get Document No
     *
     * @return name
     */
    @NotNull
    public String getDocumentNo() {
        return getName();
    } //	getDocumentNo

    /**
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        StringBuilder msgreturn =
                new StringBuilder()
                        .append(MsgKt.getElementTranslation("C_Cash_ID"))
                        .append(" ")
                        .append(getDocumentNo());
        return msgreturn.toString();
    } //	getDocumentInfo

    /**
     * Before Save
     *
     * @param newRecord
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        setOrgId(getCashBook().getOrgId());
        if (getOrgId() == 0) {
            log.saveError("Error", MsgKt.parseTranslation("@orgId@"));
            return false;
        }
        //	Calculate End Balance
        setEndingBalance(getBeginningBalance().add(getStatementDifference()));
        return true;
    } //	beforeSave

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
    } //	process

    /**
     * Unlock Document.
     *
     * @return true if success
     */
    public boolean unlockIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setProcessing(false);
        return true;
    } //	unlockIt

    /**
     * Invalidate Document
     *
     * @return true if success
     */
    public boolean invalidateIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setDocAction(X_C_Cash.DOCACTION_Prepare);
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

        //	Std Period open?
        if (!MPeriod.isOpen(
                getDateAcct(), MDocType.DOCBASETYPE_CashJournal, getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        I_C_CashLine[] lines = getLines(false);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        //	Add up Amounts
        BigDecimal difference = Env.ZERO;
        int C_Currency_ID = getCurrencyId();
        for (I_C_CashLine line : lines) {
            if (!line.isActive()) continue;
            if (C_Currency_ID == line.getCurrencyId()) difference = difference.add(line.getAmount());
            else {
                BigDecimal amt =
                        MConversionRate.convert(
                                line.getAmount(),
                                line.getCurrencyId(),
                                C_Currency_ID,
                                getDateAcct(),
                                0,
                                getClientId(),
                                getOrgId());
                if (amt == null) {
                    m_processMsg = "No Conversion Rate found - @C_CashLine_ID@= " + line.getLine();
                    return DocAction.Companion.getSTATUS_Invalid();
                }
                difference = difference.add(amt);
            }
        }
        setStatementDifference(difference);
        //	setEndingBalance(getBeginningBalance().add(getStatementDifference()));

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        if (!X_C_Cash.DOCACTION_Complete.equals(getDocAction()))
            setDocAction(X_C_Cash.DOCACTION_Complete);
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

    /**
     * Approve Document
     *
     * @return true if success
     */
    public boolean approveIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setIsApproved(true);
        return true;
    } //	approveIt

    /**
     * Reject Approval
     *
     * @return true if success
     */
    public boolean rejectIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
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
        //
        if (log.isLoggable(Level.INFO)) log.info(toString());

        I_C_CashLine[] lines = getLines(false);
        for (I_C_CashLine line : lines) {
            if (MCashLine.CASHTYPE_Invoice.equals(line.getCashType())) {
                // Check if the invoice is completed - teo_sarca BF [ 1894524 ]
                I_C_Invoice invoice = line.getInvoice();
                if (!I_C_Invoice.DOCSTATUS_Completed.equals(invoice.getDocStatus())
                        && !I_C_Invoice.DOCSTATUS_Closed.equals(invoice.getDocStatus())
                        && !I_C_Invoice.DOCSTATUS_Reversed.equals(invoice.getDocStatus())
                        && !I_C_Invoice.DOCSTATUS_Voided.equals(invoice.getDocStatus())) {
                    m_processMsg = "@Line@ " + line.getLine() + ": @InvoiceCreateDocNotCompleted@";
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
                //
                String name = MsgKt.translate("C_Cash_ID") +
                        ": " +
                        getName() +
                        " - " +
                        MsgKt.translate("Line") +
                        " " +
                        line.getLine();
                MAllocationHdr hdr =
                        new MAllocationHdr(
                                false,
                                getDateAcct(),
                                line.getCurrencyId(),
                                name
                        );
                hdr.setOrgId(getOrgId());
                if (!hdr.save()) {
                    m_processMsg = CLogger.retrieveErrorString("Could not create Allocation Hdr");
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
                //	Allocation Line
                MAllocationLine aLine =
                        new MAllocationLine(
                                hdr, line.getAmount(), line.getDiscountAmt(), line.getWriteOffAmt(), Env.ZERO);
                aLine.setInvoiceId(line.getInvoiceId());
                aLine.setCashLineId(line.getCashLineId());
                if (!aLine.save()) {
                    m_processMsg = CLogger.retrieveErrorString("Could not create Allocation Line");
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
                //	Should start WF
                if (!hdr.processIt(DocAction.Companion.getACTION_Complete())) {
                    m_processMsg = CLogger.retrieveErrorString("Could not process Allocation");
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
                if (!hdr.save()) {
                    m_processMsg = CLogger.retrieveErrorString("Could not save Allocation");
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
            } else if (MCashLine.CASHTYPE_BankAccountTransfer.equals(line.getCashType())) {
                //	Payment just as intermediate info
                MPayment pay = new MPayment(0);
                pay.setOrgId(getOrgId());
                String documentNo = getName();
                pay.setDocumentNo(documentNo);
                pay.setPaymentReference(documentNo);
                pay.setValueNoCheck("TrxType", "X"); // 	Transfer
                pay.setValueNoCheck("TenderType", "X");
                //
                // Modification for cash payment - Posterita
                pay.setCashBookId(getCashBookId());
                // End of modification - Posterita

                pay.setBankAccountId(line.getBankAccountId());
                pay.setDocumentTypeId(true); // 	Receipt
                pay.setDateTrx(getStatementDate());
                pay.setDateAcct(getDateAcct());
                pay.setAmount(line.getCurrencyId(), line.getAmount().negate()); // 	Transfer
                pay.setDescription(line.getDescription());
                pay.setDocStatus(MPayment.DOCSTATUS_Closed);
                pay.setDocAction(MPayment.DOCACTION_None);
                pay.setPosted(true);
                pay.setIsAllocated(true); // 	Has No Allocation!
                pay.setProcessed(true);
                if (!pay.save()) {
                    m_processMsg = CLogger.retrieveErrorString("Could not create Payment");
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }

                line.setPaymentId(pay.getPaymentId());
                if (!line.save()) {
                    m_processMsg = "Could not update Cash Line";
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
            }
        }

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }
        //
        setProcessed(true);
        setDocAction(X_C_Cash.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Void Document. Same as Close.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
        if (m_processMsg != null) return false;

        // FR [ 1866214 ]
        boolean retValue = reverseIt();

        if (retValue) {
            // After Void
            m_processMsg =
                    ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
            if (m_processMsg != null) return false;
            setDocAction(X_C_Cash.DOCACTION_None);
        }

        return retValue;
    } //	voidIt

    // FR [ 1866214 ]

    /**
     * ************************************************************************ Reverse Cash Period
     * needs to be open
     *
     * @return true if reversed
     */
    private boolean reverseIt() {
        if (X_C_Cash.DOCSTATUS_Closed.equals(getDocStatus())
                || X_C_Cash.DOCSTATUS_Reversed.equals(getDocStatus())
                || X_C_Cash.DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            setDocAction(X_C_Cash.DOCACTION_None);
            return false;
        }

        //	Can we delete posting
        if (!MPeriod.isOpen(
                this.getDateAcct(), MPeriodControl.DOCBASETYPE_CashJournal, getOrgId()))
            throw new IllegalStateException("@PeriodClosed@");

        //	Reverse Allocations
        I_C_AllocationHdr[] allocations =
                MAllocationHdr.getOfCash(getCashId());
        for (I_C_AllocationHdr allocation : allocations) {
            allocation.reverseCorrectIt();
            if (!allocation.save()) throw new IllegalStateException("Cannot reverse allocations");
        }

        I_C_CashLine[] cashlines = getLines(true);
        for (I_C_CashLine cashline : cashlines) {
            BigDecimal oldAmount = cashline.getAmount();
            BigDecimal oldDiscount = cashline.getDiscountAmt();
            BigDecimal oldWriteOff = cashline.getWriteOffAmt();
            cashline.setAmount(Env.ZERO);
            cashline.setDiscountAmt(Env.ZERO);
            cashline.setWriteOffAmt(Env.ZERO);
            String msgadd = MsgKt.getMsg("Voided") +
                    " (Amount=" +
                    oldAmount +
                    ", Discount=" +
                    oldDiscount +
                    ", WriteOff=" +
                    oldWriteOff +
                    ", )";
            cashline.addDescription(msgadd);
            if (MCashLine.CASHTYPE_BankAccountTransfer.equals(cashline.getCashType())) {
                if (cashline.getPaymentId() == 0)
                    throw new IllegalStateException("Cannot reverse payment");

                MPayment payment = new MPayment(cashline.getPaymentId());
                payment.reverseCorrectIt();
                payment.saveEx();
            }
            cashline.saveEx();
        }

        setName(getName() + "^");
        addDescription(MsgKt.getMsg("Voided"));
        setDocStatus(X_C_Cash.DOCSTATUS_Reversed); // 	for direct calls
        setProcessed(true);
        setPosted(true);
        setDocAction(X_C_Cash.DOCACTION_None);
        saveEx();

        //	Delete Posting
        MFactAcct.deleteEx(I_C_Cash.Table_ID, getCashId());

        return true;
    } //	reverse

    /**
     * Add to Description
     *
     * @param description text
     */
    public void addDescription(String description) {
        String desc = getDescription();
        if (desc == null) setDescription(description);
        else {
            StringBuilder msgd = new StringBuilder(desc).append(" | ").append(description);
            setDescription(msgd.toString());
        }
    } //	addDescription

    /**
     * Close Document. Cancel not delivered Quantities
     *
     * @return true if success
     */
    public boolean closeIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_CLOSE);
        if (m_processMsg != null) return false;

        setDocAction(X_C_Cash.DOCACTION_None);

        // After Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE);
        return m_processMsg == null;
    } //	closeIt

    /**
     * Reverse Correction
     *
     * @return true if success
     */
    public boolean reverseCorrectIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSECORRECT);
        if (m_processMsg != null) return false;

        // FR [ 1866214 ]
        boolean retValue = reverseIt();

        if (retValue) {
            // After reverseCorrect
            m_processMsg =
                    ModelValidationEngine.get()
                            .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
            if (m_processMsg != null) return false;
        }

        return retValue;
    } //	reverseCorrectionIt

    /**
     * Reverse Accrual - none
     *
     * @return true if success
     */
    public boolean reverseAccrualIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
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
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_REACTIVATE);
        if (m_processMsg != null) return false;

        setProcessed(false);
        if (reverseCorrectIt()) return true;

        // After reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_REACTIVATE);
        return false;
    } //	reActivateIt

    /**
     * Set Processed
     *
     * @param processed processed
     */
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        String sql = "UPDATE C_CashLine SET Processed='" +
                (processed ? "Y" : "N") +
                "' WHERE C_Cash_ID=" +
                getCashId();
        int noLine = executeUpdate(sql);
        m_lines = null;
        if (log.isLoggable(Level.FINE)) log.fine(processed + " - Lines=" + noLine);
    } //	setProcessed

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        String sb = "MCash[" + getId() +
                "-" +
                getName() +
                ", Balance=" +
                getBeginningBalance() +
                "->" +
                getEndingBalance() +
                "]";
        return sb;
    } //	toString

    /**
     * *********************************************************************** Get Summary
     *
     * @return Summary of Document
     */
    @NotNull
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        //	: Total Lines = 123.00 (#1)
        sb.append(": ")
                .append(MsgKt.translate("BeginningBalance"))
                .append("=")
                .append(getBeginningBalance())
                .append(",")
                .append(MsgKt.translate("EndingBalance"))
                .append("=")
                .append(getEndingBalance())
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
        return getCreatedBy();
    } //	getDoc_User_ID

    /**
     * Get Document Approval Amount
     *
     * @return amount difference
     */
    @NotNull
    public BigDecimal getApprovalAmt() {
        return getStatementDifference();
    } //	getApprovalAmt

    /**
     * Get Currency
     *
     * @return Currency
     */
    public int getCurrencyId() {
        return getCashBook().getCurrencyId();
    } //	getCurrencyId

    /**
     * Document Status is Complete or Closed
     *
     * @return true if CO, CL or RE
     */
    public boolean isComplete() {
        String ds = getDocStatus();
        return X_C_Cash.DOCSTATUS_Completed.equals(ds)
                || X_C_Cash.DOCSTATUS_Closed.equals(ds)
                || X_C_Cash.DOCSTATUS_Reversed.equals(ds);
    } //	isComplete

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }
} //	MCash
