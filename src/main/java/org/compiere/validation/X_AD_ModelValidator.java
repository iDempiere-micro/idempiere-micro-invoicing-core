package org.compiere.validation;

import kotliquery.Row;
import org.compiere.model.ModelValidator;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for AD_ModelValidator
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_ModelValidator extends BasePOName implements ModelValidator {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_AD_ModelValidator(int AD_ModelValidator_ID) {
        super(AD_ModelValidator_ID);
    }

    /**
     * Load Constructor
     */
    public X_AD_ModelValidator(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 4 - System
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }


    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_AD_ModelValidator[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Model Validation Class.
     *
     * @return Model Validation Class
     */
    public String getModelValidationClass() {
        return getValue(COLUMNNAME_ModelValidationClass);
    }

}
