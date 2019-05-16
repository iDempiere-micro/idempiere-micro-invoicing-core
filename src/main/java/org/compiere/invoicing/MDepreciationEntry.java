package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MPeriod;
import org.compiere.docengine.DocumentEngine;
import org.compiere.model.AccountingSchema;
import org.compiere.model.DepreciationEntry;
import org.compiere.model.DepreciationExpense;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.orm.Query;
import org.compiere.orm.TimeUtil;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdateEx;

/**
 * Depreciation Entry
 *
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 */
public class MDepreciationEntry extends X_A_Depreciation_Entry implements DocAction, IPODoc {

    /**
     *
     */
    private static final long serialVersionUID = 6631244784741228058L;
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;

    /**
     * Standard Constructor
     */
    public MDepreciationEntry(int A_Depreciation_Entry_ID) {
        super(A_Depreciation_Entry_ID);
        if (A_Depreciation_Entry_ID == 0) {
            AccountingSchema acctSchema = MClientKt.getClientWithAccounting().getAcctSchema();
            setAcctSchemaId(acctSchema.getId());
            setCurrencyId(acctSchema.getCurrencyId());
            setEntryType(X_A_Depreciation_Entry.A_ENTRY_TYPE_Depreciation); // TODO: workaround
            setPostingType(X_A_Depreciation_Entry.POSTINGTYPE_Actual); // A
            setProcessed(false);
            setProcessing(false);
            setPosted(false);
        }
    }

    /**
     * Load Constructor
     */
    public MDepreciationEntry(Row row) {
        super(row);
    }

    public static void deleteFacts(MDepreciationExp depexp) {
        final String sql = "DELETE FROM Fact_Acct WHERE AD_Table_ID=? AND Record_ID=? AND Line_ID=?";
        Object[] params =
                new Object[]{
                        DepreciationEntry.Table_ID, depexp.getDepreciationEntryId(), depexp.getId()
                };
        executeUpdateEx(sql, params);
    }

