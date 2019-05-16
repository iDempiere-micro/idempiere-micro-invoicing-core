package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_C_BankStatementMatcher;
import org.compiere.orm.BasePOName;

public class X_C_BankStatementMatcher extends BasePOName
        implements I_C_BankStatementMatcher {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_BankStatementMatcher(int C_BankStatementMatcher_ID) {
        super(C_BankStatementMatcher_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_BankStatementMatcher(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_C_BankStatementMatcher[" + getId() + "]";
    }

    /**
     * Get Classname.
     *
     * @return Java Classname
     */
    public String getClassname() {
        return getValue(COLUMNNAME_Classname);
    }

}
