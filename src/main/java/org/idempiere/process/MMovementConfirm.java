package org.idempiere.process;

import kotliquery.Row;
import org.compiere.accounting.MPeriod;
import org.compiere.accounting.MWarehouse;
import org.compiere.crm.MUser;
import org.compiere.crm.MUserKt;
import org.compiere.docengine.DocumentEngine;
import org.compiere.invoicing.MInventory;
import org.compiere.invoicing.MInventoryLine;
import org.compiere.model.DocumentType;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_M_MovementConfirm;
import org.compiere.model.I_M_MovementLine;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.process.ProcessInfo;
import org.compiere.production.MLocator;
import org.compiere.server.ServerProcessCtl;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.ValueNamePair;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MMovementConfirm extends X_M_MovementConfirm implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -5210710606049843678L;
    /**
     * Confirm Lines
     */
    private MMovementLineConfirm[] m_lines = null;
    /**
     * Physical Inventory From
     */
    private MInventory m_inventoryFrom = null;
    /**
     * Physical Inventory To
     */
    private MInventory m_inventoryTo = null;
    /**
     * Physical Inventory Info
     */
    private String m_inventoryInfo = null;
    private List<MInventory> m_inventoryDoc = null;
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
     * @param M_MovementConfirm_ID id
     */
    public MMovementConfirm(int M_MovementConfirm_ID) {
        super(M_MovementConfirm_ID);
        if (M_MovementConfirm_ID == 0) {
            setDocAction(DOCACTION_Complete);
            setDocStatus(DOCSTATUS_Drafted);
            setIsApproved(false); // N
            setProcessed(false);
        }
    } //	MMovementConfirm

    /**
     * Load Constructor
     */
    public MMovementConfirm(Row row) {
        super(row);
    } //	MMovementConfirm

    /**
     * Parent Constructor
     *
     * @param move movement
     */
    public MMovementConfirm(MMovement move) {
        this(0);
        setClientOrg(move);
        setMovementId(move.getMovementId());
    } //	MInOutConfirm

    /**
     * Create Confirmation or return existing one
     *
     * @param move          movement
     * @param checkExisting if false, new confirmation is created
     * @return Confirmation
     */
    public static I_M_MovementConfirm create(MMovement move, boolean checkExisting) {
        if (checkExisting) {
            I_M_MovementConfirm[] confirmations = move.getConfirmations(false);
            if (confirmations.length > 0) {
                return confirmations[0];
            }
        }

        MMovementConfirm confirm = new MMovementConfirm(move);
        confirm.saveEx();
        I_M_MovementLine[] moveLines = move.getLines(false);
        for (I_M_MovementLine mLine : moveLines) {
            MMovementLineConfirm cLine = new MMovementLineConfirm(confirm);
            cLine.setMovementLine(mLine);
            cLine.saveEx();
        }
        return confirm;
    } //	MInOutConfirm

    /**
     * Get Lines
     *
     * @param requery requery
     * @return array of lines
     */
    public MMovementLineConfirm[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        m_lines = MBaseMovementLineConfirmKt.getMovementLineConfirmLines(getMovementConfirmId());
        return m_lines;
    } //	getLines

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
     * Set Approved
     *
     * @param IsApproved approval
     */
    public void setIsApproved(boolean IsApproved) {
        if (IsApproved && !isApproved()) {
            int AD_User_ID = Env.getUserId();
            MUser user = MUserKt.getUser(AD_User_ID);
            String info =
                    user.getName()
                            + ": "
                            + MsgKt.translate("IsApproved")
                            + " - "
                            + new Timestamp(System.currentTimeMillis());
            addDescription(info);
        }
        super.setIsApproved(IsApproved);
    } //	setIsApproved

    /**
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        return MsgKt.getElementTranslation("M_MovementConfirm_ID") + " " + getDocumentNo();
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

        //	Std Period open?
        if (!MPeriod.isOpen(
                getUpdated(), MDocType.DOCBASETYPE_MaterialMovement, getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        MMovementLineConfirm[] lines = getLines(true);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        for (MMovementLineConfirm line : lines) {
            if (!line.isFullyConfirmed()) {
                break;
            }
        }

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        //
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
        //
        m_inventoryDoc = new ArrayList<>();
        MMovement move = new MMovement(getMovementId());
        MMovementLineConfirm[] lines = getLines(false);
        for (MMovementLineConfirm confirm : lines) {
            if (!confirm.processLine()) {
                m_processMsg = "ShipLine not saved - " + confirm;
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            if (confirm.isFullyConfirmed() && confirm.getScrappedQty().signum() == 0) {
                confirm.setProcessed(true);
                confirm.saveEx();
            } else {
                if (createDifferenceDoc(move, confirm)) {
                    confirm.setProcessed(true);
                    confirm.saveEx();
                } else {
                    log.log(
                            Level.SEVERE,
                            "completeIt - Scrapped="
                                    + confirm.getScrappedQty()
                                    + " - Difference="
                                    + confirm.getDifferenceQty());

                    if (m_processMsg == null) m_processMsg = "Differnce Doc not created";
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
            }
        } //	for all lines

        // complete movement
        setProcessed(true);
        saveEx();
        ProcessInfo processInfo =
                ServerProcessCtl.runDocumentActionWorkflow(move, DocAction.Companion.getACTION_Complete());
        if (processInfo.isError()) {
            m_processMsg = processInfo.getSummary();
            setProcessed(false);
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        if (m_inventoryInfo != null) {
            // complete inventory doc
            for (MInventory inventory : m_inventoryDoc) {
                processInfo =
                        ServerProcessCtl.runDocumentActionWorkflow(
                                inventory, DocAction.Companion.getACTION_Complete());
                if (processInfo.isError()) {
                    m_processMsg = processInfo.getSummary();
                    setProcessed(false);
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
            }

            m_processMsg = " @M_Inventory_ID@: " + m_inventoryInfo;
            addDescription(MsgKt.translate("M_Inventory_ID") + ": " + m_inventoryInfo);
        }

        m_inventoryDoc = null;

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            setProcessed(false);
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        setProcessed(true);
        setDocAction(DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Create Difference Document. Creates one or two inventory lines
     *
     * @param move    movement
     * @param confirm confirm line
     * @return true if created
     */
    private boolean createDifferenceDoc(MMovement move, MMovementLineConfirm confirm) {
        I_M_MovementLine mLine = confirm.getLine();

        //	Difference - Create Inventory Difference for Source Location
        if (Env.ZERO.compareTo(confirm.getDifferenceQty()) != 0) {
            //	Get Warehouse for Source
            MLocator loc = MLocator.get(mLine.getLocatorId());
            if (m_inventoryFrom != null && m_inventoryFrom.getWarehouseId() != loc.getWarehouseId())
                m_inventoryFrom = null;

            if (m_inventoryFrom == null) {
                MWarehouse wh = MWarehouse.get(loc.getWarehouseId());
                m_inventoryFrom = new MInventory(wh);
                m_inventoryFrom.setDescription(
                        MsgKt.translate("M_MovementConfirm_ID") + " " + getDocumentNo());
                setInventoryDocType(m_inventoryFrom);
                if (!m_inventoryFrom.save()) {
                    updateProcessMsg("Inventory not created");
                    return false;
                }
                //	First Inventory
                if (getInventoryId() == 0) {
                    setInventoryId(m_inventoryFrom.getInventoryId());
                    m_inventoryInfo = m_inventoryFrom.getDocumentNo();
                } else m_inventoryInfo += "," + m_inventoryFrom.getDocumentNo();
                m_inventoryDoc.add(m_inventoryFrom);
            }

            if (log.isLoggable(Level.INFO))
                log.info("createDifferenceDoc - Difference=" + confirm.getDifferenceQty());
            MInventoryLine line =
                    new MInventoryLine(
                            m_inventoryFrom,
                            mLine.getLocatorId(),
                            mLine.getProductId(),
                            mLine.getAttributeSetInstanceId(),
                            confirm.getDifferenceQty(),
                            Env.ZERO);
            line.setDescription(MsgKt.translate("DifferenceQty"));
            if (!line.save()) {
                updateProcessMsg("Inventory Line not created");
                return false;
            }
            confirm.setInventoryLineId(line.getInventoryLineId());
        } //	Difference

        //	Scrapped - Create Inventory Difference for Target Location
        if (Env.ZERO.compareTo(confirm.getScrappedQty()) != 0) {
            //	Get Warehouse for Target
            MLocator loc = MLocator.get(mLine.getLocatorToId());
            if (m_inventoryTo != null && m_inventoryTo.getWarehouseId() != loc.getWarehouseId())
                m_inventoryTo = null;

            if (m_inventoryTo == null) {
                MWarehouse wh = MWarehouse.get(loc.getWarehouseId());
                m_inventoryTo = new MInventory(wh);
                m_inventoryTo.setDescription(
                        MsgKt.translate("M_MovementConfirm_ID") + " " + getDocumentNo());
                setInventoryDocType(m_inventoryTo);
                if (!m_inventoryTo.save()) {
                    updateProcessMsg("Inventory not created");
                    return false;
                }
                //	First Inventory
                if (getInventoryId() == 0) {
                    setInventoryId(m_inventoryTo.getInventoryId());
                    m_inventoryInfo = m_inventoryTo.getDocumentNo();
                } else m_inventoryInfo += "," + m_inventoryTo.getDocumentNo();
                m_inventoryDoc.add(m_inventoryTo);
            }

            if (log.isLoggable(Level.INFO))
                log.info("createDifferenceDoc - Scrapped=" + confirm.getScrappedQty());
            MInventoryLine line =
                    new MInventoryLine(
                            m_inventoryTo,
                            mLine.getLocatorToId(),
                            mLine.getProductId(),
                            mLine.getAttributeSetInstanceId(),
                            confirm.getScrappedQty(),
                            Env.ZERO);
            line.setDescription(MsgKt.translate("ScrappedQty"));
            if (!line.save()) {
                updateProcessMsg("Inventory Line not created");
                return false;
            }
            confirm.setInventoryLineId(line.getInventoryLineId());
        } //	Scrapped

        return true;
    } //	createDifferenceDoc

    /**
     *
     */
    private void updateProcessMsg(String msg) {
        if (m_processMsg != null) m_processMsg = m_processMsg + " " + msg;
        else m_processMsg = msg;
        ValueNamePair error = CLogger.retrieveError();
        if (error != null)
            m_processMsg =
                    m_processMsg + ": " + MsgKt.getMsg(error.getValue()) + " " + error.getName();
    }

    /**
     * @param inventory
     */
    private void setInventoryDocType(MInventory inventory) {
        DocumentType[] doctypes =
                MDocTypeKt.getDocumentTypeOfDocBaseType(MDocType.DOCBASETYPE_MaterialPhysicalInventory);
        for (DocumentType doctype : doctypes) {
            if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(doctype.getDocSubTypeInv())) {
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
        if (log.isLoggable(Level.INFO)) log.info("voidIt - " + toString());
        // Before Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
        if (m_processMsg != null) return false;
        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        if (m_processMsg != null) return false;

        return false;
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

        //	Close Not delivered Qty
        setDocAction(DOCACTION_None);

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
     * @return false
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

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }
} //	MMovementConfirm
