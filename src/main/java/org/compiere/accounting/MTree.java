package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.orm.MRole;
import org.compiere.orm.MRoleKt;
import org.compiere.orm.MTableKt;
import org.compiere.orm.MTree_Base;
import org.compiere.orm.MTree_BaseKt;
import org.idempiere.common.util.CLogMgt;
import org.idempiere.common.util.Env;

import javax.sql.RowSet;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getRowSet;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Builds Tree. Creates tree structure - maintained in VTreePanel
 *
 * @author Jorg Janke
 * @version $Id: MTree.java,v 1.2 2006/07/30 00:51:02 jjanke Exp $
 */
public class MTree extends MTree_Base {

    /**
     *
     */
    private static final long serialVersionUID = -6412057411585787707L;
    /**
     * Is Tree editable
     */
    private boolean m_editable = false;
    /**
     * Root Node
     */
    private MTreeNode m_root = null;
    /**
     * Buffer while loading tree
     */
    private ArrayList<MTreeNode> m_buffer = new ArrayList<>();
    /**
     * Prepared Statement for Node Details
     */
    private RowSet m_nodeRowSet;
    /**
     * The tree is displayed on the Java Client (i.e. not web)
     */
    private boolean m_clientTree = true;
    private HashMap<Integer, ArrayList<Integer>> m_nodeIdMap;

    /**
     * Default Constructor. Need to call loadNodes explicitly
     *
     * @param AD_Tree_ID The tree to build
     */
    public MTree(int AD_Tree_ID) {
        super(AD_Tree_ID);
    } //  MTree

    /**
     * Construct & Load Tree
     *
     * @param AD_Tree_ID The tree to build
     * @param editable   True, if tree can be modified - includes inactive and empty summary nodes
     * @param clientTree the tree is displayed on the java client (not on web)
     */
    public MTree(
            int AD_Tree_ID, boolean editable, boolean clientTree) {
        this(AD_Tree_ID, editable, clientTree, false);
    } //  MTree

    public MTree(

            int AD_Tree_ID,
            boolean editable,
            boolean clientTree,
            boolean allNodes) {
        this(AD_Tree_ID);
        m_editable = editable;
        int AD_User_ID;
        if (allNodes) AD_User_ID = -1;
        else AD_User_ID = Env.getContextAsInt("AD_User_ID");
        m_clientTree = clientTree;
        if (log.isLoggable(Level.INFO))
            log.info(
                    "AD_Tree_ID="
                            + AD_Tree_ID
                            + ", AD_User_ID="
                            + AD_User_ID
                            + ", Editable="
                            + editable
                            + ", OnClient="
                            + clientTree);
        //
        loadNodes(AD_User_ID);
    } //  MTree

    /**
     * Load Constructor
     */
    public MTree(Row row) {
        super(row);
    } //	MTree_Base

