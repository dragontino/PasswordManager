package com.security.passwordmanager.data

import com.security.passwordmanager.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String) = try {
        // TODO: handle loggedInUser authentication
        val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
        Result.Success(fakeUser)
    } catch (e: Throwable) {
        Result.Error(IOException("Error logging in", e))
    }

    fun logout() {
        // TODO: revoke authentication
    }
}