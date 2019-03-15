package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.orm.MDocType;
import org.compiere.orm.MOrgInfo;
import org.compiere.orm.MTable;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.orm.TimeUtil;
import org.compiere.util.DisplayType;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.CLogger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Calendar Period Model
 *
 * @author Jorg Janke
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * <li>BF [ 1779438 ] Minor auto period control bug
 * <li>BF [ 1893486 ] Auto Period Control return that period is always open
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @version $Id: MPeriod.java,v 1.4 2006/07/30 00:51:05 jjanke Exp $
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 */
public class MPeriod extends X_C_Period {
    /**
     *
     */
    private static final long serialVersionUID = 769103495098446073L;
    /**
     * Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MPeriod.class);
    /**
     * Calendar
     */
    private int m_C_Calendar_ID = 0;
    /**
     * Period Controls
     */
    private MPeriodControl[] m_controls = null;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param ctx         context
     * @param C_Period_ID id
     */
    public MPeriod(Properties ctx, int C_Period_ID) {
        super(ctx, C_Period_ID);
        if (C_Period_ID == 0) {
            //	setPeriodId (0);		//	PK
            //  setC_Year_ID (0);		//	Parent
            //  setName (null);
            //  setPeriodNo (0);
            //  setStartDate (new Timestamp(System.currentTimeMillis()));
            setPeriodType(X_C_Period.PERIODTYPE_StandardCalendarPeriod);
        }
    } //	MPeriod

    public MPeriod(Properties ctx, Row row) {
        super(ctx, row);
    }

    /**
     * Parent constructor
     *
     * @param year      year
     * @param PeriodNo  no
     * @param name      name
     * @param startDate start
     * @param endDate   end
     */
    public MPeriod(MYear year, int PeriodNo, String name, Timestamp startDate, Timestamp endDate) {
        this(year.getCtx(), 0);
        setClientOrg(year);
        setC_Year_ID(year.getC_Year_ID());
        setPeriodNo(PeriodNo);
        setName(name);
        setStartDate(startDate);
        setEndDate(endDate);
    } //	MPeriod

