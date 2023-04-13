import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin

val dbUrl = "jdbc:postgresql://localhost:5433/postgres"
val dbUser = "postgres"
val dbPassword = "Hallo123_"


val jdbi = Jdbi.create(dbUrl, dbUser, dbPassword)
    .installPlugin(KotlinPlugin())