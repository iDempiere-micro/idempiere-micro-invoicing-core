package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.conversionrate.MConversionRate;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IDoc;
import org.compiere.model.IFact;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_AllocationHdr;
import org.compiere.model.I_C_BankStatement;
import org.compiere.model.I_C_Cash;
import org.compiere.model.I_C_ProjectIssue;
import org.compiere.model.I_M_MatchInv;
import org.compiere.model.I_M_MatchPO;
import org.compiere.model.I_M_Production;
import org.compiere.orm.MDocType;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.AdempiereUserError;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Posting Document Root.
 *
 * <pre>
 *  Table               Base Document Types (C_DocType.DocBaseType & AD_Reference_ID=183)
 *      Class           AD_Table_ID
 *  ------------------  ------------------------------
 *  C_Invoice:          ARI, ARC, ARF, API, APC
 *      Doc_Invoice     318 - has C_DocType_ID
 *
 *  C_Payment:          ARP, APP
 *      Doc_Payment     335 - has C_DocType_ID
 *
 *  C_Order:            SOO, POO,  POR (Requisition)
 *      Doc_Order       259 - has C_DocType_ID
 *
 *  M_InOut:            MMS, MMR
 *      Doc_InOut       319 - DocType derived
 *
 *  M_Inventory:        MMI
 *      Doc_Inventory   321 - DocType fixed
 *
 *  M_Movement:         MMM
 *      Doc_Movement    323 - DocType fixed
 *
 *  M_Production:       MMP
 *      Doc_Production  325 - DocType fixed
 *
 * M_Production:        MMO
 *      Doc_CostCollector  330 - DocType fixed
 *
 *  C_BankStatement:    CMB
 *      Doc_Bank        392 - DocType fixed
 *
 *  C_Cash:             CMC
 *      Doc_Cash        407 - DocType fixed
 *
 *  C_Allocation:       CMA
 *      Doc_Allocation  390 - DocType fixed
 *
 *  GL_Journal:         GLJ
 *      Doc_GLJournal   224 = has C_DocType_ID
 *
 *  Matching Invoice    MXI
 *      M_MatchInv      472 - DocType fixed
 *
 *  Matching PO         MXP
 *      M_MatchPO       473 - DocType fixed
 *
 * Project Issue		PJI
 * 	C_ProjectIssue	623 - DocType fixed
 *
 *  </pre>
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @version $Id: Doc.java,v 1.6 2006/07/30 00:53:33 jjanke Exp $
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 */
public abstract class Doc implements IDoc {
    /**
     * AR Invoices - ARI
     */
    public static final String DOCTYPE_ARInvoice = MDocType.DOCBASETYPE_ARInvoice;
    /**
     * AR Credit Memo
     */
    public static final String DOCTYPE_ARCredit = "ARC";
    /**
     * AR Receipt
     */
    public static final String DOCTYPE_ARReceipt = "ARR";
    /**
     * AR ProForma
     */
    public static final String DOCTYPE_ARProForma = "ARF";
    /**
     * AP Invoices
     */
    public static final String DOCTYPE_APInvoice = "API";
    /**
     * AP Credit Memo
     */
    public static final String DOCTYPE_APCredit = "APC";
    /**
     * AP Payment
     */
    public static final String DOCTYPE_APPayment = "APP";
    /**
     * CashManagement Bank Statement
     */
    public static final String DOCTYPE_BankStatement = "CMB";
    /**
     * CashManagement Cash Journals
     */
    public static final String DOCTYPE_CashJournal = "CMC";
    /**
     * CashManagement Allocations
     */
    public static final String DOCTYPE_Allocation = "CMA";
    /**
     * Material Shipment
     */
    public static final String DOCTYPE_MatShipment = "MMS";
    /**
     * Material Receipt
     */
    public static final String DOCTYPE_MatReceipt = "MMR";
    /**
     * Material Production
     */
    public static final String DOCTYPE_MatProduction = "MMP";
    /**
     * Match Invoice
     */
    public static final String DOCTYPE_MatMatchInv = "MXI";
    /**
     * Match PO
     */
    public static final String DOCTYPE_MatMatchPO = "MXP";
    /**
     * GL Journal
     */
    public static final String DOCTYPE_GLJournal = "GLJ";
    /**
     * Purchase Order
     */
    public static final String DOCTYPE_POrder = "POO";
    /**
     * Sales Order
     */
    public static final String DOCTYPE_SOrder = "SOO";
    /**
     * Project Issue
     */
    public static final String DOCTYPE_ProjectIssue = "PJI";
    /**
     * Document Status
     */
    public static final String STATUS_NotPosted = "N";

