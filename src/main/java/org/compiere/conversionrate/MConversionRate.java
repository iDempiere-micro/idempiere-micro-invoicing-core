package org.compiere.conversionrate;

import kotliquery.Row;
import org.compiere.bo.MCurrencyKt;
import org.compiere.model.I_C_Conversion_Rate;
import org.compiere.orm.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Currency Conversion Rate Model
 *
 * @author Jorg Janke
 * @version $Id: MConversionRate.java,v 1.2 2006/07/30 00:58:18 jjanke Exp $
 */
public class MConversionRate extends X_C_Conversion_Rate {
    /**
     *
     */
    private static final long serialVersionUID = -7938144674700640228L;

    /**
     * Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MConversionRate.class);

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param C_Conversion_Rate_ID id
     */
    public MConversionRate(int C_Conversion_Rate_ID) {
        super(C_Conversion_Rate_ID);
        if (C_Conversion_Rate_ID == 0) {
            super.setDivideRate(Env.ZERO);
            super.setMultiplyRate(Env.ZERO);
            setValidFrom(new Timestamp(System.currentTimeMillis()));
        }
    } //	MConversionRate

    /**
     * Load Constructor
     */
    public MConversionRate(Row row) {
        super(row);
    } //	MConversionRate

    /**
     * New Constructor
     *
     * @param po                  parent
     * @param C_ConversionType_ID conversion type
     * @param C_Currency_ID       currency
     * @param C_Currency_ID_To    currency to
     * @param MultiplyRate        multiply rate
     * @param ValidFrom           valid from
     */
    public MConversionRate(
            PO po,
            int C_ConversionType_ID,
            int C_Currency_ID,
            int C_Currency_ID_To,
            BigDecimal MultiplyRate,
            Timestamp ValidFrom) {
        this(0);
        setClientOrg(po);
        setConversionTypeId(C_ConversionType_ID);
        setCurrencyId(C_Currency_ID);
        setTargetCurrencyId(C_Currency_ID_To);
        //
        setMultiplyRate(MultiplyRate);
        setValidFrom(ValidFrom);
    } //	MConversionRate

    /**
     * Convert an amount with today's default rate
     *
     * @param CurFrom_ID   The C_Currency_ID FROM
     * @param CurTo_ID     The C_Currency_ID TO
     * @param Amt          amount to be converted
     * @param AD_Client_ID client
     * @param AD_Org_ID    organization
     * @return converted amount
     */
    public static BigDecimal convert(

            BigDecimal Amt,
            int CurFrom_ID,
            int CurTo_ID,
            int AD_Client_ID,
            int AD_Org_ID) {
        return convert(Amt, CurFrom_ID, CurTo_ID, null, 0, AD_Client_ID, AD_Org_ID);
    } //  convert

    /**
     * Convert an amount
     *
     * @param CurFrom_ID          The C_Currency_ID FROM
     * @param CurTo_ID            The C_Currency_ID TO
     * @param ConvDate            conversion date - if null - use current date
     * @param C_ConversionType_ID conversion rate type - if 0 - use Default
     * @param Amt                 amount to be converted
     * @param AD_Client_ID        client
     * @param AD_Org_ID           organization
     * @return converted amount or null if no rate
     */
    public static BigDecimal convert(

            BigDecimal Amt,
            int CurFrom_ID,
            int CurTo_ID,
            Timestamp ConvDate,
            int C_ConversionType_ID,
            int AD_Client_ID,
            int AD_Org_ID) {
        return convert(

                Amt,
                CurFrom_ID,
                CurTo_ID,
                ConvDate,
                C_ConversionType_ID,
                AD_Client_ID,
                AD_Org_ID,
                false);
    } //	convert

    /**
     * Convert an amount
     *
     * @param CurFrom_ID          The C_Currency_ID FROM
     * @param CurTo_ID            The C_Currency_ID TO
     * @param ConvDate            conversion date - if null - use current date
     * @param C_ConversionType_ID conversion rate type - if 0 - use Default
     * @param Amt                 amount to be converted
     * @param AD_Client_ID        client
     * @param AD_Org_ID           organization
     * @return converted amount or null if no rate
     */
    public static BigDecimal convert(

            BigDecimal Amt,
            int CurFrom_ID,
            int CurTo_ID,
            Timestamp ConvDate,
            int C_ConversionType_ID,
            int AD_Client_ID,
            int AD_Org_ID,
            boolean isCosting) {
        if (Amt == null) throw new IllegalArgumentException("Required parameter missing - Amt");
        if (CurFrom_ID == CurTo_ID || Amt.compareTo(Env.ZERO) == 0) return Amt;
        //	Get Rate
        BigDecimal retValue =
                getRate(CurFrom_ID, CurTo_ID, ConvDate, C_ConversionType_ID, AD_Client_ID, AD_Org_ID);
        if (retValue == null) return null;

        //	Get Amount in Currency Precision
        retValue = retValue.multiply(Amt);
        int stdPrecision =
                isCosting
                        ? MCurrencyKt.getCurrencyCostingPrecision(CurTo_ID)
                        : MCurrencyKt.getCurrencyStdPrecision(CurTo_ID);

        if (retValue.scale() > stdPrecision)
            retValue = retValue.setScale(stdPrecision, BigDecimal.ROUND_HALF_UP);

        return retValue;
    } //	convert

