package org.compiere.production;

import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_Transaction;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.KeyNamePair;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Generated Model for M_Transaction
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_Transaction extends PO implements I_M_Transaction, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_M_Transaction(Properties ctx, int M_Transaction_ID, String trxName) {
    super(ctx, M_Transaction_ID, trxName);
    /**
     * if (M_Transaction_ID == 0) { setM_AttributeSetInstance_ID (0); setM_Locator_ID (0);
     * setMovementDate (new Timestamp( System.currentTimeMillis() )); setMovementQty (Env.ZERO);
     * setMovementType (null); setM_Product_ID (0); setM_Transaction_ID (0); }
     */
  }

  /** Load Constructor */
  public X_M_Transaction(Properties ctx, ResultSet rs, String trxName) {
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

  public String toString() {
    StringBuffer sb = new StringBuffer("X_M_Transaction[").append(getId()).append("]");
    return sb.toString();
  }

  public org.compiere.model.I_C_ProjectIssue getC_ProjectIssue() throws RuntimeException {
    return (org.compiere.model.I_C_ProjectIssue)
        MTable.get(getCtx(), org.compiere.model.I_C_ProjectIssue.Table_Name)
            .getPO(getC_ProjectIssue_ID(), null);
  }

  /**
   * Set Project Issue.
   *
   * @param C_ProjectIssue_ID Project Issues (Material, Labor)
   */
  public void setC_ProjectIssue_ID(int C_ProjectIssue_ID) {
    if (C_ProjectIssue_ID < 1) set_ValueNoCheck(COLUMNNAME_C_ProjectIssue_ID, null);
    else set_ValueNoCheck(COLUMNNAME_C_ProjectIssue_ID, Integer.valueOf(C_ProjectIssue_ID));
  }

  /**
   * Get Project Issue.
   *
   * @return Project Issues (Material, Labor)
   */
  public int getC_ProjectIssue_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_ProjectIssue_ID);
    if (ii == null) return 0;
    return ii;
  }

  public I_M_AttributeSetInstance getMAttributeSetInstance() throws RuntimeException {
    return (I_M_AttributeSetInstance)
        MTable.get(getCtx(), I_M_AttributeSetInstance.Table_Name)
            .getPO(getMAttributeSetInstance_ID(), null);
  }

  /**
   * Set Attribute Set Instance.
   *
   * @param M_AttributeSetInstance_ID Product Attribute Set Instance
   */
  public void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {
    if (M_AttributeSetInstance_ID < 0) set_ValueNoCheck(COLUMNNAME_M_AttributeSetInstance_ID, null);
    else
      set_ValueNoCheck(
          COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
  }

  /**
   * Get Attribute Set Instance.
   *
   * @return Product Attribute Set Instance
   */
  public int getMAttributeSetInstance_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_AttributeSetInstance_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_M_InOutLine getM_InOutLine() throws RuntimeException {
    return (org.compiere.model.I_M_InOutLine)
        MTable.get(getCtx(), org.compiere.model.I_M_InOutLine.Table_Name)
            .getPO(getM_InOutLine_ID(), null);
  }

  /**
   * Set Shipment/Receipt Line.
   *
   * @param M_InOutLine_ID Line on Shipment or Receipt document
   */
  public void setM_InOutLine_ID(int M_InOutLine_ID) {
    if (M_InOutLine_ID < 1) set_ValueNoCheck(COLUMNNAME_M_InOutLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
  }

  /**
   * Get Shipment/Receipt Line.
   *
   * @return Line on Shipment or Receipt document
   */
  public int getM_InOutLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_InOutLine_ID);
    if (ii == null) return 0;
    return ii;
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
    if (M_InventoryLine_ID < 1) set_ValueNoCheck(COLUMNNAME_M_InventoryLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_InventoryLine_ID, Integer.valueOf(M_InventoryLine_ID));
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

  public I_M_Locator getM_Locator() throws RuntimeException {
    return (I_M_Locator)
        MTable.get(getCtx(), I_M_Locator.Table_Name).getPO(getM_Locator_ID(), null);
  }

  /**
   * Set Locator.
   *
   * @param M_Locator_ID Warehouse Locator
   */
  public void setM_Locator_ID(int M_Locator_ID) {
    if (M_Locator_ID < 1) set_ValueNoCheck(COLUMNNAME_M_Locator_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
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
    if (M_MovementLine_ID < 1) set_ValueNoCheck(COLUMNNAME_M_MovementLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_MovementLine_ID, Integer.valueOf(M_MovementLine_ID));
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
   * Set Movement Date.
   *
   * @param MovementDate Date a product was moved in or out of inventory
   */
  public void setMovementDate(Timestamp MovementDate) {
    set_ValueNoCheck(COLUMNNAME_MovementDate, MovementDate);
  }

  /**
   * Get Movement Date.
   *
   * @return Date a product was moved in or out of inventory
   */
  public Timestamp getMovementDate() {
    return (Timestamp) get_Value(COLUMNNAME_MovementDate);
  }

  /**
   * Get Record ID/ColumnName
   *
   * @return ID/ColumnName pair
   */
  public KeyNamePair getKeyNamePair() {
    return new KeyNamePair(getId(), String.valueOf(getMovementDate()));
  }

  /**
   * Set Movement Quantity.
   *
   * @param MovementQty Quantity of a product moved.
   */
  public void setMovementQty(BigDecimal MovementQty) {
    set_ValueNoCheck(COLUMNNAME_MovementQty, MovementQty);
  }

  /**
   * Get Movement Quantity.
   *
   * @return Quantity of a product moved.
   */
  public BigDecimal getMovementQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_MovementQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /** MovementType AD_Reference_ID=189 */
  public static final int MOVEMENTTYPE_AD_Reference_ID = 189;
  /** Customer Shipment = C- */
  public static final String MOVEMENTTYPE_CustomerShipment = "C-";
  /** Customer Returns = C+ */
  public static final String MOVEMENTTYPE_CustomerReturns = "C+";
  /** Vendor Receipts = V+ */
  public static final String MOVEMENTTYPE_VendorReceipts = "V+";
  /** Vendor Returns = V- */
  public static final String MOVEMENTTYPE_VendorReturns = "V-";
  /** Inventory Out = I- */
  public static final String MOVEMENTTYPE_InventoryOut = "I-";
  /** Inventory In = I+ */
  public static final String MOVEMENTTYPE_InventoryIn = "I+";
  /** Movement From = M- */
  public static final String MOVEMENTTYPE_MovementFrom = "M-";
  /** Movement To = M+ */
  public static final String MOVEMENTTYPE_MovementTo = "M+";
  /** Production + = P+ */
  public static final String MOVEMENTTYPE_ProductionPlus = "P+";
  /** Production - = P- */
  public static final String MOVEMENTTYPE_Production_ = "P-";
  /** Work Order + = W+ */
  public static final String MOVEMENTTYPE_WorkOrderPlus = "W+";
  /** Work Order - = W- */
  public static final String MOVEMENTTYPE_WorkOrder_ = "W-";
  /**
   * Set Movement Type.
   *
   * @param MovementType Method of moving the inventory
   */
  public void setMovementType(String MovementType) {

    set_ValueNoCheck(COLUMNNAME_MovementType, MovementType);
  }

  /**
   * Get Movement Type.
   *
   * @return Method of moving the inventory
   */
  public String getMovementType() {
    return (String) get_Value(COLUMNNAME_MovementType);
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

  public org.compiere.model.I_M_ProductionLine getM_ProductionLine() throws RuntimeException {
    return (org.compiere.model.I_M_ProductionLine)
        MTable.get(getCtx(), org.compiere.model.I_M_ProductionLine.Table_Name)
            .getPO(getM_ProductionLine_ID(), null);
  }

  /**
   * Set Production Line.
   *
   * @param M_ProductionLine_ID Document Line representing a production
   */
  public void setM_ProductionLine_ID(int M_ProductionLine_ID) {
    if (M_ProductionLine_ID < 1) set_ValueNoCheck(COLUMNNAME_M_ProductionLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_ProductionLine_ID, Integer.valueOf(M_ProductionLine_ID));
  }

  /**
   * Get Production Line.
   *
   * @return Document Line representing a production
   */
  public int getM_ProductionLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_ProductionLine_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Inventory Transaction.
   *
   * @param M_Transaction_ID Inventory Transaction
   */
  public void setM_Transaction_ID(int M_Transaction_ID) {
    if (M_Transaction_ID < 1) set_ValueNoCheck(COLUMNNAME_M_Transaction_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_Transaction_ID, Integer.valueOf(M_Transaction_ID));
  }

  /**
   * Get Inventory Transaction.
   *
   * @return Inventory Transaction
   */
  public int getM_Transaction_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_Transaction_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set M_Transaction_UU.
   *
   * @param M_Transaction_UU M_Transaction_UU
   */
  public void setM_Transaction_UU(String M_Transaction_UU) {
    set_Value(COLUMNNAME_M_Transaction_UU, M_Transaction_UU);
  }

  /**
   * Get M_Transaction_UU.
   *
   * @return M_Transaction_UU
   */
  public String getM_Transaction_UU() {
    return (String) get_Value(COLUMNNAME_M_Transaction_UU);
  }

  public org.eevolution.model.I_PP_Cost_Collector getPP_Cost_Collector() throws RuntimeException {
    return (org.eevolution.model.I_PP_Cost_Collector)
        MTable.get(getCtx(), org.eevolution.model.I_PP_Cost_Collector.Table_Name)
            .getPO(getPP_Cost_Collector_ID(), null);
  }

  /**
   * Set Manufacturing Cost Collector.
   *
   * @param PP_Cost_Collector_ID Manufacturing Cost Collector
   */
  public void setPP_Cost_Collector_ID(int PP_Cost_Collector_ID) {
    if (PP_Cost_Collector_ID < 1) set_Value(COLUMNNAME_PP_Cost_Collector_ID, null);
    else set_Value(COLUMNNAME_PP_Cost_Collector_ID, Integer.valueOf(PP_Cost_Collector_ID));
  }

  /**
   * Get Manufacturing Cost Collector.
   *
   * @return Manufacturing Cost Collector
   */
  public int getPP_Cost_Collector_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PP_Cost_Collector_ID);
    if (ii == null) return 0;
    return ii;
  }

  @Override
  public int getTableId() {
    return I_M_Transaction.Table_ID;
  }
}