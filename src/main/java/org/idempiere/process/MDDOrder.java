package org.idempiere.process;

import kotliquery.Row;
import org.compiere.accounting.MPeriod;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.accounting.NegativeInventoryDisallowedException;
import org.compiere.crm.MBPartner;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_M_Product;
import org.compiere.model.User;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.production.MLocator;
import org.compiere.production.MProject;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.eevolution.model.I_DD_Order;
import org.eevolution.model.I_DD_OrderLine;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;
import org.jetbrains.annotations.NotNull;
import software.hsharp.core.util.Environment;
import software.hsharp.modules.Module;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;

public class MDDOrder extends X_DD_Order implements DocAction, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -5997157712614274458L;
    /**
     * Order Lines
     */
    private MDDOrderLine[] m_lines = null;
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;

    /**
     * ************************************************************************ Default Constructor
     *
     * @param DD_Order_ID order to load, (0 create new order)
     */
    public MDDOrder(int DD_Order_ID) {
        super(DD_Order_ID);
        //  New
        if (DD_Order_ID == 0) {
            setDocStatus(DOCSTATUS_Drafted);
            setDocAction(DOCACTION_Prepare);
            //
            setDeliveryRule(DELIVERYRULE_Availability);
            setFreightCostRule(FREIGHTCOSTRULE_FreightIncluded);
            setPriorityRule(PRIORITYRULE_Medium);
            setDeliveryViaRule(DELIVERYVIARULE_Pickup);
            //
            setIsSelected(false);
            setIsSOTrx(true);
            setIsDropShip(false);
            setSendEMail(false);
            //
            setIsApproved(false);
            setIsPrinted(false);
            setIsDelivered(false);
            //
            super.setProcessed(false);
            setProcessing(false);
            setPosted(false);

            setDatePromised(new Timestamp(System.currentTimeMillis()));
            setDateOrdered(new Timestamp(System.currentTimeMillis()));

            setFreightAmt(Env.ZERO);
            setChargeAmt(Env.ZERO);
        }
    } //	MDDOrder

    /**
     * ************************************************************************ Project Constructor
     *
     * @param project      Project to create Order from
     * @param IsSOTrx      sales order
     * @param DocSubTypeSO if SO DocType Target (default DocSubTypeSO_OnCredit)
     */
    public MDDOrder(MProject project, boolean IsSOTrx, String DocSubTypeSO) {
        this(0);
        setClientId(project.getClientId());
        setOrgId(project.getOrgId());
        setCampaignId(project.getCampaignId());
        setSalesRepresentativeId(project.getSalesRepresentativeId());
        //
        setProjectId(project.getProjectId());
        setDescription(project.getName());
        Timestamp ts = project.getDateContract();
        if (ts != null) setDateOrdered(ts);
        ts = project.getDateFinish();
        if (ts != null) setDatePromised(ts);
        //
        setBusinessPartnerId(project.getBusinessPartnerId());
        setBusinessPartnerLocationId(project.getBusinessPartnerLocationId());
        setUserId(project.getUserId());
        //
        setWarehouseId(project.getWarehouseId());
        //
        setIsSOTrx(IsSOTrx);
    } //	MDDOrder

    /**
     * Load Constructor
     */
    public MDDOrder(Row row) {
        super(row);
    } //	MDDOrder

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
     * Set Business Partner Defaults & Details. SOTrx should be set.
     *
     * @param bp business partner
     */
    public void setBPartner(I_C_BPartner bp) {
        if (bp == null) return;

        setBusinessPartnerId(bp.getBusinessPartnerId());
        //	Defaults Payment Term
        int ii;

        //	Default Price List
        //	Default Delivery/Via Rule
        String ss = bp.getDeliveryRule();
        if (ss != null) setDeliveryRule(ss);
        ss = bp.getDeliveryViaRule();
        if (ss != null) setDeliveryViaRule(ss);

        if (getSalesRepresentativeId() == 0) {
            ii = Env.getUserId();
            if (ii != 0) setSalesRepresentativeId(ii);
        }

        //	Set Locations
        List<I_C_BPartner_Location> locs = bp.getLocations();
        if (locs != null) {
            for (I_C_BPartner_Location loc : locs) {
                if (loc.getIsShipTo()) {
                    super.setBusinessPartnerLocationId(loc.getBusinessPartnerLocationId());
                }
            }
            //	set to first
            if (getBusinessPartnerLocationId() == 0 && locs.size() > 0) {
                super.setBusinessPartnerLocationId(locs.get(0).getBusinessPartnerLocationId());
            }
        }
        if (getBusinessPartnerLocationId() == 0) {
            log.log(Level.SEVERE, "MDDOrder.setBPartner - Has no Ship To Address: " + bp);
        }

        //	Set Contact
        List<User> contacts = bp.getContacts();
        if (contacts != null && contacts.size() == 1) setUserId(contacts.get(0).getUserId());
    } //	setBPartner

    /**
     * ************************************************************************ String Representation
     *
     * @return info
     */
    public String toString() {
        return "MDDOrder[" +
                getId() +
                "-" +
                getDocumentNo() +
                ",IsSOTrx=" +
                isSOTrx() +
                ",C_DocType_ID=" +
                getDocumentTypeId() +
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
     * ************************************************************************ Get Lines of Order
     *
     * @param whereClause where clause or null (starting with AND)
     * @param orderClause order clause
     * @return lines
     */
    public MDDOrderLine[] getLines(String whereClause, String orderClause) {
        StringBuilder whereClauseFinal =
                new StringBuilder(MDDOrderLine.COLUMNNAME_DD_Order_ID).append("=?");
        if (!Util.isEmpty(whereClause, true))
            whereClauseFinal.append(" AND (").append(whereClause).append(")");
        //
        List<MDDOrderLine> list =
                new Query(I_DD_OrderLine.Table_Name, whereClauseFinal.toString())
                        .setParameters(getDistributionOrderId())
                        .setOrderBy(orderClause)
                        .list();
        return list.toArray(new MDDOrderLine[0]);
    } //	getLines

    /**
     * Get Lines of Order
     *
     * @param requery requery
     * @param orderBy optional order by column
     * @return lines
     */
    public MDDOrderLine[] getLines(boolean requery, String orderBy) {
        if (m_lines != null && !requery) {
            return m_lines;
        }
        //
        String orderClause = "";
        if (orderBy != null && orderBy.length() > 0) orderClause += orderBy;
        else orderClause += "Line";
        m_lines = getLines(null, orderClause);
        return m_lines;
    } //	getLines

    /**
     * Get Lines of Order. (useb by web store)
     *
     * @return lines
     */
    public MDDOrderLine[] getLines() {
        return getLines(false, null);
    } //	getLines

    /**
     * Set DocAction
     *
     * @param DocAction doc action
     */
    public void setDocAction(String DocAction) {
        setDocAction(DocAction, false);
    } //	setDocAction

    /**
     * Set Processed. Propergate to Lines/Taxes
     *
     * @param processed processed
     */
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        if (getId() == 0) return;
        String set =
                "SET Processed='" + (processed ? "Y" : "N") + "' WHERE DD_Order_ID=" + getDistributionOrderId();
        int noLine = executeUpdate("UPDATE DD_OrderLine " + set);
        m_lines = null;
        if (log.isLoggable(Level.FINE)) log.fine("setProcessed - " + processed + " - Lines=" + noLine);
    } //	setProcessed

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord new
     * @return save
     */
    protected boolean beforeSave(boolean newRecord) {
        //	Client/Org Check
        if (getOrgId() == 0) {
            int context_AD_Org_ID = Env.getOrgId();
            if (context_AD_Org_ID != 0) {
                setOrgId(context_AD_Org_ID);
                log.warning("Changed Org to Context=" + context_AD_Org_ID);
            }
        }
        if (getClientId() == 0) {
            m_processMsg = "AD_Client_ID = 0";
            return false;
        }

        //	New Record Doc Type - make sure DocType set to 0
        if (newRecord && getDocumentTypeId() == 0) setDocumentTypeId(0);

        //	Default Warehouse
        if (getWarehouseId() == 0) {
            int ii = Env.getContextAsInt("#M_Warehouse_ID");
            if (ii != 0) setWarehouseId(ii);
            else {
                log.saveError("FillMandatory", MsgKt.getElementTranslation("M_Warehouse_ID"));
                return false;
            }
        }
        //	Reservations in Warehouse
        if (!newRecord && isValueChanged("M_Warehouse_ID")) {
            MDDOrderLine[] lines = getLines(false, null);
            for (MDDOrderLine line : lines) {
                if (!line.canChangeWarehouse()) return false;
            }
        }

        //	No Partner Info - set Template
        if (getBusinessPartnerId() == 0) setBPartner(new Environment<Module>().getModule().getBusinessPartnerService().getTemplate());
        if (getBusinessPartnerLocationId() == 0)
            setBPartner(new MBPartner(getBusinessPartnerId()));

        //	Default Sales Rep
        if (getSalesRepresentativeId() == 0) {
            int ii = Env.getContextAsInt("#AD_User_ID");
            if (ii != 0) setSalesRepresentativeId(ii);
        }

        return true;
    } //	beforeSave

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return true if can be saved
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success || newRecord) return success;

        //	Propagate Description changes
        if (isValueChanged("Description") || isValueChanged("POReference")) {
            String sql =
                    "UPDATE M_Movement i"
                            + " SET (Description,POReference)="
                            + "(SELECT Description,POReference "
                            + "FROM DD_Order o WHERE i.DD_Order_ID=o.DD_Order_ID) "
                            + "WHERE DocStatus NOT IN ('RE','CL') AND DD_Order_ID="
                            + getDistributionOrderId();
            int no = executeUpdate(sql);
            if (log.isLoggable(Level.FINE)) log.fine("Description -> #" + no);
        }

        //	Sync Lines
        afterSaveSync("AD_Org_ID");
        afterSaveSync("C_BPartner_ID");
        afterSaveSync("C_BPartner_Location_ID");
        afterSaveSync("DateOrdered");
        afterSaveSync("DatePromised");
        afterSaveSync("M_Shipper_ID");
        //
        return true;
    } //	afterSave

    private void afterSaveSync(String columnName) {
        if (isValueChanged(columnName)) {
            final String whereClause = I_DD_Order.COLUMNNAME_DD_Order_ID + "=?";
            List<MDDOrderLine> lines =
                    new Query(I_DD_OrderLine.Table_Name, whereClause)
                            .setParameters(getDistributionOrderId())
                            .list();

            for (MDDOrderLine line : lines) {
                line.setValueOfColumn(columnName, getValue(columnName));
                line.saveEx();
                if (log.isLoggable(Level.FINE))
                    log.fine(columnName + " Lines -> #" + getValue(columnName));
            }
        }
    } //	afterSaveSync

    /**
     * Set DocAction
     *
     * @param DocAction     doc oction
     * @param forceCreation force creation
     */
    public void setDocAction(String DocAction, boolean forceCreation) {
        super.setDocAction(DocAction);
    } //	setDocAction

    /**
     * Before Delete
     *
     * @return true of it can be deleted
     */
    protected boolean beforeDelete() {
        if (isProcessed()) return false;

        getLines();
        for (MDDOrderLine m_line : m_lines) {
            m_line.delete(true);
        }
        return true;
    } //	beforeDelete

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
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setDocAction(DOCACTION_Prepare);
        return true;
    } //	invalidateIt

    /**
     * ************************************************************************ Prepare Document
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
        if (!MPeriod.isOpen(getDateOrdered(), dt.getDocBaseType(), getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Lines
        MDDOrderLine[] lines = getLines(true, "M_Product_ID");
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        // Bug 1564431
        if (getDeliveryRule() != null && getDeliveryRule().equals(DELIVERYRULE_CompleteOrder)) {
            for (MDDOrderLine line : lines) {
                I_M_Product product = line.getProduct();
                if (product != null && product.isExcludeAutoDelivery()) {
                    m_processMsg = "@M_Product_ID@ " + product.getSearchKey() + " @IsExcludeAutoDelivery@";
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
        }

        //	Mandatory Product Attribute Set Instance
        String mandatoryType = "='Y'"; // 	IN ('Y','S')
        String sql =
                "SELECT COUNT(*) "
                        + "FROM DD_OrderLine ol"
                        + " INNER JOIN M_Product p ON (ol.M_Product_ID=p.M_Product_ID)"
                        + " INNER JOIN M_AttributeSet pas ON (p.M_AttributeSet_ID=pas.M_AttributeSet_ID) "
                        + "WHERE pas.MandatoryType"
                        + mandatoryType
                        + " AND ol.M_AttributeSetInstance_ID IS NULL"
                        + " AND ol.DD_Order_ID=?";
        int no = getSQLValue(sql, getDistributionOrderId());
        if (no != 0) {
            m_processMsg = "@LinesWithoutProductAttribute@ (" + no + ")";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        reserveStock(lines);

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

    /**
     * Reserve Inventory. Counterpart: MMovement.completeIt()
     *
     * @param lines distribution order lines (ordered by M_Product_ID for deadlock prevention)
     * @return true if (un) reserved
     */
    public void reserveStock(MDDOrderLine[] lines) {
        BigDecimal Volume = Env.ZERO;
        BigDecimal Weight = Env.ZERO;

        StringBuilder errors = new StringBuilder();
        //	Always check and (un) Reserve Inventory
        for (MDDOrderLine line : lines) {
            MLocator locator_from = MLocator.get(line.getLocatorId());
            MLocator locator_to = MLocator.get(line.getLocatorToId());
            BigDecimal reserved_ordered =
                    line.getQtyOrdered().subtract(line.getQtyReserved()).subtract(line.getQtyDelivered());
            if (reserved_ordered.signum() == 0) {
                I_M_Product product = line.getProduct();
                if (product != null) {
                    Volume = Volume.add(product.getVolume().multiply(line.getQtyOrdered()));
                    Weight = Weight.add(product.getWeight().multiply(line.getQtyOrdered()));
                }
                continue;
            }

            if (log.isLoggable(Level.FINE))
                log.fine(
                        "Line="
                                + line.getLine()
                                + " - Ordered="
                                + line.getQtyOrdered()
                                + ",Reserved="
                                + line.getQtyReserved()
                                + ",Delivered="
                                + line.getQtyDelivered());

            //	Check Product - Stocked and Item
            I_M_Product product = line.getProduct();
            if (product != null) {
                try {
                    if (product.isStocked()) {
                        //	Update Storage
                        if (!MStorageOnHand.add(
                                locator_to.getWarehouseId(),
                                locator_to.getLocatorId(),
                                line.getProductId(),
                                line.getAttributeSetInstanceId(),
                                Env.ZERO,
                                null
                        )) {
                            throw new AdempiereException("!MStorageOnHand1");
                        }

                        if (!MStorageOnHand.add(
                                locator_from.getWarehouseId(),
                                locator_from.getLocatorId(),
                                line.getProductId(),
                                line.getMAttributeSetInstanceToId(),
                                Env.ZERO,
                                null
                        )) {
                            throw new AdempiereException("!MStorageOnHand2");
                        }
                    } //	stockec
                    //	update line
                    line.setQtyReserved(line.getQtyReserved().add(reserved_ordered));
                    line.saveEx();
                    //
                    Volume = Volume.add(product.getVolume().multiply(line.getQtyOrdered()));
                    Weight = Weight.add(product.getWeight().multiply(line.getQtyOrdered()));
                } catch (NegativeInventoryDisallowedException e) {
                    log.severe(e.getMessage());
                    errors
                            .append(MsgKt.getElementTranslation("Line"))
                            .append(" ")
                            .append(line.getLine())
                            .append(": ");
                    errors.append(e.getMessage()).append("\n");
                }
            } //	product
        } //	reverse inventory

        if (errors.toString().length() > 0) throw new AdempiereException(errors.toString());

        setVolume(Volume);
        setWeight(Weight);
    } //	reserveStock

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
     * ************************************************************************ Complete Document
     *
     * @return new status (Complete, In Progress, Invalid, Waiting ..)
     */
    @NotNull
    public CompleteActionResult completeIt() {
        @SuppressWarnings("unused")
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());

        //	Just prepare
        if (DOCACTION_Prepare.equals(getDocAction())) {
            setProcessed(false);
            return new CompleteActionResult(DocAction.Companion.getSTATUS_InProgress());
        }

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
        getLines(true, null);
        if (log.isLoggable(Level.INFO)) log.info(toString());
        StringBuilder info = new StringBuilder();
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            if (info.length() > 0) info.append(" - ");
            info.append(valid);
            m_processMsg = info.toString();
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        setProcessed(true);
        m_processMsg = info.toString();
        //
        setDocAction(DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Void Document. Set Qtys to 0 - Sales: reverse all documents
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
        if (m_processMsg != null) return false;

        MDDOrderLine[] lines = getLines(true, "M_Product_ID");
        for (MDDOrderLine line : lines) {
            BigDecimal old = line.getQtyOrdered();
            if (old.signum() != 0) {
                line.addDescription(MsgKt.getMsg("Voided") + " (" + old + ")");
                line.saveEx();
            }
        }
        addDescription(MsgKt.getMsg("Voided"));
        //	Clear Reservations
        reserveStock(lines);
        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        if (m_processMsg != null) return false;

        setProcessed(true);
        setDocAction(DOCACTION_None);
        return true;
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

        //	Close Not delivered Qty - SO/PO
        MDDOrderLine[] lines = getLines(true, "M_Product_ID");
        for (MDDOrderLine line : lines) {
            BigDecimal old = line.getQtyOrdered();
            if (old.compareTo(line.getQtyDelivered()) != 0) {
                line.setQtyOrdered(line.getQtyDelivered());
                //	QtyEntered unchanged
                line.addDescription("Close (" + old + ")");
                line.saveEx();
            }
        }
        //	Clear Reservations
        reserveStock(lines);

        setProcessed(true);
        setDocAction(DOCACTION_None);
        // After Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE);
        return m_processMsg == null;
    } //	closeIt

    /**
     * Reverse Correction - same void
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

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        return voidIt();
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
     * Re-activate.
     *
     * @return true if success
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

        setDocAction(DOCACTION_Complete);
        setProcessed(false);
        return true;
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

        if (m_lines != null) sb.append(" (#").append(m_lines.length).append(")");
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
        return getSalesRepresentativeId();
    } //	getDoc_User_ID

    @NotNull
    public BigDecimal getApprovalAmt() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getCurrencyId() {
        // TODO Auto-generated method stub
        return 0;
    }

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
} //	MDDOrder
