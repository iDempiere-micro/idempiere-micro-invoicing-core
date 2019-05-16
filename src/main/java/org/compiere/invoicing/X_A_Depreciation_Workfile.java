package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.Asset;
import org.compiere.model.DepreciationWorkfile;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for A_Depreciation_Workfile
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_A_Depreciation_Workfile extends PO
        implements DepreciationWorkfile {

    /**
     * Cofinantare = C
     */
    public static final String A_TIP_FINANTARE_Cofinantare = "C";
    /**
     * Proprie = P
     */
    public static final String A_TIP_FINANTARE_Proprie = "P";
    /**
     * Terti = T
     */
    public static final String A_TIP_FINANTARE_Terti = "T";
    /**
     * Actual = A
     */
    public static final String POSTINGTYPE_Actual = "A";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_A_Depreciation_Workfile(int A_Depreciation_Workfile_ID) {
        super(A_Depreciation_Workfile_ID);
    }

    /**
     * Load Constructor
     */
    public X_A_Depreciation_Workfile(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 7 - System - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_A_Depreciation_Workfile[" + getId() + "]";
    }

    /**
     * Get Accumulated Depreciation.
     *
     * @return Accumulated Depreciation
     */
    public BigDecimal getAccumulatedDepreciation() {
        BigDecimal bd = getValue(COLUMNNAME_A_Accumulated_Depr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Accumulated Depreciation.
     *
     * @param A_Accumulated_Depr Accumulated Depreciation
     */
    public void setAccumulatedDepreciation(BigDecimal A_Accumulated_Depr) {
        setValue(COLUMNNAME_A_Accumulated_Depr, A_Accumulated_Depr);
    }

    /**
     * Get Accumulated Depreciation (fiscal).
     *
     * @return Accumulated Depreciation (fiscal)
     */
    public BigDecimal getAccumulatedDepreciationFiscal() {
        BigDecimal bd = getValue(COLUMNNAME_A_Accumulated_Depr_F);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Accumulated Depreciation (fiscal).
     *
     * @param A_Accumulated_Depr_F Accumulated Depreciation (fiscal)
     */
    public void setAccumulatedDepreciationFiscal(BigDecimal A_Accumulated_Depr_F) {
        setValue(COLUMNNAME_A_Accumulated_Depr_F, A_Accumulated_Depr_F);
    }

    /**
     * Get Asset Cost.
     *
     * @return Asset Cost
     */
    public BigDecimal getAssetCost() {
        BigDecimal bd = getValue(COLUMNNAME_A_Asset_Cost);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Asset Cost.
     *
     * @param A_Asset_Cost Asset Cost
     */
    public void setAssetCost(BigDecimal A_Asset_Cost) {
        setValue(COLUMNNAME_A_Asset_Cost, A_Asset_Cost);
    }

    public Asset getAAsset() throws RuntimeException {
        return (Asset)
                MBaseTableKt.getTable(Asset.Table_Name)
                        .getPO(getAssetId());
    }

    /**
     * Get Asset.
     *
     * @return Asset used internally or by customers
     */
    public int getAssetId() {
        Integer ii = getValue(COLUMNNAME_A_Asset_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Asset.
     *
     * @param A_Asset_ID Asset used internally or by customers
     */
    public void setAssetId(int A_Asset_ID) {
        if (A_Asset_ID < 1) setValueNoCheck(COLUMNNAME_A_Asset_ID, null);
        else setValueNoCheck(COLUMNNAME_A_Asset_ID, A_Asset_ID);
    }

    /**
     * Set Life Years.
     *
     * @param A_Asset_Life_Years Life Years
     */
    public void setAssetLifeYears(int A_Asset_Life_Years) {
        setValue(COLUMNNAME_A_Asset_Life_Years, A_Asset_Life_Years);
    }

    /**
     * Set Life Years (fiscal).
     *
     * @param A_Asset_Life_Years_F Life Years (fiscal)
     */
    public void setAssetLifeYearsFiscal(int A_Asset_Life_Years_F) {
        setValue(COLUMNNAME_A_Asset_Life_Years_F, A_Asset_Life_Years_F);
    }

    /**
     * Get Remaining Amt.
     *
     * @return Remaining Amt
     */
    public BigDecimal getAssetRemaining() {
        BigDecimal bd = getValue(COLUMNNAME_A_Asset_Remaining);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Remaining Amt.
     *
     * @param A_Asset_Remaining Remaining Amt
     */
    public void setAssetRemaining(BigDecimal A_Asset_Remaining) {
        setValueNoCheck(COLUMNNAME_A_Asset_Remaining, A_Asset_Remaining);
    }

    /**
     * Get Remaining Amt (fiscal).
     *
     * @return Remaining Amt (fiscal)
     */
    public BigDecimal getAssetRemainingFiscal() {
        BigDecimal bd = getValue(COLUMNNAME_A_Asset_Remaining_F);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Remaining Amt (fiscal).
     *
     * @param A_Asset_Remaining_F Remaining Amt (fiscal)
     */
    public void setAssetRemainingFiscal(BigDecimal A_Asset_Remaining_F) {
        setValueNoCheck(COLUMNNAME_A_Asset_Remaining_F, A_Asset_Remaining_F);
    }

    /**
     * Get Current Period.
     *
     * @return Current Period
     */
    public int getCurrentPeriod() {
        Integer ii = getValue(COLUMNNAME_A_Current_Period);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Current Period.
     *
     * @param A_Current_Period Current Period
     */
    public void setCurrentPeriod(int A_Current_Period) {
        setValue(COLUMNNAME_A_Current_Period, A_Current_Period);
    }

    /**
     * Get Life Periods.
     *
     * @return Life Periods
     */
    public int getLifePeriod() {
        Integer ii = getValue(COLUMNNAME_A_Life_Period);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Life Periods.
     *
     * @param A_Life_Period Life Periods
     */
    public void setLifePeriod(int A_Life_Period) {
        setValue(COLUMNNAME_A_Life_Period, A_Life_Period);
    }

    /**
     * Set Life Period (fiscal).
     *
     * @param A_Life_Period_F Life Period (fiscal)
     */
    public void setLifePeriodFiscal(int A_Life_Period_F) {
        setValue(COLUMNNAME_A_Life_Period_F, A_Life_Period_F);
    }

    /**
     * Set A_Period_Posted.
     *
     * @param A_Period_Posted A_Period_Posted
     */
    public void setPeriodPosted(int A_Period_Posted) {
        setValue(COLUMNNAME_A_Period_Posted, A_Period_Posted);
    }

    /**
     * Set Current Qty.
     *
     * @param A_QTY_Current Current Qty
     */
    public void setQtyCurrent(BigDecimal A_QTY_Current) {
        setValue(COLUMNNAME_A_QTY_Current, A_QTY_Current);
    }

    /**
     * Get Asset Salvage Value.
     *
     * @return Asset Salvage Value
     */
    public BigDecimal getSalvageValue() {
        BigDecimal bd = getValue(COLUMNNAME_A_Salvage_Value);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(COLUMNNAME_DateAcct);
    }

    /**
     * Set Account Date.
     *
     * @param DateAcct Accounting Date
     */
    public void setDateAcct(Timestamp DateAcct) {
        setValue(COLUMNNAME_DateAcct, DateAcct);
    }

    /**
     * Set Depreciate.
     *
     * @param IsDepreciated The asset will be depreciated
     */
    public void setIsDepreciated(boolean IsDepreciated) {
        setValue(COLUMNNAME_IsDepreciated, IsDepreciated);
    }

    /**
     * Get PostingType.
     *
     * @return The type of posted amount for the transaction
     */
    public String getPostingType() {
        return getValue(COLUMNNAME_PostingType);
    }

    /**
     * Set PostingType.
     *
     * @param PostingType The type of posted amount for the transaction
     */
    public void setPostingType(String PostingType) {

        setValue(COLUMNNAME_PostingType, PostingType);
    }

    /**
     * Get Usable Life - Months.
     *
     * @return Months of the usable life of the asset
     */
    public int getUseLifeMonths() {
        Integer ii = getValue(COLUMNNAME_UseLifeMonths);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Usable Life - Months.
     *
     * @param UseLifeMonths Months of the usable life of the asset
     */
    public void setUseLifeMonths(int UseLifeMonths) {
        setValue(COLUMNNAME_UseLifeMonths, UseLifeMonths);
    }

    /**
     * Get Use Life - Months (fiscal).
     *
     * @return Use Life - Months (fiscal)
     */
    public int getUseLifeMonthsFiscal() {
        Integer ii = getValue(COLUMNNAME_UseLifeMonths_F);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Use Life - Months (fiscal).
     *
     * @param UseLifeMonths_F Use Life - Months (fiscal)
     */
    public void setUseLifeMonthsFiscal(int UseLifeMonths_F) {
        setValue(COLUMNNAME_UseLifeMonths_F, UseLifeMonths_F);
    }

    /**
     * Get Usable Life - Years.
     *
     * @return Years of the usable life of the asset
     */
    public int getUseLifeYears() {
        Integer ii = getValue(COLUMNNAME_UseLifeYears);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Usable Life - Years.
     *
     * @param UseLifeYears Years of the usable life of the asset
     */
    public void setUseLifeYears(int UseLifeYears) {
        setValue(COLUMNNAME_UseLifeYears, UseLifeYears);
    }

    /**
     * Get Use Life - Years (fiscal).
     *
     * @return Use Life - Years (fiscal)
     */
    public int getUseLifeYearsFiscal() {
        Integer ii = getValue(COLUMNNAME_UseLifeYears_F);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Use Life - Years (fiscal).
     *
     * @param UseLifeYears_F Use Life - Years (fiscal)
     */
    public void setUseLifeYearsFiscal(int UseLifeYears_F) {
        setValue(COLUMNNAME_UseLifeYears_F, UseLifeYears_F);
    }

    @Override
    public int getTableId() {
        return DepreciationWorkfile.Table_ID;
    }
}
