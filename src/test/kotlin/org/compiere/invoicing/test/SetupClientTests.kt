package org.compiere.invoicing.test

import org.compiere.crm.getCountry
import org.compiere.process.ProcessInfo
import org.idempiere.process.AcctSchemaDefaultCopy
import org.idempiere.process.InitialClientSetup
import kotlin.test.assertTrue

class SetupClientTests : BaseComponentTest() {
    companion object {
        internal fun createClient() {
            val clientName = randomString(20)
            val orgValue = randomString(20)
            val orgName = randomString(20)
            val userClient = randomString(20)
            val eMail = randomString(5) + "@test.com"
            val adminEmail = randomString(5) + "@test.com"
            val countryId = 101
            val country = getCountry(countryId)
            val clientSetup = InitialClientSetup(
                p_ClientName = clientName, p_OrgValue = orgValue, p_OrgName = orgName, p_AdminUserName = adminEmail,
                p_NormalUserName = userClient, p_EMail = eMail, p_UseDefaultCoA = false, p_C_Currency_ID = 102, // EUR
                p_C_Country_ID = country.id, // Germany - Deutschland
                p_CoAFile = "src/test/resources/coa/AccountingCZ.csv",
                p_Phone = randomString(10), p_Phone2 = randomString(10), p_Fax = "", p_AdminUserEmail = adminEmail,
                p_NormalUserEmail = eMail, p_CityName = randomString(9), p_Postal = randomString(6),
                p_Address1 = randomString(20)
            )
            val pi = ProcessInfo("", 0)
            assertTrue(clientSetup.startProcess(pi))
            val acctSchemaDefaultCopy = AcctSchemaDefaultCopy(
                1000000, false
            )
            assertTrue(acctSchemaDefaultCopy.startProcess(pi))
        }
    }
}