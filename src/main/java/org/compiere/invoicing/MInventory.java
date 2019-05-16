package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MAcctSchema;
import org.compiere.accounting.MClient;
import org.compiere.accounting.MClientInfo;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MPeriod;
import org.compiere.accounting.MProduct;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.accounting.NegativeInventoryDisallowedException;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.AccountingSchema;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_M_AttributeSet;
import org.compiere.model.I_M_Cost;
import org.compiere.model.I_M_Inventory;
import org.compiere.model.I_M_InventoryLine;
import org.compiere.model.I_M_InventoryLineMA;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_StorageOnHand;
import org.compiere.model.I_M_Warehouse;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.MSequence;
import org.compiere.orm.PO;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.production.MTransaction;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdateEx;

/**
 * Physical Inventory Model
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 1948157 ] Is necessary the reference for document reverse
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @author Armen Rizal, Goodwill Consulting
 * <li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 * @version $Id: MInventory.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 * @see http://sourceforge.net/tracker/?func=detail&atid=879335&aid=1948157&group_id=176962
 */
public class MInventory extends X_M_Inventory implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -2155186682727239214L;
    /**
     * Reversal Indicator
     */
    public static String REVERSE_INDICATOR = "^";
    /**
     * Lines
     */
    private I_M_InventoryLine[] m_lines = null;
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
     * @param M_Inventory_ID id
     */
    public MInventory(int M_Inventory_ID) {
        super(M_Inventory_ID);
        if (M_Inventory_ID == 0) {
            setMovementDate(new Timestamp(System.currentTimeMillis()));
            setDocAction(X_M_Inventory.DOCACTION_Complete); // CO
            setDocStatus(X_M_Inventory.DOCSTATUS_Drafted); // DR
            setIsApproved(false);
            setMovementDate(new Timestamp(System.currentTimeMillis())); // @#Date@
            setPosted(false);
            setProcessed(false);
        }
    } //	MInventory

    /**
     * Load Constructor
     */
    public MInventory(Row row) {
        super(row);
    } //	MInventory

    /**
     * Warehouse Constructor
     *
     * @param wh
     */
    public MInventory(I_M_Warehouse wh) {
        this(0);
        setClientOrg(wh);
        setWarehouseId(wh.getWarehouseId());
    }

    /**
     * Get Lines
     *
     * @param requery requery
     * @return array of lines
     */
    public I_M_InventoryLine[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        //
        List<I_M_InventoryLine> list =
                new Query<I_M_InventoryLine>(I_M_InventoryLine.Table_Name, "M_Inventory_ID=?")
                        .setParameters(getId())
                        .setOrderBy(MInventoryLine.COLUMNNAME_Line)
                        .list();
        m_lines = list.toArray(new I_M_InventoryLine[0]);
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
        else {
            setDescription(desc + " | " + description);
        }
    } //	addDescription

    /**
     * Overwrite Client/Org - from Import.
     *
     * @param AD_Client_ID client
     * @param AD_Org_ID    org
     */
    public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
        super.setClientOrg(AD_Client_ID, AD_Org_ID);
    } //	setClientOrg

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MInventory[" + getId() +
                "-" +
                getDocumentNo() +
                ",M_Warehouse_ID=" +
                getWarehouseId() +
                "]";
    } //	toString

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
            log.saveError(
                    "FillMandatory", MsgKt.getElementTranslation(I_M_Inventory.COLUMNNAME_C_DocType_ID));
            return false;
        }
        return true;
    } //	beforeSave

    /**
     * Set Processed. Propagate to Lines/Taxes
     *
     * @param processed processed
     */
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        if (getId() == 0) return;
        //
        final String sql = "UPDATE M_InventoryLine SET Processed=? WHERE M_Inventory_ID=?";
        int noLine =
                executeUpdateEx(sql, new Object[]{processed, getInventoryId()});
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
        setDocAction(X_M_Inventory.DOCACTION_Prepare);
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

                getMovementDate(),
                MDocType.DOCBASETYPE_MaterialPhysicalInventory,
                getOrgId());
        I_M_InventoryLine[] lines = getLines(false);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        // Validate mandatory ASI on lines - IDEMPIERE-1770 - ASI validation must be moved to
        // MInventory.prepareIt
        for (I_M_InventoryLine line : lines) {
            //	Product requires ASI
            if (line.getAttributeSetInstanceId() == 0) {
                MProduct product = MProduct.get(line.getProductId());
                if (product != null && product.isASIMandatory(line.isSOTrx())) {
                    if (!product
                            .getAttributeSet()
                            .excludeTableEntry(MInventoryLine.Table_ID, line.isSOTrx())) {
                        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
                        String docSubTypeInv = dt.getDocSubTypeInv();
                        BigDecimal qtyDiff = line.getQtyInternalUse();
                        if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv))
                            qtyDiff = line.getQtyBook().subtract(line.getQtyCount());
                        // verify if the ASIs are captured on lineMA
                        MInventoryLineMA[] mas =
                                MInventoryLineMA.get(line.getInventoryLineId());
                        BigDecimal qtyma = Env.ZERO;
                        for (MInventoryLineMA ma : mas) {
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
            } //	No ASI
        }

        //	TODO: Add up Amounts
        //	setApprovalAmt();

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        if (!X_M_Inventory.DOCACTION_Complete.equals(getDocAction()))
            setDocAction(X_M_Inventory.DOCACTION_Complete);
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
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        String docSubTypeInv = dt.getDocSubTypeInv();
        if (Util.isEmpty(docSubTypeInv)) {
            m_processMsg = "Document inventory subtype not configured, cannot complete";
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

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
        if (log.isLoggable(Level.INFO)) log.info(toString());

        StringBuilder errors = new StringBuilder();
        I_M_InventoryLine[] lines = getLines(false);
        for (I_M_InventoryLine line : lines) {
            if (!line.isActive()) continue;

            I_M_Product product = line.getProduct();
            try {
                BigDecimal qtyDiff = Env.ZERO;
                if (MDocType.DOCSUBTYPEINV_InternalUseInventory.equals(docSubTypeInv))
                    qtyDiff = line.getQtyInternalUse().negate();
                else if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv))
                    qtyDiff = line.getQtyCount().subtract(line.getQtyBook());
                else if (MDocType.DOCSUBTYPEINV_CostAdjustment.equals(docSubTypeInv)) {
                    if (!isReversal()) {
                        BigDecimal currentCost = line.getCurrentCostPrice();
                        ClientWithAccounting client = MClientKt.getClientWithAccounting(getClientId());
                        AccountingSchema as = client.getAcctSchema();
                        AccountingSchema[] ass = MAcctSchema.getClientAcctSchema(client.getId());

                        if (as.getCurrencyId() != getCurrencyId()) {
                            for (AccountingSchema a : ass) {
                                if (a.getCurrencyId() == getCurrencyId()) as = a;
                            }
                        }

                        I_M_Cost cost =
                                product.getCostingRecord(
                                        as, getOrgId(), line.getAttributeSetInstanceId(), getCostingMethod());
                        if (cost != null && cost.getCurrentCostPrice().compareTo(currentCost) != 0) {
                            m_processMsg = "Current Cost for Line " + line.getLine() + " have changed.";
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                    }
                }

                // If Quantity Count minus Quantity Book = Zero, then no change in Inventory
                if (qtyDiff.signum() == 0) continue;

                // Ignore the Material Policy when is Reverse Correction
                if (!isReversal()) {
                    BigDecimal qtyOnLineMA =
                            MInventoryLineMA.getManualQty(line.getInventoryLineId());

                    if (qtyDiff.signum() < 0) {
                        if (qtyOnLineMA.compareTo(qtyDiff) < 0) {
                            m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + line.getLine();
                            return new CompleteActionResult(X_M_Inventory.DOCSTATUS_Invalid);
                        }
                    } else {
                        if (qtyOnLineMA.compareTo(qtyDiff) > 0) {
                            m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + line.getLine();
                            return new CompleteActionResult(X_M_Inventory.DOCSTATUS_Invalid);
                        }
                    }
                    checkMaterialPolicy(line, qtyDiff.subtract(qtyOnLineMA));
                }
                //	Stock Movement - Counterpart MOrder.reserveStock
                if (product != null && product.isStocked()) {
                    log.fine("Material Transaction");
                    MTransaction mtrx = null;

                    // If AttributeSetInstance = Zero then create new  AttributeSetInstance use Inventory Line
                    // MA else use current AttributeSetInstance
                    if (line.getAttributeSetInstanceId() == 0 || qtyDiff.compareTo(Env.ZERO) == 0) {
                        MInventoryLineMA[] mas =
                                MInventoryLineMA.get(line.getInventoryLineId());

                        for (MInventoryLineMA ma : mas) {
                            BigDecimal QtyMA = ma.getMovementQty();
                            BigDecimal QtyNew = QtyMA.add(qtyDiff);
                            if (log.isLoggable(Level.FINE))
                                log.fine("Diff=" + qtyDiff + " - Instance OnHand=" + QtyMA + "->" + QtyNew);

                            if (!MStorageOnHand.add(

                                    getWarehouseId(),
                                    line.getLocatorId(),
                                    line.getProductId(),
                                    ma.getAttributeSetInstanceId(),
                                    QtyMA.negate(),
                                    ma.getDateMaterialPolicy()
                            )) {
                                String lastError = CLogger.retrieveErrorString("");
                                m_processMsg = "Cannot correct Inventory (MA) - " + lastError;
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }

                            // Only Update Date Last Inventory if is a Physical Inventory
                            if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv)) {
                                I_M_StorageOnHand storage =
                                        MStorageOnHand.get(

                                                line.getLocatorId(),
                                                line.getProductId(),
                                                ma.getAttributeSetInstanceId(),
                                                ma.getDateMaterialPolicy());
                                storage.setDateLastInventory(getMovementDate());
                                if (!storage.save()) {
                                    m_processMsg = "Storage not updated(2)";
                                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                                }
                            }

                            String m_MovementType = null;
                            if (QtyMA.negate().compareTo(Env.ZERO) > 0)
                                m_MovementType = MTransaction.MOVEMENTTYPE_InventoryIn;
                            else m_MovementType = MTransaction.MOVEMENTTYPE_InventoryOut;
                            //	Transaction
                            mtrx =
                                    new MTransaction(

                                            line.getOrgId(),
                                            m_MovementType,
                                            line.getLocatorId(),
                                            line.getProductId(),
                                            ma.getAttributeSetInstanceId(),
                                            QtyMA.negate(),
                                            getMovementDate()
                                    );

                            mtrx.setInventoryLineId(line.getInventoryLineId());
                            if (!mtrx.save()) {
                                m_processMsg = "Transaction not inserted(2)";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }

                            qtyDiff = QtyNew;
                        }
                    }

                    // sLine.getAttributeSetInstanceId() != 0
                    // Fallback
                    if (mtrx == null) {
                        Timestamp dateMPolicy = qtyDiff.signum() > 0 ? getMovementDate() : null;
                        if (line.getAttributeSetInstanceId() > 0) {
                            Timestamp t =
                                    MStorageOnHand.getDateMaterialPolicy(
                                            line.getProductId(),
                                            line.getAttributeSetInstanceId(),
                                            line.getLocatorId());
                            if (t != null) dateMPolicy = t;
                        }

                        // Fallback: Update Storage - see also VMatch.createMatchRecord
                        if (!MStorageOnHand.add(

                                getWarehouseId(),
                                line.getLocatorId(),
                                line.getProductId(),
                                line.getAttributeSetInstanceId(),
                                qtyDiff,
                                dateMPolicy
                        )) {
                            String lastError = CLogger.retrieveErrorString("");
                            m_processMsg = "Cannot correct Inventory OnHand (MA) - " + lastError;
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }

                        // Only Update Date Last Inventory if is a Physical Inventory
                        if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv)) {
                            I_M_StorageOnHand storage =
                                    MStorageOnHand.get(

                                            line.getLocatorId(),
                                            line.getProductId(),
                                            line.getAttributeSetInstanceId(),
                                            dateMPolicy);

                            storage.setDateLastInventory(getMovementDate());
                            if (!storage.save()) {
                                m_processMsg = "Storage not updated(2)";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                        }

                        String m_MovementType = null;
                        if (qtyDiff.compareTo(Env.ZERO) > 0)
                            m_MovementType = MTransaction.MOVEMENTTYPE_InventoryIn;
                        else m_MovementType = MTransaction.MOVEMENTTYPE_InventoryOut;
                        //	Transaction
                        mtrx =
                                new MTransaction(

                                        line.getOrgId(),
                                        m_MovementType,
                                        line.getLocatorId(),
                                        line.getProductId(),
                                        line.getAttributeSetInstanceId(),
                                        qtyDiff,
                                        getMovementDate()
                                );
                        mtrx.setInventoryLineId(line.getInventoryLineId());
                        if (!mtrx.save()) {
                            m_processMsg = "Transaction not inserted(2)";
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                    } //	Fallback
                } //	stock movement
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
        setDocAction(X_M_Inventory.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Set the definite document number after completed
     */
    private void setDefiniteDocumentNo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        if (dt.isOverwriteDateOnComplete()) {
            setMovementDate(new Timestamp(System.currentTimeMillis()));
            MPeriod.testPeriodOpen(

                    getMovementDate(),
                    MDocType.DOCBASETYPE_MaterialPhysicalInventory,
                    getOrgId());
        }
        if (dt.isOverwriteSeqOnComplete()) {
            String value = MSequence.getDocumentNo(getDocumentTypeId(), true, this);
            if (value != null) setDocumentNo(value);
        }
    }

    /**
     * Check Material Policy.
     */
    private void checkMaterialPolicy(I_M_InventoryLine line, BigDecimal qtyDiff) {

        int no = MInventoryLineMA.deleteInventoryLineMA(line.getInventoryLineId());
        if (no > 0) if (log.isLoggable(Level.CONFIG)) log.config("Delete old #" + no);

        if (qtyDiff.compareTo(Env.ZERO) == 0) return;

        //	Attribute Set Instance
        if (line.getAttributeSetInstanceId() == 0) {
            MProduct product = MProduct.get(line.getProductId());
            if (qtyDiff.signum() > 0) // 	Incoming Trx
            {
                // auto balance negative on hand
                MStorageOnHand[] storages =
                        MStorageOnHand.getWarehouseNegative(

                                getWarehouseId(),
                                line.getProductId(),
                                0,
                                null,
                                MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()),
                                line.getLocatorId(),
                                false);
                for (MStorageOnHand storage : storages) {
                    if (storage.getQtyOnHand().signum() < 0) {
                        BigDecimal maQty = qtyDiff;
                        if (maQty.compareTo(storage.getQtyOnHand().negate()) > 0) {
                            maQty = storage.getQtyOnHand().negate();
                        }

                        // backward compatibility: -ve in MA is incoming trx, +ve in MA is outgoing trx
                        MInventoryLineMA lineMA =
                                new MInventoryLineMA(
                                        line,
                                        storage.getAttributeSetInstanceId(),
                                        maQty.negate(),
                                        storage.getDateMaterialPolicy(),
                                        true);
                        lineMA.saveEx();

                        qtyDiff = qtyDiff.subtract(maQty);
                        if (qtyDiff.compareTo(Env.ZERO) == 0) break;
                    }
                }

                if (qtyDiff.compareTo(Env.ZERO) > 0) {
                    // AttributeSetInstance enable
                    I_M_AttributeSet as = line.getProduct().getMAttributeSet();
                    if (as != null && as.isInstanceAttribute()) {
                        // add quantity to last attributesetinstance
                        storages =
                                MStorageOnHand.getWarehouse(

                                        getWarehouseId(),
                                        line.getProductId(),
                                        0,
                                        null,
                                        false,
                                        true,
                                        0,
                                        null);
                        for (MStorageOnHand storage : storages) {
                            BigDecimal maQty = qtyDiff;
                            // backward compatibility: -ve in MA is incoming trx, +ve in MA is outgoing trx
                            MInventoryLineMA lineMA =
                                    new MInventoryLineMA(
                                            line,
                                            storage.getAttributeSetInstanceId(),
                                            maQty.negate(),
                                            storage.getDateMaterialPolicy(),
                                            true);
                            lineMA.saveEx();
                            qtyDiff = qtyDiff.subtract(maQty);

                            if (qtyDiff.compareTo(Env.ZERO) == 0) break;
                        }
                    }
                    if (qtyDiff.compareTo(Env.ZERO) > 0) {
                        MClientInfo m_clientInfo = MClientInfo.get(getClientId());
                        MAcctSchema acctSchema =
                                new MAcctSchema(m_clientInfo.getAcctSchema1Id());
                        if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(acctSchema))) {
                            String sqlWhere =
                                    "M_Product_ID=? AND M_Locator_ID=? AND QtyOnHand = 0 AND M_AttributeSetInstance_ID > 0 ";
                            I_M_StorageOnHand storage =
                                    new Query<I_M_StorageOnHand>(MStorageOnHand.Table_Name, sqlWhere)
                                            .setParameters(line.getProductId(), line.getLocatorId())
                                            .setOrderBy(
                                                    MStorageOnHand.COLUMNNAME_DateMaterialPolicy
                                                            + ","
                                                            + MStorageOnHand.COLUMNNAME_M_AttributeSetInstance_ID)
                                            .first();

                            if (storage != null) {
                                I_M_InventoryLineMA lineMA =
                                        MInventoryLineMA.addOrCreate(
                                                line,
                                                storage.getAttributeSetInstanceId(),
                                                qtyDiff.negate(),
                                                getMovementDate(),
                                                true);
                                lineMA.saveEx();
                            } else {
                                String costingMethod = product.getCostingMethod(acctSchema);
                                StringBuilder localWhereClause =
                                        new StringBuilder("M_Product_ID =?")
                                                .append(" AND C_AcctSchema_ID=?")
                                                .append(" AND ce.CostingMethod = ? ")
                                                .append(" AND CurrentCostPrice <> 0 ");
                                I_M_Cost cost =
                                        new Query<I_M_Cost>(
                                                I_M_Cost.Table_Name,
                                                localWhereClause.toString()
                                        )
                                                .setParameters(line.getProductId(), acctSchema.getId(), costingMethod)
                                                .addJoinClause(
                                                        " INNER JOIN M_CostElement ce ON (M_Cost.M_CostElement_ID =ce.M_CostElement_ID ) ")
                                                .setOrderBy("Updated DESC")
                                                .first();
                                if (cost != null) {
                                    I_M_InventoryLineMA lineMA =
                                            MInventoryLineMA.addOrCreate(
                                                    line,
                                                    cost.getAttributeSetInstanceId(),
                                                    qtyDiff.negate(),
                                                    getMovementDate(),
                                                    true);
                                    lineMA.saveEx();
                                } else {
                                    m_processMsg = "Cannot retrieve cost of Inventory ";
                                }
                            }

                        } else {
                            I_M_InventoryLineMA lineMA =
                                    MInventoryLineMA.addOrCreate(line, 0, qtyDiff.negate(), getMovementDate(), true);
                            lineMA.saveEx();
                        }
                    }
                }
            } else //	Outgoing Trx
            {
                String MMPolicy = product.getMMPolicy();
                MStorageOnHand[] storages =
                        MStorageOnHand.getWarehouse(

                                getWarehouseId(),
                                line.getProductId(),
                                0,
                                null,
                                MClient.MMPOLICY_FiFo.equals(MMPolicy),
                                true,
                                line.getLocatorId(),
                                false);

                BigDecimal qtyToDeliver = qtyDiff.negate();
                for (MStorageOnHand storage : storages) {
                    if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0) {
                        MInventoryLineMA ma =
                                new MInventoryLineMA(
                                        line,
                                        storage.getAttributeSetInstanceId(),
                                        qtyToDeliver,
                                        storage.getDateMaterialPolicy(),
                                        true);
                        ma.saveEx();
                        qtyToDeliver = Env.ZERO;
                        if (log.isLoggable(Level.FINE)) log.fine(ma + ", QtyToDeliver=" + qtyToDeliver);
                    } else {
                        MInventoryLineMA ma =
                                new MInventoryLineMA(
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
                    I_M_InventoryLineMA lineMA =
                            MInventoryLineMA.addOrCreate(line, 0, qtyToDeliver, getMovementDate(), true);
                    lineMA.saveEx();
                    if (log.isLoggable(Level.FINE)) log.fine("##: " + lineMA);
                }
            } //	outgoing Trx
        } //	for all lines
    } //	checkMaterialPolicy

    /**
     * Void Document.
     *
     * @return false
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());

        if (X_M_Inventory.DOCSTATUS_Closed.equals(getDocStatus())
                || X_M_Inventory.DOCSTATUS_Reversed.equals(getDocStatus())
                || X_M_Inventory.DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            return false;
        }

        //	Not Processed
        if (X_M_Inventory.DOCSTATUS_Drafted.equals(getDocStatus())
                || X_M_Inventory.DOCSTATUS_Invalid.equals(getDocStatus())
                || X_M_Inventory.DOCSTATUS_InProgress.equals(getDocStatus())
                || X_M_Inventory.DOCSTATUS_Approved.equals(getDocStatus())
                || X_M_Inventory.DOCSTATUS_NotApproved.equals(getDocStatus())) {
            // Before Void
            m_processMsg =
                    ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
            if (m_processMsg != null) return false;

            //	Set lines to 0
            I_M_InventoryLine[] lines = getLines(false);
            for (I_M_InventoryLine line : lines) {
                BigDecimal oldCount = line.getQtyCount();
                BigDecimal oldInternal = line.getQtyInternalUse();
                if (oldCount.compareTo(line.getQtyBook()) != 0 || oldInternal.signum() != 0) {
                    line.setQtyInternalUse(Env.ZERO);
                    line.setQtyCount(line.getQtyBook());
                    String msgd = "Void (" +
                            oldCount +
                            "/" +
                            oldInternal +
                            ")";
                    line.addDescription(msgd);
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
        setDocAction(X_M_Inventory.DOCACTION_None);
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

        setDocAction(X_M_Inventory.DOCACTION_None);
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

        MInventory reversal = reverse(false);
        if (reversal == null) return false;

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        m_processMsg = reversal.getDocumentNo();

        return true;
    } //	reverseCorrectIt

    private MInventory reverse(boolean accrual) {
        Timestamp reversalDate = accrual ? Env.getContextAsDate() : getMovementDate();
        if (reversalDate == null) {
            reversalDate = new Timestamp(System.currentTimeMillis());
        }

        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        MPeriod.testPeriodOpen(reversalDate, dt.getDocBaseType(), getOrgId());

        //	Deep Copy
        MInventory reversal = new MInventory(0);
        PO.copyValues(this, reversal, getClientId(), getOrgId());
        reversal.setDocumentNo(getDocumentNo() + REVERSE_INDICATOR); // 	indicate reversals
        reversal.setMovementDate(reversalDate);
        reversal.setDocStatus(X_M_Inventory.DOCSTATUS_Drafted);
        reversal.setDocAction(X_M_Inventory.DOCACTION_Complete);
        reversal.setIsApproved(false);
        reversal.setPosted(false);
        reversal.setProcessed(false);
        StringBuilder msgd = new StringBuilder("{->").append(getDocumentNo()).append(")");
        reversal.addDescription(msgd.toString());
        // FR1948157
        reversal.setReversalId(getInventoryId());
        reversal.saveEx();
        reversal.setReversal(true);

        //	Reverse Line Qty
        I_M_InventoryLine[] oLines = getLines(true);
        for (I_M_InventoryLine oLine : oLines) {
            MInventoryLine rLine = new MInventoryLine(0);
            PO.copyValues((PO)oLine, rLine, oLine.getClientId(), oLine.getOrgId());
            rLine.setInventoryId(reversal.getInventoryId());
            rLine.setParent(reversal);
            // AZ Goodwill
            // store original (voided/reversed) document line
            rLine.setReversalLineId(oLine.getInventoryLineId());
            //
            rLine.setQtyBook(oLine.getQtyCount()); // 	switch
            rLine.setQtyCount(oLine.getQtyBook());
            rLine.setQtyInternalUse(oLine.getQtyInternalUse().negate());
            rLine.setNewCostPrice(oLine.getCurrentCostPrice());
            rLine.setCurrentCostPrice(oLine.getNewCostPrice());

            rLine.saveEx();

            // We need to copy MA
            if (rLine.getAttributeSetInstanceId() == 0) {
                MInventoryLineMA[] mas =
                        MInventoryLineMA.get(oLine.getInventoryLineId());
                for (int j = 0; j < mas.length; j++) {
                    MInventoryLineMA ma =
                            new MInventoryLineMA(
                                    rLine,
                                    mas[j].getAttributeSetInstanceId(),
                                    mas[j].getMovementQty().negate(),
                                    mas[j].getDateMaterialPolicy(),
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
        reversal.setDocStatus(X_M_Inventory.DOCSTATUS_Reversed);
        reversal.setDocAction(X_M_Inventory.DOCACTION_None);
        reversal.saveEx();

        //	Update Reversed (this)
        msgd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
        addDescription(msgd.toString());
        setProcessed(true);
        // FR1948157
        setReversalId(reversal.getInventoryId());
        setDocStatus(X_M_Inventory.DOCSTATUS_Reversed); // 	may come from void
        setDocAction(X_M_Inventory.DOCACTION_None);

        return reversal;
    }

    /**
     * Reverse Accrual
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

        MInventory reversal = reverse(true);
        if (reversal == null) return false;

        // After reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        m_processMsg = reversal.getDocumentNo();

        return true;
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
        return getUpdatedBy();
    } //	getDoc_User_ID

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
        return X_M_Inventory.DOCSTATUS_Completed.equals(ds)
                || X_M_Inventory.DOCSTATUS_Closed.equals(ds)
                || X_M_Inventory.DOCSTATUS_Reversed.equals(ds);
    } //	isComplete

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }
} //	MInventory
