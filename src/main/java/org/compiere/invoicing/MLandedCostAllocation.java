package org.compiere.invoicing;

import kotliquery.Row;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;

/**
 * Landed Cost Allocation Model
 *
 * @author Jorg Janke
 * @version $Id: MLandedCostAllocation.java,v 1.3 2006/07/30 00:51:05 jjanke Exp $
 */
public class MLandedCostAllocation extends X_C_LandedCostAllocation {
    /**
     *
     */
    private static final long serialVersionUID = -8645283018475474574L;

    /**
     * ************************************************************************* Standard Constructor
     *
     * @param ctx                       context
     * @param C_LandedCostAllocation_ID id
     */
    public MLandedCostAllocation(int C_LandedCostAllocation_ID) {
        super(C_LandedCostAllocation_ID);
        if (C_LandedCostAllocation_ID == 0) {
            //	setCostElementId(0);
            setAmt(Env.ZERO);
            setQty(Env.ZERO);
            setBase(Env.ZERO);
        }
    } //	MLandedCostAllocation

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MLandedCostAllocation(Row row) {
        super(row);
    } //	MLandedCostAllocation

    /**
     * Parent Constructor
     *
     * @param parent           parent
     * @param M_CostElement_ID cost element
     */
    public MLandedCostAllocation(MInvoiceLine parent, int M_CostElement_ID) {
        this(0);
        setClientOrg(parent);
        setInvoiceLineId(parent.getInvoiceLineId());
        setCostElementId(M_CostElement_ID);
    } //	MLandedCostAllocation

    /**
     * Get Cost Allocations for invoice Line
     *
     * @param ctx              context
     * @param C_InvoiceLine_ID invoice line
     * @return landed cost alloc
     */
    public static MLandedCostAllocation[] getOfInvoiceLine(
            int C_InvoiceLine_ID) {
        return MBaseLandedCostAllocationKt.getCostAllocationsforInvoiceLine(C_InvoiceLine_ID);
    } //	getOfInvliceLine

    /**
     * Set Amt
     *
     * @param Amt       amount
     * @param precision precision
     */
    public void setAmt(double Amt, int precision) {
        BigDecimal bd = BigDecimal.valueOf(Amt);
        if (bd.scale() > precision) bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
        super.setAmt(bd);
    } //	setAmt

    /**
     * Set Allocation Qty (e.g. free products)
     *
     * @param Qty
     */
    public void setQty(BigDecimal Qty) {
        super.setQty(Qty);
    } //	setQty
} //	MLandedCostAllocation
