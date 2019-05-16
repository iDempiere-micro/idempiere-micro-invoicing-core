package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.DefaultAccountsForSchema;
import org.compiere.orm.PO;

/**
 * Generated Model for C_AcctSchema_Default
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_C_AcctSchema_Default extends PO implements DefaultAccountsForSchema {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_AcctSchema_Default(int C_AcctSchema_Default_ID) {
        super(C_AcctSchema_Default_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_AcctSchema_Default(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_C_AcctSchema_Default[" + getId() + "]";
    }

    /**
     * Get Bank Asset.
     *
     * @return Bank Asset Account
     */
    public int getBankAssetAccount() {
        Integer ii = getValue(COLUMNNAME_B_Asset_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Bank Interest Expense.
     *
     * @return Bank Interest Expense Account
     */
    public int getBankInterestExpenseAccount() {
        Integer ii = getValue(COLUMNNAME_B_InterestExp_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Bank Interest Revenue.
     *
     * @return Bank Interest Revenue Account
     */
    public int getBankInterestRevenueAccount() {
        Integer ii = getValue(COLUMNNAME_B_InterestRev_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Bank In Transit.
     *
     * @return Bank In Transit Account
     */
    public int getBankInTransitAccount() {
        Integer ii = getValue(COLUMNNAME_B_InTransit_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Payment Selection.
     *
     * @return AP Payment Selection Clearing Account
     */
    public int getAPPaymentSelectionClearingAccount() {
        Integer ii = getValue(COLUMNNAME_B_PaymentSelect_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Unallocated Cash.
     *
     * @return Unallocated Cash Clearing Account
     */
    public int getUnallocatedCashClearingAccount() {
        Integer ii = getValue(COLUMNNAME_B_UnallocatedCash_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAccountingSchemaId() {
        Integer ii = getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Accounting Schema.
     *
     * @param C_AcctSchema_ID Rules for accounting
     */
    public void setAccountingSchemaId(int C_AcctSchema_ID) {
        if (C_AcctSchema_ID < 1) setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
        else setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, C_AcctSchema_ID);
    }

    /**
     * Get Cash Book Asset.
     *
     * @return Cash Book Asset Account
     */
    public int getCashBookAssetAccount() {
        Integer ii = getValue(COLUMNNAME_CB_Asset_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cash Transfer.
     *
     * @return Cash Transfer Clearing Account
     */
    public int getCashTransferClearingAccount() {
        Integer ii = getValue(COLUMNNAME_CB_CashTransfer_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cash Book Differences.
     *
     * @return Cash Book Differences Account
     */
    public int getCashBookDifferencesAccount() {
        Integer ii = getValue(COLUMNNAME_CB_Differences_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cash Book Expense.
     *
     * @return Cash Book Expense Account
     */
    public int getCashBookExpenseAccount() {
        Integer ii = getValue(COLUMNNAME_CB_Expense_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cash Book Receipt.
     *
     * @return Cash Book Receipts Account
     */
    public int getCashBookReceiptsAccount() {
        Integer ii = getValue(COLUMNNAME_CB_Receipt_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Charge Account.
     *
     * @return Charge Account
     */
    public int getChargeAccount() {
        Integer ii = getValue(COLUMNNAME_Ch_Expense_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Customer Prepayment.
     *
     * @return Account for customer prepayments
     */
    public int getCustomerPrepaymentAccount() {
        Integer ii = getValue(COLUMNNAME_C_Prepayment_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Customer Receivables.
     *
     * @return Account for Customer Receivables
     */
    public int getCustomerReceivableAccount() {
        Integer ii = getValue(COLUMNNAME_C_Receivable_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Receivable Services.
     *
     * @return Customer Accounts Receivables Services Account
     */
    public int getCustomerReceivableServicesAccount() {
        Integer ii = getValue(COLUMNNAME_C_Receivable_Services_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Not-invoiced Receipts.
     *
     * @return Account for not-invoiced Material Receipts
     */
    public int getNotInvoicedReceiptsAccount() {
        Integer ii = getValue(COLUMNNAME_NotInvoicedReceipts_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Product Asset.
     *
     * @return Account for Product Asset (Inventory)
     */
    public int getProductAssetAccount() {
        Integer ii = getValue(COLUMNNAME_P_Asset_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Average Cost Variance.
     *
     * @return Average Cost Variance
     */
    public int getAverageCostVarianceAccount() {
        Integer ii = getValue(COLUMNNAME_P_AverageCostVariance_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Payment Discount Expense.
     *
     * @return Payment Discount Expense Account
     */
    public int getPaymentDiscountExpenseAccount() {
        Integer ii = getValue(COLUMNNAME_PayDiscount_Exp_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Payment Discount Revenue.
     *
     * @return Payment Discount Revenue Account
     */
    public int getPaymentDiscountRevenueAccount() {
        Integer ii = getValue(COLUMNNAME_PayDiscount_Rev_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Product COGS.
     *
     * @return Account for Cost of Goods Sold
     */
    public int getProductCOGSAccount() {
        Integer ii = getValue(COLUMNNAME_P_COGS_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Cost Adjustment.
     *
     * @return Product Cost Adjustment Account
     */
    public int getProductCostAdjustmentAccount() {
        Integer ii = getValue(COLUMNNAME_P_CostAdjustment_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Product Expense.
     *
     * @return Account for Product Expense
     */
    public int getProductExpenseAccount() {
        Integer ii = getValue(COLUMNNAME_P_Expense_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Inventory Clearing.
     *
     * @return Product Inventory Clearing Account
     */
    public int getInventoryClearingAccount() {
        Integer ii = getValue(COLUMNNAME_P_InventoryClearing_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Invoice Price Variance.
     *
     * @return Difference between Costs and Invoice Price (IPV)
     */
    public int getInvoicePriceVarianceAccount() {
        Integer ii = getValue(COLUMNNAME_P_InvoicePriceVariance_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project Asset.
     *
     * @return Project Asset Account
     */
    public int getProjectAssetAccount() {
        Integer ii = getValue(COLUMNNAME_PJ_Asset_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Landed Cost Clearing.
     *
     * @return Product Landed Cost Clearing Account
     */
    public int getLandedCostClearingAccount() {
        Integer ii = getValue(COLUMNNAME_P_LandedCostClearing_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Purchase Price Variance.
     *
     * @return Difference between Standard Cost and Purchase Price (PPV)
     */
    public int getPurchasePriceVarianceAccount() {
        Integer ii = getValue(COLUMNNAME_P_PurchasePriceVariance_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Rate Variance.
     *
     * @return The Rate Variance account is the account used Manufacturing Order
     */
    public int getRateVarianceAccount() {
        Integer ii = getValue(COLUMNNAME_P_RateVariance_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Product Revenue.
     *
     * @return Account for Product Revenue (Sales Account)
     */
    public int getRevenueAccount() {
        Integer ii = getValue(COLUMNNAME_P_Revenue_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Trade Discount Granted.
     *
     * @return Trade Discount Granted Account
     */
    public int getTradeDiscountGrantedAccount() {
        Integer ii = getValue(COLUMNNAME_P_TradeDiscountGrant_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Trade Discount Received.
     *
     * @return Trade Discount Receivable Account
     */
    public int getTradeDiscountReceivableAccount() {
        Integer ii = getValue(COLUMNNAME_P_TradeDiscountRec_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Realized Gain Acct.
     *
     * @return Realized Gain Account
     */
    public int getRealizedGainAccount() {
        Integer ii = getValue(COLUMNNAME_RealizedGain_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Realized Loss Acct.
     *
     * @return Realized Loss Account
     */
    public int getRealizedLossAccount() {
        Integer ii = getValue(COLUMNNAME_RealizedLoss_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Tax Credit.
     *
     * @return Account for Tax you can reclaim
     */
    public int getTaxCreditAccount() {
        Integer ii = getValue(COLUMNNAME_T_Credit_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Tax Due.
     *
     * @return Account for Tax you have to pay
     */
    public int getTaxDueAccount() {
        Integer ii = getValue(COLUMNNAME_T_Due_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Tax Expense.
     *
     * @return Account for paid tax you cannot reclaim
     */
    public int getTaxExpenseAccount() {
        Integer ii = getValue(COLUMNNAME_T_Expense_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Unearned Revenue.
     *
     * @return Account for unearned revenue
     */
    public int getUnEarnedRevenueAccount() {
        Integer ii = getValue(COLUMNNAME_UnEarnedRevenue_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Unrealized Gain Acct.
     *
     * @return Unrealized Gain Account for currency revaluation
     */
    public int getUnrealizedGainAccount() {
        Integer ii = getValue(COLUMNNAME_UnrealizedGain_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Unrealized Loss Acct.
     *
     * @return Unrealized Loss Account for currency revaluation
     */
    public int getUnrealizedLossAccount() {
        Integer ii = getValue(COLUMNNAME_UnrealizedLoss_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Vendor Liability.
     *
     * @return Account for Vendor Liability
     */
    public int getVendorLiabilityAccount() {
        Integer ii = getValue(COLUMNNAME_V_Liability_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Vendor Service Liability.
     *
     * @return Account for Vendor Service Liability
     */
    public int getVendorServiceLiabilityAccount() {
        Integer ii = getValue(COLUMNNAME_V_Liability_Services_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Vendor Prepayment.
     *
     * @return Account for Vendor Prepayments
     */
    public int getVendorPrepaymentAccount() {
        Integer ii = getValue(COLUMNNAME_V_Prepayment_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Warehouse Differences.
     *
     * @return Warehouse Differences Account
     */
    public int getWarehouseDifferencesAccount() {
        Integer ii = getValue(COLUMNNAME_W_Differences_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Write-off.
     *
     * @return Account for Receivables write-off
     */
    public int getWriteOffAccount() {
        Integer ii = getValue(COLUMNNAME_WriteOff_Acct);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return DefaultAccountsForSchema.Table_ID;
    }
}
