package entries

import commonFunctionality.*
import getJavalinApp
import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import io.javalin.testtools.TestConfig
import okhttp3.Response
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull


class DeleteEntryTest: PostgresContainerBaseTest() {

    @Test
    fun `not logged in user should not be able to delete any entries`() {
        JavalinTest.test(getJavalinApp()) { _, client ->
            val response = client.delete("/entries/1").code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `I am unable to delete other persons entries`() {
        val entryTitle = "title"
        val entryText = "text"
        val entryToSaveOfUser1 = CreateEntryCommand(entryTitle, entryText)
        val entryToSaveOfUser2 = CreateEntryCommand("otherTitel", "otherText")
        val nrOfExpectedResults: Int = 1

        JavalinTest.test(getJavalinApp(), TestConfig(captureLogs = false)) { _, client ->
            //save entries for testuser1 and 2
            val entryIdOfUser1: Long =
                client.post("/entries", entryToSaveOfUser1, ValidAuthenticationHeaderAdderUser1()).parseBodyToObject()!!
            val entryIdOfUser2: Long =
                client.post("/entries", entryToSaveOfUser2, ValidAuthenticationHeaderAdderUser2()).parseBodyToObject()!!

            //try to delete entry of testuser2 as testuser1
            val response: Response =
                client.delete("/entries/$entryIdOfUser2", req = ValidAuthenticationHeaderAdderUser1())
            //retrieving entries for testuser1 should only return entries for testuser1 and not for testuser2
            assertEquals(
                HttpStatus.FORBIDDEN.code,
                response.code,
                "when testuser tries to delete entry of testuser2, he should get a forbidden response but got ${response.code}"
            )
        }

    }

    @Test
    fun `Entry deletion works`() {
        //To check this 2 things are checked: 1. it does not appear in the overview anymore 2. it is not fetch-able anymore (with the id)
        val user1: UserFunctionality = UserFunctionality();

        //save entry and count how many entries now exist
        val newEntryId = user1.saveEntry(CreateEntryCommand("title", "text"))
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