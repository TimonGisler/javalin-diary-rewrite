import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.statement.Slf4JSqlLogger
import org.jdbi.v3.core.statement.SqlLogger
import org.jdbi.v3.core.statement.StatementContext
import org.slf4j.LoggerFactory
import java.sql.SQLException

private const val dbUrl = "jdbc:postgresql://localhost:5433/postgres"
private const val dbUser = "postgres"
private const val dbPassword = "Hallo123_"


val jdbi: Jdbi = Jdbi.create(dbUrl, dbUser, dbPassword)
    .installPlugin(KotlinPlugin())
    .setSqlLogger(CustomSqlLogger())


private class CustomSqlLogger : SqlLogger {
    private val logger = LoggerFactory.getLogger(CustomSqlLogger::class.java)

    override fun logBeforeExecution(context: StatementContext) {
        logger.info("Executing query: ${context.statement}")
    }

    override fun logAfterExecution(context: StatementContext) {}
    override fun logException(context: StatementContext, ex: SQLException) {}
}