package org.idempiere.process;

import org.compiere.accounting.MAccount;
import org.compiere.accounting.MAcctSchema;
import org.compiere.accounting.MAcctSchemaDefault;
import org.compiere.accounting.MFactAcct;
import org.compiere.accounting.MJournal;
import org.compiere.accounting.MJournalLine;
import org.compiere.invoicing.MInvoice;
import org.compiere.model.DefaultAccountsForSchema;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_ValidCombination;
import org.compiere.model.I_T_InvoiceGL;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;
import software.hsharp.core.util.DBKt;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.executeUpdate;

/**
 * Invoice Not realized Gain & Loss. The actual data shown is T_InvoiceGL_v
 *
 * @author Jorg Janke
 * @version $Id: InvoiceNGL.java,v 1.3 2006/08/04 03:53:59 jjanke Exp $ FR: [ 2214883 ] Remove SQL
 * code and Replace for Query - red1
 */
public class InvoiceNGL extends SvrProcess {
    private static String ONLY_AP = "P";
    private static String ONLY_AR = "R";
    /**
     * Mandatory Acct Schema
     */
    private int p_C_AcctSchema_ID = 0;
    /**
     * Mandatory Conversion Type
     */
    private int p_C_ConversionTypeReval_ID = 0;
    /**
     * Revaluation Date
     */
    private Timestamp p_DateReval = null;
    /**
     * Only AP/AR Transactions
     */
    private String p_APAR = "A";
    /**
     * Report all Currencies
     */
    private boolean p_IsAllCurrencies = false;
    /**
     * Optional Invoice Currency
     */
    private int p_C_Currency_ID = 0;
    /**
     * GL Document Type
     */
    private int p_C_DocTypeReval_ID = 0;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            switch (name) {
                case "C_AcctSchema_ID":
                    p_C_AcctSchema_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "C_ConversionTypeReval_ID":
                    p_C_ConversionTypeReval_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "DateReval":
                    p_DateReval = (Timestamp) iProcessInfoParameter.getParameter();
                    break;
                case "APAR":
                    p_APAR = (String) iProcessInfoParameter.getParameter();
                    break;
                case "IsAllCurrencies":
                    p_IsAllCurrencies = "Y".equals(iProcessInfoParameter.getParameter());
                    break;
                case "C_Currency_ID":
                    p_C_Currency_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                case "C_DocTypeReval_ID":
                    p_C_DocTypeReval_ID = iProcessInfoParameter.getParameterAsInt();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }
        }
    } //	prepare

    /**
     * Process
     *
     * @return info
     * @throws Exception
     */
    protected String doIt() throws Exception {
        if (p_IsAllCurrencies) p_C_Currency_ID = 0;
        if (log.isLoggable(Level.INFO))
            log.info(
                    "C_AcctSchema_ID="
                            + p_C_AcctSchema_ID
                            + ",C_ConversionTypeReval_ID="
                            + p_C_ConversionTypeReval_ID
                            + ",DateReval="
                            + p_DateReval
                            + ", APAR="
                            + p_APAR
                            + ", IsAllCurrencies="
                            + p_IsAllCurrencies
                            + ",C_Currency_ID="
                            + p_C_Currency_ID
                            + ", C_DocType_ID="
                            + p_C_DocTypeReval_ID);

        //	Parameter
        if (p_DateReval == null) p_DateReval = new Timestamp(System.currentTimeMillis());

        //	Delete - just to be sure
        StringBuilder sql =
                new StringBuilder("DELETE T_InvoiceGL WHERE AD_PInstance_ID=").append(getProcessInstanceId());
        int no = executeUpdate(sql.toString());
        if (no > 0) if (log.isLoggable(Level.INFO)) log.info("Deleted #" + no);

        //	Insert Trx
        String dateStr = DBKt.convertDate(p_DateReval, true);
        sql =
                new StringBuilder(
                        "INSERT INTO T_InvoiceGL (AD_Client_ID, AD_Org_ID, IsActive, Created,CreatedBy, Updated,UpdatedBy,")
                        .append(" AD_PInstance_ID, C_Invoice_ID, GrandTotal, OpenAmt, ")
                        .append(" Fact_Acct_ID, AmtSourceBalance, AmtAcctBalance, ")
                        .append(" AmtRevalDr, AmtRevalCr, C_DocTypeReval_ID, IsAllCurrencies, ")
                        .append(" DateReval, C_ConversionTypeReval_ID, AmtRevalDrDiff, AmtRevalCrDiff, APAR) ")
                        //	--
                        .append(
                                "SELECT i.AD_Client_ID, i.AD_Org_ID, i.IsActive, i.Created,i.CreatedBy, i.Updated,i.UpdatedBy,")
                        .append(getProcessInstanceId())
                        .append(", i.C_Invoice_ID, i.GrandTotal, invoiceOpen(i.C_Invoice_ID, 0), ")
                        .append(" fa.Fact_Acct_ID, fa.AmtSourceDr-fa.AmtSourceCr, fa.AmtAcctDr-fa.AmtAcctCr, ")
                        //	AmtRevalDr, AmtRevalCr,
                        .append(" currencyConvert(fa.AmtSourceDr, i.C_Currency_ID, a.C_Currency_ID, ")
                        .append(dateStr)
                        .append(", ")
                        .append(p_C_ConversionTypeReval_ID)
                        .append(", i.AD_Client_ID, i.orgId),")
                        .append(" currencyConvert(fa.AmtSourceCr, i.C_Currency_ID, a.C_Currency_ID, ")
                        .append(dateStr)
                        .append(", ")
                        .append(p_C_ConversionTypeReval_ID)
                        .append(", i.AD_Client_ID, i.orgId),")
                        .append((p_C_DocTypeReval_ID == 0 ? "NULL" : String.valueOf(p_C_DocTypeReval_ID)))
                        .append(", ")
                        .append((p_IsAllCurrencies ? "'Y'," : "'N',"))
                        .append(dateStr)
                        .append(", ")
                        .append(p_C_ConversionTypeReval_ID)
                        .append(", 0, 0, '")
                        .append(p_APAR)
                        .append("' ")
                        //
                        .append("FROM C_Invoice_v i")
                        .append(
                                " INNER JOIN Fact_Acct fa ON (fa.AD_Table_ID=318 AND fa.Record_ID=i.C_Invoice_ID")
                        .append(" AND (i.GrandTotal=fa.AmtSourceDr OR i.GrandTotal=fa.AmtSourceCr))")
                        .append(" INNER JOIN C_AcctSchema a ON (fa.C_AcctSchema_ID=a.C_AcctSchema_ID) ")
                        .append("WHERE i.IsPaid='N'")
                        .append(" AND EXISTS (SELECT * FROM C_ElementValue ev ")
                        .append(
                                "WHERE ev.C_ElementValue_ID=fa.Account_ID AND (ev.AccountType='A' OR ev.AccountType='L'))")
                        .append(" AND fa.C_AcctSchema_ID=")
                        .append(p_C_AcctSchema_ID);
        if (p_IsAllCurrencies) sql.append(" AND i.C_Currency_ID<>a.C_Currency_ID");
        if (ONLY_AR.equals(p_APAR)) sql.append(" AND i.IsSOTrx='Y'");
        else if (ONLY_AP.equals(p_APAR)) sql.append(" AND i.IsSOTrx='N'");
        if (!p_IsAllCurrencies && p_C_Currency_ID != 0)
            sql.append(" AND i.C_Currency_ID=").append(p_C_Currency_ID);

        no = executeUpdate(sql.toString());
        if (no != 0) {
            if (log.isLoggable(Level.INFO)) log.info("Inserted #" + no);
        } else if (log.isLoggable(Level.FINER)) {
            log.warning("Inserted #" + no + " - " + sql);
        } else {
            log.warning("Inserted #" + no);
        }

        //	Calculate Difference
        sql =
                new StringBuilder("UPDATE T_InvoiceGL gl ")
                        .append("SET (AmtRevalDrDiff,AmtRevalCrDiff)=")
                        .append("(SELECT gl.AmtRevalDr-fa.AmtAcctDr, gl.AmtRevalCr-fa.AmtAcctCr ")
                        .append("FROM Fact_Acct fa ")
                        .append("WHERE gl.Fact_Acct_ID=fa.Fact_Acct_ID) ")
                        .append("WHERE AD_PInstance_ID=")
                        .append(getProcessInstanceId());
        int noT = executeUpdate(sql.toString());
        if (noT > 0) if (log.isLoggable(Level.CONFIG)) log.config("Difference #" + noT);

        //	Percentage
        sql =
                new StringBuilder("UPDATE T_InvoiceGL SET Percent = 100 ")
                        .append("WHERE GrandTotal=OpenAmt AND AD_PInstance_ID=")
                        .append(getProcessInstanceId());
        no = executeUpdate(sql.toString());
        if (no > 0) if (log.isLoggable(Level.INFO)) log.info("Not Paid #" + no);

        sql =
                new StringBuilder("UPDATE T_InvoiceGL SET Percent = ROUND(OpenAmt*100/GrandTotal,6) ")
                        .append("WHERE GrandTotal<>OpenAmt AND GrandTotal <> 0 AND AD_PInstance_ID=")
                        .append(getProcessInstanceId());
        no = executeUpdate(sql.toString());
        if (no > 0) if (log.isLoggable(Level.INFO)) log.info("Partial Paid #" + no);

        sql =
                new StringBuilder("UPDATE T_InvoiceGL SET AmtRevalDr = AmtRevalDr * Percent/100,")
                        .append(" AmtRevalCr = AmtRevalCr * Percent/100,")
                        .append(" AmtRevalDrDiff = AmtRevalDrDiff * Percent/100,")
                        .append(" AmtRevalCrDiff = AmtRevalCrDiff * Percent/100 ")
                        .append("WHERE Percent <> 100 AND AD_PInstance_ID=")
                        .append(getProcessInstanceId());
        no = executeUpdate(sql.toString());
        if (no > 0) if (log.isLoggable(Level.CONFIG)) log.config("Partial Calc #" + no);

        //	Create Document
        String info = "";
        if (p_C_DocTypeReval_ID != 0) {
            if (p_C_Currency_ID != 0) log.warning("Can create Journal only for all currencies");
            else info = createGLJournal();
        }
        return "#" + noT + info;
    } //	doIt

    /**
     * Create GL Journal
     *
     * @return document info
     */
    private String createGLJournal() {
        // FR: [ 2214883 ] Remove SQL code and Replace for Query
        final String whereClause = "AD_PInstance_ID=?";
        List<I_T_InvoiceGL> list =
                new Query<I_T_InvoiceGL>(X_T_InvoiceGL.Table_Name, whereClause)
                        .setParameters(getProcessInstanceId())
                        .setOrderBy("AD_Org_ID")
                        .list();
        // FR: [ 2214883 ] Remove SQL code and Replace for Query

        if (list.size() == 0) return " - No Records found";

        //
        MAcctSchema as = MAcctSchema.get(p_C_AcctSchema_ID);
        DefaultAccountsForSchema asDefaultAccts = MAcctSchemaDefault.get(p_C_AcctSchema_ID);
        MGLCategory cat = MGLCategory.getDefaultSystem();
        if (cat == null) {
            MDocType docType = MDocTypeKt.getDocumentType(p_C_DocTypeReval_ID);
            cat = MGLCategory.get(docType.getGLCategoryId());
        }
        //
        MJournal journal = new MJournal(0);
        journal.setDocumentTypeId(p_C_DocTypeReval_ID);
        journal.setPostingType(MJournal.POSTINGTYPE_Actual);
        journal.setDateDoc(p_DateReval);
        journal.setDateAcct(p_DateReval); // sets the period too
        journal.setCurrencyId(as.getCurrencyId());
        journal.setAccountingSchemaId(as.getAccountingSchemaId());
        journal.setConversionTypeId(p_C_ConversionTypeReval_ID);
        journal.setGLCategoryId(cat.getGLCategoryId());
        journal.setDescription(getName()); // updated below
        if (!journal.save()) return " - Could not create Journal";
        //
        BigDecimal gainTotal = Env.ZERO;
        BigDecimal lossTotal = Env.ZERO;
        int AD_Org_ID = 0;
        MOrg org = null;
        for (int i = 0; i < list.size(); i++) {
            I_T_InvoiceGL gl = list.get(i);
            if (gl.getAmtRevalDrDiff().signum() == 0 && gl.getAmtRevalCrDiff().signum() == 0) continue;
            MInvoice invoice = new MInvoice(null, gl.getInvoiceId());
            if (invoice.getCurrencyId() == as.getCurrencyId()) continue;
            //
            if (AD_Org_ID == 0) // 	invoice org id
                AD_Org_ID = gl.getOrgId();
            //	Change in Org
            if (AD_Org_ID != gl.getOrgId()) {
                createBalancing(asDefaultAccts, journal, gainTotal, lossTotal, AD_Org_ID, (i + 1) * 10);
                //
                AD_Org_ID = gl.getOrgId();
                gainTotal = Env.ZERO;
                lossTotal = Env.ZERO;
                journal = null;
            }
            //
            if (org == null) {
                org = MOrgKt.getOrg(gl.getOrgId());
                journal.setDescription(getName() + " - " + org.getName());
                if (!journal.save()) return " - Could not set Description for Journal";
            }
            //
            MJournalLine line = new MJournalLine(journal);
            line.setLine((i + 1) * 10);
            line.setDescription(invoice.getSummary());
            //
            MFactAcct fa = new MFactAcct(gl.getFactAcctId());
            I_C_ValidCombination acct = MAccount.get(fa);
            line.setValidCombinationId(acct);
            BigDecimal dr = gl.getAmtRevalDrDiff();
            BigDecimal cr = gl.getAmtRevalCrDiff();
            // Check if acct.IsActiva to differentiate gain and loss ->
            // acct.isActiva negative dr or positive cr -> loss
            // acct.isActiva positive dr or negative cr -> gain
            // acct.isPassiva negative cr or positive dr -> gain
            // acct.isPassiva positive cr or negative dr -> loss
            if (acct.isActiva()) {
                if (dr.signum() < 0) {
                    lossTotal = lossTotal.add(dr.negate());
                } else if (dr.signum() > 0) {
                    gainTotal = gainTotal.add(dr);
                }
                if (cr.signum() > 0) {
                    lossTotal = lossTotal.add(cr);
                }
                if (cr.signum() < 0) {
                    gainTotal = gainTotal.add(cr.negate());
                }
            } else { // isPassiva
                if (cr.signum() < 0) {
                    gainTotal = gainTotal.add(cr.negate());
                } else if (cr.signum() > 0) {
                    lossTotal = lossTotal.add(cr);
                }
                if (dr.signum() > 0) {
                    gainTotal = gainTotal.add(dr);
                } else if (dr.signum() < 0) {
                    lossTotal = lossTotal.add(dr.negate());
                }
            }
            line.setAmtSourceDr(dr);
            line.setAmtAcctDr(dr);
            line.setAmtSourceCr(cr);
            line.setAmtAcctCr(cr);
            line.saveEx();
        }
        createBalancing(
                asDefaultAccts, journal, gainTotal, lossTotal, AD_Org_ID, (list.size() + 1) * 10);

        addLog(
                journal.getGLJournalId(),
                null,
                null,
                " - " + journal.getDocumentNo() + " #" + list.size(),
                MJournal.Table_ID,
                journal.getGLJournalId());
        return "OK";
    } //	createGLJournal

    /**
     * Create Balancing Entry
     *
     * @param asDefaultAccts acct schema default accounts
     * @param journal        journal
     * @param gainTotal      dr
     * @param lossTotal      cr
     * @param AD_Org_ID      org
     * @param lineNo         base line no
     */
    private void createBalancing(
            DefaultAccountsForSchema asDefaultAccts,
            MJournal journal,
            BigDecimal gainTotal,
            BigDecimal lossTotal,
            int AD_Org_ID,
            int lineNo) {
        if (journal == null) throw new IllegalArgumentException("Journal is null");
        //		CR Entry = Gain
        if (gainTotal.signum() != 0) {
            MJournalLine line = new MJournalLine(journal);
            line.setLine(lineNo + 1);
            MAccount base = MAccount.get(asDefaultAccts.getUnrealizedGainAccount());
            I_C_ValidCombination acct =
                    MAccount.get(
                            asDefaultAccts.getClientId(),
                            AD_Org_ID,
                            asDefaultAccts.getAccountingSchemaId(),
                            base.getAccountId(),
                            base.getSubAccountId(),
                            base.getProductId(),
                            base.getBusinessPartnerId(),
                            base.getTransactionOrganizationId(),
                            base.getLocationFromId(),
                            base.getLocationToId(),
                            base.getSalesRegionId(),
                            base.getProjectId(),
                            base.getCampaignId(),
                            base.getBusinessActivityId(),
                            base.getUser1Id(),
                            base.getUser2Id(),
                            base.getUserElement1Id(),
                            base.getUserElement2Id()
                    );
            line.setDescription(MsgKt.getElementTranslation("UnrealizedGain_Acct"));
            line.setValidAccountCombinationId(acct.getValidAccountCombinationId());
            line.setAmtSourceCr(gainTotal);
            line.setAmtAcctCr(gainTotal);
            line.saveEx();
        }
        //	DR Entry = Loss
        if (lossTotal.signum() != 0) {
            MJournalLine line = new MJournalLine(journal);
            line.setLine(lineNo + 2);
            MAccount base = MAccount.get(asDefaultAccts.getUnrealizedLossAccount());
            I_C_ValidCombination acct =
                    MAccount.get(
                            asDefaultAccts.getClientId(),
                            AD_Org_ID,
                            asDefaultAccts.getAccountingSchemaId(),
                            base.getAccountId(),
                            base.getSubAccountId(),
                            base.getProductId(),
                            base.getBusinessPartnerId(),
                            base.getTransactionOrganizationId(),
                            base.getLocationFromId(),
                            base.getLocationToId(),
                            base.getSalesRegionId(),
                            base.getProjectId(),
                            base.getCampaignId(),
                            base.getBusinessActivityId(),
                            base.getUser1Id(),
                            base.getUser2Id(),
                            base.getUserElement1Id(),
                            base.getUserElement2Id()
                    );
            line.setDescription(MsgKt.getElementTranslation("UnrealizedLoss_Acct"));
            line.setValidAccountCombinationId(acct.getValidAccountCombinationId());
            line.setAmtSourceDr(lossTotal);
            line.setAmtAcctDr(lossTotal);
            line.saveEx();
        }
    } //	createBalancing
} //	InvoiceNGL
