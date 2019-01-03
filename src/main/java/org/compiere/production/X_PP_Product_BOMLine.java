package org.compiere.production;

import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.eevolution.model.I_PP_Product_BOMLine;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.KeyNamePair;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Generated Model for PP_Product_BOMLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_PP_Product_BOMLine extends PO implements I_PP_Product_BOMLine, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_PP_Product_BOMLine(Properties ctx, int PP_Product_BOMLine_ID, String trxName) {
    super(ctx, PP_Product_BOMLine_ID, trxName);
    /**
     * if (PP_Product_BOMLine_ID == 0) { setIssueMethod (null); // 1 setLine (0); // @SQL=SELECT
     * COALESCE(MAX(Line),0)+10 AS DefaultValue FROM PP_Product_BOMLine WHERE
     * PP_Product_BOM_ID=@PP_Product_BOM_ID@ setM_Product_ID (0); setPP_Product_BOM_ID (0);
     * setPP_Product_BOMLine_ID (0); setValidFrom (new Timestamp( System.currentTimeMillis() ));
     * // @#Date@ }
     */
  }

  /** Load Constructor */
  public X_PP_Product_BOMLine(Properties ctx, ResultSet rs, String trxName) {
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
    StringBuffer sb = new StringBuffer("X_PP_Product_BOMLine[").append(getId()).append("]");
    return sb.toString();
  }

  /**
   * Set Quantity Assay.
   *
   * @param Assay Indicated the Quantity Assay to use into Quality Order
   */
  public void setAssay(BigDecimal Assay) {
    set_Value(COLUMNNAME_Assay, Assay);
  }

  /**
   * Get Quantity Assay.
   *
   * @return Indicated the Quantity Assay to use into Quality Order
   */
  public BigDecimal getAssay() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Assay);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Backflush Group.
   *
   * @param BackflushGroup The Grouping Components to the Backflush
   */
  public void setBackflushGroup(String BackflushGroup) {
    set_Value(COLUMNNAME_BackflushGroup, BackflushGroup);
  }

  /**
   * Get Backflush Group.
   *
   * @return The Grouping Components to the Backflush
   */
  public String getBackflushGroup() {
    return (String) get_Value(COLUMNNAME_BackflushGroup);
  }

  /** ComponentType AD_Reference_ID=53225 */
  public static final int COMPONENTTYPE_AD_Reference_ID = 53225;
  /** By-Product = BY */
  public static final String COMPONENTTYPE_By_Product = "BY";
  /** Component = CO */
  public static final String COMPONENTTYPE_Component = "CO";
  /** Phantom = PH */
  public static final String COMPONENTTYPE_Phantom = "PH";
  /** Packing = PK */
  public static final String COMPONENTTYPE_Packing = "PK";
  /** Planning = PL */
  public static final String COMPONENTTYPE_Planning = "PL";
  /** Tools = TL */
  public static final String COMPONENTTYPE_Tools = "TL";
  /** Option = OP */
  public static final String COMPONENTTYPE_Option = "OP";
  /** Variant = VA */
  public static final String COMPONENTTYPE_Variant = "VA";
  /** Co-Product = CP */
  public static final String COMPONENTTYPE_Co_Product = "CP";
  /**
   * Set Component Type.
   *
   * @param ComponentType Component Type for a Bill of Material or Formula
   */
  public void setComponentType(String ComponentType) {

    set_Value(COLUMNNAME_ComponentType, ComponentType);
  }

  /**
   * Get Component Type.
   *
   * @return Component Type for a Bill of Material or Formula
   */
  public String getComponentType() {
    return (String) get_Value(COLUMNNAME_ComponentType);
  }

  /**
   * Set Cost Allocation Percent.
   *
   * @param CostAllocationPerc Cost allocation percent in case of a co-product.
   */
  public void setCostAllocationPerc(BigDecimal CostAllocationPerc) {
    set_Value(COLUMNNAME_CostAllocationPerc, CostAllocationPerc);
  }

  /**
   * Get Cost Allocation Percent.
   *
   * @return Cost allocation percent in case of a co-product.
   */
  public BigDecimal getCostAllocationPerc() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_CostAllocationPerc);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  public org.compiere.model.I_C_UOM getC_UOM() throws RuntimeException {
    return (org.compiere.model.I_C_UOM)
        MTable.get(getCtx(), org.compiere.model.I_C_UOM.Table_Name)
            .getPO(getC_UOM_ID(), null);
  }

  /**
   * Set UOM.
   *
   * @param C_UOM_ID Unit of Measure
   */
  public void setC_UOM_ID(int C_UOM_ID) {
    if (C_UOM_ID < 1) set_Value(COLUMNNAME_C_UOM_ID, null);
    else set_Value(COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
  }

  /**
   * Get UOM.
   *
   * @return Unit of Measure
   */
  public int getC_UOM_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_UOM_ID);
    if (ii == null) return 0;
    return ii;
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
   * Set Feature.
   *
   * @param Feature Indicated the Feature for Product Configure
   */
  public void setFeature(String Feature) {
    set_Value(COLUMNNAME_Feature, Feature);
  }

  /**
   * Get Feature.
   *
   * @return Indicated the Feature for Product Configure
   */
  public String getFeature() {
    return (String) get_Value(COLUMNNAME_Feature);
  }

  /**
   * Set Forecast.
   *
   * @param Forecast Indicated the % of participation this component into a of the BOM Planning
   */
  public void setForecast(BigDecimal Forecast) {
    set_Value(COLUMNNAME_Forecast, Forecast);
  }

  /**
   * Get Forecast.
   *
   * @return Indicated the % of participation this component into a of the BOM Planning
   */
  public BigDecimal getForecast() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Forecast);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Comment/Help.
   *
   * @param Help Comment or Hint
   */
  public void setHelp(String Help) {
    set_Value(COLUMNNAME_Help, Help);
  }

  /**
   * Get Comment/Help.
   *
   * @return Comment or Hint
   */
  public String getHelp() {
    return (String) get_Value(COLUMNNAME_Help);
  }

  /**
   * Set Is Critical Component.
   *
   * @param IsCritical Indicate that a Manufacturing Order can not begin without have this component
   */
  public void setIsCritical(boolean IsCritical) {
    set_Value(COLUMNNAME_IsCritical, Boolean.valueOf(IsCritical));
  }

  /**
   * Get Is Critical Component.
   *
   * @return Indicate that a Manufacturing Order can not begin without have this component
   */
  public boolean isCritical() {
    Object oo = get_Value(COLUMNNAME_IsCritical);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
  }

  /**
   * Set Is Qty Percentage.
   *
   * @param IsQtyPercentage Indicate that this component is based in % Quantity
   */
  public void setIsQtyPercentage(boolean IsQtyPercentage) {
    set_Value(COLUMNNAME_IsQtyPercentage, Boolean.valueOf(IsQtyPercentage));
  }

  /**
   * Get Is Qty Percentage.
   *
   * @return Indicate that this component is based in % Quantity
   */
  public boolean isQtyPercentage() {
    Object oo = get_Value(COLUMNNAME_IsQtyPercentage);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
  }

  /** IssueMethod AD_Reference_ID=53226 */
  public static final int ISSUEMETHOD_AD_Reference_ID = 53226;
  /** Issue = 0 */
  public static final String ISSUEMETHOD_Issue = "0";
  /** Backflush = 1 */
  public static final String ISSUEMETHOD_Backflush = "1";
  /** Floor Stock = 2 */
  public static final String ISSUEMETHOD_FloorStock = "2";
  /**
   * Set Issue Method.
   *
   * @param IssueMethod There are two methods for issue the components to Manufacturing Order
   */
  public void setIssueMethod(String IssueMethod) {

    set_Value(COLUMNNAME_IssueMethod, IssueMethod);
  }

  /**
   * Get Issue Method.
   *
   * @return There are two methods for issue the components to Manufacturing Order
   */
  public String getIssueMethod() {
    return (String) get_Value(COLUMNNAME_IssueMethod);
  }

  /**
   * Set Lead Time Offset.
   *
   * @param LeadTimeOffset Optional Lead Time offset before starting production
   */
  public void setLeadTimeOffset(int LeadTimeOffset) {
    set_Value(COLUMNNAME_LeadTimeOffset, Integer.valueOf(LeadTimeOffset));
  }

  /**
   * Get Lead Time Offset.
   *
   * @return Optional Lead Time offset before starting production
   */
  public int getLeadTimeOffset() {
    Integer ii = (Integer) get_Value(COLUMNNAME_LeadTimeOffset);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Line No.
   *
   * @param Line Unique line for this document
   */
  public void setLine(int Line) {
    set_Value(COLUMNNAME_Line, Integer.valueOf(Line));
  }

  /**
   * Get Line No.
   *
   * @return Unique line for this document
   */
  public int getLine() {
    Integer ii = (Integer) get_Value(COLUMNNAME_Line);
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
    if (M_AttributeSetInstance_ID < 0) set_Value(COLUMNNAME_M_AttributeSetInstance_ID, null);
    else
      set_Value(COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
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

  public org.compiere.model.I_M_ChangeNotice getM_ChangeNotice() throws RuntimeException {
    return (org.compiere.model.I_M_ChangeNotice)
        MTable.get(getCtx(), org.compiere.model.I_M_ChangeNotice.Table_Name)
            .getPO(getM_ChangeNotice_ID(), null);
  }

  /**
   * Set Change Notice.
   *
   * @param M_ChangeNotice_ID Bill of Materials (Engineering) Change Notice (Version)
   */
  public void setM_ChangeNotice_ID(int M_ChangeNotice_ID) {
    if (M_ChangeNotice_ID < 1) set_Value(COLUMNNAME_M_ChangeNotice_ID, null);
    else set_Value(COLUMNNAME_M_ChangeNotice_ID, Integer.valueOf(M_ChangeNotice_ID));
  }

  /**
   * Get Change Notice.
   *
   * @return Bill of Materials (Engineering) Change Notice (Version)
   */
  public int getM_ChangeNotice_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_M_ChangeNotice_ID);
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
    if (M_Product_ID < 1) set_Value(COLUMNNAME_M_Product_ID, null);
    else set_Value(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
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
   * Get Record ID/ColumnName
   *
   * @return ID/ColumnName pair
   */
  public KeyNamePair getKeyNamePair() {
    return new KeyNamePair(getId(), String.valueOf(getM_Product_ID()));
  }

  public org.eevolution.model.I_PP_Product_BOM getPP_Product_BOM() throws RuntimeException {
    return (org.eevolution.model.I_PP_Product_BOM)
        MTable.get(getCtx(), org.eevolution.model.I_PP_Product_BOM.Table_Name)
            .getPO(getPP_Product_BOM_ID(), null);
  }

  /**
   * Set BOM & Formula.
   *
   * @param PP_Product_BOM_ID BOM & Formula
   */
  public void setPP_Product_BOM_ID(int PP_Product_BOM_ID) {
    if (PP_Product_BOM_ID < 1) set_ValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, null);
    else set_ValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, Integer.valueOf(PP_Product_BOM_ID));
  }

  /**
   * Get BOM & Formula.
   *
   * @return BOM & Formula
   */
  public int getPP_Product_BOM_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PP_Product_BOM_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set BOM Line.
   *
   * @param PP_Product_BOMLine_ID BOM Line
   */
  public void setPP_Product_BOMLine_ID(int PP_Product_BOMLine_ID) {
    if (PP_Product_BOMLine_ID < 1) set_ValueNoCheck(COLUMNNAME_PP_Product_BOMLine_ID, null);
    else set_ValueNoCheck(COLUMNNAME_PP_Product_BOMLine_ID, Integer.valueOf(PP_Product_BOMLine_ID));
  }

  /**
   * Get BOM Line.
   *
   * @return BOM Line
   */
  public int getPP_Product_BOMLine_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_PP_Product_BOMLine_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set PP_Product_BOMLine_UU.
   *
   * @param PP_Product_BOMLine_UU PP_Product_BOMLine_UU
   */
  public void setPP_Product_BOMLine_UU(String PP_Product_BOMLine_UU) {
    set_Value(COLUMNNAME_PP_Product_BOMLine_UU, PP_Product_BOMLine_UU);
  }

  /**
   * Get PP_Product_BOMLine_UU.
   *
   * @return PP_Product_BOMLine_UU
   */
  public String getPP_Product_BOMLine_UU() {
    return (String) get_Value(COLUMNNAME_PP_Product_BOMLine_UU);
  }

  /**
   * Set Quantity in %.
   *
   * @param QtyBatch Indicate the Quantity % use in this Formula
   */
  public void setQtyBatch(BigDecimal QtyBatch) {
    set_Value(COLUMNNAME_QtyBatch, QtyBatch);
  }

  /**
   * Get Quantity in %.
   *
   * @return Indicate the Quantity % use in this Formula
   */
  public BigDecimal getQtyBatch() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_QtyBatch);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Quantity.
   *
   * @param QtyBOM Indicate the Quantity use in this BOM
   */
  public void setQtyBOM(BigDecimal QtyBOM) {
    set_Value(COLUMNNAME_QtyBOM, QtyBOM);
  }

  /**
   * Get Quantity.
   *
   * @return Indicate the Quantity use in this BOM
   */
  public BigDecimal getQtyBOM() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_QtyBOM);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Scrap %.
   *
   * @param Scrap Indicate the Scrap % for calculate the Scrap Quantity
   */
  public void setScrap(BigDecimal Scrap) {
    set_Value(COLUMNNAME_Scrap, Scrap);
  }

  /**
   * Get Scrap %.
   *
   * @return Indicate the Scrap % for calculate the Scrap Quantity
   */
  public BigDecimal getScrap() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_Scrap);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Valid from.
   *
   * @param ValidFrom Valid from including this date (first day)
   */
  public void setValidFrom(Timestamp ValidFrom) {
    set_Value(COLUMNNAME_ValidFrom, ValidFrom);
  }

  /**
   * Get Valid from.
   *
   * @return Valid from including this date (first day)
   */
  public Timestamp getValidFrom() {
    return (Timestamp) get_Value(COLUMNNAME_ValidFrom);
  }

  /**
   * Set Valid to.
   *
   * @param ValidTo Valid to including this date (last day)
   */
  public void setValidTo(Timestamp ValidTo) {
    set_Value(COLUMNNAME_ValidTo, ValidTo);
  }

  /**
   * Get Valid to.
   *
   * @return Valid to including this date (last day)
   */
  public Timestamp getValidTo() {
    return (Timestamp) get_Value(COLUMNNAME_ValidTo);
  }

  @Override
  public int getTableId() {
    return I_PP_Product_BOMLine.Table_ID;
  }
}