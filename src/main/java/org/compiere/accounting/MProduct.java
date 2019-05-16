package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingSchema;
import org.compiere.model.I_M_Cost;
import org.compiere.model.I_M_CostDetail;
import org.compiere.model.I_M_CostElement;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Product_Category_Acct;
import org.compiere.model.I_M_StorageOnHand;
import org.compiere.model.I_M_StorageReservation;
import org.compiere.model.I_M_Transaction;
import org.compiere.orm.Query;
import org.compiere.product.MAttributeSet;
import org.compiere.product.MAttributeSetInstance;
import org.compiere.product.X_I_Product;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import static org.compiere.orm.MTree_Base.TREETYPE_Product;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValueEx;

public class MProduct extends org.compiere.product.MProduct {

    /**
     * Cache
     */
    private static CCache<Integer, org.compiere.product.MProduct> s_cache =
            new CCache<>(
                    I_M_Product.Table_Name, 5); // 	5 minutes

    /**
     * Import Constructor
     *
     * @param impP import
     */
    public MProduct(X_I_Product impP) {
        super(impP);
    }

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param M_Product_ID id
     */
    public MProduct(int M_Product_ID) {
        super(M_Product_ID);
    }

    /**
     * Load constructor
     */
    public MProduct(Row row) {
        super(row);
    } //	MProduct

    /**
     * Get MProduct from Cache
     *
     * @param M_Product_ID id
     * @return MProduct or null
     */
    public static MProduct get(int M_Product_ID) {
        if (M_Product_ID <= 0) {
            return null;
        }
        Integer key = M_Product_ID;
        MProduct retValue = (MProduct) s_cache.get(key);
        if (retValue != null) {
            return retValue;
        }
        retValue = new MProduct(M_Product_ID);
        if (retValue.getId() != 0) {
            s_cache.put(key, retValue);
        }
        return retValue;
    } //	get

    private String verifyStorage() {
        BigDecimal qtyOnHand = Env.ZERO;
        BigDecimal qtyOrdered = Env.ZERO;
        BigDecimal qtyReserved = Env.ZERO;
        for (I_M_StorageOnHand ohs : MStorageOnHand.getOfProduct(getId())) {
            qtyOnHand = qtyOnHand.add(ohs.getQtyOnHand());
        }
        for (I_M_StorageReservation rs :
                MStorageReservation.getOfProduct(getId())) {
            if (rs.isSOTrx()) qtyReserved = qtyReserved.add(rs.getQty());
            else qtyOrdered = qtyOrdered.add(rs.getQty());
        }

        StringBuilder errMsg = new StringBuilder();
        if (qtyOnHand.signum() != 0) errMsg.append("@QtyOnHand@ = ").append(qtyOnHand);
        if (qtyOrdered.signum() != 0) errMsg.append(" - @QtyOrdered@ = ").append(qtyOrdered);
        if (qtyReserved.signum() != 0) errMsg.append(" - @QtyReserved@").append(qtyReserved);
        return errMsg.toString();
    }

    /**
     * HasInventoryOrCost
     *
     * @return true if it has Inventory or Cost
     */
    protected boolean hasInventoryOrCost() {
        // check if it has transactions
        boolean hasTrx =
                new Query<I_M_Transaction>(
                        I_M_Transaction.Table_Name,
                        I_M_Transaction.COLUMNNAME_M_Product_ID + "=?"
                )
                        .setOnlyActiveRecords(true)
                        .setParameters(getId())
                        .match();
        if (hasTrx) {
            return true;
        }

        // check if it has cost
        return new Query<I_M_CostDetail>(
                I_M_CostDetail.Table_Name,
                I_M_CostDetail.COLUMNNAME_M_Product_ID + "=?"
        )
                .setOnlyActiveRecords(true)
                .setParameters(getId())
                .match();

    }

    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;

        //	Value/Name change in Account
        if (!newRecord && (isValueChanged("Value") || isValueChanged("Name")))
            MAccount.updateValueDescription("M_Product_ID=" + getProductId());

