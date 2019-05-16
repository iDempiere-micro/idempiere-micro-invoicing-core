package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.I_M_QualityTest;
import org.compiere.orm.BasePOName;

/**
 * Generated Model for M_QualityTest
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_M_QualityTest extends BasePOName implements I_M_QualityTest {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_M_QualityTest(int M_QualityTest_ID) {
        super(M_QualityTest_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_QualityTest(Row row) {
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
        return "X_M_QualityTest[" + getId() + "]";
    }

    /**
     * Get Quality Test.
     *
     * @return Quality Test
     */
    public int getQualityTestId() {
        Integer ii = getValue(COLUMNNAME_M_QualityTest_ID);
        if (ii == null) return 0;
        return ii;
    }

    @Override
    public int getTableId() {
        return I_M_QualityTest.Table_ID;
    }
}
