package org.idempiere.process;

import org.compiere.model.I_M_MovementLineConfirm;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.KeyNamePair;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

public class X_M_MovementLineConfirm extends PO implements I_M_MovementLineConfirm, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_M_MovementLineConfirm(Properties ctx, int M_MovementLineConfirm_ID, String trxName) {
    super(ctx, M_MovementLineConfirm_ID, trxName);
    /**
     * if (M_MovementLineConfirm_ID == 0) { setConfirmedQty (Env.ZERO); setDifferenceQty (Env.ZERO);
     * setM_MovementConfirm_ID (0); setM_MovementLineConfirm_ID (0); setM_MovementLine_ID (0);
     * setProcessed (false); setScrappedQty (Env.ZERO); setTargetQty (Env.ZERO); }
     */
  }

  /** Load Constructor */
  public X_M_MovementLineConfirm(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }

  /**
   * AccessLevel
   *
   * @return 1 - Org
   */
  protected int getAccessLevel() {
    return accessLevel.intValue();
  }

  @Override
  public int getTableId() {
    return Table_ID;
  }


  public String toString() {
    StringBuffer sb = new StringBuffer("X_M_MovementLineConfirm[").append(getId()).append("]");
    return sb.toString();
  }

  /**
   * Set Confirmed Quantity.
   *
   * @param ConfirmedQty Confirmation of a received quantity
   */
  public void setConfirmedQty(BigDecimal ConfirmedQty) {
    set_Value(COLUMNNAME_ConfirmedQty, ConfirmedQty);
  }

  /**
   * Get Confirmed Quantity.
   *
   * @return Confirmation of a received quantity
   */
  public BigDecimal getConfirmedQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ConfirmedQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Description.
   *
   * @param Description Optional short description of the record
   */
  public void setDescription(String Description) {
    set_Value(COLUMNNAME_Description, Description);
  }

  /**
   * Get Description.
   *
   * @return Optional short description of the record
   */
  public String getDescription() {
    return (String) get_Value(COLUMNNAME_Description);
  }

  /**
   * Set Difference.
   *
   * @param DifferenceQty Difference Quantity
   */
  public void setDifferenceQty(BigDecimal DifferenceQty) {
    set_Value(COLUMNNAME_DifferenceQty, DifferenceQty);
  }

  /**
   * Get Difference.
   *
   * @return Difference Quantity
   */
  public BigDecimal getDifferenceQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_DifferenceQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  public org.compiere.model.I_M_InventoryLine getM_InventoryLine() throws RuntimeException {
    return (org.compiere.model.I_M_InventoryLine)
        MTable.get(getCtx(), org.compiere.model.I_M_InventoryLine.Table_Name)
            .getPO(getM_InventoryLine_ID(), null);
  }

  /**
   * Set Phys.Inventory Line.
   *
   * @param M_InventoryLine_ID Unique line in an Inventory document
   */
  public void setM_InventoryLine_ID(int M_InventoryLine_ID) {
    if (M_InventoryLine_ID < 1) set_Value(COLUMNNAME_M_InventoryLine_ID, null);
    else set_Value(COLUMNNAME_M_InventoryLine_ID, Integer.valueOf(M_InventoryLine_ID));
  }

  /**
   * Get Phys.Inventory Line.
   *
   * @return Unique line in an Inventory document
   */
  public int getM_InventoryLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_InventoryLine_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_M_MovementConfirm getM_MovementConfirm() throws RuntimeException {
    return (org.compiere.model.I_M_MovementConfirm)
        MTable.get(getCtx(), org.compiere.model.I_M_MovementConfirm.Table_Name)
            .getPO(getM_MovementConfirm_ID(), null);
  }

  /**
   * Set Move Confirm.
   *
   * @param M_MovementConfirm_ID Inventory Move Confirmation
   */
  public void setM_MovementConfirm_ID(int M_MovementConfirm_ID) {
    if (M_MovementConfirm_ID < 1) set_ValueNoCheck(COLUMNNAME_M_MovementConfirm_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_MovementConfirm_ID, Integer.valueOf(M_MovementConfirm_ID));
  }

  /**
   * Get Move Confirm.
   *
   * @return Inventory Move Confirmation
   */
  public int getM_MovementConfirm_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_MovementConfirm_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Get Record ID/ColumnName
   *
   * @return ID/ColumnName pair
   */
  public KeyNamePair getKeyNamePair() {
    return new KeyNamePair(getId(), String.valueOf(getM_MovementConfirm_ID()));
  }

  /**
   * Set Move Line Confirm.
   *
   * @param M_MovementLineConfirm_ID Inventory Move Line Confirmation
   */
  public void setM_MovementLineConfirm_ID(int M_MovementLineConfirm_ID) {
    if (M_MovementLineConfirm_ID < 1) set_ValueNoCheck(COLUMNNAME_M_MovementLineConfirm_ID, null);
    else
      set_ValueNoCheck(
          COLUMNNAME_M_MovementLineConfirm_ID, Integer.valueOf(M_MovementLineConfirm_ID));
  }

  /**
   * Get Move Line Confirm.
   *
   * @return Inventory Move Line Confirmation
   */
  public int getM_MovementLineConfirm_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_MovementLineConfirm_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set M_MovementLineConfirm_UU.
   *
   * @param M_MovementLineConfirm_UU M_MovementLineConfirm_UU
   */
  public void setM_MovementLineConfirm_UU(String M_MovementLineConfirm_UU) {
    set_Value(COLUMNNAME_M_MovementLineConfirm_UU, M_MovementLineConfirm_UU);
  }

  /**
   * Get M_MovementLineConfirm_UU.
   *
   * @return M_MovementLineConfirm_UU
   */
  public String getM_MovementLineConfirm_UU() {
    return (String) get_Value(COLUMNNAME_M_MovementLineConfirm_UU);
  }

  public org.compiere.model.I_M_MovementLine getM_MovementLine() throws RuntimeException {
    return (org.compiere.model.I_M_MovementLine)
        MTable.get(getCtx(), org.compiere.model.I_M_MovementLine.Table_Name)
            .getPO(getM_MovementLine_ID(), null);
  }

  /**
   * Set Move Line.
   *
   * @param M_MovementLine_ID Inventory Move document Line
   */
  public void setM_MovementLine_ID(int M_MovementLine_ID) {
    if (M_MovementLine_ID < 1) set_Value(COLUMNNAME_M_MovementLine_ID, null);
    else set_Value(COLUMNNAME_M_MovementLine_ID, Integer.valueOf(M_MovementLine_ID));
  }

  /**
   * Get Move Line.
   *
   * @return Inventory Move document Line
   */
  public int getM_MovementLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_MovementLine_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Processed.
   *
   * @param Processed The document has been processed
   */
  public void setProcessed(boolean Processed) {
    set_Value(COLUMNNAME_Processed, Boolean.valueOf(Processed));
  }

  /**
   * Get Processed.
   *
   * @return The document has been processed
   */
  public boolean isProcessed() {
    Object oo = get_Value(COLUMNNAME_Processed);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
  }

  /**
   * Set Scrapped Quantity.
   *
   * @param ScrappedQty The Quantity scrapped due to QA issues
   */
  public void setScrappedQty(BigDecimal ScrappedQty) {
    set_Value(COLUMNNAME_ScrappedQty, ScrappedQty);
  }

  /**
   * Get Scrapped Quantity.
   *
   * @return The Quantity scrapped due to QA issues
   */
  public BigDecimal getScrappedQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ScrappedQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Target Quantity.
   *
   * @param TargetQty Target Movement Quantity
   */
  public void setTargetQty(BigDecimal TargetQty) {
    set_Value(COLUMNNAME_TargetQty, TargetQty);
  }

  /**
   * Get Target Quantity.
   *
   * @return Target Movement Quantity
   */
  public BigDecimal getTargetQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_TargetQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }
}