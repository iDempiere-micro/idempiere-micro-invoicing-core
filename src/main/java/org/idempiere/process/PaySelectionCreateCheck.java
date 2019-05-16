package org.idempiere.process;

import org.compiere.accounting.MPaySelection;
import org.compiere.accounting.MPaySelectionCheck;
import org.compiere.crm.MBPartner;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_PaySelectionLine;
import org.compiere.order.OrderConstants;
import org.compiere.process.SvrProcess;
import org.idempiere.common.util.AdempiereUserError;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Create Checks from Payment Selection Line
 *
 * @author Jorg Janke
 * @version $Id: PaySelectionCreateCheck.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class PaySelectionCreateCheck extends SvrProcess {
    /**
     * Target Payment Rule
     */
    private String p_PaymentRule = null;
    /**
     * Payment Selection
     */
    private int p_C_PaySelection_ID = 0;
    /**
     * The checks
     */
    private ArrayList<MPaySelectionCheck> m_list = new ArrayList<>();

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            if (name.equals("PaymentRule")) p_PaymentRule = (String) iProcessInfoParameter.getParameter();
            else log.log(Level.SEVERE, "Unknown Parameter: " + name);
        }
        p_C_PaySelection_ID = getRecordId();
        if (p_PaymentRule != null && p_PaymentRule.equals(OrderConstants.PAYMENTRULE_DirectDebit))
            p_PaymentRule = null;
    } //	prepare

    /**
     * Perform process.
     *
     * @return Message (clear text)
     * @throws Exception if not successful
     */
    protected String doIt() throws Exception {
        if (log.isLoggable(Level.INFO))
            log.info("C_PaySelection_ID=" + p_C_PaySelection_ID + ", PaymentRule=" + p_PaymentRule);

        MPaySelection psel = new MPaySelection(p_C_PaySelection_ID);
        if (psel.getId() == 0)
            throw new IllegalArgumentException("Not found C_PaySelection_ID=" + p_C_PaySelection_ID);
        if (psel.isProcessed()) throw new IllegalArgumentException("@Processed@");
        //
        I_C_PaySelectionLine[] lines = psel.getLines(false);
        for (I_C_PaySelectionLine line : lines) {
            if (!line.isActive() || line.isProcessed()) continue;
            createCheck(line);
        }
        //
        psel.setProcessed(true);
        psel.saveEx();

        return "@C_PaySelectionCheck_ID@ - #" + m_list.size();
    } //	doIt

    /**
     * Create Check from line
     *
     * @param line
     * @throws Exception for invalid bank accounts
     */
    private void createCheck(I_C_PaySelectionLine line) throws Exception {
        //	Try to find one
        for (MPaySelectionCheck check : m_list) {
            //	Add to existing
            if (check.getBusinessPartnerId() == line.getInvoice().getBusinessPartnerId()) {
                check.addLine(line);
                if (!check.save()) throw new IllegalStateException("Cannot save MPaySelectionCheck");
                line.setPaySelectionCheckId(check.getPaySelectionCheckId());
                line.setProcessed(true);
                if (!line.save()) throw new IllegalStateException("Cannot save MPaySelectionLine");
                return;
            }
        }
        //	Create new
        String PaymentRule = line.getPaymentRule();
        if (p_PaymentRule != null) {
            if (!OrderConstants.PAYMENTRULE_DirectDebit.equals(PaymentRule)) PaymentRule = p_PaymentRule;
        }
        MPaySelectionCheck check = new MPaySelectionCheck(line, PaymentRule);
        if (!check.isValid()) {
            int C_BPartner_ID = check.getBusinessPartnerId();
            I_C_BPartner bp = MBPartner.get(C_BPartner_ID);
            throw new AdempiereUserError("@NotFound@ @C_BP_BankAccount@: " + bp.getName());
        }
        if (!check.save()) throw new IllegalStateException("Cannot save MPaySelectionCheck");
        line.setPaySelectionCheckId(check.getPaySelectionCheckId());
        line.setProcessed(true);
        if (!line.save()) throw new IllegalStateException("Cannot save MPaySelectionLine");
        m_list.add(check);
    } //	createCheck
} //	PaySelectionCreateCheck
