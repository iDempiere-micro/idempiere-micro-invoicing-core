/**
 * ********************************************************************* This file is part of
 * Adempiere ERP Bazaar * http://www.adempiere.org * * This program is free software; you can
 * redistribute it and/or * modify it under the terms of the GNU General Public License * as
 * published by the Free Software Foundation; either version 2 * of the License, or (at your option)
 * any later version. * * This program is distributed in the hope that it will be useful, * but
 * WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the * GNU General Public License for more details. * * You should have
 * received a copy of the GNU General Public License * along with this program; if not, write to the
 * Free Software * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, * MA 02110-1301, USA.
 * * * Contributors: * - Daniel Tamm - usrdno * * Sponsors: * - Company (http://www.notima.se) * -
 * Company (http://www.cyberphoto.se) *
 * ********************************************************************
 */
package org.idempiere.process;

import org.compiere.accounting.MAccount;
import org.compiere.accounting.MElementValue;
import org.compiere.accounting.MProduct;
import org.compiere.model.AccountingElementValue;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_M_PriceList_Version;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_Product_Acct;
import org.compiere.orm.Query;
import org.compiere.process.SvrProcess;
import org.compiere.product.MPriceList;
import org.compiere.product.MProductPrice;
import org.idempiere.common.util.Env;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * Creates expense type products from a given range of expense account elements. With all expense
 * accounts from the chart of accounts added as expense type products, every vendor invoice can be
 * registered without having to register additional products. FR 2619262
 *
 * @author Daniel Tamm
 */
public class ExpenseTypesFromAccounts extends SvrProcess {

    private int m_clientId;
    private int m_acctSchemaId;
    private int m_priceListId;
    private String m_productValuePrefix = "";
    private String m_productValueSuffix = "";
    private String m_startElement;
    private String m_endElement;
    private int m_productCategoryId;
    private int m_taxCategoryId;
    private int m_uomId;

