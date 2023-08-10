package User

import JdbiProvider.getJdbi
import common.UserEmail
import common.UserPassword
import io.javalin.http.Context

data class RegisterCommandData(val userEmail: UserEmail, val userPassword: UserPassword)

object RegisterCommand {
    fun registerUserHandler(ctx: Context) {
        val registerCommandData: RegisterCommandData = ctx.bodyAsClass(RegisterCommandData::class.java)

        getJdbi().useHandle<Exception>() {
            //TODO TGIS, test whether or not this is safe against sql injection, it should be, but still interesting --> documtent it in obsidian
            it.createUpdate("INSERT INTO diaryUser (email, password) VALUES (:email, :password)")
                .bind("email", registerCommandData.userEmail.email)
                .bind("password", registerCommandData.userPassword.password)
                .execute()
        }
    }

}