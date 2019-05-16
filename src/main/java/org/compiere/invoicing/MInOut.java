package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MClient;
import org.compiere.accounting.MMatchInv;
import org.compiere.accounting.MMatchPO;
import org.compiere.accounting.MOrderLine;
import org.compiere.accounting.MPeriod;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.accounting.MStorageReservation;
import org.compiere.accounting.NegativeInventoryDisallowedException;
import org.compiere.crm.MBPartner;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.DocumentType;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_M_InOutConfirm;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.I_M_InOutLineMA;
import org.compiere.model.I_M_MatchInv;
import org.compiere.model.I_M_Product;
import org.compiere.order.MOrder;
import org.compiere.order.MRMALine;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgInfo;
import org.compiere.orm.MOrgInfoKt;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.MSequence;
import org.compiere.orm.MSysConfig;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.orm.TimeUtil;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.production.MTransaction;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.orm.POKt.I_ZERO;

public class MInOut extends org.compiere.order.MInOut implements DocAction, IPODoc {
    protected I_M_InOutLine[] m_lines = null;
    /**
     * Process Message
     */
    protected String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    protected boolean m_justPrepared = false;
    protected I_M_InOutConfirm[] m_confirms = null;

    public MInOut(int M_InOut_ID) {
        super(M_InOut_ID);
    }

    public MInOut(MOrder order, int C_DocTypeShipment_ID, Timestamp movementDate) {
        super(order, C_DocTypeShipment_ID, movementDate);
    }

    public MInOut(MInOut original, int c_docType_id, Timestamp movementDate) {
        super(original, c_docType_id, movementDate);
    }

    public MInOut(
            I_C_Invoice invoice, int C_DocTypeShipment_ID, Timestamp movementDate, int M_Warehouse_ID) {
        super(invoice, C_DocTypeShipment_ID, movementDate, M_Warehouse_ID);
    }

    /**
     * Load Constructor
     */
    public MInOut(Row row) {
        super(row);
    }

    /**
     * Create new Shipment by copying
     *
     * @param from         shipment
     * @param dateDoc      date of the document date
     * @param C_DocType_ID doc type
     * @param isSOTrx      sales order
     * @param counter      create counter links
     * @param setOrder     set the order link
     * @return Shipment
     */
    public static MInOut copyFrom(
            MInOut from,
            Timestamp dateDoc,
            Timestamp dateAcct,
            int C_DocType_ID,
            boolean isSOTrx,
            boolean counter,
            boolean setOrder) {
        MInOut to = new MInOut(0);
        copyValues(from, to, from.getClientId(), from.getOrgId());
        to.setValueNoCheck("M_InOut_ID", I_ZERO);
        to.setValueNoCheck("DocumentNo", null);
        //
        to.setDocStatus(DOCSTATUS_Drafted); // 	Draft
        to.setDocAction(DOCACTION_Complete);
        //
        to.setDocumentTypeId(C_DocType_ID);
        to.setIsSOTrx(isSOTrx);
        if (counter) {
            MDocType docType = MDocTypeKt.getDocumentType(C_DocType_ID);
            if (MDocType.DOCBASETYPE_MaterialDelivery.equals(docType.getDocBaseType())) {
                to.setMovementType(isSOTrx ? MOVEMENTTYPE_CustomerShipment : MOVEMENTTYPE_VendorReturns);
            } else if (MDocType.DOCBASETYPE_MaterialReceipt.equals(docType.getDocBaseType())) {
                to.setMovementType(isSOTrx ? MOVEMENTTYPE_CustomerReturns : MOVEMENTTYPE_VendorReceipts);
            }
        }

        //
        to.setDateOrdered(dateDoc);
        to.setDateAcct(dateAcct);
        to.setMovementDate(dateDoc);
        to.setDatePrinted(null);
        to.setIsPrinted(false);
        to.setDateReceived(null);
        to.setNoPackages(0);
        to.setShipDate(null);
        to.setPickDate(null);
        to.setIsInTransit(false);
        //
        to.setIsApproved(false);
        to.setInvoiceId(0);
        to.setTrackingNo(null);
        to.setIsInDispute(false);
        //
        to.setPosted(false);
        to.setProcessed(false);
        // [ 1633721 ] Reverse Documents- Processing=Y
        to.setProcessing(false);
        to.setOrderId(0); // 	Overwritten by setOrder
        to.setRMAId(0); //  Overwritten by setOrder
        if (counter) {
            to.setOrderId(0);
            to.setReferencedInOutId(from.getInOutId());
            //	Try to find Order/Invoice link
            if (from.getOrderId() != 0) {
                MOrder peer = new MOrder(from.getOrderId());
                if (peer.getRef_OrderId() != 0) to.setOrderId(peer.getRef_OrderId());
            }
            if (from.getInvoiceId() != 0) {
                MInvoice peer = new MInvoice(null, from.getInvoiceId());
                if (peer.getRef_InvoiceId() != 0) to.setInvoiceId(peer.getRef_InvoiceId());
            }
            // find RMA link
            if (from.getRMAId() != 0) {
                MRMA peer = new MRMA(from.getRMAId());
                if (peer.getRef_RMAId() > 0) to.setRMAId(peer.getRef_RMAId());
            }
        } else {
            to.setReferencedInOutId(0);
            if (setOrder) {
                to.setOrderId(from.getOrderId());
                to.setRMAId(from.getRMAId()); // Copy also RMA
            }
        }
        //
        if (!to.save()) throw new IllegalStateException("Could not create Shipment");
        if (counter) from.setReferencedInOutId(to.getInOutId());

        if (to.copyLinesFrom(from, counter, setOrder) <= 0)
            throw new IllegalStateException("Could not create Shipment Lines");

        return to;
    } //	copyFrom

    /**
     * @param from         shipment
     * @param dateDoc      date of the document date
     * @param C_DocType_ID doc type
     * @param isSOTrx      sales order
     * @param counter      create counter links
     * @param setOrder     set the order link
     * @return Shipment
     * @deprecated Create new Shipment by copying
     */
    public static MInOut copyFrom(
            MInOut from,
            Timestamp dateDoc,
            int C_DocType_ID,
            boolean isSOTrx,
            boolean counter,
            boolean setOrder) {
        return copyFrom(from, dateDoc, dateDoc, C_DocType_ID, isSOTrx, counter, setOrder);
    }

