package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_UOM_Conversion;
import org.compiere.orm.MSysConfig;
import org.compiere.orm.Query;
import org.compiere.product.MUOM;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.DBException;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Unit of Measure Conversion Model
 *
 * @author Jorg Janke
 * @version $Id: MUOMConversion.java,v 1.3 2006/07/30 00:51:03 jjanke Exp $
 */
public class MUOMConversion extends X_C_UOM_Conversion {
    /**
     *
     */
    private static final long serialVersionUID = -8449239579085422641L;
    /**
     * Static Logger
     */
    private static final CLogger s_log = CLogger.getCLogger(MUOMConversion.class);
    /**
     * Indicator for Rate
     */
    private static final BigDecimal GETRATE = BigDecimal.valueOf(123.456);
    /**
     * Product Conversion Map
     */
    private static final CCache<Integer, I_C_UOM_Conversion[]> s_conversionProduct =
            new CCache<>(
                    I_C_UOM_Conversion.Table_Name, I_C_UOM_Conversion.Table_Name + "_Of_Product", 20);

    /**
     * ************************************************************************ Default Constructor
     *
     * @param C_UOM_Conversion_ID id
     */
    public MUOMConversion(int C_UOM_Conversion_ID) {
        super(C_UOM_Conversion_ID);
    } //	MUOMConversion

    /**
     * Load Constructor
     */
    public MUOMConversion(Row row) {
        super(row);
    } //	MUOMConversion

    /**
     * Parent Constructor
     *
     * @param parent uom parent
     */
    public MUOMConversion(MUOM parent) {
        this(0);
        setClientOrg(parent);
        setUOMId(parent.getUOMId());
        setProductId(0);
        //
        setTargetUOMId(parent.getUOMId());
        setMultiplyRate(Env.ONE);
        setDivideRate(Env.ONE);
    } //	MUOMConversion

    /**
     * Parent Constructor
     *
     * @param parent product parent
     */
    public MUOMConversion(MProduct parent) {
        this(0);
        setClientOrg(parent);
        setUOMId(parent.getUOMId());
        setProductId(parent.getProductId());
        //
        setTargetUOMId(parent.getUOMId());
        setMultiplyRate(Env.ONE);
        setDivideRate(Env.ONE);
    } //	MUOMConversion

    /**
     * Convert qty to target UOM and round.
     *
     * @param C_UOM_ID    from UOM
     * @param C_UOM_To_ID to UOM
     * @param qty         qty
     * @return converted qty (std precision)
     */
    public static BigDecimal convert(int C_UOM_ID, int C_UOM_To_ID, BigDecimal qty) {
        if (qty == null || qty.compareTo(Env.ZERO) == 0 || C_UOM_ID == C_UOM_To_ID) return qty;
        BigDecimal retValue = getRate(C_UOM_ID, C_UOM_To_ID);
        if (retValue != null) {
            MUOM uom = MUOM.get(C_UOM_To_ID);
            if (uom != null) return uom.round(retValue.multiply(qty), true);
            return retValue.multiply(qty);
        }
        return null;
    } //	convert

    /**
     * Get Multiplier Rate to target UOM
     *
     * @param C_UOM_ID    from UOM
     * @param C_UOM_To_ID to UOM
     * @return multiplier
     */
    public static BigDecimal getRate(int C_UOM_ID, int C_UOM_To_ID) {
        //	nothing to do
        if (C_UOM_ID == C_UOM_To_ID) return Env.ONE;
        //
        Point p = new Point(C_UOM_ID, C_UOM_To_ID);
        //	get conversion
        return getRate(p);
    } //	convert

    /**
     * ************************************************************************ Get Conversion
     * Multiplier Rate, try to derive it if not found directly
     *
     * @param p Point with from(x) - to(y) C_UOM_ID
     * @return conversion multiplier or null
     */
    private static BigDecimal getRate(Point p) {
        BigDecimal retValue;
        retValue = getRate(p.x, p.y);
        if (retValue != null) return retValue;
        //	try to derive
        return deriveRate(p.x, p.y);
    } //	getConversion

