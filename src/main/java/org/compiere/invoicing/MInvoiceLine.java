package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MMatchInv;
import org.compiere.model.IDocLine;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_InvoiceTax;
import org.compiere.model.I_C_LandedCost;
import org.compiere.model.I_C_OrderLine;
import org.compiere.model.I_M_InOutLine;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_RMALine;
import org.compiere.order.MCharge;
import org.compiere.order.MOrderLine;
import org.compiere.order.MRMALine;
import org.compiere.orm.MRoleKt;
import org.compiere.orm.PO;
import org.compiere.orm.Query;
import org.compiere.product.IProductPricing;
import org.compiere.product.MPriceList;
import org.compiere.product.MProduct;
import org.compiere.product.MUOM;
import org.compiere.tax.IInvoiceTaxProvider;
import org.compiere.tax.MTax;
import org.compiere.tax.MTaxCategory;
import org.compiere.tax.MTaxProvider;
import org.compiere.tax.Tax;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.idempiere.icommon.model.PersistentObject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.orm.POKt.I_ZERO;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.getSQLValueBDEx;

/**
 * Invoice Line Model
 *
 * @author Jorg Janke
 * @author Teo Sarca, www.arhipac.ro
 * <li>BF [ 2804142 ] MInvoice.setRMALine should work only for CreditMemo invoices
 * https://sourceforge.net/tracker/?func=detail&aid=2804142&group_id=176962&atid=879332
 * @author Michael Judd, www.akunagroup.com
 * <li>BF [ 1733602 ] Price List including Tax Error - when a user changes the orderline or
 * invoice line for a product on a price list that includes tax, the net amount is
 * incorrectly calculated.
 * @author red1 FR: [ 2214883 ] Remove SQL code and Replace for Query
 * @version $Id: MInvoiceLine.java,v 1.5 2006/07/30 00:51:03 jjanke Exp $
 */
public class MInvoiceLine extends X_C_InvoiceLine implements I_C_InvoiceLine, IDocLine {
    /**
     *
     */
    private static final long serialVersionUID = -6174490999732876285L;
    /**
     * Static Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MInvoiceLine.class);
    /**
     * Tax
     */
    private MTax m_tax = null;
    private int m_M_PriceList_ID = 0;
    private Timestamp m_DateInvoiced = null;
    private int m_C_BPartner_ID = 0;
    private int m_C_BPartner_Location_ID = 0;
    private boolean m_IsSOTrx = true;
    private boolean m_priceSet = false;
    private I_M_Product m_product = null;
    /**
     * Charge
     */
    private MCharge m_charge = null;
    /**
     * Cached Precision
     */
    private Integer m_precision = null;
    /**
     * Product Pricing
     */
    private IProductPricing m_productPricing = null;
    /**
     * Parent
     */
    private I_C_Invoice m_parent = null;

    /**
     * ************************************************************************ Invoice Line
     * Constructor
     *
     * @param C_InvoiceLine_ID invoice line or 0
     */
    public MInvoiceLine(int C_InvoiceLine_ID) {
        super(C_InvoiceLine_ID);
        if (C_InvoiceLine_ID == 0) {
            setIsDescription(false);
            setIsPrinted(true);
            setLineNetAmt(Env.ZERO);
            setPriceEntered(Env.ZERO);
            setPriceActual(Env.ZERO);
            setPriceLimit(Env.ZERO);
            setPriceList(Env.ZERO);
            setAttributeSetInstanceId(0);
            setTaxAmt(Env.ZERO);
            //
            setQtyEntered(Env.ZERO);
            setQtyInvoiced(Env.ZERO);
        }
    } //	MInvoiceLine

    /**
     * Parent Constructor
     *
     * @param invoice parent
     */
    public MInvoiceLine(MInvoice invoice) {
        this(0);
        if (invoice.getId() == 0) throw new IllegalArgumentException("Header not saved");
        setClientOrg(invoice.getClientId(), invoice.getOrgId());
        setInvoiceId(invoice.getInvoiceId());
        setInvoice(invoice);
    } //	MInvoiceLine

    /**
     * Load Constructor
     */
    public MInvoiceLine(Row row) {
        super(row);
    } //	MInvoiceLine

    /**
     * Get Invoice Line referencing InOut Line
     *
     * @param sLine shipment line
     * @return (first) invoice line
     */
    public static I_C_InvoiceLine getOfInOutLine(I_M_InOutLine sLine) {
        if (sLine == null) return null;
        final String whereClause = I_M_InOutLine.COLUMNNAME_M_InOutLine_ID + "=?";
        List<I_C_InvoiceLine> list =
                new Query<I_C_InvoiceLine>(I_C_InvoiceLine.Table_Name, whereClause)
                        .setParameters(sLine.getInOutLineId())
                        .list();

        I_C_InvoiceLine retValue = null;
        if (list.size() > 0) {
            retValue = list.get(0);
            if (list.size() > 1) s_log.warning("More than one C_InvoiceLine of " + sLine);
        }

        return retValue;
    } //	getOfInOutLine

