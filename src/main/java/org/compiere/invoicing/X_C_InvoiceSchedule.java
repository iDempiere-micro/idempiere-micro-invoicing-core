package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_InvoiceSchedule;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

public class X_C_InvoiceSchedule extends PO implements I_C_InvoiceSchedule {

    /**
     * Daily = D
     */
    public static final String INVOICEFREQUENCY_Daily = "D";
    /**
     * Weekly = W
     */
    public static final String INVOICEFREQUENCY_Weekly = "W";
    /**
     * Monthly = M
     */
    public static final String INVOICEFREQUENCY_Monthly = "M";
    /**
     * Twice Monthly = T
     */
    public static final String INVOICEFREQUENCY_TwiceMonthly = "T";
    /**
     * Sunday = 7
     */
    public static final String INVOICEWEEKDAY_Sunday = "7";
    /**
     * Monday = 1
     */
    public static final String INVOICEWEEKDAY_Monday = "1";
    /**
     * Tuesday = 2
     */
    public static final String INVOICEWEEKDAY_Tuesday = "2";
    /**
     * Wednesday = 3
     */
    public static final String INVOICEWEEKDAY_Wednesday = "3";
    /**
     * Friday = 5
     */
    public static final String INVOICEWEEKDAY_Friday = "5";
    /**
     * Saturday = 6
     */
    public static final String INVOICEWEEKDAY_Saturday = "6";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_InvoiceSchedule(int C_InvoiceSchedule_ID) {
        super(C_InvoiceSchedule_ID);
        /*
         * if (C_InvoiceSchedule_ID == 0) { setAmt (Env.ZERO); setInvoiceSchedule_ID (0);
         * setInvoiceDay (0); // 1 setInvoiceFrequency (null); setInvoiceWeekDay (null); setIsAmount
         * (false); setIsDefault (false); setName (null); }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_InvoiceSchedule(Row row) {
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
        return "X_C_InvoiceSchedule[" + getId() + "]";
    }

    /**
     * Get Amount.
     *
     * @return Amount
     */
    public BigDecimal getAmt() {
        BigDecimal bd = getValue(COLUMNNAME_Amt);
        if (bd == null) return Env.ZERO;
        return bd;
    }

    /**
     * Get Invoice Day.
     *
     * @return Day of Invoice Generation
     */
    public int getInvoiceDay() {
        Integer ii = getValue(COLUMNNAME_InvoiceDay);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Invoice day cut-off.
     *
     * @return Last day for including shipments
     */
    public int getInvoiceDayCutoff() {
        Integer ii = getValue(COLUMNNAME_InvoiceDayCutoff);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Invoice Frequency.
     *
     * @return How often invoices will be generated
     */
    public String getInvoiceFrequency() {
        return getValue(COLUMNNAME_InvoiceFrequency);
    }

    /**
     * Get Invoice Week Day.
     *
     * @return Day to generate invoices
     */
    public String getInvoiceWeekDay() {
        return getValue(COLUMNNAME_InvoiceWeekDay);
    }

    /**
     * Get Invoice weekday cutoff.
     *
     * @return Last day in the week for shipments to be included
     */
    public String getInvoiceWeekDayCutoff() {
        return getValue(COLUMNNAME_InvoiceWeekDayCutoff);
    }

    /**
     * Get Amount Limit.
     *
     * @return Send invoices only if the amount exceeds the limit
     */
    public boolean isAmount() {
        Object oo = getValue(COLUMNNAME_IsAmount);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    @Override
    public int getTableId() {
        return I_C_InvoiceSchedule.Table_ID;
    }
}
