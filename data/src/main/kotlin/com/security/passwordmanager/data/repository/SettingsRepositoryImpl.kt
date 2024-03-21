package com.security.passwordmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.util.checkNetworkConnection
import com.security.passwordmanager.domain.model.AppVersionInfo
import com.security.passwordmanager.domain.model.ChangeUsernameException
import com.security.passwordmanager.domain.model.ColorDesign
import com.security.passwordmanager.domain.model.InformationNotFoundException
import com.security.passwordmanager.domain.model.InternetConnectionLostException
import com.security.passwordmanager.domain.model.SettingsNotFoundException
import com.security.passwordmanager.domain.model.Time
import com.security.passwordmanager.domain.model.UpdateSettingsException
import com.security.passwordmanager.domain.model.UserNotLoggedException
import com.security.passwordmanager.domain.model.UsernameNotFoundException
import com.security.passwordmanager.domain.model.settings.EncryptedSettings
import com.security.passwordmanager.domain.model.settings.Settings
import com.security.passwordmanager.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepositoryImpl(
    auth: FirebaseAuth,
    database: FirebaseDatabase,
    cryptoManager: CryptoManager
) : FirebaseEncryptedRepository(auth, database, cryptoManager), SettingsRepository {
    private companion object {
        const val SettingsReference = "Settings"
        const val VersionInfoReference = "appVersion"
    }

    override suspend fun addSettings(settings: Settings, resultAction: (Result<Unit>) -> Unit) {
        val userId = currentUser?.uid
            ?: return resultAction(Result.failure(UserNotLoggedException))

        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        getDatabaseReference(userId)
            .child(SettingsReference)
            .setValue(settings.encrypt(userId))
            .addOnSuccessListener {
                resultAction(Result.success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.failure(it.map()))
            }
    }


    override suspend fun updateSettingsProperty(
        name: String,
        value: Any,
        resultAction: (Result<Unit>) -> Unit
    ) {
        val uid = currentUser?.uid
            ?: return resultAction(Result.failure(UserNotLoggedException))

        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        getDatabaseReference(uid)
            .child(SettingsReference)
            .updateChildren(mapOf(name to value.encrypt(uid)))
            .addOnSuccessListener {
                resultAction(Result.success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.failure(it.mapOrNull() ?: UpdateSettingsException(name)))
            }
    }


    override fun fetchSettings(): Flow<Result<Settings>> = callbackFlow {
        val uid = currentUser?.uid

        if (!context.checkNetworkConnection()) {
            awaitClose {
                trySendBlocking(Result.failure(InternetConnectionLostException))
            }
        }

        if (uid == null) {
            awaitClose {
                trySendBlocking(Result.failure(UserNotLoggedException))
            }
            return@callbackFlow
        }


        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encryptedSettings = snapshot.getValue(EncryptedSettings::class.java)

                when (val value = encryptedSettings?.decrypt(uid)) {
                    null -> trySendBlocking(Result.failure(SettingsNotFoundException))
                    else -> trySendBlocking(Result.success(value))
                        .onFailure {
                            Result.failure<Settings>(Exception(it).map())
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.failure(error.toException().map()))
            }
        }

        getDatabaseReference(uid)
            .child(SettingsReference)
            .addValueEventListener(listener)

        awaitClose {
            getDatabaseReference(uid).child(SettingsReference).removeEventListener(listener)
        }
    }


    override suspend fun getSettings(resultAction: (Result<Settings>) -> Unit) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        val uid = currentUser?.uid
            ?: return resultAction(Result.failure(UserNotLoggedException))

        getDatabaseReference(uid)
            .child(SettingsReference)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val encryptedSettings = snapshot.getValue(EncryptedSettings::class.java)

                    when (val settings = encryptedSettings?.decrypt(uid)) {
                        null -> resultAction(Result.failure(SettingsNotFoundException))
                        else -> resultAction(Result.success(settings))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    resultAction(Result.failure(error.toException().map()))
                }
            })
    }


    override suspend fun changeUsername(newUsername: String, resultAction: (Result<Unit>) -> Unit) {
        val currentUser = auth.currentUser
        when {
            currentUser == null -> {
                resultAction(Result.failure(UserNotLoggedException))
            }
            !context.checkNetworkConnection() -> {
                resultAction(Result.failure(InternetConnectionLostException))
            }
            else -> {
                currentUser
                    .updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(newUsername)
                            .build()
                    )
                    .addOnSuccessListener {
                        resultAction(Result.success(Unit))
                    }
                    .addOnFailureListener {
                        resultAction(
                            Result.failure(it.mapOrNull() ?: ChangeUsernameException)
                        )
                    }
            }
        }
    }


    override fun getUsername(): Result<String> {
        val user = currentUser ?: return Result.failure(UserNotLoggedException)

        val username = user.displayName.takeIf { !it.isNullOrBlank() }
        val email = user.email.takeIf { !it.isNullOrBlank() }

        return (username ?: email)
            ?.let(Result.Companion::success)
            ?: Result.failure(UsernameNotFoundException)
    }



    override suspend fun getAppVersionInfo(
        resultAction: (Result<AppVersionInfo>) -> Unit
    ) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        getDatabaseReference(VersionInfoReference)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when (val versionInfo = snapshot.getValue(AppVersionInfo::class.java)) {
                        null -> resultAction(Result.failure(InformationNotFoundException))
                        else -> resultAction(Result.success(versionInfo))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    resultAction(Result.failure(error.toException().map()))
                }
            })
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