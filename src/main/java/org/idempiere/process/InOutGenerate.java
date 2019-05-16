package org.idempiere.process;

import org.compiere.accounting.MOrder;
import org.compiere.accounting.MStorageOnHand;
import org.compiere.invoicing.MInOut;
import org.compiere.invoicing.MInOutLine;
import org.compiere.invoicing.MLocatorType;
import org.compiere.model.IProcessInfoParameter;
import org.compiere.model.I_C_OrderLine;
import org.compiere.process.DocAction;
import org.compiere.production.MLocator;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.Env;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Generate Shipments. Manual or Automatic
 *
 * @author Jorg Janke
 * @version $Id: InOutGenerate.java,v 1.2 2006/07/30 00:51:01 jjanke Exp $
 */
public class InOutGenerate extends BaseInOutGenerate {
    /**
     * DocAction
     */
    private String p_docAction = DocAction.Companion.getACTION_None();
    /**
     * Shipment Date
     */
    private Timestamp p_DateShipped = null;

    /**
     * Last BP Location
     */
    private int m_lastC_BPartner_Location_ID = -1;

    /**
     * Storages temp space
     */
    private HashMap<SParameter, MStorageOnHand[]> m_map = new HashMap<SParameter, MStorageOnHand[]>();
    /**
     * Last Parameter
     */
    private SParameter m_lastPP = null;
    /**
     * Last Storage
     */
    private MStorageOnHand[] m_lastStorages = null;

    /**
     * Prepare - e.g., get Parameters.
     */
    protected void prepare() {
        IProcessInfoParameter[] para = getParameter();
        for (IProcessInfoParameter iProcessInfoParameter : para) {
            String name = iProcessInfoParameter.getParameterName();

            switch (name) {
                case "M_Warehouse_ID":
                    setP_M_Warehouse_ID(iProcessInfoParameter.getParameterAsInt());
                    break;
                case "C_BPartner_ID":
                    setP_C_BPartner_ID(iProcessInfoParameter.getParameterAsInt());
                    break;
                case "DatePromised":
                    setP_DatePromised((Timestamp) iProcessInfoParameter.getParameter());
                    break;
                case "Selection":
                    setP_Selection("Y".equals(iProcessInfoParameter.getParameter()));
                    break;
                case "IsUnconfirmedInOut":
                    setP_IsUnconfirmedInOut("Y".equals(iProcessInfoParameter.getParameter()));
                    break;
                case "ConsolidateDocument":
                    setP_ConsolidateDocument("Y".equals(iProcessInfoParameter.getParameter()));
                    break;
                case "DocAction":
                    p_docAction = (String) iProcessInfoParameter.getParameter();
                    break;
                case "MovementDate":
                    p_DateShipped = (Timestamp) iProcessInfoParameter.getParameter();
                    break;
                default:
                    log.log(Level.SEVERE, "Unknown Parameter: " + name);
                    break;
            }

            //  juddm - added ability to specify a shipment date from Generate Shipments
            if (p_DateShipped == null) {
                setM_movementDate(Env.getContextAsDate());
                if (getM_movementDate() == null) setM_movementDate(new Timestamp(System.currentTimeMillis()));
            } else setM_movementDate(p_DateShipped);
        }
    } //	prepare


