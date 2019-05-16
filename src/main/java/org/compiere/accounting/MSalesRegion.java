package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_SalesRegion;
import org.compiere.orm.MTree_Base;

/**
 * Sales Region Model
 *
 * @author Jorg Janke
 * @version $Id: MSalesRegion.java,v 1.3 2006/07/30 00:54:54 jjanke Exp $
 */
public class MSalesRegion extends X_C_SalesRegion {
    /**
     *
     */
    private static final long serialVersionUID = -6166934441386906620L;

    /**
     * ************************************************************************ Default Constructor
     *
     * @param ctx              context
     * @param C_SalesRegion_ID id
     * @param trxName          transaction
     */
    public MSalesRegion(int C_SalesRegion_ID) {
        super(C_SalesRegion_ID);
    } //	MSalesRegion

    /**
     * Load Constructor
     *
     * @param ctx     context
     * @param rs      result set
     * @param trxName transaction
     */
    public MSalesRegion(Row row) {
        super(row);
    } //	MSalesRegion

    /**
     * After Save. Insert - create tree
     *
     * @param newRecord insert
     * @param success   save success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;
        if (newRecord) insertTree(MTree_Base.TREETYPE_SalesRegion);
        if (newRecord || isValueChanged(I_C_SalesRegion.COLUMNNAME_Value))
            updateTree(MTree_Base.TREETYPE_SalesRegion);
        //	Value/Name change
        if (!newRecord && (isValueChanged("Value") || isValueChanged("Name")))
            MAccount.updateValueDescription(
                    "C_SalesRegion_ID=" + getSalesRegionId());

        return true;
    } //	afterSave

    /**
     * After Delete
     *
     * @param success
     * @return deleted
     */
    protected boolean afterDelete(boolean success) {
        if (success) deleteTree(MTree_Base.TREETYPE_SalesRegion);
        return success;
    } //	afterDelete
} //	MSalesRegion
