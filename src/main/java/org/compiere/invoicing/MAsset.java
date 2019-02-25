package org.compiere.invoicing;

import org.compiere.accounting.MClient;
import org.compiere.accounting.MMatchInv;
import org.compiere.accounting.MProduct;
import org.compiere.crm.MBPartner;
import org.compiere.model.I_A_Asset;
import org.compiere.model.I_A_Asset_Addition;
import org.compiere.model.I_C_Project;
import org.compiere.order.MInOut;
import org.compiere.order.MInOutLine;
import org.compiere.orm.MTable;
import org.compiere.product.MAssetChange;
import org.compiere.product.MAssetGroup;
import org.compiere.product.MAttributeSetInstance;
import org.compiere.product.X_A_Asset;
import org.compiere.util.Msg;
import org.idempiere.common.util.Env;
import org.idempiere.icommon.model.IPO;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.TO_STRING;
import static software.hsharp.core.util.DBKt.executeUpdateEx;

public class MAsset extends org.compiere.product.MAsset {

    public MAsset(Properties ctx, int A_Asset_ID) {
        super(ctx, A_Asset_ID);
    }

    public MAsset(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    } //	MAsset

    /**
     * Construct from MMatchInv
     *
     * @param match match invoice
     */
    public MAsset(MMatchInv match) {
        this(match.getCtx(), 0);

        MInvoiceLine invoiceLine =
                new MInvoiceLine(getCtx(), match.getC_InvoiceLine_ID());
        MInOutLine inoutLine = new MInOutLine(getCtx(), match.getM_InOutLine_ID());

        setIsOwned(true);
        setIsInPosession(true);
        setAssetCreateDate(inoutLine.getM_InOut().getMovementDate());

        // Asset Group:
        int A_Asset_Group_ID = invoiceLine.getA_Asset_Group_ID();
        org.compiere.accounting.MProduct product =
                org.compiere.accounting.MProduct.get(getCtx(), invoiceLine.getM_Product_ID());
        if (A_Asset_Group_ID <= 0) {
            A_Asset_Group_ID = product.getA_Asset_Group_ID();
        }
        setAssetGroupId(A_Asset_Group_ID);
        setHelp(
                Msg.getMsg(
                        MClient.get(getCtx()).getADLanguage(),
                        "CreatedFromInvoiceLine",
                        new Object[]{invoiceLine.getC_Invoice().getDocumentNo(), invoiceLine.getLine()}));

        String name = "";
        if (inoutLine.getM_Product_ID() > 0) {
            name += product.getName() + "-";
            setProductId(inoutLine.getM_Product_ID());
            setAttributeSetInstanceId(inoutLine.getMAttributeSetInstance_ID());
        }
        MBPartner bp = new MBPartner(getCtx(), invoiceLine.getC_Invoice().getC_BPartner_ID());
        name += bp.getName() + "-" + invoiceLine.getC_Invoice().getDocumentNo();
        if (log.isLoggable(Level.FINE)) log.fine("name=" + name);
        setValue(name);
        setName(name);
        setDescription(invoiceLine.getDescription());
    }

