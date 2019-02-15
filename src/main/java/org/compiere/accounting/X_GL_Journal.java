package org.compiere.accounting;

import org.compiere.model.I_GL_Journal;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.idempiere.common.util.Env;
import org.idempiere.orm.I_Persistent;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Generated Model for GL_Journal
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_GL_Journal extends PO implements I_GL_Journal, I_Persistent {

  /** */
  private static final long serialVersionUID = 20171031L;

  /** Standard Constructor */
  public X_GL_Journal(Properties ctx, int GL_Journal_ID) {
    super(ctx, GL_Journal_ID);
    /**
     * if (GL_Journal_ID == 0) { setC_AcctSchema_ID (0); // @$C_AcctSchema_ID@
     * setC_ConversionType_ID (0); setC_Currency_ID (0); // @C_Currency_ID@ setC_DocType_ID (0);
     * // @C_DocType_ID@ setC_Period_ID (0); // @C_Period_ID@ setCurrencyRate (Env.ZERO); // 1
     * setDateAcct (new Timestamp( System.currentTimeMillis() )); // @DateAcct@ setDateDoc (new
     * Timestamp( System.currentTimeMillis() )); // @DateDoc@ setDescription (null); setDocAction
     * (null); // CO setDocStatus (null); // DR setDocumentNo (null); setGL_Category_ID (0);
     * // @GL_Category_ID@ setGL_Journal_ID (0); setIsApproved (true); // Y setIsPrinted (false); //
     * N setPosted (false); // N setPostingType (null); // @PostingType@ setTotalCr (Env.ZERO); // 0
     * setTotalDr (Env.ZERO); // 0 }
     */
  }

  /** Load Constructor */
  public X_GL_Journal(Properties ctx, ResultSet rs) {
    super(ctx, rs);
  }

  /**
   * AccessLevel
   *
   * @return 1 - Org
   */
  protected int getAccessLevel() {
    return accessLevel.intValue();
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("X_GL_Journal[").append(getId()).append("]");
    return sb.toString();
  }

  public org.compiere.model.I_C_AcctSchema getC_AcctSchema() throws RuntimeException {
    return (org.compiere.model.I_C_AcctSchema)
        MTable.get(getCtx(), org.compiere.model.I_C_AcctSchema.Table_Name)
            .getPO(getC_AcctSchema_ID());
  }

  /**
   * Set Accounting Schema.
   *
   * @param C_AcctSchema_ID Rules for accounting
   */
  public void setC_AcctSchema_ID(int C_AcctSchema_ID) {
    if (C_AcctSchema_ID < 1) set_ValueNoCheck(COLUMNNAME_C_AcctSchema_ID, null);
    else set_ValueNoCheck(COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
  }

  /**
   * Get Accounting Schema.
   *
   * @return Rules for accounting
   */
  public int getC_AcctSchema_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_AcctSchema_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Currency Type.
   *
   * @param C_ConversionType_ID Currency Conversion Rate Type
   */
  public void setC_ConversionType_ID(int C_ConversionType_ID) {
    if (C_ConversionType_ID < 1) set_Value(COLUMNNAME_C_ConversionType_ID, null);
    else set_Value(COLUMNNAME_C_ConversionType_ID, Integer.valueOf(C_ConversionType_ID));
  }

  /**
   * Get Currency Type.
   *
   * @return Currency Conversion Rate Type
   */
  public int getC_ConversionType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_ConversionType_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Currency.
   *
   * @param C_Currency_ID The Currency for this record
   */
  public void setC_Currency_ID(int C_Currency_ID) {
    if (C_Currency_ID < 1) set_Value(COLUMNNAME_C_Currency_ID, null);
    else set_Value(COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
  }

  /**
   * Get Currency.
   *
   * @return The Currency for this record
   */
  public int getC_Currency_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_Currency_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Document Type.
   *
   * @param C_DocType_ID Document type or rules
   */
  public void setC_DocType_ID(int C_DocType_ID) {
    if (C_DocType_ID < 0) set_Value(COLUMNNAME_C_DocType_ID, null);
    else set_Value(COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
  }

  /**
   * Get Document Type.
   *
   * @return Document type or rules
   */
  public int getC_DocType_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_DocType_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Control Amount.
   *
   * @param ControlAmt If not zero, the Debit amount of the document must be equal this amount
   */
  public void setControlAmt(BigDecimal ControlAmt) {
    set_Value(COLUMNNAME_ControlAmt, ControlAmt);
  }

  /**
   * Get Control Amount.
   *
   * @return If not zero, the Debit amount of the document must be equal this amount
   */
  public BigDecimal getControlAmt() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ControlAmt);
    if (bd == null) return Env.ZERO;
    return bd;
  }

    public org.compiere.model.I_C_Period getC_Period() throws RuntimeException {
    return (org.compiere.model.I_C_Period)
        MTable.get(getCtx(), org.compiere.model.I_C_Period.Table_Name)
            .getPO(getC_Period_ID());
  }

  /**
   * Set Period.
   *
   * @param C_Period_ID Period of the Calendar
   */
  public void setC_Period_ID(int C_Period_ID) {
    if (C_Period_ID < 1) set_Value(COLUMNNAME_C_Period_ID, null);
    else set_Value(COLUMNNAME_C_Period_ID, C_Period_ID);
  }

  /**
   * Get Period.
   *
   * @return Period of the Calendar
   */
  public int getC_Period_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_C_Period_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Rate.
   *
   * @param CurrencyRate Currency Conversion Rate
   */
  public void setCurrencyRate(BigDecimal CurrencyRate) {
    set_Value(COLUMNNAME_CurrencyRate, CurrencyRate);
  }

  /**
   * Get Rate.
   *
   * @return Currency Conversion Rate
   */
  public BigDecimal getCurrencyRate() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_CurrencyRate);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Account Date.
   *
   * @param DateAcct Accounting Date
   */
  public void setDateAcct(Timestamp DateAcct) {
    set_Value(COLUMNNAME_DateAcct, DateAcct);
  }

  /**
   * Get Account Date.
   *
   * @return Accounting Date
   */
  public Timestamp getDateAcct() {
    return (Timestamp) get_Value(COLUMNNAME_DateAcct);
  }

  /**
   * Set Document Date.
   *
   * @param DateDoc Date of the Document
   */
  public void setDateDoc(Timestamp DateDoc) {
    set_Value(COLUMNNAME_DateDoc, DateDoc);
  }

  /**
   * Get Document Date.
   *
   * @return Date of the Document
   */
  public Timestamp getDateDoc() {
    return (Timestamp) get_Value(COLUMNNAME_DateDoc);
  }

  /**
   * Set Description.
   *
   * @param Description Optional short description of the record
   */
  public void setDescription(String Description) {
    set_Value(COLUMNNAME_Description, Description);
  }

  /**
   * Get Description.
   *
   * @return Optional short description of the record
   */
  public String getDescription() {
    return (String) get_Value(COLUMNNAME_Description);
  }

    /** Complete = CO */
  public static final String DOCACTION_Complete = "CO";
    /** Close = CL */
  public static final String DOCACTION_Close = "CL";
    /** <None> = -- */
  public static final String DOCACTION_None = "--";

    /**
   * Set Document Action.
   *
   * @param DocAction The targeted status of the document
   */
  public void setDocAction(String DocAction) {

    set_Value(COLUMNNAME_DocAction, DocAction);
  }

  /**
   * Get Document Action.
   *
   * @return The targeted status of the document
   */
  public String getDocAction() {
    return (String) get_Value(COLUMNNAME_DocAction);
  }

    /** Drafted = DR */
  public static final String DOCSTATUS_Drafted = "DR";
  /** Completed = CO */
  public static final String DOCSTATUS_Completed = "CO";
    /** Invalid = IN */
  public static final String DOCSTATUS_Invalid = "IN";
  /** Reversed = RE */
  public static final String DOCSTATUS_Reversed = "RE";
  /** Closed = CL */
  public static final String DOCSTATUS_Closed = "CL";

    /**
   * Set Document Status.
   *
   * @param DocStatus The current status of the document
   */
  public void setDocStatus(String DocStatus) {

    set_Value(COLUMNNAME_DocStatus, DocStatus);
  }

  /**
   * Get Document Status.
   *
   * @return The current status of the document
   */
  public String getDocStatus() {
    return (String) get_Value(COLUMNNAME_DocStatus);
  }

  /**
   * Set Document No.
   *
   * @param DocumentNo Document sequence number of the document
   */
  public void setDocumentNo(String DocumentNo) {
    set_ValueNoCheck(COLUMNNAME_DocumentNo, DocumentNo);
  }

  /**
   * Get Document No.
   *
   * @return Document sequence number of the document
   */
  public String getDocumentNo() {
    return (String) get_Value(COLUMNNAME_DocumentNo);
  }

    /**
   * Set Budget.
   *
   * @param GL_Budget_ID General Ledger Budget
   */
  public void setGL_Budget_ID(int GL_Budget_ID) {
    if (GL_Budget_ID < 1) set_Value(COLUMNNAME_GL_Budget_ID, null);
    else set_Value(COLUMNNAME_GL_Budget_ID, GL_Budget_ID);
  }

  /**
   * Get Budget.
   *
   * @return General Ledger Budget
   */
  public int getGL_Budget_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_GL_Budget_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set GL Category.
   *
   * @param GL_Category_ID General Ledger Category
   */
  public void setGL_Category_ID(int GL_Category_ID) {
    if (GL_Category_ID < 1) set_Value(COLUMNNAME_GL_Category_ID, null);
    else set_Value(COLUMNNAME_GL_Category_ID, GL_Category_ID);
  }

  /**
   * Get GL Category.
   *
   * @return General Ledger Category
   */
  public int getGL_Category_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_GL_Category_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Journal Batch.
   *
   * @param GL_JournalBatch_ID General Ledger Journal Batch
   */
  public void setGL_JournalBatch_ID(int GL_JournalBatch_ID) {
    if (GL_JournalBatch_ID < 1) set_ValueNoCheck(COLUMNNAME_GL_JournalBatch_ID, null);
    else set_ValueNoCheck(COLUMNNAME_GL_JournalBatch_ID, Integer.valueOf(GL_JournalBatch_ID));
  }

  /**
   * Get Journal Batch.
   *
   * @return General Ledger Journal Batch
   */
  public int getGL_JournalBatch_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_GL_JournalBatch_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Get Journal.
   *
   * @return General Ledger Journal
   */
  public int getGL_Journal_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_GL_Journal_ID);
    if (ii == null) return 0;
    return ii;
  }

    /**
   * Set Approved.
   *
   * @param IsApproved Indicates if this document requires approval
   */
  public void setIsApproved(boolean IsApproved) {
    set_ValueNoCheck(COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
  }

  /**
   * Get Approved.
   *
   * @return Indicates if this document requires approval
   */
  public boolean isApproved() {
    Object oo = get_Value(COLUMNNAME_IsApproved);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
  }

  /**
   * Set Printed.
   *
   * @param IsPrinted Indicates if this document / line is printed
   */
  public void setIsPrinted(boolean IsPrinted) {
    set_ValueNoCheck(COLUMNNAME_IsPrinted, Boolean.valueOf(IsPrinted));
  }

    /**
   * Set Posted.
   *
   * @param Posted Posting status
   */
  public void setPosted(boolean Posted) {
    set_ValueNoCheck(COLUMNNAME_Posted, Boolean.valueOf(Posted));
  }

    /** Actual = A */
  public static final String POSTINGTYPE_Actual = "A";
  /** Budget = B */
  public static final String POSTINGTYPE_Budget = "B";
  /** Commitment = E */
  public static final String POSTINGTYPE_Commitment = "E";
  /** Statistical = S */
  public static final String POSTINGTYPE_Statistical = "S";
  /** Reservation = R */
  public static final String POSTINGTYPE_Reservation = "R";
  /**
   * Set PostingType.
   *
   * @param PostingType The type of posted amount for the transaction
   */
  public void setPostingType(String PostingType) {

    set_Value(COLUMNNAME_PostingType, PostingType);
  }

  /**
   * Get PostingType.
   *
   * @return The type of posted amount for the transaction
   */
  public String getPostingType() {
    return (String) get_Value(COLUMNNAME_PostingType);
  }

  /**
   * Set Processed.
   *
   * @param Processed The document has been processed
   */
  public void setProcessed(boolean Processed) {
    set_Value(COLUMNNAME_Processed, Boolean.valueOf(Processed));
  }

  /**
   * Get Processed.
   *
   * @return The document has been processed
   */
  public boolean isProcessed() {
    Object oo = get_Value(COLUMNNAME_Processed);
    if (oo != null) {
      if (oo instanceof Boolean) return ((Boolean) oo).booleanValue();
      return "Y".equals(oo);
    }
    return false;
  }

    /**
   * Get Processed On.
   *
   * @return The date+time (expressed in decimal format) when the document has been processed
   */
  public BigDecimal getProcessedOn() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_ProcessedOn);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Process Now.
   *
   * @param Processing Process Now
   */
  public void setProcessing(boolean Processing) {
    set_Value(COLUMNNAME_Processing, Boolean.valueOf(Processing));
  }

    /**
   * Set Reversal ID.
   *
   * @param Reversal_ID ID of document reversal
   */
  public void setReversal_ID(int Reversal_ID) {
    if (Reversal_ID < 1) set_Value(COLUMNNAME_Reversal_ID, null);
    else set_Value(COLUMNNAME_Reversal_ID, Integer.valueOf(Reversal_ID));
  }

  /**
   * Get Reversal ID.
   *
   * @return ID of document reversal
   */
  public int getReversal_ID() {
    Integer ii = (Integer) get_Value(COLUMNNAME_Reversal_ID);
    if (ii == null) return 0;
    return ii;
  }

  /**
   * Set Total Credit.
   *
   * @param TotalCr Total Credit in document currency
   */
  public void setTotalCr(BigDecimal TotalCr) {
    set_ValueNoCheck(COLUMNNAME_TotalCr, TotalCr);
  }

  /**
   * Get Total Credit.
   *
   * @return Total Credit in document currency
   */
  public BigDecimal getTotalCr() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_TotalCr);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  /**
   * Set Total Debit.
   *
   * @param TotalDr Total debit in document currency
   */
  public void setTotalDr(BigDecimal TotalDr) {
    set_ValueNoCheck(COLUMNNAME_TotalDr, TotalDr);
  }

  /**
   * Get Total Debit.
   *
   * @return Total debit in document currency
   */
  public BigDecimal getTotalDr() {
    BigDecimal bd = (BigDecimal) get_Value(COLUMNNAME_TotalDr);
    if (bd == null) return Env.ZERO;
    return bd;
  }

  @Override
  public int getTableId() {
    return I_GL_Journal.Table_ID;
  }
}
