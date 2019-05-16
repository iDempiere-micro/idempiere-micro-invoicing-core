package org.compiere.docengine;

import org.compiere.accounting.Doc;
import org.compiere.accounting.MAcctSchema;
import org.compiere.accounting.MClientKt;
import org.compiere.model.AccountingSchema;
import org.compiere.model.IPODoc;
import org.compiere.model.I_C_Invoice;
import org.compiere.model.I_C_Order;
import org.compiere.model.I_M_InOut;
import org.compiere.orm.MTableKt;
import org.compiere.orm.PO;
import org.compiere.process.CompleteActionResult;
import org.compiere.process.DocAction;
import org.compiere.util.MsgKt;
import org.idempiere.common.exceptions.AdempiereException;
import org.idempiere.common.util.CLogger;
import org.idempiere.common.util.Util;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Level;

import static software.hsharp.core.orm.MBaseColumnKt.getColumnId;
import static software.hsharp.core.util.DBKt.forUpdate;
import static software.hsharp.core.util.DBKt.getSQLValueString;

/**
 * Document Action Engine
 *
 * @author Jorg Janke
 * @author Karsten Thiemann FR [ 1782412 ]
 * @author victor.perez@e-evolution.com www.e-evolution.com FR [ 1866214 ]
 * http://sourceforge.net/tracker/index.php?func=detail&aid=1866214&group_id=176962&atid=879335
 * @version $Id: DocumentEngine.java,v 1.2 2006/07/30 00:54:44 jjanke Exp $
 */
public class DocumentEngine implements DocAction {
    /**
     * Logger
     */
    private static CLogger log = CLogger.getCLogger(DocumentEngine.class);
    /**
     * Document Exception Message
     */
    private static String EXCEPTION_MSG = "Document Engine is no Document";
    /**
     * Persistent Document
     */
    private DocAction m_document;
    /**
     * Document Status
     */
    private String m_status = DocAction.Companion.getSTATUS_Drafted();
    /**
     * Process Message
     */
    private String m_message = null;
    /**
     * Actual Doc Action
     */
    private String m_action = null;

    /**
     * Doc Engine (Drafted)
     *
     * @param po document
     */
    public DocumentEngine(DocAction po) {
        this(po, DocAction.Companion.getSTATUS_Drafted());
    } //	DocActionEngine

    /**
     * Doc Engine
     *
     * @param po        document
     * @param docStatus initial document status
     */
    public DocumentEngine(DocAction po, String docStatus) {
        m_document = po;
        if (docStatus != null) m_status = docStatus;
    } //	DocActionEngine

    /**
     * Post Immediate
     *
     * @param AD_Client_ID Client ID of Document
     * @param AD_Table_ID  Table ID of Document
     * @param Record_ID    Record ID of this document
     * @param force        force posting
     * @return null, if success or error message
     */
    public static String postImmediate(

            int AD_Client_ID,
            int AD_Table_ID,
            int Record_ID,
            boolean force) {
        // Ensure the table has Posted column / i.e. GL_JournalBatch can be completed but not posted
        if (getColumnId(MTableKt.getDbTableName(AD_Table_ID), "Posted") <= 0) return null;

        String error;
        if (log.isLoggable(Level.INFO)) log.info("Table=" + AD_Table_ID + ", Record=" + Record_ID);
        AccountingSchema[] ass = MAcctSchema.getClientAcctSchema(AD_Client_ID);
        error = Doc.postImmediate(ass, AD_Table_ID, Record_ID, force);
        return error;
    } //	postImmediate

    /**
     * Process document. This replaces DocAction.processIt().
     *
     * @param doc
     * @param processAction
     * @return true if performed
     */
    public static boolean processIt(DocAction doc, String processAction) {
        boolean success = false;

        DocumentEngine engine = new DocumentEngine(doc, doc.getDocStatus());
        success = engine.processIt(processAction, doc.getDocAction());

        return success;
    }

    /**
     * Get Doc Status
     *
     * @return document status
     */
    @NotNull
    public String getDocStatus() {
        return m_status;
    } //	getDocStatus

    /**
     * Set Doc Status - Ignored
     *
     * @param ignored Status is not set directly
     * @see DocAction#setDocStatus(String)
     */
    public void setDocStatus(@NotNull String ignored) {
        // nothing to be done here
    } // 	setDocStatus

    /**
     * Document is Drafted
     *
     * @return true if drafted
     */
    public boolean isDrafted() {
        return DocAction.Companion.getSTATUS_Drafted().equals(m_status);
    } //	isDrafted

