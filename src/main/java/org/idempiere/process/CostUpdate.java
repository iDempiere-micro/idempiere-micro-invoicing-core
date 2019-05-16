package org.idempiere.process;

import org.compiere.accounting.MAcctSchema;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MCost;
import org.compiere.accounting.MCostElement;
import org.compiere.accounting.MProduct;
import org.compiere.docengine.DocumentEngine;
import org.compiere.invoicing.MInventory;
import org.compiere.invoicing.MInventoryLine;
import org.compiere.model.AccountingSchema;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.HasName;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_M_Cost;
import org.compiere.model.I_M_CostElement;
import org.compiere.model.I_M_Inventory;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.process.DocAction;
import org.compiere.process.SvrProcess;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.AdempiereSystemError;
import org.idempiere.common.util.AdempiereUserError;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Standard Cost Update
 *
 * @author Jorg Janke
 * @version $Id: CostUpdate.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class CostUpdate extends SvrProcess {
    private static final String TO_AveragePO = "A";
    private static final String TO_AverageInvoiceHistory = "DI";
    private static final String TO_AveragePOHistory = "DP";
    private static final String TO_FiFo = "F";
    private static final String TO_AverageInvoice = "I";
    private static final String TO_LiFo = "L";
    private static final String TO_PriceListLimit = "LL";
    private static final String TO_StandardCost = "S";
    private static final String TO_FutureStandardCost = "f";
    private static final String TO_LastInvoicePrice = "i";
    private static final String TO_LastPOPrice = "p";
    /**
     * Product Category
     */
    private int p_M_Product_Category_ID = 0;
    /**
     * Future Costs
     */
    private String p_SetFutureCostTo = null;
    /**
     * Standard Costs
     */
    private String p_SetStandardCostTo = null;
    /**
     * PLV
     */
    private int p_M_PriceList_Version_ID = 0;
    private int p_C_DocType_ID = 0;
    /**
     * Standard Cost Element
     */
    private I_M_CostElement m_ce = null;
    /**
     * Client Accounting SChema
     */
    private AccountingSchema[] m_ass = null;
    /**
     * Map of Cost Elements
     */
    private HashMap<String, I_M_CostElement> m_ces = new HashMap<>();

    private MDocType m_docType = null;

    /**
     * Prepare
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            //	log.fine("prepare - " + para[i]);

            switch (name) {
                case "M_Product_Category_ID":
                    p_M_Product_Category_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "SetFutureCostTo":
                    p_SetFutureCostTo = (String) iProcessInfoParameter.getParameter();
                    break;
                case "SetStandardCostTo":
                    p_SetStandardCostTo = (String) iProcessInfoParameter.getParameter();
                    break;
                case "M_PriceList_Version_ID":
                    p_M_PriceList_Version_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "C_DocType_ID":
                    p_C_DocType_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }
        }
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "M_Product_Category_ID="
                            + p_M_Product_Category_ID
                            + ", Future="
                            + p_SetFutureCostTo
                            + ", Standard="
                            + p_SetStandardCostTo
                            + "; M_PriceList_Version_ID="
                            + p_M_PriceList_Version_ID);
        if (p_SetFutureCostTo == null) p_SetFutureCostTo = "";
        if (p_SetStandardCostTo == null) p_SetStandardCostTo = "";
        //	Nothing to Do
        if (p_SetFutureCostTo.length() == 0 && p_SetStandardCostTo.length() == 0) {
            return "-";
        }
        if (!Util.isEmpty(p_SetStandardCostTo)) {
            if (p_C_DocType_ID <= 0) throw new AdempiereUserError("@FillMandatory@  @C_DocType_ID@");
            else m_docType = MDocTypeKt.getDocumentType(p_C_DocType_ID);
        }
        //	PLV required
        if (p_M_PriceList_Version_ID == 0
                && (p_SetFutureCostTo.equals(TO_PriceListLimit)
                || p_SetStandardCostTo.equals(TO_PriceListLimit)))
            throw new AdempiereUserError("@FillMandatory@  @M_PriceList_Version_ID@");

        //	Validate Source
        if (!isValid(p_SetFutureCostTo))
            throw new AdempiereUserError("@NotFound@ @M_CostElement_ID@ (Future) " + p_SetFutureCostTo);
        if (!isValid(p_SetStandardCostTo))
            throw new AdempiereUserError(
                    "@NotFound@ @M_CostElement_ID@ (Standard) " + p_SetStandardCostTo);

        //	Prepare
        ClientWithAccounting client = MClientKt.getClientWithAccounting();
        m_ce = MCostElement.getMaterialCostElement(client, MAcctSchema.COSTINGMETHOD_StandardCosting);
        if (m_ce.getId() == 0) throw new AdempiereUserError("@NotFound@ @M_CostElement_ID@ (StdCost)");
        if (log.isLoggable(Level.CONFIG)) log.config(m_ce.toString());
        m_ass = MAcctSchema.getClientAcctSchema(client.getClientId());
        for (AccountingSchema m_ass1 : m_ass) createNew(m_ass1);

        //	Update Cost
        int counter = update();

        return "#" + counter;
    } //	doIt

    /**
     * Costing Method must exist
     *
     * @param to test
     * @return true valid
     */
    private boolean isValid(String to) {
        if (p_SetFutureCostTo.length() == 0) return true;

        if (to.equals(TO_AverageInvoiceHistory)) to = TO_AverageInvoice;
        if (to.equals(TO_AveragePOHistory)) to = TO_AveragePO;
        if (to.equals(TO_FutureStandardCost)) to = TO_StandardCost;
        //
        if (to.equals(TO_AverageInvoice)
                || to.equals(TO_AveragePO)
                || to.equals(TO_FiFo)
                || to.equals(TO_LiFo)
                || to.equals(TO_StandardCost)) {
            I_M_CostElement ce = getCostElement(p_SetFutureCostTo);
            return ce != null;
        }
        return true;
    } //	isValid

    /**
     * ************************************************************************ Create New Standard
     * Costs
     *
     * @param as accounting schema
     */
    private void createNew(AccountingSchema as) {
        if (!as.getCostingLevel().equals(MAcctSchema.COSTINGLEVEL_Client)) {
            String txt = "Costing Level prevents creating new Costing records for " + as.getName();
            log.warning(txt);
            addLog(0, null, null, txt);
            return;
        }

        int counter = 0;

        MProduct[] items =
                BaseCostUpdateKt.getProductsToCreateNewStandardCosts(as, p_M_Product_Category_ID, m_ce.getCostElementId());

        for (MProduct product : items) {
            if (createNew(product, as)) counter++;
        }

        if (log.isLoggable(Level.INFO)) log.info("#" + counter);
        addLog(0, null, new BigDecimal(counter), "Created for " + as.getName());
    } //	createNew

    /**
     * Create New Client level Costing Record
     *
     * @param product product
     * @param as      acct schema
     * @return true if created
     */
    private boolean createNew(MProduct product, AccountingSchema as) {
        I_M_Cost cost = MCost.get(product, 0, as, 0, m_ce.getCostElementId());
        if (cost.isNew()) return cost.save();
        return false;
    } //	createNew

    /**
     * ************************************************************************ Update Cost Records
     *
     * @return no updated
     */
    private int update() throws Exception {
        int counter = 0;
        String sql = "SELECT * FROM M_Cost c WHERE M_CostElement_ID=?";
        if (p_M_Product_Category_ID != 0)
            sql +=
                    " AND EXISTS (SELECT * FROM M_Product p "
                            + "WHERE c.M_Product_ID=p.M_Product_ID AND p.M_Product_Category_ID=?)";
        List<MInventoryLine> lines = new ArrayList<>();
        ClientWithAccounting client = MClientKt.getClientWithAccounting();
        AccountingSchema primarySchema = client.getAcctSchema();
        MInventory inventoryDoc;

        MCost[] items = BaseCostUpdateKt.getCostsToUpdate(sql, m_ce.getCostElementId(), p_M_Product_Category_ID);

        for (MCost cost : items) {
            for (AccountingSchema m_ass1 : m_ass) {
                //	Update Costs only for default Cost Type
                if (m_ass1.getAccountingSchemaId() == cost.getAccountingSchemaId()
                        && m_ass1.getCostTypeId() == cost.getCostTypeId()) {
                    if (m_ass1.getAccountingSchemaId() == primarySchema.getAccountingSchemaId()) {
                        if (update(cost, lines)) counter++;
                    } else {
                        if (update(cost, null)) counter++;
                    }
                }
            }
        }
        if (lines.size() > 0) {
            inventoryDoc = new MInventory(0);
            inventoryDoc.setDocumentTypeId(p_C_DocType_ID);
            inventoryDoc.setCostingMethod(MCostElement.COSTINGMETHOD_StandardCosting);
            inventoryDoc.setDocAction(DocAction.Companion.getACTION_Complete());
            inventoryDoc.saveEx();

            for (MInventoryLine line : lines) {
                line.setInventoryId(inventoryDoc.getInventoryId());
                line.saveEx();
            }

            if (!DocumentEngine.processIt(inventoryDoc, DocAction.Companion.getACTION_Complete())) {
                StringBuilder msg = new StringBuilder();
                msg.append(MsgKt.getMsg("ProcessFailed")).append(": ");
                if (Env.isBaseLanguage()) msg.append(m_docType.getName());
                else msg.append(m_docType.getTranslation(HasName.COLUMNNAME_Name));
                throw new AdempiereUserError(msg.toString());
            } else {
                inventoryDoc.saveEx();
                StringBuilder msg = new StringBuilder();
                if (Env.isBaseLanguage())
                    msg.append(m_docType.getName()).append(" ").append(inventoryDoc.getDocumentNo());
                else
                    msg.append(m_docType.getTranslation(HasName.COLUMNNAME_Name))
                            .append(" ")
                            .append(inventoryDoc.getDocumentNo());
                addBufferLog(
                        getProcessInstanceId(),
                        null,
                        null,
                        msg.toString(),
                        I_M_Inventory.Table_ID,
                        inventoryDoc.getInventoryId());
            }
        }

        if (log.isLoggable(Level.INFO)) log.info("#" + counter);
        addLog(0, null, new BigDecimal(counter), "@Updated@");
        return counter;
    } //	update

    /**
     * Update Cost Records
     *
     * @param cost cost
     * @return true if updated
     * @throws Exception
     */
    private boolean update(MCost cost, List<MInventoryLine> lines) throws Exception {
        boolean updated = false;
        if (p_SetFutureCostTo.equals(p_SetStandardCostTo)) {
            BigDecimal costs = getCosts(cost, p_SetFutureCostTo);
            if (costs != null && costs.signum() != 0) {
                cost.setFutureCostPrice(costs);
                updated = true;

                if (lines != null) {
                    BigDecimal currentCost = cost.getCurrentCostPrice();
                    if (currentCost == null || currentCost.compareTo(costs) != 0) {
                        MInventoryLine line = new MInventoryLine(0);
                        line.setProductId(cost.getProductId());
                        line.setCurrentCostPrice(cost.getCurrentCostPrice());
                        line.setNewCostPrice(costs);
                        line.setLocatorId(0);
                        lines.add(line);
                    }
                }
            }
        } else {
            if (p_SetStandardCostTo.length() > 0) {
                if (lines != null) {
                    BigDecimal costs = getCosts(cost, p_SetStandardCostTo);
                    if (costs != null && costs.signum() != 0) {
                        BigDecimal currentCost = cost.getCurrentCostPrice();
                        if (currentCost == null || currentCost.compareTo(costs) != 0) {
                            MInventoryLine line = new MInventoryLine(0);
                            line.setProductId(cost.getProductId());
                            line.setCurrentCostPrice(cost.getCurrentCostPrice());
                            line.setNewCostPrice(costs);
                            line.setLocatorId(0);
                            lines.add(line);
                            updated = true;
                        }
                    }
                }
            }
            if (p_SetFutureCostTo.length() > 0) {
                BigDecimal costs = getCosts(cost, p_SetFutureCostTo);
                if (costs != null && costs.signum() != 0) {
                    cost.setFutureCostPrice(costs);
                    updated = true;
                }
            }
        }
        if (updated) updated = cost.save();
        return updated;
    } //	update

    /**
     * Get Costs
     *
     * @param cost cost
     * @param to   where to get costs from
     * @return costs (could be 0) or null if not found
     * @throws Exception
     */
    private BigDecimal getCosts(MCost cost, String to) throws Exception {
        BigDecimal retValue = null;

        //	Average Invoice
        switch (to) {
            case TO_AverageInvoice: {
                I_M_CostElement ce = getCostElement(TO_AverageInvoice);
                if (ce == null) throw new AdempiereSystemError("CostElement not found: " + TO_AverageInvoice);
                I_M_Cost xCost =
                        MCost.get(
                                cost.getClientId(),
                                cost.getOrgId(),
                                cost.getProductId(),
                                cost.getCostTypeId(),
                                cost.getAccountingSchemaId(),
                                ce.getCostElementId(),
                                cost.getAttributeSetInstanceId()
                        );
                if (xCost != null) retValue = xCost.getCurrentCostPrice();
                break;
            }
            //	Average Invoice History
            case TO_AverageInvoiceHistory: {
                I_M_CostElement ce = getCostElement(TO_AverageInvoice);
                if (ce == null) throw new AdempiereSystemError("CostElement not found: " + TO_AverageInvoice);
                I_M_Cost xCost =
                        MCost.get(
                                cost.getClientId(),
                                cost.getOrgId(),
                                cost.getProductId(),
                                cost.getCostTypeId(),
                                cost.getAccountingSchemaId(),
                                ce.getCostElementId(),
                                cost.getAttributeSetInstanceId()
                        );
                if (xCost != null) retValue = xCost.getHistoryAverage();
                break;
            }

            //	Average PO
            case TO_AveragePO: {
                I_M_CostElement ce = getCostElement(TO_AveragePO);
                if (ce == null) throw new AdempiereSystemError("CostElement not found: " + TO_AveragePO);
                I_M_Cost xCost =
                        MCost.get(
                                cost.getClientId(),
                                cost.getOrgId(),
                                cost.getProductId(),
                                cost.getCostTypeId(),
                                cost.getAccountingSchemaId(),
                                ce.getCostElementId(),
                                cost.getAttributeSetInstanceId()
                        );
                if (xCost != null) retValue = xCost.getCurrentCostPrice();
                break;
            }
            //	Average PO History
            case TO_AveragePOHistory: {
                I_M_CostElement ce = getCostElement(TO_AveragePO);
                if (ce == null) throw new AdempiereSystemError("CostElement not found: " + TO_AveragePO);
                I_M_Cost xCost =
                        MCost.get(
                                cost.getClientId(),
                                cost.getOrgId(),
                                cost.getProductId(),
                                cost.getCostTypeId(),
                                cost.getAccountingSchemaId(),
                                ce.getCostElementId(),
                                cost.getAttributeSetInstanceId()
                        );
                if (xCost != null) retValue = xCost.getHistoryAverage();
                break;
            }

            //	FiFo
            case TO_FiFo: {
                I_M_CostElement ce = getCostElement(TO_FiFo);
                if (ce == null) throw new AdempiereSystemError("CostElement not found: " + TO_FiFo);
                I_M_Cost xCost =
                        MCost.get(
                                cost.getClientId(),
                                cost.getOrgId(),
                                cost.getProductId(),
                                cost.getCostTypeId(),
                                cost.getAccountingSchemaId(),
                                ce.getCostElementId(),
                                cost.getAttributeSetInstanceId()
                        );
                if (xCost != null) retValue = xCost.getCurrentCostPrice();
                break;
            }

            //	Future Std Costs
            case TO_FutureStandardCost:
                retValue = cost.getFutureCostPrice();
                break;

            //	Last Inv Price
            case TO_LastInvoicePrice: {
                I_M_CostElement ce = getCostElement(TO_LastInvoicePrice);
                if (ce != null) {
                    I_M_Cost xCost =
                            MCost.get(
                                    cost.getClientId(),
                                    cost.getOrgId(),
                                    cost.getProductId(),
                                    cost.getCostTypeId(),
                                    cost.getAccountingSchemaId(),
                                    ce.getCostElementId(),
                                    cost.getAttributeSetInstanceId()
                            );
                    if (xCost != null) retValue = xCost.getCurrentCostPrice();
                }
                if (retValue == null) {
                    MProduct product = new MProduct(cost.getProductId());
                    MAcctSchema as = MAcctSchema.get(cost.getAccountingSchemaId());
                    retValue =
                            MCost.getLastInvoicePrice(
                                    product,
                                    cost.getAttributeSetInstanceId(),
                                    cost.getOrgId(),
                                    as.getCurrencyId());
                }
                break;
            }

            //	Last PO Price
            case TO_LastPOPrice: {
                I_M_CostElement ce = getCostElement(TO_LastPOPrice);
                if (ce != null) {
                    I_M_Cost xCost =
                            MCost.get(
                                    cost.getClientId(),
                                    cost.getOrgId(),
                                    cost.getProductId(),
                                    cost.getCostTypeId(),
                                    cost.getAccountingSchemaId(),
                                    ce.getCostElementId(),
                                    cost.getAttributeSetInstanceId()
                            );
                    if (xCost != null) retValue = xCost.getCurrentCostPrice();
                }
                if (retValue == null) {
                    MProduct product = new MProduct(cost.getProductId());
                    MAcctSchema as = MAcctSchema.get(cost.getAccountingSchemaId());
                    retValue =
                            MCost.getLastPOPrice(
                                    product,
                                    cost.getAttributeSetInstanceId(),
                                    cost.getOrgId(),
                                    as.getCurrencyId());
                }
                break;
            }

            //	FiFo
            case TO_LiFo: {
                I_M_CostElement ce = getCostElement(TO_LiFo);
                if (ce == null) throw new AdempiereSystemError("CostElement not found: " + TO_LiFo);
                I_M_Cost xCost =
                        MCost.get(
                                cost.getClientId(),
                                cost.getOrgId(),
                                cost.getProductId(),
                                cost.getCostTypeId(),
                                cost.getAccountingSchemaId(),
                                ce.getCostElementId(),
                                cost.getAttributeSetInstanceId()
                        );
                if (xCost != null) retValue = xCost.getCurrentCostPrice();
                break;
            }

            //	Price List
            case TO_PriceListLimit:
                retValue = getPrice(cost);
                break;

            //	Standard Costs
            case TO_StandardCost:
                retValue = cost.getCurrentCostPrice();
                break;
        }

        return retValue;
    } //	getCosts

    /**
     * Get Cost Element
     *
     * @param CostingMethod method
     * @return costing element or null
     */
    private I_M_CostElement getCostElement(String CostingMethod) {
        I_M_CostElement ce = m_ces.get(CostingMethod);
        if (ce == null) {
            ce = MCostElement.getMaterialCostElement(CostingMethod);
            m_ces.put(CostingMethod, ce);
        }
        return ce;
    } //	getCostElement

    /**
     * Get Price from Price List
     *
     * @param cost cost record
     * @return price or null
     */
    private BigDecimal getPrice(MCost cost) {
        BigDecimal retValue = null;
        String sql =
                "SELECT PriceLimit "
                        + "FROM M_ProductPrice "
                        + "WHERE M_Product_ID=? AND M_PriceList_Version_ID=?";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, cost.getProductId());
            pstmt.setInt(2, p_M_PriceList_Version_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retValue = rs.getBigDecimal(1);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        }
        return retValue;
    } //	getPrice
} //	CostUpdate
