package org.compiere.accounting;

import org.compiere.bo.MCurrency;
import org.compiere.conversionrate.MConversionType;
import org.compiere.model.HasName;
import org.compiere.util.DisplayType;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;

import java.sql.Timestamp;
import java.text.DateFormat;

import static software.hsharp.core.util.DBKt.getSQLValueString;

/**
 * Any exception that occurs when no currency conversion rate was found
 *
 * @author Teo Sarca, http://www.arhipac.ro
 */
public class NoCurrencyConversionException extends AdempiereException {

    /**
     *
     */
    private static final long serialVersionUID = 1593966161685137709L;

    /**
     * @param currencyId
     * @param currencyToId
     * @param conversionDate
     * @param conversionTypeId
     */
    public NoCurrencyConversionException(
            int currencyId,
            int currencyToId,
            Timestamp conversionDate,
            int conversionTypeId) {
        super(
                buildMessage(
                        currencyId,
                        currencyToId,
                        conversionDate,
                        conversionTypeId
                ));
    }

    private static String buildMessage(
            int currencyId,
            int currencyToId,
            Timestamp conversionDate,
            int conversionTypeId) {
        DateFormat df = DisplayType.getDateFormat(DisplayType.Date);

        StringBuffer sb =
                new StringBuffer("@NoCurrencyConversion@ ")
                        .append(MCurrency.getISOCode(Env.getCtx(), currencyId))
                        .append("->")
                        .append(MCurrency.getISOCode(Env.getCtx(), currencyToId));
        //
        sb.append(", @Date@: ");
        if (conversionDate != null) sb.append(df.format(conversionDate));
        else sb.append("*");
        //
        sb.append(", @C_ConversionType_ID@: ");
        if (conversionTypeId > 0) {
            final String sql =
                    "SELECT "
                            + HasName.Companion.getCOLUMNNAME_Name()
                            + " FROM "
                            + MConversionType.Table_Name
                            + " WHERE "
                            + MConversionType.COLUMNNAME_C_ConversionType_ID
                            + "=?";
            String name = getSQLValueString(sql, conversionTypeId);
            sb.append(name);
        } else {
            sb.append("*");
        }
        return sb.toString();
    }
}
