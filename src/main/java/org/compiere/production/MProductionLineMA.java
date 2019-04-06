package org.compiere.production;

import kotliquery.Row;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.model.I_M_ProductionLineMA;
import org.compiere.orm.MTable;
import org.compiere.orm.Query;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class MProductionLineMA extends X_M_ProductionLineMA {
    /**
     *
     */
    private static final long serialVersionUID = -3935841562652510880L;

    public MProductionLineMA(int M_ProductionLineMA_ID) {
        super(M_ProductionLineMA_ID);
    }

    public MProductionLineMA(Row row) {
        super(row);
    }

    /**
     * Parent constructor
     *
     * @param parent
     * @param asi
     * @param qty
     * @param dateMaterialPolicy
     */
    public MProductionLineMA(
            MProductionLine parent, int asi, BigDecimal qty, Timestamp dateMaterialPolicy) {
        super(0);
        setAttributeSetInstanceId(asi);
        setProductionLineId(parent.getId());
        setMovementQty(qty);
        setOrgId(parent.getOrgId());
        if (dateMaterialPolicy == null) {
            if (asi > 0) {
                dateMaterialPolicy =
                        MStorageOnHand.getDateMaterialPolicy(
                                parent.getProductId(), asi);
            }
            if (dateMaterialPolicy == null) {
                dateMaterialPolicy = parent.getProduction().getMovementDate();
            }
        }
        setDateMaterialPolicy(dateMaterialPolicy);
    }

    public static MProductionLineMA get(MProductionLine parent, int asi, Timestamp dateMPolicy) {
        String where = " M_ProductionLine_ID = ? AND M_AttributeSetInstance_ID = ? ";
        if (dateMPolicy == null) {
            dateMPolicy = new Timestamp(new Date().getTime());
        }
        where = where + "AND DateMaterialPolicy = trunc(cast(? as date))";

        MProductionLineMA lineMA =
                MTable.get(I_M_ProductionLineMA.Table_Name)
                        .createQuery(where)
                        .setParameters(parent.getProductionLineId(), asi, dateMPolicy)
                        .first();

        if (lineMA != null) return lineMA;
        else return new MProductionLineMA(parent, asi, Env.ZERO, dateMPolicy);
    }

    /**
     * Get Material Allocations for Line
     *
     * @param ctx                 context
     * @param M_ProductionLine_ID line
     * @param trxName             trx
     * @return allocations
     */
    public static MProductionLineMA[] get(int M_ProductionLine_ID) {

        Query query =
                MTable.get(I_M_ProductionLineMA.Table_Name)
                        .createQuery(I_M_ProductionLineMA.COLUMNNAME_M_ProductionLine_ID + "=?");
        query.setParameters(M_ProductionLine_ID);
        List<MProductionLineMA> list = query.list();
        MProductionLineMA[] retValue = list.toArray(new MProductionLineMA[0]);
        return retValue;
    } //	get

    @Override
    public void setDateMaterialPolicy(Timestamp DateMaterialPolicy) {
        if (DateMaterialPolicy != null) DateMaterialPolicy = Util.removeTime(DateMaterialPolicy);
        super.setDateMaterialPolicy(DateMaterialPolicy);
    }
}
