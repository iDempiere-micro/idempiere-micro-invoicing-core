package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_AcctSchema_GL;
import org.compiere.orm.Query;
import org.idempiere.common.util.KeyNamePair;

import java.util.ArrayList;

/**
 * Accounting Schema GL info
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 * <li>RF [ 2214883 ] Remove SQL code and Replace for Query
 * http://sourceforge.net/tracker/index.php?func=detail&aid=2214883&group_id=176962&atid=879335
 * @version $Id: MAcctSchemaGL.java,v 1.3 2006/07/30 00:58:18 jjanke Exp $
 */
public class MAcctSchemaGL extends X_C_AcctSchema_GL {

    /**
     *
     */
    private static final long serialVersionUID = 5303102649110271896L;

    /**
     * Load Constructor
     *
     * @param C_AcctSchema_ID AcctSchema
     */
    public MAcctSchemaGL(int C_AcctSchema_ID) {
        super(C_AcctSchema_ID);
        if (C_AcctSchema_ID == 0) {
            setUseCurrencyBalancing(false);
            setUseSuspenseBalancing(false);
            setUseSuspenseError(false);
        }
    } //	MAcctSchemaGL

    /**
     * Load Constructor
     *
     */
    public MAcctSchemaGL(Row row) {
        super(row);
    } //	MAcctSchemaGL

    /**
     * Get Accounting Schema GL Info
     *
     * @param C_AcctSchema_ID id
     * @return defaults
     */
    public static I_C_AcctSchema_GL get(int C_AcctSchema_ID) {
        final String whereClause = "C_AcctSchema_ID=?";
        return new Query<I_C_AcctSchema_GL>(I_C_AcctSchema_GL.Table_Name, whereClause)
                .setParameters(C_AcctSchema_ID)
                .firstOnly();
    } //	get

    /**
     * Get Acct Info list
     *
     * @return list
     */
    public ArrayList<KeyNamePair> getAcctInfo() {
        ArrayList<KeyNamePair> list = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            String columnName = getColumnName(i);
            if (columnName.endsWith("Acct")) {
                int id = (getValue(i));
                list.add(new KeyNamePair(id, columnName));
            }
        }
        return list;
    } //	getAcctInfo

    /**
     * Set Value (don't use)
     *
     * @param columnName column name
     * @param value      value
     * @return true if set
     */
    public boolean setValue(String columnName, Integer value) {
        return super.setValue(columnName, value);
    } //	setValue

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (getOrgId() != 0) setOrgId(0);
        return true;
    } //	beforeSave
} //	MAcctSchemaGL
