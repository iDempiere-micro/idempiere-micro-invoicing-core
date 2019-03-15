package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_Product_Acct;
import org.compiere.orm.PO;

import java.util.Properties;

public class X_M_Product_Acct extends PO implements I_M_Product_Acct {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_Product_Acct(Properties ctx, int M_Product_Acct_ID) {
        super(ctx, M_Product_Acct_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_Product_Acct(Properties ctx, Row row) {
        super(ctx, row);
    }

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
        return "X_M_Product_Acct[" + getId() + "]";
    }

    /**
     * Set Product Expense.
     *
     * @param P_Expense_Acct Account for Product Expense
     */
    public void setP_Expense_Acct(int P_Expense_Acct) {
        setValue(COLUMNNAME_P_Expense_Acct, P_Expense_Acct);
    }

    /**
     * Set Product Revenue.
     *
     * @param P_Revenue_Acct Account for Product Revenue (Sales Account)
     */
    public void setP_Revenue_Acct(int P_Revenue_Acct) {
        setValue(COLUMNNAME_P_Revenue_Acct, Integer.valueOf(P_Revenue_Acct));
    }

}
