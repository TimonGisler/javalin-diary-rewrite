package helperClasses

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * sets up a postgres db in a docker container and migrates it to the latest version.
 * Additionally, it sets up a jdbi instance that uses the test db.
 * This is done before every single @Test method (before every method call the db and the jdbi get reset).
 */
@Testcontainers
abstract class PostgresContainerBaseTest {

    // will be started before and stopped after each test method (must be static that that does not happen)
    @Container
    var postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15.2-alpine")
        .withExposedPorts(5432)
        .withDatabaseName("testDbName")
        .withUsername("testUserName")
        .withPassword("testPw")
//TODO TGIS create an implementation which does not always create a new container but uses the same one for all tests

    @BeforeEach
    fun setupDb() {
        val jdbcUrl = postgresContainer.jdbcUrl
        val username = postgresContainer.username
        val password = postgresContainer.password

        // migrate db so that it is identical to my production db
        Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .loggers("slf4j")
            .load()
            .migrate()

        JdbiProvider.setupTestingJdbi(jdbcUrl, username, password)
    }
}