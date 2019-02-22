package org.compiere.invoicing;

import org.compiere.model.I_A_Asset_Type;
import org.idempiere.common.util.CCache;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Asset Type
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MAssetType extends X_A_Asset_Type {
    /**
     *
     */
    private static final long serialVersionUID = -1371478760221357780L;

    private static final String A_ASSET_TYPE_MFX =
            "MFX"; // HARDCODED - you must create a Asset Type with Value=MFX to indicate is Fixed Asset
    // Object
    /**
     * Static Cache: A_Asset_Type.A_Asset_Type_ID-> MAssetType
     */
    private static CCache<Integer, MAssetType> s_cache =
            new CCache<Integer, MAssetType>(I_A_Asset_Type.Table_Name, 10, 0);

    ;

    /**
     * Standard Constructor
     */
    public MAssetType(Properties ctx, int A_Asset_Type_ID) {
        super(ctx, A_Asset_Type_ID);
    }

    /**
     * Load Constructor
     */
    public MAssetType(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    }

    /**
     * Get Asset Type
     *
     * @param ctx             context
     * @param A_Asset_Type_ID
     * @return asset type object
     */
    public static MAssetType get(Properties ctx, int A_Asset_Type_ID) {
        if (A_Asset_Type_ID <= 0) return null;
        MAssetType o = s_cache.get(A_Asset_Type_ID);
        if (o != null) return o;
        o = new MAssetType(ctx, A_Asset_Type_ID);
        if (o.getId() > 0) {
            s_cache.put(A_Asset_Type_ID, o);
            return o;
        }
        return null;
    }

    public static boolean isFixedAsset(MAsset asset) {
        return asset != null && A_ASSET_TYPE_MFX.equals(asset.getA_Asset_Type().getValue());
    }

    /**
     * Convert an Yes-No-Unknown field to Boolean
     */
    protected static Boolean getBoolean(String value, boolean useDefaults) {
        if (value == null || value.length() == 0) return null;
        String f = value.substring(0, 1);
        if ("N".equals(f)) return Boolean.FALSE;
        else if ("Y".equals(f)) return Boolean.TRUE;
        else if ("X".equals(f) && useDefaults) return getBoolean(value.substring(1), false);
        else return null;
    }

    /**
     * Is Fixed Asset
     */
    public boolean isFixedAsset() {
        return A_ASSET_TYPE_MFX.equals(getValue());
    }

    public static interface Model {
        /**
         * Get Context
         */
        public Properties getCtx();

        /**
         * Get Asset Type
         */
        public int getA_Asset_Type_ID();

        /**
         * Get In Possession. The asset is in the possession of the organization
         */
        public boolean isInPosession();

        /**
         * Get Owned. The asset is owned by the organization
         */
        public boolean isOwned();

        /**
         * Get Is Depreciated
         */
        public boolean isDepreciated();
    }

} // class MAssetType
