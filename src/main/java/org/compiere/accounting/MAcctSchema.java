package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bo.MCurrency;
import org.compiere.bo.MCurrencyKt;
import org.compiere.crm.MClientInfo;
import org.compiere.crm.MClientInfoKt;
import org.compiere.model.AccountSchemaElement;
import org.compiere.model.AccountingSchema;
import org.compiere.model.ClientInfo;
import org.compiere.model.DefaultAccountsForSchema;
import org.compiere.model.I_C_AcctSchema_GL;
import org.compiere.orm.MClient;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.Query;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.KeyNamePair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Accounting Schema Model (base)
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 * <li>RF [ 2214883 ] Remove SQL code and Replace for Query
 * http://sourceforge.net/tracker/index.php?func=detail&aid=2214883&group_id=176962&atid=879335
 * @version $Id: MAcctSchema.java,v 1.4 2006/07/30 00:58:04 jjanke Exp $
 */
public class MAcctSchema extends X_C_AcctSchema implements AccountingSchema {
    /**
     *
     */
    private static final long serialVersionUID = 8940388112876468770L;
    /**
     * Cache of Client AcctSchema Arrays *
     */
    private static CCache<Integer, AccountingSchema[]> s_schema =
            new CCache<>(ClientInfo.Table_Name, 120); //  3 clients
    /**
     * Cache of AcctSchemas *
     */
    private static CCache<Integer, MAcctSchema> s_cache =
            new CCache<>(
                    AccountingSchema.Table_Name, 120); //  3 accounting schemas
    /**
     * GL Info
     */
    private I_C_AcctSchema_GL m_gl = null;
    /**
     * Default Info
     */
    private DefaultAccountsForSchema m_default = null;
    private MAccount m_SuspenseError_Acct = null;
    private MAccount m_CurrencyBalancing_Acct = null;
    private MAccount m_DueTo_Acct = null;
    private MAccount m_DueFrom_Acct = null;
    /**
     * Accounting Currency Precision
     */
    private int m_stdPrecision = -1;
    /**
     * Costing Currency Precision
     */
    private int m_costPrecision = -1;
    /**
     * Only Post Org
     */
    private MOrg m_onlyOrg = null;
    /**
     * Only Post Org Childs
     */
    private Integer[] m_onlyOrgs = null;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_AcctSchema_ID id
     */
    public MAcctSchema(int C_AcctSchema_ID) {
        super(C_AcctSchema_ID);
        if (C_AcctSchema_ID == 0) {
            setAutoPeriodControl(true);
            setPeriodOpenFuture(2);
            setPeriodOpenHistory(2);
            setCostingMethod(X_C_AcctSchema.COSTINGMETHOD_StandardCosting);
            setCostingLevel(X_C_AcctSchema.COSTINGLEVEL_Client);
            setIsAdjustCOGS(false);
            setGAAP(X_C_AcctSchema.GAAP_InternationalGAAP);
            setHasAlias(true);
            setHasCombination(false);
            setIsAccrual(true); // Y
            setCommitmentType(X_C_AcctSchema.COMMITMENTTYPE_None);
            setIsDiscountCorrectsTax(false);
            setTaxCorrectionType(X_C_AcctSchema.TAXCORRECTIONTYPE_None);
            setIsTradeDiscountPosted(false);
            setIsPostServices(false);
            setIsExplicitCostAdjustment(false);
            setSeparator("-"); // -
        }
    } //	MAcctSchema

    /**
     * Load Constructor
     *
     */
    public MAcctSchema(Row row) {
        super(row);
    } //	MAcctSchema

    /**
     * Parent Constructor
     *
     * @param client   client
     * @param currency currency
     */
    public MAcctSchema(MClient client, KeyNamePair currency) {
        this(0);
        setClientOrg(client);
        setCurrencyId(currency.getKey());
        String msgset = client.getName() +
                " " +
                getGAAP() +
                "/" +
                getColumnCount() +
                " " +
                currency.getName();
        setName(msgset);
    } //	MAcctSchema

