package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_C_ProjectIssue;
import org.compiere.model.I_M_Locator;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import software.hsharp.core.orm.MBaseTableKt;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_ProjectIssue
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_ProjectIssue extends PO implements I_C_ProjectIssue {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_ProjectIssue(int C_ProjectIssue_ID) {
        super(C_ProjectIssue_ID);
        /**
         * if (C_ProjectIssue_ID == 0) { setProjectId (0); setProjectIssueId (0); setLine (0);
         * // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_ProjectIssue WHERE
         * C_Project_ID=@C_Project_ID@ setAttributeSetInstanceId (0); setLocatorId (0);
         * setMovementDate (new Timestamp( System.currentTimeMillis() )); setMovementQty (Env.ZERO);
         * setProductId (0); setPosted (false); // N setProcessed (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_ProjectIssue(Row row) {
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
        StringBuffer sb = new StringBuffer("X_C_ProjectIssue[").append(getId()).append("]");
        return sb.toString();
    }

    public org.compiere.model.I_C_Project getProject() throws RuntimeException {
        return (org.compiere.model.I_C_Project)
                MBaseTableKt.getTable(org.compiere.model.I_C_Project.Table_Name)
                        .getPO(getProjectId());
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
     * Get Project Issue.
     *
     * @return Project Issues (Material, Labor)
     */
    public int getProjectIssueId() {
        Integer ii = getValue(COLUMNNAME_C_ProjectIssue_ID);
        if (ii == null) return 0;
        return ii;
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
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(COLUMNNAME_Line, Integer.valueOf(Line));
    }

    /**
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getAttributeSetInstanceId() {
        Integer ii = getValue(COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
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
        if (M_InOutLine_ID < 1) setValue(COLUMNNAME_M_InOutLine_ID, null);
        else setValue(COLUMNNAME_M_InOutLine_ID, Integer.valueOf(M_InOutLine_ID));
    }

    public I_M_Locator getLocator() throws RuntimeException {
        return (I_M_Locator)
                MBaseTableKt.getTable(I_M_Locator.Table_Name).getPO(getLocatorId());
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
        if (M_Locator_ID < 1) setValue(COLUMNNAME_M_Locator_ID, null);
        else setValue(COLUMNNAME_M_Locator_ID, M_Locator_ID);
    }

    /**
     * Get Movement Date.
     *
     * @return Date a product was moved in or out of inventory
     */
    public Timestamp getMovementDate() {
        return (Timestamp) getValue(COLUMNNAME_MovementDate);
    }

    /**
     * Set Movement Date.
     *
     * @param MovementDate Date a product was moved in or out of inventory
     */
    public void setMovementDate(Timestamp MovementDate) {
        setValue(COLUMNNAME_MovementDate, MovementDate);
    }

    /**
     * Get Movement Quantity.
     *
     * @return Quantity of a product moved.
     */
    public BigDecimal getMovementQty() {
        BigDecimal bd = getValue(COLUMNNAME_MovementQty);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Movement Quantity.
     *
     * @param MovementQty Quantity of a product moved.
     */
    public void setMovementQty(BigDecimal MovementQty) {
        setValue(COLUMNNAME_MovementQty, MovementQty);
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
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Set Posted.
     *
     * @param Posted Posting status
     */
    public void setPosted(boolean Posted) {
        setValue(COLUMNNAME_Posted, Posted);
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
     * Get Expense Line.
     *
     * @return Time and Expense Report Line
     */
    public int getTimeExpenseLineId() {
        Integer ii = getValue(COLUMNNAME_S_TimeExpenseLine_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Expense Line.
     *
     * @param S_TimeExpenseLine_ID Time and Expense Report Line
     */
    public void setTimeExpenseLineId(int S_TimeExpenseLine_ID) {
        if (S_TimeExpenseLine_ID < 1) setValue(COLUMNNAME_S_TimeExpenseLine_ID, null);
        else setValue(COLUMNNAME_S_TimeExpenseLine_ID, S_TimeExpenseLine_ID);
    }

    @Override
    public int getTableId() {
        return I_C_ProjectIssue.Table_ID;
    }
}
