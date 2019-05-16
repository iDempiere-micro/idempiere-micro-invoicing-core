package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_CostElement;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for M_CostElement
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_CostElement extends BasePOName implements I_M_CostElement {

    /**
     * Material = M
     */
    public static final String COSTELEMENTTYPE_Material = "M";
    /**
     * Outside Processing = X
     */
    public static final String COSTELEMENTTYPE_OutsideProcessing = "X";
    /**
     * CostingMethod AD_Reference_ID=122
     */
    public static final int COSTINGMETHOD_AD_Reference_ID = 122;
    /**
     * Standard Costing = S
     */
    public static final String COSTINGMETHOD_StandardCosting = "S";
    /**
     * Average PO = A
     */
    public static final String COSTINGMETHOD_AveragePO = "A";
    /**
     * Lifo = L
     */
    public static final String COSTINGMETHOD_Lifo = "L";
    /**
     * Fifo = F
     */
    public static final String COSTINGMETHOD_Fifo = "F";
    /**
     * Last PO Price = p
     */
    public static final String COSTINGMETHOD_LastPOPrice = "p";
    /**
     * Average Invoice = I
     */
    public static final String COSTINGMETHOD_AverageInvoice = "I";
    /**
     * Last Invoice = i
     */
    public static final String COSTINGMETHOD_LastInvoice = "i";
    /**
     * User Defined = U
     */
    public static final String COSTINGMETHOD_UserDefined = "U";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_CostElement(int M_CostElement_ID) {
        super(M_CostElement_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_CostElement(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_M_CostElement[" + getId() + "]";
    }

    /**
     * Get Cost Element Type.
     *
     * @return Type of Cost Element
     */
    public String getCostElementType() {
        return getValue(COLUMNNAME_CostElementType);
    }

    /**
     * Set Cost Element Type.
     *
     * @param CostElementType Type of Cost Element
     */
    public void setCostElementType(String CostElementType) {

        setValue(COLUMNNAME_CostElementType, CostElementType);
    }

    /**
     * Get Costing Method.
     *
     * @return Indicates how Costs will be calculated
     */
    public String getCostingMethod() {
        return getValue(COLUMNNAME_CostingMethod);
    }

    /**
     * Set Costing Method.
     *
     * @param CostingMethod Indicates how Costs will be calculated
     */
    public void setCostingMethod(String CostingMethod) {

        setValue(COLUMNNAME_CostingMethod, CostingMethod);
    }

    /**
     * Set Calculated.
     *
     * @param IsCalculated The value is calculated by the system
     */
    public void setIsCalculated(boolean IsCalculated) {
        setValue(COLUMNNAME_IsCalculated, IsCalculated);
    }

    /**
     * Get Calculated.
     *
     * @return The value is calculated by the system
     */
    public boolean isCalculated() {
        Object oo = getValue(COLUMNNAME_IsCalculated);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Cost Element.
     *
     * @return Product Cost Element
     */
    public int getCostElementId() {
        Integer ii = getValue(COLUMNNAME_M_CostElement_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_M_CostElement.Table_ID;
    }
}
