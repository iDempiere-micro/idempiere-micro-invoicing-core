package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_InvoiceLine
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_InvoiceLine extends PO {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_InvoiceLine(int C_InvoiceLine_ID) {
        super(C_InvoiceLine_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_InvoiceLine(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return I_C_InvoiceLine.accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_C_InvoiceLine[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Asset Group.
     *
     * @return Group of Assets
     */
    public int getAssetGroupId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_A_Asset_Group_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Asset.
     *
     * @param A_Asset_ID Asset used internally or by customers
     */
    public void setA_AssetId(int A_Asset_ID) {
        if (A_Asset_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_A_Asset_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_A_Asset_ID, A_Asset_ID);
    }

    /**
     * Get Trx Organization.
     *
     * @return Performing or initiating organization
     */
    public int getTransactionOrganizationId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_AD_OrgTrx_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Trx Organization.
     *
     * @param AD_OrgTrx_ID Performing or initiating organization
     */
    public void setTransactionOrganizationId(int AD_OrgTrx_ID) {
        if (AD_OrgTrx_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_AD_OrgTrx_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_AD_OrgTrx_ID, AD_OrgTrx_ID);
    }

    /**
     * Set 1099 Box.
     *
     * @param C_1099Box_ID 1099 Box
     */
    public void set1099BoxId(int C_1099Box_ID) {
        if (C_1099Box_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_C_1099Box_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_C_1099Box_ID, C_1099Box_ID);
    }

    /**
     * Get Activity.
     *
     * @return Business Activity
     */
    public int getBusinessActivityId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_Activity_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Activity.
     *
     * @param C_Activity_ID Business Activity
     */
    public void setBusinessActivityId(int C_Activity_ID) {
        if (C_Activity_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_C_Activity_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_C_Activity_ID, C_Activity_ID);
    }

    /**
     * Get Campaign.
     *
     * @return Marketing Campaign
     */
    public int getCampaignId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_Campaign_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Campaign.
     *
     * @param C_Campaign_ID Marketing Campaign
     */
    public void setCampaignId(int C_Campaign_ID) {
        if (C_Campaign_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_C_Campaign_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_C_Campaign_ID, C_Campaign_ID);
    }

    /**
     * Get Charge.
     *
     * @return Additional document charges
     */
    public int getChargeId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_Charge_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Charge.
     *
     * @param C_Charge_ID Additional document charges
     */
    public void setChargeId(int C_Charge_ID) {
        if (C_Charge_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_C_Charge_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_C_Charge_ID, C_Charge_ID);
    }

    public I_C_Invoice getInvoice() throws RuntimeException {
        return (I_C_Invoice)
                MBaseTableKt.getTable(I_C_Invoice.Table_Name)
                        .getPO(getInvoiceId());
    }

    /**
     * Get Invoice.
     *
     * @return Invoice Identifier
     */
    public int getInvoiceId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_Invoice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Invoice.
     *
     * @param C_Invoice_ID Invoice Identifier
     */
    public void setInvoiceId(int C_Invoice_ID) {
        if (C_Invoice_ID < 1) setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_Invoice_ID, null);
        else setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_Invoice_ID, C_Invoice_ID);
    }

    /**
     * Get Invoice Line.
     *
     * @return Invoice Detail Line
     */
    public int getInvoiceLineId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_InvoiceLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Sales Order Line.
     *
     * @return Sales Order Line
     */
    public int getOrderLineId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_OrderLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sales Order Line.
     *
     * @param C_OrderLine_ID Sales Order Line
     */
    public void setOrderLineId(int C_OrderLine_ID) {
        if (C_OrderLine_ID < 1) setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_OrderLine_ID, null);
        else
            setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_OrderLine_ID, C_OrderLine_ID);
    }

    /**
     * Get Project.
     *
     * @return Financial Project
     */
    public int getProjectId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_Project_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project.
     *
     * @param C_Project_ID Financial Project
     */
    public void setProjectId(int C_Project_ID) {
        if (C_Project_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_C_Project_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_C_Project_ID, C_Project_ID);
    }

    /**
     * Get Project Phase.
     *
     * @return Phase of a Project
     */
    public int getProjectPhaseId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_ProjectPhase_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project Phase.
     *
     * @param C_ProjectPhase_ID Phase of a Project
     */
    public void setProjectPhaseId(int C_ProjectPhase_ID) {
        if (C_ProjectPhase_ID < 1) setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_ProjectPhase_ID, null);
        else
            setValueNoCheck(
                    I_C_InvoiceLine.COLUMNNAME_C_ProjectPhase_ID, C_ProjectPhase_ID);
    }

    /**
     * Get Project Task.
     *
     * @return Actual Project Task in a Phase
     */
    public int getProjectTaskId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_ProjectTask_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Project Task.
     *
     * @param C_ProjectTask_ID Actual Project Task in a Phase
     */
    public void setProjectTaskId(int C_ProjectTask_ID) {
        if (C_ProjectTask_ID < 1) setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_ProjectTask_ID, null);
        else
            setValueNoCheck(
                    I_C_InvoiceLine.COLUMNNAME_C_ProjectTask_ID, C_ProjectTask_ID);
    }

    /**
     * Get Tax.
     *
     * @return Tax identifier
     */
    public int getTaxId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_Tax_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Tax.
     *
     * @param C_Tax_ID Tax identifier
     */
    public void setTaxId(int C_Tax_ID) {
        if (C_Tax_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_C_Tax_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_C_Tax_ID, C_Tax_ID);
    }

    /**
     * Get UOM.
     *
     * @return Unit of Measure
     */
    public int getUOMId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_C_UOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set UOM.
     *
     * @param C_UOM_ID Unit of Measure
     */
    public void setUOMId(int C_UOM_ID) {
        if (C_UOM_ID < 1) setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_UOM_ID, null);
        else setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_C_UOM_ID, C_UOM_ID);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(I_C_InvoiceLine.COLUMNNAME_Description);
    }

    /**
     * Set Description Only.
     *
     * @param IsDescription if true, the line is just description and no transaction
     */
    public void setIsDescription(boolean IsDescription) {
        setValue(I_C_InvoiceLine.COLUMNNAME_IsDescription, IsDescription);
    }

    /**
     * Get Description Only.
     *
     * @return if true, the line is just description and no transaction
     */
    public boolean isDescription() {
        Object oo = getValue(I_C_InvoiceLine.COLUMNNAME_IsDescription);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(I_C_InvoiceLine.COLUMNNAME_Description, Description);
    }

    /**
     * Set Printed.
     *
     * @param IsPrinted Indicates if this document / line is printed
     */
    public void setIsPrinted(boolean IsPrinted) {
        setValue(I_C_InvoiceLine.COLUMNNAME_IsPrinted, IsPrinted);
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(I_C_InvoiceLine.COLUMNNAME_Line, Line);
    }

    /**
     * Get Line Amount.
     *
     * @return Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public BigDecimal getLineNetAmt() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_LineNetAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Amount.
     *
     * @param LineNetAmt Line Extended Amount (Quantity * Actual Price) without Freight and Charges
     */
    public void setLineNetAmt(BigDecimal LineNetAmt) {
        setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_LineNetAmt, LineNetAmt);
    }

    /**
     * Get Line Total.
     *
     * @return Total line amount incl. Tax
     */
    public BigDecimal getLineTotalAmt() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_LineTotalAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Line Total.
     *
     * @param LineTotalAmt Total line amount incl. Tax
     */
    public void setLineTotalAmt(BigDecimal LineTotalAmt) {
        setValue(I_C_InvoiceLine.COLUMNNAME_LineTotalAmt, LineTotalAmt);
    }

    /**
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getAttributeSetInstanceId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Attribute Set Instance.
     *
     * @param M_AttributeSetInstance_ID Product Attribute Set Instance
     */
    public void setAttributeSetInstanceId(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0)
            setValue(I_C_InvoiceLine.COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValue(
                    I_C_InvoiceLine.COLUMNNAME_M_AttributeSetInstance_ID,
                    M_AttributeSetInstance_ID);
    }

    /**
     * Get Shipment/Receipt Line.
     *
     * @return Line on Shipment or Receipt document
     */
    public int getInOutLineId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_M_InOutLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Shipment/Receipt Line.
     *
     * @param M_InOutLine_ID Line on Shipment or Receipt document
     */
    public void setInOutLineId(int M_InOutLine_ID) {
        if (M_InOutLine_ID < 1) setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_M_InOutLine_ID, null);
        else
            setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_M_InOutLine_ID, M_InOutLine_ID);
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_M_Product_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Get RMA Line.
     *
     * @return Return Material Authorization Line
     */
    public int getRMALineId() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_M_RMALine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set RMA Line.
     *
     * @param M_RMALine_ID Return Material Authorization Line
     */
    public void setRMALineId(int M_RMALine_ID) {
        if (M_RMALine_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_M_RMALine_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_M_RMALine_ID, M_RMALine_ID);
    }

    /**
     * Get Unit Price.
     *
     * @return Actual Price
     */
    public BigDecimal getPriceActual() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_PriceActual);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Price.
     *
     * @return Price Entered - the price based on the selected/base UoM
     */
    public BigDecimal getPriceEntered() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_PriceEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Price.
     *
     * @param PriceEntered Price Entered - the price based on the selected/base UoM
     */
    public void setPriceEntered(BigDecimal PriceEntered) {
        setValue(I_C_InvoiceLine.COLUMNNAME_PriceEntered, PriceEntered);
    }

    /**
     * Get Limit Price.
     *
     * @return Lowest price for a product
     */
    public BigDecimal getPriceLimit() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_PriceLimit);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Limit Price.
     *
     * @param PriceLimit Lowest price for a product
     */
    public void setPriceLimit(BigDecimal PriceLimit) {
        setValue(I_C_InvoiceLine.COLUMNNAME_PriceLimit, PriceLimit);
    }

    /**
     * Get List Price.
     *
     * @return List Price
     */
    public BigDecimal getPriceList() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_PriceList);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set List Price.
     *
     * @param PriceList List Price
     */
    public void setPriceList(BigDecimal PriceList) {
        setValue(I_C_InvoiceLine.COLUMNNAME_PriceList, PriceList);
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(I_C_InvoiceLine.COLUMNNAME_Processed);
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
        setValue(I_C_InvoiceLine.COLUMNNAME_Processed, Processed);
    }

    /**
     * Get Quantity.
     *
     * @return The Quantity Entered is based on the selected UoM
     */
    public BigDecimal getQtyEntered() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_QtyEntered);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity.
     *
     * @param QtyEntered The Quantity Entered is based on the selected UoM
     */
    public void setQtyEntered(BigDecimal QtyEntered) {
        setValue(I_C_InvoiceLine.COLUMNNAME_QtyEntered, QtyEntered);
    }

    /**
     * Get Quantity Invoiced.
     *
     * @return Invoiced Quantity
     */
    public BigDecimal getQtyInvoiced() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_QtyInvoiced);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Quantity Invoiced.
     *
     * @param QtyInvoiced Invoiced Quantity
     */
    public void setQtyInvoiced(BigDecimal QtyInvoiced) {
        setValue(I_C_InvoiceLine.COLUMNNAME_QtyInvoiced, QtyInvoiced);
    }

    /**
     * Set Referenced Invoice Line.
     *
     * @param Ref_InvoiceLine_ID Referenced Invoice Line
     */
    public void setRef_InvoiceLineId(int Ref_InvoiceLine_ID) {
        if (Ref_InvoiceLine_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_Ref_InvoiceLine_ID, null);
        else
            setValue(I_C_InvoiceLine.COLUMNNAME_Ref_InvoiceLine_ID, Ref_InvoiceLine_ID);
    }

    /**
     * Set Revenue Recognition Amt.
     *
     * @param RRAmt Revenue Recognition Amount
     */
    public void setRRAmt(BigDecimal RRAmt) {
        setValue(I_C_InvoiceLine.COLUMNNAME_RRAmt, RRAmt);
    }

    /**
     * Set Revenue Recognition Start.
     *
     * @param RRStartDate Revenue Recognition Start Date
     */
    public void setRRStartDate(Timestamp RRStartDate) {
        setValue(I_C_InvoiceLine.COLUMNNAME_RRStartDate, RRStartDate);
    }

    /**
     * Set Resource Assignment.
     *
     * @param S_ResourceAssignment_ID Resource Assignment
     */
    public void setS_ResourceAssignmentId(int S_ResourceAssignment_ID) {
        if (S_ResourceAssignment_ID < 1)
            setValueNoCheck(I_C_InvoiceLine.COLUMNNAME_S_ResourceAssignment_ID, null);
        else
            setValueNoCheck(
                    I_C_InvoiceLine.COLUMNNAME_S_ResourceAssignment_ID,
                    S_ResourceAssignment_ID);
    }

    /**
     * Get Tax Amount.
     *
     * @return Tax Amount for a document
     */
    public BigDecimal getTaxAmt() {
        BigDecimal bd = getValue(I_C_InvoiceLine.COLUMNNAME_TaxAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Tax Amount.
     *
     * @param TaxAmt Tax Amount for a document
     */
    public void setTaxAmt(BigDecimal TaxAmt) {
        setValue(I_C_InvoiceLine.COLUMNNAME_TaxAmt, TaxAmt);
    }

    /**
     * Get User Element List 1.
     *
     * @return User defined list element #1
     */
    public int getUser1Id() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_User1_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 1.
     *
     * @param User1_ID User defined list element #1
     */
    public void setUser1Id(int User1_ID) {
        if (User1_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_User1_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_User1_ID, User1_ID);
    }

    /**
     * Get User Element List 2.
     *
     * @return User defined list element #2
     */
    public int getUser2Id() {
        Integer ii = getValue(I_C_InvoiceLine.COLUMNNAME_User2_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set User Element List 2.
     *
     * @param User2_ID User defined list element #2
     */
    public void setUser2Id(int User2_ID) {
        if (User2_ID < 1) setValue(I_C_InvoiceLine.COLUMNNAME_User2_ID, null);
        else setValue(I_C_InvoiceLine.COLUMNNAME_User2_ID, User2_ID);
    }

    @Override
    public int getTableId() {
        return I_C_InvoiceLine.Table_ID;
    }
}
