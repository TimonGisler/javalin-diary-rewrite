package security

import io.javalin.security.RouteRole

enum class Roles: RouteRole {
    CREATOR, //only the owner/creator can access this resource
    EVERYONE//everyone can access this resource
}