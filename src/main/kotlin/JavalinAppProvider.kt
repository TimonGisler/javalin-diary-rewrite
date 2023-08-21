import User.RegisterCommand
import entries.*
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import security.AccessManager
import security.Roles

/**
 * Creates a new Javalin instance with all the config set up
 */
fun getJavalinApp(): Javalin {
    //creator role looks at "entryId" path param and checks if the user is the author of the entry, you MUST name the path param "entryId"
    //otherwise the AccessManager will not find it won't find the entryId
    return Javalin.create { config ->
        config.staticFiles.add("/public")
        config.accessManager(AccessManager())
    }
        .routes {
            //path("") { // TODO TGIS, change prefix to /api
                post("/register", RegisterCommand::registerUserHandler, Roles.EVERYONE)
                post("/login", { ctx -> ctx.json("login successful") }, Roles.AUTHENTICATED) //if he authenticated sucessfully return "login succesful"
                get("/entries/{entryId}", GetEntryCommand::getEntryCommandHandler, Roles.CREATOR)
                get("/entries", GetEntriesOverViewQuery::getEntriesOverviewHandler, Roles.AUTHENTICATED)
                post("/entries", SaveEntryCommand::saveNewEntryHandler, Roles.AUTHENTICATED)
                delete("/entries/{entryId}", DeleteEntryCommand::deleteEntryHandler, Roles.CREATOR)
                put("/entries/{entryId}", UpdateEntryCommand::updateEntry, Roles.CREATOR)
            }
        //}


}