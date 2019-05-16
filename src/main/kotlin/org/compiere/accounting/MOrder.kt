package org.compiere.accounting

import kotliquery.Row
import org.compiere.bank.MBankAccount
import org.compiere.crm.MBPartner
import org.compiere.docengine.DocumentEngine
import org.compiere.invoicing.MConversionRate
import org.compiere.invoicing.MDocTypeCounter
import org.compiere.invoicing.MInOut
import org.compiere.invoicing.MInvoice
import org.compiere.invoicing.MInvoiceLine
import org.compiere.invoicing.MInvoicePaySchedule
import org.compiere.invoicing.MPaymentTerm
import org.compiere.model.DocumentType
import org.compiere.model.IDoc
import org.compiere.model.IPODoc
import org.compiere.model.I_C_BankAccount
import org.compiere.model.I_C_Invoice
import org.compiere.model.I_C_Order
import org.compiere.model.I_C_OrderLine
import org.compiere.model.I_C_POSPayment
import org.compiere.model.I_M_InOut
import org.compiere.order.MInOutLine
import org.compiere.order.MOrderPaySchedule
import org.compiere.order.OrderConstants.DELIVERYRULE_Availability
import org.compiere.order.OrderConstants.DELIVERYRULE_CompleteOrder
import org.compiere.order.OrderConstants.DELIVERYRULE_Force
import org.compiere.order.OrderConstants.DELIVERYVIARULE_Shipper
import org.compiere.order.OrderConstants.DOCACTION_Close
import org.compiere.order.OrderConstants.DOCACTION_Complete
import org.compiere.order.OrderConstants.DOCACTION_None
import org.compiere.order.OrderConstants.DOCACTION_Prepare
import org.compiere.order.OrderConstants.DOCACTION_Void
import org.compiere.order.OrderConstants.DOCSTATUS_Closed
import org.compiere.order.OrderConstants.DOCSTATUS_Completed
import org.compiere.order.OrderConstants.DOCSTATUS_Drafted
import org.compiere.order.OrderConstants.DOCSTATUS_InProgress
import org.compiere.order.OrderConstants.DOCSTATUS_Invalid
import org.compiere.order.OrderConstants.DOCSTATUS_Reversed
import org.compiere.order.OrderConstants.INVOICERULE_AfterDelivery
import org.compiere.order.OrderConstants.INVOICERULE_Immediate
import org.compiere.order.OrderConstants.PAYMENTRULE_Cash
import org.compiere.order.OrderConstants.PAYMENTRULE_DirectDebit
import org.compiere.order.OrderConstants.PAYMENTRULE_MixedPOSPayment
import org.compiere.order.OrderConstants.PAYMENTRULE_OnCredit
import org.compiere.orm.MDocType
import org.compiere.orm.MSequence
import org.compiere.orm.MSysConfig
import org.compiere.orm.PO
import org.compiere.orm.Query
import org.compiere.orm.getClientDocumentTypes
import org.compiere.orm.getDocumentType
import org.compiere.orm.getDocumentTypeOfDocBaseType
import org.compiere.orm.getOrg
import org.compiere.orm.getOrganizationInfo
import org.compiere.process.CompleteActionResult
import org.compiere.process.DocAction
import org.compiere.product.MPriceList
import org.compiere.production.MProject
import org.compiere.util.getElementTranslation
import org.compiere.util.getMsg
import org.compiere.util.translate
import org.compiere.validation.ModelValidationEngine
import org.compiere.validation.ModelValidator
import org.idempiere.common.exceptions.AdempiereException
import org.idempiere.common.exceptions.FillMandatoryException
import org.idempiere.common.util.CLogger
import org.idempiere.common.util.Env
import org.idempiere.common.util.Util
import software.hsharp.core.util.Environment
import software.hsharp.core.util.getSQLValue
import software.hsharp.core.util.getSQLValueEx
import software.hsharp.modules.Module
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.Arrays
import java.util.logging.Level
import java.util.regex.Pattern

class MOrder : org.compiere.order.MOrder, DocAction<I_C_Invoice>, IPODoc {
    override val documentNo: String
        get() = getValue(I_C_Order.COLUMNNAME_DocumentNo) ?: throw AdempiereException("Document number not found")
    override val documentInfo: String
        get() {
            val dt = getDocumentType(if (documentTypeId > 0) documentTypeId else targetDocumentTypeId)
            return dt.nameTrl + " " + documentNo
        }
    override val currencyId: Int
        get() = getValue(I_C_Order.COLUMNNAME_C_Currency_ID) ?: 0
    override val processMsg: String
        get() = m_processMsg
    override val docAction: String
        get() = getValue(I_C_Order.COLUMNNAME_DocAction) ?: throw AdempiereException("Document action not found")

    /**
     * Order Lines
     */
    protected var m_lines: Array<I_C_OrderLine>? = null

    /**
     * *********************************************************************** Get Summary
     *
     * @return Summary of Document
     */
    override // 	: Grand Total = 123.00 (#1)
    // 	 - Description
    val summary: String
        get() {
            val sb = StringBuilder()
            sb.append(documentNo)
            sb.append(": ")
                .append(translate("GrandTotal"))
                .append("=")
                .append(grandTotal)
            if (super.m_lines != null) sb.append(" (#").append(super.m_lines!!.size).append(")")
            if (description != null && description.length > 0)
                sb.append(" - ").append(description)
            return sb.toString()
        } // 	getSummary

    /**
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    override val documentUserId: Int
        get() = salesRepresentativeId // 	getDoc_User_ID

    /**
     * Get Document Approval Amount
     *
     * @return amount
     */
    override val approvalAmt: BigDecimal
        get() = grandTotal // 	getApprovalAmt

    /**
     * ************************************************************************ Default Constructor
     *
     * @param C_Order_ID order to load, (0 create new order)
     */
    constructor(C_Order_ID: Int) : super(C_Order_ID) {}

    /**
     * Load Constructor
     */
    constructor(row: Row) : super(row) {} // 	MOrder

    /**
     * ************************************************************************ Project Constructor
     *
     * @param project Project to create Order from
     * @param IsSOTrx sales order
     * @param DocSubTypeSO if SO DocType Target (default DocSubTypeSO_OnCredit)
     */
    constructor(project: MProject, IsSOTrx: Boolean, DocSubTypeSO: String?) : this(0) {
        setClientId(project.clientId)
        setOrgId(project.orgId)
        campaignId = project.campaignId
        salesRepresentativeId = project.salesRepresentativeId
        //
        projectId = project.projectId
        description = project.name
        var ts: Timestamp? = project.dateContract
        if (ts != null) dateOrdered = ts
        ts = project.dateFinish
        if (ts != null) datePromised = ts
        //
        businessPartnerId = project.businessPartnerId
        businessPartnerLocationId = project.businessPartnerLocationId
        userId = project.userId
        //
        warehouseId = project.warehouseId
        priceListId = project.priceListId
        paymentTermId = project.paymentTermId
        //
        setIsSOTrx(IsSOTrx)
        if (IsSOTrx) {
            if (DocSubTypeSO == null || DocSubTypeSO.length == 0)
                setTargetDocumentTypeId(DocSubTypeSO_OnCredit)
            else
                setTargetDocumentTypeId(DocSubTypeSO)
        } else
            setTargetDocumentTypeId()
    } // 	MOrder

