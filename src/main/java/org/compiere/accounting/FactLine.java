package org.compiere.accounting;

import org.compiere.bo.MCurrencyKt;
import org.compiere.conversionrate.MConversionRate;
import org.compiere.model.AccountSchemaElement;
import org.compiere.model.AccountingSchema;
import org.compiere.model.I_C_ValidCombination;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Accounting Fact Entry.
 *
 * @author Jorg Janke
 * @version $Id: FactLine.java,v 1.3 2006/07/30 00:53:33 jjanke Exp $
 * <p>Contributor(s): Chris Farley: Fix Bug [ 1657372 ] M_MatchInv records can not be balanced
 * https://sourceforge.net/forum/message.php?msg_id=4151117 Carlos Ruiz - globalqss: Add
 * setAmtAcct method rounded by Currency Armen Rizal, Goodwill Consulting
 * <li>BF [ 1745154 ] Cost in Reversing Material Related Docs Bayu Sistematika -
 * <li>BF [ 2213252 ] Matching Inv-Receipt generated unproperly value for src amt Teo Sarca
 * <li>FR [ 2819081 ] FactLine.getDocLine should be public
 * https://sourceforge.net/tracker/?func=detail&atid=879335&aid=2819081&group_id=176962
 */
public final class FactLine extends X_Fact_Acct {
    /**
     *
     */
    private static final long serialVersionUID = 6141312459030795891L;
    /**
     * Account
     */
    private I_C_ValidCombination m_acct = null;
    /**
     * Accounting Schema
     */
    private AccountingSchema m_acctSchema = null;
    /**
     * Document Header
     */
    private Doc m_doc = null;
    /**
     * Document Line
     */
    private DocLine m_docLine = null;

    /**
     * Constructor
     *
     * @param AD_Table_ID - Table of Document Source
     * @param Record_ID   - Record of document
     * @param Line_ID     - Optional line id
     */
    public FactLine(int AD_Table_ID, int Record_ID, int Line_ID) {
        super(0);
        setClientId(0); // 	do not derive
        setOrgId(0); // 	do not derive
        //
        setAmtAcctCr(Env.ZERO);
        setAmtAcctDr(Env.ZERO);
        setAmtSourceCr(Env.ZERO);
        setAmtSourceDr(Env.ZERO);
        setRowTableId(AD_Table_ID);
        setRecordId(Record_ID);
        setLineId(Line_ID);
    } //  FactLine

    /**
     * Create Reversal (negate DR/CR) of the line
     *
     * @param description new description
     * @return reversal line
     */
    public FactLine reverse(String description) {
        FactLine reversal =
                new FactLine(getRowTableId(), getRecordId(), getLineId());
        reversal.setClientOrg(this); // 	needs to be set explicitly
        reversal.setDocumentInfo(m_doc, m_docLine);
        reversal.setAccount(m_acctSchema, m_acct);
        reversal.setPostingType(getPostingType());
        //
        reversal.setAmtSource(getCurrencyId(), getAmtSourceDr().negate(), getAmtSourceCr().negate());
        reversal.setQty(getQty().negate());
        reversal.convert();
        reversal.setDescription(description);

        reversal.setBusinessPartnerId(getBusinessPartnerId());
        reversal.setProductId(getProductId());
        reversal.setProjectId(getProjectId());
        reversal.setCampaignId(getCampaignId());
        reversal.setBusinessActivityId(getBusinessActivityId());
        reversal.setTransactionOrganizationId(getTransactionOrganizationId());
        reversal.setSalesRegionId(getSalesRegionId());
        reversal.setLocationToId(getLocationToId());
        reversal.setLocationFromId(getLocationFromId());
        reversal.setUser1Id(getUser1Id());
        reversal.setUser2Id(getUser1Id());
        reversal.setUserElement1Id(getUserElement1Id());
        reversal.setUserElement2Id(getUserElement2Id());

        return reversal;
    } //	reverse

