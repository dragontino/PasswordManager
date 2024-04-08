package com.security.passwordmanager.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.security.passwordmanager.data.crypto.CryptoManager
import com.security.passwordmanager.domain.repository.KmsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.asDeferred

class KmsRepositoryImpl(
    override val auth: FirebaseAuth,
    override val database: FirebaseDatabase,
    private val cryptoManager: CryptoManager,
) : KmsRepository, FirebaseRepository {
    private companion object {
        const val CLOUD_KEY_REF = "cloudKey"
        const val TAG = "KmsRepository"
    }


    private suspend fun getApiKey(): Result<String> {
        val snapshotAsync = getDatabaseReference(CLOUD_KEY_REF).get().asDeferred()
        return try {
            when (val value = snapshotAsync.await().value) {
                null -> Result.failure(Exception("There are no api keys in the database!"))
                else -> Result.success(Gson().toJson(value))
            }
        } catch (e: CancellationException) {
            Result.failure(e)
        }
    }


    override suspend fun initializeKmsClient(): Result<Unit> {
        if (cryptoManager.hasAnyClient()) return Result.success(Unit)

        val apiKeyResult = getApiKey()
        apiKeyResult.getOrNull()?.let(cryptoManager::addClientWithApiKey)
        apiKeyResult.exceptionOrNull()?.let { Log.e(TAG, it.message, it) }
        return apiKeyResult.map {}
    }
}
