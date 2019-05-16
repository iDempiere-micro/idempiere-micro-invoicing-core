package org.idempiere.process;

import org.compiere.model.Client;
import org.compiere.model.Column;
import org.compiere.model.Table;
import org.compiere.orm.MClientKt;
import org.compiere.orm.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.DisplayType;
import org.idempiere.common.util.AdempiereUserError;
import org.idempiere.common.util.Language;
import org.idempiere.orm.PO;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.convertString;
import static software.hsharp.core.util.DBKt.executeUpdate;

/**
 * Document Translation Sync
 *
 * @author Jorg Janke
 * @version $Id: TranslationDocSync.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class TranslationDocSync extends SvrProcess {
    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message
     * @throws Exception
     */
    protected String doIt() throws Exception {
        Client client = MClientKt.getClient();
        String baselang = Language.getBaseLanguageCode();
        if (client.isMultiLingualDocument() && client.getADLanguage().equals(baselang)) {
            throw new AdempiereUserError("@clientId@: @IsMultiLingualDocument@");
        }
        if (log.isLoggable(Level.INFO)) log.info("" + client);
        List<Table> tables =
                new Query<Table>(
                        "AD_Table",
                        "TableName LIKE '%_Trl' AND TableName NOT LIKE 'AD%'"
                )
                        .setOrderBy("TableName")
                        .list();
        for (Table table : tables) {
            processTable(table, client);
        }

        return "OK";
    } //	doIt

    /**
     * Process Translation Table
     *
     * @param table table
     */
    private void processTable(Table table, Client client) {
        StringBuilder columnNames = new StringBuilder();
        Column[] columns = table.getColumns(false);
        for (Column column : columns) {
            if ((!column.getColumnName().equals(PO.getUUIDColumnName(table.getDbTableName())))
                    && (column.getReferenceId() == DisplayType.String
                    || column.getReferenceId() == DisplayType.Text)) {
                String columnName = column.getColumnName();
                if (columnNames.length() != 0) columnNames.append(",");
                columnNames.append(columnName);
            }
        }
        String trlTable = table.getDbTableName();
        String baseTable = trlTable.substring(0, trlTable.length() - 4);

        if (log.isLoggable(Level.CONFIG)) log.config(baseTable + ": " + columnNames);

        if (client.isMultiLingualDocument()) {
            String baselang = Language.getBaseLanguageCode();
            if (!client.getADLanguage().equals(baselang)) {
                // tenant language <> base language
                // auto update translation for tenant language

                String sql = "UPDATE " +
                        trlTable +
                        " SET (" +
                        columnNames +
                        ",IsTranslated) = (SELECT " +
                        columnNames +
                        ",'Y' FROM " +
                        baseTable +
                        " b WHERE " +
                        trlTable +
                        "." +
                        baseTable +
                        "_ID=b." +
                        baseTable +
                        "_ID) WHERE AD_Client_ID=" +
                        getClientId() +
                        " AND AD_Language=" +
                        convertString(client.getADLanguage());
                int no = executeUpdate(sql);
                addLog(0, null, new BigDecimal(no), baseTable);
            }  // tenant language = base language
            // nothing to do

        } else {
            // auto update all translations

            String sql = "UPDATE " +
                    trlTable +
                    " SET (" +
                    columnNames +
                    ",IsTranslated) = (SELECT " +
                    columnNames +
                    ",'Y' FROM " +
                    baseTable +
                    " b WHERE " +
                    trlTable +
                    "." +
                    baseTable +
                    "_ID=b." +
                    baseTable +
                    "_ID) WHERE AD_Client_ID=" +
                    getClientId();
            int no = executeUpdate(sql);
            addLog(0, null, new BigDecimal(no), baseTable);
        }
    } //	processTable
} //	TranslationDocSync
