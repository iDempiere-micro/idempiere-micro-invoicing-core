package org.compiere.production;

import org.compiere.model.I_M_Product;
import org.compiere.product.MProduct;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.exceptions.DBException;
import org.idempiere.common.util.Env;

import javax.swing.tree.DefaultMutableTreeNode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import static software.hsharp.core.util.DBKt.prepareStatement;
import static software.hsharp.core.util.DBKt.setParameters;

class ProductLowLevelCalculator {
    private Hashtable<Integer, Integer> tableproduct = new Hashtable<Integer, Integer>();

    public ProductLowLevelCalculator() {
    }

    /**
     * get low level of a Product
     *
     * @return int low level
     */
    public int getLowLevel(int M_Product_ID) {
        int AD_Client_ID = Env.getClientId();
        tableproduct.clear(); // reset tableproduct cache
        DefaultMutableTreeNode ibom = null;

        tableproduct.put(M_Product_ID, 0); // insert parent into cache
        ibom = iparent(AD_Client_ID, M_Product_ID, 0); // start traversing tree

        return ibom.getDepth();
    }

    /**
     * get an implotion the product
     *
     * @return DefaultMutableTreeNode Tree with all parent product
     */
    private DefaultMutableTreeNode iparent(
            int AD_Client_ID, int M_Product_ID, int PP_Product_BOM_ID) {

        DefaultMutableTreeNode parent =
                new DefaultMutableTreeNode(
                        M_Product_ID + "|" + PP_Product_BOM_ID);

        String sql =
                "SELECT PP_Product_BOMLine_ID FROM PP_Product_BOMLine"
                        + " WHERE IsActive=? AND AD_Client_ID=? AND M_Product_ID=?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            setParameters(pstmt, new Object[]{true, AD_Client_ID, M_Product_ID});
            rs = pstmt.executeQuery();
            while (rs.next()) {
                // If not the first bom line at this level
                if (rs.getRow() > 1) {
                    // need to reset tableproduct cache
                    tableproduct.clear();
                    tableproduct.put(M_Product_ID, PP_Product_BOM_ID); // insert parent into cache
                }
                DefaultMutableTreeNode bom = icomponent(AD_Client_ID, rs.getInt(1), M_Product_ID, parent);
                if (bom != null) {
                    parent.add(bom);
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {

            rs = null;
            pstmt = null;
        }
        return parent;
    }

    /**
     * get an implotion the product
     *
     * @return DefaultMutableTreeNode Tree with all parent product
     */
    private DefaultMutableTreeNode icomponent(
            int AD_Client_ID, int PP_Product_BOMLine_ID, int M_Product_ID, DefaultMutableTreeNode bom) {
        final String sql =
                "SELECT pbom.M_Product_ID , pbom.Value , pbom.PP_Product_BOM_ID FROM  PP_Product_BOMLine pboml"
                        + " INNER JOIN PP_Product_BOM pbom ON (pbom.PP_Product_BOM_ID = pboml.PP_Product_BOM_ID)"
                        + " WHERE pbom.IsActive=? AND pboml.IsActive=? AND pboml.AD_Client_ID=? AND pboml.PP_Product_BOMLine_ID=? ";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            setParameters(pstmt, new Object[]{true, true, AD_Client_ID, PP_Product_BOMLine_ID});
            rs = pstmt.executeQuery();
            while (rs.next()) {
                if (M_Product_ID != rs.getInt(1)) {
                    // BOM Loop Error
                    if (!tableproduct(rs.getInt(1), rs.getInt(3))) {
                        bom.add(iparent(AD_Client_ID, rs.getInt(1), rs.getInt(3)));
                    } else {
                        throw new AdempiereException(
                                "Cycle BOM & Formula:" + rs.getString(2) + "(" + rs.getString(3) + ")");
                    }
                } else {
                    // Child = Parent error
                    I_M_Product product = MProduct.get(M_Product_ID);
                    throw new AdempiereException(
                            "Cycle BOM & Formula:"
                                    + rs.getString(2)
                                    + "("
                                    + rs.getString(3)
                                    + ")"
                                    + " - Component: "
                                    + product.getSearchKey()
                                    + "("
                                    + product.getProductId()
                                    + ")");
                }
            }
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {

            rs = null;
            pstmt = null;
        }
        return null;
    }

    /**
     * find a product in cache
     *
     * @return true if product is found
     */
    private boolean tableproduct(int M_Product_ID, int PP_Product_BOM_ID) {
        if (tableproduct.containsKey(M_Product_ID)) {
            return true;
        }
        tableproduct.put(M_Product_ID, PP_Product_BOM_ID);
        return false;
    }
}
