package org.idempiere.process;

import org.compiere.model.Column;
import org.compiere.model.Element;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.MColumn;
import org.compiere.orm.MTable;
import org.compiere.orm.M_Element;
import org.compiere.orm.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.AdempiereSystemError;

import java.util.logging.Level;

/**
 * Copy columns from one table to other
 *
 * @author Carlos Ruiz - globalqss
 * @version $Id: CopyColumnsFromTable
 */
public class CopyColumnsFromTable extends SvrProcess {
    /**
     * Target Table
     */
    private int p_target_AD_Table_ID = 0;
    /**
     * Source Table
     */
    private int p_source_AD_Table_ID = 0;

    /**
     * Column Count
     */
    private int m_count = 0;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("AD_Table_ID")) p_source_AD_Table_ID = iProcessInfoParameter.getParameterAsInt();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_target_AD_Table_ID = getRecordId();
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (p_target_AD_Table_ID == 0)
            throw new AdempiereSystemError("@NotFound@ @AD_Table_ID@ " + p_target_AD_Table_ID);
        if (p_source_AD_Table_ID == 0)
            throw new AdempiereSystemError("@NotFound@ @AD_Table_ID@ " + p_source_AD_Table_ID);
        if (log.isLoggable(Level.INFO))
            log.info(
                    "Source AD_Table_ID="
                            + p_source_AD_Table_ID
                            + ", Target AD_Table_ID="
                            + p_target_AD_Table_ID);

        MTable targetTable = new MTable(p_target_AD_Table_ID);
        Column[] targetColumns = targetTable.getColumns(true);
        if (targetColumns.length > 0)
            throw new AdempiereSystemError(MsgKt.getMsg("ErrorCopyColumns"));

        MTable sourceTable = new MTable(p_source_AD_Table_ID);
        Column[] sourceColumns = sourceTable.getColumns(true);

        for (Column sourceColumn : sourceColumns) {
            MColumn colTarget = new MColumn(targetTable);
            PO.copyValues((PO)sourceColumn, colTarget);
            colTarget.setColumnTableId(targetTable.getTableTableId());
            colTarget.setEntityType(targetTable.getEntityType());
            // special case the key -> sourceTable_ID
            if (sourceColumn.getColumnName().equals(sourceTable.getDbTableName() + "_ID")) {
                String targetColumnName = targetTable.getDbTableName() + "_ID";
                colTarget.setColumnName(targetColumnName);
                // if the element doesn't exist, create it
                Element element = M_Element.get(targetColumnName);
                if (element == null) {
                    element =
                            new M_Element(targetColumnName, targetTable.getEntityType());
                    if (targetColumnName.equalsIgnoreCase(targetTable.getDbTableName() + "_ID")) {
                        element.setColumnName(targetTable.getDbTableName() + "_ID");
                        element.setName(targetTable.getName());
                        element.setPrintName(targetTable.getName());
                    }
                    element.saveEx();
                }
                colTarget.setElementId(element.getElementId());
                colTarget.setName(targetTable.getName());
                colTarget.setDescription(targetTable.getDescription());
                colTarget.setHelp(targetTable.getHelp());
            }
            // special case the UUID column -> sourceTable_UU
            if (sourceColumn.getColumnName().equals(sourceTable.getDbTableName() + "_UU")) {
                String targetColumnName = targetTable.getDbTableName() + "_UU";
                colTarget.setColumnName(targetColumnName);
                // if the element doesn't exist, create it
                Element element = M_Element.get(targetColumnName);
                if (element == null) {
                    element =
                            new M_Element(targetColumnName, targetTable.getEntityType());
                    if (targetColumnName.equalsIgnoreCase(targetTable.getDbTableName() + "_UU")) {
                        element.setColumnName(targetTable.getDbTableName() + "_UU");
                        element.setName(targetTable.getDbTableName() + "_UU");
                        element.setPrintName(targetTable.getDbTableName() + "_UU");
                    }
                    element.saveEx();
                }
                colTarget.setElementId(element.getElementId());
                colTarget.setName(targetTable.getName());
                colTarget.setDescription(targetTable.getDescription());
                colTarget.setHelp(targetTable.getHelp());
            }
            colTarget.setIsActive(sourceColumn.isActive());
            colTarget.saveEx();
            // TODO: Copy translations
            m_count++;
        }

        //
        return "#" + m_count;
    } //	doIt
} //	CopyColumnsFromTable
