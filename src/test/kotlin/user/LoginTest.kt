package user

import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginTest: PostgresContainerBaseTest() {

    @Test
    fun `After successful login user receives a 200 ok`(){
        //it is important to somehow tell the client that the infos were correct
        val user1 = UserFunctionality()
        val code = user1.login()

        assertEquals(200, code, "Login should return 200 ok")
    }
}