    //  Posting Status - AD_Reference_ID=234     //
    /**
     * Document Status
     */
    public static final String STATUS_NotBalanced = "b";
    /**
     * Document Status
     */
    public static final String STATUS_NotConvertible = "c";
    /**
     * Document Status
     */
    public static final String STATUS_PeriodClosed = "p";
    /**
     * Document Status
     */
    public static final String STATUS_InvalidAccount = "i";
    /**
     * Document Status
     */
    public static final String STATUS_PostPrepared = "y";
    /**
     * Document Status
     */
    public static final String STATUS_Posted = "Y";
    /**
     * Document Status
     */
    public static final String STATUS_Error = "E";
    /**
     * Amount Type - Invoice - Gross
     */
    public static final int AMTTYPE_Gross = 0;
    /**
     * Amount Type - Invoice - Net
     */
    public static final int AMTTYPE_Net = 1;
    /**
     * Amount Type - Invoice - Charge
     */
    public static final int AMTTYPE_Charge = 2;
    /**
     * Account Type - Invoice - Charge
     */
    public static final int ACCTTYPE_Charge = 0;
    /**
     * Account Type - Invoice - AR
     */
    public static final int ACCTTYPE_C_Receivable = 1;
    /**
     * Account Type - Invoice - AP
     */
    public static final int ACCTTYPE_V_Liability = 2;
    /**
     * Account Type - Invoice - AP Service
     */
    public static final int ACCTTYPE_V_Liability_Services = 3;
    /**
     * Account Type - Invoice - AR Service
     */
    public static final int ACCTTYPE_C_Receivable_Services = 4;
    /**
     * Account Type - Payment - Unallocated
     */
    public static final int ACCTTYPE_UnallocatedCash = 10;
    /**
     * Account Type - Payment - Transfer
     */
    public static final int ACCTTYPE_BankInTransit = 11;
    /**
     * Account Type - Payment - Selection
     */
    public static final int ACCTTYPE_PaymentSelect = 12;
    /**
     * Account Type - Payment - Prepayment
     */
    public static final int ACCTTYPE_C_Prepayment = 13;
    /**
     * Account Type - Payment - Prepayment
     */
    public static final int ACCTTYPE_V_Prepayment = 14;
    /**
     * Account Type - Cash - Asset
     */
    public static final int ACCTTYPE_CashAsset = 20;
    /**
     * Account Type - Cash - Transfer
     */
    public static final int ACCTTYPE_CashTransfer = 21;
    /**
     * Account Type - Cash - Expense
     */
    public static final int ACCTTYPE_CashExpense = 22;
    /**
     * Account Type - Cash - Receipt
     */
    public static final int ACCTTYPE_CashReceipt = 23;
    /**
     * Account Type - Cash - Difference
     */
    public static final int ACCTTYPE_CashDifference = 24;
    /**
     * Account Type - Allocation - Discount Expense (AR)
     */
    public static final int ACCTTYPE_DiscountExp = 30;
    /**
     * Account Type - Allocation - Discount Revenue (AP)
     */
    public static final int ACCTTYPE_DiscountRev = 31;
    /**
     * Account Type - Allocation - Write Off
     */
    public static final int ACCTTYPE_WriteOff = 32;
    /**
     * Account Type - Bank Statement - Asset
     */
    public static final int ACCTTYPE_BankAsset = 40;
    /**
     * Account Type - Bank Statement - Interest Revenue
     */
    public static final int ACCTTYPE_InterestRev = 41;
    /**
     * Account Type - Bank Statement - Interest Exp
     */
    public static final int ACCTTYPE_InterestExp = 42;
    /**
     * Inventory Accounts - Differences
     */
    public static final int ACCTTYPE_InvDifferences = 50;
    /**
     * Inventory Accounts - NIR
     */
    public static final int ACCTTYPE_NotInvoicedReceipts = 51;
    /**
     * Project Accounts - Assets
     */
    public static final int ACCTTYPE_ProjectAsset = 61;
    /**
     * Project Accounts - WIP
     */
    public static final int ACCTTYPE_ProjectWIP = 62;
    /**
     * GL Accounts - PPV Offset
     */
    public static final int ACCTTYPE_PPVOffset = 101;
    /**
     * GL Accounts - Commitment Offset
     */
    public static final int ACCTTYPE_CommitmentOffset = 111;
    /**
     * GL Accounts - Commitment Offset Sales
     */
    public static final int ACCTTYPE_CommitmentOffsetSales = 112;
    /**
     * No Currency in Document Indicator (-1)
     */
    protected static final int NO_CURRENCY = -2;
    /**
     * ************************************************************************ Document Types
     * -------------- C_DocType.DocBaseType & AD_Reference_ID=183 C_Invoice: ARI, ARC, ARF, API, APC
     * C_Payment: ARP, APP C_Order: SOO, POO M_Transaction: MMI, MMM, MMS, MMR C_BankStatement: CMB
     * C_Cash: CMC C_Allocation: CMA GL_Journal: GLJ C_ProjectIssue PJI M_Requisition POR
     * ************************************************************************
     */
    private static final String DOC_TYPE_BY_DOC_BASE_TYPE_SQL =
            "SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID=? AND DocBaseType=? AND IsActive='Y' ORDER BY IsDefault DESC, C_DocType_ID";
    /**
     * Static Log
     */
    protected static CLogger s_log = CLogger.getCLogger(Doc.class);
    /**
     * Log per Document
     */
    protected CLogger log = CLogger.getCLogger(getClass());
    /**
     * The Document
     */
    protected IPODoc p_po = null;
    /**
     * Contained Doc Lines
     */
    protected DocLine[] p_lines;
    /**
     * Actual Document Status
     */
    protected String p_Status = null;
    /**
     * Error Message
     */
    protected String p_Error = null;
    /**
     * Accounting Schema
     */
    private AccountingSchema m_as = null;
    /**
     * Document Type
     */
    private String m_DocumentType = null;
    /**
     * Document Status
     */
    private String m_DocStatus = null;
    /**
     * Document No
     */
    private String m_DocumentNo = null;
    /**
     * Description
     */
    private String m_Description = null;
    /**
     * GL Category
     */
    private int m_GL_Category_ID = 0;
    /**
     * GL Period
     */
    private MPeriod m_period = null;
    /**
     * Period ID
     */
    private int m_C_Period_ID = 0;
    /**
     * Location From
     */
    private int m_C_LocFrom_ID = 0;
    /**
     * Location To
     */
    private int m_C_LocTo_ID = 0;
    /**
     * Accounting Date
     */
    private Timestamp m_DateAcct = null;

    /* *********************************************************************** */
    /**
     * Document Date
     */
    private Timestamp m_DateDoc = null;
    /**
     * Tax Included
     */
    private boolean m_TaxIncluded = false;
    /**
     * Is (Source) Multi-Currency Document - i.e. the document has different currencies (if true, the
     * document will not be source balanced)
     */
    private boolean m_MultiCurrency = false;
    /**
     * BP Sales Region
     */
    private int m_BP_C_SalesRegion_ID = -1;
    /**
     * B Partner
     */
    private int m_C_BPartner_ID = -1;
    /**
     * Bank Account
     */
    private int m_C_BankAccount_ID = -1;
    /**
     * Cach Book
     */
    private int m_C_CashBook_ID = -1;
    /**
     * Currency
     */
    private int m_C_Currency_ID = -1;
    /**
     * Facts
     */
    private ArrayList<IFact> m_fact = null;

    /**
     * Source Amounts (may not all be used)
     */
    private BigDecimal[] m_Amounts = new BigDecimal[4];
    /**
     * Quantity
     */
    private BigDecimal m_qty = null;

    protected abstract IPODoc createNewInstance(Row rs);

    /**
     * ************************************************************************ Constructor
     *
     * @param as                  accounting schema
     * @param clazz               Document Class
     * @param rs                  result set
     * @param defaultDocumentType default document type or null
     */
    public Doc(
            AccountingSchema as, Class<?> clazz, Row rs, String defaultDocumentType) {
        p_Status = STATUS_Error;
        m_as = as;

        String className = clazz.getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        try {
            p_po = createNewInstance(rs);
        } catch (Exception e) {
            String msg = className + ": " + e.getLocalizedMessage();
            log.severe(msg);
            throw new IllegalArgumentException(msg, e);
        }
        p_po.load(); // reload the PO to get any virtual column that was not obtained using
        // the rs (IDEMPIERE-775)

        //	DocStatus
        int index = p_po.getColumnIndex("DocStatus");
        if (index != -1) m_DocStatus = (String) p_po.getValue(index);

        //	Document Type
        setDocumentType(defaultDocumentType);

        //	Amounts
        for (int i = 0; i < m_Amounts.length; i++) {
            m_Amounts[i] = Env.ZERO;
        }
    } //  Doc

    /**
     * Create Posting document
     *
     * @param as          accounting schema
     * @param AD_Table_ID Table ID of Documents
     * @param Record_ID   record ID to load
     * @return Document or null
     */
    public static IDoc get(AccountingSchema as, int AD_Table_ID, int Record_ID) {
        return DocManager.INSTANCE.getDocument(as, AD_Table_ID, Record_ID);
    } //	get

