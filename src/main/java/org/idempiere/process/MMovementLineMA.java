package org.idempiere.process;

import kotliquery.Row;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.model.I_M_Movement;
import org.compiere.model.I_M_MovementLine;
import org.compiere.model.I_M_MovementLineMA;
import org.compiere.orm.Query;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.getSQLValueBD;

public class MMovementLineMA extends X_M_MovementLineMA {
    /**
     *
     */
    private static final long serialVersionUID = -155379485409000271L;

    /**
     * ************************************************************************ Standard Constructor
     *
     * @param ctx                 context
     * @param M_MovementLineMA_ID ignored
     */
    public MMovementLineMA(int M_MovementLineMA_ID) {
        super(M_MovementLineMA_ID);
        if (M_MovementLineMA_ID != 0) throw new IllegalArgumentException("Multi-Key");
    } //	MMovementLineMA

    /**
     * Load Constructor
     *
     * @param ctx context
     */
    public MMovementLineMA(Row row) {
        super(row);
    } //	MMovementLineMA

    /**
     * Parent Constructor
     *
     * @param parent                    parent
     * @param M_AttributeSetInstance_ID asi
     * @param MovementQty               qty
     * @param DateMaterialPolicy
     */
    public MMovementLineMA(
            MMovementLine parent,
            int M_AttributeSetInstance_ID,
            BigDecimal MovementQty,
            Timestamp DateMaterialPolicy) {
        this(parent, M_AttributeSetInstance_ID, MovementQty, DateMaterialPolicy, true);
    }

    /**
     * @param parent
     * @param M_AttributeSetInstance_ID
     * @param MovementQty
     * @param DateMaterialPolicy
     * @param isAutoGenerated
     */
    public MMovementLineMA(
            MMovementLine parent,
            int M_AttributeSetInstance_ID,
            BigDecimal MovementQty,
            Timestamp DateMaterialPolicy,
            boolean isAutoGenerated) {
        this(0);
        setClientOrg(parent);
        setMovementLineId(parent.getMovementLineId());
        //
        setAttributeSetInstanceId(M_AttributeSetInstance_ID);
        setMovementQty(MovementQty);
        if (DateMaterialPolicy == null) {
            if (M_AttributeSetInstance_ID > 0) {
                DateMaterialPolicy =
                        MStorageOnHand.getDateMaterialPolicy(
                                parent.getProductId(), M_AttributeSetInstance_ID);
            }
            if (DateMaterialPolicy == null) {
                DateMaterialPolicy = parent.getParent().getMovementDate();
            }
        }
        setDateMaterialPolicy(DateMaterialPolicy);
        setIsAutoGenerated(isAutoGenerated);
    } //	MMovementLineMA

    /**
     * Get Material Allocations for Line
     *
     * @param ctx               context
     * @param M_MovementLine_ID line
     * @return allocations
     */
    public static MMovementLineMA[] get(int M_MovementLine_ID) {
        return MBaseMovementLineMAKt.getMaterialAllocationsForLine(M_MovementLine_ID);
    } //	get

    /**
     * Delete all Material Allocation for Movement Line
     *
     * @param M_MovementLine_ID movement line
     * @param trxName           transaction
     * @return number of rows deleted or -1 for error
     */
    public static int deleteMovementLineMA(int M_MovementLine_ID) {
        String sql = "DELETE FROM M_MovementLineMA WHERE M_MovementLine_ID=? AND IsAutoGenerated='Y' ";
        return executeUpdate(sql, M_MovementLine_ID);
    } //	deleteMovementLineMA

    public static MMovementLineMA addOrCreate(
            MMovementLine line,
            int M_AttributeSetInstance_ID,
            BigDecimal MovementQty,
            Timestamp DateMaterialPolicy,
            boolean isAutoGenerated) {
        Query query =
                new Query(
                        I_M_MovementLineMA.Table_Name,
                        "M_MovementLine_ID=? AND M_AttributeSetInstance_ID=? AND DateMaterialPolicy=trunc(cast(? as date))"
                );
        MMovementLineMA po =
                query
                        .setParameters(
                                line.getMovementLineId(), M_AttributeSetInstance_ID, DateMaterialPolicy)
                        .first();
        if (po == null)
            po =
                    new MMovementLineMA(
                            line, M_AttributeSetInstance_ID, MovementQty, DateMaterialPolicy, isAutoGenerated);
        else po.setMovementQty(po.getMovementQty().add(MovementQty));
        return po;
    }

    /**
     * @param M_MovementLine_ID
     * @param trxName
     * @return
     */
    public static BigDecimal getManualQty(int M_MovementLine_ID) {
        String sql =
                "SELECT SUM(movementqty) FROM M_MovementLineMA ma WHERE ma.M_MovementLine_ID=?  AND ma.IsAutoGenerated='N'";
        BigDecimal totalQty = getSQLValueBD(sql, M_MovementLine_ID);
        return totalQty == null ? Env.ZERO : totalQty;
    } // totalLineQty

    @Override
    public void setDateMaterialPolicy(Timestamp DateMaterialPolicy) {
        if (DateMaterialPolicy != null) DateMaterialPolicy = Util.removeTime(DateMaterialPolicy);
        super.setDateMaterialPolicy(DateMaterialPolicy);
    }

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MMovementLineMA[");
        sb.append("M_MovementLine_ID=")
                .append(getMovementLineId())
                .append(",M_AttributeSetInstance_ID=")
                .append(getAttributeSetInstanceId())
                .append(", Qty=")
                .append(getMovementQty())
                .append("]");
        return sb.toString();
    } //	toString

    /**
     * ************************************************************************ Before Save
     *
     * @param newRecord new
     * @return save
     */
    protected boolean beforeSave(boolean newRecord) {
        // Set DateMaterialPolicy
        if (!newRecord && isValueChanged(COLUMNNAME_M_AttributeSetInstance_ID)) {
            I_M_MovementLine line = getMovementLine();

            Timestamp dateMPolicy = null;
            if (getAttributeSetInstanceId() > 0) {
                dateMPolicy =
                        MStorageOnHand.getDateMaterialPolicy(
                                line.getProductId(), getAttributeSetInstanceId());
            }

            if (dateMPolicy == null) {
                I_M_Movement movement = line.getMovement();
                dateMPolicy = movement.getMovementDate();
            }

            setDateMaterialPolicy(dateMPolicy);
        }

        return true;
    } // beforeSave
} //	MMovementLineMA
