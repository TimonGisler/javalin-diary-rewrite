package entries

import getJavalinApp
import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.ValidAuthenticationHeaderAdderUser1
import commonFunctionality.ValidAuthenticationHeaderAdderUser2
import commonFunctionality.parseBodyToObject
import io.javalin.http.HttpStatus
import io.javalin.json.JavalinJackson
import io.javalin.json.fromJsonString
import io.javalin.testtools.JavalinTest
import okhttp3.Response
import kotlin.test.Test
import kotlin.test.assertEquals


class GetEntriesOverviewTest: PostgresContainerBaseTest() {

    @Test
    fun `not logged in user should not be able to fetch any entries`(){
        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.get("/entries").code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `overview only returns entries of this user`(){
        val entryTitle = "title"
        val entryText = "text"
        val entryToSaveOfUser1 = CreateEntryCommand(entryTitle, entryText)
        val entryToSaveOfUser2 = CreateEntryCommand("otherTitel", "otherText")
        val nrOfExpectedResults: Int = 1

        JavalinTest.test(getJavalinApp())  { _, client ->
            //save entries for testuser1 and 2
            client.post("/entries", entryToSaveOfUser1, ValidAuthenticationHeaderAdderUser1())
            client.post("/entries", entryToSaveOfUser2, ValidAuthenticationHeaderAdderUser2())

            //TODO TGIS, kotlin is too stupid to create EntryQueryResponse from the json, it uses a LinkedHashMap instead of EntryQueryResponse...
            val responseResponse: Response = client.get("/entries", ValidAuthenticationHeaderAdderUser1())
            val response: Array<EntryQueryResponse> = client.get("/entries", ValidAuthenticationHeaderAdderUser1()).parseBodyToObject()!!

            //retrieving entries for testuser1 should only return entries for testuser1 and not for testuser2
            assertEquals(nrOfExpectedResults, response.size)
            assertEquals(entryTitle, response[0].title)
        }

    }

}