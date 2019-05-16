package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_ProjectPhase;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_ProjectPhase
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_ProjectPhase extends BasePOName implements I_C_ProjectPhase {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_ProjectPhase(int C_ProjectPhase_ID) {
        super(C_ProjectPhase_ID);
        /**
         * if (C_ProjectPhase_ID == 0) { setCommittedAmt (Env.ZERO); setProjectId (0);
         * setProjectPhaseId (0); setIsCommitCeiling (false); setIsComplete (false); setName (null);
         * setPlannedAmt (Env.ZERO); setProjInvoiceRule (null); // @ProjInvoiceRule@ setSeqNo (0);
         * // @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM C_ProjectPhase WHERE
         * C_Project_ID=@C_Project_ID@ }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_ProjectPhase(Row row) {
        super(row);
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
        StringBuffer sb = new StringBuffer("X_C_ProjectPhase[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Set Committed Amount.
     *
     * @param CommittedAmt The (legal) commitment amount
     */
    public void setCommittedAmt(BigDecimal CommittedAmt) {
        setValue(COLUMNNAME_CommittedAmt, CommittedAmt);
    }

    /**
     * Set Order.
     *
     * @param C_Order_ID Order
     */
    public void setOrderId(int C_Order_ID) {
        if (C_Order_ID < 1) setValueNoCheck(COLUMNNAME_C_Order_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
    }

    /**
     * Get Standard Phase.
     *
     * @return Standard Phase of the Project Type
     */
    public int getPhaseId() {
        Integer ii = getValue(COLUMNNAME_C_Phase_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Standard Phase.
     *
     * @param C_Phase_ID Standard Phase of the Project Type
     */
    public void setPhaseId(int C_Phase_ID) {
        if (C_Phase_ID < 1) setValueNoCheck(COLUMNNAME_C_Phase_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Phase_ID, Integer.valueOf(C_Phase_ID));
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValueNoCheck(COLUMNNAME_C_Project_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
    }

    /**
     * Get Project Phase.
     *
     * @return Phase of a Project
     */
    public int getProjectPhaseId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectPhase_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
    }

    /**
     * Set Comment/Help.
     *
     * @param Help Comment or Hint
     */
    public void setHelp(String Help) {
        setValue(COLUMNNAME_Help, Help);
    }

    /**
     * Set Commitment is Ceiling.
     *
     * @param IsCommitCeiling The commitment amount/quantity is the chargeable ceiling
     */
    public void setIsCommitCeiling(boolean IsCommitCeiling) {
        setValue(COLUMNNAME_IsCommitCeiling, Boolean.valueOf(IsCommitCeiling));
    }

    /**
     * Set Complete.
     *
     * @param IsComplete It is complete
     */
    public void setIsComplete(boolean IsComplete) {
        setValue(COLUMNNAME_IsComplete, Boolean.valueOf(IsComplete));
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
    }

    /**
     * Get Unit Price.
     *
     * @return Actual Price
     */
    public BigDecimal getPriceActual() {
        BigDecimal bd = getValue(COLUMNNAME_PriceActual);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Quantity.
     *
     * @return Quantity
     */
    public BigDecimal getQty() {
        BigDecimal bd = getValue(COLUMNNAME_Qty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        setValue(COLUMNNAME_Qty, Qty);
    }

    /**
     * Get Sequence.
     *
     * @return Method of ordering records; lowest number comes first
     */
    public int getSeqNo() {
        Integer ii = getValue(COLUMNNAME_SeqNo);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sequence.
     *
     * @param SeqNo Method of ordering records; lowest number comes first
     */
    public void setSeqNo(int SeqNo) {
        setValue(COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
    }

    @Override
    public int getTableId() {
        return I_C_ProjectPhase.Table_ID;
    }
}
