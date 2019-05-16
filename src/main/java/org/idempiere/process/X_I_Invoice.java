package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_I_Invoice;
import org.compiere.orm.BasePOName;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class X_I_Invoice extends BasePOName implements I_I_Invoice {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_I_Invoice(int I_Invoice_ID) {
        super(I_Invoice_ID);
    }

    /**
     * Load Constructor
     */
    public X_I_Invoice(Row row) {
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
        return "X_I_Invoice[" + getId() + "]";
    }

    /**
     * Get Address 1.
     *
     * @return Address line 1 for this location
     */
    public String getAddress1() {
        return getValue(COLUMNNAME_Address1);
    }

    /**
     * Get Address 2.
     *
     * @return Address line 2 for this location
     */
    public String getAddress2() {
        return getValue(COLUMNNAME_Address2);
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
     * Get User/Contact.
     *
     * @return User within the system - Internal or Business Partner Contact
     */
    public int getUserId() {
        Integer ii = getValue(COLUMNNAME_AD_User_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User/Contact.
     *
     * @param AD_User_ID User within the system - Internal or Business Partner Contact
     */
    public void setUserId(int AD_User_ID) {
        if (AD_User_ID < 1) setValue(COLUMNNAME_AD_User_ID, null);
        else setValue(COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
    }

    /**
     * Get Business Partner Key.
     *
     * @return Key of the Business Partner
     */
    public String getBPartnerValue() {
        return getValue(COLUMNNAME_BPartnerValue);
    }

    /**
     * Set Business Partner Key.
     *
     * @param BPartnerValue Key of the Business Partner
     */
    public void setBPartnerValue(String BPartnerValue) {
        setValue(COLUMNNAME_BPartnerValue, BPartnerValue);
    }

    /**
     * Get 1099 Box.
     *
     * @return 1099 Box
     */
    public int get1099BoxId() {
        Integer ii = getValue(COLUMNNAME_C_1099Box_ID);
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
     * Set Business Partner .
     *
     * @param C_BPartner_ID Identifies a Business Partner
     */
    public void setBusinessPartnerId(int C_BPartner_ID) {
        if (C_BPartner_ID < 1) setValue(COLUMNNAME_C_BPartner_ID, null);
        else setValue(COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
    }

    /**
     * Get Partner Location.
     *
     * @return Identifies the (ship to) address for this Business Partner
     */
    public int getBusinessPartnerLocationId() {
        Integer ii = getValue(COLUMNNAME_C_BPartner_Location_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Partner Location.
     *
     * @param C_BPartner_Location_ID Identifies the (ship to) address for this Business Partner
     */
    public void setBusinessPartnerLocationId(int C_BPartner_Location_ID) {
        if (C_BPartner_Location_ID < 1) setValue(COLUMNNAME_C_BPartner_Location_ID, null);
        else setValue(COLUMNNAME_C_BPartner_Location_ID, Integer.valueOf(C_BPartner_Location_ID));
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
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getChargeId() {
        Integer ii = getValue(COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Country.
     *
     * @return Country
     */
    public int getCountryId() {
        Integer ii = getValue(COLUMNNAME_C_Country_ID);
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
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValue(COLUMNNAME_C_Invoice_ID, null);
        else setValue(COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
    }

    /**
     * Set Invoice Line.
     *
     * @param C_InvoiceLine_ID Invoice Detail Line
     */
    public void setInvoiceLineId(int C_InvoiceLine_ID) {
        if (C_InvoiceLine_ID < 1) setValue(COLUMNNAME_C_InvoiceLine_ID, null);
        else setValue(COLUMNNAME_C_InvoiceLine_ID, Integer.valueOf(C_InvoiceLine_ID));
    }

    /**
     * Get City.
     *
     * @return Identifies a City
     */
    public String getCity() {
        return getValue(COLUMNNAME_City);
    }

    /**
     * Get Address.
     *
     * @return Location or Address
     */
    public int getLocationId() {
        Integer ii = getValue(COLUMNNAME_C_Location_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Address.
     *
     * @param C_Location_ID Location or Address
     */
    public void setLocationId(int C_Location_ID) {
        if (C_Location_ID < 1) setValue(COLUMNNAME_C_Location_ID, null);
        else setValue(COLUMNNAME_C_Location_ID, Integer.valueOf(C_Location_ID));
    }

    /**
     * Get Contact Name.
     *
     * @return Business Partner Contact Name
     */
    public String getContactName() {
        return getValue(COLUMNNAME_ContactName);
    }

    /**
     * Get Payment Term.
     *
     * @return The terms of Payment (timing, discount)
     */
    public int getPaymentTermId() {
        Integer ii = getValue(COLUMNNAME_C_PaymentTerm_ID);
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
     * Get Region.
     *
     * @return Identifies a geographical Region
     */
    public int getRegionId() {
        Integer ii = getValue(COLUMNNAME_C_Region_ID);
        if (ii == null) return 0;
        return ii;
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
        if (C_Tax_ID < 1) setValue(COLUMNNAME_C_Tax_ID, null);
        else setValue(COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
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
     * Get Date Invoiced.
     *
     * @return Date printed on Invoice
     */
    public Timestamp getDateInvoiced() {
        return (Timestamp) getValue(COLUMNNAME_DateInvoiced);
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
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Get EMail Address.
     *
     * @return Electronic Mail Address
     */
    public String getEMail() {
        return getValue(COLUMNNAME_EMail);
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
     * Get Sales Transaction.
     *
     * @return This is a Sales Transaction
     */
    public boolean isSOTrx() {
        Object oo = getValue(COLUMNNAME_IsSOTrx);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Line Description.
     *
     * @return Description of the Line
     */
    public String getLineDescription() {
        return getValue(COLUMNNAME_LineDescription);
    }

    /**
     * Get Price List.
     *
     * @return Unique identifier of a Price List
     */
    public int getPriceListId() {
        Integer ii = getValue(COLUMNNAME_M_PriceList_ID);
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
     * Get Phone.
     *
     * @return Identifies a telephone number
     */
    public String getPhone() {
        return getValue(COLUMNNAME_Phone);
    }

    /**
     * Get ZIP.
     *
     * @return Postal code
     */
    public String getPostal() {
        return getValue(COLUMNNAME_Postal);
    }

    /**
     * Get Unit Price.
     *
     * @return Actual Price
     */
    public BigDecimal getPriceActual() {
        BigDecimal bd = getValue(COLUMNNAME_PriceActual);
        if (bd == null) return Env.ZERO;
        return bd;
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
     * Get Ordered Quantity.
     *
     * @return Ordered Quantity
     */
    public BigDecimal getQtyOrdered() {
        BigDecimal bd = getValue(COLUMNNAME_QtyOrdered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Sales Representative.
     *
     * @return Sales Representative or Company Agent
     */
    public int getSalesRepresentativeId() {
        Integer ii = getValue(COLUMNNAME_SalesRep_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Tax Amount.
     *
     * @return Tax Amount for a document
     */
    public BigDecimal getTaxAmt() {
        BigDecimal bd = getValue(COLUMNNAME_TaxAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

}