    /**
     * Get Invoice Line referencing InOut Line - from MatchInv
     *
     * @param sLine shipment line
     * @return (first) invoice line
     */
    public static MInvoiceLine getOfInOutLineFromMatchInv(org.compiere.order.MInOutLine sLine) {
        if (sLine == null) return null;
        final String whereClause =
                "C_InvoiceLine_ID IN (SELECT C_InvoiceLine_ID FROM M_MatchInv WHERE M_InOutLine_ID=?)";
        List<MInvoiceLine> list =
                new Query(Table_Name, whereClause)
                        .setParameters(sLine.getInOutLineId())
                        .list();

        MInvoiceLine retValue = null;
        if (list.size() > 0) {
            retValue = list.get(0);
            if (list.size() > 1) s_log.warning("More than one C_InvoiceLine of " + sLine);
        }

        return retValue;
    }

    /**
     * Set Defaults from Order. Called also from copy lines from invoice Does not set Parent !!
     *
     * @param invoice invoice
     */
    public void setInvoice(I_C_Invoice invoice) {
        m_parent = invoice;
        m_M_PriceList_ID = invoice.getPriceListId();
        m_DateInvoiced = invoice.getDateInvoiced();
        m_C_BPartner_ID = invoice.getBusinessPartnerId();
        m_C_BPartner_Location_ID = invoice.getBusinessPartnerLocationId();
        m_IsSOTrx = invoice.isSOTrx();
        m_precision = invoice.getPrecision();
    } //	setOrder

    /**
     * Get Parent
     *
     * @return parent
     */
    public I_C_Invoice getParent() {
        if (m_parent == null) m_parent = new MInvoice(null, getInvoiceId());
        return m_parent;
    } //	getParent

    /**
     * Set values from Order Line. Does not set quantity!
     *
     * @param oLine line
     */
    public void setOrderLine(I_C_OrderLine oLine) {
        setOrderLineId(oLine.getOrderLineId());
        //
        setLine(oLine.getLine());
        setIsDescription(oLine.isDescription());
        setDescription(oLine.getDescription());
        //
        if (oLine.getProductId() == 0) setChargeId(oLine.getChargeId());
        //
        setProductId(oLine.getProductId());
        setAttributeSetInstanceId(oLine.getAttributeSetInstanceId());
        setS_ResourceAssignmentId(oLine.getResourceAssignmentId());
        setUOMId(oLine.getUOMId());
        //
        setPriceEntered(oLine.getPriceEntered());
        setPriceActual(oLine.getPriceActual());
        setPriceLimit(oLine.getPriceLimit());
        setPriceList(oLine.getPriceList());
        //
        setTaxId(oLine.getTaxId());
        setLineNetAmt(oLine.getLineNetAmt());
        //
        setProjectId(oLine.getProjectId());
        setProjectPhaseId(oLine.getProjectPhaseId());
        setProjectTaskId(oLine.getProjectTaskId());
        setBusinessActivityId(oLine.getBusinessActivityId());
        setCampaignId(oLine.getCampaignId());
        setTransactionOrganizationId(oLine.getTransactionOrganizationId());
        setUser1Id(oLine.getUser1Id());
        setUser2Id(oLine.getUser2Id());
        //
        setRRAmt(oLine.getRRAmt());
        setRRStartDate(oLine.getRRStartDate());
    } //	setOrderLine

