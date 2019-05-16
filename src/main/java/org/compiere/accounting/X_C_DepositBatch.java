package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_DepositBatch;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for C_DepositBatch
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_DepositBatch extends PO implements I_C_DepositBatch {

    /**
     * Drafted = DR
     */
    public static final String DOCSTATUS_Drafted = "DR";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_DepositBatch(int C_DepositBatch_ID) {
        super(C_DepositBatch_ID);
        /*
         * if (C_DepositBatch_ID == 0) { setBankAccountId (0); setDepositBatchId (0);
         * setDocumentTypeId (0); setDateDeposit (new Timestamp( System.currentTimeMillis() )); // @#Date@
         * setDateDoc (new Timestamp( System.currentTimeMillis() )); // @#Date@ setDepositAmt
         * (Env.ZERO); setDocStatus (null); // DR setDocumentNo (null); setProcessed (false); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_DepositBatch(Row row) {
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
        return "X_C_DepositBatch[" + getId() + "]";
    }

    /**
     * Get Deposit Batch.
     *
     * @return Deposit Batch
     */
    public int getDepositBatchId() {
        Integer ii = getValue(COLUMNNAME_C_DepositBatch_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Deposit Batch.
     *
     * @param C_DepositBatch_ID Deposit Batch
     */
    public void setDepositBatchId(int C_DepositBatch_ID) {
        if (C_DepositBatch_ID < 1) setValueNoCheck(COLUMNNAME_C_DepositBatch_ID, null);
        else setValueNoCheck(COLUMNNAME_C_DepositBatch_ID, Integer.valueOf(C_DepositBatch_ID));
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
     * Set Document Type.
     *
     * @param C_DocType_ID Document type or rules
     */
    public void setDocumentTypeId(int C_DocType_ID) {
        if (C_DocType_ID < 0) setValue(COLUMNNAME_C_DocType_ID, null);
        else setValue(COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
    }

    /**
     * Get Deposit Date.
     *
     * @return Deposit Date
     */
    public Timestamp getDateDeposit() {
        return (Timestamp) getValue(COLUMNNAME_DateDeposit);
    }

    /**
     * Set Deposit Date.
     *
     * @param DateDeposit Deposit Date
     */
    public void setDateDeposit(Timestamp DateDeposit) {
        setValue(COLUMNNAME_DateDeposit, DateDeposit);
    }

    /**
     * Get Document Date.
     *
     * @return Date of the Document
     */
    public Timestamp getDateDoc() {
        return (Timestamp) getValue(COLUMNNAME_DateDoc);
    }

    /**
     * Set Document Date.
     *
     * @param DateDoc Date of the Document
     */
    public void setDateDoc(Timestamp DateDoc) {
        setValue(COLUMNNAME_DateDoc, DateDoc);
    }

    /**
     * Get Deposit Amount.
     *
     * @return Deposit Amount
     */
    public BigDecimal getDepositAmt() {
        BigDecimal bd = getValue(COLUMNNAME_DepositAmt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set Deposit Amount.
     *
     * @param DepositAmt Deposit Amount
     */
    public void setDepositAmt(BigDecimal DepositAmt) {
        setValue(COLUMNNAME_DepositAmt, DepositAmt);
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
     * Set Document Status.
     *
     * @param DocStatus The current status of the document
     */
    public void setDocStatus(String DocStatus) {

        setValue(COLUMNNAME_DocStatus, DocStatus);
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
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Boolean.valueOf(Processing));
    }

    @Override
    public int getTableId() {
        return I_C_DepositBatch.Table_ID;
    }
}
