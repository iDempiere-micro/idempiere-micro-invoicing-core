package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.invoicing.MConversionRate;
import org.compiere.model.AccountingSchema;
import org.compiere.model.ClientInfoWithAccounting;
import org.compiere.model.ClientOrganization;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.I_M_Cost;
import org.compiere.model.I_M_CostElement;
import org.compiere.model.I_M_Product_PO;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.Query;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.DBException;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Product Cost Model
 *
 * @author Jorg Janke
 * @author Carlos Ruiz - globalqss
 * <li>integrate bug fix from Teo Sarca - [ 1619112 ] Posible problem for LastPO costing,
 * Batch/Lot level
 * @author Red1
 * <li>FR: [ 2214883 ] Remove SQL code and Replace for Query - red1 (only non-join query)
 * @author Teo Sarca
 * <li>BF [ 2847648 ] Manufacture & shipment cost errors
 * https://sourceforge.net/tracker/?func=detail&aid=2847648&group_id=176962&atid=934929
 * @version $Id: MCost.java,v 1.6 2006/07/30 00:51:02 jjanke Exp $
 */
public class MCost extends X_M_Cost {
    /**
     *
     */
    private static final long serialVersionUID = -8904980122276406878L;
    /**
     * Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MCost.class);
    /**
     * Data is entered Manually
     */
    private boolean m_manual = true;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param ignored multi-key
     */
    public MCost(int ignored) {
        super(ignored);
        if (ignored == 0) {
            setAttributeSetInstanceId(0);
            //
            setCurrentCostPrice(Env.ZERO);
            setFutureCostPrice(Env.ZERO);
            setCurrentQty(Env.ZERO);
            setCumulatedAmt(Env.ZERO);
            setCumulatedQty(Env.ZERO);
        } else throw new IllegalArgumentException("Multi-Key");
    } //	MCost

    /**
     * Load Constructor
     *
     */
    public MCost(Row row) {
        super(row);
        m_manual = false;
    } //	MCost

    /**
     * Parent Constructor
     *
     * @param product                   Product
     * @param M_AttributeSetInstance_ID asi
     * @param as                        Acct Schema
     * @param AD_Org_ID                 org
     * @param M_CostElement_ID          cost element
     */
    public MCost(
            MProduct product,
            int M_AttributeSetInstance_ID,
            AccountingSchema as,
            int AD_Org_ID,
            int M_CostElement_ID) {
        this(0);
        setClientOrg(product.getClientId(), AD_Org_ID);
        setAccountingSchemaId(as.getAccountingSchemaId());
        setCostTypeId(as.getCostTypeId());
        setProductId(product.getProductId());
        setAttributeSetInstanceId(M_AttributeSetInstance_ID);
        setCostElementId(M_CostElement_ID);
        //
        m_manual = false;
    } //	MCost

    /**
     * Retrieve/Calculate Current Cost Price
     *
     * @param product                   product
     * @param M_AttributeSetInstance_ID real asi
     * @param as                        accounting schema
     * @param AD_Org_ID                 real org
     * @param costingMethod             AcctSchema.COSTINGMETHOD_*
     * @param qty                       qty
     * @param zeroCostsOK               zero/no costs are OK
     * @return current cost price or null
     */
    public static BigDecimal getCurrentCost(
            MProduct product,
            int M_AttributeSetInstance_ID,
            AccountingSchema as,
            int AD_Org_ID,
            String costingMethod,
            BigDecimal qty,
            boolean zeroCostsOK) {
        String CostingLevel = product.getCostingLevel(as);
        if (MAcctSchema.COSTINGLEVEL_Client.equals(CostingLevel)) {
            AD_Org_ID = 0;
            M_AttributeSetInstance_ID = 0;
        } else if (MAcctSchema.COSTINGLEVEL_Organization.equals(CostingLevel))
            M_AttributeSetInstance_ID = 0;
        else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(CostingLevel)) AD_Org_ID = 0;
        //	Costing Method
        if (costingMethod == null) {
            costingMethod = product.getCostingMethod(as);
            if (costingMethod == null) {
                throw new IllegalArgumentException("No Costing Method");
            }
        }

        //	Create/Update Costs
        MCostDetail.processProduct(product);

