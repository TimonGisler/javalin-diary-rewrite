package entries

import io.javalin.http.Context

object SaveEntryCommand {

    // return value
    fun saveNewEntryHandler(ctx: Context){
        saveNewEntry(
            0,
            ctx.formParam("title")!!,
            ctx.formParam("text")!!
        )
    }

    private fun saveNewEntry(userId: Long, title: String, text: String){
        println("Saving new entry for user $userId with title $title and text $text")
    }
}