package entries

import JdbiProvider.getJdbi
import common.getUserId
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class CreateEntryCommand(val title: String, val text: String)

object SaveEntryCommand {
    private val logger: Logger = LoggerFactory.getLogger(SaveEntryCommand::class.java)

    fun saveNewEntryHandler(ctx: Context){
        logger.info("SaveEntryCommand.saveNewEntryHandler() called")
        val createEntryData: CreateEntryCommand = ctx.bodyAsClass()
        val idOfSavedEntry: Long = saveNewEntry(createEntryData, ctx.getUserId())
        ctx.status(HttpStatus.CREATED)
        ctx.json(idOfSavedEntry)
    }

    private fun saveNewEntry(createEntryCommand: CreateEntryCommand, userId: Long): Long {
        return getJdbi().withHandle<Long, Exception > { handle ->
            handle.createUpdate(
                "INSERT INTO entry (title, text, creatorid) VALUES (:title, :text, :creatorId)"
            )
                .bind("title", createEntryCommand.title)
                .bind("text", createEntryCommand.text)
                .bind("creatorId", userId)
                .executeAndReturnGeneratedKeys("id")
                .mapTo(Long::class.java)
                .one()
        }
    }

}