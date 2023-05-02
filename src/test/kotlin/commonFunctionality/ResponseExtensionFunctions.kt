package commonFunctionality

import io.javalin.json.JavalinJackson
import okhttp3.Response

inline fun <reified T : Any> Response.parseBodyToObject(): T? {
    val responseBodyAsString = this.body?.string() ?: return null
    return JavalinJackson().fromJsonString(responseBodyAsString, T::class.java)
}
