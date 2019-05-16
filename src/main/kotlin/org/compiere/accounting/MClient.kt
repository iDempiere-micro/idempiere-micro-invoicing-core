package org.compiere.accounting

import kotliquery.Row
import org.compiere.model.AccountingSchema
import org.compiere.model.Client
import org.compiere.model.ClientInfoWithAccounting
import org.compiere.model.ClientWithAccounting
import org.compiere.orm.MSysConfig
import org.compiere.orm.Query
import org.idempiere.common.util.AdempiereSystemError
import org.idempiere.common.util.CCache
import org.idempiere.common.util.Env
import org.idempiere.common.util.Language
import org.idempiere.common.util.all
import org.idempiere.common.util.factory
import org.idempiere.common.util.loadUsing
import software.hsharp.core.util.Environment
import java.util.Locale

private fun loadAllClients() = Query<ClientWithAccounting>(Client.Table_Name, null).list()

private val clientFactory = factory(loadAllClients()) { MClient(it) }

/**
 * Get client by Id
 */
fun getClientWithAccounting(id: Int) = id loadUsing clientFactory

/**
 * Get current client
 */
fun getClientWithAccounting() = getClientWithAccounting(Environment.current.clientId)

/**
 * Get all clients
 */
fun getAllClientsWithAccounting() = clientFactory.all()

/**
 * Client Model
 *
 * @author Jorg Janke
 * @author Carlos Ruiz - globalqss integrate bug fix reported by Teo Sarca [ 1619085 ] Client setup
 * creates duplicate trees
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 *  * BF [ 1886480 ] Print Format Item Trl not updated even if not multilingual
 * @version $Id: MClient.java,v 1.2 2006/07/30 00:58:37 jjanke Exp $
 */
class MClient : MBaseClient, ClientWithAccounting {
    /**
     * Language
     */
    private var m_language: Language? = null

    /**
     * Get Client Info
     *
     * @return Client Info
     */
    override val info: ClientInfoWithAccounting
        get() {
            if (m_info == null || m_info !is ClientInfoWithAccounting) m_info = MClientInfo.get(clientId)
            return m_info as ClientInfoWithAccounting
        } // 	getMClientInfo

    /**
     * Get Language
     *
     * @return client language
     */
    // DAP Env.verifyLanguage(m_language);
    val language: Language?
        get() {
            if (m_language == null) {
                m_language = Language.getLanguage(adLanguage)
            }
            return m_language
        } // 	getLanguage

    /**
     * Get Locale
     *
     * @return locale
     */
    override val locale: Locale
        get() {
            val lang = language
            return if (lang != null) lang.locale else Locale.getDefault()
        } // 	getLocale

    /**
     * Get Primary Accounting Schema
     *
     * @return Acct Schema or null
     */
    override val acctSchema: AccountingSchema
        get() {
            if (m_info == null) m_info = MClientInfo.get(clientId)
            if (m_info != null) {
                val C_AcctSchema_ID = m_info!!.acctSchema1Id
                if (C_AcctSchema_ID != 0) return MAcctSchema.get(C_AcctSchema_ID)
            }
            throw AdempiereSystemError("Client $id does not have accounting schema")
        } // 	getMClientInfo

    /**
     * Get Default Accounting Currency
     *
     * @return currency or 0
     */
    override val currencyId: Int
        get() {
            if (m_info == null) info
            return if (m_info != null) info.currencyId else 0
        } // 	getCurrencyId

    constructor(ad_client_id: Int) : super(ad_client_id) {}

    constructor(AD_Client_ID: Int, createNew: Boolean) : super(AD_Client_ID, createNew) {}

    constructor(row: Row) : super(row) {} // 	MClient

    /**
     * String Representation
     *
     * @return info
     */
    override fun toString(): String {
        val sb = StringBuilder("MClient[").append(id).append("-").append(searchKey).append("]")
        return sb.toString()
    } // 	toString

    /**
     * Get AD_Language
     *
     * @return Language
     */
    override fun getADLanguage(): String {
        return super.getADLanguage() ?: return Language.getBaseLanguageCode()
    } // 	getADLanguage

    /**
     * Set AD_Language
     *
     * @param AD_Language new language
     */
    override fun setADLanguage(AD_Language: String) {
        m_language = null
        super.setADLanguage(AD_Language)
    } // 	setADLanguage

