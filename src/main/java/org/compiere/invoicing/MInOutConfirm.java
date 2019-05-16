package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MWarehouse;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.DocumentType;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_M_InOutConfirm;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.I_M_InOutLineConfirm;
import org.compiere.order.X_M_InOutConfirm;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
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

import static org.compiere.orm.MDocType.DOCBASETYPE_MaterialPhysicalInventory;
import static org.compiere.orm.MDocType.DOCSUBTYPEINV_PhysicalInventory;

public class MInOutConfirm extends org.compiere.order.MInOutConfirm implements DocAction, IPODoc {
    /**
     * Confirm Lines
     */
    private I_M_InOutLineConfirm[] m_lines = null;
    /**
     * Credit Memo to create
     */
    private MInvoice m_creditMemo = null;
    /**
     * Physical Inventory to create
     */
    private MInventory m_inventory = null;
    /**
     * Process Message
     */
    private String m_processMsg = null;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param M_InOutConfirm_ID id
     */
    public MInOutConfirm(int M_InOutConfirm_ID) {
        super(M_InOutConfirm_ID);
    }

    public MInOutConfirm(Row row) {
        super(row);
    }

    /**
     * Parent Constructor
     *
     * @param ship        shipment
     * @param confirmType confirmation type
     */
    public MInOutConfirm(MInOut ship, String confirmType) {
        super(ship, confirmType);
    }

    /**
     * Create Confirmation or return existing one
     *
     * @param ship          shipment
     * @param confirmType   confirmation type
     * @param checkExisting if false, new confirmation is created
     * @return Confirmation
     */
    public static I_M_InOutConfirm create(MInOut ship, String confirmType, boolean checkExisting) {
        if (checkExisting) {
            I_M_InOutConfirm[] confirmations = ship.getConfirmations(false);
            for (I_M_InOutConfirm confirm : confirmations) {
                if (confirm.getConfirmType().equals(confirmType)) {
                    if (s_log.isLoggable(Level.INFO)) s_log.info("create - existing: " + confirm);
                    return confirm;
                }
            }
        }

        MInOutConfirm confirm = new MInOutConfirm(ship, confirmType);
        confirm.saveEx();
        I_M_InOutLine[] shipLines = ship.getLines(false);
        for (I_M_InOutLine sLine : shipLines) {
            MInOutLineConfirm cLine = new MInOutLineConfirm(confirm);
            cLine.setInOutLine(sLine);
            cLine.saveEx();
        }
        if (s_log.isLoggable(Level.INFO)) s_log.info("New: " + confirm);
        return confirm;
    } //	MInOutConfirm

    /**
     * Get Lines
     *
     * @param requery requery
     * @return array of lines
     */
    public I_M_InOutLineConfirm[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        final String whereClause = I_M_InOutLineConfirm.COLUMNNAME_M_InOutConfirm_ID + "=?";
        List<I_M_InOutLineConfirm> list =
                new Query<I_M_InOutLineConfirm>(I_M_InOutLineConfirm.Table_Name, whereClause)
                        .setParameters(getInOutConfirmId())
                        .list();
        m_lines = new I_M_InOutLineConfirm[list.size()];
        list.toArray(m_lines);
        return m_lines;
    } //	getLines

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

