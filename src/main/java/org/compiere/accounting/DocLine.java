package org.compiere.accounting;

import org.compiere.model.AccountingSchema;
import org.compiere.model.IDocLine;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_M_CostDetail;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValue;

/**
 * Standard Document Line
 *
 * @author Jorg Janke
 * @author Armen Rizal, Goodwill Consulting
 * <li>BF [ 1745154 ] Cost in Reversing Material Related Docs
 * @version $Id: DocLine.java,v 1.2 2006/07/30 00:53:33 jjanke Exp $
 */
public class DocLine {
    /**
     * Persistent Object
     */
    protected IDocLine p_po = null;
    /**
     * Log
     */
    protected CLogger log = CLogger.getCLogger(getClass());
    /**
     * Parent
     */
    private Doc m_doc = null;
    /**
     * Qty
     */
    private BigDecimal m_qty = null;
    /**
     * Debit Journal Amt
     */
    private BigDecimal m_AmtSourceDr = Env.ZERO;

    //  -- GL Amounts
    /**
     * Credit Journal Amt
     */
    private BigDecimal m_AmtSourceCr = Env.ZERO;
    /**
     * Net Line Amt
     */
    private BigDecimal m_LineNetAmt = null;
    /**
     * List Amount
     */
    private BigDecimal m_ListAmt = Env.ZERO;
    /**
     * Discount Amount
     */
    private BigDecimal m_DiscountAmt = Env.ZERO;
    /**
     * Converted Amounts
     */
    private BigDecimal m_AmtAcctDr = null;
    private BigDecimal m_AmtAcctCr = null;
    /**
     * Acct Schema
     */
    private int m_C_AcctSchema_ID = 0;
    /**
     * Product Costs
     */
    private ProductCost m_productCost = null;
    /**
     * Outside Processing
     */
    private int m_PP_Cost_Collector_ID = 0;
    /**
     * Account used only for GL Journal
     */
    private I_C_ValidCombination m_account = null;
    /**
     * Accounting Date
     */
    private Timestamp m_DateAcct = null;
    /**
     * Document Date
     */
    private Timestamp m_DateDoc = null;
    /**
     * Sales Region
     */
    private int m_C_SalesRegion_ID = -1;
    /**
     * Sales Region
     */
    private int m_C_BPartner_ID = -1;
    /**
     * Location From
     */
    private int m_C_LocFrom_ID = 0;
    /**
     * Location To
     */
    private int m_C_LocTo_ID = 0;
    /**
     * Item
     */
    private Boolean m_isItem = null;
    /**
     * Currency
     */
    private int m_C_Currency_ID = -1;
    /**
     * Conversion Type
     */
    private int m_C_ConversionType_ID = -1;
    /**
     * Period
     */
    private int m_C_Period_ID = -1;
    // AZ Goodwill
    private int m_ReversalLine_ID = 0;

    /**
     * Create Document Line
     *
     * @param po  line persistent object
     * @param doc header
     */
    public DocLine(IDocLine po, Doc doc) {
        if (po == null) throw new IllegalArgumentException("PO is null");
        p_po = po;
        m_doc = doc;
        //
        //  Document Consistency
        if (p_po.getOrgId() == 0) p_po.setOrgId(m_doc.getOrgId());
    } //	DocLine

    /**
     * Get Currency
     *
     * @return currencyId
     */
    public int getCurrencyId() {
        if (m_C_Currency_ID == -1) {
            int index = p_po.getColumnIndex("C_Currency_ID");
            m_C_Currency_ID = getIntValueIfColumnExists(index);
            if (m_C_Currency_ID <= 0) m_C_Currency_ID = m_doc.getCurrencyId();
        }
        return m_C_Currency_ID;
    } //  getCurrencyId

    /**
     * Get Conversion Type
     *
     * @return C_ConversionType_ID
     */
    public int getConversionTypeId() {
        if (m_C_ConversionType_ID == -1) {
            int index = p_po.getColumnIndex("C_ConversionType_ID");
            m_C_ConversionType_ID = getIntValueIfColumnExists(index);
            if (m_C_ConversionType_ID <= 0) m_C_ConversionType_ID = m_doc.getConversionTypeId();
        }
        return m_C_ConversionType_ID;
    } //  getConversionTypeId

    /**
     * Set C_ConversionType_ID
     *
     * @param C_ConversionType_ID id
     */
    protected void setConversionTypeId(int C_ConversionType_ID) {
        m_C_ConversionType_ID = C_ConversionType_ID;
    } //	setConversionTypeId

