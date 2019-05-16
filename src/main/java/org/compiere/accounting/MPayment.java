package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.bank.IBAN;
import org.compiere.bank.MBankAccount;
import org.compiere.crm.X_C_BPartner;
import org.compiere.docengine.DocumentEngine;
import org.compiere.invoicing.MBPBankAccount;
import org.compiere.invoicing.MConversionRate;
import org.compiere.invoicing.MDocTypeCounter;
import org.compiere.invoicing.MInvoice;
import org.compiere.invoicing.MPaymentTransaction;
import org.compiere.invoicing.MPaymentValidate;
import org.compiere.invoicing.PaymentUtil;
import org.compiere.model.IDoc;
import org.compiere.model.IPODoc;
import org.compiere.model.IPaymentProcessor;
import org.compiere.model.IProcessInfo;
import org.compiere.model.I_C_BPartner;
import org.compiere.model.I_C_Cash;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_Payment;
import org.compiere.model.I_C_PaymentAllocate;
import org.compiere.model.PaymentInterface;
import org.compiere.order.MOnlineTrxHistory;
import org.compiere.order.OrderConstants;
import org.compiere.orm.MDocType;
import org.compiere.orm.MDocTypeKt;
import org.compiere.orm.MOrg;
import org.compiere.orm.MOrgKt;
import org.compiere.orm.MSequence;
import org.compiere.orm.MSysConfig;
import org.compiere.orm.PO;
import org.compiere.orm.PeriodClosedException;
import org.compiere.orm.Query;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.process.IProcessUI;
import org.compiere.process.ProcessCall;
import org.compiere.util.MsgKt;
import org.compiere.validation.ModelValidationEngine;
import org.compiere.validation.ModelValidator;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import static org.compiere.orm.MDocType.DOCBASETYPE_APPayment;
import static org.compiere.orm.MDocType.DOCBASETYPE_ARReceipt;
import static software.hsharp.core.util.DBKt.executeUpdate;
import static software.hsharp.core.util.DBKt.forUpdate;
import static software.hsharp.core.util.DBKt.getSQLValue;
import static software.hsharp.core.util.DBKt.prepareStatement;

/**
 * Payment Model. - retrieve and create payments for invoice
 *
 * <pre>
 *  Event chain
 *  - Payment inserted
 *      C_Payment_Trg fires
 *          update DocumentNo with payment summary
 *  - Payment posted (C_Payment_Post)
 *      create allocation line
 *          C_Allocation_Trg fires
 *              Update C_BPartner Open Item Amount
 *      update invoice (IsPaid)
 *      link invoice-payment if batch
 *
 *  Lifeline:
 *  -   Created by VPayment or directly
 *  -   When changed in VPayment
 *      - old payment is reversed
 *      - new payment created
 *
 *  When Payment is posed, the Allocation is made
 *  </pre>
 *
 * @author Jorg Janke
 * @author victor.perez@e-evolution.com, e-Evolution http://www.e-evolution.com
 * <li>FR [ 1948157 ] Is necessary the reference for document reverse
 * @author Carlos Ruiz - globalqss [ 2141475 ] Payment <> allocations must not be completed -
 * implement lots of validations on prepareIt
 * @version $Id: MPayment.java,v 1.4 2006/10/02 05:18:39 jjanke Exp $
 * @sse http://sourceforge.net/tracker/index.php?func=detail&aid=1866214&group_id=176962&atid=879335
 * <li>FR [ 2520591 ] Support multiples calendar for Org
 * @see http://sourceforge.net/tracker/?func=detail&atid=879335&aid=1948157&group_id=176962
 * <li>FR [ 1866214 ]
 * @see http://sourceforge.net/tracker2/?func=detail&atid=879335&aid=2520591&group_id=176962
 */
