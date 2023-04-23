package entries

import io.javalin.json.JavalinJackson
import io.javalin.json.toJsonString
import io.javalin.testtools.JavalinTest
import javalinApp
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals

//I test both because it is easier, otherwise how do I check that the entries really were saved?
//should I write custom query? This query would break the second I change the schema.
@Testcontainers
class SavingAndRetrievingEntriesTest{

    // will be started before and stopped after each test method (must be static that that does not happen)
    @Container
    var postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15.2-alpine")
        .withExposedPorts(5432)
        .withDatabaseName("testDbName")
        .withUsername("testUserName")
        .withPassword("testPw")

    @BeforeEach
    fun setupDb() {
        val jdbcUrl = postgresContainer.jdbcUrl
        val username = postgresContainer.username
        val password = postgresContainer.password

        Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .loggers("slf4j")
            .load()
            .migrate()

        JdbiProvider.setupTestingJdbi(jdbcUrl, username, password)
    }



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


}