    /**
     * Set Amount (DR)
     *
     * @param sourceAmt source amt
     */
    public void setAmount(BigDecimal sourceAmt) {
        m_AmtSourceDr = sourceAmt == null ? Env.ZERO : sourceAmt;
        m_AmtSourceCr = Env.ZERO;
    } //  setAmounts

    /**
     * Set Amounts
     *
     * @param amtSourceDr source amount dr
     * @param amtSourceCr source amount cr
     */
    public void setAmount(BigDecimal amtSourceDr, BigDecimal amtSourceCr) {
        m_AmtSourceDr = amtSourceDr == null ? Env.ZERO : amtSourceDr;
        m_AmtSourceCr = amtSourceCr == null ? Env.ZERO : amtSourceCr;
    } //  setAmounts

    /**
     * Set Converted Amounts
     *
     * @param C_AcctSchema_ID acct schema
     * @param amtAcctDr       acct amount dr
     * @param amtAcctCr       acct amount cr
     */
    public void setConvertedAmt(int C_AcctSchema_ID, BigDecimal amtAcctDr, BigDecimal amtAcctCr) {
        m_C_AcctSchema_ID = C_AcctSchema_ID;
        m_AmtAcctDr = amtAcctDr;
        m_AmtAcctCr = amtAcctCr;
    } //  setConvertedAmt

    /**
     * Line Net Amount or Dr-Cr
     *
     * @return balance
     */
    public BigDecimal getAmtSource() {
        return m_AmtSourceDr.subtract(m_AmtSourceCr);
    } //  getAmount

    /**
     * Get (Journal) Line Source Dr Amount
     *
     * @return DR source amount
     */
    public BigDecimal getAmtSourceDr() {
        return m_AmtSourceDr;
    } //  getAmtSourceDr

    /**
     * Get (Journal) Line Source Cr Amount
     *
     * @return CR source amount
     */
    public BigDecimal getAmtSourceCr() {
        return m_AmtSourceCr;
    } //  getAmtSourceCr

    /**
     * Line Journal Accounted Dr Amount
     *
     * @return DR accounted amount
     */
    public BigDecimal getAmtAcctDr() {
        return m_AmtAcctDr;
    } //  getAmtAcctDr

    /**
     * Line Journal Accounted Cr Amount
     *
     * @return CR accounted amount
     */
    public BigDecimal getAmtAcctCr() {
        return m_AmtAcctCr;
    } //  getAmtAccrCr

    /**
     * Charge Amount
     *
     * @return charge amount
     */
    public BigDecimal getChargeAmt() {
        int index = p_po.getColumnIndex("ChargeAmt");
        if (index != -1) {
            BigDecimal bd = (BigDecimal) p_po.getValue(index);
            if (bd != null) return bd;
        }
        return Env.ZERO;
    } //  getChargeAmt

    /**
     * Set Product Amounts
     *
     * @param LineNetAmt Line Net Amt
     * @param PriceList  Price List
     * @param Qty        Qty for discount calc
     */
    public void setAmount(BigDecimal LineNetAmt, BigDecimal PriceList, BigDecimal Qty) {
        m_LineNetAmt = LineNetAmt == null ? Env.ZERO : LineNetAmt;

        if (PriceList != null && Qty != null) m_ListAmt = PriceList.multiply(Qty);
        if (m_ListAmt.compareTo(Env.ZERO) == 0) m_ListAmt = m_LineNetAmt;
        m_DiscountAmt = m_ListAmt.subtract(m_LineNetAmt);
        //
        setAmount(m_ListAmt, m_DiscountAmt);
        //	Log.trace(this,Log.l6_Database, "DocLine_Invoice.setAmount",
        //		"LineNet=" + m_LineNetAmt + ", List=" + m_ListAmt + ", Discount=" + m_DiscountAmt
        //		+ " => Amount=" + getAmount());
    } //  setAmounts

    /**
     * Line Discount
     *
     * @return discount amount
     */
    public BigDecimal getDiscount() {
        return m_DiscountAmt;
    } //  getDiscount

