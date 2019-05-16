package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_I_GLJournal;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_I_GLJournal extends PO implements I_I_GLJournal {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_GLJournal(int I_GLJournal_ID) {
        super(I_GLJournal_ID);
        /* if (I_GLJournal_ID == 0) { setI_GLJournal_ID (0); setIsImported (false); } */
    }

    /**
     * Load Constructor
     */
    public X_I_GLJournal(Row row) {
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

    @Override
    public int getTableId() {
        return Table_ID;
    }


    public String toString() {
        return "X_I_GLJournal[" + getId() + "]";
    }

    /**
     * Get Account.
     *
     * @return Account used
     */
    public int getAccountId() {
        Integer ii = getValue(COLUMNNAME_Account_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Document Org.
     *
     * @return Document Organization (independent from account organization)
     */
    public int getOrgDocId() {
        Integer ii = getValue(COLUMNNAME_AD_OrgDoc_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getTransactionOrganizationId() {
        Integer ii = getValue(COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Accounted Credit.
     *
     * @return Accounted Credit Amount
     */
    public BigDecimal getAmtAcctCr() {
        BigDecimal bd = getValue(COLUMNNAME_AmtAcctCr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Accounted Debit.
     *
     * @return Accounted Debit Amount
     */
    public BigDecimal getAmtAcctDr() {
        BigDecimal bd = getValue(COLUMNNAME_AmtAcctDr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Source Credit.
     *
     * @return Source Credit Amount
     */
    public BigDecimal getAmtSourceCr() {
        BigDecimal bd = getValue(COLUMNNAME_AmtSourceCr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Source Debit.
     *
     * @return Source Debit Amount
     */
    public BigDecimal getAmtSourceDr() {
        BigDecimal bd = getValue(COLUMNNAME_AmtSourceDr);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Batch Description.
     *
     * @return Description of the Batch
     */
    public String getBatchDescription() {
        return getValue(COLUMNNAME_BatchDescription);
    }

    /**
     * Get Batch Document No.
     *
     * @return Document Number of the Batch
     */
    public String getBatchDocumentNo() {
        return getValue(COLUMNNAME_BatchDocumentNo);
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
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Business Partner .
     *
     * @return Identifies a Business Partner
     */
    public int getBusinessPartnerId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Currency Type.
     *
     * @return Currency Conversion Rate Type
     */
    public int getConversionTypeId() {
        Integer ii = getValue(COLUMNNAME_C_ConversionType_ID);
        if (ii == null) return 0;
        return ii;
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

    /**
     * Get Document Type.
     *
     * @return Document type or rules
     */
    public int getDocumentTypeId() {
        Integer ii = getValue(COLUMNNAME_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Location From.
     *
     * @return Location that inventory was moved from
     */
    public int getLocationFromId() {
        Integer ii = getValue(COLUMNNAME_C_LocFrom_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Location To.
     *
     * @return Location that inventory was moved to
     */
    public int getLocationToId() {
        Integer ii = getValue(COLUMNNAME_C_LocTo_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Period.
     *
     * @return Period of the Calendar
     */
    public int getPeriodId() {
        Integer ii = getValue(COLUMNNAME_C_Period_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Sales Region.
     *
     * @return Sales coverage region
     */
    public int getSalesRegionId() {
        Integer ii = getValue(COLUMNNAME_C_SalesRegion_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get UOM.
     *
     * @return Unit of Measure
     */
    public int getUOMId() {
        Integer ii = getValue(COLUMNNAME_C_UOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Rate.
     *
     * @return Currency Conversion Rate
     */
    public BigDecimal getCurrencyRate() {
        BigDecimal bd = getValue(COLUMNNAME_CurrencyRate);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Combination.
     *
     * @return Valid Account Combination
     */
    public int getValidAccountCombinationId() {
        Integer ii = getValue(COLUMNNAME_C_ValidCombination_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Combination.
     *
     * @param C_ValidCombination_ID Valid Account Combination
     */
    public void setValidAccountCombinationId(int C_ValidCombination_ID) {
        if (C_ValidCombination_ID < 1) setValue(COLUMNNAME_C_ValidCombination_ID, null);
        else setValue(COLUMNNAME_C_ValidCombination_ID, Integer.valueOf(C_ValidCombination_ID));
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
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Get Budget.
     *
     * @return General Ledger Budget
     */
    public int getGLBudgetId() {
        Integer ii = getValue(COLUMNNAME_GL_Budget_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get GL Category.
     *
     * @return General Ledger Category
     */
    public int getGLCategoryId() {
        Integer ii = getValue(COLUMNNAME_GL_Category_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Journal Batch.
     *
     * @param GL_JournalBatch_ID General Ledger Journal Batch
     */
    public void setGLJournalBatchId(int GL_JournalBatch_ID) {
        if (GL_JournalBatch_ID < 1) setValue(COLUMNNAME_GL_JournalBatch_ID, null);
        else setValue(COLUMNNAME_GL_JournalBatch_ID, Integer.valueOf(GL_JournalBatch_ID));
    }

    /**
     * Set Journal.
     *
     * @param GL_Journal_ID General Ledger Journal
     */
    public void setGLJournalId(int GL_Journal_ID) {
        if (GL_Journal_ID < 1) setValue(COLUMNNAME_GL_Journal_ID, null);
        else setValue(COLUMNNAME_GL_Journal_ID, Integer.valueOf(GL_Journal_ID));
    }

    /**
     * Set Journal Line.
     *
     * @param GL_JournalLine_ID General Ledger Journal Line
     */
    public void setGLJournalLineId(int GL_JournalLine_ID) {
        if (GL_JournalLine_ID < 1) setValue(COLUMNNAME_GL_JournalLine_ID, null);
        else setValue(COLUMNNAME_GL_JournalLine_ID, Integer.valueOf(GL_JournalLine_ID));
    }

    /**
     * Set Import Error Message.
     *
     * @param I_ErrorMsg Messages generated from import process
     */
    public void setImportErrorMsg(String I_ErrorMsg) {
        setValue(COLUMNNAME_I_ErrorMsg, I_ErrorMsg);
    }

    /**
     * Set Imported.
     *
     * @param I_IsImported Has this import been processed
     */
    public void setIsImported(boolean I_IsImported) {
        setValue(COLUMNNAME_I_IsImported, Boolean.valueOf(I_IsImported));
    }

    /**
     * Get Create New Batch.
     *
     * @return If selected a new batch is created
     */
    public boolean isCreateNewBatch() {
        Object oo = getValue(COLUMNNAME_IsCreateNewBatch);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Create New Journal.
     *
     * @return If selected a new journal within the batch is created
     */
    public boolean isCreateNewJournal() {
        Object oo = getValue(COLUMNNAME_IsCreateNewJournal);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Journal Document No.
     *
     * @return Document number of the Journal
     */
    public String getJournalDocumentNo() {
        return getValue(COLUMNNAME_JournalDocumentNo);
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = getValue(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Processed.
     *
     * @param Processed The document has been processed
     */
    public void setProcessed(boolean Processed) {
        setValue(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    /**
     * Get Quantity.
     *
     * @return Quantity
     */
    public BigDecimal getQty() {
        BigDecimal bd = getValue(COLUMNNAME_Qty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1Id() {
        Integer ii = getValue(COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }
}
