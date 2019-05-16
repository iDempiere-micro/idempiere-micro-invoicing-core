package org.compiere.accounting

import kotliquery.Row
import org.compiere.product.MProduct
import software.hsharp.core.util.DB
import software.hsharp.core.util.asResource
import software.hsharp.core.util.forUpdate
import software.hsharp.core.util.queryOf
import java.sql.Timestamp

/**
 * Get Storage Info for Warehouse or locator
 *
 * @param warehouseId ignore if M_Locator_ID > 0
 * @param productId product
 * @param attributeSetInstanceId instance id, 0 to retrieve all instance
 * @param minGuaranteeDate optional minimum guarantee date if all attribute instances
 * @param FiFo first in-first-out
 * @param positiveOnly if true, only return storage records with qtyOnHand > 0
 * @param locatorId optional locator id
 * @param lockForUpdate
 * @return existing - ordered by location priority (desc) and/or guarantee date
 */
fun getStorageInfoForWarehouseOrLocator(
    warehouseId: Int,
    productId: Int,
    attributeSetInstanceId: Int,
    minGuaranteeDate: Timestamp?,
    FiFo: Boolean,
    positiveOnly: Boolean,
    locatorId: Int,
    lockForUpdate: Boolean
): Array<MStorageOnHand> {
    if (warehouseId == 0 && locatorId == 0 || productId == 0)
        return emptyArray()

    val allAttributeInstances = attributeSetInstanceId == 0

    val sql = "/sql/getStorageInfoForWarehouseOrLocatorBase.sql".asResource {
        var partialSql = it

        partialSql += if (locatorId > 0)
            "WHERE l.M_Locator_ID = ?"
        else
            "WHERE l.M_Warehouse_ID=?"
        partialSql += " AND s.M_Product_ID=?" + " AND COALESCE(s.M_AttributeSetInstance_ID,0)=? "
        partialSql += if (positiveOnly) {
            " AND s.QtyOnHand > 0 "
        } else {
            " AND s.QtyOnHand <> 0 "
        }
        partialSql += "ORDER BY l.PriorityNo DESC, DateMaterialPolicy "
        partialSql += if (!FiFo)
            " DESC, s.M_AttributeSetInstance_ID DESC "
        else
            ", s.M_AttributeSetInstance_ID "
        // 	All Attribute Set Instances
        if (allAttributeInstances) {
            partialSql = ("SELECT s.M_Product_ID,s.M_Locator_ID,s.M_AttributeSetInstance_ID," +
                    " s.AD_Client_ID,s.AD_Org_ID,s.IsActive,s.Created,s.CreatedBy,s.Updated,s.UpdatedBy," +
                    " s.QtyOnHand,s.DateLastInventory,s.M_StorageOnHand_UU,s.DateMaterialPolicy " +
                    " FROM M_StorageOnHand s" +
                    " INNER JOIN M_Locator l ON (l.M_Locator_ID=s.M_Locator_ID)" +
                    " LEFT OUTER JOIN M_AttributeSetInstance asi ON (s.M_AttributeSetInstance_ID=asi.M_AttributeSetInstance_ID) ")
            partialSql += if (locatorId > 0)
                "WHERE l.M_Locator_ID = ?"
            else
                "WHERE l.M_Warehouse_ID=?"
            partialSql += " AND s.M_Product_ID=? "
            partialSql += if (positiveOnly) {
                " AND s.QtyOnHand > 0 "
            } else {
                " AND s.QtyOnHand <> 0 "
            }

            if (minGuaranteeDate != null) {
                partialSql += "AND (asi.GuaranteeDate IS NULL OR asi.GuaranteeDate>?) "
            }

            val product = MProduct.get(productId)

            if (product.isUseGuaranteeDateForMPolicy) {
                partialSql += "ORDER BY l.PriorityNo DESC, COALESCE(asi.GuaranteeDate,s.DateMaterialPolicy)"
                partialSql += if (!FiFo)
                    " DESC, s.M_AttributeSetInstance_ID DESC "
                else
                    ", s.M_AttributeSetInstance_ID "
            } else {
                partialSql += "ORDER BY l.PriorityNo DESC, l.M_Locator_ID, s.DateMaterialPolicy"
                partialSql += if (!FiFo)
                    " DESC, s.M_AttributeSetInstance_ID DESC "
                else
                    ", s.M_AttributeSetInstance_ID "
            }

            partialSql += ", s.QtyOnHand DESC"
        }
        partialSql
    }

    val parameters =
        listOf(if (locatorId > 0) locatorId else warehouseId, productId) +
        (if (!allAttributeInstances) listOf(attributeSetInstanceId)
        else if (minGuaranteeDate != null) listOf(minGuaranteeDate) else emptyList())

    fun load(row: Row): MStorageOnHand {
        val storage = MStorageOnHand(row)
        if (lockForUpdate) {
            forUpdate(storage)
        }
        return storage
    }

    val query = queryOf(sql, parameters).map { row -> load(row) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getWarehouse

/**
 * Get Storage Info for Warehouse or locator
 *
 * @param M_Warehouse_ID ignore if M_Locator_ID > 0
 * @param M_Product_ID product
 * @param M_AttributeSetInstance_ID instance id, 0 to retrieve storages that don't have asi, -1 to
 * retrieve all instance
 * @param minGuaranteeDate optional minimum guarantee date if all attribute instances
 * @param FiFo first in-first-out
 * @param M_Locator_ID optional locator id
 * @param lockForUpdate
 * @return existing - ordered by location priority (desc) and/or guarantee date
 */
fun getStorageInfoForWarehouseOrLocatorNegative(
    M_Warehouse_ID: Int,
    M_Product_ID: Int,
    M_AttributeSetInstance_ID: Int,
    minGuaranteeDate: Timestamp?,
    FiFo: Boolean,
    M_Locator_ID: Int,
    lockForUpdate: Boolean
): Array<MStorageOnHand> {
    if (M_Warehouse_ID == 0 && M_Locator_ID == 0 || M_Product_ID == 0)
        return emptyArray()

    val sql = "/sql/getStorageInfoForWarehouseOrLocatorNegativeBase.sql".asResource {
        var partialSql = it
        partialSql += if (M_Locator_ID > 0)
            "WHERE l.M_Locator_ID = ?"
        else
            "WHERE l.M_Warehouse_ID=?"
        partialSql += " AND s.M_Product_ID=? " + " AND s.QtyOnHand < 0 "

        if (minGuaranteeDate != null) {
            partialSql += "AND (asi.GuaranteeDate IS NULL OR asi.GuaranteeDate>?) "
        }

        if (M_AttributeSetInstance_ID > 0) {
            partialSql += "AND s.M_AttributeSetInstance_ID=? "
        } else if (M_AttributeSetInstance_ID == 0) {
            partialSql += "AND (s.M_AttributeSetInstance_ID=0 OR s.M_AttributeSetInstance_ID IS NULL) "
        }

        val product = MProduct.get(M_Product_ID)

        if (product.isUseGuaranteeDateForMPolicy) {
            partialSql += "ORDER BY l.PriorityNo DESC, " + "asi.GuaranteeDate"
            if (!FiFo) partialSql += " DESC"
        } else {
            partialSql += "ORDER BY l.PriorityNo DESC, l.M_Locator_ID, s.DateMaterialPolicy"
            if (!FiFo)
                partialSql += " DESC, s.M_AttributeSetInstance_ID DESC "
            else
                partialSql += ", s.M_AttributeSetInstance_ID "
        }

        partialSql += ", s.QtyOnHand DESC"
        partialSql
    }

    val parameters =
        listOf(if (M_Locator_ID > 0) M_Locator_ID else M_Warehouse_ID, M_Product_ID) +
                (if (minGuaranteeDate != null)
                    listOf(minGuaranteeDate) else emptyList()) +
                (if (M_AttributeSetInstance_ID > 0)
                    listOf(M_AttributeSetInstance_ID) else emptyList()
                        )

    fun load(row: Row): MStorageOnHand? {
        if (row.bigDecimal(11).signum() != 0) {
            val storage = MStorageOnHand(row)
            if (lockForUpdate) {
                forUpdate(storage)
            }
            return storage
        }
        return null
    }

    val query = queryOf(sql, parameters).map { load(it) }.asList
    return DB.current.run(query).toTypedArray()
} // 	getWarehouse