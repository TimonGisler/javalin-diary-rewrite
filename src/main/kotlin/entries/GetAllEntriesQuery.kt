package entries

import io.javalin.http.Context

object GetAllEntriesQuery {

    // return value
    data class Entry(val text: String, val title: String)
    data class AllEntries(val entries: List<Entry>)

    fun getAllEntriesHandler(ctx: Context){
        val entries = fetchAllEntries(1)
        ctx.json(entries)
    }

    private fun fetchAllEntries(userId: Long): AllEntries{
        return AllEntries(listOf(
            Entry("text", "title"),
            Entry("text2", "title2"),
            Entry("text3", "title3"),
        ))
    }
}