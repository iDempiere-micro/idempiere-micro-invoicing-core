package org.compiere.validation;

import kotliquery.Row;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.compiere.model.TableScriptValidator;
import org.compiere.orm.Query;
import org.idempiere.common.util.CCache;

import java.util.List;

import static software.hsharp.core.util.DBKt.getSQLValueEx;


/**
 * Table Validator Scripts
 *
 * @author Carlos Ruiz
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * <li>BF [ 1885496 ] Performance NEEDS
 * <li>BF [ 2819654 ] Table Script Validator SeqNo is not set
 * https://sourceforge.net/tracker/?func=detail&atid=879332&aid=2819654&group_id=176962
 * @version $Id: MTableScriptValidator.java
 */
public class MTableScriptValidator extends X_AD_Table_ScriptValidator {
    /**
     *
     */
    private static final long serialVersionUID = 6272423660330749776L;
    /**
     * Cache
     */
    private static CCache<Integer, TableScriptValidator> s_cache =
            new CCache<Integer, TableScriptValidator>(Table_Name, 20);
    /**
     * Cache / Table Event
     */
    private static CCache<MultiKey, List<TableScriptValidator>> s_cacheTableEvent =
            new CCache<>(
                    null, Table_Name + "_TableEvent", 20);

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param AD_Table_ScriptValidator_ID id
     */
    public MTableScriptValidator(int AD_Table_ScriptValidator_ID) {
        super(AD_Table_ScriptValidator_ID);
    } //	MTableScriptValidator

    /**
     * Load Constructor
     *
     */
    public MTableScriptValidator(Row row) {
        super(row);
    } //	MTableScriptValidator

    /**
     * Get Model Validation Script Rules for a table/event
     *
     * @param event Event
     * @return array of MTableScriptValidator or null if error or no validators found
     */
    public static List<TableScriptValidator> getModelValidatorRules(
            int ad_table_id, String event) {
        // Try cache
        final MultiKey key = new MultiKey(ad_table_id, event);
        List<TableScriptValidator> mvrs = s_cacheTableEvent.get(key);
        if (mvrs != null) {
            if (mvrs.size() > 0) return mvrs;
            else return null;
        }
        //
        // Fetch now
        final String whereClause = "AD_Table_ID=? AND EventModelValidator=?";
        mvrs =
                new Query<TableScriptValidator>(Table_Name, whereClause)
                        .setParameters(ad_table_id, event)
                        .setOnlyActiveRecords(true)
                        .setOrderBy(COLUMNNAME_SeqNo)
                        .list();
        // Store to cache
        for (TableScriptValidator rule : mvrs) {
            s_cache.put(rule.getId(), rule);
        }

        // Store to cache
        if (mvrs != null) s_cacheTableEvent.put(key, mvrs);
        //
        if (mvrs != null && mvrs.size() > 0) return mvrs;
        else return null;
    } //	getModelValidatorRules

    @Override
    protected boolean beforeSave(boolean newRecord) {
        if (getSeqNo() == 0) {
            final String sql =
                    "SELECT COALESCE(MAX(SeqNo),0) + 10 FROM "
                            + Table_Name
                            + " WHERE AD_Table_ID=? AND EventModelValidator=?";
            int seqNo = getSQLValueEx(sql, getDBTableId(), getEventModelValidator());
            setSeqNo(seqNo);
        }
        //
        return true;
    }

    @Override
    public String toString() {
        return "MTableScriptValidator[" + getId() +
                "-" +
                getDBTableId() +
                "-" +
                getEventModelValidator() +
                "]";
    } //	toString
} //	MTableScriptValidator