    /**
     * Create Invoice
     *
     * @param dt order document type
     * @param shipment optional shipment
     * @param invoiceDate invoice date
     * @return invoice or null
     */
    protected fun createInvoice(dt: MDocType, shipment: MInOut?, invoiceDate: Timestamp?): I_C_Invoice? {
        if (log.isLoggable(Level.INFO)) log.info(dt.toString())
        val invoice = MInvoice(this, dt.docTypeInvoiceId, invoiceDate)
        if (!invoice.save()) {
            m_processMsg = "Could not create Invoice"
            return null
        }

        // 	If we have a Shipment - use that as a base
        if (shipment != null) {
            if (INVOICERULE_AfterDelivery != invoiceRule)
                invoiceRule = INVOICERULE_AfterDelivery
            //
            val sLines = shipment.getLines(false)
            for (sLine in sLines) {
                //
                val iLine = MInvoiceLine(invoice)
                iLine.setShipLine(sLine)
                // 	Qty = Delivered
                if (sLine.sameOrderLineUOM())
                    iLine.qtyEntered = sLine.qtyEntered
                else
                    iLine.qtyEntered = sLine.movementQty
                iLine.qtyInvoiced = sLine.movementQty
                if (!iLine.save()) {
                    m_processMsg = "Could not create Invoice Line from Shipment Line"
                    return null
                }
                //
                sLine.setIsInvoiced(true)
                if (!sLine.save()) {
                    log.warning("Could not update Shipment line: $sLine")
                }
            }
        } else
        // 	Create Invoice from Order
        {
            if (INVOICERULE_Immediate != invoiceRule) invoiceRule = INVOICERULE_Immediate
            //
            val oLines = lines.toTypedArray()
            for (oLine in oLines) {
                //
                val iLine = MInvoiceLine(invoice)
                iLine.setOrderLine(oLine)
                // 	Qty = Ordered - Invoiced
                iLine.qtyInvoiced = oLine.qtyOrdered.subtract(oLine.qtyInvoiced)
                if (oLine.qtyOrdered.compareTo(oLine.qtyEntered) == 0)
                    iLine.qtyEntered = iLine.qtyInvoiced
                else
                    iLine.qtyEntered = iLine
                        .qtyInvoiced
                        .multiply(oLine.qtyEntered)
                        .divide(oLine.qtyOrdered, 12, BigDecimal.ROUND_HALF_UP)
                if (!iLine.save()) {
                    m_processMsg = "Could not create Invoice Line from Order Line"
                    return null
                }
            }
        }

        // Copy payment schedule from order to invoice if any
        for (ops in MOrderPaySchedule.getOrderPaySchedule(orderId, 0)) {
            val ips = MInvoicePaySchedule(0)
            PO.copyValues(ops, ips)
            ips.invoiceId = invoice.invoiceId
            ips.setOrgId(ops.orgId)
            ips.setProcessing(ops.isProcessing)
            ips.setIsActive(ops.isActive())
            if (!ips.save()) {
                m_processMsg = "ERROR: creating pay schedule for invoice from : $ops"
                return null
            }
        }

        // added AdempiereException by zuhri
        if (!invoice.processIt(DocAction.ACTION_Complete))
            throw AdempiereException("Failed when processing document - " + invoice.processMsg)
        // end added
        invoice.saveEx()
        cashLineId = invoice.cashLineId
        if (DOCSTATUS_Completed != invoice.docStatus) {
            m_processMsg = "@C_Invoice_ID@: " + invoice.processMsg
            return null
        }
        return invoice
    } // 	createInvoice

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord new
     * @return save
     */
    override fun beforeSave(newRecord: Boolean): Boolean {
        // 	Client/Org Check
        if (orgId == 0) {
            val context_AD_Org_ID = Env.getOrgId()
            if (context_AD_Org_ID != 0) {
                setOrgId(context_AD_Org_ID)
                log.warning("Changed Org to Context=$context_AD_Org_ID")
            }
        }
        if (clientId == 0) {
            m_processMsg = "AD_Client_ID = 0"
            return false
        }

        // 	New Record Doc Type - make sure DocType set to 0
        if (newRecord && documentTypeId == 0) documentTypeId = 0

        // 	Default Warehouse
        if (warehouseId == 0) {
            val ii = Env.getContextAsInt("#M_Warehouse_ID")
            if (ii != 0)
                warehouseId = ii
            else {
                throw FillMandatoryException(COLUMNNAME_M_Warehouse_ID)
            }
        }
        val wh = MWarehouse.get(warehouseId)
        // 	Warehouse Org
        if (newRecord || isValueChanged("AD_Org_ID") || isValueChanged("M_Warehouse_ID")) {
            if (wh.orgId != orgId) log.saveWarning("WarehouseOrgConflict", "")
        }

        val disallowNegInv = wh.isDisallowNegativeInv
        val DeliveryRule = deliveryRule
        if (disallowNegInv && DELIVERYRULE_Force == DeliveryRule || DeliveryRule == null || DeliveryRule.length == 0)
            deliveryRule = DELIVERYRULE_Availability

        // 	Reservations in Warehouse
        if (!newRecord && isValueChanged("M_Warehouse_ID")) {
            val lines = getLines(false, null).toTypedArray()
            for (line in lines) {
                if (!line.canChangeWarehouse()) return false
            }
        }

        // 	No Partner Info - set Template
        if (businessPartnerId == 0) {
            setBPartner(Environment<Module>().module.businessPartnerService.getTemplate())
        }
        if (businessPartnerLocationId == 0)
            setBPartner(businessPartnerService.getById(businessPartnerId))
        // 	No Bill - get from Ship
        if (getInvoiceBusinessPartnerId() == 0) {
            setBill_BPartnerId(businessPartnerId)
            businessPartnerInvoicingLocationId = businessPartnerLocationId
        }
        if (businessPartnerInvoicingLocationId == 0)
            businessPartnerInvoicingLocationId = businessPartnerLocationId

        // 	Default Price List
        if (priceListId == 0) {
            val ii = getSQLValueEx(
                "SELECT M_PriceList_ID FROM M_PriceList " +
                        "WHERE AD_Client_ID=? AND IsSOPriceList=? AND IsActive=?" +
                        "ORDER BY IsDefault DESC",
                clientId,
                isSOTrx,
                true
            )
            if (ii != 0) priceListId = ii
        }
        // 	Default Currency
        if (currencyId == 0) {
            val sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?"
            val ii = getSQLValue(sql, priceListId)
            if (ii != 0)
                setCurrencyId(ii)
            else
                setCurrencyId(Env.getContextAsInt("#C_Currency_ID"))
        }

        // 	Default Sales Rep
        if (salesRepresentativeId == 0) {
            val ii = Env.getContextAsInt("#SalesRep_ID")
            if (ii != 0) salesRepresentativeId = ii
        }

        // 	Default Document Type
        if (targetDocumentTypeId == 0) setTargetDocumentTypeId(DocSubTypeSO_Standard)

        // 	Default Payment Term
        if (paymentTermId == 0) {
            var ii = Env.getContextAsInt("#C_PaymentTerm_ID")
            if (ii != 0)
                paymentTermId = ii
            else {
                val sql =
                    "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y' AND IsActive='Y'"
                ii = getSQLValue(sql, clientId)
                if (ii != 0) paymentTermId = ii
            }
        }

        // IDEMPIERE-63
        // for documents that can be reactivated we cannot allow changing
        // C_DocTypeTarget_ID or C_DocType_ID if they were already processed and
        // isOverwriteSeqOnComplete
        // neither change the Date if isOverwriteDateOnComplete
        val previousProcessedOn = getValueOld(COLUMNNAME_ProcessedOn) as BigDecimal?
        if (!newRecord && previousProcessedOn != null && previousProcessedOn.signum() > 0) {
            val previousDocTypeID = (getValueOld(COLUMNNAME_C_DocTypeTarget_ID) as Int?)!!
            val previousdt = getDocumentType(previousDocTypeID)
            if (isValueChanged(COLUMNNAME_C_DocType_ID) || isValueChanged(COLUMNNAME_C_DocTypeTarget_ID)) {
                if (previousdt.isOverwriteSeqOnComplete) {
                    log.saveError("Error", getMsg("CannotChangeProcessedDocType"))
                    return false
                }
            }
            if (isValueChanged(COLUMNNAME_DateOrdered)) {
                if (previousdt.isOverwriteDateOnComplete) {
                    log.saveError("Error", getMsg("CannotChangeProcessedDate"))
                    return false
                }
            }
        }

        // IDEMPIERE-1597 Price List and Date must be not-updateable
        if (!newRecord && (isValueChanged(COLUMNNAME_M_PriceList_ID) || isValueChanged(COLUMNNAME_DateOrdered))) {
            val cnt = getSQLValueEx(
                "SELECT COUNT(*) FROM C_OrderLine WHERE C_Order_ID=? AND M_Product_ID>0",
                orderId
            )
            if (cnt > 0) {
                if (isValueChanged(COLUMNNAME_M_PriceList_ID)) {
                    log.saveError("Error", getMsg("CannotChangePl"))
                    return false
                }
                if (isValueChanged(COLUMNNAME_DateOrdered)) {
                    val pList = MPriceList.get(priceListId)
                    val plOld =
                        pList.getPriceListVersion((getValueOld(COLUMNNAME_DateOrdered) as Timestamp?)!!)
                    val plNew =
                        pList.getPriceListVersion((getValue<Any>(COLUMNNAME_DateOrdered) as Timestamp?)!!)
                    if (plNew == null || plNew != plOld) {
                        log.saveError("Error", getMsg("CannotChangeDateOrdered"))
                        return false
                    }
                }
            }
        }

        if (!recursiveCall && !newRecord && isValueChanged(COLUMNNAME_C_PaymentTerm_ID)) {
            recursiveCall = true
            try {
                val pt = MPaymentTerm(paymentTermId)
                val valid = pt.applyOrder(this)
                setIsPayScheduleValid(valid)
            } finally {
                recursiveCall = false
            }
        }

        return true
    } // 	beforeSave

