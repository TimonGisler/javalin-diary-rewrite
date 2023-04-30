package helperClasses

import JdbiProvider.getJdbi

/**
 * A file which contains some magic variables which are used in the tests.
 */

const val testUserMail : String = "test@test.com"
const val testUserPassword : String = "testpw"
val testUserId:Long = getJdbi().withHandle<Long, Exception>{
    it.createQuery("SELECT uniqueid FROM public.user  WHERE email = :email AND password = :password")
        .bind("email", testUserMail)
        .bind("password", testUserPassword)
        .mapTo(Long::class.java)
        .one()
}
