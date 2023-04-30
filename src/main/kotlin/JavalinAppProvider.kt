import entries.GetEntriesOverViewQuery
import entries.SaveEntryCommand
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post
import security.AccessManager
import security.Roles

/**
 * Creates a new Javalin instance with all the config set up
 */
fun getJavalinApp(): Javalin {
    return Javalin.create { config ->
        config.accessManager(AccessManager())}
        .routes {
            get("/entries", GetEntriesOverViewQuery::getEntriesOverviewHandler, Roles.CREATOR)
            post("/entries", SaveEntryCommand::saveNewEntryHandler, Roles.EVERYONE) //TODO TGIS, change to only logged in users
        }
}