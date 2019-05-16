package org.compiere.accounting

import kotliquery.Row
import org.compiere.model.AccountingSchema
import org.compiere.model.IDoc
import org.idempiere.common.exceptions.AdempiereException
import org.idempiere.common.exceptions.DBException
import org.idempiere.common.util.CLogger
import software.hsharp.core.orm.getTable
import software.hsharp.core.util.DB
import software.hsharp.core.util.executeUpdate
import software.hsharp.core.util.prepareStatement
import software.hsharp.core.util.queryOf
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * This class contains methods to manage the posting of financial document. Most of the code is adapted from the legacy code in Doc.java
 * @author Jorg Janke
 * @author hengsin
 */
object DocManager {

    private val s_log = CLogger.getCLogger(DocManager::class.java)

    /** AD_Table_ID's of documents           */
    private var documentsTableID: IntArray? = null

    /** Table Names of documents           */
    private var documentsTableName: Array<String>? = null

    /*
	 * Array of tables with Post column
	 */
    fun getDocumentsTableID(): IntArray {
        fillDocumentsTableArrays()
        return documentsTableID ?: throw AdempiereException("loading documents failed")
    }

    fun getDocumentsTableName(): Array<String> {
        fillDocumentsTableArrays()
        return documentsTableName ?: throw AdempiereException("loading documents failed")
    }

    @Synchronized
    private fun fillDocumentsTableArrays() {
        if (documentsTableID == null) {
            val sql = "SELECT t.AD_Table_ID, t.TableName " +
                    "FROM AD_Table t, AD_Column c " +
                    "WHERE t.AD_Table_ID=c.AD_Table_ID AND " +
                    "c.ColumnName='Posted' AND " +
                    "IsView='N' " +
                    "ORDER BY t.AD_Table_ID"
            val tableIDs: MutableList<Int> = mutableListOf()
            val tableNames: MutableList<String> = mutableListOf()
            val preparedStatement: PreparedStatement?
            val resultSet: ResultSet?
            try {
                preparedStatement = prepareStatement(sql)
                resultSet = preparedStatement!!.executeQuery()
                while (resultSet!!.next()) {
                    tableIDs.add(resultSet.getInt(1))
                    tableNames.add(resultSet.getString(2))
                }
            } catch (e: SQLException) {
                throw DBException(e)
            } finally {
            }
            // 	Convert to array
            documentsTableID = tableIDs.toIntArray()
            documentsTableName = tableNames.toTypedArray()
        }
    }

    /**
     * Create Posting document
     * @param as accounting schema
     * @param AD_Table_ID Table ID of Documents
     * @param Record_ID record ID to load
     * @return Document or null
     */
    fun getDocument(`as`: AccountingSchema, AD_Table_ID: Int, Record_ID: Int): IDoc? {
        var tableName: String? = null
        val documentsTableId = getDocumentsTableID()
        val documentsTableName = getDocumentsTableName()
        for (i in 0 until documentsTableId.size) {
            if (documentsTableId[i] == AD_Table_ID) {
                tableName = documentsTableName[i]
                break
            }
        }
        if (tableName == null) {
            s_log.severe("Not found AD_Table_ID=$AD_Table_ID")
            return null
        }

        val factory = DefaultDocumentFactory()
        val doc = factory.getDocument(`as`, AD_Table_ID, Record_ID)
        if (doc != null)
            return doc

        return null
    }

    /**
     * Create Posting document
     * @param as accounting schema
     * @param AD_Table_ID Table ID of Documents
     * @param rs ResultSet
     * @return Document
     */
    fun getDocument(`as`: AccountingSchema, AD_Table_ID: Int, rs: Row): IDoc? {
        val factory = DefaultDocumentFactory()
        val doc = factory.getDocument(`as`, AD_Table_ID, rs)
        if (doc != null)
            return doc

        return null
    }

    /**
     * Post Document
     * @param ass accounting schema
     * @param AD_Table_ID Transaction table
     * @param Record_ID Record ID of this document
     * @param force force posting
     * @param repost Repost document
     * @return null if the document was posted or error message
     */
    fun postDocument(
        ass: Array<AccountingSchema>,
        AD_Table_ID: Int,
        Record_ID: Int,
        force: Boolean,
        repost: Boolean
    ): String? {

        var tableName: String? = null
        val documentsTableId = getDocumentsTableID()
        val documentsTableName = getDocumentsTableName()

        for (i in 0 until documentsTableId.size) {
            if (documentsTableId[i] == AD_Table_ID) {
                tableName = documentsTableName[i]
                break
            }
        }
        if (tableName == null) {
            s_log.severe("Table not a financial document. AD_Table_ID=$AD_Table_ID")
            val returnMessage = StringBuilder("Table not a financial document. AD_Table_ID=").append(AD_Table_ID)
            return returnMessage.toString()
        }

        val sql = StringBuilder("SELECT * FROM ")
            .append(tableName)
            .append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'")

        val query =
            queryOf(sql.toString(), listOf(Record_ID))
                .map { row -> postDocument(ass, AD_Table_ID, row, force, repost) }.asSingle
        return DB.current.run(query) ?: "NoDoc"
    }

    /**
     * Post Document
     * @param ass accounting schema
     * @param AD_Table_ID Transaction table
     * @param rs Result set
     * @param force force posting
     * @param repost Repost document
     * @return null if the document was posted or error message
     */
    fun postDocument(
        ass: Array<AccountingSchema>,
        AD_Table_ID: Int,
        rs: Row,
        force: Boolean,
        repost: Boolean
    ): String? {
        var error: String? = null
        var status = ""
        for (`as` in ass) {
            val doc = Doc.get(`as`, AD_Table_ID, rs)
            if (doc != null) {
                error = doc.post(force, repost) // 	repost
                status = doc.postStatus
                if (error != null && error.trim { it <= ' ' }.isNotEmpty()) {
                    s_log.info("Error Posting $doc to $`as` Error: $error")
                    throw Error("Error Posting $doc to $`as` Error: $error")
                }
            } else {
                s_log.info("Error Posting $doc to $`as` Error:  NoDoc")
                throw Error("Error Posting $doc to $`as` Error: $error")
            }
        }

        val table = getTable(AD_Table_ID)
        val recordId = rs.int(table.tableKeyColumns[0])
        //  Commit Doc
        if (!save(AD_Table_ID, recordId, status)) {
            val dbError = CLogger.retrieveError()
            // log.log(Level.SEVERE, "(doc not saved) ... rolling back");
            error = if (dbError != null)
                dbError.value
            else
                "SaveError"
        }
        return error
    }

    /**************************************************************************
     * Save to Disk - set posted flag
     * @return true if saved
     */
    private fun save(AD_Table_ID: Int, Record_ID: Int, status: String): Boolean {
        val table = getTable(AD_Table_ID)
        val sql = StringBuilder("UPDATE ")
        sql.append(table.dbTableName).append(" SET Posted='").append(status)
            .append("',Processing='N' ")
            .append("WHERE ")
            .append(table.dbTableName).append("_ID=").append(Record_ID)
        CLogger.resetLast()
        val no = executeUpdate(sql.toString())
        return no == 1
    } //  save
}