        return getCurrentCost(
                product,
                M_AttributeSetInstance_ID,
                as,
                AD_Org_ID,
                as.getCostTypeId(),
                costingMethod,
                qty,
                zeroCostsOK);
    } //	getCurrentCost

    /**
     * Get Current Cost Price for Costing Level
     *
     * @param product        product
     * @param M_ASI_ID       costing level asi
     * @param as             AcctSchema
     * @param Org_ID         costing level org
     * @param M_CostType_ID  cost type
     * @param costingMethod  method
     * @param qty            quantity
     * @param zeroCostsOK    zero/no costs are OK
     * @return cost price or null
     */
    private static BigDecimal getCurrentCost(
            MProduct product,
            int M_ASI_ID,
            AccountingSchema as,
            int Org_ID,
            int M_CostType_ID,
            String costingMethod,
            BigDecimal qty,
            boolean zeroCostsOK) {
        String costElementType;
        BigDecimal percent;
        //
        BigDecimal materialCostEach = Env.ZERO;
        BigDecimal otherCostEach = Env.ZERO;
        BigDecimal percentage = Env.ZERO;
        int count = 0;
        //
        String sql =
                "SELECT"
                        + " COALESCE(SUM(c.CurrentCostPrice),0)," // 1
                        + " ce.CostElementType, ce.CostingMethod," // 2,3
                        + " c.Percent, c.M_CostElement_ID ," // 4,5
                        + " COALESCE(SUM(c.CurrentCostPriceLL),0) " // 6
                        + " FROM M_Cost c"
                        + " LEFT OUTER JOIN M_CostElement ce ON (c.M_CostElement_ID=ce.M_CostElement_ID) "
                        + "WHERE c.AD_Client_ID=? AND c.AD_Org_ID=?" //	#1/2
                        + " AND c.M_Product_ID=?" //	#3
                        + " AND (c.M_AttributeSetInstance_ID=? OR c.M_AttributeSetInstance_ID=0)" //	#4
                        + " AND c.M_CostType_ID=? AND c.C_AcctSchema_ID=?" //	#5/6
                        + " AND (ce.CostingMethod IS NULL OR ce.CostingMethod=?) " //	#7
                        + "GROUP BY ce.CostElementType, ce.CostingMethod, c.Percent, c.M_CostElement_ID";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, product.getClientId());
            pstmt.setInt(2, Org_ID);
            pstmt.setInt(3, product.getProductId());
            pstmt.setInt(4, M_ASI_ID);
            pstmt.setInt(5, M_CostType_ID);
            pstmt.setInt(6, as.getAccountingSchemaId());
            pstmt.setString(7, costingMethod);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                BigDecimal currentCostPrice = rs.getBigDecimal(1);
                BigDecimal currentCostPriceLL = rs.getBigDecimal(6);
                costElementType = rs.getString(2);
                String cm = rs.getString(3);
                percent = rs.getBigDecimal(4);
                // M_CostElement_ID = rs.getInt(5);
                if (s_log.isLoggable(Level.FINEST))
                    s_log.finest(
                            "CurrentCostPrice="
                                    + currentCostPrice
                                    + ", CurrentCostPriceLL="
                                    + currentCostPriceLL
                                    + ", CostElementType="
                                    + costElementType
                                    + ", CostingMethod="
                                    + cm
                                    + ", Percent="
                                    + percent);
                //
                if (currentCostPrice != null && currentCostPrice.signum() != 0) {
                    if (cm != null) {
                        materialCostEach = materialCostEach.add(currentCostPrice);
                    } else {
                        otherCostEach = otherCostEach.add(currentCostPrice);
                    }
                }
                if (percent != null && percent.signum() != 0) percentage = percentage.add(percent);
                count++;
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        if (count > 1) // 	Print summary
            if (s_log.isLoggable(Level.FINEST))
                s_log.finest(
                        "MaterialCost="
                                + materialCostEach
                                + ", OtherCosts="
                                + otherCostEach
                                + ", Percentage="
                                + percentage);

        //	Seed Initial Costs
        if (materialCostEach.signum() == 0) // 	no costs
        {
            if (zeroCostsOK) return Env.ZERO;
        }

        //	Material Costs
        BigDecimal materialCost = materialCostEach.multiply(qty);
        //	Standard costs - just Material Costs
        if (MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod)) {
            if (s_log.isLoggable(Level.FINER)) s_log.finer("MaterialCosts = " + materialCost);
            return materialCost;
        }
        if (MCostElement.COSTINGMETHOD_Fifo.equals(costingMethod)
                || MCostElement.COSTINGMETHOD_Lifo.equals(costingMethod)) {
            I_M_CostElement ce = MCostElement.getMaterialCostElement(as, costingMethod);
            materialCost = MCostQueue.getCosts(product, M_ASI_ID, as, Org_ID, ce, qty);
        }

        //	Other Costs
        BigDecimal otherCost = otherCostEach.multiply(qty);

        //	Costs
        BigDecimal costs = otherCost.add(materialCost);
        if (costs.signum() == 0) return null;

        if (s_log.isLoggable(Level.FINER)) s_log.finer("Sum Costs = " + costs);
        int precision = as.getCostingPrecision();
        if (percentage.signum() == 0) // 	no percentages
        {
            if (costs.scale() > precision) costs = costs.setScale(precision, BigDecimal.ROUND_HALF_UP);
            return costs;
        }
        //
        BigDecimal percentCost = costs.multiply(percentage);
        percentCost = percentCost.divide(Env.ONEHUNDRED, precision, BigDecimal.ROUND_HALF_UP);
        costs = costs.add(percentCost);
        if (costs.scale() > precision) costs = costs.setScale(precision, BigDecimal.ROUND_HALF_UP);
        if (s_log.isLoggable(Level.FINER))
            s_log.finer("Sum Costs = " + costs + " (Add=" + percentCost + ")");
        return costs;
    } //	getCurrentCost

    /**
     * Get Seed Costs
     *
     * @param product        product
     * @param M_ASI_ID       costing level asi
     * @param as             accounting schema
     * @param Org_ID         costing level org
     * @param costingMethod  costing method
     * @param C_OrderLine_ID optional order line
     * @return price or null
     */
    public static BigDecimal getSeedCosts(
            MProduct product,
            int M_ASI_ID,
            AccountingSchema as,
            int Org_ID,
            String costingMethod,
            int C_OrderLine_ID) {
        BigDecimal retValue = null;
        //	Direct Data
        if (MCostElement.COSTINGMETHOD_AverageInvoice.equals(costingMethod)) return null;
        else if (MCostElement.COSTINGMETHOD_AveragePO.equals(costingMethod)) return null;
        else if (MCostElement.COSTINGMETHOD_Fifo.equals(costingMethod)) return null;
        else if (MCostElement.COSTINGMETHOD_Lifo.equals(costingMethod)) return null;
        else if (MCostElement.COSTINGMETHOD_LastInvoice.equals(costingMethod))
            retValue = getLastInvoicePrice(product, M_ASI_ID, Org_ID, as.getCurrencyId());
        else if (MCostElement.COSTINGMETHOD_LastPOPrice.equals(costingMethod)) {
            if (C_OrderLine_ID != 0)
                retValue = getPOPrice(product, C_OrderLine_ID, as.getCurrencyId());
            if (retValue == null || retValue.signum() == 0)
                retValue = getLastPOPrice(product, M_ASI_ID, Org_ID, as.getCurrencyId());
        } else if (!MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod) && !MCostElement.COSTINGMETHOD_UserDefined.equals(costingMethod)) {
            throw new IllegalArgumentException("Unknown Costing Method = " + costingMethod);
        }
        if (retValue != null && retValue.signum() > 0) {
            if (s_log.isLoggable(Level.FINE))
                s_log.fine(product.getName() + ", CostingMethod=" + costingMethod + " - " + retValue);
            return retValue;
        }

        //	Look for exact Order Line
        if (C_OrderLine_ID != 0) {
            retValue = getPOPrice(product, C_OrderLine_ID, as.getCurrencyId());
            if (retValue != null && retValue.signum() > 0) {
                if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + ", PO - " + retValue);
                return retValue;
            }
        }

        //	Look for Standard Costs first
        if (!MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod)) {
            I_M_CostElement ce =
                    MCostElement.getMaterialCostElement(as, MCostElement.COSTINGMETHOD_StandardCosting);
            I_M_Cost cost =
                    get(product, M_ASI_ID, as, Org_ID, ce.getCostElementId());
            if (cost != null && cost.getCurrentCostPrice().signum() > 0) {
                if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + ", Standard - " + cost);
                return cost.getCurrentCostPrice();
            }
        }

        //	We do not have a price
        //	PO first
        if (MCostElement.COSTINGMETHOD_LastPOPrice.equals(costingMethod)
                || MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod)) {
            //	try Last PO
            retValue = getLastPOPrice(product, M_ASI_ID, Org_ID, as.getCurrencyId());
            if (Org_ID != 0 && (retValue == null || retValue.signum() == 0))
                retValue = getLastPOPrice(product, M_ASI_ID, 0, as.getCurrencyId());
            if (retValue != null && retValue.signum() > 0) {
                if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + ", LastPO = " + retValue);
                return retValue;
            }
        } else //	Inv first
        {
            //	try last Inv
            retValue = getLastInvoicePrice(product, M_ASI_ID, Org_ID, as.getCurrencyId());
            if (Org_ID != 0 && (retValue == null || retValue.signum() == 0))
                retValue = getLastInvoicePrice(product, M_ASI_ID, 0, as.getCurrencyId());
            if (retValue != null && retValue.signum() != 0) {
                if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + ", LastInv = " + retValue);
                return retValue;
            }
        }

        //	Still Nothing
        //	Inv second
        if (MCostElement.COSTINGMETHOD_LastPOPrice.equals(costingMethod)
                || MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod)) {
            //	try last Inv
            retValue = getLastInvoicePrice(product, M_ASI_ID, Org_ID, as.getCurrencyId());
            if (Org_ID != 0 && (retValue == null || retValue.signum() == 0))
                retValue = getLastInvoicePrice(product, M_ASI_ID, 0, as.getCurrencyId());
            if (retValue != null && retValue.signum() > 0) {
                if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + ", LastInv = " + retValue);
                return retValue;
            }
        } else //	PO second
        {
            //	try Last PO
            retValue = getLastPOPrice(product, M_ASI_ID, Org_ID, as.getCurrencyId());
            if (Org_ID != 0 && (retValue == null || retValue.signum() == 0))
                retValue = getLastPOPrice(product, M_ASI_ID, 0, as.getCurrencyId());
            if (retValue != null && retValue.signum() > 0) {
                if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + ", LastPO = " + retValue);
                return retValue;
            }
        }

        //	Still nothing try ProductPO
        I_M_Product_PO[] pos =
                MProductPO.getOfProduct(product.getProductId());
        for (I_M_Product_PO po : pos) {
            BigDecimal price = po.getPricePO();
            if (price == null || price.signum() == 0) price = po.getPriceList();
            if (price != null && price.signum() != 0) {
                price =
                        MConversionRate.convert(

                                price,
                                po.getCurrencyId(),
                                as.getCurrencyId(),
                                as.getClientId(),
                                Org_ID);
                if (price != null && price.signum() != 0) {
                    if (po.getUOMId() != product.getUOMId()) {
                        price =
                                MUOMConversion.convertProductTo(
                                        product.getProductId(), po.getUOMId(), price);
                    }
                }
                if (price != null && price.signum() != 0) {
                    retValue = price;
                    if (s_log.isLoggable(Level.FINE))
                        s_log.fine(product.getName() + ", Product_PO = " + retValue);
                    return retValue;
                }
            }
        }

        //	Still nothing try Purchase Price List
        BigDecimal price = getSeedCostFromPriceList(product, as, Org_ID);
        if (price != null && price.signum() > 0) {
            retValue = price;
        }

        if (s_log.isLoggable(Level.FINE)) s_log.fine(product.getName() + " = " + retValue);
        return retValue;
    } //	getSeedCosts

    private static BigDecimal getSeedCostFromPriceList(MProduct product, AccountingSchema as, int orgID) {
        String sql =
                "SELECT pp.PriceList, pp.PriceStd FROM M_ProductPrice pp"
                        + " INNER JOIN M_PriceList_Version plv ON (pp.M_PriceList_Version_ID = plv.M_PriceList_Version_ID AND plv.ValidFrom <= trunc(sysdate))"
                        + " INNER JOIN M_PriceList pl ON (plv.M_PriceList_ID = pl.M_PriceList_ID AND pl.IsSOPriceList = 'N')"
                        + " WHERE pp.clientId = ? AND pp.orgId IN (0, ?) AND pp.M_Product_ID = ? AND pp.PriceList > 0 AND pp.IsActive = 'Y' "
                        + " ORDER BY pp.orgId Desc, plv.ValidFrom Desc";
        PreparedStatement st;
        ResultSet rs;
        try {
            st = prepareStatement(sql);
            st.setInt(1, as.getClientId());
            st.setInt(2, orgID);
            st.setInt(3, product.getProductId());
            rs = st.executeQuery();
            if (rs.next()) {
                BigDecimal priceList = rs.getBigDecimal(1);
                BigDecimal priceStd = rs.getBigDecimal(2);
                if (priceStd != null && priceStd.signum() > 0) return priceStd;
                else return priceList;
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get Last Invoice Price in currency
     *
     * @param product       product
     * @param M_ASI_ID      attribute set instance
     * @param AD_Org_ID     org
     * @param C_Currency_ID accounting currency
     * @return last invoice price in currency
     */
    public static BigDecimal getLastInvoicePrice(
            MProduct product, int M_ASI_ID, int AD_Org_ID, int C_Currency_ID) {
        BigDecimal retValue = null;
        StringBuilder sql =
                new StringBuilder(
                        "SELECT currencyConvert(il.PriceActual, i.C_Currency_ID, ?, i.DateAcct, i.C_ConversionType_ID, il.AD_Client_ID, il.orgId) ")
                        // ,il.PriceActual, il.QtyInvoiced, i.DateInvoiced, il.Line
                        .append("FROM C_InvoiceLine il ")
                        .append(" INNER JOIN C_Invoice i ON (il.C_Invoice_ID=i.C_Invoice_ID) ")
                        .append("WHERE il.M_Product_ID=?")
                        .append(" AND i.IsSOTrx='N'");
        if (AD_Org_ID != 0) sql.append(" AND il.AD_Org_ID=?");
        else if (M_ASI_ID != 0) sql.append(" AND il.M_AttributeSetInstance_ID=?");
        sql.append(" ORDER BY i.DateInvoiced DESC, il.Line DESC");
        //
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql.toString());
            pstmt.setInt(1, C_Currency_ID);
            pstmt.setInt(2, product.getProductId());
            if (AD_Org_ID != 0) pstmt.setInt(3, AD_Org_ID);
            else if (M_ASI_ID != 0) pstmt.setInt(3, M_ASI_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) retValue = rs.getBigDecimal(1);
        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql.toString(), e);
        }

        if (retValue != null) {
            if (s_log.isLoggable(Level.FINER)) s_log.finer(product.getName() + " = " + retValue);
            return retValue;
        }
        return null;
    } //	getLastInvoicePrice

    /**
     * Get Last PO Price in currency
     *
     * @param product       product
     * @param M_ASI_ID      attribute set instance
     * @param AD_Org_ID     org
     * @param C_Currency_ID accounting currency
     * @return last PO price in currency or null
     */
    public static BigDecimal getLastPOPrice(
            MProduct product, int M_ASI_ID, int AD_Org_ID, int C_Currency_ID) {
        BigDecimal retValue = null;
        StringBuilder sql =
                new StringBuilder(
                        "SELECT currencyConvert(ol.PriceCost, o.C_Currency_ID, ?, o.DateAcct, o.C_ConversionType_ID, ol.AD_Client_ID, ol.orgId),")
                        .append(
                                " currencyConvert(ol.PriceActual, o.C_Currency_ID, ?, o.DateAcct, o.C_ConversionType_ID, ol.AD_Client_ID, ol.orgId) ")
                        //	,ol.PriceCost,ol.PriceActual, ol.QtyOrdered, o.DateOrdered, ol.Line
                        .append("FROM C_OrderLine ol")
                        .append(" INNER JOIN C_Order o ON (ol.C_Order_ID=o.C_Order_ID) ")
                        .append("WHERE ol.M_Product_ID=?")
                        .append(" AND o.IsSOTrx='N'");
        if (AD_Org_ID != 0) sql.append(" AND ol.AD_Org_ID=?");
        else if (M_ASI_ID != 0) sql.append(" AND ol.M_AttributeSetInstance_ID=?");
        sql.append(" ORDER BY o.DateOrdered DESC, ol.Line DESC");
        //
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql.toString());
            pstmt.setInt(1, C_Currency_ID);
            pstmt.setInt(2, C_Currency_ID);
            pstmt.setInt(3, product.getProductId());
            if (AD_Org_ID != 0) pstmt.setInt(4, AD_Org_ID);
            else if (M_ASI_ID != 0) pstmt.setInt(4, M_ASI_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retValue = rs.getBigDecimal(1);
                if (retValue == null || retValue.signum() == 0) retValue = rs.getBigDecimal(2);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }

        if (retValue != null) {
            if (s_log.isLoggable(Level.FINER)) s_log.finer(product.getName() + " = " + retValue);
            return retValue;
        }
        return null;
    } //	getLastPOPrice

    /**
     * Get PO Price in currency
     *
     * @param product        product
     * @param C_OrderLine_ID order line
     * @param C_Currency_ID  accounting currency
     * @return last PO price in currency or null
     */
    public static BigDecimal getPOPrice(MProduct product, int C_OrderLine_ID, int C_Currency_ID) {
        BigDecimal retValue = null;
        String sql =
                "SELECT currencyConvert(ol.PriceCost, o.C_Currency_ID, ?, o.DateAcct, o.C_ConversionType_ID, ol.AD_Client_ID, ol.orgId),"
                        + " currencyConvert(ol.PriceActual, o.C_Currency_ID, ?, o.DateAcct, o.C_ConversionType_ID, ol.AD_Client_ID, ol.orgId) "
                        //	,ol.PriceCost,ol.PriceActual, ol.QtyOrdered, o.DateOrdered, ol.Line
                        + "FROM C_OrderLine ol"
                        + " INNER JOIN C_Order o ON (ol.C_Order_ID=o.C_Order_ID) "
                        + "WHERE ol.C_OrderLine_ID=?"
                        + " AND o.IsSOTrx='N'";
        //
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, C_Currency_ID);
            pstmt.setInt(2, C_Currency_ID);
            pstmt.setInt(3, C_OrderLine_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retValue = rs.getBigDecimal(1);
                if (retValue == null || retValue.signum() == 0) retValue = rs.getBigDecimal(2);
            }
        } catch (Exception e) {
            s_log.log(Level.SEVERE, sql, e);
        }

        if (retValue != null) {
            if (s_log.isLoggable(Level.FINER)) s_log.finer(product.getName() + " = " + retValue);
            return retValue;
        }
        return null;
    } //	getPOPrice

    /**
     * ************************************************************************ Create costing for
     * client. Handles Transaction if not in a transaction
     *
     * @param client client
     */
    public static void create(ClientWithAccounting client) {
        MBaseCostKt.createCostingForClient(client);
    } //	create

    /**
     * Create standard Costing records for Product
     *
     * @param product product
     */
    protected static void create(MProduct product) {
        s_log.config(product.getName());

        //	Cost Elements
        I_M_CostElement[] ces = MCostElement.getCostingMethods(product);
        I_M_CostElement ce = null;
        for (I_M_CostElement element : ces) {
            if (X_M_CostElement.COSTINGMETHOD_StandardCosting.equals(element.getCostingMethod())) {
                ce = element;
                break;
            }
        }
        if (ce == null) {
            s_log.fine("No Standard Costing in System");
            return;
        }

        AccountingSchema[] mass =
                MAcctSchema.getClientAcctSchema(
                        product.getClientId());
        ClientOrganization[] orgs = null;

        int M_ASI_ID = 0; // 	No Attribute
        for (AccountingSchema as : mass) {
            String cl = product.getCostingLevel(as);
            //	Create Std Costing
            if (MAcctSchema.COSTINGLEVEL_Client.equals(cl)) {
                createCostingRecord(product, M_ASI_ID, as, 0, ce.getCostElementId());
            } else if (MAcctSchema.COSTINGLEVEL_Organization.equals(cl)) {
                if (as.getOrganizationOnlyId() > 0
                        && MOrgKt.getOrg(as.getOrganizationOnlyId()).isSummary()) {
                    ClientWithAccounting client = MClientKt.getClientWithAccounting(product.getClientId());
                    ClientInfoWithAccounting ci = client.getInfo();
                    MTree vTree =
                            new MTree(

                                    ci.getTreeOrgId(),
                                    false,
                                    true,
                                    true);

                    MTreeNode root = vTree.getRoot();
                    createForChildOrg(root, product, as, M_ASI_ID, ce, false);
                } else {
                    if (orgs == null) orgs = MOrgKt.getClientOrganizations(product);

                    for (ClientOrganization o : orgs) {
                        if (o.isSummary()) continue;
                        if (as.getOrganizationOnlyId() == o.getOrgId() || as.getOrganizationOnlyId() == 0) {
                            createCostingRecord(
                                    product, M_ASI_ID, as, o.getOrgId(), ce.getCostElementId());
                        }
                    }
                }

            } else {
                s_log.warning(
                        "Not created: Std.Cost for " + product.getName() + " - Costing Level on Batch/Lot");
            } //	accounting schema loop
        }
    } //	create

    private static void createForChildOrg(
            MTreeNode root,
            MProduct product,
            AccountingSchema as,
            int M_ASI_ID,
            I_M_CostElement ce,
            boolean found) {
        int parentId = root.getNodeId();
        if (!found) found = (parentId == as.getOrganizationOnlyId());
        Enumeration<?> nodeEnum = root.children();
        MTreeNode child;
        while (nodeEnum.hasMoreElements()) {
            child = (MTreeNode) nodeEnum.nextElement();
            if (child != null && child.getChildCount() > 0) {
                createForChildOrg(child, product, as, M_ASI_ID, ce, found);
            } else if (found) {
                int orgId = child.getNodeId();
                MOrg org = MOrgKt.getOrg(orgId);
                if (!org.isSummary())
                    createCostingRecord(product, M_ASI_ID, as, orgId, ce.getCostElementId());
            }
        }
    }

    private static void createCostingRecord(
            MProduct product, int M_ASI_ID, AccountingSchema as, int AD_Org_ID, int M_CostElement_ID) {
        I_M_Cost cost =
                MCost.get(product, M_ASI_ID, as, AD_Org_ID, M_CostElement_ID);
        if (cost.isNew()) {
            if (cost.save()) {
                if (s_log.isLoggable(Level.CONFIG))
                    s_log.config("Std.Cost for " + product.getName() + " - " + as.getName());
            } else {
                s_log.warning("Not created: Std.Cost for " + product.getName() + " - " + as.getName());
            }
        }
    }

    /**
     * Delete standard Costing records for Product
     *
     * @param product product
     */
    protected static void delete(MProduct product) {
        s_log.config(product.getName());
        //	Cost Elements
        List<I_M_CostElement> ces = MCostElement.getCostElementsWithCostingMethods(product);

        AccountingSchema[] mass =
                MAcctSchema.getClientAcctSchema(
                        product.getClientId());
        ClientOrganization[] orgs = null;

        int M_ASI_ID = 0; // 	No Attribute
        for (AccountingSchema as : mass) {
            String cl = product.getCostingLevel(as);
            //	Create Std Costing
            if (MAcctSchema.COSTINGLEVEL_Client.equals(cl)) {
                for (I_M_CostElement ce : ces) {
                    I_M_Cost cost =
                            MCost.get(product, M_ASI_ID, as, 0, ce.getCostElementId());
                    if (cost != null) cost.deleteEx(true);
                }
            } else if (MAcctSchema.COSTINGLEVEL_Organization.equals(cl)) {
                if (orgs == null) orgs = MOrgKt.getClientOrganizations(product);
                for (ClientOrganization o : orgs) {
                    for (I_M_CostElement ce : ces) {
                        I_M_Cost cost =
                                MCost.get(
                                        product,
                                        M_ASI_ID,
                                        as,
                                        o.getOrgId(),
                                        ce.getCostElementId()
                                );
                        if (cost != null) cost.deleteEx(true);
                    }
                } //	for all orgs
            } else {
                s_log.warning(
                        "Not created: Std.Cost for " + product.getName() + " - Costing Level on Batch/Lot");
            }
        } //	accounting schema loop
    } //	create

    /**
     * Get/Create Cost Record. CostingLevel is not validated
     *
     * @param product                   product
     * @param M_AttributeSetInstance_ID costing level asi
     * @param as                        accounting schema
     * @param AD_Org_ID                 costing level org
     * @param M_CostElement_ID          element
     * @return cost price or null
     */
    public static I_M_Cost get(
            MProduct product,
            int M_AttributeSetInstance_ID,
            AccountingSchema as,
            int AD_Org_ID,
            int M_CostElement_ID) {
        I_M_Cost cost;
        // FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
        final String whereClause =
                "AD_Client_ID=? AND AD_Org_ID=?"
                        + " AND M_Product_ID=?"
                        + " AND M_AttributeSetInstance_ID=?"
                        + " AND M_CostType_ID=? AND C_AcctSchema_ID=?"
                        + " AND M_CostElement_ID=?";
        cost =
                new Query<I_M_Cost>(I_M_Cost.Table_Name, whereClause)
                        .setParameters(
                                product.getClientId(),
                                AD_Org_ID,
                                product.getProductId(),
                                M_AttributeSetInstance_ID,
                                as.getCostTypeId(),
                                as.getAccountingSchemaId(),
                                M_CostElement_ID)
                        .firstOnly();
        // FR: [ 2214883 ] - end -
        //	New
        if (cost == null)
            cost = new MCost(product, M_AttributeSetInstance_ID, as, AD_Org_ID, M_CostElement_ID);
        return cost;
    } //	get

    /**
     * Get Cost Record
     *
     * @param AD_Client_ID              client
     * @param AD_Org_ID                 org
     * @param M_Product_ID              product
     * @param M_CostType_ID             cost type
     * @param C_AcctSchema_ID           as
     * @param M_CostElement_ID          cost element
     * @param M_AttributeSetInstance_ID asi
     * @return cost or null
     */
    public static I_M_Cost get(
            int AD_Client_ID,
            int AD_Org_ID,
            int M_Product_ID,
            int M_CostType_ID,
            int C_AcctSchema_ID,
            int M_CostElement_ID,
            int M_AttributeSetInstance_ID) {
        final String whereClause =
                "AD_Client_ID=? AND AD_Org_ID=?"
                        + " AND "
                        + I_M_Cost.COLUMNNAME_M_Product_ID
                        + "=?"
                        + " AND "
                        + I_M_Cost.COLUMNNAME_M_CostType_ID
                        + "=?"
                        + " AND "
                        + I_M_Cost.COLUMNNAME_C_AcctSchema_ID
                        + "=?"
                        + " AND "
                        + I_M_Cost.COLUMNNAME_M_CostElement_ID
                        + "=?"
                        + " AND "
                        + I_M_Cost.COLUMNNAME_M_AttributeSetInstance_ID
                        + "=?";
        final Object[] params =
                new Object[]{
                        AD_Client_ID,
                        AD_Org_ID,
                        M_Product_ID,
                        M_CostType_ID,
                        C_AcctSchema_ID,
                        M_CostElement_ID,
                        M_AttributeSetInstance_ID
                };
        return new Query<I_M_Cost>(I_M_Cost.Table_Name, whereClause)
                .setOnlyActiveRecords(true)
                .setParameters(params)
                .firstOnly();
    } //	get

    /**
     * Add Cumulative Amt/Qty and Current Qty
     *
     * @param amt amt
     * @param qty qty
     */
    public void add(BigDecimal amt, BigDecimal qty) {
        MCostElement costElement = (MCostElement) getCostElement();
        if (costElement.isAveragePO() || costElement.isAverageInvoice()) {
            if (getCurrentQty().add(qty).signum() < 0) {
                throw new AverageCostingNegativeQtyException(
                        "Product(ID)="
                                + getProductId()
                                + ", Current Qty="
                                + getCurrentQty()
                                + ", Trx Qty="
                                + qty
                                + ", CostElement="
                                + costElement.getName()
                                + ", Schema="
                                + getAccountingSchema().getName());
            }
        }
        setCumulatedAmt(getCumulatedAmt().add(amt));
        setCumulatedQty(getCumulatedQty().add(qty));
        setCurrentQty(getCurrentQty().add(qty));
    } //	add

    /**
     * Add Amt/Qty and calculate weighted average. ((OldAvg*OldQty)+(Price*Qty)) / (OldQty+Qty)
     *
     * @param amt total amt (price * qty)
     * @param qty qty
     */
    public void setWeightedAverage(BigDecimal amt, BigDecimal qty) {
        // amount must follow the sign of qty
        if (amt.signum() != 0 && qty.signum() != 0 && amt.signum() != qty.signum()) {
            amt = amt.negate();
        }

        // can't do cost adjustment if there's no stock left
        if (qty.signum() == 0 && getCurrentQty().signum() <= 0) {
            throw new AverageCostingZeroQtyException(
                    "Product(ID)="
                            + getProductId()
                            + ", Current Qty="
                            + getCurrentQty()
                            + ", Trx Qty="
                            + qty
                            + ", CostElement="
                            + getCostElement().getName()
                            + ", Schema="
                            + getAccountingSchema().getName());
        }

        if (getCurrentQty().add(qty).signum() < 0) {
            throw new AverageCostingNegativeQtyException(
                    "Product(ID)="
                            + getProductId()
                            + ", Current Qty="
                            + getCurrentQty()
                            + ", Trx Qty="
                            + qty
                            + ", CostElement="
                            + getCostElement().getName()
                            + ", Schema="
                            + getAccountingSchema().getName());
        }

        BigDecimal sumQty = getCurrentQty().add(qty);
        if (sumQty.signum() != 0) {
            BigDecimal oldSum = getCurrentCostPrice().multiply(getCurrentQty());
            BigDecimal oldCost = oldSum.divide(sumQty, 12, BigDecimal.ROUND_HALF_UP);
            BigDecimal newCost = amt.divide(sumQty, 12, BigDecimal.ROUND_HALF_UP); // amt is total already
            BigDecimal cost = oldCost.add(newCost);
            if (cost.scale() > (getPrecision() * 2)) {
                cost = cost.setScale((getPrecision() * 2), BigDecimal.ROUND_HALF_UP);
            }
            setCurrentCostPrice(cost);
        }
        //
        setCumulatedAmt(getCumulatedAmt().add(amt));
        setCumulatedQty(getCumulatedQty().add(qty));
        setCurrentQty(getCurrentQty().add(qty));
    } //	setWeightedAverage

    /**
     *
     */
    public void setWeightedAverageInitial(BigDecimal amtUnit) {
        BigDecimal cost = amtUnit;
        if (cost.scale() > (getPrecision() * 2)) {
            cost = cost.setScale((getPrecision() * 2), BigDecimal.ROUND_HALF_UP);
        }
        setCurrentCostPrice(cost);
    } //	setWeightedAverageInitial

    /**
     * Get Costing Precision
     *
     * @return precision (6)
     */
    private int getPrecision() {
        MAcctSchema as = MAcctSchema.get(getAccountingSchemaId());
        if (as != null) return as.getCostingPrecision();
        return 6;
    } //	gerPrecision

    /**
     * Set Current Cost Price
     *
     * @param currentCostPrice if null set to 0
     */
    public void setCurrentCostPrice(BigDecimal currentCostPrice) {
        if (currentCostPrice != null) super.setCurrentCostPrice(currentCostPrice);
        else super.setCurrentCostPrice(Env.ZERO);
    } //	setCurrentCostPrice

    /**
     * Get History Average (Amt/Qty)
     *
     * @return average if amt/aty <> 0 otherwise null
     */
    public BigDecimal getHistoryAverage() {
        BigDecimal retValue = null;
        if (getCumulatedQty().signum() != 0 && getCumulatedAmt().signum() != 0)
            retValue =
                    getCumulatedAmt().divide(getCumulatedQty(), getPrecision(), BigDecimal.ROUND_HALF_UP);
        return retValue;
    } //	getHistoryAverage

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MCost[");
        sb.append("AD_Client_ID=").append(getClientId());
        if (getOrgId() != 0) sb.append(",AD_Org_ID=").append(getOrgId());
        sb.append(",M_Product_ID=").append(getProductId());
        if (getAttributeSetInstanceId() != 0)
            sb.append(",AD_ASI_ID=").append(getAttributeSetInstanceId());
        //	sb.append (",C_AcctSchema_ID=").append (getAccountingSchemaId());
        //	sb.append (",M_CostType_ID=").append (getCostTypeId());
        sb.append(",M_CostElement_ID=").append(getCostElementId());
        //
        sb.append(", CurrentCost=")
                .append(getCurrentCostPrice())
                .append(", C.Amt=")
                .append(getCumulatedAmt())
                .append(",C.Qty=")
                .append(getCumulatedQty())
                .append("]");
        return sb.toString();
    } //	toString

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true if can be saved
     */
    protected boolean beforeSave(boolean newRecord) {
        // The method getCostElement() not should be cached because is a transaction
        // MCostElement ce = getCostElement();
        MCostElement ce = (MCostElement) getCostElement();
        //	Check if data entry makes sense
        if (m_manual) {
            MAcctSchema as = new MAcctSchema(getAccountingSchemaId());
            MProduct product = MProduct.get(getProductId());
            String CostingLevel = product.getCostingLevel(as);
            if (MAcctSchema.COSTINGLEVEL_Client.equals(CostingLevel)) {
                if (getOrgId() != 0 || getAttributeSetInstanceId() != 0) {
                    log.saveError("CostingLevelClient", "");
                    return false;
                }
            } else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(CostingLevel)) {
                if (getAttributeSetInstanceId() == 0 && ce.isCostingMethod()) {
                    log.saveError("FillMandatory", MsgKt.getElementTranslation("M_AttributeSetInstance_ID"));
                    return false;
                }
                if (getOrgId() != 0) setOrgId(0);
            }
        }

        //	Cannot enter calculated
        if (m_manual && ce != null && ce.isCalculated()) {
            log.saveError("Error", MsgKt.getElementTranslation("IsCalculated"));
            return false;
        }
        //	Percentage
        if (ce != null) {
            if (ce.isCalculated()
                    || MCostElement.COSTELEMENTTYPE_Material.equals(ce.getCostElementType())
                    && getPercent() != 0) setPercent(0);
        }
        if (getPercent() != 0) {
            if (getCurrentCostPrice().signum() != 0) setCurrentCostPrice(Env.ZERO);
            if (getFutureCostPrice().signum() != 0) setFutureCostPrice(Env.ZERO);
            if (getCumulatedAmt().signum() != 0) setCumulatedAmt(Env.ZERO);
            if (getCumulatedQty().signum() != 0) setCumulatedQty(Env.ZERO);
        }

        // -ve current qty will break moving average costing
        if (ce != null
                && (ce.isAveragePO() || ce.isAverageInvoice())
                && isValueChanged(I_M_Cost.COLUMNNAME_CurrentQty)) {
            if (getCurrentQty().signum() < 0) {
                throw new AverageCostingNegativeQtyException(
                        "Product(ID)="
                                + getProductId()
                                + ", Current Qty="
                                + getCurrentQty()
                                + ", CostElement="
                                + getCostElement().getName()
                                + ", Schema="
                                + getAccountingSchema().getName());
            }
        }

        return true;
    } //	beforeSave

    /**
     * Before Delete
     *
     * @return true
     */
    protected boolean beforeDelete() {
        return true;
    } //	beforeDelete

    @Override
    public void setCurrentQty(BigDecimal CurrentQty) {
        MCostElement ce = (MCostElement) getCostElement();
        if (CurrentQty.signum() < 0) {
            if (ce.isAveragePO() || ce.isAverageInvoice()) {
                throw new AverageCostingNegativeQtyException(
                        "Product="
                                + getProduct().getName()
                                + ", Current Qty="
                                + getCurrentQty()
                                + ", New Current Qty="
                                + CurrentQty
                                + ", CostElement="
                                + ce.getName()
                                + ", Schema="
                                + getAccountingSchema().getName());
            }
        }
        super.setCurrentQty(CurrentQty);
    }

} //	MCost