    /**
     * Create Posting document
     *
     * @param as          accounting schema
     * @param AD_Table_ID Table ID of Documents
     * @param rs          ResultSet
     * @return Document
     * @throws AdempiereUserError
     */
    public static IDoc get(AccountingSchema as, int AD_Table_ID, Row rs) {
        return DocManager.INSTANCE.getDocument(as, AD_Table_ID, rs);
    } //  get

    /**
     * Post Document
     *
     * @param ass         accounting schemata
     * @param AD_Table_ID Transaction table
     * @param Record_ID   Record ID of this document
     * @param force       force posting
     * @return null if the document was posted or error message
     */
    public static String postImmediate(
            AccountingSchema[] ass, int AD_Table_ID, int Record_ID, boolean force) {
        return DocManager.INSTANCE.postDocument(ass, AD_Table_ID, Record_ID, force, true);
    } //  post

    public String getPostStatus() {
        return p_Status;
    }

    /**
     * Get Table Name
     *
     * @return table name
     */
    public String getTableName() {
        return p_po.getTableName();
    } //	getTableName

    /**
     * Get Table ID
     *
     * @return table id
     */
    public int getTableId() {
        return p_po.getTableId();
    } //	getTableId

    /**
     * Get Record_ID
     *
     * @return record id
     */
    public int getId() {
        return p_po.getId();
    } //	getId

    /**
     * Get Persistent Object
     *
     * @return po
     */
    public IPODoc getPO() {
        return p_po;
    } //	getPO

    /**
     * Post Document.
     *
     * <pre>
     *  - try to lock document (Processed='Y' (AND Processing='N' AND Posted='N'))
     * 		- if not ok - return false
     *          - postlogic (for all Accounting Schema)
     *              - create Fact lines
     *          - postCommit
     *              - commits Fact lines and Document & sets Processing = 'N'
     *              - if error - create Note
     *  </pre>
     *
     * @param force  if true ignore that locked
     * @param repost if true ignore that already posted
     * @return null if posted error otherwise
     */
    public final String post(boolean force, boolean repost) {
        if (m_DocStatus != null && !m_DocStatus.equals(DocumentEngine.Companion.getSTATUS_Completed())
                && !m_DocStatus.equals(DocumentEngine.Companion.getSTATUS_Closed())
                && !m_DocStatus.equals(DocumentEngine.Companion.getSTATUS_Voided())
                && !m_DocStatus.equals(DocumentEngine.Companion.getSTATUS_Reversed())) {
            return "Invalid DocStatus='" +
                    m_DocStatus +
                    "' for DocumentNo=" +
                    getDocumentNo();
        }

        //
        if (p_po.getClientId() != m_as.getClientId()) {
            StringBuilder error =
                    new StringBuilder("AD_Client_ID Conflict - Document=")
                            .append(p_po.getClientId())
                            .append(", AcctSchema=")
                            .append(m_as.getClientId());
            log.severe(error.toString());
            return error.toString();
        }

        //  Lock Record ----
        // 	outside trx if on server
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(getTableName())
                .append(" SET Processing='Y' WHERE ")
                .append(getTableName())
                .append("_ID=")
                .append(getId())
                .append(" AND Processed='Y' AND IsActive='Y'");
        if (!force) sql.append(" AND (Processing='N' OR Processing IS NULL)");
        if (!repost) sql.append(" AND Posted='N'");
        if (executeUpdate(sql.toString()) == 1) {
            if (log.isLoggable(Level.INFO)) log.info("Locked: " + getTableName() + "_ID=" + getId());
        } else {
            log.log(
                    Level.SEVERE,
                    "Resubmit - Cannot lock "
                            + getTableName()
                            + "_ID="
                            + getId()
                            + ", Force="
                            + force
                            + ",RePost="
                            + repost);
            if (!p_po.isActive()) return MsgKt.translate("CannotPostInactiveDocument");
            if (force) return MsgKt.translate("CannotLockReSubmit");
            return MsgKt.translate("CannotLockReSubmitOrRePostWithForce");
        }

        p_Error = loadDocumentDetails();
        if (p_Error != null) return p_Error;
        if (isDeferPosting()) {
            unlock();
            p_Status = STATUS_NotPosted;
            return null;
        }

        //  Delete existing Accounting
        if (repost) {
            if (isPosted() && !isPeriodOpen()) // 	already posted - don't delete if period closed
            {
                log.log(Level.SEVERE, toString() + " - Period Closed for already posed document");
                unlock();
                return "PeriodClosed";
            }
            //	delete it
            deleteAcct();
        } else if (isPosted()) {
            log.log(Level.SEVERE, toString() + " - Document already posted");
            unlock();
            return "AlreadyPosted";
        }

        p_Status = STATUS_NotPosted;

        //  Create Fact per AcctSchema
        m_fact = new ArrayList<>();

        getPO().setDoc(this);
        try {
            //	if acct schema has "only" org, skip
            boolean skip = false;
            if (m_as.getOrganizationOnlyId() != 0) {
                //	Header Level Org
                skip = m_as.isSkipOrg(getOrgId());
                //	Line Level Org
                if (p_lines != null) {
                    for (int line = 0; skip && line < p_lines.length; line++) {
                        skip = m_as.isSkipOrg(p_lines[line].getOrgId());
                        if (!skip) break;
                    }
                }
            }
            if (!skip) {
                //	post
                p_Status = postLogic();
            } else {
                p_Status = STATUS_Posted; // skipped is OK
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "", e);
            p_Status = STATUS_Error;
            p_Error = e.toString();
        }

        String validatorMsg = null;
        // Call validator on before post
        if (p_Status.equals(STATUS_Posted)) {
            validatorMsg =
                    ModelValidationEngine.get().fireDocValidate(getPO(), ModelValidator.TIMING_BEFORE_POST);
            if (validatorMsg != null) {
                p_Status = STATUS_Error;
                p_Error = validatorMsg;
            }
        }

        //  commitFact
        p_Status = postCommit(p_Status);

        if (p_Status.equals(STATUS_Posted)) {
            validatorMsg =
                    ModelValidationEngine.get().fireDocValidate(getPO(), ModelValidator.TIMING_AFTER_POST);
            if (validatorMsg != null) {
                p_Status = STATUS_Error;
                p_Error = validatorMsg;
            }
        }

        //  Create Note
        if (!p_Status.equals(STATUS_Posted)) {
            //  Insert Note
            String AD_MessageValue = "PostingError-" + p_Status;
            //	Text
            StringBuilder Text = new StringBuilder(MsgKt.getMsg(AD_MessageValue));
            if (p_Error != null) Text.append(" (").append(p_Error).append(")");
            String cn = getClass().getName();
            Text.append(" - ")
                    .append(cn.substring(cn.lastIndexOf('.')))
                    .append(" (")
                    .append(getDocumentType())
                    .append(" - DocumentNo=")
                    .append(getDocumentNo())
                    .append(", DateAcct=")
                    .append(getDateAcct().toString(), 0, 10)
                    .append(", Amount=")
                    .append(getAmount())
                    .append(", Sta=")
                    .append(p_Status)
                    .append(" - PeriodOpen=")
                    .append(isPeriodOpen())
                    .append(", Balanced=")
                    .append(isBalanced())
                    .append(", Schema=")
                    .append(m_as.getName());
            p_Error = Text.toString();
        }

        //  dispose facts
        for (IFact fact : m_fact) {
            if (fact != null) fact.dispose();
        }
        p_lines = null;

        if (p_Status.equals(STATUS_Posted)) return null;
        return p_Error;
    } //  post