        //	Name/Description Change in Asset	MAsset.setValueNameDescription
        if (!newRecord && (isValueChanged("Name") || isValueChanged("Description"))) {
            String sql =
                    "UPDATE A_Asset a "
                            + "SET (Name, Description)="
                            + "(SELECT SUBSTR((SELECT bp.Name FROM C_BPartner bp WHERE bp.C_BPartner_ID=a.C_BPartner_ID) || ' - ' || p.Name,1,60), p.Description "
                            + "FROM M_Product p "
                            + "WHERE p.M_Product_ID=a.M_Product_ID) "
                            + "WHERE IsActive='Y'"
                            //	+ " AND GuaranteeDate > SysDate"
                            + "  AND M_Product_ID="
                            + getProductId();
            int no = executeUpdate(sql);
            if (log.isLoggable(Level.FINE)) log.fine("Asset Description updated #" + no);
        }

        //	New - Acct, Tree, Old Costing
        if (newRecord) {
            insertAccounting(
                    "M_Product_Acct",
                    "M_Product_Category_Acct",
                    "p.M_Product_Category_ID=" + getProductCategoryId());
            insertTree(TREETYPE_Product);
        }
        if (newRecord || isValueChanged(I_M_Product.COLUMNNAME_Value))
            updateTree(TREETYPE_Product);

        //	New Costing
        if (newRecord || isValueChanged("M_Product_Category_ID")) MCost.create(this);

