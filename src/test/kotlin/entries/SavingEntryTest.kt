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

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `logged in user can save entry`(){
        val entryTitle = "title"
        val entryText = "text"
        val entryToSave = CreateEntryCommand(entryTitle, entryText)

        JavalinTest.test(getJavalinApp()) { _, client ->
            val response: Int = client.post("/entries", entryToSave, ValidAuthenticationHeaderAdderUser1()).code

            assertEquals(HttpStatus.CREATED.code, response)
        }


    }

    @Test
    fun `user with wrong pw should not be able to save anything`(){
        val encodedCredentials: String = Base64.getEncoder().encodeToString("$testUser1Mail:thisPwDoesNotExist".toByteArray())

        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.get("/entries") {
                it.header("Authorization", "Basic $encodedCredentials")
            }.code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }
}