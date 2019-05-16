package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_Product_Category_Acct;
import org.compiere.orm.PO;

/**
 * Generated Model for M_Product_Category_Acct
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_Product_Category_Acct extends PO
        implements I_M_Product_Category_Acct {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Product_Category_Acct(int M_Product_Category_Acct_ID) {
        super(M_Product_Category_Acct_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_Product_Category_Acct(Row row) {
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
        return "X_M_Product_Category_Acct[" + getId() + "]";
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Costing Level.
     *
     * @return The lowest level to accumulate Costing Information
     */
    public String getCostingLevel() {
        return getValue(COLUMNNAME_CostingLevel);
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
     * Get Product Category.
     *
     * @return Category of a Product
     */
    public int getProductCategoryId() {
        Integer ii = getValue(COLUMNNAME_M_Product_Category_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_M_Product_Category_Acct.Table_ID;
    }
}
