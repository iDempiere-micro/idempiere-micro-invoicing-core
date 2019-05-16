package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MPeriod;
import org.compiere.model.AssetAccounting;
import org.compiere.model.DepreciationExpense;
import org.compiere.model.DepreciationWorkfile;
import org.compiere.model.IDocLine;
import org.compiere.orm.MDocType;
import org.compiere.orm.Query;
import org.compiere.orm.TimeUtil;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.CLogger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdateEx;


public class MDepreciationExp extends X_A_Depreciation_Exp implements IDocLine {

    /**
     *
     */
    private static final long serialVersionUID = 6731366890875525147L;

    private static CLogger s_log = CLogger.getCLogger(MDepreciationExp.class);
    private CLogger log = CLogger.getCLogger(this.getClass());

    /**
     * Standard Constructor
     */
    public MDepreciationExp(int A_Depreciation_Exp_ID) {
        super(A_Depreciation_Exp_ID);
    }

    /**
     * Load Constructor
     */
    public MDepreciationExp(Row row) {
        super(row);
    }

    /**
     * Create entry
     */
    public static MDepreciationExp createEntry(

            String entryType,
            int A_Asset_ID,
            int A_Period,
            Timestamp DateAcct,
            String postingType,
            int drAcct,
            int crAcct,
            BigDecimal expense,
            String description,
            MDepreciationWorkfile assetwk) {
        MDepreciationExp depexp = new MDepreciationExp(0);
        depexp.setEntryType(entryType);
        depexp.setAssetId(A_Asset_ID);
        depexp.setDRAccountId(drAcct);
        depexp.setCRAccountId(crAcct);
        depexp.setAccountNumberAcct(drAcct); // TODO: DELETEME
        depexp.setPostingType(postingType);
        depexp.setExpense(expense);
        depexp.setDescription(MsgKt.parseTranslation(description));
        depexp.setPeriod(A_Period);
        depexp.setIsDepreciated(true);
        depexp.setDateAcct(DateAcct);
        //
        depexp.updateFrom(assetwk);
        //
        if (s_log.isLoggable(Level.FINE)) s_log.fine("depexp=" + depexp);
        return depexp;
    }

    /**
     * Create Depreciation Entries Produce record:
     *
     * <pre>
     * 	68.. = 28..   depreciation value
     * </pre>
     */
    public static Collection<MDepreciationExp> createDepreciation(
            MDepreciationWorkfile assetwk,
            int PeriodNo,
            Timestamp dateAcct,
            BigDecimal amt,
            BigDecimal amt_F,
            BigDecimal accumAmt,
            BigDecimal accumAmt_F,
            String help) {
        ArrayList<MDepreciationExp> list = new ArrayList<>();
        AssetAccounting assetAcct = assetwk.getAssetAccounting(dateAcct);
        MDepreciationExp depexp;

        depexp =
                createEntry(

                        X_A_Depreciation_Exp.A_ENTRY_TYPE_Depreciation,
                        assetwk.getAssetId(),
                        PeriodNo,
                        dateAcct,
                        assetwk.getPostingType(),
                        assetAcct.getDepreciationAcct(),
                        assetAcct.getAccumdepreciationAcct(),
                        amt,
                        "@AssetDepreciationAmt@",
                        assetwk);
        if (depexp != null) {
            depexp.setOrgId(assetwk.getAAsset().getOrgId()); // added by zuhri
            if (accumAmt != null) depexp.setAccumulatedDepreciation(accumAmt);
            if (accumAmt_F != null) depexp.setAccumulatedDepreciationFiscal(accumAmt_F);
            if (help != null && help.length() > 0) depexp.setHelp(help);
            depexp.setFiscalExpense(amt_F);
            depexp.setAccumulatedDepreciationDelta(amt);
            depexp.setAccumulatedDepreciationFiscalDelta(amt_F);
            depexp.saveEx();
            list.add(depexp);
        }
        return list;
    }

    public static void checkExistsNotProcessedEntries(
            int A_Asset_ID, Timestamp dateAcct, String postingType) {
        final String whereClause =
                MDepreciationExp.COLUMNNAME_A_Asset_ID
                        + "=?"
                        + " AND TRUNC("
                        + MDepreciationExp.COLUMNNAME_DateAcct
                        + ",'MONTH')<?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_PostingType
                        + "=?"
                        + " AND "
                        + MDepreciationExp.COLUMNNAME_Processed
                        + "=?";
        boolean match =
                new Query<DepreciationExpense>(MDepreciationExp.Table_Name, whereClause)
                        .setParameters(
                                A_Asset_ID, TimeUtil.getMonthFirstDay(dateAcct), postingType, false)
                        .match();
        if (match) {
            throw new AssetException("There are unprocessed records to date");
        }
    }

