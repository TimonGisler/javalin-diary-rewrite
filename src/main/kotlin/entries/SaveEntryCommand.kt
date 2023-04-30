package entries

import JdbiProvider.getJdbi
import common.getUserId
import io.javalin.http.Context
import io.javalin.http.bodyAsClass

data class CreateEntryCommand(val title: String, val text: String)

object SaveEntryCommand {
    fun saveNewEntryHandler(ctx: Context){
        val createEntryData: CreateEntryCommand = ctx.bodyAsClass()
        saveNewEntry(createEntryData, ctx.getUserId())
    }

    private fun saveNewEntry(createEntryCommand: CreateEntryCommand, userId: Long){

        getJdbi().useHandle<Exception> { handle ->


            handle.createUpdate(
                "INSERT INTO entry (title, text, creatorid) VALUES (:title, :text, :creatorId)"
            )
                .bind("title", createEntryCommand.title)
                .bind("text", createEntryCommand.text)
                .bind("creatorId", userId)
                .execute()
        }
    }
}