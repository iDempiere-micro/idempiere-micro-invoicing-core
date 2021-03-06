package org.idempiere.process;

import org.compiere.model.IProcessInfoParameter;
import org.compiere.orm.Query;
import org.compiere.process.SvrProcess;
import org.compiere.product.MProductBOM;
import org.idempiere.common.exceptions.AdempiereException;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class UniversalSubstitution extends SvrProcess {

    int productId = 0;
    int replacementId = 0;

    @Override
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (int i = 0; i < para.length; i++) {
            String name = para[i].getParameterName();
            if (name.equals("M_Product_ID")) productId = para[i].getParameterAsInt();
            else if (name.equals("Substitute_ID")) replacementId = para[i].getParameterAsInt();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
    }

    @Override
    protected String doIt() throws SQLException {

        if (productId == 0 || replacementId == 0)
            throw new AdempiereException("Product and replacement product required");

        List<MProductBOM> boms =
                new Query(MProductBOM.Table_Name, "M_ProductBOM_ID=?")
                        .setParameters(productId)
                        .list();

        int count = 0;
        // Use model class to invalidate the product
        for (MProductBOM bom : boms) {
            bom.setBOMProductId(replacementId);
            bom.saveEx();
            count++;
        }
        StringBuilder msgreturn = new StringBuilder().append(count).append(" BOM products updated");
        return msgreturn.toString();
    }
}