    /**
     * Delete Accounting
     *
     * @return number of records
     */
    protected int deleteAcct() {
        String sql = "DELETE Fact_Acct WHERE AD_Table_ID=" +
                getTableId() +
                " AND Record_ID=" +
                p_po.getId() +
                " AND C_AcctSchema_ID=" +
                m_as.getAccountingSchemaId();
        int no = executeUpdate(sql);
        if (no != 0) if (log.isLoggable(Level.INFO)) log.info("deleted=" + no);
        return no;
    } //	deleteAcct

    /**
     * Posting logic for Accounting Schema index
     *
     * @return posting status/error code
     */
    private final String postLogic() {
        //  rejectUnbalanced
        if (!m_as.isSuspenseBalancing() && !isBalanced()) return STATUS_NotBalanced;

        //  rejectUnconvertible
        if (!isConvertible(m_as)) return STATUS_NotConvertible;

        //  rejectPeriodClosed
        if (!isPeriodOpen()) return STATUS_PeriodClosed;

        //  createFacts
        ArrayList<IFact> facts = createFacts(m_as);
        if (facts == null) return STATUS_Error;

        // call modelValidator
        String validatorMsg = ModelValidationEngine.get().fireFactsValidate(m_as, facts, getPO());
        if (validatorMsg != null) {
            p_Error = validatorMsg;
            return STATUS_Error;
        }

        for (int f = 0; f < facts.size(); f++) {
            IFact fact = facts.get(f);
            if (fact == null) return STATUS_Error;
            m_fact.add(fact);
            //
            p_Status = STATUS_PostPrepared;

            //	check accounts
            if (!fact.checkAccounts()) return STATUS_InvalidAccount;

            //	distribute
            if (!fact.distribute()) return STATUS_Error;

            //  balanceSource
            if (!fact.isSourceBalanced()) {
                fact.balanceSource();
                if (!fact.isSourceBalanced()) return STATUS_NotBalanced;
            }

            //  balanceSegments
            if (!fact.isSegmentBalanced()) {
                fact.balanceSegments();
                if (!fact.isSegmentBalanced()) return STATUS_NotBalanced;
            }

            //  balanceAccounting
            if (!fact.isAcctBalanced()) {
                fact.balanceAccounting();
                if (!fact.isAcctBalanced()) return STATUS_NotBalanced;
            }
        } //	for all facts

        return STATUS_Posted;
    } //  postLogic

    /**
     * Post Commit. Save Facts & Document
     *
     * @param status status
     * @return Posting Status
     */
    private final String postCommit(String status) {
        if (log.isLoggable(Level.INFO))
            log.info("Sta=" + status + " DT=" + getDocumentType() + " ID=" + p_po.getId());
        p_Status = status;

        try {
            //  *** Transaction Start       ***
            //  Commit Facts
            if (status.equals(STATUS_Posted)) {
                for (IFact fact : m_fact) {
                    if (fact != null && !fact.save()) {
                        log.log(Level.SEVERE, "(fact not saved) ... rolling back");
                        unlock();
                        throw new AdempiereException("(fact not saved) ... rolling back");
                    }
                }
            }

            unlock();

            //  *** Transaction End         ***
        } catch (Exception e) {
            log.log(Level.SEVERE, "... rolling back", e);
            status = STATUS_Error;
            unlock();
            throw new AdempiereException(e);
        }
        p_Status = status;
        return status;
    } //  postCommit

    /**
     * Unlock Document
     */
    private void unlock() {
        // 	outside trx if on server
        String sql = "UPDATE " + getTableName() +
                " SET Processing='N' WHERE " +
                getTableName() +
                "_ID=" +
                p_po.getId();
        executeUpdate(sql);
    } //  unlock

    /**
     * ************************************************************************ Load Document Type and
     * GL Info. Set p_DocumentType and p_GL_Category_ID
     *
     * @return document type (i.e. C_DocType.DocBaseType)
     */
    public String getDocumentType() {
        if (m_DocumentType == null) setDocumentType(null);
        return m_DocumentType;
    } //  getDocumentType

    /**
     * Load Document Type and GL Info. Set p_DocumentType and p_GL_Category_ID
     *
     * @param DocumentType
     */
    public void setDocumentType(String DocumentType) {
        if (DocumentType != null) m_DocumentType = DocumentType;
        //  IDEMPIERE-3342 - prefer the category defined for the doctype if there is such column in the
        // table
        if (p_po.getColumnIndex("C_DocType_ID") >= 0 && getDocumentTypeId() != 0) {
            String sql = "SELECT DocBaseType, GL_Category_ID FROM C_DocType WHERE C_DocType_ID=?";
            PreparedStatement pstmt = null;
            ResultSet rsDT = null;
            try {
                pstmt = prepareStatement(sql);
                pstmt.setInt(1, getDocumentTypeId());
                rsDT = pstmt.executeQuery();
                if (rsDT.next()) {
                    m_DocumentType = rsDT.getString(1);
                    m_GL_Category_ID = rsDT.getInt(2);
                }
            } catch (SQLException e) {
                log.log(Level.SEVERE, sql, e);
            } finally {
                rsDT = null;
                pstmt = null;
            }
        }
        if (m_DocumentType == null) {
            log.log(
                    Level.SEVERE,
                    "No DocBaseType for C_DocType_ID="
                            + getDocumentTypeId()
                            + ", DocumentNo="
                            + getDocumentNo());
        }

        //  We have a document Type, but no GL info - search for DocType
        if (m_GL_Category_ID == 0) {
            String sql =
                    "SELECT GL_Category_ID FROM C_DocType " + "WHERE AD_Client_ID=? AND DocBaseType=?";
            PreparedStatement pstmt = null;
            ResultSet rsDT = null;
            try {
                pstmt = prepareStatement(sql);
                pstmt.setInt(1, getClientId());
                pstmt.setString(2, m_DocumentType);
                rsDT = pstmt.executeQuery();
                if (rsDT.next()) m_GL_Category_ID = rsDT.getInt(1);
            } catch (SQLException e) {
                log.log(Level.SEVERE, sql, e);
            } finally {
                rsDT = null;
                pstmt = null;
            }
        }

        //  Still no GL_Category - get Default GL Category
        if (m_GL_Category_ID == 0) {
            String sql =
                    "SELECT GL_Category_ID FROM GL_Category "
                            + "WHERE AD_Client_ID=? "
                            + "ORDER BY IsDefault DESC";
            PreparedStatement pstmt = null;
            ResultSet rsDT = null;
            try {
                pstmt = prepareStatement(sql);
                pstmt.setInt(1, getClientId());
                rsDT = pstmt.executeQuery();
                if (rsDT.next()) m_GL_Category_ID = rsDT.getInt(1);
            } catch (SQLException e) {
                log.log(Level.SEVERE, sql, e);
            } finally {
                rsDT = null;
                pstmt = null;
            }
        }
        //
        if (m_GL_Category_ID == 0) log.log(Level.SEVERE, "No default GL_Category - " + toString());

        if (m_DocumentType == null) throw new IllegalStateException("Document Type not found");
    } //	setDocumentType