        I_M_InOutLineConfirm[] lines = getLines(true);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        //	Set dispute if not fully confirmed
        boolean difference = false;
        for (I_M_InOutLineConfirm line : lines) {
            if (!line.isFullyConfirmed()) {
                difference = true;
                break;
            }
        }
        setIsInDispute(difference);

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();
        //
        m_justPrepared = true;
        if (!X_M_InOutConfirm.DOCACTION_Complete.equals(getDocAction()))
            setDocAction(X_M_InOutConfirm.DOCACTION_Complete);
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

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
        if (m_processMsg != null)
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());

        //	Implicit Approval
        if (!isApproved()) approveIt();
        if (log.isLoggable(Level.INFO)) log.info(toString());
        //
        MInOut inout = new MInOut(getInOutId());
        I_M_InOutLineConfirm[] lines = getLines(false);

        //	Check if we need to split Shipment
        if (isInDispute()) {
            MDocType dt = MDocTypeKt.getDocumentType(inout.getDocumentTypeId());
            if (dt.isSplitWhenDifference()) {
                if (dt.getDocTypeDifferenceId() == 0) {
                    m_processMsg = "No Split Document Type defined for: " + dt.getName();
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
                splitInOut(inout, dt.getDocTypeDifferenceId(), lines);
                m_lines = null;
            }
        }

        //	All lines
        for (I_M_InOutLineConfirm confirmLine : lines) {
            if (!confirmLine.processLine(inout.isSOTrx(), getConfirmType())) {
                m_processMsg = "ShipLine not saved - " + confirmLine;
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            if (confirmLine.isFullyConfirmed()) {
                confirmLine.setProcessed(true);
                confirmLine.saveEx();
            } else {
                if (createDifferenceDoc(inout, confirmLine)) {
                    confirmLine.setProcessed(true);
                    confirmLine.saveEx();
                } else {
                    log.log(
                            Level.SEVERE,
                            "Scrapped="
                                    + confirmLine.getScrappedQty()
                                    + " - Difference="
                                    + confirmLine.getDifferenceQty());
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
            }
        } //	for all lines

        if (m_creditMemo != null) m_processMsg += " @C_Invoice_ID@=" + m_creditMemo.getDocumentNo();
        if (m_inventory != null) m_processMsg += " @M_Inventory_ID@=" + m_inventory.getDocumentNo();

        //	Try to complete Shipment
        //	if (inout.processIt(DocAction.ACTION_Complete))
        //		m_processMsg = "@M_InOut_ID@ " + inout.getDocumentNo() + ": @Completed@";

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        setProcessed(true);
        setDocAction(X_M_InOutConfirm.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Split Shipment into confirmed and dispute
     *
     * @param original     original shipment
     * @param C_DocType_ID target DocType
     * @param confirmLines confirm lines
     */
    private void splitInOut(MInOut original, int C_DocType_ID, I_M_InOutLineConfirm[] confirmLines) {
        MInOut split = null;
        //	Go through confirmations
        for (I_M_InOutLineConfirm confirmLine : confirmLines) {
            BigDecimal differenceQty = confirmLine.getDifferenceQty();
            if (differenceQty.compareTo(Env.ZERO) == 0) continue;
            //
            I_M_InOutLine oldLine = confirmLine.getLine();
            if (log.isLoggable(Level.FINE)) log.fine("Qty=" + differenceQty + ", Old=" + oldLine);
            //
            // Create Header
            if (split == null) {
                split = new MInOut(original, C_DocType_ID, original.getMovementDate());
                StringBuilder msgd = new StringBuilder("Splitted from ").append(original.getDocumentNo());
                split.addDescription(msgd.toString());
                split.setIsInDispute(true);
                split.saveEx();
                msgd = new StringBuilder("Split: ").append(split.getDocumentNo());
                original.addDescription(msgd.toString());
                original.saveEx();
            }
            //
            org.compiere.order.MInOutLine splitLine = new org.compiere.order.MInOutLine(split);
            splitLine.setOrderLineId(oldLine.getOrderLineId());
            splitLine.setUOMId(oldLine.getUOMId());
            splitLine.setDescription(oldLine.getDescription());
            splitLine.setIsDescription(oldLine.isDescription());
            splitLine.setLine(oldLine.getLine());
            splitLine.setAttributeSetInstanceId(oldLine.getAttributeSetInstanceId());
            splitLine.setLocatorId(oldLine.getLocatorId());
            splitLine.setProductId(oldLine.getProductId());
            splitLine.setWarehouseId(oldLine.getWarehouseId());
            splitLine.setReferencedInOutLineId(oldLine.getReferencedInOutLineId());
            StringBuilder msgd = new StringBuilder("Split: from ").append(oldLine.getMovementQty());
            splitLine.addDescription(msgd.toString());
            //	Qtys
            splitLine.setQty(differenceQty); // 	Entered/Movement
            splitLine.saveEx();
            //	Old
            msgd = new StringBuilder("Splitted: from ").append(oldLine.getMovementQty());
            oldLine.addDescription(msgd.toString());
            oldLine.setQty(oldLine.getMovementQty().subtract(differenceQty));
            oldLine.saveEx();
            //	Update Confirmation Line
            confirmLine.setTargetQty(confirmLine.getTargetQty().subtract(differenceQty));
            confirmLine.setDifferenceQty(Env.ZERO);
            confirmLine.saveEx();
        } //	for all confirmations

        // Nothing to split
        if (split == null) {
            return;
        }

        m_processMsg = "Split @M_InOut_ID@=" + split.getDocumentNo() + " - @M_InOutConfirm_ID@=";

        MDocType dt = MDocTypeKt.getDocumentType(original.getDocumentTypeId());
        if (!dt.isPrepareSplitDocument()) {
            return;
        }

        //	Create Dispute Confirmation
        if (!split.processIt(DocAction.Companion.getACTION_Prepare()))
            throw new AdempiereException(split.getProcessMsg());
        //	split.createConfirmation();
        split.saveEx();
        I_M_InOutConfirm[] splitConfirms = split.getConfirmations(true);
        if (splitConfirms.length > 0) {
            int index = 0;
            if (splitConfirms[index].isProcessed()) {
                if (splitConfirms.length > 1) index++; // 	try just next
                if (splitConfirms[index].isProcessed()) {
                    m_processMsg += splitConfirms[index].getDocumentNo() + " processed??";
                    return;
                }
            }
            splitConfirms[index].setIsInDispute(true);
            splitConfirms[index].saveEx();
            m_processMsg += splitConfirms[index].getDocumentNo();
            //	Set Lines to unconfirmed
            I_M_InOutLineConfirm[] splitConfirmLines = splitConfirms[index].getLines(false);
            for (I_M_InOutLineConfirm splitConfirmLine : splitConfirmLines) {
                splitConfirmLine.setScrappedQty(Env.ZERO);
                splitConfirmLine.setConfirmedQty(Env.ZERO);
                splitConfirmLine.saveEx();
            }
        } else m_processMsg += "??";
    } //	splitInOut

    /**
     * Create Difference Document
     *
     * @param inout   shipment/receipt
     * @param confirm confirm line
     * @return true if created
     */
    private boolean createDifferenceDoc(org.compiere.order.MInOut inout, I_M_InOutLineConfirm confirm) {
        if (m_processMsg == null) m_processMsg = "";
        else if (m_processMsg.length() > 0) m_processMsg += "; ";
        //	Credit Memo if linked Document
        if (confirm.getDifferenceQty().signum() != 0
                && !inout.isSOTrx()
                && inout.getReferencedInOutId() != 0) {
            if (log.isLoggable(Level.INFO)) log.info("Difference=" + confirm.getDifferenceQty());
            if (m_creditMemo == null) {
                m_creditMemo = new MInvoice(inout, null);
                String msgd = MsgKt.translate("M_InOutConfirm_ID") +
                        " " +
                        getDocumentNo();
                m_creditMemo.setDescription(msgd);
                m_creditMemo.setTargetDocumentTypeId(MDocType.DOCBASETYPE_APCreditMemo);
                m_creditMemo.saveEx();
                setInvoiceId(m_creditMemo.getInvoiceId());
            }
            MInvoiceLine line = new MInvoiceLine(m_creditMemo);
            line.setShipLine(confirm.getLine());
            if (confirm.getLine().getProduct() != null) {
                // use product UOM in case the shipment hasn't the same uom than the order
                line.setUOMId(confirm.getLine().getProduct().getUOMId());
            }
            // Note: confirmation is always in the qty according to the product UOM
            line.setQty(confirm.getDifferenceQty()); // 	Entered/Invoiced
            line.saveEx();
            confirm.setInvoiceLineId(line.getInvoiceLineId());
        }

        //	Create Inventory Difference
        if (confirm.getScrappedQty().signum() != 0) {
            if (log.isLoggable(Level.INFO)) log.info("Scrapped=" + confirm.getScrappedQty());
            if (m_inventory == null) {
                MWarehouse wh = MWarehouse.get(inout.getWarehouseId());
                m_inventory = new MInventory(wh);
                String msgd = MsgKt.translate("M_InOutConfirm_ID") +
                        " " +
                        getDocumentNo();
                m_inventory.setDescription(msgd);
                setInventoryDocType(m_inventory);
                m_inventory.saveEx();
                setInventoryId(m_inventory.getInventoryId());
            }
            I_M_InOutLine ioLine = confirm.getLine();
            MInventoryLine line =
                    new MInventoryLine(
                            m_inventory,
                            ioLine.getLocatorId(),
                            ioLine.getProductId(),
                            ioLine.getAttributeSetInstanceId(),
                            confirm.getScrappedQty(),
                            Env.ZERO);
            if (!line.save()) {
                m_processMsg += "Inventory Line not created";
                return false;
            }
            confirm.setInventoryLineId(line.getInventoryLineId());
        }

        //
        if (!confirm.save()) {
            m_processMsg += "Confirmation Line not saved";
            return false;
        }
        return true;
    } //	createDifferenceDoc

    /**
     * @param inventory
     */
    private void setInventoryDocType(MInventory inventory) {
        DocumentType[] doctypes =
                MDocTypeKt.getDocumentTypeOfDocBaseType(DOCBASETYPE_MaterialPhysicalInventory);
        for (DocumentType doctype : doctypes) {
            if (DOCSUBTYPEINV_PhysicalInventory.equals(doctype.getDocSubTypeInv())) {
                inventory.setDocumentTypeId(doctype.getDocTypeId());
                break;
            }
        }
    }

    /**
     * Void Document.
     *
     * @return false
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
        if (m_processMsg != null) return false;

        if (X_M_InOutConfirm.DOCSTATUS_Closed.equals(getDocStatus())
                || X_M_InOutConfirm.DOCSTATUS_Reversed.equals(getDocStatus())
                || X_M_InOutConfirm.DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            return false;
        }

        //	Not Processed
        if (X_M_InOutConfirm.DOCSTATUS_Drafted.equals(getDocStatus())
                || X_M_InOutConfirm.DOCSTATUS_Invalid.equals(getDocStatus())
                || X_M_InOutConfirm.DOCSTATUS_InProgress.equals(getDocStatus())
                || X_M_InOutConfirm.DOCSTATUS_Approved.equals(getDocStatus())
                || X_M_InOutConfirm.DOCSTATUS_NotApproved.equals(getDocStatus())) {
            org.compiere.order.MInOut inout = (org.compiere.order.MInOut) getInOut();
            if (!org.compiere.order.MInOut.DOCSTATUS_Voided.equals(inout.getDocStatus())
                    && !MInOut.DOCSTATUS_Reversed.equals(inout.getDocStatus())) {
                throw new AdempiereException("@M_InOut_ID@ @DocStatus@<>VO");
            }
            for (I_M_InOutLineConfirm confirmLine : getLines(true)) {
                confirmLine.setTargetQty(Env.ZERO);
                confirmLine.setConfirmedQty(Env.ZERO);
                confirmLine.setScrappedQty(Env.ZERO);
                confirmLine.setDifferenceQty(Env.ZERO);
                confirmLine.setProcessed(true);
                confirmLine.saveEx();
            }
            setIsCancelled(true);
        } else {
            return reverseCorrectIt();
        }

        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        if (m_processMsg != null) return false;

        setProcessed(true);
        setDocAction(X_M_InOutConfirm.DOCACTION_None);

        return true;
    } //	voidIt

    /**
     * Close Document.
     *
     * @return true if success
     */
    public boolean closeIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_CLOSE);
        if (m_processMsg != null) return false;

        setDocAction(X_M_InOutConfirm.DOCACTION_None);

        // After Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE);
        return m_processMsg == null;
    } //	closeIt

    /**
     * Reverse Correction
     *
     * @return false
     */
    public boolean reverseCorrectIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
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
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MInOutConfirm[" + getId() + "-" + getSummary() + "]";
    } //	toString

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }

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
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        return MsgKt.getElementTranslation("M_InOutConfirm_ID") +
                " " +
                getDocumentNo();
    } //	getDocumentInfo

    /**
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    public int getDocumentUserId() {
        return getUpdatedBy();
    } //	getDoc_User_ID

    /**
     * Get Document Currency
     *
     * @return C_Currency_ID
     */
    public int getCurrencyId() {
        //	MPriceList pl = MPriceList.get(getPriceListId());
        //	return pl.getCurrencyId();
        return 0;
    } //	getCurrencyId

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
        setDocAction(X_M_InOutConfirm.DOCACTION_Prepare);
        return true;
    } //	invalidateIt
}
