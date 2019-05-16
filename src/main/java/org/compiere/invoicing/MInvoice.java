package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MAllocationHdr;
import org.compiere.accounting.MAllocationLine;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MConversionRateUtil;
import org.compiere.accounting.MMatchInv;
import org.compiere.accounting.MMatchPO;
import org.compiere.accounting.MOrderLine;
import org.compiere.accounting.MPayment;
import org.compiere.accounting.MPaymentProcessor;
import org.compiere.accounting.MPeriod;
import org.compiere.accounting.MProduct;
import org.compiere.bank.MBankAccount;
import org.compiere.bo.MCurrencyKt;
import org.compiere.crm.MBPartner;
import org.compiere.crm.MUser;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.DocumentType;
import org.compiere.model.HasName;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_BPartner_Location;
import org.compiere.model.I_C_BankAccount;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceBatch;
import org.compiere.model.I_C_InvoiceBatchLine;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_InvoiceTax;
import org.compiere.model.I_M_MatchInv;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_PriceList_Version;
import org.compiere.model.User;
import org.compiere.order.BPartnerNoAddressException;
import org.compiere.order.MInOut;
import org.compiere.order.MInOutLine;
import org.compiere.order.MOrder;
import org.compiere.order.MRMALine;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.MSequence;
import org.compiere.orm.MSysConfig;
import org.compiere.orm.PO;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.product.MPriceList;
import org.compiere.product.MProductBOM;
import org.compiere.production.MProject;
import org.compiere.tax.IInvoiceTaxProvider;
import org.compiere.tax.MTax;
import org.compiere.tax.MTaxProvider;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.exceptions.DBException;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;
import software.hsharp.core.util.Environment;
import software.hsharp.modules.Module;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.orm.POKt.I_ZERO;
import static software.hsharp.core.orm.POKt.getAllIDs;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.executeUpdateEx;
import static software.hsharp.core.util.DBKt.forUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.getSQLValueEx;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Invoice Model. Please do not set DocStatus and C_DocType_ID directly. They are set in the
 * process() method. Use DocAction and C_DocTypeTarget_ID instead.
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * @version $Id: MInvoice.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 * @see http://sourceforge.net/tracker/?func=detail&atid=879335&aid=1948157&group_id=176962
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 * Modifications: Added RMA functionality (Ashley Ramdass) Modifications: Generate DocNo^
 * instead of using a new number whan an invoice is reversed (Diego Ruiz-globalqss)
 */
public class MInvoice extends X_C_Invoice implements DocAction, I_C_Invoice, IPODoc {
    /**
     *
     */
    private static final long serialVersionUID = -9210893813732918522L;
    /**
     * Cache
     */
    private static CCache<Integer, MInvoice> s_cache =
            new CCache<>(I_C_Invoice.Table_Name, 2); // 	2 minutes
    private static volatile boolean recursiveCall = false;
    /* Save array of documents to process AFTER completing this one */
    ArrayList<IPODoc> docsPostProcess = new ArrayList<>();
    /**
     * Open Amount
     */
    private BigDecimal m_openAmt = null;
    /**
     * Invoice Lines
     */
    private I_C_InvoiceLine[] m_lines;
    /**
     * Invoice Taxes
     */
    private I_C_InvoiceTax[] m_taxes;
    /**
     * Reversal Flag
     */
    private boolean m_reversal = false;
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;

    /**
     * ************************************************************************ Invoice Constructor
     *
     * @param C_Invoice_ID invoice or 0 for new
     */
    public MInvoice(Row row, int C_Invoice_ID) {
        super(row, C_Invoice_ID);
        if (C_Invoice_ID == 0) {
            setDocStatus(X_C_Invoice.DOCSTATUS_Drafted); // 	Draft
            setDocAction(X_C_Invoice.DOCACTION_Complete);
            //
            setPaymentRule(X_C_Invoice.PAYMENTRULE_OnCredit); // 	Payment Terms

            setDateInvoiced(new Timestamp(System.currentTimeMillis()));
            setDateAcct(new Timestamp(System.currentTimeMillis()));
            //
            setChargeAmt(Env.ZERO);
            setTotalLines(Env.ZERO);
            setGrandTotal(Env.ZERO);
            //
            setIsSOTrx(true);
            setIsTaxIncluded(false);
            setIsApproved(false);
            setIsDiscountPrinted(false);
            setIsPaid(false);
            setSendEMail(false);
            setIsPrinted(false);
            setIsTransferred(false);
            setIsSelfService(false);
            setIsPayScheduleValid(false);
            setIsInDispute(false);
            setPosted(false);
            super.setProcessed(false);
            setProcessing(false);
        }
    } //	MInvoice

    /**
     * Create Invoice from Order
     *
     * @param order              order
     * @param C_DocTypeTarget_ID target document type
     * @param invoiceDate        date or null
     */
    public MInvoice(MOrder order, int C_DocTypeTarget_ID, Timestamp invoiceDate) {
        this(null, 0);
        setClientOrg(order);
        setOrder(order); // 	set base settings
        //
        if (C_DocTypeTarget_ID <= 0) {
            MDocType odt = MDocTypeKt.getDocumentType(order.getDocumentTypeId());
            if (odt != null) {
                C_DocTypeTarget_ID = odt.getDocTypeInvoiceId();
                if (C_DocTypeTarget_ID <= 0)
                    throw new AdempiereException(
                            "@NotFound@ @C_DocTypeInvoice_ID@ - @C_DocType_ID@:"
                                    + odt.getTranslation(HasName.COLUMNNAME_Name));
            }
        }
        setTargetDocumentTypeId(C_DocTypeTarget_ID);
        if (invoiceDate != null) setDateInvoiced(invoiceDate);
        setDateAcct(getDateInvoiced());
        //
        setSalesRepresentativeId(order.getSalesRepresentativeId());
        //
        setBusinessPartnerId(order.getInvoiceBusinessPartnerId());
        setBusinessPartnerLocationId(order.getBusinessPartnerInvoicingLocationId());
        setUserId(order.getInvoiceUserId());
    } //	MInvoice

    /**
     * Create Invoice from Shipment
     *
     * @param ship        shipment
     * @param invoiceDate date or null
     */
    public MInvoice(org.compiere.order.MInOut ship, Timestamp invoiceDate) {
        this(null, 0);
        setClientOrg(ship);
        setShipment(ship); // 	set base settings
        //
        setTargetDocumentTypeId();
        if (invoiceDate != null) setDateInvoiced(invoiceDate);
        setDateAcct(getDateInvoiced());
        //
        setSalesRepresentativeId(ship.getSalesRepresentativeId());
    } //	MInvoice

    /**
     * Create Invoice from Batch Line
     *
     * @param batch batch
     * @param line  batch line
     */
    public MInvoice(I_C_InvoiceBatch batch, I_C_InvoiceBatchLine line) {
        this(null, 0);
        setClientOrg(line);
        setDocumentNo(line.getDocumentNo());
        //
        setIsSOTrx(batch.isSOTrx());
        I_C_BPartner bp = getBusinessPartnerService().getById(line.getBusinessPartnerId());
        setBPartner(bp); // 	defaults
        //
        setIsTaxIncluded(line.isTaxIncluded());
        //	May conflict with default price list
        setCurrencyId(batch.getCurrencyId());
        setConversionTypeId(batch.getConversionTypeId());
        //
        setDescription(batch.getDescription());
        //
        setTransactionOrganizationId(line.getTransactionOrganizationId());
        setProjectId(line.getProjectId());
        setBusinessActivityId(line.getBusinessActivityId());
        setUser1Id(line.getUser1Id());
        setUser2Id(line.getUser2Id());
        //
        setTargetDocumentTypeId(line.getDocumentTypeId());
        setDateInvoiced(line.getDateInvoiced());
        setDateAcct(line.getDateAcct());
        //
        setSalesRepresentativeId(batch.getSalesRepresentativeId());
        //
        setBusinessPartnerId(line.getBusinessPartnerId());
        setBusinessPartnerLocationId(line.getBusinessPartnerLocationId());
        setUserId(line.getUserId());
    } //	MInvoice

    /**
     * Get Payments Of BPartner
     *
     * @param C_BPartner_ID id
     * @return array
     */
    public static I_C_Invoice[] getOfBPartner(int C_BPartner_ID) {
        List<I_C_Invoice> list =
                new Query<I_C_Invoice>(I_C_Invoice.Table_Name, I_C_Invoice.COLUMNNAME_C_BPartner_ID + "=?")
                        .setParameters(C_BPartner_ID)
                        .list();
        return list.toArray(new I_C_Invoice[0]);
    } //	getOfBPartner

    /**
     * Create new Invoice by copying
     *
     * @param from               invoice
     * @param dateDoc            date of the document date
     * @param C_DocTypeTarget_ID target doc type
     * @param isSOTrx            sales order
     * @param counter            create counter links
     * @param setOrder           set Order links
     * @return Invoice
     */
    public static MInvoice copyFrom(
            MInvoice from,
            Timestamp dateDoc,
            Timestamp dateAcct,
            int C_DocTypeTarget_ID,
            boolean isSOTrx,
            boolean counter,
            boolean setOrder) {
        return copyFrom(
                from, dateDoc, dateAcct, C_DocTypeTarget_ID, isSOTrx, counter, setOrder, null);
    }

