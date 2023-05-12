package entries

import JdbiProvider.getJdbi
import io.javalin.http.Context
import io.javalin.http.HttpStatus
import io.javalin.http.bodyAsClass
import io.javalin.security.AccessManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DeleteEntryCommand {
    private val logger: Logger = LoggerFactory.getLogger(AccessManager::class.java)

    fun deleteEntryHandler(ctx: Context) {
        val deleteEntryId: Long = ctx.pathParam("entryId").toLong()
        logger.info("DeleteEntryCommand.deleteEntryHandler() called with deleteEntryId: $deleteEntryId")
        deleteEntry(deleteEntryId)
        ctx.status(HttpStatus.NO_CONTENT)
    }

    private fun deleteEntry(entryIdToDelete:Long) {
        getJdbi().useHandle<Exception> { handle ->
            handle.createUpdate("DELETE FROM entry WHERE id = :id")
                .bind("id", entryIdToDelete)
                .execute()
        }
    }
}