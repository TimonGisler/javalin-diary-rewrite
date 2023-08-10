package commonFunctionality

import entries.CreateEntryCommand
import entries.GetEntryCommandResponse
import entries.SingleEntryOverviewEntryQueryResponse
import getJavalinApp
import io.javalin.testtools.JavalinTest
import io.javalin.testtools.TestConfig
import okhttp3.Request
import okhttp3.Response
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
    val logger: Logger = LoggerFactory.getLogger(UserFunctionality::class.java)

    /** saves the entry to the database */
    fun saveEntry(entryToSave: CreateEntryCommand): Int {
        var newEntryId: Int? = null

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
             val response: Response = client.post("/entries", entryToSave, authenticationHeaderAdder)
            newEntryId = response.parseBodyToObject()
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

    fun deleteEntry(entryToDelete: Int) {
        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
            client.delete("/entries/$entryToDelete", req = authenticationHeaderAdder)
        }
    }

    /**
     * Get the entry or return null if the entry could not be fetched
     */
    fun getEntry(idOfEntryToGet: Int): GetEntryCommandResponse? {
        var response: GetEntryCommandResponse? = null

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
            val rawResponse = client.get("/entries/$idOfEntryToGet", authenticationHeaderAdder)

            response = try {
                rawResponse.parseBodyToObject()
            } catch (e: Exception) {
                //TODO TGIS, the reason i have this code is that if for example this entry does not exist anymore i get an error string e.g. "This entry does not exist"
                //which cannot be parsed to an object because it isn't json the "correct" way would be to probably check the status code and not blindly catch all exceptions
                logger.info("Exception while parsing response: ${e.message} returning null")
                null
            }
        }

        return response
    }
}