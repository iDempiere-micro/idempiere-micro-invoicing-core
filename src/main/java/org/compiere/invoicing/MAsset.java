package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MMatchInv;
import org.compiere.accounting.MProduct;
import org.compiere.model.Asset;
import org.compiere.model.AssetAddition;
import org.compiere.model.AssetGroupAccounting;
import org.compiere.model.DepreciationWorkfile;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Project;
import org.compiere.model.I_M_InOutLine;
import org.compiere.order.MInOut;
import org.compiere.order.MInOutLine;
import org.compiere.product.MAssetChange;
import org.compiere.product.MAssetGroup;
import org.compiere.product.MAttributeSetInstance;
import org.compiere.product.X_A_Asset;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;
import org.idempiere.icommon.model.PersistentObject;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.convertString;
import static software.hsharp.core.util.DBKt.executeUpdateEx;

public class MAsset extends org.compiere.product.MAsset {

    public MAsset(int A_Asset_ID) {
        super(A_Asset_ID);
    }

    public MAsset(Row row) {
        super(row);
    } //	MAsset

    /**
     * Construct from MMatchInv
     *
     * @param match match invoice
     */
    public MAsset(MMatchInv match) {
        this(0);

        MInvoiceLine invoiceLine =
                new MInvoiceLine(match.getInvoiceLineId());
        MInOutLine inoutLine = new MInOutLine(match.getInOutLineId());

        setIsOwned(true);
        setIsInPosession(true);
        setAssetCreateDate(inoutLine.getInOut().getMovementDate());

        // Asset Group:
        int A_Asset_Group_ID = invoiceLine.getAssetGroupId();
        MProduct product =
                MProduct.get(invoiceLine.getProductId());
        if (A_Asset_Group_ID <= 0) {
            A_Asset_Group_ID = product.getAssetGroupId();
        }
        setAssetGroupId(A_Asset_Group_ID);
        setHelp(
                MsgKt.getMsg(
                        MClientKt.getClientWithAccounting().getADLanguage(),
                        "CreatedFromInvoiceLine",
                        new Object[]{invoiceLine.getInvoice().getDocumentNo(), invoiceLine.getLine()}));

        String name = "";
        if (inoutLine.getProductId() > 0) {
            name += product.getName() + "-";
            setProductId(inoutLine.getProductId());
            setAttributeSetInstanceId(inoutLine.getAttributeSetInstanceId());
        }
        I_C_BPartner bp = getBusinessPartnerService().getById(invoiceLine.getInvoice().getBusinessPartnerId());
        name += bp.getName() + "-" + invoiceLine.getInvoice().getDocumentNo();
        if (log.isLoggable(Level.FINE)) log.fine("name=" + name);
        setValue(name);
        setName(name);
        setDescription(invoiceLine.getDescription());
    }

    /**
     * Construct from MIFixedAsset (import)
     */
    public MAsset(MIFixedAsset ifa) {
        this(0);

        setOrgId(ifa.getOrgId()); // added by @win
        setIsOwned(true);
        setIsInPosession(true);

        String inventoryNo = ifa.getInventoryNo();
        if (inventoryNo != null) {
            inventoryNo = inventoryNo.trim();
            setInventoryNo(inventoryNo);
            setValue(inventoryNo);
        }
        setAssetCreateDate(ifa.getAssetServiceDate());
        MProduct product = ifa.getProduct();
        if (product != null) {
            setProductId(product.getProductId());
            setAssetGroupId(ifa.getAssetGroupId());
            MAttributeSetInstance asi = MAttributeSetInstance.create(product);
            setAttributeSetInstanceId(asi.getAttributeSetInstanceId());
        }

        setDateAcct(ifa.getDateAcct());
        setName(ifa.getName());
        setDescription(ifa.getDescription());
    }

    /**
     * @param project
     * @author Edwin Ang
     */
    public MAsset(I_C_Project project) {
        this(0);
        setIsOwned(true);
        setIsInPosession(true);
        setAssetCreateDate(new Timestamp(System.currentTimeMillis()));
        setHelp(
                MsgKt.getMsg(
                        MClientKt.getClientWithAccounting().getADLanguage(),
                        "CreatedFromProject",
                        new Object[]{project.getName()}));
        setDateAcct(new Timestamp(System.currentTimeMillis()));
        setDescription(project.getDescription());
    }

