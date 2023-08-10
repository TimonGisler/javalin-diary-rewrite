package user

import User.RegisterCommandData
import common.UserEmail
import common.UserPassword
import commonFunctionality.CustomValidAuthenticationHeaderAdderUser
import commonFunctionality.PostgresContainerBaseTest
import commonFunctionality.UserFunctionality
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterTest : PostgresContainerBaseTest() {

    @Test
    fun `After successful registering user receives a 200 ok`(){
        //somehow I need to tell the client that everything worked, and then I can display success message or smith
        val user1 = UserFunctionality()
        val registerData = RegisterCommandData(UserEmail("testmail"), UserPassword("testpw"));
        val code: Int = user1.register(registerData)

        assertEquals(200, code, "The user should receive a 200 sucessfuly ok after registering")
    }

    @Test
    fun `After registring a new user exists which can login`(){
        val user1 = UserFunctionality()
        val email: String = "nonexistentemail"
        val password: String = "somerandompw"

        val customAuthenticationHeaderAdder: CustomValidAuthenticationHeaderAdderUser = CustomValidAuthenticationHeaderAdderUser(email, password)
        val registerData = RegisterCommandData(UserEmail(email), UserPassword(password));

        val loginCodeBeforeRegistering = user1.login(customAuthenticationHeaderAdder)
        user1.register(registerData)
        val loginCodeAfterRegistering = user1.login(customAuthenticationHeaderAdder)

        assertEquals(401, loginCodeBeforeRegistering, "The user should not be able to login before registering")
        assertEquals(200, loginCodeAfterRegistering, "The user should be able to login after registering")
    }

}