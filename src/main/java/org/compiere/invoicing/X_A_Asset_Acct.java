package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.AssetAccounting;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Generated Model for A_Asset_Acct
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_A_Asset_Acct extends PO implements AssetAccounting {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_A_Asset_Acct(int A_Asset_Acct_ID) {
        super(A_Asset_Acct_ID);
    }

    /**
     * Load Constructor
     */
    public X_A_Asset_Acct(Row row) {
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
        StringBuffer sb = new StringBuffer("X_A_Asset_Acct[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get Accumulated Depreciation Account.
     *
     * @return Accumulated Depreciation Account
     */
    public int getAccumdepreciationAcct() {
        Integer ii = getValue(COLUMNNAME_A_Accumdepreciation_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Asset.
     *
     * @param A_Asset_ID Asset used internally or by customers
     */
    public void setAssetId(int A_Asset_ID) {
        if (A_Asset_ID < 1) setValueNoCheck(COLUMNNAME_A_Asset_ID, null);
        else setValueNoCheck(COLUMNNAME_A_Asset_ID, Integer.valueOf(A_Asset_ID));
    }

    /**
     * Get Depreciation Account.
     *
     * @return Depreciation Account
     */
    public int getDepreciationAcct() {
        Integer ii = getValue(COLUMNNAME_A_Depreciation_Acct);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Depreciation (fiscal).
     *
     * @param A_Depreciation_F_ID Depreciation (fiscal)
     */
    public void setDepreciationFiscalId(int A_Depreciation_F_ID) {
        if (A_Depreciation_F_ID < 1) setValue(COLUMNNAME_A_Depreciation_F_ID, null);
        else setValue(COLUMNNAME_A_Depreciation_F_ID, Integer.valueOf(A_Depreciation_F_ID));
    }

    /**
     * Set Depreciation.
     *
     * @param A_Depreciation_ID Depreciation
     */
    public void setDepreciationId(int A_Depreciation_ID) {
        if (A_Depreciation_ID < 1) setValue(COLUMNNAME_A_Depreciation_ID, null);
        else setValue(COLUMNNAME_A_Depreciation_ID, Integer.valueOf(A_Depreciation_ID));
    }

    /**
     * Get Variable Percent.
     *
     * @return Variable Percent
     */
    public BigDecimal getDepreciationVariablePercent() {
        BigDecimal bd = getValue(COLUMNNAME_A_Depreciation_Variable_Perc);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Variable Percent (fiscal).
     *
     * @return Variable Percent (fiscal)
     */
    public BigDecimal getDepreciationVariablePercentFiscal() {
        BigDecimal bd = getValue(COLUMNNAME_A_Depreciation_Variable_Perc_F);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Set A_Period_End.
     *
     * @param A_Period_End A_Period_End
     */
    public void setPeriodEnd(int A_Period_End) {
        setValue(COLUMNNAME_A_Period_End, Integer.valueOf(A_Period_End));
    }

    /**
     * Set A_Period_Start.
     *
     * @param A_Period_Start A_Period_Start
     */
    public void setPeriodStart(int A_Period_Start) {
        setValue(COLUMNNAME_A_Period_Start, Integer.valueOf(A_Period_Start));
    }

    /**
     * Set Asset Salvage Value.
     *
     * @param A_Salvage_Value Asset Salvage Value
     */
    public void setSalvageValue(BigDecimal A_Salvage_Value) {
        setValue(COLUMNNAME_A_Salvage_Value, A_Salvage_Value);
    }

    /**
     * Get Accounting Schema.
     *
     * @return Rules for accounting
     */
    public int getAcctSchemaId() {
        Integer ii = getValue(COLUMNNAME_C_AcctSchema_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get PostingType.
     *
     * @return The type of posted amount for the transaction
     */
    public String getPostingType() {
        return getValue(COLUMNNAME_PostingType);
    }

    /**
     * Get Valid from.
     *
     * @return Valid from including this date (first day)
     */
    public Timestamp getValidFrom() {
        return (Timestamp) getValue(COLUMNNAME_ValidFrom);
    }

    /**
     * Set Valid from.
     *
     * @param ValidFrom Valid from including this date (first day)
     */
    public void setValidFrom(Timestamp ValidFrom) {
        setValueNoCheck(COLUMNNAME_ValidFrom, ValidFrom);
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }
}
