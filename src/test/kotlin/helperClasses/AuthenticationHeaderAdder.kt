package helperClasses

import okhttp3.Request
import java.util.*
import java.util.function.Consumer

/**
 * Can be passed to the JavalinTest.test() method to add the authentication header to the request.
 */
class AuthenticationHeaderAdder: Consumer<Request.Builder>{
    private val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUserMail:$testUserPassword".toByteArray())

    override fun accept(builder: Request.Builder) {
        builder.header("Authorization", "Basic $encodedCredentials")
    }
}