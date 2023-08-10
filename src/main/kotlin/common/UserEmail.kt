package common

data class UserEmail(val email: String) {
    init {
        //validation
        if (email.isBlank()) throw IllegalArgumentException("email must not be blank")
    }
}