    /**
     * ************************************************************************ Process document
     *
     * @param action document action
     * @return true if performed
     */
    override fun processIt(action: String): Boolean {
        m_processMsg = null
        val engine = DocumentEngine(this, docStatus)
        return engine.processIt(action, docAction)
    } // 	processIt

    /**
     * Get Lines of Order. (used by web store)
     *
     * @return lines
     */
    override fun getLines(): List<I_C_OrderLine> {
        return getLines(false, null)
    } // 	getLines

    /**
     * Get Lines of Order
     *
     * @param requery requery
     * @param orderBy optional order by column
     * @return lines
     */
    override fun getLines(requery: Boolean, orderBy: String?): List<I_C_OrderLine> {
        if (super.m_lines != null && !requery) {
            return Arrays.asList(*super.m_lines!!)
        }
        //
        var orderClause = ""
        orderClause += if (orderBy != null && orderBy.isNotEmpty())
            orderBy
        else
            "Line"
        super.m_lines = getLines(null, orderClause)
        return Arrays.asList(*super.m_lines!!)
    } // 	getLines

    /**
     * ************************************************************************ Get Lines of Order
     *
     * @param whereClause where clause or null (starting with AND)
     * @param orderClause order clause
     * @return lines
     */
    override fun getLines(whereClause: String?, orderClause: String): Array<I_C_OrderLine> {
        // red1 - using new Query class from Teo / Victor's MDDOrder.java implementation
        val whereClauseFinal = StringBuilder(MOrderLine.COLUMNNAME_C_Order_ID + "=? ")
        if (!Util.isEmpty(whereClause, true)) whereClauseFinal.append(whereClause)
        val orderClause1 = if (orderClause.isEmpty()) MOrderLine.COLUMNNAME_Line else orderClause
        //
        val list = Query<I_C_OrderLine>(I_C_OrderLine.Table_Name, whereClauseFinal.toString())
            .setParameters(id)
            .setOrderBy(orderClause1)
            .list()
        for (ol in list) {
            ol.setHeaderInfo(this)
        }
        //
        return list.toTypedArray()
    } // 	getLines

    /**
     * ************************************************************************ Prepare Document
     *
     * @return new status (In Progress or Invalid)
     */
    override fun prepareIt(): String {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE)
        if (m_processMsg != null) return DocAction.STATUS_Invalid
        val dt = getDocumentType(targetDocumentTypeId)

        // 	Std Period open?
        if (!MPeriod.isOpen(dateAcct, dt.docBaseType, orgId)) {
            m_processMsg = "@PeriodClosed@"
            return DocAction.STATUS_Invalid
        }

        if (isSOTrx && deliveryViaRule == DELIVERYVIARULE_Shipper) {
            if (shipperId == 0) {
                m_processMsg = "@FillMandatory@" + getElementTranslation(COLUMNNAME_M_Shipper_ID)
                return DocAction.STATUS_Invalid
            }

            if (!calculateFreightCharge()) return DocAction.STATUS_Invalid
        }

        // 	Lines
        var lines = getLines(true, MOrderLine.COLUMNNAME_M_Product_ID).toTypedArray()
        if (lines.size == 0) {
            m_processMsg = "@NoLines@"
            return DocAction.STATUS_Invalid
        }

        // Bug 1564431
        if (deliveryRule != null && deliveryRule == DELIVERYRULE_CompleteOrder) {
            for (line in lines) {
                val product = line.product
                if (product != null && product.isExcludeAutoDelivery) {
                    m_processMsg = "@M_Product_ID@ " + product.searchKey + " @IsExcludeAutoDelivery@"
                    return DocAction.STATUS_Invalid
                }
            }
        }

        // 	Convert DocType to Target
        if (documentTypeId != targetDocumentTypeId) {
            // 	Cannot change Std to anything else if different warehouses
            if (documentTypeId != 0) {
                val dtOld = getDocumentType(documentTypeId)
                if (MDocType.DOCSUBTYPESO_StandardOrder == dtOld.docSubTypeSO && // 	From SO
                    MDocType.DOCSUBTYPESO_StandardOrder != dt.docSubTypeSO
                )
                // 	To !SO
                {
                    for (line in lines) {
                        if (line.warehouseId != warehouseId) {
                            log.warning("different Warehouse $line")
                            m_processMsg = "@CannotChangeDocType@"
                            return DocAction.STATUS_Invalid
                        }
                    }
                }
            }

            // 	New or in Progress/Invalid
            if (DOCSTATUS_Drafted == docStatus ||
                DOCSTATUS_InProgress == docStatus ||
                DOCSTATUS_Invalid == docStatus ||
                documentTypeId == 0
            ) {
                documentTypeId = targetDocumentTypeId
            } else
            // 	convert only if offer
            {
                if (dt.isOffer)
                    documentTypeId = targetDocumentTypeId
                else {
                    m_processMsg = "@CannotChangeDocType@"
                    return DocAction.STATUS_Invalid
                }
            }
        } // 	convert DocType

