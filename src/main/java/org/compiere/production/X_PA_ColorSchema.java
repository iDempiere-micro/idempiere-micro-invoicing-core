package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_PA_ColorSchema;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for PA_ColorSchema
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PA_ColorSchema extends BasePOName implements I_PA_ColorSchema {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PA_ColorSchema(int PA_ColorSchema_ID) {
        super(PA_ColorSchema_ID);
    }

    /**
     * Load Constructor
     */
    public X_PA_ColorSchema(Row row) {
        super(row);
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
        return "X_PA_ColorSchema[" + getId() + "]";
    }

    /**
     * Get Color 1.
     *
     * @return First color used
     */
    public int getPrintColor1Id() {
        Integer ii = getValue(COLUMNNAME_AD_PrintColor1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Color 1.
     *
     * @param AD_PrintColor1_ID First color used
     */
    public void setPrintColor1Id(int AD_PrintColor1_ID) {
        if (AD_PrintColor1_ID < 1) setValue(COLUMNNAME_AD_PrintColor1_ID, null);
        else setValue(COLUMNNAME_AD_PrintColor1_ID, Integer.valueOf(AD_PrintColor1_ID));
    }

    /**
     * Get Color 2.
     *
     * @return Second color used
     */
    public int getPrintColor2Id() {
        Integer ii = getValue(COLUMNNAME_AD_PrintColor2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Color 2.
     *
     * @param AD_PrintColor2_ID Second color used
     */
    public void setPrintColor2Id(int AD_PrintColor2_ID) {
        if (AD_PrintColor2_ID < 1) setValue(COLUMNNAME_AD_PrintColor2_ID, null);
        else setValue(COLUMNNAME_AD_PrintColor2_ID, Integer.valueOf(AD_PrintColor2_ID));
    }

    /**
     * Get Color 3.
     *
     * @return Third color used
     */
    public int getPrintColor3Id() {
        Integer ii = getValue(COLUMNNAME_AD_PrintColor3_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Color 3.
     *
     * @param AD_PrintColor3_ID Third color used
     */
    public void setPrintColor3Id(int AD_PrintColor3_ID) {
        if (AD_PrintColor3_ID < 1) setValue(COLUMNNAME_AD_PrintColor3_ID, null);
        else setValue(COLUMNNAME_AD_PrintColor3_ID, Integer.valueOf(AD_PrintColor3_ID));
    }

    /**
     * Get Color 4.
     *
     * @return Forth color used
     */
    public int getPrintColor4Id() {
        Integer ii = getValue(COLUMNNAME_AD_PrintColor4_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Mark 1 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark1Percent() {
        Integer ii = getValue(COLUMNNAME_Mark1Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 1 Percent.
     *
     * @param Mark1Percent Percentage up to this color is used
     */
    public void setMark1Percent(int Mark1Percent) {
        setValue(COLUMNNAME_Mark1Percent, Integer.valueOf(Mark1Percent));
    }

    /**
     * Get Mark 2 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark2Percent() {
        Integer ii = getValue(COLUMNNAME_Mark2Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 2 Percent.
     *
     * @param Mark2Percent Percentage up to this color is used
     */
    public void setMark2Percent(int Mark2Percent) {
        setValue(COLUMNNAME_Mark2Percent, Integer.valueOf(Mark2Percent));
    }

    /**
     * Get Mark 3 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark3Percent() {
        Integer ii = getValue(COLUMNNAME_Mark3Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 3 Percent.
     *
     * @param Mark3Percent Percentage up to this color is used
     */
    public void setMark3Percent(int Mark3Percent) {
        setValue(COLUMNNAME_Mark3Percent, Integer.valueOf(Mark3Percent));
    }

    /**
     * Get Mark 4 Percent.
     *
     * @return Percentage up to this color is used
     */
    public int getMark4Percent() {
        Integer ii = getValue(COLUMNNAME_Mark4Percent);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Mark 4 Percent.
     *
     * @param Mark4Percent Percentage up to this color is used
     */
    public void setMark4Percent(int Mark4Percent) {
        setValue(COLUMNNAME_Mark4Percent, Integer.valueOf(Mark4Percent));
    }

    @Override
    public int getTableId() {
        return I_PA_ColorSchema.Table_ID;
    }
}
