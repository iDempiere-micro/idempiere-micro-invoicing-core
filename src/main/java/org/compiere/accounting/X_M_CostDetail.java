package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.model.I_M_CostDetail;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for M_CostDetail
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_CostDetail extends PO implements I_M_CostDetail, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_M_CostDetail(Properties ctx, int M_CostDetail_ID, String trxName) {
    super(ctx, M_CostDetail_ID, trxName);
    /**
     * if (M_CostDetail_ID == 0) { setAmt (Env.ZERO); setC_AcctSchema_ID (0); setIsSOTrx (false);
     * setM_AttributeSetInstance_ID (0); setM_CostDetail_ID (0); setM_Product_ID (0); setProcessed
     * (false); setQty (Env.ZERO); }
     */
  }

  /** Load Constructor */
  public X_M_CostDetail(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  }

  public X_M_CostDetail(Properties ctx, Row row) {
    super(ctx, row);
  } //	MCostDetail

  /**
   * AccessLevel
   *
   * @return 3 - Client - Org
   */
  protected int getAccessLevel() {
    return accessLevel.intValue();
  }


  @Override
  public int getTableId() {
    return Table_ID;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("X_M_CostDetail[").append(getId()).append("]");
    return sb.toString();
  }

  /**
   * Set Amount.
   *
   * @param Amt Amount
   */
  public void setAmt(BigDecimal Amt) {
    set_Value(COLUMNNAME_Amt, Amt);
  }

  /**
   * Get Amount.
   *
   * @return Amount
   */
  public BigDecimal getAmt() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Amt);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException {
    return (org.compiere.model.I_C_AcctSchema)
        MTable.get(getCtx(), org.compiere.model.I_C_AcctSchema.Table_Name)
            .getPO(getC_AcctSchema_ID(), null);
  }

  /**
   * Set Accounting Schema.
   *
   * @param C_AcctSchema_ID Rules for accounting
   */
  public void setC_AcctSchema_ID(int C_AcctSchema_ID) {
    if (C_AcctSchema_ID < 1) set_ValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
    else set_ValueNoCheck(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
  }

  /**
   * Get Accounting Schema.
   *
   * @return Rules for accounting
   */
  public int getC_AcctSchema_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_AcctSchema_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_C_InvoiceLine getC_InvoiceLine() throws RuntimeException {
    return (org.compiere.model.I_C_InvoiceLine)
        MTable.get(getCtx(), org.compiere.model.I_C_InvoiceLine.Table_Name)
            .getPO(getC_InvoiceLine_ID(), null);
  }

  /**
   * Set Invoice Line.
   *
   * @param C_InvoiceLine_ID Invoice Detail Line
   */
  public void setC_InvoiceLine_ID(int C_InvoiceLine_ID) {
    if (C_InvoiceLine_ID < 1) set_ValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
  }

  /**
   * Get Invoice Line.
   *
   * @return Invoice Detail Line
   */
  public int getC_InvoiceLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_InvoiceLine_ID);
    if (ii == null) return 0;
    return ii;
  }

  public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException {
    return (org.compiere.model.I_C_OrderLine)
        MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_Name)
            .getPO(getC_OrderLine_ID(), null);
  }

  /**
   * Set Sales Order Line.
   *
   * @param C_OrderLine_ID Sales Order Line
   */
  public void setC_OrderLine_ID(int C_OrderLine_ID) {
    if (C_OrderLine_ID < 1) set_ValueNoCheck(COLUMNNAME_C_OrderLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
  }

  /**
   * Get Sales Order Line.
   *
   * @return Sales Order Line
   */
  public int getC_OrderLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_OrderLine_ID);
    if (ii == null) return 0;
    return ii;
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
    if (C_ProjectIssue_ID < 1) set_Value(COLUMNNAME_C_ProjectIssue_ID, null);
    else set_Value(COLUMNNAME_C_ProjectIssue_ID, Integer.valueOf(C_ProjectIssue_ID));
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

  /**
   * Set Accumulated Amt.
   *
   * @param CumulatedAmt Total Amount
   */
  public void setCumulatedAmt(BigDecimal CumulatedAmt) {
    set_Value(COLUMNNAME_CumulatedAmt, CumulatedAmt);
  }

  /**
   * Get Accumulated Amt.
   *
   * @return Total Amount
   */
  public BigDecimal getCumulatedAmt() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_CumulatedAmt);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Accumulated Qty.
   *
   * @param CumulatedQty Total Quantity
   */
  public void setCumulatedQty(BigDecimal CumulatedQty) {
    set_Value(COLUMNNAME_CumulatedQty, CumulatedQty);
  }

  /**
   * Get Accumulated Qty.
   *
   * @return Total Quantity
   */
  public BigDecimal getCumulatedQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_CumulatedQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Current Cost Price.
   *
   * @param CurrentCostPrice The currently used cost price
   */
  public void setCurrentCostPrice(BigDecimal CurrentCostPrice) {
    set_Value(COLUMNNAME_CurrentCostPrice, CurrentCostPrice);
  }

  /**
   * Get Current Cost Price.
   *
   * @return The currently used cost price
   */
  public BigDecimal getCurrentCostPrice() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_CurrentCostPrice);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Current Quantity.
   *
   * @param CurrentQty Current Quantity
   */
  public void setCurrentQty(BigDecimal CurrentQty) {
    set_Value(COLUMNNAME_CurrentQty, CurrentQty);
  }

  /**
   * Get Current Quantity.
   *
   * @return Current Quantity
   */
  public BigDecimal getCurrentQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_CurrentQty);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Delta Amount.
   *
   * @param DeltaAmt Difference Amount
   */
  public void setDeltaAmt(BigDecimal DeltaAmt) {
    set_Value(COLUMNNAME_DeltaAmt, DeltaAmt);
  }

  /**
   * Get Delta Amount.
   *
   * @return Difference Amount
   */
  public BigDecimal getDeltaAmt() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_DeltaAmt);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Delta Quantity.
   *
   * @param DeltaQty Quantity Difference
   */
  public void setDeltaQty(BigDecimal DeltaQty) {
    set_Value(COLUMNNAME_DeltaQty, DeltaQty);
  }

  /**
   * Get Delta Quantity.
   *
   * @return Quantity Difference
   */
  public BigDecimal getDeltaQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_DeltaQty);
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
   * Set Sales Transaction.
   *
   * @param IsSOTrx This is a Sales Transaction
   */
  public void setIsSOTrx(boolean IsSOTrx) {
    set_Value(COLUMNNAME_IsSOTrx, Boolean.valueOf(IsSOTrx));
  }

  /**
   * Get Sales Transaction.
   *
   * @return This is a Sales Transaction
   */
  public boolean isSOTrx() {
    Object oo = get_Value(COLUMNNAME_IsSOTrx);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
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

  /**
   * Set Cost Detail.
   *
   * @param M_CostDetail_ID Cost Detail Information
   */
  public void setM_CostDetail_ID(int M_CostDetail_ID) {
    if (M_CostDetail_ID < 1) set_ValueNoCheck(COLUMNNAME_M_CostDetail_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_CostDetail_ID, Integer.valueOf(M_CostDetail_ID));
  }

  /**
   * Get Cost Detail.
   *
   * @return Cost Detail Information
   */
  public int getM_CostDetail_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_CostDetail_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set M_CostDetail_UU.
   *
   * @param M_CostDetail_UU M_CostDetail_UU
   */
  public void setM_CostDetail_UU(String M_CostDetail_UU) {
    set_Value(COLUMNNAME_M_CostDetail_UU, M_CostDetail_UU);
  }

  /**
   * Get M_CostDetail_UU.
   *
   * @return M_CostDetail_UU
   */
  public String getM_CostDetail_UU() {
    return (String) get_Value(COLUMNNAME_M_CostDetail_UU);
  }

  public org.compiere.model.I_M_CostElement getM_CostElement() throws RuntimeException {
    return (org.compiere.model.I_M_CostElement)
        MTable.get(getCtx(), org.compiere.model.I_M_CostElement.Table_Name)
            .getPO(getM_CostElement_ID(), null);
  }

  /**
   * Set Cost Element.
   *
   * @param M_CostElement_ID Product Cost Element
   */
  public void setM_CostElement_ID(int M_CostElement_ID) {
    if (M_CostElement_ID < 1) set_ValueNoCheck(COLUMNNAME_M_CostElement_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_CostElement_ID, Integer.valueOf(M_CostElement_ID));
  }

  /**
   * Get Cost Element.
   *
   * @return Product Cost Element
   */
  public int getM_CostElement_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_CostElement_ID);
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

  public org.compiere.model.I_M_MatchInv getM_MatchInv() throws RuntimeException {
    return (org.compiere.model.I_M_MatchInv)
        MTable.get(getCtx(), org.compiere.model.I_M_MatchInv.Table_Name)
            .getPO(getM_MatchInv_ID(), null);
  }

  /**
   * Set Match Invoice.
   *
   * @param M_MatchInv_ID Match Shipment/Receipt to Invoice
   */
  public void setM_MatchInv_ID(int M_MatchInv_ID) {
    if (M_MatchInv_ID < 1) set_ValueNoCheck(COLUMNNAME_M_MatchInv_ID, null);
    else set_ValueNoCheck(COLUMNNAME_M_MatchInv_ID, Integer.valueOf(M_MatchInv_ID));
  }

  /**
   * Get Match Invoice.
   *
   * @return Match Shipment/Receipt to Invoice
   */
  public int getM_MatchInv_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_MatchInv_ID);
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
    if (M_ProductionLine_ID < 1) set_Value(COLUMNNAME_M_ProductionLine_ID, null);
    else set_Value(COLUMNNAME_M_ProductionLine_ID, Integer.valueOf(M_ProductionLine_ID));
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
    if (PP_Cost_Collector_ID < 1) set_ValueNoCheck(COLUMNNAME_PP_Cost_Collector_ID, null);
    else set_ValueNoCheck(COLUMNNAME_PP_Cost_Collector_ID, Integer.valueOf(PP_Cost_Collector_ID));
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

  /**
   * Set Price.
   *
   * @param Price Price
   */
  public void setPrice(BigDecimal Price) {
    throw new IllegalArgumentException("Price is virtual column");
  }

  /**
   * Get Price.
   *
   * @return Price
   */
  public BigDecimal getPrice() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Price);
    if (bd == null) return Env.ZERO;
    return bd;
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
   * Set Quantity.
   *
   * @param Qty Quantity
   */
  public void setQty(BigDecimal Qty) {
    set_Value(COLUMNNAME_Qty, Qty);
  }

  /**
   * Get Quantity.
   *
   * @return Quantity
   */
  public BigDecimal getQty() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Qty);
    if (bd == null) return Env.ZERO;
    return bd;
  }
}