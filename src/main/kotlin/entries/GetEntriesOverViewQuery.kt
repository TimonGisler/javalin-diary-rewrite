package entries

import JdbiProvider.getJdbi
import common.getUserId
import io.javalin.http.Context
import java.time.OffsetDateTime

// return value
data class EntryQueryResponse(val title: String, val creationDate: OffsetDateTime , val id: Long)

object GetEntriesOverViewQuery {

    fun getEntriesOverviewHandler(ctx: Context){
        val userId: Long = ctx.getUserId()
        val entries = fetchEntriesOverview(userId)
        ctx.json(entries)
    }

    private fun fetchEntriesOverview(creatorId:Long): List<EntryQueryResponse> {
        return getJdbi().withHandle<List<EntryQueryResponse>, Exception> {
            it.createQuery("SELECT * FROM entry WHERE creatorid = :creatorId")
                .bind("creatorId", creatorId)
                .mapTo(EntryQueryResponse::class.java)
                .list()

        }
    }
}