        // 	Mandatory Product Attribute Set Instance
        for (line in getLines()) {
            if (line.productId > 0 && line.attributeSetInstanceId == 0) {
                val product = line.product
                if (product.isASIMandatory(isSOTrx)) {
                    if (product.attributeSet == null) {
                        m_processMsg = "@NoAttributeSet@=" + product.searchKey
                        return DocAction.STATUS_Invalid
                    }
                    if (!product.attributeSet.excludeTableEntry(MOrderLine.Table_ID, isSOTrx)) {
                        m_processMsg = "@M_AttributeSet_ID@ @IsMandatory@ (@Line@ #" +
                                line.line +
                                ", @M_Product_ID@=" +
                                product.searchKey +
                                ")"
                        return DocAction.STATUS_Invalid
                    }
                }
            }
        }

        // 	Lines
        if (explodeBOM()) lines = getLines(true, MOrderLine.COLUMNNAME_M_Product_ID).toTypedArray()
        if (!reserveStock(dt, lines)) {
            val innerMsg = CLogger.retrieveErrorString("")
            m_processMsg = "Cannot reserve Stock"
            if (!Util.isEmpty(innerMsg)) m_processMsg = "$m_processMsg -> $innerMsg"
            return DocAction.STATUS_Invalid
        }
        if (!calculateTaxTotal()) {
            m_processMsg = "Error calculating tax"
            return DocAction.STATUS_Invalid
        }

        if (grandTotal.signum() != 0 && (PAYMENTRULE_OnCredit == paymentRule || PAYMENTRULE_DirectDebit == paymentRule)) {
            if (!createPaySchedule()) {
                m_processMsg = "@ErrorPaymentSchedule@"
                return DocAction.STATUS_Invalid
            }
        } else {
            if (MOrderPaySchedule.getOrderPaySchedule(orderId, 0).size > 0) {
                m_processMsg = "@ErrorPaymentSchedule@"
                return DocAction.STATUS_Invalid
            }
        }

