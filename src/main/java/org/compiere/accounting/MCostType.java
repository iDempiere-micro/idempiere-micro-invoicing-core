package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.AccountingSchema;
import org.compiere.util.MsgKt;

/**
 * Cost Type Model
 *
 * @author Jorg Janke
 * @version $Id: MCostType.java,v 1.2 2006/07/30 00:58:38 jjanke Exp $
 */
public class MCostType extends X_M_CostType {
    /**
     *
     */
    private static final long serialVersionUID = -2060640115481013228L;

    /**
     * Standard Constructor
     *
     * @param ctx           context
     * @param M_CostType_ID id
     */
    public MCostType(int M_CostType_ID) {
        super(M_CostType_ID);
    } //	MCostType

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MCostType(Row row) {
        super(row);
    } //	MCostType

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MCostType[" + getId() + "-" + getName() + "]";
    } //	toString

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (getOrgId() != 0) setOrgId(0);
        return true;
    } //	beforeSave

    /**
     * Before Delete
     *
     * @return true if it can be deleted
     */
    protected boolean beforeDelete() {
        AccountingSchema[] ass = MAcctSchema.getClientAcctSchema(getClientId());
        for (AccountingSchema accountingSchema : ass) {
            if (accountingSchema.getCostTypeId() == getCostTypeId()) {
                log.saveError(
                        "CannotDelete", MsgKt.getElementTranslation("C_AcctSchema_ID") + " - " + accountingSchema.getName());
                return false;
            }
        }
        return true;
    } //	beforeDelete

    public void setClientOrg(int a, int b) {
        super.setClientOrg(a, b);
    }
} //	MCostType
