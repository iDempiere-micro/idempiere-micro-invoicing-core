package org.compiere.production;

import org.compiere.model.I_PA_ColorSchema;
import org.compiere.orm.BasePOName;
import org.idempiere.orm.I_Persistent;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for PA_ColorSchema
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_ColorSchema extends BasePOName implements I_PA_ColorSchema, I_Persistent {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_ColorSchema(Properties ctx, int PA_ColorSchema_ID) {
        super(ctx, PA_ColorSchema_ID);
        /**
         * if (PA_ColorSchema_ID == 0) { setAD_PrintColor1_ID (0); setAD_PrintColor2_ID (0);
         * setEntityType (null); // @SQL=select get_sysconfig('DEFAULT_ENTITYTYPE','U',0,0) from dual
         * setMark1Percent (0); setMark2Percent (0); setName (null); setPA_ColorSchema_ID (0); }
         */
    }

    /**
     * Load Constructor
     */
    public X_PA_ColorSchema(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    }

    /**
     * AccessLevel
     *
     * @return 6 - System - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_PA_ColorSchema[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Color 1.
     *
     * @return First color used
     */
    public int getAD_PrintColor1_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_AD_PrintColor1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Color 1.
     *
     * @param AD_PrintColor1_ID First color used
     */
    public void setAD_PrintColor1_ID(int AD_PrintColor1_ID) {
        if (AD_PrintColor1_ID < 1) set_Value(COLUMNNAME_AD_PrintColor1_ID, null);
        else set_Value(COLUMNNAME_AD_PrintColor1_ID, Integer.valueOf(AD_PrintColor1_ID));
    }

    /**
     * Get Color 2.
     *
     * @return Second color used
     */
    public int getAD_PrintColor2_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_AD_PrintColor2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Color 2.
     *
     * @param AD_PrintColor2_ID Second color used
     */
    public void setAD_PrintColor2_ID(int AD_PrintColor2_ID) {
        if (AD_PrintColor2_ID < 1) set_Value(COLUMNNAME_AD_PrintColor2_ID, null);
        else set_Value(COLUMNNAME_AD_PrintColor2_ID, Integer.valueOf(AD_PrintColor2_ID));
    }

    /**
     * Get Color 3.
     *
     * @return Third color used
     */
    public int getAD_PrintColor3_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_AD_PrintColor3_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Color 3.
     *
     * @param AD_PrintColor3_ID Third color used
     */
    public void setAD_PrintColor3_ID(int AD_PrintColor3_ID) {
        if (AD_PrintColor3_ID < 1) set_Value(COLUMNNAME_AD_PrintColor3_ID, null);
        else set_Value(COLUMNNAME_AD_PrintColor3_ID, Integer.valueOf(AD_PrintColor3_ID));
    }

    /**
     * Get Color 4.
     *
     * @return Forth color used
     */
    public int getAD_PrintColor4_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_AD_PrintColor4_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Mark 1 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark1Percent() {
        Integer ii = (Integer) get_Value(COLUMNNAME_Mark1Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 1 Percent.
     *
     * @param Mark1Percent Percentage up to this color is used
     */
    public void setMark1Percent(int Mark1Percent) {
        set_Value(COLUMNNAME_Mark1Percent, Integer.valueOf(Mark1Percent));
    }

    /**
     * Get Mark 2 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark2Percent() {
        Integer ii = (Integer) get_Value(COLUMNNAME_Mark2Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 2 Percent.
     *
     * @param Mark2Percent Percentage up to this color is used
     */
    public void setMark2Percent(int Mark2Percent) {
        set_Value(COLUMNNAME_Mark2Percent, Integer.valueOf(Mark2Percent));
    }

    /**
     * Get Mark 3 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark3Percent() {
        Integer ii = (Integer) get_Value(COLUMNNAME_Mark3Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 3 Percent.
     *
     * @param Mark3Percent Percentage up to this color is used
     */
    public void setMark3Percent(int Mark3Percent) {
        set_Value(COLUMNNAME_Mark3Percent, Integer.valueOf(Mark3Percent));
    }

    /**
     * Get Mark 4 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark4Percent() {
        Integer ii = (Integer) get_Value(COLUMNNAME_Mark4Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 4 Percent.
     *
     * @param Mark4Percent Percentage up to this color is used
     */
    public void setMark4Percent(int Mark4Percent) {
        set_Value(COLUMNNAME_Mark4Percent, Integer.valueOf(Mark4Percent));
    }

    @Override
    public int getTableId() {
        return I_PA_ColorSchema.Table_ID;
    }
}