    /**
     * Create new Invoice by copying
     *
     * @param from               invoice
     * @param dateDoc            date of the document date
     * @param C_DocTypeTarget_ID target doc type
     * @param isSOTrx            sales order
     * @param counter            create counter links
     * @param setOrder           set Order links
     * @return Invoice
     */
    public static MInvoice copyFrom(
            MInvoice from,
            Timestamp dateDoc,
            Timestamp dateAcct,
            int C_DocTypeTarget_ID,
            boolean isSOTrx,
            boolean counter,
            boolean setOrder,
            String documentNo) {
        MInvoice to = new MInvoice(null, 0);
        PO.copyValues(from, to, from.getClientId(), from.getOrgId());
        to.setValueNoCheck("C_Invoice_ID", I_ZERO);
        to.setValueNoCheck("DocumentNo", documentNo);
        //
        to.setDocStatus(X_C_Invoice.DOCSTATUS_Drafted); // 	Draft
        to.setDocAction(X_C_Invoice.DOCACTION_Complete);
        //
        to.setDocumentTypeId(0);
        to.setTargetDocumentTypeId(C_DocTypeTarget_ID);
        to.setIsSOTrx(isSOTrx);
        //
        to.setDateInvoiced(dateDoc);
        to.setDateAcct(dateAcct);
        to.setDatePrinted(null);
        to.setIsPrinted(false);
        //
        to.setIsApproved(false);
        to.setPaymentId(0);
        to.setCashLineId(0);
        to.setIsPaid(false);
        to.setIsInDispute(false);
        //
        //	Amounts are updated by trigger when adding lines
        to.setGrandTotal(Env.ZERO);
        to.setTotalLines(Env.ZERO);
        //
        to.setIsTransferred(false);
        to.setPosted(false);
        to.setProcessed(false);
        // [ 1633721 ] Reverse Documents- Processing=Y
        to.setProcessing(false);
        //	delete references
        to.setIsSelfService(false);
        if (!setOrder) to.setOrderId(0);
        if (counter) {
            to.setRef_InvoiceId(from.getInvoiceId());
            MOrg org = MOrgKt.getOrg(from.getOrgId());
            int counterC_BPartner_ID = org.getLinkedBusinessPartnerId();
            if (counterC_BPartner_ID == 0) return null;
            to.setBPartner(MBPartner.get(counterC_BPartner_ID));
            //	Try to find Order link
            if (from.getOrderId() != 0) {
                MOrder peer = new MOrder(from.getOrderId());
                if (peer.getRef_OrderId() != 0) to.setOrderId(peer.getRef_OrderId());
            }
            // Try to find RMA link
            if (from.getRMAId() != 0) {
                MRMA peer = new MRMA(from.getRMAId());
                if (peer.getRef_RMAId() > 0) to.setRMAId(peer.getRef_RMAId());
            }
            //
        } else to.setRef_InvoiceId(0);

        to.saveEx();
        if (counter) from.setRef_InvoiceId(to.getInvoiceId());

        //	Lines
        if (to.copyLinesFrom(from, counter, setOrder) == 0)
            throw new IllegalStateException("Could not create Invoice Lines");

        return to;
    }

    /**
     * @param from               invoice
     * @param dateDoc            date of the document date
     * @param C_DocTypeTarget_ID target doc type
     * @param isSOTrx            sales order
     * @param counter            create counter links
     * @param trxName            trx
     * @param setOrder           set Order links
     * @return Invoice
     * @deprecated Create new Invoice by copying
     */
    public static MInvoice copyFrom(
            MInvoice from,
            Timestamp dateDoc,
            int C_DocTypeTarget_ID,
            boolean isSOTrx,
            boolean counter,
            String trxName,
            boolean setOrder) {
        MInvoice to =
                copyFrom(from, dateDoc, dateDoc, C_DocTypeTarget_ID, isSOTrx, counter, setOrder);
        return to;
    } //	copyFrom

    /**
     * Get MInvoice from Cache
     *
     * @param C_Invoice_ID id
     * @return MInvoice
     */
    public static MInvoice get(int C_Invoice_ID) {
        Integer key = new Integer(C_Invoice_ID);
        MInvoice retValue = s_cache.get(key);
        if (retValue != null) return retValue;
        retValue = new MInvoice(null, C_Invoice_ID);
        if (retValue.getId() != 0) s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * Overwrite Client/Org if required
     *
     * @param AD_Client_ID client
     * @param AD_Org_ID    org
     */
    public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
        super.setClientOrg(AD_Client_ID, AD_Org_ID);
    } //	setClientOrg

    /**
     * Set Business Partner Defaults & Details
     *
     * @param bp business partner
     */
    public void setBPartner(I_C_BPartner bp) {
        if (bp == null) return;

        setBusinessPartnerId(bp.getBusinessPartnerId());
        //	Set Defaults
        int ii = 0;
        if (isSOTrx()) ii = bp.getPaymentTermId();
        else ii = bp.getPurchaseOrderPaymentTermId();
        if (ii != 0) setPaymentTermId(ii);
        //
        if (isSOTrx()) ii = bp.getPriceListId();
        else ii = bp.getPurchaseOrderPriceListId();
        if (ii != 0) setPriceListId(ii);
        //
        String ss = bp.getPaymentRule();
        if (ss != null) setPaymentRule(ss);

        //	Set Locations
        List<I_C_BPartner_Location> locs = bp.getLocations();
        if (locs != null) {
            for (int i = 0; i < locs.size(); i++) {
                I_C_BPartner_Location loc = locs.get(i);
                if ((loc.getIsBillTo() && isSOTrx()) || (loc.isPayFrom() && !isSOTrx()))
                    setBusinessPartnerLocationId(loc.getBusinessPartnerLocationId());
            }
            //	set to first
            if (getBusinessPartnerLocationId() == 0 && locs.size() > 0)
                setBusinessPartnerLocationId(locs.get(0).getBusinessPartnerLocationId());
        }
        if (getBusinessPartnerLocationId() == 0)
            log.log(
                    Level.SEVERE,
                    new BPartnerNoAddressException(bp).getLocalizedMessage()); // TODO: throw exception?

        //	Set Contact
        List<User> contacts = bp.getContacts();
        if (contacts != null && contacts.size() == 1) setUserId(contacts.get(0).getUserId());
    } //	setBPartner

    /**
     * Set Order References
     *
     * @param order order
     */
    public void setOrder(MOrder order) {
        if (order == null) return;

        setOrderId(order.getOrderId());
        setIsSOTrx(order.isSOTrx());
        setIsDiscountPrinted(order.isDiscountPrinted());
        setIsSelfService(order.isSelfService());
        setSendEMail(order.isSendEMail());
        //
        setPriceListId(order.getPriceListId());
        setIsTaxIncluded(order.isTaxIncluded());
        setCurrencyId(order.getCurrencyId());
        setConversionTypeId(order.getConversionTypeId());
        //
        setPaymentRule(order.getPaymentRule());
        setPaymentTermId(order.getPaymentTermId());
        setPOReference(order.getPOReference());
        setDescription(order.getDescription());
        setDateOrdered(order.getDateOrdered());
        //
        setTransactionOrganizationId(order.getTransactionOrganizationId());
        setProjectId(order.getProjectId());
        setCampaignId(order.getCampaignId());
        setBusinessActivityId(order.getBusinessActivityId());
        setUser1Id(order.getUser1Id());
        setUser2Id(order.getUser2Id());
    } //	setOrder

    /**
     * Set Shipment References
     *
     * @param ship shipment
     */
    public void setShipment(MInOut ship) {
        if (ship == null) return;

        setIsSOTrx(ship.isSOTrx());
        //
        I_C_BPartner bp = getBusinessPartnerService().getById(ship.getBusinessPartnerId());
        setBPartner(bp);
        //
        setUserId(ship.getUserId());
        //
        setSendEMail(ship.isSendEMail());
        //
        setPOReference(ship.getPOReference());
        setDescription(ship.getDescription());
        setDateOrdered(ship.getDateOrdered());
        //
        setTransactionOrganizationId(ship.getTransactionOrganizationId());
        setProjectId(ship.getProjectId());
        setCampaignId(ship.getCampaignId());
        setBusinessActivityId(ship.getBusinessActivityId());
        setUser1Id(ship.getUser1Id());
        setUser2Id(ship.getUser2Id());
        //
        if (ship.getOrderId() != 0) {
            setOrderId(ship.getOrderId());
            MOrder order = new MOrder(ship.getOrderId());
            setIsDiscountPrinted(order.isDiscountPrinted());
            setPriceListId(order.getPriceListId());
            setIsTaxIncluded(order.isTaxIncluded());
            setCurrencyId(order.getCurrencyId());
            setConversionTypeId(order.getConversionTypeId());
            setPaymentRule(order.getPaymentRule());
            setPaymentTermId(order.getPaymentTermId());
            //
            MDocType dt = MDocTypeKt.getDocumentType(order.getDocumentTypeId());
            if (dt.getDocTypeInvoiceId() != 0) setTargetDocumentTypeId(dt.getDocTypeInvoiceId());
            // Overwrite Invoice BPartner
            setBusinessPartnerId(order.getInvoiceBusinessPartnerId());
            // Overwrite Invoice Address
            setBusinessPartnerLocationId(order.getBusinessPartnerInvoicingLocationId());
            // Overwrite Contact
            setUserId(order.getInvoiceUserId());
            //
        }
        // Check if Shipment/Receipt is based on RMA
        if (ship.getRMAId() != 0) {
            setRMAId(ship.getRMAId());

            MRMA rma = new MRMA(ship.getRMAId());
            // Retrieves the invoice DocType
            MDocType dt = MDocTypeKt.getDocumentType(rma.getDocumentTypeId());
            if (dt.getDocTypeInvoiceId() != 0) {
                setTargetDocumentTypeId(dt.getDocTypeInvoiceId());
            }
            setIsSOTrx(rma.isSOTrx());

            org.compiere.order.MOrder rmaOrder = rma.getOriginalOrder();
            if (rmaOrder != null) {
                setPriceListId(rmaOrder.getPriceListId());
                setIsTaxIncluded(rmaOrder.isTaxIncluded());
                setCurrencyId(rmaOrder.getCurrencyId());
                setConversionTypeId(rmaOrder.getConversionTypeId());
                setPaymentRule(rmaOrder.getPaymentRule());
                setPaymentTermId(rmaOrder.getPaymentTermId());
                setBusinessPartnerLocationId(rmaOrder.getBusinessPartnerInvoicingLocationId());
            }
        }
    } //	setShipment

