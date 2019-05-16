package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_ProjectTask;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;

/**
 * Generated Model for C_ProjectTask
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_ProjectTask extends BasePOName implements I_C_ProjectTask {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_ProjectTask(int C_ProjectTask_ID) {
        super(C_ProjectTask_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_ProjectTask(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_ProjectTask[").append(getId()).append("]");
        return sb.toString();
    }

    public org.compiere.model.I_C_ProjectPhase getProjectPhase() throws RuntimeException {
        return (org.compiere.model.I_C_ProjectPhase)
                MBaseTableKt.getTable(org.compiere.model.I_C_ProjectPhase.Table_Name)
                        .getPO(getProjectPhaseId());
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
     * Set Project Phase.
     *
     * @param C_ProjectPhase_ID Phase of a Project
     */
    public void setProjectPhaseId(int C_ProjectPhase_ID) {
        if (C_ProjectPhase_ID < 1) setValueNoCheck(COLUMNNAME_C_ProjectPhase_ID, null);
        else setValueNoCheck(COLUMNNAME_C_ProjectPhase_ID, C_ProjectPhase_ID);
    }

    /**
     * Get Project Task.
     *
     * @return Actual Project Task in a Phase
     */
    public int getProjectTaskId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectTask_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Standard Task.
     *
     * @return Standard Project Type Task
     */
    public int getTaskId() {
        Integer ii = getValue(COLUMNNAME_C_Task_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Standard Task.
     *
     * @param C_Task_ID Standard Project Type Task
     */
    public void setTaskId(int C_Task_ID) {
        if (C_Task_ID < 1) setValueNoCheck(COLUMNNAME_C_Task_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Task_ID, Integer.valueOf(C_Task_ID));
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
        return I_C_ProjectTask.Table_ID;
    }
}