    /**
     * Construct from MIFixedAsset (import)
     *
     * @param match match invoice
     */
    public MAsset(MIFixedAsset ifa) {
        this(ifa.getCtx(), 0);

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
        // setAssetServiceDate(ifa.getAssetServiceDate()); //commented by @win
    /* commented by @win
    setA_Asset_Class_ID(ifa.getA_Asset_Class_ID());
    */
        // commented by @win
        org.compiere.accounting.MProduct product = ifa.getProduct();
        if (product != null) {
            setProductId(product.getM_Product_ID());
            setAssetGroupId(ifa.getA_Asset_Group_ID());
            MAttributeSetInstance asi = MAttributeSetInstance.create(getCtx(), product);
            setAttributeSetInstanceId(asi.getMAttributeSetInstance_ID());
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
        this(project.getCtx(), 0);
        setIsOwned(true);
        setIsInPosession(true);
        setAssetCreateDate(new Timestamp(System.currentTimeMillis()));
        setHelp(
                Msg.getMsg(
                        MClient.get(getCtx()).getADLanguage(),
                        "CreatedFromProject",
                        new Object[]{project.getName()}));
        setDateAcct(new Timestamp(System.currentTimeMillis()));
        setDescription(project.getDescription());
    }

    public MAsset(MInOut mInOut, MInOutLine sLine, int deliveryCount) {
        this(mInOut.getCtx(), 0);
        setIsOwned(false);
        setIsInPosession(false);
        setAssetCreateDate(new Timestamp(System.currentTimeMillis()));
        setHelp(
                Msg.getMsg(
                        MClient.get(getCtx()).getADLanguage(),
                        "CreatedFromShipment: ",
                        new Object[]{mInOut.getDocumentNo()}));
        setDateAcct(new Timestamp(System.currentTimeMillis()));
        setDescription(sLine.getDescription());
    }

    /**
     * Create Asset from Inventory
     *
     * @param inventory     inventory
     * @param invLine       inventory line
     * @param deliveryCount 0 or number of delivery
     * @return A_Asset_ID
     */
    public MAsset(MInventory inventory, MInventoryLine invLine, BigDecimal qty, BigDecimal costs) {
        super(invLine.getCtx(), 0);
        setClientOrg(invLine);

        org.compiere.accounting.MProduct product = MProduct.get(getCtx(), invLine.getM_Product_ID());
        // Defaults from group:
        MAssetGroup assetGroup =
                MAssetGroup.get(
                        invLine.getCtx(), invLine.getM_Product().getM_Product_Category().getA_Asset_Group_ID());
        if (assetGroup == null)
            assetGroup = MAssetGroup.get(invLine.getCtx(), product.getA_Asset_Group_ID());
        setAssetGroup(assetGroup);

        // setValue(prod)
        setName(product.getName());
        setHelp(invLine.getDescription());
        //	Header
        setAssetServiceDate(inventory.getMovementDate());
        setIsOwned(true);
        setIsInPosession(true);

        //	Product
        setProductId(product.getM_Product_ID());
        //	Guarantee & Version
        // setGuaranteeDate(TimeUtil.addDays(shipment.getMovementDate(), product.getGuaranteeDays()));
        setVersionNo(product.getVersionNo());
        // ASI
        if (invLine.getMAttributeSetInstance_ID() != 0) {
            MAttributeSetInstance asi =
                    new MAttributeSetInstance(getCtx(), invLine.getMAttributeSetInstance_ID());
            setASI(asi);
        }
        // setSerNo(invLine.getSerNo());
        setQty(qty);

        // Costs:
        // setA_Asset_Cost(costs);  //commented by @win, set at asset addition

        // Activity
    /*
    if (invLine.getC_Activity_ID() > 0)
    	setC_Activity_ID(invLine.getC_Activity_ID());
    */
        if (inventory.getC_Activity_ID() > 0) setActivityId(inventory.getC_Activity_ID());

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

    public static MAsset getFromShipment(Properties ctx, int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public static MAsset get(Properties ctx, int A_Asset_ID) {
        return (MAsset) MTable.get(ctx, MAsset.Table_Name).getPO(A_Asset_ID);
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
                            + TO_STRING(invNo)
                            + " WHERE A_Asset_ID="
                            + getAssetId());
            if (log.isLoggable(Level.FINE)) log.fine("InventoryNo=" + getInventoryNo());
        }

        // If new record, create accounting and workfile
        if (newRecord) {
            // @win: set value at asset group as default value for asset
            MAssetGroup assetgroup = new MAssetGroup(getCtx(), getAssetGroupId());
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
            for (MAssetGroupAcct assetgrpacct :
                    MAssetGroupAcct.forA_Asset_Group_ID(getCtx(), getAssetGroupId())) {
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
                assetwk.setUseLifeMonths_F(0);
                assetwk.saveEx();

                // Change Log
                MAssetChange.createAndSave(getCtx(), "CRT", new IPO[]{this, assetwk, assetacct});
            }

        } else {
            MAssetChange.createAndSave(getCtx(), "UPD", new IPO[]{this});
        }

        //
        // Update child.IsDepreciated flag
        if (!newRecord && is_ValueChanged(I_A_Asset.COLUMNNAME_IsDepreciated)) {
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
                            + I_A_Asset_Addition.Table_Name
                            + " WHERE "
                            + I_A_Asset_Addition.COLUMNNAME_Processed
                            + "=? AND "
                            + I_A_Asset_Addition.COLUMNNAME_A_Asset_ID
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
            date = Env.getContextAsDate(getCtx(), "#Date");
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
            Collection<MDepreciationWorkfile> workFiles =
                    MDepreciationWorkfile.forA_Asset_ID(getCtx(), getAssetId());
            for (MDepreciationWorkfile assetwk : workFiles) {
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
