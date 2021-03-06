package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_M_StorageReservation;
import org.compiere.orm.Query;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdateEx;
import static software.hsharp.core.util.DBKt.forUpdate;
import static software.hsharp.core.util.DBKt.getSQLValueBD;

public class MStorageReservation extends X_M_StorageReservation {
    /**
     *
     */
    private static final long serialVersionUID = 8179093165315835613L;
    private static CLogger s_log = CLogger.getCLogger(MStorageReservation.class);

    public MStorageReservation(int M_StorageReservation_ID) {
        super(M_StorageReservation_ID);
    }

    public MStorageReservation(Row row) {
        super(row);
    }

    /**
     * Full NEW Constructor
     *
     * @param warehouse                 (parent) warehouse
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID attribute
     * @param isSOTrx
     */
    private MStorageReservation(
            MWarehouse warehouse, int M_Product_ID, int M_AttributeSetInstance_ID, boolean isSOTrx) {
        this(0);
        setClientOrg(warehouse);
        setWarehouseId(warehouse.getWarehouseId());
        setProductId(M_Product_ID);
        setAttributeSetInstanceId(M_AttributeSetInstance_ID);
        setIsSOTrx(isSOTrx);
        setQty(Env.ZERO);
    } //	MStorageReservation

    /**
     * Get Storage Info
     *
     * @param M_Warehouse_ID            warehouse
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID instance
     * @param isSOTrx
     * @return existing or null
     */
    public static MStorageReservation get(

            int M_Warehouse_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            boolean isSOTrx) {
        return MBaseStorageReservationKt.getStorageInfo(
                M_Warehouse_ID,
                M_Product_ID,
                M_AttributeSetInstance_ID,
                isSOTrx
        );
    } //	get