    public MAsset(MInOut mInOut, I_M_InOutLine sLine) {
        this(0);
        setIsOwned(false);
        setIsInPosession(false);
        setAssetCreateDate(new Timestamp(System.currentTimeMillis()));
        setHelp(
                MsgKt.getMsg(
                        MClientKt.getClientWithAccounting().getADLanguage(),
                        "CreatedFromShipment: ",
                        new Object[]{mInOut.getDocumentNo()}));
        setDateAcct(new Timestamp(System.currentTimeMillis()));
        setDescription(sLine.getDescription());
    }

    /**
     * Create Asset from Inventory
     *
     * @param inventory inventory
     * @param invLine   inventory line
     * @return A_Asset_ID
     */
    public MAsset(MInventory inventory, MInventoryLine invLine, BigDecimal qty) {
        super(0);
        setClientOrg(invLine);

        MProduct product = MProduct.get(invLine.getProductId());
        // Defaults from group:
        MAssetGroup assetGroup =
                MAssetGroup.get(
                        invLine.getProduct().getProductCategory().getAssetGroupId());
        if (assetGroup == null)
            assetGroup = MAssetGroup.get(product.getAssetGroupId());
        setAssetGroup(assetGroup);

        // setValue(prod)
        setName(product.getName());
        setHelp(invLine.getDescription());
        //	Header
        setAssetServiceDate(inventory.getMovementDate());
        setIsOwned(true);
        setIsInPosession(true);

        //	Product
        setProductId(product.getProductId());
        //	Guarantee & Version
        setVersionNo(product.getVersionNo());
        // ASI
        if (invLine.getAttributeSetInstanceId() != 0) {
            MAttributeSetInstance asi =
                    new MAttributeSetInstance(invLine.getAttributeSetInstanceId());
            setASI(asi);
        }
        setQty(qty);

        if (inventory.getBusinessActivityId() > 0) setActivityId(inventory.getBusinessActivityId());

        //

        if (MAssetType.isFixedAsset(this)) {
            setAssetStatus(X_A_Asset.A_ASSET_STATUS_New);
        } else {
            setAssetStatus(X_A_Asset.A_ASSET_STATUS_Activated);
            setProcessed(true);
        }

        // added by @win;
        setAssetStatus(X_A_Asset.A_ASSET_STATUS_New);
        // end added by @win

    }