    /**
     * Document is Invalid
     *
     * @return true if Invalid
     */
    public boolean isInvalid() {
        return DocAction.Companion.getSTATUS_Invalid().equals(m_status);
    } //	isInvalid

    /**
     * Document is In Progress
     *
     * @return true if In Progress
     */
    public boolean isInProgress() {
        return DocAction.Companion.getSTATUS_InProgress().equals(m_status);
    } //	isInProgress

    /**
     * Document is Approved
     *
     * @return true if Approved
     */
    public boolean isApproved() {
        return DocAction.Companion.getSTATUS_Approved().equals(m_status);
    } //	isApproved

    /**
     * Document is Not Approved
     *
     * @return true if Not Approved
     */
    public boolean isNotApproved() {
        return DocAction.Companion.getSTATUS_NotApproved().equals(m_status);
    } //	isNotApproved

    /**
     * Document is Waiting Payment or Confirmation
     *
     * @return true if Waiting Payment
     */
    public boolean isWaiting() {
        return DocAction.Companion.getSTATUS_WaitingPayment().equals(m_status)
                || DocAction.Companion.getSTATUS_WaitingConfirmation().equals(m_status);
    } //	isWaitingPayment

    /**
     * Document is Completed
     *
     * @return true if Completed
     */
    public boolean isCompleted() {
        return DocAction.Companion.getSTATUS_Completed().equals(m_status);
    } //	isCompleted

    /**
     * Document is Reversed
     *
     * @return true if Reversed
     */
    public boolean isReversed() {
        return DocAction.Companion.getSTATUS_Reversed().equals(m_status);
    } //	isReversed

    /**
     * Document is Closed
     *
     * @return true if Closed
     */
    public boolean isClosed() {
        return DocAction.Companion.getSTATUS_Closed().equals(m_status);
    } //	isClosed

    /**
     * Document is Voided
     *
     * @return true if Voided
     */
    public boolean isVoided() {
        return DocAction.Companion.getSTATUS_Voided().equals(m_status);
    } //	isVoided

    /**
     * Process actual document. Checks if user (document) action is valid and then process action
     * Calls the individual actions which call the document action
     *
     * @param processAction document action based on workflow
     * @param docAction     document action based on document
     * @return true if performed
     */
    public boolean processIt(String processAction, String docAction) {
        // ensure doc status not change by other session
        if (m_document instanceof PO) {
            PO docPO = (PO) m_document;
            if (docPO.getId() > 0
                    && docPO.getValueOld("DocStatus") != null) {
                forUpdate(docPO);
                String docStatusOriginal = (String) docPO.getValueOld("DocStatus");
                String statusSql =
                        "SELECT DocStatus FROM "
                                + docPO.getTableName()
                                + " WHERE "
                                + docPO.getKeyColumns()[0]
                                + " = ? ";
                String currentStatus = getSQLValueString(statusSql, docPO.getId());
                if (!docStatusOriginal.equals(currentStatus) && currentStatus != null) {
                    currentStatus = getSQLValueString(statusSql, docPO.getId());
                    if (!docStatusOriginal.equals(currentStatus)) {
                        throw new IllegalStateException(
                                MsgKt.getMsg("DocStatusChanged") + " " + docPO.toString());
                    }
                }
            }
        }

        m_message = null;
        m_action = null;
        //	Std User Workflows - see MWFNodeNext.isValidFor

        if (isValidAction(processAction)) // 	WF Selection first
            m_action = processAction;
            //
        else if (isValidAction(docAction)) // 	User Selection second
            m_action = docAction;
            //	Nothing to do
        else if (processAction.equals(DocAction.Companion.getACTION_None())
                || docAction.equals(DocAction.Companion.getACTION_None())) {
            log.info("**** No Action (Prc=" + processAction + "/Doc=" + docAction + ") " + m_document);
            return true;
        } else {
            throw new IllegalStateException(
                    "Status="
                            + getDocStatus()
                            + " - Invalid Actions: Process="
                            + processAction
                            + ", Doc="
                            + docAction);
        }
        log
                .info(
                        "**** Action="
                                + m_action
                                + " (Prc="
                                + processAction
                                + "/Doc="
                                + docAction
                                + ") "
                                + m_document);
        boolean success = processIt(m_action);
        log.fine("**** Action=" + m_action + " - Success=" + success);
        return success;
    } //	process

