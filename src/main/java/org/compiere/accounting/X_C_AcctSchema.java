package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_AcctSchema;
import org.compiere.orm.BasePOName;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for C_AcctSchema
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_AcctSchema extends BasePOName implements I_C_AcctSchema {

    /**
     * PO Commitment only = C
     */
    public static final String COMMITMENTTYPE_POCommitmentOnly = "C";
    /**
     * PO Commitment & Reservation = B
     */
    public static final String COMMITMENTTYPE_POCommitmentReservation = "B";
    /**
     * None = N
     */
    public static final String COMMITMENTTYPE_None = "N";
    /**
     * PO/SO Commitment & Reservation = A
     */
    public static final String COMMITMENTTYPE_POSOCommitmentReservation = "A";
    /**
     * SO Commitment only = S
     */
    public static final String COMMITMENTTYPE_SOCommitmentOnly = "S";
    /**
     * PO/SO Commitment = O
     */
    public static final String COMMITMENTTYPE_POSOCommitment = "O";
    /**
     * Client = C
     */
    public static final String COSTINGLEVEL_Client = "C";
    /**
     * Organization = O
     */
    public static final String COSTINGLEVEL_Organization = "O";
    /**
     * Batch/Lot = B
     */
    public static final String COSTINGLEVEL_BatchLot = "B";
    /**
     * Standard Costing = S
     */
    public static final String COSTINGMETHOD_StandardCosting = "S";
    /**
     * Average PO = A
     */
    public static final String COSTINGMETHOD_AveragePO = "A";
    /**
     * Last PO Price = p
     */
    public static final String COSTINGMETHOD_LastPOPrice = "p";
    /**
     * Average Invoice = I
     */
    public static final String COSTINGMETHOD_AverageInvoice = "I";
    /**
     * International GAAP = UN
     */
    public static final String GAAP_InternationalGAAP = "UN";
    /**
     * None = N
     */
    public static final String TAXCORRECTIONTYPE_None = "N";
    /**
     * Write-off only = W
     */
    public static final String TAXCORRECTIONTYPE_Write_OffOnly = "W";
    /**
     * Discount only = D
     */
    public static final String TAXCORRECTIONTYPE_DiscountOnly = "D";
    /**
     * Write-off and Discount = B
     */
    public static final String TAXCORRECTIONTYPE_Write_OffAndDiscount = "B";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_AcctSchema(Properties ctx, int C_AcctSchema_ID) {
        super(ctx, C_AcctSchema_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_AcctSchema(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    }

    public X_C_AcctSchema(Properties ctx, Row row) {
        super(ctx, row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return I_C_AcctSchema.accessLevel.intValue();
    }

    public String toString() {
        return "X_C_AcctSchema[" + getId() + "]";
    }

    /**
     * Get Only Organization.
     *
     * @return Create posting entries only for this organization
     */
    public int getOrganizationOnlyId() {
        Integer ii = (Integer) getValue(I_C_AcctSchema.COLUMNNAME_AD_OrgOnly_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Only Organization.
     *
     * @param AD_OrgOnly_ID Create posting entries only for this organization
     */
    public void setOrganizationOnlyId(int AD_OrgOnly_ID) {
        if (AD_OrgOnly_ID < 1) setValue(I_C_AcctSchema.COLUMNNAME_AD_OrgOnly_ID, null);
        else setValue(I_C_AcctSchema.COLUMNNAME_AD_OrgOnly_ID, AD_OrgOnly_ID);
    }

    /**
     * Get Automatic Period Control.
     *
     * @return If selected, the periods are automatically opened and closed
     */
    public boolean isAutoPeriodControl() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_AutoPeriodControl);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Automatic Period Control.
     *
     * @param AutoPeriodControl If selected, the periods are automatically opened and closed
     */
    public void setAutoPeriodControl(boolean AutoPeriodControl) {
        setValue(I_C_AcctSchema.COLUMNNAME_AutoPeriodControl, AutoPeriodControl);
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = (Integer) getValue(I_C_AcctSchema.COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency.
     *
     * @return The Currency for this record
     */
    public int getCurrencyId() {
        Integer ii = (Integer) getValue(I_C_AcctSchema.COLUMNNAME_C_Currency_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValue(I_C_AcctSchema.COLUMNNAME_C_Currency_ID, null);
        else setValue(I_C_AcctSchema.COLUMNNAME_C_Currency_ID, C_Currency_ID);
    }

    /**
     * Get Commitment Type.
     *
     * @return Create Commitment and/or Reservations for Budget Control
     */
    public String getCommitmentType() {
        return (String) getValue(I_C_AcctSchema.COLUMNNAME_CommitmentType);
    }

    /**
     * Set Commitment Type.
     *
     * @param CommitmentType Create Commitment and/or Reservations for Budget Control
     */
    public void setCommitmentType(String CommitmentType) {

        setValue(I_C_AcctSchema.COLUMNNAME_CommitmentType, CommitmentType);
    }

    /**
     * Get Costing Level.
     *
     * @return The lowest level to accumulate Costing Information
     */
    public String getCostingLevel() {
        return (String) getValue(I_C_AcctSchema.COLUMNNAME_CostingLevel);
    }

    /**
     * Set Costing Level.
     *
     * @param CostingLevel The lowest level to accumulate Costing Information
     */
    public void setCostingLevel(String CostingLevel) {

        setValue(I_C_AcctSchema.COLUMNNAME_CostingLevel, CostingLevel);
    }

    /**
     * Get Costing Method.
     *
     * @return Indicates how Costs will be calculated
     */
    public String getCostingMethod() {
        return (String) getValue(I_C_AcctSchema.COLUMNNAME_CostingMethod);
    }

    /**
     * Set Costing Method.
     *
     * @param CostingMethod Indicates how Costs will be calculated
     */
    public void setCostingMethod(String CostingMethod) {

        setValue(I_C_AcctSchema.COLUMNNAME_CostingMethod, CostingMethod);
    }

    /**
     * Set Period.
     *
     * @param C_Period_ID Period of the Calendar
     */
    public void setPeriodId(int C_Period_ID) {
        if (C_Period_ID < 1) setValueNoCheck(I_C_AcctSchema.COLUMNNAME_C_Period_ID, null);
        else setValueNoCheck(I_C_AcctSchema.COLUMNNAME_C_Period_ID, C_Period_ID);
    }

    /**
     * Get GAAP.
     *
     * @return Generally Accepted Accounting Principles
     */
    public String getGAAP() {
        return (String) getValue(I_C_AcctSchema.COLUMNNAME_GAAP);
    }

    /**
     * Set GAAP.
     *
     * @param GAAP Generally Accepted Accounting Principles
     */
    public void setGAAP(String GAAP) {

        setValue(I_C_AcctSchema.COLUMNNAME_GAAP, GAAP);
    }

    /**
     * Set Use Account Alias.
     *
     * @param HasAlias Ability to select (partial) account combinations by an Alias
     */
    public void setHasAlias(boolean HasAlias) {
        setValue(I_C_AcctSchema.COLUMNNAME_HasAlias, HasAlias);
    }

    /**
     * Set Use Account Combination Control.
     *
     * @param HasCombination Combination of account elements are checked
     */
    public void setHasCombination(boolean HasCombination) {
        setValue(I_C_AcctSchema.COLUMNNAME_HasCombination, HasCombination);
    }

    /**
     * Set Accrual.
     *
     * @param IsAccrual Indicates if Accrual or Cash Based accounting will be used
     */
    public void setIsAccrual(boolean IsAccrual) {
        setValue(I_C_AcctSchema.COLUMNNAME_IsAccrual, IsAccrual);
    }

    /**
     * Get Accrual.
     *
     * @return Indicates if Accrual or Cash Based accounting will be used
     */
    public boolean isAccrual() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsAccrual);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Adjust COGS.
     *
     * @param IsAdjustCOGS Adjust Cost of Good Sold
     */
    public void setIsAdjustCOGS(boolean IsAdjustCOGS) {
        setValue(I_C_AcctSchema.COLUMNNAME_IsAdjustCOGS, IsAdjustCOGS);
    }

    /**
     * Get Allow Negative Posting.
     *
     * @return Allow to post negative accounting values
     */
    public boolean isAllowNegativePosting() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsAllowNegativePosting);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Correct tax for Discounts/Charges.
     *
     * @param IsDiscountCorrectsTax Correct the tax for payment discount and charges
     */
    public void setIsDiscountCorrectsTax(boolean IsDiscountCorrectsTax) {
        setValue(I_C_AcctSchema.COLUMNNAME_IsDiscountCorrectsTax, IsDiscountCorrectsTax);
    }

    /**
     * Get Correct tax for Discounts/Charges.
     *
     * @return Correct the tax for payment discount and charges
     */
    public boolean isDiscountCorrectsTax() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsDiscountCorrectsTax);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Explicit Cost Adjustment.
     *
     * @param IsExplicitCostAdjustment Post the cost adjustment explicitly
     */
    public void setIsExplicitCostAdjustment(boolean IsExplicitCostAdjustment) {
        setValue(I_C_AcctSchema.COLUMNNAME_IsExplicitCostAdjustment, IsExplicitCostAdjustment);
    }

    /**
     * Get Explicit Cost Adjustment.
     *
     * @return Post the cost adjustment explicitly
     */
    public boolean isExplicitCostAdjustment() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsExplicitCostAdjustment);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Post if Clearing Equal.
     *
     * @return This flag controls if Adempiere must post when clearing (transit) and final accounts
     * are the same
     */
    public boolean isPostIfClearingEqual() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsPostIfClearingEqual);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Post Services Separately.
     *
     * @param IsPostServices Differentiate between Services and Product Receivable/Payables
     */
    public void setIsPostServices(boolean IsPostServices) {
        setValue(I_C_AcctSchema.COLUMNNAME_IsPostServices, IsPostServices);
    }

    /**
     * Get Post Services Separately.
     *
     * @return Differentiate between Services and Product Receivable/Payables
     */
    public boolean isPostServices() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsPostServices);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Post Trade Discount.
     *
     * @param IsTradeDiscountPosted Generate postings for trade discounts
     */
    public void setIsTradeDiscountPosted(boolean IsTradeDiscountPosted) {
        setValue(I_C_AcctSchema.COLUMNNAME_IsTradeDiscountPosted, IsTradeDiscountPosted);
    }

    /**
     * Get Post Trade Discount.
     *
     * @return Generate postings for trade discounts
     */
    public boolean isTradeDiscountPosted() {
        Object oo = getValue(I_C_AcctSchema.COLUMNNAME_IsTradeDiscountPosted);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Cost Type.
     *
     * @return Type of Cost (e.g. Current, Plan, Future)
     */
    public int getCostTypeId() {
        Integer ii = (Integer) getValue(I_C_AcctSchema.COLUMNNAME_M_CostType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Cost Type.
     *
     * @param M_CostType_ID Type of Cost (e.g. Current, Plan, Future)
     */
    public void setCostTypeId(int M_CostType_ID) {
        if (M_CostType_ID < 1) setValue(I_C_AcctSchema.COLUMNNAME_M_CostType_ID, null);
        else setValue(I_C_AcctSchema.COLUMNNAME_M_CostType_ID, M_CostType_ID);
    }

    /**
     * Get Future Days.
     *
     * @return Number of days to be able to post to a future date (based on system date)
     */
    public int getPeriodOpenFuture() {
        Integer ii = (Integer) getValue(I_C_AcctSchema.COLUMNNAME_Period_OpenFuture);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Future Days.
     *
     * @param Period_OpenFuture Number of days to be able to post to a future date (based on system
     *                          date)
     */
    public void setPeriodOpenFuture(int Period_OpenFuture) {
        setValue(I_C_AcctSchema.COLUMNNAME_Period_OpenFuture, Period_OpenFuture);
    }

    /**
     * Get History Days.
     *
     * @return Number of days to be able to post in the past (based on system date)
     */
    public int getPeriodOpenHistory() {
        Integer ii = (Integer) getValue(I_C_AcctSchema.COLUMNNAME_Period_OpenHistory);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set History Days.
     *
     * @param Period_OpenHistory Number of days to be able to post in the past (based on system date)
     */
    public void setPeriodOpenHistory(int Period_OpenHistory) {
        setValue(I_C_AcctSchema.COLUMNNAME_Period_OpenHistory, Period_OpenHistory);
    }

    /**
     * Get Element Separator.
     *
     * @return Element Separator
     */
    public String getSeparator() {
        return (String) getValue(I_C_AcctSchema.COLUMNNAME_Separator);
    }

    /**
     * Set Element Separator.
     *
     * @param Separator Element Separator
     */
    public void setSeparator(String Separator) {
        setValue(I_C_AcctSchema.COLUMNNAME_Separator, Separator);
    }

    /**
     * Get Tax Correction.
     *
     * @return Type of Tax Correction
     */
    public String getTaxCorrectionType() {
        return (String) getValue(I_C_AcctSchema.COLUMNNAME_TaxCorrectionType);
    }

    /**
     * Set Tax Correction.
     *
     * @param TaxCorrectionType Type of Tax Correction
     */
    public void setTaxCorrectionType(String TaxCorrectionType) {

        setValue(I_C_AcctSchema.COLUMNNAME_TaxCorrectionType, TaxCorrectionType);
    }

    @Override
    public int getTableId() {
        return I_C_AcctSchema.Table_ID;
    }
}
