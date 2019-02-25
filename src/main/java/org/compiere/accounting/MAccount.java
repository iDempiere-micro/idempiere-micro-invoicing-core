package org.compiere.accounting;

import org.compiere.crm.MLocation;
import org.compiere.crm.X_C_BPartner;
import org.compiere.orm.MOrg;
import org.compiere.orm.Query;
import org.compiere.product.X_M_Product;
import org.compiere.production.X_C_Project;
import org.idempiere.common.util.CLogger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Account Object Entity to maintain all segment values. C_ValidCombination
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 * <li>RF [ 2214883 ] Remove SQL code and Replace for Query
 * http://sourceforge.net/tracker/index.php?func=detail&aid=2214883&group_id=176962&atid=879335
 * @author Teo Sarca, www.arhipac.ro
 * <li>FR [ 2694043 ] Query. first/firstOnly usage best practice
 * @version $Id: MAccount.java,v 1.4 2006/07/30 00:58:04 jjanke Exp $
 */
public class MAccount extends X_C_ValidCombination {
    /**
     *
     */
    private static final long serialVersionUID = 7980515458720808532L;
    /**
     * Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MAccount.class);
    /**
     * Account Segment
     */
    private MElementValue m_accountEV = null;

    /**
     * ************************************************************************ Default constructor
     *
     * @param ctx                   context
     * @param C_ValidCombination_ID combination
     * @param trxName               transaction
     */
    public MAccount(Properties ctx, int C_ValidCombination_ID) {
        super(ctx, C_ValidCombination_ID);
        if (C_ValidCombination_ID == 0) {
            //	setAccount_ID (0);
            //	setAccountingSchemaId (0);
            setIsFullyQualified(false);
        }
    } //  MAccount

    /**
     * Load constructor
     *
     * @param ctx     context
     * @param rs      result set
     * @param trxName transaction
     */
    public MAccount(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    } //  MAccount

    /**
     * Parent Constructor
     *
     * @param as account schema
     */
    public MAccount(MAcctSchema as) {
        this(as.getCtx(), 0);
        setClientOrg(as);
        setC_AcctSchema_ID(as.getAccountingSchemaId());
    } //	Account

    /*
     * Deprecated - use the same method with trxName instead
     */
    @Deprecated
    public static MAccount get(
            Properties ctx,
            int ad_Client_ID,
            int ad_Org_ID,
            int c_AcctSchema_ID,
            int new_account_id,
            int c_SubAcct_ID,
            int m_Product_ID,
            int c_BPartner_ID,
            int ad_OrgTrx_ID,
            int c_LocFrom_ID,
            int c_LocTo_ID,
            int c_SalesRegion_ID,
            int c_Project_ID,
            int c_Campaign_ID,
            int c_Activity_ID,
            int user1_ID,
            int user2_ID,
            int userElement1_ID,
            int userElement2_ID) {
        return get(
                ctx,
                ad_Client_ID,
                ad_Org_ID,
                c_AcctSchema_ID,
                new_account_id,
                c_SubAcct_ID,
                m_Product_ID,
                c_BPartner_ID,
                ad_OrgTrx_ID,
                c_LocFrom_ID,
                c_LocTo_ID,
                c_SalesRegion_ID,
                c_Project_ID,
                c_Campaign_ID,
                c_Activity_ID,
                user1_ID,
                user2_ID,
                userElement1_ID,
                userElement2_ID,
                null);
    }