    /**
     * Set Line Net Amt Difference
     *
     * @param diff difference (to be subtracted)
     */
    public void setLineNetAmtDifference(BigDecimal diff) {
        String msg = "Diff=" + diff + " - LineNetAmt=" + m_LineNetAmt;
        m_LineNetAmt = m_LineNetAmt.subtract(diff);
        m_DiscountAmt = m_ListAmt.subtract(m_LineNetAmt);
        setAmount(m_ListAmt, m_DiscountAmt);
        msg += " -> " + m_LineNetAmt;
        log.fine(msg);
    } //	setLineNetAmtDifference

    /**
     * Get Accounting Date
     *
     * @return accounting date
     */
    public Timestamp getDateAcct() {
        if (m_DateAcct != null) return m_DateAcct;
        int index = p_po.getColumnIndex("DateAcct");
        if (index != -1) {
            m_DateAcct = (Timestamp) p_po.getValue(index);
            if (m_DateAcct != null) return m_DateAcct;
        }
        m_DateAcct = m_doc.getDateAcct();
        return m_DateAcct;
    } //  getDateAcct

    /**
     * ************************************************************************ Set Accounting Date
     *
     * @param dateAcct acct date
     */
    public void setDateAcct(Timestamp dateAcct) {
        m_DateAcct = dateAcct;
    } //  setDateAcct

    /**
     * Get FX Conversion Date
     *
     * <p>The foreign exchange rate conversion date may be different from the accounting posting date
     * in some cases (e.g. bank statement)
     *
     * @return FX conversion date
     */
    public Timestamp getDateConv() {
        Timestamp dateConv = null;
        int index = p_po.getColumnIndex("DateAcct");
        if (index != -1) {
            dateConv = (Timestamp) p_po.getValue(index);
        }

        if (dateConv == null) dateConv = getDateAcct();

        return dateConv;
    } //  getDateAcct

    /**
     * Get Document Date
     *
     * @return document date
     */
    public Timestamp getDateDoc() {
        if (m_DateDoc != null) return m_DateDoc;
        int index = p_po.getColumnIndex("DateDoc");
        if (index != -1) {
            m_DateDoc = (Timestamp) p_po.getValue(index);
            if (m_DateDoc != null) return m_DateDoc;
        }
        m_DateDoc = m_doc.getDateDoc();
        return m_DateDoc;
    } //  getDateDoc

    /**
     * Set Document Date
     *
     * @param dateDoc doc date
     */
    public void setDateDoc(Timestamp dateDoc) {
        m_DateDoc = dateDoc;
    } //  setDateDoc

    /**
     * Get GL Journal Account
     *
     * @return account
     */
    public I_C_ValidCombination getAccount() {
        return m_account;
    } //  getAccount

    /**
     * ************************************************************************ Set GL Journal Account
     *
     * @param acct account
     */
    public void setAccount(I_C_ValidCombination acct) {
        m_account = acct;
    } //  setAccount

    /**
     * Line Account from Product (or Charge).
     *
     * @param AcctType see ProductCost.ACCTTYPE_* (0..3)
     * @param as       Accounting schema
     * @return Requested Product Account
     */
    public I_C_ValidCombination getAccount(int AcctType, AccountingSchema as) {
        //	Charge Account
        if (getProductId() == 0 && getChargeId() != 0) {
            BigDecimal amt = new BigDecimal(-1); // 	Revenue (-)
            if (!m_doc.isSOTrx()) amt = new BigDecimal(+1); // 	Expense (+)
            I_C_ValidCombination acct = getChargeAccount(as);
            if (acct != null) return acct;
        }
        //	Product Account
        return getProductCost().getAccount(AcctType, as);
    } //  getAccount

    /**
     * Get Charge
     *
     * @return C_Charge_ID
     */
    public int getChargeId() {
        int index = p_po.getColumnIndex("C_Charge_ID");
        return getIntValueIfColumnExists(index);
    } //	getChargeId

    private int getIntValueIfColumnExists(int index) {
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    }

    /**
     * Get Charge Account
     *
     * @param as     account schema
     * @return Charge Account or null
     */
    public I_C_ValidCombination getChargeAccount(AccountingSchema as) {
        int C_Charge_ID = getChargeId();
        if (C_Charge_ID == 0) return null;
        return MCharge.getAccount(C_Charge_ID, as);
    } //  getChargeAccount

    /**
     * Get Period
     *
     * @return C_Period_ID
     */
    protected int getPeriodId() {
        if (m_C_Period_ID == -1) {
            int index = p_po.getColumnIndex("C_Period_ID");
            m_C_Period_ID = getIntValueIfColumnExists(index);
            if (m_C_Period_ID == -1) m_C_Period_ID = 0;
        }
        return m_C_Period_ID;
    } //	getPeriodId

