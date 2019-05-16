package org.compiere.invoicing;

import kotliquery.Row;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.compiere.model.AssetAccounting;
import org.compiere.model.AssetGroupAccounting;
import org.compiere.model.DepreciationExpense;
import org.compiere.model.DepreciationWorkfile;
import org.compiere.model.SetGetModel;
import org.compiere.model.UseLife;
import org.compiere.orm.PO;
import org.compiere.orm.Query;
import org.compiere.orm.SetGetUtil;
import org.compiere.orm.TimeUtil;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdateEx;

/**
 * Depreciation Workfile Model
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MDepreciationWorkfile extends X_A_Depreciation_Workfile implements UseLife {
    /**
     *
     */
    private static final long serialVersionUID = -3814417671427820714L;
    /**
     * Static cache: Asset/PostingType -> Workfile
     */
    private static CCache<MultiKey, DepreciationWorkfile> s_cacheAsset =
            new CCache<>(
                    DepreciationWorkfile.Table_Name,
                    DepreciationWorkfile.Table_Name + "_Asset",
                    10);
    /**
     * Asset (parent)
     */
    private MAsset m_asset = null;
    /**
     * Logger
     */
    private CLogger log = CLogger.getCLogger(getClass());
    /**
     *
     */
    private boolean m_isFiscal = false;

    /**
     * Default Constructor
     */
    public MDepreciationWorkfile(int A_Depreciation_Workfile_ID) {
        super(A_Depreciation_Workfile_ID);
        if (A_Depreciation_Workfile_ID == 0) {
            setPostingType(X_A_Depreciation_Workfile.POSTINGTYPE_Actual);
            setQtyCurrent(Env.ZERO);
            setAssetCost(Env.ZERO);
            setAccumulatedDepreciation(Env.ZERO);
            setPeriodPosted(0);
            setCurrentPeriod(0);
        }
    } //	MDepreciationWorkfile

    /**
     * Load Constructor
     */
    public MDepreciationWorkfile(Row row) {
        super(row);
    } //	MDepreciationWorkfile

    /**
     *
     */
    public MDepreciationWorkfile(MAsset asset, String postingType, AssetGroupAccounting assetgrpacct) {
        this(0);
        setAssetId(asset.getAssetId());
        setOrgId(asset.getOrgId()); // @win added
        setAssetCost(asset.getA_Asset_Cost());
        setAccumulatedDepreciation(asset.getA_Accumulated_Depr());
        setAccumulatedDepreciationFiscal(asset.getA_Accumulated_Depr_F());
        setCurrentPeriod(asset.getA_Current_Period());

        setIsDepreciated(asset.isDepreciated());
        setPostingType(postingType);
        //
        // Copy UseLife values from asset group to workfile
        if (assetgrpacct == null) {
            assetgrpacct =
                    MAssetGroupAcct.forA_Asset_GroupId(
                            asset.getAssetGroupId(), postingType);
        }
        UseLifeImpl.copyValues(this, (PO)assetgrpacct);

        //
        // Set Date Acct from Asset
        Timestamp dateAcct = asset.getDateAcct();
        if (dateAcct != null) {
            dateAcct = TimeUtil.addMonths(dateAcct, 1);
            setDateAcct(dateAcct);
        }
        //
        // Set UseLife values from asset (if any)
        if (asset.getUseLifeMonths() > 0) {
            UseLifeImpl.get(this, false).setUseLifeMonths(asset.getUseLifeMonths());
        }
        if (asset.getUseLifeMonthsFiscal() > 0) {
            UseLifeImpl.get(this, true).setUseLifeMonths(asset.getUseLifeMonthsFiscal());
        }
        //
        dump();
    }

    public static Collection<DepreciationWorkfile> forA_AssetId(
            int asset_id) {
        return new Query<DepreciationWorkfile>(
                DepreciationWorkfile.Table_Name,
                DepreciationWorkfile.COLUMNNAME_A_Asset_ID + "=?"
        )
                .setParameters(asset_id)
                .list();
    }

    /**
     * Get/load workfile from cache (if trxName is null)
     *
     * @param A_Asset_ID
     * @param postingType
     * @return workfile
     */
    public static DepreciationWorkfile get(
            int A_Asset_ID, String postingType) {
        if (A_Asset_ID <= 0 || postingType == null) {
            return null;
        }

        final MultiKey key = new MultiKey(A_Asset_ID, postingType);
        DepreciationWorkfile wk = s_cacheAsset.get(key);
        if (wk != null) return wk;
        final String whereClause =
            DepreciationWorkfile.COLUMNNAME_A_Asset_ID
                        + "=?"
                        + " AND "
                        + DepreciationWorkfile.COLUMNNAME_PostingType
                        + "=? ";
        wk =
                new Query<DepreciationWorkfile>(DepreciationWorkfile.Table_Name, whereClause)
                        .setParameters(A_Asset_ID, postingType)
                        .firstOnly();

        if (wk != null) {
            s_cacheAsset.put(key, wk);
        }
        return wk;
    }

    /**
     * Update Founding Mode related fields
     *
     * @param m                 model
     * @param changedColumnName column name that has been changed
     */
    public static void updateFinantare(SetGetModel m, String changedColumnName) {
        // Own contribution:
        BigDecimal valCofinantare =
                SetGetUtil.getAttrValueAsBigDecimal(
                        m, DepreciationWorkfile.COLUMNNAME_A_Valoare_Cofinantare);
        // Asset Value:
        BigDecimal assetCost =
                SetGetUtil.getAttrValueAsBigDecimal(m, DepreciationWorkfile.COLUMNNAME_A_Asset_Cost);
        // Third value:
        BigDecimal valTert =
                SetGetUtil.getAttrValueAsBigDecimal(
                        m, DepreciationWorkfile.COLUMNNAME_A_Valoare_Tert);

        // Calculate values
        if (valCofinantare.signum() == 0 && valTert.signum() == 0) {
            // Values ​​have never been set, so put everything on their own financing
            valCofinantare = assetCost;
            valTert = Env.ZERO;
        } else if (DepreciationWorkfile.COLUMNNAME_A_Asset_Cost.equals(changedColumnName)) {
            valCofinantare = assetCost.subtract(valTert);
        } else if (DepreciationWorkfile.COLUMNNAME_A_Valoare_Cofinantare.equals(
                changedColumnName)) {
            valTert = assetCost.subtract(valCofinantare);
        } else if (DepreciationWorkfile.COLUMNNAME_A_Valoare_Tert.equals(changedColumnName)) {
            valCofinantare = assetCost.subtract(valTert);
        } else {
            valTert = assetCost.subtract(valCofinantare);
        }

        // Financing Type
        String tipFinantare = X_A_Depreciation_Workfile.A_TIP_FINANTARE_Cofinantare;
        if (valTert.signum() == 0) {
            tipFinantare = X_A_Depreciation_Workfile.A_TIP_FINANTARE_Proprie;
        } else if (valCofinantare.signum() == 0) {
            tipFinantare = X_A_Depreciation_Workfile.A_TIP_FINANTARE_Terti;
        }
        //
        // Set values
        m.setAttrValue(DepreciationWorkfile.COLUMNNAME_A_Tip_Finantare, tipFinantare);
        m.setAttrValue(DepreciationWorkfile.COLUMNNAME_A_Valoare_Cofinantare, valCofinantare);
        m.setAttrValue(DepreciationWorkfile.COLUMNNAME_A_Valoare_Tert, valTert);
        //
        // If the method is invoked for a persistent object when reset mode of financing
        if (X_A_Depreciation_Workfile.A_TIP_FINANTARE_Proprie.equals(tipFinantare)
                && SetGetUtil.isPersistent(m)) {
            m.setAttrValue(DepreciationWorkfile.COLUMNNAME_A_FundingMode_ID, null);
        }
    }

    /**
     * Get Asset
     */
    public MAsset getAsset() {
        return getAsset(false);
    }

    /**
     * Get asset using this trxName
     *
     * @param requery requery asset
     * @return parent asset
     */
    public MAsset getAsset(boolean requery) {
        if (m_asset == null || requery) {
            m_asset = MAsset.get(getAssetId());
        }
        if (m_asset.getId() <= 0) {
            m_asset = null;
        }
        return m_asset;
    }

    /**
     * Gets asset's service date (commissioning)
     *
     * @return asset service date
     */
    public Timestamp getAssetServiceDate() {
        MAsset asset = getAsset();
        if (asset == null) {
            return null;
        }
        return asset.getAssetServiceDate();
    }

    /**
     * Gets asset's class
     *
     * @return asset class id
     */
  /* commented out by @win
  public int getA_Asset_ClassId()
  {
  	MAsset asset = getAsset();
  	if (asset == null) {
  		return 0;
  	}
  	return asset.getA_Asset_ClassId();
  }
  */
    // end comment by @win
    protected boolean beforeSave(boolean newRecord) {
        if (log.isLoggable(Level.INFO)) log.info("Entering: trxName=" + null);

        // copy UseLife to A_Life
        if (newRecord) { // @win: should only update only if newrecord
            setLifePeriod(getUseLifeMonths());
            setAssetLifeYears(getUseLifeYears());
            setLifePeriodFiscal(getUseLifeMonthsFiscal());
            setAssetLifeYearsFiscal(getUseLifeYearsFiscal());
        }

        // If it is fully amortized, change the state's FA
        MAsset asset = getAsset(true);
        if (MAsset.A_ASSET_STATUS_Activated.equals(asset.getAssetStatus()) && isFullyDepreciated()) {
            asset.changeStatus(MAsset.A_ASSET_STATUS_Depreciated, null);
            asset.saveEx();
        }

        // Fix DateAcct
        if (isValueChanged(DepreciationWorkfile.COLUMNNAME_DateAcct)) {
            setDateAcct(TimeUtil.getMonthLastDay(getDateAcct()));
        }

        //
        BigDecimal cost = getAssetCost();
        BigDecimal accumDep_C = getAccumulatedDepreciation();
        setAssetRemaining(cost.subtract(accumDep_C));
        BigDecimal accumDep_F = getAccumulatedDepreciationFiscal();
        setAssetRemainingFiscal(cost.subtract(accumDep_F));

        // Financing
        {
            String mainColumnName = null;
            if (newRecord || isValueChanged(DepreciationWorkfile.COLUMNNAME_A_Asset_Cost)) {
                mainColumnName = DepreciationWorkfile.COLUMNNAME_A_Asset_Cost;
            } else if (isValueChanged(DepreciationWorkfile.COLUMNNAME_A_Valoare_Cofinantare)) {
                mainColumnName = DepreciationWorkfile.COLUMNNAME_A_Valoare_Cofinantare;
            } else if (isValueChanged(DepreciationWorkfile.COLUMNNAME_A_Valoare_Tert)) {
                mainColumnName = DepreciationWorkfile.COLUMNNAME_A_Valoare_Tert;
            }
            updateFinantare(this, mainColumnName);
        }

        if (log.isLoggable(Level.INFO))
            log.info("Leaving: trxName=" + null + " [RETURN TRUE]");
        return true;
    } //	beforeSave

    /**
     * Asset is fully depreciated
     *
     * <ul>
     * <li>If PostingType != ACTUAL then return false
     * <li>Do not check your current asset
     * </ul>
     *
     * @return true if the asset is fully depreciated, false otherwise
     */
    public boolean isFullyDepreciated() {
        if (!getPostingType().equals(X_A_Depreciation_Workfile.POSTINGTYPE_Actual)) {
            return false;
        }

        // check if is fully depreciated
        BigDecimal remainingAmt_C = getRemainingCost(null, false);
        BigDecimal remainingAmt_F = getRemainingCost(null, true);
        if (remainingAmt_C.signum() == 0 && remainingAmt_F.signum() == 0) {
            // if A_Asset_Cost is 0 have a voided addition, in this case asset is not full depreciated
            return getAssetCost().signum() != 0;//
        }

        return false;
    }

    /**
     * Get Asset Accounting for this workfile
     *
     * @return asset accounting model
     */
    public AssetAccounting getAssetAccounting(Timestamp dateAcct) {
        return MAssetAcct.forA_AssetId(getAssetId(), getPostingType(), dateAcct);
    }

    /**
     * Returns the current cost of FAs. It is calculated as the difference between acquisition value
     * and the value that you (A_Salvage_Value)
     *
     * @return the current cost of FAs
     */
    public BigDecimal getActualCost() {
        return getActualCost(getAssetCost());
    }

    /**
     *
     */
    public BigDecimal getActualCost(BigDecimal assetCost) {
        return assetCost.subtract(getSalvageValue());
    }

    /**
     * Adjust Accumulated depreciation
     *
     * @param amt
     * @param amt_F
     * @param reset
     * @return
     */
    public boolean adjustAccumulatedDepreciation(BigDecimal amt, BigDecimal amt_F, boolean reset) {
        if (amt == null) {
            amt = Env.ZERO;
        }
        if (amt_F == null) {
            amt_F = Env.ZERO;
        }
        setAccumulatedDepreciation(amt.add(reset ? Env.ZERO : getAccumulatedDepreciation()));
        setAccumulatedDepreciationFiscal(amt_F.add(reset ? Env.ZERO : getAccumulatedDepreciationFiscal()));
        return true;
    }

    /**
     *
     */
    public int getUseLifeMonths(boolean fiscal) {
        return fiscal ? getUseLifeMonthsFiscal() : getUseLifeMonths();
    }

    /**
     *
     */
    public BigDecimal getAccumulatedDepreciation(boolean fiscal) {
        return fiscal ? getAccumulatedDepreciationFiscal() : getAccumulatedDepreciation();
    }

    /**
     * Returns the residual (remaining) value
     */
    public BigDecimal getRemainingCost(BigDecimal accumAmt, boolean fiscal) {
        BigDecimal cost = getActualCost();
        if (accumAmt == null) {
            accumAmt = getAccumulatedDepreciation(fiscal);
        }
        return cost.subtract(accumAmt);
    }

    /**
     * Returns the residual (remaining) value
     */
    public BigDecimal getRemainingCost(BigDecimal accumAmt) {
        return getRemainingCost(accumAmt, isFiscal());
    }

    /**
     *
     */
    public int getRemainingPeriods(int A_Current_Period, MDepreciation method) {
        int useLifePeriods = getUseLifeMonths(isFiscal());
        if (method != null) {
            useLifePeriods += method.getFixMonthOffset();
        }
        int currentPeriod = (A_Current_Period >= 0 ? A_Current_Period : getCurrentPeriod());
        return useLifePeriods - currentPeriod;
    }

    /**
     *
     */
    public int getRemainingPeriods(int A_Current_Period) {
        return getRemainingPeriods(A_Current_Period, null);
    }

    /**
     *
     */
    public boolean isFiscal() {
        return m_isFiscal;
    }

    /**
     * Set fiscal flag (temporary - is not modifing the workfile)
     *
     * @param fiscal
     */
    public void setFiscal(boolean fiscal) {
        m_isFiscal = fiscal;
    }

    /**
     * Increment the current period (A_Current_Period) 1, and a month DateAcct
     */
    public void incrementCurrentPeriod() {
        int old_period = getCurrentPeriod();
        Timestamp old_date = getDateAcct();
        int new_period = old_period + 1;
        Timestamp new_date = TimeUtil.addMonths(getDateAcct(), 1);
        setCurrentPeriod(new_period);
        setDateAcct(new_date);
        //
        if (log.isLoggable(Level.FINE))
            log.fine(
                    "(A_Current_Period, DateAcct)=("
                            + old_period
                            + ", "
                            + old_date
                            + ")->("
                            + new_period
                            + ", "
                            + new_date
                            + ")");
    }

    /**
     * Set A Current Period (and Data Act) processed just after the last expense. Do not save.
     */
    public void setCurrentPeriod() {
        String whereClause =
                MDepreciationExp.COLUMNNAME_A_Asset_ID
                        + "=?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_PostingType
                        + "=?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_Processed
                        + "=? AND IsActive=?";
        //
        DepreciationExpense depexp =
                new Query<DepreciationExpense>(MDepreciationExp.Table_Name, whereClause)
                        .setParameters(getAssetId(), getPostingType(), true, true)
                        .setOrderBy(
                                MDepreciationExp.COLUMNNAME_A_Period
                                        + " DESC"
                                        + ","
                                        + MDepreciationExp.COLUMNNAME_DateAcct
                                        + " DESC")
                        .first();
        if (depexp != null) {
            setCurrentPeriod(depexp.getPeriod());
            setDateAcct(depexp.getDateAcct());
            incrementCurrentPeriod();
        } else {
            log.info("There are no records from which to infer its");
        }
    }

    /**
     * Truncate not processed depreciation entries. IS NOT modifying workfile.
     */
    public void truncDepreciation() {


        int A_Current_Period = getCurrentPeriod();
        final String sql =
                "DELETE FROM "
                        + MDepreciationExp.Table_Name
                        + " WHERE "
                        + MDepreciationExp.COLUMNNAME_Processed
                        + "=?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_A_Period
                        + ">=?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_A_Asset_ID
                        + "=?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_PostingType
                        + "=?";
        Object[] params = new Object[]{false, A_Current_Period, getAssetId(), getPostingType()};
        int no = executeUpdateEx(sql, params);
        if (log.isLoggable(Level.FINE)) log.fine("sql=" + sql + "\nDeleted #" + no);
    } //	truncDepreciation

    public boolean setAttrValue(String ColumnName, Object value) {
        int index = getColumnIndex(ColumnName);
        if (index < 0) return false;
        return setValueNoCheck(ColumnName, value);
    }

    public Object getAttrValue(String ColumnName) {
        int index = getColumnIndex(ColumnName);
        if (index < 0) return null;
        return getValue(index);
    }

    public boolean isAttrValueChanged(String ColumnName) {
        int index = getColumnIndex(ColumnName);
        if (index < 0) return false;
        return isValueChanged(index);
    }

} //	MDepreciationWorkfile
