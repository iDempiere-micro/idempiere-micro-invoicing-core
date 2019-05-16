package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_M_ChangeRequest;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for M_ChangeRequest
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_M_ChangeRequest extends BasePOName implements I_M_ChangeRequest {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_ChangeRequest(int M_ChangeRequest_ID) {
        super(M_ChangeRequest_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_ChangeRequest(Row row) {
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
        return "X_M_ChangeRequest[" + getId() + "]";
    }

    /**
     * Set Comment/Help.
     *
     * @param Help Comment or Hint
     */
    public void setHelp(String Help) {
        setValue(COLUMNNAME_Help, Help);
    }

    /**
     * Set Approved.
     *
     * @param IsApproved Indicates if this document requires approval
     */
    public void setIsApproved(boolean IsApproved) {
        setValue(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
    }

    /**
     * Get Change Notice.
     *
     * @return Bill of Materials (Engineering) Change Notice (Version)
     */
    public int getChangeNoticeId() {
        Integer ii = getValue(COLUMNNAME_M_ChangeNotice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Change Notice.
     *
     * @param M_ChangeNotice_ID Bill of Materials (Engineering) Change Notice (Version)
     */
    public void setChangeNoticeId(int M_ChangeNotice_ID) {
        if (M_ChangeNotice_ID < 1) setValueNoCheck(COLUMNNAME_M_ChangeNotice_ID, null);
        else setValueNoCheck(COLUMNNAME_M_ChangeNotice_ID, Integer.valueOf(M_ChangeNotice_ID));
    }

    /**
     * Get Fixed in.
     *
     * @return Fixed in Change Notice
     */
    public int getFixChangeNoticeId() {
        Integer ii = getValue(COLUMNNAME_M_FixChangeNotice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get BOM & Formula.
     *
     * @return BOM & Formula
     */
    public int getProductBOMId() {
        Integer ii = getValue(COLUMNNAME_PP_Product_BOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set BOM & Formula.
     *
     * @param PP_Product_BOM_ID BOM & Formula
     */
    public void setProductBOMId(int PP_Product_BOM_ID) {
        if (PP_Product_BOM_ID < 1) setValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, null);
        else setValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, Integer.valueOf(PP_Product_BOM_ID));
    }

    /**
     * Get Processed.
     *
     * @return The document has been processed
     */
    public boolean isProcessed() {
        Object oo = getValue(COLUMNNAME_Processed);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
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
        setValue(COLUMNNAME_Processed, Boolean.valueOf(Processed));
    }

    @Override
    public int getTableId() {
        return I_M_ChangeRequest.Table_ID;
    }
}
