package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.HouseKeeping;
import org.compiere.orm.BasePONameValue;

import java.sql.Timestamp;

public class X_AD_HouseKeeping extends BasePONameValue implements HouseKeeping {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_HouseKeeping(int AD_HouseKeeping_ID) {
        super(AD_HouseKeeping_ID);
    }

    /**
     * Load Constructor
     */
    public X_AD_HouseKeeping(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 4 - System
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_AD_HouseKeeping[" + getId() + "]";
    }

    /**
     * Get Table.
     *
     * @return Database Table information
     */
    public int getTableID() {
        Integer ii = getValue(COLUMNNAME_AD_Table_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Backup Folder.
     *
     * @return Backup Folder
     */
    public String getBackupFolder() {
        return getValue(COLUMNNAME_BackupFolder);
    }

    /**
     * Get Export XML Backup.
     *
     * @return Export XML Backup
     */
    public boolean isExportXMLBackup() {
        Object oo = getValue(COLUMNNAME_IsExportXMLBackup);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Save In Historic.
     *
     * @return Save In Historic
     */
    public boolean isSaveInHistoric() {
        Object oo = getValue(COLUMNNAME_IsSaveInHistoric);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Last Deleted.
     *
     * @param LastDeleted Last Deleted
     */
    public void setLastDeleted(int LastDeleted) {
        setValue(COLUMNNAME_LastDeleted, Integer.valueOf(LastDeleted));
    }

    /**
     * Set Last Run.
     *
     * @param LastRun Last Run
     */
    public void setLastRun(Timestamp LastRun) {
        setValue(COLUMNNAME_LastRun, LastRun);
    }

    /**
     * Get Sql WHERE.
     *
     * @return Fully qualified SQL WHERE clause
     */
    public String getWhereClause() {
        return getValue(COLUMNNAME_WhereClause);
    }
}
