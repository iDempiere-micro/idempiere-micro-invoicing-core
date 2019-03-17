package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_A_Asset_Group_Acct;
import org.compiere.model.UseLife;
import org.compiere.orm.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

/**
 * Asset Group Accounting Model
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MAssetGroupAcct extends X_A_Asset_Group_Acct implements UseLife {

    /**
     *
     */
    private static final long serialVersionUID = -3458020679308192943L;

    /**
     * Default ConstructorX_A_Asset_Group_Acct
     *
     * @param ctx                     context
     * @param X_A_Asset_Group_Acct_ID id
     */
    public MAssetGroupAcct(Properties ctx, int X_A_Asset_Group_Acct_ID) {
        super(ctx, X_A_Asset_Group_Acct_ID);
    } //	MAssetGroupAcct

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MAssetGroupAcct(Properties ctx, Row row) {
        super(ctx, row);
    } //	MAssetGroupAcct

    /**
     * Get Asset Group Accountings for given group
     */
    public static List<MAssetGroupAcct> forA_Asset_GroupId(Properties ctx, int A_Asset_Group_ID) {
        return new Query(
                ctx,
                I_A_Asset_Group_Acct.Table_Name,
                I_A_Asset_Group_Acct.COLUMNNAME_A_Asset_Group_ID + "=?"
        )
                .setParameters(A_Asset_Group_ID)
                .list();
    }

    /**
     * Get Asset Group Accountings for given group
     */
    public static MAssetGroupAcct forA_Asset_GroupId(
            Properties ctx, int A_Asset_Group_ID, String postingType) {
        final String whereClause =
                I_A_Asset_Group_Acct.COLUMNNAME_A_Asset_Group_ID
                        + "=? AND "
                        + I_A_Asset_Group_Acct.COLUMNNAME_PostingType
                        + "=?";
        return new Query(ctx, I_A_Asset_Group_Acct.Table_Name, whereClause)
                .setParameters(A_Asset_Group_ID, postingType)
                .firstOnly();
    }

  /* commented by @win
  public int getA_Asset_ClassId()
  {
  	return getParent().getA_Asset_ClassId();
  }
  */

    public Timestamp getAssetServiceDate() {
        return null;
    }

    public boolean beforeSave(boolean newRecord) {
        return UseLifeImpl.get(this).validate();
    }

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
} //	MAssetGroupAcct
