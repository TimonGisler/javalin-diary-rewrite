import entries.GetAllEntriesQuery
import entries.SaveEntryCommand
import io.javalin.Javalin

fun main(args: Array<String>) {
    val app = Javalin.create()
        .start(7070)

    //ENTRIES
    app.post("/entries", SaveEntryCommand::saveNewEntryHandler)
    app.get("/entries", GetAllEntriesQuery::getAllEntriesHandler)
}

data class stringDto(val text: String)