    /**
     * ************************************************************************ Is the Source Document
     * Balanced
     *
     * @return true if (source) balanced
     */
    public boolean isBalanced() {
        //  Multi-Currency documents are source balanced by definition
        if (isMultiCurrency()) return true;
        //
        boolean retValue = getBalance().signum() == 0;
        if (retValue) {
            if (log.isLoggable(Level.FINE)) log.fine("Yes " + toString());
        } else {
            log.warning("NO - " + toString());
        }
        return retValue;
    } //	isBalanced

    /**
     * Is Document convertible to currency and Conversion Type
     *
     * @param acctSchema accounting schema
     * @return true, if convertible to accounting currency
     */
    public boolean isConvertible(AccountingSchema acctSchema) {
        //  No Currency in document
        if (getCurrencyId() == NO_CURRENCY) {
            if (log.isLoggable(Level.FINE)) log.fine("(none) - " + toString());
            return true;
        }
        // Journal from a different acct schema
        if (this instanceof Doc_GLJournal) {
            int glj_as = (Integer) p_po.getValue("C_AcctSchema_ID");
            if (acctSchema.getAccountingSchemaId() != glj_as) return true;
        }
        //  Get All Currencies
        HashSet<Integer> set = new HashSet<Integer>();
        set.add(getCurrencyId());
        for (int i = 0; p_lines != null && i < p_lines.length; i++) {
            int C_Currency_ID = p_lines[i].getCurrencyId();
            if (C_Currency_ID != NO_CURRENCY) set.add(C_Currency_ID);
        }

        //  just one and the same
        if (set.size() == 1 && acctSchema.getCurrencyId() == getCurrencyId()) {
            if (log.isLoggable(Level.FINE))
                log.fine("(same) Cur=" + getCurrencyId() + " - " + toString());
            return true;
        }

        boolean convertible = true;
        Iterator<Integer> it = set.iterator();
        while (it.hasNext() && convertible) {
            int C_Currency_ID = it.next();
            if (C_Currency_ID != acctSchema.getCurrencyId()) {
                BigDecimal amt =
                        MConversionRate.getRate(
                                C_Currency_ID,
                                acctSchema.getCurrencyId(),
                                getDateAcct(),
                                getConversionTypeId(),
                                getClientId(),
                                getOrgId());
                if (amt == null) {
                    convertible = false;
                    log.warning(
                            "NOT from C_Currency_ID="
                                    + C_Currency_ID
                                    + " to "
                                    + acctSchema.getCurrencyId()
                                    + " - "
                                    + toString());
                } else if (log.isLoggable(Level.FINE)) log.fine("From C_Currency_ID=" + C_Currency_ID);
            }
        }

        if (log.isLoggable(Level.FINE))
            log.fine(
                    "Convertible="
                            + convertible
                            + ", AcctSchema C_Currency_ID="
                            + acctSchema.getCurrencyId()
                            + " - "
                            + toString());
        return convertible;
    } //	isConvertible

    /**
     * Calculate Period from DateAcct. m_C_Period_ID is set to -1 of not open to 0 if not found
     */
    public void setPeriod() {
        if (m_period != null) return;

        //	Period defined in GL Journal (e.g. adjustment period)
        int index = p_po.getColumnIndex("C_Period_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) m_period = MPeriod.get(ii);
        }
        if (m_period == null)
            m_period = MPeriod.get(getDateAcct(), getOrgId());
        //	Is Period Open?
        if (m_period != null && m_period.isOpen(getDocumentType(), getDateAcct()))
            m_C_Period_ID = m_period.getPeriodId();
        else m_C_Period_ID = -1;
        //
        if (log.isLoggable(Level.FINE))
            log.fine( // + clientId + " - "
                    getDateAcct() + " - " + getDocumentType() + " => " + m_C_Period_ID);
    } //  setPeriodId

    /**
     * Get C_Period_ID
     *
     * @return period
     */
    public int getPeriodId() {
        if (m_period == null) setPeriod();
        return m_C_Period_ID;
    } //	getPeriodId

    /**
     * Is Period Open
     *
     * @return true if period is open
     */
    public boolean isPeriodOpen() {
        setPeriod();
        boolean open = m_C_Period_ID > 0;
        if (open) {
            if (log.isLoggable(Level.FINE)) log.fine("Yes - " + toString());
        } else {
            log.warning("NO - " + toString());
        }
        return open;
    } //	isPeriodOpen

    /**
     * Get the Amount (loaded in loadDocumentDetails)
     *
     * @param AmtType see AMTTYPE_*
     * @return Amount
     */
    public BigDecimal getAmount(int AmtType) {
        if (AmtType < 0 || AmtType >= m_Amounts.length) return null;
        return m_Amounts[AmtType];
    } //	getAmount

    /**
     * Set the Amount
     *
     * @param AmtType see AMTTYPE_*
     * @param amt     Amount
     */
    public void setAmount(int AmtType, BigDecimal amt) {
        if (AmtType < 0 || AmtType >= m_Amounts.length) return;
        if (amt == null) m_Amounts[AmtType] = Env.ZERO;
        else m_Amounts[AmtType] = amt;
    } //	setAmount

    /**
     * Get Amount with index 0
     *
     * @return Amount (primary document amount)
     */
    public BigDecimal getAmount() {
        return m_Amounts[0];
    } //  getAmount

    /**
     * Get Quantity
     *
     * @return Quantity
     */
    public BigDecimal getQty() {
        if (m_qty == null) {
            int index = p_po.getColumnIndex("Qty");
            if (index != -1) m_qty = (BigDecimal) p_po.getValue(index);
            else m_qty = Env.ZERO;
        }
        return m_qty;
    } //  getQty