    /**
     * Get Currency Conversion Rate
     *
     * @param CurFrom_ID        The C_Currency_ID FROM
     * @param CurTo_ID          The C_Currency_ID TO
     * @param ConvDate          The Conversion date - if null - use current date
     * @param ConversionType_ID Conversion rate type - if 0 - use Default
     * @param AD_Client_ID      client
     * @param AD_Org_ID         organization
     * @return currency Rate or null
     */
    public static BigDecimal getRate(
            int CurFrom_ID,
            int CurTo_ID,
            Timestamp ConvDate,
            int ConversionType_ID,
            int AD_Client_ID,
            int AD_Org_ID) {
        if (CurFrom_ID == CurTo_ID) return Env.ONE;
        //	Conversion Type
        int C_ConversionType_ID = ConversionType_ID;
        if (C_ConversionType_ID == 0) C_ConversionType_ID = MConversionType.getDefault(AD_Client_ID);
        //	Conversion Date
        if (ConvDate == null) ConvDate = new Timestamp(System.currentTimeMillis());

        //	Get Rate
        String sql =
                "SELECT MultiplyRate "
                        + "FROM C_Conversion_Rate "
                        + "WHERE C_Currency_ID=?" //	#1
                        + " AND C_Currency_ID_To=?" //	#2
                        + " AND	C_ConversionType_ID=?" //	#3
                        + " AND	? BETWEEN ValidFrom AND ValidTo" //	#4	TRUNC (?) ORA-00932: inconsistent
                        // datatypes: expected NUMBER got TIMESTAMP
                        + " AND clientId IN (0,?)" //	#5
                        + " AND orgId IN (0,?) " //	#6
                        + " AND IsActive = 'Y' "
                        + "ORDER BY clientId DESC, orgId DESC, ValidFrom DESC";
        BigDecimal retValue = null;
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, CurFrom_ID);
            pstmt.setInt(2, CurTo_ID);
            pstmt.setInt(3, C_ConversionType_ID);
            pstmt.setTimestamp(4, ConvDate);
            pstmt.setInt(5, AD_Client_ID);
            pstmt.setInt(6, AD_Org_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) retValue = rs.getBigDecimal(1);
        } catch (Exception e) {
            s_log.log(Level.SEVERE, "getRate", e);
        }
        if (retValue == null)
            if (s_log.isLoggable(Level.INFO))
                s_log.info(
                        "getRate - not found - CurFrom="
                                + CurFrom_ID
                                + ", CurTo="
                                + CurTo_ID
                                + ", "
                                + ConvDate
                                + ", Type="
                                + ConversionType_ID
                                + (ConversionType_ID == C_ConversionType_ID ? "" : "->" + C_ConversionType_ID)
                                + ", Client="
                                + AD_Client_ID
                                + ", Org="
                                + AD_Org_ID);
        return retValue;
    } //	getRate

    /**
     * Set Multiply Rate Sets also Divide Rate
     *
     * @param MultiplyRate multiply rate
     */
    public void setMultiplyRate(BigDecimal MultiplyRate) {
        if (MultiplyRate == null
                || MultiplyRate.compareTo(Env.ZERO) == 0
                || MultiplyRate.compareTo(Env.ONE) == 0) {
            super.setDivideRate(Env.ONE);
            super.setMultiplyRate(Env.ONE);
        } else {
            super.setMultiplyRate(MultiplyRate);
            double dd = 1 / MultiplyRate.doubleValue();
            super.setDivideRate(BigDecimal.valueOf(dd));
        }
    } //	setMultiplyRate

    /**
     * Set Divide Rate. Sets also Multiply Rate
     *
     * @param DivideRate divide rate
     */
    public void setDivideRate(BigDecimal DivideRate) {
        if (DivideRate == null
                || DivideRate.compareTo(Env.ZERO) == 0
                || DivideRate.compareTo(Env.ONE) == 0) {
            super.setDivideRate(Env.ONE);
            super.setMultiplyRate(Env.ONE);
        } else {
            super.setDivideRate(DivideRate);
            double dd = 1 / DivideRate.doubleValue();
            super.setMultiplyRate(BigDecimal.valueOf(dd));
        }
    } //	setDivideRate

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MConversionRate[" + getId() +
                ",Currency=" +
                getCurrencyId() +
                ",To=" +
                getTargetCurrencyId() +
                ", Multiply=" +
                getMultiplyRate() +
                ",Divide=" +
                getDivideRate() +
                ", ValidFrom=" +
                getValidFrom() +
                "]";
    } //	toString

    /**
     * Before Save. - Same Currency - Date Range Check - Set To date to 2056
     *
     * @param newRecord new
     * @return true if OK to save
     */
    protected boolean beforeSave(boolean newRecord) {
        //	From - To is the same
        if (getCurrencyId() == getTargetCurrencyId()) {
            log.saveError("Error", MsgKt.parseTranslation("@C_Currency_ID@ = @C_Currency_ID@"));
            return false;
        }
        //	Nothing to convert
        if (getMultiplyRate().compareTo(Env.ZERO) <= 0) {
            log.saveError("Error", MsgKt.parseTranslation("@MultiplyRate@ <= 0"));
            return false;
        }

        //	Date Range Check
        Timestamp from = getValidFrom();
        if (getValidTo() == null) {
            // setValidTo (TimeUtil.getDay(2056, 1, 29));	//	 no exchange rates after my 100th birthday
            log.saveError(
                    "FillMandatory", MsgKt.getElementTranslation(I_C_Conversion_Rate.COLUMNNAME_ValidTo));
            return false;
        }
        Timestamp to = getValidTo();

        if (to.before(from)) {
            SimpleDateFormat df = DisplayType.getDateFormat(DisplayType.Date);
            log.saveError("Error", df.format(to) + " < " + df.format(from));
            return false;
        }

        return true;
    } //	beforeSave
} //	MConversionRate
