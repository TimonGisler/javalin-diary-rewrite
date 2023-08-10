package user

import commonFunctionality.UserFunctionality
import org.junit.jupiter.api.fail
import kotlin.test.Test

class RegisterTest {

    @Test
    fun `After successful registering user receives a 200 ok`(){
        //somehow I need to tell the client that everything worked, and then I can display success message or smith
        val user1 = UserFunctionality()

        val code: Int = user1.register()
    }

    @Test
    fun `After registring a new user exists which can login`(){
        fail("not implemented")
    }

}