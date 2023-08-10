package common

data class UserPassword(val password: String) {
    init {
        //validation
        if (password.isBlank()) throw IllegalArgumentException("password must not be blank")
    }
}
