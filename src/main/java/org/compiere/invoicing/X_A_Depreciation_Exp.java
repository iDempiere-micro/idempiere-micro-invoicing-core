package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.DepreciationExpense;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for A_Depreciation_Exp
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_A_Depreciation_Exp extends PO implements DepreciationExpense {

    /**
     * Depreciation = DEP
     */
    public static final String A_ENTRY_TYPE_Depreciation = "DEP";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_A_Depreciation_Exp(int A_Depreciation_Exp_ID) {
        super(A_Depreciation_Exp_ID);
    }

    /**
     * Load Constructor
     */
    public X_A_Depreciation_Exp(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 3 - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_A_Depreciation_Exp[" + getId() + "]";
    }

    /**
     * Set A_Account_Number_Acct.
     *
     * @param A_Account_Number_Acct A_Account_Number_Acct
     */
    public void setAccountNumberAcct(int A_Account_Number_Acct) {
        setValue(COLUMNNAME_A_Account_Number_Acct, A_Account_Number_Acct);
    }

    /**
     * Get Accumulated Depreciation.
     *
     * @return Accumulated Depreciation
     */
    public BigDecimal getAccumulatedDepreciation() {
        BigDecimal bd = getValue(COLUMNNAME_A_Accumulated_Depr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Accumulated Depreciation.
     *
     * @param A_Accumulated_Depr Accumulated Depreciation
     */
    public void setAccumulatedDepreciation(BigDecimal A_Accumulated_Depr) {
        setValue(COLUMNNAME_A_Accumulated_Depr, A_Accumulated_Depr);
    }

    /**
     * Set Accumulated Depreciation (delta).
     *
     * @param A_Accumulated_Depr_Delta Accumulated Depreciation (delta)
     */
    public void setAccumulatedDepreciationDelta(BigDecimal A_Accumulated_Depr_Delta) {
        setValue(COLUMNNAME_A_Accumulated_Depr_Delta, A_Accumulated_Depr_Delta);
    }

    /**
     * Get Accumulated Depreciation (fiscal).
     *
     * @return Accumulated Depreciation (fiscal)
     */
    public BigDecimal getAccumulatedDepreciationFiscal() {
        BigDecimal bd = getValue(COLUMNNAME_A_Accumulated_Depr_F);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Accumulated Depreciation (fiscal).
     *
     * @param A_Accumulated_Depr_F Accumulated Depreciation (fiscal)
     */
    public void setAccumulatedDepreciationFiscal(BigDecimal A_Accumulated_Depr_F) {
        setValue(COLUMNNAME_A_Accumulated_Depr_F, A_Accumulated_Depr_F);
    }

    /**
     * Set Accumulated Depreciation - fiscal (delta).
     *
     * @param A_Accumulated_Depr_F_Delta Accumulated Depreciation - fiscal (delta)
     */
    public void setAccumulatedDepreciationFiscalDelta(BigDecimal A_Accumulated_Depr_F_Delta) {
        setValue(COLUMNNAME_A_Accumulated_Depr_F_Delta, A_Accumulated_Depr_F_Delta);
    }

    /**
     * Set Asset Cost.
     *
     * @param A_Asset_Cost Asset Cost
     */
    public void setAssetCost(BigDecimal A_Asset_Cost) {
        setValue(COLUMNNAME_A_Asset_Cost, A_Asset_Cost);
    }

    /**
     * Get Asset.
     *
     * @return Asset used internally or by customers
     */
    public int getAssetId() {
        Integer ii = getValue(COLUMNNAME_A_Asset_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Asset.
     *
     * @param A_Asset_ID Asset used internally or by customers
     */
    public void setAssetId(int A_Asset_ID) {
        if (A_Asset_ID < 1) setValue(COLUMNNAME_A_Asset_ID, null);
        else setValue(COLUMNNAME_A_Asset_ID, A_Asset_ID);
    }

    /**
     * Set Remaining Amt.
     *
     * @param A_Asset_Remaining Remaining Amt
     */
    public void setAssetRemaining(BigDecimal A_Asset_Remaining) {
        setValue(COLUMNNAME_A_Asset_Remaining, A_Asset_Remaining);
    }

    /**
     * Set Remaining Amt (fiscal).
     *
     * @param A_Asset_Remaining_F Remaining Amt (fiscal)
     */
    public void setAssetRemainingFiscal(BigDecimal A_Asset_Remaining_F) {
        setValue(COLUMNNAME_A_Asset_Remaining_F, A_Asset_Remaining_F);
    }

    /**
     * Get Depreciation Entry.
     *
     * @return Depreciation Entry
     */
    public int getDepreciationEntryId() {
        Integer ii = getValue(COLUMNNAME_A_Depreciation_Entry_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Entry Type.
     *
     * @return Entry Type
     */
    public String getEntryType() {
        return getValue(COLUMNNAME_A_Entry_Type);
    }

    /**
     * Set Entry Type.
     *
     * @param A_Entry_Type Entry Type
     */
    public void setEntryType(String A_Entry_Type) {

        setValue(COLUMNNAME_A_Entry_Type, A_Entry_Type);
    }

    /**
     * Get Asset Period.
     *
     * @return Asset Period
     */
    public int getPeriod() {
        Integer ii = getValue(COLUMNNAME_A_Period);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Asset Period.
     *
     * @param A_Period Asset Period
     */
    public void setPeriod(int A_Period) {
        setValue(COLUMNNAME_A_Period, A_Period);
    }

    /**
     * Set Account (credit).
     *
     * @param CR_Account_ID Account used
     */
    public void setCRAccountId(int CR_Account_ID) {
        if (CR_Account_ID < 1) setValue(COLUMNNAME_CR_Account_ID, null);
        else setValue(COLUMNNAME_CR_Account_ID, CR_Account_ID);
    }

    /**
     * Get Account Date.
     *
     * @return Accounting Date
     */
    public Timestamp getDateAcct() {
        return (Timestamp) getValue(COLUMNNAME_DateAcct);
    }

    /**
     * Set Account Date.
     *
     * @param DateAcct Accounting Date
     */
    public void setDateAcct(Timestamp DateAcct) {
        setValue(COLUMNNAME_DateAcct, DateAcct);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
    }

    /**
     * Set Account (debit).
     *
     * @param DR_Account_ID Account used
     */
    public void setDRAccountId(int DR_Account_ID) {
        if (DR_Account_ID < 1) setValue(COLUMNNAME_DR_Account_ID, null);
        else setValue(COLUMNNAME_DR_Account_ID, DR_Account_ID);
    }

    /**
     * Get Expense.
     *
     * @return Expense
     */
    public BigDecimal getExpense() {
        BigDecimal bd = getValue(COLUMNNAME_Expense);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Expense.
     *
     * @param Expense Expense
     */
    public void setExpense(BigDecimal Expense) {
        setValue(COLUMNNAME_Expense, Expense);
    }

    /**
     * Get Expense (fiscal).
     *
     * @return Expense (fiscal)
     */
    public BigDecimal getFiscalExpense() {
        BigDecimal bd = getValue(COLUMNNAME_Expense_F);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Expense (fiscal).
     *
     * @param Expense_F Expense (fiscal)
     */
    public void setFiscalExpense(BigDecimal Expense_F) {
        setValue(COLUMNNAME_Expense_F, Expense_F);
    }

    /**
     * Set Comment/Help.
     *
     * @param Help Comment or Hint
     */
    public void setHelp(String Help) {
        setValue(COLUMNNAME_Help, Help);
    }

    /**
     * Set Depreciate.
     *
     * @param IsDepreciated The asset will be depreciated
     */
    public void setIsDepreciated(boolean IsDepreciated) {
        setValue(COLUMNNAME_IsDepreciated, IsDepreciated);
    }

    /**
     * Get PostingType.
     *
     * @return The type of posted amount for the transaction
     */
    public String getPostingType() {
        return getValue(COLUMNNAME_PostingType);
    }

    /**
     * Set PostingType.
     *
     * @param PostingType The type of posted amount for the transaction
     */
    public void setPostingType(String PostingType) {

        setValue(COLUMNNAME_PostingType, PostingType);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Processed);
    }

    /**
     * Set Usable Life - Months.
     *
     * @param UseLifeMonths Months of the usable life of the asset
     */
    public void setUseLifeMonths(int UseLifeMonths) {
        setValue(COLUMNNAME_UseLifeMonths, UseLifeMonths);
    }

    /**
     * Set Use Life - Months (fiscal).
     *
     * @param UseLifeMonths_F Use Life - Months (fiscal)
     */
    public void setUseLifeMonthsFiscal(int UseLifeMonths_F) {
        setValue(COLUMNNAME_UseLifeMonths_F, UseLifeMonths_F);
    }

}