    /**
     * Process actual document - do not call directly. Calls the individual actions which call the
     * document action
     *
     * @param action document action
     * @return true if performed
     */
    public boolean processIt(@NotNull String action) {
        m_message = null;
        m_action = action;
        //
        if (DocAction.Companion.getACTION_Unlock().equals(m_action)) return unlockIt();
        if (DocAction.Companion.getACTION_Invalidate().equals(m_action)) return invalidateIt();
        if (DocAction.Companion.getACTION_Prepare().equals(m_action))
            return DocAction.Companion.getSTATUS_InProgress().equals(prepareIt());
        if (DocAction.Companion.getACTION_Approve().equals(m_action)) return approveIt();
        if (DocAction.Companion.getACTION_Reject().equals(m_action)) return rejectIt();
        if (DocAction.Companion.getACTION_Complete().equals(m_action)
                || DocAction.Companion.getACTION_WaitComplete().equals(m_action)) {
            String status = null;
            if (isDrafted() || isInvalid()) // 	prepare if not prepared yet
            {
                status = prepareIt();
                if (!DocAction.Companion.getSTATUS_InProgress().equals(status)) return false;
            }
            status = completeIt().getNewStatusName();
            boolean ok =
                    DocAction.Companion.getSTATUS_Completed().equals(status)
                            || DocAction.Companion.getSTATUS_InProgress().equals(status)
                            || DocAction.Companion.getSTATUS_WaitingPayment().equals(status)
                            || DocAction.Companion.getSTATUS_WaitingConfirmation().equals(status);
            if (m_document != null && ok) {
                // PostProcess documents when invoice or inout (this is to postprocess the generated MatchPO
                // and MatchInv if any)
                ArrayList<IPODoc> docsPostProcess = new ArrayList<IPODoc>();
                if (m_document instanceof I_C_Invoice || m_document instanceof I_M_InOut) {
                    if (m_document instanceof I_C_Invoice) {
                        docsPostProcess = ((I_C_Invoice) m_document).getDocsPostProcess();
                    }
                    if (m_document instanceof I_M_InOut) {
                        docsPostProcess = ((I_M_InOut) m_document).getDocsPostProcess();
                    }
                }
                if (m_document instanceof PO && docsPostProcess.size() > 0) {
                    // Process (this is to update the ProcessedOn flag with a timestamp after the original
                    // document)
                    for (IPODoc docafter : docsPostProcess) {
                        docafter.setProcessedOn("Processed", true, false);
                        docafter.saveEx();
                    }
                }

                if (DocAction.Companion.getSTATUS_Completed().equals(status)
                        && MClientKt.getClientWithAccounting().isClientAccountingImmediate()) {
                    m_document.saveEx();
                    postIt();

                    if (m_document instanceof PO && docsPostProcess.size() > 0) {
                        for (IPODoc docafter : docsPostProcess) {
                            @SuppressWarnings("unused")
                            String ignoreError =
                                    DocumentEngine.postImmediate(
                                            docafter.getClientId(),
                                            docafter.getTableId(),
                                            docafter.getId(),
                                            true
                                    );
                        }
                    }
                }
            }
            return ok;
        }
        if (DocAction.Companion.getACTION_ReActivate().equals(m_action)) return reActivateIt();
        if (DocAction.Companion.getACTION_Reverse_Accrual().equals(m_action)) return reverseAccrualIt();
        if (DocAction.Companion.getACTION_Reverse_Correct().equals(m_action)) return reverseCorrectIt();
        if (DocAction.Companion.getACTION_Close().equals(m_action)) return closeIt();
        if (DocAction.Companion.getACTION_Void().equals(m_action)) return voidIt();
        if (DocAction.Companion.getACTION_Post().equals(m_action)) return postIt();
        //
        return false;
    } //	processDocument

