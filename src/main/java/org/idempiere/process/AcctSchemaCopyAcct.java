package org.idempiere.process;

import org.compiere.accounting.MAccount;
import org.compiere.accounting.MAcctSchema;
import org.compiere.accounting.MAcctSchemaDefault;
import org.compiere.accounting.MAcctSchemaElement;
import org.compiere.accounting.MAcctSchemaGL;
import org.compiere.model.AccountSchemaElement;
import org.compiere.model.DefaultAccountsForSchema;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_AcctSchema_GL;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereSystemError;
import org.idempiere.common.util.AdempiereUserError;
import org.idempiere.common.util.KeyNamePair;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Copy Accounts from one Acct Schema to another
 *
 * @author Jorg Janke
 * @version $Id: AcctSchemaCopyAcct.java,v 1.3 2006/07/30 00:51:01 jjanke Exp $
 */
public class AcctSchemaCopyAcct extends SvrProcess {
    private int p_SourceAcctSchema_ID = 0;
    private int p_TargetAcctSchema_ID = 0;

    /**
     * Prepare
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("C_AcctSchema_ID")) p_SourceAcctSchema_ID = iProcessInfoParameter.getParameterAsInt();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_TargetAcctSchema_ID = getRecordId();
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info(
                    "SourceAcctSchema_ID="
                            + p_SourceAcctSchema_ID
                            + ", TargetAcctSchema_ID="
                            + p_TargetAcctSchema_ID);

        if (p_SourceAcctSchema_ID == 0 || p_TargetAcctSchema_ID == 0)
            throw new AdempiereSystemError("ID=0");

        if (p_SourceAcctSchema_ID == p_TargetAcctSchema_ID)
            throw new AdempiereUserError("Must be different");

        MAcctSchema source = MAcctSchema.get(p_SourceAcctSchema_ID);
        if (source.getId() == 0)
            throw new AdempiereSystemError("NotFound Source C_AcctSchema_ID=" + p_SourceAcctSchema_ID);
        MAcctSchema target = new MAcctSchema(p_TargetAcctSchema_ID);
        if (target.getId() == 0)
            throw new AdempiereSystemError("NotFound Target C_AcctSchema_ID=" + p_TargetAcctSchema_ID);

        //
        AccountSchemaElement[] targetElements = target.getAcctSchemaElements();
        if (targetElements.length == 0)
            throw new AdempiereUserError("NotFound Target C_AcctSchema_Element");

        //	Accounting Element must be the same
        AccountSchemaElement sourceAcctElement =
                source.getAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_Account);
        if (sourceAcctElement == null)
            throw new AdempiereUserError("NotFound Source AC C_AcctSchema_Element");
        AccountSchemaElement targetAcctElement =
                target.getAcctSchemaElement(MAcctSchemaElement.ELEMENTTYPE_Account);
        if (targetAcctElement == null)
            throw new AdempiereUserError("NotFound Target AC C_AcctSchema_Element");
        if (sourceAcctElement.getElementId() != targetAcctElement.getElementId())
            throw new AdempiereUserError("@C_Element_ID@ different");

        if (MAcctSchemaGL.get(p_TargetAcctSchema_ID) == null) copyGL(target);
        if (MAcctSchemaDefault.get(p_TargetAcctSchema_ID) == null) copyDefault(target);

        return "@OK@";
    } //	doIt

    /**
     * Copy GL
     *
     * @param targetAS target
     * @throws Exception
     */
    private void copyGL(MAcctSchema targetAS) throws Exception {
        I_C_AcctSchema_GL source = MAcctSchemaGL.get(p_SourceAcctSchema_ID);
        MAcctSchemaGL target = new MAcctSchemaGL(0);
        target.setAccountingSchemaId(p_TargetAcctSchema_ID);
        ArrayList<KeyNamePair> list = source.getAcctInfo();
        for (KeyNamePair pp : list) {
            int sourceC_ValidCombination_ID = pp.getKey();
            String columnName = pp.getName();
            MAccount sourceAccount = MAccount.get(sourceC_ValidCombination_ID);
            I_C_ValidCombination targetAccount = createAccount(targetAS, sourceAccount);
            target.setValue(columnName, targetAccount.getValidAccountCombinationId());
        }
        if (!target.save()) throw new AdempiereSystemError("Could not Save GL");
    } //	copyGL

