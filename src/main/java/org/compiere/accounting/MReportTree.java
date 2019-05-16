package org.compiere.accounting;

import org.compiere.orm.MRole;
import org.compiere.orm.MRoleKt;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Report Tree Model
 *
 * @author Jorg Janke
 * @version $Id: MReportTree.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MReportTree {
    /**
     * Map with Tree
     */
    private static CCache<String, MReportTree> s_trees =
            new CCache<String, MReportTree>(null, "MReportTree", 20);
    /**
     * Optional Hierarchy
     */
    private int m_PA_Hierarchy_ID = 0;
    /**
     * Element Type
     */
    private String m_ElementType = null;
    /**
     * Tree Type
     */
    private String m_TreeType = null;
    /**
     * The Tree
     */
    private MTree m_tree = null;
    /**
     * Logger
     */
    private CLogger log = CLogger.getCLogger(getClass());

    public MReportTree(int PA_Hierarchy_ID, String ElementType) {
        this(PA_Hierarchy_ID, false, ElementType);
    }

    /**
     * ************************************************************************ Report Tree
     *
     * @param PA_Hierarchy_ID optional hierarchy
     * @param allNodes        true to always get full tree
     * @param ElementType     Account Schema Element Type
     */
    public MReportTree(int PA_Hierarchy_ID, boolean allNodes, String ElementType) {
        m_ElementType = ElementType;
        m_TreeType = m_ElementType;
        if (MAcctSchemaElement.ELEMENTTYPE_Account.equals(m_ElementType)
                || MAcctSchemaElement.ELEMENTTYPE_UserElementList1.equals(m_ElementType)
                || MAcctSchemaElement.ELEMENTTYPE_UserElementList2.equals(m_ElementType))
            m_TreeType = MTree.TREETYPE_ElementValue;
        if (MAcctSchemaElement.ELEMENTTYPE_OrgTrx.equals(m_ElementType))
            m_TreeType = MTree.TREETYPE_Organization;
        m_PA_Hierarchy_ID = PA_Hierarchy_ID;
        //
        int AD_Tree_ID = getAD_TreeId();
        //	Not found
        if (AD_Tree_ID == 0)
            throw new IllegalArgumentException(
                    "No AD_Tree_ID for TreeType=" + m_TreeType + ", PA_Hierarchy_ID=" + PA_Hierarchy_ID);
        //
        boolean clientTree = true;
        m_tree =
                new MTree(

                        AD_Tree_ID,
                        true,
                        clientTree,
                        allNodes); // include inactive and empty summary nodes
        // remove summary nodes without children
        m_tree.trimTree();
    } //	MReportTree

    /**
     * Get Report Tree (cached)
     *
     * @param PA_Hierarchy_ID optional hierarchy
     * @param ElementType     Account Schema Element Type
     * @return tree
     */
    public static MReportTree get(int PA_Hierarchy_ID, String ElementType) {
        MRole role = MRoleKt.getDefaultRole();
        String key =
                Env.getClientId()
                        + "_"
                        + role.getRoleId()
                        + "_"
                        + PA_Hierarchy_ID
                        + "_"
                        + ElementType;
        if (!role.isAccessAllOrgs() && role.isUseUserOrgAccess()) {
            key =
                    Env.getClientId()
                            + "_"
                            + Env.getUserId()
                            + "_"
                            + role.getRoleId()
                            + "_"
                            + PA_Hierarchy_ID
                            + "_"
                            + ElementType;
        }

        MReportTree tree = s_trees.get(key);
        if (tree == null) {
            tree = new MReportTree(PA_Hierarchy_ID, ElementType);
            s_trees.put(key, tree);
        }
        return tree;
    } //	get

    /**
     * Get AD_Tree_ID
     *
     * @return tree
     */
    protected int getAD_TreeId() {
        if (m_PA_Hierarchy_ID == 0) return getDefaultAD_TreeId();

        MHierarchy hierarchy = MHierarchy.get(m_PA_Hierarchy_ID);
        int AD_Tree_ID = hierarchy.getAD_TreeId(m_TreeType);

        if (AD_Tree_ID == 0) return getDefaultAD_TreeId();

        return AD_Tree_ID;
    } //	getTreeId

    /**
     * Get Default AD_Tree_ID see MTree.getDefaultAD_Tree_ID
     *
     * @return tree
     */
    protected int getDefaultAD_TreeId() {
        int AD_Tree_ID = 0;
        int AD_Client_ID = Env.getClientId();

        String sql =
                "SELECT AD_Tree_ID, Name FROM AD_Tree "
                        + "WHERE AD_Client_ID=? AND TreeType=? AND IsActive='Y' AND IsAllNodes='Y' "
                        + "ORDER BY IsDefault DESC, AD_Tree_ID"; //	assumes first is primary tree
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, AD_Client_ID);
            pstmt.setString(2, m_TreeType);
            rs = pstmt.executeQuery();
            if (rs.next()) AD_Tree_ID = rs.getInt(1);
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
        } finally {

            rs = null;
            pstmt = null;
        }

        return AD_Tree_ID;
    } //	getDefaultAD_Tree_ID

    /**
     * Get Child IDs
     *
     * @param ID start node
     * @return array if IDs
     */
    public Integer[] getChildIDs(int ID) {
        if (log.isLoggable(Level.FINE)) log.fine("(" + m_ElementType + ") ID=" + ID);
        ArrayList<Integer> list = new ArrayList<Integer>();
        //
        MTreeNode node = m_tree.getRoot().findNode(ID);
        if (log.isLoggable(Level.FINEST)) log.finest("Root=" + node);
        //
        if (node != null && node.isSummary()) {
            Enumeration<?> en = node.preorderEnumeration();
            while (en.hasMoreElements()) {
                MTreeNode nn = (MTreeNode) en.nextElement();
                if (!nn.isSummary()) {
                    list.add(new Integer(nn.getNodeId()));
                    if (log.isLoggable(Level.FINEST)) log.finest("- " + nn);
                } else if (log.isLoggable(Level.FINEST)) log.finest("- skipped parent (" + nn + ")");
            }
        } else //	not found or not summary
            list.add(new Integer(ID));
        //
        Integer[] retValue = new Integer[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getWhereClause

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MReportTree[ElementType=");
        sb.append(m_ElementType)
                .append(",TreeType=")
                .append(m_TreeType)
                .append(",")
                .append(m_tree)
                .append("]");
        return sb.toString();
    } //	toString
} //	MReportTree
