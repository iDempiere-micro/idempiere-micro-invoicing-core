// package org.compiere.mfg.model;
package org.compiere.production;

import kotliquery.Row;
import org.compiere.orm.Query;
import org.compiere.product.MProduct;
import org.eevolution.model.I_PP_Product_BOM;
import org.idempiere.common.util.CCache;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * PP Product BOM Model.
 *
 * @author Victor Perez www.e-evolution.com
 * @author Teo Sarca, http://www.arhipac.ro
 */
public class MPPProductBOM extends X_PP_Product_BOM {

    /**
     *
     */
    private static final long serialVersionUID = 1561124355655122911L;
    /**
     * Cache
     */
    private static CCache<Integer, MPPProductBOM> s_cache =
            new CCache<Integer, MPPProductBOM>(I_PP_Product_BOM.Table_Name, 40, 5);
    /**
     * BOM Lines
     */
    private List<MPPProductBOMLine> m_lines = null;

    public MPPProductBOM(Properties ctx, int PP_Product_BOM_ID) {
        super(ctx, PP_Product_BOM_ID);
    }

    public MPPProductBOM(Properties ctx, Row row) {
        super(ctx, row);
    }

    /**
     * Get Product BOM by ID (cached)
     *
     * @param ctx
     * @param PP_Product_BOM_ID
     * @return product bom
     */
    public static MPPProductBOM get(Properties ctx, int PP_Product_BOM_ID) {
        if (PP_Product_BOM_ID <= 0) return null;
        MPPProductBOM bom = s_cache.get(PP_Product_BOM_ID);
        if (bom != null) return bom;
        bom = new MPPProductBOM(ctx, PP_Product_BOM_ID);
        if (bom.getId() == PP_Product_BOM_ID) {
            s_cache.put(PP_Product_BOM_ID, bom);
        } else {
            bom = null;
        }
        return bom;
    }

    /**
     * Get BOM with Default Logic (Product = BOM Product and BOM Value = Product Value)
     *
     * @param product
     * @param trxName
     * @return product BOM
     */
    public static MPPProductBOM getDefault(MProduct product) {
        MPPProductBOM bom =
                new Query(
                        product.getCtx(),
                        I_PP_Product_BOM.Table_Name,
                        "M_Product_ID=? AND Value=?"
                )
                        .setParameters(product.getProductId(), product.getSearchKey())
                        .setClientId()
                        .firstOnly();
        // If outside trx, then cache it
        if (bom != null) {
            s_cache.put(bom.getId(), bom);
        }
        //
        return bom;
    }

    /**
     * Get BOM Lines valid date for Product BOM
     *
     * @param valid Date to Validate
     * @return BOM Lines
     */
    public MPPProductBOMLine[] getLines(Timestamp valid) {
        List<MPPProductBOMLine> list = new ArrayList<MPPProductBOMLine>(); // Selected BOM Lines Only
        for (MPPProductBOMLine bl : getLines(true)) {
            if (bl.isValidFromTo(valid)) {
                list.add(bl);
            }
        }
        //
        return list.toArray(new MPPProductBOMLine[list.size()]);
    } //	getLines

    /**
     * Get BOM Lines for Product BOM
     *
     * @return BOM Lines
     */
    public MPPProductBOMLine[] getLines(boolean reload) {
        if (this.m_lines == null || reload) {
            final String whereClause = MPPProductBOMLine.COLUMNNAME_PP_Product_BOM_ID + "=?";
            this.m_lines =
                    new Query(getCtx(), MPPProductBOMLine.Table_Name, whereClause)
                            .setParameters(getProductBOMId())
                            .setOnlyActiveRecords(true)
                            .setOrderBy(MPPProductBOMLine.COLUMNNAME_Line)
                            .list();
        }
        return this.m_lines.toArray(new MPPProductBOMLine[this.m_lines.size()]);
    } //	getLines

    @Override
    protected boolean afterDelete(boolean success) {
        if (!success) return false;

        updateProduct();
        return true;
    }

    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return false;

        if (newRecord || isValueChanged("IsActive")) {
            updateProduct();
        }
        return true;
    }

    private void updateProduct() {
        int count =
                new Query(
                        getCtx(),
                        I_PP_Product_BOM.Table_Name,
                        I_PP_Product_BOM.COLUMNNAME_M_Product_ID + "=?"
                )
                        .setParameters(getProductId())
                        .setOnlyActiveRecords(true)
                        .count();
        MProduct product = new MProduct(getCtx(), getProductId());
        product.setIsBOM(count > 0);
        product.saveEx();
    }

    @Override
    public String toString() {
        StringBuffer sb =
                new StringBuffer("MPPProductBOM[")
                        .append(getId())
                        .append("-")
                        .append(getDocumentNo())
                        .append(", Value=")
                        .append(getValue())
                        .append("]");
        return sb.toString();
    }
} //	MPPProductBOM
