package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.Column;
import org.compiere.model.I_R_RequestUpdate;
import software.hsharp.core.orm.MBaseTableKt;

/**
 * Request Update Model
 *
 * @author Jorg Janke
 * @author Teo Sarca
 * <li>FR [ 2884541 ] MRequestUpdate should detect automatically the fields
 * https://sourceforge.net/tracker/?func=detail&aid=2884541&group_id=176962&atid=879335
 * @version $Id: MRequestUpdate.java,v 1.2 2006/07/30 00:51:05 jjanke Exp $
 */
public class MRequestUpdate extends X_R_RequestUpdate {
    /**
     *
     */
    private static final long serialVersionUID = -8862921042436867124L;

    /**
     * Standard Constructor
     *
     * @param R_RequestUpdate_ID id
     */
    public MRequestUpdate(int R_RequestUpdate_ID) {
        super(R_RequestUpdate_ID);
    } //	MRequestUpdate

    /**
     * Load Constructor
     */
    public MRequestUpdate(Row row) {
        super(row);
    } //	MRequestUpdate

    /**
     * Parent Constructor
     *
     * @param parent request
     */
    public MRequestUpdate(MRequest parent) {
        super(0);
        setClientOrg(parent);
        setRequestId(parent.getRequestId());
        //
        for (final Column col : MBaseTableKt.getTable(I_R_RequestUpdate.Table_ID).getColumns(false)) {
            if (col.isStandardColumn()
                    || col.isKey()
                    || col.isParent()
                    || col.isUUIDColumn()
                    || col.isVirtualColumn()) continue;
            final String columnName = col.getColumnName();
            final int i = parent.getColumnIndex(columnName);
            if (i >= 0) {
                setValueOfColumn(columnName, parent.getValue(i));
            }
        }
    } //	MRequestUpdate

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (getConfidentialTypeEntry() == null)
            setConfidentialTypeEntry(X_R_RequestUpdate.CONFIDENTIALTYPEENTRY_PublicInformation);
        return true;
    } //	beforeSave
} //	MRequestUpdate
