
import entries.DeleteEntryCommand
import entries.GetEntriesOverViewQuery
import entries.GetEntryCommand
import entries.SaveEntryCommand
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
        config.accessManager(AccessManager())}
        .routes {
            get("/entries/{entryId}", GetEntryCommand::getEntryCommandHandler, Roles.CREATOR)
            get("/entries", GetEntriesOverViewQuery::getEntriesOverviewHandler, Roles.AUTHENTICATED)
            post("/entries", SaveEntryCommand::saveNewEntryHandler, Roles.EVERYONE) //TODO TGIS, change to only logged in users
            delete("/entries/{entryId}", DeleteEntryCommand::deleteEntryHandler, Roles.CREATOR)
        }
}