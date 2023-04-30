package common

import JdbiProvider.getJdbi
import io.javalin.http.Context
import io.javalin.http.Header
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.BasicAuthCredentials

/**
 * Gets the id of the user which made the request from the database or throws UnauthorizedResponse if the user is not authenticated
 */
fun Context.getUserId(): Long {
    return getJdbi().withHandle<Long, Exception> {
        val basicAuth: BasicAuthCredentials = this.basicAuthCredentials() ?: run {
            this.header(Header.WWW_AUTHENTICATE, "Basic")
            throw UnauthorizedResponse("Both username and password are required to access this resource")
        }

        val userName: String = basicAuth.username
        val password: String = basicAuth.password

        it.createQuery("SELECT uniqueid FROM public.user  WHERE email = :email AND password = :password")
            .bind("email", userName)
            .bind("password", password)
            .mapTo(Long::class.java)
            .findOne()
            .orElseThrow { UnauthorizedResponse("Username or password is wrong") }
    }
}