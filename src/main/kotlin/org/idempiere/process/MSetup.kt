package org.idempiere.process

import org.compiere.accounting.MAccount
import org.compiere.accounting.MAcctSchema
import org.compiere.accounting.MCalendar
import org.compiere.accounting.MCashBook
import org.compiere.accounting.MClient
import org.compiere.accounting.MElement
import org.compiere.accounting.MElementValue
import org.compiere.accounting.MProduct
import org.compiere.accounting.MWarehouse
import org.compiere.accounting.X_C_AcctSchema_Default
import org.compiere.accounting.X_C_AcctSchema_GL
import org.compiere.crm.MBPGroup
import org.compiere.crm.MBPartnerLocation
import org.compiere.crm.MLocation
import org.compiere.crm.MUser
import org.compiere.model.AccountingSchema
import org.compiere.orm.MDocType
import org.compiere.orm.MOrg
import org.compiere.orm.MRole
import org.compiere.orm.MRoleOrgAccess
import org.compiere.orm.MSequence
import org.compiere.orm.PO
import org.compiere.orm.checkClientSequences
import org.compiere.orm.getOrganizationInfo
import org.compiere.process.ProcessInfo
import org.compiere.process.ProcessInfoParameter
import org.compiere.process.ProcessUtil
import org.compiere.product.MDiscountSchema
import org.compiere.product.MPriceList
import org.compiere.product.MPriceListVersion
import org.compiere.product.MProductCategory
import org.compiere.product.MProductPrice
import org.compiere.production.MLocator
import org.compiere.tax.MTax
import org.compiere.util.DisplayType
import org.compiere.util.SystemIDs
import org.compiere.util.getElementTranslation
import org.compiere.util.getMsg
import org.compiere.util.translate
import org.idempiere.common.exceptions.AdempiereException
import org.idempiere.common.util.AdempiereSystemError
import org.idempiere.common.util.AdempiereUserError
import org.idempiere.common.util.CLogger
import org.idempiere.common.util.Env
import org.idempiere.common.util.KeyNamePair
import software.hsharp.core.orm.getTable
import software.hsharp.core.util.Environment
import software.hsharp.core.util.convertString
import software.hsharp.core.util.executeUpdateEx
import software.hsharp.core.util.prepareStatement
import software.hsharp.modules.Module
import java.io.File
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.UUID
import java.util.logging.Level

