package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.MColumn;
import org.compiere.orm.MTable;
import org.compiere.orm.M_Element;
import org.compiere.orm.PO;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;
import org.idempiere.common.util.AdempiereSystemError;
import org.idempiere.common.util.Env;

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
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (para[i].getParameter() == null) ;
            else if (name.equals("AD_Table_ID")) p_source_AD_Table_ID = para[i].getParameterAsInt();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_target_AD_Table_ID = getRecord_ID();
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

        MTable targetTable = new MTable(getCtx(), p_target_AD_Table_ID);
        MColumn[] targetColumns = targetTable.getColumns(true);
        if (targetColumns.length > 0)
            throw new AdempiereSystemError(Msg.getMsg(Env.getCtx(), "ErrorCopyColumns"));

        MTable sourceTable = new MTable(getCtx(), p_source_AD_Table_ID);
        MColumn[] sourceColumns = sourceTable.getColumns(true);

        for (int i = 0; i < sourceColumns.length; i++) {
            MColumn colTarget = new MColumn(targetTable);
            PO.copyValues(sourceColumns[i], colTarget);
            colTarget.setAD_Table_ID(targetTable.getAD_Table_ID());
            colTarget.setEntityType(targetTable.getEntityType());
            // special case the key -> sourceTable_ID
            if (sourceColumns[i].getColumnName().equals(sourceTable.getTableName() + "_ID")) {
                String targetColumnName = new String(targetTable.getTableName() + "_ID");
                colTarget.setColumnName(targetColumnName);
                // if the element doesn't exist, create it
                M_Element element = M_Element.get(getCtx(), targetColumnName);
                if (element == null) {
                    element =
                            new M_Element(getCtx(), targetColumnName, targetTable.getEntityType());
                    if (targetColumnName.equalsIgnoreCase(targetTable.getTableName() + "_ID")) {
                        element.setColumnName(targetTable.getTableName() + "_ID");
                        element.setName(targetTable.getName());
                        element.setPrintName(targetTable.getName());
                    }
                    element.saveEx();
                }
                colTarget.setAD_Element_ID(element.getAD_Element_ID());
                colTarget.setName(targetTable.getName());
                colTarget.setDescription(targetTable.getDescription());
                colTarget.setHelp(targetTable.getHelp());
            }
            // special case the UUID column -> sourceTable_UU
            if (sourceColumns[i].getColumnName().equals(sourceTable.getTableName() + "_UU")) {
                String targetColumnName = new String(targetTable.getTableName() + "_UU");
                colTarget.setColumnName(targetColumnName);
                // if the element doesn't exist, create it
                M_Element element = M_Element.get(getCtx(), targetColumnName);
                if (element == null) {
                    element =
                            new M_Element(getCtx(), targetColumnName, targetTable.getEntityType());
                    if (targetColumnName.equalsIgnoreCase(targetTable.getTableName() + "_UU")) {
                        element.setColumnName(targetTable.getTableName() + "_UU");
                        element.setName(targetTable.getTableName() + "_UU");
                        element.setPrintName(targetTable.getTableName() + "_UU");
                    }
                    element.saveEx();
                }
                colTarget.setAD_Element_ID(element.getAD_Element_ID());
                colTarget.setName(targetTable.getName());
                colTarget.setDescription(targetTable.getDescription());
                colTarget.setHelp(targetTable.getHelp());
            }
            colTarget.setIsActive(sourceColumns[i].isActive());
            colTarget.saveEx();
            // TODO: Copy translations
            m_count++;
        }

        //
        return "#" + m_count;
    } //	doIt
} //	CopyColumnsFromTable
