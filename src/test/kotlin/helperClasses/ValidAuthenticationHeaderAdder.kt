package helperClasses

import okhttp3.Request
import java.util.*
import java.util.function.Consumer

/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 */
class ValidAuthenticationHeaderAdderUser1: Consumer<Request.Builder>{
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser1Mail:$testUser1Password".toByteArray())

    override fun accept(builder: Request.Builder) {
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}

/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 */
class ValidAuthenticationHeaderAdderUser2: Consumer<Request.Builder>{
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser2Mail:$testUser2Password".toByteArray())

    override fun accept(builder: Request.Builder) {
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}