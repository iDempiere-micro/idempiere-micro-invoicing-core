package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.conversionrate.MConversionRate;
import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInOutLine;
import org.compiere.invoicing.MInOutLineMA;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_OrderLandedCostAllocation;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.I_M_InOutLineMA;
import org.compiere.model.I_M_RMALine;
import org.compiere.tax.MTax;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.getSQLValueEx;

/**
 * Post Shipment/Receipt Documents.
 *
 * <pre>
 *  Table:              M_InOut (319)
 *  Document Types:     MMS, MMR
 *  </pre>
 *
 * @author Jorg Janke
 * @author Armen Rizal, Goodwill Consulting
 * <li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 * <li>BF [ 2858043 ] Correct Included Tax in Average Costing
 * @version $Id: Doc_InOut.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 */
public class Doc_InOut extends Doc {
    private int m_Reversal_ID = 0;
    @SuppressWarnings("unused")
    private String m_DocStatus = "";

    /**
     * Constructor
     *
     * @param as accounting schema
     * @param rs record
     */
    public Doc_InOut(MAcctSchema as, Row rs) {
        super(as, MInOut.class, rs, null);
    } //  DocInOut

    @Override
    protected IPODoc createNewInstance(Row rs) {
        return new MInOut(rs);
    }

    /**
     * Load Document Details
     *
     * @return error message or null
     */
    protected String loadDocumentDetails() {
        setCurrencyId(NO_CURRENCY);
        MInOut inout = (MInOut) getPO();
        setDateDoc(inout.getMovementDate());
        m_Reversal_ID = inout.getReversalId(); // store original (voided/reversed) document
        m_DocStatus = inout.getDocStatus();
        //	Contained Objects
        p_lines = loadLines(inout);
        if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length);
        return null;
    } //  loadDocumentDetails

    /**
     * Load InOut Line
     *
     * @param inout shipment/receipt
     * @return DocLine Array
     */
    private DocLine[] loadLines(MInOut inout) {
        ArrayList<DocLine> list = new ArrayList<>();
        I_M_InOutLine[] lines = inout.getLines(false);
        for (I_M_InOutLine line : lines) {
            if (line.isDescription()
                    || line.getProductId() == 0
                    || line.getMovementQty().signum() == 0) {
                if (log.isLoggable(Level.FINER)) log.finer("Ignored: " + line);
                continue;
            }

            DocLine docLine = new DocLine(line, this);
            BigDecimal Qty = line.getMovementQty();
            docLine.setReversalLineId(line.getReversalLineId());
            docLine.setQty(
                    Qty, getDocumentType().equals(DOCTYPE_MatShipment)); //  sets Trx and Storage Qty

            // Define if Outside Processing
            String sql =
                    "SELECT PP_Cost_Collector_ID  FROM C_OrderLine WHERE C_OrderLine_ID=? AND PP_Cost_Collector_ID IS NOT NULL";
            int PP_Cost_Collector_ID =
                    getSQLValueEx(sql, line.getOrderLineId());
            docLine.setCostCollectorId(PP_Cost_Collector_ID);
            //
            if (log.isLoggable(Level.FINE)) log.fine(docLine.toString());
            list.add(docLine);
        }

        //	Return Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);
        return dls;
    } //	loadLines

    /**
     * Get Balance
     *
     * @return Zero (always balanced)
     */
    public BigDecimal getBalance() {
        return Env.ZERO;
    } //  getBalance

    /**
     * Create Facts (the accounting logic) for MMS, MMR.
     *
     * <pre>
     *  Shipment
     *      CoGS (RevOrg)   DR
     *      Inventory               CR
     *  Shipment of Project Issue
     *      CoGS            DR
     *      Project                 CR
     *  Receipt
     *      Inventory       DR
     *      NotInvoicedReceipt      CR
     *  </pre>
     *
     * @param as accounting schema
     * @return Fact
     */
    public ArrayList<IFact> createFacts(AccountingSchema as) {
        //
        ArrayList<IFact> facts = new ArrayList<>();
        //  create Fact Header
        Fact fact = new Fact(this, as, Fact.POST_Actual);
        setCurrencyId(as.getCurrencyId());

        //  Line pointers
        FactLine dr;
        FactLine cr;

        //  *** Sales - Shipment
        if (getDocumentType().equals(DOCTYPE_MatShipment) && isSOTrx()) {
            for (DocLine line : p_lines) {
                MProduct product = line.getProduct();
                BigDecimal costs = null;
                if (!isReversal(line)) {
                    if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as))) {
                        if (line.getAttributeSetInstanceId() == 0) {
                            MInOutLine ioLine = (MInOutLine) line.getPO();
                            I_M_InOutLineMA[] mas = MInOutLineMA.get(ioLine.getId());
                            if (mas != null && mas.length > 0) {
                                costs = BigDecimal.ZERO;
                                for (I_M_InOutLineMA ma : mas) {
                                    BigDecimal QtyMA = ma.getMovementQty();
                                    ProductCost pc = line.getProductCost();
                                    pc.setQty(QtyMA);
                                    pc.setM_AttributeSetInstanceId(ma.getAttributeSetInstanceId());
                                    BigDecimal maCosts =
                                            line.getProductCosts(as, line.getOrgId(), true, "M_InOutLine_ID=?");

                                    costs = costs.add(maCosts);
                                }
                            }
                        } else {
                            costs = line.getProductCosts(as, line.getOrgId(), true, "M_InOutLine_ID=?");
                        }
                    } else {
                        // MZ Goodwill
                        // if Shipment CostDetail exist then get Cost from Cost Detail
                        costs = line.getProductCosts(as, line.getOrgId(), true, "M_InOutLine_ID=?");
                    }

                    // end MZ
                    if (costs == null || costs.signum() == 0) // 	zero costs OK
                    {
                        if (product.isStocked()) {
                            // ok if we have purchased zero cost item from vendor before
                            int count =
                                    getSQLValue(
                                            "SELECT Count(*) FROM M_CostDetail WHERE M_Product_ID=? AND Processed='Y' AND Amt=0.00 AND Qty > 0 AND (C_OrderLine_ID > 0 OR C_InvoiceLine_ID > 0)",
                                            product.getProductId());
                            if (count > 0) {
                                costs = BigDecimal.ZERO;
                            } else {
                                p_Error = "No Costs for " + line.getProduct().getName();
                                log.log(Level.WARNING, p_Error);
                                return null;
                            }
                        } else //	ignore service
                            continue;
                    }
                } else {
                    // temp to avoid NPE
                    costs = BigDecimal.ZERO;
                }

                //  CoGS            DR
                dr =
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Cogs, as),
                                as.getCurrencyId(),
                                costs,
                                null);
                if (dr == null) {
                    p_Error = "FactLine DR not created: " + line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                dr.setLocatorId(line.getLocatorId());
                dr.setLocationFromLocator(line.getLocatorId(), true); //  from Loc
                dr.setLocationFromBPartner(getBusinessPartnerLocationId(), false); //  to Loc
                dr.setOrgId(line.getOrderOrgId()); // 	Revenue X-Org
                dr.setQty(line.getQty().negate());

                if (isReversal(line)) {
                    //	Set AmtAcctDr from Original Shipment/Receipt
                    if (!dr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, line.getReversalLineId(), Env.ONE)) {
                        if (!product.isStocked()) { // 	ignore service
                            fact.remove(dr);
                            continue;
                        }
                        p_Error = "Original Shipment/Receipt not posted yet";
                        return null;
                    }
                }

                //  Inventory               CR
                cr =
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
                                as.getCurrencyId(),
                                null,
                                costs);
                if (cr == null) {
                    p_Error = "FactLine CR not created: " + line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                cr.setLocatorId(line.getLocatorId());
                cr.setLocationFromLocator(line.getLocatorId(), true); // from Loc
                cr.setLocationFromBPartner(getBusinessPartnerLocationId(), false); // to Loc

                if (isReversal(line)) {
                    //	Set AmtAcctCr from Original Shipment/Receipt
                    if (!cr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, line.getReversalLineId(), Env.ONE)) {
                        p_Error = "Original Shipment/Receipt not posted yet";
                        return null;
                    }
                    costs = cr.getAcctBalance(); // get original cost
                }
                if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as))) {
                    if (line.getAttributeSetInstanceId() == 0) {
                        MInOutLine ioLine = (MInOutLine) line.getPO();
                        I_M_InOutLineMA[] mas = MInOutLineMA.get(ioLine.getId());
                        if (mas != null && mas.length > 0) {
                            for (I_M_InOutLineMA ma : mas) {
                                if (!MCostDetail.createShipment(
                                        as,
                                        line.getOrgId(),
                                        line.getProductId(),
                                        ma.getAttributeSetInstanceId(),
                                        line.getId(),
                                        0,
                                        costs,
                                        ma.getMovementQty().negate(),
                                        line.getDescription(),
                                        true
                                )) {
                                    p_Error = "Failed to create cost detail record";
                                    return null;
                                }
                            }
                        }
                    } else {
                        //
                        if (line.getProductId() != 0) {
                            if (!MCostDetail.createShipment(
                                    as,
                                    line.getOrgId(),
                                    line.getProductId(),
                                    line.getAttributeSetInstanceId(),
                                    line.getId(),
                                    0,
                                    costs,
                                    line.getQty(),
                                    line.getDescription(),
                                    true
                            )) {
                                p_Error = "Failed to create cost detail record";
                                return null;
                            }
                        }
                    }
                } else {
                    //
                    if (line.getProductId() != 0) {
                        if (!MCostDetail.createShipment(
                                as,
                                line.getOrgId(),
                                line.getProductId(),
                                line.getAttributeSetInstanceId(),
                                line.getId(),
                                0,
                                costs,
                                line.getQty(),
                                line.getDescription(),
                                true)) {
                            p_Error = "Failed to create cost detail record";
                            return null;
                        }
                    }
                }
            } //	for all lines

            /* Commitment release *** */
            if (as.isAccrual() && as.isCreateSOCommitment()) {
                for (DocLine line : p_lines) {
                    Fact factcomm =
                            Doc_Order.getCommitmentSalesRelease(as, this, line.getQty(), line.getId(), Env.ONE);
                    if (factcomm != null) facts.add(factcomm);
                }
            } //	Commitment

        } //	Shipment
        //	  *** Sales - Return
        else if (getDocumentType().equals(DOCTYPE_MatReceipt) && isSOTrx()) {
            for (DocLine line : p_lines) {
                MProduct product = line.getProduct();
                BigDecimal costs;
                if (!isReversal(line)) {
                    if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as))) {
                        if (line.getAttributeSetInstanceId() == 0) {
                            MInOutLine ioLine = (MInOutLine) line.getPO();
                            I_M_InOutLineMA[] mas = MInOutLineMA.get(ioLine.getId());
                            costs = BigDecimal.ZERO;
                            if (mas != null && mas.length > 0) {
                                for (I_M_InOutLineMA ma : mas) {
                                    BigDecimal QtyMA = ma.getMovementQty();
                                    ProductCost pc = line.getProductCost();
                                    pc.setQty(QtyMA);
                                    pc.setM_AttributeSetInstanceId(ma.getAttributeSetInstanceId());
                                    BigDecimal maCosts =
                                            line.getProductCosts(as, line.getOrgId(), true, "M_InOutLine_ID=?");

                                    costs = costs.add(maCosts);
                                }
                            }
                        } else {
                            costs = line.getProductCosts(as, line.getOrgId(), true, "M_InOutLine_ID=?");
                        }
                    } else {
                        // MZ Goodwill
                        // if Shipment CostDetail exist then get Cost from Cost Detail
                        costs = line.getProductCosts(as, line.getOrgId(), true, "M_InOutLine_ID=?");
                        // end MZ
                    }
                    if (costs == null || costs.signum() == 0) // 	zero costs OK
                    {
                        if (product.isStocked()) {
                            p_Error = "No Costs for " + line.getProduct().getName();
                            log.log(Level.WARNING, p_Error);
                            return null;
                        } else //	ignore service
                            continue;
                    }
                } else {
                    costs = BigDecimal.ZERO;
                }
                //  Inventory               DR
                dr =
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
                                as.getCurrencyId(),
                                costs,
                                null);
                if (dr == null) {
                    p_Error = "FactLine DR not created: " + line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                dr.setLocatorId(line.getLocatorId());
                dr.setLocationFromLocator(line.getLocatorId(), true); // from Loc
                dr.setLocationFromBPartner(getBusinessPartnerLocationId(), false); // to Loc
                if (isReversal(line)) {
                    //	Set AmtAcctDr from Original Shipment/Receipt
                    if (!dr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, line.getReversalLineId(), Env.ONE)) {
                        if (!product.isStocked()) { // 	ignore service
                            fact.remove(dr);
                            continue;
                        }
                        p_Error = "Original Shipment/Receipt not posted yet";
                        return null;
                    }
                    costs = dr.getAcctBalance(); // get original cost
                }
                //
                if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as))) {
                    if (line.getAttributeSetInstanceId() == 0) {
                        MInOutLine ioLine = (MInOutLine) line.getPO();
                        I_M_InOutLineMA[] mas = MInOutLineMA.get(ioLine.getId());
                        if (mas != null && mas.length > 0) {
                            for (I_M_InOutLineMA ma : mas) {
                                if (!MCostDetail.createShipment(
                                        as,
                                        line.getOrgId(),
                                        line.getProductId(),
                                        ma.getAttributeSetInstanceId(),
                                        line.getId(),
                                        0,
                                        costs,
                                        ma.getMovementQty(),
                                        line.getDescription(),
                                        true)) {
                                    p_Error = "Failed to create cost detail record";
                                    return null;
                                }
                            }
                        }
                    } else {
                        if (line.getProductId() != 0) {
                            if (!MCostDetail.createShipment(
                                    as,
                                    line.getOrgId(),
                                    line.getProductId(),
                                    line.getAttributeSetInstanceId(),
                                    line.getId(),
                                    0,
                                    costs,
                                    line.getQty(),
                                    line.getDescription(),
                                    true)) {
                                p_Error = "Failed to create cost detail record";
                                return null;
                            }
                        }
                    }
                } else {
                    //
                    if (line.getProductId() != 0) {
                        if (!MCostDetail.createShipment(
                                as,
                                line.getOrgId(),
                                line.getProductId(),
                                line.getAttributeSetInstanceId(),
                                line.getId(),
                                0,
                                costs,
                                line.getQty(),
                                line.getDescription(),
                                true)) {
                            p_Error = "Failed to create cost detail record";
                            return null;
                        }
                    }
                }

                //  CoGS            CR
                cr =
                        fact.createLine(
                                line,
                                line.getAccount(ProductCost.ACCTTYPE_P_Cogs, as),
                                as.getCurrencyId(),
                                null,
                                costs);
                if (cr == null) {
                    p_Error = "FactLine CR not created: " + line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                cr.setLocatorId(line.getLocatorId());
                cr.setLocationFromLocator(line.getLocatorId(), true); //  from Loc
                cr.setLocationFromBPartner(getBusinessPartnerLocationId(), false); //  to Loc
                cr.setOrgId(line.getOrderOrgId()); // 	Revenue X-Org
                cr.setQty(line.getQty().negate());
                if (isReversal(line)) {
                    //	Set AmtAcctCr from Original Shipment/Receipt
                    if (!cr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, line.getReversalLineId(), Env.ONE)) {
                        p_Error = "Original Shipment/Receipt not posted yet";
                        return null;
                    }
                }
            } //	for all lines
        } //	Sales Return

        //  *** Purchasing - Receipt
        else if (getDocumentType().equals(DOCTYPE_MatReceipt) && !isSOTrx()) {
            for (DocLine p_line : p_lines) {
                // Elaine 2008/06/26
                int C_Currency_ID = as.getCurrencyId();
                //
                BigDecimal costs;
                MProduct product = p_line.getProduct();
                MOrderLine orderLine = null;
                BigDecimal landedCost = BigDecimal.ZERO;
                String costingMethod = product.getCostingMethod(as);
                if (!isReversal(p_line)) {
                    int C_OrderLine_ID = p_line.getOrderLineId();
                    if (C_OrderLine_ID > 0) {
                        orderLine = new MOrderLine(C_OrderLine_ID);
                        I_C_OrderLandedCostAllocation[] allocations =
                                MOrderLandedCostAllocation.getOfOrderLine(C_OrderLine_ID);
                        for (I_C_OrderLandedCostAllocation allocation : allocations) {
                            BigDecimal totalAmt = allocation.getAmt();
                            BigDecimal totalQty = allocation.getQty();
                            BigDecimal amt =
                                    totalAmt.multiply(p_line.getQty()).divide(totalQty, RoundingMode.HALF_UP);
                            landedCost = landedCost.add(amt);
                        }
                    }

                    // get costing method for product
                    if (MAcctSchema.COSTINGMETHOD_AveragePO.equals(costingMethod)
                            || MAcctSchema.COSTINGMETHOD_AverageInvoice.equals(costingMethod)
                            || MAcctSchema.COSTINGMETHOD_LastPOPrice.equals(costingMethod)
                            || (MAcctSchema.COSTINGMETHOD_StandardCosting.equals(costingMethod)
                            && MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as)))) {
                        // Low - check if c_orderline_id is valid
                        if (orderLine != null) {
                            // Elaine 2008/06/26
                            C_Currency_ID = orderLine.getCurrencyId();
                            //
                            costs = orderLine.getPriceCost();
                            if (costs == null || costs.signum() == 0) {
                                costs = orderLine.getPriceActual();
                                //	Goodwill: Correct included Tax
                                int C_Tax_ID = orderLine.getTaxId();
                                if (orderLine.isTaxIncluded() && C_Tax_ID != 0) {
                                    MTax tax = MTax.get(C_Tax_ID);
                                    if (!tax.isZeroTax()) {
                                        int stdPrecision = MCurrencyKt.getCurrencyStdPrecision(C_Currency_ID);
                                        BigDecimal costTax = tax.calculateTax(costs, true, stdPrecision);
                                        if (log.isLoggable(Level.FINE))
                                            log.fine("Costs=" + costs + " - Tax=" + costTax);
                                        costs = costs.subtract(costTax);
                                    }
                                } //	correct included Tax
                            }
                            costs = costs.multiply(p_line.getQty());
                        } else {
                            p_Error = "Resubmit - No Costs for " + product.getName() + " (required order line)";
                            log.log(Level.WARNING, p_Error);
                            return null;
                        }
                        //
                    } else {
                        costs = p_line.getProductCosts(as, p_line.getOrgId(), false); // 	current costs
                    }

                    if (costs == null || costs.signum() == 0) {
                        // ok if purchase price is actually zero
                        if (orderLine != null && orderLine.getPriceActual().signum() == 0) {
                            costs = BigDecimal.ZERO;
                        } else {
                            p_Error = "Resubmit - No Costs for " + product.getName();
                            log.log(Level.WARNING, p_Error);
                            return null;
                        }
                    }
                } else {
                    costs = BigDecimal.ZERO;
                }

                //  Inventory/Asset			DR
                I_C_ValidCombination assets = p_line.getAccount(ProductCost.ACCTTYPE_P_Asset, as);
                if (product.isService()) {
                    // if the line is a Outside Processing then DR WIP
                    if (p_line.getCostCollectorId() > 0)
                        assets = p_line.getAccount(ProductCost.ACCTTYPE_P_WorkInProcess, as);
                    else assets = p_line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
                }

                BigDecimal drAsset = costs;
                if (landedCost.signum() != 0
                        && (MAcctSchema.COSTINGMETHOD_AverageInvoice.equals(costingMethod)
                        || MAcctSchema.COSTINGMETHOD_AveragePO.equals(costingMethod))) {
                    drAsset = drAsset.add(landedCost);
                }
                dr = fact.createLine(p_line, assets, C_Currency_ID, drAsset, null);
                //
                if (dr == null) {
                    p_Error = "DR not created: " + p_line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                dr.setLocatorId(p_line.getLocatorId());
                dr.setLocationFromBPartner(getBusinessPartnerLocationId(), true); // from Loc
                dr.setLocationFromLocator(p_line.getLocatorId(), false); // to Loc
                if (isReversal(p_line)) {
                    //	Set AmtAcctDr from Original Shipment/Receipt
                    if (!dr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, p_line.getReversalLineId(), Env.ONE)) {
                        if (!product.isStocked()) { // 	ignore service
                            fact.remove(dr);
                            continue;
                        }
                        p_Error = "Original Receipt not posted yet";
                        return null;
                    }
                }

                //  NotInvoicedReceipt				CR
                cr =
                        fact.createLine(
                                p_line, getAccount(Doc.ACCTTYPE_NotInvoicedReceipts, as), C_Currency_ID, null, costs);
                //
                if (cr == null) {
                    p_Error = "CR not created: " + p_line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                cr.setLocatorId(p_line.getLocatorId());
                cr.setLocationFromBPartner(getBusinessPartnerLocationId(), true); //  from Loc
                cr.setLocationFromLocator(p_line.getLocatorId(), false); //  to Loc
                cr.setQty(p_line.getQty().negate());
                if (isReversal(p_line)) {
                    //	Set AmtAcctCr from Original Shipment/Receipt
                    if (!cr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, p_line.getReversalLineId(), Env.ONE)) {
                        p_Error = "Original Receipt not posted yet";
                        return null;
                    }
                }
                if (!fact.isAcctBalanced()) {
                    if (isReversal(p_line)) {
                        dr =
                                fact.createLine(
                                        p_line,
                                        p_line.getAccount(ProductCost.ACCTTYPE_P_LandedCostClearing, as),
                                        C_Currency_ID,
                                        Env.ONE,
                                        null);
                        if (!dr.updateReverseLine(
                                MInOut.Table_ID, m_Reversal_ID, p_line.getReversalLineId(), Env.ONE)) {
                            p_Error = "Original Receipt not posted yet";
                            return null;
                        }
                    } else if (landedCost.signum() != 0) {
                        cr =
                                fact.createLine(
                                        p_line,
                                        p_line.getAccount(ProductCost.ACCTTYPE_P_LandedCostClearing, as),
                                        C_Currency_ID,
                                        null,
                                        landedCost);
                        //
                        if (cr == null) {
                            p_Error = "CR not created: " + p_line;
                            log.log(Level.WARNING, p_Error);
                            return null;
                        }
                        cr.setLocatorId(p_line.getLocatorId());
                        cr.setLocationFromBPartner(getBusinessPartnerLocationId(), true); //  from Loc
                        cr.setLocationFromLocator(p_line.getLocatorId(), false); //  to Loc
                        cr.setQty(p_line.getQty().negate());
                    }
                }
            }
        } //	Receipt
        //	  *** Purchasing - return
        else if (getDocumentType().equals(DOCTYPE_MatShipment) && !isSOTrx()) {
            for (DocLine p_line : p_lines) {
                // Elaine 2008/06/26
                int C_Currency_ID = as.getCurrencyId();
                //
                BigDecimal costs;
                MProduct product = p_line.getProduct();
                if (!isReversal(p_line)) {
                    MInOutLine ioLine = (MInOutLine) p_line.getPO();
                    I_M_RMALine rmaLine = ioLine.getRMALine();
                    costs = rmaLine != null ? rmaLine.getAmt() : BigDecimal.ZERO;
                    I_M_InOutLine originalInOutLine = rmaLine != null ? rmaLine.getInOutLine() : null;
                    if (originalInOutLine != null && originalInOutLine.getOrderLineId() > 0) {
                        MOrderLine originalOrderLine = (MOrderLine) originalInOutLine.getOrderLine();
                        //	Goodwill: Correct included Tax
                        int C_Tax_ID = originalOrderLine.getTaxId();
                        if (originalOrderLine.isTaxIncluded() && C_Tax_ID != 0) {
                            MTax tax = MTax.get(C_Tax_ID);
                            if (!tax.isZeroTax()) {
                                int stdPrecision =
                                        MCurrencyKt.getCurrencyStdPrecision(originalOrderLine.getCurrencyId());
                                BigDecimal costTax = tax.calculateTax(costs, true, stdPrecision);
                                if (log.isLoggable(Level.FINE)) log.fine("Costs=" + costs + " - Tax=" + costTax);
                                costs = costs.subtract(costTax);
                            }
                        } //	correct included Tax

                        // different currency
                        if (C_Currency_ID != originalOrderLine.getCurrencyId()) {
                            costs =
                                    MConversionRate.convert(
                                            costs,
                                            originalOrderLine.getCurrencyId(),
                                            C_Currency_ID,
                                            getDateAcct(),
                                            0,
                                            getClientId(),
                                            getOrgId(),
                                            true);
                        }

                        costs = costs.multiply(p_line.getQty());
                        costs = costs.negate();
                    } else {
                        if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as))) {
                            if (p_line.getAttributeSetInstanceId() == 0) {
                                I_M_InOutLineMA[] mas = MInOutLineMA.get(ioLine.getId());
                                costs = BigDecimal.ZERO;
                                if (mas != null && mas.length > 0) {
                                    for (I_M_InOutLineMA ma : mas) {
                                        BigDecimal QtyMA = ma.getMovementQty();
                                        ProductCost pc = p_line.getProductCost();
                                        pc.setQty(QtyMA);
                                        pc.setM_AttributeSetInstanceId(ma.getAttributeSetInstanceId());
                                        BigDecimal maCosts =
                                                p_line.getProductCosts(as, p_line.getOrgId(), true, "M_InOutLine_ID=?");

                                        costs = costs.add(maCosts);
                                    }
                                }
                            } else {
                                costs = p_line.getProductCosts(as, p_line.getOrgId(), false); // 	current costs
                            }
                        } else {
                            costs = p_line.getProductCosts(as, p_line.getOrgId(), false); // 	current costs
                        }

                        if (costs == null || costs.signum() == 0) {
                            p_Error = "Resubmit - No Costs for " + product.getName();
                            log.log(Level.WARNING, p_Error);
                            return null;
                        }
                    }
                } else {
                    // update below
                    costs = Env.ONE;
                }
                //  NotInvoicedReceipt				DR
                // Elaine 2008/06/26
        /*dr = fact.createLine(line,
        getAccount(Doc.ACCTTYPE_NotInvoicedReceipts, as),
        as.getCurrencyId(), costs , null);*/
                dr =
                        fact.createLine(
                                p_line, getAccount(Doc.ACCTTYPE_NotInvoicedReceipts, as), C_Currency_ID, costs, null);
                //
                if (dr == null) {
                    p_Error = "CR not created: " + p_line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                dr.setLocatorId(p_line.getLocatorId());
                dr.setLocationFromBPartner(getBusinessPartnerLocationId(), true); //  from Loc
                dr.setLocationFromLocator(p_line.getLocatorId(), false); //  to Loc
                dr.setQty(p_line.getQty().negate());
                if (isReversal(p_line)) {
                    //	Set AmtAcctDr from Original Shipment/Receipt
                    if (!dr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, p_line.getReversalLineId(), Env.ONE)) {
                        if (!product.isStocked()) { // 	ignore service
                            fact.remove(dr);
                            continue;
                        }
                        p_Error = "Original Receipt not posted yet";
                        return null;
                    }
                }

                //  Inventory/Asset			CR
                I_C_ValidCombination assets = p_line.getAccount(ProductCost.ACCTTYPE_P_Asset, as);
                if (product.isService()) assets = p_line.getAccount(ProductCost.ACCTTYPE_P_Expense, as);
                // Elaine 2008/06/26
        /*cr = fact.createLine(line, assets,
        as.getCurrencyId(), null, costs);*/
                cr = fact.createLine(p_line, assets, C_Currency_ID, null, costs);
                //
                if (cr == null) {
                    p_Error = "DR not created: " + p_line;
                    log.log(Level.WARNING, p_Error);
                    return null;
                }
                cr.setLocatorId(p_line.getLocatorId());
                cr.setLocationFromBPartner(getBusinessPartnerLocationId(), true); // from Loc
                cr.setLocationFromLocator(p_line.getLocatorId(), false); // to Loc
                if (isReversal(p_line)) {
                    //	Set AmtAcctCr from Original Shipment/Receipt
                    if (!cr.updateReverseLine(
                            MInOut.Table_ID, m_Reversal_ID, p_line.getReversalLineId(), Env.ONE)) {
                        p_Error = "Original Receipt not posted yet";
                        return null;
                    }
                }

                String costingError = createVendorRMACostDetail(as, p_line, costs);
                if (!Util.isEmpty(costingError)) {
                    p_Error = costingError;
                    return null;
                }
            }

        } //	Purchasing Return
        else {
            p_Error = "DocumentType unknown: " + getDocumentType();
            log.log(Level.SEVERE, p_Error);
            return null;
        }
        //
        facts.add(fact);
        return facts;
    } //  createFact

    private boolean isReversal(DocLine line) {
        return m_Reversal_ID != 0 && line.getReversalLineId() != 0;
    }

    private String createVendorRMACostDetail(AccountingSchema as, DocLine line, BigDecimal costs) {
        BigDecimal tQty = line.getQty();
        BigDecimal tAmt = costs;
        if (tAmt.signum() != tQty.signum()) {
            tAmt = tAmt.negate();
        }
        MProduct product = line.getProduct();
        if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(product.getCostingLevel(as))) {
            if (line.getAttributeSetInstanceId() == 0) {
                I_M_InOutLineMA[] mas = MInOutLineMA.get(line.getId());
                if (mas != null && mas.length > 0) {
                    for (I_M_InOutLineMA ma : mas) {
                        if (!MCostDetail.createShipment(
                                as,
                                line.getOrgId(),
                                line.getProductId(),
                                ma.getAttributeSetInstanceId(),
                                line.getId(),
                                0,
                                tAmt,
                                ma.getMovementQty().negate(),
                                line.getDescription(),
                                false)) return "SaveError";
                    }
                }
            } else {
                if (!MCostDetail.createShipment(
                        as,
                        line.getOrgId(),
                        line.getProductId(),
                        line.getAttributeSetInstanceId(),
                        line.getId(),
                        0,
                        tAmt,
                        tQty,
                        line.getDescription(),
                        false)) return "SaveError";
            }
        } else {
            if (!MCostDetail.createShipment(
                    as,
                    line.getOrgId(),
                    line.getProductId(),
                    line.getAttributeSetInstanceId(),
                    line.getId(),
                    0,
                    tAmt,
                    tQty,
                    line.getDescription(),
                    false)) return "SaveError";
        }
        return "";
    }
} //  Doc_InOut
