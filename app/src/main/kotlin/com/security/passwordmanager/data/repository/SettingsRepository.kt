package com.security.passwordmanager.data.repository

import android.security.keystore.UserNotAuthenticatedException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.security.passwordmanager.R
import com.security.passwordmanager.checkNetworkConnection
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.AppVersionInfo
import com.security.passwordmanager.data.model.settings.EncryptedSettings
import com.security.passwordmanager.data.model.settings.Settings
import com.security.passwordmanager.presentation.model.Time
import com.security.passwordmanager.presentation.model.enums.ColorDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class SettingsRepository(
    auth: FirebaseAuth,
    database: DatabaseReference,
    cryptoManager: CryptoManager
) : FirebaseEncryptedRepository(auth, database, cryptoManager) {
    private companion object {
        const val SettingsReference = "Settings"
        const val VersionInfoReference = "appVersion"
    }

    val username get() = currentUser?.displayName ?: currentUser?.email

    suspend fun addSettings(settings: Settings, resultAction: (Result<Unit>) -> Unit = {}) =
        withContext(Dispatchers.IO) {
            resultAction(Result.Loading)

            val userId = currentUser?.uid

            if (userId == null) {
                resultAction(Result.Error(Exception(context.getString(R.string.user_not_authenticated_exception))))
                return@withContext
            }

            getDatabaseReference(userId)
                .child(SettingsReference)
                .setValue(settings.encrypt(userId))
                .addOnSuccessListener {
                    resultAction(Result.Success(Unit))
                }
                .addOnFailureListener {
                    resultAction(Result.Error(it))
                }
        }


    suspend fun updateSettingsProperty(
        name: String,
        value: Any,
        resultAction: (Result<Unit>) -> Unit
    ) = withContext(Dispatchers.IO) {
        resultAction(Result.Loading)

        val uid = currentUser?.uid

        if (uid == null) {
            resultAction(Result.Error(Exception(context.getString(R.string.user_not_authenticated_exception))))
            return@withContext
        }

        getDatabaseReference(uid)
            .child(SettingsReference)
            .updateChildren(
                mapOf(name to value.encrypt(uid))
            )
            .addOnSuccessListener {
                resultAction(Result.Success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.Error(it))
            }
    }


    fun fetchSettings(): Flow<Result<Settings>> = callbackFlow {
        val uid = currentUser?.uid

        if (uid == null) {
            awaitClose {
                trySendBlocking(
                    Result.Error(
                        UserNotAuthenticatedException(context.getString(R.string.user_not_authenticated_exception)),
                    ),
                )
            }
            return@callbackFlow
        }


        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encryptedSettings = snapshot.getValue(EncryptedSettings::class.java)

                when (val value = encryptedSettings?.decrypt(uid)) {
                    null -> trySendBlocking(
                        Result.Error(NullPointerException(context.getString(R.string.cannot_find_settings)))
                    )

                    else -> trySendBlocking(Result.Success(value))
                        .onFailure { Result.Error(Exception(it)) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.Error(error.toException()))
            }
        }

        getDatabaseReference(uid)
            .child(SettingsReference)
            .addValueEventListener(listener)

        awaitClose {
            getDatabaseReference(uid).child(SettingsReference).removeEventListener(listener)
        }
    }


    suspend fun getSettings(resultAction: (Result<Settings>) -> Unit) =
        withContext(Dispatchers.IO) {
            resultAction(Result.Loading)

            val uid = currentUser?.uid

            if (uid == null) {
                resultAction(
                    Result.Error(
                        UserNotAuthenticatedException(context.getString(R.string.user_not_authenticated_exception))
                    )
                )
                return@withContext
            }


            getDatabaseReference(uid)
                .child(SettingsReference)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val encryptedSettings = snapshot.getValue(EncryptedSettings::class.java)

                        when (val settings = encryptedSettings?.decrypt(uid)) {
                            null -> resultAction(
                                Result.Error(
                                    NullPointerException(context.getString(R.string.cannot_find_settings))
                                )
                            )

                            else -> resultAction(Result.Success(settings))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        resultAction(Result.Error(error.toException()))
                    }
                })
        }


    suspend fun changeUsername(newUsername: String, result: (Result<Unit>) -> Unit) {
        withContext(Dispatchers.IO) {
            val currentUser = auth.currentUser
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
                        .addOnSuccessListener {
                            result(Result.Success(Unit))
                        }
                        .addOnFailureListener {
                            result(Result.Error(it))
                        }
                }
            }
        }
    }



    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        resultAction: (Result<Unit>) -> Unit
    ) = withContext(Dispatchers.IO) {
        resultAction(Result.Loading)

        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (currentUser == null || email == null) {
            resultAction(
                Result.Error(
                    UserNotAuthenticatedException(context.getString(R.string.user_not_authenticated_exception))
                )
            )
            return@withContext
        }
        else if (!context.checkNetworkConnection()) {
            resultAction(
                Result.Error(
                    Exception(context.getString(R.string.check_internet_connection))
                )
            )
            return@withContext
        }

        val authCredential = EmailAuthProvider.getCredential(email, oldPassword)

        currentUser.reauthenticate(authCredential)
            .addOnSuccessListener {
                currentUser.updatePassword(newPassword)
                    .addOnSuccessListener {
                        resultAction(Result.Success(Unit))
                    }
                    .addOnFailureListener {
                        resultAction(Result.Error(it.getRightMessages()))
                    }
            }
            .addOnFailureListener {
                resultAction(Result.Error(it.getRightMessages()))
            }
    }



    suspend fun getCurrentAppVersionInfo(
        resultAction: (Result<AppVersionInfo>) -> Unit
    ): Unit = withContext(Dispatchers.IO) {

        resultAction(Result.Loading)
        getDatabaseReference(VersionInfoReference)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when (val versionInfo = snapshot.getValue(AppVersionInfo::class.java)) {
                        null -> resultAction(
                            Result.Error(
                                NullPointerException(context.getString(R.string.cannot_find_info))
                            )
                        )

                        else -> resultAction(Result.Success(versionInfo))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    resultAction(Result.Error(error.toException()))
                }

            })
    }


    fun signOut() {
        auth.signOut()
    }


    private fun Settings.encrypt(userId: String) = EncryptedSettings(
        colorDesign = colorDesign.encrypt(userId),
        sunriseTime = sunsetTime.encrypt(userId),
        sunsetTime = sunsetTime.encrypt(userId),
        beautifulFont = beautifulFont.encrypt(userId),
        autofill = autofill.encrypt(userId),
        dynamicColor = dynamicColor.encrypt(userId),
        pullToRefresh = pullToRefresh.encrypt(userId),
        loadIcons = loadIcons.encrypt(userId),
    )


    private fun EncryptedSettings.decrypt(userId: String): Settings {
        fun toBoolean(string: String) = when (string) {
            "true" -> true
            else -> false
        }


        return Settings(
            colorDesign = colorDesign.decrypt(userId) { ColorDesign.valueOf(it) },
            sunriseTime = sunriseTime.decrypt(userId) { Time(it) },
            sunsetTime = sunsetTime.decrypt(userId) { Time(it) },
            beautifulFont = beautifulFont.decrypt(userId, ::toBoolean),
            autofill = autofill.decrypt(userId, ::toBoolean),
            dynamicColor = dynamicColor.decrypt(userId, ::toBoolean),
            pullToRefresh = pullToRefresh.decrypt(userId, ::toBoolean),
            loadIcons = loadIcons.decrypt(userId, ::toBoolean),
        )
    }
}