package commonFunctionality

import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer

/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 */
class ValidAuthenticationHeaderAdderUser1: Consumer<Request.Builder>{
    private val logger: Logger = LoggerFactory.getLogger(ValidAuthenticationHeaderAdderUser1::class.java)
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser1Mail:$testUser1Password".toByteArray())

    override fun accept(builder: Request.Builder) {
        logger.info("ValidAuthenticationHeaderAdderUser1.accept() called")
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}

/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 */
class ValidAuthenticationHeaderAdderUser2: Consumer<Request.Builder>{
    private val logger: Logger = LoggerFactory.getLogger(ValidAuthenticationHeaderAdderUser1::class.java)
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser2Mail:$testUser2Password".toByteArray())

    override fun accept(builder: Request.Builder) {
        logger.info("ValidAuthenticationHeaderAdderUser1.accept() called")
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}