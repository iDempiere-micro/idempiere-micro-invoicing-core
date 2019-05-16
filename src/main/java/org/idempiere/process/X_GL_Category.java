package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_GL_Category;
import org.compiere.orm.BasePOName;

public class X_GL_Category extends BasePOName implements I_GL_Category {

    /**
     * Manual = M
     */
    public static final String CATEGORYTYPE_Manual = "M";
    /**
     * Document = D
     */
    public static final String CATEGORYTYPE_Document = "D";
    /**
     * System generated = S
     */
    public static final String CATEGORYTYPE_SystemGenerated = "S";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_GL_Category(int GL_Category_ID) {
        super(GL_Category_ID);
    }


    /**
     * Load Constructor
     */
    public X_GL_Category(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_GL_Category[" + getId() + "]";
    }

    /**
     * Get Category Type.
     *
     * @return Source of the Journal with this category
     */
    public String getCategoryType() {
        return getValue(COLUMNNAME_CategoryType);
    }

    /**
     * Set Category Type.
     *
     * @param CategoryType Source of the Journal with this category
     */
    public void setCategoryType(String CategoryType) {

        setValue(COLUMNNAME_CategoryType, CategoryType);
    }

    /**
     * Get GL Category.
     *
     * @return General Ledger Category
     */
    public int getGLCategoryId() {
        Integer ii = getValue(COLUMNNAME_GL_Category_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Default.
     *
     * @param IsDefault Default value
     */
    public void setIsDefault(boolean IsDefault) {
        setValue(COLUMNNAME_IsDefault, Boolean.valueOf(IsDefault));
    }

    /**
     * Get Default.
     *
     * @return Default value
     */
    public boolean isDefault() {
        Object oo = getValue(COLUMNNAME_IsDefault);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }
}
