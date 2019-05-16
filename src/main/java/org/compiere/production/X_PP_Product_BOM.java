package org.compiere.production;

import kotliquery.Row;
import org.compiere.model.HasName;
import org.compiere.model.I_M_AttributeSetInstance;
import org.compiere.orm.PO;
import org.eevolution.model.I_PP_Product_BOM;
import software.hsharp.core.orm.MBaseTableKt;

import java.sql.Timestamp;

/**
 * Generated Model for PP_Product_BOM
 *
 * @author iDempiere (generated)
 * @version Release 5.1 - $Id$
 */
public abstract class X_PP_Product_BOM extends PO implements I_PP_Product_BOM {

    /**
     *
     */
    private static final long serialVersionUID = 20171031L;

    /**
     * Standard Constructor
     */
    public X_PP_Product_BOM(int PP_Product_BOM_ID) {
        super(PP_Product_BOM_ID);
    }

    /**
     * Load Constructor
     */
    public X_PP_Product_BOM(Row row) {
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

    public String toString() {
        return "X_PP_Product_BOM[" + getId() + "]";
    }

    /**
     * Get BOM Type.
     *
     * @return Type of BOM
     */
    public String getBOMType() {
        return getValue(COLUMNNAME_BOMType);
    }

    /**
     * Set BOM Type.
     *
     * @param BOMType Type of BOM
     */
    public void setBOMType(String BOMType) {

        setValue(COLUMNNAME_BOMType, BOMType);
    }

    /**
     * Get BOM Use.
     *
     * @return The use of the Bill of Material
     */
    public String getBOMUse() {
        return getValue(COLUMNNAME_BOMUse);
    }

    /**
     * Set BOM Use.
     *
     * @param BOMUse The use of the Bill of Material
     */
    public void setBOMUse(String BOMUse) {

        setValue(COLUMNNAME_BOMUse, BOMUse);
    }

    /**
     * Get Copy From.
     *
     * @return Copy From Record
     */
    public String getCopyFrom() {
        return getValue(COLUMNNAME_CopyFrom);
    }

    /**
     * Set Copy From.
     *
     * @param CopyFrom Copy From Record
     */
    public void setCopyFrom(String CopyFrom) {
        setValue(COLUMNNAME_CopyFrom, CopyFrom);
    }

    public org.compiere.model.I_C_UOM getUOM() throws RuntimeException {
        return (org.compiere.model.I_C_UOM)
                MBaseTableKt.getTable(org.compiere.model.I_C_UOM.Table_Name)
                        .getPO(getUOMId());
    }

    /**
     * Get UOM.
     *
     * @return Unit of Measure
     */
    public int getUOMId() {
        Integer ii = getValue(COLUMNNAME_C_UOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set UOM.
     *
     * @param C_UOM_ID Unit of Measure
     */
    public void setUOMId(int C_UOM_ID) {
        if (C_UOM_ID < 1) setValue(COLUMNNAME_C_UOM_ID, null);
        else setValue(COLUMNNAME_C_UOM_ID, C_UOM_ID);
    }

    /**
     * Get Description.
     *
     * @return Optional short description of the record
     */
    public String getDescription() {
        return getValue(COLUMNNAME_Description);
    }

    /**
     * Set Description.
     *
     * @param Description Optional short description of the record
     */
    public void setDescription(String Description) {
        setValue(COLUMNNAME_Description, Description);
    }

    /**
     * Get Document No.
     *
     * @return Document sequence number of the document
     */
    public String getDocumentNo() {
        return getValue(COLUMNNAME_DocumentNo);
    }

    /**
     * Set Document No.
     *
     * @param DocumentNo Document sequence number of the document
     */
    public void setDocumentNo(String DocumentNo) {
        setValue(COLUMNNAME_DocumentNo, DocumentNo);
    }

    /**
     * Get Comment/Help.
     *
     * @return Comment or Hint
     */
    public String getHelp() {
        return getValue(COLUMNNAME_Help);
    }

    /**
     * Set Comment/Help.
     *
     * @param Help Comment or Hint
     */
    public void setHelp(String Help) {
        setValue(COLUMNNAME_Help, Help);
    }

    public I_M_AttributeSetInstance getMAttributeSetInstance() throws RuntimeException {
        return (I_M_AttributeSetInstance)
                MBaseTableKt.getTable(I_M_AttributeSetInstance.Table_Name)
                        .getPO(getAttributeSetInstanceId());
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
            setValue(COLUMNNAME_M_AttributeSetInstance_ID, M_AttributeSetInstance_ID);
    }

    public org.compiere.model.I_M_ChangeNotice getChangeNotice() throws RuntimeException {
        return (org.compiere.model.I_M_ChangeNotice)
                MBaseTableKt.getTable(org.compiere.model.I_M_ChangeNotice.Table_Name)
                        .getPO(getChangeNoticeId());
    }

    /**
     * Get Change Notice.
     *
     * @return Bill of Materials (Engineering) Change Notice (Version)
     */
    public int getChangeNoticeId() {
        Integer ii = getValue(COLUMNNAME_M_ChangeNotice_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Change Notice.
     *
     * @param M_ChangeNotice_ID Bill of Materials (Engineering) Change Notice (Version)
     */
    public void setChangeNoticeId(int M_ChangeNotice_ID) {
        if (M_ChangeNotice_ID < 1) setValue(COLUMNNAME_M_ChangeNotice_ID, null);
        else setValue(COLUMNNAME_M_ChangeNotice_ID, M_ChangeNotice_ID);
    }

    public org.compiere.model.I_M_Product getProduct() throws RuntimeException {
        return (org.compiere.model.I_M_Product)
                MBaseTableKt.getTable(org.compiere.model.I_M_Product.Table_Name)
                        .getPO(getProductId());
    }

    /**
     * Get Product.
     *
     * @return Product, Service, Item
     */
    public int getProductId() {
        Integer ii = getValue(COLUMNNAME_M_Product_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set Product.
     *
     * @param M_Product_ID Product, Service, Item
     */
    public void setProductId(int M_Product_ID) {
        if (M_Product_ID < 1) setValue(COLUMNNAME_M_Product_ID, null);
        else setValue(COLUMNNAME_M_Product_ID, M_Product_ID);
    }

    /**
     * Get Name.
     *
     * @return Alphanumeric identifier of the entity
     */
    public String getName() {
        return getValue(HasName.COLUMNNAME_Name);
    }

    /**
     * Set Name.
     *
     * @param Name Alphanumeric identifier of the entity
     */
    public void setName(String Name) {
        setValue(HasName.COLUMNNAME_Name, Name);
    }

    /**
     * Get BOM & Formula.
     *
     * @return BOM & Formula
     */
    public int getProductBOMId() {
        Integer ii = getValue(COLUMNNAME_PP_Product_BOM_ID);
        if (ii == null) return 0;
        return ii;
    }

    /**
     * Set BOM & Formula.
     *
     * @param PP_Product_BOM_ID BOM & Formula
     */
    public void setProductBOMId(int PP_Product_BOM_ID) {
        if (PP_Product_BOM_ID < 1) setValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, null);
        else setValueNoCheck(COLUMNNAME_PP_Product_BOM_ID, PP_Product_BOM_ID);
    }

    /**
     * Get PP_Product_BOM_UU.
     *
     * @return PP_Product_BOM_UU
     */
    public String getProductBOM_UU() {
        return getValue(COLUMNNAME_PP_Product_BOM_UU);
    }

    /**
     * Set PP_Product_BOM_UU.
     *
     * @param PP_Product_BOM_UU PP_Product_BOM_UU
     */
    public void setProduct_BOM_UU(String PP_Product_BOM_UU) {
        setValue(COLUMNNAME_PP_Product_BOM_UU, PP_Product_BOM_UU);
    }

    /**
     * Get Process Now.
     *
     * @return Process Now
     */
    public boolean isProcessing() {
        Object oo = getValue(COLUMNNAME_Processing);
        if (oo != null) {
            if (oo instanceof Boolean) return (Boolean) oo;
            return "Y".equals(oo);
        }
        return false;
    }

    /**
     * Set Process Now.
     *
     * @param Processing Process Now
     */
    public void setProcessing(boolean Processing) {
        setValue(COLUMNNAME_Processing, Processing);
    }

    /**
     * Get Revision.
     *
     * @return Revision
     */
    public String getRevision() {
        return getValue(COLUMNNAME_Revision);
    }

    /**
     * Set Revision.
     *
     * @param Revision Revision
     */
    public void setRevision(String Revision) {
        setValue(COLUMNNAME_Revision, Revision);
    }

    /**
     * Get Valid from.
     *
     * @return Valid from including this date (first day)
     */
    public Timestamp getValidFrom() {
        return (Timestamp) getValue(COLUMNNAME_ValidFrom);
    }

    /**
     * Set Valid from.
     *
     * @param ValidFrom Valid from including this date (first day)
     */
    public void setValidFrom(Timestamp ValidFrom) {
        setValue(COLUMNNAME_ValidFrom, ValidFrom);
    }

    /**
     * Get Valid to.
     *
     * @return Valid to including this date (last day)
     */
    public Timestamp getValidTo() {
        return (Timestamp) getValue(COLUMNNAME_ValidTo);
    }

    /**
     * Set Valid to.
     *
     * @param ValidTo Valid to including this date (last day)
     */
    public void setValidTo(Timestamp ValidTo) {
        setValue(COLUMNNAME_ValidTo, ValidTo);
    }

    /**
     * Get Search Key.
     *
     * @return Search key for the record in the format required - must be unique
     */
    public String getValue() {
        return getValue(COLUMNNAME_Value);
    }

    /**
     * Set Search Key.
     *
     * @param Value Search key for the record in the format required - must be unique
     */
    public void setValue(String Value) {
        setValue(COLUMNNAME_Value, Value);
    }

    @Override
    public int getTableId() {
        return I_PP_Product_BOM.Table_ID;
    }
}
