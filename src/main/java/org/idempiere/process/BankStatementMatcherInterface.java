package org.idempiere.process;

import org.compiere.accounting.X_I_BankStatement;
import org.compiere.model.I_C_BankStatementLine;

public interface BankStatementMatcherInterface {
    /**
     * Match Bank Statement Line
     *
     * @param bsl bank statement line
     * @return found matches or null
     */
    BankStatementMatchInfo findMatch(I_C_BankStatementLine bsl);

    /**
     * Match Bank Statement Import Line
     *
     * @param ibs bank statement import line
     * @return found matches or null
     */
    BankStatementMatchInfo findMatch(X_I_BankStatement ibs);
} //	BankStatementMatcherInterface