    /**
     * Derive Standard Conversions
     *
     * @param C_UOM_ID    from UOM
     * @param C_UOM_To_ID to UOM
     * @return Conversion or null
     */
    public static BigDecimal deriveRate(int C_UOM_ID, int C_UOM_To_ID) {
        if (C_UOM_ID == C_UOM_To_ID) return Env.ONE;
        //	get Info
        MUOM from = MUOM.get(C_UOM_ID);
        MUOM to = MUOM.get(C_UOM_To_ID);
        if (from == null || to == null) return null;

        //	Time - Minute
        if (from.isMinute()) {
            if (to.isHour()) return BigDecimal.valueOf(1.0 / 60.0);
            if (to.isDay()) return BigDecimal.valueOf(1.0 / 1440.0); // 	24 * 60
            if (to.isWorkDay()) return BigDecimal.valueOf(1.0 / 480.0); // 	8 * 60
            if (to.isWeek()) return BigDecimal.valueOf(1.0 / 10080.0); // 	7 * 24 * 60
            if (to.isMonth()) return BigDecimal.valueOf(1.0 / 43200.0); // 	30 * 24 * 60
            if (to.isWorkMonth()) return BigDecimal.valueOf(1.0 / 9600.0); // 	4 * 5 * 8 * 60
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 525600.0); // 	365 * 24 * 60
        }
        //	Time - Hour
        if (from.isHour()) {
            if (to.isMinute()) return BigDecimal.valueOf(60.0);
            if (to.isDay()) return BigDecimal.valueOf(1.0 / 24.0);
            if (to.isWorkDay()) return BigDecimal.valueOf(1.0 / 8.0);
            if (to.isWeek()) return BigDecimal.valueOf(1.0 / 168.0); // 	7 * 24
            if (to.isMonth()) return BigDecimal.valueOf(1.0 / 720.0); // 	30 * 24
            if (to.isWorkMonth()) return BigDecimal.valueOf(1.0 / 160.0); // 	4 * 5 * 8
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 8760.0); // 	365 * 24
        }
        //	Time - Day
        if (from.isDay()) {
            if (to.isMinute()) return BigDecimal.valueOf(1440.0); // 	24 * 60
            if (to.isHour()) return BigDecimal.valueOf(24.0);
            if (to.isWorkDay()) return BigDecimal.valueOf(3.0); // 	24 / 8
            if (to.isWeek()) return BigDecimal.valueOf(1.0 / 7.0); // 	7
            if (to.isMonth()) return BigDecimal.valueOf(1.0 / 30.0); // 	30
            if (to.isWorkMonth()) return BigDecimal.valueOf(1.0 / 20.0); // 	4 * 5
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 365.0); // 	365
        }
        //	Time - WorkDay
        if (from.isWorkDay()) {
            if (to.isMinute()) return BigDecimal.valueOf(480.0); // 	8 * 60
            if (to.isHour()) return BigDecimal.valueOf(8.0); // 	8
            if (to.isDay()) return BigDecimal.valueOf(1.0 / 3.0); // 	24 / 8
            if (to.isWeek()) return BigDecimal.valueOf(1.0 / 5); // 	5
            if (to.isMonth()) return BigDecimal.valueOf(1.0 / 20.0); // 	4 * 5
            if (to.isWorkMonth()) return BigDecimal.valueOf(1.0 / 20.0); // 	4 * 5
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 240.0); // 	4 * 5 * 12
        }
        //	Time - Week
        if (from.isWeek()) {
            if (to.isMinute()) return BigDecimal.valueOf(10080.0); // 	7 * 24 * 60
            if (to.isHour()) return BigDecimal.valueOf(168.0); // 	7 * 24
            if (to.isDay()) return BigDecimal.valueOf(7.0);
            if (to.isWorkDay()) return BigDecimal.valueOf(5.0);
            if (to.isMonth()) return BigDecimal.valueOf(1.0 / 4.0); // 	4
            if (to.isWorkMonth()) return BigDecimal.valueOf(1.0 / 4.0); // 	4
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 50.0); // 	50
        }
        //	Time - Month
        if (from.isMonth()) {
            if (to.isMinute()) return BigDecimal.valueOf(43200.0); // 	30 * 24 * 60
            if (to.isHour()) return BigDecimal.valueOf(720.0); // 	30 * 24
            if (to.isDay()) return BigDecimal.valueOf(30.0); // 	30
            if (to.isWorkDay()) return BigDecimal.valueOf(20.0); // 	4 * 5
            if (to.isWeek()) return BigDecimal.valueOf(4.0); // 	4
            if (to.isWorkMonth()) return BigDecimal.valueOf(1.5); // 	30 / 20
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 12.0); // 	12
        }
        //	Time - WorkMonth
        if (from.isWorkMonth()) {
            if (to.isMinute()) return BigDecimal.valueOf(9600.0); // 	4 * 5 * 8 * 60
            if (to.isHour()) return BigDecimal.valueOf(160.0); // 	4 * 5 * 8
            if (to.isDay()) return BigDecimal.valueOf(20.0); // 	4 * 5
            if (to.isWorkDay()) return BigDecimal.valueOf(20.0); // 	4 * 5
            if (to.isWeek()) return BigDecimal.valueOf(4.0); // 	4
            if (to.isMonth()) return BigDecimal.valueOf(20.0 / 30.0); // 	20 / 30
            if (to.isYear()) return BigDecimal.valueOf(1.0 / 12.0); // 	12
        }
        //	Time - Year
        if (from.isYear()) {
            if (to.isMinute()) return BigDecimal.valueOf(518400.0); // 	12 * 30 * 24 * 60
            if (to.isHour()) return BigDecimal.valueOf(8640.0); // 	12 * 30 * 24
            if (to.isDay()) return BigDecimal.valueOf(365.0); // 	365
            if (to.isWorkDay()) return BigDecimal.valueOf(240.0); // 	12 * 4 * 5
            if (to.isWeek()) return BigDecimal.valueOf(50.0); // 	52
            if (to.isMonth()) return BigDecimal.valueOf(12.0); // 	12
            if (to.isWorkMonth()) return BigDecimal.valueOf(12.0); // 	12
        }
        //
        return null;
    } //	deriveRate

    /**
     * Get Converted Qty from Server (no cache)
     *
     * @param qty           The quantity to be converted
     * @param C_UOM_From_ID The C_UOM_ID of the qty
     * @param C_UOM_To_ID   The targeted UOM
     * @param StdPrecision  if true, standard precision, if false costing precision
     * @return amount
     * @depreciated should not be used
     */
    public static BigDecimal convert(
            int C_UOM_From_ID, int C_UOM_To_ID, BigDecimal qty, boolean StdPrecision) {
        //  Nothing to do
        if (qty == null || qty.compareTo(Env.ZERO) == 0 || C_UOM_From_ID == C_UOM_To_ID) return qty;
        //
        BigDecimal retValue = null;
        int precision = 2;
        String sql =
                "SELECT c.MultiplyRate, uomTo.StdPrecision, uomTo.CostingPrecision "
                        + "FROM	C_UOM_Conversion c"
                        + " INNER JOIN C_UOM uomTo ON (c.C_UOM_TO_ID=uomTo.C_UOM_ID) "
                        + "WHERE c.IsActive='Y' AND c.C_UOM_ID=? AND c.C_UOM_TO_ID=? " //	#1/2
                        + " AND c.M_Product_ID IS NULL"
                        + " ORDER BY c.clientId DESC, c.orgId DESC";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, C_UOM_From_ID);
            pstmt.setInt(2, C_UOM_To_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                retValue = rs.getBigDecimal(1);
                precision = rs.getInt(StdPrecision ? 2 : 3);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }
        if (retValue == null) {
            if (s_log.isLoggable(Level.INFO))
                s_log.info("NOT found - FromUOM=" + C_UOM_From_ID + ", ToUOM=" + C_UOM_To_ID);
            return null;
        }

        //	Just get Rate
        if (GETRATE.equals(qty)) return retValue;

        //	Calculate & Scale
        retValue = retValue.multiply(qty);
        if (retValue.scale() > precision)
            retValue = retValue.setScale(precision, BigDecimal.ROUND_HALF_UP);
        return retValue;
    } //  convert

    /**
     * ************************************************************************ Convert PRICE
     * expressed in entered UoM to equivalent price in product UoM and round. <br>
     * OR Convert QTY in product UOM to qty in entered UoM and round. <br>
     * eg: $6/6pk => $1/ea <br>
     * OR 6 X ea => 1 X 6pk
     *
     * @param M_Product_ID product
     * @param C_UOM_To_ID  entered UOM
     * @param qtyPrice     quantity or price
     * @return Product: Qty/Price (precision rounded)
     */
    public static BigDecimal convertProductTo(
            int M_Product_ID, int C_UOM_To_ID, BigDecimal qtyPrice) {
        if (qtyPrice == null || qtyPrice.signum() == 0 || M_Product_ID == 0 || C_UOM_To_ID == 0)
            return qtyPrice;

        BigDecimal retValue = getProductRateTo(M_Product_ID, C_UOM_To_ID);
        if (retValue != null) {
            if (Env.ONE.compareTo(retValue) == 0) return qtyPrice;
            MUOM uom = MUOM.get(C_UOM_To_ID);
            if (uom != null) return uom.round(retValue.multiply(qtyPrice), true);
            return retValue.multiply(qtyPrice);
        }
        return null;
    } //	convertProductTo

    /**
     * Get multiply rate to convert PRICE from price in entered UOM to price in product UOM <br>
     * OR multiply rate to convert QTY from product UOM to entered UOM
     *
     * @param M_Product_ID product
     * @param C_UOM_To_ID  entered UOM
     * @return multiplier or null
     */
    public static BigDecimal getProductRateTo(int M_Product_ID, int C_UOM_To_ID) {
        if (M_Product_ID == 0) return null;
        I_C_UOM_Conversion[] rates = getProductConversions(M_Product_ID);

        for (I_C_UOM_Conversion rate : rates) {
            if (rate.getTargetUOMId() == C_UOM_To_ID) return rate.getMultiplyRate();
        }

        List<I_C_UOM_Conversion> conversions =
                new Query<I_C_UOM_Conversion>(I_C_UOM_Conversion.Table_Name, "C_UOM_ID=? AND C_UOM_TO_ID=?")
                        .setParameters(MProduct.get(M_Product_ID).getUOMId(), C_UOM_To_ID)
                        .setOnlyActiveRecords(true)
                        .list();
        for (I_C_UOM_Conversion rate : conversions) {
            if (rate.getTargetUOMId() == C_UOM_To_ID) return rate.getMultiplyRate();
        }
        return null;
    } //	getProductRateTo

    /**
     * Get Product Conversions (cached)
     *
     * @param M_Product_ID product
     * @return array of conversions
     */
    public static I_C_UOM_Conversion[] getProductConversions(int M_Product_ID) {
        if (M_Product_ID == 0) return new MUOMConversion[0];
        Integer key = M_Product_ID;
        I_C_UOM_Conversion[] result = s_conversionProduct.get(key);
        if (result != null) return result;

        ArrayList<I_C_UOM_Conversion> list = new ArrayList<>();
        //	Add default conversion
        MUOMConversion defRate = new MUOMConversion(MProduct.get(M_Product_ID));
        list.add(defRate);
        //
        final String whereClause =
                "M_Product_ID=?"
                        + " AND EXISTS (SELECT 1 FROM M_Product p "
                        + "WHERE C_UOM_Conversion.M_Product_ID=p.M_Product_ID AND C_UOM_Conversion.C_UOM_ID=p.C_UOM_ID)";
        List<I_C_UOM_Conversion> conversions =
                new Query<I_C_UOM_Conversion>(I_C_UOM_Conversion.Table_Name, whereClause)
                        .setParameters(M_Product_ID)
                        .setOnlyActiveRecords(true)
                        .list();
        list.addAll(conversions);

        //	Convert & save
        result = new I_C_UOM_Conversion[list.size()];
        list.toArray(result);
        s_conversionProduct.put(key, result);
        if (s_log.isLoggable(Level.FINE))
            s_log.fine("getProductConversions - M_Product_ID=" + M_Product_ID + " #" + result.length);
        return result;
    } //	getProductConversions

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true if can be saved
     */
    protected boolean beforeSave(boolean newRecord) {
        //	From - To is the same
        if (getUOMId() == getTargetUOMId()) {
            log.saveError("Error", MsgKt.parseTranslation("@C_UOM_ID@ = @C_UOM_To_ID@"));
            return false;
        }
        //	Nothing to convert
        if (getMultiplyRate().compareTo(Env.ZERO) <= 0) {
            log.saveError("Error", MsgKt.parseTranslation("@MultiplyRate@ <= 0"));
            return false;
        }
        //	Enforce Product UOM
        if (MSysConfig.getBooleanValue(
                MSysConfig.ProductUOMConversionUOMValidate, true, getClientId())) {
            if (getProductId() != 0 && (newRecord || isValueChanged("M_Product_ID"))) {
                // Check of product must be in the same transaction as the conversion being saved
                MProduct product = new MProduct(getProductId());
                if (product.getUOMId() != getUOMId()) {
                    MUOM uom = MUOM.get(product.getUOMId());
                    log.saveError("ProductUOMConversionUOMError", uom.getName());
                    return false;
                }
            }
        }

        //	The Product UoM needs to be the smallest UoM - Multiplier must be < 0; Divider must be > 0
        if (MSysConfig.getBooleanValue(
                MSysConfig.ProductUOMConversionRateValidate, true, getClientId())) {
            if (getProductId() != 0 && getDivideRate().compareTo(Env.ONE) < 0) {
                log.saveError("ProductUOMConversionRateError", "");
                return false;
            }
        }

        return true;
    } //	beforeSave

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MUOMConversion[" + getId() +
                "-C_UOM_ID=" +
                getUOMId() +
                ",C_UOM_To_ID=" +
                getTargetUOMId() +
                ",M_Product_ID=" +
                getProductId() +
                "-Multiply=" +
                getMultiplyRate() +
                "/Divide=" +
                getDivideRate() +
                "]";
    } //	toString
} //	UOMConversion
