package entries

import io.javalin.http.Context
import io.javalin.http.bodyAsClass
import jdbi

data class CreateEntryCommand(val title: String, val text: String)

object SaveEntryCommand {
    fun saveNewEntryHandler(ctx: Context){
        val createEntryData: CreateEntryCommand = ctx.bodyAsClass()

        saveNewEntry(createEntryData)
    }

    private fun saveNewEntry(createEntryCommand: CreateEntryCommand){

        jdbi.useHandle<Exception> { handle ->
            handle.createUpdate(
                "INSERT INTO entry (title, text) VALUES (:title, :text)"
            )
                .bind("title", createEntryCommand.title)
                .bind("text", createEntryCommand.text)
                .execute()
        }
    }
}