    /**
     * Update fields from asset workfile
     *
     * @param wk asset workfile
     */
    public void updateFrom(DepreciationWorkfile wk) {
        setAssetCost(wk.getAssetCost());
        setAccumulatedDepreciation(wk.getAccumulatedDepreciation());
        setAccumulatedDepreciationFiscal(wk.getAccumulatedDepreciationFiscal());
        setUseLifeMonths(wk.getUseLifeMonths());
        setUseLifeMonthsFiscal(wk.getUseLifeMonthsFiscal());
        setAssetRemaining(wk.getAssetRemaining());
        setAssetRemainingFiscal(wk.getAssetRemainingFiscal());
    }

    private DepreciationWorkfile getA_Depreciation_Workfile() {
        return MDepreciationWorkfile.get(getAssetId(), getPostingType());
    }

    /**
     * Process this entry and save the modified workfile.
     */
    public void process() {
        if (isProcessed()) {
            log.fine("@AlreadyProcessed@");
            return;
        }

        //
        DepreciationWorkfile assetwk = getA_Depreciation_Workfile();
        if (assetwk == null) {
            throw new AssetException("@NotFound@ @A_Depreciation_Workfile_ID@");
        }
        //
        String entryType = getEntryType();
        if (MDepreciationExp.A_ENTRY_TYPE_Depreciation.equals(entryType)) {
            checkExistsNotProcessedEntries(
                    getAssetId(), getDateAcct(), getPostingType());
            //
            // Check if the asset is Active:
            if (!assetwk.getAsset().getAssetStatus().equals(MAsset.A_ASSET_STATUS_Activated)) {
                throw new AssetNotActiveException(assetwk.getAsset().getId());
            }
            //
            setDateAcct(assetwk.getDateAcct());
            assetwk.adjustAccumulatedDepreciation(getExpense(), getFiscalExpense(), false);
        }  // nothing to do for other entry types

        //
        setProcessed(true);
        updateFrom(assetwk);
        saveEx();

        //
        // Update workfile
        assetwk.setCurrentPeriod();
        assetwk.saveEx();
    }

    protected boolean beforeDelete() {
        if (isProcessed()) {
            // TODO : check if we can reverse it (check period, check dateacct etc)
            DepreciationWorkfile assetwk = getA_Depreciation_Workfile();
            assetwk.adjustAccumulatedDepreciation(
                    getAccumulatedDepreciation().negate(), getAccumulatedDepreciationFiscal().negate(), false);
            assetwk.saveEx();
        }
        // Try to delete postings
        if (isPosted()) {
            MPeriod.testPeriodOpen(
                    getDateAcct(), MDocType.DOCBASETYPE_GLDocument, getOrgId());
            MDepreciationEntry.deleteFacts(this);
        }
        return true;
    }

    protected boolean afterDelete(boolean success) {
        if (!success) {
            return false;
        }
        //
        // If it was processed, we need to update workfile's current period
        if (isProcessed()) {
            DepreciationWorkfile wk = getA_Depreciation_Workfile();
            wk.setCurrentPeriod();
            wk.saveEx();
        }
        //
        return true;
    }

    protected boolean isPosted() {
        return isProcessed() && getDepreciationEntryId() > 0;
    }

    public void setProcessed(boolean Processed) {
        super.setProcessed(Processed);
        //
        if (getId() > 0) {
            final String sql =
                    "UPDATE "
                            + DepreciationExpense.Table_Name
                            + " SET Processed=? WHERE "
                            + DepreciationExpense.COLUMNNAME_A_Depreciation_Exp_ID
                            + "=?";
            executeUpdateEx(sql, new Object[]{Processed, getId()});
        }
    }

    public String toString() {
        return getClass().getSimpleName()
                + "["
                + getId()
                + ",A_Asset_ID="
                + getAssetId()
                + ",A_Period="
                + getPeriod()
                + ",DateAcct="
                + getDateAcct()
                + ",Expense="
                + getExpense()
                + ",Entry_ID="
                + getDepreciationEntryId()
                + "]";
    }
}
