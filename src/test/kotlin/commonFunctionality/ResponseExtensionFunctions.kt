package commonFunctionality

import io.javalin.json.JavalinJackson
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(ValidAuthenticationHeaderAdderUser1::class.java)


inline fun <reified T : Any> Response.parseBodyToObject(): T? {
    logger.info("Response.parseBodyToObject() called, parsing body to object of type ${T::class.java}")
    val responseBodyAsString = this.body?.string() ?: return null
    return JavalinJackson().fromJsonString(responseBodyAsString, T::class.java)
}
