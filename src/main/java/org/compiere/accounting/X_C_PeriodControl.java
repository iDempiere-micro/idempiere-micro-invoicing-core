package org.compiere.accounting;

import kotliquery.Row;
import org.compiere.model.I_C_PeriodControl;
import org.compiere.orm.PO;

/**
 * Generated Model for C_PeriodControl
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public class X_C_PeriodControl extends PO implements I_C_PeriodControl {

    /**
     * GL Journal = GLJ
     */
    public static final String DOCBASETYPE_GLJournal = "GLJ";
    /**
     * AP Invoice = API
     */
    public static final String DOCBASETYPE_APInvoice = "API";
    /**
     * AP Payment = APP
     */
    public static final String DOCBASETYPE_APPayment = "APP";
    /**
     * AR Invoice = ARI
     */
    public static final String DOCBASETYPE_ARInvoice = "ARI";
    /**
     * AR Receipt = ARR
     */
    public static final String DOCBASETYPE_ARReceipt = "ARR";
    /**
     * Sales Order = SOO
     */
    public static final String DOCBASETYPE_SalesOrder = "SOO";
    /**
     * AR Pro Forma Invoice = ARF
     */
    public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
    /**
     * Material Delivery = MMS
     */
    public static final String DOCBASETYPE_MaterialDelivery = "MMS";
    /**
     * Material Receipt = MMR
     */
    public static final String DOCBASETYPE_MaterialReceipt = "MMR";
    /**
     * Material Movement = MMM
     */
    public static final String DOCBASETYPE_MaterialMovement = "MMM";
    /**
     * Purchase Order = POO
     */
    public static final String DOCBASETYPE_PurchaseOrder = "POO";
    /**
     * Purchase Requisition = POR
     */
    public static final String DOCBASETYPE_PurchaseRequisition = "POR";
    /**
     * Material Physical Inventory = MMI
     */
    public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
    /**
     * AP Credit Memo = APC
     */
    public static final String DOCBASETYPE_APCreditMemo = "APC";
    /**
     * AR Credit Memo = ARC
     */
    public static final String DOCBASETYPE_ARCreditMemo = "ARC";
    /**
     * Bank Statement = CMB
     */
    public static final String DOCBASETYPE_BankStatement = "CMB";
    /**
     * Cash Journal = CMC
     */
    public static final String DOCBASETYPE_CashJournal = "CMC";
    /**
     * Payment Allocation = CMA
     */
    public static final String DOCBASETYPE_PaymentAllocation = "CMA";
    /**
     * Material Production = MMP
     */
    public static final String DOCBASETYPE_MaterialProduction = "MMP";
    /**
     * Match Invoice = MXI
     */
    public static final String DOCBASETYPE_MatchInvoice = "MXI";
    /**
     * Match PO = MXP
     */
    public static final String DOCBASETYPE_MatchPO = "MXP";
    /**
     * Project Issue = PJI
     */
    public static final String DOCBASETYPE_ProjectIssue = "PJI";
    /**
     * Maintenance Order = MOF
     */
    public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
    /**
     * Manufacturing Order = MOP
     */
    public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
    /**
     * Quality Order = MQO
     */
    public static final String DOCBASETYPE_QualityOrder = "MQO";
    /**
     * Payroll = HRP
     */
    public static final String DOCBASETYPE_Payroll = "HRP";
    /**
     * Distribution Order = DOO
     */
    public static final String DOCBASETYPE_DistributionOrder = "DOO";
    /**
     * Manufacturing Cost Collector = MCC
     */
    public static final String DOCBASETYPE_ManufacturingCostCollector = "MCC";
    /**
     * Open Period = O
     */
    public static final String PERIODACTION_OpenPeriod = "O";
    /**
     * Close Period = C
     */
    public static final String PERIODACTION_ClosePeriod = "C";
    /**
     * Permanently Close Period = P
     */
    public static final String PERIODACTION_PermanentlyClosePeriod = "P";
    /**
     * <No Action> = N
     */
    public static final String PERIODACTION_NoAction = "N";
    /**
     * Open = O
     */
    public static final String PERIODSTATUS_Open = "O";
    /**
     * Closed = C
     */
    public static final String PERIODSTATUS_Closed = "C";
    /**
     * Permanently closed = P
     */
    public static final String PERIODSTATUS_PermanentlyClosed = "P";
    /**
     * Never opened = N
     */
    public static final String PERIODSTATUS_NeverOpened = "N";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_C_PeriodControl(int C_PeriodControl_ID) {
        super(C_PeriodControl_ID);
        /**
         * if (C_PeriodControl_ID == 0) { setPeriodControl_ID (0); setPeriodId (0); setDocBaseType
         * (null); setPeriodAction (null); // N }
         */
    }

    /**
     * Load Constructor
     */
    public X_C_PeriodControl(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 2 - Client
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    public String toString() {
        return "X_C_PeriodControl[" + getId() + "]";
    }

    /**
     * Get Period.
     *
     * @return Period of the Calendar
     */
    public int getPeriodId() {
        Integer ii = getValue(COLUMNNAME_C_Period_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Period.
     *
     * @param C_Period_ID Period of the Calendar
     */
    public void setPeriodId(int C_Period_ID) {
        if (C_Period_ID < 1) setValueNoCheck(COLUMNNAME_C_Period_ID, null);
        else setValueNoCheck(COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
    }

    /**
     * Get Document BaseType.
     *
     * @return Logical type of document
     */
    public String getDocBaseType() {
        return getValue(COLUMNNAME_DocBaseType);
    }

    /**
     * Set Document BaseType.
     *
     * @param DocBaseType Logical type of document
     */
    public void setDocBaseType(String DocBaseType) {

        setValueNoCheck(COLUMNNAME_DocBaseType, DocBaseType);
    }

    /**
     * Get Period Action.
     *
     * @return Action taken for this period
     */
    public String getPeriodAction() {
        return getValue(COLUMNNAME_PeriodAction);
    }

    /**
     * Set Period Action.
     *
     * @param PeriodAction Action taken for this period
     */
    public void setPeriodAction(String PeriodAction) {

        setValue(COLUMNNAME_PeriodAction, PeriodAction);
    }

    /**
     * Get Period Status.
     *
     * @return Current state of this period
     */
    public String getPeriodStatus() {
        return getValue(COLUMNNAME_PeriodStatus);
    }

    /**
     * Set Period Status.
     *
     * @param PeriodStatus Current state of this period
     */
    public void setPeriodStatus(String PeriodStatus) {

        setValueNoCheck(COLUMNNAME_PeriodStatus, PeriodStatus);
    }

    @Override
    public int getTableId() {
        return I_C_PeriodControl.Table_ID;
    }
}
