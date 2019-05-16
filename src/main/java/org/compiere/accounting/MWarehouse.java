package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_Locator;
import org.compiere.model.I_M_Warehouse;
import org.compiere.orm.MOrg;
import org.compiere.orm.PO;
import org.compiere.orm.Query;
import org.compiere.production.MLocator;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.Env;

import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValueEx;

/**
 * Warehouse Model
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com
 * @author Teo Sarca, http://www.arhipac.ro
 * <li>BF [ 1874419 ] JDBC Statement not close in a finally block
 * @version $Id: MWarehouse.java,v 1.3 2006/07/30 00:58:05 jjanke Exp $
 * @see FR [ 1966337 ] New Method to get the Transit Warehouse based in ID Org
 * http://sourceforge.net/tracker/index.php?func=detail&aid=1966337&group_id=176962&atid=879335
 */
public class MWarehouse extends X_M_Warehouse {
    /**
     *
     */
    private static final long serialVersionUID = 2696705459515717619L;
    /**
     * Cache
     */
    private static CCache<Integer, MWarehouse> s_cache =
            new CCache<Integer, MWarehouse>(I_M_Warehouse.Table_Name, 50);
    /**
     * Warehouse Locators
     */
    private I_M_Locator[] m_locators = null;

    /**
     * Standard Constructor
     *
     * @param M_Warehouse_ID id
     */
    public MWarehouse(int M_Warehouse_ID) {
        super(M_Warehouse_ID);
        if (M_Warehouse_ID == 0) {
            //	setValue (null);
            //	setName (null);
            //	setLocationId (0);
            setSeparator("*"); // *
        }
    } //	MWarehouse

    /**
     * Load Constructor
     */
    public MWarehouse(Row row) {
        super(row);
    } //	MWarehouse

    /**
     * Organization Constructor
     *
     * @param org parent
     */
    public MWarehouse(MOrg org) {
        this(0);
        setClientOrg(org);
        setSearchKey(org.getSearchKey());
        setName(org.getName());
        if (org.getInfo() != null) setLocationId(org.getInfo().getLocationId());
    } //	MWarehouse

    /**
     * Retrieves warehouse from cache under transaction scope
     *
     * @param M_Warehouse_ID id of warehouse to load
     * @return warehouse
     */
    public static MWarehouse get(int M_Warehouse_ID) {
        Integer key = M_Warehouse_ID;
        MWarehouse retValue = s_cache.get(key);
        if (retValue != null) return retValue;
        //
        retValue = new MWarehouse(M_Warehouse_ID);
        s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * Get Warehouses for Org
     *
     * @param AD_Org_ID id
     * @return warehouse
     */
    public static I_M_Warehouse[] getForOrg(int AD_Org_ID) {
        final String whereClause = "AD_Org_ID=?";
        List<I_M_Warehouse> list =
                new Query<I_M_Warehouse>(I_M_Warehouse.Table_Name, whereClause)
                        .setParameters(AD_Org_ID)
                        .setOnlyActiveRecords(true)
                        .setOrderBy(I_M_Warehouse.COLUMNNAME_M_Warehouse_ID)
                        .list();
        return list.toArray(new I_M_Warehouse[0]);
    } //	get

    /**
     * Get Locators
     *
     * @param reload if true reload
     * @return array of locators
     */
    public I_M_Locator[] getLocators(boolean reload) {
        if (!reload && m_locators != null) return m_locators;
        //
        final String whereClause = "M_Warehouse_ID=?";
        List<I_M_Locator> list =
                new Query<I_M_Locator>(I_M_Locator.Table_Name, whereClause)
                        .setParameters(getWarehouseId())
                        .setOnlyActiveRecords(true)
                        .setOrderBy("X,Y,Z")
                        .list();
        m_locators = list.toArray(new I_M_Locator[0]);
        return m_locators;
    } //	getLocators

    /**
     * Get Default Locator
     *
     * @return (first) default locator
     */
    public I_M_Locator getDefaultLocator() {
        I_M_Locator[] locators = getLocators(false);
        for (int i = 0; i < locators.length; i++) {
            if (locators[i].isDefault() && locators[i].isActive()) return locators[i];
        }
        //	No Default - first one
        if (locators.length > 0) {
            log.warning("No default locator for " + getName());
            return locators[0];
        } else {
            String whereClause = "M_Warehouse_ID=?";
            List<PO> list =
                    new Query(I_M_Locator.Table_Name, whereClause)
                            .setParameters(getWarehouseId())
                            .setOnlyActiveRecords(false)
                            .list();
            if (!list.isEmpty()) {
                if (log.isLoggable(Level.INFO)) log.info("All locator is inactive for " + getName());
                // Do not auto create if there are inactive locator since that could trigger unique key
                // exception
                return null;
            }
        }
        //	No Locator - create one
        MLocator loc = new MLocator(this, "Standard");
        loc.setIsDefault(true);
        loc.saveEx();
        if (log.isLoggable(Level.INFO)) log.info("Created default locator for " + getName());
        return loc;
    } //	getLocators

    /**
     * Before Save
     *
     * @param newRecord new
     * @return success
     */
    @Override
    protected boolean beforeSave(boolean newRecord) {
    /* Disallow Negative Inventory cannot be checked if there are storage records
    with negative onhand. */
        if (isValueChanged("IsDisallowNegativeInv") && isDisallowNegativeInv()) {
            String sql =
                    "SELECT M_Product_ID FROM M_StorageOnHand s "
                            + "WHERE s.M_Locator_ID IN (SELECT M_Locator_ID FROM M_Locator l "
                            + "WHERE M_Warehouse_ID=? )"
                            + " GROUP BY M_Product_ID, M_Locator_ID, M_AttributeSetInstance_ID "
                            + " HAVING SUM(s.QtyOnHand) < 0 ";

            int prdid = getSQLValueEx(sql, getWarehouseId());
            if (prdid > 0) {
                log.saveError("Error", MsgKt.translate("NegativeOnhandExists"));
                return false;
            }
        }

        if (getOrgId() == 0) {
            int context_AD_Org_ID = Env.getOrgId();
            if (context_AD_Org_ID != 0) {
                setOrgId(context_AD_Org_ID);
                log.warning("Changed Org to Context=" + context_AD_Org_ID);
            } else {
                log.saveError("Error", MsgKt.translate("Org0NotAllowed"));
                return false;
            }
        }

        return true;
    }

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (newRecord && success) insertAccounting("M_Warehouse_Acct", "C_AcctSchema_Default", null);

        return success;
    } //	afterSave
} //	MWarehouse