    /**
     * Get AccountSchema of Client
     *
     * @param C_AcctSchema_ID schema id
     * @return Accounting schema
     */
    public static MAcctSchema get(int C_AcctSchema_ID) {
        //  Check Cache
        Integer key = C_AcctSchema_ID;
        MAcctSchema retValue = s_cache.get(key);
        if (retValue != null) return retValue;
        retValue = new MAcctSchema(C_AcctSchema_ID);
        s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * Get AccountSchema of Client
     *
     * @param AD_Client_ID client or 0 for all
     * @return Array of AcctSchema of Client
     */
    public static synchronized AccountingSchema[] getClientAcctSchema(
            int AD_Client_ID) {
        //  Check Cache
        Integer key = AD_Client_ID;
        if (s_schema.containsKey(key)) return s_schema.get(key);

        //  Create New
        ArrayList<AccountingSchema> list = new ArrayList<>();
        MClientInfo info = MClientInfoKt.getClientInfo(AD_Client_ID);
        MAcctSchema as = MAcctSchema.get(info.getAcctSchema1Id());
        if (as.getId() != 0) list.add(as);

        ArrayList<Object> params = new ArrayList<>();
        StringBuilder whereClause =
                new StringBuilder("IsActive=? ")
                        .append(
                                " AND EXISTS (SELECT * FROM C_AcctSchema_GL gl WHERE C_AcctSchema.C_AcctSchema_ID=gl.C_AcctSchema_ID)")
                        .append(
                                " AND EXISTS (SELECT * FROM C_AcctSchema_Default d WHERE C_AcctSchema.C_AcctSchema_ID=d.C_AcctSchema_ID)");
        params.add("Y");
        if (AD_Client_ID != 0) {
            whereClause.append(" AND AD_Client_ID=?");
            params.add(AD_Client_ID);
        }

        List<AccountingSchema> ass =
                new Query<AccountingSchema>(AccountingSchema.Table_Name, whereClause.toString())
                        .setParameters(params)
                        .setOrderBy(MAcctSchema.COLUMNNAME_C_AcctSchema_ID)
                        .list();

        for (AccountingSchema acctschema : ass) {
            if (acctschema.getId() != info.getAcctSchema1Id()) // 	already in list
            {
                if (acctschema.getId() != 0) list.add(acctschema);
            }
        }
        //  Save
        AccountingSchema[] retValue = new AccountingSchema[list.size()];
        list.toArray(retValue);
        s_schema.put(key, retValue);
        return retValue;
    } //  getClientAcctSchema

    /**
     * ************************************************************************ AcctSchema Elements
     *
     * @return ArrayList of AcctSchemaElement
     */
    public AccountSchemaElement[] getAcctSchemaElements() {
        return MAcctSchemaElement.getAcctSchemaElements(this);
    } //  getAcctSchemaElements

    /**
     * Get AcctSchema Element
     *
     * @param elementType segment type - AcctSchemaElement.ELEMENTTYPE_
     * @return AcctSchemaElement
     */
    public AccountSchemaElement getAcctSchemaElement(String elementType) {
        /* Element List */
        for (AccountSchemaElement ase : getAcctSchemaElements()) {
            if (ase.getElementType().equals(elementType)) return ase;
        }
        return null;
    } //  getAcctSchemaElement

    /**
     * Get AcctSchema GL info
     *
     * @return GL info
     */
    public I_C_AcctSchema_GL getAcctSchemaGL() {
        if (m_gl == null) m_gl = MAcctSchemaGL.get(getAccountingSchemaId());
        if (m_gl == null)
            throw new IllegalStateException(
                    "No GL Definition for C_AcctSchema_ID=" + getAccountingSchemaId());
        return m_gl;
    } //	getAcctSchemaGL

    /**
     * Get AcctSchema Defaults
     *
     * @return defaults
     */
    public DefaultAccountsForSchema getAcctSchemaDefault() {
        if (m_default == null) m_default = MAcctSchemaDefault.get(getAccountingSchemaId());
        if (m_default == null)
            throw new IllegalStateException(
                    "No Default Definition for C_AcctSchema_ID=" + getAccountingSchemaId());
        return m_default;
    } //	getAcctSchemaDefault

    /**
     * String representation
     *
     * @return String rep
     */
    public String toString() {
        return "AcctSchema[" + getId() + "-" + getName() + "]";
    } //	toString

    /**
     * Is Suspense Balancing active
     *
     * @return suspense balancing
     */
    public boolean isSuspenseBalancing() {
        if (m_gl == null) getAcctSchemaGL();
        return m_gl.isUseSuspenseBalancing() && m_gl.getSuspenseBalancingAccount() != 0;
    } //	isSuspenseBalancing

    /**
     * Get Suspense Error Account
     *
     * @return suspense error account
     */
    public MAccount getSuspenseBalancingAccount() {
        if (m_SuspenseError_Acct != null) return m_SuspenseError_Acct;
        if (m_gl == null) getAcctSchemaGL();
        int C_ValidCombination_ID = m_gl.getSuspenseBalancingAccount();
        m_SuspenseError_Acct = MAccount.get(C_ValidCombination_ID);
        return m_SuspenseError_Acct;
    } //	getSuspenseBalancing_Acct

    /**
     * Is Currency Balancing active
     *
     * @return suspense balancing
     */
    public boolean isCurrencyBalancing() {
        if (m_gl == null) getAcctSchemaGL();
        return m_gl.isUseCurrencyBalancing();
    } //	isSuspenseBalancing

    /**
     * Get Currency Balancing Account
     *
     * @return currency balancing account
     */
    public MAccount getCurrencyBalancingAccount() {
        if (m_CurrencyBalancing_Acct != null) return m_CurrencyBalancing_Acct;
        if (m_gl == null) getAcctSchemaGL();
        int C_ValidCombination_ID = m_gl.getCurrencyBalancingAccount();
        m_CurrencyBalancing_Acct = MAccount.get(C_ValidCombination_ID);
        return m_CurrencyBalancing_Acct;
    } //	getCurrencyBalancingAccount

    /**
     * Get Due To Account for Segment
     *
     * @param segment ignored
     * @return Account
     */
    public MAccount getDueToAccount(String segment) {
        if (m_DueTo_Acct != null) return m_DueTo_Acct;
        if (m_gl == null) getAcctSchemaGL();
        int C_ValidCombination_ID = m_gl.getIntercompanyDueToAccount();
        m_DueTo_Acct = MAccount.get(C_ValidCombination_ID);
        return m_DueTo_Acct;
    } //	getDueTo_Acct

    /**
     * Get Due From Account for Segment
     *
     * @param segment ignored
     * @return Account
     */
    public MAccount getDueFromAccount(String segment) {
        if (m_DueFrom_Acct != null) return m_DueFrom_Acct;
        if (m_gl == null) getAcctSchemaGL();
        int C_ValidCombination_ID = m_gl.getIntercompanyDueFromAccount();
        m_DueFrom_Acct = MAccount.get(C_ValidCombination_ID);
        return m_DueFrom_Acct;
    } //	getDueFrom_Acct

    /**
     * Get Std Precision of accounting Currency
     *
     * @return precision
     */
    public int getStdPrecision() {
        if (m_stdPrecision < 0) {
            MCurrency cur = MCurrencyKt.getCurrency(getCurrencyId());
            m_stdPrecision = cur.getStdPrecision();
            m_costPrecision = cur.getCostingPrecision();
        }
        return m_stdPrecision;
    } //	getStdPrecision

    /**
     * Get Costing Precision of accounting Currency
     *
     * @return precision
     */
    public int getCostingPrecision() {
        if (m_costPrecision < 0) getStdPrecision();
        return m_costPrecision;
    } //	getCostingPrecision

    /**
     * Check Costing Setup. Make sure that there is a Cost Type and Cost Element
     */
    public void checkCosting() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        //	Create Cost Type
        if (getCostTypeId() == 0) {
            MCostType ct = new MCostType(0);
            ct.setClientOrg(getClientId(), 0);
            ct.setName(getName());
            ct.saveEx();
            setCostTypeId(ct.getCostTypeId());
        }

        //	Create Cost Elements
        MCostElement.getMaterialCostElement(this, getCostingMethod());

        //	Default Costing Level
        if (getCostingLevel() == null) setCostingLevel(X_C_AcctSchema.COSTINGLEVEL_Client);
        if (getCostingMethod() == null) setCostingMethod(X_C_AcctSchema.COSTINGMETHOD_StandardCosting);
        if (getGAAP() == null) setGAAP(X_C_AcctSchema.GAAP_InternationalGAAP);
    } //	checkCosting