    /**
     * Save
     *
     * @return true if saved
     */
    override fun save(): Boolean {
        return if (id == 0 && !createNew) saveUpdate() else super.save()
    } // 	save

    override fun getRequestUser(): String {
        // IDEMPIERE-722
        if (clientId != 0 && isSendCredentialsSystem) {
            val sysclient = getClientWithAccounting(0)
            return sysclient.getRequestUser()
        }
        return super.getRequestUser()
    }

    override fun getRequestUserPW(): String {
        // IDEMPIERE-722
        if (clientId != 0 && isSendCredentialsSystem) {
            val sysclient = getClientWithAccounting(0)
            return sysclient.getRequestUserPW()
        }
        return super.getRequestUserPW()
    }

    override fun isSmtpAuthorization(): Boolean {
        // IDEMPIERE-722
        if (clientId != 0 && isSendCredentialsSystem) {
            val sysclient = getClientWithAccounting(0)
            return sysclient.isSmtpAuthorization()
        }
        return super.isSmtpAuthorization()
    }

    override fun getSMTPPort(): Int {
        // IDEMPIERE-722
        if (clientId != 0 && isSendCredentialsSystem) {
            val sysclient = getClientWithAccounting(0)
            return sysclient.getSMTPPort()
        }
        return super.getSMTPPort()
    }

    override fun isSecureSMTP(): Boolean {
        // IDEMPIERE-722
        if (clientId != 0 && isSendCredentialsSystem) {
            val sysclient = getClientWithAccounting(0)
            return sysclient.isSecureSMTP()
        }
        return super.isSecureSMTP()
    }

    /**
     * Get SMTP Host
     *
     * @return SMTP or loaclhost
     */
    override fun getSMTPHost(): String {
        var s: String?
        if (clientId != 0 && isSendCredentialsSystem) {
            val sysclient = getClientWithAccounting(0)
            s = sysclient.getSMTPHost()
        } else {
            s = super.getSMTPHost()
        }
        if (s == null) s = "localhost"
        return s
    } // 	getSMTPHost

    companion object {
        private val serialVersionUID = -4420908648355523008L
        // IDEMPIERE-722
        private val MAIL_SEND_CREDENTIALS_USER = "U"
        private val MAIL_SEND_CREDENTIALS_SYSTEM = "S"
        protected var s_cache = CCache<Int, Client>(Client.Table_Name, 120)

        /**
         * Get all clients
         *
         * @return clients
         */
        val all: Array<Client>
            get() = getAll("") // 	getAll

        /**
         * Get all clients
         *
         * @param orderBy by clause
         * @return clients
         */
        fun getAll(orderBy: String): Array<Client> {
            val list = Query<Client>(Client.Table_Name, null).setOrderBy(orderBy).list()
            for (client in list) {
                s_cache[client.clientId] = client
            }
            return list.toTypedArray()
        } // 	getAll

        // default
        val isClientAccounting: Boolean
            get() {
                val ca = MSysConfig.getConfigValue(
                    MSysConfig.CLIENT_ACCOUNTING,
                    CLIENT_ACCOUNTING_QUEUE,
                    Env.getClientId()
                )
                return ca.equals(
                    CLIENT_ACCOUNTING_IMMEDIATE,
                    ignoreCase = true
                ) || ca.equals(CLIENT_ACCOUNTING_QUEUE, ignoreCase = true)
            }

        // default
        val isSendCredentialsSystem: Boolean
            get() {
                val msc = MSysConfig.getConfigValue(
                    MSysConfig.MAIL_SEND_CREDENTIALS,
                    MAIL_SEND_CREDENTIALS_USER,
                    Env.getClientId()
                )
                return MAIL_SEND_CREDENTIALS_SYSTEM.equals(msc, ignoreCase = true)
            }

        /**
         * Get client
         *
         * @param AD_Client_ID id
         * @return client
         */
        @JvmOverloads
        operator fun get(AD_Client_ID: Int = Environment.current.clientId): MClient {
            var client: MClient? = s_cache[AD_Client_ID] as MClient
            if (client != null) return client
            client = MClient(AD_Client_ID)
            s_cache[AD_Client_ID] = client
            return client
        } // 	get
    }
}
