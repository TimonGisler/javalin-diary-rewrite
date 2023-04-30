package entries

import JdbiProvider.getJdbi
import io.javalin.http.Context

// return value
data class EntryQueryResponse(val title: String, val text: String)

object GetAllEntriesQuery {

    fun getAllEntriesHandler(ctx: Context){
        val entries = fetchAllEntries()
        ctx.json(entries)
    }

    private fun fetchAllEntries(): List<EntryQueryResponse> {
        return getJdbi().withHandle<List<EntryQueryResponse>, Exception> {
            it.createQuery("SELECT * FROM entry")
                .mapTo(EntryQueryResponse::class.java)
                .list()

        }
    }
}