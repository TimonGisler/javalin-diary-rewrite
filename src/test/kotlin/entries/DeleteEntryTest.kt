package entries

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import commonFunctionality.ValidAuthenticationHeaderAdderUser2
import getJavalinApp
import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import okhttp3.Response
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class DeleteEntryTest: PostgresContainerBaseTest() {

    @Test
    fun `not logged in user should not be able to delete any entries`() {
        JavalinTest.test(getJavalinApp()) { _, client ->
            val response = client.delete("/api/entries/1").code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `I am unable to delete other persons entries`() {
        val user1 = UserFunctionality()
        val user2 = UserFunctionality(ValidAuthenticationHeaderAdderUser2())

        val entryTitle = "title"
        val entryText = "text"
        val entryToSaveOfUser1 = SaveEntryCommandData(entryTitle, entryText)
        val entryToSaveOfUser2 = SaveEntryCommandData("otherTitel", "otherText")

        //save entries for testuser1 and 2
        val entryIdOfUser1: Long = user1.saveEntry(entryToSaveOfUser1).toLong()
        val entryIdOfUser2 = user2.saveEntry(entryToSaveOfUser2)

        //try to delete entry of testuser2 as testuser1
        val response: Response = user1.deleteEntry(entryIdOfUser2)

        //retrieving entries for testuser1 should only return entries for testuser1 and not for testuser2
        assertEquals(
            HttpStatus.FORBIDDEN.code,
            response.code,
            "when testuser tries to delete entry of testuser2, he should get a forbidden response but got ${response.code}"
        )

    }

    @Test
    fun `Entry deletion works`() {
        //To check this 2 things are checked: 1. it does not appear in the overview anymore 2. it is not fetch-able anymore (with the id)
        val user1: UserFunctionality = UserFunctionality();

        //save entry and count how many entries now exist
        val newEntryId = user1.saveEntry(SaveEntryCommandData("title", "text"))
        val noOfEntriesAfterSave: Int = user1.getEntriesOverview().size

        //delete entry and count how many entries now exist (should be one less)
        user1.deleteEntry(newEntryId)
        val noOfEntriesAfterDelete: Int = user1.getEntriesOverview().size

        //try to fetch entry (should not be possible)
        //val entry: SingleEntryQueryResponse? = user1.getEntry(newEntryId)
        val entry: GetEntryCommandResponse? = user1.getEntry(newEntryId)

        assertEquals(noOfEntriesAfterSave - 1, noOfEntriesAfterDelete)
        assertNull(entry) // no entry should exist



    }

}