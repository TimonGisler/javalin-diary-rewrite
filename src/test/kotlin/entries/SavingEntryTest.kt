package entries

import getJavalinApp
import helperClasses.*
import io.javalin.http.HttpStatus
import io.javalin.json.JavalinJackson
import io.javalin.json.toJsonString
import io.javalin.testtools.JavalinTest
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class SavingEntryTest: PostgresContainerBaseTest(){

    @Test
    fun `not logged in user cannot save anything`(){
        val entryTitle = "title"
        val entryText = "text"
        val entryToSave = CreateEntryCommand(entryTitle, entryText)
        //start my javalin app (the javalinApp is passed otherwise a new one gets create, but this new one obviously isn't
        //correctly configured e.g. the routes will not be set up)
        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.post("/entries", entryToSave).code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response) //401 is the code for unauthorized
        }
    }

    @Test
    fun `logged in user can save entry`(){
        val entryTitle = "title"
        val entryText = "text"
        val entryToSave = CreateEntryCommand(entryTitle, entryText)
        val expectedEntryResponse: String = JavalinJackson().toJsonString(listOf(EntryQueryResponse(entryTitle, entryText)))

        JavalinTest.test(getJavalinApp()) { _, client ->
            client.post("/entries", entryToSave, AuthenticationHeaderAdder())

            val response: String? = client.get("/entries", AuthenticationHeaderAdder()).body?.string()

            assertEquals(expectedEntryResponse, response)
        }


    }

    @Test
    fun `after saving a entry user should also be able to retrieve it`(){
        val entryTitle = "title"
        val entryText = "text"
        val entryToSave = CreateEntryCommand(entryTitle, entryText)
        val expectedEntryResponse = JavalinJackson().toJsonString(listOf(EntryQueryResponse(entryTitle, entryText)))

        JavalinTest.test(getJavalinApp())  { _, client ->
            client.post("/entries", entryToSave, AuthenticationHeaderAdder())

            val response: String? = client.get("/entries", AuthenticationHeaderAdder()).body?.string()

            assertEquals(expectedEntryResponse, response)
        }
    }

    @Test
    fun `not logged in user should not be able to fetch any entries`(){
        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.get("/entries").code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `user with wrong pw or username should not be able to save anything`(){
        val encodedCredentials: String = Base64.getEncoder().encodeToString("somethingRandomAndWrong:thisPwDoesNotExist".toByteArray())

        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.get("/entries") {
                it.header("Authorization", "Basic $encodedCredentials")
            }.code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }
}