    /**
     * ************************************************************************ Create Line
     *
     * @param order     order
     * @param orderLine line
     * @param qty       qty
     * @param storages  storage info
     * @param force     force delivery
     */
    protected void createLine(
            @NotNull MOrder order,
            @NotNull I_C_OrderLine orderLine,
            @NotNull BigDecimal qty,
            MStorageOnHand[] storages,
            boolean force) {
        //	Complete last Shipment - can have multiple shipments
        if (m_lastC_BPartner_Location_ID != orderLine.getBusinessPartnerLocationId()) completeShipment();
        m_lastC_BPartner_Location_ID = orderLine.getBusinessPartnerLocationId();
        MInOut m_shipment = getM_shipment();
        //	Create New Shipment
        if (m_shipment == null) {
            m_shipment = new MInOut(order, 0, getM_movementDate());
            m_shipment.setWarehouseId(orderLine.getWarehouseId()); // 	sets Org too
            if (order.getBusinessPartnerId() != orderLine.getBusinessPartnerId())
                m_shipment.setBusinessPartnerId(orderLine.getBusinessPartnerId());
            if (order.getBusinessPartnerLocationId() != orderLine.getBusinessPartnerLocationId())
                m_shipment.setBusinessPartnerLocationId(orderLine.getBusinessPartnerLocationId());
            if (!m_shipment.save()) throw new IllegalStateException("Could not create Shipment");
        }
        //	Non Inventory Lines
        if (storages == null) {
            MInOutLine line = new MInOutLine(m_shipment);
            line.setOrderLine(orderLine, 0, Env.ZERO);
            line.setQty(qty); // 	Correct UOM
            if (orderLine.getQtyEntered().compareTo(orderLine.getQtyOrdered()) != 0)
                line.setQtyEntered(
                        qty.multiply(orderLine.getQtyEntered())
                                .divide(orderLine.getQtyOrdered(), 12, BigDecimal.ROUND_HALF_UP));
            line.setLine(getM_line() + orderLine.getLine());
            if (!line.save()) throw new IllegalStateException("Could not create Shipment Line");
            if (log.isLoggable(Level.FINE)) log.fine(line.toString());
            return;
        }

        //	Inventory Lines
        ArrayList<MInOutLine> list = new ArrayList<>();
        BigDecimal toDeliver = qty;
        for (int i = 0; i < storages.length; i++) {
            MStorageOnHand storage = storages[i];
            BigDecimal deliver = toDeliver;
            // skip negative storage record
            if (storage.getQtyOnHand().signum() < 0) continue;

            //	Not enough On Hand
            if (deliver.compareTo(storage.getQtyOnHand()) > 0
                    && storage.getQtyOnHand().signum() >= 0) // 	positive storage
            {
                //	Adjust to OnHand Qty
                if (!force || i + 1 != storages.length) // 	if force not on last location
                    deliver = storage.getQtyOnHand();
            }
            if (deliver.signum() == 0) // 	zero deliver
                continue;
            int M_Locator_ID = storage.getLocatorId();
            //
            MInOutLine line = null;
            if (orderLine.getAttributeSetInstanceId() == 0) //      find line with Locator
            {
                for (MInOutLine test : list) {
                    if (test.getLocatorId() == M_Locator_ID && test.getAttributeSetInstanceId() == 0) {
                        line = test;
                        break;
                    }
                }
            }
            if (line == null) // 	new line
            {
                line = new MInOutLine(m_shipment);
                line.setOrderLine(orderLine, M_Locator_ID, order.isSOTrx() ? deliver : Env.ZERO);
                line.setQty(deliver);
                list.add(line);
            } else //	existing line
                line.setQty(line.getMovementQty().add(deliver));
            if (orderLine.getQtyEntered().compareTo(orderLine.getQtyOrdered()) != 0)
                line.setQtyEntered(
                        line.getMovementQty()
                                .multiply(orderLine.getQtyEntered())
                                .divide(orderLine.getQtyOrdered(), 12, BigDecimal.ROUND_HALF_UP));
            line.setLine(getM_line() + orderLine.getLine());
            if (!line.save()) throw new IllegalStateException("Could not create Shipment Line");
            if (log.isLoggable(Level.FINE)) log.fine("ToDeliver=" + qty + "/" + deliver + " - " + line);
            toDeliver = toDeliver.subtract(deliver);
            //      Temp adjustment, actual update happen in MInOut.completeIt - just in memory - not
            // saved
            storage.setQtyOnHand(storage.getQtyOnHand().subtract(deliver));
            //
            if (toDeliver.signum() == 0) break;
        }
        if (toDeliver.signum() != 0) {
            if (!force) {
                throw new IllegalStateException("Not All Delivered - Remainder=" + toDeliver);
            } else {

                MInOutLine line = new MInOutLine(m_shipment);
                line.setOrderLine(orderLine, 0, order.isSOTrx() ? toDeliver : Env.ZERO);
                line.setQty(toDeliver);
                if (orderLine.getQtyEntered().compareTo(orderLine.getQtyOrdered()) != 0)
                    line.setQtyEntered(
                            line.getMovementQty()
                                    .multiply(orderLine.getQtyEntered())
                                    .divide(orderLine.getQtyOrdered(), 12, BigDecimal.ROUND_HALF_UP));
                line.setLine(getM_line() + orderLine.getLine());
                if (!line.save()) throw new IllegalStateException("Could not create Shipment Line");
            }
        }
    } //	createLine

    /**
     * Get Storages
     *
     * @param M_Warehouse_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param minGuaranteeDate
     * @param FiFo
     * @return storages
     */
    @NotNull
    protected MStorageOnHand[] getStorages(
            int M_Warehouse_ID,
            int M_Product_ID,
            int M_AttributeSetInstance_ID,
            Timestamp minGuaranteeDate,
            boolean FiFo) {
        m_lastPP =
                new SParameter(
                        M_Warehouse_ID, M_Product_ID, M_AttributeSetInstance_ID, minGuaranteeDate, FiFo);
        //
        m_lastStorages = m_map.get(m_lastPP);

        if (m_lastStorages == null) {
            MStorageOnHand[] tmpStorages =
                    MStorageOnHand.getWarehouse(
                            M_Warehouse_ID,
                            M_Product_ID,
                            M_AttributeSetInstance_ID,
                            minGuaranteeDate,
                            FiFo,
                            false,
                            0,
                            null);

            /* IDEMPIERE-2668 - filter just locators enabled for shipping */
            List<MStorageOnHand> m_storagesForShipping = new ArrayList<>();
            for (MStorageOnHand soh : tmpStorages) {
                MLocator loc = MLocator.get(soh.getLocatorId());
                MLocatorType lt = null;
                if (loc.getLocatorTypeId() > 0)
                    lt = MLocatorType.get(loc.getLocatorTypeId());
                if (lt == null || lt.isAvailableForShipping()) m_storagesForShipping.add(soh);
            }
            m_lastStorages = new MStorageOnHand[m_storagesForShipping.size()];
            m_storagesForShipping.toArray(m_lastStorages);

            m_map.put(m_lastPP, m_lastStorages);
        }
        return m_lastStorages;
    } //	getStorages

