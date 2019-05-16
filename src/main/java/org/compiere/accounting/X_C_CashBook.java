package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_CashBook;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for C_CashBook
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_CashBook extends BasePOName implements I_C_CashBook {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_CashBook(int C_CashBook_ID) {
        super(C_CashBook_ID);
        /**
         * if (C_CashBook_ID == 0) { setCashBookId (0); setCurrencyId (0); setIsDefault (false);
         * setName (null); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_CashBook(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_CashBook[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Cash Book.
     *
     * @return Cash Book for recording petty cash transactions
     */
    public int getCashBookId() {
        Integer ii = getValue(COLUMNNAME_C_CashBook_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(COLUMNNAME_C_Currency_ID, null);
        else setValue(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
    }

    @Override
    public int getTableId() {
        return I_C_CashBook.Table_ID;
    }
}