        return success;
    } //	afterSave

    @Override
    protected boolean beforeDelete() {
        if (I_M_Product.PRODUCTTYPE_Resource.equals(getProductType()) && getResourceID() > 0) {
            throw new AdempiereException("@S_Resource_ID@<>0");
        }
        //	Check Storage
        if (isStocked() || I_M_Product.PRODUCTTYPE_Item.equals(getProductType())) {
            String errMsg = verifyStorage();
            if (!Util.isEmpty(errMsg)) {
                log.saveError("Error", MsgKt.parseTranslation(errMsg));
                return false;
            }
        }
        //	delete costing
        MCost.delete(this);

        //
        return true;
    } //	beforeDelete

    /**
     * Check if ASI is mandatory
     *
     * @param isSOTrx is outgoing trx?
     * @return true if ASI is mandatory, false otherwise
     */
    @Override
    public boolean isASIMandatory(boolean isSOTrx) {
        //
        //	If CostingLevel is BatchLot ASI is always mandatory - check all client acct schemas
        AccountingSchema[] mass = MAcctSchema.getClientAcctSchema(getClientId());
        for (AccountingSchema as : mass) {
            String cl = getCostingLevel(as);
            if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(cl)) {
                return true;
            }
        }
        //
        // Check Attribute Set settings
        int M_AttributeSet_ID = getAttributeSetId();
        if (M_AttributeSet_ID != 0) {
            MAttributeSet mas = MAttributeSet.get(M_AttributeSet_ID);
            if (mas == null || !mas.isInstanceAttribute()) return false;
                // Outgoing transaction
            else if (isSOTrx) return mas.isMandatory();
                // Incoming transaction
            else // isSOTrx == false
                return mas.isMandatoryAlways();
        }
        //
        // Default not mandatory
        return false;
    }

    /**
     * Get Product Costing Level
     *
     * @param as accounting schema
     * @return product costing level
     */
    public String getCostingLevel(AccountingSchema as) {
        String costingLevel = null;
        I_M_Product_Category_Acct pca =
                MProductCategoryAcct.get(getProductCategoryId(), as.getId());
        if (pca != null) {
            costingLevel = pca.getCostingLevel();
        }
        if (costingLevel == null) {
            costingLevel = as.getCostingLevel();
        }
        return costingLevel;
    }

    /**
     * Get Product Costing Method
     *
     * @return product costing method
     */
    public String getCostingMethod(AccountingSchema as) {
        String costingMethod = null;
        I_M_Product_Category_Acct pca =
                MProductCategoryAcct.get(getProductCategoryId(), as.getId());
        if (pca != null) {
            costingMethod = pca.getCostingMethod();
        }
        if (costingMethod == null) {
            costingMethod = as.getCostingMethod();
        }
        return costingMethod;
    }

    public I_M_Cost getCostingRecord(AccountingSchema as, int AD_Org_ID, int M_ASI_ID, String costingMethod) {
        String costingLevel = getCostingLevel(as);
        if (MAcctSchema.COSTINGLEVEL_Client.equals(costingLevel)) {
            AD_Org_ID = 0;
            M_ASI_ID = 0;
        } else if (MAcctSchema.COSTINGLEVEL_Organization.equals(costingLevel)) M_ASI_ID = 0;
        else if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(costingLevel)) {
            AD_Org_ID = 0;
            if (M_ASI_ID == 0) return null;
        }
        I_M_CostElement ce = MCostElement.getMaterialCostElement(costingMethod, AD_Org_ID);
        if (ce == null) {
            return null;
        }
        I_M_Cost cost = MCost.get(this, M_ASI_ID, as, AD_Org_ID, ce.getCostElementId());
        return cost.isNew() ? null : cost;
    }

    @Override
    protected boolean beforeSave(boolean newRecord) {
        //	Check Storage
        if (!newRecord
                && //
                ((isValueChanged("IsActive") && !isActive()) // 	now not active
                        || (isValueChanged("IsStocked") && !isStocked()) // 	now not stocked
                        || (isValueChanged("ProductType") // 	from Item
                        && I_M_Product.PRODUCTTYPE_Item.equals(getValueOld("ProductType"))))) {
            String errMsg = verifyStorage();
            if (!Util.isEmpty(errMsg)) {
                log.saveError("Error", MsgKt.parseTranslation(errMsg));
                return false;
            }
        } //	storage

        // it checks if UOM has been changed , if so disallow the change if the condition is true.
        if ((!newRecord) && isValueChanged("C_UOM_ID") && hasInventoryOrCost()) {
            log.saveError("Error", MsgKt.getMsg("SaveUomError"));
            return false;
        }

        //	Reset Stocked if not Item
        // AZ Goodwill: Bug Fix isStocked always return false
        // if (isStocked() && !PRODUCTTYPE_Item.equals(getProductType()))
        if (!I_M_Product.PRODUCTTYPE_Item.equals(getProductType())) setIsStocked(false);

        //	UOM reset
        if (m_precision != null && isValueChanged("C_UOM_ID")) m_precision = null;

        // AttributeSetInstance reset
        if (getAttributeSetInstanceId() > 0
                && isValueChanged(I_M_Product.COLUMNNAME_M_AttributeSet_ID)) {
            MAttributeSetInstance asi =
                    new MAttributeSetInstance(getAttributeSetInstanceId());
            if (asi.getAttributeSetId() != getAttributeSetId()) setAttributeSetInstanceId(0);
        }
        if (!newRecord && isValueChanged(I_M_Product.COLUMNNAME_M_AttributeSetInstance_ID)) {
            // IDEMPIERE-2752 check if the ASI is referenced in other products before trying to delete it
            int oldasiid = getValueOldAsInt(I_M_Product.COLUMNNAME_M_AttributeSetInstance_ID);
            if (oldasiid > 0) {
                MAttributeSetInstance oldasi =
                        new MAttributeSetInstance(

                                getValueOldAsInt(I_M_Product.COLUMNNAME_M_AttributeSetInstance_ID));
                int cnt =
                        getSQLValueEx(
                                "SELECT COUNT(*) FROM M_Product WHERE M_AttributeSetInstance_ID=?",
                                oldasi.getAttributeSetInstanceId());
                if (cnt == 1) {
                    // Delete the old m_attributesetinstance
                    try {
                        oldasi.deleteEx(true);
                    } catch (AdempiereException ex) {
                        log.saveError("Error", "Error deleting the AttributeSetInstance");
                        return false;
                    }
                }
            }
        }

        return true;
    } //	beforeSave

    @Override
    public List<I_M_StorageOnHand> getStorageOnHand() {
        return MStorageOnHand.getOfProduct(getId());
    }

}
