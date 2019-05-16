package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_M_RMALine;
import org.compiere.model.I_M_RMATax;
import org.compiere.order.MOrder;
import org.compiere.order.MRMALine;
import org.compiere.order.X_M_RMA;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValueEx;

public class MRMA extends org.compiere.order.MRMA implements DocAction, IPODoc {
    /**
     * Lines
     */
    protected I_M_RMALine[] m_lines = null;

    /**
     * Standard Constructor
     *
     * @param M_RMA_ID id
     */
    public MRMA(int M_RMA_ID) {
        super(M_RMA_ID);
    }

    public MRMA(Row row) {
        super(row);
    }

    /**
     * Create new RMA by copying
     *
     * @param from         RMA
     * @param C_DocType_ID doc type
     * @param isSOTrx      sales order
     * @param counter      create counter links
     * @return MRMA
     */
    public static MRMA copyFrom(
            MRMA from, int C_DocType_ID, boolean isSOTrx, boolean counter) {
        MRMA to = new MRMA(0);
        return (MRMA) doCopyFrom(from, C_DocType_ID, isSOTrx, counter, to);
    }

    /**
     * Get the original invoice on which the shipment/receipt defined is based upon.
     *
     * @return invoice
     */
    public I_C_Invoice getOriginalInvoice() {
        MInOut shipment = getShipment();
        if (shipment == null) {
            return null;
        }

        int invId;

        if (shipment.getInvoiceId() != 0) {
            invId = shipment.getInvoiceId();
        } else {
            String sqlStmt = "SELECT C_Invoice_ID FROM C_Invoice WHERE C_Order_ID=?";
            invId = getSQLValueEx(sqlStmt, shipment.getOrderId());
        }

        if (invId <= 0) {
            return null;
        }

        return new MInvoice(null, invId);
    }