    /**
     * Set Target Document Type
     *
     * @param DocBaseType doc type MDocType.DOCBASETYPE_
     */
    public void setTargetDocumentTypeId(String DocBaseType) {
        String sql =
                "SELECT C_DocType_ID FROM C_DocType "
                        + "WHERE AD_Client_ID=? AND orgId in (0,?) AND DocBaseType=?"
                        + " AND IsActive='Y' "
                        + "ORDER BY IsDefault DESC, orgId DESC";
        int C_DocType_ID = getSQLValueEx(sql, getClientId(), getOrgId(), DocBaseType);
        if (C_DocType_ID <= 0)
            log.log(Level.SEVERE, "Not found for AD_Client_ID=" + getClientId() + " - " + DocBaseType);
        else {
            log.fine(DocBaseType);
            setTargetDocumentTypeId(C_DocType_ID);
            boolean isSOTrx =
                    MDocType.DOCBASETYPE_ARInvoice.equals(DocBaseType)
                            || MDocType.DOCBASETYPE_ARCreditMemo.equals(DocBaseType);
            setIsSOTrx(isSOTrx);
        }
    } //	setTargetDocumentTypeId

    /**
     * Set Target Document Type. Based on SO flag AP/AP Invoice
     */
    public void setTargetDocumentTypeId() {
        if (getTargetDocumentTypeId() > 0) return;
        if (isSOTrx()) setTargetDocumentTypeId(MDocType.DOCBASETYPE_ARInvoice);
        else setTargetDocumentTypeId(MDocType.DOCBASETYPE_APInvoice);
    } //	setTargetDocumentTypeId

    /**
     * Get Grand Total
     *
     * @param creditMemoAdjusted adjusted for CM (negative)
     * @return grand total
     */
    public BigDecimal getGrandTotal(boolean creditMemoAdjusted) {
        if (!creditMemoAdjusted) return super.getGrandTotal();
        //
        BigDecimal amt = getGrandTotal();
        if (isCreditMemo()) return amt.negate();
        return amt;
    } //	getGrandTotal

    /**
     * Get Invoice Lines of Invoice
     *
     * @param whereClause starting with AND
     * @return lines
     */
    private I_C_InvoiceLine[] getLines(String whereClause) {
        String whereClauseFinal = "C_Invoice_ID=? ";
        if (whereClause != null) whereClauseFinal += whereClause;
        List<I_C_InvoiceLine> list =
                new Query<I_C_InvoiceLine>(I_C_InvoiceLine.Table_Name, whereClauseFinal)
                        .setParameters(getInvoiceId())
                        .setOrderBy("Line, C_InvoiceLine_ID")
                        .list();
        return list.toArray(new I_C_InvoiceLine[list.size()]);
    } //	getLines

    /**
     * Get Invoice Lines
     *
     * @param requery
     * @return lines
     */
    public I_C_InvoiceLine[] getLines(boolean requery) {
        if (m_lines == null || m_lines.length == 0 || requery) m_lines = getLines(null);
        return m_lines;
    } //	getLines

    /**
     * Get Lines of Invoice
     *
     * @return lines
     */
    public I_C_InvoiceLine[] getLines() {
        return getLines(false);
    } //	getLines

    /**
     * Renumber Lines
     *
     * @param step start and step
     */
    public void renumberLines(int step) {
        int number = step;
        I_C_InvoiceLine[] lines = getLines(false);
        for (I_C_InvoiceLine line : lines) {
            line.setLine(number);
            line.saveEx();
            number += step;
        }
        m_lines = null;
    } //	renumberLines

    /**
     * Copy Lines From other Invoice.
     *
     * @param otherInvoice invoice
     * @param counter      create counter links
     * @param setOrder     set order links
     * @return number of lines copied
     */
    public int copyLinesFrom(MInvoice otherInvoice, boolean counter, boolean setOrder) {
        if (isProcessed() || isPosted() || otherInvoice == null) return 0;
        I_C_InvoiceLine[] fromLines = otherInvoice.getLines(false);
        int count = 0;
        for (I_C_InvoiceLine i_c_invoiceLine : fromLines) {
            MInvoiceLine line = new MInvoiceLine(0);
            if (counter) //	header
                PO.copyValues((PO)i_c_invoiceLine, line, getClientId(), getOrgId());
            else PO.copyValues((PO)i_c_invoiceLine, line, i_c_invoiceLine.getClientId(), i_c_invoiceLine.getOrgId());
            line.setInvoiceId(getInvoiceId());
            line.setInvoice(this);
            line.setValueNoCheck("C_InvoiceLine_ID", I_ZERO); // new
            //	Reset
            if (!setOrder) line.setOrderLineId(0);
            line.setRef_InvoiceLineId(0);
            line.setInOutLineId(0);
            line.setA_AssetId(0);
            line.setAttributeSetInstanceId(0);
            line.setS_ResourceAssignmentId(0);
            //	New Tax
            if (getBusinessPartnerId() != otherInvoice.getBusinessPartnerId()) line.setTax(); // 	recalculate
            //
            if (counter) {
                line.setRef_InvoiceLineId(i_c_invoiceLine.getInvoiceLineId());
                if (i_c_invoiceLine.getOrderLineId() != 0) {
                    MOrderLine peer = new MOrderLine(i_c_invoiceLine.getOrderLineId());
                    if (peer.getRef_OrderLineId() != 0) line.setOrderLineId(peer.getRef_OrderLineId());
                }
                line.setInOutLineId(0);
                if (i_c_invoiceLine.getInOutLineId() != 0) {
                    MInOutLine peer =
                            new MInOutLine(
                                    i_c_invoiceLine.getInOutLineId());
                    if (peer.getReferencedInOutLineId() != 0) line.setInOutLineId(peer.getReferencedInOutLineId());
                }
            }
            //
            line.setProcessed(false);
            if (line.save()) count++;
            //	Cross Link
            if (counter) {
                i_c_invoiceLine.setRef_InvoiceLineId(line.getInvoiceLineId());
                i_c_invoiceLine.saveEx();
            }

            // MZ Goodwill
            // copy the landed cost
            line.copyLandedCostFrom(i_c_invoiceLine);
            line.allocateLandedCosts();
            // end MZ
        }
        if (fromLines.length != count)
            log.log(Level.SEVERE, "Line difference - From=" + fromLines.length + " <> Saved=" + count);
        return count;
    } //	copyLinesFrom

    /**
     * Is Reversal
     *
     * @return reversal
     */
    public boolean isReversal() {
        return m_reversal;
    } //	isReversal

    /**
     * Set Reversal
     *
     * @param reversal reversal
     */
    public void setReversal(boolean reversal) {
        m_reversal = reversal;
    } //	setReversal

    /**
     * Get Taxes
     *
     * @param requery requery
     * @return array of taxes
     */
    public I_C_InvoiceTax[] getTaxes(boolean requery) {
        if (m_taxes != null && !requery) return m_taxes;

        final String whereClause = MInvoiceTax.COLUMNNAME_C_Invoice_ID + "=?";
        List<I_C_InvoiceTax> list =
                new Query<I_C_InvoiceTax>(I_C_InvoiceTax.Table_Name, whereClause)
                        .setParameters(getId())
                        .list();
        m_taxes = list.toArray(new I_C_InvoiceTax[0]);
        return m_taxes;
    } //	getTaxes

    /**
     * Add to Description
     *
     * @param description text
     */
    public void addDescription(String description) {
        String desc = getDescription();
        if (desc == null) setDescription(description);
        else {
            StringBuilder msgd = new StringBuilder(desc).append(" | ").append(description);
            setDescription(msgd.toString());
        }
    } //	addDescription

    /**
     * Is it a Credit Memo?
     *
     * @return true if CM
     */
    public boolean isCreditMemo() {
        MDocType dt =
                MDocTypeKt.getDocumentType(
                        getDocumentTypeId() == 0 ? getTargetDocumentTypeId() : getDocumentTypeId());
        return MDocType.DOCBASETYPE_APCreditMemo.equals(dt.getDocBaseType())
                || MDocType.DOCBASETYPE_ARCreditMemo.equals(dt.getDocBaseType());
    } //	isCreditMemo

    /**
     * Set Processed. Propergate to Lines/Taxes
     *
     * @param processed processed
     */
    public void setProcessed(boolean processed) {
        super.setProcessed(processed);
        if (getId() == 0) return;
        StringBuilder set =
                new StringBuilder("SET Processed='")
                        .append((processed ? "Y" : "N"))
                        .append("' WHERE C_Invoice_ID=")
                        .append(getInvoiceId());

        StringBuilder msgdb = new StringBuilder("UPDATE C_InvoiceLine ").append(set);
        int noLine = executeUpdate(msgdb.toString());
        msgdb = new StringBuilder("UPDATE C_InvoiceTax ").append(set);
        int noTax = executeUpdate(msgdb.toString());
        m_lines = null;
        m_taxes = null;
        if (log.isLoggable(Level.FINE)) log.fine(processed + " - Lines=" + noLine + ", Tax=" + noTax);
    } //	setProcessed