        // 	Credit Check
        if (isSOTrx) {
            if (MDocType.DOCSUBTYPESO_POSOrder != dt.docSubTypeSO ||
                PAYMENTRULE_Cash != paymentRule ||
                MSysConfig.getBooleanValue(
                    MSysConfig.CHECK_CREDIT_ON_CASH_POS_ORDER, true, clientId, orgId
                )
            ) {
                if (MDocType.DOCSUBTYPESO_PrepayOrder != dt.docSubTypeSO || MSysConfig.getBooleanValue(
                        MSysConfig.CHECK_CREDIT_ON_PREPAY_ORDER, true, clientId, orgId
                    )
                ) {
                    val bp = businessPartnerService.getById(getInvoiceBusinessPartnerId()) ?: throw AdempiereException("Bill business partner not found") // bill bp is guaranteed on beforeSave

                    if (grandTotal.signum() > 0)
                    // IDEMPIERE-365 - just check credit if is going to increase the debt
                    {

                        if (MBPartner.SOCREDITSTATUS_CreditStop == bp.soCreditStatus) {
                            m_processMsg = ("@BPartnerCreditStop@ - @TotalOpenBalance@=" +
                                    bp.totalOpenBalance +
                                    ", @SO_CreditLimit@=" +
                                    bp.salesOrderCreditLimit)
                            return DocAction.STATUS_Invalid
                        }
                        if (MBPartner.SOCREDITSTATUS_CreditHold == bp.soCreditStatus) {
                            m_processMsg = ("@BPartnerCreditHold@ - @TotalOpenBalance@=" +
                                    bp.totalOpenBalance +
                                    ", @SO_CreditLimit@=" +
                                    bp.salesOrderCreditLimit)
                            return DocAction.STATUS_Invalid
                        }
                        val grandTotal = MConversionRate.convertBase(

                            grandTotal,
                            currencyId,
                            dateOrdered,
                            conversionTypeId,
                            clientId,
                            orgId
                        )
                        if (MBPartner.SOCREDITSTATUS_CreditHold == bp.getSOCreditStatus(grandTotal)) {
                            m_processMsg = ("@BPartnerOverOCreditHold@ - @TotalOpenBalance@=" +
                                    bp.totalOpenBalance +
                                    ", @GrandTotal@=" +
                                    grandTotal +
                                    ", @SO_CreditLimit@=" +
                                    bp.salesOrderCreditLimit)
                            return DocAction.STATUS_Invalid
                        }
                    }
                }
            }
        }

        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE)
        if (m_processMsg != null) return DocAction.STATUS_Invalid

        m_justPrepared = true
        // 	if (!DOCACTION_Complete.equals(docAction))		don't set for just prepare
        // 		setDocAction(DOCACTION_Complete);
        return DocAction.STATUS_InProgress
    } // 	prepareIt

    /**
     * Reserve Inventory. Counterpart: MInOut.completeIt()
     *
     * @param dt document type or null
     * @param lines order lines (ordered by M_Product_ID for deadlock prevention)
     * @return true if (un) reserved
     */
    protected fun reserveStock(dt: MDocType?, lines: Array<I_C_OrderLine>): Boolean {
        val documentType = dt ?: getDocumentType(documentTypeId)

        // 	Binding
        var binding = !documentType.isProposal
        // 	Not binding - i.e. Target=0
        if (DOCACTION_Void == docAction ||
            // 	Closing Binding Quotation
            MDocType.DOCSUBTYPESO_Quotation == documentType.docSubTypeSO && DOCACTION_Close == docAction
        )
        // || isDropShip() )
            binding = false
        val isSOTrx = isSOTrx
        if (log.isLoggable(Level.FINE)) log.fine("Binding=$binding - IsSOTrx=$isSOTrx")
        // 	Force same WH for all but SO/PO
        val header_M_Warehouse_ID =
            if (MDocType.DOCSUBTYPESO_StandardOrder == documentType.docSubTypeSO || MDocType.DOCBASETYPE_PurchaseOrder == documentType.docBaseType)
                0 else warehouseId // 	don't enforce

        var Volume = Env.ZERO
        var Weight = Env.ZERO

        // 	Always check and (un) Reserve Inventory
        for (line in lines) {
            // 	Check/set WH/Org
            if (header_M_Warehouse_ID != 0)
            // 	enforce WH
            {
                if (header_M_Warehouse_ID != line.warehouseId)
                    line.warehouseId = header_M_Warehouse_ID
                if (orgId != line.orgId) line.setOrgId(orgId)
            }
            // 	Binding
            val target = if (binding) line.qtyOrdered else Env.ZERO
            var difference = target.subtract(line.qtyReserved).subtract(line.qtyDelivered)

            if (difference.signum() == 0 || line.qtyOrdered.signum() < 0) {
                if (difference.signum() == 0 || line.qtyReserved.signum() == 0) {
                    val product = line.product
                    if (product != null) {
                        Volume = Volume.add(product.volume.multiply(line.qtyOrdered))
                        Weight = Weight.add(product.weight.multiply(line.qtyOrdered))
                    }
                    continue
                } else if (line.qtyOrdered.signum() < 0 && line.qtyReserved.signum() > 0) {
                    difference = line.qtyReserved.negate()
                }
            }

            if (log.isLoggable(Level.FINE))
                log.fine(
                    "Line=" +
                            line.line +
                            " - Target=" +
                            target +
                            ",Difference=" +
                            difference +
                            " - Ordered=" +
                            line.qtyOrdered +
                            ",Reserved=" +
                            line.qtyReserved +
                            ",Delivered=" +
                            line.qtyDelivered
                )

            // 	Check Product - Stocked and Item
            val product = line.product
            if (product != null) {
                if (product.isStocked) {
                    // 	Update Reservation Storage
                    if (!MStorageReservation.add(

                            line.warehouseId,
                            line.productId,
                            line.attributeSetInstanceId,
                            difference,
                            isSOTrx
                        )
                    )
                        return false
                } // 	stocked
                // 	update line
                line.qtyReserved = line.qtyReserved.add(difference)
                if (!line.save()) return false
                //
                Volume = Volume.add(product.volume.multiply(line.qtyOrdered))
                Weight = Weight.add(product.weight.multiply(line.qtyOrdered))
            } // 	product
        } // 	reverse inventory

        setVolume(Volume)
        setWeight(Weight)
        return true
    } // 	reserveStock

    /**
     * ************************************************************************ Complete Document
     *
     * @return new status (Complete, In Progress, Invalid, Waiting ..)
     */
    override fun completeIt(): CompleteActionResult<I_C_Invoice> {
        val dt = getDocumentType(documentTypeId)
        val DocSubTypeSO = dt.docSubTypeSO

        // 	Just prepare
        if (DOCACTION_Prepare == docAction) {
            isProcessed = false
            return CompleteActionResult(DocAction.STATUS_InProgress)
        }

        // Set the definite document number after completed (if needed)
        setDefiniteDocumentNo()

        // 	Offers
        if (MDocType.DOCSUBTYPESO_Proposal == DocSubTypeSO || MDocType.DOCSUBTYPESO_Quotation == DocSubTypeSO) {
            // 	Binding
            if (MDocType.DOCSUBTYPESO_Quotation == DocSubTypeSO)
                reserveStock(dt, getLines(true, MOrderLine.COLUMNNAME_M_Product_ID).toTypedArray())
            m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE)
            if (m_processMsg != null)
                return CompleteActionResult(DocAction.STATUS_Invalid)
            m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE)
            if (m_processMsg != null)
                return CompleteActionResult(DocAction.STATUS_Invalid)
            isProcessed = true
            return CompleteActionResult(DocAction.STATUS_Completed)
        }
        // 	Waiting Payment - until we have a payment
        if (!m_forceCreation &&
            MDocType.DOCSUBTYPESO_PrepayOrder == DocSubTypeSO &&
            paymentId == 0 &&
            cashLineId == 0
        ) {
            isProcessed = true
            return CompleteActionResult(DocAction.STATUS_WaitingPayment)
        }

        // 	Re-Check
        if (!m_justPrepared) {
            val status = prepareIt()
            m_justPrepared = false
            if (DocAction.STATUS_InProgress != status)
                return CompleteActionResult(status)
        }

        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE)
        if (m_processMsg != null)
            return CompleteActionResult(DocAction.STATUS_Invalid)

        // 	Implicit Approval
        if (!isApproved) approveIt()
        getLines(true, null)
        if (log.isLoggable(Level.INFO)) log.info(toString())
        val info = StringBuilder()

        val realTimePOS = MSysConfig.getBooleanValue(MSysConfig.REAL_TIME_POS, false, clientId)

        // 	Create SO Shipment - Force Shipment
        var shipment: MInOut? = null
        if (MDocType.DOCSUBTYPESO_OnCreditOrder == DocSubTypeSO || // 	(W)illCall(I)nvoice

            MDocType.DOCSUBTYPESO_WarehouseOrder == DocSubTypeSO || // 	(W)illCall(P)ickup

            MDocType.DOCSUBTYPESO_POSOrder == DocSubTypeSO || // 	(W)alkIn(R)eceipt

            MDocType.DOCSUBTYPESO_PrepayOrder == DocSubTypeSO
        ) {
            if (DELIVERYRULE_Force != deliveryRule) {
                val wh = MWarehouse(warehouseId)
                if (!wh.isDisallowNegativeInv) deliveryRule = DELIVERYRULE_Force
            }
            //
            shipment = createShipment(dt, if (realTimePOS) null else dateOrdered)
            if (shipment == null)
                return CompleteActionResult(DocAction.STATUS_Invalid)
            info.append("@M_InOut_ID@: ").append(shipment.documentNo)
            val msg = shipment.processMsg
            if (msg != null && msg.isNotEmpty()) info.append(" (").append(msg).append(")")
        } // 	Shipment

        var invoice: I_C_Invoice? = null
        // 	Create SO Invoice - Always invoice complete Order
        if (MDocType.DOCSUBTYPESO_POSOrder == DocSubTypeSO ||
            MDocType.DOCSUBTYPESO_OnCreditOrder == DocSubTypeSO ||
            MDocType.DOCSUBTYPESO_PrepayOrder == DocSubTypeSO
        ) {
            invoice = createInvoice(dt, shipment, if (realTimePOS) null else dateOrdered)
            if (invoice == null) return CompleteActionResult(DocAction.STATUS_Invalid)
            info.append(" - @C_Invoice_ID@: ").append(invoice.documentNo)
            val msg = invoice.processMsg
            if (msg != null && msg.length > 0) info.append(" (").append(msg).append(")")
        } // 	Invoice

        val msg = createPOSPayments()
        if (msg != null) {
            m_processMsg = msg
            return CompleteActionResult(DocAction.STATUS_Invalid)
        }

        // 	Counter Documents
        val counter = createCounterDoc()
        if (counter != null) info.append(" - @CounterDoc@: @Order@=").append(counter.documentNo)
        // 	User Validation
        val valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE)
        if (valid != null) {
            if (info.length > 0) info.append(" - ")
            info.append(valid)
            m_processMsg = info.toString()
            return CompleteActionResult(DocAction.STATUS_Invalid)
        }

        // landed cost
        if (!isSOTrx) {
            val error = landedCostAllocation()
            if (!Util.isEmpty(error)) {
                m_processMsg = error
                return CompleteActionResult(DocAction.STATUS_Invalid)
            }
        }

        isProcessed = true
        m_processMsg = info.toString()
        //
        setDocAction(DOCACTION_Close)
        return CompleteActionResult(DocAction.STATUS_Completed, invoice)
    } // 	completeIt

    protected fun landedCostAllocation(): String {
        val landedCosts = MOrderLandedCost.getOfOrder(orderId)
        for (landedCost in landedCosts) {
            val error = landedCost.distributeLandedCost()
            if (!Util.isEmpty(error)) return error
        }
        return ""
    }

    protected fun createPOSPayments(): String? {

        // Just for POS order with payment rule mixed
        if (!this.isSOTrx) return null
        if (DocSubTypeSO_POS != this.documentType.docSubTypeSO)
            return null
        if (PAYMENTRULE_MixedPOSPayment != this.paymentRule) return null

        // Verify sum of all payments pos must be equal to the grandtotal of POS invoice (minus
        // withholdings)
        val invoices = this.invoices
        if (invoices == null || invoices.size == 0) return "@NoPOSInvoices@"
        val lastInvoice = invoices[0]
        val grandTotal = lastInvoice.grandTotal

        val pps = Query<I_C_POSPayment>(X_C_POSPayment.Table_Name, "C_Order_ID=?")
            .setParameters(this.orderId)
            .setOnlyActiveRecords(true)
            .list()
        var totalPOSPayments = Env.ZERO
        for (pp in pps) {
            totalPOSPayments = totalPOSPayments.add(pp.payAmt)
        }
        if (totalPOSPayments.compareTo(grandTotal) != 0)
            return ("@POSPaymentDiffers@ - @C_POSPayment_ID@=" +
                    totalPOSPayments +
                    ", @GrandTotal@=" +
                    grandTotal)

        val whereClause = "AD_Org_ID=? AND C_Currency_ID=?"
        val ba = Query<I_C_BankAccount>(MBankAccount.Table_Name, whereClause)
            .setParameters(this.orgId, this.currencyId)
            .setOrderBy("IsDefault DESC")
            .first() ?: return "@NoAccountOrgCurrency@"

        val doctypes = getDocumentTypeOfDocBaseType(MDocType.DOCBASETYPE_ARReceipt)
        if (doctypes.size == 0) return "No document type for AR Receipt"
        var doctype: DocumentType? = null
        for (doc in doctypes) {
            if (doc.orgId == this.orgId) {
                doctype = doc
                break
            }
        }
        if (doctype == null) doctype = doctypes[0]

        // Create a payment for each non-guarantee record
        // associate the payment id and mark the record as processed
        for (pp in pps) {
            val tt = X_C_POSTenderType(pp.posTenderTypeId)
            if (tt.isGuarantee) continue
            if (pp.isPostDated) continue

            val payment = MPayment(0)
            payment.setOrgId(this.orgId)

            payment.tenderType = pp.tenderType
            if (MPayment.TENDERTYPE_CreditCard == pp.tenderType) {
                payment.trxType = MPayment.TRXTYPE_Sales
                payment.creditCardType = pp.creditCardType
                payment.creditCardNumber = pp.creditCardNumber
                payment.voiceAuthCode = pp.voiceAuthCode
            }

            payment.bankAccountId = ba.bankAccountId
            payment.routingNo = pp.routingNo
            payment.accountNo = pp.accountNo
            payment.swiftCode = pp.swiftCode
            payment.iban = pp.iban
            payment.checkNo = pp.checkNo
            payment.micr = pp.micr
            payment.setIsPrepayment(false)

            payment.dateAcct = this.dateAcct
            payment.dateTrx = this.dateOrdered
            //
            payment.businessPartnerId = this.businessPartnerId
            payment.invoiceId = lastInvoice.invoiceId
            // payment.setOrderId(this.getOrderId()); / do not set order to avoid the prepayment
            // flag
            payment.documentTypeId = doctype.docTypeId
            payment.setCurrencyId(this.currencyId)

            payment.payAmt = pp.payAmt

            // 	Copy statement line reference data
            payment.accountName = pp.accountName

            payment.posTenderTypeId = pp.posTenderTypeId

            // 	Save payment
            payment.saveEx()

            pp.setPaymentId(payment.paymentId)
            pp.setProcessed(true)
            pp.saveEx()

            payment.setDocAction(MPayment.DOCACTION_Complete)
            if (!payment.processIt(MPayment.DOCACTION_Complete))
                return "Cannot Complete the Payment :$payment"

            payment.saveEx()
        }

        return null
    }

    /**
     * Set the definite document number after completed
     */
    protected fun setDefiniteDocumentNo() {
        val dt = getDocumentType(documentTypeId)
        if (dt.isOverwriteDateOnComplete) {
            /* a42niem - BF IDEMPIERE-63 - check if document has been completed before */
            if (this.processedOn.signum() == 0) {
                dateOrdered = Timestamp(System.currentTimeMillis())
                if (dateAcct.before(dateOrdered)) {
                    dateAcct = dateOrdered
                    MPeriod.testPeriodOpen(dateAcct, documentTypeId, orgId)
                }
            }
        }
        if (dt.isOverwriteSeqOnComplete) {
            /* a42niem - BF IDEMPIERE-63 - check if document has been completed before */
            if (this.processedOn.signum() == 0) {
                val value = MSequence.getDocumentNo(documentTypeId, true, this)
                if (value != null) setDocumentNo(value)
            }
        }
    }

    /**
     * Create Shipment
     *
     * @param dt order document type
     * @param movementDate optional movement date (default today)
     * @return shipment or null
     */
    protected fun createShipment(dt: MDocType, movementDate: Timestamp?): MInOut? {
        if (log.isLoggable(Level.INFO)) log.info("For $dt")
        val shipment = MInOut(this, dt.docTypeShipmentId, movementDate)
        // 	shipment.setDateAcct(getDateAcct());
        if (!shipment.save()) {
            m_processMsg = "Could not create Shipment"
            return null
        }
        //
        val oLines = getLines(true, null).toTypedArray()
        for (oLine in oLines) {
            //
            val ioLine = MInOutLine(shipment)
            // 	Qty = Ordered - Delivered
            val MovementQty = oLine.qtyOrdered.subtract(oLine.qtyDelivered)
            // 	Location
            var M_Locator_ID = MStorageOnHand.getLocatorId(
                oLine.warehouseId,
                oLine.productId,
                oLine.attributeSetInstanceId,
                MovementQty
            )
            if (M_Locator_ID == 0)
            // 	Get default Location
            {
                val wh = MWarehouse.get(oLine.warehouseId)
                M_Locator_ID = wh.defaultLocator!!.locatorId
            }
            //
            ioLine.setOrderLine(oLine, M_Locator_ID, MovementQty)
            ioLine.setQty(MovementQty)
            if (oLine.qtyEntered.compareTo(oLine.qtyOrdered) != 0)
                ioLine.qtyEntered = MovementQty.multiply(oLine.qtyEntered)
                    .divide(oLine.qtyOrdered, 6, BigDecimal.ROUND_HALF_UP)
            if (!ioLine.save()) {
                m_processMsg = "Could not create Shipment Line"
                return null
            }
        }
        // added AdempiereException by zuhri
        if (!shipment.processIt(DocAction.ACTION_Complete))
            throw AdempiereException("Failed when processing document - " + shipment.processMsg)
        // end added
        shipment.saveEx()
        if (DOCSTATUS_Completed != shipment.docStatus) {
            m_processMsg = "@M_InOut_ID@: " + shipment.processMsg
            return null
        }
        return shipment
    } // 	createShipment

    /**
     * Create Counter Document
     *
     * @return counter order
     */
    protected fun createCounterDoc(): org.compiere.order.MOrder? {
        // 	Is this itself a counter doc ?
        if (ref_OrderId != 0) return null

        // 	Org Must be linked to BPartner
        val org = getOrg(orgId)
        val counterC_BPartner_ID = org.linkedBusinessPartnerId
        if (counterC_BPartner_ID == 0) return null
        // 	Business Partner needs to be linked to Org
        val bp = businessPartnerService.getById(businessPartnerId) ?: throw AdempiereException("Business partner not found")
        val counterAD_Org_ID = bp.linkedOrganizationId
        if (counterAD_Org_ID == 0) return null

        val counterBP = businessPartnerService.getById(counterC_BPartner_ID) ?: throw AdempiereException("Business partner not found")
        val counterOrgInfo = getOrganizationInfo(counterAD_Org_ID)
        if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.name)

        // 	Document Type
        val C_DocTypeTarget_ID: Int
        val counterDT = MDocTypeCounter.getCounterDocType(documentTypeId)
        if (counterDT != null) {
            if (log.isLoggable(Level.FINE)) log.fine(counterDT.toString())
            if (!counterDT.isCreateCounter || !counterDT.isValid) return null
            C_DocTypeTarget_ID = counterDT.counterDocTypeId
        } else
        // 	indirect
        {
            C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocTypeId(documentTypeId)
            if (log.isLoggable(Level.FINE)) log.fine("Indirect C_DocTypeTarget_ID=$C_DocTypeTarget_ID")
            if (C_DocTypeTarget_ID <= 0) return null
        }
        // 	Deep Copy
        val counter = copyFrom(
            this, dateOrdered, C_DocTypeTarget_ID, !isSOTrx, counter = true, copyASI = false
        )
        //
        counter.setOrgId(counterAD_Org_ID)
        counter.warehouseId = counterOrgInfo.warehouseId
        //
        // 		counter.setBPartner(counterBP); // was set on copyFrom
        counter.datePromised = datePromised // default is date ordered
        // 	References (Should not be required)
        counter.salesRepresentativeId = salesRepresentativeId
        counter.saveEx()

        // 	Update copied lines
        val counterLines = counter.getLines(true, null).toTypedArray()
        for (counterLine in counterLines) {
            counterLine.order = counter // 	copies header values (BP, etc.)
            counterLine.setPrice()
            counterLine.setTax()
            counterLine.saveEx()
        }
        if (log.isLoggable(Level.FINE)) log.fine(counter.toString())

        // 	Document Action
        if (counterDT != null) {
            if (counterDT.docAction != null) {
                counter.setDocAction(counterDT.docAction)
                // added AdempiereException by zuhri
                if (!counter.processIt(counterDT.docAction))
                    throw AdempiereException(
                        "Failed when processing document - " + counter.processMsg
                    )
                // end added
                counter.saveEx()
            }
        }
        return counter
    } // 	createCounterDoc

    /**
     * Void Document. Set Qtys to 0 - Sales: reverse all documents
     *
     * @return true if success
     */
    override fun voidIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        // Before Void
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID)
        if (m_processMsg != null) return false

        if (link_OrderId > 0) {
            val so = org.compiere.order.MOrder(link_OrderId)
            so.link_OrderId = 0
            so.saveEx()
        }

        if (!createReversals()) return false

        val lines = getLines(true, MOrderLine.COLUMNNAME_M_Product_ID).toTypedArray()
        for (line in lines) {
            val old = line.qtyOrdered
            if (old.signum() != 0) {
                line.addDescription(getMsg("Voided") + " (" + old + ")")
                line.setQty(Env.ZERO)
                line.lineNetAmt = Env.ZERO
                line.saveEx()
            }
            // AZ Goodwill
            if (!isSOTrx) {
                deleteMatchPOCostDetail(line)
            }
            if (line.link_OrderLineId > 0) {
                val soline = MOrderLine(line.link_OrderLineId)
                soline.link_OrderLineId = 0
                soline.saveEx()
            }
        }

        // update taxes
        val taxes = getTaxes(true)
        for (tax in taxes) {
            if (!(tax.calculateTaxFromLines() && tax.save())) return false
        }

        addDescription(getMsg("Voided"))
        // 	Clear Reservations
        if (!reserveStock(null, lines)) {
            m_processMsg = "Cannot unreserve Stock (void)"
            return false
        }

        // UnLink All Requisitions
        MRequisitionLine.unlinkC_OrderId(id)

        /* globalqss - 2317928 - Reactivating/Voiding order must reset posted */
        MFactAcct.deleteEx(Table_ID, orderId)
        isPosted = false

        // After Void
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID)
        if (m_processMsg != null) return false

        totalLines = Env.ZERO
        grandTotal = Env.ZERO
        isProcessed = true
        setDocAction(DOCACTION_None)
        return true
    } // 	voidIt

    /**
     * Get Shipments of Order
     *
     * @return shipments
     */
    override fun getShipments(): Array<I_M_InOut> {
        val whereClause = ("EXISTS (SELECT 1 FROM M_InOutLine iol, C_OrderLine ol" +
                " WHERE iol.M_InOut_ID=M_InOut.M_InOut_ID" +
                " AND iol.C_OrderLine_ID=ol.C_OrderLine_ID" +
                " AND ol.C_Order_ID=?)")
        val list = Query<I_M_InOut>(I_M_InOut.Table_Name, whereClause)
            .setParameters(id)
            .setOrderBy("M_InOut_ID DESC")
            .list()
        return list.toTypedArray()
    } // 	getShipments

    /**
     * Create Shipment/Invoice Reversals
     *
     * @return true if success
     */
    protected fun createReversals(): Boolean {
        // 	Cancel only Sales
        if (!isSOTrx) return true

        log.info("createReversals")
        val info = StringBuilder()

        // 	Reverse All *Shipments*
        info.append("@M_InOut_ID@:")
        val shipments = shipments
        for (ship in shipments) {
            // 	if closed - ignore
            if (MInOut.DOCSTATUS_Closed == ship.docStatus ||
                MInOut.DOCSTATUS_Reversed == ship.docStatus ||
                MInOut.DOCSTATUS_Voided == ship.docStatus
            )
                continue

            // 	If not completed - void - otherwise reverse it
            if (MInOut.DOCSTATUS_Completed != ship.docStatus) {
                if (ship.voidIt()) ship.docStatus = MInOut.DOCSTATUS_Voided
            } else if (ship.reverseCorrectIt())
            // 	completed shipment
            {
                ship.docStatus = MInOut.DOCSTATUS_Reversed
                info.append(" ").append(ship.documentNo)
            } else {
                m_processMsg = "Could not reverse Shipment $ship"
                return false
            }
            ship.docAction = MInOut.DOCACTION_None
            ship.saveEx()
        } // 	for all shipments

        // 	Reverse All *Invoices*
        info.append(" - @C_Invoice_ID@:")
        val invoices = invoices
        for (invoice in invoices) {
            // 	if closed - ignore
            if (I_C_Invoice.DOCSTATUS_Closed == invoice.docStatus ||
                I_C_Invoice.DOCSTATUS_Reversed == invoice.docStatus ||
                I_C_Invoice.DOCSTATUS_Voided == invoice.docStatus
            )
                continue

            // 	If not completed - void - otherwise reverse it
            if (I_C_Invoice.DOCSTATUS_Completed != invoice.docStatus) {
                if (invoice.voidIt()) invoice.docStatus = I_C_Invoice.DOCSTATUS_Voided
            } else if (invoice.reverseCorrectIt())
            // 	completed invoice
            {
                invoice.docStatus = I_C_Invoice.DOCSTATUS_Reversed
                info.append(" ").append(invoice.documentNo)
            } else {
                m_processMsg = "Could not reverse Invoice $invoice"
                return false
            }
            invoice.setDocAction(MInvoice.DOCACTION_None)
            invoice.saveEx()
        } // 	for all shipments

        m_processMsg = info.toString()
        return true
    } // 	createReversals

    /**
     * Close Document. Cancel not delivered Quantities
     *
     * @return true if success
     */
    override fun closeIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        // Before Close
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_CLOSE)
        if (m_processMsg != null) return false

        // 	Close Not delivered Qty - SO/PO
        val lines = getLines(true, MOrderLine.COLUMNNAME_M_Product_ID).toTypedArray()
        for (line in lines) {
            val old = line.qtyOrdered
            if (old.compareTo(line.qtyDelivered) != 0) {
                line.qtyLostSales = line.qtyOrdered.subtract(line.qtyDelivered)
                line.qtyOrdered = line.qtyDelivered
                // 	QtyEntered unchanged
                line.addDescription("Close ($old)")
                line.saveEx()
            }
        }
        // 	Clear Reservations
        if (!reserveStock(null, lines)) {
            m_processMsg = "Cannot unreserve Stock (close)"
            return false
        }

        isProcessed = true
        setDocAction(DOCACTION_None)
        // After Close
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE)
        return m_processMsg == null
    } // 	closeIt

    /**
     * @author: phib re-open a closed order (reverse steps of close())
     */
    fun reopenIt(): String {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        if (DOCSTATUS_Closed != docStatus) {
            return "Not closed - can't reopen"
        }

        //
        val lines = getLines(true, MOrderLine.COLUMNNAME_M_Product_ID).toTypedArray()
        for (line in lines) {
            if (Env.ZERO.compareTo(line.qtyLostSales) != 0) {
                line.qtyOrdered = line.qtyLostSales.add(line.qtyDelivered)
                line.qtyLostSales = Env.ZERO
                // 	QtyEntered unchanged

                // Strip Close() tags from description
                var desc: String? = line.description
                if (desc == null) desc = ""
                val pattern = Pattern.compile("( \\| )?Close \\(.*\\)")
                val parts = pattern.split(desc)
                desc = ""
                for (s in parts) {
                    desc = desc + s
                }
                line.description = desc
                if (!line.save()) return "Couldn't save orderline"
            }
        }
        // 	Clear Reservations
        if (!reserveStock(null, lines)) {
            m_processMsg = "Cannot unreserve Stock (close)"
            return "Failed to update reservations"
        }

        docStatus = DOCSTATUS_Completed
        setDocAction(DOCACTION_Close)
        return if (!this.save())
            "Couldn't save reopened order"
        else
            ""
    } // 	reopenIt

    /**
     * Reverse Correction - same void
     *
     * @return true if success
     */
    override fun reverseCorrectIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        // Before reverseCorrect
        m_processMsg = ModelValidationEngine.get()
            .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSECORRECT)
        if (m_processMsg != null) return false

        // After reverseCorrect
        m_processMsg = ModelValidationEngine.get()
            .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT)
        return if (m_processMsg != null) false else voidIt()
    } // 	reverseCorrectionIt

    /**
     * Reverse Accrual - none
     *
     * @return false
     */
    override fun reverseAccrualIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        // Before reverseAccrual
        m_processMsg = ModelValidationEngine.get()
            .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSEACCRUAL)
        if (m_processMsg != null) return false

        // After reverseAccrual
        m_processMsg = ModelValidationEngine.get()
            .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL)
        return if (m_processMsg != null) false else false
    } // 	reverseAccrualIt

    /**
     * Re-activate.
     *
     * @return true if success
     */
    override fun reActivateIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        // Before reActivate
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_REACTIVATE)
        if (m_processMsg != null) return false

        val dt = getDocumentType(documentTypeId)
        val DocSubTypeSO = dt.docSubTypeSO

        // 	Replace Prepay with POS to revert all doc
        if (MDocType.DOCSUBTYPESO_PrepayOrder == DocSubTypeSO) {
            var newDT: DocumentType? = null
            val dts = getClientDocumentTypes
            for (type in dts) {
                if (MDocType.DOCSUBTYPESO_PrepayOrder == type.docSubTypeSO) {
                    if (type.isDefault || newDT == null) newDT = type
                }
            }
            if (newDT == null)
                return false
            else
                documentTypeId = newDT.docTypeId
        }

        // 	PO - just re-open
        if (!isSOTrx) {
            if (log.isLoggable(Level.INFO)) log.info("Existing documents not modified - $dt")
            // 	Reverse Direct Documents
        } else if (MDocType.DOCSUBTYPESO_OnCreditOrder == DocSubTypeSO || // 	(W)illCall(I)nvoice

            MDocType.DOCSUBTYPESO_WarehouseOrder == DocSubTypeSO || // 	(W)illCall(P)ickup

            MDocType.DOCSUBTYPESO_POSOrder == DocSubTypeSO
        )
        // 	(W)alkIn(R)eceipt
        {
            if (!createReversals()) return false
        } else {
            if (log.isLoggable(Level.INFO))
                log.info("Existing documents not modified - SubType=$DocSubTypeSO")
        }

        /* globalqss - 2317928 - Reactivating/Voiding order must reset posted */
        MFactAcct.deleteEx(Table_ID, orderId)
        isPosted = false

        // After reActivate
        m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_REACTIVATE)
        if (m_processMsg != null) return false

        setDocAction(DOCACTION_Complete)
        isProcessed = false
        return true
    } // 	reActivateIt

    // AZ Goodwill
    protected fun deleteMatchPOCostDetail(line: I_C_OrderLine): String {
        // Get Account Schemas to delete MCostDetail
        val acctschemas = MAcctSchema.getClientAcctSchema(clientId)
        for (`as` in acctschemas) {
            if (`as`.isSkipOrg(id)) {
                continue
            }

            // update/delete Cost Detail and recalculate Current Cost
            val mPO = MMatchPO.getOrderLine(line.orderLineId)
            // delete Cost Detail if the Matched PO has been deleted
            if (mPO.size == 0) {
                val cd = MCostDetail.get(

                    "C_OrderLine_ID=?",
                    line.orderLineId,
                    line.attributeSetInstanceId,
                    `as`.accountingSchemaId
                )
                if (cd != null) {
                    cd.isProcessed = false
                    cd.delete(true)
                }
            }
        }

        return ""
    }

    override fun setDoc(doc: IDoc) {}

    override fun setProcessedOn(processed: String, b: Boolean, b1: Boolean) {}

    /**
     * Unlock Document.
     *
     * @return true if success
     */
    override fun unlockIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info("unlockIt - " + toString())
        setProcessing(false)
        return true
    } // 	unlockIt

    /**
     * Invalidate Document
     *
     * @return true if success
     */
    override fun invalidateIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(toString())
        setDocAction(DOCACTION_Prepare)
        return true
    } // 	invalidateIt

    /**
     * Reject Approval
     *
     * @return true if success
     */
    override fun rejectIt(): Boolean {
        if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + toString())
        setIsApproved(false)
        return true
    } // 	rejectIt

    override var docStatus: String
        get() = getValue(I_C_Order.COLUMNNAME_DocStatus) ?: throw AdempiereException("Status not found")
        set(value) {
            setValue(I_C_Order.COLUMNNAME_DocStatus, value) }

    /**
     * Document Status is Complete or Closed
     *
     * @return true if CO, CL or RE
     */
    override fun isComplete(): Boolean {
        val ds = docStatus
        return (DOCSTATUS_Completed == ds ||
                DOCSTATUS_Closed == ds ||
                DOCSTATUS_Reversed == ds)
    } // 	isComplete

    companion object {
        /**
         * Create new Order by copying
         *
         * @param from order
         * @param dateDoc date of the document date
         * @param C_DocTypeTarget_ID target document type
         * @param isSOTrx sales order
         * @param counter create counter links
         * @param copyASI copy line attributes Attribute Set Instance, Resaouce Assignment
         * @return Order
         */
        fun copyFrom(
            from: MOrder,
            dateDoc: Timestamp,
            C_DocTypeTarget_ID: Int,
            isSOTrx: Boolean,
            counter: Boolean,
            copyASI: Boolean
        ): MOrder {
            val to = MOrder(0)
            return doCopyFrom(
                from,
                dateDoc,
                C_DocTypeTarget_ID,
                isSOTrx,
                counter,
                copyASI,
                to
            ) as MOrder
        }
    }
}
