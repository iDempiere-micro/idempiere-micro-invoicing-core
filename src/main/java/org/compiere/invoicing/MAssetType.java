package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.AssetType;
import org.idempiere.common.util.CCache;

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
            new CCache<Integer, MAssetType>(AssetType.Table_Name, 0);

    /**
     * Standard Constructor
     */
    public MAssetType(int A_Asset_Type_ID) {
        super(A_Asset_Type_ID);
    }

    /**
     * Load Constructor
     */
    public MAssetType(Row row) {
        super(row);
    }

    /**
     * Get Asset Type
     *
     * @param ctx             context
     * @param A_Asset_Type_ID
     * @return asset type object
     */
    public static MAssetType get(int A_Asset_Type_ID) {
        if (A_Asset_Type_ID <= 0) return null;
        MAssetType o = s_cache.get(A_Asset_Type_ID);
        if (o != null) return o;
        o = new MAssetType(A_Asset_Type_ID);
        if (o.getId() > 0) {
            s_cache.put(A_Asset_Type_ID, o);
            return o;
        }
        return null;
    }

    public static boolean isFixedAsset(MAsset asset) {
        return asset != null && A_ASSET_TYPE_MFX.equals(asset.getAssetType().getSearchKey());
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
        return A_ASSET_TYPE_MFX.equals(getSearchKey());
    }

    public interface Model {
        /**
         * Get Context
         */


        /**
         * Get Asset Type
         */
        int getAssetTypeId();

        /**
         * Get In Possession. The asset is in the possession of the organization
         */
        boolean isInPosession();

        /**
         * Get Owned. The asset is owned by the organization
         */
        boolean isOwned();

        /**
         * Get Is Depreciated
         */
        boolean isDepreciated();
    }

} // class MAssetType
