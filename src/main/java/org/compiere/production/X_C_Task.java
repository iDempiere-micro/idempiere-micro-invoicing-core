package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_Task;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for C_Task
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_Task extends BasePOName implements I_C_Task {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_Task(int C_Task_ID) {
        super(C_Task_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_Task(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_Task[").append(getId()).append("]");
        return sb.toString();
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Get Comment/Help.
     *
     * @return Comment or Hint
     */
    public String getHelp() {
        return getValue(COLUMNNAME_Help);
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

    /**
     * Get Standard Quantity.
     *
     * @return Standard Quantity
     */
    public BigDecimal getStandardQty() {
        BigDecimal bd = getValue(COLUMNNAME_StandardQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Standard Quantity.
     *
     * @param StandardQty Standard Quantity
     */
    public void setStandardQty(BigDecimal StandardQty) {
        setValue(COLUMNNAME_StandardQty, StandardQty);
    }

    @Override
    public int getTableId() {
        return I_C_Task.Table_ID;
    }
}