    /**
     * Create PO Commitment Accounting
     *
     * @return true if creaet commitments
     */
    public boolean isCreatePOCommitment() {
        String s = getCommitmentType();
        if (s == null) return false;
        return X_C_AcctSchema.COMMITMENTTYPE_POCommitmentOnly.equals(s)
                || X_C_AcctSchema.COMMITMENTTYPE_POCommitmentReservation.equals(s)
                || X_C_AcctSchema.COMMITMENTTYPE_POSOCommitmentReservation.equals(s)
                || X_C_AcctSchema.COMMITMENTTYPE_POSOCommitment.equals(s);
    } //	isCreateCommitment

    /**
     * Create SO Commitment Accounting
     *
     * @return true if creaet commitments
     */
    public boolean isCreateSOCommitment() {
        String s = getCommitmentType();
        if (s == null) return false;
        return X_C_AcctSchema.COMMITMENTTYPE_SOCommitmentOnly.equals(s)
                || X_C_AcctSchema.COMMITMENTTYPE_POSOCommitmentReservation.equals(s)
                || X_C_AcctSchema.COMMITMENTTYPE_POSOCommitment.equals(s);
    } //	isCreateCommitment

    /**
     * Create Commitment/Reservation Accounting
     *
     * @return true if create reservations
     */
    public boolean isCreateReservation() {
        String s = getCommitmentType();
        if (s == null) return false;
        return X_C_AcctSchema.COMMITMENTTYPE_POCommitmentReservation.equals(s)
                || X_C_AcctSchema.COMMITMENTTYPE_POSOCommitmentReservation.equals(s);
    } //	isCreateReservation

