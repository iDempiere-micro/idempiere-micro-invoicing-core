package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_GL_Distribution;
import org.compiere.model.I_GL_DistributionLine;
import org.compiere.orm.Query;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogMgt;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * GL Distribution Model
 *
 * @author Jorg Janke
 * @author red1 FR: [ 2214883 ] Remove SQL code and Replace for Query
 * @version $Id: MDistribution.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MDistribution extends X_GL_Distribution {
    /**
     *
     */
    private static final long serialVersionUID = 3782058638272715005L;
    /**
     * Static Logger
     */
    @SuppressWarnings("unused")
    private static CLogger s_log = CLogger.getCLogger(MDistribution.class);
    /**
     * Distributions by Account
     */
    private static CCache<Integer, I_GL_Distribution[]> s_accounts =
            new CCache<>(I_GL_Distribution.Table_Name, 100);
    /**
     * The Lines
     */
    private I_GL_DistributionLine[] m_lines = null;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param GL_Distribution_ID id
     */
    public MDistribution(int GL_Distribution_ID) {
        super(GL_Distribution_ID);
        if (GL_Distribution_ID == 0) {
            //
            setAnyAcct(true); // Y
            setAnyActivity(true); // Y
            setAnyBPartner(true); // Y
            setAnyCampaign(true); // Y
            setAnyLocFrom(true); // Y
            setAnyLocTo(true); // Y
            setAnyOrg(true); // Y
            setAnyOrgTrx(true); // Y
            setAnyProduct(true); // Y
            setAnyProject(true); // Y
            setAnySalesRegion(true); // Y
            setAnyUser1(true); // Y
            setAnyUser2(true); // Y
            //
            setIsValid(false); // N
            setPercentTotal(Env.ZERO);
        }
    } //	MDistribution

    /**
     * Load Constructor
     */
    public MDistribution(Row row) {
        super(row);
    } //	MDistribution

    /**
     * Get Distribution for combination
     *
     * @param acct         account (ValidCombination)
     * @param PostingType  only posting type
     * @param C_DocType_ID only document type
     * @return array of distributions
     */
    public static I_GL_Distribution[] get(I_C_ValidCombination acct, String PostingType, int C_DocType_ID) {
        return get(
                acct.getAccountingSchemaId(),
                PostingType,
                C_DocType_ID,
                acct.getOrgId(),
                acct.getAccountId(),
                acct.getProductId(),
                acct.getBusinessPartnerId(),
                acct.getProjectId(),
                acct.getCampaignId(),
                acct.getBusinessActivityId(),
                acct.getTransactionOrganizationId(),
                acct.getSalesRegionId(),
                acct.getLocationToId(),
                acct.getLocationFromId(),
                acct.getUser1Id(),
                acct.getUser2Id());
    } //	get

    /**
     * Get Distributions for combination
     *
     * @param C_AcctSchema_ID  schema
     * @param PostingType      posting type
     * @param C_DocType_ID     document type
     * @param AD_Org_ID        org
     * @param Account_ID       account
     * @param M_Product_ID     product
     * @param C_BPartner_ID    partner
     * @param C_Project_ID     project
     * @param C_Campaign_ID    campaign
     * @param C_Activity_ID    activity
     * @param AD_OrgTrx_ID     trx org
     * @param C_SalesRegion_ID
     * @param C_LocTo_ID       location to
     * @param C_LocFrom_ID     location from
     * @param User1_ID         user 1
     * @param User2_ID         user 2
     * @return array of distributions or null
     */
    public static I_GL_Distribution[] get(

            int C_AcctSchema_ID,
            String PostingType,
            int C_DocType_ID,
            int AD_Org_ID,
            int Account_ID,
            int M_Product_ID,
            int C_BPartner_ID,
            int C_Project_ID,
            int C_Campaign_ID,
            int C_Activity_ID,
            int AD_OrgTrx_ID,
            int C_SalesRegion_ID,
            int C_LocTo_ID,
            int C_LocFrom_ID,
            int User1_ID,
            int User2_ID) {
        I_GL_Distribution[] acctList = getAll();
        if (acctList == null || acctList.length == 0) return null;
        //
        ArrayList<I_GL_Distribution> list = new ArrayList<>();
        for (I_GL_Distribution distribution : acctList) {
            if (!distribution.isActive() || !distribution.isValid()) continue;
            //	Mandatory Acct Schema
            if (distribution.getAccountingSchemaId() != C_AcctSchema_ID) continue;
            //	Only Posting Type / DocType
            if (distribution.getPostingType() != null
                    && !distribution.getPostingType().equals(PostingType)) continue;
            if (distribution.getDocumentTypeId() != 0 && distribution.getDocumentTypeId() != C_DocType_ID)
                continue;

            //	Optional Elements - "non-Any"
            if (!distribution.isAnyOrg() && distribution.getOrgId() != AD_Org_ID) continue;
            if (!distribution.isAnyAcct() && distribution.getAccountId() != Account_ID) continue;
            if (!distribution.isAnyProduct() && distribution.getProductId() != M_Product_ID) continue;
            if (!distribution.isAnyBPartner() && distribution.getBusinessPartnerId() != C_BPartner_ID)
                continue;
            if (!distribution.isAnyProject() && distribution.getProjectId() != C_Project_ID) continue;
            if (!distribution.isAnyCampaign() && distribution.getCampaignId() != C_Campaign_ID)
                continue;
            if (!distribution.isAnyActivity() && distribution.getBusinessActivityId() != C_Activity_ID)
                continue;
            if (!distribution.isAnyOrgTrx() && distribution.getTransactionOrganizationId() != AD_OrgTrx_ID) continue;
            if (!distribution.isAnySalesRegion()
                    && distribution.getSalesRegionId() != C_SalesRegion_ID) continue;
            if (!distribution.isAnyLocTo() && distribution.getLocationToId() != C_LocTo_ID) continue;
            if (!distribution.isAnyLocFrom() && distribution.getLocationFromId() != C_LocFrom_ID) continue;
            if (!distribution.isAnyUser1() && distribution.getUser1Id() != User1_ID) continue;
            if (!distribution.isAnyUser2() && distribution.getUser2Id() != User2_ID) continue;
            //
            list.add(distribution);
        } //	 for all distributions with acct
        //
        I_GL_Distribution[] retValue = new I_GL_Distribution[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	get

    /**
     * Get Distributions for Account
     *
     * @param Account_ID id
     * @return array of distributions
     */
    public static I_GL_Distribution[] get(int Account_ID) {
        Integer key = Account_ID;
        I_GL_Distribution[] retValue = s_accounts.get(key);
        if (retValue != null) return retValue;
        String whereClause = "";
        Object[] parameters = new Object[]{};
        if (Account_ID >= 0) {
            whereClause = "Account_ID=?";
            parameters = new Object[]{Account_ID};
        }
        List<I_GL_Distribution> list =
                new Query<I_GL_Distribution>(I_GL_Distribution.Table_Name, whereClause)
                        .setClientId()
                        .setParameters(parameters)
                        .list();
        //
        retValue = new I_GL_Distribution[list.size()];
        list.toArray(retValue);
        s_accounts.put(key, retValue);
        return retValue;
    } //	get

    /**
     * Get All Distributions
     *
     * @return array of distributions
     */
    public static I_GL_Distribution[] getAll() {
        return get(-1);
    } //	get

    /**
     * Get Lines and calculate total
     *
     * @param reload reload data
     * @return array of lines
     */
    public I_GL_DistributionLine[] getLines(boolean reload) {
        if (m_lines != null && !reload) {
            return m_lines;
        }
        BigDecimal PercentTotal = Env.ZERO;
        // red1 Query
        final String whereClause = I_GL_DistributionLine.COLUMNNAME_GL_Distribution_ID + "=?";
        List<I_GL_DistributionLine> list =
                new Query<I_GL_DistributionLine>(I_GL_DistributionLine.Table_Name, whereClause)
                        .setParameters(getGLDistributionId())
                        .setOrderBy("Line")
                        .list();
        // red1 Query  -end-
        boolean hasNullRemainder = false;
        for (I_GL_DistributionLine dl : list) {
            if (dl.isActive()) {
                PercentTotal = PercentTotal.add(dl.getPercent());
                hasNullRemainder = Env.ZERO.compareTo(dl.getPercent()) == 0;
            }
            dl.setParent(this);
        }
        //	Update Ratio when saved and difference
        if (hasNullRemainder) PercentTotal = Env.ONEHUNDRED;
        if (getId() != 0 && PercentTotal.compareTo(getPercentTotal()) != 0) {
            setPercentTotal(PercentTotal);
            saveEx();
        }
        //	return
        m_lines = new I_GL_DistributionLine[list.size()];
        list.toArray(m_lines);
        return m_lines;
    } //	getLines

    /**
     * Validate Distribution
     *
     * @return error message or null
     */
    public String validate() {
        String retValue = null;
        getLines(true);
        if (m_lines.length == 0) retValue = "@NoLines@";
        else if (getPercentTotal().compareTo(Env.ONEHUNDRED) != 0) retValue = "@PercentTotal@ <> 100";
        else {
            //	More then one line with 0
            int lineFound = -1;
            for (I_GL_DistributionLine m_line : m_lines) {
                if (m_line.getPercent().compareTo(Env.ZERO) == 0) {
                    if (lineFound >= 0 && m_line.getPercent().compareTo(Env.ZERO) == 0) {
                        retValue = "@Line@ " + lineFound + " + " + m_line.getLine() + ": == 0";
                        break;
                    }
                    lineFound = m_line.getLine();
                }
            } //	for all lines
        }

        setIsValid(retValue == null);
        return retValue;
    } //	validate

    /**
     * Distribute Amount to Lines
     *
     * @param acct          account
     * @param Amt           amount
     * @param Qty
     * @param C_Currency_ID currency
     */
    public void distribute(I_C_ValidCombination acct, BigDecimal Amt, BigDecimal Qty, int C_Currency_ID) {
        if (log.isLoggable(Level.INFO))
            log.info("distribute - Amt=" + Amt + " - Qty=" + Qty + " - " + acct);
        getLines(false);
        int precision = MCurrencyKt.getCurrencyStdPrecision(C_Currency_ID);
        //	First Round
        BigDecimal total = Env.ZERO;
        BigDecimal totalQty = Env.ZERO;
        int indexBiggest = -1;
        int indexZeroPercent = -1;
        for (int i = 0; i < m_lines.length; i++) {
            I_GL_DistributionLine dl = m_lines[i];
            if (!dl.isActive()) continue;
            dl.setAccount(acct);
            //	Calculate Amount
            dl.calculateAmt(Amt, precision);
            //	Calculate Quantity
            dl.calculateQty(Qty);
            total = total.add(dl.getAmt());
            totalQty = totalQty.add(dl.getQty());
            //	Remainder
            if (dl.getPercent().compareTo(Env.ZERO) == 0) indexZeroPercent = i;
            if (indexZeroPercent == -1) {
                if (indexBiggest == -1) indexBiggest = i;
                else if (dl.getAmt().compareTo(m_lines[indexBiggest].getAmt()) > 0) indexBiggest = i;
            }
        }
        //	Adjust Remainder
        BigDecimal difference = Amt.subtract(total);
        if (difference.compareTo(Env.ZERO) != 0) {
            if (indexZeroPercent != -1) {
                m_lines[indexZeroPercent].setAmt(difference);
            } else if (indexBiggest != -1) {
                m_lines[indexBiggest].setAmt(m_lines[indexBiggest].getAmt().add(difference));
            } else log.warning("distribute - Remaining Difference=" + difference);
        }
        //	Adjust Remainder
        BigDecimal differenceQty = Qty.subtract(totalQty);
        if (differenceQty.compareTo(Env.ZERO) != 0) {
            if (indexZeroPercent != -1) {
                m_lines[indexZeroPercent].setQty(differenceQty);
            } else if (indexBiggest != -1) {
                m_lines[indexBiggest].setQty(m_lines[indexBiggest].getQty().add(differenceQty));
            } else log.warning("distribute - Remaining Qty Difference=" + differenceQty);
        }
        //
        if (CLogMgt.isLevelFinest()) {
            for (I_GL_DistributionLine m_line : m_lines) {
                if (m_line.isActive())
                    if (log.isLoggable(Level.FINE))
                        log.fine("distribute = Amt=" + m_line.getAmt() + " - " + m_line.getAccount());
            }
        }
    } //	distribute

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        //	Reset not selected Any
        if (isAnyAcct() && getAccountId() != 0) setAccountId(0);
        if (isAnyActivity() && getBusinessActivityId() != 0) setBusinessActivityId(0);
        if (isAnyBPartner() && getBusinessPartnerId() != 0) setBusinessPartnerId(0);
        if (isAnyCampaign() && getCampaignId() != 0) setCampaignId(0);
        if (isAnyLocFrom() && getLocationFromId() != 0) setLocationFromId(0);
        if (isAnyLocTo() && getLocationToId() != 0) setLocationToId(0);
        if (isAnyOrg() && this.getOrgId() != 0) this.setOrgId(0);
        if (isAnyOrgTrx() && getTransactionOrganizationId() != 0) setTransactionOrganizationId(0);
        if (isAnyProduct() && getProductId() != 0) setProductId(0);
        if (isAnyProject() && getProjectId() != 0) setProjectId(0);
        if (isAnySalesRegion() && getSalesRegionId() != 0) setSalesRegionId(0);
        if (isAnyUser1() && getUser1Id() != 0) setUser1Id(0);
        if (isAnyUser2() && getUser2Id() != 0) setUser2Id(0);
        return true;
    } //	beforeSave
} //	MDistribution
