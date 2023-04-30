import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.statement.SqlLogger
import org.jdbi.v3.core.statement.StatementContext
import org.slf4j.LoggerFactory
import java.sql.SQLException

private const val dbUrl = "jdbc:postgresql://localhost:5433/postgres" //TODO TGIS, use system.getenv to get connection and pw
private const val dbUser = "postgres"
private const val dbPassword = "Hallo123_"

object JdbiProvider{

    /**
     * This function is used to set up the jdbi instance for testing
     * It replaces the [customJdbi] with a new base instance which accesses the testing database
     */
    fun setupTestingJdbi(dbUrl: String, dbUser: String, dbPassword: String){
        customJdbi = Jdbi.create(dbUrl, dbUser, dbPassword)
    }


    /**
     * set this jdbi if not the default jdbi should be created (for example when testing)
     */
    private var customJdbi: Jdbi? = null

    fun getJdbi(): Jdbi {
        return (customJdbi ?: Jdbi.create(dbUrl, dbUser, dbPassword))
            .installPlugin(KotlinPlugin())
            .setSqlLogger(CustomSqlLogger())
    }

}


/**
 * Custom logger which logs the query that actually got executed
 */
private class CustomSqlLogger : SqlLogger {
    private val logger = LoggerFactory.getLogger(CustomSqlLogger::class.java)

    override fun logBeforeExecution(context: StatementContext) {
        logger.info("Executing query: ${context.statement}")
    }

    override fun logAfterExecution(context: StatementContext) {}
    override fun logException(context: StatementContext, ex: SQLException) {}
}