    /**
     * Set values from Shipment Line. Does not set quantity!
     *
     * @param sLine ship line
     */
    public void setShipLine(I_M_InOutLine sLine) {
        setInOutLineId(sLine.getInOutLineId());
        setOrderLineId(sLine.getOrderLineId());
        // Set RMALine ID if shipment/receipt is based on RMA Doc
        setRMALineId(sLine.getRMALineId());

        //
        setLine(sLine.getLine());
        setIsDescription(sLine.isDescription());
        setDescription(sLine.getDescription());
        //
        setProductId(sLine.getProductId());
        if (sLine.sameOrderLineUOM() || getProduct() == null) setUOMId(sLine.getUOMId());
        else
            // use product UOM if the shipment hasn't the same uom than the order
            setUOMId(getProduct().getUOMId());
        setAttributeSetInstanceId(sLine.getAttributeSetInstanceId());
        //	setS_ResourceAssignmentId(sLine.getResourceAssignmentId());
        if (getProductId() == 0) setChargeId(sLine.getChargeId());
        //
        int C_OrderLine_ID = sLine.getOrderLineId();
        if (C_OrderLine_ID != 0) {
            MOrderLine oLine = new MOrderLine(C_OrderLine_ID);
            setS_ResourceAssignmentId(oLine.getResourceAssignmentId());
            //
            if (sLine.sameOrderLineUOM()) setPriceEntered(oLine.getPriceEntered());
            else setPriceEntered(oLine.getPriceActual());
            setPriceActual(oLine.getPriceActual());
            setPriceLimit(oLine.getPriceLimit());
            setPriceList(oLine.getPriceList());
            //
            setTaxId(oLine.getTaxId());
            setLineNetAmt(oLine.getLineNetAmt());
            setProjectId(oLine.getProjectId());
        }
        // Check if shipment line is based on RMA
        else if (sLine.getRMALineId() != 0) {
            // Set Pricing details from the RMA Line on which it is based
            MRMALine rmaLine = new MRMALine(sLine.getRMALineId());

            setPrice();
            setPrice(rmaLine.getAmt());
            setTaxId(rmaLine.getTaxId());
            setLineNetAmt(rmaLine.getLineNetAmt());
        } else {
            setPrice();
            setTax();
        }
        //
        setProjectId(sLine.getProjectId());
        setProjectPhaseId(sLine.getProjectPhaseId());
        setProjectTaskId(sLine.getProjectTaskId());
        setBusinessActivityId(sLine.getBusinessActivityId());
        setCampaignId(sLine.getCampaignId());
        setTransactionOrganizationId(sLine.getTransactionOrganizationId());
        setUser1Id(sLine.getUser1Id());
        setUser2Id(sLine.getUser2Id());
    } //	setShipLine

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
     * Set M_AttributeSetInstance_ID
     *
     * @param M_AttributeSetInstance_ID id
     */
    public void setAttributeSetInstanceId(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID == 0) // 	 0 is valid ID
            setValue("M_AttributeSetInstance_ID", 0);
        else super.setAttributeSetInstanceId(M_AttributeSetInstance_ID);
    } //	setAttributeSetInstanceId

    /**
     * ************************************************************************ Set Price for Product
     * and PriceList. Uses standard SO price list of not set by invoice constructor
     */
    public void setPrice() {
        if (getProductId() == 0 || isDescription()) return;
        if (m_M_PriceList_ID == 0 || m_C_BPartner_ID == 0) setInvoice(getParent());
        if (m_M_PriceList_ID == 0 || m_C_BPartner_ID == 0)
            throw new IllegalStateException("setPrice - PriceList unknown!");
        setPrice(m_M_PriceList_ID);
    } //	setPrice

    /**
     * Set Price for Product and PriceList
     *
     * @param M_PriceList_ID price list
     */
    public void setPrice(int M_PriceList_ID) {
        if (getProductId() == 0 || isDescription()) return;
        //
        if (log.isLoggable(Level.FINE)) log.fine("M_PriceList_ID=" + M_PriceList_ID);
        m_productPricing = MProduct.getProductPricing();
        m_productPricing.setInvoiceLine(this);
        m_productPricing.setPriceListId(M_PriceList_ID);
        //
        setPriceActual(m_productPricing.getPriceStd());
        setPriceList(m_productPricing.getPriceList());
        setPriceLimit(m_productPricing.getPriceLimit());
        //
        if (getQtyEntered().compareTo(getQtyInvoiced()) == 0) setPriceEntered(getPriceActual());
        else
            setPriceEntered(
                    getPriceActual()
                            .multiply(
                                    getQtyInvoiced()
                                            .divide(getQtyEntered(), 6, BigDecimal.ROUND_HALF_UP))); // 	precision
        //
        if (getUOMId() == 0) setUOMId(m_productPricing.getUOMId());
        //
        m_priceSet = true;
    } //	setPrice

    /**
     * Set Price Entered/Actual. Use this Method if the Line UOM is the Product UOM
     *
     * @param PriceActual price
     */
    public void setPrice(BigDecimal PriceActual) {
        setPriceEntered(PriceActual);
        setPriceActual(PriceActual);
    } //	setPrice

    /**
     * Set Price Actual. (actual price is not updateable)
     *
     * @param PriceActual actual price
     */
    public void setPriceActual(BigDecimal PriceActual) {
        if (PriceActual == null) throw new IllegalArgumentException("PriceActual is mandatory");
        setValueNoCheck("PriceActual", PriceActual);
    } //	setPriceActual

    /**
     * Set Tax - requires Warehouse
     *
     * @return true if found
     */
    public boolean setTax() {
        if (isDescription()) return true;
        //
        int M_Warehouse_ID = Env.getContextAsInt("#M_Warehouse_ID");
        //
        int C_Tax_ID =
                Tax.get(

                        getProductId(),
                        getChargeId(),
                        m_DateInvoiced,
                        m_DateInvoiced,
                        getOrgId(),
                        M_Warehouse_ID,
                        m_C_BPartner_Location_ID, //	should be bill to
                        m_C_BPartner_Location_ID,
                        m_IsSOTrx,
                        null);
        if (C_Tax_ID == 0) {
            log.log(Level.SEVERE, "No Tax found");
            return false;
        }
        setTaxId(C_Tax_ID);
        return true;
    } //	setTax

    /**
     * Calculate Tax Amt. Assumes Line Net is calculated
     */
    public void setTaxAmt() {
        BigDecimal TaxAmt;
        if (getTaxId() == 0) return;
        setLineNetAmt();
        MTax tax = MTax.get(getTaxId());
        if (tax.isDocumentLevel() && m_IsSOTrx) // 	AR Inv Tax
            return;
        //
        TaxAmt = tax.calculateTax(getLineNetAmt(), isTaxIncluded(), getPrecision());
        if (isTaxIncluded()) setLineTotalAmt(getLineNetAmt());
        else setLineTotalAmt(getLineNetAmt().add(TaxAmt));
        super.setTaxAmt(TaxAmt);
    } //	setTaxAmt

    /**
     * Calculate Extended Amt. May or may not include tax
     */
    public void setLineNetAmt() {
        //	Calculations & Rounding
        BigDecimal bd = getPriceActual().multiply(getQtyInvoiced());

        boolean documentLevel = getTax().isDocumentLevel();

        //	juddm: Tax Exempt & Tax Included in Price List & not Document Level - Adjust Line Amount
        //  http://sourceforge.net/tracker/index.php?func=detail&aid=1733602&group_id=176962&atid=879332
        if (isTaxIncluded() && !documentLevel) {
            BigDecimal taxStdAmt = Env.ZERO, taxThisAmt = Env.ZERO;

            MTax invoiceTax = getTax();
            MTax stdTax = null;

            if (getProduct() == null) {
                if (getCharge() != null) // Charge
                {
                    stdTax =
                            new MTax(

                                    ((MTaxCategory) getCharge().getTaxCategory()).getDefaultTax().getTaxId());
                }

            } else // Product
                stdTax =
                        new MTax(

                                ((MTaxCategory) getProduct().getTaxCategory()).getDefaultTax().getTaxId());

            if (stdTax != null) {

                if (log.isLoggable(Level.FINE)) log.fine("stdTax rate is " + stdTax.getRate());
                if (log.isLoggable(Level.FINE)) log.fine("invoiceTax rate is " + invoiceTax.getRate());

                taxThisAmt = taxThisAmt.add(invoiceTax.calculateTax(bd, isTaxIncluded(), getPrecision()));
                taxStdAmt = taxStdAmt.add(stdTax.calculateTax(bd, isTaxIncluded(), getPrecision()));

                bd = bd.subtract(taxStdAmt).add(taxThisAmt);

                if (log.isLoggable(Level.FINE))
                    log.fine(
                            "Price List includes Tax and Tax Changed on Invoice Line: New Tax Amt: "
                                    + taxThisAmt
                                    + " Standard Tax Amt: "
                                    + taxStdAmt
                                    + " Line Net Amt: "
                                    + bd);
            }
        }
        int precision = getPrecision();
        if (bd.scale() > precision) bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
        super.setLineNetAmt(bd);
    } //	setLineNetAmt

    /**
     * Get Charge
     *
     * @return product or null
     */
    public MCharge getCharge() {
        if (m_charge == null && getChargeId() != 0)
            m_charge = MCharge.get(getChargeId());
        return m_charge;
    }

    /**
     * Get Tax
     *
     * @return tax
     */
    protected MTax getTax() {
        if (m_tax == null) m_tax = MTax.get(getTaxId());
        return m_tax;
    } //	getTax

    /**
     * Set Qty Invoiced
     *
     * @param Qty Invoiced/Entered
     */
    public void setQty(BigDecimal Qty) {
        setQtyEntered(Qty);
        setQtyInvoiced(getQtyEntered());
    } //	setQtyInvoiced

    /**
     * Set Qty Entered - enforce entered UOM
     *
     * @param QtyEntered
     */
    public void setQtyEntered(BigDecimal QtyEntered) {
        if (QtyEntered != null && getUOMId() != 0) {
            int precision = MUOM.getPrecision(getUOMId());
            QtyEntered = QtyEntered.setScale(precision, BigDecimal.ROUND_HALF_UP);
        }
        super.setQtyEntered(QtyEntered);
    } //	setQtyEntered

    /**
     * Set Qty Invoiced - enforce Product UOM
     *
     * @param QtyInvoiced
     */
    public void setQtyInvoiced(BigDecimal QtyInvoiced) {
        I_M_Product product = getProduct();
        if (QtyInvoiced != null && product != null) {
            int precision = product.getUOMPrecision();
            QtyInvoiced = QtyInvoiced.setScale(precision, BigDecimal.ROUND_HALF_UP);
        }
        super.setQtyInvoiced(QtyInvoiced);
    } //	setQtyInvoiced

    /**
     * Set M_Product_ID
     *
     * @param M_Product_ID product
     * @param setUOM       set UOM from product
     */
    public void setProductId(int M_Product_ID, boolean setUOM) {
        if (setUOM) setProduct(MProduct.get(M_Product_ID));
        else super.setProductId(M_Product_ID);
        setAttributeSetInstanceId(0);
    } //	setProductId

    /**
     * Get Product
     *
     * @return product or null
     */
    public I_M_Product getProduct() {
        if (m_product == null && getProductId() != 0)
            m_product = MProduct.get(getProductId());
        return m_product;
    } //	getProduct

    /**
     * Set Product
     *
     * @param product product
     */
    public void setProduct(I_M_Product product) {
        m_product = product;
        if (m_product != null) {
            setProductId(m_product.getProductId());
            setUOMId(m_product.getUOMId());
        } else {
            setProductId(0);
            setUOMId(0);
        }
        setAttributeSetInstanceId(0);
    } //	setProduct

    /**
     * Get C_Project_ID
     *
     * @return project
     */
    public int getProjectId() {
        int ii = super.getProjectId();
        if (ii == 0) ii = getParent().getProjectId();
        return ii;
    } //	getProjectId

    /**
     * Get C_Activity_ID
     *
     * @return Activity
     */
    public int getBusinessActivityId() {
        int ii = super.getBusinessActivityId();
        if (ii == 0) ii = getParent().getBusinessActivityId();
        return ii;
    } //	getBusinessActivityId

    /**
     * Get C_Campaign_ID
     *
     * @return Campaign
     */
    public int getCampaignId() {
        int ii = super.getCampaignId();
        if (ii == 0) ii = getParent().getCampaignId();
        return ii;
    } //	getCampaignId

    /**
     * Get User2_ID
     *
     * @return User2
     */
    public int getUser1Id() {
        int ii = super.getUser1Id();
        if (ii == 0) ii = getParent().getUser1Id();
        return ii;
    } //	getUser1Id

    /**
     * Get User2_ID
     *
     * @return User2
     */
    public int getUser2Id() {
        int ii = super.getUser2Id();
        if (ii == 0) ii = getParent().getUser2Id();
        return ii;
    } //	getUser2Id

    /**
     * Get AD_OrgTrx_ID
     *
     * @return trx org
     */
    public int getTransactionOrganizationId() {
        int ii = super.getTransactionOrganizationId();
        if (ii == 0) ii = getParent().getTransactionOrganizationId();
        return ii;
    } //	getTransactionOrganizationId

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MInvoiceLine[" +
                getId() +
                "," +
                getLine() +
                ",QtyInvoiced=" +
                getQtyInvoiced() +
                ",LineNetAmt=" +
                getLineNetAmt() +
                "]";
    } //	toString

    /**
     * Get Currency Precision
     *
     * @return precision
     */
    public int getPrecision() {
        if (m_precision != null) return m_precision;

        String sql =
                "SELECT c.StdPrecision "
                        + "FROM C_Currency c INNER JOIN C_Invoice x ON (x.C_Currency_ID=c.C_Currency_ID) "
                        + "WHERE x.C_Invoice_ID=?";
        int i = getSQLValue(sql, getInvoiceId());
        if (i < 0) {
            log.warning("getPrecision = " + i + " - set to 2");
            i = 2;
        }
        m_precision = i;
        return m_precision;
    } //	getPrecision

    /**
     * Is Tax Included in Amount
     *
     * @return true if tax is included
     */
    public boolean isTaxIncluded() {
        if (m_M_PriceList_ID == 0) {
            m_M_PriceList_ID =
                    getSQLValue(
                            "SELECT M_PriceList_ID FROM C_Invoice WHERE C_Invoice_ID=?",
                            getInvoiceId());
        }
        I_M_PriceList pl = MPriceList.get(m_M_PriceList_ID);
        return pl.isTaxIncluded();
    } //	isTaxIncluded

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord
     * @return true if save
     */
    protected boolean beforeSave(boolean newRecord) {
        if (log.isLoggable(Level.FINE)) log.fine("New=" + newRecord);
        boolean parentComplete = getParent().isComplete();
        boolean isReversal = getParent().isReversal();
        if (newRecord && parentComplete) {
            log.saveError("ParentComplete", MsgKt.translate("C_InvoiceLine"));
            return false;
        }
        // Re-set invoice header (need to update m_IsSOTrx flag) - phib [ 1686773 ]
        setInvoice(getParent());

        if (!parentComplete && !isReversal) { // do not change things when parent is complete
            //	Charge
            if (getChargeId() != 0) {
                if (getProductId() != 0) setProductId(0);
            } else //	Set Product Price
            {
                if (!m_priceSet
                        && Env.ZERO.compareTo(getPriceActual()) == 0
                        && Env.ZERO.compareTo(getPriceList()) == 0) setPrice();
                // IDEMPIERE-1574 Sales Order Line lets Price under the Price Limit when updating
                //	Check PriceLimit
                boolean enforce = m_IsSOTrx && getParent().getPriceList().isEnforcePriceLimit();
                if (enforce && MRoleKt.getDefaultRole().isOverwritePriceLimit()) enforce = false;
                //	Check Price Limit?
                if (enforce
                        && !getPriceLimit().equals(Env.ZERO)
                        && getPriceActual().compareTo(getPriceLimit()) < 0) {
                    log.saveError(
                            "UnderLimitPrice",
                            "PriceEntered=" + getPriceEntered() + ", PriceLimit=" + getPriceLimit());
                    return false;
                }
                //
            }

            //	Set Tax
            if (getTaxId() == 0) setTax();

            //	Get Line No
            if (getLine() == 0) {
                String sql = "SELECT COALESCE(MAX(Line),0)+10 FROM C_InvoiceLine WHERE C_Invoice_ID=?";
                int ii = getSQLValue(sql, getInvoiceId());
                setLine(ii);
            }
            //	UOM
            if (getUOMId() == 0) {
                int C_UOM_ID = MUOM.getDefault_UOMId();
                if (C_UOM_ID > 0) setUOMId(C_UOM_ID);
            }
            //	Qty Precision
            if (newRecord || isValueChanged("QtyEntered")) setQtyEntered(getQtyEntered());
            if (newRecord || isValueChanged("QtyInvoiced")) setQtyInvoiced(getQtyInvoiced());

            //	Calculations & Rounding
            setLineNetAmt();
            // TaxAmt recalculations should be done if the TaxAmt is zero
            // or this is an Invoice(Customer) - teo_sarca, globalqss [ 1686773 ]
            if (m_IsSOTrx || getTaxAmt().compareTo(Env.ZERO) == 0) setTaxAmt();
            //

            /* Carlos Ruiz - globalqss
             * IDEMPIERE-178 Orders and Invoices must disallow amount lines without product/charge
             */
            if (getParent().getDocTypeTarget().isChargeOrProductMandatory()) {
                if (getChargeId() == 0
                        && getProductId() == 0
                        && (getPriceEntered().signum() != 0 || getQtyEntered().signum() != 0)) {
                    log.saveError("FillMandatory", MsgKt.translate("ChargeOrProductMandatory"));
                    return false;
                }
            }
        }

        return true;
    } //	beforeSave

    /**
     * Recalculate invoice tax
     *
     * @param oldTax true if the old C_Tax_ID should be used
     * @return true if success, false otherwise
     * @author teo_sarca [ 1583825 ]
     */
    public boolean updateInvoiceTax(boolean oldTax) {
        I_C_InvoiceTax tax = MInvoiceTax.get(this, getPrecision(), oldTax);
        if (tax != null) {
            if (!tax.calculateTaxFromLines()) return false;

            // red1 - solving BUGS #[ 1701331 ] , #[ 1786103 ]
            if (tax.getTaxAmt().signum() != 0) {
                return tax.save();
            } else {
                return tax.isNew() || tax.delete(false);
            }
        }
        return true;
    }

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return saved
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;
        MTax tax = new MTax(getTaxId());
        MTaxProvider provider =
                new MTaxProvider(tax.getTaxProviderId());
        IInvoiceTaxProvider calculator =
                MTaxProvider.getTaxProvider(provider, new StandardInvoiceTaxProvider());
        if (calculator == null) throw new AdempiereException(MsgKt.getMsg("TaxNoProvider"));
        return calculator.recalculateTax(provider, this, newRecord);
    } //	afterSave

    /**
     * After Delete
     *
     * @param success success
     * @return deleted
     */
    protected boolean afterDelete(boolean success) {
        if (!success) return success;

        // reset shipment line invoiced flag
        if (getInOutLineId() > 0) {
            org.compiere.order.MInOutLine sLine =
                    new org.compiere.order.MInOutLine(getInOutLineId());
            sLine.setIsInvoiced(false);
            sLine.saveEx();
        }

        return updateHeaderTax();
    } //	afterDelete

    /**
     * Update Tax & Header
     *
     * @return true if header updated with tax
     */
    public boolean updateHeaderTax() {
        // Update header only if the document is not processed - teo_sarca BF [ 2317305 ]
        if (isProcessed() && !isValueChanged(COLUMNNAME_Processed)) return true;

        //	Recalculate Tax for this Tax
        MTax tax = new MTax(getTaxId());
        MTaxProvider provider =
                new MTaxProvider(tax.getTaxProviderId());
        IInvoiceTaxProvider calculator =
                MTaxProvider.getTaxProvider(provider, new StandardInvoiceTaxProvider());
        if (calculator == null) throw new AdempiereException(MsgKt.getMsg("TaxNoProvider"));
        if (!calculator.updateInvoiceTax(provider, this)) return false;

        return calculator.updateHeaderTax(provider, this);
    } //	updateHeaderTax

    /**
     * ************************************************************************ Allocate Landed Costs
     *
     * @return error message or ""
     */
    public String allocateLandedCosts() {
        if (isProcessed()) return "Processed";
        String sql = "DELETE C_LandedCostAllocation WHERE C_InvoiceLine_ID=" +
                getInvoiceLineId();
        int no = executeUpdate(sql);
        if (no != 0) if (log.isLoggable(Level.INFO)) log.info("Deleted #" + no);
        MLandedCost[] lcs = MLandedCost.getLandedCosts(this);
        if (lcs.length == 0) return "";

        int inserted = 0;
        //	*** Single Criteria ***
        StringBuilder msgreturn;
        if (lcs.length == 1) {
            MLandedCost lc = lcs[0];
            if (lc.getInOutId() != 0 && lc.getInOutLineId() == 0) {
                //	Create List
                ArrayList<I_M_InOutLine> list = new ArrayList<>();
                MInOut ship = new MInOut(lc.getInOutId());
                I_M_InOutLine[] lines = ship.getLines();
                for (I_M_InOutLine line : lines) {
                    if (line.isDescription() || line.getProductId() == 0) continue;
                    if (lc.getProductId() == 0 || lc.getProductId() == line.getProductId())
                        list.add(line);
                }
                if (list.size() == 0) return "No Matching Lines (with Product) in Shipment";
                //	Calculate total & base
                BigDecimal total = Env.ZERO;
                for (I_M_InOutLine iol : list) {
                    total = total.add(iol.getBase(lc.getLandedCostDistribution()));
                }
                if (total.signum() == 0) {
                    msgreturn =
                            new StringBuilder("Total of Base values is 0 - ")
                                    .append(lc.getLandedCostDistribution());
                    return msgreturn.toString();
                }
                //	Create Allocations
                for (I_M_InOutLine iol : list) {
                    MLandedCostAllocation lca = new MLandedCostAllocation(this, lc.getCostElementId());
                    lca.setProductId(iol.getProductId());
                    lca.setInOutLineId(iol.getInOutLineId());
                    lca.setAttributeSetInstanceId(iol.getAttributeSetInstanceId());
                    BigDecimal base = iol.getBase(lc.getLandedCostDistribution());
                    lca.setBase(base);
                    // MZ Goodwill
                    // add set Qty from InOutLine
                    lca.setQty(iol.getMovementQty());
                    // end MZ
                    if (base.signum() != 0) {
                        double result = getLineNetAmt().multiply(base).doubleValue();
                        result /= total.doubleValue();
                        lca.setAmt(result, getParent().getCurrency().getCostingPrecision());
                    }
                    if (!lca.save()) {
                        msgreturn = new StringBuilder("Cannot save line Allocation = ").append(lca);
                        return msgreturn.toString();
                    }
                    inserted++;
                }
                if (log.isLoggable(Level.INFO)) log.info("Inserted " + inserted);
                allocateLandedCostRounding();
                return "";
            }
            //	Single Line
            else if (lc.getInOutLineId() != 0) {
                MInOutLine iol = new MInOutLine(lc.getInOutLineId());
                if (iol.isDescription() || iol.getProductId() == 0) {
                    msgreturn = new StringBuilder("Invalid Receipt Line - ").append(iol);
                    return msgreturn.toString();
                }
                MLandedCostAllocation lca = new MLandedCostAllocation(this, lc.getCostElementId());
                lca.setProductId(iol.getProductId());
                lca.setAttributeSetInstanceId(iol.getAttributeSetInstanceId());
                lca.setInOutLineId(iol.getInOutLineId());
                BigDecimal base = iol.getBase(lc.getLandedCostDistribution());
                if (base.signum() == 0) return "Base value is 0 - " + lc.getLandedCostDistribution();
                lca.setBase(base);
                lca.setAmt(getLineNetAmt());
                // MZ Goodwill
                // add set Qty from InOutLine
                lca.setQty(iol.getMovementQty());
                // end MZ
                if (lca.save()) return "";
                msgreturn = new StringBuilder("Cannot save single line Allocation = ").append(lc);
                return msgreturn.toString();
            }
            //	Single Product
            else if (lc.getProductId() != 0) {
                MLandedCostAllocation lca = new MLandedCostAllocation(this, lc.getCostElementId());
                lca.setProductId(lc.getProductId()); // 	No ASI
                lca.setAmt(getLineNetAmt());
                if (lc.getLandedCostDistribution().equals(MLandedCost.LANDEDCOSTDISTRIBUTION_Costs)) {
                    lca.setBase(getLineNetAmt());
                    lca.setQty(getLineNetAmt());
                } else {
                    lca.setBase(getQtyInvoiced());
                    lca.setQty(getQtyInvoiced());
                }
                if (lca.save()) return "";
                msgreturn = new StringBuilder("Cannot save Product Allocation = ").append(lc);
                return msgreturn.toString();
            } else {
                msgreturn = new StringBuilder("No Reference for ").append(lc);
                return msgreturn.toString();
            }
        }

        //	*** Multiple Criteria ***
        String LandedCostDistribution = lcs[0].getLandedCostDistribution();
        int M_CostElement_ID = lcs[0].getCostElementId();
        for (int i = 0; i < lcs.length; i++) {
            MLandedCost lc = lcs[i];
            if (!LandedCostDistribution.equals(lc.getLandedCostDistribution()))
                return "Multiple Landed Cost Rules must have consistent Landed Cost Distribution";
            if (lc.getProductId() != 0 && lc.getInOutId() == 0 && lc.getInOutLineId() == 0)
                return "Multiple Landed Cost Rules cannot directly allocate to a Product";
            if (M_CostElement_ID != lc.getCostElementId())
                return "Multiple Landed Cost Rules cannot different Cost Elements";
        }
        //	Create List
        ArrayList<I_M_InOutLine> list = new ArrayList<>();
        for (MLandedCost lc : lcs) {
            if (lc.getInOutId() != 0 && lc.getInOutLineId() == 0) // 	entire receipt
            {
                MInOut ship = new MInOut(lc.getInOutId());
                I_M_InOutLine[] lines = ship.getLines();
                for (I_M_InOutLine line : lines) {
                    if (line.isDescription() // 	decription or no product
                            || line.getProductId() == 0) continue;
                    if (lc.getProductId() == 0 // 	no restriction or product match
                            || lc.getProductId() == line.getProductId()) list.add(line);
                }
            } else if (lc.getInOutLineId() != 0) // 	receipt line
            {
                org.compiere.order.MInOutLine iol =
                        new org.compiere.order.MInOutLine(lc.getInOutLineId());
                if (!iol.isDescription() && iol.getProductId() != 0) list.add(iol);
            }
        }
        if (list.size() == 0) return "No Matching Lines (with Product)";
        //	Calculate total & base
        BigDecimal total = Env.ZERO;
        for (I_M_InOutLine mInOutLine : list) {
            MInOutLine iol = (MInOutLine) mInOutLine;
            total = total.add(iol.getBase(LandedCostDistribution));
        }
        if (total.signum() == 0) {
            msgreturn = new StringBuilder("Total of Base values is 0 - ").append(LandedCostDistribution);
            return msgreturn.toString();
        }
        //	Create Allocations
        for (I_M_InOutLine mInOutLine : list) {
            MInOutLine iol = (MInOutLine) mInOutLine;
            MLandedCostAllocation lca = new MLandedCostAllocation(this, lcs[0].getCostElementId());
            lca.setProductId(iol.getProductId());
            lca.setAttributeSetInstanceId(iol.getAttributeSetInstanceId());
            lca.setInOutLineId(iol.getInOutLineId());
            BigDecimal base = iol.getBase(LandedCostDistribution);
            lca.setBase(base);
            // MZ Goodwill
            // add set Qty from InOutLine
            lca.setQty(iol.getMovementQty());
            // end MZ
            if (base.signum() != 0) {
                double result = getLineNetAmt().multiply(base).doubleValue();
                result /= total.doubleValue();
                lca.setAmt(result, getParent().getCurrency().getCostingPrecision());
            }
            if (!lca.save()) {
                msgreturn = new StringBuilder("Cannot save line Allocation = ").append(lca);
                return msgreturn.toString();
            }
            inserted++;
        }

        if (log.isLoggable(Level.INFO)) log.info("Inserted " + inserted);
        allocateLandedCostRounding();
        return "";
    } //	allocate Costs

    /**
     * Allocate Landed Cost - Enforce Rounding
     */
    private void allocateLandedCostRounding() {
        MLandedCostAllocation[] allocations =
                MLandedCostAllocation.getOfInvoiceLine(getInvoiceLineId());
        MLandedCostAllocation largestAmtAllocation = null;
        BigDecimal allocationAmt = Env.ZERO;
        for (MLandedCostAllocation allocation : allocations) {
            if (largestAmtAllocation == null
                    || allocation.getAmt().compareTo(largestAmtAllocation.getAmt()) > 0)
                largestAmtAllocation = allocation;
            allocationAmt = allocationAmt.add(allocation.getAmt());
        }
        BigDecimal difference = getLineNetAmt().subtract(allocationAmt);
        if (difference.signum() != 0) {
            largestAmtAllocation.setAmt(largestAmtAllocation.getAmt().add(difference));
            largestAmtAllocation.saveEx();
            if (log.isLoggable(Level.CONFIG))
                log.config(
                        "Difference="
                                + difference
                                + ", C_LandedCostAllocation_ID="
                                + largestAmtAllocation.getLandedCostAllocationId()
                                + ", Amt"
                                + largestAmtAllocation.getAmt());
        }
    } //	allocateLandedCostRounding

    // MZ Goodwill

    /**
     * Get LandedCost of InvoiceLine
     *
     * @param whereClause starting with AND
     * @return landedCost
     */
    public I_C_LandedCost[] getLandedCost(String whereClause) {
        return MBaseInvoiceLineKt.getInvoiceLineLandedCost(getInvoiceLineId(), whereClause);
    } //	getLandedCost

    /**
     * Copy LandedCost From other InvoiceLine.
     *
     * @param otherInvoiceLine invoiceline
     * @return number of lines copied
     */
    public int copyLandedCostFrom(I_C_InvoiceLine otherInvoiceLine) {
        if (otherInvoiceLine == null) return 0;
        I_C_LandedCost[] fromLandedCosts = otherInvoiceLine.getLandedCost(null);
        int count = 0;
        for (I_C_LandedCost cost : fromLandedCosts) {
            MLandedCost landedCost = new MLandedCost(0);
            PO.copyValues(
                    (PO)cost,
                    landedCost,
                    cost.getClientId(),
                    cost.getOrgId());
            landedCost.setInvoiceLineId(getInvoiceLineId());
            landedCost.setValueNoCheck("C_LandedCost_ID", I_ZERO); // new
            if (landedCost.save()) count++;
        }
        if (fromLandedCosts.length != count)
            log.log(
                    Level.SEVERE,
                    "LandedCost difference - From=" + fromLandedCosts.length + " <> Saved=" + count);
        return count;
    } //	copyLinesFrom
    // end MZ

    /**
     * @param rmaLine
     */
    public void setRMALine(I_M_RMALine rmaLine) {
        // Check if this invoice is CreditMemo - teo_sarca [ 2804142 ]
        if (!getParent().isCreditMemo()) {
            throw new AdempiereException("InvoiceNotCreditMemo");
        }
        setOrgId(rmaLine.getOrgId());
        setRMALineId(rmaLine.getRMALineId());
        setDescription(rmaLine.getDescription());
        setLine(rmaLine.getLine());
        setChargeId(rmaLine.getChargeId());
        setProductId(rmaLine.getProductId());
        setUOMId(rmaLine.getUOMId());
        setTaxId(rmaLine.getTaxId());
        setPrice(rmaLine.getAmt());
        BigDecimal qty = rmaLine.getQty();
        if (rmaLine.getQtyInvoiced() != null) qty = qty.subtract(rmaLine.getQtyInvoiced());
        setQty(qty);
        setLineNetAmt();
        setTaxAmt();
        setLineTotalAmt(rmaLine.getLineNetAmt());
        setProjectId(rmaLine.getProjectId());
        setBusinessActivityId(rmaLine.getBusinessActivityId());
        setCampaignId(rmaLine.getCampaignId());
    }


    public void clearParent() {
        this.m_parent = null;
    }

    public void setClientOrg(PersistentObject po) {
        super.setClientOrg(po);
    }

    /**
     * @return matched qty
     */
    public BigDecimal getMatchedQty() {
        String sql =
                "SELECT COALESCE(SUM("
                        + MMatchInv.COLUMNNAME_Qty
                        + "),0)"
                        + " FROM "
                        + MMatchInv.Table_Name
                        + " WHERE "
                        + MMatchInv.COLUMNNAME_C_InvoiceLine_ID
                        + "=?"
                        + " AND "
                        + MMatchInv.COLUMNNAME_Processed
                        + "=?";
        return getSQLValueBDEx(sql, new Object[]{getInvoiceLineId(), true});
    }
} //	MInvoiceLine
