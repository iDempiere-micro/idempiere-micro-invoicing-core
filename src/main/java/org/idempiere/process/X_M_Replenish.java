package org.idempiere.process;

import org.compiere.model.I_M_Replenish;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

public class X_M_Replenish extends PO implements I_M_Replenish, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_M_Replenish(Properties ctx, int M_Replenish_ID, String trxName) {
    super(ctx, M_Replenish_ID, trxName);
    /**
     * if (M_Replenish_ID == 0) { setLevel_Max (Env.ZERO); setLevel_Min (Env.ZERO); setM_Product_ID
     * (0); setM_Warehouse_ID (0); setReplenishType (null); }
     */
  }

  /** Load Constructor */
  public X_M_Replenish(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }

  /**
   * AccessLevel
   *
   * @return 3 - Client - Org
   */
  protected int getAccessLevel() {
    return accessLevel.intValue();
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("X_M_Replenish[").append(getId()).append("]");
    return sb.toString();
  }

  /**
   * Set Maximum Level.
   *
   * @param Level_Max Maximum Inventory level for this product
   */
  public void setLevel_Max(BigDecimal Level_Max) {
    set_Value(COLUMNNAME_Level_Max, Level_Max);
  }

  /**
   * Get Maximum Level.
   *
   * @return Maximum Inventory level for this product
   */
  public BigDecimal getLevel_Max() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Level_Max);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Minimum Level.
   *
   * @param Level_Min Minimum Inventory level for this product
   */
  public void setLevel_Min(BigDecimal Level_Min) {
    set_Value(COLUMNNAME_Level_Min, Level_Min);
  }

  /**
   * Get Minimum Level.
   *
   * @return Minimum Inventory level for this product
   */
  public BigDecimal getLevel_Min() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Level_Min);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  public org.compiere.model.I_M_Locator getM_Locator() throws RuntimeException {
    return (org.compiere.model.I_M_Locator)
        MTable.get(getCtx(), org.compiere.model.I_M_Locator.Table_Name)
            .getPO(getM_Locator_ID(), null);
  }

  /**
   * Set Locator.
   *
   * @param M_Locator_ID Warehouse Locator
   */
  public void setM_Locator_ID(int M_Locator_ID) {
    if (M_Locator_ID < 1) set_Value(COLUMNNAME_M_Locator_ID, null);
    else set_Value(COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
  }

  /**
   * Get Locator.
   *
   * @return Warehouse Locator
   */
  public int getM_Locator_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_Locator_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_M_Product getM_Product() throws RuntimeException {
    return (org.compiere.model.I_M_Product)
        MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
            .getPO(getM_Product_ID(), null);
  }

  /**
   * Set Product.
   *
   * @param M_Product_ID Product, Service, Item
   */
  public void setM_Product_ID(int M_Product_ID) {
    if (M_Product_ID < 1) set_ValueNoCheck(COLUMNNAME_M_Product_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
  }

  /**
   * Get Product.
   *
   * @return Product, Service, Item
   */
  public int getM_Product_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_Product_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set M_Replenish_UU.
   *
   * @param M_Replenish_UU M_Replenish_UU
   */
  public void setM_Replenish_UU(String M_Replenish_UU) {
    set_Value(COLUMNNAME_M_Replenish_UU, M_Replenish_UU);
  }

  /**
   * Get M_Replenish_UU.
   *
   * @return M_Replenish_UU
   */
  public String getM_Replenish_UU() {
    return (String) get_Value(COLUMNNAME_M_Replenish_UU);
  }

  public org.compiere.model.I_M_Warehouse getM_Warehouse() throws RuntimeException {
    return (org.compiere.model.I_M_Warehouse)
        MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_Name)
            .getPO(getM_Warehouse_ID(), null);
  }

  /**
   * Set Warehouse.
   *
   * @param M_Warehouse_ID Storage Warehouse and Service Point
   */
  public void setM_Warehouse_ID(int M_Warehouse_ID) {
    if (M_Warehouse_ID < 1) set_ValueNoCheck(COLUMNNAME_M_Warehouse_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_Warehouse_ID, Integer.valueOf(M_Warehouse_ID));
  }

  /**
   * Get Warehouse.
   *
   * @return Storage Warehouse and Service Point
   */
  public int getM_Warehouse_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_Warehouse_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_M_Warehouse getM_WarehouseSource() throws RuntimeException {
    return (org.compiere.model.I_M_Warehouse)
        MTable.get(getCtx(), org.compiere.model.I_M_Warehouse.Table_Name)
            .getPO(getM_WarehouseSource_ID(), null);
  }

  /**
   * Set Source Warehouse.
   *
   * @param M_WarehouseSource_ID Optional Warehouse to replenish from
   */
  public void setM_WarehouseSource_ID(int M_WarehouseSource_ID) {
    if (M_WarehouseSource_ID < 1) set_Value(COLUMNNAME_M_WarehouseSource_ID, null);
    else set_Value(COLUMNNAME_M_WarehouseSource_ID, Integer.valueOf(M_WarehouseSource_ID));
  }

  /**
   * Get Source Warehouse.
   *
   * @return Optional Warehouse to replenish from
   */
  public int getM_WarehouseSource_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_WarehouseSource_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Qty Batch Size.
   *
   * @param QtyBatchSize Qty Batch Size
   */
  public void setQtyBatchSize(BigDecimal QtyBatchSize) {
    set_Value(COLUMNNAME_QtyBatchSize, QtyBatchSize);
  }

  /**
   * Get Qty Batch Size.
   *
   * @return Qty Batch Size
   */
  public BigDecimal getQtyBatchSize() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_QtyBatchSize);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /** ReplenishType AD_Reference_ID=164 */
  public static final int REPLENISHTYPE_AD_Reference_ID = 164;
  /** Maintain Maximum Level = 2 */
  public static final String REPLENISHTYPE_MaintainMaximumLevel = "2";
  /** Manual = 0 */
  public static final String REPLENISHTYPE_Manual = "0";
  /** Reorder below Minimum Level = 1 */
  public static final String REPLENISHTYPE_ReorderBelowMinimumLevel = "1";
  /** Custom = 9 */
  public static final String REPLENISHTYPE_Custom = "9";
  /**
   * Set Replenish Type.
   *
   * @param ReplenishType Method for re-ordering a product
   */
  public void setReplenishType(String ReplenishType) {

    set_Value(COLUMNNAME_ReplenishType, ReplenishType);
  }

  /**
   * Get Replenish Type.
   *
   * @return Method for re-ordering a product
   */
  public String getReplenishType() {
    return (String) get_Value(COLUMNNAME_ReplenishType);
  }

  @Override
  public int getTableId() {
    return Table_ID;
  }
}