    /**
     * Get Lines of Shipment
     *
     * @param requery refresh from db
     * @return lines
     */
    public I_M_InOutLine[] getLines(boolean requery) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        List<I_M_InOutLine> list =
                new Query<I_M_InOutLine>(I_M_InOutLine.Table_Name, "M_InOut_ID=?")
                        .setParameters(getInOutId())
                        .setOrderBy(MInOutLine.COLUMNNAME_Line)
                        .list();
        //
        m_lines = new I_M_InOutLine[list.size()];
        list.toArray(m_lines);
        return m_lines;
    } //	getMInOutLines

    /**
     * Get Lines of Shipment
     *
     * @return lines
     */
    public I_M_InOutLine[] getLines() {
        return getLines(false);
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

        //  Order OR RMA can be processed on a shipment/receipt
        if (getOrderId() != 0 && getRMAId() != 0) {
            m_processMsg = "@OrderOrRMA@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        //	Std Period open?
        if (!MPeriod.isOpen(getDateAcct(), dt.getDocBaseType(), getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Credit Check
        if (isSOTrx() && !isReversal()) {
            I_C_Order order = getOrder();
            if (order == null
                    || !MDocType.DOCSUBTYPESO_PrepayOrder.equals(order.getDocumentType().getDocSubTypeSO())
                    || MSysConfig.getBooleanValue(
                    MSysConfig.CHECK_CREDIT_ON_PREPAY_ORDER, true, getClientId(), getOrgId())) {
                I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
                if (MBPartner.SOCREDITSTATUS_CreditStop.equals(bp.getSOCreditStatus())) {
                    m_processMsg =
                            "@BPartnerCreditStop@ - @TotalOpenBalance@="
                                    + bp.getTotalOpenBalance()
                                    + ", @SO_CreditLimit@="
                                    + bp.getSalesOrderCreditLimit();
                    return DocAction.Companion.getSTATUS_Invalid();
                }
                if (MBPartner.SOCREDITSTATUS_CreditHold.equals(bp.getSOCreditStatus())) {
                    m_processMsg =
                            "@BPartnerCreditHold@ - @TotalOpenBalance@="
                                    + bp.getTotalOpenBalance()
                                    + ", @SO_CreditLimit@="
                                    + bp.getSalesOrderCreditLimit();
                    return DocAction.Companion.getSTATUS_Invalid();
                }
                if (!MBPartner.SOCREDITSTATUS_NoCreditCheck.equals(bp.getSOCreditStatus())
                        && Env.ZERO.compareTo(bp.getSalesOrderCreditLimit()) != 0) {
                    BigDecimal notInvoicedAmt = MBPartner.getNotInvoicedAmt(getBusinessPartnerId());
                    if (MBPartner.SOCREDITSTATUS_CreditHold.equals(bp.getSOCreditStatus(notInvoicedAmt))) {
                        m_processMsg =
                                "@BPartnerOverSCreditHold@ - @TotalOpenBalance@="
                                        + bp.getTotalOpenBalance()
                                        + ", @NotInvoicedAmt@="
                                        + notInvoicedAmt
                                        + ", @SO_CreditLimit@="
                                        + bp.getSalesOrderCreditLimit();
                        return DocAction.Companion.getSTATUS_Invalid();
                    }
                }
            }
        }

        //	Lines
        I_M_InOutLine[] lines = getLines(true);
        if (lines == null || lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }
        BigDecimal Volume = Env.ZERO;
        BigDecimal Weight = Env.ZERO;

        //	Mandatory Attributes
        for (I_M_InOutLine line : lines) {
            I_M_Product product = line.getProduct();
            if (product != null) {
                Volume = Volume.add(product.getVolume().multiply(line.getMovementQty()));
                Weight = Weight.add(product.getWeight().multiply(line.getMovementQty()));
            }
            //
            if (line.getAttributeSetInstanceId() != 0) continue;
            if (product != null && product.isASIMandatory(isSOTrx())) {
                if (product.getAttributeSet() != null
                        && !product.getAttributeSet().excludeTableEntry(MInOutLine.Table_ID, isSOTrx())) {
                    m_processMsg =
                            "@M_AttributeSet_ID@ @IsMandatory@ (@Line@ #"
                                    + line.getLine()
                                    + ", @M_Product_ID@="
                                    + product.getSearchKey()
                                    + ")";
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
        }
        setVolume(Volume);
        setWeight(Weight);

        if (!isReversal()) //	don't change reversal
        {
            createConfirmation();
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
     * Get Confirmations
     *
     * @param requery requery
     * @return array of Confirmations
     */
    public I_M_InOutConfirm[] getConfirmations(boolean requery) {
        if (m_confirms != null && !requery) {
            return m_confirms;
        }
        List<I_M_InOutConfirm> list =
                new Query<I_M_InOutConfirm>(I_M_InOutConfirm.Table_Name, "M_InOut_ID=?")
                        .setParameters(getInOutId())
                        .list();
        m_confirms = new I_M_InOutConfirm[list.size()];
        list.toArray(m_confirms);
        return m_confirms;
    } //	getConfirmations

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
        I_M_InOutConfirm[] confirmations = getConfirmations(true);
        for (I_M_InOutConfirm confirm : confirmations) {
            if (!confirm.isProcessed()) {
                if (MInOutConfirm.CONFIRMTYPE_CustomerConfirmation.equals(confirm.getConfirmType()))
                    continue;
                //
                m_processMsg =
                        "Open @M_InOutConfirm_ID@: "
                                + confirm.getConfirmTypeName()
                                + " - "
                                + confirm.getDocumentNo();
                return new CompleteActionResult(DocAction.Companion.getSTATUS_InProgress());
            }
        }

        //	Implicit Approval
        if (!isApproved()) approveIt();
        if (log.isLoggable(Level.INFO)) log.info(toString());
        StringBuilder info = new StringBuilder();

        StringBuilder errors = new StringBuilder();
        //	For all lines
        I_M_InOutLine[] lines = getLines(false);
        for (I_M_InOutLine sLine : lines) {
            I_M_Product product = sLine.getProduct();

            try {
                //	Qty & Type
                String MovementType = getMovementType();
                BigDecimal Qty = sLine.getMovementQty();
                if (MovementType.charAt(1) == '-') // 	C- Customer Shipment - V- Vendor Return
                    Qty = Qty.negate();

                //	Update Order Line
                MOrderLine oLine = null;
                if (sLine.getOrderLineId() != 0) {
                    oLine = new MOrderLine(sLine.getOrderLineId());
                    if (log.isLoggable(Level.FINE))
                        log.fine(
                                "OrderLine - Reserved="
                                        + oLine.getQtyReserved()
                                        + ", Delivered="
                                        + oLine.getQtyDelivered());
                }

                // Load RMA Line
                MRMALine rmaLine = null;

                if (sLine.getRMALineId() != 0) {
                    rmaLine = new MRMALine(sLine.getRMALineId());
                }

                if (log.isLoggable(Level.INFO))
                    log.info("Line=" + sLine.getLine() + " - Qty=" + sLine.getMovementQty());

                //	Stock Movement - Counterpart MOrder.reserveStock
                if (product != null && product.isStocked()) {
                    // Ignore the Material Policy when is Reverse Correction
                    if (!isReversal()) {
                        BigDecimal movementQty = sLine.getMovementQty();
                        BigDecimal qtyOnLineMA =
                                MInOutLineMA.getManualQty(sLine.getInOutLineId());

                        if ((movementQty.signum() != 0
                                && qtyOnLineMA.signum() != 0
                                && movementQty.signum() != qtyOnLineMA.signum()) // must have same sign
                                || (qtyOnLineMA.abs().compareTo(movementQty.abs())
                                > 0)) { // compare absolute values
                            // More then line qty on attribute tab for line 10
                            m_processMsg = "@Over_Qty_On_Attribute_Tab@ " + sLine.getLine();
                            return new CompleteActionResult(DOCSTATUS_Invalid);
                        }

                        checkMaterialPolicy(sLine, movementQty.subtract(qtyOnLineMA));
                    }

                    log.fine("Material Transaction");
                    MTransaction mtrx = null;

                    //
                    BigDecimal overReceipt = BigDecimal.ZERO;
                    if (!isReversal()) {
                        if (oLine != null) {
                            BigDecimal toDelivered = oLine.getQtyOrdered().subtract(oLine.getQtyDelivered());
                            if (toDelivered.signum() < 0) // IDEMPIERE-2889
                                toDelivered = Env.ZERO;
                            if (sLine.getMovementQty().compareTo(toDelivered) > 0)
                                overReceipt = sLine.getMovementQty().subtract(toDelivered);
                            if (overReceipt.signum() != 0) {
                                sLine.setQtyOverReceipt(overReceipt);
                                sLine.saveEx();
                            }
                        }
                    } else {
                        overReceipt = sLine.getQtyOverReceipt();
                    }
                    BigDecimal orderedQtyToUpdate = sLine.getMovementQty().subtract(overReceipt);
                    //
                    if (sLine.getAttributeSetInstanceId() == 0) {
                        I_M_InOutLineMA[] mas =
                                MInOutLineMA.get(sLine.getInOutLineId());
                        for (I_M_InOutLineMA ma : mas) {
                            BigDecimal QtyMA = ma.getMovementQty();
                            if (MovementType.charAt(1) == '-') // 	C- Customer Shipment - V- Vendor Return
                                QtyMA = QtyMA.negate();

                            //	Update Storage - see also VMatch.createMatchRecord
                            if (!MStorageOnHand.add(

                                    getWarehouseId(),
                                    sLine.getLocatorId(),
                                    sLine.getProductId(),
                                    ma.getAttributeSetInstanceId(),
                                    QtyMA,
                                    ma.getDateMaterialPolicy()
                            )) {
                                String lastError = CLogger.retrieveErrorString("");
                                m_processMsg =
                                        "Cannot correct Inventory OnHand (MA) ["
                                                + product.getSearchKey()
                                                + "] - "
                                                + lastError;
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }

                            //	Create Transaction
                            mtrx =
                                    new MTransaction(

                                            sLine.getOrgId(),
                                            MovementType,
                                            sLine.getLocatorId(),
                                            sLine.getProductId(),
                                            ma.getAttributeSetInstanceId(),
                                            QtyMA,
                                            getMovementDate()
                                    );
                            mtrx.setInOutLineId(sLine.getInOutLineId());
                            if (!mtrx.save()) {
                                m_processMsg =
                                        "Could not create Material Transaction (MA) [" + product.getSearchKey() + "]";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                        }

                        if (oLine != null && mtrx != null && oLine.getQtyOrdered().signum() > 0) {
                            if (sLine.getOrderLineId() != 0) {
                                if (!MStorageReservation.add(

                                        oLine.getWarehouseId(),
                                        sLine.getProductId(),
                                        oLine.getAttributeSetInstanceId(),
                                        orderedQtyToUpdate.negate(),
                                        isSOTrx()
                                )) {
                                    String lastError = CLogger.retrieveErrorString("");
                                    m_processMsg =
                                            "Cannot correct Inventory "
                                                    + (isSOTrx() ? "Reserved" : "Ordered")
                                                    + " (MA) - ["
                                                    + product.getSearchKey()
                                                    + "] - "
                                                    + lastError;
                                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                                }
                            }
                        }
                    }
                    //	sLine.getAttributeSetInstanceId() != 0
                    if (mtrx == null) {
                        Timestamp dateMPolicy = null;
                        MStorageOnHand[] storages =
                                MStorageOnHand.getWarehouse(

                                        0,
                                        sLine.getProductId(),
                                        sLine.getAttributeSetInstanceId(),
                                        null,
                                        MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()),
                                        false,
                                        sLine.getLocatorId(),
                                        null);
                        for (MStorageOnHand storage : storages) {
                            if (storage.getQtyOnHand().compareTo(sLine.getMovementQty()) >= 0) {
                                dateMPolicy = storage.getDateMaterialPolicy();
                                break;
                            }
                        }

                        if (dateMPolicy == null && storages.length > 0)
                            dateMPolicy = storages[0].getDateMaterialPolicy();

                        if (dateMPolicy == null) dateMPolicy = getMovementDate();

                        //	Fallback: Update Storage - see also VMatch.createMatchRecord
                        if (!MStorageOnHand.add(

                                getWarehouseId(),
                                sLine.getLocatorId(),
                                sLine.getProductId(),
                                sLine.getAttributeSetInstanceId(),
                                Qty,
                                dateMPolicy
                        )) {
                            String lastError = CLogger.retrieveErrorString("");
                            m_processMsg =
                                    "Cannot correct Inventory OnHand [" + product.getSearchKey() + "] - " + lastError;
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                        if (oLine != null && oLine.getQtyOrdered().signum() > 0) {
                            if (!MStorageReservation.add(

                                    oLine.getWarehouseId(),
                                    sLine.getProductId(),
                                    oLine.getAttributeSetInstanceId(),
                                    orderedQtyToUpdate.negate(),
                                    isSOTrx()
                            )) {
                                m_processMsg =
                                        "Cannot correct Inventory Reserved "
                                                + (isSOTrx() ? "Reserved [" : "Ordered [")
                                                + product.getSearchKey()
                                                + "]";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                        }

                        //	FallBack: Create Transaction
                        mtrx =
                                new MTransaction(

                                        sLine.getOrgId(),
                                        MovementType,
                                        sLine.getLocatorId(),
                                        sLine.getProductId(),
                                        sLine.getAttributeSetInstanceId(),
                                        Qty,
                                        getMovementDate()
                                );
                        mtrx.setInOutLineId(sLine.getInOutLineId());
                        if (!mtrx.save()) {
                            m_processMsg =
                                    CLogger.retrieveErrorString(
                                            "Could not create Material Transaction [" + product.getSearchKey() + "]");
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                    }
                } //	stock movement

                //	Correct Order Line
                if (product != null && oLine != null) // 	other in VMatch.createMatchRecord
                {
                    oLine.setQtyReserved(
                            oLine
                                    .getQtyReserved()
                                    .subtract(sLine.getMovementQty().subtract(sLine.getQtyOverReceipt())));
                }

                //	Update Sales Order Line
                if (oLine != null) {
                    if (isSOTrx() //	PO is done by Matching
                            || sLine.getProductId() == 0) // 	PO Charges, empty lines
                    {
                        if (isSOTrx()) oLine.setQtyDelivered(oLine.getQtyDelivered().subtract(Qty));
                        else oLine.setQtyDelivered(oLine.getQtyDelivered().add(Qty));
                        oLine.setDateDelivered(getMovementDate()); // 	overwrite=last
                    }
                    if (!oLine.save()) {
                        m_processMsg = "Could not update Order Line";
                        return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                    } else if (log.isLoggable(Level.FINE))
                        log.fine(
                                "OrderLine -> Reserved="
                                        + oLine.getQtyReserved()
                                        + ", Delivered="
                                        + oLine.getQtyReserved());
                }
                //  Update RMA Line Qty Delivered
                else if (rmaLine != null) {
                    if (isSOTrx()) {
                        rmaLine.setQtyDelivered(rmaLine.getQtyDelivered().add(Qty));
                    } else {
                        rmaLine.setQtyDelivered(rmaLine.getQtyDelivered().subtract(Qty));
                    }
                    if (!rmaLine.save()) {
                        m_processMsg = "Could not update RMA Line";
                        return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                    }
                }

                //	Create Asset for SO
                if (product != null
                        && isSOTrx()
                        && product.isCreateAsset()
                        && !product.getProductCategory().getAssetGroup().isFixedAsset()
                        && sLine.getMovementQty().signum() > 0
                        && !isReversal()) {
                    log.fine("Asset");
                    info.append("@A_Asset_ID@: ");
                    int noAssets = sLine.getMovementQty().intValue();
                    if (!product.isOneAssetPerUOM()) noAssets = 1;
                    for (int i = 0; i < noAssets; i++) {
                        if (i > 0) info.append(" - ");
                        product.isOneAssetPerUOM();
                        MAsset asset = new MAsset(this, sLine);
                        if (!asset.save()) {
                            m_processMsg = "Could not create Asset";
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                        info.append(asset.getValue());
                    }
                } //	Asset

                //	Matching
                if (!isSOTrx() && sLine.getProductId() != 0 && !isReversal()) {
                    BigDecimal matchQty = sLine.getMovementQty();
                    //	Invoice - Receipt Match (requires Product)
                    I_C_InvoiceLine iLine = MInvoiceLine.getOfInOutLine(sLine);
                    if (iLine != null && iLine.getProductId() != 0) {
                        if (matchQty.compareTo(iLine.getQtyInvoiced()) > 0) matchQty = iLine.getQtyInvoiced();

                        I_M_MatchInv[] matches =
                                MMatchInv.get(

                                        sLine.getInOutLineId(),
                                        iLine.getInvoiceLineId());
                        if (matches.length == 0) {
                            MMatchInv inv = new MMatchInv(iLine, getMovementDate(), matchQty);
                            if (sLine.getAttributeSetInstanceId() != iLine.getAttributeSetInstanceId()) {
                                iLine.setAttributeSetInstanceId(sLine.getAttributeSetInstanceId());
                                iLine.saveEx(); // 	update matched invoice with ASI
                                inv.setAttributeSetInstanceId(sLine.getAttributeSetInstanceId());
                            }
                            if (!inv.save()) {
                                m_processMsg = CLogger.retrieveErrorString("Could not create Inv Matching");
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                            addDocsPostProcess(inv);
                        }
                    }

                    //	Link to Order
                    if (sLine.getOrderLineId() != 0) {
                        log.fine("PO Matching");
                        //	Ship - PO
                        MMatchPO po = MMatchPO.create(null, sLine, getMovementDate(), matchQty);
                        if (po != null) {
                            if (!po.save()) {
                                m_processMsg = "Could not create PO Matching";
                                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                            }
                            if (!po.isPosted()) addDocsPostProcess(po);
                            MMatchInv matchInvCreated = po.getMatchInvCreated();
                            if (matchInvCreated != null) {
                                addDocsPostProcess(matchInvCreated);
                            }
                        }
                        //	Update PO with ASI
                        if (oLine != null
                                && oLine.getAttributeSetInstanceId() == 0
                                && sLine.getMovementQty().compareTo(oLine.getQtyOrdered())
                                == 0) //  just if full match [ 1876965 ]
                        {
                            oLine.setAttributeSetInstanceId(sLine.getAttributeSetInstanceId());
                            oLine.saveEx();
                        }
                    } else //	No Order - Try finding links via Invoice
                    {
                        //	Invoice has an Order Link
                        if (iLine != null && iLine.getOrderLineId() != 0) {
                            //	Invoice is created before  Shipment
                            log.fine("PO(Inv) Matching");
                            //	Ship - Invoice
                            MMatchPO po = MMatchPO.create(iLine, sLine, getMovementDate(), matchQty);
                            if (po != null) {
                                if (!po.save()) {
                                    m_processMsg = "Could not create PO(Inv) Matching";
                                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                                }
                                if (!po.isPosted()) addDocsPostProcess(po);
                            }

                            //	Update PO with ASI
                            oLine = new MOrderLine(iLine.getOrderLineId());
                            if (oLine.getAttributeSetInstanceId() == 0 && sLine.getMovementQty().compareTo(oLine.getQtyOrdered()) == 0) //  just if full match [ 1876965 ]
                            {
                                oLine.setAttributeSetInstanceId(sLine.getAttributeSetInstanceId());
                                oLine.saveEx();
                            }
                        }
                    } //	No Order
                } //	PO Matching
            } catch (NegativeInventoryDisallowedException e) {
                log.severe(e.getMessage());
                errors
                        .append(MsgKt.getElementTranslation("Line"))
                        .append(" ")
                        .append(sLine.getLine())
                        .append(": ");
                errors.append(e.getMessage()).append("\n");
            }
        } //	for all lines

        if (errors.toString().length() > 0) {
            m_processMsg = errors.toString();
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        //	Counter Documents
        MInOut counter = createCounterDoc();
        if (counter != null)
            info.append(" - @CounterDoc@: @M_InOut_ID@=").append(counter.getDocumentNo());

        //  Drop Shipments
        MInOut dropShipment = createDropShipment();
        if (dropShipment != null)
            info.append(" - @DropShipment@: @M_InOut_ID@=").append(dropShipment.getDocumentNo());
        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        m_processMsg = info.toString();
        setProcessed(true);
        setDocAction(DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

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
            I_M_InOutLine[] lines = getLines(false);
            for (I_M_InOutLine line : lines) {
                BigDecimal old = line.getMovementQty();
                if (old.signum() != 0) {
                    line.setQty(Env.ZERO);
                    line.addDescription("Void (" + old + ")");
                    line.saveEx();
                }
            }
            //
            // Void Confirmations
            setDocStatus(DOCSTATUS_Voided); // need to set & save docstatus to be able to check it in
            // MInOutConfirm.voidIt()
            saveEx();
            voidConfirmations();
        } else {
            boolean accrual = false;
            try {
                MPeriod.testPeriodOpen(getDateAcct(), getDocumentTypeId(), getOrgId());
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

        setProcessed(true);
        setDocAction(DOCACTION_None);

        // After Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE);
        return m_processMsg == null;
    } //	closeIt

    /**
     * Reverse Correction - same date
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

        MInOut reversal = reverse(false);
        if (reversal == null) return false;

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        m_processMsg = reversal.getDocumentNo();
        setProcessed(true);
        setDocStatus(DOCSTATUS_Reversed); // 	 may come from void
        setDocAction(DOCACTION_None);
        return true;
    } //	reverseCorrectionIt

    protected MInOut reverse(boolean accrual) {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        Timestamp reversalDate = accrual ? Env.getContextAsDate() : getDateAcct();
        if (reversalDate == null) {
            reversalDate = new Timestamp(System.currentTimeMillis());
        }
        Timestamp reversalMovementDate = accrual ? reversalDate : getMovementDate();
        if (!MPeriod.isOpen(reversalDate, dt.getDocBaseType(), getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return null;
        }

        //	Reverse/Delete Matching
        if (!isSOTrx()) {
            if (!reverseMatching(reversalDate)) return null;
        }

        //	Deep Copy
        MInOut reversal =
                copyFrom(
                        this,
                        reversalMovementDate,
                        reversalDate,
                        getDocumentTypeId(),
                        isSOTrx(),
                        false,
                        true);
        if (reversal == null) {
            m_processMsg = "Could not create Ship Reversal";
            return null;
        }
        reversal.setReversal(true);

        //	Reverse Line Qty
        I_M_InOutLine[] sLines = getLines(false);
        I_M_InOutLine[] rLines = reversal.getLines(false);
        for (int i = 0; i < rLines.length; i++) {
            I_M_InOutLine rLine = rLines[i];
            rLine.setQtyEntered(rLine.getQtyEntered().negate());
            rLine.setMovementQty(rLine.getMovementQty().negate());
            rLine.setQtyOverReceipt(rLine.getQtyOverReceipt().negate());
            rLine.setAttributeSetInstanceId(sLines[i].getAttributeSetInstanceId());
            // Goodwill: store original (voided/reversed) document line
            rLine.setReversalLineId(sLines[i].getInOutLineId());
            if (!rLine.save()) {
                m_processMsg = "Could not correct Ship Reversal Line";
                return null;
            }
            //	We need to copy MA
            if (rLine.getAttributeSetInstanceId() == 0) {
                I_M_InOutLineMA[] mas =
                        MInOutLineMA.get(sLines[i].getInOutLineId());
                for (I_M_InOutLineMA ma1 : mas) {
                    MInOutLineMA ma =
                            new MInOutLineMA(
                                    rLine,
                                    ma1.getAttributeSetInstanceId(),
                                    ma1.getMovementQty().negate(),
                                    ma1.getDateMaterialPolicy(),
                                    true);
                    ma.saveEx();
                }
            }
        }
        reversal.setOrderId(getOrderId());
        // Set M_RMA_ID
        reversal.setRMAId(getRMAId());
        StringBuilder msgadd = new StringBuilder("{->").append(getDocumentNo()).append(")");
        reversal.addDescription(msgadd.toString());
        // FR1948157
        reversal.setReversalId(getInOutId());
        reversal.saveEx();
        //
        reversal.docsPostProcess = this.docsPostProcess;
        this.docsPostProcess = new ArrayList<>();
        //
        if (!reversal.processIt(DocAction.Companion.getACTION_Complete())
                || !reversal.getDocStatus().equals(DocAction.Companion.getSTATUS_Completed())) {
            m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
            return null;
        }
        reversal.closeIt();
        reversal.setProcessing(false);
        reversal.setDocStatus(DOCSTATUS_Reversed);
        reversal.setDocAction(DOCACTION_None);
        reversal.saveEx();
        //
        msgadd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
        addDescription(msgadd.toString());

        //
        // Void Confirmations
        setDocStatus(DOCSTATUS_Reversed); // need to set & save docstatus to be able to check it in
        // MInOutConfirm.voidIt()
        saveEx();
        // FR1948157
        this.setReversalId(reversal.getInOutId());
        voidConfirmations();
        return reversal;
    }

    protected boolean reverseMatching(Timestamp reversalDate) {
        I_M_MatchInv[] mInv = MMatchInv.getInOut(getInOutId());
        for (I_M_MatchInv mMatchInv : mInv) {
            if (mMatchInv.getReversalId() > 0) continue;

            String description = mMatchInv.getDescription();
            if (description == null || !description.endsWith("<-)")) {
                if (!mMatchInv.reverse(reversalDate)) {
                    log.log(
                            Level.SEVERE,
                            "Failed to create reversal for match invoice " + mMatchInv.getDocumentNo());
                    return false;
                }
                addDocsPostProcess(new MMatchInv(mMatchInv.getReversalId()));
            }
        }
        MMatchPO[] mMatchPOList = MMatchPO.getInOut(getInOutId());
        for (MMatchPO mMatchPO : mMatchPOList) {
            if (mMatchPO.getReversalId() > 0) continue;

            String description = mMatchPO.getDescription();
            if (description == null || !description.endsWith("<-)")) {
                if (!mMatchPO.reverse(reversalDate)) {
                    log.log(
                            Level.SEVERE,
                            "Failed to create reversal for match purchase order " + mMatchPO.getDocumentNo());
                    return false;
                }
                addDocsPostProcess(new MMatchPO(mMatchPO.getReversalId()));
            }
        }
        return true;
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

        MInOut reversal = reverse(true);
        if (reversal == null) return false;

        // After reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        m_processMsg = reversal.getDocumentNo();
        setProcessed(true);
        setDocStatus(DOCSTATUS_Reversed); // 	 may come from void
        setDocAction(DOCACTION_None);
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

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }

    /**
     * Create the missing next Confirmation
     */
    public void createConfirmation() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        boolean pick = dt.isPickQAConfirm();
        boolean ship = dt.isShipConfirm();
        //	Nothing to do
        if (!pick && !ship) {
            log.fine("No need");
            return;
        }

        //	Create Both .. after each other
        if (pick && ship) {
            boolean havePick = false;
            boolean haveShip = false;
            I_M_InOutConfirm[] confirmations = getConfirmations(false);
            for (I_M_InOutConfirm confirm : confirmations) {
                if (MInOutConfirm.CONFIRMTYPE_PickQAConfirm.equals(confirm.getConfirmType())) {
                    if (!confirm.isProcessed()) // 	wait intil done
                    {
                        if (log.isLoggable(Level.FINE)) log.fine("Unprocessed: " + confirm);
                        return;
                    }
                    havePick = true;
                } else if (MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm.equals(confirm.getConfirmType()))
                    haveShip = true;
            }
            //	Create Pick
            if (!havePick) {
                MInOutConfirm.create(this, MInOutConfirm.CONFIRMTYPE_PickQAConfirm, false);
                return;
            }
            //	Create Ship
            if (!haveShip) {
                MInOutConfirm.create(this, MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm, false);
                return;
            }
            return;
        }
        //	Create just one
        if (pick) MInOutConfirm.create(this, MInOutConfirm.CONFIRMTYPE_PickQAConfirm, true);
        else MInOutConfirm.create(this, MInOutConfirm.CONFIRMTYPE_ShipReceiptConfirm, true);
    } //	createConfirmation

    protected void voidConfirmations() {
        for (I_M_InOutConfirm confirm : getConfirmations(true)) {
            if (!confirm.isProcessed()) {
                if (!confirm.processIt(MInOutConfirm.DOCACTION_Void))
                    throw new AdempiereException(confirm.getProcessMsg());
                confirm.saveEx();
            }
        }
    }

    /**
     * Set the definite document number after completed
     */
    protected void setDefiniteDocumentNo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        if (dt.isOverwriteDateOnComplete()) {
            setMovementDate(TimeUtil.getDay(0));
            if (getDateAcct().before(getMovementDate())) {
                setDateAcct(getMovementDate());
                MPeriod.testPeriodOpen(getDateAcct(), getDocumentTypeId(), getOrgId());
            }
        }
        if (dt.isOverwriteSeqOnComplete()) {
            String value = MSequence.getDocumentNo(getDocumentTypeId(), true, this);
            if (value != null) setDocumentNo(value);
        }
    }

    /**
     * Check Material Policy Sets line ASI
     */
    protected void checkMaterialPolicy(I_M_InOutLine line, BigDecimal qty) {

        int no = MInOutLineMA.deleteInOutLineMA(line.getInOutLineId());
        if (no > 0) if (log.isLoggable(Level.CONFIG)) log.config("Delete old #" + no);

        if (Env.ZERO.compareTo(qty) == 0) return;

        //	Incoming Trx
        String MovementType = getMovementType();
        boolean inTrx = MovementType.charAt(1) == '+'; // 	V+ Vendor Receipt

        boolean needSave = false;

        I_M_Product product = line.getProduct();

        //	Need to have Location
        if (product != null && line.getLocatorId() == 0) {
            // MWarehouse w = MWarehouse.get(getWarehouseId());
            line.setWarehouseId(getWarehouseId());
            line.setLocatorId(inTrx ? Env.ZERO : line.getMovementQty()); // 	default Locator
            needSave = true;
        }

        //	Attribute Set Instance
        //  Create an  Attribute Set Instance to any receipt FIFO/LIFO
        if (product != null && line.getAttributeSetInstanceId() == 0) {
            // Validate Transaction
            if (getMovementType().compareTo(MInOut.MOVEMENTTYPE_VendorReceipts) == 0) {
                // auto balance negative on hand
                BigDecimal qtyToReceive = autoBalanceNegative(line, product, qty);

                // Allocate remaining qty.
                if (qtyToReceive.compareTo(Env.ZERO) > 0) {
                    I_M_InOutLineMA ma =
                            MInOutLineMA.addOrCreate(line, 0, qtyToReceive, getMovementDate(), true);
                    ma.saveEx();
                }

            } else if (getMovementType().compareTo(MInOut.MOVEMENTTYPE_CustomerReturns) == 0) {
                BigDecimal qtyToReturn = autoBalanceNegative(line, product, qty);

                if (line.getRMALineId() != 0 && qtyToReturn.compareTo(Env.ZERO) > 0) {
                    // Linking to shipment line
                    MRMALine rmaLine = new MRMALine(line.getRMALineId());
                    if (rmaLine.getInOutLineId() > 0) {
                        // retrieving ASI which is not already returned
                        MInOutLineMA[] shipmentMAS =
                                MInOutLineMA.getNonReturned(rmaLine.getInOutLineId());

                        for (MInOutLineMA sMA : shipmentMAS) {
                            BigDecimal lineMAQty = sMA.getMovementQty();
                            if (lineMAQty.compareTo(qtyToReturn) > 0) {
                                lineMAQty = qtyToReturn;
                            }

                            I_M_InOutLineMA ma =
                                    MInOutLineMA.addOrCreate(
                                            line,
                                            sMA.getAttributeSetInstanceId(),
                                            lineMAQty,
                                            sMA.getDateMaterialPolicy(),
                                            true);
                            ma.saveEx();

                            qtyToReturn = qtyToReturn.subtract(lineMAQty);
                            if (qtyToReturn.compareTo(Env.ZERO) == 0) break;
                        }
                    }
                }
                if (qtyToReturn.compareTo(Env.ZERO) > 0) {
                    // Use movement data for  Material policy if no linkage found to Shipment.
                    I_M_InOutLineMA ma = MInOutLineMA.addOrCreate(line, 0, qtyToReturn, getMovementDate(), true);
                    ma.saveEx();
                }
            }
            // Create consume the Attribute Set Instance using policy FIFO/LIFO
            else if (getMovementType().compareTo(MInOut.MOVEMENTTYPE_VendorReturns) == 0
                    || getMovementType().compareTo(MInOut.MOVEMENTTYPE_CustomerShipment) == 0) {
                String MMPolicy = product.getMMPolicy();
                Timestamp minGuaranteeDate = getMovementDate();
                MStorageOnHand[] storages =
                        MStorageOnHand.getWarehouse(

                                getWarehouseId(),
                                line.getProductId(),
                                line.getAttributeSetInstanceId(),
                                minGuaranteeDate,
                                MClient.MMPOLICY_FiFo.equals(MMPolicy),
                                true,
                                line.getLocatorId(),
                                false);
                BigDecimal qtyToDeliver = qty;
                for (MStorageOnHand storage : storages) {
                    if (storage.getQtyOnHand().compareTo(qtyToDeliver) >= 0) {
                        MInOutLineMA ma =
                                new MInOutLineMA(
                                        line,
                                        storage.getAttributeSetInstanceId(),
                                        qtyToDeliver,
                                        storage.getDateMaterialPolicy(),
                                        true);
                        ma.saveEx();
                        qtyToDeliver = Env.ZERO;
                    } else {
                        MInOutLineMA ma =
                                new MInOutLineMA(
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

                if (qtyToDeliver.signum() != 0) {
                    // Over Delivery
                    I_M_InOutLineMA ma =
                            MInOutLineMA.addOrCreate(
                                    line, line.getAttributeSetInstanceId(), qtyToDeliver, getMovementDate(), true);
                    ma.saveEx();
                    if (log.isLoggable(Level.FINE)) log.fine("##: " + ma);
                }
            } //	outgoing Trx
        } //	attributeSetInstance

        if (needSave) {
            line.saveEx();
        }
    } //	checkMaterialPolicy

    /**
     * ************************************************************************ Create Counter
     * Document
     *
     * @return InOut
     */
    protected MInOut createCounterDoc() {
        //	Is this a counter doc ?
        if (getReferencedInOutId() != 0) return null;

        //	Org Must be linked to BPartner
        MOrg org = MOrgKt.getOrg(getOrgId());
        int counterC_BPartner_ID = org.getLinkedBusinessPartnerId();
        if (counterC_BPartner_ID == 0) return null;
        //	Business Partner needs to be linked to Org
        I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
        int counterAD_Org_ID = bp.getLinkedOrganizationId();
        if (counterAD_Org_ID == 0) return null;

        I_C_BPartner counterBP = getBusinessPartnerService().getById(counterC_BPartner_ID);
        MOrgInfo counterOrgInfo = MOrgInfoKt.getOrganizationInfo(counterAD_Org_ID);
        if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.getName());

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
        MInOut counter =
                copyFrom(
                        this,
                        getMovementDate(),
                        getDateAcct(),
                        C_DocTypeTarget_ID,
                        !isSOTrx(),
                        true,
                        true);

        //
        counter.setOrgId(counterAD_Org_ID);
        counter.setWarehouseId(counterOrgInfo.getWarehouseId());
        //
        counter.setBPartner(counterBP);

        if (isDropShip()) {
            counter.setIsDropShip(true);
            counter.setDropShipBPartnerId(getDropShipBPartnerId());
            counter.setDropShipLocationId(getDropShipLocationId());
            counter.setDropShipUserId(getDropShipUserId());
        }

        //	Refernces (Should not be required
        counter.setSalesRepresentativeId(getSalesRepresentativeId());
        counter.saveEx();

        String MovementType = counter.getMovementType();
        boolean inTrx = MovementType.charAt(1) == '+'; // 	V+ Vendor Receipt

        //	Update copied lines
        I_M_InOutLine[] counterLines = counter.getLines(true);
        for (I_M_InOutLine counterLine : counterLines) {
            counterLine.setClientOrg(counter);
            counterLine.setWarehouseId(counter.getWarehouseId());
            counterLine.setLocatorId(0);
            counterLine.setLocatorId(inTrx ? Env.ZERO : counterLine.getMovementQty());
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
     * Automatically creates a customer shipment for any drop shipment material receipt Based on
     * createCounterDoc() by JJ
     *
     * @return shipment if created else null
     */
    protected MInOut createDropShipment() {

        if (isSOTrx() || !isDropShip() || getOrderId() == 0) return null;

        int linkedOrderID = new MOrder(getOrderId()).getLink_OrderId();
        if (linkedOrderID <= 0) return null;

        //	Document Type
        int C_DocTypeTarget_ID = 0;
        DocumentType[] shipmentTypes =
                MDocTypeKt.getDocumentTypeOfDocBaseType(MDocType.DOCBASETYPE_MaterialDelivery);

        for (DocumentType shipmentType : shipmentTypes) {
            if (shipmentType.isSOTrx() && (C_DocTypeTarget_ID == 0 || shipmentType.isDefault()))
                C_DocTypeTarget_ID = shipmentType.getDocTypeId();
        }

        //	Deep Copy
        MInOut dropShipment =
                copyFrom(
                        this,
                        getMovementDate(),
                        getDateAcct(),
                        C_DocTypeTarget_ID,
                        !isSOTrx(),
                        false,
                        true);

        dropShipment.setOrderId(linkedOrderID);

        // get invoice id from linked order
        int invID = new MOrder(linkedOrderID).getInvoiceId();
        if (invID != 0) dropShipment.setInvoiceId(invID);

        dropShipment.setBusinessPartnerId(getDropShipBPartnerId());
        dropShipment.setBusinessPartnerLocationId(getDropShipLocationId());
        dropShipment.setUserId(getDropShipUserId());
        dropShipment.setIsDropShip(false);
        dropShipment.setDropShipBPartnerId(0);
        dropShipment.setDropShipLocationId(0);
        dropShipment.setDropShipUserId(0);
        dropShipment.setMovementType(MOVEMENTTYPE_CustomerShipment);

        //	References (Should not be required
        dropShipment.setSalesRepresentativeId(getSalesRepresentativeId());
        dropShipment.saveEx();

        //		Update line order references to linked sales order lines
        I_M_InOutLine[] lines = dropShipment.getLines(true);
        for (I_M_InOutLine dropLine : lines) {
            MOrderLine ol = new MOrderLine(dropLine.getOrderLineId());
            if (ol.getOrderLineId() != 0) {
                dropLine.setOrderLineId(ol.getLink_OrderLineId());
                dropLine.saveEx();
            }
        }

        if (log.isLoggable(Level.FINE)) log.fine(dropShipment.toString());

        dropShipment.setDocAction(DocAction.Companion.getACTION_Complete());
        // added AdempiereException by Zuhri
        if (!dropShipment.processIt(DocAction.Companion.getACTION_Complete()))
            throw new AdempiereException(
                    "Failed when processing document - " + dropShipment.getProcessMsg());
        // end added
        dropShipment.saveEx();

        return dropShipment;
    }

    protected BigDecimal autoBalanceNegative(
            I_M_InOutLine line, I_M_Product product, BigDecimal qtyToReceive) {
        MStorageOnHand[] storages =
                MStorageOnHand.getWarehouseNegative(

                        getWarehouseId(),
                        line.getProductId(),
                        0,
                        null,
                        org.compiere.orm.MClient.MMPOLICY_FiFo.equals(product.getMMPolicy()),
                        line.getLocatorId(),
                        false);

        Timestamp dateMPolicy;

        for (MStorageOnHand storage : storages) {
            if (storage.getQtyOnHand().signum() < 0 && qtyToReceive.compareTo(Env.ZERO) > 0) {
                dateMPolicy = storage.getDateMaterialPolicy();
                BigDecimal lineMAQty = qtyToReceive;
                if (lineMAQty.compareTo(storage.getQtyOnHand().negate()) > 0)
                    lineMAQty = storage.getQtyOnHand().negate();

                // Using ASI from storage record
                MInOutLineMA ma =
                        new MInOutLineMA(
                                line, storage.getAttributeSetInstanceId(), lineMAQty, dateMPolicy, true);
                ma.saveEx();
                qtyToReceive = qtyToReceive.subtract(lineMAQty);
            }
        }
        return qtyToReceive;
    }


    /**
     * Get Document Approval Amount
     *
     * @return amount
     */
    @NotNull
    public BigDecimal getApprovalAmt() {
        return Env.ZERO;
    } //	getApprovalAmt

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
        sb.append(":")
                //	.append(MsgKt.translate("TotalLines")).append("=").append(getTotalLines())
                .append(" (#")
                .append(getLines(false).length)
                .append(")");
        //	 - Description
        if (getDescription() != null && getDescription().length() > 0)
            sb.append(" - ").append(getDescription());
        return sb.toString();
    } //	getSummary

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
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    public int getDocumentUserId() {
        return getSalesRepresentativeId();
    } //	getDoc_User_ID

    /**
     * Get C_Currency_ID
     *
     * @return Accounting Currency
     */
    public int getCurrencyId() {
        return Env.getContextAsInt("$C_Currency_ID");
    } //	getCurrencyId
}
