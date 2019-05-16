package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_CashBook;
import org.compiere.orm.Query;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogger;

/**
 * Cash Book Model
 *
 * @author Jorg Janke
 * @author red1 - FR: [ 2214883 ] Remove SQL code and Replace for Query
 * @version $Id: MCashBook.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class MCashBook extends X_C_CashBook {
    /**
     *
     */
    private static final long serialVersionUID = 3991585668643587699L;
    /**
     * Cache
     */
    private static CCache<Integer, I_C_CashBook> s_cache =
            new CCache<>(I_C_CashBook.Table_Name, 20);
    /**
     * Static Logger
     */
    @SuppressWarnings("unused")
    private static CLogger s_log = CLogger.getCLogger(MCashBook.class);

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_CashBook_ID id
     */
    public MCashBook(int C_CashBook_ID) {
        super(C_CashBook_ID);
    } //	MCashBook

    /**
     * Load Constructor
     *
     */
    public MCashBook(Row row) {
        super(row);
    } //	MCashBook

    /**
     * Gets MCashBook from Cache under transaction scope
     *
     * @param C_CashBook_ID id of cashbook to load
     * @return Cashbook
     */
    public static I_C_CashBook get(int C_CashBook_ID) {
        Integer key = C_CashBook_ID;
        I_C_CashBook retValue = s_cache.get(key);
        if (retValue != null) return retValue;
        retValue = new MCashBook(C_CashBook_ID);
        if (retValue.getId() != 0) s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * Get CashBook for Org and Currency
     *
     * @param AD_Org_ID     org
     * @param C_Currency_ID currency
     * @return cash book or null
     */
    public static I_C_CashBook get(int AD_Org_ID, int C_Currency_ID) {
        //	Try from cache
        for (I_C_CashBook cb : s_cache.values()) {
            if (cb.getOrgId() == AD_Org_ID && cb.getCurrencyId() == C_Currency_ID) return cb;
        }

        //	Get from DB
        final String whereClause =
                I_C_CashBook.COLUMNNAME_AD_Org_ID
                        + "=? AND "
                        + I_C_CashBook.COLUMNNAME_C_Currency_ID
                        + "=?";
        I_C_CashBook retValue =
                new Query<I_C_CashBook>(I_C_CashBook.Table_Name, whereClause)
                        .setParameters(AD_Org_ID, C_Currency_ID)
                        .setOrderBy("IsDefault DESC")
                        .first();
        if (retValue != null) {
            Integer key = retValue.getCashBookId();
            s_cache.put(key, retValue);
        }
        return retValue;
    } //	get

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (newRecord && success) insertAccounting("C_CashBook_Acct", "C_AcctSchema_Default", null);

        return success;
    } //	afterSave
} //	MCashBook
