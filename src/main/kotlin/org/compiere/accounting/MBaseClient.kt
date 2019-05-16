package org.compiere.accounting

import kotliquery.Row
import org.compiere.orm.MTree_Base
import org.compiere.orm.MTree_Base.Companion.TREETYPE_Activity
import org.compiere.orm.MTree_Base.Companion.TREETYPE_BPartner
import org.compiere.orm.MTree_Base.Companion.TREETYPE_Campaign
import org.compiere.orm.MTree_Base.Companion.TREETYPE_CustomTable
import org.compiere.orm.MTree_Base.Companion.TREETYPE_ElementValue
import org.compiere.orm.MTree_Base.Companion.TREETYPE_Menu
import org.compiere.orm.MTree_Base.Companion.TREETYPE_Organization
import org.compiere.orm.MTree_Base.Companion.TREETYPE_Product
import org.compiere.orm.MTree_Base.Companion.TREETYPE_Project
import org.compiere.orm.MTree_Base.Companion.TREETYPE_SalesRegion
import org.compiere.orm.MTree_Base.Companion.TREETYPE_User1
import org.compiere.orm.MTree_Base.Companion.TREETYPE_User2
import org.compiere.orm.MTree_Base.Companion.TREETYPE_User3
import org.compiere.orm.MTree_Base.Companion.TREETYPE_User4
import org.idempiere.common.util.Env
import software.hsharp.core.util.DB
import software.hsharp.core.util.convertString
import software.hsharp.core.util.queryOf
import java.util.logging.Level

/**
 * Basic client class that allows setting up a new client info
 */
open class MBaseClient : org.compiere.orm.MClient {
    constructor(Id: Int) : super(Id)
    constructor(rs: Row) : super(rs)
    constructor(clientId: Int, createNew: Boolean) : super(clientId, createNew)

    /** Client Info Setup Tree for Account  */
    private var m_AD_Tree_Account_ID: Int = 0

    /**
     * Setup a new [MClientInfo]
     */
    fun setupClientInfo(language: String): Boolean {
        //  Tree IDs
        var AD_Tree_Org_ID = 0
        var AD_Tree_BPartner_ID = 0
        var AD_Tree_Project_ID = 0
        var AD_Tree_SalesRegion_ID = 0
        var AD_Tree_Product_ID = 0
        var AD_Tree_Campaign_ID = 0
        var AD_Tree_Activity_ID = 0

        fun mapRow(row: Row): Boolean {
            val value = row.string(1)
            val name = StringBuilder().append(name).append(" ").append(row.string(2))
            val success =
            //
                if (value == TREETYPE_Organization) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_Org_ID = tree.treeId
                    result
                } else if (value == TREETYPE_BPartner) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_BPartner_ID = tree.treeId
                    result
                } else if (value == TREETYPE_Project) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_Project_ID = tree.treeId
                    result
                } else if (value == TREETYPE_SalesRegion) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_SalesRegion_ID = tree.treeId
                    result
                } else if (value == TREETYPE_Product) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_Product_ID = tree.treeId
                    result
                } else if (value == TREETYPE_ElementValue) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    m_AD_Tree_Account_ID = tree.treeId
                    result
                } else if (value == TREETYPE_Campaign) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_Campaign_ID = tree.treeId
                    result
                } else if (value == TREETYPE_Activity) {
                    val tree = MTree_Base(this, name.toString(), value)
                    val result = tree.save()
                    AD_Tree_Activity_ID = tree.treeId
                    result
                } else if (value == TREETYPE_Menu || // 	No Menu

                    value == TREETYPE_CustomTable || // 	No Custom Table

                    value == TREETYPE_User1 || // 	No custom user trees

                    value == TREETYPE_User2 ||
                    value == TREETYPE_User3 ||
                    value == TREETYPE_User4
                )
                    true
                else
                // 	PC (Product Category), BB (BOM)
                {
                    val tree = MTree_Base(this, name.toString(), value)
                    tree.save()
                }

            if (!success) {
                log.log(Level.SEVERE, "Tree NOT created: $name")
            }

            return success
        }

        // 	Create Trees
        val sql: StringBuilder? =
            if (Env.isBaseLanguage(language))
            // 	Get TreeTypes & Name
                StringBuilder(
                    "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=120 AND IsActive='Y'"
                )
            else
                StringBuilder("SELECT l.Value, t.Name FROM AD_Ref_List l, AD_Ref_List_Trl t ")
                    .append(
                        "WHERE l.AD_Reference_ID=120 AND l.AD_Ref_List_ID=t.AD_Ref_List_ID AND l.IsActive='Y'"
                    )
                    .append(" AND t.AD_Language=")
                    .append(convertString(language))

        val loadQuery = queryOf(sql.toString(), listOf()).map { mapRow(it) }.asList
        val success = DB.current.run(loadQuery).min() ?: false

        if (!success) return false

        // 	Create ClientInfo
        val clientInfo = MClientInfo(
            this,
            AD_Tree_Org_ID,
            AD_Tree_BPartner_ID,
            AD_Tree_Project_ID,
            AD_Tree_SalesRegion_ID,
            AD_Tree_Product_ID,
            AD_Tree_Campaign_ID,
            AD_Tree_Activity_ID
        )
        val result = clientInfo.save()
        return result
    }

    /**
     * Get AD_Tree_Account_ID created in setup client info
     *
     * @return Account Tree ID
     */
    val setup_AD_Tree_Account_ID get() = m_AD_Tree_Account_ID
}