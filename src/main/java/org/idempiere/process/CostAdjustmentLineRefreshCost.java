package org.idempiere.process;

import org.compiere.accounting.MClientKt;
import org.compiere.accounting.MProduct;
import org.compiere.invoicing.MInventoryLine;
import org.compiere.model.AccountingSchema;
import org.compiere.model.ClientWithAccounting;
import org.compiere.model.I_M_Cost;
import org.compiere.process.SvrProcess;

import java.math.BigDecimal;

/**
 * @author hengsin
 */
public class CostAdjustmentLineRefreshCost extends SvrProcess {

    /* (non-Javadoc)
     * @see org.compiere.process.SvrProcess#prepare()
     */
    @Override
    protected void prepare() {
    }

    /* (non-Javadoc)
     * @see org.compiere.process.SvrProcess#doIt()
     */
    @Override
    protected String doIt() throws Exception {
        MInventoryLine line = new MInventoryLine(getRecordId());
        MProduct product = line.getProduct();
        ClientWithAccounting client = MClientKt.getClientWithAccounting(line.getClientId());
        AccountingSchema as = client.getAcctSchema();
        I_M_Cost cost =
                product.getCostingRecord(
                        as,
                        line.getOrgId(),
                        line.getAttributeSetInstanceId(),
                        line.getInventory().getCostingMethod());
        if (cost != null) {
            line.setCurrentCostPrice(cost.getCurrentCostPrice());
            line.setNewCostPrice(cost.getCurrentCostPrice());
            line.saveEx();
        } else {
            line.setCurrentCostPrice(BigDecimal.ZERO);
            line.setNewCostPrice(BigDecimal.ZERO);
            line.saveEx();
        }

        return "@Ok@";
    }
}
