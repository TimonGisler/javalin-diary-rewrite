package entries

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import commonFunctionality.ValidAuthenticationHeaderAdderUser2
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.test.*

class GetSpecificEntryTest: PostgresContainerBaseTest(){

    @Test
    fun `I am unable to get other persons entries`() {
        val user1: UserFunctionality = UserFunctionality()
        val user2: UserFunctionality = UserFunctionality(ValidAuthenticationHeaderAdderUser2())

        val newEntryId = user1.saveEntry(SaveEntryCommandData("title", "text"))

        val entryOfUser1 = user1.getEntry(newEntryId)
        val entryOfUser1FetchetByUser2 = user2.getEntry(newEntryId)

        assertNull(entryOfUser1FetchetByUser2, "User2 should not be able to get the entry of user1 --> entry should be null")
        assertNotNull(entryOfUser1, "User1 should be able to get the entry of user1 --> entry should not be null")
    }

    @Test
    fun `After saving entry I should be able to retrieve it and it should contain correct text and title`() {
        val user1: UserFunctionality = UserFunctionality()
        val createEntryCommand: SaveEntryCommandData = SaveEntryCommandData("testtitle", "testtext")
        val entryId: Int = user1.saveEntry(createEntryCommand)

        val entry: GetEntryCommandResponse = user1.getEntry(entryId)!!

        assertEquals(createEntryCommand.title, entry.title, "The title of the entry should be the same as the title of the createEntryCommand")
        assertEquals(createEntryCommand.text, entry.text, "The text of the entry should be the same as the text of the createEntryCommand")
    }

    @Test
    fun `Entry should return with correct date`() {
        val user1: UserFunctionality = UserFunctionality()
        val idOfNewEntry = user1.saveEntry(SaveEntryCommandData("title", "text"))

        val newEntry = user1.getEntry(idOfNewEntry)
        val durationBetweenDates = Duration.between(newEntry!!.creationDate, OffsetDateTime.now()).toSeconds()


        //duration between dates should not be more than 20 seconds
        assertTrue(durationBetweenDates < 20, "Duration between dates should not be more than 20 seconds")

    }
}