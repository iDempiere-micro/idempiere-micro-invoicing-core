package org.idempiere.process;

import org.compiere.accounting.MOrder;
import org.compiere.accounting.MOrderLine;
import org.compiere.accounting.MProductPO;
import org.compiere.conversionrate.MConversionRate;
import org.compiere.crm.MBPartner;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.production.MProject;
import org.compiere.production.MProjectLine;
import org.compiere.util.Msg;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Generate Purchase Order from Project.
 *
 * @author Jorg Janke
 * @version $Id: ProjectGenPO.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class ProjectGenPO extends SvrProcess {
  /** Project Parameter */
  private int m_C_Project_ID = 0;
  /** Opt Project Line Parameter */
  private int m_C_ProjectPhase_ID = 0;
  /** Opt Project Line Parameter */
  private int m_C_ProjectLine_ID = 0;
  /** Consolidate Document */
  private boolean m_ConsolidateDocument = true;
  /** List of POs for Consolidation */
  private ArrayList<MOrder> m_pos = new ArrayList<MOrder>();

  /** Prepare - e.g., get Parameters. */
  protected void prepare() {
    IProcessInfoParameter[] para = getParameter();
    for (int i = 0; i < para.length; i++) {
      String name = para[i].getParameterName();
      if (para[i].getParameter() == null) ;
      else if (name.equals("C_Project_ID"))
        m_C_Project_ID = ((BigDecimal) para[i].getParameter()).intValue();
      else if (name.equals("C_ProjectPhase_ID"))
        m_C_ProjectPhase_ID = ((BigDecimal) para[i].getParameter()).intValue();
      else if (name.equals("C_ProjectLine_ID"))
        m_C_ProjectLine_ID = ((BigDecimal) para[i].getParameter()).intValue();
      else if (name.equals("ConsolidateDocument"))
        m_ConsolidateDocument = "Y".equals(para[i].getParameter());
      else log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
    }
  } //	prepare

  /**
   * Perform process.
   *
   * @return Message
   * @throws Exception if not successful
   */
  protected String doIt() throws Exception {
    if (log.isLoggable(Level.INFO))
      log.info(
          "doIt - C_Project_ID="
              + m_C_Project_ID
              + " - C_ProjectLine_ID="
              + m_C_ProjectLine_ID
              + " - Consolidate="
              + m_ConsolidateDocument);
    if (m_C_ProjectLine_ID != 0) {
      MProjectLine projectLine = new MProjectLine(getCtx(), m_C_ProjectLine_ID);
      MProject project = new MProject(getCtx(), projectLine.getC_Project_ID());
      createPO(project, projectLine);
    } else if (m_C_ProjectPhase_ID != 0) {
      MProject project = new MProject(getCtx(), m_C_Project_ID);
      for (MProjectLine line : project.getPhaseLines(m_C_ProjectPhase_ID)) {
        if (line.isActive()) {
          createPO(project, line);
        }
      }
    } else {
      MProject project = new MProject(getCtx(), m_C_Project_ID);
      for (MProjectLine line : project.getLines()) {
        if (line.isActive()) {
          createPO(project, line);
        }
      }
    }
    return "";
  } //	doIt

  /**
   * Create PO from Planned Amt/Qty
   *
   * @param projectLine project line
   */
  private void createPO(MProject project, MProjectLine projectLine) {
    if (projectLine.getM_Product_ID() == 0) {
      addLog(projectLine.getLine(), null, null, "Line has no Product");
      return;
    }
    if (projectLine.getC_OrderPO_ID() != 0) {
      addLog(projectLine.getLine(), null, null, "Line was ordered previously");
      return;
    }

    //	PO Record
    MProductPO[] pos =
        MProductPO.getOfProduct(getCtx(), projectLine.getM_Product_ID());
    if (pos == null || pos.length == 0) {
      addLog(projectLine.getLine(), null, null, "Product has no PO record");
      return;
    }

    //	Create to Order
    MOrder order = null;
    //	try to find PO to C_BPartner
    for (int i = 0; i < m_pos.size(); i++) {
      MOrder test = (MOrder) m_pos.get(i);
      if (test.getC_BPartner_ID() == pos[0].getC_BPartner_ID()) {
        order = test;
        break;
      }
    }
    if (order == null) // 	create new Order
    {
      //	Vendor
      MBPartner bp = new MBPartner(getCtx(), pos[0].getC_BPartner_ID());
      //	New Order
      order = new MOrder(project, false, null);
      int AD_Org_ID = projectLine.getOrgId();
      if (AD_Org_ID == 0) {
        log.warning("createPOfromProjectLine - orgId=0");
        AD_Org_ID = Env.getOrgId(getCtx());
        if (AD_Org_ID != 0) projectLine.setAD_Org_ID(AD_Org_ID);
      }
      order.setClientOrg(projectLine.getClientId(), AD_Org_ID);
      order.setBPartner(bp);
      order.saveEx();
      //	optionally save for consolidation
      if (m_ConsolidateDocument) m_pos.add(order);
    }

    //	Create Line
    MOrderLine orderLine = new MOrderLine(order);
    orderLine.setM_Product_ID(projectLine.getM_Product_ID(), true);
    orderLine.setQty(projectLine.getPlannedQty());
    orderLine.setDescription(projectLine.getDescription());

    //	(Vendor) PriceList Price
    orderLine.setPrice();
    if (orderLine.getPriceActual().signum() == 0) {
      //	Try to find purchase price
      BigDecimal poPrice = pos[0].getPricePO();
      int C_Currency_ID = pos[0].getC_Currency_ID();
      //
      if (poPrice == null || poPrice.signum() == 0) poPrice = pos[0].getPriceLastPO();
      if (poPrice == null || poPrice.signum() == 0) poPrice = pos[0].getPriceList();
      //	We have a price
      if (poPrice != null && poPrice.signum() != 0) {
        if (order.getC_Currency_ID() != C_Currency_ID)
          poPrice =
              MConversionRate.convert(
                  getCtx(),
                  poPrice,
                  C_Currency_ID,
                  order.getC_Currency_ID(),
                  order.getDateAcct(),
                  order.getC_ConversionType_ID(),
                  order.getClientId(),
                  order.getOrgId());
        orderLine.setPrice(poPrice);
      }
    }

    orderLine.setTax();
    orderLine.saveEx();

    //	update ProjectLine
    projectLine.setC_OrderPO_ID(order.getC_Order_ID());
    projectLine.saveEx();
    addBufferLog(
        order.getC_Order_ID(),
        order.getDateOrdered(),
        new BigDecimal(orderLine.getLine()),
        Msg.getElement(Env.getADLanguage(Env.getCtx()), "C_Order_ID", false)
            + ":"
            + order.getDocumentNo(),
        order.getTableId(),
        order.getC_Order_ID());
  } //	createPOfromProjectLine
} //	ProjectGenPO
