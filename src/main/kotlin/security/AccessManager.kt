package security

import JdbiProvider.getJdbi
import io.javalin.http.*
import io.javalin.security.AccessManager
import io.javalin.security.BasicAuthCredentials
import io.javalin.security.RouteRole
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AccessManager: AccessManager {
    private val logger: Logger = LoggerFactory.getLogger(AccessManager::class.java)

    override fun manage(handler: Handler, ctx: Context, routeRoles: Set<RouteRole>) { //routeRoles are the roles which are allowed to access the route
        logger.info("AccessManager.manage() called")
        logger.info("findings user roles")
        val userRoles: List<Roles> = getRoleOfUser(ctx)
        logger.info("checking if user has the required role to access this route")
        when {
            routeRoles.contains(Roles.EVERYONE) -> handler.handle(ctx)
            routeRoles.any { it in userRoles } -> handler.handle(ctx)
            userRoles.isNotEmpty() -> ctx.status(HttpStatus.FORBIDDEN) //user is logged in (has role) but not allowed to access this route
            else -> { // user is not authenticated
                ctx.header(Header.WWW_AUTHENTICATE, "Basic") //tell client which type of authentication to use
                throw ForbiddenResponse("The user has not the required role to access this route")
            }
        }
    }

    private fun getRoleOfUser(ctx: Context): List<Roles>{
        val roles: MutableList<Roles> = mutableListOf()
        val basicAuth = ctx.basicAuthCredentials()
        if (basicAuth == null) {
            logger.info("basicAuth is null")
            ctx.basicAuthCredentials()
            throw UnauthorizedResponse("Username or password is wrong")
        }
        val userId: Long = getUserId(basicAuth)!!
        val entryId: Long? = getEntryIdOrNull(ctx)
        when{
            isUserAuthorOfEntry(userId, entryId) -> roles.add(Roles.CREATOR)
        }
        logger.info("user roles are: $roles")
        return roles
    }

    private fun getEntryIdOrNull(ctx: Context) = try {
        ctx.pathParam("entryId").toLong()
    } catch (e: Exception) {
        null
    }

    private fun getUserId(basicAuth: BasicAuthCredentials?): Long? {
        if (basicAuth == null) throw BadGatewayResponse("BasicAuthCredentials are null")

        val userId = getJdbi().withHandle<Long, Exception> { handle ->
            handle.createQuery("SELECT uniqueid FROM public.user  WHERE email = :email AND password = :password")
                .bind("email", basicAuth.username)
                .bind("password", basicAuth.password)
                .mapTo(Long::class.java)
                .findOne()
                .orElse(null)
        }

        logger.info("userId: $userId")
        return userId
    }

    private fun isUserAuthorOfEntry(creatorId: Long, entryId: Long?): Boolean =
        getJdbi().withHandle<Boolean, Exception> { handle ->
            handle.createQuery("SELECT EXISTS(SELECT * from public.entry where creatorId = :creatorId AND id = :entryId)")
                .bind("creatorId", creatorId)
                .bind("entryId", entryId)
                .mapTo(Boolean::class.java)
                .one()
        }

}