    /**
     * ************************************************************************ Get (Journal)
     * AcctSchema
     *
     * @return C_AcctSchema_ID
     */
    public int getAccountingSchemaId() {
        return m_C_AcctSchema_ID;
    } //  getAccountingSchemaId

    /**
     * Get Line ID
     *
     * @return id
     */
    public int getId() {
        return p_po.getId();
    } //	getId

    /**
     * Get orgId
     *
     * @return org
     */
    public int getOrgId() {
        return p_po.getOrgId();
    } //	getOrgId

    /**
     * Get Order orgId
     *
     * @return order org if defined
     */
    public int getOrderOrgId() {
        int C_OrderLine_ID = getOrderLineId();
        if (C_OrderLine_ID != 0) {
            String sql = "SELECT ad_org_id FROM C_OrderLine WHERE C_OrderLine_ID=?";
            int AD_Org_ID = getSQLValue(sql, C_OrderLine_ID);
            if (AD_Org_ID > 0) return AD_Org_ID;
        }
        return getOrgId();
    } //	getOrderOrg_ID

    /**
     * Product
     *
     * @return M_Product_ID
     */
    public int getProductId() {
        int index = p_po.getColumnIndex("M_Product_ID");
        return getIntValueIfColumnExists(index);
    } //  getProductId

    /**
     * Is this an Item Product (vs. not a Service, a charge)
     *
     * @return true if product
     */
    public boolean isItem() {
        if (m_isItem != null) return m_isItem;

        m_isItem = Boolean.FALSE;
        if (getProductId() != 0) {
            org.compiere.product.MProduct product = MProduct.get(getProductId());
            if (product.getId() == getProductId() && product.isItem()) m_isItem = Boolean.TRUE;
        }
        return m_isItem;
    } //	isItem

    /**
     * ASI
     *
     * @return M_AttributeSetInstance_ID
     */
    public int getAttributeSetInstanceId() {
        int index = p_po.getColumnIndex("M_AttributeSetInstance_ID");
        return getIntValueIfColumnExists(index);
    } //  getAttributeSetInstanceId

    /**
     * Get Warehouse Locator (from)
     *
     * @return M_Locator_ID
     */
    public int getLocatorId() {
        int index = p_po.getColumnIndex("M_Locator_ID");
        return getIntValueIfColumnExists(index);
    } //  getLocatorId

    /**
     * Get Order Line Reference
     *
     * @return C_OrderLine_ID
     */
    public int getOrderLineId() {
        int index = p_po.getColumnIndex("C_OrderLine_ID");
        return getIntValueIfColumnExists(index);
    } //  getOrderLineId

    /**
     * Get C_LocFrom_ID
     *
     * @return loc from
     */
    public int getLocationFromId() {
        return m_C_LocFrom_ID;
    } //	getLocationFromId

    /**
     * Get PP_Cost_Collector_ID
     *
     * @return Cost Collector ID
     */
    public int getCostCollectorId() {
        return m_PP_Cost_Collector_ID;
    } //	getLocationFromId

    /**
     * Get PP_Cost_Collector_ID
     *
     * @return Cost Collector ID
     */
    public void setCostCollectorId(int PP_Cost_Collector_ID) {
        m_PP_Cost_Collector_ID = PP_Cost_Collector_ID;
    } //	getLocationFromId

    /**
     * Get C_LocTo_ID
     *
     * @return loc to
     */
    public int getLocationToId() {
        return m_C_LocTo_ID;
    } //	getLocationToId

    // MZ Goodwill

    /**
     * Get Product Cost Info
     *
     * @return product cost
     */
    public ProductCost getProductCost() {
        if (m_productCost == null)
            m_productCost =
                    new ProductCost(
                            getProductId(), getAttributeSetInstanceId());
        return m_productCost;
    } //	getProductCost
    // end MZ

    /**
     * Get Total Product Costs from Cost Detail or from Current Cost
     *
     * @param as          accounting schema
     * @param AD_Org_ID   trx org
     * @param zeroCostsOK zero/no costs are OK
     * @param whereClause null are OK
     * @return costs
     */
    public BigDecimal getProductCosts(
            AccountingSchema as, int AD_Org_ID, boolean zeroCostsOK, String whereClause) {
        if (whereClause != null
                && !as.getCostingMethod().equals(MAcctSchema.COSTINGMETHOD_StandardCosting)) {
            I_M_CostDetail cd =
                    MCostDetail.get(

                            whereClause,
                            getId(),
                            getAttributeSetInstanceId(),
                            as.getAccountingSchemaId());
            if (cd != null) return cd.getAmt();
        }
        return getProductCosts(as, AD_Org_ID, zeroCostsOK);
    } //  getProductCosts

