package org.compiere.query;

import org.compiere.model.Column;
import org.compiere.model.Table;
import org.compiere.orm.MTableKt;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.KeyNamePair;
import org.idempiere.common.util.ValueNamePair;
import software.hsharp.core.orm.MBaseTableKt;
import software.hsharp.core.util.DBKt;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import static software.hsharp.core.util.DBKt.convertString;

/**
 * Query Descriptor. Maintains restrictions (WHERE clause)
 *
 * @author Jorg Janke
 * @author Teo Sarca
 * <li>BF [ 2860022 ] MQuery.get() is generating restrictions for non-existent column
 * https://sourceforge.net/tracker/?func=detail&aid=2860022&group_id=176962&atid=879332
 * @version $Id: MQuery.java,v 1.4 2006/07/30 00:58:04 jjanke Exp $
 */
public class MQuery implements Serializable {

    /**
     * Equal
     */
    public static final String EQUAL = "=";
    /**
     * Not Equal
     */
    public static final String NOT_EQUAL = "!=";
    /**
     * Like
     */
    public static final String LIKE = " LIKE ";
    /**
     * Not Like
     */
    public static final String NOT_LIKE = " NOT LIKE ";
    /**
     * Greater
     */
    public static final String GREATER = ">";
    /**
     * Greater Equal
     */
    public static final String GREATER_EQUAL = ">=";
    /**
     * Less
     */
    public static final String LESS = "<";
    /**
     * Less Equal
     */
    public static final String LESS_EQUAL = "<=";
    /**
     * Between
     */
    public static final String BETWEEN = " BETWEEN ";
    /**
     * For IDEMPIERE-377
     */
    public static final String NOT_NULL = " IS NOT NULL ";
    /**
     * For IDEMPIERE-377
     */
    public static final String NULL = " IS NULL ";
    /**
     * Operators for Strings
     */
    public static final ValueNamePair[] OPERATORS =
            new ValueNamePair[]{
                    new ValueNamePair(EQUAL, " = "), // 	0 - EQUAL_INDEX
                    new ValueNamePair(NOT_EQUAL, " != "), //  1 - NOT_EQUAL_INDEX
                    new ValueNamePair(LIKE, " ~ "),
                    new ValueNamePair(NOT_LIKE, " !~ "),
                    new ValueNamePair(GREATER, " > "),
                    new ValueNamePair(GREATER_EQUAL, " >= "),
                    new ValueNamePair(LESS, " < "),
                    new ValueNamePair(LESS_EQUAL, " <= "),
                    new ValueNamePair(BETWEEN, " >-< "), // 	8 - BETWEEN_INDEX
                    new ValueNamePair(NULL, " NULL "),
                    new ValueNamePair(NOT_NULL, " !NULL ")
            };
    /**
     * Serialization Info *
     */
    private static final long serialVersionUID = 4883859385509199306L;
    /**
     * New Record String
     */
    private static final String NEWRECORD = "2=3";
    /**
     * Table Name
     */
    private String m_TableName = "";
    /**
     * List of Restrictions
     */
    private ArrayList<Restriction> m_list = new ArrayList<Restriction>();

    /**
     * ************************************************************************ Constructor w/o table
     * name
     */
    public MQuery() {
    } // 	MQuery

    /**
     * Constructor
     *
     * @param TableName Table Name
     */
    public MQuery(String TableName) {
        m_TableName = TableName;
    } //	MQuery

    /**
     * Constructor get TableNAme from Table
     *
     * @param AD_Table_ID Table_ID
     */
    public MQuery(int AD_Table_ID) { // 	Use Client Context as r/o
        m_TableName = MTableKt.getDbTableName(AD_Table_ID);
    } //	MQuery

    /**
     * Create No Record query.
     *
     * @param tableName table name
     * @param newRecord new Record Indicator (2=3)
     * @return query
     */
    public static MQuery getNoRecordQuery(String tableName, boolean newRecord) {
        MQuery query = new MQuery(tableName);
        if (newRecord) query.addRestriction(NEWRECORD);
        else query.addRestriction("1=2");
        query.setRecordCount(0);
        return query;
    } //	getNoRecordQuery

    /**
     * Set Record Count
     *
     * @param count count
     */
    public void setRecordCount(int count) {
    } //	setRecordCount

    /**
     * Add Restriction
     *
     * @param ColumnName ColumnName
     * @param Operator   Operator, e.g. = != ..
     * @param Code       Code, e.g 0, All%
     */
    public void addRestriction(String ColumnName, String Operator, Object Code) {
        Restriction r = new Restriction(ColumnName, Operator, Code, null, null, true, 0);
        m_list.add(r);
    } //	addRestriction

    /**
     * Add Restriction
     *
     * @param ColumnName ColumnName
     * @param Operator   Operator, e.g. = != ..
     * @param Code       Code, e.g 0
     */
    public void addRestriction(String ColumnName, String Operator, int Code) {
        Restriction r = new Restriction(ColumnName, Operator, new Integer(Code), null, null, true, 0);
        m_list.add(r);
    } //	addRestriction

