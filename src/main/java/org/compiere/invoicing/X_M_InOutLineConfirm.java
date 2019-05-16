package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_M_InOutLineConfirm;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Generated Model for M_InOutLineConfirm
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_InOutLineConfirm extends PO implements I_M_InOutLineConfirm {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_InOutLineConfirm(int M_InOutLineConfirm_ID) {
        super(M_InOutLineConfirm_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_InOutLineConfirm(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 1 - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_M_InOutLineConfirm[" + getId() + "]";
    }

    /**
     * Set Invoice Line.
     *
     * @param C_InvoiceLine_ID Invoice Detail Line
     */
    public void setInvoiceLineId(int C_InvoiceLine_ID) {
        if (C_InvoiceLine_ID < 1) setValue(COLUMNNAME_C_InvoiceLine_ID, null);
        else setValue(COLUMNNAME_C_InvoiceLine_ID, C_InvoiceLine_ID);
    }

    /**
     * Set Confirmation No.
     *
     * @param ConfirmationNo Confirmation Number
     */
    public void setConfirmationNo(String ConfirmationNo) {
        setValue(COLUMNNAME_ConfirmationNo, ConfirmationNo);
    }

    /**
     * Get Confirmed Quantity.
     *
     * @return Confirmation of a received quantity
     */
    public BigDecimal getConfirmedQty() {
        BigDecimal bd = getValue(COLUMNNAME_ConfirmedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Confirmed Quantity.
     *
     * @param ConfirmedQty Confirmation of a received quantity
     */
    public void setConfirmedQty(BigDecimal ConfirmedQty) {
        setValue(COLUMNNAME_ConfirmedQty, ConfirmedQty);
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
     * Get Difference.
     *
     * @return Difference Quantity
     */
    public BigDecimal getDifferenceQty() {
        BigDecimal bd = getValue(COLUMNNAME_DifferenceQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Difference.
     *
     * @param DifferenceQty Difference Quantity
     */
    public void setDifferenceQty(BigDecimal DifferenceQty) {
        setValue(COLUMNNAME_DifferenceQty, DifferenceQty);
    }

    /**
     * Set Ship/Receipt Confirmation.
     *
     * @param M_InOutConfirm_ID Material Shipment or Receipt Confirmation
     */
    public void setInOutConfirmId(int M_InOutConfirm_ID) {
        if (M_InOutConfirm_ID < 1) setValueNoCheck(COLUMNNAME_M_InOutConfirm_ID, null);
        else setValueNoCheck(COLUMNNAME_M_InOutConfirm_ID, M_InOutConfirm_ID);
    }

    /**
     * Get Shipment/Receipt Line.
     *
     * @return Line on Shipment or Receipt document
     */
    public int getInOutLineId() {
        Integer ii = getValue(COLUMNNAME_M_InOutLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Shipment/Receipt Line.
     *
     * @param M_InOutLine_ID Line on Shipment or Receipt document
     */
    public void setInOutLineId(int M_InOutLine_ID) {
        if (M_InOutLine_ID < 1) setValueNoCheck(COLUMNNAME_M_InOutLine_ID, null);
        else setValueNoCheck(COLUMNNAME_M_InOutLine_ID, M_InOutLine_ID);
    }

    /**
     * Set Phys.Inventory Line.
     *
     * @param M_InventoryLine_ID Unique line in an Inventory document
     */
    public void setInventoryLineId(int M_InventoryLine_ID) {
        if (M_InventoryLine_ID < 1) setValue(COLUMNNAME_M_InventoryLine_ID, null);
        else setValue(COLUMNNAME_M_InventoryLine_ID, M_InventoryLine_ID);
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
     * Get Scrapped Quantity.
     *
     * @return The Quantity scrapped due to QA issues
     */
    public BigDecimal getScrappedQty() {
        BigDecimal bd = getValue(COLUMNNAME_ScrappedQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Scrapped Quantity.
     *
     * @param ScrappedQty The Quantity scrapped due to QA issues
     */
    public void setScrappedQty(BigDecimal ScrappedQty) {
        setValue(COLUMNNAME_ScrappedQty, ScrappedQty);
    }

    /**
     * Get Target Quantity.
     *
     * @return Target Movement Quantity
     */
    public BigDecimal getTargetQty() {
        BigDecimal bd = getValue(COLUMNNAME_TargetQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Target Quantity.
     *
     * @param TargetQty Target Movement Quantity
     */
    public void setTargetQty(BigDecimal TargetQty) {
        setValueNoCheck(COLUMNNAME_TargetQty, TargetQty);
    }

    @Override
    public int getTableId() {
        return I_M_InOutLineConfirm.Table_ID;
    }
}