    /**
     * Get Total Product Costs
     *
     * @param as          accounting schema
     * @param AD_Org_ID   trx org
     * @param zeroCostsOK zero/no costs are OK
     * @return costs
     */
    public BigDecimal getProductCosts(AccountingSchema as, int AD_Org_ID, boolean zeroCostsOK) {
        ProductCost pc = getProductCost();
        int C_OrderLine_ID = getOrderLineId();
        String costingMethod = null;
        BigDecimal costs =
                pc.getProductCosts(as, AD_Org_ID, costingMethod, C_OrderLine_ID, zeroCostsOK);
        if (costs != null) return costs;
        return Env.ZERO;
    } //  getProductCosts

    /**
     * Get Product
     *
     * @return product or null if no product
     */
    public MProduct getProduct() {
        if (m_productCost == null)
            m_productCost =
                    new ProductCost(
                            getProductId(), getAttributeSetInstanceId());
        return m_productCost.getProduct();
    } //	getProduct

    /**
     * Get Revenue Recognition
     *
     * @return C_RevenueRecognition_ID or 0
     */
    public int getRevenueRecognitionId() {
        MProduct product = getProduct();
        if (product != null) return product.getRevenueRecognitionId();
        return 0;
    } //  getRevenueRecognitionId

    /**
     * Quantity UOM
     *
     * @return Transaction or Storage M_UOM_ID
     */
    public int getUOMId() {
        //	Trx UOM
        int index = p_po.getColumnIndex("C_UOM_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        //  Storage UOM
        MProduct product = getProduct();
        if (product != null) return product.getUOMId();
        //
        return 0;
    } //  getUOM

    /**
     * Quantity
     *
     * @param qty     transaction Qty
     * @param isSOTrx SL order trx (i.e. negative qty)
     */
    public void setQty(BigDecimal qty, boolean isSOTrx) {
        if (qty == null) m_qty = Env.ZERO;
        else if (isSOTrx) m_qty = qty.negate();
        else m_qty = qty;
        getProductCost().setQty(qty);
    } //  setQty

    /**
     * Quantity
     *
     * @return transaction Qty
     */
    public BigDecimal getQty() {
        return m_qty;
    } //  getQty

    /**
     * Description
     *
     * @return doc line description
     */
    public String getDescription() {
        int index = p_po.getColumnIndex("Description");
        if (index != -1) return (String) p_po.getValue(index);
        return null;
    } //	getDescription

    /**
     * Line Tax
     *
     * @return C_Tax_ID
     */
    public int getTaxId() {
        int index = p_po.getColumnIndex("C_Tax_ID");
        return getIntValueIfColumnExists(index);
    } //	getTaxId

    /**
     * Get Line Number
     *
     * @return line no
     */
    public int getLine() {
        int index = p_po.getColumnIndex("Line");
        return getIntValueIfColumnExists(index);
    } //  getLine

    /**
     * Get BPartner
     *
     * @return C_BPartner_ID
     */
    public int getBusinessPartnerId() {
        if (m_C_BPartner_ID == -1) {
            int index = p_po.getColumnIndex("C_BPartner_ID");
            m_C_BPartner_ID = getIntValueIfColumnExists(index);
            if (m_C_BPartner_ID <= 0) m_C_BPartner_ID = m_doc.getBusinessPartnerId();
        }
        return m_C_BPartner_ID;
    } //  getBusinessPartnerId

    /**
     * Set C_BPartner_ID
     *
     * @param C_BPartner_ID id
     */
    protected void setBusinessPartnerId(int C_BPartner_ID) {
        m_C_BPartner_ID = C_BPartner_ID;
    } //	setBusinessPartnerId

    /**
     * Get C_BPartner_Location_ID
     *
     * @return BPartner Location
     */
    public int getBusinessPartnerLocationId() {
        int index = p_po.getColumnIndex("C_BPartner_Location_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return m_doc.getBusinessPartnerLocationId();
    } //	getBusinessPartnerLocationId

    /**
     * Get TrxOrg
     *
     * @return AD_OrgTrx_ID
     */
    public int getTransactionOrganizationId() {
        int index = p_po.getColumnIndex("AD_OrgTrx_ID");
        return getIntValueIfColumnExists(index);
    } //  getTransactionOrganizationId

    /**
     * Get SalesRegion. - get Sales Region from BPartner
     *
     * @return C_SalesRegion_ID
     */
    public int getSalesRegionId() {
        if (m_C_SalesRegion_ID == -1) // 	never tried
        {
            if (getBusinessPartnerLocationId() != 0)
            //	&& m_acctSchema.isAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_SalesRegion))
            {
                String sql =
                        "SELECT COALESCE(C_SalesRegion_ID,0) FROM C_BPartner_Location WHERE C_BPartner_Location_ID=?";
                m_C_SalesRegion_ID = getSQLValue(sql, getBusinessPartnerLocationId());
                if (log.isLoggable(Level.FINE))
                    log.fine("C_SalesRegion_ID=" + m_C_SalesRegion_ID + " (from BPL)");
                if (m_C_SalesRegion_ID == 0) m_C_SalesRegion_ID = -2; // 	don't try again
            } else m_C_SalesRegion_ID = -2; // 	don't try again
        }
        if (m_C_SalesRegion_ID < 0) // 	invalid
            return 0;
        return m_C_SalesRegion_ID;
    } //  getSalesRegionId

    /**
     * Get Project
     *
     * @return C_Project_ID
     */
    public int getProjectId() {
        int index = p_po.getColumnIndex("C_Project_ID");
        return getIntValueIfColumnExists(index);
    } //  getProjectId

    /**
     * Get Project Phase
     *
     * @return C_ProjectPhase_ID
     */
    public int getProjectPhaseId() {
        int index = p_po.getColumnIndex("C_ProjectPhase_ID");
        return getIntValueIfColumnExists(index);
    } //  getProjectPhaseId

    /**
     * Get Project Task
     *
     * @return C_ProjectTask_ID
     */
    public int getProjectTaskId() {
        int index = p_po.getColumnIndex("C_ProjectTask_ID");
        return getIntValueIfColumnExists(index);
    } //  getProjectTaskId

    /**
     * Get Campaign
     *
     * @return C_Campaign_ID
     */
    public int getCampaignId() {
        int index = p_po.getColumnIndex("C_Campaign_ID");
        return getIntValueIfColumnExists(index);
    } //  getCampaignId

    /**
     * Get Activity
     *
     * @return C_Activity_ID
     */
    public int getBusinessActivityId() {
        int index = p_po.getColumnIndex("C_Activity_ID");
        return getIntValueIfColumnExists(index);
    } //  getBusinessActivityId

    /**
     * Get User 1
     *
     * @return user defined 1
     */
    public int getUser1Id() {
        int index = p_po.getColumnIndex("User1_ID");
        return getIntValueIfColumnExists(index);
    } //  getUser1Id

    /**
     * Get User 2
     *
     * @return user defined 2
     */
    public int getUser2Id() {
        int index = p_po.getColumnIndex("User2_ID");
        return getIntValueIfColumnExists(index);
    } //  getUser2Id

    /**
     * Get User Defined Column
     *
     * @param ColumnName column name
     * @return user defined column value
     */
    public int getValue(String ColumnName) {
        int index = p_po.getColumnIndex(ColumnName);
        return getIntValueIfColumnExists(index);
    } //  getValue

    /**
     * Get ReversalLine_ID get original (voided/reversed) document line
     *
     * @return ReversalLine_ID
     */
    public int getReversalLineId() {
        return m_ReversalLine_ID;
    } //  getReversalLineId

    /**
     * Set ReversalLine_ID store original (voided/reversed) document line
     *
     * @param ReversalLine_ID
     */
    public void setReversalLineId(int ReversalLine_ID) {
        m_ReversalLine_ID = ReversalLine_ID;
    } //  setReversalLineId
    // end AZ Goodwill

    public IDocLine getPO() {
        return p_po;
    }

    /**
     * String representation
     *
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("DocLine=[");
        sb.append(p_po.getId());
        if (getDescription() != null) sb.append(",").append(getDescription());
        if (getProductId() != 0) sb.append(",M_Product_ID=").append(getProductId());
        sb.append(",Qty=").append(m_qty).append(",Amt=").append(getAmtSource()).append("]");
        return sb.toString();
    } //	toString
} //	DocumentLine