    /**
     * Add Range Restriction (BETWEEN)
     *
     * @param ColumnName ColumnName
     * @param Code       Code, e.g 0, All%
     * @param Code_to    Code, e.g 0, All%
     */
    public void addRangeRestriction(String ColumnName, Object Code, Object Code_to) {
        Restriction r = new Restriction(ColumnName, Code, Code_to, null, null, null, true, 0);
        m_list.add(r);
    } //	addRestriction

    /**
     * Add Restriction
     *
     * @param whereClause SQL WHERE clause
     */
    public void addRestriction(String whereClause) {
        if (whereClause == null || whereClause.trim().length() == 0) return;
        Restriction r = new Restriction(whereClause, true, 0);
        m_list.add(r);
    } //	addRestriction

    /**
     * Create the resulting Query WHERE Clause
     *
     * @param fullyQualified fully qualified Table.ColumnName
     * @return Where Clause
     */
    public String getWhereClause(boolean fullyQualified) {
        int currentDepth = 0;
        boolean qualified = fullyQualified;
        if (qualified && (m_TableName == null || m_TableName.length() == 0)) qualified = false;
        //
        StringBuilder sb = new StringBuilder();
        if (!isActive()) return sb.toString();

        sb.append('(');
        for (int i = 0; i < m_list.size(); i++) {
            Restriction r = m_list.get(i);
            if (i != 0) sb.append(r.andCondition ? " AND " : " OR ");
            for (; currentDepth < r.joinDepth; currentDepth++) {
                sb.append('(');
            }
            if (qualified) sb.append(r.getSQL(m_TableName));
            else sb.append(r.getSQL(null));

            for (; currentDepth > r.joinDepth; currentDepth--) {
                sb.append(')');
            }
        }

        // close brackets
        for (; currentDepth > 0; currentDepth--) {
            sb.append(')');
        }
        sb.append(')');
        return sb.toString();
    } //	getWhereClause

    /**
     * Is Query Active
     *
     * @return true if number of restrictions > 0
     */
    public boolean isActive() {
        return m_list.size() != 0;
    } //	isActive

    /**
     * Get Table Name
     *
     * @return Table Name
     */
    public String getTableName() {
        return m_TableName;
    } //	getTableName

    /**
     * String representation
     *
     * @return info
     */
    public String toString() {
        if (isActive()) return getWhereClause(true);
        return "MQuery[" + m_TableName + ",Restrictions=0]";
    } //	toString

} //	MQuery

/**
 * ************************************************************************** Query Restriction
 */
