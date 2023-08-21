package commonFunctionality

import User.RegisterCommandData
import entries.GetEntryCommandResponse
import entries.SaveEntryCommandData
import entries.SingleEntryOverviewEntryQueryResponse
import entries.UpdateEntryCommandData
import getJavalinApp
import io.javalin.testtools.JavalinTest
import io.javalin.testtools.TestConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.function.Consumer


/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 */
//TODO TGIS, make them use the CustomValidAuthenticationHeaderAdderUser internally
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
class CustomValidAuthenticationHeaderAdderUser (private val mail: String, private val password: String): Consumer<Request.Builder> {
    private val logger: Logger = LoggerFactory.getLogger(ValidAuthenticationHeaderAdderUser1::class.java)
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$mail:$password".toByteArray())

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
    private val logger: Logger = LoggerFactory.getLogger(UserFunctionality::class.java)
    private val noTimeOutOkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(Duration.ZERO)
        .readTimeout(Duration.ZERO)
        .writeTimeout(Duration.ZERO)
        .build()

    // by default prints all the errors (does not capture them) and by default has no timeout --> helps for debugging
    private val defaultTestConfig = TestConfig(captureLogs = false, okHttpClient = noTimeOutOkHttpClient)

    fun register(registerCommandData: RegisterCommandData): Int {
        var code: Int? = null
        JavalinTest.test(getJavalinApp(), defaultTestConfig) { _, client ->
            code = client.post("/api/register", registerCommandData).code
        }
        return code!!
    }

    fun login(customAuthenticationHeaderAdder: Consumer<Request.Builder> = authenticationHeaderAdder): Int {
        var code: Int? = null
        JavalinTest.test(getJavalinApp(), defaultTestConfig) { _, client ->
            code = client.post("/api/login", req =  customAuthenticationHeaderAdder).code
        }
        return code!!
    }

    /** saves the entry to the database */
    fun saveEntry(entryToSave: SaveEntryCommandData): Int {
        var newEntryId: Int? = null

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false, okHttpClient = noTimeOutOkHttpClient)) { _, client ->
             val response: Response = client.post("/api/entries", entryToSave, authenticationHeaderAdder)
            newEntryId = response.parseBodyToObject()
        }

        return newEntryId!!
    }

    fun getEntriesOverview(): Array<SingleEntryOverviewEntryQueryResponse> {
        var response: Array<SingleEntryOverviewEntryQueryResponse>? = null
        JavalinTest.test(getJavalinApp(), defaultTestConfig) { _, client ->
            response = client.get("/api/entries", authenticationHeaderAdder).parseBodyToObject()!!
        }

        return response!!
    }

    fun deleteEntry(entryToDelete: Int): Response {
        var response: Response? = null
        JavalinTest.test(getJavalinApp(), defaultTestConfig) { _, client ->
            response = client.delete("/api/entries/$entryToDelete", req = authenticationHeaderAdder)
        }

        return response!!
    }

    /**
     * Get the entry or return null if the entry could not be fetched
     */
    fun getEntry(idOfEntryToGet: Int): GetEntryCommandResponse? {
        var response: GetEntryCommandResponse? = null

        JavalinTest.test(getJavalinApp(), defaultTestConfig) { _, client ->
            val rawResponse = client.get("/api/entries/$idOfEntryToGet", authenticationHeaderAdder)

            response = try {
                rawResponse.parseBodyToObject()
            } catch (e: Exception) {
                //TODO TGIS, the reason i have this code is that if for example this entry does not exist anymore i get an error string e.g. "This entry does not exist"
                //which cannot be parsed to an object because it isn't json the "correct" way would be to probably check the status code and not blindly catch all exceptions
                logger.info("Exception while parsing response with error message: ${e.message}")
                null
            }
        }

        return response
    }

    fun updateEntry(updateEntryCommandData: UpdateEntryCommandData, idOfEntryToUpdate: Long): Int {
        var code: Int? = null

        JavalinTest.test(getJavalinApp(), defaultTestConfig) { _, client ->
            code = client.put("/api/entries/${idOfEntryToUpdate}", updateEntryCommandData, req = authenticationHeaderAdder).code
        }

        return code!!
    }
}