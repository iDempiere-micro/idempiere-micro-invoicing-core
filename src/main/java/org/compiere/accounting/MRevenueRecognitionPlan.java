package org.compiere.accounting;

import kotliquery.Row;
import org.idempiere.common.util.Env;

import java.util.Properties;

/**
 * Revenue Recognition Plan
 *
 * @author Jorg Janke
 * @version $Id: MRevenueRecognitionPlan.java,v 1.2 2006/07/30 00:51:05 jjanke Exp $
 */
public class MRevenueRecognitionPlan extends X_C_RevenueRecognition_Plan {
    /**
     *
     */
    private static final long serialVersionUID = -8437258098744762898L;

    /**
     * Standard Constructor
     *
     * @param ctx                          context
     * @param C_RevenueRecognition_Plan_ID id
     */
    public MRevenueRecognitionPlan(Properties ctx, int C_RevenueRecognition_Plan_ID) {
        super(ctx, C_RevenueRecognition_Plan_ID);
        if (C_RevenueRecognition_Plan_ID == 0) {
            setTotalAmt(Env.ZERO);
            setRecognizedAmt(Env.ZERO);
        }
    } //	MRevenueRecognitionPlan

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MRevenueRecognitionPlan(Properties ctx, Row row) {
        super(ctx, row);
    } //	MRevenueRecognitionPlan


} //	MRevenueRecognitionPlan
