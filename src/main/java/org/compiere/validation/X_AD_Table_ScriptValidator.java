package org.compiere.validation;

import org.compiere.model.I_AD_Table_ScriptValidator;
import org.compiere.orm.PO;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for AD_Table_ScriptValidator
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_AD_Table_ScriptValidator extends PO
        implements I_AD_Table_ScriptValidator {

    /**
     * Table Before New = TBN
     */
    public static final String EVENTMODELVALIDATOR_TableBeforeNew = "TBN";
    /**
     * Table Before Change = TBC
     */
    public static final String EVENTMODELVALIDATOR_TableBeforeChange = "TBC";
    /**
     * Table Before Delete = TBD
     */
    public static final String EVENTMODELVALIDATOR_TableBeforeDelete = "TBD";
    /**
     * Table After New = TAN
     */
    public static final String EVENTMODELVALIDATOR_TableAfterNew = "TAN";
    /**
     * Table After Change = TAC
     */
    public static final String EVENTMODELVALIDATOR_TableAfterChange = "TAC";
    /**
     * Table After Delete = TAD
     */
    public static final String EVENTMODELVALIDATOR_TableAfterDelete = "TAD";
    /**
     * Document Before Prepare = DBPR
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforePrepare = "DBPR";
    /**
     * Document Before Void = DBVO
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforeVoid = "DBVO";
    /**
     * Document Before Close = DBCL
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforeClose = "DBCL";
    /**
     * Document Before Reactivate = DBAC
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforeReactivate = "DBAC";
    /**
     * Document Before Reverse Correct = DBRC
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforeReverseCorrect = "DBRC";
    /**
     * Document Before Reverse Accrual = DBRA
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforeReverseAccrual = "DBRA";
    /**
     * Document Before Complete = DBCO
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforeComplete = "DBCO";
    /**
     * Document Before Post = DBPO
     */
    public static final String EVENTMODELVALIDATOR_DocumentBeforePost = "DBPO";
    /**
     * Document After Prepare = DAPR
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterPrepare = "DAPR";
    /**
     * Document After Void = DAVO
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterVoid = "DAVO";
    /**
     * Document After Close = DACL
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterClose = "DACL";
    /**
     * Document After Reactivate = DAAC
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterReactivate = "DAAC";
    /**
     * Document After Reverse Correct = DARC
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterReverseCorrect = "DARC";
    /**
     * Document After Reverse Accrual = DARA
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterReverseAccrual = "DARA";
    /**
     * Document After Complete = DACO
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterComplete = "DACO";
    /**
     * Document After Post = DAPO
     */
    public static final String EVENTMODELVALIDATOR_DocumentAfterPost = "DAPO";
    /**
     * Table After New Replication = TANR
     */
    public static final String EVENTMODELVALIDATOR_TableAfterNewReplication = "TANR";
    /**
     * Table After Change Replication = TACR
     */
    public static final String EVENTMODELVALIDATOR_TableAfterChangeReplication = "TACR";
    /**
     * Table Before Delete Replication = TBDR
     */
    public static final String EVENTMODELVALIDATOR_TableBeforeDeleteReplication = "TBDR";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;
    /**
     * Standard Constructor
     */
    public X_AD_Table_ScriptValidator(
            Properties ctx, int AD_Table_ScriptValidator_ID) {
        super(ctx, AD_Table_ScriptValidator_ID);
        /**
         * if (AD_Table_ScriptValidator_ID == 0) { setAD_Rule_ID (0); setColumnTableId (0);
         * setAD_Table_ScriptValidator_ID (0); setEventModelValidator (null); setSeqNo (0); // 0 }
         */
    }
    /**
     * Load Constructor
     */
    public X_AD_Table_ScriptValidator(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    }

    /**
     * AccessLevel
     *
     * @return 4 - System
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_AD_Table_ScriptValidator[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Rule.
     *
     * @return Rule
     */
    public int getRuleId() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_Rule_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Table.
     *
     * @return Database Table information
     */
    public int getDBTableId() {
        Integer ii = (Integer) getValue(COLUMNNAME_AD_Table_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Event Model Validator.
     *
     * @return Event Model Validator
     */
    public String getEventModelValidator() {
        return (String) getValue(COLUMNNAME_EventModelValidator);
    }

    /**
     * Get Sequence.
     *
     * @return Method of ordering records; lowest number comes first
     */
    public int getSeqNo() {
        Integer ii = (Integer) getValue(COLUMNNAME_SeqNo);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sequence.
     *
     * @param SeqNo Method of ordering records; lowest number comes first
     */
    public void setSeqNo(int SeqNo) {
        set_Value(COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
    }

    @Override
    public int getTableId() {
        return I_AD_Table_ScriptValidator.Table_ID;
    }
}
