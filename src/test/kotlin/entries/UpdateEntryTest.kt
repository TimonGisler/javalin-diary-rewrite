package entries

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import commonFunctionality.ValidAuthenticationHeaderAdderUser2
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateEntryTest : PostgresContainerBaseTest() {

    @Test
    fun `I am unable to modify other persons entries`() {
        val user1 = UserFunctionality()
        val user2 = UserFunctionality(ValidAuthenticationHeaderAdderUser2())

        val entryId = user1.saveEntry(SaveEntryCommandData("title", "text"))
        val code = user2.updateEntry(UpdateEntryCommandData("new title", "new text"), entryId.toLong())

        assertEquals(403, code)
    }

    @Test
    fun `After modifying entry entry should be changed after fetch`() {
        val user1 = UserFunctionality()
        val entryId = user1.saveEntry(SaveEntryCommandData("title", "text"))
        val updateEntryCommand = UpdateEntryCommandData("new title", "new text")

        user1.updateEntry(updateEntryCommand, entryId.toLong())
        val entryAfterUpdate: GetEntryCommandResponse = user1.getEntry(entryId)!!


        assertEquals(updateEntryCommand.updatedTitle, entryAfterUpdate.title)
        assertEquals(updateEntryCommand.updatedText, entryAfterUpdate.text)
    }

}