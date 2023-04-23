package entries

import helperClasses.PostgresContainerBaseTest
import io.javalin.json.JavalinJackson
import io.javalin.json.toJsonString
import io.javalin.testtools.JavalinTest
import javalinApp
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

//I test both because it is easier, otherwise how do I check that the entries really were saved?
//should I write custom query? This query would break the second I change the schema.
class SavingAndRetrievingEntryTest: PostgresContainerBaseTest(){

    @Test
    fun `after saving a entry user should also be able to retrieve it`(){
        val entryTitle = "title"
        val entryText = "text"
        val entryToSave = CreateEntryCommand(entryTitle, entryText)
        val expectedEntryResponse = JavalinJackson().toJsonString(listOf(EntryQueryResponse(entryTitle, entryText)))
        //update jdbi to use test db
        JavalinTest.test(javalinApp)  { _, client ->
            client.post("/entries", entryToSave)
            val response = client.get("/entries").body?.string()

            assertEquals(expectedEntryResponse, response)
        }
    }

    @Test
    fun `only the user that saved an entry should be able to retrieve it again`(){
        //TODO TGIS
    }
}