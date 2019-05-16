package org.compiere.validation;

import org.compiere.model.AccountingSchema;
import org.compiere.model.IFact;
import org.idempiere.icommon.model.PersistentObject;

import java.util.List;

public interface FactsValidator {

    /**
     * Get Client to be monitored
     *
     * @return clientId
     */
    int getClientId();

    /**
     * @param facts
     * @param po
     * @return error message or null - if not null, the pocument will be marked as Invalid.
     */
    String factsValidate(AccountingSchema schema, List<IFact> facts, PersistentObject po);
}
