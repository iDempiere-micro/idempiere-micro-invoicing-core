package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_DocTypeCounter;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for C_DocTypeCounter
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_DocTypeCounter extends BasePOName implements I_C_DocTypeCounter {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_DocTypeCounter(int C_DocTypeCounter_ID) {
        super(C_DocTypeCounter_ID);
    }

    /**
     * Load Constructor
     */
    public X_C_DocTypeCounter(Row row) {
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
        return "X_C_DocTypeCounter[" + getId() + "]";
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
        else setValue(COLUMNNAME_C_DocType_ID, C_DocType_ID);
    }

    /**
     * Get Counter Document Type.
     *
     * @return Generated Counter Document Type (To)
     */
    public int getCounterDocTypeId() {
        Integer ii = getValue(COLUMNNAME_Counter_C_DocType_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Counter Document Type.
     *
     * @param Counter_C_DocType_ID Generated Counter Document Type (To)
     */
    public void setCounterDocTypeId(int Counter_C_DocType_ID) {
        if (Counter_C_DocType_ID < 1) setValue(COLUMNNAME_Counter_C_DocType_ID, null);
        else setValue(COLUMNNAME_Counter_C_DocType_ID, Counter_C_DocType_ID);
    }

    /**
     * Get Document Action.
     *
     * @return The targeted status of the document
     */
    public String getDocAction() {
        return getValue(COLUMNNAME_DocAction);
    }

    /**
     * Set Create Counter Document.
     *
     * @param IsCreateCounter Create Counter Document
     */
    public void setIsCreateCounter(boolean IsCreateCounter) {
        setValue(COLUMNNAME_IsCreateCounter, IsCreateCounter);
    }

    /**
     * Get Create Counter Document.
     *
     * @return Create Counter Document
     */
    public boolean isCreateCounter() {
        Object oo = getValue(COLUMNNAME_IsCreateCounter);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Valid.
     *
     * @param IsValid Element is valid
     */
    public void setIsValid(boolean IsValid) {
        setValue(COLUMNNAME_IsValid, IsValid);
    }

    /**
     * Get Valid.
     *
     * @return Element is valid
     */
    public boolean isValid() {
        Object oo = getValue(COLUMNNAME_IsValid);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    @Override
    public int getTableId() {
        return I_C_DocTypeCounter.Table_ID;
    }
}