    /**
     * Copy Default
     *
     * @param targetAS target
     * @throws Exception
     */
    private void copyDefault(MAcctSchema targetAS) throws Exception {
        DefaultAccountsForSchema source = MAcctSchemaDefault.get(p_SourceAcctSchema_ID);
        MAcctSchemaDefault target = new MAcctSchemaDefault(0);
        target.setAccountingSchemaId(p_TargetAcctSchema_ID);
        target.setAccountingSchemaId(p_TargetAcctSchema_ID);
        ArrayList<KeyNamePair> list = source.getAcctInfo();
        for (KeyNamePair pp : list) {
            int sourceC_ValidCombination_ID = pp.getKey();
            String columnName = pp.getName();
            MAccount sourceAccount = MAccount.get(sourceC_ValidCombination_ID);
            I_C_ValidCombination targetAccount = createAccount(targetAS, sourceAccount);
            target.setValue(columnName, targetAccount.getValidAccountCombinationId());
        }
        if (!target.save()) throw new AdempiereSystemError("Could not Save Default");
    } //	copyDefault

    /**
     * Create Account
     *
     * @param targetAS   target AS
     * @param sourceAcct source account
     * @return target account
     */
    private I_C_ValidCombination createAccount(MAcctSchema targetAS, MAccount sourceAcct) {
        int AD_Client_ID = targetAS.getClientId();
        int C_AcctSchema_ID = targetAS.getAccountingSchemaId();
        //
        int AD_Org_ID = 0;
        int Account_ID = 0;
        int C_SubAcct_ID = 0;
        int M_Product_ID = 0;
        int C_BPartner_ID = 0;
        int AD_OrgTrx_ID = 0;
        int C_LocFrom_ID = 0;
        int C_LocTo_ID = 0;
        int C_SalesRegion_ID = 0;
        int C_Project_ID = 0;
        int C_Campaign_ID = 0;
        int C_Activity_ID = 0;
        int User1_ID = 0;
        int User2_ID = 0;
        int UserElement1_ID = 0;
        int UserElement2_ID = 0;
        //
        //  Active Elements
        AccountSchemaElement[] elements = targetAS.getAcctSchemaElements();
        for (AccountSchemaElement ase : elements) {
            String elementType = ase.getElementType();
            //
            switch (elementType) {
                case MAcctSchemaElement.ELEMENTTYPE_Organization:
                    AD_Org_ID = sourceAcct.getOrgId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_Account:
                    Account_ID = sourceAcct.getAccountId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_SubAccount:
                    C_SubAcct_ID = sourceAcct.getSubAccountId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_BPartner:
                    C_BPartner_ID = sourceAcct.getBusinessPartnerId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_Product:
                    M_Product_ID = sourceAcct.getProductId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_Activity:
                    C_Activity_ID = sourceAcct.getBusinessActivityId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_LocationFrom:
                    C_LocFrom_ID = sourceAcct.getLocationFromId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_LocationTo:
                    C_LocTo_ID = sourceAcct.getLocationToId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_Campaign:
                    C_Campaign_ID = sourceAcct.getCampaignId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_OrgTrx:
                    AD_OrgTrx_ID = sourceAcct.getTransactionOrganizationId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_Project:
                    C_Project_ID = sourceAcct.getProjectId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_SalesRegion:
                    C_SalesRegion_ID = sourceAcct.getSalesRegionId();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_UserElementList1:
                    User1_ID = sourceAcct.getUser1Id();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_UserElementList2:
                    User2_ID = sourceAcct.getUser2Id();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_UserColumn1:
                    UserElement1_ID = sourceAcct.getUserElement1Id();
                    break;
                case MAcctSchemaElement.ELEMENTTYPE_UserColumn2:
                    UserElement2_ID = sourceAcct.getUserElement2Id();
                    break;
            }
            //	No UserElement
        }
        //
        return MAccount.get(
                AD_Client_ID,
                AD_Org_ID,
                C_AcctSchema_ID,
                Account_ID,
                C_SubAcct_ID,
                M_Product_ID,
                C_BPartner_ID,
                AD_OrgTrx_ID,
                C_LocFrom_ID,
                C_LocTo_ID,
                C_SalesRegion_ID,
                C_Project_ID,
                C_Campaign_ID,
                C_Activity_ID,
                User1_ID,
                User2_ID,
                UserElement1_ID,
                UserElement2_ID
        );
    } //	createAccount
} //	AcctSchemaCopyAcct
