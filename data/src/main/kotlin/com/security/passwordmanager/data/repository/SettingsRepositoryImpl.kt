package com.security.passwordmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.security.passwordmanager.data.crypto.CryptoManager
import com.security.passwordmanager.data.model.SettingsDB
import com.security.passwordmanager.data.util.checkNetworkConnection
import com.security.passwordmanager.domain.model.AppVersionInfo
import com.security.passwordmanager.domain.model.ChangeUsernameException
import com.security.passwordmanager.domain.model.DecryptionException
import com.security.passwordmanager.domain.model.EncryptionException
import com.security.passwordmanager.domain.model.InformationNotFoundException
import com.security.passwordmanager.domain.model.InternetConnectionLostException
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.domain.model.SettingsNotFoundException
import com.security.passwordmanager.domain.model.UpdateSettingsException
import com.security.passwordmanager.domain.model.UserNotAuthenticatedException
import com.security.passwordmanager.domain.model.UsernameNotFoundException
import com.security.passwordmanager.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SettingsRepositoryImpl(
    override val auth: FirebaseAuth,
    override val database: FirebaseDatabase,
    cryptoManager: CryptoManager
) : SettingsRepository, FirebaseRepository, EncryptionRepository(cryptoManager) {
    private companion object {
        const val SETTINGS_REFERENCE = "Settings"
        const val VERSION_INFO_REFERENCE = "appVersion"
    }

    override suspend fun addSettings(settings: Settings, resultAction: (Result<Unit>) -> Unit) {
        val userId = currentUser?.uid
            ?: return resultAction(Result.failure(UserNotAuthenticatedException))

        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        val encryptedSettings = SettingsDB(settings)
            .encrypt(EncryptionHelperImpl(userId, SETTINGS_REFERENCE))
            ?: return resultAction(Result.failure(EncryptionException))

        getDatabaseReference(userId)
            .child(SETTINGS_REFERENCE)
            .setValue(encryptedSettings)
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
            ?: return resultAction(Result.failure(UserNotAuthenticatedException))
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        val encryptionHelper = EncryptionHelperImpl(uid, SETTINGS_REFERENCE)
        val encryptedValue = encryptionHelper.encrypt(value = value.toString(), valueName = name)

        getDatabaseReference(uid)
            .child(SETTINGS_REFERENCE)
            .updateChildren(mapOf(name to encryptedValue))
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
                trySendBlocking(Result.failure(UserNotAuthenticatedException))
            }
            return@callbackFlow
        }


        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encryptedSettings = snapshot.getValue(SettingsDB::class.java)

                if (encryptedSettings == null) {
                    trySendBlocking(Result.failure(SettingsNotFoundException))
                    return
                }

                val settings = encryptedSettings
                    .decrypt(EncryptionHelperImpl(uid, SETTINGS_REFERENCE))
                    ?.mapToSettings()

                val result = settings
                    ?.let(Result.Companion::success)
                    ?: Result.failure(DecryptionException)

                trySendBlocking(result).onFailure {
                    Result.failure<Settings>(Exception(it).map())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.failure(error.toException().map()))
            }
        }

        getDatabaseReference(uid)
            .child(SETTINGS_REFERENCE)
            .addValueEventListener(listener)

        awaitClose {
            getDatabaseReference(uid).child(SETTINGS_REFERENCE).removeEventListener(listener)
        }
    }


    override suspend fun getSettings(resultAction: (Result<Settings>) -> Unit) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        val uid = currentUser?.uid
            ?: return resultAction(Result.failure(UserNotAuthenticatedException))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val encryptedSettings = snapshot.getValue(SettingsDB::class.java)
                    ?: return resultAction(Result.failure(SettingsNotFoundException))

                val settings = encryptedSettings
                    .decrypt(EncryptionHelperImpl(uid, SETTINGS_REFERENCE))
                    ?.mapToSettings()

                when (settings) {
                    null -> resultAction(Result.failure(DecryptionException))
                    else -> resultAction(Result.success(settings))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultAction(Result.failure(error.toException().map()))
            }
        }

        getDatabaseReference(uid)
            .child(SETTINGS_REFERENCE)
            .addListenerForSingleValueEvent(listener)
    }


    override suspend fun changeUsername(newUsername: String, resultAction: (Result<Unit>) -> Unit) {
        val currentUser = auth.currentUser
        when {
            currentUser == null -> {
                resultAction(Result.failure(UserNotAuthenticatedException))
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
        val user = currentUser ?: return Result.failure(UserNotAuthenticatedException)

        val username = user.displayName.takeIf { !it.isNullOrBlank() }
        val email = user.email.takeIf { !it.isNullOrBlank() }

        return (username ?: email)
            ?.let(Result.Companion::success)
            ?: Result.failure(UsernameNotFoundException)
    }



    override suspend fun getAppVersionInfo(resultAction: (Result<AppVersionInfo>) -> Unit) {
        if (!context.checkNetworkConnection()) {
            return resultAction(Result.failure(InternetConnectionLostException))
        }

        getDatabaseReference(VERSION_INFO_REFERENCE)
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
}