    /**
     * Unlock Document. Status: Drafted
     *
     * @return true if success
     * @see DocAction#unlockIt()
     */
    public boolean unlockIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Unlock())) return false;
        if (m_document != null) {
            if (m_document.unlockIt()) {
                m_status = DocAction.Companion.getSTATUS_Drafted();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Drafted();
        return true;
    } //	unlockIt

    /**
     * Invalidate Document. Status: Invalid
     *
     * @return true if success
     * @see DocAction#invalidateIt()
     */
    public boolean invalidateIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Invalidate())) return false;
        if (m_document != null) {
            if (m_document.invalidateIt()) {
                m_status = DocAction.Companion.getSTATUS_Invalid();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Invalid();
        return true;
    } //	invalidateIt

    /**
     * Process Document. Status is set by process
     *
     * @return new status (In Progress or Invalid)
     * @see DocAction#prepareIt()
     */
    @NotNull
    public String prepareIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Prepare())) return m_status;
        if (m_document != null) {
            m_status = m_document.prepareIt();
            m_document.setDocStatus(m_status);
        }
        return m_status;
    } //	processIt

    /**
     * Approve Document. Status: Approved
     *
     * @return true if success
     * @see DocAction#approveIt()
     */
    public boolean approveIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Approve())) return false;
        if (m_document != null) {
            if (m_document.approveIt()) {
                m_status = DocAction.Companion.getSTATUS_Approved();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Approved();
        return true;
    } //	approveIt

    /**
     * Reject Approval. Status: Not Approved
     *
     * @return true if success
     * @see DocAction#rejectIt()
     */
    public boolean rejectIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Reject())) return false;
        if (m_document != null) {
            if (m_document.rejectIt()) {
                m_status = DocAction.Companion.getSTATUS_NotApproved();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_NotApproved();
        return true;
    } //	rejectIt

    /**
     * Complete Document. Status is set by process
     *
     * @return new document status (Complete, In Progress, Invalid, Waiting ..)
     * @see DocAction#completeIt()
     */
    @NotNull
    public CompleteActionResult completeIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Complete()))
            return new CompleteActionResult(m_status);
        if (m_document != null) {
            m_status = m_document.completeIt().getNewStatusName();
            m_document.setDocStatus(m_status);
        }
        return new CompleteActionResult(m_status);
    } //	completeIt

    /**
     * Post Document Does not change status
     *
     * @return true if success
     */
    public boolean postIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Post()) || m_document == null) return false;

        String error =
                DocumentEngine.postImmediate(

                        m_document.getClientId(),
                        m_document.getTableId(),
                        m_document.getId(),
                        true
                );
        if (DocAction.Companion.getACTION_Post().equals(m_action)) {
            // forced post via process - throw exception to inform the caller about the error
            if (!Util.isEmpty(error)) {
                throw new AdempiereException(error);
            }
        }
        return (error == null);
    } //	postIt

    /**
     * Void Document. Status: Voided
     *
     * @return true if success
     * @see DocAction#voidIt()
     */
    public boolean voidIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Void())) return false;
        if (m_document != null) {
            if (m_document.voidIt()) {
                m_status = DocAction.Companion.getSTATUS_Voided();
                if (!m_document.getDocStatus().equals(DocAction.Companion.getSTATUS_Reversed()))
                    m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Voided();
        return true;
    } //	voidIt

    /**
     * Close Document. Status: Closed
     *
     * @return true if success
     * @see DocAction#closeIt()
     */
    public boolean closeIt() {
        if ((m_document == null // 	orders can be closed any time
                || m_document.getTableId() != I_C_Order.Table_ID) && !isValidAction(DocAction.Companion.getACTION_Close()))
            return false;
        if (m_document != null) {
            if (m_document.closeIt()) {
                m_status = DocAction.Companion.getSTATUS_Closed();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Closed();
        return true;
    } //	closeIt

    /**
     * Reverse Correct Document. Status: Reversed
     *
     * @return true if success
     * @see DocAction#reverseCorrectIt()
     */
    public boolean reverseCorrectIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Reverse_Correct())) return false;
        if (m_document != null) {
            if (m_document.reverseCorrectIt()) {
                m_status = DocAction.Companion.getSTATUS_Reversed();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Reversed();
        return true;
    } //	reverseCorrectIt

    /**
     * Reverse Accrual Document. Status: Reversed
     *
     * @return true if success
     * @see DocAction#reverseAccrualIt()
     */
    public boolean reverseAccrualIt() {
        if (!isValidAction(DocAction.Companion.getACTION_Reverse_Accrual())) return false;
        if (m_document != null) {
            if (m_document.reverseAccrualIt()) {
                m_status = DocAction.Companion.getSTATUS_Reversed();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_Reversed();
        return true;
    } //	reverseAccrualIt

    /**
     * Re-activate Document. Status: In Progress
     *
     * @return true if success
     * @see DocAction#reActivateIt()
     */
    public boolean reActivateIt() {
        if (!isValidAction(DocAction.Companion.getACTION_ReActivate())) return false;
        if (m_document != null) {
            if (m_document.reActivateIt()) {
                m_status = DocAction.Companion.getSTATUS_InProgress();
                m_document.setDocStatus(m_status);
                return true;
            }
            return false;
        }
        m_status = DocAction.Companion.getSTATUS_InProgress();
        return true;
    } //	reActivateIt

    /**
     * ************************************************************************ Get Action Options
     * based on current Status
     *
     * @return array of actions
     */
    public String[] getActionOptions() {
        if (isInvalid())
            return new String[]{
                    DocAction.Companion.getACTION_Prepare(),
                    DocAction.Companion.getACTION_Invalidate(),
                    DocAction.Companion.getACTION_Unlock(),
                    DocAction.Companion.getACTION_Void()
            };

        if (isDrafted())
            return new String[]{
                    DocAction.Companion.getACTION_Prepare(),
                    DocAction.Companion.getACTION_Invalidate(),
                    DocAction.Companion.getACTION_Complete(),
                    DocAction.Companion.getACTION_Unlock(),
                    DocAction.Companion.getACTION_Void()
            };

        if (isInProgress() || isApproved())
            return new String[]{
                    DocAction.Companion.getACTION_Complete(),
                    DocAction.Companion.getACTION_WaitComplete(),
                    DocAction.Companion.getACTION_Approve(),
                    DocAction.Companion.getACTION_Reject(),
                    DocAction.Companion.getACTION_Unlock(),
                    DocAction.Companion.getACTION_Void(),
                    DocAction.Companion.getACTION_Prepare()
            };

        if (isNotApproved())
            return new String[]{
                    DocAction.Companion.getACTION_Reject(),
                    DocAction.Companion.getACTION_Prepare(),
                    DocAction.Companion.getACTION_Unlock(),
                    DocAction.Companion.getACTION_Void()
            };

        if (isWaiting())
            return new String[]{
                    DocAction.Companion.getACTION_Complete(),
                    DocAction.Companion.getACTION_WaitComplete(),
                    DocAction.Companion.getACTION_ReActivate(),
                    DocAction.Companion.getACTION_Void(),
                    DocAction.Companion.getACTION_Close()
            };

        if (isCompleted())
            return new String[]{
                    DocAction.Companion.getACTION_Close(),
                    DocAction.Companion.getACTION_ReActivate(),
                    DocAction.Companion.getACTION_Reverse_Accrual(),
                    DocAction.Companion.getACTION_Reverse_Correct(),
                    DocAction.Companion.getACTION_Post(),
                    DocAction.Companion.getACTION_Void()
            };

        if (isClosed())
            return new String[]{
                    DocAction.Companion.getACTION_Post(), DocAction.Companion.getACTION_ReOpen()
            };

        if (isReversed() || isVoided()) return new String[]{DocAction.Companion.getACTION_Post()};

        return new String[]{};
    } //	getActionOptions

    /**
     * Is The Action Valid based on current state
     *
     * @param action action
     * @return true if valid
     */
    public boolean isValidAction(String action) {
        String[] options = getActionOptions();
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(action)) return true;
        }
        return false;
    } //	isValidAction

    /**
     * Get Process Message
     *
     * @return clear text error message
     */
    @NotNull
    public String getProcessMsg() {
        return m_message;
    } //	getProcessMsg

    /**
     * *********************************************************************** Get Summary
     *
     * @return throw exception
     */
    @NotNull
    public String getSummary() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document No
     *
     * @return throw exception
     */
    @NotNull
    public String getDocumentNo() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document Info
     *
     * @return throw exception
     */
    @NotNull
    public String getDocumentInfo() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document Owner
     *
     * @return throw exception
     */
    public int getDocumentUserId() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document Currency
     *
     * @return throw exception
     */
    public int getCurrencyId() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document Approval Amount
     *
     * @return throw exception
     */
    @NotNull
    public BigDecimal getApprovalAmt() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document Client
     *
     * @return throw exception
     */
    public int getClientId() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Document Organization
     *
     * @return throw exception
     */
    public int getOrgId() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get Doc Action
     *
     * @return Document Action
     */
    @NotNull
    public String getDocAction() {
        return m_action;
    }

    /**
     * Save Document
     *
     * @return throw exception
     */
    public boolean save() {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Save Document
     *
     * @return throw exception
     */
    public void saveEx() throws AdempiereException {
        throw new IllegalStateException(EXCEPTION_MSG);
    }

    /**
     * Get ID of record
     *
     * @return ID
     */
    public int getId() {
        if (m_document != null) return m_document.getId();
        throw new IllegalStateException(EXCEPTION_MSG);
    } //	getId

    /**
     * Get AD_Table_ID
     *
     * @return AD_Table_ID
     */
    public int getTableId() {
        if (m_document != null) return m_document.getTableId();
        throw new IllegalStateException(EXCEPTION_MSG);
    } //	getTableId
} //	DocumentEnine
