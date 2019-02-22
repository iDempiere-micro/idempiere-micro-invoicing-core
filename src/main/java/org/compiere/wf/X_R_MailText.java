package org.compiere.wf;

import org.compiere.model.I_R_MailText;
import org.compiere.orm.BasePOName;
import org.idempiere.orm.I_Persistent;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Generated Model for R_MailText
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_R_MailText extends BasePOName implements I_R_MailText, I_Persistent {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_R_MailText(Properties ctx, int R_MailText_ID) {
        super(ctx, R_MailText_ID);
    }

    /**
     * Load Constructor
     */
    public X_R_MailText(Properties ctx, ResultSet rs) {
        super(ctx, rs);
    }

    /**
     * AccessLevel
     *
     * @return 7 - System - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("X_R_MailText[").append(getId()).append("]");
        return sb.toString();
    }

    /**
     * Get HTML.
     *
     * @return Text has HTML tags
     */
    public boolean isHtml() {
        Object oo = get_Value(COLUMNNAME_IsHtml);
        if (oo != null) {
            if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Get Subject.
     *
     * @return Mail Header (Subject)
     */
    public String getMailHeader() {
        return (String) get_Value(COLUMNNAME_MailHeader);
    }

    /**
     * Get Mail Text.
     *
     * @return Text used for Mail message
     */
    public String getMailText() {
        return (String) get_Value(COLUMNNAME_MailText);
    }

    /**
     * Get Mail Text 2.
     *
     * @return Optional second text part used for Mail message
     */
    public String getMailText2() {
        return (String) get_Value(COLUMNNAME_MailText2);
    }

    /**
     * Get Mail Text 3.
     *
     * @return Optional third text part used for Mail message
     */
    public String getMailText3() {
        return (String) get_Value(COLUMNNAME_MailText3);
    }

    /**
     * Get Mail Template.
     *
     * @return Text templates for mailings
     */
    public int getR_MailText_ID() {
        Integer ii = (Integer) get_Value(COLUMNNAME_R_MailText_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_R_MailText.Table_ID;
    }
}
