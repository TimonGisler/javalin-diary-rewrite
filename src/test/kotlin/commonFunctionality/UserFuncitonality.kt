package commonFunctionality

import entries.CreateEntryCommand
import entries.GetEntryCommandResponse
import entries.SingleEntryOverviewEntryQueryResponse
import getJavalinApp
import io.javalin.testtools.JavalinTest
import io.javalin.testtools.TestConfig
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer


/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 * TODO TGIS, these two classes can be combined into one and either pass the mail and pw or the encoded credentials directly
 */
class ValidAuthenticationHeaderAdderUser1: Consumer<Request.Builder>{
    private val logger: Logger = LoggerFactory.getLogger(ValidAuthenticationHeaderAdderUser1::class.java)
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser1Mail:$testUser1Password".toByteArray())

    override fun accept(builder: Request.Builder) {
        logger.info("ValidAuthenticationHeaderAdderUser1.accept() called")
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}
class ValidAuthenticationHeaderAdderUser2: Consumer<Request.Builder>{
    private val logger: Logger = LoggerFactory.getLogger(ValidAuthenticationHeaderAdderUser1::class.java)
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser2Mail:$testUser2Password".toByteArray())

    override fun accept(builder: Request.Builder) {
        logger.info("ValidAuthenticationHeaderAdderUser1.accept() called")
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}


/**
 * This class contains functions that are used in multiple tests
 * Things the user could ask the api to do
 * Basically it helps simulate the user
 */
class UserFunctionality (private val authenticationHeaderAdder: Consumer<Request.Builder> = ValidAuthenticationHeaderAdderUser1()) {

    /** saves the entry to the database */
    fun saveEntry(entryToSave: CreateEntryCommand): Int {
        var newEntryId: Int? = null

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
             newEntryId = client.post("/entries", entryToSave, authenticationHeaderAdder).parseBodyToObject()!!
        }

        return newEntryId!!
    }

    fun getEntriesOverview(): Array<SingleEntryOverviewEntryQueryResponse> {
        var response: Array<SingleEntryOverviewEntryQueryResponse>? = null

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
            response = client.get("/entries", authenticationHeaderAdder).parseBodyToObject()!!
        }

        return response!!
    }

    fun deleteEntry(newEntryId: Int) {
        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
            client.delete("/entries/$newEntryId", authenticationHeaderAdder)
        }
    }

    fun getEntry(idOfEntryToGet: Int): GetEntryCommandResponse? {
        var response: GetEntryCommandResponse? = null

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
            val rawResponse = client.get("/entries/$idOfEntryToGet", authenticationHeaderAdder)
            response = rawResponse.parseBodyToObject()
        }

        return response
    }
}