class Restriction implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4521978087587321243L;
    /**
     * Direct Where Clause
     */
    protected String DirectWhereClause = null;
    /**
     * Column Name
     */
    protected String ColumnName;
    /**
     * Name
     */
    protected String InfoName;
    /**
     * Operator
     */
    protected String Operator;
    /**
     * SQL Where Code
     */
    protected Object Code;
    /**
     * Info
     */
    protected String InfoDisplay;
    /**
     * SQL Where Code To
     */
    protected Object Code_to;
    /**
     * Info To
     */
    protected String InfoDisplay_to;
    /**
     * And/Or Condition
     */
    protected boolean andCondition = true;
    /**
     * And/Or condition nesting depth ( = number of open brackets at and/or)
     */
    protected int joinDepth = 0;

    /**
     * Restriction
     *
     * @param columnName  ColumnName
     * @param operator    Operator, e.g. = != ..
     * @param code        Code, e.g 0, All%
     * @param infoName    Display Name
     * @param infoDisplay Display of Code (Lookup)
     */
    Restriction(
            String columnName,
            String operator,
            Object code,
            String infoName,
            String infoDisplay,
            boolean andCondition,
            int depth) {
        this.ColumnName = columnName.trim();
        if (infoName != null) InfoName = infoName;
        else InfoName = ColumnName;

        this.andCondition = andCondition;
        this.joinDepth = depth < 0 ? 0 : depth;

        //
        this.Operator = operator;
        //	Boolean
        if (code instanceof Boolean) Code = ((Boolean) code).booleanValue() ? "Y" : "N";
        else if (code instanceof KeyNamePair) Code = new Integer(((KeyNamePair) code).getKey());
        else if (code instanceof ValueNamePair) Code = ((ValueNamePair) code).getValue();
        else Code = code;
        //	clean code
        if (Code instanceof String) {
            if (Code.toString().startsWith("'") && Code.toString().endsWith("'")) {
                Code = Code.toString().substring(1);
                Code = Code.toString().substring(0, Code.toString().length() - 2);
            }
        }
        if (infoDisplay != null) InfoDisplay = infoDisplay.trim();
        else if (code != null) InfoDisplay = code.toString();
    } //	Restriction

    /**
     * Range Restriction (BETWEEN)
     *
     * @param columnName     ColumnName
     * @param code           Code, e.g 0, All%
     * @param code_to        Code, e.g 0, All%
     * @param infoName       Display Name
     * @param infoDisplay    Display of Code (Lookup)
     * @param infoDisplay_to Display of Code (Lookup)
     */
    Restriction(
            String columnName,
            Object code,
            Object code_to,
            String infoName,
            String infoDisplay,
            String infoDisplay_to,
            boolean andCondition,
            int depth) {
        this(columnName, MQuery.BETWEEN, code, infoName, infoDisplay, andCondition, depth);

        //	Code_to
        Code_to = code_to;
        if (Code_to instanceof String) {
            if (Code_to.toString().startsWith("'")) Code_to = Code_to.toString().substring(1);
            if (Code_to.toString().endsWith("'"))
                Code_to = Code_to.toString().substring(0, Code_to.toString().length() - 2);
        }
        //	InfoDisplay_to
        if (infoDisplay_to != null) InfoDisplay_to = infoDisplay_to.trim();
        else if (Code_to != null) InfoDisplay_to = Code_to.toString();
    } //	Restriction

    /**
     * Create Restriction with direct WHERE clause
     *
     * @param whereClause SQL WHERE Clause
     */
    Restriction(String whereClause, boolean andCondition, int depth) {
        DirectWhereClause = whereClause;
        this.andCondition = andCondition;
        this.joinDepth = depth;
    } //	Restriction

    /**
     * Return SQL construct for this restriction
     *
     * @param tableName optional table name
     * @return SQL WHERE construct
     */
    public String getSQL(String tableName) {
        if (DirectWhereClause != null) return DirectWhereClause;
        // verify if is a virtual column, do not prefix tableName if this is a virtualColumn
        boolean virtualColumn = false;
        if (tableName != null && tableName.length() > 0) {
            Table table = MBaseTableKt.getTable(tableName);
            if (table != null) {
                for (Column col : table.getColumns(false)) {
                    String colSQL = col.getColumnSQL();
                    if (colSQL != null && colSQL.contains("@"))
                        colSQL = Env.parseContext(-1, colSQL, false, true);
                    if (ColumnName.equals(colSQL)) {
                        virtualColumn = true;
                        break;
                    }
                }
            }
        }
        //
        StringBuilder sb = new StringBuilder();
        if (!virtualColumn && tableName != null && tableName.length() > 0) {
            //	Assumes - REPLACE(INITCAP(variable),'s','X') or UPPER(variable)
            int pos = ColumnName.lastIndexOf('(') + 1; // 	including (
            int end = ColumnName.indexOf(')');
            //	We have a Function in the ColumnName
            if (pos != -1
                    && end != -1
                    && !(pos - 1 == ColumnName.indexOf('(') && ColumnName.trim().startsWith("(")))
                sb.append(ColumnName, 0, pos)
                        .append(tableName)
                        .append(".")
                        .append(ColumnName, pos, end)
                        .append(ColumnName.substring(end));
            else {
                int selectIndex = ColumnName.toLowerCase().indexOf("select ");
                int fromIndex = ColumnName.toLowerCase().indexOf(" from ");
                if (selectIndex >= 0 && fromIndex > 0) {
                    sb.append(ColumnName);
                } else {
                    sb.append(tableName).append(".").append(ColumnName);
                }
            }
        } else sb.append(ColumnName);

        sb.append(Operator);
        if (!(Operator.equals(MQuery.NULL) || Operator.equals(MQuery.NOT_NULL))) {
            if (Code instanceof String) sb.append(convertString(Code.toString()));
            else if (Code instanceof Timestamp) sb.append(DBKt.convertDate((Timestamp) Code, false));
            else sb.append(Code);

            //	Between
            //	if (Code_to != null && InfoDisplay_to != null)
            if (MQuery.BETWEEN.equals(Operator)) {
                sb.append(" AND ");
                if (Code_to instanceof String) sb.append(convertString(Code_to.toString()));
                else if (Code_to instanceof Timestamp) sb.append(DBKt.convertDate((Timestamp) Code_to, false));
                else sb.append(Code_to);
            }
        }
        return sb.toString();
    } //	getSQL

    /**
     * Get String Representation
     *
     * @return info
     */
    public String toString() {
        return getSQL(null);
    } //	toString

    /**
     * Get Info Name
     *
     * @return Info Name
     */
    public String getInfoName() {
        return InfoName;
    } //	getInfoName

    /**
     * Get Info Operator
     *
     * @return info Operator
     */
    public String getInfoOperator() {
        for (int i = 0; i < MQuery.OPERATORS.length; i++) {
            if (MQuery.OPERATORS[i].getValue().equals(Operator)) return MQuery.OPERATORS[i].getName();
        }
        return Operator;
    } //	getInfoOperator

    /**
     * Get Display with optional To
     *
     * @return info display
     */
    public String getInfoDisplayAll() {
        if (InfoDisplay_to == null) return InfoDisplay;
        return InfoDisplay + " - " + InfoDisplay_to;
    } //	getInfoDisplay
} //	Restriction
