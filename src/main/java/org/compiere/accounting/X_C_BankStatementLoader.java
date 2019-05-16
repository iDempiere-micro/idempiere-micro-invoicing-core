package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_BankStatementLoader;
import org.compiere.orm.BasePOName;

public class X_C_BankStatementLoader extends BasePOName
        implements I_C_BankStatementLoader {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_BankStatementLoader(int C_BankStatementLoader_ID) {
        super(C_BankStatementLoader_ID);
        /**
         * if (C_BankStatementLoader_ID == 0) { setBankAccountId (0); setBankStatementLoader_ID
         * (0); setName (null); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_BankStatementLoader(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_BankStatementLoader[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Statement Loader Class.
     *
     * @return Class name of the bank statement loader
     */
    public String getStmtLoaderClass() {
        return getValue(COLUMNNAME_StmtLoaderClass);
    }

}
