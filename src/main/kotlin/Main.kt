import entries.GetAllEntriesQuery
import entries.SaveEntryCommand
import io.javalin.Javalin
import org.jdbi.v3.core.Jdbi

fun main(args: Array<String>) {
    val app = Javalin.create()
        .start(7070)

    //ENTRIES
    app.post("/entries", SaveEntryCommand::saveNewEntryHandler)
    app.get("/entries", GetAllEntriesQuery::getAllEntriesHandler)

}