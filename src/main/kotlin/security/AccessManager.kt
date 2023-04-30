package security

import JdbiProvider.getJdbi
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.Header
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.AccessManager
import io.javalin.security.BasicAuthCredentials
import io.javalin.security.RouteRole

class AccessManager: AccessManager {
    override fun manage(handler: Handler, ctx: Context, routeRoles: Set<RouteRole>) { //routeRoles are the roles which are allowed to access the route
        when {
            routeRoles.contains(Roles.EVERYONE) -> handler.handle(ctx)
            getRoleOfUser(ctx).any { routeRoles.contains(it) } -> handler.handle(ctx)
            else -> {
                ctx.header(Header.WWW_AUTHENTICATE, "Basic") //tell client which type of authentication to use
                throw UnauthorizedResponse("The user has not the required role to access this route")
            }
        }
    }

    private fun getRoleOfUser(ctx: Context): List<Roles>{
        val roles: MutableList<Roles> = mutableListOf()
        val basicAuth = ctx.basicAuthCredentials()
        val userId: Long = getUserId(basicAuth) ?: throw UnauthorizedResponse("Username or password is wrong")
        when{
            isUserAuthor(userId) -> roles.add(Roles.CREATOR)
        }

        return roles
    }

    private fun getUserId(basicAuth: BasicAuthCredentials?): Long? {
        if (basicAuth == null) throw UnauthorizedResponse("Both username and password are required to access this resource")

        return getJdbi().withHandle<Long, Exception> { handle ->
            handle.createQuery("SELECT uniqueid FROM public.user  WHERE email = :email AND password = :password")
                .bind("email", basicAuth.username)
                .bind("password", basicAuth.password)
                .mapTo(Long::class.java)
                .findOne()
                .orElse(null)
        }
    }

    private fun isUserAuthor(creatorId: Long): Boolean =
        getJdbi().withHandle<Boolean, Exception> { handle ->
            handle.createQuery("SELECT EXISTS(SELECT * from public.entry where creatorId = :creatorId)")
                .bind("creatorId", creatorId)
                .mapTo(Boolean::class.java)
                .one()
        }

}