    protected boolean beforeSave(boolean newRecord) {
        setPeriodId();
        return true;
    }

    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) {
            return false;
        }
        if (!isProcessed()
                && (newRecord || isValueChanged(DepreciationEntry.COLUMNNAME_DateAcct))) {
            selectLines();
        }
        return true;
    }

    protected boolean afterDelete(boolean success) {
        if (!success) {
            return false;
        }

        unselectLines();
        return true;
    }

    public void setPeriodId() {
        MPeriod period = MPeriod.get(getDateAcct(), getOrgId());
        if (period == null) {
            throw new AdempiereException("@NotFound@ @C_Period_ID@");
        }
        setPeriodId(period.getId());
    }

    private void unselectLines() {
        String sql =
                "UPDATE "
                        + MDepreciationExp.Table_Name
                        + " SET "
                        + MDepreciationExp.COLUMNNAME_A_Depreciation_Entry_ID
                        + "=NULL "
                        + " WHERE "
                        + MDepreciationExp.COLUMNNAME_A_Depreciation_Entry_ID
                        + "=?";
        int id = getId();
        if (id <= 0) { // Use old ID is current ID is missing (i.e. object was deleted)
            id = getDeletedSingleKeyRecordId();
        }
        int no = executeUpdateEx(sql, new Object[]{id});
        if (log.isLoggable(Level.FINE)) log.fine("Updated #" + no);
    }

    private void selectLines() {
        // Reset selected lines:
        unselectLines();
        // Select lines:
        final String sql =
                "UPDATE "
                        + MDepreciationExp.Table_Name
                        + " SET "
                        + MDepreciationExp.COLUMNNAME_A_Depreciation_Entry_ID
                        + "=?"
                        + " WHERE "
                        + MDepreciationExp.COLUMNNAME_A_Depreciation_Entry_ID
                        + " IS NULL"
                        + " AND TRUNC("
                        + MDepreciationExp.COLUMNNAME_DateAcct
                        + ",'MONTH') = ?"
                        + " AND AD_Client_ID=? AND AD_Org_ID=?";

        Timestamp dateAcct = TimeUtil.trunc(getDateAcct(), TimeUtil.TRUNC_MONTH);
        int no =
                executeUpdateEx(
                        sql, new Object[]{getId(), dateAcct, getClientId(), getOrgId()});
        if (log.isLoggable(Level.FINE)) log.fine("Updated #" + no);
    }

    /**
     * Get Lines
     */
    public List<DepreciationExpense> getLinesIterator(boolean onlyNotProcessed) {
        final List<Object> params = new ArrayList<>();
        String whereClause = MDepreciationExp.COLUMNNAME_A_Depreciation_Entry_ID + "=?";
        params.add(getId());

        if (onlyNotProcessed) {
            whereClause += " AND " + MDepreciationExp.COLUMNNAME_Processed + "=?";
            params.add(false);
        }

        // ORDER BY clause - very important
        String orderBy =
                MDepreciationExp.COLUMNNAME_A_Asset_ID
                        + ","
                        + MDepreciationExp.COLUMNNAME_PostingType
                        + ","
                        + MDepreciationExp.COLUMNNAME_A_Period
                        + ","
                        + MDepreciationExp.COLUMNNAME_A_Entry_Type;

        return new Query<DepreciationExpense>(DepreciationExpense.Table_Name, whereClause)
                .setOrderBy(orderBy)
                .setParameters(params)
                .list();
    }

    public boolean processIt(@NotNull String processAction) {
        m_processMsg = null;
        DocumentEngine engine = new DocumentEngine(this, getDocStatus());
        return engine.processIt(processAction, getDocAction());
    } //	processIt

    public boolean unlockIt() {
        if (log.isLoggable(Level.INFO)) log.info("unlockIt - " + toString());
        //	setProcessing(false);
        return true;
    } //	unlockIt

    public boolean invalidateIt() {
        return false;
    }

    @NotNull
    public String prepareIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
        if (m_processMsg != null) {
            return DocAction.Companion.getSTATUS_Invalid();
        }

        MPeriod.testPeriodOpen(getDateAcct(), getDocTypeId(), getOrgId());

        m_justPrepared = true;

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) {
            return DocAction.Companion.getSTATUS_Invalid();
        }

        setDocAction(X_A_Depreciation_Entry.DOCACTION_Complete);
        return DocAction.Companion.getSTATUS_InProgress();
    }

    public boolean approveIt() {
        if (log.isLoggable(Level.INFO)) log.info("approveIt - " + toString());
        setIsApproved(true);
        return true;
    }

    public boolean rejectIt() {
        if (log.isLoggable(Level.INFO)) log.info("rejectIt - " + toString());
        setIsApproved(false);
        return true;
    } //	rejectIt

    @NotNull
    public CompleteActionResult completeIt() {
        //	Re-Check
        if (!m_justPrepared) {
            String status = prepareIt();
            m_justPrepared = false;
            if (!DocAction.Companion.getSTATUS_InProgress().equals(status))
                return new CompleteActionResult(status);
        }
        //	Implicit Approval
        if (!isApproved()) {
            approveIt();
        }

        final MPeriod period = MPeriod.get(getPeriodId());

        final ArrayList<Exception> errors = new ArrayList<>();
        final List<DepreciationExpense> it = getLinesIterator(true);
        //
        for (DepreciationExpense depexp : it) {
            try {
                // Check if is in Period
                if (!period.isInPeriod(depexp.getDateAcct())) {
                    throw new AssetException(
                            "The date is not within this Period"
                                    + " ("
                                    + depexp
                                    + ", Data="
                                    + depexp.getDateAcct()
                                    + ", Period="
                                    + period.getName()
                                    + ")"); // TODO: translate
                }
                depexp.process();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                errors.add(e);
            }
        }
        //
        if (errors.size() > 0) {
            throw new AssetArrayException(errors);
        }

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        setProcessed(true);
        setDocAction(X_A_Depreciation_Entry.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    public boolean voidIt() {
        return false;
    }

    public boolean closeIt() {
        setDocAction(X_A_Depreciation_Entry.DOCACTION_None);
        return true;
    }

    public boolean reverseCorrectIt() {
        return false;
    }

    public boolean reverseAccrualIt() {
        return false;
    }

    public boolean reActivateIt() {
        return false;
    } //	reActivateIt

    @NotNull
    public String getSummary() {
        return toString();
    }

    @NotNull
    public String getProcessMsg() {
        return m_processMsg;
    }

    public int getDocumentUserId() {
        return getCreatedBy();
    }

    @NotNull
    public BigDecimal getApprovalAmt() {
        return null;
    }

    @NotNull
    public String getDocumentInfo() {
        return getDocumentNo();
    }

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }

    /**
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(@NotNull String DocStatus) {

        setValue(COLUMNNAME_DocStatus, DocStatus);
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = getValue(COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }
}
