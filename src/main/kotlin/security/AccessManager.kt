package security

import JdbiProvider.getJdbi
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.AccessManager
import io.javalin.security.BasicAuthCredentials
import io.javalin.security.RouteRole
import org.slf4j.Logger
import org.slf4j.LoggerFactory

//TODO TGIS, rework this AccessManager it is confusing imo
class AccessManager: AccessManager {
    private val logger: Logger = LoggerFactory.getLogger(AccessManager::class.java)

    override fun manage(handler: Handler, ctx: Context, routeRoles: Set<RouteRole>) { //routeRoles are the roles which are allowed to access the route
        logger.info("AccessManager.manage() called")

        if (routeRoles.contains(Roles.EVERYONE)){ //if everyone is allowed on this route, no further checks needed
            handler.handle(ctx)
            return
        }

        logger.info("findings user roles")
        val userRoles: List<Roles> = getRoleOfUser(ctx)

        logger.info("checking if user has the required role to access this route")
        when {
            routeRoles.any { it in userRoles } -> handler.handle(ctx) //if the user has one of the roles which are allowed to access this route, handle the request
            //if the user cannot access the route check if he is authenticated and throw the appropriate exception --> if he is authenticated but cannot access the resources he lacks the permission
            userRoles.contains(Roles.AUTHENTICATED) -> throw ForbiddenResponse("User does not have the permission to access this resource")
            else -> throw UnauthorizedResponse("The user is not authenticated")

        }
    }

    //TODO TGIS a more oop approach would probably to have an User object which I can fetch from the db and which has a list of roles + id
    //and then I also can move the whole fetch logic into a repository or smth --> probably not, I want all the acessmanager logic in this class
    private fun getRoleOfUser(ctx: Context): List<Roles>{
        val roles: MutableList<Roles> = mutableListOf()
        val userId: Long = getAuthenticatedUserId(ctx.basicAuthCredentials())
        val entryId: Long? = getEntryId(ctx)

        if (userId!=null) roles.add(Roles.AUTHENTICATED) //if userId is not null, this means the correct credentials were provided

        //if the user wants to do something with an specific entry check if he is the creator of the entry
        entryId?.let {
            if (isUserAuthorOfEntry(userId, entryId)) roles.add(Roles.CREATOR)
        }

        logger.info("user roles are: $roles")
        return roles
    }

    /**
     * Returns the userId of the user which made request.
     * If no basic auth is provided, an UnauthorizedResponse is thrown
     * If the email or password is incorrect, an UnauthorizedResponse is thrown
     */
    private fun getAuthenticatedUserId(basicAuth: BasicAuthCredentials?): Long {
        if (basicAuth == null) throw UnauthorizedResponse("No basic auth provided (null)")

        val email = basicAuth.username
        val password = basicAuth.password

        val userId = getJdbi().withHandle<Long, Exception> { handle ->
            handle.createQuery("SELECT uniqueid FROM public.diaryUser  WHERE email = :email AND password = :password")
                .bind("email", email)
                .bind("password", password)
                .mapTo(Long::class.java)
                .findOne()
                .orElse(null)
        }

        logger.info("userId: $userId")
        //If no userId was found the email or password must be wrong --> throw UnauthorizedResponse
        return userId ?: throw UnauthorizedResponse("Email or password is incorrect")
    }


    private fun getEntryId(ctx: Context) = try {
        ctx.pathParam("entryId").toLong()
    } catch (e: Exception) {
        logger.info("No entryId provided in path ${ctx.path()}")
        null
    }

    private fun isUserAuthorOfEntry(creatorId: Long, entryId: Long): Boolean =
        getJdbi().withHandle<Boolean, Exception> { handle ->
            handle.createQuery("SELECT EXISTS(SELECT * from public.entry where creatorId = :creatorId AND id = :entryId)")
                .bind("creatorId", creatorId)
                .bind("entryId", entryId)
                .mapTo(Boolean::class.java)
                .one()
        }

}

