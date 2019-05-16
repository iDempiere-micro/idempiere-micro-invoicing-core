package org.idempiere.process;

import kotliquery.Row;
import org.compiere.accounting.MClient;
import org.compiere.accounting.MPeriod;
import org.compiere.accounting.MProduct;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.accounting.NegativeInventoryDisallowedException;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.DocumentType;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_M_MovementConfirm;
import org.compiere.model.I_M_MovementLine;
import org.compiere.model.I_M_MovementLineMA;
import org.compiere.model.I_M_Product;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.MSequence;
import org.compiere.orm.PO;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.production.MLocator;
import org.compiere.production.MTransaction;
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

import static software.hsharp.core.util.DBKt.executeUpdateEx;

public class MMovement extends X_M_Movement implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -1628932946440487727L;
    /**
     * Reversal Indicator
     */
    public static String REVERSE_INDICATOR = "^";
    /**
     * Lines
     */
    private I_M_MovementLine[] m_lines = null;
    /**
     * Confirmations
     */
    private I_M_MovementConfirm[] m_confirms = null;
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
     * Standard Constructor
     *
     * @param M_Movement_ID id
     */
    public MMovement(int M_Movement_ID) {
        super(M_Movement_ID);
        if (M_Movement_ID == 0) {
            setDocAction(DOCACTION_Complete); // CO
            setDocStatus(DOCSTATUS_Drafted); // DR
            setIsApproved(false);
            setIsInTransit(false);
            setMovementDate(new Timestamp(System.currentTimeMillis())); // @#Date@
            setPosted(false);
            super.setProcessed(false);
        }
    } //	MMovement

    /**
     * Load Constructor
     *
     */
    public MMovement(Row row) {
        super(row);
    } //	MMovement

    /**
     * Get Lines
     *
     * @param requery requery
     * @return array of lines
     */
    public I_M_MovementLine[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        //
        final String whereClause = "M_Movement_ID=?";
        List<I_M_MovementLine> list =
                new Query<I_M_MovementLine>(I_M_MovementLine.Table_Name, whereClause)
                        .setParameters(getMovementId())
                        .setOrderBy(MMovementLine.COLUMNNAME_Line)
                        .list();
        m_lines = new I_M_MovementLine[list.size()];
        list.toArray(m_lines);
        return m_lines;
    } //	getLines

    /**
     * Get Confirmations
     *
     * @param requery requery
     * @return array of Confirmations
     */
    public I_M_MovementConfirm[] getConfirmations(boolean requery) {
        if (m_confirms != null && !requery) return m_confirms;

        List<I_M_MovementConfirm> list =
                new Query<I_M_MovementConfirm>(I_M_MovementConfirm.Table_Name, "M_Movement_ID=?")
                        .setParameters(getId())
                        .list();
        m_confirms = list.toArray(new I_M_MovementConfirm[0]);
        return m_confirms;
    } //	getConfirmations

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
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        return dt.getNameTrl() + " " + getDocumentNo();
    } //	getDocumentInfo

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (getDocumentTypeId() == 0) {
            DocumentType[] types = MDocTypeKt.getDocumentTypeOfDocBaseType(MDocType.DOCBASETYPE_MaterialMovement);
            if (types.length > 0) // 	get first
                setDocumentTypeId(types[0].getDocTypeId());
            else {
                log.saveError("Error", MsgKt.parseTranslation("@NotFound@ @C_DocType_ID@"));
                return false;
            }
        }
        return true;
    } //	beforeSave

    /**
     * Set Processed. Propergate to Lines/Taxes
     *
     * @param processed processed
     */
    @Override
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        if (getId() == 0) return;
        final String sql = "UPDATE M_MovementLine SET Processed=? WHERE M_Movement_ID=?";
        int noLine = executeUpdateEx(sql, new Object[]{processed, getId()});
        m_lines = null;
        if (log.isLoggable(Level.FINE)) log.fine("Processed=" + processed + " - Lines=" + noLine);
    } //	setProcessed

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
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());

        //	Std Period open?
        if (!MPeriod.isOpen(getMovementDate(), dt.getDocBaseType(), getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        I_M_MovementLine[] lines = getLines(false);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        // Validate mandatory ASI on lines - IDEMPIERE-1770 - ASI validation must be moved to
        // MMovement.prepareIt
        for (I_M_MovementLine line : lines) {
            //      Mandatory Instance
            I_M_Product product = line.getProduct();
            if (line.getAttributeSetInstanceId() == 0) {
                if (product != null && product.isASIMandatory(true)) {
                    if (!product
                            .getAttributeSet()
                            .excludeTableEntry(MMovementLine.Table_ID, true)) { // outgoing
                        BigDecimal qtyDiff = line.getMovementQty();
                        // verify if the ASIs are captured on lineMA
                        MMovementLineMA[] mas =
                                MMovementLineMA.get(line.getMovementLineId());
                        BigDecimal qtyma = Env.ZERO;
                        for (MMovementLineMA ma : mas) {
                            if (!ma.isAutoGenerated()) {
                                qtyma = qtyma.add(ma.getMovementQty());
                            }
                        }
                        if (qtyma.subtract(qtyDiff).signum() != 0) {
                            m_processMsg =
                                    "@Line@ " + line.getLine() + ": @FillMandatory@ @M_AttributeSetInstance_ID@";
                            return DocAction.Companion.getSTATUS_Invalid();
                        }
                    }
                }
            }
            if (line.getMAttributeSetInstanceToId() == 0) {
                if (product != null
                        && product.isASIMandatory(false)
                        && line.getMAttributeSetInstanceToId() == 0) {
                    if (!product
                            .getAttributeSet()
                            .excludeTableEntry(MMovementLine.Table_ID, false)) { // incoming
                        m_processMsg =
                                "@Line@ " + line.getLine() + ": @FillMandatory@ @M_AttributeSetInstanceTo_ID@";
                        return DocAction.Companion.getSTATUS_Invalid();
                    }
                }
            } //      ASI
        }

        //	Confirmation
        if (dt.isInTransit()) createConfirmation();

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        if (!DOCACTION_Complete.equals(getDocAction())) setDocAction(DOCACTION_Complete);
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

    /**
     * Create Movement Confirmation
     */
    private void createConfirmation() {
        I_M_MovementConfirm[] confirmations = getConfirmations(false);
        if (confirmations.length > 0) return;

        //	Create Confirmation
        MMovementConfirm.create(this, false);
    } //	createConfirmation

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

        // Set the definite document number after completed (if needed)
        setDefiniteDocumentNo();

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
        if (m_processMsg != null)
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());

        //	Outstanding (not processed) Incoming Confirmations ?
        I_M_MovementConfirm[] confirmations = getConfirmations(true);
        for (I_M_MovementConfirm confirm : confirmations) {
            if (!confirm.isProcessed()) {
                m_processMsg = "Open: @M_MovementConfirm_ID@ - " + confirm.getDocumentNo();
                return new CompleteActionResult(DocAction.Companion.getSTATUS_InProgress());
            }
        }

        //	Implicit Approval
        if (!isApproved()) approveIt();
        if (log.isLoggable(Level.INFO)) log.info(toString());

        StringBuilder errors = new StringBuilder();
        //
        I_M_MovementLine[] lines = getLines(false);
        for (I_M_MovementLine line : lines) {
            MTransaction trxFrom = null;

            // Stock Movement - Counterpart MOrder.reserveStock
            I_M_Product product = line.getProduct();
            try {
                if (product != null && product.isStocked()) {
                    // Ignore the Material Policy when is Reverse Correction
                    if (!isReversal()) {
                        BigDecimal qtyOnLineMA =
                                MMovementLineMA.getManualQty(line.getMovementLineId());
                        BigDecimal movementQty = line.getMovementQty();

                        if (qtyOnLineMA.compareTo(movementQty) > 0) {
                            // More then line qty on attribute tab for line 10
                            m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + line.getLine();
                            return new CompleteActionResult(DOCSTATUS_Invalid);
                        }

                        checkMaterialPolicy(line, movementQty.subtract(qtyOnLineMA));
                    }

                    if (line.getAttributeSetInstanceId() == 0) {
                        MMovementLineMA[] mas =
                                MMovementLineMA.get(line.getMovementLineId());
                        for (MMovementLineMA ma : mas) {
                            //
                            MLocator locator = new MLocator(line.getLocatorId());
                            // Update Storage
                            if (!MStorageOnHand.add(

                                    locator.getWarehouseId(),
                                    line.getLocatorId(),
                                    line.getProductId(),
                                    ma.getAttributeSetInstanceId(),
                                    ma.getMovementQty().negate(),
                                    ma.getDateMaterialPolicy()
                            )) {
                                String lastError = CLogger.retrieveErrorString("");
                                m_processMsg = "Cannot correct Inventory OnHand (MA) - " + lastError;
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }

                            int M_AttributeSetInstanceTo_ID = line.getMAttributeSetInstanceToId();
                            // only can be same asi if locator is different
                            if (M_AttributeSetInstanceTo_ID == 0
                                    && line.getLocatorId() != line.getLocatorToId()) {
                                M_AttributeSetInstanceTo_ID = ma.getAttributeSetInstanceId();
                            }
                            // Update Storage
                            MLocator locatorTo = new MLocator(line.getLocatorToId());
                            if (!MStorageOnHand.add(

                                    locatorTo.getWarehouseId(),
                                    line.getLocatorToId(),
                                    line.getProductId(),
                                    M_AttributeSetInstanceTo_ID,
                                    ma.getMovementQty(),
                                    ma.getDateMaterialPolicy()
                            )) {
                                String lastError = CLogger.retrieveErrorString("");
                                m_processMsg = "Cannot correct Inventory OnHand (MA) - " + lastError;
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }

                            //
                            trxFrom =
                                    new MTransaction(

                                            line.getOrgId(),
                                            MTransaction.MOVEMENTTYPE_MovementFrom,
                                            line.getLocatorId(),
                                            line.getProductId(),
                                            ma.getAttributeSetInstanceId(),
                                            ma.getMovementQty().negate(),
                                            getMovementDate()
                                    );
                            trxFrom.setMovementLineId(line.getMovementLineId());
                            if (!trxFrom.save()) {
                                m_processMsg = "Transaction From not inserted (MA)";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                            //
                            MTransaction trxTo =
                                    new MTransaction(

                                            line.getOrgId(),
                                            MTransaction.MOVEMENTTYPE_MovementTo,
                                            line.getLocatorToId(),
                                            line.getProductId(),
                                            M_AttributeSetInstanceTo_ID,
                                            ma.getMovementQty(),
                                            getMovementDate()
                                    );
                            trxTo.setMovementLineId(line.getMovementLineId());
                            if (!trxTo.save()) {
                                m_processMsg = "Transaction To not inserted (MA)";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                        }
                    }
                    //	Fallback - We have ASI
                    if (trxFrom == null) {
                        Timestamp dateMPolicy = null;
                        MStorageOnHand[] storages;
                        if (line.getMovementQty().compareTo(Env.ZERO) > 0) {
                            // Find Date Material Policy bases on ASI
                            storages =
                                    MStorageOnHand.getWarehouse(

                                            0,
                                            line.getProductId(),
                                            line.getAttributeSetInstanceId(),
                                            null,
                                            MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()),
                                            false,
                                            line.getLocatorId(),
                                            null);
                        } else {
                            // Case of reversal
                            storages =
                                    MStorageOnHand.getWarehouse(

                                            0,
                                            line.getProductId(),
                                            line.getMAttributeSetInstanceToId(),
                                            null,
                                            MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()),
                                            false,
                                            line.getLocatorToId(),
                                            null);
                        }
                        for (MStorageOnHand storage : storages) {
                            if (storage.getQtyOnHand().compareTo(line.getMovementQty()) >= 0) {
                                dateMPolicy = storage.getDateMaterialPolicy();
                                break;
                            }
                        }

                        if (dateMPolicy == null && storages.length > 0)
                            dateMPolicy = storages[0].getDateMaterialPolicy();

                        MLocator locator = new MLocator(line.getLocatorId());
                        // Update Storage
                        Timestamp effDateMPolicy = dateMPolicy;
                        if (dateMPolicy == null && line.getMovementQty().negate().signum() > 0)
                            effDateMPolicy = getMovementDate();
                        if (!MStorageOnHand.add(

                                locator.getWarehouseId(),
                                line.getLocatorId(),
                                line.getProductId(),
                                line.getAttributeSetInstanceId(),
                                line.getMovementQty().negate(),
                                effDateMPolicy
                        )) {
                            String lastError = CLogger.retrieveErrorString("");
                            m_processMsg = "Cannot correct Inventory OnHand (MA) - " + lastError;
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }

                        // Update Storage
                        effDateMPolicy = dateMPolicy;
                        if (dateMPolicy == null && line.getMovementQty().signum() > 0)
                            effDateMPolicy = getMovementDate();
                        MLocator locatorTo = new MLocator(line.getLocatorToId());
                        if (!MStorageOnHand.add(

                                locatorTo.getWarehouseId(),
                                line.getLocatorToId(),
                                line.getProductId(),
                                line.getMAttributeSetInstanceToId(),
                                line.getMovementQty(),
                                effDateMPolicy
                        )) {
                            String lastError = CLogger.retrieveErrorString("");
                            m_processMsg = "Cannot correct Inventory OnHand (MA) - " + lastError;
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }

                        //
                        trxFrom =
                                new MTransaction(

                                        line.getOrgId(),
                                        MTransaction.MOVEMENTTYPE_MovementFrom,
                                        line.getLocatorId(),
                                        line.getProductId(),
                                        line.getAttributeSetInstanceId(),
                                        line.getMovementQty().negate(),
                                        getMovementDate()
                                );
                        trxFrom.setMovementLineId(line.getMovementLineId());
                        if (!trxFrom.save()) {
                            m_processMsg = "Transaction From not inserted";
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                        //
                        MTransaction trxTo =
                                new MTransaction(

                                        line.getOrgId(),
                                        MTransaction.MOVEMENTTYPE_MovementTo,
                                        line.getLocatorToId(),
                                        line.getProductId(),
                                        line.getMAttributeSetInstanceToId(),
                                        line.getMovementQty(),
                                        getMovementDate()
                                );
                        trxTo.setMovementLineId(line.getMovementLineId());
                        if (!trxTo.save()) {
                            m_processMsg = "Transaction To not inserted";
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                    } //	Fallback
                } // product stock
            } catch (NegativeInventoryDisallowedException e) {
                log.severe(e.getMessage());
                errors
                        .append(MsgKt.getElementTranslation("Line"))
                        .append(" ")
                        .append(line.getLine())
                        .append(": ");
                errors.append(e.getMessage()).append("\n");
            }
        } //	for all lines

        if (errors.toString().length() > 0) {
            m_processMsg = errors.toString();
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
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
        setDocAction(DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Set the definite document number after completed
     */
    private void setDefiniteDocumentNo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        if (dt.isOverwriteDateOnComplete()) {
            setMovementDate(new Timestamp(System.currentTimeMillis()));
            MPeriod.testPeriodOpen(getMovementDate(), getDocumentTypeId(), getOrgId());
        }
        if (dt.isOverwriteSeqOnComplete()) {
            String value = MSequence.getDocumentNo(getDocumentTypeId(), true, this);
            if (value != null) setDocumentNo(value);
        }
    }

    /**
     * Check Material Policy Sets line ASI
     */
    private void checkMaterialPolicy(I_M_MovementLine line, BigDecimal qtyToDeliver) {

        int no = MMovementLineMA.deleteMovementLineMA(line.getMovementLineId());
        if (no > 0) if (log.isLoggable(Level.CONFIG)) log.config("Delete old #" + no);

        if (Env.ZERO.compareTo(qtyToDeliver) == 0) return;

        boolean needSave = false;

        //	Attribute Set Instance
        if (line.getAttributeSetInstanceId() == 0) {

            MProduct product = MProduct.get(line.getProductId());
            String MMPolicy = product.getMMPolicy();
            MStorageOnHand[] storages =
                    MStorageOnHand.getWarehouse(

                            0,
                            line.getProductId(),
                            0,
                            null,
                            MClient.MMPOLICY_FiFo.equals(MMPolicy),
                            true,
                            line.getLocatorId(),
                            null);

            for (MStorageOnHand storage : storages) {
                if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0) {
                    MMovementLineMA ma =
                            new MMovementLineMA(
                                    line,
                                    storage.getAttributeSetInstanceId(),
                                    qtyToDeliver,
                                    storage.getDateMaterialPolicy(),
                                    true);
                    ma.saveEx();
                    qtyToDeliver = Env.ZERO;
                    if (log.isLoggable(Level.FINE)) log.fine(ma + ", QtyToDeliver=" + qtyToDeliver);
                } else {
                    MMovementLineMA ma =
                            new MMovementLineMA(
                                    line,
                                    storage.getAttributeSetInstanceId(),
                                    storage.getQtyOnHand(),
                                    storage.getDateMaterialPolicy(),
                                    true);
                    ma.saveEx();
                    qtyToDeliver = qtyToDeliver.subtract(storage.getQtyOnHand());
                    if (log.isLoggable(Level.FINE)) log.fine(ma + ", QtyToDeliver=" + qtyToDeliver);
                }
                if (qtyToDeliver.signum() == 0) break;
            }

            //	No AttributeSetInstance found for remainder
            if (qtyToDeliver.signum() != 0) {
                I_M_MovementLineMA ma =
                        MMovementLineMA.addOrCreate(line, 0, qtyToDeliver, getMovementDate(), true);
                ma.saveEx();
                if (log.isLoggable(Level.FINE)) log.fine("##: " + ma);
            }
        } //	attributeSetInstance

    } //	checkMaterialPolicy

    /**
     * Void Document.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());

        if (DOCSTATUS_Closed.equals(getDocStatus())
                || DOCSTATUS_Reversed.equals(getDocStatus())
                || DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            return false;
        }

        //	Not Processed
        if (DOCSTATUS_Drafted.equals(getDocStatus())
                || DOCSTATUS_Invalid.equals(getDocStatus())
                || DOCSTATUS_InProgress.equals(getDocStatus())
                || DOCSTATUS_Approved.equals(getDocStatus())
                || DOCSTATUS_NotApproved.equals(getDocStatus())) {
            // Before Void
            m_processMsg =
                    ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
            if (m_processMsg != null) return false;

            //	Set lines to 0
            I_M_MovementLine[] lines = getLines(false);
            for (I_M_MovementLine line : lines) {
                BigDecimal old = line.getMovementQty();
                if (old.compareTo(Env.ZERO) != 0) {
                    line.setMovementQty(Env.ZERO);
                    line.addDescription("Void (" + old + ")");
                    line.saveEx();
                }
            }
        } else {
            boolean accrual = false;
            try {
                MPeriod.testPeriodOpen(getMovementDate(), getDocumentTypeId(), getOrgId());
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
        setDocAction(DOCACTION_None);
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
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSECORRECT);
        if (m_processMsg != null) return false;

        MMovement reversal = reverse(false);
        if (reversal == null) return false;

        m_processMsg = reversal.getDocumentNo();

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        return m_processMsg == null;
    } //	reverseCorrectionIt

    private MMovement reverse(boolean accrual) {
        Timestamp reversalDate = accrual ? Env.getContextAsDate() : getMovementDate();
        if (reversalDate == null) {
            reversalDate = new Timestamp(System.currentTimeMillis());
        }

        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        if (!MPeriod.isOpen(reversalDate, dt.getDocBaseType(), getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return null;
        }

        //	Deep Copy
        MMovement reversal = new MMovement(0);
        copyValues(this, reversal, getClientId(), getOrgId());
        reversal.setDocStatus(DOCSTATUS_Drafted);
        reversal.setDocAction(DOCACTION_Complete);
        reversal.setIsApproved(false);
        reversal.setIsInTransit(false);
        reversal.setPosted(false);
        reversal.setProcessed(false);
        reversal.setMovementDate(reversalDate);
        reversal.setDocumentNo(getDocumentNo() + REVERSE_INDICATOR); // 	indicate reversals
        reversal.addDescription("{->" + getDocumentNo() + ")");
        // FR [ 1948157  ]
        reversal.setReversalId(getMovementId());
        if (!reversal.save()) {
            m_processMsg = "Could not create Movement Reversal";
            return null;
        }
        reversal.setReversal(true);
        //	Reverse Line Qty
        I_M_MovementLine[] oLines = getLines(true);
        for (I_M_MovementLine oLine : oLines) {
            MMovementLine rLine = new MMovementLine(0);
            copyValues((PO)oLine, rLine, oLine.getClientId(), oLine.getOrgId());
            rLine.setMovementId(reversal.getMovementId());
            // AZ Goodwill
            // store original (voided/reversed) document line
            rLine.setReversalLineId(oLine.getMovementLineId());
            //
            rLine.setMovementQty(rLine.getMovementQty().negate());
            rLine.setTargetQty(Env.ZERO);
            rLine.setScrappedQty(Env.ZERO);
            rLine.setConfirmedQty(Env.ZERO);
            rLine.setProcessed(false);
            if (!rLine.save()) {
                m_processMsg = "Could not create Movement Reversal Line";
                return null;
            }

            // We need to copy MA
            if (rLine.getAttributeSetInstanceId() == 0) {
                MMovementLineMA[] mas =
                        MMovementLineMA.get(oLine.getMovementLineId());
                for (MMovementLineMA mMovementLineMA : mas) {
                    MMovementLineMA ma =
                            new MMovementLineMA(
                                    rLine,
                                    mMovementLineMA.getAttributeSetInstanceId(),
                                    mMovementLineMA.getMovementQty().negate(),
                                    mMovementLineMA.getDateMaterialPolicy(),
                                    true);
                    ma.saveEx();
                }
            }
        }
        //
        if (!reversal.processIt(DocAction.Companion.getACTION_Complete())) {
            m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
            return null;
        }
        reversal.closeIt();
        reversal.setDocStatus(DOCSTATUS_Reversed);
        reversal.setDocAction(DOCACTION_None);
        reversal.saveEx();

        //	Update Reversed (this)
        addDescription("(" + reversal.getDocumentNo() + "<-)");
        // FR [ 1948157  ]
        setReversalId(reversal.getMovementId());
        setProcessed(true);
        setDocStatus(DOCSTATUS_Reversed); // 	may come from void
        setDocAction(DOCACTION_None);

        return reversal;
    }

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

        MMovement reversal = reverse(true);
        if (reversal == null) return false;

        m_processMsg = reversal.getDocumentNo();

        // After reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
        return m_processMsg == null;
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
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    public int getDocumentUserId() {
        return getCreatedBy();
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
} //	MMovement