    /**
     * Get Tax Correction Type
     *
     * @return tax correction type
     */
    public String getTaxCorrectionType() {
        if (super.getTaxCorrectionType() == null) // 	created 07/23/06 2.5.3d
            setTaxCorrectionType(
                    isDiscountCorrectsTax()
                            ? X_C_AcctSchema.TAXCORRECTIONTYPE_Write_OffAndDiscount
                            : X_C_AcctSchema.TAXCORRECTIONTYPE_None);
        return super.getTaxCorrectionType();
    } //	getTaxCorrectionType

    /**
     * Tax Correction
     *
     * @return true if not none
     */
    public boolean isTaxCorrection() {
        return !getTaxCorrectionType().equals(X_C_AcctSchema.TAXCORRECTIONTYPE_None);
    } //	isTaxCorrection

    /**
     * Tax Correction for Discount
     *
     * @return true if tax is corrected for Discount
     */
    public boolean isTaxCorrectionDiscount() {
        return getTaxCorrectionType().equals(X_C_AcctSchema.TAXCORRECTIONTYPE_DiscountOnly)
                || getTaxCorrectionType().equals(X_C_AcctSchema.TAXCORRECTIONTYPE_Write_OffAndDiscount);
    } //	isTaxCorrectionDiscount

    /**
     * Tax Correction for WriteOff
     *
     * @return true if tax is corrected for WriteOff
     */
    public boolean isTaxCorrectionWriteOff() {
        return getTaxCorrectionType().equals(X_C_AcctSchema.TAXCORRECTIONTYPE_Write_OffOnly)
                || getTaxCorrectionType().equals(X_C_AcctSchema.TAXCORRECTIONTYPE_Write_OffAndDiscount);
    } //	isTaxCorrectionWriteOff

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (getOrgId() != 0) setOrgId(0);
        if (super.getTaxCorrectionType() == null)
            setTaxCorrectionType(
                    isDiscountCorrectsTax()
                            ? X_C_AcctSchema.TAXCORRECTIONTYPE_Write_OffAndDiscount
                            : X_C_AcctSchema.TAXCORRECTIONTYPE_None);
        checkCosting();
        //	Check Primary
        if (getOrganizationOnlyId() != 0) {
            MClientInfo info = MClientInfoKt.getClientInfo(getClientId());
            if (info.getAcctSchema1Id() == getAccountingSchemaId()) setOrganizationOnlyId(0);
        }
        return true;
    } //	beforeSave

    /**
     * Get Only Org Children
     *
     * @return array of orgId
     */
    public synchronized Integer[] getOnlyOrgs() {
        if (m_onlyOrgs == null) {
            MReportTree tree =
                    new MReportTree(0, true, MAcctSchemaElement.ELEMENTTYPE_Organization);
            m_onlyOrgs = tree.getChildIDs(getOrganizationOnlyId());
        }
        return m_onlyOrgs;
    } //	getOnlyOrgs

    /**
     * Set Only Org Childs
     *
     * @param orgs
     * @throws IllegalStateException every time when you call it
     * @deprecated only orgs are now fetched automatically
     */
    public void setOnlyOrgs(Integer[] orgs) {
        //		m_onlyOrgs = orgs;
        throw new IllegalStateException("The OnlyOrgs are now fetched automatically");
    } //	setOnlyOrgs

    /**
     * Skip creating postings for this Org.
     *
     * @param AD_Org_ID
     * @return true if to skip
     */
    public synchronized boolean isSkipOrg(int AD_Org_ID) {
        if (getOrganizationOnlyId() == 0) return false;
        //	Only Organization
        if (getOrganizationOnlyId() == AD_Org_ID) return false;
        if (m_onlyOrg == null) m_onlyOrg = MOrgKt.getOrg(getOrganizationOnlyId());
        //	Not Summary Only - i.e. skip it
        if (!m_onlyOrg.isSummary()) return true;
        final Integer[] onlyOrgs = getOnlyOrgs();
        if (onlyOrgs == null) {
            return false;
        }
        for (Integer onlyOrg : onlyOrgs) {
            if (AD_Org_ID == onlyOrg) return false;
        }
        return true;
    } //	isSkipOrg
} //	MAcctSchema
