package org.compiere.invoicing;

import org.compiere.accounting.MAcctSchema;
import org.compiere.model.I_A_Asset_Acct;
import org.compiere.orm.Query;
import org.compiere.orm.SetGetUtil;
import org.compiere.orm.TimeUtil;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Asset Acct Model
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MAssetAcct extends X_A_Asset_Acct {
  /** */
  private static final long serialVersionUID = -3919172418904053712L;

  /** DO NOT USE DIRECTLY */
  public MAssetAcct(Properties ctx, int X_A_Asset_Acct_ID) {
    super(ctx, X_A_Asset_Acct_ID);
    if (X_A_Asset_Acct_ID == 0) {
      setA_Salvage_Value(Env.ZERO);
    }
  }

  public MAssetAcct(Properties ctx, ResultSet rs) {
    super(ctx, rs);
  }

  /** Static Cache: A_Asset_Acct_ID -> MAssetAcct */
  private static CCache<Integer, MAssetAcct> s_cache =
      new CCache<Integer, MAssetAcct>(I_A_Asset_Acct.Table_Name, 5);

    /**
   * Get asset accounting.
   *
   * @param ctx context
   * @param A_Asset_ID asset
   * @param postingType Posting type
   * @param dateAcct check ValidFrom
   * @return asset accounting for the given asset
   */
  public static MAssetAcct forA_Asset_ID(
      Properties ctx, int A_Asset_ID, String postingType, Timestamp dateAcct) {
    //
    ArrayList<Object> params = new ArrayList<Object>();
    StringBuilder whereClause =
        new StringBuilder(
            I_A_Asset_Acct.COLUMNNAME_A_Asset_ID
                + "=? AND "
                + I_A_Asset_Acct.COLUMNNAME_PostingType
                + "=?");
    params.add(A_Asset_ID);
    params.add(postingType);
    if (dateAcct != null) {
      whereClause.append(" AND " + I_A_Asset_Acct.COLUMNNAME_ValidFrom).append("<=?");
      params.add(dateAcct);
    }
    MAssetAcct acct =
        new Query(ctx, I_A_Asset_Acct.Table_Name, whereClause.toString())
            .setParameters(params)
            .setOrderBy(I_A_Asset_Acct.COLUMNNAME_ValidFrom + " DESC NULLS LAST")
            .first();
    addToCache(acct);
    return acct;
  }

  private static void addToCache(MAssetAcct acct) {
    if (acct == null || acct.getId() <= 0) {
      return;
    }
    s_cache.put(acct.getId(), acct);
  }

  /**
   * Create new asset accounting from asset group accounting
   *
   * @param asset asset
   * @param assetgrpacct asset group accounting
   */
  public MAssetAcct(MAsset asset, MAssetGroupAcct assetgrpacct) {
    this(assetgrpacct.getCtx(), 0);

    SetGetUtil.copyValues(this, assetgrpacct, null, null);
    setA_Asset_ID(asset.getA_Asset_ID());
    if (asset.getA_Depreciation_ID() > 0) {
      setA_Depreciation_ID(asset.getA_Depreciation_ID());
    }
    if (asset.getA_Depreciation_F_ID() > 0) {
      setA_Depreciation_F_ID(asset.getA_Depreciation_F_ID());
    }
    setA_Period_Start(1);
    setA_Period_End(asset.getUseLifeMonths());
    // ~ setProcessing(false);
    dump();
  }

  /** */
  public BigDecimal getA_Depreciation_Variable_Perc(boolean fiscal) {
    return fiscal ? getA_Depreciation_Variable_Perc_F() : getA_Depreciation_Variable_Perc();
  }

  public MAcctSchema getC_AcctSchema() {
    return MAcctSchema.get(getCtx(), getC_AcctSchema_ID());
  }

    @Override
  protected boolean beforeSave(boolean newRecord) {
    if (getValidFrom() == null && newRecord) {
      setValidFrom(TimeUtil.getDay(1970, 01, 01)); // FIXME
    }
    return true;
  }
} //	class MAssetAcct
