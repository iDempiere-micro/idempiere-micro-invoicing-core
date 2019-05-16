package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.docengine.DocumentEngine;
import org.compiere.invoicing.MConversionRate;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_AllocationHdr;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.TypedQuery;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.PO;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.orm.POKt.I_ZERO;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.forUpdate;

/**
 * Payment Allocation Model. Allocation Trigger update C_BPartner
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 1866214 ]
 * <li>http://sourceforge.net/tracker/index.php?func=detail&aid=1866214&group_id=176962&atid=879335
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * <li>http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 * <li>BF [ 2880182 ] Error you can allocate a payment to invoice that was paid
 * <li>https://sourceforge.net/tracker/index.php?func=detail&aid=2880182&group_id=176962&atid=879332
 * @version $Id: MAllocationHdr.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MAllocationHdr extends X_C_AllocationHdr implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -7787519874581251920L;
    /**
     * Tolerance Gain and Loss
     */
    private static final BigDecimal TOLERANCE = BigDecimal.valueOf(0.02);
    /**
     * Lines
     */
    private MAllocationLine[] m_lines = null;

    // FR [ 1866214 ]
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;
    /**
     * Reversal Flag
     */
    private boolean m_reversal = false;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_AllocationHdr_ID id
     */
    public MAllocationHdr(Row row, int C_AllocationHdr_ID) {
        super(row, C_AllocationHdr_ID);
        if (C_AllocationHdr_ID == 0) {
            //	setDocumentNo (null);
            setDateTrx(new Timestamp(System.currentTimeMillis()));
            setDateAcct(getDateTrx());
            setDocAction(X_C_AllocationHdr.DOCACTION_Complete); // CO
            setDocStatus(X_C_AllocationHdr.DOCSTATUS_Drafted); // DR
            //	setCurrencyId (0);
            setApprovalAmt(Env.ZERO);
            setIsApproved(false);
            setIsManual(false);
            //
            setPosted(false);
            setProcessed(false);
            setProcessing(false);
            setDocumentTypeId(MDocTypeKt.getDocumentTypeDocType("CMA"));
        }
    } //	MAllocation

    /**
     * Mandatory New Constructor
     *
     * @param IsManual      manual trx
     * @param DateTrx       date (if null today)
     * @param C_Currency_ID currency
     * @param description   description
     */
    public MAllocationHdr(

            boolean IsManual,
            Timestamp DateTrx,
            int C_Currency_ID,
            String description) {
        this(null, 0);
        setIsManual(IsManual);
        if (DateTrx != null) {
            setDateTrx(DateTrx);
            setDateAcct(DateTrx);
        }
        setCurrencyId(C_Currency_ID);
        if (description != null) setDescription(description);
    } //  create Allocation

    /**
     * Get Allocations of Payment
     *
     * @param C_Payment_ID payment
     * @return allocations of payment
     */
    public static MAllocationHdr[] getOfPayment(int C_Payment_ID) {
        return MBaseAllocationHdrKt.getAllocationsOfPayment(C_Payment_ID);
    } //	getOfPayment

    /**
     * Get Allocations of Invoice
     *
     * @param C_Invoice_ID payment
     * @return allocations of payment
     */
    public static MAllocationHdr[] getOfInvoice(int C_Invoice_ID) {
        return MBaseAllocationHdrKt.getAllocationsOfInvoice(C_Invoice_ID);
    } //	getOfInvoice

    /**
     * Get Allocations of Cash
     *
     * @param C_Cash_ID Cash ID
     * @return allocations of payment
     */
    public static I_C_AllocationHdr[] getOfCash(int C_Cash_ID) {
        final String whereClause =
                "IsActive='Y'"
                        + " AND EXISTS (SELECT 1 FROM C_CashLine cl, C_AllocationLine al "
                        + "where cl.C_Cash_ID=? and al.C_CashLine_ID=cl.C_CashLine_ID "
                        + "and C_AllocationHdr.C_AllocationHdr_ID=al.C_AllocationHdr_ID)";
        TypedQuery<I_C_AllocationHdr> query = MBaseTableKt.getTable(I_C_AllocationHdr.Table_ID).createQuery(whereClause);
        query.setParameters(C_Cash_ID);
        List<I_C_AllocationHdr> list = query.list();
        I_C_AllocationHdr[] retValue = new I_C_AllocationHdr[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getOfCash

    /**
     * Create new Allocation by copying
     *
     * @param from     allocation
     * @param dateAcct date of the document accounting date
     * @param dateTrx  date of the document transaction.
     * @return Allocation
     */
    public static MAllocationHdr copyFrom(
            MAllocationHdr from, Timestamp dateAcct, Timestamp dateTrx) {
        MAllocationHdr to = new MAllocationHdr(null, 0);
        PO.copyValues(from, to, from.getClientId(), from.getOrgId());
        to.setValueNoCheck("DocumentNo", null);
        //
        to.setDocStatus(X_C_AllocationHdr.DOCSTATUS_Drafted); // 	Draft
        to.setDocAction(X_C_AllocationHdr.DOCACTION_Complete);
        //
        to.setDateTrx(dateAcct);
        to.setDateAcct(dateTrx);
        to.setIsManual(false);
        //
        to.setIsApproved(false);
        //
        to.setPosted(false);
        to.setProcessed(false);

        to.saveEx();

        //	Lines
        if (to.copyLinesFrom(from) == 0)
            throw new AdempiereException("Could not create Allocation Lines");

        return to;
    } //	copyFrom

    /**
     * Get Lines
     *
     * @param requery if true requery
     * @return lines
     */
    public MAllocationLine[] getLines(boolean requery) {
        if (m_lines != null && m_lines.length != 0 && !requery) {
            return m_lines;
        }
        m_lines = MBaseAllocationHdrKt.getAllocationLines(getPaymentAllocationHeaderId(), this);
        return m_lines;
    } //	getLines

    /**
     * Set Processed
     *
     * @param processed Processed
     */
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        if (getId() == 0) return;
        String sql = "UPDATE C_AllocationHdr SET Processed='" +
                (processed ? "Y" : "N") +
                "' WHERE C_AllocationHdr_ID=" +
                getPaymentAllocationHeaderId();
        int no = executeUpdate(sql);
        m_lines = null;
        if (log.isLoggable(Level.FINE)) log.fine(processed + " - #" + no);
    } //	setProcessed

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord
     * @return save
     */
    protected boolean beforeSave(boolean newRecord) {
        //	Changed from Not to Active
        if (!newRecord && isValueChanged("IsActive") && isActive()) {
            log.severe("Cannot Re-Activate deactivated Allocations");
            return false;
        }
        return true;
    } //	beforeSave

    /**
     * Before Delete.
     *
     * @return true if acct was deleted
     */
    protected boolean beforeDelete() {

        if (isPosted()) {
            MPeriod.testPeriodOpen(
                    getDateTrx(), MDocType.DOCBASETYPE_PaymentAllocation, getOrgId());
            setPosted(false);
            MFactAcct.deleteEx(I_C_AllocationHdr.Table_ID, getId());
        }
        //	Mark as Inactive
        setIsActive(false); // 	updated DB for line delete/process
        this.saveEx();

        //	Unlink
        getLines(true);
        if (!updateBP(true)) return false;

        for (MAllocationLine line : m_lines) {
            line.deleteEx(true);
        }
        return true;
    } //	beforeDelete

    /**
     * After Save
     *
     * @param newRecord
     * @param success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        return success;
    } //	afterSave

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
        setDocAction(X_C_AllocationHdr.DOCACTION_Prepare);
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
        MPeriod.testPeriodOpen(
                getDateAcct(), MDocType.DOCBASETYPE_PaymentAllocation, getOrgId());
        getLines(true);
        if (m_lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        // Stop the Document Workflow if invoice to allocate is as paid
        if (!isReversal()) {
            for (MAllocationLine line : m_lines) {
                if (line.getInvoiceId() != 0) {
                    String whereClause = I_C_Invoice.COLUMNNAME_C_Invoice_ID +
                            "=? AND " +
                            I_C_Invoice.COLUMNNAME_IsPaid +
                            "=? AND " +
                            I_C_Invoice.COLUMNNAME_DocStatus +
                            " NOT IN (?,?)";
                    boolean InvoiceIsPaid =
                            new Query<I_C_Invoice>(I_C_Invoice.Table_Name, whereClause)
                                    .setClientId()
                                    .setParameters(
                                            line.getInvoiceId(),
                                            "Y",
                                            I_C_Invoice.DOCSTATUS_Voided,
                                            I_C_Invoice.DOCSTATUS_Reversed)
                                    .match();
                    if (InvoiceIsPaid && line.getAmount().signum() > 0)
                        throw new AdempiereException("@ValidationError@ @C_Invoice_ID@ @IsPaid@");
                }
            }
        }

        //	Add up Amounts & validate
        BigDecimal approval = Env.ZERO;
        for (MAllocationLine line : m_lines) {
            approval = approval.add(line.getWriteOffAmt()).add(line.getDiscountAmt());
            //	Make sure there is BP
            if (line.getBusinessPartnerId() == 0) {
                m_processMsg = "No Business Partner";
                return DocAction.Companion.getSTATUS_Invalid();
            }

            // IDEMPIERE-1850 - validate date against related docs
            if (line.getInvoiceId() > 0) {
                if (line.getInvoice().getDateAcct().after(getDateAcct())) {
                    m_processMsg = "Wrong allocation date";
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
            if (line.getPaymentId() > 0) {
                if (line.getPayment().getDateAcct().after(getDateAcct())) {
                    m_processMsg = "Wrong allocation date";
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
        }
        setApprovalAmt(approval);
        //
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        if (!X_C_AllocationHdr.DOCACTION_Complete.equals(getDocAction()))
            setDocAction(X_C_AllocationHdr.DOCACTION_Complete);

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
        if (log.isLoggable(Level.INFO)) log.info(toString());

        //	Link
        getLines(false);
        if (!updateBP(isReversal()))
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());

        for (MAllocationLine line : m_lines) {
            line.processIt(isReversal());
        }

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        setProcessed(true);
        setDocAction(X_C_AllocationHdr.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Void Document. Same as Close.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());

        boolean retValue;
        if (X_C_AllocationHdr.DOCSTATUS_Closed.equals(getDocStatus())
                || X_C_AllocationHdr.DOCSTATUS_Reversed.equals(getDocStatus())
                || X_C_AllocationHdr.DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            setDocAction(X_C_AllocationHdr.DOCACTION_None);
            return false;
        }

        //	Not Processed
        if (X_C_AllocationHdr.DOCSTATUS_Drafted.equals(getDocStatus())
                || X_C_AllocationHdr.DOCSTATUS_Invalid.equals(getDocStatus())
                || X_C_AllocationHdr.DOCSTATUS_InProgress.equals(getDocStatus())
                || X_C_AllocationHdr.DOCSTATUS_Approved.equals(getDocStatus())
                || X_C_AllocationHdr.DOCSTATUS_NotApproved.equals(getDocStatus())) {
            // Before Void
            m_processMsg =
                    ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
            if (m_processMsg != null) return false;

            //	Set lines to 0
            MAllocationLine[] lines = getLines(true);
            if (!updateBP(true)) return false;

            for (MAllocationLine line : lines) {
                line.setAmount(Env.ZERO);
                line.setDiscountAmt(Env.ZERO);
                line.setWriteOffAmt(Env.ZERO);
                line.setOverUnderAmt(Env.ZERO);
                line.saveEx();
                // Unlink invoices
                line.processIt(true);
            }

            addDescription(MsgKt.getMsg("Voided"));
            retValue = true;
        } else {
            boolean accrual = false;
            try {
                MPeriod.testPeriodOpen(
                        getDateTrx(), MPeriodControl.DOCBASETYPE_PaymentAllocation, getOrgId());
            } catch (PeriodClosedException e) {
                accrual = true;
            }

            if (accrual) return reverseAccrualIt();
            else return reverseCorrectIt();
        }

        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        if (m_processMsg != null) return false;

        setProcessed(true);
        setDocAction(X_C_AllocationHdr.DOCACTION_None);

        return retValue;
    } //	voidIt

    /**
     * Close Document. Cancel not delivered Qunatities
     *
     * @return true if success
     */
    public boolean closeIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_CLOSE);
        if (m_processMsg != null) return false;

        setDocAction(X_C_AllocationHdr.DOCACTION_None);

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

        boolean retValue = reverseIt(false);

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        setDocAction(X_C_AllocationHdr.DOCACTION_None);
        return retValue;
    } //	reverseCorrectionIt

    /**
     * Reverse Accrual - none
     *
     * @return false
     */
    public boolean reverseAccrualIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        boolean retValue = reverseIt(true);

        // After reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        setDocAction(X_C_AllocationHdr.DOCACTION_None);
        return retValue;
    } //	reverseAccrualIt

    /**
     * Re-activate
     *
     * @return false
     */
    public boolean reActivateIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_REACTIVATE);
        if (m_processMsg != null) return false;

        // After reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_REACTIVATE);
        if (m_processMsg != null) return false;

        return false;
    } //	reActivateIt

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MAllocationHdr[" + getId() + "-" + getSummary() + "]";
    } //	toString

    /**
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        return MsgKt.getElementTranslation("C_AllocationHdr_ID") +
                " " +
                getDocumentNo();
    } //	getDocumentInfo

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
        return getCreatedBy();
    } //	getDoc_User_ID

    /**
     * Add to Description
     *
     * @param description text
     */
    public void addDescription(String description) {
        String desc = getDescription();
        if (desc == null) setDescription(description);
        else setDescription(desc + " | " + description);
    } //	addDescription

    /**
     * ************************************************************************ Reverse Allocation.
     * Period needs to be open
     *
     * @return true if reversed
     */
    private boolean reverseIt(boolean accrual) {
        if (!isActive()
                || getDocStatus().equals(X_C_AllocationHdr.DOCSTATUS_Voided) // Goodwill.co.id
                || getDocStatus().equals(X_C_AllocationHdr.DOCSTATUS_Reversed)) {
            // Goodwill: don't throw exception here
            //	BF: Reverse is not allowed at Payment void when Allocation is already reversed at Invoice
            // void
            // throw new IllegalStateException("Allocation already reversed (not active)");
            log.warning("Allocation already reversed (not active)");
            return true;
        }

        Timestamp reversalDate = accrual ? Env.getContextAsDate() : getDateAcct();
        if (reversalDate == null) {
            reversalDate = new Timestamp(System.currentTimeMillis());
        }

        //	Can we delete posting
        MPeriod.testPeriodOpen(
                reversalDate, MPeriodControl.DOCBASETYPE_PaymentAllocation, getOrgId());

        if (accrual) {
            //	Deep Copy
            MAllocationHdr reversal = copyFrom(this, reversalDate, reversalDate);
            if (reversal == null) {
                m_processMsg = "Could not create Payment Allocation Reversal";
                return false;
            }
            reversal.setReversalId(getPaymentAllocationHeaderId());

            //	Reverse Line Amt
            MAllocationLine[] rLines = reversal.getLines(false);
            for (MAllocationLine rLine : rLines) {
                rLine.setAmount(rLine.getAmount().negate());
                rLine.setDiscountAmt(rLine.getDiscountAmt().negate());
                rLine.setWriteOffAmt(rLine.getWriteOffAmt().negate());
                rLine.setOverUnderAmt(rLine.getOverUnderAmt().negate());
                if (!rLine.save()) {
                    m_processMsg = "Could not correct Payment Allocation Reversal Line";
                    return false;
                }
            }
            reversal.setReversal(true);
            reversal.setDocumentNo(getDocumentNo() + "^");
            reversal.addDescription("{->" + getDocumentNo() + ")");
            //
            if (!DocumentEngine.processIt(reversal, DocAction.Companion.getACTION_Complete())) {
                m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
                return false;
            }

            DocumentEngine.processIt(reversal, DocAction.Companion.getACTION_Close());
            reversal.setProcessing(false);
            reversal.setDocStatus(X_C_AllocationHdr.DOCSTATUS_Reversed);
            reversal.setDocAction(X_C_AllocationHdr.DOCACTION_None);
            reversal.saveEx();
            m_processMsg = reversal.getDocumentNo();
            addDescription("(" + reversal.getDocumentNo() + "<-)");

        } else {
            //	Set Inactive
            setIsActive(false);
            if (!isPosted()) setPosted(true);
            setDocumentNo(getDocumentNo() + "^");
            setDocStatus(X_C_AllocationHdr.DOCSTATUS_Reversed); // 	for direct calls
            if (!save() || isActive()) throw new IllegalStateException("Cannot de-activate allocation");

            //	Delete Posting
            MFactAcct.deleteEx(MAllocationHdr.Table_ID, getPaymentAllocationHeaderId());

            //	Unlink Invoices
            getLines(true);
            if (!updateBP(true)) return false;

            for (MAllocationLine line : m_lines) {
                line.setIsActive(false);
                line.setAmount(Env.ZERO);
                line.setDiscountAmt(Env.ZERO);
                line.setWriteOffAmt(Env.ZERO);
                line.setOverUnderAmt(Env.ZERO);
                line.saveEx();
                line.processIt(true); // 	reverse
            }

            addDescription(MsgKt.getMsg("Voided"));
        }

        setProcessed(true);
        setDocStatus(X_C_AllocationHdr.DOCSTATUS_Reversed); // 	may come from void
        setDocAction(X_C_AllocationHdr.DOCACTION_None);
        return true;
    } //	reverse

    private boolean updateBP(boolean reverse) {

        getLines(false);
        for (MAllocationLine line : m_lines) {
            int C_Payment_ID = line.getPaymentId();
            int C_BPartner_ID = line.getBusinessPartnerId();
            int M_Invoice_ID = line.getInvoiceId();

            if ((C_BPartner_ID == 0) || ((M_Invoice_ID == 0) && (C_Payment_ID == 0))) continue;

            boolean isSOTrxInvoice = false;
            MInvoice invoice =
                    M_Invoice_ID > 0 ? new MInvoice(null, M_Invoice_ID) : null;
            if (M_Invoice_ID > 0) isSOTrxInvoice = invoice.isSOTrx();

            I_C_BPartner bpartner = this.getBusinessPartnerService().getById(line.getBusinessPartnerId());
            forUpdate(bpartner);

            BigDecimal allocAmt = line.getAmount().add(line.getDiscountAmt()).add(line.getWriteOffAmt());
            BigDecimal openBalanceDiff = Env.ZERO;
            ClientWithAccounting client =
                    MClientKt.getClientWithAccounting(getClientId());

            boolean paymentProcessed = false;
            boolean paymentIsReceipt = false;

            // Retrieve payment information
            if (C_Payment_ID > 0) {
                MPayment payment;
                int convTypeID;
                Timestamp paymentDate;

                payment = new MPayment(C_Payment_ID);
                convTypeID = payment.getConversionTypeId();
                paymentDate = payment.getDateAcct();
                paymentProcessed = payment.isProcessed();
                paymentIsReceipt = payment.isReceipt();

                // Adjust open amount with allocated amount.
                if (paymentProcessed) {
                    if (invoice != null) {
                        // If payment is already processed, only adjust open balance by discount and write off
                        // amounts.
                        BigDecimal amt =
                                MConversionRate.convertBase(

                                        line.getWriteOffAmt().add(line.getDiscountAmt()),
                                        getCurrencyId(),
                                        paymentDate,
                                        convTypeID,
                                        getClientId(),
                                        getOrgId());
                        if (amt == null) {
                            m_processMsg =
                                    MConversionRateUtil.getErrorMessage(

                                            "ErrorConvertingAllocationCurrencyToBaseCurrency",
                                            getCurrencyId(),
                                            MClientKt.getClientWithAccounting().getCurrencyId(),
                                            convTypeID,
                                            paymentDate,
                                            null);
                            return false;
                        }
                        openBalanceDiff = openBalanceDiff.add(amt);
                    } else {
                        // Allocating payment to payment.
                        BigDecimal amt =
                                MConversionRate.convertBase(

                                        allocAmt,
                                        getCurrencyId(),
                                        paymentDate,
                                        convTypeID,
                                        getClientId(),
                                        getOrgId());
                        if (amt == null) {
                            m_processMsg =
                                    MConversionRateUtil.getErrorMessage(

                                            "ErrorConvertingAllocationCurrencyToBaseCurrency",
                                            getCurrencyId(),
                                            MClientKt.getClientWithAccounting().getCurrencyId(),
                                            convTypeID,
                                            paymentDate,
                                            null);
                            return false;
                        }
                        openBalanceDiff = openBalanceDiff.add(amt);
                    }
                } else {
                    // If payment has not been processed, adjust open balance by entire allocated amount.
                    BigDecimal allocAmtBase =
                            MConversionRate.convertBase(

                                    allocAmt,
                                    getCurrencyId(),
                                    getDateAcct(),
                                    convTypeID,
                                    getClientId(),
                                    getOrgId());
                    if (allocAmtBase == null) {
                        m_processMsg =
                                MConversionRateUtil.getErrorMessage(

                                        "ErrorConvertingAllocationCurrencyToBaseCurrency",
                                        getCurrencyId(),
                                        MClientKt.getClientWithAccounting().getCurrencyId(),
                                        convTypeID,
                                        getDateAcct(),
                                        null);
                        return false;
                    }

                    openBalanceDiff = openBalanceDiff.add(allocAmtBase);
                }
            } else if (invoice != null) {
                // adjust open balance by discount and write off amounts.
                BigDecimal amt =
                        MConversionRate.convertBase(

                                line.getWriteOffAmt().add(line.getDiscountAmt()),
                                getCurrencyId(),
                                invoice.getDateAcct(),
                                invoice.getConversionTypeId(),
                                getClientId(),
                                getOrgId());
                if (amt == null) {
                    m_processMsg =
                            MConversionRateUtil.getErrorMessage(

                                    "ErrorConvertingAllocationCurrencyToBaseCurrency",
                                    getCurrencyId(),
                                    MClientKt.getClientWithAccounting().getCurrencyId(),
                                    invoice.getConversionTypeId(),
                                    invoice.getDateAcct(),
                                    null);
                    return false;
                }
                openBalanceDiff = openBalanceDiff.add(amt);
            }

            // Adjust open amount for currency gain/loss
            if ((invoice != null)
                    && ((getCurrencyId() != client.getCurrencyId())
                    || (getCurrencyId() != invoice.getCurrencyId()))) {
                if (getCurrencyId() != invoice.getCurrencyId()) {
                    allocAmt =
                            MConversionRate.convert(

                                    allocAmt,
                                    getCurrencyId(),
                                    invoice.getCurrencyId(),
                                    getDateAcct(),
                                    invoice.getConversionTypeId(),
                                    getClientId(),
                                    getOrgId());
                    if (allocAmt == null) {
                        m_processMsg =
                                MConversionRateUtil.getErrorMessage(

                                        "ErrorConvertingAllocationCurrencyToInvoiceCurrency",
                                        getCurrencyId(),
                                        invoice.getCurrencyId(),
                                        invoice.getConversionTypeId(),
                                        getDateAcct(),
                                        null);
                        return false;
                    }
                }
                BigDecimal invAmtAccted =
                        MConversionRate.convertBase(

                                invoice.getGrandTotal(),
                                invoice.getCurrencyId(),
                                invoice.getDateAcct(),
                                invoice.getConversionTypeId(),
                                getClientId(),
                                getOrgId());
                if (invAmtAccted == null) {
                    m_processMsg =
                            MConversionRateUtil.getErrorMessage(

                                    "ErrorConvertingInvoiceCurrencyToBaseCurrency",
                                    invoice.getCurrencyId(),
                                    MClientKt.getClientWithAccounting().getCurrencyId(),
                                    invoice.getConversionTypeId(),
                                    invoice.getDateAcct(),
                                    null);
                    return false;
                }

                BigDecimal allocAmtAccted =
                        MConversionRate.convertBase(

                                allocAmt,
                                invoice.getCurrencyId(),
                                getDateAcct(),
                                invoice.getConversionTypeId(),
                                getClientId(),
                                getOrgId());
                if (allocAmtAccted == null) {
                    m_processMsg =
                            MConversionRateUtil.getErrorMessage(

                                    "ErrorConvertingInvoiceCurrencyToBaseCurrency",
                                    invoice.getCurrencyId(),
                                    MClientKt.getClientWithAccounting().getCurrencyId(),
                                    invoice.getConversionTypeId(),
                                    getDateAcct(),
                                    null);
                    return false;
                }

                if (allocAmt.compareTo(invoice.getGrandTotal()) == 0) {
                    openBalanceDiff = openBalanceDiff.add(invAmtAccted).subtract(allocAmtAccted);
                } else {
                    //	allocation as a percentage of the invoice
                    double multiplier = allocAmt.doubleValue() / invoice.getGrandTotal().doubleValue();
                    //	Reduce Orig Invoice Accounted
                    invAmtAccted = invAmtAccted.multiply(BigDecimal.valueOf(multiplier));
                    //	Difference based on percentage of Orig Invoice
                    openBalanceDiff =
                            openBalanceDiff.add(invAmtAccted).subtract(allocAmtAccted); // 	gain is negative
                    //	ignore Tolerance
                    if (openBalanceDiff.abs().compareTo(TOLERANCE) < 0) openBalanceDiff = Env.ZERO;
                    //	Round
                    int precision = MCurrencyKt.getCurrencyStdPrecision(client.getCurrencyId());
                    if (openBalanceDiff.scale() > precision)
                        openBalanceDiff = openBalanceDiff.setScale(precision, BigDecimal.ROUND_HALF_UP);
                }
            }

            //	Total Balance
            BigDecimal newBalance = bpartner.getTotalOpenBalance();
            if (newBalance == null) newBalance = Env.ZERO;

            BigDecimal originalBalance = new BigDecimal(newBalance.toString());

            if (openBalanceDiff.signum() != 0) {
                if (reverse) newBalance = newBalance.add(openBalanceDiff);
                else newBalance = newBalance.subtract(openBalanceDiff);
            }

            // Update BP Credit Used only for Customer Invoices and for payment-to-payment allocations.
            BigDecimal newCreditAmt;
            if (isSOTrxInvoice || (invoice == null && paymentIsReceipt && paymentProcessed)) {
                if (invoice == null) openBalanceDiff = openBalanceDiff.negate();

                newCreditAmt = bpartner.getSalesOrderCreditUsed();

                if (reverse) {
                    if (newCreditAmt == null) newCreditAmt = openBalanceDiff;
                    else newCreditAmt = newCreditAmt.add(openBalanceDiff);
                } else {
                    if (newCreditAmt == null) newCreditAmt = openBalanceDiff.negate();
                    else newCreditAmt = newCreditAmt.subtract(openBalanceDiff);
                }

                if (log.isLoggable(Level.FINE)) {
                    log.fine(
                            "TotalOpenBalance="
                                    + bpartner.getTotalOpenBalance()
                                    + "("
                                    + openBalanceDiff
                                    + ", Credit="
                                    + bpartner.getSalesOrderCreditUsed()
                                    + "->"
                                    + newCreditAmt
                                    + ", Balance="
                                    + bpartner.getTotalOpenBalance()
                                    + " -> "
                                    + newBalance);
                }
                bpartner.setSalesOrderCreditUsed(newCreditAmt);
            } else {
                if (log.isLoggable(Level.FINE)) {
                    log.fine(
                            "TotalOpenBalance="
                                    + bpartner.getTotalOpenBalance()
                                    + "("
                                    + openBalanceDiff
                                    + ", Balance="
                                    + bpartner.getTotalOpenBalance()
                                    + " -> "
                                    + newBalance);
                }
            }

            if (newBalance.compareTo(originalBalance) != 0) bpartner.setTotalOpenBalance(newBalance);

            bpartner.setSOCreditStatus();
            if (!bpartner.save()) {
                m_processMsg = "Could not update Business Partner";
                return false;
            }
        } // for all lines

        return true;
    } //	updateBP

    /**
     * Document Status is Complete or Closed
     *
     * @return true if CO, CL or RE
     */
    public boolean isComplete() {
        String ds = getDocStatus();
        return X_C_AllocationHdr.DOCSTATUS_Completed.equals(ds)
                || X_C_AllocationHdr.DOCSTATUS_Closed.equals(ds)
                || X_C_AllocationHdr.DOCSTATUS_Reversed.equals(ds);
    } //	isComplete

    // Goodwill.co.id

    /**
     * Copy Lines From other Allocation.
     *
     * @param otherAllocation allocation
     * @return number of lines copied
     */
    public int copyLinesFrom(MAllocationHdr otherAllocation) {
        if (isProcessed() || isPosted() || (otherAllocation == null)) return 0;
        MAllocationLine[] fromLines = otherAllocation.getLines(false);
        int count = 0;
        for (MAllocationLine fromLine : fromLines) {
            MAllocationLine line = new MAllocationLine(0);
            PO.copyValues(fromLine, line, fromLine.getClientId(), fromLine.getOrgId());
            line.setPaymentAllocationHeaderId(getPaymentAllocationHeaderId());
            line.setParent(this);
            line.setValueNoCheck("C_AllocationLine_ID", I_ZERO); // new

            if (line.getPaymentId() != 0) {
                MPayment payment = new MPayment(line.getPaymentId());
                if (X_C_AllocationHdr.DOCSTATUS_Reversed.equals(payment.getDocStatus())) {
                    MPayment reversal = (MPayment) payment.getReversal();
                    if (reversal != null) {
                        line.setPaymentInfo(reversal.getPaymentId(), 0);
                    }
                }
            }

            line.saveEx();
            count++;
        }
        if (fromLines.length != count)
            log.log(Level.WARNING, "Line difference - From=" + fromLines.length + " <> Saved=" + count);
        return count;
    } //	copyLinesFrom

    /**
     * Is Reversal
     *
     * @return reversal
     */
    private boolean isReversal() {
        return m_reversal;
    } //	isReversal

    /**
     * Set Reversal
     *
     * @param reversal reversal
     */
    private void setReversal(boolean reversal) {
        m_reversal = reversal;
    } //	setReversal

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }
} //  MAllocation
