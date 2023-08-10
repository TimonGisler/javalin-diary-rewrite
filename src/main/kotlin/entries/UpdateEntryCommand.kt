package entries

import JdbiProvider.getJdbi
import io.javalin.http.Context
import io.javalin.http.bodyAsClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class UpdateEntryCommandData(val updatedTitle: String, val updatedText: String)

object UpdateEntryCommand {
    private val logger: Logger = LoggerFactory.getLogger(UpdateEntryCommand::class.java)

    fun updateEntry(ctx: Context) {
        val updateEntryCommandData: UpdateEntryCommandData = ctx.bodyAsClass()
        val idOfEntryToUpdate: Long = ctx.pathParam("entryId").toLong()

        logger.info("UpdateEntryCommand.updateEntry() called with updateEntryCommandData: $updateEntryCommandData")

        getJdbi().useHandle<Exception> { handle ->
            handle.createUpdate(
                "UPDATE entry SET title = :updatedTitle, text = :updatedText WHERE id = :idOfEntryToUpdate"
            )
                .bind("updatedTitle", updateEntryCommandData.updatedTitle)
                .bind("updatedText", updateEntryCommandData.updatedText)
                .bind("idOfEntryToUpdate", idOfEntryToUpdate)
                .execute()
        }
    }

}