    /**
     * Get Period from Cache
     *
     * @param ctx         context
     * @param C_Period_ID id
     * @return MPeriod
     */
    public static MPeriod get(Properties ctx, int C_Period_ID) {
        if (C_Period_ID <= 0) return null;
        //
        Integer key = new Integer(C_Period_ID);
        CCache<Integer, MPeriod> s_cache = MBasePeriodKt.getPeriodCache();
        MPeriod retValue = (MPeriod) s_cache.get(key);
        if (retValue != null) return retValue;
        //
        retValue = new MPeriod(ctx, C_Period_ID);
        if (retValue.getId() != 0) s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * Find standard Period of DateAcct based on Client Calendar
     *
     * @param ctx      context
     * @param DateAcct date
     * @return active Period or null
     * @deprecated
     */
    public static MPeriod get(Properties ctx, Timestamp DateAcct) {
        return get(ctx, DateAcct, 0);
    } //	get

    /**
     * Find standard Period of DateAcct based on Client Calendar
     *
     * @param ctx       context
     * @param DateAcct  date
     * @param AD_Org_ID Organization
     * @return active Period or null
     */
    public static MPeriod get(Properties ctx, Timestamp DateAcct, int AD_Org_ID) {

        if (DateAcct == null) return null;

        int C_Calendar_ID = getC_Calendar_ID(ctx, AD_Org_ID);

        return findByCalendar(ctx, DateAcct, C_Calendar_ID);
    } //	get

    /**
     * @param ctx
     * @param DateAcct
     * @param C_Calendar_ID
     * @return MPeriod
     */
    public static MPeriod findByCalendar(
            Properties ctx, Timestamp DateAcct, int C_Calendar_ID) {
        return MBasePeriodKt.findByCalendar(ctx, DateAcct, C_Calendar_ID);
    }

    /**
     * Find valid standard Period of DateAcct based on Client Calendar
     *
     * @param ctx      context
     * @param DateAcct date
     * @return C_Period_ID or 0
     * @deprecated
     */
    public static int getC_Period_ID(Properties ctx, Timestamp DateAcct) {
        MPeriod period = get(ctx, DateAcct);
        if (period == null) return 0;
        return period.getC_Period_ID();
    } //	getC_Period_ID

    /**
     * Find valid standard Period of DateAcct based on Client Calendar
     *
     * @param ctx       context
     * @param DateAcct  date
     * @param AD_Org_ID Organization
     * @return C_Period_ID or 0
     */
    public static int getC_Period_ID(Properties ctx, Timestamp DateAcct, int AD_Org_ID) {
        MPeriod period = get(ctx, DateAcct, AD_Org_ID);
        if (period == null) return 0;
        return period.getC_Period_ID();
    } //	getC_Period_ID

    /**
     * Is standard Period Open for Document Base Type
     *
     * @param ctx         context
     * @param DateAcct    date
     * @param DocBaseType base type
     * @return true if open
     * @deprecated
     */
    public static boolean isOpen(Properties ctx, Timestamp DateAcct, String DocBaseType) {
        return isOpen(ctx, DateAcct, DocBaseType, 0);
    } //	isOpen

    /**
     * Is standard Period Open for Document Base Type
     *
     * @param ctx         context
     * @param DateAcct    date
     * @param DocBaseType base type
     * @param AD_Org_ID   Organization
     * @return true if open
     */
    public static boolean isOpen(
            Properties ctx, Timestamp DateAcct, String DocBaseType, int AD_Org_ID) {
        if (DateAcct == null) {
            s_log.warning("No DateAcct");
            return false;
        }
        if (DocBaseType == null) {
            s_log.warning("No DocBaseType");
            return false;
        }
        MPeriod period = MPeriod.get(ctx, DateAcct, AD_Org_ID);
        if (period == null) {
            s_log.warning("No Period for " + DateAcct + " (" + DocBaseType + ")");
            return false;
        }
        boolean open = period.isOpen(DocBaseType, DateAcct);
        if (!open)
            s_log.warning(period.getName() + ": Not open for " + DocBaseType + " (" + DateAcct + ")");
        return open;
    } //	isOpen

    /**
     * Find first Year Period of DateAcct based on Client Calendar
     *
     * @param ctx      context
     * @param DateAcct date
     * @return active first Period
     * @deprecated
     */
    public static MPeriod getFirstInYear(Properties ctx, Timestamp DateAcct) {
        return getFirstInYear(ctx, DateAcct, 0);
    } //	getFirstInYear

    /**
     * Find first Year Period of DateAcct based on Client Calendar
     *
     * @param ctx       context
     * @param DateAcct  date
     * @param AD_Org_ID TODO
     * @return active first Period
     */
    public static MPeriod getFirstInYear(Properties ctx, Timestamp DateAcct, int AD_Org_ID) {
        return MBasePeriodKt.getFirstPeriodInYear(ctx, DateAcct, AD_Org_ID);
    } //	getFirstInYear

    /**
     * Convenient method for testing if a period is open
     *
     * @param ctx
     * @param dateAcct
     * @param docBaseType
     * @throws PeriodClosedException if period is closed
     * @see #isOpen(Properties, Timestamp, String)
     * @deprecated
     */
    public static void testPeriodOpen(Properties ctx, Timestamp dateAcct, String docBaseType)
            throws PeriodClosedException {
        if (!MPeriod.isOpen(ctx, dateAcct, docBaseType)) {
            throw new PeriodClosedException(dateAcct, docBaseType);
        }
    }

    /**
     * Convenient method for testing if a period is open
     *
     * @param ctx
     * @param dateAcct
     * @param docBaseType
     * @param AD_Org_ID   Organization
     * @throws PeriodClosedException if period is closed
     * @see #isOpen(Properties, Timestamp, String, int)
     */
    public static void testPeriodOpen(
            Properties ctx, Timestamp dateAcct, String docBaseType, int AD_Org_ID)
            throws PeriodClosedException {
        if (!MPeriod.isOpen(ctx, dateAcct, docBaseType, AD_Org_ID)) {
            throw new PeriodClosedException(dateAcct, docBaseType);
        }
    }

    /**
     * Convenient method for testing if a period is open
     *
     * @param ctx
     * @param dateAcct
     * @param C_DocType_ID
     * @throws PeriodClosedException
     * @see {@link #isOpen(Properties, Timestamp, String)}
     * @deprecated
     */
    public static void testPeriodOpen(Properties ctx, Timestamp dateAcct, int C_DocType_ID)
            throws PeriodClosedException {
        MDocType dt = MDocType.get(ctx, C_DocType_ID);
        testPeriodOpen(ctx, dateAcct, dt.getDocBaseType());
    }

    /**
     * Convenient method for testing if a period is open
     *
     * @param ctx
     * @param dateAcct
     * @param C_DocType_ID
     * @param AD_Org_ID    Organization
     * @throws PeriodClosedException
     * @see {@link #isOpen(Properties, Timestamp, String, int)}
     */
    public static void testPeriodOpen(
            Properties ctx, Timestamp dateAcct, int C_DocType_ID, int AD_Org_ID)
            throws PeriodClosedException {
        MDocType dt = MDocType.get(ctx, C_DocType_ID);
        testPeriodOpen(ctx, dateAcct, dt.getDocBaseType(), AD_Org_ID);
    }

    /**
     * Get Calendar for Organization
     *
     * @param ctx       Context
     * @param AD_Org_ID Organization
     * @return
     */
    public static int getC_Calendar_ID(Properties ctx, int AD_Org_ID) {
        int C_Calendar_ID = 0;
        if (AD_Org_ID != 0) {
            MOrgInfo info = MOrgInfo.get(ctx, AD_Org_ID);
            C_Calendar_ID = info.getCalendarId();
        }

        if (C_Calendar_ID == 0) {
            MClientInfo cInfo = MClientInfo.get(ctx);
            C_Calendar_ID = cInfo.getCalendarId();
        }

        return C_Calendar_ID;
    } //  getCalendarId

    /**
     * Get Period Control
     *
     * @param requery requery
     * @return period controls
     */
    public MPeriodControl[] getPeriodControls(boolean requery) {
        if (m_controls != null && !requery) return m_controls;
        m_controls = MBasePeriodKt.getPeriodControls(getCtx(), getC_Period_ID());
        return m_controls;
    } //	getPeriodControls

    /**
     * Get Period Control
     *
     * @param DocBaseType Document Base Type
     * @return period control or null
     */
    public MPeriodControl getPeriodControl(String DocBaseType) {
        if (DocBaseType == null) return null;
        getPeriodControls(false);
        for (int i = 0; i < m_controls.length; i++) {
            //	log.fine("getPeriodControl - " + 1 + " - " + m_controls[i]);
            if (DocBaseType.equals(m_controls[i].getDocBaseType())) return m_controls[i];
        }
        return null;
    } //	getPeriodControl

    /**
     * Date In Period
     *
     * @param date date
     * @return true if in period
     */
    public boolean isInPeriod(Timestamp date) {
        if (date == null) return false;
        Timestamp dateOnly = TimeUtil.getDay(date);
        Timestamp from = TimeUtil.getDay(getStartDate());
        if (dateOnly.before(from)) return false;
        Timestamp to = TimeUtil.getDay(getEndDate());
        if (dateOnly.after(to)) return false;
        return true;
    } //	isInPeriod

    /**
     * Is Period Open for Doc Base Type
     *
     * @param DocBaseType document base type
     * @return true if open
     * @deprecated since 3.3.1b; use {@link #isOpen(String, Timestamp)} instead
     */
    public boolean isOpen(String DocBaseType) {
        return isOpen(DocBaseType, null);
    }

    /**
     * Is Period Open for Doc Base Type
     *
     * @param DocBaseType document base type
     * @param dateAcct    date; Applies only for "Auto Period Control":
     *                    <li>if not null, date should be in auto period range (today - OpenHistory,
     *                    today+OpenHistory)
     *                    <li>if null, this period should be in auto period range
     * @return true if open
     * @since 3.3.1b
     */
    public boolean isOpen(String DocBaseType, Timestamp dateAcct) {
        if (!isActive()) {
            s_log.warning("Period not active: " + getName());
            return false;
        }

        MAcctSchema as = MClient.get(getCtx(), getClientId()).getAcctSchema();
        if (as != null && as.isAutoPeriodControl()) {
            Timestamp today =
                    TimeUtil.trunc(new Timestamp(System.currentTimeMillis()), TimeUtil.TRUNC_DAY);
            Timestamp first = TimeUtil.addDays(today, -as.getPeriodOpenHistory());
            Timestamp last = TimeUtil.addDays(today, as.getPeriodOpenFuture());
            Timestamp date1, date2;
            if (dateAcct != null) {
                date1 = TimeUtil.trunc(dateAcct, TimeUtil.TRUNC_DAY);
                date2 = date1;
            } else {
                date1 = getStartDate();
                date2 = getEndDate();
            }
            //
            if (date1.before(first)) {
                log.warning("Automatic Period Control:" + date1 + " before first day - " + first);
                return false;
            }
            if (date2.after(last)) {
                log.warning("Automatic Period Control:" + date2 + " after last day - " + last);
                return false;
            }
            //	We are OK
            if (isInPeriod(today)) {
                as.setPeriodId(getC_Period_ID());
                as.saveEx();
            }
            return true;
        }

        //	Standard Period Control
        if (DocBaseType == null) {
            log.warning(getName() + " - No DocBaseType");
            return false;
        }
        MPeriodControl pc = getPeriodControl(DocBaseType);
        if (pc == null) {
            log.warning(getName() + " - Period Control not found for " + DocBaseType);
            return false;
        }
        if (log.isLoggable(Level.FINE)) log.fine(getName() + ": " + DocBaseType);
        return pc.isOpen();
    } //	isOpen

    /**
     * Standard Period
     *
     * @return true if standard calendar periods
     */
    public boolean isStandardPeriod() {
        return X_C_Period.PERIODTYPE_StandardCalendarPeriod.equals(getPeriodType());
    } //	isStandardPeriod

    /**
     * Before Save. Truncate Dates
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        //	Truncate Dates
        Timestamp date = getStartDate();
        if (date != null) setStartDate(TimeUtil.getDay(date));
        else return false;
        //
        date = getEndDate();
        if (date != null) setEndDate(TimeUtil.getDay(date));
        else setEndDate(TimeUtil.getMonthLastDay(getStartDate()));

        if (getEndDate().before(getStartDate())) {
            SimpleDateFormat df = DisplayType.getDateFormat(DisplayType.Date);
            log.saveError("Error", df.format(getEndDate()) + " < " + df.format(getStartDate()));
            return false;
        }

        MYear year = new MYear(getCtx(), getC_Year_ID());

        Query query =
                MTable.get(getCtx(), "C_Period")
                        .createQuery(
                                "C_Year_ID IN (SELECT y.C_Year_ID from C_Year y WHERE"
                                        + "                   y.C_Calendar_ID =?)"
                                        + " AND (? BETWEEN StartDate AND EndDate"
                                        + " OR ? BETWEEN StartDate AND EndDate)"
                                        + " AND PeriodType=?");
        query.setParameters(year.getC_Calendar_ID(), getStartDate(), getEndDate(), getPeriodType());

        List<MPeriod> periods = query.list();

        for (int i = 0; i < periods.size(); i++) {
            if (periods.get(i).getC_Period_ID() != getC_Period_ID()) {
                log.saveError("Error", "Period overlaps with: " + periods.get(i).getName());
                return false;
            }
        }

        return true;
    } //	beforeSave

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return success
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        if (!success) return success;
        if (newRecord) {
            //	SELECT Value FROM AD_Ref_List WHERE AD_Reference_ID=183
            MDocType[] types = MDocType.getOfClient(getCtx());
            int count = 0;
            ArrayList<String> baseTypes = new ArrayList<String>();
            for (int i = 0; i < types.length; i++) {
                MDocType type = types[i];
                String DocBaseType = type.getDocBaseType();
                if (baseTypes.contains(DocBaseType)) continue;
                MPeriodControl pc = new MPeriodControl(this, DocBaseType);
                pc.saveEx();
                count++;
                baseTypes.add(DocBaseType);
            }
            if (log.isLoggable(Level.FINE)) log.fine("PeriodControl #" + count);
        }
        return success;
    } //	afterSave

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MPeriod[");
        sb.append(getId())
                .append("-")
                .append(getName())
                .append(", ")
                .append(getStartDate())
                .append("-")
                .append(getEndDate())
                .append("]");
        return sb.toString();
    } //	toString

    /**
     * Get Calendar of Period
     *
     * @return calendar
     */
    public int getC_Calendar_ID() {
        if (m_C_Calendar_ID == 0) {
            MYear year = (MYear) getC_Year();
            if (year != null) m_C_Calendar_ID = year.getC_Calendar_ID();
            else log.severe("@NotFound@ C_Year_ID=" + getC_Year_ID());
        }
        return m_C_Calendar_ID;
    } //  getCalendarId
} //	MPeriod