    /**
     * *********************************************************************** Load Nodes and Bar
     *
     * @param AD_User_ID user for tree bar
     */
    private void loadNodes(int AD_User_ID) {
        //  SQL for TreeNodes
        StringBuffer sql;
        if (getTreeType()
                .equals(TREETYPE_Menu)) // specific sql, need to load TreeBar IDEMPIERE 329 - nmicoud
        {
            sql =
                    new StringBuffer("SELECT " + "tn.Node_ID,tn.Parent_ID,tn.SeqNo,tb.IsActive " + "FROM ")
                            .append(getNodeTableName()).append(" tn" + " LEFT OUTER JOIN AD_TreeBar tb ON (tn.AD_Tree_ID=tb.AD_Tree_ID" + " AND tn.Node_ID=tb.Node_ID AND tb.IsFavourite = 'Y'").append(AD_User_ID != -1 ? " AND tb.AD_User_ID=? " : "").append(") ").append("WHERE tn.AD_Tree_ID=?"); //	#2
            if (!m_editable) sql.append(" AND tn.IsActive='Y'");
            sql.append(" ORDER BY COALESCE(tn.Parent_ID, -1), tn.SeqNo");
        } else // IDEMPIERE 329 - nmicoud
        {
            String sourceTableName = MTree_BaseKt.getSourceTableName(getTreeType());
            if (sourceTableName == null) {
                if (getTreeTableId() > 0) sourceTableName = MTableKt.getDbTableName(getTreeTableId());
            }
            sql =
                    new StringBuffer("SELECT " + "tn.Node_ID,tn.Parent_ID,tn.SeqNo,st.IsActive " + "FROM ")
                            .append(sourceTableName)
                            .append(" st " + "LEFT OUTER JOIN ")
                            .append(getNodeTableName()).append(" tn ON (tn.Node_ID=st.").append(sourceTableName).append("_ID) ").append("WHERE tn.AD_Tree_ID=?"); //	#2
            if (!m_editable) sql.append(" AND tn.IsActive='Y'");
            sql.append(" ORDER BY COALESCE(tn.Parent_ID, -1), tn.SeqNo");
            // do not check access if allNodes
            if (AD_User_ID != -1)
                sql =
                        new StringBuffer(
                                MRoleKt.getDefaultRole()
                                        .addAccessSQL(
                                                sql.toString(),
                                                "st",
                                                MRole.SQL_FULLYQUALIFIED,
                                                MRole.SQL_RO)); // SQL_RO for Org_ID = 0
        }
        if (log.isLoggable(Level.FINEST)) log.finest(sql.toString());
        //  The Node Loop
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            // load Node details - addToTree -> getNodeDetail
            getNodeDetails();
            //
            pstmt = prepareStatement(sql.toString());
            int idx = 1;
            if (AD_User_ID != -1 && getTreeType().equals(TREETYPE_Menu)) // IDEMPIERE 329 - nmicoud
                pstmt.setInt(idx++, AD_User_ID);
            pstmt.setInt(idx++, getTreeId());
            //	Get Tree & Bar
            rs = pstmt.executeQuery();
            m_root = new MTreeNode(0, 0, getName(), getDescription(), 0, true, null, false, null);
            while (rs.next()) {
                int node_ID = rs.getInt(1);
                int parent_ID = rs.getInt(2);
                int seqNo = rs.getInt(3);
                boolean onBar = (rs.getString(4) != null);
                //
                if (node_ID != 0 || parent_ID != 0) {
                    addToTree(node_ID, parent_ID, seqNo, onBar); // 	calls getNodeDetail
                }
            }
            //
            // closing the rowset will also close connection for oracle rowset implementation
            // m_nodeRowSet.close();
            m_nodeRowSet = null;
            m_nodeIdMap = null;
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql.toString(), e);
            m_nodeRowSet = null;
            m_nodeIdMap = null;
        }

        //  Done with loading - add remainder from buffer
        if (m_buffer.size() != 0) {
            if (log.isLoggable(Level.FINEST)) log.finest("clearing buffer - Adding to: " + m_root);
            for (int i = 0; i < m_buffer.size(); i++) {
                MTreeNode node = m_buffer.get(i);
                MTreeNode parent = m_root.findNode(node.getParentId());
                if (parent != null && parent.getAllowsChildren()) {
                    parent.add(node);
                    int sizeBeforeCheckBuffer = m_buffer.size();
                    checkBuffer(node);
                    if (sizeBeforeCheckBuffer == m_buffer.size()) m_buffer.remove(i);
                    i = -1; // 	start again with i=0
                }
            }
        }

        //	Nodes w/o parent
        if (m_buffer.size() != 0) {
            log.severe("Nodes w/o parent - adding to root - " + m_buffer);
            for (int i = 0; i < m_buffer.size(); i++) {
                MTreeNode node = m_buffer.get(i);
                m_root.add(node);
                int sizeBeforeCheckBuffer = m_buffer.size();
                checkBuffer(node);
                if (sizeBeforeCheckBuffer == m_buffer.size()) m_buffer.remove(i);
                i = -1;
            }
            if (m_buffer.size() != 0) log.severe("Still nodes in Buffer - " + m_buffer);
        } //	nodes w/o parents

        //  clean up
        if (!m_editable && m_root.getChildCount() > 0) trimTree();
        //		diagPrintTree();
        if (CLogMgt.isLevelFinest() || m_root.getChildCount() == 0)
            if (log.isLoggable(Level.FINE)) log.fine("ChildCount=" + m_root.getChildCount());
    } //  loadNodes

    /**
     * Add Node to Tree. If not found add to buffer
     *
     * @param node_ID   Node_ID
     * @param parent_ID Parent_ID
     * @param seqNo     SeqNo
     * @param onBar     on bar
     */
    private void addToTree(int node_ID, int parent_ID, int seqNo, boolean onBar) {
        //  Create new Node
        MTreeNode child = getNodeDetail(node_ID, parent_ID, seqNo, onBar);
        if (child == null) return;

        //  Add to Tree
        MTreeNode parent = null;
        if (m_root != null) parent = m_root.findNode(parent_ID);
        //  Parent found
        if (parent != null && parent.getAllowsChildren()) {
            parent.add(child);
            //  see if we can add nodes from buffer
            if (m_buffer.size() > 0) checkBuffer(child);
        } else m_buffer.add(child);
    } //  addToTree

    /**
     * Check the buffer for nodes which have newNode as Parents
     *
     * @param newNode new node
     */
    private void checkBuffer(MTreeNode newNode) {
        //	Ability to add nodes
        if (!newNode.isSummary() || !newNode.getAllowsChildren()) return;
        //
        for (int i = 0; i < m_buffer.size(); i++) {
            MTreeNode node = m_buffer.get(i);
            if (node.getParentId() == newNode.getNodeId()) {
                try {
                    newNode.add(node);
                } catch (Exception e) {
                    log.severe(
                            "Adding " + node.getName() + " to " + newNode.getName() + ": " + e.getMessage());
                }
                m_buffer.remove(i);
                i--;
            }
        }
    } //  checkBuffer

    /**
     * ************************************************************************ Get Node Detail. Loads
     * data into RowSet m_nodeRowSet Columns: - ID - Name - Description - IsSummary - ImageIndicator -
     * additional for Menu Parameter: - Node_ID The SQL contains security/access control
     */
    private void getNodeDetails() {
        //  SQL for Node Info
        StringBuilder sqlNode = new StringBuilder();
        String sourceTable = "t";
        String fromClause = getSourceTableName(false); // 	fully qualified
        String columnNameX = getSourceTableName(true);
        String color = getActionColorName();
        if (getTreeType().equals(TREETYPE_Menu)) {
            boolean base = Env.isBaseLanguage();
            sourceTable = "m";
            if (base)
                sqlNode.append(
                        "SELECT m.AD_Menu_ID, m.Name,m.Description,m.IsSummary,m.Action, "
                                + "m.AD_Window_ID, m.AD_Process_ID, m.AD_Form_ID, m.AD_Workflow_ID, m.AD_Task_ID, m.AD_InfoWindow_ID "
                                + "FROM AD_Menu m");
            else
                sqlNode.append(
                        "SELECT m.AD_Menu_ID,  t.Name,t.Description,m.IsSummary,m.Action, "
                                + "m.AD_Window_ID, m.AD_Process_ID, m.AD_Form_ID, m.AD_Workflow_ID, m.AD_Task_ID, m.AD_InfoWindow_ID "
                                + "FROM AD_Menu m, AD_Menu_Trl t");
            if (!base)
                sqlNode
                        .append(" WHERE m.AD_Menu_ID=t.AD_Menu_ID AND t.AD_Language='")
                        .append(Env.getADLanguage())
                        .append("'");
            if (!m_editable) {
                boolean hasWhere = sqlNode.indexOf(" WHERE ") != -1;
                sqlNode.append(hasWhere ? " AND " : " WHERE ").append("m.IsActive='Y' ");
            }
            //	Do not show Beta
            {
                boolean hasWhere = sqlNode.indexOf(" WHERE ") != -1;
                sqlNode.append(hasWhere ? " AND " : " WHERE ");
                sqlNode
                        .append(
                                "(m.AD_Window_ID IS NULL OR EXISTS (SELECT * FROM AD_Window w WHERE m.AD_Window_ID=w.AD_Window_ID AND w.IsBetaFunctionality='N'))")
                        .append(
                                " AND (m.AD_Process_ID IS NULL OR EXISTS (SELECT * FROM AD_Process p WHERE m.AD_Process_ID=p.AD_Process_ID AND p.IsBetaFunctionality='N'))")
                        .append(
                                " AND (m.AD_Workflow_ID IS NULL OR EXISTS (SELECT * FROM AD_Workflow wf WHERE m.AD_Workflow_ID=wf.AD_Workflow_ID AND wf.IsBetaFunctionality='N'))")
                        .append(
                                " AND (m.AD_Form_ID IS NULL OR EXISTS (SELECT * FROM AD_Form f WHERE m.AD_Form_ID=f.AD_Form_ID AND f.IsBetaFunctionality='N'))");
            }
            //	In R/O Menu - Show only defined Forms
            if (!m_editable) {
                boolean hasWhere = sqlNode.indexOf(" WHERE ") != -1;
                sqlNode.append(hasWhere ? " AND " : " WHERE ");
                sqlNode.append(
                        "(m.AD_Form_ID IS NULL OR EXISTS (SELECT * FROM AD_Form f WHERE m.AD_Form_ID=f.AD_Form_ID AND ");
                if (m_clientTree) sqlNode.append("f.Classname");
                else sqlNode.append("f.JSPURL");
                sqlNode.append(" IS NOT NULL))");
            }
        } else if (getTreeTableId() != 0) {
            String tableName = MTableKt.getDbTableName(getTreeTableId());
            sqlNode.append("SELECT t.").append(tableName).append("_ID,");
            if (isTreeDrivenByValue()) sqlNode.append("t.Value || ' - ' || t.Name,");
            else sqlNode.append("t.Name,");

            sqlNode
                    .append("t.Description,t.IsSummary,")
                    .append(color)
                    .append(" FROM ")
                    .append(tableName)
                    .append(" t ");
            if (!m_editable) sqlNode.append(" WHERE t.IsActive='Y'");
        } else if (isTreeDrivenByValue()) {
            sqlNode
                    .append("SELECT t.")
                    .append(columnNameX)
                    .append("_ID, t.Value || ' - ' || t.Name, t.Description, t.IsSummary,")
                    .append(color)
                    .append(" FROM ")
                    .append(fromClause);
            if (!m_editable) sqlNode.append(" WHERE t.IsActive='Y'");
        } else {
            if (columnNameX == null)
                throw new IllegalArgumentException("Unknown TreeType=" + getTreeType());
            sqlNode
                    .append("SELECT t.")
                    .append(columnNameX)
                    .append("_ID,t.Name,t.Description,t.IsSummary,")
                    .append(color)
                    .append(" FROM ")
                    .append(fromClause);
            if (!m_editable) sqlNode.append(" WHERE t.IsActive='Y'");
        }
        String sql = sqlNode.toString();
        if (!m_editable) //	editable = menu/etc. window
            sql =
                    MRoleKt.getDefaultRole(false)
                            .addAccessSQL(sql, sourceTable, MRole.SQL_FULLYQUALIFIED, m_editable);
        log.fine(sql);
        m_nodeRowSet = getRowSet();
        m_nodeIdMap = new HashMap<>(50);
        try {
            m_nodeRowSet.beforeFirst();
            int i = 0;
            while (m_nodeRowSet.next()) {
                i++;
                Integer nodeId = m_nodeRowSet.getInt(1);
                ArrayList<Integer> list = m_nodeIdMap.computeIfAbsent(nodeId, k -> new ArrayList<>(5));
                list.add(i);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "", e);
        }
    } //  getNodeDetails

    /**
     * Get Menu Node Details. As SQL contains security access, not all nodes will be found
     *
     * @param node_ID   Key of the record
     * @param parent_ID Parent ID of the record
     * @param seqNo     Sort index
     * @param onBar     Node also on Shortcut bar
     * @return Node
     */
    private MTreeNode getNodeDetail(int node_ID, int parent_ID, int seqNo, boolean onBar) {
        MTreeNode retValue = null;
        try {
            // m_nodeRowSet.beforeFirst();
            ArrayList<Integer> nodeList = m_nodeIdMap.get(node_ID);
            int size = nodeList != null ? nodeList.size() : 0;
            int i = 0;
            // while (m_nodeRowSet.next())
            while (i < size) {
                Integer nodeId = nodeList.get(i);
                i++;
                m_nodeRowSet.absolute(nodeId);
                int node = m_nodeRowSet.getInt(1);
                if (node_ID != node) // 	search for correct one
                    continue;
                //	ID,Name,Description,IsSummary,Action/Color
                int index = 2;
                String name = m_nodeRowSet.getString(index++);
                String description = m_nodeRowSet.getString(index++);
                boolean isSummary = "Y".equals(m_nodeRowSet.getString(index++));
                String actionColor = m_nodeRowSet.getString(index++);
                //	Menu only
                if (getTreeType().equals(TREETYPE_Menu) && !isSummary) {
                    MRoleKt.getDefaultRole(false);
                    //
                    // 	rw or ro for Role
                    if (m_editable) //	Menu Window can see all
                    {
                        retValue =
                                new MTreeNode(
                                        node_ID,
                                        seqNo,
                                        name,
                                        description,
                                        parent_ID,
                                        isSummary,
                                        actionColor,
                                        onBar,
                                        null); //	menu has no color
                    }
                } else //	always add
                {
                    Color color = null; // 	action
                    //
                    retValue =
                            new MTreeNode(
                                    node_ID,
                                    seqNo,
                                    name,
                                    description,
                                    parent_ID,
                                    isSummary,
                                    null,
                                    onBar,
                                    color); //	no action
                }
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "", e);
        }
        return retValue;
    } //  getNodeDetails

    /**
     * ************************************************************************ Trim tree of empty
     * summary nodes
     */
    public void trimTree() {
        boolean needsTrim = m_root != null;
        while (needsTrim) {
            needsTrim = false;
            Enumeration<?> en = m_root.preorderEnumeration();
            while (m_root.getChildCount() > 0 && en.hasMoreElements()) {
                MTreeNode nd = (MTreeNode) en.nextElement();
                if (nd.isSummary() && nd.getChildCount() == 0) {
                    nd.removeFromParent();
                    needsTrim = true;
                }
            }
        }
    } //  trimTree

    /**
     * Get Root node
     *
     * @return root
     */
    public MTreeNode getRoot() {
        return m_root;
    } //  getRoot

    /**
     * String representation
     *
     * @return info
     */
    public String toString() {
        return "MTree[" + "AD_Tree_ID=" + getTreeId() + ", Name=" + getName() +
                "]";
    }
} //  MTree
