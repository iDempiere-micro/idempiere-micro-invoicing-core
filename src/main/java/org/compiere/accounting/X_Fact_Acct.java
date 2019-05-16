package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_Fact_Acct;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for Fact_Acct
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_Fact_Acct extends PO implements I_Fact_Acct {

    /**
     * Actual = A
     */
    public static final String POSTINGTYPE_Actual = "A";
    /**
     * Budget = B
     */
    public static final String POSTINGTYPE_Budget = "B";
    /**
     * Commitment = E
     */
    public static final String POSTINGTYPE_Commitment = "E";
    /**
     * Reservation = R
     */
    public static final String POSTINGTYPE_Reservation = "R";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_Fact_Acct(int Fact_Acct_ID) {
        super(Fact_Acct_ID);
    }

    /**
     * Load Constructor
     */
    public X_Fact_Acct(Row row) {
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

    public String toString() {
        StringBuffer sb = new StringBuffer("X_Fact_Acct[").append(getId()).append("]");
        return sb.toString();
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
     * Set Account.
     *
     * @param Account_ID Account used
     */
    public void setAccountId(int Account_ID) {
        if (Account_ID < 1) setValueNoCheck(COLUMNNAME_Account_ID, null);
        else setValueNoCheck(COLUMNNAME_Account_ID, Integer.valueOf(Account_ID));
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
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setTransactionOrganizationId(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) setValueNoCheck(COLUMNNAME_AD_OrgTrx_ID, null);
        else setValueNoCheck(COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
    }

    /**
     * Get Table.
     *
     * @return Database Table information
     */
    public int getRowTableId() {
        Integer ii = getValue(COLUMNNAME_AD_Table_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Table.
     *
     * @param AD_Table_ID Database Table information
     */
    public void setRowTableId(int AD_Table_ID) {
        if (AD_Table_ID < 1) setValueNoCheck(COLUMNNAME_AD_Table_ID, null);
        else setValueNoCheck(COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
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
     * Set Accounted Credit.
     *
     * @param AmtAcctCr Accounted Credit Amount
     */
    public void setAmtAcctCr(BigDecimal AmtAcctCr) {
        setValueNoCheck(COLUMNNAME_AmtAcctCr, AmtAcctCr);
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
     * Set Accounted Debit.
     *
     * @param AmtAcctDr Accounted Debit Amount
     */
    public void setAmtAcctDr(BigDecimal AmtAcctDr) {
        setValueNoCheck(COLUMNNAME_AmtAcctDr, AmtAcctDr);
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
     * Set Source Credit.
     *
     * @param AmtSourceCr Source Credit Amount
     */
    public void setAmtSourceCr(BigDecimal AmtSourceCr) {
        setValueNoCheck(COLUMNNAME_AmtSourceCr, AmtSourceCr);
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
     * Set Source Debit.
     *
     * @param AmtSourceDr Source Debit Amount
     */
    public void setAmtSourceDr(BigDecimal AmtSourceDr) {
        setValueNoCheck(COLUMNNAME_AmtSourceDr, AmtSourceDr);
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
        else setValueNoCheck(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
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
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValueNoCheck(COLUMNNAME_C_Activity_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Activity_ID, Integer.valueOf(C_Activity_ID));
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
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValueNoCheck(COLUMNNAME_C_BPartner_ID, null);
        else setValueNoCheck(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
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
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValueNoCheck(COLUMNNAME_C_Campaign_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Campaign_ID, Integer.valueOf(C_Campaign_ID));
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
     * Set Currency.
     *
     * @param C_Currency_ID The Currency for this record
     */
    public void setCurrencyId(int C_Currency_ID) {
        if (C_Currency_ID < 1) setValueNoCheck(COLUMNNAME_C_Currency_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
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
     * Set Location From.
     *
     * @param C_LocFrom_ID Location that inventory was moved from
     */
    public void setLocationFromId(int C_LocFrom_ID) {
        if (C_LocFrom_ID < 1) setValueNoCheck(COLUMNNAME_C_LocFrom_ID, null);
        else setValueNoCheck(COLUMNNAME_C_LocFrom_ID, Integer.valueOf(C_LocFrom_ID));
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
     * Set Location To.
     *
     * @param C_LocTo_ID Location that inventory was moved to
     */
    public void setLocationToId(int C_LocTo_ID) {
        if (C_LocTo_ID < 1) setValueNoCheck(COLUMNNAME_C_LocTo_ID, null);
        else setValueNoCheck(COLUMNNAME_C_LocTo_ID, Integer.valueOf(C_LocTo_ID));
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
     * Set Period.
     *
     * @param C_Period_ID Period of the Calendar
     */
    public void setPeriodId(int C_Period_ID) {
        if (C_Period_ID < 1) setValueNoCheck(COLUMNNAME_C_Period_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
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
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValueNoCheck(COLUMNNAME_C_Project_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Project_ID, Integer.valueOf(C_Project_ID));
    }

    /**
     * Get Project Phase.
     *
     * @return Phase of a Project
     */
    public int getProjectPhaseId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectPhase_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project Phase.
     *
     * @param C_ProjectPhase_ID Phase of a Project
     */
    public void setProjectPhaseId(int C_ProjectPhase_ID) {
        if (C_ProjectPhase_ID < 1) setValueNoCheck(COLUMNNAME_C_ProjectPhase_ID, null);
        else setValueNoCheck(COLUMNNAME_C_ProjectPhase_ID, Integer.valueOf(C_ProjectPhase_ID));
    }

    /**
     * Get Project Task.
     *
     * @return Actual Project Task in a Phase
     */
    public int getProjectTaskId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectTask_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project Task.
     *
     * @param C_ProjectTask_ID Actual Project Task in a Phase
     */
    public void setProjectTaskId(int C_ProjectTask_ID) {
        if (C_ProjectTask_ID < 1) setValueNoCheck(COLUMNNAME_C_ProjectTask_ID, null);
        else setValueNoCheck(COLUMNNAME_C_ProjectTask_ID, Integer.valueOf(C_ProjectTask_ID));
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
     * Set Sales Region.
     *
     * @param C_SalesRegion_ID Sales coverage region
     */
    public void setSalesRegionId(int C_SalesRegion_ID) {
        if (C_SalesRegion_ID < 1) setValueNoCheck(COLUMNNAME_C_SalesRegion_ID, null);
        else setValueNoCheck(COLUMNNAME_C_SalesRegion_ID, Integer.valueOf(C_SalesRegion_ID));
    }

    /**
     * Get Sub Account.
     *
     * @return Sub account for Element Value
     */
    public int getSubAccountId() {
        Integer ii = getValue(COLUMNNAME_C_SubAcct_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sub Account.
     *
     * @param C_SubAcct_ID Sub account for Element Value
     */
    public void setSubAccountId(int C_SubAcct_ID) {
        if (C_SubAcct_ID < 1) setValue(COLUMNNAME_C_SubAcct_ID, null);
        else setValue(COLUMNNAME_C_SubAcct_ID, Integer.valueOf(C_SubAcct_ID));
    }

    /**
     * Get Tax.
     *
     * @return Tax identifier
     */
    public int getTaxId() {
        Integer ii = getValue(COLUMNNAME_C_Tax_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Tax.
     *
     * @param C_Tax_ID Tax identifier
     */
    public void setTaxId(int C_Tax_ID) {
        if (C_Tax_ID < 1) setValueNoCheck(COLUMNNAME_C_Tax_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
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
     * Set UOM.
     *
     * @param C_UOM_ID Unit of Measure
     */
    public void setUOMId(int C_UOM_ID) {
        if (C_UOM_ID < 1) setValueNoCheck(COLUMNNAME_C_UOM_ID, null);
        else setValueNoCheck(COLUMNNAME_C_UOM_ID, Integer.valueOf(C_UOM_ID));
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
        setValueNoCheck(COLUMNNAME_DateAcct, DateAcct);
    }

    /**
     * Set Transaction Date.
     *
     * @param DateTrx Transaction Date
     */
    public void setDateTrx(Timestamp DateTrx) {
        setValueNoCheck(COLUMNNAME_DateTrx, DateTrx);
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
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
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
     * Set Budget.
     *
     * @param GL_Budget_ID General Ledger Budget
     */
    public void setGLBudgetId(int GL_Budget_ID) {
        if (GL_Budget_ID < 1) setValueNoCheck(COLUMNNAME_GL_Budget_ID, null);
        else setValueNoCheck(COLUMNNAME_GL_Budget_ID, Integer.valueOf(GL_Budget_ID));
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
     * Set GL Category.
     *
     * @param GL_Category_ID General Ledger Category
     */
    public void setGLCategoryId(int GL_Category_ID) {
        if (GL_Category_ID < 1) setValueNoCheck(COLUMNNAME_GL_Category_ID, null);
        else setValueNoCheck(COLUMNNAME_GL_Category_ID, Integer.valueOf(GL_Category_ID));
    }

    /**
     * Get Line ID.
     *
     * @return Transaction line ID (internal)
     */
    public int getLineId() {
        Integer ii = getValue(COLUMNNAME_Line_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line ID.
     *
     * @param Line_ID Transaction line ID (internal)
     */
    public void setLineId(int Line_ID) {
        if (Line_ID < 1) setValueNoCheck(COLUMNNAME_Line_ID, null);
        else setValueNoCheck(COLUMNNAME_Line_ID, Integer.valueOf(Line_ID));
    }

    /**
     * Get Locator.
     *
     * @return Warehouse Locator
     */
    public int getLocatorId() {
        Integer ii = getValue(COLUMNNAME_M_Locator_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Locator.
     *
     * @param M_Locator_ID Warehouse Locator
     */
    public void setLocatorId(int M_Locator_ID) {
        if (M_Locator_ID < 1) setValueNoCheck(COLUMNNAME_M_Locator_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Locator_ID, Integer.valueOf(M_Locator_ID));
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
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValueNoCheck(COLUMNNAME_M_Product_ID, null);
        else setValueNoCheck(COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
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

        setValueNoCheck(COLUMNNAME_PostingType, PostingType);
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
     * Set Quantity.
     *
     * @param Qty Quantity
     */
    public void setQty(BigDecimal Qty) {
        setValueNoCheck(COLUMNNAME_Qty, Qty);
    }

    /**
     * Get Record ID.
     *
     * @return Direct internal record ID
     */
    public int getRecordId() {
        Integer ii = getValue(COLUMNNAME_Record_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Record ID.
     *
     * @param Record_ID Direct internal record ID
     */
    public void setRecordId(int Record_ID) {
        if (Record_ID < 0) setValueNoCheck(COLUMNNAME_Record_ID, null);
        else setValueNoCheck(COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
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
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValueNoCheck(COLUMNNAME_User1_ID, null);
        else setValueNoCheck(COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
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

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValueNoCheck(COLUMNNAME_User2_ID, null);
        else setValueNoCheck(COLUMNNAME_User2_ID, Integer.valueOf(User2_ID));
    }

    /**
     * Get User Column 1.
     *
     * @return User defined accounting Element
     */
    public int getUserElement1Id() {
        Integer ii = getValue(COLUMNNAME_UserElement1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Column 1.
     *
     * @param UserElement1_ID User defined accounting Element
     */
    public void setUserElement1Id(int UserElement1_ID) {
        if (UserElement1_ID < 1) setValueNoCheck(COLUMNNAME_UserElement1_ID, null);
        else setValueNoCheck(COLUMNNAME_UserElement1_ID, Integer.valueOf(UserElement1_ID));
    }

    /**
     * Get User Column 2.
     *
     * @return User defined accounting Element
     */
    public int getUserElement2Id() {
        Integer ii = getValue(COLUMNNAME_UserElement2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Column 2.
     *
     * @param UserElement2_ID User defined accounting Element
     */
    public void setUserElement2Id(int UserElement2_ID) {
        if (UserElement2_ID < 1) setValueNoCheck(COLUMNNAME_UserElement2_ID, null);
        else setValueNoCheck(COLUMNNAME_UserElement2_ID, Integer.valueOf(UserElement2_ID));
    }

    @Override
    public int getTableId() {
        return I_Fact_Acct.Table_ID;
    }
}