public class MPayment extends X_C_Payment
        implements DocAction, ProcessCall, PaymentInterface, IPODoc {

    private static final long serialVersionUID = -7179638016937305380L;
    /**
     * Reversal Indicator
     */
    public static String REVERSE_INDICATOR = "^";
    /**
     * Temporary Bank Account Processors
     */
    private MBankAccountProcessor[] m_mBankAccountProcessors = null;
    /**
     * Temporary Bank Account Processor
     */
    private MBankAccountProcessor m_mBankAccountProcessor = null;
    /**
     * Error Message
     */
    private String m_errorMessage = null;
    /**
     * Process Message
     */
    private String m_processMsg = null;
    /**
     * Just Prepared Flag
     */
    private boolean m_justPrepared = false;
    @SuppressWarnings("unused")
    private IProcessUI m_processUI;
    // IDEMPIERE-2588
    private MAllocationHdr m_justCreatedAllocInv = null;

    /**
     * ************************************************************************ Default Constructor
     *
     * @param C_Payment_ID payment to load, (0 create new payment)
     */
    public MPayment(int C_Payment_ID) {
        super(null, C_Payment_ID);
        //  New
        if (C_Payment_ID == 0) {
            setDocAction(X_C_Payment.DOCACTION_Complete);
            setDocStatus(X_C_Payment.DOCSTATUS_Drafted);
            setTrxType(X_C_Payment.TRXTYPE_Sales);
            //
            setAddressVerified(X_C_Payment.R_AVSZIP_Unavailable);
            setVerifiedZip(X_C_Payment.R_AVSZIP_Unavailable);
            //
            setIsReceipt(true);
            setIsApproved(false);
            setIsReconciled(false);
            setIsAllocated(false);
            setIsOnline(false);
            setIsSelfService(false);
            setIsDelayedCapture(false);
            setIsPrepayment(false);
            setProcessed(false);
            setProcessing(false);
            setPosted(false);
            //
            setPayAmt(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setTaxAmt(Env.ZERO);
            setWriteOffAmt(Env.ZERO);
            setIsOverUnderPayment(true);
            setOverUnderAmt(Env.ZERO);
            //
            setDateTrx(new Timestamp(System.currentTimeMillis()));
            setDateAcct(getDateTrx());
            setTenderType(X_C_Payment.TENDERTYPE_Check);
        }
    } //  MPayment

    /**
     * Load Constructor
     */
    public MPayment(Row row) {
        super(row, -1);
    } //	MPayment

    /**
     * Get Payments Of BPartner
     *
     * @param C_BPartner_ID id
     * @return array
     */
    public static I_C_Payment[] getOfBPartner(int C_BPartner_ID) {
        // FR: [ 2214883 ] Remove SQL code and Replace for Query - red1
        final String whereClause = "C_BPartner_ID=?";
        List<I_C_Payment> list =
                new Query<I_C_Payment>(I_C_Payment.Table_Name, whereClause)
                        .setParameters(C_BPartner_ID)
                        .list();

        //
        I_C_Payment[] retValue = new I_C_Payment[list.size()];
        list.toArray(retValue);
        return retValue;
    } //	getOfBPartner

    /**
     * Is Cash Trx
     *
     * @return true if Cash Trx
     */
    public boolean isCashTrx() {
        return "X".equals(getTenderType());
    } //	isCashTrx

    /**
     * Is Cashbook Trx
     *
     * @return true if this is a cashbook trx
     */
    public boolean isCashbookTrx() {
        return isCashTrx()
                && !MSysConfig.getBooleanValue(MSysConfig.CASH_AS_PAYMENT, true, getClientId());
    }

    /**
     * Set ACH BankAccount Info
     *
     * @return true if valid
     */
    public boolean setBankACH(MPaySelectionCheck preparedPayment) {
        //	Our Bank
        setBankAccountId(preparedPayment.getParent().getBankAccountId());
        //	Target Bank
        int C_BP_BankAccount_ID = preparedPayment.getBusinessPartnerBankAccountId();
        MBPBankAccount ba = new MBPBankAccount(C_BP_BankAccount_ID);
        setRoutingNo(ba.getRoutingNo());
        setAccountNo(ba.getAccountNo());
        setIBAN(ba.getIBAN());
        setSwiftCode(ba.getSwiftCode());
        setDescription(preparedPayment.getPaySelection().getName());
        setIsReceipt(
                OrderConstants.PAYMENTRULE_DirectDebit.equals // 	AR only
                        (preparedPayment.getPaymentRule()));
        if (MPaySelectionCheck.PAYMENTRULE_DirectDebit.equals(preparedPayment.getPaymentRule()))
            setTenderType(MPayment.TENDERTYPE_DirectDebit);
        else if (MPaySelectionCheck.PAYMENTRULE_DirectDeposit.equals(preparedPayment.getPaymentRule()))
            setTenderType(MPayment.TENDERTYPE_DirectDeposit);
        //
        int check =
                MPaymentValidate.validateRoutingNo(getRoutingNo()).length()
                        + MPaymentValidate.validateAccountNo(getAccountNo()).length();
        return check == 0;
    } //	setBankACH

    /**
     * Set Check BankAccount Info
     *
     * @param C_BankAccount_ID bank account
     * @param isReceipt        true if receipt
     * @param checkNo          check no
     * @return true if valid
     */
    public boolean setBankCheck(int C_BankAccount_ID, boolean isReceipt, String checkNo) {
        return setBankCheck(C_BankAccount_ID, isReceipt, null, null, checkNo);
    } //	setBankCheck

    /**
     * Set Check BankAccount Info
     *
     * @param C_BankAccount_ID bank account
     * @param isReceipt        true if receipt
     * @param routingNo        routing no
     * @param accountNo        account no
     * @param checkNo          chack no
     * @return true if valid
     */
    public boolean setBankCheck(
            int C_BankAccount_ID, boolean isReceipt, String routingNo, String accountNo, String checkNo) {
        setTenderType(X_C_Payment.TENDERTYPE_Check);
        setIsReceipt(isReceipt);
        //
        if (C_BankAccount_ID > 0
                && (routingNo == null
                || routingNo.length() == 0
                || accountNo == null
                || accountNo.length() == 0)) setBankAccountDetails(C_BankAccount_ID);
        else {
            setBankAccountId(C_BankAccount_ID);
            setRoutingNo(routingNo);
            setAccountNo(accountNo);
        }
        setCheckNo(checkNo);
        //
        int check =
                MPaymentValidate.validateRoutingNo(routingNo).length()
                        + MPaymentValidate.validateAccountNo(accountNo).length()
                        + MPaymentValidate.validateCheckNo(checkNo).length();
        return check == 0; //  no error message
    } //  setBankCheck

    /**
     * Set Bank Account Details. Look up Routing No & Bank Acct No
     *
     * @param C_BankAccount_ID bank account
     */
    public void setBankAccountDetails(int C_BankAccount_ID) {
        if (C_BankAccount_ID == 0) return;
        setBankAccountId(C_BankAccount_ID);
        //
        String sql =
                "SELECT b.RoutingNo, ba.AccountNo, ba.IBAN, b.SwiftCode "
                        + "FROM C_BankAccount ba"
                        + " INNER JOIN C_Bank b ON (ba.C_Bank_ID=b.C_Bank_ID) "
                        + "WHERE C_BankAccount_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, C_BankAccount_ID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                setRoutingNo(rs.getString(1));
                setAccountNo(rs.getString(2));
                setIBAN(rs.getString(3));
                setSwiftCode(rs.getString(4));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
        } finally {

            rs = null;
            pstmt = null;
        }
    } //	setBankAccountDetails

    /**
     * ************************************************************************ Process Payment
     *
     * @return true if approved
     */
    public boolean processOnline() {
        if (log.isLoggable(Level.INFO)) log.info("Amt=" + getPayAmt());
        //
        setIsOnline(true);
        setErrorMessage(null);
        //	prevent charging twice
        if (getTrxType().equals(X_C_Payment.TRXTYPE_Void)
                || getTrxType().equals(X_C_Payment.TRXTYPE_CreditPayment)) {
            if (isVoided()) {
                if (log.isLoggable(Level.INFO))
                    log.info("Already voided - " + getTransmissionResult() + " - " + getResponseMessage());
                setErrorMessage(MsgKt.getMsg("PaymentAlreadyVoided"));
                return true;
            }
        } else if (getTrxType().equals(X_C_Payment.TRXTYPE_DelayedCapture)) {
            if (isDelayedCapture()) {
                if (log.isLoggable(Level.INFO))
                    log.info("Already delayed capture - " + getTransmissionResult() + " - " + getResponseMessage());
                setErrorMessage(MsgKt.getMsg("PaymentAlreadyDelayedCapture"));
                return true;
            }
        } else {
            if (isApproved()) {
                if (log.isLoggable(Level.INFO))
                    log.info("Already processed - " + getTransmissionResult() + " - " + getResponseMessage());
                setErrorMessage(MsgKt.getMsg("PaymentAlreadyProcessed"));
                return true;
            }
        }

        if (m_mBankAccountProcessor == null) setPaymentProcessor();
        if (m_mBankAccountProcessor == null) {
            if (getPaymentProcessorId() > 0) {
                MPaymentProcessor pp =
                        new MPaymentProcessor(getPaymentProcessorId());
                log.log(Level.WARNING, "No Payment Processor Model " + pp.toString());
                setErrorMessage(MsgKt.getMsg("PaymentNoProcessorModel") + ": " + pp.toString());
            } else {
                log.log(Level.WARNING, "No Payment Processor Model");
                setErrorMessage(MsgKt.getMsg("PaymentNoProcessorModel"));
            }
            return false;
        }

        boolean approved = false;

        try {
            IPaymentProcessor pp =
                    MPaymentTransaction.createPaymentProcessor(m_mBankAccountProcessor, this);
            if (pp == null) setErrorMessage(MsgKt.getMsg("PaymentNoProcessor"));
            else {
                // Validate before trying to process
                //				String msg = pp.validate();
                //				if (msg!=null && msg.trim().length()>0) {
                //					setErrorMessage(MsgKt.getMsg( msg));
                //				} else {
                // Process if validation succeeds
                approved = pp.processCC();

                if (approved) setErrorMessage(null);
                else {
                    if (getTrxType().equals(X_C_Payment.TRXTYPE_Void)
                            || getTrxType().equals(X_C_Payment.TRXTYPE_CreditPayment))
                        setErrorMessage("From " + getCreditCardName() + ": " + getVoidMessage());
                    else setErrorMessage("From " + getCreditCardName() + ": " + getResponseMessage());
                }
                //				}
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "processOnline", e);
            setErrorMessage(MsgKt.getMsg("PaymentNotProcessed") + ": " + e.getMessage());
        }

        if (approved) {
            setCreditCardNumber(PaymentUtil.encrpytCreditCard(getCreditCardNumber()));
            setCreditCardVV(PaymentUtil.encrpytCvv(getCreditCardVV()));

            setDateTrx(new Timestamp(System.currentTimeMillis()));
            setDateAcct(new Timestamp(System.currentTimeMillis()));
            setProcessed(true); // prevent editing of payment details once approved
        }

        setIsApproved(approved);

        try {

            MPaymentTransaction m_mPaymentTransaction = createPaymentTransaction(null);
            m_mPaymentTransaction.setIsApproved(approved);
            if (getTrxType().equals(X_C_Payment.TRXTYPE_Void)
                    || getTrxType().equals(X_C_Payment.TRXTYPE_CreditPayment))
                m_mPaymentTransaction.setIsVoided(approved);
            m_mPaymentTransaction.setProcessed(approved);
            m_mPaymentTransaction.setPaymentId(getPaymentId());
            m_mPaymentTransaction.saveEx();

            MOnlineTrxHistory history = new MOnlineTrxHistory(0);
            history.setRowTableId(MPaymentTransaction.Table_ID);
            history.setRecordId(m_mPaymentTransaction.getPaymentTransactionId());
            history.setIsError(!approved);
            history.setProcessed(approved);

            StringBuilder msg = new StringBuilder();
            if (approved) {
                if (getTrxType().equals(X_C_Payment.TRXTYPE_Void)
                        || getTrxType().equals(X_C_Payment.TRXTYPE_CreditPayment))
                    msg.append(getVoidMessage() + "\n");
                else {
                    msg.append("Result: " + getTransmissionResult() + "\n");
                    msg.append("Response Message: " + getResponseMessage() + "\n");
                    msg.append("Reference: " + getPaymentReference() + "\n");
                    msg.append("Authorization Code: " + getAuthorizationCode() + "\n");
                }
            } else msg.append("ERROR: " + getErrorMessage() + "\n");
            msg.append("Transaction Type: " + getTrxType());
            history.setTextMsg(msg.toString());

            history.saveEx();
        } catch (Exception e) {
            log.log(Level.SEVERE, "processOnline", e);
            setErrorMessage(MsgKt.getMsg("PaymentNotProcessed") + ": " + e.getMessage());
            throw new Error(e);
        }

        if (getTrxType().equals(X_C_Payment.TRXTYPE_Void)
                || getTrxType().equals(X_C_Payment.TRXTYPE_CreditPayment)) setIsVoided(approved);

        return approved;
    } //  processOnline

    /**
     * Process Online Payment. implements ProcessCall after standard constructor Called when pressing
     * the Process_Online button in C_Payment
     *
     * @param pi Process Info
     * @return true if the next process should be performed
     */
    public boolean startProcess(IProcessInfo pi) {
        if (log.isLoggable(Level.INFO)) log.info("startProcess - " + pi.getRecordId());
        boolean retValue = false;
        //
        if (pi.getRecordId() != getId()) {
            log.log(Level.SEVERE, "startProcess - Not same Payment - " + pi.getRecordId());
            return false;
        }
        //  Process it
        retValue = processOnline();
        saveEx();
        return retValue; //  Payment processed
    } //  startProcess

    /**
     * Before Save
     *
     * @param newRecord new
     * @return save
     */
    protected boolean beforeSave(boolean newRecord) {
        if (isComplete()
                && !isValueChanged(I_C_Payment.COLUMNNAME_Processed)
                && (isValueChanged(I_C_Payment.COLUMNNAME_C_BankAccount_ID)
                || isValueChanged(I_C_Payment.COLUMNNAME_C_BPartner_ID)
                || isValueChanged(I_C_Payment.COLUMNNAME_C_Charge_ID)
                || isValueChanged(I_C_Payment.COLUMNNAME_C_Currency_ID)
                || isValueChanged(I_C_Payment.COLUMNNAME_C_DocType_ID)
                || isValueChanged(I_C_Payment.COLUMNNAME_DateAcct)
                || isValueChanged(I_C_Payment.COLUMNNAME_DateTrx)
                || isValueChanged(I_C_Payment.COLUMNNAME_DiscountAmt)
                || isValueChanged(I_C_Payment.COLUMNNAME_PayAmt)
                || isValueChanged(I_C_Payment.COLUMNNAME_WriteOffAmt))) {
            log.saveError("PaymentAlreadyProcessed", MsgKt.translate("C_Payment_ID"));
            return false;
        }
        // @Trifon - CashPayments
        // if ( getTenderType().equals("X") ) {
        if (isCashbookTrx()) {
            // Cash Book Is mandatory
            if (getCashBookId() <= 0) {
                log.saveError("Error", MsgKt.parseTranslation("@Mandatory@: @C_CashBook_ID@"));
                return false;
            }
        } else {
            // Bank Account Is mandatory
            if (getBankAccountId() <= 0) {
                log.saveError("Error", MsgKt.parseTranslation("@Mandatory@: @C_BankAccount_ID@"));
                return false;
            }
        }
        // end @Trifon - CashPayments

        //	We have a charge
        if (getChargeId() != 0) {
            if (newRecord || isValueChanged("C_Charge_ID")) {
                setOrderId(0);
                setInvoiceId(0);
                setWriteOffAmt(Env.ZERO);
                setDiscountAmt(Env.ZERO);
                setIsOverUnderPayment(false);
                setOverUnderAmt(Env.ZERO);
                setIsPrepayment(false);
            }
        }
        //	We need a BPartner
        else if (getBusinessPartnerId() == 0 && !isCashTrx()) {
            if (getInvoiceId() != 0) ;
            else if (getOrderId() != 0) ;
            else {
                log.saveError("Error", MsgKt.parseTranslation("@NotFound@: @C_BPartner_ID@"));
                return false;
            }
        }
        //	Prepayment: No charge and order or project (not as acct dimension)
        if (newRecord
                || isValueChanged("C_Charge_ID")
                || isValueChanged("C_Invoice_ID")
                || isValueChanged("C_Order_ID")
                || isValueChanged("C_Project_ID"))
            setIsPrepayment(
                    getChargeId() == 0
                            && getBusinessPartnerId() != 0
                            && (getOrderId() != 0 || (getProjectId() != 0 && getInvoiceId() == 0)));
        if (isPrepayment()) {
            if (newRecord || isValueChanged("C_Order_ID") || isValueChanged("C_Project_ID")) {
                setWriteOffAmt(Env.ZERO);
                setDiscountAmt(Env.ZERO);
                setIsOverUnderPayment(false);
                setOverUnderAmt(Env.ZERO);
            }
        }

        //	Document Type/Receipt
        if (getDocumentTypeId() == 0) setDocumentTypeId();
        else {
            MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
            setIsReceipt(dt.isSOTrx());
        }
        setDocumentNo();
        //
        if (getDateAcct() == null) setDateAcct(getDateTrx());
        //
        if (!isOverUnderPayment()) setOverUnderAmt(Env.ZERO);

        //	Organization
        if ((newRecord || isValueChanged("C_BankAccount_ID"))
                && getChargeId() == 0) // 	allow different org for charge
        {
            MBankAccount ba = MBankAccount.get(getBankAccountId());
            if (ba.getOrgId() != 0) setOrgId(ba.getOrgId());
        }

        // [ adempiere-Bugs-1885417 ] Validate BP on Payment Prepare or BeforeSave
        // there is bp and (invoice or order)
        if (getBusinessPartnerId() != 0 && (getInvoiceId() != 0 || getOrderId() != 0)) {
            if (getInvoiceId() != 0) {
                MInvoice inv = new MInvoice(null, getInvoiceId());
                if (inv.getBusinessPartnerId() != getBusinessPartnerId()) {
                    log.saveError("Error", MsgKt.parseTranslation("BP different from BP Invoice"));
                    return false;
                }
            }
            if (getOrderId() != 0) {
                MOrder ord = new MOrder(getOrderId());
                if (ord.getBusinessPartnerId() != getBusinessPartnerId()) {
                    log.saveError("Error", MsgKt.parseTranslation("BP different from BP Order"));
                    return false;
                }
            }
        }

        if (isProcessed()) {
            if (getCreditCardNumber() != null) {
                String encrpytedCCNo = PaymentUtil.encrpytCreditCard(getCreditCardNumber());
                if (!encrpytedCCNo.equals(getCreditCardNumber())) setCreditCardNumber(encrpytedCCNo);
            }

            if (getCreditCardVV() != null) {
                String encrpytedCvv = PaymentUtil.encrpytCvv(getCreditCardVV());
                if (!encrpytedCvv.equals(getCreditCardVV())) setCreditCardVV(encrpytedCvv);
            }
        }

        if (MSysConfig.getBooleanValue(
                MSysConfig.IBAN_VALIDATION, true, Env.getClientId())) {
            if (!Util.isEmpty(getIBAN())) {
                setIBAN(IBAN.normalizeIBAN(getIBAN()));
                if (!IBAN.isValid(getIBAN())) {
                    log.saveError("Error", "IBAN is invalid");
                    return false;
                }
            }
        }

        return true;
    } //	beforeSave

    /**
     * Document Status is Complete or Closed
     *
     * @return true if CO, CL or RE
     */
    public boolean isComplete() {
        String ds = getDocStatus();
        return X_C_Payment.DOCSTATUS_Completed.equals(ds)
                || X_C_Payment.DOCSTATUS_Closed.equals(ds)
                || X_C_Payment.DOCSTATUS_Reversed.equals(ds);
    } //	isComplete

    /**
     * Get Allocated Amt in Payment Currency
     *
     * @return amount or null
     */
    public BigDecimal getAllocatedAmt() {
        BigDecimal retValue = null;
        if (getChargeId() != 0) return getPayAmt();
        //
        String sql =
                "SELECT SUM(currencyConvert(al.Amount,"
                        + "ah.C_Currency_ID, p.C_Currency_ID,ah.DateTrx,p.C_ConversionType_ID, al.AD_Client_ID,al.orgId)) "
                        + "FROM C_AllocationLine al"
                        + " INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID=ah.C_AllocationHdr_ID) "
                        + " INNER JOIN C_Payment p ON (al.C_Payment_ID=p.C_Payment_ID) "
                        + "WHERE al.C_Payment_ID=?"
                        + " AND ah.IsActive='Y' AND al.IsActive='Y'";
        //	+ " AND al.C_Invoice_ID IS NOT NULL";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getPaymentId());
            rs = pstmt.executeQuery();
            if (rs.next()) retValue = rs.getBigDecimal(1);
        } catch (Exception e) {
            log.log(Level.SEVERE, "getAllocatedAmt", e);
        } finally {

            rs = null;
            pstmt = null;
        }
        //	log.fine("getAllocatedAmt - " + retValue);
        //	? ROUND(NVL(v_AllocatedAmt,0), 2);
        return retValue;
    } //	getAllocatedAmt

    /**
     * Test Allocation (and set allocated flag)
     *
     * @return true if updated
     */
    public boolean testAllocation() {
        //	Cash Trx always allocated!!! WHY???
    /* @Trifon - CashPayments
    		if (isCashTrx())
    		{
    			if (!isAllocated())
    			{
    				setIsAllocated(true);
    				return true;
    			}
    			return false;
    		}
    */
        //
        BigDecimal alloc = getAllocatedAmt();
        if (alloc == null) alloc = Env.ZERO;
        BigDecimal total = getPayAmt();
        if (!isReceipt()) total = total.negate();
        boolean test = total.compareTo(alloc) == 0;
        boolean change = test != isAllocated();
        if (change) setIsAllocated(test);
        if (log.isLoggable(Level.FINE))
            log.fine("Allocated=" + test + " (" + alloc + "=" + total + ")");
        return change;
    } //	testAllocation

    /**
     * Get Error Message
     *
     * @return error message
     */
    public String getErrorMessage() {
        return m_errorMessage;
    } //	getErrorMessage

    /**
     * ************************************************************************ Set Error Message
     *
     * @param errorMessage error message
     */
    public void setErrorMessage(String errorMessage) {
        m_errorMessage = errorMessage;
    } //	setErrorMessage

    /**
     * Set Bank Account for Payment.
     *
     * @param C_BankAccount_ID C_BankAccount_ID
     */
    public void setBankAccountId(int C_BankAccount_ID) {
        if (C_BankAccount_ID == 0) {
            setPaymentProcessor();
            if (getBankAccountId() == 0) throw new IllegalArgumentException("Can't find Bank Account");
        } else super.setBankAccountId(C_BankAccount_ID);
    } //	setBankAccountId

    /**
     * Set BankAccount and PaymentProcessor
     *
     * @return true if found
     */
    public boolean setPaymentProcessor() {
        return setPaymentProcessor(getTenderType(), getCreditCardType(), getPaymentProcessorId());
    } //	setPaymentProcessor

    /**
     * Set BankAccount and PaymentProcessor
     *
     * @param tender TenderType see TENDER_
     * @param CCType CC Type see CC_
     * @return true if found
     */
    public boolean setPaymentProcessor(String tender, String CCType, int C_PaymentProcessor_ID) {
        m_mBankAccountProcessor = null;
        //	Get Processor List
        if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0)
            m_mBankAccountProcessors =
                    MBankAccountProcessor.find(

                            tender,
                            CCType,
                            getClientId(),
                            getCurrencyId(),
                            getPayAmt()
                    );
        //	Relax Amount
        if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0)
            m_mBankAccountProcessors =
                    MBankAccountProcessor.find(

                            tender,
                            CCType,
                            getClientId(),
                            getCurrencyId(),
                            Env.ZERO
                    );
        if (m_mBankAccountProcessors == null || m_mBankAccountProcessors.length == 0) return false;

        //	Find the first right one
        for (int i = 0; i < m_mBankAccountProcessors.length; i++) {
            MBankAccountProcessor bankAccountProcessor = m_mBankAccountProcessors[i];
            if (bankAccountProcessor.accepts(tender, CCType)) {
                if (C_PaymentProcessor_ID == 0
                        || bankAccountProcessor.getPaymentProcessorId() == C_PaymentProcessor_ID) {
                    m_mBankAccountProcessor = m_mBankAccountProcessors[i];
                    break;
                }
            }
        }
        if (m_mBankAccountProcessor != null) {
            setBankAccountId(m_mBankAccountProcessor.getBankAccountId());
            setPaymentProcessorId(m_mBankAccountProcessor.getPaymentProcessorId());
        }
        //
        return m_mBankAccountProcessor != null;
    } //  setPaymentProcessor

    /**
     * ************************************************************************ Credit Card Number
     *
     * @param CreditCardNumber CreditCard Number
     */
    public void setCreditCardNumber(String CreditCardNumber) {
        super.setCreditCardNumber(MPaymentValidate.checkNumeric(CreditCardNumber));
    } //	setCreditCardNumber

    /**
     * Verification Code
     *
     * @param newCreditCardVV CC verification
     */
    public void setCreditCardVV(String newCreditCardVV) {
        super.setCreditCardVV(MPaymentValidate.checkNumeric(newCreditCardVV));
    } //	setCreditCardVV

    /**
     * Two Digit CreditCard MM
     *
     * @param CreditCardExpMM Exp month
     */
    public void setCreditCardExpMM(int CreditCardExpMM) {
        if (CreditCardExpMM < 1 || CreditCardExpMM > 12) ;
        else super.setCreditCardExpMM(CreditCardExpMM);
    } //	setCreditCardExpMM

    /**
     * Two digit CreditCard YY (til 2020)
     *
     * @param newCreditCardExpYY 2 or 4 digit year
     */
    public void setCreditCardExpYY(int newCreditCardExpYY) {
        int CreditCardExpYY = newCreditCardExpYY;
        if (newCreditCardExpYY > 1999) CreditCardExpYY = newCreditCardExpYY - 2000;
        super.setCreditCardExpYY(CreditCardExpYY);
    } //	setCreditCardExpYY

    /**
     * MICR
     *
     * @param MICR MICR
     */
    public void setMicr(String MICR) {
        super.setMicr(MPaymentValidate.checkNumeric(MICR));
    } //	setBankMICR

    /**
     * Routing No
     *
     * @param RoutingNo Routing No
     */
    public void setRoutingNo(String RoutingNo) {
        // super.setRoutingNo (MPaymentValidate.checkNumeric(RoutingNo));
        super.setRoutingNo(RoutingNo);
    } //	setBankRoutingNo

    //	---------------

    /**
     * Bank Account No
     *
     * @param AccountNo AccountNo
     */
    public void setAccountNo(String AccountNo) {
        super.setAccountNo(MPaymentValidate.checkNumeric(AccountNo));
    } //	setBankAccountNo

    /**
     * Check No
     *
     * @param CheckNo Check No
     */
    public void setCheckNo(String CheckNo) {
        super.setCheckNo(MPaymentValidate.checkNumeric(CheckNo));
    } //	setBankCheckNo

    /**
     * Set DocumentNo to Payment info. If there is a R_PnRef that is set automatically
     */
    private void setDocumentNo() {
        //	Cash Transfer
        if ("X".equals(getTenderType())) return;
        //	Current Document No
        String documentNo = getDocumentNo();
        //	Existing reversal
        if (documentNo != null && documentNo.indexOf(REVERSE_INDICATOR) >= 0) return;

        //	If external number exists - enforce it
        if (getPaymentReference() != null && getPaymentReference().length() > 0) {
            if (!getPaymentReference().equals(documentNo)) setDocumentNo(getPaymentReference());
            return;
        }

        documentNo = "";
        // globalqss - read configuration to assign credit card or check number number for Payments
        //	Credit Card
        if (X_C_Payment.TENDERTYPE_CreditCard.equals(getTenderType())) {
            if (MSysConfig.getBooleanValue(
                    MSysConfig.PAYMENT_OVERWRITE_DOCUMENTNO_WITH_CREDIT_CARD, true, getClientId())) {
                documentNo =
                        getCreditCardType()
                                + " "
                                + Obscure.obscure(getCreditCardNumber())
                                + " "
                                + getCreditCardExpMM()
                                + "/"
                                + getCreditCardExpYY();
            }
        }
        //	Own Check No
        else if (X_C_Payment.TENDERTYPE_Check.equals(getTenderType())
                && !isReceipt()
                && getCheckNo() != null
                && getCheckNo().length() > 0) {
            if (MSysConfig.getBooleanValue(
                    MSysConfig.PAYMENT_OVERWRITE_DOCUMENTNO_WITH_CHECK_ON_PAYMENT, true, getClientId())) {
                documentNo = getCheckNo();
            }
        }
        //	Customer Check: Routing: Account #Check
        else if (X_C_Payment.TENDERTYPE_Check.equals(getTenderType()) && isReceipt()) {
            if (MSysConfig.getBooleanValue(
                    MSysConfig.PAYMENT_OVERWRITE_DOCUMENTNO_WITH_CHECK_ON_RECEIPT, true, getClientId())) {
                if (getRoutingNo() != null) documentNo = getRoutingNo() + ": ";
                if (getAccountNo() != null) documentNo += getAccountNo();
                if (getCheckNo() != null) {
                    if (documentNo.length() > 0) documentNo += " ";
                    documentNo += "#" + getCheckNo();
                }
            }
        }

        //	Set Document No
        documentNo = documentNo.trim();
        if (documentNo.length() > 0) setDocumentNo(documentNo);
    } //	setDocumentNo

    /**
     * Set Refernce No (and Document No)
     *
     * @param R_PnRef reference
     */
    public void setPaymentReference(String R_PnRef) {
        super.setPaymentReference(R_PnRef);
        if (R_PnRef != null) setDocumentNo(R_PnRef);
    } //	setPaymentReference

    /**
     * Set Payment Amount
     *
     * @param PayAmt Pay Amt
     */
    public void setPayAmt(BigDecimal PayAmt) {
        super.setPayAmt(PayAmt == null ? Env.ZERO : PayAmt);
    } //	setPayAmt

    /**
     * Set Payment Amount
     *
     * @param C_Currency_ID currency
     * @param payAmt        amount
     */
    public void setAmount(int C_Currency_ID, BigDecimal payAmt) {
        if (C_Currency_ID == 0)
            C_Currency_ID = org.compiere.accounting.MClientKt.getClientWithAccounting().getCurrencyId();
        setCurrencyId(C_Currency_ID);
        setPayAmt(payAmt);
    } //  setAmount

    /**
     * Discount Amt
     *
     * @param DiscountAmt Discount
     */
    public void setDiscountAmt(BigDecimal DiscountAmt) {
        super.setDiscountAmt(DiscountAmt == null ? Env.ZERO : DiscountAmt);
    } //	setDiscountAmt

    /**
     * WriteOff Amt
     *
     * @param WriteOffAmt WriteOff
     */
    public void setWriteOffAmt(BigDecimal WriteOffAmt) {
        super.setWriteOffAmt(WriteOffAmt == null ? Env.ZERO : WriteOffAmt);
    } //	setWriteOffAmt

    /**
     * OverUnder Amt
     *
     * @param OverUnderAmt OverUnder
     */
    public void setOverUnderAmt(BigDecimal OverUnderAmt) {
        super.setOverUnderAmt(OverUnderAmt == null ? Env.ZERO : OverUnderAmt);
        setIsOverUnderPayment(getOverUnderAmt().compareTo(Env.ZERO) != 0);
    } //	setOverUnderAmt

    /**
     * Tax Amt
     *
     * @param TaxAmt Tax
     */
    public void setTaxAmt(BigDecimal TaxAmt) {
        super.setTaxAmt(TaxAmt == null ? Env.ZERO : TaxAmt);
    } //	setTaxAmt

    /**
     * Set Doc Type bases on IsReceipt
     */
    private void setDocumentTypeId() {
        setDocumentTypeId(isReceipt());
    } //	setDocumentTypeId

    /**
     * Set Doc Type
     *
     * @param isReceipt is receipt
     */
    public void setDocumentTypeId(boolean isReceipt) {
        setIsReceipt(isReceipt);
        String sql =
                "SELECT C_DocType_ID FROM C_DocType WHERE IsActive='Y' AND AD_Client_ID=? AND DocBaseType=? ORDER BY IsDefault DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getClientId());
            if (isReceipt) pstmt.setString(2, DOCBASETYPE_ARReceipt);
            else pstmt.setString(2, DOCBASETYPE_APPayment);
            rs = pstmt.executeQuery();
            if (rs.next()) setDocumentTypeId(rs.getInt(1));
            else log.warning("setDocType - NOT found - isReceipt=" + isReceipt);
        } catch (SQLException e) {
            log.log(Level.SEVERE, sql, e);
        } finally {

            rs = null;
            pstmt = null;
        }
    } //	setDocumentTypeId

    /**
     * Set Document Type
     *
     * @param C_DocType_ID doc type
     */
    public void setDocumentTypeId(int C_DocType_ID) {
        //	if (getDocumentNo() != null && getDocTypeId() != C_DocType_ID)
        //		setDocumentNo(null);
        super.setDocumentTypeId(C_DocType_ID);
    } //	setDocumentTypeId

    /**
     * Verify Document Type with Invoice
     *
     * @param pAllocs
     * @return true if ok
     */
    private boolean verifyDocType(I_C_PaymentAllocate[] pAllocs) {
        if (getDocumentTypeId() == 0) return false;
        //
        Boolean documentSO = null;
        //	Check Invoice First
        if (getInvoiceId() > 0) {
            String sql =
                    "SELECT idt.IsSOTrx "
                            + "FROM C_Invoice i"
                            + " INNER JOIN C_DocType idt ON (CASE WHEN i.C_DocType_ID=0 THEN i.C_DocTypeTarget_ID ELSE i.C_DocType_ID END=idt.C_DocType_ID) "
                            + "WHERE i.C_Invoice_ID=?";
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = prepareStatement(sql);
                pstmt.setInt(1, getInvoiceId());
                rs = pstmt.executeQuery();
                if (rs.next()) documentSO = new Boolean("Y".equals(rs.getString(1)));
            } catch (Exception e) {
                log.log(Level.SEVERE, sql, e);
            } finally {

                rs = null;
                pstmt = null;
            }
        } //	now Order - in Adempiere is allowed to pay PO or receive SO
        else if (getOrderId() > 0) {
            String sql =
                    "SELECT odt.IsSOTrx "
                            + "FROM C_Order o"
                            + " INNER JOIN C_DocType odt ON (o.C_DocType_ID=odt.C_DocType_ID) "
                            + "WHERE o.C_Order_ID=?";
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try {
                pstmt = prepareStatement(sql);
                pstmt.setInt(1, getOrderId());
                rs = pstmt.executeQuery();
                if (rs.next()) documentSO = new Boolean("Y".equals(rs.getString(1)));
            } catch (Exception e) {
                log.log(Level.SEVERE, sql, e);
            } finally {

                rs = null;
                pstmt = null;
            }
        } //	now Charge
        else if (getChargeId() > 0) {
            // do nothing about charge
        } // now payment allocate
        else {
            if (pAllocs.length > 0) {
                for (I_C_PaymentAllocate pAlloc : pAllocs) {
                    String sql =
                            "SELECT idt.IsSOTrx "
                                    + "FROM C_Invoice i"
                                    + " INNER JOIN C_DocType idt ON (i.C_DocType_ID=idt.C_DocType_ID) "
                                    + "WHERE i.C_Invoice_ID=?";
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;
                    try {
                        pstmt = prepareStatement(sql);
                        pstmt.setInt(1, pAlloc.getInvoiceId());
                        rs = pstmt.executeQuery();
                        if (rs.next()) {
                            if (documentSO != null) { // already set, compare with current
                                if (documentSO.booleanValue() != ("Y".equals(rs.getString(1)))) {
                                    return false;
                                }
                            } else {
                                documentSO = new Boolean("Y".equals(rs.getString(1)));
                            }
                        }
                    } catch (Exception e) {
                        log.log(Level.SEVERE, sql, e);
                    } finally {

                        rs = null;
                        pstmt = null;
                    }
                }
            }
        }

        //	DocumentType
        Boolean paymentSO = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "SELECT IsSOTrx " + "FROM C_DocType " + "WHERE C_DocType_ID=?";
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getDocumentTypeId());
            rs = pstmt.executeQuery();
            if (rs.next()) paymentSO = new Boolean("Y".equals(rs.getString(1)));
        } catch (Exception e) {
            log.log(Level.SEVERE, sql, e);
        } finally {

            rs = null;
            pstmt = null;
        }
        //	No Payment info
        if (paymentSO == null) return false;
        setIsReceipt(paymentSO.booleanValue());

        //	We have an Invoice .. and it does not match
        return documentSO == null || documentSO.booleanValue() == paymentSO.booleanValue();//	OK
    } //	verifyDocType

    /**
     * Verify Payment Allocate is ignored (must not exists) if the payment header has
     * charge/invoice/order
     *
     * @param pAllocs
     * @return true if ok
     */
    private boolean verifyPaymentAllocateVsHeader(I_C_PaymentAllocate[] pAllocs) {
        if (pAllocs.length > 0) {
            return getChargeId() <= 0 && getInvoiceId() <= 0 && getOrderId() <= 0;
        }
        return true;
    }

    /**
     * Verify Payment Allocate Sum must be equal to the Payment Amount
     *
     * @param pAllocs
     * @return true if ok
     */
    private boolean verifyPaymentAllocateSum(I_C_PaymentAllocate[] pAllocs) {
        BigDecimal sumPaymentAllocates = Env.ZERO;
        if (pAllocs.length > 0) {
            for (I_C_PaymentAllocate pAlloc : pAllocs)
                sumPaymentAllocates = sumPaymentAllocates.add(pAlloc.getAmount());
            if (getPayAmt().compareTo(sumPaymentAllocates) != 0) {
                if (isReceipt() && getPayAmt().compareTo(sumPaymentAllocates) < 0) {
                    return MSysConfig.getBooleanValue(
                            MSysConfig.ALLOW_OVER_APPLIED_PAYMENT, false, Env.getClientId());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Get Name of Credit Card
     *
     * @return Name
     */
    public String getCreditCardName() {
        return getCreditCardName(getCreditCardType());
    } //	getCreditCardName

    /**
     * Get Name of Credit Card
     *
     * @param CreditCardType credit card type
     * @return Name
     */
    public String getCreditCardName(String CreditCardType) {
        if (CreditCardType == null) return "--";
        else if (X_C_Payment.CREDITCARDTYPE_MasterCard.equals(CreditCardType)) return "MasterCard";
        else if (X_C_Payment.CREDITCARDTYPE_Visa.equals(CreditCardType)) return "Visa";
        else if (X_C_Payment.CREDITCARDTYPE_Amex.equals(CreditCardType)) return "Amex";
        else if (X_C_Payment.CREDITCARDTYPE_ATM.equals(CreditCardType)) return "ATM";
        else if (X_C_Payment.CREDITCARDTYPE_Diners.equals(CreditCardType)) return "Diners";
        else if (X_C_Payment.CREDITCARDTYPE_Discover.equals(CreditCardType)) return "Discover";
        else if (X_C_Payment.CREDITCARDTYPE_PurchaseCard.equals(CreditCardType)) return "PurchaseCard";
        return "?" + CreditCardType + "?";
    } //	getCreditCardName

    /**
     * Add to Description
     *
     * @param description text
     */
    public void addDescription(String description) {
        String desc = getDescription();
        if (desc == null) setDescription(description);
        else setDescription(desc + " | " + description);
    } //	addDescription

    /**
     * Get Pay Amt
     *
     * @param absolute if true the absolute amount (i.e. negative if payment)
     * @return amount
     */
    public BigDecimal getPayAmt(boolean absolute) {
        if (isReceipt()) return super.getPayAmt();
        return super.getPayAmt().negate();
    } //	getPayAmt

    /**
     * ************************************************************************ Process document
     *
     * @param processAction document action
     * @return true if performed
     */
    public boolean processIt(@NotNull String processAction) {
        m_processMsg = null;
        DocumentEngine engine = new DocumentEngine(this, getDocStatus());
        return engine.processIt(processAction, getDocAction());
    } //	process

    /**
     * Unlock Document.
     *
     * @return true if success
     */
    public boolean unlockIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setProcessing(false);
        return true;
    } //	unlockIt

    /**
     * Invalidate Document
     *
     * @return true if success
     */
    public boolean invalidateIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setDocAction(X_C_Payment.DOCACTION_Prepare);
        return true;
    } //	invalidateIt

    /**
     * ************************************************************************ Prepare Document
     *
     * @return new status (In Progress or Invalid)
     */
    @NotNull
    public String prepareIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        if (!MPaySelectionCheck.deleteGeneratedDraft(getPaymentId())) {
            m_processMsg = "Could not delete draft generated payment selection lines";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Std Period open?
        if (!MPeriod.isOpen(

                getDateAcct(),
                isReceipt() ? DOCBASETYPE_ARReceipt : DOCBASETYPE_APPayment,
                getOrgId())) {
            m_processMsg = "@PeriodClosed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Unsuccessful Online Payment
        if (isOnline() && !isApproved()) {
            if (getTransmissionResult() != null) m_processMsg = "@OnlinePaymentFailed@";
            else m_processMsg = "@PaymentNotProcessed@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Waiting Payment - Need to create Invoice & Shipment
        if (getOrderId() != 0 && getInvoiceId() == 0) { // 	see WebOrder.process
            MOrder order = new MOrder(getOrderId());
            if (X_C_Payment.DOCSTATUS_WaitingPayment.equals(order.getDocStatus())) {
                order.setPaymentId(getPaymentId());
                order.setDocAction(OrderConstants.DOCACTION_WaitComplete);
                // added AdempiereException by zuhri
                if (!order.processIt(OrderConstants.DOCACTION_WaitComplete))
                    throw new AdempiereException(
                            "Failed when processing document - " + order.getProcessMsg());
                // end added
                m_processMsg = order.getProcessMsg();
                order.saveEx();
                //	Set Invoice
                I_C_Invoice[] invoices = order.getInvoices();
                int length = invoices.length;
                if (length > 0) // 	get last invoice
                    setInvoiceId(invoices[length - 1].getInvoiceId());
                //
                if (getInvoiceId() == 0) {
                    m_processMsg = "@NotFound@ @C_Invoice_ID@";
                    return DocAction.Companion.getSTATUS_Invalid();
                }
            } //	WaitingPayment
        }

        I_C_PaymentAllocate[] pAllocs = MPaymentAllocate.get(this);

        //	Consistency of Invoice / Document Type and IsReceipt
        if (!verifyDocType(pAllocs)) {
            m_processMsg = "@PaymentDocTypeInvoiceInconsistent@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Payment Allocate is ignored if charge/invoice/order exists in header
        if (!verifyPaymentAllocateVsHeader(pAllocs)) {
            m_processMsg = "@PaymentAllocateIgnored@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Payment Amount must be equal to sum of Allocate amounts
        if (!verifyPaymentAllocateSum(pAllocs)) {
            m_processMsg = "@PaymentAllocateSumInconsistent@";
            return DocAction.Companion.getSTATUS_Invalid();
        }

        //	Do not pay when Credit Stop/Hold
        if (!isReceipt()) {
            I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
            if (X_C_BPartner.SOCREDITSTATUS_CreditStop.equals(bp.getSOCreditStatus())) {
                m_processMsg =
                        "@BPartnerCreditStop@ - @TotalOpenBalance@="
                                + bp.getTotalOpenBalance()
                                + ", @SO_CreditLimit@="
                                + bp.getSalesOrderCreditLimit();
                return DocAction.Companion.getSTATUS_Invalid();
            }
            if (X_C_BPartner.SOCREDITSTATUS_CreditHold.equals(bp.getSOCreditStatus())) {
                m_processMsg =
                        "@BPartnerCreditHold@ - @TotalOpenBalance@="
                                + bp.getTotalOpenBalance()
                                + ", @SO_CreditLimit@="
                                + bp.getSalesOrderCreditLimit();
                return DocAction.Companion.getSTATUS_Invalid();
            }
        }

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
        if (m_processMsg != null) return DocAction.Companion.getSTATUS_Invalid();

        m_justPrepared = true;
        if (!X_C_Payment.DOCACTION_Complete.equals(getDocAction()))
            setDocAction(X_C_Payment.DOCACTION_Complete);
        return DocAction.Companion.getSTATUS_InProgress();
    } //	prepareIt

    /**
     * Approve Document
     *
     * @return true if success
     */
    public boolean approveIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setIsApproved(true);
        return true;
    } //	approveIt

    /**
     * Reject Approval
     *
     * @return true if success
     */
    public boolean rejectIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        setIsApproved(false);
        return true;
    } //	rejectIt

    /**
     * ************************************************************************ Complete Document
     *
     * @return new status (Complete, In Progress, Invalid, Waiting ..)
     */
    @NotNull
    public CompleteActionResult completeIt() {
        //	Re-Check
        if (!m_justPrepared) {
            String status = prepareIt();
            m_justPrepared = false;
            if (!DocAction.Companion.getSTATUS_InProgress().equals(status))
                return new CompleteActionResult(status);
        }

        // Set the definite document number after completed (if needed)
        setDefiniteDocumentNo();

        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
        if (m_processMsg != null)
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());

        //	Implicit Approval
        if (!isApproved()) approveIt();
        if (log.isLoggable(Level.INFO)) log.info(toString());

        //	Charge Handling
        boolean createdAllocationRecords = false;
        if (getChargeId() != 0) {
            setIsAllocated(true);
        } else {
            createdAllocationRecords = allocateIt(); // 	Create Allocation Records
            testAllocation();
        }

        //	Project update
        getProjectId();//	MProject project = new MProject(getProjectId());
//	Update BP for Prepayments
        if (getBusinessPartnerId() != 0
                && getInvoiceId() == 0
                && getChargeId() == 0
                && MPaymentAllocate.get(this).length == 0
                && !createdAllocationRecords) {
            I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
            forUpdate(bp);
            //	Update total balance to include this payment
            BigDecimal payAmt =
                    MConversionRate.convertBase(

                            getPayAmt(),
                            getCurrencyId(),
                            getDateAcct(),
                            getConversionTypeId(),
                            getClientId(),
                            getOrgId());
            if (payAmt == null) {
                m_processMsg =
                        MConversionRateUtil.getErrorMessage(

                                "ErrorConvertingCurrencyToBaseCurrency",
                                getCurrencyId(),
                                MClientKt.getClientWithAccounting().getCurrencyId(),
                                getConversionTypeId(),
                                getDateAcct(),
                                null);
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            //	Total Balance
            BigDecimal newBalance = bp.getTotalOpenBalance();
            if (newBalance == null) newBalance = Env.ZERO;
            if (isReceipt()) newBalance = newBalance.subtract(payAmt);
            else newBalance = newBalance.add(payAmt);

            bp.setTotalOpenBalance(newBalance);
            bp.setSOCreditStatus();
            bp.saveEx();
        }

        //	Counter Doc
        MPayment counter = createCounterDoc();
        if (counter != null) m_processMsg += " @CounterDoc@: @C_Payment_ID@=" + counter.getDocumentNo();

        // @Trifon - CashPayments
        // if ( getTenderType().equals("X") ) {
        if (isCashbookTrx()) {
            // Create Cash Book entry
            if (getCashBookId() <= 0) {
                log.saveError("Error", MsgKt.parseTranslation("@Mandatory@: @C_CashBook_ID@"));
                m_processMsg = "@NoCashBook@";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            I_C_Cash cash =
                    MCash.get(getOrgId(), getDateAcct(), getCurrencyId());
            if (cash == null || cash.getId() == 0) {
                m_processMsg = "@NoCashBook@";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
            MCashLine cl = new MCashLine(cash);
            cl.setCashType(X_C_CashLine.CASHTYPE_GeneralReceipts);
            cl.setDescription("Generated From Payment #" + getDocumentNo());
            cl.setCurrencyId(this.getCurrencyId());
            cl.setPaymentId(getPaymentId()); // Set Reference to payment.
            StringBuilder info = new StringBuilder();
            info.append("Cash journal ( ").append(cash.getDocumentNo()).append(" )");
            m_processMsg = info.toString();
            //	Amount
            BigDecimal amt = this.getPayAmt();
            cl.setAmount(amt);
            //
            cl.setDiscountAmt(Env.ZERO);
            cl.setWriteOffAmt(Env.ZERO);
            cl.setIsGenerated(true);

            if (!cl.save()) {
                m_processMsg = "Could not save Cash Journal Line";
                return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
            }
        }
        // End Trifon - CashPayments

        //	update C_Invoice.C_Payment_ID and C_Order.C_Payment_ID reference
        if (getInvoiceId() != 0) {
            MInvoice inv = new MInvoice(null, getInvoiceId());
            if (inv.getPaymentId() != getPaymentId()) {
                inv.setPaymentId(getPaymentId());
                inv.saveEx();
            }
        }
        if (getOrderId() != 0) {
            MOrder ord = new MOrder(getOrderId());
            if (ord.getPaymentId() != getPaymentId()) {
                ord.setPaymentId(getPaymentId());
                ord.saveEx();
            }
        }

        //	User Validation
        String valid =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
        if (valid != null) {
            m_processMsg = valid;
            return new CompleteActionResult(DocAction.Companion.getSTATUS_Invalid());
        }

        //
        setProcessed(true);
        setDocAction(X_C_Payment.DOCACTION_Close);
        return new CompleteActionResult(DocAction.Companion.getSTATUS_Completed());
    } //	completeIt

    /**
     * Set the definite document number after completed
     */
    private void setDefiniteDocumentNo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        if (dt.isOverwriteDateOnComplete()) {
            setDateTrx(new Timestamp(System.currentTimeMillis()));
            if (getDateAcct().before(getDateTrx())) {
                setDateAcct(getDateTrx());
                MPeriod.testPeriodOpen(getDateAcct(), getDocumentTypeId(), getOrgId());
            }
        }
        if (dt.isOverwriteSeqOnComplete()) {
            String value = MSequence.getDocumentNo(getDocumentTypeId(), true, this);
            if (value != null) setDocumentNo(value);
        }
    }

    /**
     * Create Counter Document
     *
     * @return payment
     */
    private MPayment createCounterDoc() {
        //	Is this a counter doc ?
        if (getReferencedPaymentId() != 0) return null;

        //	Org Must be linked to BPartner
        MOrg org = MOrgKt.getOrg(getOrgId());
        int counterC_BPartner_ID = org.getLinkedBusinessPartnerId();
        if (counterC_BPartner_ID == 0) return null;
        //	Business Partner needs to be linked to Org
        I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
        int counterAD_Org_ID = bp.getLinkedOrganizationId();
        if (counterAD_Org_ID == 0) return null;

        I_C_BPartner counterBP = getBusinessPartnerService().getById(counterC_BPartner_ID);
        //	MOrgInfo counterOrgInfo = MOrgInfoKt.getOrganizationInfo(counterAD_Org_ID);
        if (log.isLoggable(Level.INFO)) log.info("Counter BP=" + counterBP.getName());

        //	Document Type
        int C_DocTypeTarget_ID = 0;
        MDocTypeCounter counterDT = MDocTypeCounter.getCounterDocType(getDocumentTypeId());
        if (counterDT != null) {
            if (log.isLoggable(Level.FINE)) log.fine(counterDT.toString());
            if (!counterDT.isCreateCounter() || !counterDT.isValid()) return null;
            C_DocTypeTarget_ID = counterDT.getCounterDocTypeId();
        } else //	indirect
        {
            C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocTypeId(getDocumentTypeId());
            if (log.isLoggable(Level.FINE)) log.fine("Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID);
            if (C_DocTypeTarget_ID <= 0) return null;
        }

        //	Deep Copy
        MPayment counter = new MPayment(0);
        counter.setOrgId(counterAD_Org_ID);
        counter.setBusinessPartnerId(counterBP.getBusinessPartnerId());
        counter.setIsReceipt(!isReceipt());
        counter.setDocumentTypeId(C_DocTypeTarget_ID);
        counter.setTrxType(getTrxType());
        counter.setTenderType(getTenderType());
        //
        counter.setPayAmt(getPayAmt());
        counter.setDiscountAmt(getDiscountAmt());
        counter.setTaxAmt(getTaxAmt());
        counter.setWriteOffAmt(getWriteOffAmt());
        counter.setIsOverUnderPayment(isOverUnderPayment());
        counter.setOverUnderAmt(getOverUnderAmt());
        counter.setCurrencyId(getCurrencyId());
        counter.setConversionTypeId(getConversionTypeId());
        //
        counter.setDateTrx(getDateTrx());
        counter.setDateAcct(getDateAcct());
        counter.setReferencedPaymentId(getPaymentId());
        //
        String sql =
                "SELECT C_BankAccount_ID FROM C_BankAccount "
                        + "WHERE C_Currency_ID=? AND orgId IN (0,?) AND IsActive='Y' "
                        + "ORDER BY IsDefault DESC";
        int C_BankAccount_ID = getSQLValue(sql, getCurrencyId(), counterAD_Org_ID);
        counter.setBankAccountId(C_BankAccount_ID);

        //	References
        counter.setBusinessActivityId(getBusinessActivityId());
        counter.setCampaignId(getCampaignId());
        counter.setProjectId(getProjectId());
        counter.setUser1Id(getUser1Id());
        counter.setUser2Id(getUser2Id());
        counter.saveEx();
        if (log.isLoggable(Level.FINE)) log.fine(counter.toString());
        setReferencedPaymentId(counter.getPaymentId());

        //	Document Action
        if (counterDT != null) {
            if (counterDT.getDocAction() != null) {
                counter.setDocAction(counterDT.getDocAction());
                // added AdempiereException by zuhri
                if (!counter.processIt(counterDT.getDocAction()))
                    throw new AdempiereException(
                            "Failed when rocessing document - " + counter.getProcessMsg());
                // end added
                counter.saveEx();
            }
        }
        return counter;
    } //	createCounterDoc

    /**
     * Allocate It. Only call when there is NO allocation as it will create duplicates. If an invoice
     * exists, it allocates that otherwise it allocates Payment Selection.
     *
     * @return true if allocated
     */
    public boolean allocateIt() {
        //	Create invoice Allocation -	See also MCash.completeIt
        if (getInvoiceId() != 0) {
            return allocateInvoice();
        }
        //	Invoices of a AP Payment Selection
        if (allocatePaySelection()) return true;

        if (getOrderId() != 0) return false;

        //	Allocate to multiple Payments based on entry
        I_C_PaymentAllocate[] pAllocs = MPaymentAllocate.get(this);
        if (pAllocs.length == 0) return false;

        MAllocationHdr alloc =
                new MAllocationHdr(

                        false,
                        getDateTrx(),
                        getCurrencyId(),
                        MsgKt.translate("C_Payment_ID") + ": " + getDocumentNo()
                );
        alloc.setOrgId(getOrgId());
        alloc.setDateAcct(
                getDateAcct()); // in case date acct is different from datetrx in payment; IDEMPIERE-1532
        // tbayen
        if (!alloc.save()) {
            log.severe("P.Allocations not created");
            return false;
        }
        //	Lines
        for (I_C_PaymentAllocate pa : pAllocs) {
            MAllocationLine aLine = null;
            if (isReceipt())
                aLine =
                        new MAllocationLine(
                                alloc,
                                pa.getAmount(),
                                pa.getDiscountAmt(),
                                pa.getWriteOffAmt(),
                                pa.getOverUnderAmt());
            else
                aLine =
                        new MAllocationLine(
                                alloc,
                                pa.getAmount().negate(),
                                pa.getDiscountAmt().negate(),
                                pa.getWriteOffAmt().negate(),
                                pa.getOverUnderAmt().negate());
            aLine.setDocInfo(pa.getBusinessPartnerId(), 0, pa.getInvoiceId());
            aLine.setPaymentInfo(getPaymentId(), 0);
            if (!aLine.save()) log.warning("P.Allocations - line not saved");
            else {
                pa.setAllocationLineId(aLine.getAllocationLineId());
                pa.saveEx();
            }
        }
        // added AdempiereException by zuhri
        if (!alloc.processIt(DocAction.Companion.getACTION_Complete()))
            throw new AdempiereException("Failed when processing document - " + alloc.getProcessMsg());
        // end added
        m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();
        return alloc.save();
    } //	allocateIt

    /**
     * Allocate single AP/AR Invoice
     *
     * @return true if allocated
     */
    private boolean allocateInvoice() {
        //	calculate actual allocation
        BigDecimal allocationAmt = getPayAmt(); // 	underpayment
        if (getOverUnderAmt().signum() < 0 && getPayAmt().signum() > 0)
            allocationAmt = allocationAmt.add(getOverUnderAmt()); // 	overpayment (negative)

        MAllocationHdr alloc =
                new MAllocationHdr(

                        false,
                        getDateTrx(),
                        getCurrencyId(),
                        MsgKt.translate("C_Payment_ID") + ": " + getDocumentNo() + " [1]"
                );
        alloc.setOrgId(getOrgId());
        alloc.setDateAcct(getDateAcct()); // in case date acct is different from datetrx in payment
        alloc.saveEx();
        MAllocationLine aLine = null;
        if (isReceipt())
            aLine =
                    new MAllocationLine(
                            alloc, allocationAmt, getDiscountAmt(), getWriteOffAmt(), getOverUnderAmt());
        else
            aLine =
                    new MAllocationLine(
                            alloc,
                            allocationAmt.negate(),
                            getDiscountAmt().negate(),
                            getWriteOffAmt().negate(),
                            getOverUnderAmt().negate());
        aLine.setDocInfo(getBusinessPartnerId(), 0, getInvoiceId());
        aLine.setPaymentId(getPaymentId());
        aLine.saveEx();
        // added AdempiereException by zuhri
        if (!alloc.processIt(DocAction.Companion.getACTION_Complete()))
            throw new AdempiereException("Failed when processing document - " + alloc.getProcessMsg());
        // end added
        alloc.saveEx();
        m_justCreatedAllocInv = alloc;
        m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();

        //	Get Project from Invoice
        int C_Project_ID =
                getSQLValue(
                        "SELECT MAX(C_Project_ID) FROM C_Invoice WHERE C_Invoice_ID=?",
                        getInvoiceId());
        if (C_Project_ID > 0 && getProjectId() == 0) setProjectId(C_Project_ID);
        else if (C_Project_ID > 0 && getProjectId() > 0 && C_Project_ID != getProjectId())
            log.warning(
                    "Invoice C_Project_ID=" + C_Project_ID + " <> Payment C_Project_ID=" + getProjectId());
        return true;
    } //	allocateInvoice

    /**
     * Allocate Payment Selection
     *
     * @return true if allocated
     */
    private boolean allocatePaySelection() {
        MAllocationHdr alloc =
                new MAllocationHdr(

                        false,
                        getDateTrx(),
                        getCurrencyId(),
                        MsgKt.translate("C_Payment_ID") + ": " + getDocumentNo() + " [n]"
                );
        alloc.setOrgId(getOrgId());
        alloc.setDateAcct(getDateAcct()); // in case date acct is different from datetrx in payment

        String sql =
                "SELECT psc.C_BPartner_ID, psl.C_Invoice_ID, psl.IsSOTrx, " //	1..3
                        + " psl.PayAmt, psl.DiscountAmt, psl.DifferenceAmt, psl.OpenAmt, psl.WriteOffAmt " // 4..8
                        + "FROM C_PaySelectionLine psl"
                        + " INNER JOIN C_PaySelectionCheck psc ON (psl.C_PaySelectionCheck_ID=psc.C_PaySelectionCheck_ID) "
                        + "WHERE psc.C_Payment_ID=?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = prepareStatement(sql);
            pstmt.setInt(1, getPaymentId());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int C_BPartner_ID = rs.getInt(1);
                int C_Invoice_ID = rs.getInt(2);
                if (C_BPartner_ID == 0 && C_Invoice_ID == 0) continue;
                boolean isSOTrx = "Y".equals(rs.getString(3));
                BigDecimal PayAmt = rs.getBigDecimal(4);
                BigDecimal DiscountAmt = rs.getBigDecimal(5);
                BigDecimal WriteOffAmt = rs.getBigDecimal(8);
                BigDecimal OpenAmt = rs.getBigDecimal(7);
                BigDecimal OverUnderAmt =
                        OpenAmt.subtract(PayAmt).subtract(DiscountAmt).subtract(WriteOffAmt);
                //
                if (alloc.getId() == 0 && !alloc.save()) {
                    log.log(Level.SEVERE, "Could not create Allocation Hdr");
                    return false;
                }
                MAllocationLine aLine = null;
                if (isSOTrx)
                    aLine = new MAllocationLine(alloc, PayAmt, DiscountAmt, WriteOffAmt, OverUnderAmt);
                else
                    aLine =
                            new MAllocationLine(
                                    alloc,
                                    PayAmt.negate(),
                                    DiscountAmt.negate(),
                                    WriteOffAmt.negate(),
                                    OverUnderAmt.negate());
                aLine.setDocInfo(C_BPartner_ID, 0, C_Invoice_ID);
                aLine.setPaymentId(getPaymentId());
                if (!aLine.save()) log.log(Level.SEVERE, "Could not create Allocation Line");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "allocatePaySelection", e);
        } finally {

            rs = null;
            pstmt = null;
        }

        //	Should start WF
        boolean ok = true;
        if (alloc.getId() == 0) {
            if (log.isLoggable(Level.FINE))
                log.fine("No Allocation created - C_Payment_ID=" + getPaymentId());
            ok = false;
        } else {
            // added Adempiere Exception by zuhri
            if (alloc.processIt(DocAction.Companion.getACTION_Complete())) ok = alloc.save();
            else
                throw new AdempiereException("Failed when processing document - " + alloc.getProcessMsg());
            // end added by zuhri
            m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();
        }
        return ok;
    } //	allocatePaySelection

    /**
     * De-allocate Payment. Unkink Invoices and Orders and delete Allocations
     *
     * @param accrual
     */
    private void deAllocate(boolean accrual) {
        // if (getOrderId() != 0) setOrderId(0); // IDEMPIERE-1764
        //	if (getInvoiceId() == 0)
        //		return;
        //	De-Allocate all
        MAllocationHdr[] allocations =
                MAllocationHdr.getOfPayment(getPaymentId());
        if (log.isLoggable(Level.FINE)) log.fine("#" + allocations.length);
        for (int i = 0; i < allocations.length; i++) {
            if (X_C_Payment.DOCSTATUS_Reversed.equals(allocations[i].getDocStatus())
                    || X_C_Payment.DOCSTATUS_Voided.equals(allocations[i].getDocStatus())) {
                continue;
            }

            if (accrual) {
                allocations[i].setDocAction(DocAction.Companion.getACTION_Reverse_Accrual());
                if (!allocations[i].processIt(DocAction.Companion.getACTION_Reverse_Accrual()))
                    throw new AdempiereException(allocations[i].getProcessMsg());
            } else {
                allocations[i].setDocAction(DocAction.Companion.getACTION_Reverse_Correct());
                if (!allocations[i].processIt(DocAction.Companion.getACTION_Reverse_Correct()))
                    throw new AdempiereException(allocations[i].getProcessMsg());
            }
            allocations[i].saveEx();
        }

        // 	Unlink (in case allocation did not get it)
        if (getInvoiceId() != 0) {
            //	Invoice
            String sql =
                    "UPDATE C_Invoice "
                            + "SET C_Payment_ID = NULL, IsPaid='N' "
                            + "WHERE C_Invoice_ID="
                            + getInvoiceId()
                            + " AND C_Payment_ID="
                            + getPaymentId();
            int no = executeUpdate(sql);
            if (no != 0) if (log.isLoggable(Level.FINE)) log.fine("Unlink Invoice #" + no);
            //	Order
            sql =
                    "UPDATE C_Order o "
                            + "SET C_Payment_ID = NULL "
                            + "WHERE EXISTS (SELECT * FROM C_Invoice i "
                            + "WHERE o.C_Order_ID=i.C_Order_ID AND i.C_Invoice_ID="
                            + getInvoiceId()
                            + ")"
                            + " AND C_Payment_ID="
                            + getPaymentId();
            no = executeUpdate(sql);
            if (no != 0) if (log.isLoggable(Level.FINE)) log.fine("Unlink Order #" + no);
        }
        //
        setInvoiceId(0);
        setIsAllocated(false);
    } //	deallocate

    /**
     * Void Document.
     *
     * @return true if success
     */
    public boolean voidIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());

        if (X_C_Payment.DOCSTATUS_Closed.equals(getDocStatus())
                || X_C_Payment.DOCSTATUS_Reversed.equals(getDocStatus())
                || X_C_Payment.DOCSTATUS_Voided.equals(getDocStatus())) {
            m_processMsg = "Document Closed: " + getDocStatus();
            setDocAction(X_C_Payment.DOCACTION_None);
            return false;
        }
        //	If on Bank Statement, don't void it - reverse it
        if (getBankStatementLineId() > 0) return reverseCorrectIt();

        //	Not Processed
        if (X_C_Payment.DOCSTATUS_Drafted.equals(getDocStatus())
                || X_C_Payment.DOCSTATUS_Invalid.equals(getDocStatus())
                || X_C_Payment.DOCSTATUS_InProgress.equals(getDocStatus())
                || X_C_Payment.DOCSTATUS_Approved.equals(getDocStatus())
                || X_C_Payment.DOCSTATUS_NotApproved.equals(getDocStatus())) {
            // Before Void
            m_processMsg =
                    ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_VOID);
            if (m_processMsg != null) return false;

            if (!voidOnlinePayment()) return false;

            addDescription(MsgKt.getMsg("Voided") + " (" + getPayAmt() + ")");
            setPayAmt(Env.ZERO);
            setDiscountAmt(Env.ZERO);
            setWriteOffAmt(Env.ZERO);
            setOverUnderAmt(Env.ZERO);
            setIsAllocated(false);
            //	Unlink & De-Allocate
            deAllocate(false);
        } else {
            boolean accrual = false;
            try {
                MPeriod.testPeriodOpen(getDateAcct(), getDocumentTypeId(), getOrgId());
            } catch (PeriodClosedException e) {
                accrual = true;
            }

            if (accrual) return reverseAccrualIt();
            else return reverseCorrectIt();
        }

        //
        // After Void
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_VOID);
        if (m_processMsg != null) return false;

        setProcessed(true);
        setDocAction(X_C_Payment.DOCACTION_None);
        return true;
    } //	voidIt

    /**
     * Close Document.
     *
     * @return true if success
     */
    public boolean closeIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_CLOSE);
        if (m_processMsg != null) return false;

        setDocAction(X_C_Payment.DOCACTION_None);

        // After Close
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_CLOSE);
        return m_processMsg == null;
    } //	closeIt

    /**
     * Reverse Correction
     *
     * @return true if success
     */
    public boolean reverseCorrectIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSECORRECT);
        if (m_processMsg != null) return false;

        StringBuilder info = reverse(false);
        if (info == null) {
            return false;
        }

        // After reverseCorrect
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSECORRECT);
        if (m_processMsg != null) return false;

        m_processMsg = info.toString();
        return true;
    } //	reverseCorrectionIt

    private StringBuilder reverse(boolean accrual) {
        if (!voidOnlinePayment()) return null;

        //	Std Period open?
        Timestamp dateAcct = accrual ? Env.getContextAsDate() : getDateAcct();
        if (dateAcct == null) {
            dateAcct = new Timestamp(System.currentTimeMillis());
        }
        MPeriod.testPeriodOpen(dateAcct, getDocumentTypeId(), getOrgId());

        //	Create Reversal
        MPayment reversal = new MPayment(0);
        PO.copyValues(this, reversal);
        reversal.setClientOrg(this);
        // reversal.setOrderId(0); // IDEMPIERE-1764
        reversal.setInvoiceId(0);
        reversal.setDateAcct(dateAcct);
        //
        reversal.setDocumentNo(getDocumentNo() + REVERSE_INDICATOR); // 	indicate reversals
        reversal.setDocStatus(X_C_Payment.DOCSTATUS_Drafted);
        reversal.setDocAction(X_C_Payment.DOCACTION_Complete);
        //
        reversal.setPayAmt(getPayAmt().negate());
        reversal.setDiscountAmt(getDiscountAmt().negate());
        reversal.setWriteOffAmt(getWriteOffAmt().negate());
        reversal.setOverUnderAmt(getOverUnderAmt().negate());
        //
        reversal.setIsAllocated(true);
        reversal.setIsReconciled(false);
        reversal.setIsOnline(false);
        reversal.setIsApproved(true);
        reversal.setPaymentReference(null);
        reversal.setTransmissionResult(null);
        reversal.setResponseMessage(null);
        reversal.setAuthorizationCode(null);
        reversal.setResponseInfo(null);
        reversal.setProcessing(false);
        reversal.setOProcessing("N");
        reversal.setProcessed(false);
        reversal.setPosted(false);
        reversal.setDescription(getDescription());
        reversal.addDescription("{->" + getDocumentNo() + ")");
        // FR [ 1948157  ]
        reversal.setReversalId(getPaymentId());
        reversal.saveEx();
        //	Post Reversal
        if (!reversal.processIt(DocAction.Companion.getACTION_Complete())) {
            m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
            return null;
        }
        reversal.closeIt();
        reversal.setDocStatus(X_C_Payment.DOCSTATUS_Reversed);
        reversal.setDocAction(X_C_Payment.DOCACTION_None);
        reversal.saveEx();

        //	Unlink & De-Allocate
        deAllocate(accrual);
        setIsAllocated(true); // 	the allocation below is overwritten
        //	Set Status
        addDescription("(" + reversal.getDocumentNo() + "<-)");
        setDocStatus(X_C_Payment.DOCSTATUS_Reversed);
        setDocAction(X_C_Payment.DOCACTION_None);
        setProcessed(true);
        // FR [ 1948157  ]
        setReversalId(reversal.getPaymentId());

        StringBuilder info = new StringBuilder(reversal.getDocumentNo());

        //	Create automatic Allocation
        MAllocationHdr alloc =
                new MAllocationHdr(

                        false,
                        getDateTrx(),
                        getCurrencyId(),
                        MsgKt.translate("C_Payment_ID") + ": " + reversal.getDocumentNo()
                );
        alloc.setOrgId(getOrgId());
        alloc.setDateAcct(
                dateAcct); // dateAcct variable already take into account the accrual parameter
        alloc.saveEx();

        //	Original Allocation
        MAllocationLine aLine =
                new MAllocationLine(alloc, getPayAmt(true), Env.ZERO, Env.ZERO, Env.ZERO);
        aLine.setDocInfo(getBusinessPartnerId(), 0, 0);
        aLine.setPaymentInfo(getPaymentId(), 0);
        if (!aLine.save()) log.warning("Automatic allocation - line not saved");
        //	Reversal Allocation
        aLine = new MAllocationLine(alloc, reversal.getPayAmt(true), Env.ZERO, Env.ZERO, Env.ZERO);
        aLine.setDocInfo(reversal.getBusinessPartnerId(), 0, 0);
        aLine.setPaymentInfo(reversal.getPaymentId(), 0);
        if (!aLine.save()) log.warning("Automatic allocation - reversal line not saved");

        // added AdempiereException by zuhri
        if (!alloc.processIt(DocAction.Companion.getACTION_Complete()))
            throw new AdempiereException("Failed when processing document - " + alloc.getProcessMsg());
        // end added
        alloc.saveEx();
        //
        info.append(" - @C_AllocationHdr_ID@: ").append(alloc.getDocumentNo());

        //	Update BPartner
        if (getBusinessPartnerId() != 0) {
            I_C_BPartner bp = getBusinessPartnerService().getById(getBusinessPartnerId());
            bp.setTotalOpenBalance();
            bp.saveEx();
        }

        return info;
    }

    /**
     * Get Bank Statement Line of payment or 0
     *
     * @return id or 0
     */
    private int getBankStatementLineId() {
        String sql = "SELECT C_BankStatementLine_ID FROM C_BankStatementLine WHERE C_Payment_ID=?";
        int id = getSQLValue(sql, getPaymentId());
        if (id < 0) return 0;
        return id;
    } //	getBankStatementLine_ID

    /**
     * Reverse Accrual - none
     *
     * @return true if success
     */
    public boolean reverseAccrualIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());

        // Before reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        StringBuilder info = reverse(true);
        if (info == null) {
            return false;
        }

        // After reverseAccrual
        m_processMsg =
                ModelValidationEngine.get()
                        .fireDocValidate(this, ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
        if (m_processMsg != null) return false;

        m_processMsg = info.toString();
        return true;
    } //	reverseAccrualIt

    /**
     * Re-activate
     *
     * @return true if success
     */
    public boolean reActivateIt() {
        if (log.isLoggable(Level.INFO)) log.info(toString());
        // Before reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_REACTIVATE);
        if (m_processMsg != null) return false;

        if (!reverseCorrectIt()) return false;

        // After reActivate
        m_processMsg =
                ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_REACTIVATE);
        return m_processMsg == null;
    } //	reActivateIt

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("MPayment[");
        sb.append(getId())
                .append("-")
                .append(getDocumentNo())
                .append(",Receipt=")
                .append(isReceipt())
                .append(",PayAmt=")
                .append(getPayAmt())
                .append(",Discount=")
                .append(getDiscountAmt())
                .append(",WriteOff=")
                .append(getWriteOffAmt())
                .append(",OverUnder=")
                .append(getOverUnderAmt());
        return sb.toString();
    } //	toString

    /**
     * Get Document Info
     *
     * @return document info (untranslated)
     */
    @NotNull
    public String getDocumentInfo() {
        MDocType dt = MDocTypeKt.getDocumentType(getDocumentTypeId());
        return dt.getNameTrl() + " " + getDocumentNo();
    } //	getDocumentInfo

    /**
     * *********************************************************************** Get Summary
     *
     * @return Summary of Document
     */
    @NotNull
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDocumentNo());
        //	: Total Lines = 123.00 (#1)
        sb.append(": ")
                .append(MsgKt.translate("PayAmt"))
                .append("=")
                .append(getPayAmt())
                .append(",")
                .append(MsgKt.translate("WriteOffAmt"))
                .append("=")
                .append(getWriteOffAmt());
        //	 - Description
        if (getDescription() != null && getDescription().length() > 0)
            sb.append(" - ").append(getDescription());
        return sb.toString();
    } //	getSummary

    /**
     * Get Process Message
     *
     * @return clear text error message
     */
    @NotNull
    public String getProcessMsg() {
        return m_processMsg;
    } //	getProcessMsg

    /**
     * Get Document Owner (Responsible)
     *
     * @return AD_User_ID
     */
    public int getDocumentUserId() {
        return getCreatedBy();
    } //	getDoc_User_ID

    /**
     * Get Document Approval Amount
     *
     * @return amount payment(AP) or write-off(AR)
     */
    @NotNull
    public BigDecimal getApprovalAmt() {
        if (isReceipt()) return getWriteOffAmt();
        return getPayAmt();
    } //	getApprovalAmt

    @Override
    public void setProcessUI(IProcessUI processMonitor) {
        m_processUI = processMonitor;
    }

    public MPaymentTransaction createPaymentTransaction(String trxName) {
        MPaymentTransaction paymentTransaction = new MPaymentTransaction(0);
        paymentTransaction.setAccountCity(getAccountCity());
        paymentTransaction.setAccountCountry(getAccountCountry());
        paymentTransaction.setAccountEMail(getAccountEMail());
        paymentTransaction.setPaymentIdentificationDriverLicence(getPaymentIdentificationDriverLicence());
        paymentTransaction.setSocialSecurityNoPaymentIdentification(getSocialSecurityNoPaymentIdentification());
        paymentTransaction.setAccountName(getAccountName());
        paymentTransaction.setAccountState(getAccountState());
        paymentTransaction.setAccountStreet(getAccountStreet());
        paymentTransaction.setAccountZip(getAccountZip());
        paymentTransaction.setAccountNo(getAccountNo());
        paymentTransaction.setIBAN(getIBAN());
        paymentTransaction.setOrgId(getOrgId());
        paymentTransaction.setBankAccountId(getBankAccountId());
        paymentTransaction.setBusinessPartnerBankAccountId(getBusinessPartnerBankAccountId());
        paymentTransaction.setBusinessPartnerId(getBusinessPartnerId());
        paymentTransaction.setConversionTypeId(getConversionTypeId());
        paymentTransaction.setCurrencyId(getCurrencyId());
        paymentTransaction.setInvoiceId(getInvoiceId());
        paymentTransaction.setOrderId(getOrderId());
        paymentTransaction.setPaymentProcessorId(getPaymentProcessorId());
        paymentTransaction.setPOSTenderTypeId(getPOSTenderTypeId());
        paymentTransaction.setCheckNo(getCheckNo());
        paymentTransaction.setCreditCardExpMM(getCreditCardExpMM());
        paymentTransaction.setCreditCardExpYY(getCreditCardExpYY());
        paymentTransaction.setCreditCardNumber(getCreditCardNumber());
        paymentTransaction.setCreditCardType(getCreditCardType());
        paymentTransaction.setCreditCardVV(getCreditCardVV());
        paymentTransaction.setCustomerAddressID(getCustomerAddressID());
        paymentTransaction.setCustomerPaymentProfileID(getCustomerPaymentProfileID());
        paymentTransaction.setCustomerProfileID(getCustomerProfileID());
        paymentTransaction.setDateTrx(getDateTrx());
        paymentTransaction.setDescription(getDescription());
        paymentTransaction.setIsActive(isActive());
        paymentTransaction.setIsApproved(isApproved());
        paymentTransaction.setIsDelayedCapture(isDelayedCapture());
        paymentTransaction.setIsOnline(isOnline());
        paymentTransaction.setIsReceipt(isReceipt());
        paymentTransaction.setIsSelfService(isSelfService());
        paymentTransaction.setIsVoided(isVoided());
        paymentTransaction.setMicr(getMicr());
        paymentTransaction.setOriginalTransactionId(getOriginalTransactionId());
        paymentTransaction.setPayAmt(getPayAmt());
        paymentTransaction.setPONum(getPONum());
        paymentTransaction.setProcessed(isProcessed());
        paymentTransaction.setAuthorizationCode(getAuthorizationCode());
        paymentTransaction.setAddressVerified(getAddressVerified());
        paymentTransaction.setVerifiedZip(getVerifiedZip());
        paymentTransaction.setCVV2Match(isCVV2Match());
        paymentTransaction.setResponseInfo(getResponseInfo());
        paymentTransaction.setPaymentReference(getPaymentReference());
        paymentTransaction.setResponseMessage(getResponseMessage());
        paymentTransaction.setTransmissionResult(getTransmissionResult());
        paymentTransaction.setVoidMessage(getVoidMessage());
        paymentTransaction.setRoutingNo(getRoutingNo());
        paymentTransaction.setSwiftCode(getSwiftCode());
        paymentTransaction.setTaxAmt(getTaxAmt());
        paymentTransaction.setTenderType(getTenderType());
        paymentTransaction.setTrxType(getTrxType());
        paymentTransaction.setVoiceAuthCode(getVoiceAuthCode());

        return paymentTransaction;
    }

    private boolean voidOnlinePayment() {
        if (getTenderType().equals(X_C_Payment.TENDERTYPE_CreditCard) && isOnline()) {
            setOriginalTransactionId(getPaymentReference());
            setTrxType(X_C_Payment.TRXTYPE_Void);
            if (!processOnline()) {
                setTrxType(X_C_Payment.TRXTYPE_CreditPayment);
                if (!processOnline()) {
                    log.log(Level.SEVERE, "Failed to cancel payment online");
                    m_processMsg = MsgKt.getMsg("PaymentNotCancelled");
                    return false;
                }
            }
        }

        if (getInvoiceId() != 0) {
            MInvoice inv = new MInvoice(null, getInvoiceId());
            inv.setPaymentId(0);
            inv.saveEx();
        }
        if (getOrderId() != 0) {
            MOrder ord = new MOrder(getOrderId());
            ord.setPaymentId(0);
            ord.saveEx();
        }

        return true;
    }

    public MAllocationHdr getJustCreatedAllocInv() {
        return m_justCreatedAllocInv;
    }

    @Override
    public void setDoc(IDoc doc) {
    }

    @Override
    public void setProcessedOn(String processed, boolean b, boolean b1) {
    }
} //  MPayment