    /**
     * Validate Invoice Pay Schedule
     *
     * @return pay schedule is valid
     */
    public boolean validatePaySchedule() {
        MInvoicePaySchedule[] schedule =
                MInvoicePaySchedule.getInvoicePaySchedule(getInvoiceId(), 0);
        if (log.isLoggable(Level.FINE)) log.fine("#" + schedule.length);
        if (schedule.length == 0) {
            setIsPayScheduleValid(false);
            return false;
        }
        //	Add up due amounts
        BigDecimal total = Env.ZERO;
        for (int i = 0; i < schedule.length; i++) {
            schedule[i].setParent(this);
            BigDecimal due = schedule[i].getDueAmt();
            if (due != null) total = total.add(due);
        }
        boolean valid = getGrandTotal().compareTo(total) == 0;
        setIsPayScheduleValid(valid);

        //	Update Schedule Lines
        for (int i = 0; i < schedule.length; i++) {
            if (schedule[i].isValid() != valid) {
                schedule[i].setIsValid(valid);
                schedule[i].saveEx();
            }
        }
        return valid;
    } //	validatePaySchedule

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        log.fine("");
        //	No Partner Info - set Template
        if (getBusinessPartnerId() == 0) setBPartner(new Environment<Module>().getModule().getBusinessPartnerService().getTemplate());
        if (getBusinessPartnerLocationId() == 0)
            setBPartner(getBusinessPartnerService().getById(getBusinessPartnerId()));

        //	Price List
        if (getPriceListId() == 0) {
            int ii = Env.getContextAsInt("#M_PriceList_ID");
            if (ii != 0) {
                MPriceList pl = new MPriceList(ii);
                if (isSOTrx() == pl.isSOPriceList()) setPriceListId(ii);
            }

            if (getPriceListId() == 0) {
                String sql =
                        "SELECT M_PriceList_ID FROM M_PriceList WHERE AD_Client_ID=? AND IsSOPriceList=? AND IsActive='Y' ORDER BY IsDefault DESC";
                ii = getSQLValue(sql, getClientId(), isSOTrx());
                if (ii != 0) setPriceListId(ii);
            }
        }

        //	Currency
        if (getCurrencyId() == 0) {
            String sql = "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID=?";
            int ii = getSQLValue(sql, getPriceListId());
            if (ii != 0) setCurrencyId(ii);
            else setCurrencyId(Env.getContextAsInt("#C_Currency_ID"));
        }

        //	Sales Rep
        if (getSalesRepresentativeId() == 0) {
            int ii = Env.getContextAsInt("#SalesRep_ID");
            if (ii != 0) setSalesRepresentativeId(ii);
        }

        //	Document Type
        if (getDocumentTypeId() == 0) setDocumentTypeId(0); // 	make sure it's set to 0
        if (getTargetDocumentTypeId() == 0)
            setTargetDocumentTypeId(
                    isSOTrx() ? MDocType.DOCBASETYPE_ARInvoice : MDocType.DOCBASETYPE_APInvoice);

        //	Payment Term
        if (getPaymentTermId() == 0) {
            int ii = Env.getContextAsInt("#C_PaymentTerm_ID");
            if (ii != 0) setPaymentTermId(ii);
            else {
                String sql =
                        "SELECT C_PaymentTerm_ID FROM C_PaymentTerm WHERE AD_Client_ID=? AND IsDefault='Y' AND IsActive='Y'";
                ii = getSQLValue(sql, getClientId());
                if (ii != 0) setPaymentTermId(ii);
            }
        }

        // assign cash plan line from order
        if (getOrderId() > 0 && getCashPlanLineId() <= 0) {
            MOrder order = new MOrder(getOrderId());
            if (order.getCashPlanLineId() > 0) setCashPlanLineId(order.getCashPlanLineId());
        }

        // IDEMPIERE-1597 Price List and Date must be not-updateable
        if (!newRecord
                && (isValueChanged(I_C_Invoice.COLUMNNAME_M_PriceList_ID)
                || isValueChanged(I_C_Invoice.COLUMNNAME_DateInvoiced))) {
            int cnt =
                    getSQLValueEx(
                            "SELECT COUNT(*) FROM C_InvoiceLine WHERE C_Invoice_ID=? AND M_Product_ID>0",
                            getInvoiceId());
            if (cnt > 0) {
                if (isValueChanged(I_C_Invoice.COLUMNNAME_M_PriceList_ID)) {
                    log.saveError("Error", MsgKt.getMsg("CannotChangePlIn"));
                    return false;
                }
                if (isValueChanged(I_C_Invoice.COLUMNNAME_DateInvoiced)) {
                    I_M_PriceList pList = MPriceList.get(getPriceListId());
                    I_M_PriceList_Version plOld =
                            pList.getPriceListVersion(
                                    (Timestamp) getValueOld(I_C_Invoice.COLUMNNAME_DateInvoiced));
                    I_M_PriceList_Version plNew =
                            pList.getPriceListVersion((Timestamp) getValue(I_C_Invoice.COLUMNNAME_DateInvoiced));
                    if (plNew == null || !plNew.equals(plOld)) {
                        log.saveError("Error", MsgKt.getMsg("CannotChangeDateInvoiced"));
                        return false;
                    }
                }
            }
        }

        if (!recursiveCall
                && (!newRecord && isValueChanged(I_C_Invoice.COLUMNNAME_C_PaymentTerm_ID))) {
            recursiveCall = true;
            try {
                MPaymentTerm pt = new MPaymentTerm(getPaymentTermId());
                boolean valid = pt.apply(this);
                setIsPayScheduleValid(valid);
            } catch (Exception e) {
                throw e;
            } finally {
                recursiveCall = false;
            }
        }

