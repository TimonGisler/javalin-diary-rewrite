package entries

import getJavalinApp
import io.javalin.http.HttpStatus
import io.javalin.testtools.JavalinTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class DeleteEntryTest {
    //TODO TGIS

    @Test
    fun `not logged in user should not be able to delete any entries`(){
        JavalinTest.test(getJavalinApp())  { _, client ->
            val response = client.delete("/entries/1").code

            assertEquals(HttpStatus.UNAUTHORIZED.code, response)
        }
    }

    @Test
    fun `I am unable to delete other persons entries`(){
    }

    @Test
    fun `After deleting entry it should no longer appear in overview`(){
    }

    @Test
    fun `After deleting entry it should no longer be fetch-able`(){
    }
}