package entries

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import commonFunctionality.ValidAuthenticationHeaderAdderUser2
import getJavalinApp
import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import kotlin.test.Test
import kotlin.test.assertEquals


class GetEntriesOverviewTest: PostgresContainerBaseTest() {

    @Test
    fun `not logged in user should not be able to fetch any entries`(){
        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.get("/api/entries").code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `overview only returns entries of this user`(){
        val user1 = UserFunctionality()
        val user2 = UserFunctionality(ValidAuthenticationHeaderAdderUser2())

        val entryTitle = "title"
        val entryText = "text"
        val entryToSaveOfUser1 = SaveEntryCommandData(entryTitle, entryText)
        val entryToSaveOfUser2 = SaveEntryCommandData("otherTitel", "otherText")
        val nrOfExpectedResults: Int = 1

        //save entries for testuser1 and 2
        user1.saveEntry(entryToSaveOfUser1)
        user2.saveEntry(entryToSaveOfUser2)

        val response: Array<SingleEntryOverviewEntryQueryResponse> = user1.getEntriesOverview()

        //retrieving entries for testuser1 should only return entries for testuser1 and not for testuser2
        assertEquals(nrOfExpectedResults, response.size)
        assertEquals(entryTitle, response[0].title)
    }

}