    /**
     * Get existing Account or create it
     *
     * @param ctx              context
     * @param AD_Client_ID
     * @param AD_Org_ID
     * @param C_AcctSchema_ID
     * @param Account_ID
     * @param C_SubAcct_ID
     * @param M_Product_ID
     * @param C_BPartner_ID
     * @param AD_OrgTrx_ID
     * @param C_LocFrom_ID
     * @param C_LocTo_ID
     * @param C_SalesRegion_ID
     * @param C_Project_ID
     * @param C_Campaign_ID
     * @param C_Activity_ID
     * @param User1_ID
     * @param User2_ID
     * @param UserElement1_ID
     * @param UserElement2_ID
     * @param trxName
     * @return account or null
     */
    public static MAccount get(
            Properties ctx,
            int AD_Client_ID,
            int AD_Org_ID,
            int C_AcctSchema_ID,
            int Account_ID,
            int C_SubAcct_ID,
            int M_Product_ID,
            int C_BPartner_ID,
            int AD_OrgTrx_ID,
            int C_LocFrom_ID,
            int C_LocTo_ID,
            int C_SalesRegion_ID,
            int C_Project_ID,
            int C_Campaign_ID,
            int C_Activity_ID,
            int User1_ID,
            int User2_ID,
            int UserElement1_ID,
            int UserElement2_ID,
            String trxName) {
        StringBuilder info = new StringBuilder();
        info.append("AD_Client_ID=").append(AD_Client_ID).append(",AD_Org_ID=").append(AD_Org_ID);
        //	Schema
        info.append(",C_AcctSchema_ID=").append(C_AcctSchema_ID);
        //	Account
        info.append(",Account_ID=").append(Account_ID).append(" ");

        ArrayList<Object> params = new ArrayList<Object>();
        //		Mandatory fields
        StringBuilder whereClause =
                new StringBuilder("AD_Client_ID=?") // 	#1
                        .append(" AND AD_Org_ID=?")
                        .append(" AND C_AcctSchema_ID=?")
                        .append(" AND Account_ID=?"); // 	#4
        params.add(AD_Client_ID);
        params.add(AD_Org_ID);
        params.add(C_AcctSchema_ID);
        params.add(Account_ID);
        //	Optional fields
        if (C_SubAcct_ID == 0) whereClause.append(" AND C_SubAcct_ID IS NULL");
        else {
            whereClause.append(" AND C_SubAcct_ID=?");
            params.add(C_SubAcct_ID);
        }
        if (M_Product_ID == 0) whereClause.append(" AND M_Product_ID IS NULL");
        else {
            whereClause.append(" AND M_Product_ID=?");
            params.add(M_Product_ID);
        }
        if (C_BPartner_ID == 0) whereClause.append(" AND C_BPartner_ID IS NULL");
        else {
            whereClause.append(" AND C_BPartner_ID=?");
            params.add(C_BPartner_ID);
        }
        if (AD_OrgTrx_ID == 0) whereClause.append(" AND AD_OrgTrx_ID IS NULL");
        else {
            whereClause.append(" AND AD_OrgTrx_ID=?");
            params.add(AD_OrgTrx_ID);
        }
        if (C_LocFrom_ID == 0) whereClause.append(" AND C_LocFrom_ID IS NULL");
        else {
            whereClause.append(" AND C_LocFrom_ID=?");
            params.add(C_LocFrom_ID);
        }
        if (C_LocTo_ID == 0) whereClause.append(" AND C_LocTo_ID IS NULL");
        else {
            whereClause.append(" AND C_LocTo_ID=?");
            params.add(C_LocTo_ID);
        }
        if (C_SalesRegion_ID == 0) whereClause.append(" AND C_SalesRegion_ID IS NULL");
        else {
            whereClause.append(" AND C_SalesRegion_ID=?");
            params.add(C_SalesRegion_ID);
        }
        if (C_Project_ID == 0) whereClause.append(" AND C_Project_ID IS NULL");
        else {
            whereClause.append(" AND C_Project_ID=?");
            params.add(C_Project_ID);
        }
        if (C_Campaign_ID == 0) whereClause.append(" AND C_Campaign_ID IS NULL");
        else {
            whereClause.append(" AND C_Campaign_ID=?");
            params.add(C_Campaign_ID);
        }
        if (C_Activity_ID == 0) whereClause.append(" AND C_Activity_ID IS NULL");
        else {
            whereClause.append(" AND C_Activity_ID=?");
            params.add(C_Activity_ID);
        }
        if (User1_ID == 0) whereClause.append(" AND User1_ID IS NULL");
        else {
            whereClause.append(" AND User1_ID=?");
            params.add(User1_ID);
        }
        if (User2_ID == 0) whereClause.append(" AND User2_ID IS NULL");
        else {
            whereClause.append(" AND User2_ID=?");
            params.add(User2_ID);
        }
        if (UserElement1_ID == 0) whereClause.append(" AND UserElement1_ID IS NULL");
        else {
            whereClause.append(" AND UserElement1_ID=?");
            params.add(UserElement1_ID);
        }
        if (UserElement2_ID == 0) whereClause.append(" AND UserElement2_ID IS NULL");
        else {
            whereClause.append(" AND UserElement2_ID=?");
            params.add(UserElement2_ID);
        }
        //	whereClause.append(" ORDER BY IsFullyQualified DESC");

        MAccount existingAccount =
                new Query(ctx, MAccount.Table_Name, whereClause.toString())
                        .setParameters(params)
                        .setOnlyActiveRecords(true)
                        .firstOnly();

        //	Existing
        if (existingAccount != null) return existingAccount;

        //	New
        MAccount newAccount = new MAccount(ctx, 0);
        newAccount.setClientOrg(AD_Client_ID, AD_Org_ID);
        newAccount.setC_AcctSchema_ID(C_AcctSchema_ID);
        newAccount.setAccount_ID(Account_ID);
        //	--  Optional Accounting fields
        newAccount.setC_SubAcct_ID(C_SubAcct_ID);
        newAccount.setM_Product_ID(M_Product_ID);
        newAccount.setC_BPartner_ID(C_BPartner_ID);
        newAccount.setAD_OrgTrx_ID(AD_OrgTrx_ID);
        newAccount.setC_LocFrom_ID(C_LocFrom_ID);
        newAccount.setC_LocTo_ID(C_LocTo_ID);
        newAccount.setC_SalesRegion_ID(C_SalesRegion_ID);
        newAccount.setC_Project_ID(C_Project_ID);
        newAccount.setC_Campaign_ID(C_Campaign_ID);
        newAccount.setC_Activity_ID(C_Activity_ID);
        newAccount.setUser1_ID(User1_ID);
        newAccount.setUser2_ID(User2_ID);
        newAccount.setUserElement1_ID(UserElement1_ID);
        newAccount.setUserElement2_ID(UserElement2_ID);
        //
        if (!newAccount.save()) {
            s_log.log(Level.SEVERE, "Could not create new account - " + info);
            return null;
        }
        if (s_log.isLoggable(Level.FINE)) s_log.fine("New: " + newAccount);
        return newAccount;
    } //	get

