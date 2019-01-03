package org.compiere.invoicing;

import org.compiere.model.I_A_Asset_Group_Acct;
import org.compiere.model.UseLife;
import org.compiere.orm.PO;
import org.compiere.orm.Query;
import org.compiere.product.MAssetGroup;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * Asset Group Accounting Model
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MAssetGroupAcct extends X_A_Asset_Group_Acct implements UseLife {

  /** */
  private static final long serialVersionUID = -3458020679308192943L;

  /** Get Asset Group Accountings for given group */
  public static List<MAssetGroupAcct> forA_Asset_Group_ID(Properties ctx, int A_Asset_Group_ID) {
    return new Query(
            ctx,
            I_A_Asset_Group_Acct.Table_Name,
            I_A_Asset_Group_Acct.COLUMNNAME_A_Asset_Group_ID + "=?",
            null)
        .setParameters(new Object[] {A_Asset_Group_ID})
        .list();
  }

  /** Get Asset Group Accountings for given group */
  public static MAssetGroupAcct forA_Asset_Group_ID(
      Properties ctx, int A_Asset_Group_ID, String postingType) {
    final String whereClause =
        I_A_Asset_Group_Acct.COLUMNNAME_A_Asset_Group_ID
            + "=? AND "
            + I_A_Asset_Group_Acct.COLUMNNAME_PostingType
            + "=?";
    return new Query(ctx, I_A_Asset_Group_Acct.Table_Name, whereClause, null)
        .setParameters(new Object[] {A_Asset_Group_ID, postingType})
        .firstOnly();
  }

  /**
   * Default ConstructorX_A_Asset_Group_Acct
   *
   * @param ctx context
   * @param X_A_Asset_Group_Acct_ID id
   */
  public MAssetGroupAcct(Properties ctx, int X_A_Asset_Group_Acct_ID, String trxName) {
    super(ctx, X_A_Asset_Group_Acct_ID, trxName);
  } //	MAssetGroupAcct

  /**
   * Load Constructor
   *
   * @param ctx context
   * @param rs result set
   */
  public MAssetGroupAcct(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  } //	MAssetGroupAcct

  /** Asset Group */
  private MAssetGroup m_parent = null;

  /** Get Asset Group */
  public MAssetGroup getParent() {
    if (m_parent == null) {
      int A_Asset_Group_ID = getA_Asset_Group_ID();
      if (is_new()) {
        m_parent = new MAssetGroup(getCtx(), A_Asset_Group_ID, null);
      } else {
        m_parent = MAssetGroup.get(getCtx(), A_Asset_Group_ID);
      }
    }
    return m_parent;
  }

  /* commented by @win
  public int getA_Asset_Class_ID()
  {
  	return getParent().getA_Asset_Class_ID();
  }
  */

  public Timestamp getAssetServiceDate() {
    return null;
  }

  /**
   * Clone this object, using specified group
   *
   * @param grp the new asset group
   * @return new asset group accounting (NOTE: it's not saved)
   */
  public MAssetGroupAcct copy(MAssetGroup grp) {
    MAssetGroupAcct newAcct = new MAssetGroupAcct(grp.getCtx(), 0, null);
    PO.copyValues(this, newAcct, grp. getClientId(), grp. getOrgId());
    newAcct.setA_Asset_Group_ID(grp.getA_Asset_Group_ID());
    return newAcct;
  }

  public boolean beforeSave(boolean newRecord) {
    if (!UseLifeImpl.get(this).validate()) {
      return false;
    }
    return true;
  }

  public boolean set_AttrValue(String ColumnName, Object value) {
    int index = get_ColumnIndex(ColumnName);
    if (index < 0) return false;
    return set_ValueNoCheck(ColumnName, value);
  }

  public Object get_AttrValue(String ColumnName) {
    int index = get_ColumnIndex(ColumnName);
    if (index < 0) return null;
    return get_Value(index);
  }

  public boolean is_AttrValueChanged(String ColumnName) {
    int index = get_ColumnIndex(ColumnName);
    if (index < 0) return false;
    return is_ValueChanged(index);
  }
} //	MAssetGroupAcct
