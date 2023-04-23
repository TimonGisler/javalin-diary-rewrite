import entries.GetAllEntriesQuery
import entries.SaveEntryCommand

fun main(args: Array<String>) {
    val app = javalinApp
        .start(7070)

    //ENTRIES
    app.post("/entries", SaveEntryCommand::saveNewEntryHandler)
    app.get("/entries", GetAllEntriesQuery::getAllEntriesHandler)

}