    public static MAsset get(int A_Asset_ID) {
        return (MAsset) MBaseTableKt.getTable(MAsset.Table_Name).getPO(A_Asset_ID);
    } //	get

    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) {
            return success;
        }

        //
        // Set parent
        if (getParentAssetId() <= 0) {
            int A_Asset_ID = getAssetId();
            setParentAssetId(A_Asset_ID);
            executeUpdateEx(
                    "UPDATE A_Asset SET A_Parent_Asset_ID=A_Asset_ID WHERE A_Asset_ID=" + A_Asset_ID);
            if (log.isLoggable(Level.FINE)) log.fine("A_Parent_Asset_ID=" + getParentAssetId());
        }

        //
        // Set inventory number:
        String invNo = getInventoryNo();
        if (invNo == null || invNo.trim().length() == 0) {
            invNo = "" + getId();
            setInventoryNo(invNo);
            executeUpdateEx(
                    "UPDATE A_Asset SET InventoryNo="
                            + convertString(invNo)
                            + " WHERE A_Asset_ID="
                            + getAssetId());
            if (log.isLoggable(Level.FINE)) log.fine("InventoryNo=" + getInventoryNo());
        }

        // If new record, create accounting and workfile
        if (newRecord) {
            // @win: set value at asset group as default value for asset
            MAssetGroup assetgroup = new MAssetGroup(getAssetGroupId());
            String isDepreciated = (assetgroup.isDepreciated()) ? "Y" : "N";
            String isOwned = (assetgroup.isOwned()) ? "Y" : "N";
            setIsDepreciated(assetgroup.isDepreciated());
            setIsOwned(assetgroup.isOwned());
            executeUpdateEx(
                    "UPDATE A_Asset SET IsDepreciated='"
                            + isDepreciated
                            + "', isOwned ='"
                            + isOwned
                            + "' WHERE A_Asset_ID="
                            + getAssetId());
            // end @win

            // for each asset group acounting create an asset accounting and a workfile too
            for (AssetGroupAccounting assetgrpacct :
                    MAssetGroupAcct.forA_Asset_GroupId(getAssetGroupId())) {
                // Asset Accounting
                MAssetAcct assetacct = new MAssetAcct(this, assetgrpacct);
                assetacct.setOrgId(getOrgId()); // added by @win
                assetacct.saveEx();

                // Asset Depreciation Workfile
                MDepreciationWorkfile assetwk =
                        new MDepreciationWorkfile(this, assetacct.getPostingType(), assetgrpacct);
                assetwk.setOrgId(getOrgId()); // added by @win
                assetwk.setUseLifeYears(0);
                assetwk.setUseLifeMonths(0);
                assetwk.setUseLifeYearsFiscal(0);
                assetwk.setUseLifeMonthsFiscal(0);
                assetwk.saveEx();

                // Change Log
                MAssetChange.createAndSave("CRT", new PersistentObject[]{this, assetwk, assetacct});
            }

        } else {
            MAssetChange.createAndSave("UPD", new PersistentObject[]{this});
        }

        //
        // Update child.IsDepreciated flag
        if (!newRecord && isValueChanged(Asset.COLUMNNAME_IsDepreciated)) {
            final String sql =
                    "UPDATE "
                            + MDepreciationWorkfile.Table_Name
                            + " SET "
                            + MDepreciationWorkfile.COLUMNNAME_IsDepreciated
                            + "=?"
                            + " WHERE "
                            + MDepreciationWorkfile.COLUMNNAME_A_Asset_ID
                            + "=?";
            executeUpdateEx(sql, new Object[]{isDepreciated(), getAssetId()});
        }

        return true;
    } //	afterSave

    @Override
    protected boolean beforeDelete() {
        // delete addition
        {
            String sql =
                    "DELETE FROM "
                            + AssetAddition.Table_Name
                            + " WHERE "
                            + AssetAddition.COLUMNNAME_Processed
                            + "=? AND "
                            + AssetAddition.COLUMNNAME_A_Asset_ID
                            + "=?";
            int no = executeUpdateEx(sql, new Object[]{false, getAssetId()});
            if (log.isLoggable(Level.INFO)) log.info("@A_Asset_Addition@ @Deleted@ #" + no);
        }
        //
        // update invoice line
        {
            final String sql =
                    "UPDATE "
                            + MInvoiceLine.Table_Name
                            + " SET "
                            + " "
                            + MInvoiceLine.COLUMNNAME_A_Asset_ID
                            + "=?"
                            + ","
                            + MInvoiceLine.COLUMNNAME_A_Processed
                            + "=?"
                            + " WHERE "
                            + MInvoiceLine.COLUMNNAME_A_Asset_ID
                            + "=?";
            int no = executeUpdateEx(sql, new Object[]{null, false, getAssetId()});
            if (log.isLoggable(Level.INFO)) log.info("@C_InvoiceLine@ @Updated@ #" + no);
        }
        return true;
    } //      beforeDelete

    /**
     * Change asset status to newStatus
     *
     * @param newStatus see A_ASSET_STATUS_
     * @param date      state change date; if null context "#Date" will be used
     */
    @Override
    public void changeStatus(String newStatus, Timestamp date) {
        String status = getAssetStatus();
        if (log.isLoggable(Level.FINEST))
            log.finest("Entering: " + status + "->" + newStatus + ", date=" + date);

        //
        // If date is null, use context #Date
        if (date == null) {
            date = Env.getContextAsDate();
        }

        //
        //	Activation/Addition
        if (newStatus.equals(X_A_Asset.A_ASSET_STATUS_Activated)) {
            setAssetActivationDate(date);
        }
        //
        // Preservation
        if (newStatus.equals(X_A_Asset.A_ASSET_STATUS_Preservation)) {
            setAssetDisposalDate(date);
            // TODO: move to MAsetDisposal
            Collection<DepreciationWorkfile> workFiles =
                    MDepreciationWorkfile.forA_AssetId(getAssetId());
            for (DepreciationWorkfile assetwk : workFiles) {
                assetwk.truncDepreciation();
                assetwk.saveEx();
            }
        }
        // Disposal
        if (newStatus.equals(X_A_Asset.A_ASSET_STATUS_Disposed)) { // casat, vandut
            setAssetDisposalDate(date);
        }

        // Set new status
        setAssetStatus(newStatus);
    } //	changeStatus
}
