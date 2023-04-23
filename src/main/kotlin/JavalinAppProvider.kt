import entries.GetAllEntriesQuery
import entries.SaveEntryCommand
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.post

    val javalinApp = Javalin.create().routes {
        get("/entries", GetAllEntriesQuery::getAllEntriesHandler)
        post("/entries", SaveEntryCommand::saveNewEntryHandler)
    }
