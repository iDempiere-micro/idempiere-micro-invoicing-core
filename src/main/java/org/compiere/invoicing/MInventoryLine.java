package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MAcctSchema;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MCostElement;
import org.compiere.accounting.MProduct;
import org.compiere.model.AccountingSchema;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.IDocLine;
import org.compiere.model.I_M_Cost;
import org.compiere.model.I_M_Inventory;
import org.compiere.model.I_M_InventoryLine;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.process.DocAction;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

import static software.hsharp.core.util.DBKt.getSQLValue;

/**
 * Physical Inventory Line Model
 *
 * @author Jorg Janke
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * <li>BF [ 1817757 ] Error on saving MInventoryLine in a custom environment
 * <li>BF [ 1722982 ] Error with inventory when you enter count qty in negative
 * @version $Id: MInventoryLine.java,v 1.3 2006/07/30 00:51:02 jjanke Exp $
 */
public class MInventoryLine extends X_M_InventoryLine implements IDocLine {
    /**
     *
     */
    private static final long serialVersionUID = -3864175464877913555L;
    /**
     * Parent
     */
    private MInventory m_parent = null;
    /**
     * Product
     */
    private MProduct m_product = null;

    /**
     * ************************************************************************ Default Constructor
     *
     * @param M_InventoryLine_ID line
     */
    public MInventoryLine(int M_InventoryLine_ID) {
        super(M_InventoryLine_ID);
        if (M_InventoryLine_ID == 0) {
            setLine(0);
            setAttributeSetInstanceId(0); // 	FK
            setInventoryType(X_M_InventoryLine.INVENTORYTYPE_InventoryDifference);
            setQtyBook(Env.ZERO);
            setQtyCount(Env.ZERO);
            setProcessed(false);
        }
    } //	MInventoryLine

    /**
     * Load Constructor
     */
    public MInventoryLine(Row row) {
        super(row);
    } //	MInventoryLine

    /**
     * Detail Constructor. Locator/Product/AttributeSetInstance must be unique
     *
     * @param inventory                 parent
     * @param M_Locator_ID              locator
     * @param M_Product_ID              product
     * @param M_AttributeSetInstance_ID instance
     * @param QtyBook                   book value
     * @param QtyCount                  count value
     * @param QtyInternalUse            internal use value
     */
    public MInventoryLine(
            MInventory inventory,
            int M_Locator_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            BigDecimal QtyBook,
            BigDecimal QtyCount,
            BigDecimal QtyInternalUse) {
        this(0);
        if (inventory.getId() == 0) throw new IllegalArgumentException("Header not saved");
        m_parent = inventory;
        setInventoryId(inventory.getInventoryId()); // 	Parent
        setClientOrg(inventory.getClientId(), inventory.getOrgId());
        setLocatorId(M_Locator_ID); // 	FK
        setProductId(M_Product_ID); // 	FK
        setAttributeSetInstanceId(M_AttributeSetInstance_ID);
        //
        if (QtyBook != null) setQtyBook(QtyBook);
        if (QtyCount != null && QtyCount.signum() != 0) setQtyCount(QtyCount);
        if (QtyInternalUse != null && QtyInternalUse.signum() != 0) setQtyInternalUse(QtyInternalUse);
        // m_isManualEntry = false;
    } //	MInventoryLine

    public MInventoryLine(
            MInventory inventory,
            int M_Locator_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            BigDecimal QtyBook,
            BigDecimal QtyCount) {
        this(inventory, M_Locator_ID, M_Product_ID, M_AttributeSetInstance_ID, QtyBook, QtyCount, null);
    }

    /**
     * Get Product
     *
     * @return product or null if not defined
     */
    public MProduct getProduct() {
        int M_Product_ID = getProductId();
        if (M_Product_ID == 0) return null;
        if (m_product != null && m_product.getProductId() != M_Product_ID)
            m_product = null; // 	reset
        if (m_product == null) m_product = MProduct.get(M_Product_ID);
        return m_product;
    } //	getProduct

    /**
     * Set Count Qty - enforce UOM
     *
     * @param QtyCount qty
     */
    @Override
    public void setQtyCount(BigDecimal QtyCount) {
        if (QtyCount != null) {
            MProduct product = getProduct();
            if (product != null) {
                int precision = product.getUOMPrecision();
                QtyCount = QtyCount.setScale(precision, BigDecimal.ROUND_HALF_UP);
            }
        }
        super.setQtyCount(QtyCount);
    } //	setQtyCount

    /**
     * Set Internal Use Qty - enforce UOM
     *
     * @param QtyInternalUse qty
     */
    @Override
    public void setQtyInternalUse(BigDecimal QtyInternalUse) {
        if (QtyInternalUse != null) {
            MProduct product = getProduct();
            if (product != null) {
                int precision = product.getUOMPrecision();
                QtyInternalUse = QtyInternalUse.setScale(precision, BigDecimal.ROUND_HALF_UP);
            }
        }
        super.setQtyInternalUse(QtyInternalUse);
    } //	setQtyInternalUse

    /**
     * Add to Description
     *
     * @param description text
     */
    public void addDescription(String description) {
        String desc = getDescription();
        if (desc == null) setDescription(description);
        else {
            setDescription(desc + " | " + description);
        }
    } //	addDescription

    /**
     * Get Parent
     *
     * @return parent
     */
    public I_M_Inventory getParent() {
        if (m_parent == null) m_parent = new MInventory(getInventoryId());
        return m_parent;
    } //	getParent