    /**
     * Get Storage Info for Product across warehouses
     *
     * @param M_Product_ID product
     * @return existing or null
     */
    public static I_M_StorageReservation[] getOfProduct(
            int M_Product_ID) {
        String sqlWhere = "M_Product_ID=?";

        List<I_M_StorageReservation> list =
                new Query<I_M_StorageReservation>(I_M_StorageReservation.Table_Name, sqlWhere)
                        .setParameters(M_Product_ID)
                        .list();

        I_M_StorageReservation[] retValue = new I_M_StorageReservation[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getOfProduct

    /**
     * Get Quantity Reserved of Warehouse
     *
     * @param M_Product_ID
     * @param M_Warehouse_ID
     * @param M_AttributeSetInstance_ID
     * @param isSOTrx                   - true to get reserved, false to get ordered
     * @return
     */
    private static BigDecimal getQty(
            int M_Product_ID,
            int M_Warehouse_ID,
            int M_AttributeSetInstance_ID,
            boolean isSOTrx) {
        ArrayList<Object> params = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT SUM(Qty) FROM M_StorageReservation sr")
                .append(" WHERE sr.M_Product_ID=? AND sr.M_Warehouse_ID=?")
                .append(" AND sr.IsSOTrx=?");

        params.add(M_Product_ID);
        params.add(M_Warehouse_ID);
        params.add(isSOTrx ? "Y" : "N");

        // With ASI
        if (M_AttributeSetInstance_ID != 0) {
            sql.append(" AND M_AttributeSetInstance_ID=?");
            params.add(M_AttributeSetInstance_ID);
        }

        BigDecimal qty = getSQLValueBD(sql.toString(), params);
        if (qty == null) qty = Env.ZERO;

        return qty;
    }

    /**
     * Get Available Qty. The call is accurate only if there is a storage record and assumes that the
     * product is stocked
     *
     * @param M_Warehouse_ID            wh
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID masi
     * @return qty available (QtyOnHand-QtyReserved) or null
     */
    public static BigDecimal getQtyAvailable(
            int M_Warehouse_ID, int M_Product_ID, int M_AttributeSetInstance_ID) {
        BigDecimal qtyOnHand =
                MStorageOnHand.getQtyOnHandForReservation(
                        M_Product_ID, M_Warehouse_ID, M_AttributeSetInstance_ID);
        BigDecimal qtyReserved =
                MStorageReservation.getQty(
                        M_Product_ID, M_Warehouse_ID, M_AttributeSetInstance_ID, true);
        BigDecimal retValue = qtyOnHand.subtract(qtyReserved);
        return retValue;
    }

    /**
     * @param M_Warehouse_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param diffQty
     * @param isSOTrx
     * @return
     */
    public static boolean add(

            int M_Warehouse_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            BigDecimal diffQty,
            boolean isSOTrx) {

        if (diffQty == null || diffQty.signum() == 0) return true;

        /* Do NOT use FIFO ASI for reservation */
        MProduct prd = new MProduct(M_Product_ID);
        if (prd.getAttributeSetId() == 0 || !prd.getMAttributeSet().isInstanceAttribute()) {
            // Product doesn't manage attribute set, always reserved with 0
            M_AttributeSetInstance_ID = 0;
        }

        //	Get Storage
        MStorageReservation storage =
                getCreate(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, isSOTrx);
        forUpdate(storage);
        //	Verify
        if (storage.getWarehouseId() != M_Warehouse_ID
                && storage.getProductId() != M_Product_ID
                && storage.getAttributeSetInstanceId() != M_AttributeSetInstance_ID) {
            s_log.severe(
                    "No Storage found - M_Warehouse_ID="
                            + M_Warehouse_ID
                            + ",M_Product_ID="
                            + M_Product_ID
                            + ",ASI="
                            + M_AttributeSetInstance_ID);
            return false;
        }

        storage.addQty(diffQty);
        if (s_log.isLoggable(Level.FINE)) {
            StringBuilder diffText =
                    new StringBuilder("(Qty=").append(diffQty).append(") -> ").append(storage.toString());
            s_log.fine(diffText.toString());
        }
        return true;
    }

    /**
     * Update Storage Info add. Called from MProjectIssue
     *
     * @param M_Warehouse_ID                     warehouse
     * @param M_Product_ID                       product
     * @param M_AttributeSetInstance_ID          AS Instance
     * @param reservationAttributeSetInstance_ID reservation AS Instance
     * @param diffQty                            add
     * @param isSOTrx
     * @param trxName                            transaction
     * @return true if updated
     */
    @Deprecated
    public static boolean add(

            int M_Warehouse_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            int reservationAttributeSetInstance_ID,
            BigDecimal diffQty,
            boolean isSOTrx,
            String trxName) {

        return add(

                M_Warehouse_ID,
                M_Product_ID,
                reservationAttributeSetInstance_ID,
                diffQty,
                isSOTrx
        );
    } //	add

    /**
     * Create or Get Storage Info
     *
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID instance
     * @return existing/new or null
     */
    public static MStorageReservation getCreate(

            int M_Warehouse_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            boolean isSOTrx) {
        if (M_Warehouse_ID == 0) throw new IllegalArgumentException("M_Warehouse_ID=0");
        if (M_Product_ID == 0) throw new IllegalArgumentException("M_Product_ID=0");
        MStorageReservation retValue =
                get(M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, isSOTrx);
        if (retValue != null) return retValue;

        //	Insert row based on warehouse
        MWarehouse warehouse = new MWarehouse(M_Warehouse_ID);
        if (warehouse.getId() != M_Warehouse_ID)
            throw new IllegalArgumentException("Not found M_Warehouse_ID=" + M_Warehouse_ID);
        //
        retValue = new MStorageReservation(warehouse, M_Product_ID, M_AttributeSetInstance_ID, isSOTrx);
        retValue.saveEx();
        if (s_log.isLoggable(Level.FINE)) s_log.fine("New " + retValue);
        return retValue;
    } //	getCreate

    /**
     * Add quantity on hand directly - not using cached value - solving IDEMPIERE-2629
     *
     * @param addition
     */
    public void addQty(BigDecimal addition) {
        final String sql =
                "UPDATE M_StorageReservation SET Qty=Qty+?, Updated=SYSDATE, UpdatedBy=? "
                        + "WHERE M_Product_ID=? AND M_Warehouse_ID=? AND M_AttributeSetInstance_ID=? AND IsSOTrx=?";
        executeUpdateEx(
                sql,
                new Object[]{
                        addition,
                        Env.getUserId(),
                        getProductId(),
                        getWarehouseId(),
                        getAttributeSetInstanceId(),
                        isSOTrx()
                }
        );
        loadFromMap(null);
    }

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MStorageReservation[" +
                "M_Warehouse_ID=" +
                getWarehouseId() +
                ",M_Product_ID=" +
                getProductId() +
                ",M_AttributeSetInstance_ID=" +
                getAttributeSetInstanceId() +
                ",IsSOTrx=" +
                isSOTrx() +
                ": Qty=" +
                getQty() +
                "]";
    } //	toString
}