    @Override
    protected void prepare() {

        // Get parameters
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();
            if (iProcessInfoParameter.getParameter() != null) {
                switch (name) {
                    case "M_Product_Category_ID":
                        m_productCategoryId = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "C_AcctSchema_ID":
                        m_acctSchemaId = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "M_PriceList_ID":
                        m_priceListId = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "C_UOM_ID":
                        m_uomId = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "C_TaxCategory_ID":
                        m_taxCategoryId = iProcessInfoParameter.getParameterAsInt();
                        break;
                    case "ProductValuePrefix":
                        m_productValuePrefix = iProcessInfoParameter.getParameter().toString();
                        break;
                    case "ProductValueSuffix":
                        m_productValueSuffix = iProcessInfoParameter.getParameter().toString();
                        break;
                    case "StartElement":
                        m_startElement = iProcessInfoParameter.getParameter().toString();
                        break;
                    case "EndElement":
                        m_endElement = iProcessInfoParameter.getParameter().toString();
                        break;
                    default:
                        log.log(Level.SEVERE, "Unknown Parameter: " + name);
                        break;
                }
            }
        }
    }

    @Override
    protected String doIt() throws Exception {

        // Fetch price list
        MPriceList priceList = new MPriceList(m_priceListId);
        // Get current client id from price list since I for some reason can't read it from
        // context.
        m_clientId = priceList.getClientId();

        // Get active price list version
        I_M_PriceList_Version pv = priceList.getPriceListVersion(null);
        if (pv == null)
            throw new Exception("Pricelist " + priceList.getName() + " has no default version.");

        I_M_Product product;

        // Read all existing applicable products into memory for quick comparison.
        List<I_M_Product> products =
                new Query<I_M_Product>(I_M_Product.Table_Name, "ProductType=?")
                        .setParameters(MProduct.PRODUCTTYPE_ExpenseType)
                        .list();

        Map<String, I_M_Product> productMap = new TreeMap<>();
        for (I_M_Product prod : products) {
            product = prod;
            productMap.put(product.getSearchKey(), product);
        }

        // Read all existing valid combinations comparison
        I_C_ValidCombination validComb;
        List<I_C_ValidCombination> validCombs =
                new Query<I_C_ValidCombination>(
                        I_C_ValidCombination.Table_Name,
                        "C_AcctSchema_ID=? and AD_Client_ID=? and orgId=0"
                )
                        .setParameters(m_acctSchemaId, m_clientId)
                        .list();

        Map<Integer, I_C_ValidCombination> validCombMap = new TreeMap<>();
        for (I_C_ValidCombination comb : validCombs) {
            validComb = comb;
            validCombMap.put(validComb.getAccountId(), validComb);
        }

        // Read all accounttypes that fit the given criteria.
        List<AccountingElementValue> result =
                new Query<AccountingElementValue>(
                        AccountingElementValue.Table_Name,
                        "AccountType=? and isSummary='N' and Value>=? and Value<=? and AD_Client_ID=?"
                )
                        .setParameters(
                                MElementValue.ACCOUNTTYPE_Expense, m_startElement, m_endElement, m_clientId)
                        .list();

        AccountingElementValue elem;
        MProductPrice priceRec;
        I_M_Product_Acct productAcct;
        String expenseItemValue;
        BigDecimal zero = Env.ZERO;
        int addCount = 0;
        int skipCount = 0;

        for (AccountingElementValue i_c_elementValue : result) {
            elem = i_c_elementValue;
            expenseItemValue = m_productValuePrefix + elem.getSearchKey() + m_productValueSuffix;
            // See if a product with this key already exists
            product = productMap.get(expenseItemValue);
            if (product == null) {
                // Create a new product from the account element
                product = new MProduct(0);
                product.setClientId(m_clientId);
                product.setSearchKey(expenseItemValue);
                product.setName(elem.getName());
                product.setDescription(elem.getDescription());
                product.setIsActive(true);
                product.setProductType(MProduct.PRODUCTTYPE_ExpenseType);
                product.setProductCategoryId(m_productCategoryId);
                product.setUOMId(m_uomId);
                product.setTaxCategoryId(m_taxCategoryId);
                product.setIsStocked(false);
                product.setIsPurchased(true);
                product.setIsSold(false);
                // Save the product
                product.saveEx();

                // Add a zero product price to the price list so it shows up in the price list
                priceRec = new MProductPrice(pv.getId(), product.getId());
                priceRec.setValueOfColumn("AD_Client_ID", m_clientId);
                priceRec.setPrices(zero, zero, zero);
                priceRec.saveEx();

                // Set the revenue and expense accounting of the product to the given account element
                // Get the valid combination
                validComb = validCombMap.get(elem.getElementValueId());
                if (validComb == null) {
                    // Create new valid combination
                    validComb = new MAccount(0);
                    validComb.setClientId(m_clientId);
                    validComb.setOrgId(0);
                    validComb.setAlias(elem.getSearchKey());
                    validComb.setAccountId(elem.getId());
                    validComb.setAccountingSchemaId(m_acctSchemaId);
                    validComb.saveEx();
                }

                // TODO: It might be needed to make the accounting more specific, but the purpose
                // of the process now is to create general accounts so this is intentional.
                productAcct =
                        new Query<I_M_Product_Acct>(
                                I_M_Product_Acct.Table_Name,
                                "M_Product_ID=? and C_AcctSchema_ID=?"
                        )
                                .setParameters(product.getId(), m_acctSchemaId)
                                .first();
                productAcct.setProductExpenseAccount(validComb.getId());
                productAcct.setRevenueAccount(validComb.getId());
                productAcct.saveEx();

                addCount++;
            } else {
                skipCount++;
            }
        }

        StringBuilder returnStr = new StringBuilder().append(addCount).append(" products added.");
        if (skipCount > 0) returnStr.append(" ").append(skipCount).append(" products skipped.");
        return (returnStr.toString());
    }
}
