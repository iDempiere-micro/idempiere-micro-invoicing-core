package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingSchema;
import org.compiere.model.I_M_Cost;
import org.compiere.model.I_M_CostDetail;
import org.compiere.model.I_M_CostElement;
import org.compiere.model.I_M_Product;
import org.compiere.orm.Query;
import org.compiere.production.MProductionLine;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.forUpdate;
import static software.hsharp.core.util.DBKt.getSQLValueString;

/**
 * Cost Detail Model
 *
 * @author Jorg Janke
 * @author Armen Rizal, Goodwill Consulting
 * <li>BF: 2431123 Return Trx changes weighted average cost
 * <li>BF: 1568752 Average invoice costing: landed costs incorrectly applied
 * @author Armen Rizal & Bayu Cahya
 * <li>BF [ 2129781 ] Cost Detail not created properly for multi acc schema
 * @author Teo Sarca
 * <li>BF [ 2847648 ] Manufacture & shipment cost errors
 * https://sourceforge.net/tracker/?func=detail&aid=2847648&group_id=176962&atid=934929
 * @author red1 FR: [ 2214883 ] Remove SQL code and Replace for Query
 * @version $Id: MCostDetail.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MCostDetail extends X_M_CostDetail {
    /**
     *
     */
    private static final long serialVersionUID = -448632684360931078L;

    private static final String INOUTLINE_DOCBASETYPE_SQL =
            "SELECT c.DocBaseType From M_InOut io "
                    + "INNER JOIN M_InOutLine iol ON io.M_InOut_ID=iol.M_InOut_ID "
                    + "INNER JOIN C_DocType c ON io.C_DocType_ID=c.C_DocType_ID "
                    + "WHERE iol.M_InOutLine_ID=?";
    /**
     * Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MCostDetail.class);

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param M_CostDetail_ID id
     */
    public MCostDetail(int M_CostDetail_ID) {
        super(M_CostDetail_ID);
        if (M_CostDetail_ID == 0) {
            setAttributeSetInstanceId(0);
            setProcessed(false);
            setAmt(Env.ZERO);
            setQty(Env.ZERO);
            setIsSOTrx(false);
            setDeltaAmt(Env.ZERO);
            setDeltaQty(Env.ZERO);
        }
    } //	MCostDetail

    /**
     * Load Constructor
     *
     */
    public MCostDetail(Row row) {
        super(row);
    } //	MCostDetail


    /**
     * New Constructor
     *
     * @param as                        accounting schema
     * @param AD_Org_ID                 org
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID asi
     * @param M_CostElement_ID          optional cost element for Freight
     * @param Amt                       amt
     * @param Qty                       qty
     * @param Description               optional description
     */
    public MCostDetail(
            AccountingSchema as,
            int AD_Org_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            int M_CostElement_ID,
            BigDecimal Amt,
            BigDecimal Qty,
            String Description) {
        this(0);
        setClientOrg(as.getClientId(), AD_Org_ID);
        setAccountingSchemaId(as.getAccountingSchemaId());
        setProductId(M_Product_ID);
        setAttributeSetInstanceId(M_AttributeSetInstance_ID);
        //
        setCostElementId(M_CostElement_ID);
        //
        setAmt(Amt);
        setQty(Qty);
        setDescription(Description);
    } //	MCostDetail

    /**
     * Create New Invoice Cost Detail for AP Invoices. Called from Doc_Invoice - for Invoice
     * Adjustments
     *
     * @param as                        accounting schema
     * @param AD_Org_ID                 org
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID asi
     * @param C_InvoiceLine_ID          invoice
     * @param M_CostElement_ID          optional cost element for Freight
     * @param Amt                       amt
     * @param Qty                       qty
     * @param Description               optional description
     * @return true if created
     */
    public static boolean createInvoice(
            AccountingSchema as,
            int AD_Org_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            int C_InvoiceLine_ID,
            int M_CostElement_ID,
            BigDecimal Amt,
            BigDecimal Qty,
            String Description) {
        I_M_CostDetail cd =
                get(
                        "C_InvoiceLine_ID=? AND Coalesce(M_CostElement_ID,0)="
                                + M_CostElement_ID
                                + " AND M_Product_ID="
                                + M_Product_ID,
                        C_InvoiceLine_ID,
                        M_AttributeSetInstance_ID,
                        as.getAccountingSchemaId());
        //
        if (cd == null) // 	createNew
        {
            cd =
                    new MCostDetail(
                            as,
                            AD_Org_ID,
                            M_Product_ID,
                            M_AttributeSetInstance_ID,
                            M_CostElement_ID,
                            Amt,
                            Qty,
                            Description);
            cd.setInvoiceLineId(C_InvoiceLine_ID);
        } else {
            if (cd.isProcessed()) {
                // MZ Goodwill
                // set deltaAmt=Amt, deltaQty=qty, and set Cost Detail for Amt and Qty
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            } else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            // end MZ
            else if (cd.isProcessed()) return true; // 	nothing to do
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (s_log.isLoggable(Level.CONFIG)) s_log.config("(" + ok + ") " + cd);
        return ok;
    } //	createInvoice

    /**
     * Create New Shipment Cost Detail for SO Shipments. Called from Doc_MInOut - for SO Shipments
     *
     * @param as                        accounting schema
     * @param AD_Org_ID                 org
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID asi
     * @param M_InOutLine_ID            shipment
     * @param M_CostElement_ID          optional cost element for Freight
     * @param Amt                       amt
     * @param Qty                       qty
     * @param Description               optional description
     * @param IsSOTrx                   sales order
     * @return true if no error
     */
    public static boolean createShipment(
            AccountingSchema as,
            int AD_Org_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            int M_InOutLine_ID,
            int M_CostElement_ID,
            BigDecimal Amt,
            BigDecimal Qty,
            String Description,
            boolean IsSOTrx) {
        I_M_CostDetail cd =
                get(
                        "M_InOutLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID,
                        M_InOutLine_ID,
                        M_AttributeSetInstance_ID,
                        as.getAccountingSchemaId());
        //
        if (cd == null) // 	createNew
        {
            cd =
                    new MCostDetail(
                            as,
                            AD_Org_ID,
                            M_Product_ID,
                            M_AttributeSetInstance_ID,
                            M_CostElement_ID,
                            Amt,
                            Qty,
                            Description);
            cd.setInOutLineId(M_InOutLine_ID);
            cd.setIsSOTrx(IsSOTrx);
        } else {
            if (cd.isProcessed()) {
                // MZ Goodwill
                // set deltaAmt=Amt, deltaQty=qty, and set Cost Detail for Amt and Qty
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            } else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            // end MZ
            else if (cd.isProcessed()) return true; // 	nothing to do
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (s_log.isLoggable(Level.CONFIG)) s_log.config("(" + ok + ") " + cd);
        return ok;
    } //	createShipment

    /**
     * Create New Order Cost Detail for Physical Inventory. Called from Doc_Inventory
     *
     * @param as                        accounting schema
     * @param AD_Org_ID                 org
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID asi
     * @param M_InventoryLine_ID        order
     * @param M_CostElement_ID          optional cost element
     * @param Amt                       amt total amount
     * @param Qty                       qty
     * @param Description               optional description
     * @param trxName                   transaction
     * @return true if no error
     */
    public static boolean createInventory(
            AccountingSchema as,
            int AD_Org_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            int M_InventoryLine_ID,
            int M_CostElement_ID,
            BigDecimal Amt,
            BigDecimal Qty,
            String Description,
            String trxName) {
        I_M_CostDetail cd =
                get(

                        "M_InventoryLine_ID=? AND Coalesce(M_CostElement_ID,0)=" + M_CostElement_ID,
                        M_InventoryLine_ID,
                        M_AttributeSetInstance_ID,
                        as.getAccountingSchemaId());
        //
        if (cd == null) // 	createNew
        {
            cd =
                    new MCostDetail(
                            as,
                            AD_Org_ID,
                            M_Product_ID,
                            M_AttributeSetInstance_ID,
                            M_CostElement_ID,
                            Amt,
                            Qty,
                            Description);
            cd.setInventoryLineId(M_InventoryLine_ID);
        } else {
            if (cd.isProcessed()) {
                // MZ Goodwill
                // set deltaAmt=Amt, deltaQty=qty, and set Cost Detail for Amt and Qty
                cd.setDeltaAmt(Amt.subtract(cd.getAmt()));
                cd.setDeltaQty(Qty.subtract(cd.getQty()));
            } else {
                cd.setDeltaAmt(BigDecimal.ZERO);
                cd.setDeltaQty(BigDecimal.ZERO);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            if (cd.isDelta()) {
                cd.setProcessed(false);
                cd.setAmt(Amt);
                cd.setQty(Qty);
            }
            // end MZ
            else if (cd.isProcessed()) return true; // 	nothing to do
        }
        boolean ok = cd.save();
        if (ok && !cd.isProcessed()) {
            ok = cd.process();
        }
        if (s_log.isLoggable(Level.CONFIG)) s_log.config("(" + ok + ") " + cd);
        return ok;
    } //	createInventory

    /**
     * ************************************************************************ Get Cost Detail
     *
     * @param whereClause               where clause
     * @param ID                        1st parameter
     * @param M_AttributeSetInstance_ID ASI
     * @return cost detail
     */
    public static I_M_CostDetail get(

            String whereClause,
            int ID,
            int M_AttributeSetInstance_ID,
            int C_AcctSchema_ID) {
        String localWhereClause = whereClause +
                " AND M_AttributeSetInstance_ID=?" +
                " AND C_AcctSchema_ID=?";
        return new Query<I_M_CostDetail>(I_M_CostDetail.Table_Name, localWhereClause)
                .setParameters(ID, M_AttributeSetInstance_ID, C_AcctSchema_ID)
                .first();
    } //	get

    /**
     * Process Cost Details for product
     *
     * @param product product
     * @return true if no error
     */
    public static boolean processProduct(I_M_Product product) {
        final String whereClause =
                I_M_CostDetail.COLUMNNAME_M_Product_ID
                        + "=?"
                        + " AND "
                        + I_M_CostDetail.COLUMNNAME_Processed
                        + "=?";
        int counterOK = 0;
        int counterError = 0;
        List<I_M_CostDetail> list =
                new Query<I_M_CostDetail>(I_M_CostDetail.Table_Name, whereClause)
                        .setParameters(product.getProductId(), false)
                        .setOrderBy(
                                "C_AcctSchema_ID, M_CostElement_ID, AD_Org_ID, M_AttributeSetInstance_ID, Created")
                        .list();
        for (I_M_CostDetail cd : list) {
            if (cd.process()) // 	saves
                counterOK++;
            else counterError++;
        }
        if (s_log.isLoggable(Level.CONFIG))
            s_log.config("OK=" + counterOK + ", Errors=" + counterError);
        return counterError == 0;
    } //	processProduct

    /**
     * Set Amt
     *
     * @param Amt amt
     */
    public void setAmt(BigDecimal Amt) {
        if (isProcessed()) throw new IllegalStateException("Cannot change Amt - processed");
        if (Amt == null) super.setAmt(Env.ZERO);
        else super.setAmt(Amt);
    } //	setAmt

    /**
     * Set Qty
     *
     * @param Qty qty
     */
    public void setQty(BigDecimal Qty) {
        if (isProcessed()) throw new IllegalStateException("Cannot change Qty - processed");
        if (Qty == null) super.setQty(Env.ZERO);
        else super.setQty(Qty);
    } //	setQty

    /**
     * Is Shipment
     *
     * @return true if sales order shipment
     */
    public boolean isShipment() {
        return isSOTrx() && getInOutLineId() != 0;
    } //	isShipment

    /**
     * @return true if return to vendor
     */
    public boolean isVendorRMA() {
        if (!isSOTrx() && getInOutLineId() > 0) {
            String docBaseType =
                    getSQLValueString(INOUTLINE_DOCBASETYPE_SQL, getInOutLineId());
            return Doc.DOCTYPE_MatShipment.equals(docBaseType);
        }
        return false;
    }

    /**
     * Is this a Delta Record (previously processed)?
     *
     * @return true if delta is not null
     */
    public boolean isDelta() {
        return !(getDeltaAmt().signum() == 0 && getDeltaQty().signum() == 0);
    } //	isDelta

    /**
     * Before Delete
     *
     * @return false if processed
     */
    protected boolean beforeDelete() {
        return !isProcessed();
    } //	beforeDelete

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MCostDetail[");
        sb.append(getId());
        if (getOrderLineId() != 0) sb.append(",C_OrderLine_ID=").append(getOrderLineId());
        if (getInOutLineId() != 0) sb.append(",M_InOutLine_ID=").append(getInOutLineId());
        if (getInvoiceLineId() != 0) sb.append(",C_InvoiceLine_ID=").append(getInvoiceLineId());
        if (getProjectIssueId() != 0)
            sb.append(",C_ProjectIssue_ID=").append(getProjectIssueId());
        if (getMovementLineId() != 0)
            sb.append(",M_MovementLine_ID=").append(getMovementLineId());
        if (getInventoryLineId() != 0)
            sb.append(",M_InventoryLine_ID=").append(getInventoryLineId());
        if (getProductionLineId() != 0)
            sb.append(",M_ProductionLine_ID=").append(getProductionLineId());
        sb.append(",Amt=").append(getAmt()).append(",Qty=").append(getQty());
        if (isDelta())
            sb.append(",DeltaAmt=").append(getDeltaAmt()).append(",DeltaQty=").append(getDeltaQty());
        sb.append("]");
        return sb.toString();
    } //	toString

    /**
     * ************************************************************************ Process Cost Detail
     * Record. The record is saved if processed.
     *
     * @return true if processed
     */
    public synchronized boolean process() {
        if (isProcessed()) {
            log.info("Already processed");
            return true;
        }
        boolean ok = false;

        //	get costing level for product
        MAcctSchema as = MAcctSchema.get(getAccountingSchemaId());
        MProduct product = new MProduct(getProductId());
        String CostingLevel = product.getCostingLevel(as);
        //	Org Element
        int Org_ID = getOrgId();
        int M_ASI_ID = getAttributeSetInstanceId();
        if (MAcctSchema.COSTINGLEVEL_Client.equals(CostingLevel)) {
            Org_ID = 0;
            M_ASI_ID = 0;
        } else if (MAcctSchema.COSTINGLEVEL_Organization.equals(CostingLevel)) M_ASI_ID = 0;
        else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(CostingLevel)) Org_ID = 0;

        //	Create Material Cost elements
        if (getCostElementId() == 0) {
            I_M_CostElement[] ces = MCostElement.getCostingMethods(this);
            for (I_M_CostElement ce : ces) {
                if (ce.isAverageInvoice() || ce.isAveragePO() || ce.isLifo() || ce.isFifo()) {
                    if (!product.isStocked()) continue;
                }
                ok = process(as, product, ce, Org_ID, M_ASI_ID);
                if (!ok) break;
            }
        } //	Material Cost elements
        else {
            MCostElement ce = MCostElement.get(getCostElementId());
            if (ce.getCostingMethod() == null) {
                I_M_CostElement[] ces = MCostElement.getCostingMethods(this);
                for (I_M_CostElement costingElement : ces) {
                    if (costingElement.isAverageInvoice()
                            || costingElement.isAveragePO()
                            || costingElement.isLifo()
                            || costingElement.isFifo()) {
                        if (!product.isStocked()) continue;
                    }
                    ok = process(as, product, costingElement, Org_ID, M_ASI_ID);
                    if (!ok) break;
                }
            } else {
                if (ce.isAverageInvoice() || ce.isAveragePO() || ce.isLifo() || ce.isFifo()) {
                    if (product.isStocked()) ok = process(as, product, ce, Org_ID, M_ASI_ID);
                } else {
                    ok = process(as, product, ce, Org_ID, M_ASI_ID);
                }
            }
        }

        //	Save it
        if (ok) {
            setDeltaAmt(null);
            setDeltaQty(null);
            setProcessed(true);
            ok = save();
        }
        if (log.isLoggable(Level.INFO)) log.info(ok + " - " + toString());
        return ok;
    } //	process

    /**
     * Process cost detail for cost record
     *
     * @param as       accounting schema
     * @param product  product
     * @param ce       cost element
     * @param Org_ID   org - corrected for costing level
     * @param M_ASI_ID - asi corrected for costing level
     * @return true if cost ok
     */
    private boolean process(
            MAcctSchema as, MProduct product, I_M_CostElement ce, int Org_ID, int M_ASI_ID) {
        // handle compatibility issue between average invoice and average po
        String costingMethod = product.getCostingMethod(as);
        if (X_M_Cost.COSTINGMETHOD_AverageInvoice.equals(costingMethod)) {
            if (ce.isAveragePO()) return true;
        } else if (X_M_Cost.COSTINGMETHOD_AveragePO.equals(costingMethod)) {
            if (ce.isAverageInvoice()) return true;
        }

        I_M_Cost cost = MCost.get(product, M_ASI_ID, as, Org_ID, ce.getCostElementId());

        forUpdate(cost);

        // save history for m_cost
        X_M_CostHistory history = new X_M_CostHistory(0);
        history.setAttributeSetInstanceId(cost.getAttributeSetInstanceId());
        history.setCostDetailId(this.getCostDetailId());
        history.setCostElementId(ce.getCostElementId());
        history.setCostTypeId(cost.getCostTypeId());
        history.setClientOrg(cost.getClientId(), cost.getOrgId());
        history.setOldQty(cost.getCurrentQty());
        history.setOldCostPrice(cost.getCurrentCostPrice());
        history.setOldCAmt(cost.getCumulatedAmt());
        history.setOldCQty(cost.getCumulatedQty());

        // MZ Goodwill
        // used deltaQty and deltaAmt if exist
        BigDecimal qty = Env.ZERO;
        BigDecimal amt = Env.ZERO;
        if (isDelta()) {
            qty = getDeltaQty();
            amt = getDeltaAmt();
        } else {
            qty = getQty();
            amt = getAmt();
        }
        // end MZ

        // determine whether this is cost only adjustment entry
        boolean costAdjustment = false;
        if (this.getCostElementId() > 0 && this.getCostElementId() != ce.getCostElementId()) {
            MCostElement thisCostElement = MCostElement.get(getCostElementId());
            if (thisCostElement.getCostingMethod() == null && ce.getCostingMethod() != null) {
                qty = BigDecimal.ZERO;
                costAdjustment = true;
            }
        }

        int precision = as.getCostingPrecision();
        BigDecimal price = amt;
        if (qty.signum() != 0) price = amt.divide(qty, precision, BigDecimal.ROUND_HALF_UP);

        /*
         * All Costing Methods if (ce.isAverageInvoice()) else if (ce.isAveragePO()) else if
         * (ce.isFifo()) else if (ce.isLifo()) else if (ce.isLastInvoice()) else if (ce.isLastPOPrice())
         * else if (ce.isStandardCosting()) else if (ce.isUserDefined()) else if (!ce.isCostingMethod())
         */

        //	*** Purchase Order Detail Record ***
        if (getOrderLineId() != 0) {
            boolean isReturnTrx = qty.signum() < 0;

            if (ce.isAveragePO()) {
                cost.setWeightedAverage(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("PO - AveragePO - " + cost);
            } else if (ce.isLastPOPrice() && !costAdjustment) {
                if (!isReturnTrx) {
                    if (qty.signum() != 0) cost.setCurrentCostPrice(price);
                    else {
                        BigDecimal cCosts = cost.getCurrentCostPrice().add(amt);
                        cost.setCurrentCostPrice(cCosts);
                    }
                }
                cost.add(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("PO - LastPO - " + cost);
            } else if (ce.isStandardCosting() && !costAdjustment) {
                // Update cost record only if it is zero
                if (cost.getCurrentCostPrice().signum() == 0
                        && cost.getCurrentCostPriceLL().signum() == 0) {
                    cost.setCurrentCostPrice(price);
                    if (cost.getCurrentCostPrice().signum() == 0) {
                        cost.setCurrentCostPrice(
                                MCost.getSeedCosts(
                                        product, M_ASI_ID, as, Org_ID, ce.getCostingMethod(), getOrderLineId()));
                    }
                    if (log.isLoggable(Level.FINEST))
                        log.finest(
                                "PO - Standard - CurrentCostPrice(seed)="
                                        + cost.getCurrentCostPrice()
                                        + ", price="
                                        + price);
                }
                cost.add(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("PO - Standard - " + cost);
            } else if (ce.isUserDefined()) {
                //	Interface
                if (log.isLoggable(Level.FINER)) log.finer("PO - UserDef - " + cost);
            } else if (!ce.isCostingMethod()) {
                if (log.isLoggable(Level.FINER)) log.finer("PO - " + ce + " - " + cost);
            }
            //	else
            //		log.warning("PO - " + ce + " - " + cost);
        }

        //	*** AP Invoice Detail Record ***
        else if (getInvoiceLineId() != 0) {
            boolean isReturnTrx = qty.signum() < 0;

            if (ce.isAverageInvoice()) {
                cost.setWeightedAverage(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("Inv - AverageInv - " + cost);
            } else if (ce.isAveragePO() && costAdjustment) {
                cost.setWeightedAverage(amt, qty);
            } else if (ce.isFifo() || ce.isLifo()) {
                //	Real ASI - costing level Org
                MCostQueue cq =
                        MCostQueue.get(
                                product,
                                getAttributeSetInstanceId(),
                                as,
                                Org_ID,
                                ce.getCostElementId()
                        );
                cq.setCosts(amt, qty, precision);
                cq.saveEx();
                //	Get Costs - costing level Org/ASI
                MCostQueue[] cQueue = MCostQueue.getQueue(product, M_ASI_ID, as, Org_ID, ce);
                if (cQueue != null && cQueue.length > 0)
                    cost.setCurrentCostPrice(cQueue[0].getCurrentCostPrice());
                cost.add(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("Inv - FiFo/LiFo - " + cost);
            } else if (ce.isLastInvoice() && !costAdjustment) {
                if (!isReturnTrx) {
                    if (qty.signum() != 0) cost.setCurrentCostPrice(price);
                    else {
                        BigDecimal cCosts = cost.getCurrentCostPrice().add(amt);
                        cost.setCurrentCostPrice(cCosts);
                    }
                }
                cost.add(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("Inv - LastInv - " + cost);
            } else if (ce.isStandardCosting() && !costAdjustment) {
                // Update cost record only if it is zero
                if (cost.getCurrentCostPrice().signum() == 0
                        && cost.getCurrentCostPriceLL().signum() == 0) {
                    cost.setCurrentCostPrice(price);
                    //	seed initial price
                    if (cost.getCurrentCostPrice().signum() == 0) {
                        cost.setCurrentCostPrice(
                                MCost.getSeedCosts(
                                        product, M_ASI_ID, as, Org_ID, ce.getCostingMethod(), getOrderLineId()));
                        if (log.isLoggable(Level.FINEST))
                            log.finest(
                                    "Inv - Standard - CurrentCostPrice(seed)="
                                            + cost.getCurrentCostPrice()
                                            + ", price="
                                            + price);
                    }
                    cost.add(amt, qty);
                }
                if (log.isLoggable(Level.FINER)) log.finer("Inv - Standard - " + cost);
            } else if (ce.isUserDefined()) {
                //	Interface
                cost.add(amt, qty);
                if (log.isLoggable(Level.FINER)) log.finer("Inv - UserDef - " + cost);
            }
            //	else
            //		log.warning("Inv - " + ce + " - " + cost);
        } else if (getInOutLineId() != 0 && costAdjustment) {
            if (ce.isAverageInvoice()) {
                cost.setWeightedAverage(amt, qty);
            }
        }
        //	*** Qty Adjustment Detail Record ***
        else if (getInOutLineId() != 0 // 	AR Shipment Detail Record
                || getMovementLineId() != 0
                || getInventoryLineId() != 0
                || getProductionLineId() != 0
                || getProjectIssueId() != 0
                || getManufacturingCostCollectorId() != 0) {
            boolean addition = qty.signum() > 0;
            boolean adjustment = getInventoryLineId() > 0 && qty.signum() == 0 && amt.signum() != 0;
            boolean isVendorRMA = isVendorRMA();
            //
            if (ce.isAverageInvoice()) {
                if (!isVendorRMA) {
                    if (adjustment) {
                        costingMethod = getInventoryLine().getInventory().getCostingMethod();
                        if (MCostElement.COSTINGMETHOD_AverageInvoice.equals(costingMethod)) {
                            if (cost.getCurrentQty().signum() == 0 && qty.signum() == 0) {
                                // IDEMPIERE-2057 - this is a cost adjustment when there is no qty - setting the
                                // initial cost
                                cost.setWeightedAverageInitial(amt);
                            } else {
                                cost.setWeightedAverage(amt.multiply(cost.getCurrentQty()), qty);
                            }
                        }
                    } else if (addition) {
                        cost.setWeightedAverage(amt, qty);
                        // shouldn't accumulate reversal of customer shipment qty and amt
                        if (isShipment()) {
                            cost.setCumulatedQty(history.getOldCQty());
                            cost.setCumulatedAmt(history.getOldCAmt());
                        }
                    } else cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - AverageInv - " + cost);
                }
            } else if (ce.isAveragePO()) {
                if (adjustment) {
                    costingMethod = getInventoryLine().getInventory().getCostingMethod();
                    if (MCostElement.COSTINGMETHOD_AveragePO.equals(costingMethod)) {
                        if (cost.getCurrentQty().signum() == 0 && qty.signum() == 0) {
                            // IDEMPIERE-2057 - this is a cost adjustment when there is no qty - setting the
                            // initial cost
                            cost.setWeightedAverageInitial(amt);
                        } else {
                            cost.setWeightedAverage(amt.multiply(cost.getCurrentQty()), qty);
                        }
                    }
                } else if (addition) {
                    cost.setWeightedAverage(amt, qty);
                    // shouldn't accumulate reversal of customer shipment qty and amt
                    if (isShipment() && !isVendorRMA()) {
                        cost.setCumulatedQty(history.getOldCQty());
                        cost.setCumulatedAmt(history.getOldCAmt());
                    }
                } else {
                    if (isVendorRMA) {
                        cost.setWeightedAverage(amt, qty);
                    } else {
                        cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    }
                }
                if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - AveragePO - " + cost);
            } else if (ce.isFifo() || ce.isLifo()) {
                if (!isVendorRMA && !adjustment) {
                    if (addition) {
                        //	Real ASI - costing level Org
                        MCostQueue cq =
                                MCostQueue.get(
                                        product,
                                        getAttributeSetInstanceId(),
                                        as,
                                        Org_ID,
                                        ce.getCostElementId()
                                );
                        cq.setCosts(amt, qty, precision);
                        cq.saveEx();
                    } else {
                        //	Adjust Queue - costing level Org/ASI
                        MCostQueue.adjustQty(product, M_ASI_ID, as, Org_ID, ce, qty.negate(), null);
                    }
                    //	Get Costs - costing level Org/ASI
                    MCostQueue[] cQueue =
                            MCostQueue.getQueue(product, M_ASI_ID, as, Org_ID, ce);
                    if (cQueue != null && cQueue.length > 0)
                        cost.setCurrentCostPrice(cQueue[0].getCurrentCostPrice());
                    cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - FiFo/Lifo - " + cost);
                }
            } else if (ce.isLastInvoice() && !isVendorRMA && !adjustment) {
                cost.setCurrentQty(cost.getCurrentQty().add(qty));
                if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - LastInv - " + cost);
            } else if (ce.isLastPOPrice() && !isVendorRMA && !adjustment) {
                cost.setCurrentQty(cost.getCurrentQty().add(qty));
                if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - LastPO - " + cost);
            } else if (ce.isStandardCosting() && !isVendorRMA) {
                if (adjustment) {
                    costingMethod = getInventoryLine().getInventory().getCostingMethod();
                    if (MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod)) {
                        cost.add(amt.multiply(cost.getCurrentQty()), qty);
                        cost.setCurrentCostPrice(cost.getCurrentCostPrice().add(amt));
                    }
                } else if (addition) {
                    MProductionLine productionLine =
                            getProductionLineId() > 0
                                    ? new MProductionLine(getProductionLineId())
                                    : null;
                    if (productionLine != null && productionLine.getProductionReversalId() > 0)
                        cost.setCurrentQty(cost.getCurrentQty().add(qty));
                    else cost.add(amt, qty);

                    //	Initial
                    if (cost.getCurrentCostPrice().signum() == 0
                            && cost.getCurrentCostPriceLL().signum() == 0
                            && cost.isNew()) {
                        cost.setCurrentCostPrice(price);
                        if (log.isLoggable(Level.FINEST))
                            log.finest("QtyAdjust - Standard - CurrentCostPrice=" + price);
                    }
                } else {
                    cost.setCurrentQty(cost.getCurrentQty().add(qty));
                }
                if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - Standard - " + cost);
            } else if (ce.isUserDefined() && !isVendorRMA && !adjustment) {
                //	Interface
                if (addition) cost.add(amt, qty);
                else cost.setCurrentQty(cost.getCurrentQty().add(qty));
                if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - UserDef - " + cost);
            } else if (!ce.isCostingMethod()) {
                // Should not happen
                if (log.isLoggable(Level.FINER)) log.finer("QtyAdjust - ?none? - " + cost);
            } else if (ce.isStandardCosting() && isVendorRMA) {
                cost.add(amt, qty);

            } else log.warning("QtyAdjust - " + ce + " - " + cost);

        } else if (getMatchInvoiceId() > 0) {
            if (ce.isAveragePO()) {
                cost.setWeightedAverage(amt, qty);
            }
        } else //	unknown or no id
        {
            log.warning("Unknown Type: " + toString());
            return false;
        }

        // Should only update cost detail with value from as costing method
        if (as.getCostingMethod().equals(ce.getCostingMethod())) {
            setCurrentCostPrice(cost.getCurrentCostPrice());
            setCurrentQty(cost.getCurrentQty());
            setCumulatedAmt(cost.getCumulatedAmt());
            setCumulatedQty(cost.getCumulatedQty());
        }

        // update history
        history.setNewQty(cost.getCurrentQty());
        history.setNewCostPrice(cost.getCurrentCostPrice());
        history.setNewCAmt(cost.getCumulatedAmt());
        history.setNewCQty(cost.getCumulatedQty());
        // save history if there are movement of qty or costprice
        if (history.getNewQty().compareTo(history.getOldQty()) != 0
                || history.getNewCostPrice().compareTo(history.getOldCostPrice()) != 0) {
            if (!history.save()) return false;
        }

        return cost.save();
    } //	process
} //	MCostDetail
