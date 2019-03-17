package org.compiere.production;

import kotliquery.Row;

import java.util.Properties;

public class MQualityTest extends X_M_QualityTest {

    /**
     *
     */
    private static final long serialVersionUID = -8585270006299484402L;

    public MQualityTest(Properties ctx, int M_QualityTest_ID) {
        super(ctx, M_QualityTest_ID);
    }

    public MQualityTest(Properties ctx, Row row) {
        super(ctx, row);
    }

    public MQualityTestResult createResult(int m_attributesetinstance_id) {
        MQualityTestResult result = new MQualityTestResult(getCtx(), 0);
        result.setClientOrg(this);
        result.setQualityTestId(getQualityTestId());
        result.setAttributeSetInstanceId(m_attributesetinstance_id);
        result.setProcessed(false);
        result.setIsQCPass(false);
        result.saveEx();
        return result;
    }
}
