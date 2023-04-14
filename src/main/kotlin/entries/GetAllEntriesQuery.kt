package entries

import io.javalin.http.Context
import jdbi

object GetAllEntriesQuery {

    // return value
    data class Entry(val text: String, val title: String)
    fun getAllEntriesHandler(ctx: Context){
        println("Moin")
        val entries = fetchAllEntries()
        ctx.json(entries)
    }

    private fun fetchAllEntries(): List<Entry> {
        return jdbi.withHandle<List<Entry>, Exception> {
            it.createQuery("SELECT * FROM entry")
                .mapTo(Entry::class.java)
                .list()

        }
    }
}