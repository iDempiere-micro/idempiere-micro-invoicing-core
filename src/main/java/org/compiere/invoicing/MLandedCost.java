package org.compiere.invoicing;

import org.compiere.util.Msg;
import org.idempiere.common.util.CLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.close;
import static software.hsharp.core.util.DBKt.prepareStatement;


/**
 * Landed Cost Model
 *
 * @author Jorg Janke
 * @version $Id: MLandedCost.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MLandedCost extends X_C_LandedCost {
  /** */
  private static final long serialVersionUID = -5645509613930428050L;

  /**
   * Get Costs of Invoice Line
   *
   * @param il invoice line
   * @return array of landed cost lines
   */
  public static MLandedCost[] getLandedCosts(MInvoiceLine il) {
    ArrayList<MLandedCost> list = new ArrayList<MLandedCost>();
    String sql = "SELECT * FROM C_LandedCost WHERE C_InvoiceLine_ID=?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = prepareStatement(sql, null);
      pstmt.setInt(1, il.getC_InvoiceLine_ID());
      rs = pstmt.executeQuery();
      while (rs.next()) {
        list.add(new MLandedCost(il.getCtx(), rs, null));
      }
    } catch (Exception e) {
      s_log.log(Level.SEVERE, sql, e);
    } finally {
      close(rs, pstmt);
      rs = null;
      pstmt = null;
    }
    //
    MLandedCost[] retValue = new MLandedCost[list.size()];
    list.toArray(retValue);
    return retValue;
  } // getLandedCosts

  /** Logger */
  private static CLogger s_log = CLogger.getCLogger(MLandedCost.class);

  /**
   * ************************************************************************* Standard Constructor
   *
   * @param ctx context
   * @param C_LandedCost_ID id
   * @param trxName trx
   */
  public MLandedCost(Properties ctx, int C_LandedCost_ID, String trxName) {
    super(ctx, C_LandedCost_ID, trxName);
    if (C_LandedCost_ID == 0) {
      //	setC_InvoiceLine_ID (0);
      //	setM_CostElement_ID (0);
      setLandedCostDistribution(X_C_LandedCost.LANDEDCOSTDISTRIBUTION_Quantity); // Q
    }
  } //	MLandedCost

  /**
   * Load Constructor
   *
   * @param ctx context
   * @param rs result set
   * @param trxName trx
   */
  public MLandedCost(Properties ctx, ResultSet rs, String trxName) {
    super(ctx, rs, trxName);
  } //	MLandedCost

  /**
   * Before Save
   *
   * @param newRecord new
   * @return true if ok
   */
  protected boolean beforeSave(boolean newRecord) {
    //	One Reference
    if (getM_Product_ID() == 0 && getM_InOut_ID() == 0 && getM_InOutLine_ID() == 0) {
      log.saveError(
          "Error",
          Msg.parseTranslation(
              getCtx(), "@NotFound@ @M_Product_ID@ | @M_InOut_ID@ | @M_InOutLine_ID@"));
      return false;
    }
    //	No Product if Line entered
    if (getM_InOutLine_ID() != 0 && getM_Product_ID() != 0) setM_Product_ID(0);

    return true;
  } //	beforeSave

  /**
   * Allocate Costs. Done at Invoice Line Level
   *
   * @return error message or ""
   */
  public String allocateCosts() {
    MInvoiceLine il = new MInvoiceLine(getCtx(), getC_InvoiceLine_ID(), null);
    return il.allocateLandedCosts();
  } //	allocateCosts

  /**
   * String Representation
   *
   * @return info
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("MLandedCost[");
    sb.append(getId())
        .append(",CostDistribution=")
        .append(getLandedCostDistribution())
        .append(",M_CostElement_ID=")
        .append(getM_CostElement_ID());
    if (getM_InOut_ID() != 0) sb.append(",M_InOut_ID=").append(getM_InOut_ID());
    if (getM_InOutLine_ID() != 0) sb.append(",M_InOutLine_ID=").append(getM_InOutLine_ID());
    if (getM_Product_ID() != 0) sb.append(",M_Product_ID=").append(getM_Product_ID());
    sb.append("]");
    return sb.toString();
  } //	toString
} //	MLandedCost