class MSetup : Module by Environment<Module>().module
/**
 * Constructor
 */ {

    /**	Logger			 */
    protected var log: CLogger = CLogger.getCLogger(javaClass)

    private val m_lang: String
    private var m_info: StringBuffer = StringBuffer()
    //
    private var m_clientName: String? = null
    // 	private String          m_orgName;
    //
    private val m_stdColumns = "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy"
    private var m_stdValues: String? = null
    private var m_stdValuesOrg: String? = null
    //
    private var m_nap: NaturalAccountMap<String, MElementValue>? = null

    //
    private var m_client: MClient = MClient(0, true)
    private var m_org: MOrg? = null
    private var m_as: MAcctSchema? = null
    //
    /**
     * Get AD_User_ID
     * @return AD_User_ID
     */
    var aD_User_ID: Int = 0
        private set
    private var AD_User_Name: String? = null
    private var AD_User_U_ID: Int = 0
    private var AD_User_U_Name: String? = null
    private var m_calendar: MCalendar? = null
    private var m_AD_Tree_Account_ID: Int = 0
    private var C_Cycle_ID: Int = 0
    //
    private var m_hasProject = false
    private var m_hasMCampaign = false
    private var m_hasSRegion = false
    private var m_hasActivity = false

    /**
     * Get Client
     * @return clientId
     */
    val aD_Client_ID: Int
        get() = m_client.clientId
    /**
     * Get orgId
     * @return orgId
     */
    val aD_Org_ID: Int
        get() = m_org!!.orgId
    /**
     * Get Info
     * @return Info
     */
    val info: String
        get() = m_info.toString()

    init {
        // 	copy
        m_lang = Env.getADLanguage()
    } //  MSetup

    /**
     * Create Client Info.
     * - Client, Trees, Org, Role, User, User_Role
     * @param clientName client name
     * @param orgName org name
     * @param userClient user id client
     * @param userOrg user id org
     * @param isSetInitialPassword
     * @return true if created
     */
    fun createClient(
        clientName: String,
        orgValue: String?,
        orgName: String,
        userClient: String,
        userOrg: String,
        phone: String,
        phone2: String,
        fax: String,
        eMail: String,
        taxID: String?,
        adminEmail: String,
        userEmail: String,
        isSetInitialPassword: Boolean
    ): Boolean {
        var orgValue1 = orgValue
        log.info(clientName)

        //  info header
        m_info = StringBuffer()
        //  Standard columns
        var name: String?
        var sql: String?
        var no: Int

        /**
         * Create Client
         */
        name = clientName
        if (name.isEmpty())
            name = "newClient"
        m_clientName = name
        m_client = MClient(0, true)
        m_client.searchKey = m_clientName!!
        m_client.name = m_clientName!!
        if (!m_client.save()) {
            val err = "Client NOT created"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        val AD_Client_ID = m_client.clientId
        Environment.current.login(AD_Client_ID, 0, 0)

        // 	Standard Values
        m_stdValues = AD_Client_ID.toString() + ",0,'Y',SysDate,0,SysDate,0"
        //  Info - Client
        m_info.append(translate(m_lang, "AD_Client_ID")).append("=").append(name).append("\n")

        // 	Setup Sequences
        if (!checkClientSequences(AD_Client_ID)) {
            val err = "Sequences NOT created"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }

        //  Trees and Client Info
        if (!m_client.setupClientInfo(m_lang)) {
            val err = "Client Info NOT created"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        m_AD_Tree_Account_ID = m_client.setup_AD_Tree_Account_ID

        /**
         * Create Org
         */
        name = orgName
        if (name.isEmpty())
            name = "newOrg"
        if (orgValue1 == null || orgValue1.length == 0)
            orgValue1 = name
        m_org = MOrg(m_client, orgValue1, name)
        if (!m_org!!.save()) {
            val err = "Organization NOT created"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        Environment.current.login(AD_Client_ID, aD_Org_ID, 0)

        m_stdValuesOrg = AD_Client_ID.toString() + "," + aD_Org_ID + ",'Y',SysDate,0,SysDate,0"
        //  Info
        m_info.append(translate(m_lang, "AD_Org_ID")).append("=").append(name).append("\n")

        // Set Organization Phone, Phone2, Fax, EMail
        val orgInfo = getOrganizationInfo(aD_Org_ID)
        orgInfo.setPhone(phone)
        orgInfo.setPhone2(phone2)
        orgInfo.setFax(fax)
        orgInfo.setEMail(eMail)
        if (taxID != null && taxID.length > 0) {
            orgInfo.setTaxID(taxID)
        }
        if (!orgInfo.save()) {
            val err = "Organization Info NOT Updated"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }

        /**
         * Create Roles
         * - Admin
         * - User
         */
        name = m_clientName!! + " Admin"
        val admin = MRole(0)
        admin.setClientOrg(m_client)
        admin.name = name
        admin.userLevel = MRole.USERLEVEL_ClientPlusOrganization
        admin.setPreferenceType(MRole.PREFERENCETYPE_Client)
        admin.setIsShowAcct(true)
        admin.setIsAccessAdvanced(true)
        if (!admin.save()) {
            val err = "Admin Role A NOT inserted"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        // 	OrgAccess x, 0
        val adminClientAccess = MRoleOrgAccess(admin, 0)
        if (!adminClientAccess.save())
            log.log(Level.SEVERE, "Admin Role_OrgAccess 0 NOT created")
        //  OrgAccess x,y
        val adminOrgAccess = MRoleOrgAccess(admin, m_org!!.orgId)
        if (!adminOrgAccess.save())
            log.log(Level.SEVERE, "Admin Role_OrgAccess NOT created")

        //  Info - Admin Role
        m_info.append(translate(m_lang, "AD_Role_ID")).append("=").append(name).append("\n")

        //
        name = m_clientName!! + " User"
        val user = MRole(0)
        user.setClientOrg(m_client)
        user.name = name
        user.setIsAccessAdvanced(false)
        if (!user.save()) {
            val err = "User Role A NOT inserted"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        //  OrgAccess x,y
        val userOrgAccess = MRoleOrgAccess(user, m_org!!.orgId)
        if (!userOrgAccess.save())
            log.log(Level.SEVERE, "User Role_OrgAccess NOT created")

        //  Info - Client Role
        m_info.append(translate(m_lang, "AD_Role_ID")).append("=").append(name).append("\n")

        /**
         * Create Users
         * - Client
         * - Org
         */
        val clientAdminUser = MUser(0)

        name = userClient
        if (name.isEmpty())
            name = m_clientName!! + "Client"

        if (isSetInitialPassword)
            clientAdminUser.password = name
        clientAdminUser.description = name
        clientAdminUser.name = name
        clientAdminUser.searchKey = name
        clientAdminUser.setClientId(AD_Client_ID)
        clientAdminUser.setOrgId(0)
        clientAdminUser.eMail = adminEmail

        try {
            clientAdminUser.saveEx()
        } catch (ex: AdempiereException) {
            val err = "Admin User NOT inserted - " + AD_User_Name!!
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }

        aD_User_ID = clientAdminUser.userId
        AD_User_Name = name

        //  Info
        m_info.append(translate(m_lang, "AD_User_ID")).append("=").append(AD_User_Name).append("/")
            .append(AD_User_Name).append("\n")

        val clientUser = MUser(0)

        name = userOrg
        if (name.isEmpty())
            name = m_clientName!! + "Org"

        if (isSetInitialPassword)
            clientUser.password = name
        clientUser.description = name
        clientUser.name = name
        clientUser.searchKey = name
        clientUser.setClientId(AD_Client_ID)
        clientUser.setOrgId(0)
        clientUser.eMail = userEmail

        try {
            clientUser.saveEx()
        } catch (ex: AdempiereException) {
            val err = "Org User NOT inserted - " + AD_User_U_Name!!
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }

        AD_User_U_ID = clientUser.userId
        AD_User_U_Name = name
        //  Info
        m_info.append(translate(m_lang, "AD_User_ID")).append("=").append(AD_User_U_Name).append("/")
            .append(AD_User_U_Name).append("\n")

        /**
         * Create User-Role
         */
        //  ClientUser          - Admin & User
        sql = ("INSERT INTO AD_User_Roles(" + m_stdColumns + ",AD_User_ID,AD_Role_ID,AD_User_Roles_UU)" +
                " VALUES (" + m_stdValues + "," + aD_User_ID + "," + admin.roleId + "," + convertString(UUID.randomUUID().toString()) + ")")
        no = executeUpdateEx(sql)
        if (no != 1)
            log.log(Level.SEVERE, "UserRole ClientUser+Admin NOT inserted")
        sql = ("INSERT INTO AD_User_Roles(" + m_stdColumns + ",AD_User_ID,AD_Role_ID,AD_User_Roles_UU)" +
                " VALUES (" + m_stdValues + "," + aD_User_ID + "," + user.roleId + "," + convertString(UUID.randomUUID().toString()) + ")")
        no = executeUpdateEx(sql)
        if (no != 1)
            log.log(Level.SEVERE, "UserRole ClientUser+User NOT inserted")
        //  OrgUser             - User
        sql = ("INSERT INTO AD_User_Roles(" + m_stdColumns + ",AD_User_ID,AD_Role_ID,AD_User_Roles_UU)" +
                " VALUES (" + m_stdValues + "," + AD_User_U_ID + "," + user.roleId + "," + convertString(UUID.randomUUID().toString()) + ")")
        no = executeUpdateEx(sql)
        if (no != 1)
            log.log(Level.SEVERE, "UserRole OrgUser+Org NOT inserted")

        // 	Processors
        val ap = AccountingProcessorModel(m_client, aD_User_ID)
        ap.scheduleId = SystemIDs.SCHEDULE_10_MINUTES
        ap.saveEx()

        val rp = MRequestProcessor(m_client, aD_User_ID)
        rp.scheduleId = SystemIDs.SCHEDULE_15_MINUTES
        rp.saveEx()

        log.info("fini")
        return true
    } //  createClient

    /**************************************************************************
     * Create Accounting elements.
     * - Calendar
     * - Account Trees
     * - Account Values
     * - Accounting Schema
     * - Default Accounts
     *
     * @param currency currency
     * @param hasProduct has product segment
     * @param hasBPartner has bp segment
     * @param hasProject has project segment
     * @param hasMCampaign has campaign segment
     * @param hasSRegion has sales region segment
     * @param hasActivity has activity segment
     * @param AccountingFile file name of accounting file
     * @param inactivateDefaults inactivate the default accounts after created
     * @param useDefaultCoA use the Default CoA (load and group summary account)
     * @return true if created
     */
    fun createAccounting(
        currency: KeyNamePair,
        hasProduct: Boolean,
        hasBPartner: Boolean,
        hasProject: Boolean,
        hasMCampaign: Boolean,
        hasSRegion: Boolean,
        hasActivity: Boolean,
        AccountingFile: File,
        inactivateDefaults: Boolean
    ): Boolean {
        if (log.isLoggable(Level.INFO)) log.info(m_client.toString())
        //
        m_hasProject = hasProject
        m_hasMCampaign = hasMCampaign
        m_hasSRegion = hasSRegion
        m_hasActivity = hasActivity

        //  Standard variables
        m_info = StringBuffer()
        var name: String?
        var sqlCmd: StringBuffer?
        var no: Int

        /**
         * Create Calendar
         */
        m_calendar = MCalendar(m_client)
        if (!m_calendar!!.save()) {
            val err = "Calendar NOT inserted"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        //  Info
        m_info.append(translate(m_lang, "C_Calendar_ID")).append("=").append(m_calendar!!.name).append("\n")

        if (m_calendar!!.createYear(m_client.locale) == null)
            log.log(Level.SEVERE, "Year NOT inserted")

        // 	Create Account Elements
        name = m_clientName + " " + translate(m_lang, "Account_ID")
        val element = MElement(
            m_client, name,
            MElement.ELEMENTTYPE_Account, m_AD_Tree_Account_ID
        )
        if (!element.save()) {
            val err = "Acct Element NOT inserted"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        val C_Element_ID = element.elementId
        m_info.append(translate(m_lang, "C_Element_ID")).append("=").append(name).append("\n")

        // 	Create Account Values
        m_nap = NaturalAccountMap()
        val errMsg = m_nap!!.parseFile(AccountingFile)
        if (errMsg.length != 0) {
            log.log(Level.SEVERE, errMsg)
            m_info.append(errMsg)
            throw Error(errMsg)
        }
        if (m_nap!!.saveAccounts(aD_Client_ID, aD_Org_ID, C_Element_ID, !inactivateDefaults))
            m_info.append(translate(m_lang, "C_ElementValue_ID")).append(" # ").append(m_nap!!.size).append("\n")
        else {
            val err = "Acct Element Values NOT inserted"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }

        val summary_ID = m_nap!!.getElementValueId("SUMMARY")
        if (log.isLoggable(Level.FINE)) log.fine("summary_ID=$summary_ID")
        if (summary_ID > 0) {
            executeUpdateEx(
                "UPDATE AD_TreeNode SET Parent_ID=? WHERE AD_Tree_ID=? AND Node_ID!=?",
                arrayOf<Any>(summary_ID, m_AD_Tree_Account_ID, summary_ID)
            )
        }

        val C_ElementValue_ID = m_nap!!.getElementValueId("DEFAULT_ACCT")
        if (log.isLoggable(Level.FINE)) log.fine("C_ElementValue_ID=$C_ElementValue_ID")

        /**
         * Create AccountingSchema
         */
        m_as = MAcctSchema(m_client, currency)
        if (!m_as!!.save()) {
            val err = "AcctSchema NOT inserted"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        //  Info
        m_info.append(translate(m_lang, "C_AcctSchema_ID")).append("=").append(m_as!!.name).append("\n")

        /**
         * Create AccountingSchema Elements (Structure)
         */
        val sql2: String?
        if (Env.isBaseLanguage(m_lang))
        // 	Get ElementTypes & Name
            sql2 = "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=181"
        else
            sql2 = ("SELECT l.Value, t.Name FROM AD_Ref_List l, AD_Ref_List_Trl t " +
                    "WHERE l.AD_Reference_ID=181 AND l.AD_Ref_List_ID=t.AD_Ref_List_ID" +
                    " AND t.AD_Language=" + convertString(m_lang)) // bug [ 1638421 ]
        val stmt: PreparedStatement?
        val rs: ResultSet?
        try {
            val AD_Client_ID = m_client.clientId
            stmt = prepareStatement(sql2)
            rs = stmt!!.executeQuery()
            while (rs!!.next()) {
                val ElementType = rs.getString(1)
                name = rs.getString(2)
                //
                var IsMandatory: String? = null
                var IsBalanced = "N"
                var SeqNo = 0
                var C_AcctSchema_Element_ID = 0

                if (ElementType == "OO") {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "Y"
                    IsBalanced = "Y"
                    SeqNo = 10
                } else if (ElementType == "AC") {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "Y"
                    SeqNo = 20
                } else if (ElementType == "PR" && hasProduct) {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "N"
                    SeqNo = 30
                } else if (ElementType == "BP" && hasBPartner) {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "N"
                    SeqNo = 40
                } else if (ElementType == "PJ" && hasProject) {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "N"
                    SeqNo = 50
                } else if (ElementType == "MC" && hasMCampaign) {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "N"
                    SeqNo = 60
                } else if (ElementType == "SR" && hasSRegion) {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "N"
                    SeqNo = 70
                } else if (ElementType == "AY" && hasActivity) {
                    C_AcctSchema_Element_ID = getNextID(AD_Client_ID, "C_AcctSchema_Element")
                    IsMandatory = "N"
                    SeqNo = 80
                }
                // 	Not OT, LF, LT, U1, U2

                if (IsMandatory != null) {
                    sqlCmd = StringBuffer("INSERT INTO C_AcctSchema_Element(")
                    sqlCmd.append(m_stdColumns).append(",C_AcctSchema_Element_ID,C_AcctSchema_ID,")
                        .append("ElementType,Name,SeqNo,IsMandatory,IsBalanced,C_AcctSchema_Element_UU) VALUES (")
                    sqlCmd.append(m_stdValues).append(",").append(C_AcctSchema_Element_ID).append(",")
                        .append(m_as!!.accountingSchemaId).append(",")
                        .append("'").append(ElementType).append("','").append(name).append("',").append(SeqNo)
                        .append(",'")
                        .append(IsMandatory).append("','").append(IsBalanced).append("',")
                        .append(convertString(UUID.randomUUID().toString())).append(")")
                    no = executeUpdateEx(sqlCmd.toString())
                    if (no == 1)
                        m_info.append(
                            translate(
                                m_lang,
                                "C_AcctSchema_Element_ID"
                            )
                        ).append("=").append(name).append("\n")

                    /** Default value for mandatory elements: OO and AC  */
                    if (ElementType == "OO") {
                        sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET Org_ID=")
                        sqlCmd.append(aD_Org_ID).append(" WHERE C_AcctSchema_Element_ID=")
                            .append(C_AcctSchema_Element_ID)
                        no = executeUpdateEx(sqlCmd.toString())
                        if (no != 1)
                            log.log(Level.SEVERE, "Default Org in AcctSchemaElement NOT updated")
                    }
                    if (ElementType == "AC") {
                        sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET C_ElementValue_ID=")
                        sqlCmd.append(C_ElementValue_ID).append(", C_Element_ID=").append(C_Element_ID)
                        sqlCmd.append(" WHERE C_AcctSchema_Element_ID=").append(C_AcctSchema_Element_ID)
                        no = executeUpdateEx(sqlCmd.toString())
                        if (no != 1)
                            log.log(Level.SEVERE, "Default Account in AcctSchemaElement NOT updated")
                    }
                }
            }
        } catch (e1: SQLException) {
            log.log(Level.SEVERE, "Elements", e1)
            m_info.append(e1.message)
            throw Error(e1.message)
        } finally {
        }
        //  Create AcctSchema

        //  Create Defaults Accounts
        try {
            createAccountingRecord(X_C_AcctSchema_GL.Table_Name)
            createAccountingRecord(X_C_AcctSchema_Default.Table_Name)
        } catch (e: Exception) {
            val err = e.localizedMessage
            log.log(Level.SEVERE, err)
            e.printStackTrace()
            m_info.append(err)
            throw Error(err)
        }

        //  GL Categories
        createGLCategory("Standard", MGLCategory.CATEGORYTYPE_Manual, true)
        val GL_None = createGLCategory("None", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_GL = createGLCategory("Manual", MGLCategory.CATEGORYTYPE_Manual, false)
        val GL_ARI = createGLCategory("AR Invoice", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_ARR = createGLCategory("AR Receipt", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_MM = createGLCategory("Material Management", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_API = createGLCategory("AP Invoice", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_APP = createGLCategory("AP Payment", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_CASH = createGLCategory("Cash/Payments", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_Manufacturing = createGLCategory("Manufacturing", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_Distribution = createGLCategory("Distribution", MGLCategory.CATEGORYTYPE_Document, false)
        val GL_Payroll = createGLCategory("Payroll", MGLCategory.CATEGORYTYPE_Document, false)

        // 	Base DocumentTypes
        val ii = createDocType(
            "GL Journal", getElementTranslation("GL_Journal_ID"),
            MDocType.DOCBASETYPE_GLJournal, null, 0, 0, 1000, GL_GL, false
        )
        if (ii == 0) {
            val err = "Document Type not created"
            m_info.append(err)
            throw Error(err)
        }
        createDocType(
            "GL Journal Batch", getElementTranslation("GL_JournalBatch_ID"),
            MDocType.DOCBASETYPE_GLJournal, null, 0, 0, 100, GL_GL, false
        )
        // 	MDocType.DOCBASETYPE_GLDocument
        //
        val DT_I = createDocType(
            "AR Invoice", getElementTranslation("C_Invoice_ID", true),
            MDocType.DOCBASETYPE_ARInvoice, null, 0, 0, 100000, GL_ARI, false
        )
        val DT_II = createDocType(
            "AR Invoice Indirect", getElementTranslation("C_Invoice_ID", true),
            MDocType.DOCBASETYPE_ARInvoice, null, 0, 0, 150000, GL_ARI, false
        )
        val DT_IC = createDocType(
            "AR Credit Memo", getMsg("CreditMemo"),
            MDocType.DOCBASETYPE_ARCreditMemo, null, 0, 0, 170000, GL_ARI, false
        )
        // 	MDocType.DOCBASETYPE_ARProFormaInvoice

        createDocType(
            "AP Invoice", getElementTranslation("C_Invoice_ID", false),
            MDocType.DOCBASETYPE_APInvoice, null, 0, 0, 0, GL_API, false
        )
        val DT_IPC = createDocType(
            "AP CreditMemo", getMsg("CreditMemo"),
            MDocType.DOCBASETYPE_APCreditMemo, null, 0, 0, 0, GL_API, false
        )
        createDocType(
            "Match Invoice", getElementTranslation("M_MatchInv_ID", false),
            MDocType.DOCBASETYPE_MatchInvoice, null, 0, 0, 390000, GL_API, false
        )

        createDocType(
            "AR Receipt", getElementTranslation("C_Payment_ID", true),
            MDocType.DOCBASETYPE_ARReceipt, null, 0, 0, 0, GL_ARR, false
        )
        createDocType(
            "AP Payment", getElementTranslation("C_Payment_ID", false),
            MDocType.DOCBASETYPE_APPayment, null, 0, 0, 0, GL_APP, false
        )
        createDocType(
            "Allocation", "Allocation",
            MDocType.DOCBASETYPE_PaymentAllocation, null, 0, 0, 490000, GL_CASH, false
        )

        val DT_S = createDocType(
            "MM Shipment", "Delivery Note",
            MDocType.DOCBASETYPE_MaterialDelivery, null, 0, 0, 500000, GL_MM, false
        )
        val DT_SI = createDocType(
            "MM Shipment Indirect", "Delivery Note",
            MDocType.DOCBASETYPE_MaterialDelivery, null, 0, 0, 550000, GL_MM, false
        )
        val DT_VRM = createDocType(
            "MM Vendor Return", "Vendor Return",
            MDocType.DOCBASETYPE_MaterialDelivery, null, 0, 0, 590000, GL_MM, true
        )

        createDocType(
            "MM Receipt", "Vendor Delivery",
            MDocType.DOCBASETYPE_MaterialReceipt, null, 0, 0, 0, GL_MM, false
        )
        val DT_RM = createDocType(
            "MM Customer Return", "Customer Return",
            MDocType.DOCBASETYPE_MaterialReceipt, null, 0, 0, 570000, GL_MM, true
        )

        createDocType(
            "Purchase Order", getElementTranslation("C_Order_ID", false),
            MDocType.DOCBASETYPE_PurchaseOrder, null, 0, 0, 800000, GL_None, false
        )
        createDocType(
            "Match PO", getElementTranslation("M_MatchPO_ID", false),
            MDocType.DOCBASETYPE_MatchPO, null, 0, 0, 890000, GL_None, false
        )
        createDocType(
            "Purchase Requisition", getElementTranslation("M_Requisition_ID", false),
            MDocType.DOCBASETYPE_PurchaseRequisition, null, 0, 0, 900000, GL_None, false
        )
        createDocType(
            "Vendor Return Material", "Vendor Return Material Authorization",
            MDocType.DOCBASETYPE_PurchaseOrder, MDocType.DOCSUBTYPESO_ReturnMaterial, DT_VRM,
            DT_IPC, 990000, GL_MM, false
        )

        createDocType(
            "Bank Statement", getElementTranslation("C_BankStatemet_ID", true),
            MDocType.DOCBASETYPE_BankStatement, null, 0, 0, 700000, GL_CASH, false
        )
        createDocType(
            "Cash Journal", getElementTranslation("C_Cash_ID", true),
            MDocType.DOCBASETYPE_CashJournal, null, 0, 0, 750000, GL_CASH, false
        )

        createDocType(
            "Material Movement", getElementTranslation("M_Movement_ID", false),
            MDocType.DOCBASETYPE_MaterialMovement, null, 0, 0, 610000, GL_MM, false
        )
        createDocType(
            "Physical Inventory",
            getElementTranslation("M_Inventory_ID", false),
            MDocType.DOCBASETYPE_MaterialPhysicalInventory,
            MDocType.DOCSUBTYPEINV_PhysicalInventory,
            0,
            0,
            620000,
            GL_MM,
            false
        )
        createDocType(
            "Material Production", getElementTranslation("M_Production_ID", false),
            MDocType.DOCBASETYPE_MaterialProduction, null, 0, 0, 630000, GL_MM, false
        )
        createDocType(
            "Project Issue", getElementTranslation("C_ProjectIssue_ID", false),
            MDocType.DOCBASETYPE_ProjectIssue, null, 0, 0, 640000, GL_MM, false
        )
        createDocType(
            "Internal Use Inventory",
            "Internal Use Inventory",
            MDocType.DOCBASETYPE_MaterialPhysicalInventory,
            MDocType.DOCSUBTYPEINV_InternalUseInventory,
            0,
            0,
            650000,
            GL_MM,
            false
        )
        createDocType(
            "Cost Adjustment",
            "Cost Adjustment",
            MDocType.DOCBASETYPE_MaterialPhysicalInventory,
            MDocType.DOCSUBTYPEINV_CostAdjustment,
            0,
            0,
            660000,
            GL_MM,
            false
        )

        //  Order Entry
        createDocType(
            "Binding offer", "Quotation",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_Quotation,
            0, 0, 10000, GL_None, false
        )
        createDocType(
            "Non binding offer", "Proposal",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_Proposal,
            0, 0, 20000, GL_None, false
        )
        createDocType(
            "Prepay Order", "Prepay Order",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_PrepayOrder,
            DT_S, DT_I, 30000, GL_None, false
        )
        createDocType(
            "Customer Return Material", "Customer Return Material Authorization",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_ReturnMaterial,
            DT_RM, DT_IC, 30000, GL_None, false
        )
        createDocType(
            "Standard Order", "Order Confirmation",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_StandardOrder,
            DT_S, DT_I, 50000, GL_None, false
        )
        createDocType(
            "Credit Order", "Order Confirmation",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_OnCreditOrder,
            DT_SI, DT_I, 60000, GL_None, false
        ) //  RE
        createDocType(
            "Warehouse Order", "Order Confirmation",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_WarehouseOrder,
            DT_S, DT_I, 70000, GL_None, false
        ) //  LS

        // Manufacturing Document
        createDocType(
            "Manufacturing Order", "Manufacturing Order",
            MDocType.DOCBASETYPE_ManufacturingOrder, null,
            0, 0, 80000, GL_Manufacturing, false
        )
        createDocType(
            "Manufacturing Cost Collector", "Cost Collector",
            MDocType.DOCBASETYPE_ManufacturingCostCollector, null,
            0, 0, 81000, GL_Manufacturing, false
        )
        createDocType(
            "Maintenance Order", "Maintenance Order",
            MDocType.DOCBASETYPE_MaintenanceOrder, null,
            0, 0, 86000, GL_Manufacturing, false
        )
        createDocType(
            "Quality Order", "Quality Order",
            MDocType.DOCBASETYPE_QualityOrder, null,
            0, 0, 87000, GL_Manufacturing, false
        )
        createDocType(
            "Distribution Order", "Distribution Order",
            MDocType.DOCBASETYPE_DistributionOrder, null,
            0, 0, 88000, GL_Distribution, false
        )
        // Payroll
        createDocType(
            "Payroll", "Payroll",
            MDocType.DOCBASETYPE_Payroll, null,
            0, 0, 90000, GL_Payroll, false
        )

        val DT = createDocType(
            "POS Order", "Order Confirmation",
            MDocType.DOCBASETYPE_SalesOrder, MDocType.DOCSUBTYPESO_POSOrder,
            DT_SI, DT_II, 80000, GL_None, false
        ) // Bar
        // 	POS As Default for window SO
        createPreference("C_DocTypeTarget_ID", DT.toString(), 143)

        //  Update ClientInfo
        sqlCmd = StringBuffer("UPDATE AD_ClientInfo SET ")
        sqlCmd.append("C_AcctSchema1_ID=").append(m_as!!.accountingSchemaId)
            .append(", C_Calendar_ID=").append(m_calendar!!.calendarId)
            .append(" WHERE AD_Client_ID=").append(m_client.clientId)
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1) {
            val err = "ClientInfo not updated"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }

        // 	Validate Completeness
        val processInfo = ProcessInfo("Document Type Verify", 0)
        processInfo.setADClientID(aD_Client_ID)
        processInfo.userId = aD_User_ID
        processInfo.parameter = arrayOfNulls<ProcessInfoParameter>(0)
        if (!ProcessUtil.startJavaProcess(processInfo, null, DocumentTypeVerify())) {
            val err = "Document type verification failed. Message=" + processInfo.summary!!
            log.log(Level.SEVERE, err)
            m_info.append(err)
            throw Error(err)
        }
        //
        log.info("fini")
        return true
    } //  createAccounting

    @Throws(Exception::class)
    private fun createAccountingRecord(tableName: String) {
        val table = getTable(tableName)
        val acct: PO = table.getPO(-1) ?: throw AdempiereSystemError("Unable to create new entry for $tableName") // Note this should create a new Acct; ugly hack, because we return null for 0

        val cols = table.getColumns(false)
        for (c in cols) {
            if (!c.isActive())
                continue
            val columnName = c.columnName
            if (c.isStandardColumn) {
            } else if (DisplayType.Account == c.referenceId) {
                acct.setValue(columnName, getAcct(columnName))
                if (log.isLoggable(Level.INFO)) log.info("Account: $columnName")
            } else if (DisplayType.YesNo == c.referenceId) {
                acct.setValue(columnName, java.lang.Boolean.TRUE)
                if (log.isLoggable(Level.INFO)) log.info("YesNo: " + c.columnName)
            }
        }
        acct.setClientId(m_client.clientId)
        acct.setValue(AccountingSchema.COLUMNNAME_C_AcctSchema_ID, m_as!!.accountingSchemaId)
        //
        if (!acct.save()) {
            throw AdempiereUserError(CLogger.retrieveErrorString(table.name + " not created"))
        }
    }

    /**
     * Get Account ID for key
     * @param key key
     * @return C_ValidCombination_ID
     * @throws AdempiereUserError
     */
    @Throws(AdempiereUserError::class)
    private fun getAcct(key: String): Int? {
        log.fine(key)
        //  Element
        val C_ElementValue_ID = m_nap!!.getElementValueId(key.toUpperCase())
        if (C_ElementValue_ID == 0) {
            throw AdempiereUserError("Account not defined: $key")
        }

        val vc = MAccount.getDefault(m_as, true) // 	optional null
        vc.setOrgId(0)
        vc.accountId = C_ElementValue_ID
        if (!vc.save()) {
            throw AdempiereUserError("Not Saved - Key=$key, C_ElementValue_ID=$C_ElementValue_ID")
        }
        val C_ValidCombination_ID = vc.validAccountCombinationId
        if (C_ValidCombination_ID == 0) {
            throw AdempiereUserError("No account - Key=$key, C_ElementValue_ID=$C_ElementValue_ID")
        }
        return C_ValidCombination_ID
    } //  getAcct

    /**
     * Create GL Category
     * @param Name name
     * @param CategoryType category type MGLCategory.CATEGORYTYPE_*
     * @param isDefault is default value
     * @return GL_Category_ID
     */
    private fun createGLCategory(Name: String, CategoryType: String, isDefault: Boolean): Int {
        val cat = MGLCategory(0)
        cat.name = Name
        cat.categoryType = CategoryType
        cat.setIsDefault(isDefault)
        if (!cat.save()) {
            log.log(Level.SEVERE, "GL Category NOT created - $Name")
            return 0
        }
        //
        return cat.glCategoryId
    } //  createGLCategory

    /**
     * Create Document Types with Sequence
     * @param Name name
     * @param PrintName print name
     * @param DocBaseType document base type
     * @param DocSubTypeSO sales order sub type
     * @param C_DocTypeShipment_ID shipment doc
     * @param C_DocTypeInvoice_ID invoice doc
     * @param StartNo start doc no
     * @param GL_Category_ID gl category
     * @param isReturnTrx is return trx
     * @return C_DocType_ID doc type or 0 for error
     */
    private fun createDocType(
        Name: String,
        PrintName: String?,
        DocBaseType: String,
        DocSubTypeSO: String?,
        C_DocTypeShipment_ID: Int,
        C_DocTypeInvoice_ID: Int,
        StartNo: Int,
        GL_Category_ID: Int,
        isReturnTrx: Boolean
    ): Int {
        var sequence: MSequence? = null
        if (StartNo != 0) {
            sequence = MSequence(aD_Client_ID, Name, StartNo)
            if (!sequence.save()) {
                log.log(Level.SEVERE, "Sequence NOT created - $Name")
                return 0
            }
        }

        val dt = MDocType(DocBaseType, Name)
        if (PrintName != null && PrintName.length > 0)
            dt.printName = PrintName // 	Defaults to Name
        if (DocSubTypeSO != null) {
            if (MDocType.DOCBASETYPE_MaterialPhysicalInventory == DocBaseType) {
                dt.docSubTypeInv = DocSubTypeSO
            } else {
                dt.docSubTypeSO = DocSubTypeSO
            }
        }
        if (C_DocTypeShipment_ID != 0)
            dt.docTypeShipmentId = C_DocTypeShipment_ID
        if (C_DocTypeInvoice_ID != 0)
            dt.docTypeInvoiceId = C_DocTypeInvoice_ID
        if (GL_Category_ID != 0)
            dt.glCategoryId = GL_Category_ID
        if (sequence == null)
            dt.setIsDocNoControlled(false)
        else {
            dt.setIsDocNoControlled(true)
            dt.docNoSequenceId = sequence.sequenceId
        }
        dt.setIsSOTrx()
        if (isReturnTrx)
            dt.setIsSOTrx(!dt.isSOTrx)
        if (!dt.save()) {
            log.log(Level.SEVERE, "DocType NOT created - $Name")
            return 0
        }
        //
        return dt.docTypeId
    } //  createDocType

    /**************************************************************************
     * Create Default main entities.
     * - Dimensions & BPGroup, Prod Category)
     * - Location, Locator, Warehouse
     * - PriceList
     * - Cashbook, PaymentTerm
     * @param C_Country_ID country
     * @param City city
     * @param C_Region_ID region
     * @param C_Currency_ID currency
     * @return true if created
     */
    fun createEntities(
        C_Country_ID: Int,
        City: String,
        C_Region_ID: Int,
        C_Currency_ID: Int,
        postal: String,
        address1: String
    ): Boolean {
        if (m_as == null) {
            log.severe("No AcctountingSChema")
            throw Error("No AcctountingSChema")
        }
        if (log.isLoggable(Level.INFO))
            log.info(
                "C_Country_ID=" + C_Country_ID +
                        ", City=" + City + ", C_Region_ID=" + C_Region_ID
            )
        m_info.append("\n----\n")
        //
        val defaultName = translate(m_lang, "Standard")
        val defaultEntry = "'$defaultName',"
        var sqlCmd: StringBuffer?
        var no: Int

        // 	Create Marketing Channel/Campaign
        val C_Channel_ID = getNextID(aD_Client_ID, "C_Channel")
        sqlCmd = StringBuffer("INSERT INTO C_Channel ")
        sqlCmd.append("(C_Channel_ID,Name,")
        sqlCmd.append(m_stdColumns).append(",C_Channel_UU) VALUES (")
        sqlCmd.append(C_Channel_ID).append(",").append(defaultEntry)
        sqlCmd.append(m_stdValues).append(",").append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "Channel NOT inserted")

        val C_Campaign_ID = getNextID(aD_Client_ID, "C_Campaign")
        sqlCmd = StringBuffer("INSERT INTO C_Campaign ")
        sqlCmd.append("(C_Campaign_ID,C_Channel_ID,").append(m_stdColumns).append(",")
        sqlCmd.append(" Value,Name,Costs,C_Campaign_UU) VALUES (")
        sqlCmd.append(C_Campaign_ID).append(",").append(C_Channel_ID).append(",").append(m_stdValues).append(",")
        sqlCmd.append(defaultEntry).append(defaultEntry).append("0").append(",")
            .append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no == 1)
            m_info.append(translate(m_lang, "C_Campaign_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "Campaign NOT inserted")
        if (m_hasMCampaign) {
            //  Default
            sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET ")
            sqlCmd.append("C_Campaign_ID=").append(C_Campaign_ID)
            sqlCmd.append(" WHERE C_AcctSchema_ID=").append(m_as!!.accountingSchemaId)
            sqlCmd.append(" AND ElementType='MC'")
            no = executeUpdateEx(sqlCmd.toString())
            if (no != 1)
                log.log(Level.SEVERE, "AcctSchema Element Campaign NOT updated")
        }
        // Campaign Translation
        sqlCmd =
            StringBuffer("INSERT INTO C_Campaign_Trl (AD_Language,C_Campaign_ID, Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,C_Campaign_Trl_UU)")
        sqlCmd.append(" SELECT l.AD_Language,t.C_Campaign_ID, t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy, generate_uuid() FROM AD_Language l, C_Campaign t")
        sqlCmd.append(" WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.C_Campaign_ID=")
            .append(C_Campaign_ID)
        sqlCmd.append(" AND NOT EXISTS (SELECT * FROM C_Campaign_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.C_Campaign_ID=t.C_Campaign_ID)")
        no = executeUpdateEx(sqlCmd.toString())
        if (no < 0)
            log.log(Level.SEVERE, "Campaign Translation NOT inserted")

        // 	Create Sales Region
        val C_SalesRegion_ID = getNextID(aD_Client_ID, "C_SalesRegion")
        sqlCmd = StringBuffer("INSERT INTO C_SalesRegion ")
        sqlCmd.append("(C_SalesRegion_ID,").append(m_stdColumns).append(",")
        sqlCmd.append(" Value,Name,IsSummary,C_SalesRegion_UU) VALUES (")
        sqlCmd.append(C_SalesRegion_ID).append(",").append(m_stdValues).append(", ")
        sqlCmd.append(defaultEntry).append(defaultEntry).append("'N'").append(",")
            .append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no == 1)
            m_info.append(translate(m_lang, "C_SalesRegion_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "SalesRegion NOT inserted")
        if (m_hasSRegion) {
            //  Default
            sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET ")
            sqlCmd.append("C_SalesRegion_ID=").append(C_SalesRegion_ID)
            sqlCmd.append(" WHERE C_AcctSchema_ID=").append(m_as!!.accountingSchemaId)
            sqlCmd.append(" AND ElementType='SR'")
            no = executeUpdateEx(sqlCmd.toString())
            if (no != 1)
                log.log(Level.SEVERE, "AcctSchema Element SalesRegion NOT updated")
        }
        // Sales Region Translation
        sqlCmd =
            StringBuffer("INSERT INTO C_SalesRegion_Trl (AD_Language,C_SalesRegion_ID, Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,C_SalesRegion_Trl_UU)")
        sqlCmd.append(" SELECT l.AD_Language,t.C_SalesRegion_ID, t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy, generate_uuid() FROM AD_Language l, C_SalesRegion t")
        sqlCmd.append(" WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.C_SalesRegion_ID=")
            .append(C_SalesRegion_ID)
        sqlCmd.append(" AND NOT EXISTS (SELECT * FROM C_SalesRegion_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.C_SalesRegion_ID=t.C_SalesRegion_ID)")
        no = executeUpdateEx(sqlCmd.toString())
        if (no < 0)
            log.log(Level.SEVERE, "Sales Region Translation NOT inserted")

        // 	Create Activity
        val C_Activity_ID = getNextID(aD_Client_ID, "C_Activity")
        sqlCmd = StringBuffer("INSERT INTO C_Activity ")
        sqlCmd.append("(C_Activity_ID,").append(m_stdColumns).append(",")
        sqlCmd.append(" Value,Name,IsSummary,C_Activity_UU) VALUES (")
        sqlCmd.append(C_Activity_ID).append(",").append(m_stdValues).append(", ")
        sqlCmd.append(defaultEntry).append(defaultEntry).append("'N'").append(",")
            .append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no == 1)
            m_info.append(translate(m_lang, "C_Activity_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "Activity NOT inserted")
        if (m_hasActivity) {
            //  Default
            sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET ")
            sqlCmd.append("C_Activity_ID=").append(C_Activity_ID)
            sqlCmd.append(" WHERE C_AcctSchema_ID=").append(m_as!!.accountingSchemaId)
            sqlCmd.append(" AND ElementType='AY'")
            no = executeUpdateEx(sqlCmd.toString())
            if (no != 1)
                log.log(Level.SEVERE, "AcctSchema Element Activity NOT updated")
        }
        // Activity Translation
        sqlCmd =
            StringBuffer("INSERT INTO C_Activity_Trl (AD_Language,C_Activity_ID, Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,C_Activity_Trl_UU)")
        sqlCmd.append(" SELECT l.AD_Language,t.C_Activity_ID, t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy, generate_uuid() FROM AD_Language l, C_Activity t")
        sqlCmd.append(" WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.C_Activity_ID=")
            .append(C_Activity_ID)
        sqlCmd.append(" AND NOT EXISTS (SELECT * FROM C_Activity_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.C_Activity_ID=t.C_Activity_ID)")
        no = executeUpdateEx(sqlCmd.toString())
        if (no < 0)
            log.log(Level.SEVERE, "Activity Translation NOT inserted")

        /**
         * Business Partner
         */
        //  Create BP Group
        val bpg = MBPGroup(0)
        bpg.setValue(defaultName)
        bpg.name = defaultName
        bpg.setIsDefault(true)
        if (bpg.save())
            m_info.append(translate(m_lang, "C_BP_Group_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "BP Group NOT inserted")

        // 	Create BPartner
        val bp = businessPartnerService.getEmpty()
        bp.searchKey = defaultName
        bp.name = defaultName
        bp.setBPGroup(bpg)
        if (bp.save())
            m_info.append(translate(m_lang, "C_BPartner_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "BPartner NOT inserted")
        //  Location for Standard BP
        val bpLoc = MLocation(C_Country_ID, C_Region_ID, City)
        bpLoc.saveEx()
        val bpl = MBPartnerLocation(bp)
        bpl.locationId = bpLoc.locationId
        if (!bpl.save())
            log.log(Level.SEVERE, "BP_Location (Standard) NOT inserted")
        //  Default
        sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET ")
        sqlCmd.append("C_BPartner_ID=").append(bp.businessPartnerId)
        sqlCmd.append(" WHERE C_AcctSchema_ID=").append(m_as!!.accountingSchemaId)
        sqlCmd.append(" AND ElementType='BP'")
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "AcctSchema Element BPartner NOT updated")
        createPreference("C_BPartner_ID", bp.businessPartnerId.toString(), 143)

        /**
         * Product
         */
        //  Create Product Category
        val pc = MProductCategory(0)
        pc.setSearchKey(defaultName)
        pc.name = defaultName
        pc.setIsDefault(true)
        if (pc.save())
            m_info.append(translate(m_lang, "M_Product_Category_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "Product Category NOT inserted")

        //  UOM (EA)
        val C_UOM_ID = 100

        //  TaxCategory
        val C_TaxCategory_ID = getNextID(aD_Client_ID, "C_TaxCategory")
        sqlCmd = StringBuffer("INSERT INTO C_TaxCategory ")
        sqlCmd.append("(C_TaxCategory_ID,").append(m_stdColumns).append(",")
        sqlCmd.append(" Name,IsDefault,C_TaxCategory_UU) VALUES (")
        sqlCmd.append(C_TaxCategory_ID).append(",").append(m_stdValues).append(", ")
        if (C_Country_ID == SystemIDs.COUNTRY_US)
        // US
            sqlCmd.append("'Sales Tax','Y',")
        else
            sqlCmd.append(defaultEntry).append("'Y',")
        sqlCmd.append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "TaxCategory NOT inserted")

        //  TaxCategory translation
        sqlCmd =
            StringBuffer("INSERT INTO C_TaxCategory_Trl (AD_Language,C_TaxCategory_ID, Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,C_TaxCategory_Trl_UU)")
        sqlCmd.append(" SELECT l.AD_Language,t.C_TaxCategory_ID, t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy, generate_uuid() FROM AD_Language l, C_TaxCategory t")
        sqlCmd.append(" WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.C_TaxCategory_ID=")
            .append(C_TaxCategory_ID)
        sqlCmd.append(" AND NOT EXISTS (SELECT * FROM C_TaxCategory_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.C_TaxCategory_ID=t.C_TaxCategory_ID)")
        no = executeUpdateEx(sqlCmd.toString())
        if (no < 0)
            log.log(Level.SEVERE, "TaxCategory Translation NOT inserted")

        //  Tax - Zero Rate
        val tax = MTax("Standard", Env.ZERO, C_TaxCategory_ID)
        tax.setIsDefault(true)
        if (tax.save())
            m_info.append(translate(m_lang, "C_Tax_ID"))
                .append("=").append(tax.name).append("\n")
        else
            log.log(Level.SEVERE, "Tax NOT inserted")

        // 	Create Product
        val product = MProduct(0)
        product.searchKey = defaultName
        product.name = defaultName
        product.uomId = C_UOM_ID
        product.productCategoryId = pc.productCategoryId
        product.taxCategoryId = C_TaxCategory_ID
        if (product.save())
            m_info.append(translate(m_lang, "M_Product_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "Product NOT inserted")
        //  Default
        sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET ")
        sqlCmd.append("M_Product_ID=").append(product.productId)
        sqlCmd.append(" WHERE C_AcctSchema_ID=").append(m_as!!.accountingSchemaId)
        sqlCmd.append(" AND ElementType='PR'")
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "AcctSchema Element Product NOT updated")

        /**
         * Location, Warehouse, Locator
         */
        //  Location (Company)
        val loc = MLocation(C_Country_ID, C_Region_ID, City)
        loc.address1 = address1
        loc.postal = postal
        loc.saveEx()
        sqlCmd = StringBuffer("UPDATE AD_OrgInfo SET C_Location_ID=")
        sqlCmd.append(loc.locationId).append(" WHERE AD_Org_ID=").append(aD_Org_ID)
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "Location NOT inserted")
        createPreference("C_Country_ID", C_Country_ID.toString(), 0)

        //  Default Warehouse
        val locwh = MLocation(C_Country_ID, C_Region_ID, City)
        locwh.address1 = address1
        locwh.postal = postal
        locwh.saveEx()
        val wh = MWarehouse(0)
        wh.searchKey = defaultName
        wh.name = defaultName
        wh.setLocationId(locwh.locationId)
        if (!wh.save())
            log.log(Level.SEVERE, "Warehouse NOT inserted")

        //   Locator
        val locator = MLocator(wh, defaultName)
        locator.setIsDefault(true)
        if (!locator.save())
            log.log(Level.SEVERE, "Locator NOT inserted")

        //  Update ClientInfo
        sqlCmd = StringBuffer("UPDATE AD_ClientInfo SET ")
        sqlCmd.append("C_BPartnerCashTrx_ID=").append(bp.businessPartnerId)
        sqlCmd.append(",M_ProductFreight_ID=").append(product.productId)
        sqlCmd.append(" WHERE AD_Client_ID=").append(aD_Client_ID)
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1) {
            val err = "ClientInfo not updated"
            log.log(Level.SEVERE, err)
            m_info.append(err)
            return false
        }

        /**
         * Other
         */
        //  PriceList
        val pl = MPriceList(0)
        pl.name = defaultName
        pl.currencyId = C_Currency_ID
        pl.setIsDefault(true)
        if (!pl.save())
            log.log(Level.SEVERE, "PriceList NOT inserted")
        //  Price List
        val ds = MDiscountSchema(0)
        ds.name = defaultName
        ds.discountType = MDiscountSchema.DISCOUNTTYPE_Pricelist
        if (!ds.save())
            log.log(Level.SEVERE, "DiscountSchema NOT inserted")
        //  PriceList Version
        val plv = MPriceListVersion(pl)
        plv.setName()
        plv.setDiscountSchemaId(ds.discountSchemaId)
        if (!plv.save())
            log.log(Level.SEVERE, "PriceList_Version NOT inserted")
        //  ProductPrice
        val pp = MProductPrice(
            plv, product.productId,
            Env.ONE, Env.ONE, Env.ONE
        )
        if (!pp.save())
            log.log(Level.SEVERE, "ProductPrice NOT inserted")

        // 	Create Sales Rep for Client-User
        val bpCU = businessPartnerService.getEmpty()
        bpCU.searchKey = AD_User_U_Name!!
        bpCU.name = AD_User_U_Name!!
        bpCU.setBPGroup(bpg)
        bpCU.setIsEmployee(true)
        bpCU.setIsSalesRep(true)
        if (bpCU.save())
            m_info.append(translate(m_lang, "SalesRep_ID")).append("=").append(AD_User_U_Name).append("\n")
        else
            log.log(Level.SEVERE, "SalesRep (User) NOT inserted")
        //  Location for Client-User
        val bpLocCU = MLocation(C_Country_ID, C_Region_ID, City)
        bpLocCU.saveEx()
        val bplCU = MBPartnerLocation(bpCU)
        bplCU.locationId = bpLocCU.locationId
        if (!bplCU.save())
            log.log(Level.SEVERE, "BP_Location (User) NOT inserted")
        //  Update User
        sqlCmd = StringBuffer("UPDATE AD_User SET C_BPartner_ID=")
        sqlCmd.append(bpCU.businessPartnerId).append(" WHERE AD_User_ID=").append(AD_User_U_ID)
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "User of SalesRep (User) NOT updated")

        // 	Create Sales Rep for Client-Admin
        val bpCA = businessPartnerService.getEmpty()
        bpCA.searchKey = AD_User_Name!!
        bpCA.name = AD_User_Name!!
        bpCA.setBPGroup(bpg)
        bpCA.setIsEmployee(true)
        bpCA.setIsSalesRep(true)
        if (bpCA.save())
            m_info.append(translate(m_lang, "SalesRep_ID")).append("=").append(AD_User_Name).append("\n")
        else
            log.log(Level.SEVERE, "SalesRep (Admin) NOT inserted")
        //  Location for Client-Admin
        val bpLocCA = MLocation(C_Country_ID, C_Region_ID, City)
        bpLocCA.saveEx()
        val bplCA = MBPartnerLocation(bpCA)
        bplCA.locationId = bpLocCA.locationId
        if (!bplCA.save())
            log.log(Level.SEVERE, "BP_Location (Admin) NOT inserted")
        //  Update User
        sqlCmd = StringBuffer("UPDATE AD_User SET C_BPartner_ID=")
        sqlCmd.append(bpCA.businessPartnerId).append(" WHERE AD_User_ID=").append(aD_User_ID)
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "User of SalesRep (Admin) NOT updated")

        //  Payment Term
        val C_PaymentTerm_ID = getNextID(aD_Client_ID, "C_PaymentTerm")
        sqlCmd = StringBuffer("INSERT INTO C_PaymentTerm ")
        sqlCmd.append("(C_PaymentTerm_ID,").append(m_stdColumns).append(",")
        sqlCmd.append("Value,Name,NetDays,GraceDays,DiscountDays,Discount,DiscountDays2,Discount2,IsDefault,C_PaymentTerm_UU) VALUES (")
        sqlCmd.append(C_PaymentTerm_ID).append(",").append(m_stdValues).append(",")
        sqlCmd.append("'Immediate','Immediate',0,0,0,0,0,0,'Y'").append(",")
            .append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "PaymentTerm NOT inserted")
        // Payment Term Translation
        sqlCmd =
            StringBuffer("INSERT INTO C_PaymentTerm_Trl (AD_Language,C_PaymentTerm_ID, Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy,C_PaymentTerm_Trl_UU)")
        sqlCmd.append(" SELECT l.AD_Language,t.C_PaymentTerm_ID, t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy, generate_uuid() FROM AD_Language l, C_PaymentTerm t")
        sqlCmd.append(" WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.C_PaymentTerm_ID=")
            .append(C_PaymentTerm_ID)
        sqlCmd.append(" AND NOT EXISTS (SELECT * FROM C_PaymentTerm_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.C_PaymentTerm_ID=t.C_PaymentTerm_ID)")
        no = executeUpdateEx(sqlCmd.toString())
        if (no < 0)
            log.log(Level.SEVERE, "Payment Term Translation NOT inserted")

        //  Project Cycle
        C_Cycle_ID = getNextID(aD_Client_ID, "C_Cycle")
        sqlCmd = StringBuffer("INSERT INTO C_Cycle ")
        sqlCmd.append("(C_Cycle_ID,").append(m_stdColumns).append(",")
        sqlCmd.append(" Name,C_Currency_ID,C_Cycle_UU) VALUES (")
        sqlCmd.append(C_Cycle_ID).append(",").append(m_stdValues).append(", ")
        sqlCmd.append(defaultEntry).append(C_Currency_ID).append(",").append(convertString(UUID.randomUUID().toString()))
            .append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "Cycle NOT inserted")

        /**
         * Organization level data	===========================================
         */

        // 	Create Default Project
        val C_Project_ID = getNextID(aD_Client_ID, "C_Project")
        sqlCmd = StringBuffer("INSERT INTO C_Project ")
        sqlCmd.append("(C_Project_ID,").append(m_stdColumns).append(",")
        sqlCmd.append(" Value,Name,C_Currency_ID,IsSummary,C_Project_UU) VALUES (")
        sqlCmd.append(C_Project_ID).append(",").append(m_stdValuesOrg).append(", ")
        sqlCmd.append(defaultEntry).append(defaultEntry).append(C_Currency_ID).append(",'N'").append(",")
            .append(convertString(UUID.randomUUID().toString())).append(")")
        no = executeUpdateEx(sqlCmd.toString())
        if (no == 1)
            m_info.append(translate(m_lang, "C_Project_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "Project NOT inserted")
        //  Default Project
        if (m_hasProject) {
            sqlCmd = StringBuffer("UPDATE C_AcctSchema_Element SET ")
            sqlCmd.append("C_Project_ID=").append(C_Project_ID)
            sqlCmd.append(" WHERE C_AcctSchema_ID=").append(m_as!!.accountingSchemaId)
            sqlCmd.append(" AND ElementType='PJ'")
            no = executeUpdateEx(sqlCmd.toString())
            if (no != 1)
                log.log(Level.SEVERE, "AcctSchema Element Project NOT updated")
        }

        //  CashBook
        val cb = MCashBook(0)
        cb.name = defaultName
        cb.currencyId = C_Currency_ID
        if (cb.save())
            m_info.append(translate(m_lang, "C_CashBook_ID")).append("=").append(defaultName).append("\n")
        else
            log.log(Level.SEVERE, "CashBook NOT inserted")
        //
        log.info("finish")
        return true
    } //  createEntities

    /**
     * Create Preference
     * @param Attribute attribute
     * @param Value value
     * @param AD_Window_ID window
     */
    private fun createPreference(Attribute: String, Value: String, AD_Window_ID: Int) {
        val AD_Preference_ID = getNextID(aD_Client_ID, "AD_Preference")
        val sqlCmd = StringBuilder("INSERT INTO AD_Preference ")
        sqlCmd.append("(AD_Preference_ID,").append("AD_Preference_UU,").append(m_stdColumns).append(",")
        sqlCmd.append("Attribute,Value,AD_Window_ID) VALUES (")
        sqlCmd.append(AD_Preference_ID).append(",").append(convertString(UUID.randomUUID().toString())).append(",")
            .append(m_stdValues).append(",")
        sqlCmd.append("'").append(Attribute).append("','").append(Value).append("',")
        if (AD_Window_ID == 0)
            sqlCmd.append("NULL)")
        else
            sqlCmd.append(AD_Window_ID).append(")")
        val no = executeUpdateEx(sqlCmd.toString())
        if (no != 1)
            log.log(Level.SEVERE, "Preference NOT inserted - $Attribute")
    } //  createPreference

    /**************************************************************************
     * Get Next ID
     * @param AD_Client_ID client
     * @param TableName table name
     * @return id
     */
    private fun getNextID(AD_Client_ID: Int, TableName: String): Int {
        // 	TODO: Exception
        return MSequence.getNextID(AD_Client_ID, TableName)
    } // 	getNextID
} //  MSetup