    /**
     * Set Account Info
     *
     * @param acctSchema account schema
     * @param acct       account
     */
    public void setAccount(AccountingSchema acctSchema, I_C_ValidCombination acct) {
        m_acctSchema = acctSchema;
        setAccountingSchemaId(acctSchema.getAccountingSchemaId());
        //
        m_acct = acct;
        if (getClientId() == 0) setClientId(m_acct.getClientId());
        setAccountId(m_acct.getAccountId());
        setSubAccountId(m_acct.getSubAccountId());

        //	User Defined References
        AccountSchemaElement ud1 =
                m_acctSchema.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserColumn1);
        if (ud1 != null) {
            String ColumnName1 = ud1.getDisplayColumnName();
            if (ColumnName1 != null) {
                int ID1 = 0;
                if (m_docLine != null) ID1 = m_docLine.getValue(ColumnName1);
                if (ID1 == 0) {
                    if (m_doc == null) throw new IllegalArgumentException("Document not set yet");
                    ID1 = m_doc.getValue(ColumnName1);
                }
                if (ID1 != 0) setUserElement1Id(ID1);
            }
        }
        AccountSchemaElement ud2 =
                m_acctSchema.getAcctSchemaElement(X_C_AcctSchema_Element.ELEMENTTYPE_UserColumn2);
        if (ud2 != null) {
            String ColumnName2 = ud2.getDisplayColumnName();
            if (ColumnName2 != null) {
                int ID2 = 0;
                if (m_docLine != null) ID2 = m_docLine.getValue(ColumnName2);
                if (ID2 == 0) {
                    if (m_doc == null) throw new IllegalArgumentException("Document not set yet");
                    ID2 = m_doc.getValue(ColumnName2);
                }
                if (ID2 != 0) setUserElement2Id(ID2);
            }
        }
    } //  setAccount

    /**
     * Set Source Amounts
     *
     * @param C_Currency_ID currency
     * @param AmtSourceDr   source amount dr
     * @param AmtSourceCr   source amount cr
     * @return true, if any if the amount is not zero
     */
    public boolean setAmtSource(int C_Currency_ID, BigDecimal AmtSourceDr, BigDecimal AmtSourceCr) {
        if (!m_acctSchema.isAllowNegativePosting()) {
            // begin Victor Perez e-evolution 30.08.2005
            // fix Debit & Credit
            if (AmtSourceDr != null) {
                if (AmtSourceDr.compareTo(Env.ZERO) < 0) {
                    AmtSourceCr = AmtSourceDr.abs();
                    AmtSourceDr = Env.ZERO;
                }
            }
            if (AmtSourceCr != null) {
                if (AmtSourceCr.compareTo(Env.ZERO) < 0) {
                    AmtSourceDr = AmtSourceCr.abs();
                    AmtSourceCr = Env.ZERO;
                }
            }
            // end Victor Perez e-evolution 30.08.2005
        }

        setCurrencyId(C_Currency_ID);
        if (AmtSourceDr != null) setAmtSourceDr(AmtSourceDr);
        if (AmtSourceCr != null) setAmtSourceCr(AmtSourceCr);
        //  one needs to be non zero
        if (getAmtSourceDr().compareTo(Env.ZERO) == 0 && getAmtSourceCr().compareTo(Env.ZERO) == 0)
            return false;
        //	Currency Precision
        int precision = MCurrencyKt.getCurrencyStdPrecision(C_Currency_ID);
        if (AmtSourceDr != null && AmtSourceDr.scale() > precision) {
            BigDecimal AmtSourceDr1 = AmtSourceDr.setScale(precision, BigDecimal.ROUND_HALF_UP);
            if (AmtSourceDr1.compareTo(AmtSourceDr) != 0)
                log.warning("Source DR Precision " + AmtSourceDr + " -> " + AmtSourceDr1);
            setAmtSourceDr(AmtSourceDr1);
        }
        if (AmtSourceCr != null && AmtSourceCr.scale() > precision) {
            BigDecimal AmtSourceCr1 = AmtSourceCr.setScale(precision, BigDecimal.ROUND_HALF_UP);
            if (AmtSourceCr1.compareTo(AmtSourceCr) != 0)
                log.warning("Source CR Precision " + AmtSourceCr + " -> " + AmtSourceCr1);
            setAmtSourceCr(AmtSourceCr1);
        }
        return true;
    } //  setAmtSource

    /**
     * Set Accounted Amounts (alternative: call convert)
     *
     * @param AmtAcctDr acct amount dr
     * @param AmtAcctCr acct amount cr
     */
    public void setAmtAcct(BigDecimal AmtAcctDr, BigDecimal AmtAcctCr) {
        if (!m_acctSchema.isAllowNegativePosting()) {
            // begin Victor Perez e-evolution 30.08.2005
            // fix Debit & Credit
            if (AmtAcctDr.compareTo(Env.ZERO) < 0) {
                AmtAcctCr = AmtAcctDr.abs();
                AmtAcctDr = Env.ZERO;
            }
            if (AmtAcctCr.compareTo(Env.ZERO) < 0) {
                AmtAcctDr = AmtAcctCr.abs();
                AmtAcctCr = Env.ZERO;
            }
            // end Victor Perez e-evolution 30.08.2005
        }
        setAmtAcctDr(AmtAcctDr);
        setAmtAcctCr(AmtAcctCr);
    } //  setAmtAcct

    /**
     * Set Accounted Amounts rounded by currency
     *
     * @param C_Currency_ID currency
     * @param AmtAcctDr     acct amount dr
     * @param AmtAcctCr     acct amount cr
     */
    public void setAmtAcct(int C_Currency_ID, BigDecimal AmtAcctDr, BigDecimal AmtAcctCr) {
        if (!m_acctSchema.isAllowNegativePosting()) {
            // fix Debit & Credit
            if (AmtAcctDr != null) {
                if (AmtAcctDr.compareTo(Env.ZERO) < 0) {
                    AmtAcctCr = AmtAcctDr.abs();
                    AmtAcctDr = Env.ZERO;
                }
            }
            if (AmtAcctCr != null) {
                if (AmtAcctCr.compareTo(Env.ZERO) < 0) {
                    AmtAcctDr = AmtAcctCr.abs();
                    AmtAcctCr = Env.ZERO;
                }
            }
        }
        setAmtAcctDr(AmtAcctDr);
        setAmtAcctCr(AmtAcctCr);
        //	Currency Precision
        int precision = MCurrencyKt.getCurrencyStdPrecision(C_Currency_ID);
        if (AmtAcctDr != null && AmtAcctDr.scale() > precision) {
            BigDecimal AmtAcctDr1 = AmtAcctDr.setScale(precision, BigDecimal.ROUND_HALF_UP);
            if (AmtAcctDr1.compareTo(AmtAcctDr) != 0)
                log.warning("Accounted DR Precision " + AmtAcctDr + " -> " + AmtAcctDr1);
            setAmtAcctDr(AmtAcctDr1);
        }
        if (AmtAcctCr != null && AmtAcctCr.scale() > precision) {
            BigDecimal AmtAcctCr1 = AmtAcctCr.setScale(precision, BigDecimal.ROUND_HALF_UP);
            if (AmtAcctCr1.compareTo(AmtAcctCr) != 0)
                log.warning("Accounted CR Precision " + AmtAcctCr + " -> " + AmtAcctCr1);
            setAmtAcctCr(AmtAcctCr1);
        }
    } //  setAmtAcct

    /**
     * Set Document Info
     *
     * @param doc     document
     * @param docLine doc line
     */
    public void setDocumentInfo(Doc doc, DocLine docLine) {
        m_doc = doc;
        m_docLine = docLine;
        //	reset
        setOrgId(0);
        setSalesRegionId(0);
        //	Client
        if (getClientId() == 0) setClientId(m_doc.getClientId());
        //	Date Trx
        setDateTrx(m_doc.getDateDoc());
        if (m_docLine != null && m_docLine.getDateDoc() != null) setDateTrx(m_docLine.getDateDoc());
        //	Date Acct
        setDateAcct(m_doc.getDateAcct());
        if (m_docLine != null && m_docLine.getDateAcct() != null) setDateAcct(m_docLine.getDateAcct());
        //	Period, Tax
        if (m_docLine != null && m_docLine.getPeriodId() != 0)
            setPeriodId(m_docLine.getPeriodId());
        else setPeriodId(m_doc.getPeriodId());
        if (m_docLine != null) setTaxId(m_docLine.getTaxId());
        //	Description
        StringBuilder description = new StringBuilder().append(m_doc.getDocumentNo());
        if (m_docLine != null) {
            description.append(" #").append(m_docLine.getLine());
            if (m_docLine.getDescription() != null)
                description.append(" (").append(m_docLine.getDescription()).append(")");
            else if (m_doc.getDescription() != null && m_doc.getDescription().length() > 0)
                description.append(" (").append(m_doc.getDescription()).append(")");
        } else if (m_doc.getDescription() != null && m_doc.getDescription().length() > 0)
            description.append(" (").append(m_doc.getDescription()).append(")");
        setDescription(description.toString());
        //	Journal Info
        setGLBudgetId(m_doc.getGLBudgetId());
        setGLCategoryId(m_doc.getGLCategoryId());

        //	Product
        if (m_docLine != null) setProductId(m_docLine.getProductId());
        if (getProductId() == 0) setProductId(m_doc.getProductId());
        //	UOM
        if (m_docLine != null) setUOMId(m_docLine.getUOMId());
        //	Qty
        if (getValue("Qty") == null) // not previously set
        {
            setQty(m_doc.getQty()); // 	neg = outgoing
            if (m_docLine != null) setQty(m_docLine.getQty());
        }

        //	Loc From (maybe set earlier)
        if (getLocationFromId() == 0 && m_docLine != null) setLocationFromId(m_docLine.getLocationFromId());
        if (getLocationFromId() == 0) setLocationFromId(m_doc.getLocationFromId());
        //	Loc To (maybe set earlier)
        if (getLocationToId() == 0 && m_docLine != null) setLocationToId(m_docLine.getLocationToId());
        if (getLocationToId() == 0) setLocationToId(m_doc.getLocationToId());
        //	BPartner
        if (m_docLine != null) setBusinessPartnerId(m_docLine.getBusinessPartnerId());
        if (getBusinessPartnerId() == 0) setBusinessPartnerId(m_doc.getBusinessPartnerId());
        //	Sales Region from BPLocation/Sales Rep
        //	Trx Org
        if (m_docLine != null) setTransactionOrganizationId(m_docLine.getTransactionOrganizationId());
        if (getTransactionOrganizationId() == 0) setTransactionOrganizationId(m_doc.getTransactionOrganizationId());
        //	Project
        if (m_docLine != null) setProjectId(m_docLine.getProjectId());
        if (getProjectId() == 0) setProjectId(m_doc.getProjectId());
        if (m_docLine != null) setProjectPhaseId(m_docLine.getProjectPhaseId());
        if (getProjectPhaseId() == 0) setProjectPhaseId(m_doc.getProjectPhaseId());
        if (m_docLine != null) setProjectTaskId(m_docLine.getProjectTaskId());
        if (getProjectTaskId() == 0) setProjectTaskId(m_doc.getProjectTaskId());
        //	Campaign
        if (m_docLine != null) setCampaignId(m_docLine.getCampaignId());
        if (getCampaignId() == 0) setCampaignId(m_doc.getCampaignId());
        //	Activity
        if (m_docLine != null) setBusinessActivityId(m_docLine.getBusinessActivityId());
        if (getBusinessActivityId() == 0) setBusinessActivityId(m_doc.getBusinessActivityId());
        //	User List 1
        if (m_docLine != null) setUser1Id(m_docLine.getUser1Id());
        if (getUser1Id() == 0) setUser1Id(m_doc.getUser1Id());
        //	User List 2
        if (m_docLine != null) setUser2Id(m_docLine.getUser2Id());
        if (getUser2Id() == 0) setUser2Id(m_doc.getUser2Id());
        //	References in setAccount
    } //  setDocumentInfo

    /**
     * Get Document Line
     *
     * @return doc line
     */
    public DocLine getDocLine() {
        return m_docLine;
    } //	getDocLine

    /**
     * Set Description
     *
     * @param description description
     */
    public void addDescription(String description) {
        String original = getDescription();
        if (original == null || original.trim().length() == 0) super.setDescription(description);
        else {
            StringBuilder msgd = new StringBuilder(original).append(" - ").append(description);
            super.setDescription(msgd.toString());
        }
    } //	addDescription

    /**
     * Set Warehouse Locator. - will overwrite Organization -
     *
     * @param M_Locator_ID locator
     */
    public void setLocatorId(int M_Locator_ID) {
        super.setLocatorId(M_Locator_ID);
        setOrgId(0); // 	reset
    } //  setLocatorId

    /**
     * ************************************************************************ Set Location
     *
     * @param C_Location_ID location
     * @param isFrom        from
     */
    public void setLocation(int C_Location_ID, boolean isFrom) {
        if (isFrom) setLocationFromId(C_Location_ID);
        else setLocationToId(C_Location_ID);
    } //  setLocator

    /**
     * Set Location from Locator
     *
     * @param M_Locator_ID locator
     * @param isFrom       from
     */
    public void setLocationFromLocator(int M_Locator_ID, boolean isFrom) {
        if (M_Locator_ID == 0) return;
        int C_Location_ID = 0;
        String sql =
                "SELECT w.C_Location_ID FROM M_Warehouse w, M_Locator l "
                        + "WHERE w.M_Warehouse_ID=l.M_Warehouse_ID AND l.M_Locator_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, M_Locator_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) C_Location_ID = rs.getInt(1);
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
            return;
        } finally {

            rs = null;
            pstmt = null;
        }
        if (C_Location_ID != 0) setLocation(C_Location_ID, isFrom);
    } //  setLocationFromLocator

    /**
     * Set Location from Busoness Partner Location
     *
     * @param C_BPartner_Location_ID bp location
     * @param isFrom                 from
     */
    public void setLocationFromBPartner(int C_BPartner_Location_ID, boolean isFrom) {
        if (C_BPartner_Location_ID == 0) return;
        int C_Location_ID = 0;
        String sql = "SELECT C_Location_ID FROM C_BPartner_Location WHERE C_BPartner_Location_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, C_BPartner_Location_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) C_Location_ID = rs.getInt(1);
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
            return;
        } finally {

            rs = null;
            pstmt = null;
        }
        if (C_Location_ID != 0) setLocation(C_Location_ID, isFrom);
    } //  setLocationFromBPartner

    /**
     * Set Location from Organization
     *
     * @param AD_Org_ID org
     * @param isFrom    from
     */
    public void setLocationFromOrg(int AD_Org_ID, boolean isFrom) {
        if (AD_Org_ID == 0) return;
        int C_Location_ID = 0;
        String sql = "SELECT C_Location_ID FROM AD_OrgInfo WHERE AD_Org_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, AD_Org_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) C_Location_ID = rs.getInt(1);
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
            return;
        } finally {

            rs = null;
            pstmt = null;
        }
        if (C_Location_ID != 0) setLocation(C_Location_ID, isFrom);
    } //  setLocationFromOrg

    /**
     * ************************************************************************ Returns Source Balance
     * of line
     *
     * @return source balance
     */
    public BigDecimal getSourceBalance() {
        if (getAmtSourceDr() == null) setAmtSourceDr(Env.ZERO);
        if (getAmtSourceCr() == null) setAmtSourceCr(Env.ZERO);
        //
        return getAmtSourceDr().subtract(getAmtSourceCr());
    } //  getSourceBalance

    /**
     * Is Debit Source Balance
     *
     * @return true if DR source balance
     */
    public boolean isDrSourceBalance() {
        return getSourceBalance().signum() != -1;
    } //  isDrSourceBalance

    /**
     * Get Accounted Balance
     *
     * @return accounting balance
     */
    public BigDecimal getAcctBalance() {
        if (getAmtAcctDr() == null) setAmtAcctDr(Env.ZERO);
        if (getAmtAcctCr() == null) setAmtAcctCr(Env.ZERO);
        return getAmtAcctDr().subtract(getAmtAcctCr());
    } //  getAcctBalance

    /**
     * Is Account on Balance Sheet
     *
     * @return true if account is a balance sheet account
     */
    public boolean isBalanceSheet() {
        return m_acct.isBalanceSheet();
    } //	isBalanceSheet

    /**
     * Currect Accounting Amount.
     *
     * <pre>
     *  Example:    1       -1      1       -1
     *  Old         100/0   100/0   0/100   0/100
     *  New         99/0    101/0   0/99    0/101
     *  </pre>
     *
     * @param deltaAmount delta amount
     */
    public void currencyCorrect(BigDecimal deltaAmount) {
        boolean negative = deltaAmount.compareTo(Env.ZERO) < 0;
        boolean adjustDr = getAmtAcctDr().abs().compareTo(getAmtAcctCr().abs()) > 0;

        if (log.isLoggable(Level.FINE))
            log.fine(
                    deltaAmount.toString()
                            + "; Old-AcctDr="
                            + getAmtAcctDr()
                            + ",AcctCr="
                            + getAmtAcctCr()
                            + "; Negative="
                            + negative
                            + "; AdjustDr="
                            + adjustDr);

        if (adjustDr)
            if (negative) setAmtAcctDr(getAmtAcctDr().subtract(deltaAmount));
            else setAmtAcctDr(getAmtAcctDr().subtract(deltaAmount));
        else if (negative) setAmtAcctCr(getAmtAcctCr().add(deltaAmount));
        else setAmtAcctCr(getAmtAcctCr().add(deltaAmount));

        if (log.isLoggable(Level.FINE))
            log.fine("New-AcctDr=" + getAmtAcctDr() + ",AcctCr=" + getAmtAcctCr());
    } //	currencyCorrect

    /**
     * Convert to Accounted Currency
     *
     * @return true if converted
     */
    public boolean convert() {
        //  Document has no currency
        if (getCurrencyId() == Doc.NO_CURRENCY) setCurrencyId(m_acctSchema.getCurrencyId());

        if (m_acctSchema.getCurrencyId() == getCurrencyId()) {
            setAmtAcctDr(getAmtSourceDr());
            setAmtAcctCr(getAmtSourceCr());
            return true;
        }
        //	Get Conversion Type from Line or Header
        int C_ConversionType_ID = 0;
        int AD_Org_ID = 0;
        if (m_docLine != null) // 	get from line
        {
            C_ConversionType_ID = m_docLine.getConversionTypeId();
            AD_Org_ID = m_docLine.getOrgId();
        }
        if (C_ConversionType_ID == 0) // 	get from header
        {
            if (m_doc == null) {
                log.severe("No Document VO");
                return false;
            }
            C_ConversionType_ID = m_doc.getConversionTypeId();
            if (AD_Org_ID == 0) AD_Org_ID = m_doc.getOrgId();
        }

        Timestamp convDate = getDateAcct();

        if (m_docLine != null
                && (m_doc instanceof Doc_BankStatement || m_doc instanceof Doc_AllocationHdr))
            convDate = m_docLine.getDateConv();

        setAmtAcctDr(
                MConversionRate.convert(
                        getAmtSourceDr(),
                        getCurrencyId(),
                        m_acctSchema.getCurrencyId(),
                        convDate,
                        C_ConversionType_ID,
                        m_doc.getClientId(),
                        AD_Org_ID));
        if (getAmtAcctDr() == null) return false;
        setAmtAcctCr(
                MConversionRate.convert(
                        getAmtSourceCr(),
                        getCurrencyId(),
                        m_acctSchema.getCurrencyId(),
                        convDate,
                        C_ConversionType_ID,
                        m_doc.getClientId(),
                        AD_Org_ID));
        return true;
    } //	convert

    /**
     * Get Account
     *
     * @return account
     */
    public I_C_ValidCombination getAccount() {
        return m_acct;
    } //	getAccount

    /**
     * To String
     *
     * @return String
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("FactLine=[");
        sb.append(getRowTableId())
                .append(":")
                .append(getRecordId())
                .append(",")
                .append(m_acct)
                .append(",Cur=")
                .append(getCurrencyId())
                .append(", DR=")
                .append(getAmtSourceDr())
                .append("|")
                .append(getAmtAcctDr())
                .append(", CR=")
                .append(getAmtSourceCr())
                .append("|")
                .append(getAmtAcctCr())
                .append("]");
        return sb.toString();
    } //	toString

    /**
     * Get orgId (balancing segment). (if not set directly - from document line, document,
     * account, locator)
     *
     * <p>Note that Locator needs to be set before - otherwise segment balancing might produce the
     * wrong results
     *
     * @return orgId
     */
    public int getOrgId() {
        if (super.getOrgId() != 0) //  set earlier
            return super.getOrgId();
        //	Prio 1 - get from locator - if exist
        if (getLocatorId() != 0) {
            String sql = "SELECT AD_Org_ID FROM M_Locator WHERE M_Locator_ID=? AND AD_Client_ID=?";
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = prepareStatement(sql);
                pstmt.setInt(1, getLocatorId());
                pstmt.setInt(2, getClientId());
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    setOrgId(rs.getInt(1));
                    if (log.isLoggable(Level.FINER))
                        log.finer(
                                "AD_Org_ID="
                                        + super.getOrgId()
                                        + " (1 from M_Locator_ID="
                                        + getLocatorId()
                                        + ")");
                } else log.log(Level.SEVERE, "AD_Org_ID - Did not find M_Locator_ID=" + getLocatorId());
            } catch (SQLException e) {
                log.log(Level.SEVERE, sql, e);
            } finally {

                rs = null;
                pstmt = null;
            }
        } //  M_Locator_ID != 0

        //	Prio 2 - get from doc line - if exists (document context overwrites)
        if (m_docLine != null && super.getOrgId() == 0) {
            setOrgId(m_docLine.getOrgId());
            if (log.isLoggable(Level.FINER))
                log.finer("AD_Org_ID=" + super.getOrgId() + " (2 from DocumentLine)");
        }
        //	Prio 3 - get from doc - if not GL
        if (m_doc != null && super.getOrgId() == 0) {
            if (Doc.DOCTYPE_GLJournal.equals(m_doc.getDocumentType())) {
                setOrgId(m_acct.getOrgId()); // 	inter-company GL
                if (log.isLoggable(Level.FINER))
                    log.finer("AD_Org_ID=" + super.getOrgId() + " (3 from Acct)");
            } else {
                setOrgId(m_doc.getOrgId());
                if (log.isLoggable(Level.FINER))
                    log.finer("AD_Org_ID=" + super.getOrgId() + " (3 from Document)");
            }
        }
        //	Prio 4 - get from account - if not GL
        if (m_doc != null && super.getOrgId() == 0) {
            if (Doc.DOCTYPE_GLJournal.equals(m_doc.getDocumentType())) {
                setOrgId(m_doc.getOrgId());
                if (log.isLoggable(Level.FINER))
                    log.finer("AD_Org_ID=" + super.getOrgId() + " (4 from Document)");
            } else {
                setOrgId(m_acct.getOrgId());
                if (log.isLoggable(Level.FINER))
                    log.finer("AD_Org_ID=" + super.getOrgId() + " (4 from Acct)");
            }
        }
        return super.getOrgId();
    } //  setOrgId

    /**
     * Get/derive Sales Region
     *
     * @return Sales Region
     */
    public int getSalesRegionId() {
        if (super.getSalesRegionId() != 0) return super.getSalesRegionId();
        //
        if (m_docLine != null) setSalesRegionId(m_docLine.getSalesRegionId());
        if (m_doc != null) {
            if (super.getSalesRegionId() == 0) setSalesRegionId(m_doc.getSalesRegionId());
            if (super.getSalesRegionId() == 0 && m_doc.getBusinessPartnerSalesRegionId() > 0)
                setSalesRegionId(m_doc.getBusinessPartnerSalesRegionId());
            //	derive SalesRegion if AcctSegment
            if (super.getSalesRegionId() == 0
                    && m_doc.getBusinessPartnerLocationId() != 0
                    && m_doc.getBusinessPartnerSalesRegionId() == -1) // 	never tried
            //	&& m_acctSchema.isAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_SalesRegion))
            {
                String sql =
                        "SELECT COALESCE(C_SalesRegion_ID,0) FROM C_BPartner_Location WHERE C_BPartner_Location_ID=?";
                setSalesRegionId(getSQLValue(sql, m_doc.getBusinessPartnerLocationId()));
                if (super.getSalesRegionId() != 0) // 	save in VO
                {
                    m_doc.setBP_C_SalesRegionId(super.getSalesRegionId());
                    if (log.isLoggable(Level.FINE))
                        log.fine("C_SalesRegion_ID=" + super.getSalesRegionId() + " (from BPL)");
                } else //	From Sales Rep of Document -> Sales Region
                {
                    sql = "SELECT COALESCE(MAX(C_SalesRegion_ID),0) FROM C_SalesRegion WHERE SalesRep_ID=?";
                    setSalesRegionId(getSQLValue(sql, m_doc.getSalesRepresentativeId()));
                    if (super.getSalesRegionId() != 0) // 	save in VO
                    {
                        m_doc.setBP_C_SalesRegionId(super.getSalesRegionId());
                        if (log.isLoggable(Level.FINE))
                            log.fine("C_SalesRegion_ID=" + super.getSalesRegionId() + " (from SR)");
                    } else m_doc.setBP_C_SalesRegionId(-2); // 	don't try again
                }
            }
            if (m_acct != null && super.getSalesRegionId() == 0)
                setSalesRegionId(m_acct.getSalesRegionId());
        }
        //
        //	log.fine("C_SalesRegion_ID=" + super.getSalesRegionId()
        //		+ ", C_BPartner_Location_ID=" + m_docVO.C_BPartner_Location_ID
        //		+ ", BP_C_SalesRegion_ID=" + m_docVO.BP_C_SalesRegion_ID
        //		+ ", SR=" + m_acctSchema.isAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_SalesRegion));
        return super.getSalesRegionId();
    } //	getSalesRegionId

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (newRecord) {
            if (log.isLoggable(Level.FINE)) log.fine(toString());
            //
            getOrgId();
            getSalesRegionId();
            //  Set Default Account Info
            if (getProductId() == 0) setProductId(m_acct.getProductId());
            if (getLocationFromId() == 0) setLocationFromId(m_acct.getLocationFromId());
            if (getLocationToId() == 0) setLocationToId(m_acct.getLocationToId());
            if (getBusinessPartnerId() == 0) setBusinessPartnerId(m_acct.getBusinessPartnerId());
            if (getTransactionOrganizationId() == 0)
                setTransactionOrganizationId(m_acct.getTransactionOrganizationId());
            if (getProjectId() == 0) setProjectId(m_acct.getProjectId());
            if (getCampaignId() == 0) setCampaignId(m_acct.getCampaignId());
            if (getBusinessActivityId() == 0) setBusinessActivityId(m_acct.getBusinessActivityId());
            if (getUser1Id() == 0) setUser1Id(m_acct.getUser1Id());
            if (getUser2Id() == 0) setUser2Id(m_acct.getUser2Id());

            //  Revenue Recognition for AR Invoices
            if (m_doc.getDocumentType().equals(Doc.DOCTYPE_ARInvoice)
                    && m_docLine != null
                    && m_docLine.getRevenueRecognitionId() != 0) {
                int AD_User_ID = 0;
                setAccountId(
                        createRevenueRecognition(
                                m_docLine.getRevenueRecognitionId(),
                                m_docLine.getId(),
                                getClientId(),
                                getOrgId(),
                                getAccountId(),
                                getSubAccountId(),
                                getProductId(),
                                getBusinessPartnerId(),
                                getTransactionOrganizationId(),
                                getLocationFromId(),
                                getLocationToId(),
                                getSalesRegionId(),
                                getProjectId(),
                                getCampaignId(),
                                getBusinessActivityId(),
                                getUser1Id(),
                                getUser2Id(),
                                getUserElement1Id(),
                                getUserElement2Id()));
            }
        }
        return true;
    } //	beforeSave

    /**
     * ************************************************************************ Revenue Recognition.
     * Called from FactLine.save
     *
     * <p>Create Revenue recognition plan and return Unearned Revenue account to be used instead of
     * Revenue Account. If not found, it returns the revenue account.
     *
     * @param C_RevenueRecognition_ID revenue recognition
     * @param C_InvoiceLine_ID        invoice line
     * @param AD_Client_ID            client
     * @param AD_Org_ID               org
     * @param Account_ID              of Revenue Account
     * @param C_SubAcct_ID            sub account
     * @param M_Product_ID            product
     * @param C_BPartner_ID           bpartner
     * @param AD_OrgTrx_ID            trx org
     * @param C_LocFrom_ID            loc from
     * @param C_LocTo_ID              loc to
     * @param C_SRegion_ID            sales region
     * @param C_Project_ID            project
     * @param C_Campaign_ID           campaign
     * @param C_Activity_ID           activity
     * @param User1_ID                user1
     * @param User2_ID                user2
     * @param UserElement1_ID         user element 1
     * @param UserElement2_ID         user element 2
     * @return Account_ID for Unearned Revenue or Revenue Account if not found
     */
    private int createRevenueRecognition(
            int C_RevenueRecognition_ID,
            int C_InvoiceLine_ID,
            int AD_Client_ID,
            int AD_Org_ID,
            int Account_ID,
            int C_SubAcct_ID,
            int M_Product_ID,
            int C_BPartner_ID,
            int AD_OrgTrx_ID,
            int C_LocFrom_ID,
            int C_LocTo_ID,
            int C_SRegion_ID,
            int C_Project_ID,
            int C_Campaign_ID,
            int C_Activity_ID,
            int User1_ID,
            int User2_ID,
            int UserElement1_ID,
            int UserElement2_ID) {
        if (log.isLoggable(Level.FINE)) log.fine("From Accout_ID=" + Account_ID);
        //  get VC for P_Revenue (from Product)
        I_C_ValidCombination revenue =
                MAccount.get(
                        AD_Client_ID,
                        AD_Org_ID,
                        getAccountingSchemaId(),
                        Account_ID,
                        C_SubAcct_ID,
                        M_Product_ID,
                        C_BPartner_ID,
                        AD_OrgTrx_ID,
                        C_LocFrom_ID,
                        C_LocTo_ID,
                        C_SRegion_ID,
                        C_Project_ID,
                        C_Campaign_ID,
                        C_Activity_ID,
                        User1_ID,
                        User2_ID,
                        UserElement1_ID,
                        UserElement2_ID
                );
        if (revenue != null && revenue.getId() == 0) revenue.saveEx();
        if (revenue == null || revenue.getId() == 0) {
            log.severe("Revenue_Acct not found");
            return Account_ID;
        }
        int P_Revenue_Acct = revenue.getId();

        //  get Unearned Revenue Acct from BPartner Group
        int UnearnedRevenue_Acct = 0;
        int new_Account_ID = 0;
        String sql =
                "SELECT ga.UnearnedRevenue_Acct, vc.Account_ID "
                        + "FROM C_BP_Group_Acct ga, C_BPartner p, C_ValidCombination vc "
                        + "WHERE ga.C_BP_Group_ID=p.C_BP_Group_ID"
                        + " AND ga.UnearnedRevenue_Acct=vc.C_ValidCombination_ID"
                        + " AND ga.C_AcctSchema_ID=? AND p.C_BPartner_ID=?";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getAccountingSchemaId());
            pstmt.setInt(2, C_BPartner_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                UnearnedRevenue_Acct = rs.getInt(1);
                new_Account_ID = rs.getInt(2);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
        }
        if (new_Account_ID == 0) {
            log.severe("UnearnedRevenue_Acct not found");
            return Account_ID;
        }

        MRevenueRecognitionPlan plan = new MRevenueRecognitionPlan(0);
        plan.setRevenueRecognitionId(C_RevenueRecognition_ID);
        plan.setAccountingSchemaId(getAccountingSchemaId());
        plan.setInvoiceLineId(C_InvoiceLine_ID);
        plan.setUnEarnedRevenueAccount(UnearnedRevenue_Acct);
        plan.setRevenueAccount(P_Revenue_Acct);
        plan.setCurrencyId(getCurrencyId());
        plan.setTotalAmt(getAcctBalance());
        if (!plan.save()) {
            log.severe("Plan NOT created");
            return Account_ID;
        }
        if (log.isLoggable(Level.FINE))
            log.fine(
                    "From Acctount_ID="
                            + Account_ID
                            + " to "
                            + new_Account_ID
                            + " - Plan from UnearnedRevenue_Acct="
                            + UnearnedRevenue_Acct
                            + " to Revenue_Acct="
                            + P_Revenue_Acct);
        return new_Account_ID;
    } //  createRevenueRecognition

    /**
     * ************************************************************************ Update Line with
     * reversed Original Amount in Accounting Currency. Also copies original dimensions like Project,
     * etc. Called from Doc_MatchInv
     *
     * @param AD_Table_ID table
     * @param Record_ID   record
     * @param Line_ID     line
     * @param multiplier  targetQty/documentQty
     * @return true if success
     */
    public boolean updateReverseLine(
            int AD_Table_ID, int Record_ID, int Line_ID, BigDecimal multiplier) {
        boolean success = false;

        MFactAcct fact = BaseFactLineKt.updateReverseLineGetData(AD_Table_ID, Record_ID, Line_ID,
                getAccountingSchemaId(), m_acct.getAccountId(), getLocatorId()
        );
        //  Accounted Amounts - reverse
        BigDecimal dr = fact.getAmtAcctDr();
        BigDecimal cr = fact.getAmtAcctCr();
        setAmtAcct(fact.getCurrencyId(), cr.multiply(multiplier), dr.multiply(multiplier));
        //
        BigDecimal drSourceAmt = fact.getAmtSourceDr();
        BigDecimal crSourceAmt = fact.getAmtSourceCr();
        setAmtSource(
                fact.getCurrencyId(),
                crSourceAmt.multiply(multiplier),
                drSourceAmt.multiply(multiplier));
        //
        success = true;
        if (log.isLoggable(Level.FINE))
            log.fine(
                    "(Table=" +
                            AD_Table_ID +
                            ",Record_ID=" +
                            Record_ID +
                            ",Line=" +
                            Record_ID +
                            ", Account=" +
                            m_acct +
                            ",dr=" +
                            dr +
                            ",cr=" +
                            cr +
                            ") - DR=" +
                            getAmtSourceDr() +
                            "|" +
                            getAmtAcctDr() +
                            ", CR=" +
                            getAmtSourceCr() +
                            "|" +
                            getAmtAcctCr());
        //	Dimensions
        setTransactionOrganizationId(fact.getTransactionOrganizationId());
        setProjectId(fact.getProjectId());
        setProjectPhaseId(fact.getProjectPhaseId());
        setProjectTaskId(fact.getProjectTaskId());
        setBusinessActivityId(fact.getBusinessActivityId());
        setCampaignId(fact.getCampaignId());
        setSalesRegionId(fact.getSalesRegionId());
        setLocationFromId(fact.getLocationFromId());
        setLocationToId(fact.getLocationToId());
        setProductId(fact.getProductId());
        setLocatorId(fact.getLocatorId());
        setUser1Id(fact.getUser1Id());
        setUser2Id(fact.getUser2Id());
        setUOMId(fact.getUOMId());
        setTaxId(fact.getTaxId());
        //	Org for cross charge
        setOrgId(fact.getOrgId());
        if (fact.getQty() != null) setQty(fact.getQty().negate());

        return success;
    } //  updateReverseLine
} //	FactLine
