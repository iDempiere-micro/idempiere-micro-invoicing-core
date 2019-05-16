package org.idempiere.process;

import org.compiere.accounting.MTree;
import org.compiere.model.Table;
import org.compiere.orm.MTree_Base;
import org.compiere.orm.MTree_Node;
import org.compiere.orm.MTree_NodeBP;
import org.compiere.orm.MTree_NodeMM;
import org.compiere.orm.MTree_NodePR;
import org.compiere.orm.PO;
import org.compiere.process.SvrProcess;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.prepareStatement;


/**
 * Tree Maintenance
 *
 * @author Jorg Janke
 * @version $Id: TreeMaintenance.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class TreeMaintenance extends SvrProcess {
    /**
     * Tree
     */
    private int m_AD_Tree_ID;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        m_AD_Tree_ID = getRecordId(); // 	from Window
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO)) log.info("AD_Tree_ID=" + m_AD_Tree_ID);
        if (m_AD_Tree_ID == 0) throw new IllegalArgumentException("Tree_ID = 0");
        MTree tree = new MTree(m_AD_Tree_ID);
        if (tree == null || tree.getTreeId() == 0)
            throw new IllegalArgumentException("No Tree -" + tree);
        //
        if (MTree.TREETYPE_BoM.equals(tree.getTreeType())) return "BOM Trees not implemented";
        return verifyTree(tree);
    } //	doIt

    /**
     * Verify Tree
     *
     * @param tree tree
     */
    private String verifyTree(MTree_Base tree) {
        String nodeTableName = tree.getNodeTableName();
        String sourceTableName = tree.getSourceTableName(true);
        String sourceTableKey = sourceTableName + "_ID";
        int AD_Client_ID = tree.getClientId();
        int C_Element_ID = 0;
        if (MTree.TREETYPE_ElementValue.equals(tree.getTreeType())) {
            StringBuilder sql =
                    new StringBuilder("SELECT C_Element_ID FROM C_Element ")
                            .append("WHERE AD_Tree_ID=")
                            .append(tree.getTreeId());
            C_Element_ID = getSQLValue(sql.toString());
            if (C_Element_ID <= 0) throw new IllegalStateException("No Account Element found");
        }

        //	Delete unused
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE ")
                .append(nodeTableName)
                .append(" WHERE AD_Tree_ID=")
                .append(tree.getTreeId())
                .append(" AND Node_ID NOT IN (SELECT ")
                .append(sourceTableKey)
                .append(" FROM ")
                .append(sourceTableName)
                .append(" WHERE AD_Client_ID=")
                .append(AD_Client_ID);
        if (C_Element_ID > 0) sql.append(" AND C_Element_ID=").append(C_Element_ID);
        sql.append(")");
        if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
        //
        int deletes = executeUpdate(sql.toString());
        addLog(0, null, new BigDecimal(deletes), tree.getName() + " Deleted");
        if (!tree.isAllNodes()) {
            StringBuilder msgreturn = new StringBuilder().append(tree.getName()).append(" OK");
            return msgreturn.toString();
        }
        //	Insert new
        int inserts = 0;
        sql = new StringBuilder();
        sql.append("SELECT ")
                .append(sourceTableKey)
                .append(" FROM ")
                .append(sourceTableName)
                .append(" WHERE AD_Client_ID=")
                .append(AD_Client_ID);
        if (C_Element_ID > 0) sql.append(" AND C_Element_ID=").append(C_Element_ID);
        sql.append(" AND ")
                .append(sourceTableKey)
                .append("  NOT IN (SELECT Node_ID FROM ")
                .append(nodeTableName)
                .append(" WHERE AD_Tree_ID=")
                .append(tree.getTreeId())
                .append(")");
        if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
        //
        boolean ok = true;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int Node_ID = rs.getInt(1);
                org.compiere.orm.PO node = null;
                if (nodeTableName.equals("AD_TreeNode")) node = new MTree_Node(tree, Node_ID);
                else if (nodeTableName.equals("AD_TreeNodeBP")) node = new MTree_NodeBP(tree, Node_ID);
                else if (nodeTableName.equals("AD_TreeNodePR")) node = new MTree_NodePR(tree, Node_ID);
                else if (nodeTableName.equals("AD_TreeNodeMM")) node = new MTree_NodeMM(tree, Node_ID);
                //
                if (node == null) log.log(Level.SEVERE, "No Model for " + nodeTableName);
                else {
                    if (node.save()) inserts++;
                    else log.log(Level.SEVERE, "Could not add to " + tree + " Node_ID=" + Node_ID);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "verifyTree", e);
            ok = false;
        } finally {

            rs = null;
            pstmt = null;
        }

        //	Driven by Value
        if (tree.isTreeDrivenByValue()) {
            sql = new StringBuilder();
            sql.append("SELECT ")
                    .append(sourceTableKey)
                    .append(" FROM ")
                    .append(sourceTableName)
                    .append(" WHERE AD_Client_ID=")
                    .append(AD_Client_ID);
            if (C_Element_ID > 0) sql.append(" AND C_Element_ID=").append(C_Element_ID);
            if (log.isLoggable(Level.FINER)) log.finer(sql.toString());
            //
            Table table = MBaseTableKt.getTable(sourceTableName);
            try {
                pstmt = prepareStatement(sql.toString());
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    int Node_ID = rs.getInt(1);
                    PO rec = (PO) table.getPO(Node_ID);
                    rec.updateTree(tree.getTreeType());
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, "verifyTree", e);
            }
        }

        addLog(0, null, new BigDecimal(inserts), tree.getName() + " Inserted");
        return tree.getName() + (ok ? " OK" : " Error");
    } //	verifyTree
} //	TreeMaintenence