    /**
     * Get Parent
     *
     * @param parent parent
     */
    protected void setParent(MInventory parent) {
        m_parent = parent;
    } //	setParent

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MInventoryLine[" + getId() +
                "-M_Product_ID=" +
                getProductId() +
                ",QtyCount=" +
                getQtyCount() +
                ",QtyInternalUse=" +
                getQtyInternalUse() +
                ",QtyBook=" +
                getQtyBook() +
                ",M_AttributeSetInstance_ID=" +
                getAttributeSetInstanceId() +
                "]";
    } //	toString

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true if can be saved
     */
    protected boolean beforeSave(boolean newRecord) {
        if (newRecord && getParent().isComplete()) {
            log.saveError("ParentComplete", MsgKt.translate("M_InventoryLine"));
            return false;
        }

        //	Set Line No
        if (getLine() == 0) {
            String sql =
                    "SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM M_InventoryLine WHERE M_Inventory_ID=?";
            int ii = getSQLValue(sql, getInventoryId());
            setLine(ii);
        }

        //	Enforce Qty UOM
        if (newRecord || isValueChanged("QtyCount")) setQtyCount(getQtyCount());
        if (newRecord || isValueChanged("QtyInternalUse")) setQtyInternalUse(getQtyInternalUse());

        MDocType dt = MDocTypeKt.getDocumentType(getParent().getDocumentTypeId());
        String docSubTypeInv = dt.getDocSubTypeInv();

        if (MDocType.DOCSUBTYPEINV_InternalUseInventory.equals(docSubTypeInv)) {

            // Internal Use Inventory validations
            if (!X_M_InventoryLine.INVENTORYTYPE_ChargeAccount.equals(getInventoryType()))
                setInventoryType(X_M_InventoryLine.INVENTORYTYPE_ChargeAccount);
            //
            if (getChargeId() == 0) {
                log.saveError("InternalUseNeedsCharge", "");
                return false;
            }
            // error if book or count are filled on an internal use inventory
            // i.e. coming from import or web services
            if (getQtyBook().signum() != 0) {
                log.saveError("Quantity", MsgKt.getElementTranslation(I_M_InventoryLine.COLUMNNAME_QtyBook));
                return false;
            }
            if (getQtyCount().signum() != 0) {
                log.saveError("Quantity", MsgKt.getElementTranslation(I_M_InventoryLine.COLUMNNAME_QtyCount));
                return false;
            }
            if (getQtyInternalUse().signum() == 0
                    && !getParent().getDocAction().equals(DocAction.Companion.getACTION_Void())) {
                log.saveError(
                        "FillMandatory", MsgKt.getElementTranslation(I_M_InventoryLine.COLUMNNAME_QtyInternalUse));
                return false;
            }

        } else if (MDocType.DOCSUBTYPEINV_PhysicalInventory.equals(docSubTypeInv)) {

            // Physical Inventory validations
            if (X_M_InventoryLine.INVENTORYTYPE_ChargeAccount.equals(getInventoryType())) {
                if (getChargeId() == 0) {
                    log.saveError("FillMandatory", MsgKt.getElementTranslation("C_Charge_ID"));
                    return false;
                }
            } else if (getChargeId() != 0) {
                setChargeId(0);
            }
            if (getQtyInternalUse().signum() != 0) {
                log.saveError(
                        "Quantity", MsgKt.getElementTranslation(I_M_InventoryLine.COLUMNNAME_QtyInternalUse));
                return false;
            }
        } else if (MDocType.DOCSUBTYPEINV_CostAdjustment.equals(docSubTypeInv)) {
            int M_ASI_ID = getAttributeSetInstanceId();
            MProduct product = new MProduct(getProductId());
            ClientWithAccounting client = MClientKt.getClientWithAccounting();
            AccountingSchema as = client.getAcctSchema();
            String costingLevel = product.getCostingLevel(as);
            if (MAcctSchema.COSTINGLEVEL_BatchLot.equals(costingLevel)) {
                if (M_ASI_ID == 0) {
                    log.saveError(
                            "FillMandatory",
                            MsgKt.getElementTranslation(I_M_InventoryLine.COLUMNNAME_M_AttributeSetInstance_ID));
                    return false;
                }
            }

            String costingMethod = getParent().getCostingMethod();
            int AD_Org_ID = getOrgId();
            I_M_Cost cost = product.getCostingRecord(as, AD_Org_ID, M_ASI_ID, costingMethod);
            if (cost == null) {
                if (!MCostElement.COSTINGMETHOD_StandardCosting.equals(costingMethod)) {
                    log.saveError("NoCostingRecord", "");
                    return false;
                }
            }
            setLocatorId(0);
        } else {
            log.saveError("Error", "Document inventory subtype not configured, cannot complete");
            return false;
        }

        //	Set AD_Org to parent if not charge
        if (getChargeId() == 0) setOrgId(getParent().getOrgId());

        return true;
    } //	beforeSave

    /**
     * Is Internal Use Inventory
     *
     * @return true if is internal use inventory
     */
    public boolean isInternalUseInventory() {
        //  IDEMPIERE-675
        MDocType dt = MDocTypeKt.getDocumentType(getParent().getDocumentTypeId());
        String docSubTypeInv = dt.getDocSubTypeInv();
        return (MDocType.DOCSUBTYPEINV_InternalUseInventory.equals(docSubTypeInv));
    }

    /**
     * Get Movement Qty (absolute value)
     * <li>negative value means outgoing trx
     * <li>positive value means incoming trx
     *
     * @return movement qty
     */
    public BigDecimal getMovementQty() {
        if (isInternalUseInventory()) {
            return getQtyInternalUse().negate();
        } else {
            return getQtyCount().subtract(getQtyBook());
        }
    }

    /**
     * @return true if is an outgoing transaction
     */
    public boolean isSOTrx() {
        return getMovementQty().signum() < 0;
    }
} //	MInventoryLine