    /**
     * Get the Valid Combination id for Accounting Schema
     *
     * @param AcctType see ACCTTYPE_*
     * @param as       accounting schema
     * @return C_ValidCombination_ID
     */
    public int getValidCombinationId(int AcctType, AccountingSchema as) {
        int para_1 = 0; //  first parameter (second is always AcctSchema)
        String sql = null;

        /* Account Type - Invoice */
        switch (AcctType) {
            case ACCTTYPE_Charge:
// 	see getChargeAccount in DocLine

                int cmp = getAmount(AMTTYPE_Charge).compareTo(Env.ZERO);
                if (cmp == 0) return 0;
                else
                    sql = "SELECT CH_Expense_Acct FROM C_Charge_Acct WHERE C_Charge_ID=? AND C_AcctSchema_ID=?";

                para_1 = getChargeId();
                break;
            case ACCTTYPE_V_Liability:
                sql =
                        "SELECT V_Liability_Acct FROM C_BP_Vendor_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_V_Liability_Services:
                sql =
                        "SELECT V_Liability_Services_Acct FROM C_BP_Vendor_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_C_Receivable:
                sql =
                        "SELECT C_Receivable_Acct FROM C_BP_Customer_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_C_Receivable_Services:
                sql =
                        "SELECT C_Receivable_Services_Acct FROM C_BP_Customer_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_V_Prepayment:
                sql =
                        "SELECT V_Prepayment_Acct FROM C_BP_Vendor_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_C_Prepayment:
                sql =
                        "SELECT C_Prepayment_Acct FROM C_BP_Customer_Acct WHERE C_BPartner_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;

            /* Account Type - Payment */
            case ACCTTYPE_UnallocatedCash:
                sql =
                        "SELECT B_UnallocatedCash_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBankAccountId();
                break;
            case ACCTTYPE_BankInTransit:
                sql =
                        "SELECT B_InTransit_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBankAccountId();
                break;
            case ACCTTYPE_PaymentSelect:
                sql =
                        "SELECT B_PaymentSelect_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBankAccountId();
                break;

            /* Account Type - Allocation */
            case ACCTTYPE_DiscountExp:
                sql =
                        "SELECT a.PayDiscount_Exp_Acct FROM C_BP_Group_Acct a, C_BPartner bp "
                                + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_DiscountRev:
                sql =
                        "SELECT PayDiscount_Rev_Acct FROM C_BP_Group_Acct a, C_BPartner bp "
                                + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;
            case ACCTTYPE_WriteOff:
                sql =
                        "SELECT WriteOff_Acct FROM C_BP_Group_Acct a, C_BPartner bp "
                                + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;

            /* Account Type - Bank Statement */
            case ACCTTYPE_BankAsset:
                sql =
                        "SELECT B_Asset_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBankAccountId();
                break;
            case ACCTTYPE_InterestRev:
                sql =
                        "SELECT B_InterestRev_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBankAccountId();
                break;
            case ACCTTYPE_InterestExp:
                sql =
                        "SELECT B_InterestExp_Acct FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID=?";
                para_1 = getBankAccountId();
                break;

            /* Account Type - Cash */
            case ACCTTYPE_CashAsset:
                sql = "SELECT CB_Asset_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
                para_1 = getCashBookId();
                break;
            case ACCTTYPE_CashTransfer:
                sql =
                        "SELECT CB_CashTransfer_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
                para_1 = getCashBookId();
                break;
            case ACCTTYPE_CashExpense:
                sql =
                        "SELECT CB_Expense_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
                para_1 = getCashBookId();
                break;
            case ACCTTYPE_CashReceipt:
                sql =
                        "SELECT CB_Receipt_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
                para_1 = getCashBookId();
                break;
            case ACCTTYPE_CashDifference:
                sql =
                        "SELECT CB_Differences_Acct FROM C_CashBook_Acct WHERE C_CashBook_ID=? AND C_AcctSchema_ID=?";
                para_1 = getCashBookId();
                break;

            /* Inventory Accounts */
            case ACCTTYPE_InvDifferences:
                sql =
                        "SELECT W_Differences_Acct FROM M_Warehouse_Acct WHERE M_Warehouse_ID=? AND C_AcctSchema_ID=?";
                para_1 = getWarehouseId();
                break;
            case ACCTTYPE_NotInvoicedReceipts:
                sql =
                        "SELECT NotInvoicedReceipts_Acct FROM C_BP_Group_Acct a, C_BPartner bp "
                                + "WHERE a.C_BP_Group_ID=bp.C_BP_Group_ID AND bp.C_BPartner_ID=? AND a.C_AcctSchema_ID=?";
                para_1 = getBusinessPartnerId();
                break;

            /* Project Accounts */
            case ACCTTYPE_ProjectAsset:
                sql = "SELECT PJ_Asset_Acct FROM C_Project_Acct WHERE C_Project_ID=? AND C_AcctSchema_ID=?";
                para_1 = getProjectId();
                break;
            case ACCTTYPE_ProjectWIP:
                sql = "SELECT PJ_WIP_Acct FROM C_Project_Acct WHERE C_Project_ID=? AND C_AcctSchema_ID=?";
                para_1 = getProjectId();
                break;

            /* GL Accounts */
            case ACCTTYPE_PPVOffset:
                sql = "SELECT PPVOffset_Acct FROM C_AcctSchema_GL WHERE C_AcctSchema_ID=?";
                para_1 = -1;
                break;
            case ACCTTYPE_CommitmentOffset:
                sql = "SELECT CommitmentOffset_Acct FROM C_AcctSchema_GL WHERE C_AcctSchema_ID=?";
                para_1 = -1;
                break;
            case ACCTTYPE_CommitmentOffsetSales:
                sql = "SELECT CommitmentOffsetSales_Acct FROM C_AcctSchema_GL WHERE C_AcctSchema_ID=?";
                para_1 = -1;
                break;
            default:
                log.severe("Not found AcctType=" + AcctType);
                return 0;
        }
        //  Do we have sql & Parameter
        if (sql == null || para_1 == 0) {
            log.severe("No Parameter for AcctType=" + AcctType + " - SQL=" + sql);
            return 0;
        }

        //  Get Acct
        int Account_ID = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            if (para_1 == -1) //  GL Accounts
                pstmt.setInt(1, as.getAccountingSchemaId());
            else {
                pstmt.setInt(1, para_1);
                pstmt.setInt(2, as.getAccountingSchemaId());
            }
            rs = pstmt.executeQuery();
            if (rs.next()) Account_ID = rs.getInt(1);
        } catch (SQLException e) {
            log.log(Level.SEVERE, "AcctType=" + AcctType + " - SQL=" + sql, e);
            return 0;
        } finally {

            rs = null;
            pstmt = null;
        }
        //	No account
        if (Account_ID == 0) {
            log.severe("NO account Type=" + AcctType + ", Record=" + p_po.getId());
            return 0;
        }
        return Account_ID;
    } //	getAccountId

    /**
     * Get the account for Accounting Schema
     *
     * @param AcctType see ACCTTYPE_*
     * @param as       accounting schema
     * @return Account
     */
    public final MAccount getAccount(int AcctType, AccountingSchema as) {
        int C_ValidCombination_ID = getValidCombinationId(AcctType, as);
        if (C_ValidCombination_ID == 0) return null;
        //	Return Account
        return MAccount.get(C_ValidCombination_ID);
    } //	getAccount

    /**
     * String Representation
     *
     * @return String
     */
    public String toString() {
        return p_po.toString();
    } //  toString

    /**
     * Get clientId
     *
     * @return client
     */
    public int getClientId() {
        return p_po.getClientId();
    } //	getClientId

    /**
     * Get orgId
     *
     * @return org
     */
    public int getOrgId() {
        return p_po.getOrgId();
    } //	getOrgId

    /**
     * Get Document No
     *
     * @return document No
     */
    public String getDocumentNo() {
        if (m_DocumentNo != null) return m_DocumentNo;
        int index = p_po.getColumnIndex("DocumentNo");
        if (index == -1) index = p_po.getColumnIndex("Name");
        if (index == -1) throw new UnsupportedOperationException("No DocumentNo");
        m_DocumentNo = (String) p_po.getValue(index);
        return m_DocumentNo;
    } //	getDocumentNo

    /**
     * Get Description
     *
     * @return Description
     */
    public String getDescription() {
        if (m_Description == null) {
            int index = p_po.getColumnIndex("Description");
            if (index != -1) m_Description = (String) p_po.getValue(index);
            else m_Description = "";
        }
        return m_Description;
    } //	getDescription

    /**
     * Get C_Currency_ID
     *
     * @return currency
     */
    public int getCurrencyId() {
        if (m_C_Currency_ID == -1) {
            int index = p_po.getColumnIndex("C_Currency_ID");
            if (index != -1) {
                Integer ii = (Integer) p_po.getValue(index);
                if (ii != null) m_C_Currency_ID = ii;
            }
            if (m_C_Currency_ID == -1) m_C_Currency_ID = NO_CURRENCY;
        }
        return m_C_Currency_ID;
    } //	getCurrencyId

    /**
     * Set C_Currency_ID
     *
     * @param C_Currency_ID id
     */
    public void setCurrencyId(int C_Currency_ID) {
        m_C_Currency_ID = C_Currency_ID;
    } //	setCurrencyId

    /**
     * Is Multi Currency
     *
     * @return mc
     */
    public boolean isMultiCurrency() {
        return m_MultiCurrency;
    } //	isMultiCurrency

    /**
     * Is Tax Included
     *
     * @return tax incl
     */
    public boolean isTaxIncluded() {
        return m_TaxIncluded;
    } //	isTaxIncluded

    /**
     * Set Tax Included
     *
     * @param ti Tax Included
     */
    public void setIsTaxIncluded(boolean ti) {
        m_TaxIncluded = ti;
    } //	setIsTaxIncluded

    /**
     * Get C_ConversionType_ID
     *
     * @return ConversionType
     */
    public int getConversionTypeId() {
        int index = p_po.getColumnIndex("C_ConversionType_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getConversionTypeId

    /**
     * Get GL_Category_ID
     *
     * @return category
     */
    public int getGLCategoryId() {
        int index = p_po.getColumnIndex("GL_Category_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return m_GL_Category_ID;
    } //	getGLCategoryId

    /**
     * Get GL_Category_ID
     *
     * @return category
     */
    public int getGLBudgetId() {
        int index = p_po.getColumnIndex("GL_Budget_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getGLBudgetId

    /**
     * Get Accounting Date
     *
     * @return currency
     */
    public Timestamp getDateAcct() {
        if (m_DateAcct != null) return m_DateAcct;
        int index = p_po.getColumnIndex("DateAcct");
        if (index != -1) {
            m_DateAcct = (Timestamp) p_po.getValue(index);
            if (m_DateAcct != null) return m_DateAcct;
        }
        throw new IllegalStateException("No DateAcct");
    } //	getDateAcct

    /**
     * Set Date Acct
     *
     * @param da accounting date
     */
    public void setDateAcct(Timestamp da) {
        m_DateAcct = da;
    } //	setDateAcct

    /**
     * Get Document Date
     *
     * @return currency
     */
    public Timestamp getDateDoc() {
        if (m_DateDoc != null) return m_DateDoc;
        int index = p_po.getColumnIndex("DateDoc");
        if (index == -1) index = p_po.getColumnIndex("MovementDate");
        if (index != -1) {
            m_DateDoc = (Timestamp) p_po.getValue(index);
            if (m_DateDoc != null) return m_DateDoc;
        }
        throw new IllegalStateException("No DateDoc");
    } //	getDateDoc

    /**
     * Set Date Doc
     *
     * @param dd document date
     */
    public void setDateDoc(Timestamp dd) {
        m_DateDoc = dd;
    } //	setDateDoc

    /**
     * Is Document Posted
     *
     * @return true if posted
     */
    public boolean isPosted() {
        int index = p_po.getColumnIndex("Posted");
        if (index != -1) {
            Object posted = p_po.getValue(index);
            if (posted instanceof Boolean) return (Boolean) posted;
            if (posted instanceof String) return "Y".equals(posted);
        }
        throw new IllegalStateException("No Posted");
    } //	isPosted

    /**
     * Is Sales Trx
     *
     * @return true if posted
     */
    public boolean isSOTrx() {
        int index = p_po.getColumnIndex("IsSOTrx");
        if (index == -1) index = p_po.getColumnIndex("IsReceipt");
        if (index != -1) {
            Object posted = p_po.getValue(index);
            if (posted instanceof Boolean) return (Boolean) posted;
            if (posted instanceof String) return "Y".equals(posted);
        }
        return false;
    } //	isSOTrx

    /**
     * Get C_DocType_ID
     *
     * @return DocType
     */
    public int getDocumentTypeId() {
        int index = p_po.getColumnIndex("C_DocType_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            //	DocType does not exist - get DocTypeTarget
            if (ii != null && ii == 0) {
                index = p_po.getColumnIndex("C_DocTypeTarget_ID");
                if (index != -1) ii = (Integer) p_po.getValue(index);
            }
            if (ii != null) return ii;
        } else {
            switch (p_po.getTableName()) {
                case I_M_MatchPO.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_MatMatchPO);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
                case I_M_MatchInv.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_MatMatchInv);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
                case I_C_AllocationHdr.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_Allocation);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
                case I_C_BankStatement.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_BankStatement);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
                case I_C_Cash.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_CashJournal);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
                case I_C_ProjectIssue.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_ProjectIssue);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
                case I_M_Production.Table_Name: {
                    int docTypeId =
                            getSQLValue(
                                    DOC_TYPE_BY_DOC_BASE_TYPE_SQL,
                                    p_po.getClientId(),
                                    Doc.DOCTYPE_MatProduction);
                    if (docTypeId > 0) return docTypeId;
                    break;
                }
            }
        }
        return 0;
    } //	getDocTypeId

    /**
     * Get header level C_Charge_ID
     *
     * @return Charge
     */
    public int getChargeId() {
        int index = p_po.getColumnIndex("C_Charge_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getChargeId

    /**
     * Get SalesRep_ID
     *
     * @return SalesRep
     */
    public int getSalesRepresentativeId() {
        int index = p_po.getColumnIndex("SalesRep_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getSalesRepresentativeId

    /**
     * Get C_BankAccount_ID
     *
     * @return BankAccount
     */
    public int getBankAccountId() {
        if (m_C_BankAccount_ID == -1) {
            int index = p_po.getColumnIndex("C_BankAccount_ID");
            if (index != -1) {
                Integer ii = (Integer) p_po.getValue(index);
                if (ii != null) m_C_BankAccount_ID = ii;
            }
            if (m_C_BankAccount_ID == -1) m_C_BankAccount_ID = 0;
        }
        return m_C_BankAccount_ID;
    } //	getBankAccountId

    /**
     * Set C_BankAccount_ID
     *
     * @param C_BankAccount_ID bank acct
     */
    public void setBankAccountId(int C_BankAccount_ID) {
        m_C_BankAccount_ID = C_BankAccount_ID;
    } //	setBankAccountId

    /**
     * Get C_CashBook_ID
     *
     * @return CashBook
     */
    public int getCashBookId() {
        if (m_C_CashBook_ID == -1) {
            int index = p_po.getColumnIndex("C_CashBook_ID");
            if (index != -1) {
                Integer ii = (Integer) p_po.getValue(index);
                if (ii != null) m_C_CashBook_ID = ii;
            }
            if (m_C_CashBook_ID == -1) m_C_CashBook_ID = 0;
        }
        return m_C_CashBook_ID;
    } //	getCashBookId

    /**
     * Set C_CashBook_ID
     *
     * @param C_CashBook_ID cash book
     */
    public void setCashBookId(int C_CashBook_ID) {
        m_C_CashBook_ID = C_CashBook_ID;
    } //	setCashBookId

    /**
     * Get M_Warehouse_ID
     *
     * @return Warehouse
     */
    public int getWarehouseId() {
        int index = p_po.getColumnIndex("M_Warehouse_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getWarehouseId

    /**
     * Get C_BPartner_ID
     *
     * @return BPartner
     */
    public int getBusinessPartnerId() {
        if (m_C_BPartner_ID == -1) {
            int index = p_po.getColumnIndex("C_BPartner_ID");
            if (index != -1) {
                Integer ii = (Integer) p_po.getValue(index);
                if (ii != null) m_C_BPartner_ID = ii;
            }
            if (m_C_BPartner_ID == -1) m_C_BPartner_ID = 0;
        }
        return m_C_BPartner_ID;
    } //	getBusinessPartnerId

    /**
     * Set C_BPartner_ID
     *
     * @param C_BPartner_ID bp
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
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
        return 0;
    } //	getBusinessPartnerLocationId

    /**
     * Get C_Project_ID
     *
     * @return Project
     */
    public int getProjectId() {
        int index = p_po.getColumnIndex("C_Project_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getProjectId

    /**
     * Get C_ProjectPhase_ID
     *
     * @return Project Phase
     */
    public int getProjectPhaseId() {
        int index = p_po.getColumnIndex("C_ProjectPhase_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getProjectPhaseId

    /**
     * Get C_ProjectTask_ID
     *
     * @return Project Task
     */
    public int getProjectTaskId() {
        int index = p_po.getColumnIndex("C_ProjectTask_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getProjectTaskId

    /**
     * Get C_SalesRegion_ID
     *
     * @return Sales Region
     */
    public int getSalesRegionId() {
        int index = p_po.getColumnIndex("C_SalesRegion_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getSalesRegionId

    /**
     * Get C_SalesRegion_ID
     *
     * @return Sales Region
     */
    public int getBusinessPartnerSalesRegionId() {
        if (m_BP_C_SalesRegion_ID == -1) {
            int index = p_po.getColumnIndex("C_SalesRegion_ID");
            if (index != -1) {
                Integer ii = (Integer) p_po.getValue(index);
                if (ii != null) m_BP_C_SalesRegion_ID = ii;
            }
            if (m_BP_C_SalesRegion_ID == -1) m_BP_C_SalesRegion_ID = 0;
        }
        return m_BP_C_SalesRegion_ID;
    } //	getBP_C_SalesRegion_ID

    /**
     * Set C_SalesRegion_ID
     *
     * @param C_SalesRegion_ID id
     */
    public void setBP_C_SalesRegionId(int C_SalesRegion_ID) {
        m_BP_C_SalesRegion_ID = C_SalesRegion_ID;
    } //	setBP_C_SalesRegion_ID

    /**
     * Get C_Activity_ID
     *
     * @return Activity
     */
    public int getBusinessActivityId() {
        int index = p_po.getColumnIndex("C_Activity_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getBusinessActivityId

    /**
     * Get C_Campaign_ID
     *
     * @return Campaign
     */
    public int getCampaignId() {
        int index = p_po.getColumnIndex("C_Campaign_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getCampaignId

    /**
     * Get M_Product_ID
     *
     * @return Product
     */
    public int getProductId() {
        int index = p_po.getColumnIndex("M_Product_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getProductId

    /**
     * Get AD_OrgTrx_ID
     *
     * @return Trx Org
     */
    public int getTransactionOrganizationId() {
        int index = p_po.getColumnIndex("AD_OrgTrx_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getTransactionOrganizationId

    /**
     * Get C_LocFrom_ID
     *
     * @return loc from
     */
    public int getLocationFromId() {
        return m_C_LocFrom_ID;
    } //	getLocationFromId

    /**
     * Get C_LocTo_ID
     *
     * @return loc to
     */
    public int getLocationToId() {
        return m_C_LocTo_ID;
    } //	getLocationToId

    /**
     * Get User1_ID
     *
     * @return Campaign
     */
    public int getUser1Id() {
        int index = p_po.getColumnIndex("User1_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getUser1Id

    /**
     * Get User2_ID
     *
     * @return Campaign
     */
    public int getUser2Id() {
        int index = p_po.getColumnIndex("User2_ID");
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getUser2Id

    /**
     * Get User Defined value
     *
     * @return User defined
     */
    public int getValue(String ColumnName) {
        int index = p_po.getColumnIndex(ColumnName);
        if (index != -1) {
            Integer ii = (Integer) p_po.getValue(index);
            if (ii != null) return ii;
        }
        return 0;
    } //	getValue

    /* ********************************************************************** */
    //  To be overwritten by Subclasses

    /**
     * Load Document Details
     *
     * @return error message or null
     */
    protected abstract String loadDocumentDetails();

    /**
     * Get Source Currency Balance - subtracts line (and tax) amounts from total - no rounding
     *
     * @return positive amount, if total header is bigger than lines
     */
    public abstract BigDecimal getBalance();

    /**
     * Create Facts (the accounting logic)
     *
     * @param as accounting schema
     * @return Facts
     */
    public abstract ArrayList<IFact> createFacts(AccountingSchema as);

    /**
     * Return document whether need to defer posting or not
     */
    public boolean isDeferPosting() {
        return false;
    }
} //  Doc
