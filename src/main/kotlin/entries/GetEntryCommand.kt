package entries

import JdbiProvider.getJdbi
import io.javalin.http.Context
import java.time.OffsetDateTime

//return value
data class GetEntryCommandResponse(val title: String, val creationDate: OffsetDateTime, val id: Long)

object GetEntryCommand {
    fun getEntryCommandHandler(ctx: Context) {
        val idOfEntryToGet: Long = ctx.pathParam("entryId").toLong()
        ctx.json(getEntry(idOfEntryToGet))
    }

    private fun getEntry(idOfEntryToGet: Long): GetEntryCommandResponse {
        return getJdbi().withHandle<GetEntryCommandResponse, Exception> { handle ->
            handle.createQuery("SELECT * FROM entry WHERE id = :id")
                .bind("id", idOfEntryToGet)
                .bind("title", "title")
                .mapTo(GetEntryCommandResponse::class.java)
                .one()
        }
    }

}