    /**
     * Complete Shipment
     */
    protected void completeShipment() {
        MInOut m_shipment = getM_shipment();
        if (m_shipment != null) {
            if (!DocAction.Companion.getACTION_None().equals(p_docAction)) {
                //	Fails if there is a confirmation
                if (!m_shipment.processIt(p_docAction)) {
                    log.warning("Failed: " + m_shipment);
                    throw new IllegalStateException(
                            "Shipment Process Failed: " + m_shipment + " - " + m_shipment.getProcessMsg());
                }
            }
            m_shipment.saveEx();
            String message =
                    MsgKt.parseTranslation("@ShipmentProcessed@ " + m_shipment.getDocumentNo());
            addBufferLog(
                    m_shipment.getInOutId(),
                    m_shipment.getMovementDate(),
                    null,
                    message,
                    m_shipment.getTableId(),
                    m_shipment.getInOutId());
            setM_created(getM_created() + 1);

            // reset storage cache as MInOut.completeIt will update m_storage
            m_map = new HashMap<SParameter, MStorageOnHand[]>();
            m_lastPP = null;
            m_lastStorages = null;
        }
        m_shipment = null;
        setM_line(0);
    } //	completeOrder

    /**
     * InOutGenerate Parameter
     */
    static class SParameter {
        /**
         * Warehouse
         */
        public int M_Warehouse_ID;
        /**
         * Product
         */
        public int M_Product_ID;
        /**
         * ASI
         */
        public int M_AttributeSetInstance_ID;
        /**
         * AS
         */
        public int M_AttributeSet_ID;
        /**
         * All instances
         */
        public boolean allAttributeInstances;
        /**
         * Mon Guarantee Date
         */
        public Timestamp minGuaranteeDate;
        /**
         * FiFo
         */
        public boolean FiFo;

        /**
         * Parameter
         *
         * @param p_Warehouse_ID            warehouse
         * @param p_Product_ID
         * @param p_AttributeSetInstance_ID
         * @param p_minGuaranteeDate
         * @param p_FiFo
         */
        protected SParameter(
                int p_Warehouse_ID,
                int p_Product_ID,
                int p_AttributeSetInstance_ID,
                Timestamp p_minGuaranteeDate,
                boolean p_FiFo) {
            this.M_Warehouse_ID = p_Warehouse_ID;
            this.M_Product_ID = p_Product_ID;
            this.M_AttributeSetInstance_ID = p_AttributeSetInstance_ID;
            this.minGuaranteeDate = p_minGuaranteeDate;
            this.FiFo = p_FiFo;
        }

        /**
         * Equals
         *
         * @param obj
         * @return true if equal
         */
        public boolean equals(Object obj) {
            if (obj instanceof SParameter) {
                SParameter cmp = (SParameter) obj;
                boolean eq =
                        cmp.M_Warehouse_ID == M_Warehouse_ID
                                && cmp.M_Product_ID == M_Product_ID
                                && cmp.M_AttributeSetInstance_ID == M_AttributeSetInstance_ID
                                && cmp.M_AttributeSet_ID == M_AttributeSet_ID
                                && cmp.allAttributeInstances == allAttributeInstances
                                && cmp.FiFo == FiFo;
                if (eq && (cmp.minGuaranteeDate != null || minGuaranteeDate != null) && (cmp.minGuaranteeDate == null
                        || minGuaranteeDate == null
                        || !cmp.minGuaranteeDate.equals(minGuaranteeDate))) {
                    eq = false;
                }
                return eq;
            }
            return false;
        } //	equals

        /**
         * hashCode
         *
         * @return hash code
         */
        public int hashCode() {
            long hash =
                    M_Warehouse_ID
                            + (M_Product_ID * 2)
                            + (M_AttributeSetInstance_ID * 3)
                            + (M_AttributeSet_ID * 4);

            if (allAttributeInstances) hash *= -1;
            if (FiFo) hash *= -2;
            if (hash < 0) hash = -hash + 7;
            while (hash > Integer.MAX_VALUE) hash -= Integer.MAX_VALUE;
            //
            if (minGuaranteeDate != null) {
                hash += minGuaranteeDate.hashCode();
                while (hash > Integer.MAX_VALUE) hash -= Integer.MAX_VALUE;
            }

            return (int) hash;
        } //	hashCode
    } //	Parameter
} //	InOutGenerate
