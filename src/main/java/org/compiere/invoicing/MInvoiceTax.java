package org.compiere.invoicing;

import kotliquery.Row;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.I_C_InvoiceTax;
import org.compiere.model.I_C_Tax;
import org.compiere.orm.Query;
import org.compiere.tax.MTax;
import org.idempiere.common.exceptions.DBException;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Invoice Tax Model
 *
 * @author Jorg Janke
 * @author Teo Sarca, www.arhipac.ro
 * <li>FR [ 2214883 ] Remove SQL code and Replace for Query
 * @version $Id: MInvoiceTax.java,v 1.5 2006/10/06 00:42:24 jjanke Exp $
 */
public class MInvoiceTax extends X_C_InvoiceTax implements I_C_InvoiceTax {
    /**
     *
     */
    private static final long serialVersionUID = -5560880305482497098L;
    /**
     * Static Logger
     */
    private static CLogger s_log = CLogger.getCLogger(MInvoiceTax.class);
    /**
     * Tax
     */
    private MTax m_tax = null;
    /**
     * Cached Precision
     */
    private Integer m_precision = null;

    /**
     * ************************************************************************ Persistency
     * Constructor
     *
     * @param ignored ignored
     */
    public MInvoiceTax(int ignored) {
        super(0);
        if (ignored != 0) throw new IllegalArgumentException("Multi-Key");
        setTaxAmt(Env.ZERO);
        setTaxBaseAmt(Env.ZERO);
        setIsTaxIncluded(false);
    } //	MInvoiceTax

    /**
     * Load Constructor. Set Precision and TaxIncluded for tax calculations!
     *
     */
    public MInvoiceTax(Row row) {
        super(row);
    } //	MInvoiceTax

    /**
     * Get Tax Line for Invoice Line
     *
     * @param line      invoice line
     * @param precision currency precision
     * @param oldTax    if true old tax is returned
     * @return existing or new tax
     */
    public static I_C_InvoiceTax get(
            I_C_InvoiceLine line, int precision, boolean oldTax) {
        I_C_InvoiceTax retValue;
        if (line == null || line.getInvoiceId() == 0) return null;
        int C_Tax_ID = line.getTaxId();
        boolean isOldTax =
                oldTax
                        && (line instanceof org.idempiere.orm.PO)
                        && ((org.idempiere.orm.PO) line).isValueChanged(MInvoiceLine.COLUMNNAME_C_Tax_ID);
        if (isOldTax) {
            Object old = ((org.idempiere.orm.PO) line).getValueOld(MInvoiceLine.COLUMNNAME_C_Tax_ID);
            if (old == null) return null;
            C_Tax_ID = (Integer) old;
        }
        if (C_Tax_ID == 0) {
            if (!line.isDescription()) s_log.warning("C_Tax_ID=0");
            return null;
        }

        retValue =
                new Query<I_C_InvoiceTax>(Table_Name, "C_Invoice_ID=? AND C_Tax_ID=?")
                        .setParameters(line.getInvoiceId(), C_Tax_ID)
                        .firstOnly();
        if (retValue != null) {
            retValue.setPrecision(precision);
            if (s_log.isLoggable(Level.FINE)) s_log.fine("(old=" + oldTax + ") " + retValue);
            return retValue;
        }
        // If the old tax was required and there is no MInvoiceTax for that
        // return null, and not create another MInvoiceTax - teo_sarca [ 1583825 ]
        else {
            if (isOldTax) return null;
        }

        //	Create New
        retValue = new MInvoiceTax(0);
        retValue.setClientOrg(line);
        retValue.setInvoiceId(line.getInvoiceId());
        retValue.setTaxId(line.getTaxId());
        retValue.setPrecision(precision);
        retValue.setIsTaxIncluded(line.isTaxIncluded());
        if (s_log.isLoggable(Level.FINE)) s_log.fine("(new) " + retValue);
        return retValue;
    } //	get

    /**
     * Get Precision
     *
     * @return Returns the precision or 2
     */
    private int getPrecision() {
        if (m_precision == null) return 2;
        return m_precision;
    } //	getPrecision

    /**
     * Set Precision
     *
     * @param precision The precision to set.
     */
    public void setPrecision(int precision) {
        m_precision = precision;
    } //	setPrecision

    /**
     * Get Tax
     *
     * @return tax
     */
    public I_C_Tax getTax() {
        if (m_tax == null) m_tax = MTax.get(getTaxId());
        return m_tax;
    } //	getTax

    /**
     * ************************************************************************ Calculate/Set Tax Base
     * Amt from Invoice Lines
     *
     * @return true if tax calculated
     */
    public boolean calculateTaxFromLines() {
        BigDecimal taxBaseAmt = Env.ZERO;
        BigDecimal taxAmt = Env.ZERO;
        //
        boolean documentLevel = getTax().isDocumentLevel();
        I_C_Tax tax = getTax();
        //
        String sql =
                "SELECT il.LineNetAmt, COALESCE(il.TaxAmt,0), i.IsSOTrx "
                        + "FROM C_InvoiceLine il"
                        + " INNER JOIN C_Invoice i ON (il.C_Invoice_ID=i.C_Invoice_ID) "
                        + "WHERE il.C_Invoice_ID=? AND il.C_Tax_ID=?";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getInvoiceId());
            pstmt.setInt(2, getTaxId());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                //	BaseAmt
                BigDecimal baseAmt = rs.getBigDecimal(1);
                taxBaseAmt = taxBaseAmt.add(baseAmt);
                //	TaxAmt
                BigDecimal amt = rs.getBigDecimal(2);
                if (amt == null) amt = Env.ZERO;
                boolean isSOTrx = "Y".equals(rs.getString(3));
                //
                // phib [ 1702807 ]: manual tax should never be amended
                // on line level taxes
                if (documentLevel || amt.signum() == 0 || isSOTrx) // 	manually entered
                {
                    if (documentLevel || baseAmt.signum() == 0) amt = Env.ZERO;
                    else // calculate line tax
                        amt = tax.calculateTax(baseAmt, isTaxIncluded(), getPrecision());
                }
                //
                taxAmt = taxAmt.add(amt);
            }
        } catch (SQLException e) {
            throw new DBException(e);
        }

        //	Calculate Tax
        if (documentLevel || taxAmt.signum() == 0)
            taxAmt = tax.calculateTax(taxBaseAmt, isTaxIncluded(), getPrecision());
        setTaxAmt(taxAmt);

        //	Set Base
        if (isTaxIncluded()) setTaxBaseAmt(taxBaseAmt.subtract(taxAmt));
        else setTaxBaseAmt(taxBaseAmt);
        return true;
    } //	calculateTaxFromLines

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MInvoiceTax[" + "C_Invoice_ID=" +
                getInvoiceId() +
                ",C_Tax_ID=" +
                getTaxId() +
                ", Base=" +
                getTaxBaseAmt() +
                ",Tax=" +
                getTaxAmt() +
                "]";
    } //	toString

    protected void setClientOrg(I_C_Invoice po) {
        super.setClientOrg(po);
    } //	setClientOrg

    @Override
    public void setClientOrg(I_C_InvoiceLine line) {
        super.setClientOrg(line);
    }

    /**
     * Get Tax Provider.
     *
     * @return Tax Provider
     */
    public int getTaxProviderId() {
        Integer ii = getValue(I_C_InvoiceTax.COLUMNNAME_C_TaxProvider_ID);
        if (ii == null) return 0;
        return ii;
    }
} //	MInvoiceTax
