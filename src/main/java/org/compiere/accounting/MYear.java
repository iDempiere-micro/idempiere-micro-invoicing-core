package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.I_C_Year;
import org.compiere.process.SvrProcess;
import org.idempiere.common.exceptions.FillMandatoryException;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Language;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * Year Model
 *
 * @author Jorg Janke
 * @author Teo Sarca, www.arhipac.ro
 * <li>BF [ 1761918 ] Error creating periods for a year with per. created partial
 * <li>BF [ 2430755 ] Year Create Periods display proper error message
 * @version $Id: MYear.java,v 1.5 2006/10/11 04:12:39 jjanke Exp $
 */
public class MYear extends X_C_Year {
    /**
     *
     */
    private static final long serialVersionUID = 2110541427179611810L;

    /**
     * Standard Constructor
     *
     * @param ctx       context
     * @param C_Year_ID id
     */
    public MYear(int C_Year_ID) {
        super(C_Year_ID);
        if (C_Year_ID == 0) {
            setProcessing(false); // N
        }
    } //	MYear

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MYear(Row row) {
        super(row);
    } //	MYear

    /**
     * Parent Constructor
     *
     * @param calendar parent
     */
    public MYear(MCalendar calendar) {
        this(0);
        setClientOrg(calendar);
        setCalendarId(calendar.getCalendarId());
        setYear();
    } //	MYear

    /**
     * Set current Year
     */
    private void setYear() {
        GregorianCalendar cal = new GregorianCalendar(Language.getLoginLanguage().getLocale());
        String Year = String.valueOf(cal.get(Calendar.YEAR));
        super.setFiscalYear(Year);
    } //	setYear

    /**
     * Get Year As Int
     *
     * @return year as int or 0
     */
    public int getYearAsInt() {
        String year = getFiscalYear();
        try {
            return Integer.parseInt(year);
        } catch (Exception e) {
            StringTokenizer st = new StringTokenizer(year, "/-, \t\n\r\f");
            if (st.hasMoreTokens()) {
                String year2 = st.nextToken();
                try {
                    return Integer.parseInt(year2);
                } catch (Exception e2) {
                    log.log(Level.WARNING, year + "->" + year2 + " - " + e2.toString());
                }
            } else log.log(Level.WARNING, year + " - " + e.toString());
        }
        return 0;
    } //	getYearAsInt

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MYear[");
        sb.append(getId()).append("-").append(getFiscalYear()).append("]");
        return sb.toString();
    } //	toString

    @Override
    protected boolean beforeSave(boolean newRecord) {
        int yy = getYearAsInt();
        if (yy == 0) {
            throw new FillMandatoryException(I_C_Year.COLUMNNAME_FiscalYear);
        }
        return true;
    } //	beforeSave

    /**
     * Create 12 Standard (Jan-Dec) Periods. Creates also Period Control from DocType.
     *
     * @param locale locale
     * @return true if created
     * @see DocumentTypeVerify#createPeriodControls(Properties, int, SvrProcess, String)
     */
    public void createStdPeriods(Locale locale) {
        createStdPeriods(locale, null, null);
    } //	createStdPeriods

    /**
     * Create 12 Standard Periods from the specified start date. Creates also Period Control from
     * DocType.
     *
     * @param locale     locale
     * @param startDate  first day of the calendar year
     * @param dateFormat SimpleDateFormat pattern for generating the period names.
     * @return true if created
     * @see DocumentTypeVerify#createPeriodControls(Properties, int, SvrProcess, String)
     */
    public boolean createStdPeriods(Locale locale, Timestamp startDate, String dateFormat) {
        if (locale == null) {
            ClientWithAccounting client = MClientKt.getClientWithAccounting();
            locale = client.getLocale();
        }

        if (locale == null && Language.getLoginLanguage() != null)
            locale = Language.getLoginLanguage().getLocale();
        if (locale == null) locale = Env.getLanguage().getLocale();
        //
        SimpleDateFormat formatter;
        if (dateFormat == null || dateFormat.equals("")) dateFormat = "MMM-yy";
        formatter = new SimpleDateFormat(dateFormat, locale);

        //
        int year = getYearAsInt();
        GregorianCalendar cal = new GregorianCalendar(locale);
        if (startDate != null) {
            cal.setTime(startDate);
            if (cal.get(Calendar.YEAR) != year) // specified start date takes precedence in setting year
                year = cal.get(Calendar.YEAR);

        } else {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        //
        for (int month = 0; month < 12; month++) {

            Timestamp start = new Timestamp(cal.getTimeInMillis());
            String name = formatter.format(start);
            // get last day of same month
            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Timestamp end = new Timestamp(cal.getTimeInMillis());
            //
            MPeriod period = MPeriod.findByCalendar(start, getCalendarId());
            if (period == null) {
                period = new MPeriod(this, month + 1, name, start, end);
            } else {
                period.setYearId(this.getYearId());
                period.setPeriodNo(month + 1);
                period.setName(name);
                period.setStartDate(start);
                period.setEndDate(end);
            }
            period.saveEx(); // 	Creates Period Control
            // get first day of next month
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        return true;
    } //	createStdPeriods
} //	MYear