        return true;
    } //	beforeSave

    /**
     * Before Delete
     *
     * @return true if it can be deleted
     */
    protected boolean beforeDelete() {
        if (getOrderId() != 0) {
            // Load invoice lines for afterDelete()
            getLines();
        }
        return true;
    } //	beforeDelete

    /**
     * After Delete
     *
     * @param success success
     * @return deleted
     */
    protected boolean afterDelete(boolean success) {
        // If delete invoice failed then do nothing
        if (!success) return success;

        if (getOrderId() != 0) {
            // reset shipment line invoiced flag
            I_C_InvoiceLine[] lines = getLines(false);
            for (I_C_InvoiceLine line : lines) {
                if (line.getInOutLineId() > 0) {
                    MInOutLine sLine =
                            new MInOutLine(
                                    line.getInOutLineId());
                    sLine.setIsInvoiced(false);
                    sLine.saveEx();
                }
            }
        }
        return true;
    } // afterDelete

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb =
                new StringBuilder("MInvoice[")
                        .append(getId())
                        .append("-")
                        .append(getDocumentNo())
                        .append(",GrandTotal=")
                        .append(getGrandTotal());
        if (m_lines != null) sb.append(" (#").append(m_lines.length).append(")");
        sb.append("]");
        return sb.toString();
    } //	toString

    /**
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        StringBuilder msgreturn =
                new StringBuilder().append(dt.getNameTrl()).append(" ").append(getDocumentNo());
        return msgreturn.toString();
    } //	getDocumentInfo

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success || newRecord) return success;

        if (isValueChanged("AD_Org_ID")) {
            StringBuilder sql =
                    new StringBuilder("UPDATE C_InvoiceLine ol")
                            .append(" SET orgId =")
                            .append("(SELECT AD_Org_ID")
                            .append(" FROM C_Invoice o WHERE ol.C_Invoice_ID=o.C_Invoice_ID) ")
                            .append("WHERE C_Invoice_ID=")
                            .append(getInvoiceId());
            int no = executeUpdate(sql.toString());
            if (log.isLoggable(Level.FINE)) log.fine("Lines -> #" + no);
        }
        return true;
    } //	afterSave

    /**
     * Set Price List (and Currency) when valid
     *
     * @param M_PriceList_ID price list
     */
    @Override
    public void setPriceListId(int M_PriceList_ID) {
        I_M_PriceList pl = MPriceList.get(M_PriceList_ID);
        if (pl != null) {
            setCurrencyId(pl.getCurrencyId());
            super.setPriceListId(M_PriceList_ID);
        }
    } //	setPriceListId

    /**
     * Get Allocated Amt in Invoice Currency
     *
     * @return pos/neg amount or null
     */
    public BigDecimal getAllocatedAmt() {
        BigDecimal retValue = null;
        String sql =
                "SELECT SUM(currencyConvert(al.Amount+al.DiscountAmt+al.WriteOffAmt,"
                        + "ah.C_Currency_ID, i.C_Currency_ID,ah.DateTrx,COALESCE(i.C_ConversionType_ID,0), al.AD_Client_ID,al.orgId)) "
                        + "FROM C_AllocationLine al"
                        + " INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID=ah.C_AllocationHdr_ID)"
                        + " INNER JOIN C_Invoice i ON (al.C_Invoice_ID=i.C_Invoice_ID) "
                        + "WHERE al.C_Invoice_ID=?"
                        + " AND ah.IsActive='Y' AND al.IsActive='Y'";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getInvoiceId());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retValue = rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        //	log.fine("getAllocatedAmt - " + retValue);
        //	? ROUND(NVL(v_AllocatedAmt,0), 2);
        return retValue;
    } //	getAllocatedAmt

    /**
     * Test Allocation (and set paid flag)
     *
     * @return true if updated
     */
    public boolean testAllocation(boolean beingCompleted) {
        boolean change = false;

        if (isProcessed() || beingCompleted) {
            BigDecimal alloc = getAllocatedAmt(); // 	absolute
            if (alloc == null) alloc = Env.ZERO;
            BigDecimal total = getGrandTotal();
            if (!isSOTrx()) total = total.negate();
            if (isCreditMemo()) total = total.negate();
            boolean test = total.compareTo(alloc) == 0;
            change = test != isPaid();
            if (change) setIsPaid(test);
            if (log.isLoggable(Level.FINE)) log.fine("Paid=" + test + " (" + alloc + "=" + total + ")");
        }

        return change;
    } //	testAllocation

    public boolean testAllocation() {
        return testAllocation(false);
    }

    /**
     * Get Open Amount. Used by web interface
     *
     * @return Open Amt
     */
    public BigDecimal getOpenAmt() {
        return getOpenAmt(true, null);
    } //	getOpenAmt

    /**
     * Get Open Amount
     *
     * @param creditMemoAdjusted adjusted for CM (negative)
     * @param paymentDate        ignored Payment Date
     * @return Open Amt
     */
    public BigDecimal getOpenAmt(boolean creditMemoAdjusted, Timestamp paymentDate) {
        if (isPaid()) return Env.ZERO;
        //
        if (m_openAmt == null) {
            m_openAmt = getGrandTotal();
            //	Payment Discount
            //	Payment Schedule
            BigDecimal allocated = getAllocatedAmt();
            if (allocated != null) {
                allocated = allocated.abs(); // 	is absolute
                m_openAmt = m_openAmt.subtract(allocated);
            }
        }
        //
        if (!creditMemoAdjusted) return m_openAmt;
        if (isCreditMemo()) return m_openAmt.negate();
        return m_openAmt;
    } //	getOpenAmt

    /**
     * Get Currency Precision
     *
     * @return precision
     */
    public int getPrecision() {
        return MCurrencyKt.getCurrencyStdPrecision(getCurrencyId());
    } //	getPrecision

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
        if (log.isLoggable(Level.INFO)) log.info("invalidateIt - " + toString());
        setDocAction(X_C_Invoice.DOCACTION_Prepare);
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

        MPeriod.testPeriodOpen(getDateAcct(), getTargetDocumentTypeId(), getOrgId());

        //	Lines
        I_C_InvoiceLine[] lines = getLines(true);
        if (lines.length == 0) {
            m_processMsg = "@NoLines@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Convert/Check DocType
        if (getDocumentTypeId() != getTargetDocumentTypeId()) setDocumentTypeId(getTargetDocumentTypeId());
        if (getDocumentTypeId() == 0) {
            m_processMsg = "No Document Type";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        explodeBOM();
        if (!calculateTaxTotal()) //	setTotals
        {
            m_processMsg = "Error calculating Tax";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        if (getGrandTotal().signum() != 0
                && (X_C_Invoice.PAYMENTRULE_OnCredit.equals(getPaymentRule())
                || X_C_Invoice.PAYMENTRULE_DirectDebit.equals(getPaymentRule()))) {
            if (!createPaySchedule()) {
                m_processMsg = "@ErrorPaymentSchedule@";
                return DocAction.Companion.getSTATUS_Invalid();
            }
        } else {
            if (MInvoicePaySchedule.getInvoicePaySchedule(getInvoiceId(), 0)
                    .length
                    > 0) {
                m_processMsg = "@ErrorPaymentSchedule@";
                return DocAction.Companion.getSTATUS_Invalid();
            }
        }

        //	Credit Status
        if (isSOTrx()) {
            MDocType doc = (MDocType) getDocTypeTarget();
            // IDEMPIERE-365 - just check credit if is going to increase the debt
            if ((doc.getDocBaseType().equals(MDocType.DOCBASETYPE_ARCreditMemo)
                    && getGrandTotal().signum() < 0)
                    || (doc.getDocBaseType().equals(MDocType.DOCBASETYPE_ARInvoice)
                    && getGrandTotal().signum() > 0)) {
                I_C_BPartner bp =  getBusinessPartnerService().getById(getBusinessPartnerId());
                if (MBPartner.SOCREDITSTATUS_CreditStop.equals(bp.getSOCreditStatus())) {
                    m_processMsg =
                            "@BPartnerCreditStop@ - @TotalOpenBalance@="
                                    + bp.getTotalOpenBalance()
                                    + ", @SO_CreditLimit@="
                                    + bp.getSalesOrderCreditLimit();
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
        }

        //	Landed Costs
        if (!isSOTrx()) {
            for (I_C_InvoiceLine line : lines) {
                String error = line.allocateLandedCosts();
                if (error != null && error.length() > 0) {
                    m_processMsg = error;
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            }
        }

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        //	Add up Amounts
        m_justPrepared = true;
        if (!X_C_Invoice.DOCACTION_Complete.equals(getDocAction()))
            setDocAction(X_C_Invoice.DOCACTION_Complete);
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

    /**
     * Explode non stocked BOM.
     */
    private void explodeBOM() {
        String where =
                "AND IsActive='Y' AND EXISTS "
                        + "(SELECT * FROM M_Product p WHERE C_InvoiceLine.M_Product_ID=p.M_Product_ID"
                        + " AND	p.IsBOM='Y' AND p.IsVerified='Y' AND p.IsStocked='N')";
        //
        String sql = "SELECT COUNT(*) FROM C_InvoiceLine " + "WHERE C_Invoice_ID=? " + where;
        int count = getSQLValueEx(sql, getInvoiceId());
        while (count != 0) {
            renumberLines(100);

            //	Order Lines with non-stocked BOMs
            I_C_InvoiceLine[] lines = getLines(where);
            for (I_C_InvoiceLine line : lines) {
                MProduct product = MProduct.get(line.getProductId());
                if (log.isLoggable(Level.FINE)) log.fine(product.getName());
                //	New Lines
                int lineNo = line.getLine();

                for (MProductBOM bom : MProductBOM.getBOMLines(product)) {
                    MInvoiceLine newLine = new MInvoiceLine(this);
                    newLine.setLine(++lineNo);
                    newLine.setProductId(bom.getBOMProductId(), true);
                    newLine.setQty(line.getQtyInvoiced().multiply(bom.getBOMQty()));
                    if (bom.getDescription() != null) newLine.setDescription(bom.getDescription());
                    newLine.setPrice();
                    newLine.saveEx();
                }

                //	Convert into Comment Line
                line.setProductId(0);
                line.setAttributeSetInstanceId(0);
                line.setPriceEntered(Env.ZERO);
                line.setPriceActual(Env.ZERO);
                line.setPriceLimit(Env.ZERO);
                line.setPriceList(Env.ZERO);
                line.setLineNetAmt(Env.ZERO);
                //
                StringBuilder description = new StringBuilder().append(product.getName());
                if (product.getDescription() != null)
                    description.append(" ").append(product.getDescription());
                if (line.getDescription() != null) description.append(" ").append(line.getDescription());
                line.setDescription(description.toString());
                line.saveEx();
            } //	for all lines with BOM

            m_lines = null;
            count = getSQLValue(sql, getInvoiceId());
            renumberLines(10);
        } //	while count != 0
    } //	explodeBOM

    /**
     * Calculate Tax and Total
     *
     * @return true if calculated
     */
    public boolean calculateTaxTotal() {
        log.fine("");
        //	Delete Taxes
        StringBuilder msgdb =
                new StringBuilder("DELETE C_InvoiceTax WHERE C_Invoice_ID=").append(getInvoiceId());
        executeUpdateEx(msgdb.toString());
        m_taxes = null;

        MTaxProvider[] providers = getTaxProviders();
        for (MTaxProvider provider : providers) {
            IInvoiceTaxProvider calculator =
                    MTaxProvider.getTaxProvider(provider, new StandardInvoiceTaxProvider());
            if (calculator == null) throw new AdempiereException(MsgKt.getMsg("TaxNoProvider"));

            if (!calculator.calculateInvoiceTaxTotal(provider, this)) return false;
        }
        return true;
    } //	calculateTaxTotal

    /**
     * (Re) Create Pay Schedule
     *
     * @return true if valid schedule
     */
    private boolean createPaySchedule() {
        if (getPaymentTermId() == 0) return false;
        MPaymentTerm pt = new MPaymentTerm(getPaymentTermId());
        if (log.isLoggable(Level.FINE)) log.fine(pt.toString());

        int numSchema = pt.getSchedule(false).length;

        MInvoicePaySchedule[] schedule =
                MInvoicePaySchedule.getInvoicePaySchedule(getInvoiceId(), 0);

        if (schedule.length > 0) {
            if (numSchema == 0)
                return false; // created a schedule for a payment term that doesn't manage schedule
            return validatePaySchedule();
        } else {
            boolean isValid = pt.apply(this); // 	calls validate pay schedule
            if (numSchema == 0) return true; // no schedule, no schema, OK
            else return isValid;
        }
    } //	createPaySchedule

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
        StringBuilder info = new StringBuilder();

        // POS supports multiple payments
        boolean fromPOS = false;
        if (getOrderId() > 0) {
            fromPOS = getOrder().getPOSId() > 0;
        }

        //	Create Cash Payment
        if (X_C_Invoice.PAYMENTRULE_Cash.equals(getPaymentRule()) && !fromPOS) {
            String whereClause = "AD_Org_ID=? AND C_Currency_ID=?";
            I_C_BankAccount ba =
                    new Query<I_C_BankAccount>(MBankAccount.Table_Name, whereClause)
                            .setParameters(getOrgId(), getCurrencyId())
                            .setOnlyActiveRecords(true)
                            .setOrderBy("IsDefault DESC")
                            .first();
            if (ba == null) {
                m_processMsg = "@NoAccountOrgCurrency@";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }

            String docBaseType = "";
            if (isSOTrx()) docBaseType = MDocType.DOCBASETYPE_ARReceipt;
            else docBaseType = MDocType.DOCBASETYPE_APPayment;

            DocumentType[] doctypes = MDocTypeKt.getDocumentTypeOfDocBaseType(docBaseType);
            if (doctypes.length == 0) {
                m_processMsg = "No document type ";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            DocumentType doctype = null;
            for (DocumentType doc : doctypes) {
                if (doc.getOrgId() == this.getOrgId()) {
                    doctype = doc;
                    break;
                }
            }
            if (doctype == null) doctype = doctypes[0];

            MPayment payment = new MPayment(0);
            payment.setOrgId(getOrgId());
            payment.setTenderType(MPayment.TENDERTYPE_Cash);
            payment.setBankAccountId(ba.getBankAccountId());
            payment.setBusinessPartnerId(getBusinessPartnerId());
            payment.setInvoiceId(getInvoiceId());
            payment.setCurrencyId(getCurrencyId());
            payment.setDocumentTypeId(doctype.getDocTypeId());
            if (isCreditMemo()) payment.setPayAmt(getGrandTotal().negate());
            else payment.setPayAmt(getGrandTotal());
            payment.setIsPrepayment(false);
            payment.setDateAcct(getDateAcct());
            payment.setDateTrx(getDateInvoiced());

            //	Save payment
            payment.saveEx();

            payment.setDocAction(MPayment.DOCACTION_Complete);
            if (!payment.processIt(MPayment.DOCACTION_Complete)) {
                m_processMsg = "Cannot Complete the Payment : [" + payment.getProcessMsg() + "] " + payment;
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }

            payment.saveEx();
            info.append("@C_Payment_ID@: " + payment.getDocumentInfo());

            // IDEMPIERE-2588 - add the allocation generation with the payment
            if (payment.getJustCreatedAllocInv() != null)
                addDocsPostProcess(payment.getJustCreatedAllocInv());
        } //	Payment

        //	Update Order & Match
        int matchInv = 0;
        int matchPO = 0;
        I_C_InvoiceLine[] lines = getLines(false);
        for (I_C_InvoiceLine line : lines) {
            //	Matching - Inv-Shipment
            if (!isSOTrx()
                    && line.getInOutLineId() != 0
                    && line.getProductId() != 0
                    && !isReversal()) {
                MInOutLine receiptLine =
                        new MInOutLine(line.getInOutLineId());
                BigDecimal matchQty = line.getQtyInvoiced();

                if (receiptLine.getMovementQty().compareTo(matchQty) < 0)
                    matchQty = receiptLine.getMovementQty();

                MMatchInv inv = new MMatchInv(line, getDateInvoiced(), matchQty);
                if (!inv.save()) {
                    m_processMsg = CLogger.retrieveErrorString("Could not create Invoice Matching");
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
                matchInv++;
                addDocsPostProcess(inv);
            }

            //	Update Order Line
            MOrderLine ol = null;
            if (line.getOrderLineId() != 0) {
                if (isSOTrx() || line.getProductId() == 0) {
                    ol = new MOrderLine(line.getOrderLineId());
                    if (line.getQtyInvoiced() != null)
                        ol.setQtyInvoiced(ol.getQtyInvoiced().add(line.getQtyInvoiced()));
                    if (!ol.save()) {
                        m_processMsg = "Could not update Order Line";
                        return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                    }
                }
                //	Order Invoiced Qty updated via Matching Inv-PO
                else if (!isSOTrx() && line.getProductId() != 0 && !isReversal()) {
                    //	MatchPO is created also from MInOut when Invoice exists before Shipment
                    BigDecimal matchQty = line.getQtyInvoiced();
                    MMatchPO po = MMatchPO.create(line, null, getDateInvoiced(), matchQty);
                    if (po != null) {
                        if (!po.save()) {
                            m_processMsg = "Could not create PO Matching";
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                        matchPO++;
                        if (!po.isPosted()
                                && po.getInOutLineId()
                                > 0) // match po don't post if receipt is not assigned, and it doesn't create
                            // avg po record
                            addDocsPostProcess(po);

                        I_M_MatchInv[] matchInvoices =
                                MMatchInv.getInvoiceLine(line.getInvoiceLineId());
                        if (matchInvoices != null && matchInvoices.length > 0) {
                            for (I_M_MatchInv matchInvoice : matchInvoices) {
                                if (!matchInvoice.isPosted()) {
                                    addDocsPostProcess(matchInvoice);
                                }
                            }
                        }
                    }
                }
            }

            // Update QtyInvoiced RMA Line
            if (line.getRMALineId() != 0) {
                MRMALine rmaLine = new MRMALine(line.getRMALineId());
                if (rmaLine.getQtyInvoiced() != null)
                    rmaLine.setQtyInvoiced(rmaLine.getQtyInvoiced().add(line.getQtyInvoiced()));
                else rmaLine.setQtyInvoiced(line.getQtyInvoiced());
                if (!rmaLine.save()) {
                    m_processMsg = "Could not update RMA Line";
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                }
            }
            //
        } //	for all lines
        if (matchInv > 0) info.append(" @M_MatchInv_ID@#").append(matchInv).append(" ");
        if (matchPO > 0) info.append(" @M_MatchPO_ID@#").append(matchPO).append(" ");

        //	Update BP Statistics
        I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
        forUpdate(bp);
        //	Update total revenue and balance / credit limit (reversed on AllocationLine.processIt)
        BigDecimal invAmt =
                MConversionRate.convertBase(

                        getGrandTotal(true), // 	CM adjusted
                        getCurrencyId(),
                        getDateAcct(),
                        getConversionTypeId(),
                        getClientId(),
                        getOrgId());
        if (invAmt == null) {
            m_processMsg =
                    MConversionRateUtil.getErrorMessage(

                            "ErrorConvertingCurrencyToBaseCurrency",
                            getCurrencyId(),
                            MClientKt.getClientWithAccounting().getCurrencyId(),
                            getConversionTypeId(),
                            getDateAcct(),
                            null);
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }
        //	Total Balance
        BigDecimal newBalance = bp.getTotalOpenBalance();
        if (newBalance == null) newBalance = Env.ZERO;
        if (isSOTrx()) {
            newBalance = newBalance.add(invAmt);
            //
            if (bp.getFirstSale() == null) bp.setFirstSale(getDateInvoiced());
            BigDecimal newLifeAmt = bp.getActualLifeTimeValue();
            if (newLifeAmt == null) newLifeAmt = invAmt;
            else newLifeAmt = newLifeAmt.add(invAmt);
            BigDecimal newCreditAmt = bp.getSalesOrderCreditUsed();
            if (newCreditAmt == null) newCreditAmt = invAmt;
            else newCreditAmt = newCreditAmt.add(invAmt);
            //
            if (log.isLoggable(Level.FINE))
                log.fine(
                        "GrandTotal="
                                + getGrandTotal(true)
                                + "("
                                + invAmt
                                + ") BP Life="
                                + bp.getActualLifeTimeValue()
                                + "->"
                                + newLifeAmt
                                + ", Credit="
                                + bp.getSalesOrderCreditUsed()
                                + "->"
                                + newCreditAmt
                                + ", Balance="
                                + bp.getTotalOpenBalance()
                                + " -> "
                                + newBalance);
            bp.setActualLifeTimeValue(newLifeAmt);
            bp.setSalesOrderCreditUsed(newCreditAmt);
        } //	SO
        else {
            newBalance = newBalance.subtract(invAmt);
            if (log.isLoggable(Level.FINE))
                log.fine(
                        "GrandTotal="
                                + getGrandTotal(true)
                                + "("
                                + invAmt
                                + ") Balance="
                                + bp.getTotalOpenBalance()
                                + " -> "
                                + newBalance);
        }
        bp.setTotalOpenBalance(newBalance);
        bp.setSOCreditStatus();
        if (!bp.save()) {
            m_processMsg = "Could not update Business Partner";
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        //	User - Last Result/Contact
        if (getUserId() != 0) {
            MUser user = new MUser(getUserId());
            user.setLastContact(new Timestamp(System.currentTimeMillis()));
            StringBuilder msgset =
                    new StringBuilder()
                            .append(MsgKt.translate("C_Invoice_ID"))
                            .append(": ")
                            .append(getDocumentNo());
            user.setLastResult(msgset.toString());
            if (!user.save()) {
                m_processMsg = "Could not update Business Partner User";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
        } //	user

        //	Update Project
        if (isSOTrx() && getProjectId() != 0) {
            MProject project = new MProject(getProjectId());
            BigDecimal amt = getGrandTotal(true);
            int C_CurrencyTo_ID = project.getCurrencyId();
            if (C_CurrencyTo_ID != getCurrencyId())
                amt =
                        MConversionRate.convert(

                                amt,
                                getCurrencyId(),
                                C_CurrencyTo_ID,
                                getDateAcct(),
                                0,
                                getClientId(),
                                getOrgId());
            if (amt == null) {
                m_processMsg =
                        MConversionRateUtil.getErrorMessage(

                                "ErrorConvertingCurrencyToProjectCurrency",
                                getCurrencyId(),
                                C_CurrencyTo_ID,
                                0,
                                getDateAcct(),
                                null);
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            BigDecimal newAmt = project.getInvoicedAmt();
            if (newAmt == null) newAmt = amt;
            else newAmt = newAmt.add(amt);
            if (log.isLoggable(Level.FINE))
                log.fine(
                        "GrandTotal="
                                + getGrandTotal(true)
                                + "("
                                + amt
                                + ") Project "
                                + project.getName()
                                + " - Invoiced="
                                + project.getInvoicedAmt()
                                + "->"
                                + newAmt);
            project.setInvoicedAmt(newAmt);
            if (!project.save()) {
                m_processMsg = "Could not update Project";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
        } //	project

        // auto delay capture authorization payment
        if (isSOTrx() && !isReversal()) {
            StringBuilder whereClause = new StringBuilder();
            whereClause.append("C_Order_ID IN (");
            whereClause.append("SELECT C_Order_ID ");
            whereClause.append("FROM C_OrderLine ");
            whereClause.append("WHERE C_OrderLine_ID IN (");
            whereClause.append("SELECT C_OrderLine_ID ");
            whereClause.append("FROM C_InvoiceLine ");
            whereClause.append("WHERE C_Invoice_ID = ");
            whereClause.append(getInvoiceId()).append("))");
            int[] orderIDList = getAllIDs(MOrder.Table_Name, whereClause.toString());

            int[] ids =
                    MPaymentTransaction.getAuthorizationPaymentTransactionIDs(
                            orderIDList, getInvoiceId());
            if (ids.length > 0) {
                boolean pureCIM = true;
                ArrayList<MPaymentTransaction> ptList = new ArrayList<MPaymentTransaction>();
                BigDecimal totalPayAmt = BigDecimal.ZERO;
                for (int id : ids) {
                    MPaymentTransaction pt = new MPaymentTransaction(id);

                    if (!pt.setPaymentProcessor()) {
                        if (pt.getPaymentProcessorId() > 0) {
                            MPaymentProcessor pp =
                                    new MPaymentProcessor(pt.getPaymentProcessorId());
                            m_processMsg = MsgKt.getMsg("PaymentNoProcessorModel") + ": " + pp.toString();
                        } else m_processMsg = MsgKt.getMsg("PaymentNoProcessorModel");
                        return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                    }

                    boolean isCIM =
                            pt.getPaymentProcessorId() > 0
                                    && pt.getCustomerPaymentProfileID() != null
                                    && pt.getCustomerPaymentProfileID().length() > 0;
                    if (pureCIM && !isCIM) pureCIM = false;

                    totalPayAmt = totalPayAmt.add(pt.getPayAmt());
                    ptList.add(pt);
                }

                // automatically void authorization payment and create a new sales payment when invoiced
                // amount is NOT equals to the authorized amount (applied to CIM payment processor)
                if (getGrandTotal().compareTo(totalPayAmt) != 0 && ptList.size() > 0 && pureCIM) {
                    // create a new sales payment
                    MPaymentTransaction newSalesPT =
                            MPaymentTransaction.copyFrom(
                                    ptList.get(0),
                                    new Timestamp(System.currentTimeMillis()),
                                    MPayment.TRXTYPE_Sales,
                                    "",
                                    null);
                    newSalesPT.setIsApproved(false);
                    newSalesPT.setIsVoided(false);
                    newSalesPT.setIsDelayedCapture(false);
                    newSalesPT.setDescription(
                            "InvoicedAmt: " + getGrandTotal() + " <> TotalAuthorizedAmt: " + totalPayAmt);
                    newSalesPT.setInvoiceId(getInvoiceId());
                    newSalesPT.setPayAmt(getGrandTotal());

                    // void authorization payment
                    for (MPaymentTransaction pt : ptList) {
                        pt.setDescription(
                                "InvoicedAmt: " + getGrandTotal() + " <> AuthorizedAmt: " + pt.getPayAmt());
                        boolean ok = pt.voidOnlineAuthorizationPaymentTransaction();
                        pt.saveEx();
                        if (!ok) {
                            m_processMsg =
                                    MsgKt.getMsg("VoidAuthorizationPaymentFailed")
                                            + ": "
                                            + pt.getErrorMessage();
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                    }

                    // process the new sales payment
                    boolean ok = newSalesPT.processOnline();
                    newSalesPT.saveEx();
                    if (!ok) {
                        m_processMsg =
                                MsgKt.getMsg("CreateNewSalesPaymentFailed")
                                        + ": "
                                        + newSalesPT.getErrorMessage();
                        return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                    }
                } else if (getGrandTotal().compareTo(totalPayAmt) != 0 && ptList.size() > 0) {
                    m_processMsg = "InvoicedAmt: " + getGrandTotal() + " <> AuthorizedAmt: " + totalPayAmt;
                    return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                } else {
                    // delay capture authorization payment
                    for (MPaymentTransaction pt : ptList) {
                        boolean ok = pt.delayCaptureOnlineAuthorizationPaymentTransaction(getInvoiceId());
                        pt.saveEx();
                        if (!ok) {
                            m_processMsg =
                                    MsgKt.getMsg("DelayCaptureAuthFailed") + ": " + pt.getErrorMessage();
                            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
                        }
                    }
                }
                if (testAllocation(true)) {
                    saveEx();
                }
            }
        }

        if (X_C_Invoice.PAYMENTRULE_Cash.equals(getPaymentRule())) {
            if (testAllocation(true)) {
                saveEx();
            }
        }
        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        //	Counter Documents
        MInvoice counter = createCounterDoc();
        if (counter != null)
            info.append(" - @CounterDoc@: @C_Invoice_ID@=").append(counter.getDocumentNo());

        m_processMsg = info.toString().trim();
        setProcessed(true);
        setDocAction(X_C_Invoice.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    private void addDocsPostProcess(IPODoc doc) {
        docsPostProcess.add(doc);
    }

    public ArrayList<IPODoc> getDocsPostProcess() {
        return docsPostProcess;
    }

    /**
     * Set the definite document number after completed
     */
    private void setDefiniteDocumentNo() {
        if (isReversal()
                && !MSysConfig.getBooleanValue(
                MSysConfig.Invoice_ReverseUseNewNumber, true, getClientId())) // IDEMPIERE-1771
            return;
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        if (dt.isOverwriteDateOnComplete()) {
            setDateInvoiced(new Timestamp(System.currentTimeMillis()));
            if (getDateAcct().before(getDateInvoiced())) {
                setDateAcct(getDateInvoiced());
                MPeriod.testPeriodOpen(getDateAcct(), getDocumentTypeId(), getOrgId());
            }
        }
        if (dt.isOverwriteSeqOnComplete()) {
            String value = MSequence.getDocumentNo(getDocumentTypeId(), true, this);
            if (value != null) setDocumentNo(value);
        }
    }

    /**
     * Create Counter Document
     *
     * @return counter invoice
     */
    private MInvoice createCounterDoc() {
        //	Is this a counter doc ?
        if (getRef_InvoiceId() != 0) return null;

        //	Org Must be linked to BPartner
        MOrg org = MOrgKt.getOrg(getOrgId());
        int counterC_BPartner_ID = org.getLinkedBusinessPartnerId();
        if (counterC_BPartner_ID == 0) return null;
        //	Business Partner needs to be linked to Org
        I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
        int counterAD_Org_ID = bp.getLinkedOrganizationId();
        if (counterAD_Org_ID == 0) return null;

        I_C_BPartner counterBP = getBusinessPartnerService().getById(counterC_BPartner_ID);
        //		MOrgInfo counterOrgInfo = MOrgInfoKt.getOrganizationInfo(counterAD_Org_ID);
        if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.getName());

        //	Document Type
        int C_DocTypeTarget_ID = 0;
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
        MInvoice counter =
                copyFrom(
                        this,
                        getDateInvoiced(),
                        getDateAcct(),
                        C_DocTypeTarget_ID,
                        !isSOTrx(),
                        true,
                        true);
        //
        counter.setOrgId(counterAD_Org_ID);
        //	counter.setWarehouseId(counterOrgInfo.getWarehouseId());
        //
        //		counter.setBPartner(counterBP);// was set on copyFrom
        //	References (Should not be required)
        counter.setSalesRepresentativeId(getSalesRepresentativeId());
        counter.saveEx();

        //	Update copied lines
        I_C_InvoiceLine[] counterLines = counter.getLines(true);
        for (I_C_InvoiceLine counterLine : counterLines) {
            counterLine.setClientOrg(counter);
            counterLine.setInvoice(counter); // 	copies header values (BP, etc.)
            counterLine.setPrice();
            counterLine.setTax();
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
     * Void Document.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());

        if (X_C_Invoice.DOCSTATUS_Closed.equals(getDocStatus())
                || X_C_Invoice.DOCSTATUS_Reversed.equals(getDocStatus())
                || X_C_Invoice.DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            setDocAction(X_C_Invoice.DOCACTION_None);
            return false;
        }

        //	Not Processed
        if (X_C_Invoice.DOCSTATUS_Drafted.equals(getDocStatus())
                || X_C_Invoice.DOCSTATUS_Invalid.equals(getDocStatus())
                || X_C_Invoice.DOCSTATUS_InProgress.equals(getDocStatus())
                || X_C_Invoice.DOCSTATUS_Approved.equals(getDocStatus())
                || X_C_Invoice.DOCSTATUS_NotApproved.equals(getDocStatus())) {
            // Before Void
            m_processMsg =
                    ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
            if (m_processMsg != null) return false;

            //	Set lines to 0
            I_C_InvoiceLine[] lines = getLines(false);
            for (I_C_InvoiceLine line : lines) {
                BigDecimal old = line.getQtyInvoiced();
                if (old.compareTo(Env.ZERO) != 0) {
                    line.setQty(Env.ZERO);
                    line.setTaxAmt(Env.ZERO);
                    line.setLineNetAmt(Env.ZERO);
                    line.setLineTotalAmt(Env.ZERO);
                    String msgadd = MsgKt.getMsg("Voided") +
                            " (" +
                            old +
                            ")";
                    line.addDescription(msgadd);
                    //	Unlink Shipment
                    if (line.getInOutLineId() != 0) {
                        MInOutLine ioLine =
                                new MInOutLine(
                                        line.getInOutLineId());
                        ioLine.setIsInvoiced(false);
                        ioLine.saveEx();
                        line.setInOutLineId(0);
                    }
                    line.saveEx();
                }
            }
            addDescription(MsgKt.getMsg("Voided"));
            setIsPaid(true);
            setPaymentId(0);
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
        setDocAction(X_C_Invoice.DOCACTION_None);
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
        setDocAction(X_C_Invoice.DOCACTION_None);

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

        MInvoice reversal = reverse(false);
        if (reversal == null) return false;

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        m_processMsg = reversal.getDocumentNo();

        return true;
    } //	reverseCorrectIt

    private MInvoice reverse(boolean accrual) {
        Timestamp reversalDate = accrual ? Env.getContextAsDate() : getDateAcct();
        if (reversalDate == null) {
            reversalDate = new Timestamp(System.currentTimeMillis());
        }
        Timestamp reversalDateInvoiced = accrual ? reversalDate : getDateInvoiced();

        MPeriod.testPeriodOpen(reversalDate, getDocumentTypeId(), getOrgId());
        //
        reverseAllocations(accrual, getInvoiceId());
        //	Reverse/Delete Matching
        if (!isSOTrx()) {
            I_M_MatchInv[] mInv = MMatchInv.getInvoice(getInvoiceId());
            for (I_M_MatchInv i_m_matchInv : mInv) {
                if (i_m_matchInv.getReversalId() > 0) continue;

                if (!i_m_matchInv.reverse(reversalDate)) {
                    m_processMsg = "Could not Reverse MatchInv";
                    return null;
                }
                addDocsPostProcess(new MMatchInv(i_m_matchInv.getReversalId()));
            }
            MMatchPO[] mPO = MMatchPO.getInvoice(getInvoiceId());
            for (MMatchPO mMatchPO : mPO) {
                if (mMatchPO.getReversalId() > 0) continue;

                if (mMatchPO.getInOutLineId() == 0) {
                    if (!mMatchPO.reverse(reversalDate)) {
                        m_processMsg = "Could not Reverse MatchPO";
                        return null;
                    }
                    addDocsPostProcess(new MMatchPO(mMatchPO.getReversalId()));
                } else {
                    mMatchPO.setInvoiceLineId(null);
                    mMatchPO.saveEx();
                }
            }
        }
        //
        loadFromMap(null); // 	reload allocation reversal info

        //	Deep Copy
        MInvoice reversal = null;
        if (MSysConfig.getBooleanValue(MSysConfig.Invoice_ReverseUseNewNumber, true, getClientId()))
            reversal =
                    copyFrom(
                            this,
                            reversalDateInvoiced,
                            reversalDate,
                            getDocumentTypeId(),
                            isSOTrx(),
                            false,
                            true);
        else
            reversal =
                    copyFrom(
                            this,
                            reversalDateInvoiced,
                            reversalDate,
                            getDocumentTypeId(),
                            isSOTrx(),
                            false,
                            true,
                            getDocumentNo() + "^");
        if (reversal == null) {
            m_processMsg = "Could not create Invoice Reversal";
            return null;
        }
        reversal.setReversal(true);

        //	Reverse Line Qty
        I_C_InvoiceLine[] oLines = getLines(false);
        I_C_InvoiceLine[] rLines = reversal.getLines(true);
        for (int i = 0; i < rLines.length; i++) {
            I_C_InvoiceLine rLine = rLines[i];
            rLine.getParent().setReversal(true);
            I_C_InvoiceLine oLine = oLines[i];
            rLine.setQtyEntered(oLine.getQtyEntered().negate());
            rLine.setQtyInvoiced(oLine.getQtyInvoiced().negate());
            rLine.setLineNetAmt(oLine.getLineNetAmt().negate());
            rLine.setTaxAmt(oLine.getTaxAmt().negate());
            rLine.setLineTotalAmt(oLine.getLineTotalAmt().negate());
            rLine.setPriceActual(oLine.getPriceActual());
            rLine.setPriceList(oLine.getPriceList());
            rLine.setPriceLimit(oLine.getPriceLimit());
            rLine.setPriceEntered(oLine.getPriceEntered());
            rLine.setUOMId(oLine.getUOMId());
            if (!rLine.save()) {
                m_processMsg = "Could not correct Invoice Reversal Line";
                return null;
            }
        }
        reversal.setOrderId(getOrderId());
        StringBuilder msgadd = new StringBuilder("{->").append(getDocumentNo()).append(")");
        reversal.addDescription(msgadd.toString());
        // FR1948157
        reversal.setReversalId(getInvoiceId());
        reversal.saveEx();
        //
        reversal.docsPostProcess = this.docsPostProcess;
        this.docsPostProcess = new ArrayList<IPODoc>();
        //
        if (!reversal.processIt(DocAction.Companion.getACTION_Complete())) {
            m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
            return null;
        }
        //
        reverseAllocations(accrual, reversal.getInvoiceId());

        reversal.setPaymentId(0);
        reversal.setIsPaid(true);
        reversal.closeIt();
        reversal.setProcessing(false);
        reversal.setDocStatus(X_C_Invoice.DOCSTATUS_Reversed);
        reversal.setDocAction(X_C_Invoice.DOCACTION_None);
        reversal.saveEx();
        //
        msgadd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
        addDescription(msgadd.toString());

        //	Clean up Reversed (this)
        I_C_InvoiceLine[] iLines = getLines(false);
        for (I_C_InvoiceLine iLine : iLines) {
            if (iLine.getInOutLineId() != 0) {
                MInOutLine ioLine =
                        new MInOutLine(iLine.getInOutLineId());
                ioLine.setIsInvoiced(false);
                ioLine.saveEx();
                //	Reconsiliation
                iLine.setInOutLineId(0);
                iLine.saveEx();
            }
        }
        setProcessed(true);
        // FR1948157
        setReversalId(reversal.getInvoiceId());
        setDocStatus(X_C_Invoice.DOCSTATUS_Reversed); // 	may come from void
        setDocAction(X_C_Invoice.DOCACTION_None);
        setPaymentId(0);
        setIsPaid(true);

        //	Create Allocation
        StringBuilder msgall =
                new StringBuilder()
                        .append(MsgKt.translate("C_Invoice_ID"))
                        .append(": ")
                        .append(getDocumentNo())
                        .append("/")
                        .append(reversal.getDocumentNo());
        MAllocationHdr alloc =
                new MAllocationHdr(
                        false, reversalDate, getCurrencyId(), msgall.toString());
        alloc.setOrgId(getOrgId());
        alloc.saveEx();
        //	Amount
        BigDecimal gt = getGrandTotal(true);
        if (!isSOTrx()) gt = gt.negate();
        //	Orig Line
        MAllocationLine aLine = new MAllocationLine(alloc, gt, Env.ZERO, Env.ZERO, Env.ZERO);
        aLine.setInvoiceId(getInvoiceId());
        aLine.saveEx();
        //	Reversal Line
        MAllocationLine rLine = new MAllocationLine(alloc, gt.negate(), Env.ZERO, Env.ZERO, Env.ZERO);
        rLine.setInvoiceId(reversal.getInvoiceId());
        rLine.saveEx();
        // added AdempiereException by zuhri
        if (!alloc.processIt(DocAction.Companion.getACTION_Complete()))
            throw new AdempiereException("Failed when processing document - " + alloc.getProcessMsg());
        // end added
        alloc.saveEx();

        return reversal;
    }

    private void reverseAllocations(boolean accrual, int invoiceID) {
        for (MAllocationHdr allocation :
                MAllocationHdr.getOfInvoice(invoiceID)) {
            if (accrual) {
                allocation.setDocAction(DocAction.Companion.getACTION_Reverse_Accrual());
                allocation.reverseAccrualIt();
            } else {
                allocation.setDocAction(DocAction.Companion.getACTION_Reverse_Correct());
                allocation.reverseCorrectIt();
            }
            allocation.saveEx();
        }
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

        MInvoice reversal = reverse(true);
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
        //	: Grand Total = 123.00 (#1)
        sb.append(": ")
                .append(MsgKt.translate("GrandTotal"))
                .append("=")
                .append(getGrandTotal())
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
        return getSalesRepresentativeId();
    } //	getDoc_User_ID

    /**
     * Get Document Approval Amount
     *
     * @return amount
     */
    @NotNull
    public BigDecimal getApprovalAmt() {
        return getGrandTotal();
    } //	getApprovalAmt

    /**
     * @param rma
     */
    public void setRMA(MRMA rma) {
        setRMAId(rma.getRMAId());
        setOrgId(rma.getOrgId());
        setDescription(rma.getDescription());
        setBusinessPartnerId(rma.getBusinessPartnerId());
        setSalesRepresentativeId(rma.getSalesRepresentativeId());

        setGrandTotal(rma.getAmt());
        setIsSOTrx(rma.isSOTrx());
        setTotalLines(rma.getAmt());

        I_C_Invoice originalInvoice = rma.getOriginalInvoice();

        if (originalInvoice == null) {
            throw new IllegalStateException("Not invoiced - RMA: " + rma.getDocumentNo());
        }

        setBusinessPartnerLocationId(originalInvoice.getBusinessPartnerLocationId());
        setUserId(originalInvoice.getUserId());
        setCurrencyId(originalInvoice.getCurrencyId());
        setIsTaxIncluded(originalInvoice.isTaxIncluded());
        setPriceListId(originalInvoice.getPriceListId());
        setProjectId(originalInvoice.getProjectId());
        setBusinessActivityId(originalInvoice.getBusinessActivityId());
        setCampaignId(originalInvoice.getCampaignId());
        setUser1Id(originalInvoice.getUser1Id());
        setUser2Id(originalInvoice.getUser2Id());
    }

    /**
     * Document Status is Complete or Closed
     *
     * @return true if CO, CL or RE
     */
    public boolean isComplete() {
        String ds = getDocStatus();
        return X_C_Invoice.DOCSTATUS_Completed.equals(ds)
                || X_C_Invoice.DOCSTATUS_Closed.equals(ds)
                || X_C_Invoice.DOCSTATUS_Reversed.equals(ds);
    } //	isComplete

    /**
     * Get tax providers
     *
     * @return array of tax provider
     */
    public MTaxProvider[] getTaxProviders() {
        Hashtable<Integer, MTaxProvider> providers = new Hashtable<Integer, MTaxProvider>();
        I_C_InvoiceLine[] lines = getLines();
        for (I_C_InvoiceLine line : lines) {
            MTax tax = new MTax(line.getTaxId());
            MTaxProvider provider = providers.get(tax.getTaxProviderId());
            if (provider == null)
                providers.put(
                        tax.getTaxProviderId(),
                        new MTaxProvider(tax.getTaxProviderId()));
        }

        MTaxProvider[] retValue = new MTaxProvider[providers.size()];
        providers.values().toArray(retValue);
        return retValue;
    }

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }

    @Override
    public int getTableId() {
        return I_C_Invoice.Table_ID;
    }
} //	MInvoice