    /**
     * Get from existing Accounting fact
     *
     * @param fa accounting fact
     * @return account
     */
    public static MAccount get(X_Fact_Acct fa) {
        MAccount acct =
                get(
                        fa.getCtx(),
                        fa.getClientId(),
                        fa.getOrgId(),
                        fa.getC_AcctSchema_ID(),
                        fa.getAccount_ID(),
                        fa.getC_SubAcct_ID(),
                        fa.getM_Product_ID(),
                        fa.getC_BPartner_ID(),
                        fa.getAD_OrgTrx_ID(),
                        fa.getC_LocFrom_ID(),
                        fa.getC_LocTo_ID(),
                        fa.getC_SalesRegion_ID(),
                        fa.getC_Project_ID(),
                        fa.getC_Campaign_ID(),
                        fa.getC_Activity_ID(),
                        fa.getUser1_ID(),
                        fa.getUser2_ID(),
                        fa.getUserElement1_ID(),
                        fa.getUserElement2_ID());
        return acct;
    } //	get

    /**
     * Factory: default combination
     *
     * @param acctSchema   accounting schema
     * @param optionalNull if true, the optional values are null
     * @return Account
     */
    public static MAccount getDefault(MAcctSchema acctSchema, boolean optionalNull) {
        MAccount vc = new MAccount(acctSchema);
        //  Active Elements
        MAcctSchemaElement[] elements = acctSchema.getAcctSchemaElements();
        for (int i = 0; i < elements.length; i++) {
            MAcctSchemaElement ase = elements[i];
            String elementType = ase.getElementType();
            int defaultValue = ase.getDefaultValue();
            boolean setValue = ase.isMandatory() || (!ase.isMandatory() && !optionalNull);
            //
            if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_Organization))
                vc.setOrgId(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_Account))
                vc.setAccount_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_SubAccount) && setValue)
                vc.setC_SubAcct_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_BPartner) && setValue)
                vc.setC_BPartner_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_Product) && setValue)
                vc.setM_Product_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_Activity) && setValue)
                vc.setC_Activity_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_LocationFrom) && setValue)
                vc.setC_LocFrom_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_LocationTo) && setValue)
                vc.setC_LocTo_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_Campaign) && setValue)
                vc.setC_Campaign_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_OrgTrx) && setValue)
                vc.setAD_OrgTrx_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_Project) && setValue)
                vc.setC_Project_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_SalesRegion) && setValue)
                vc.setC_SalesRegion_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_UserElementList1) && setValue)
                vc.setUser1_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_UserElementList2) && setValue)
                vc.setUser2_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_UserColumn1) && setValue)
                vc.setUserElement1_ID(defaultValue);
            else if (elementType.equals(MAcctSchemaElement.ELEMENTTYPE_UserColumn2) && setValue)
                vc.setUserElement2_ID(defaultValue);
        }
        if (s_log.isLoggable(Level.FINE))
            s_log.fine(
                    "Client_ID="
                            + vc.getClientId()
                            + ", Org_ID="
                            + vc.getOrgId()
                            + " - AcctSchema_ID="
                            + vc.getC_AcctSchema_ID()
                            + ", Account_ID="
                            + vc.getAccount_ID());
        return vc;
    } //  getDefault

    /**
     * Get Account
     *
     * @param ctx                   context
     * @param C_ValidCombination_ID combination
     * @return Account
     */
    public static MAccount get(Properties ctx, int C_ValidCombination_ID) {
        //	Maybe later cache
        return new MAccount(ctx, C_ValidCombination_ID);
    } //  getAccount

    /**
     * Update Value/Description after change of account element value/description.
     *
     * @param ctx     context
     * @param where   where clause
     * @param trxName transaction
     */
    public static void updateValueDescription(Properties ctx, final String where) {
        List<MAccount> accounts =
                new Query(ctx, MAccount.Table_Name, where)
                        .setOrderBy(MAccount.COLUMNNAME_C_ValidCombination_ID)
                        .list();

        for (MAccount account : accounts) {
            account.setValueDescription();
            account.saveEx();
        }
    } //	updateValueDescription

    /**
     * ************************************************************************ Return String
     * representation
     *
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MAccount=[");
        sb.append(getC_ValidCombination_ID());
        if (getCombination() != null) sb.append(",").append(getCombination());
        else {
            //	.append(",Client=").append( getClientId())
            sb.append(",Schema=")
                    .append(getC_AcctSchema_ID())
                    .append(",Org=")
                    .append(getOrgId())
                    .append(",Acct=")
                    .append(getAccount_ID())
                    .append(" ");
            if (getC_SubAcct_ID() != 0) sb.append(",C_SubAcct_ID=").append(getC_SubAcct_ID());
            if (getM_Product_ID() != 0) sb.append(",M_Product_ID=").append(getM_Product_ID());
            if (getC_BPartner_ID() != 0) sb.append(",C_BPartner_ID=").append(getC_BPartner_ID());
            if (getAD_OrgTrx_ID() != 0) sb.append(",AD_OrgTrx_ID=").append(getAD_OrgTrx_ID());
            if (getC_LocFrom_ID() != 0) sb.append(",C_LocFrom_ID=").append(getC_LocFrom_ID());
            if (getC_LocTo_ID() != 0) sb.append(",C_LocTo_ID=").append(getC_LocTo_ID());
            if (getC_SalesRegion_ID() != 0) sb.append(",C_SalesRegion_ID=").append(getC_SalesRegion_ID());
            if (getC_Project_ID() != 0) sb.append(",C_Project_ID=").append(getC_Project_ID());
            if (getC_Campaign_ID() != 0) sb.append(",C_Campaign_ID=").append(getC_Campaign_ID());
            if (getC_Activity_ID() != 0) sb.append(",C_Activity_ID=").append(getC_Activity_ID());
            if (getUser1_ID() != 0) sb.append(",User1_ID=").append(getUser1_ID());
            if (getUser2_ID() != 0) sb.append(",User2_ID=").append(getUser2_ID());
            if (getUserElement1_ID() != 0) sb.append(",UserElement1_ID=").append(getUserElement1_ID());
            if (getUserElement2_ID() != 0) sb.append(",UserElement2_ID=").append(getUserElement2_ID());
        }
        sb.append("]");
        return sb.toString();
    } //	toString

    /**
     * Set Account_ID
     *
     * @param Account_ID id
     */
    public void setAccount_ID(int Account_ID) {
        m_accountEV = null; // 	reset
        super.setAccount_ID(Account_ID);
    } //	setAccount

    /**
     * Set Account_ID
     *
     * @return element value
     */
    public MElementValue getAccount() {
        if (m_accountEV == null) {
            if (getAccount_ID() != 0)
                m_accountEV = new MElementValue(getCtx(), getAccount_ID());
        }
        return m_accountEV;
    } //	setAccount

    /**
     * Get Account Type
     *
     * @return Account Type of Account Element
     */
    public String getAccountType() {
        if (m_accountEV == null) getAccount();
        if (m_accountEV == null) {
            log.log(Level.SEVERE, "No ElementValue for Account_ID=" + getAccount_ID());
            return "";
        }
        return m_accountEV.getAccountType();
    } //	getAccountType

    /**
     * Is this a Balance Sheet Account
     *
     * @return boolean
     */
    public boolean isBalanceSheet() {
        String accountType = getAccountType();
        return (MElementValue.ACCOUNTTYPE_Asset.equals(accountType)
                || MElementValue.ACCOUNTTYPE_Liability.equals(accountType)
                || MElementValue.ACCOUNTTYPE_OwnerSEquity.equals(accountType));
    } //	isBalanceSheet

    /**
     * Is this an Activa Account
     *
     * @return boolean
     */
    public boolean isActiva() {
        return MElementValue.ACCOUNTTYPE_Asset.equals(getAccountType());
    } //	isActive

    /**
     * Set Value and Description and Fully Qualified Flag for Combination
     */
    public void setValueDescription() {
        StringBuilder combi = new StringBuilder();
        StringBuilder descr = new StringBuilder();
        boolean fullyQualified = true;
        //
        MAcctSchema as = new MAcctSchema(getCtx(), getC_AcctSchema_ID()); // 	In Trx!
        MAcctSchemaElement[] elements = MAcctSchemaElement.getAcctSchemaElements(as);
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) {
                combi.append(as.getSeparator());
                descr.append(as.getSeparator());
            }
            MAcctSchemaElement element = elements[i];
            String combiStr = "_"; // 	not defined
            String descrStr = "_";

            if (MAcctSchemaElement.ELEMENTTYPE_Organization.equals(element.getElementType())) {
                if (getOrgId() != 0) {
                    MOrg org = new MOrg(getCtx(), getOrgId()); // 	in Trx!
                    combiStr = org.getSearchKey();
                    descrStr = org.getName();
                } else {
                    combiStr = "*";
                    descrStr = "*";
                    // fullyQualified = false; IDEMPIERE 159 - allow combination with org *
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_Account.equals(element.getElementType())) {
                if (getAccount_ID() != 0) {
                    if (m_accountEV == null)
                        m_accountEV = new MElementValue(getCtx(), getAccount_ID());
                    combiStr = m_accountEV.getSearchKey();
                    descrStr = m_accountEV.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Account");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_SubAccount.equals(element.getElementType())) {
                if (getC_SubAcct_ID() != 0) {
                    X_C_SubAcct sa = new X_C_SubAcct(getCtx(), getC_SubAcct_ID());
                    combiStr = sa.getSearchKey();
                    descrStr = sa.getName();
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_Product.equals(element.getElementType())) {
                if (getM_Product_ID() != 0) {
                    X_M_Product product = new X_M_Product(getCtx(), getM_Product_ID());
                    combiStr = product.getValue();
                    descrStr = product.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Product");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_BPartner.equals(element.getElementType())) {
                if (getC_BPartner_ID() != 0) {
                    X_C_BPartner partner = new X_C_BPartner(getCtx(), getC_BPartner_ID());
                    combiStr = partner.getSearchKey();
                    descrStr = partner.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Business Partner");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_OrgTrx.equals(element.getElementType())) {
                if (getAD_OrgTrx_ID() != 0) {
                    MOrg org = new MOrg(getCtx(), getAD_OrgTrx_ID()); // in Trx!
                    combiStr = org.getSearchKey();
                    descrStr = org.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Trx Org");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_LocationFrom.equals(element.getElementType())) {
                if (getC_LocFrom_ID() != 0) {
                    MLocation loc = new MLocation(getCtx(), getC_LocFrom_ID()); // 	in Trx!
                    combiStr = loc.getPostal();
                    descrStr = loc.getCity();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Location From");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_LocationTo.equals(element.getElementType())) {
                if (getC_LocTo_ID() != 0) {
                    MLocation loc = new MLocation(getCtx(), getC_LocFrom_ID()); // 	in Trx!
                    combiStr = loc.getPostal();
                    descrStr = loc.getCity();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Location To");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_SalesRegion.equals(element.getElementType())) {
                if (getC_SalesRegion_ID() != 0) {
                    MSalesRegion loc = new MSalesRegion(getCtx(), getC_SalesRegion_ID());
                    combiStr = loc.getSearchKey();
                    descrStr = loc.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: SalesRegion");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_Project.equals(element.getElementType())) {
                if (getC_Project_ID() != 0) {
                    X_C_Project project = new X_C_Project(getCtx(), getC_Project_ID());
                    combiStr = project.getSearchKey();
                    descrStr = project.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Project");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_Campaign.equals(element.getElementType())) {
                if (getC_Campaign_ID() != 0) {
                    X_C_Campaign campaign = new X_C_Campaign(getCtx(), getC_Campaign_ID());
                    combiStr = campaign.getSearchKey();
                    descrStr = campaign.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Campaign");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_Activity.equals(element.getElementType())) {
                if (getC_Activity_ID() != 0) {
                    X_C_Activity act = new X_C_Activity(getCtx(), getC_Activity_ID());
                    combiStr = act.getSearchKey();
                    descrStr = act.getName();
                } else if (element.isMandatory()) {
                    log.warning("Mandatory Element missing: Campaign");
                    fullyQualified = false;
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_UserElementList1.equals(element.getElementType())) {
                if (getUser1_ID() != 0) {
                    MElementValue ev = new MElementValue(getCtx(), getUser1_ID());
                    combiStr = ev.getSearchKey();
                    descrStr = ev.getName();
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_UserElementList2.equals(element.getElementType())) {
                if (getUser2_ID() != 0) {
                    MElementValue ev = new MElementValue(getCtx(), getUser2_ID());
                    combiStr = ev.getSearchKey();
                    descrStr = ev.getName();
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_UserColumn1.equals(element.getElementType())) {
                if (getUserElement1_ID() != 0) {
                }
            } else if (MAcctSchemaElement.ELEMENTTYPE_UserColumn2.equals(element.getElementType())) {
                if (getUserElement2_ID() != 0) {
                }
            }
            combi.append(combiStr);
            descr.append(descrStr);
        }
        //	Set Values
        super.setCombination(combi.toString());
        super.setDescription(descr.toString());
        if (fullyQualified != isFullyQualified()) setIsFullyQualified(fullyQualified);
        if (log.isLoggable(Level.FINE))
            log.fine(
                    "Combination="
                            + getCombination()
                            + " - "
                            + getDescription()
                            + " - FullyQualified="
                            + fullyQualified);
    } //	setValueDescription

    /**
     * Validate combination
     *
     * @return true if valid
     */
    public boolean validate() {
        boolean ok = true;
        //	Validate Sub-Account
        if (getC_SubAcct_ID() != 0) {
            X_C_SubAcct sa = new X_C_SubAcct(getCtx(), getC_SubAcct_ID());
            if (sa.getC_ElementValue_ID() != getAccount_ID()) {
                log.saveError(
                        "Error",
                        "C_SubAcct.C_ElementValue_ID="
                                + sa.getC_ElementValue_ID()
                                + "<>Account_ID="
                                + getAccount_ID());
                ok = false;
            }
        }
        return ok;
    } //	validate

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        setValueDescription();
        return validate();
    } //	beforeSave
} //	Account