    /**
     * Before Save Set BPartner, Currency
     *
     * @param newRecord new
     * @return true
     */
    @Override
    protected boolean beforeSave(boolean newRecord) {
        if (newRecord) setOrderId(0);
        getShipment();
        //	Set BPartner
        if (getBusinessPartnerId() == 0) {
            if (m_inout != null) setBusinessPartnerId(m_inout.getBusinessPartnerId());
        }
        //	Set Currency
        if (getCurrencyId() == 0) {
            if (m_inout != null) {
                if (m_inout.getOrderId() != 0) {
                    org.compiere.order.MOrder order =
                            new MOrder(m_inout.getOrderId());
                    setCurrencyId(order.getCurrencyId());
                } else if (m_inout.getInvoiceId() != 0) {
                    MInvoice invoice = new MInvoice(null, m_inout.getInvoiceId());
                    setCurrencyId(invoice.getCurrencyId());
                }
            }
        }

        // Verification whether Shipment/Receipt matches RMA for sales transaction
        if (m_inout != null && m_inout.isSOTrx() != isSOTrx()) {
            log.saveError("RMA.IsSOTrx <> InOut.IsSOTrx", "");
            return false;
        }

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

        I_M_RMALine[] lines = getLines(false);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        for (I_M_RMALine line : lines) {
            if (line.getInOutLineId() != 0) {
                if (!line.checkQty()) {
                    m_processMsg = "@AmtReturned>Shipped@";
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
        }

        // Updates Amount
        if (!calculateTaxTotal()) {
            m_processMsg = "Error calculating tax";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

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

        // Set the definite document number after completed (if needed)
        setDefiniteDocumentNo();

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
        if (m_processMsg != null)
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());

        //	Implicit Approval
        if (!isApproved()) approveIt();
        if (log.isLoggable(Level.INFO)) log.info("completeIt - " + toString());
        //
    /*
    Flow for the creation of the credit memo document changed
          if (true)
    {
    	m_processMsg = "Need to code creating the credit memo";
    	return DocAction.STATUS_InProgress;
    }
          */

        //		Counter Documents
        org.compiere.order.MRMA counter = createCounterDoc();
        if (counter != null) m_processMsg = "@CounterDoc@: RMA=" + counter.getDocumentNo();

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        //
        setProcessed(true);
        setDocAction(X_M_RMA.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * ************************************************************************ Create Counter
     * Document
     *
     * @return InOut
     */
    private org.compiere.order.MRMA createCounterDoc() {
        //	Is this a counter doc ?
        if (getRef_RMAId() > 0) return null;

        //	Org Must be linked to BPartner
        org.compiere.orm.MOrg org = MOrgKt.getOrg(getOrgId());
        int counterC_BPartner_ID = org.getLinkedBusinessPartnerId();
        if (counterC_BPartner_ID == 0) return null;
        //	Business Partner needs to be linked to Org
        I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
        int counterAD_Org_ID = bp.getLinkedOrganizationId();
        if (counterAD_Org_ID == 0) return null;

        //	Document Type
        int C_DocTypeTarget_ID;
        MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getDocumentTypeId());
        if (counterDT != null) {
            if (log.isLoggable(Level.FINE)) log.fine(counterDT.toString());
            if (!counterDT.isCreateCounter() || !counterDT.isValid()) return null;
            C_DocTypeTarget_ID = counterDT.getCounterDocTypeId();
        } else //	indirect
        {
            C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocTypeId(getDocumentTypeId());
            if (log.isLoggable(Level.FINE)) log.fine("Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);
            if (C_DocTypeTarget_ID <= 0) return null;
        }

        //	Deep Copy
        MRMA counter = copyFrom(this, C_DocTypeTarget_ID, !isSOTrx(), true);

        //
        counter.setOrgId(counterAD_Org_ID);
        counter.setBusinessPartnerId(counterC_BPartner_ID);
        counter.saveEx();

        //	Update copied lines
        I_M_RMALine[] counterLines = counter.getLines(true);
        for (I_M_RMALine counterLine : counterLines) {
            counterLine.setClientOrg(counter);
            //
            counterLine.saveEx();
        }

        if (log.isLoggable(Level.FINE)) log.fine(counter.toString());

        //	Document Action
        if (counterDT != null) {
            if (counterDT.getDocAction() != null) {
                counter.setDocAction(counterDT.getDocAction());
                // added AdempiereException by zuhri
                if (!counter.processIt(counterDT.getDocAction()))
                    throw new AdempiereException(
                            "Failed when processing document - " + counter.getProcessMsg());
                // end added
                counter.saveEx();
            }
        }
        return counter;
    } //	createCounterDoc

    /**
     * Void Document.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info("voidIt - " + toString());
        // Before Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
        if (m_processMsg != null) return false;

        // IDEMPIERE-98 - Implement void for completed RMAs - Diego Ruiz - globalqss
        String validation =
                "SELECT COUNT(1) "
                        + "FROM M_InOut "
                        + "WHERE M_RMA_ID=? AND (DocStatus NOT IN ('VO','RE'))";
        int count = getSQLValueEx(validation, getRMAId());

        if (count == 0) {
            I_M_RMALine[] lines = getLines(true);
            // Set Qty and Amt on all lines to be Zero
            for (I_M_RMALine rmaLine : lines) {
                rmaLine.addDescription(MsgKt.getMsg("Voided") + " (" + rmaLine.getQty() + ")");
                rmaLine.setQty(Env.ZERO);
                rmaLine.setAmt(Env.ZERO);
                rmaLine.saveEx();
            }

            addDescription(MsgKt.getMsg("Voided"));
            setAmt(Env.ZERO);
        } else {
            m_processMsg = MsgKt.getMsg("RMACannotBeVoided");
            return false;
        }

        // update taxes
        I_M_RMATax[] taxes = getTaxes(true);
        for (I_M_RMATax tax : taxes) {
            if (!(tax.calculateTaxFromLines() && tax.save())) return false;
        }

        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        if (m_processMsg != null) return false;

        setProcessed(true);
        setDocAction(X_M_RMA.DOCACTION_None);
        return true;
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
        return m_processMsg == null;
    } //	closeIt

    /**
     * Reverse Correction
     *
     * @return true if success
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
     * @return true if success
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
                .append(MsgKt.translate("Amt"))
                .append("=")
                .append(getAmt())
                .append(" (#")
                .append(getLines(false).length)
                .append(")");
        //	 - Description
        if (getDescription() != null && getDescription().length() > 0)
            sb.append(" - ").append(getDescription());
        return sb.toString();
    } //	getSummary

    /**
     * Get Lines
     *
     * @param requery requery
     * @return lines
     */
    public I_M_RMALine[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        List<I_M_RMALine> list =
                new Query<I_M_RMALine>(I_M_RMALine.Table_Name, "M_RMA_ID=?")
                        .setParameters(getRMAId())
                        .setOrderBy(MRMALine.COLUMNNAME_Line)
                        .list();

        m_lines = new MRMALine[list.size()];
        list.toArray(m_lines);
        return m_lines;
    } //	getLines

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }

    /**
     * Get Shipment
     *
     * @return shipment
     */
    public MInOut getShipment() {
        if (m_inout == null && getInOutId() != 0)
            m_inout = new MInOut(getInOutId());
        return (MInOut) m_inout;
    } //	getShipment

    /**
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    public int getDocumentUserId() {
        return getSalesRepresentativeId();
    } //	getDoc_User_ID

    /**
     * Get Document Approval Amount
     *
     * @return amount
     */
    @NotNull
    public BigDecimal getApprovalAmt() {
        return getAmt();
    } //	getApprovalAmt

}
