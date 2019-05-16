package org.idempiere.process;

import kotliquery.Row;
import org.compiere.model.I_M_BOMProduct;
import org.compiere.orm.PO;

import java.math.BigDecimal;

public class X_M_BOMProduct extends PO implements I_M_BOMProduct {

    /**
     * Standard Product = S
     */
    public static final String BOMPRODUCTTYPE_StandardProduct = "S";
    /**
     * Alternative = A
     */
    public static final String BOMPRODUCTTYPE_Alternative = "A";
    /**
     * Alternative (Default) = D
     */
    public static final String BOMPRODUCTTYPE_AlternativeDefault = "D";
    /**
     * Outside Processing = X
     */
    public static final String BOMPRODUCTTYPE_OutsideProcessing = "X";
    /**
     *
     */
    private static final long serialVersionUID = 20171031L;


    /**
     * Standard Constructor
     */
    public X_M_BOMProduct(int M_BOMProduct_ID) {
        super(M_BOMProduct_ID);
    }

    /**
     * Load Constructor
     */
    public X_M_BOMProduct(Row row) {
        super(row);
    }

    /**
     * AccessLevel
     *
     * @return 3 - Client - Org
     */
    protected int getAccessLevel() {
        return accessLevel.intValue();
    }

    @Override
    public int getTableId() {
        return Table_ID;
    }

    public String toString() {
        return "X_M_BOMProduct[" + getId() + "]";
    }

    /**
     * Get Component Type.
     *
     * @return BOM Product Type
     */
    public String getBOMProductType() {
        return getValue(COLUMNNAME_BOMProductType);
    }

    /**
     * Set Component Type.
     *
     * @param BOMProductType BOM Product Type
     */
    public void setBOMProductType(String BOMProductType) {

        setValue(COLUMNNAME_BOMProductType, BOMProductType);
    }

    /**
     * Set BOM Quantity.
     *
     * @param BOMQty Bill of Materials Quantity
     */
    public void setBOMQty(BigDecimal BOMQty) {
        setValue(COLUMNNAME_BOMQty, BOMQty);
    }

    /**
     * Set Phantom.
     *
     * @param IsPhantom Phantom Component
     */
    public void setIsPhantom(boolean IsPhantom) {
        setValue(COLUMNNAME_IsPhantom, Boolean.valueOf(IsPhantom));
    }

    /**
     * Get Lead Time Offset.
     *
     * @return Optional Lead Time offset before starting production
     */
    public int getLeadTimeOffset() {
        Integer ii = getValue(COLUMNNAME_LeadTimeOffset);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Lead Time Offset.
     *
     * @param LeadTimeOffset Optional Lead Time offset before starting production
     */
    public void setLeadTimeOffset(int LeadTimeOffset) {
        setValue(COLUMNNAME_LeadTimeOffset, Integer.valueOf(LeadTimeOffset));
    }

    /**
     * Get Line No.
     *
     * @return Unique line for this document
     */
    public int getLine() {
        Integer ii = getValue(COLUMNNAME_Line);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Line No.
     *
     * @param Line Unique line for this document
     */
    public void setLine(int Line) {
        setValue(COLUMNNAME_Line, Integer.valueOf(Line));
    }

    /**
     * Get Attribute Set Instance.
     *
     * @return Product Attribute Set Instance
     */
    public int getAttributeSetInstanceId() {
        Integer ii = getValue(COLUMNNAME_M_AttributeSetInstance_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Attribute Set Instance.
     *
     * @param M_AttributeSetInstance_ID Product Attribute Set Instance
     */
    public void setAttributeSetInstanceId(int M_AttributeSetInstance_ID) {
        if (M_AttributeSetInstance_ID < 0) setValue(COLUMNNAME_M_AttributeSetInstance_ID, null);
        else
            setValue(COLUMNNAME_M_AttributeSetInstance_ID, Integer.valueOf(M_AttributeSetInstance_ID));
    }

    /**
     * Get Alternative Group.
     *
     * @return Product BOM Alternative Group
     */
    public int getBOMAlternativeGroupId() {
        Integer ii = getValue(COLUMNNAME_M_BOMAlternative_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get BOM.
     *
     * @return Bill of Material
     */
    public int getBOMId() {
        Integer ii = getValue(COLUMNNAME_M_BOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get BOM Component.
     *
     * @return Bill of Material Component (Product)
     */
    public int getBOMProductId() {
        Integer ii = getValue(COLUMNNAME_M_BOMProduct_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get BOM Product.
     *
     * @return Bill of Material Component Product
     */
    public int getProductBOMId() {
        Integer ii = getValue(COLUMNNAME_M_ProductBOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set BOM Product.
     *
     * @param M_ProductBOM_ID Bill of Material Component Product
     */
    public void setProductBOMId(int M_ProductBOM_ID) {
        if (M_ProductBOM_ID < 1) setValue(COLUMNNAME_M_ProductBOM_ID, null);
        else setValue(COLUMNNAME_M_ProductBOM_ID, Integer.valueOf(M_ProductBOM_ID));
    }

    /**
     * Get Product Operation.
     *
     * @return Product Manufacturing Operation
     */
    public int getProductOperationId() {
        Integer ii = getValue(COLUMNNAME_M_ProductOperation_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Get Sequence.
     *
     * @return Method of ordering records; lowest number comes first
     */
    public int getSeqNo() {
        Integer ii = getValue(COLUMNNAME_SeqNo);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Sequence.
     *
     * @param SeqNo Method of ordering records; lowest number comes first
     */
    public void setSeqNo(int SeqNo) {
        setValue(COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
    }
}
