package com.security.passwordmanager.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.security.passwordmanager.R
import com.security.passwordmanager.checkNetworkConnection
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.Settings
import com.security.passwordmanager.data.room.SettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val settingsDao: SettingsDao,
    private val firebaseAuth: FirebaseAuth
) {
    private suspend fun addSettings(settings: Settings) {
        settingsDao.addSettings(settings)
    }

    suspend fun updateSettings(settings: Settings) {
        settingsDao.updateSettings(settings)
    }


    fun getSettings(email: String): Flow<Settings> =
        settingsDao
            .getSettings(email)
            .map {
                return@map if (it == null) {
                    val newSettings = Settings(email = email)
                    addSettings(newSettings)
                    newSettings
                }
                else it
            }



    fun changeUsername(newUsername: String, context: Context, result: (Result<Unit>) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        when {
            currentUser == null -> {
                result(Result.Error(Exception(context.getString(R.string.change_username_exception))))
            }
            !context.checkNetworkConnection() -> {
                result(Result.Error(Exception(context.getString(R.string.check_internet_connection))))
            }
            else -> {
                result(Result.Loading)
                currentUser
                    .updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername)
                            .build()
                    )
                    .addOnCompleteListener {
                        if (it.isSuccessful) result(Result.Success(Unit))
                    }
                    .addOnFailureListener {
                        result(Result.Error(it))
                    }
            }
        }
    }


    fun signOut() {
        firebaseAuth.signOut()
    }
}