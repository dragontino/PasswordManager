package com.security.passwordmanager.domain.usecase

import android.util.Log
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.repository.EntityRepository
import com.security.passwordmanager.domain.util.Encrypt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class EntityUseCase(
    private val repository: EntityRepository,
    private val dispatcher: CoroutineDispatcher
) {
    private companion object {
        const val TAG = "EntityUseCase"
    }



    suspend fun insertEntity(
        entity: DatabaseEntity,
        newId: (Result<String>) -> Unit
    ) = withContext(dispatcher) {
        repository.insertEntity(entity) { result ->
            result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            newId(result)
        }
    }


    suspend fun updateEntity(
        id: String,
        type: EntityType,
        updatesMap: Map<String, Any?>,
        encryptValue: (value: Any, Encrypt) -> Any,
        resultAction: (Result<Unit>) -> Unit
    ) = withContext(dispatcher) {
        repository.updateEntity(id,  type, updatesMap, encryptValue) { result ->
            result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            resultAction(result)
        }
    }


    suspend fun getEntityRecordById(
        id: String,
        type: EntityType,
        error: (Throwable) -> Unit,
        success: (DatabaseEntity) -> Unit
    ) = withContext(dispatcher) {
        repository.getEntityRecordById(id, type) { result ->
            result.exceptionOrNull()?.let {
                Log.e(TAG, it.localizedMessage, it)
                error(it)
            }
            result.getOrNull()?.let(success)
        }
    }


    suspend fun checkIfEntityRecordIsNew(entity: DatabaseEntity): String? =
        withContext(dispatcher) {
            val result = repository.findRecordInDB(entity)
            result.exceptionOrNull()?.let { Log.e(TAG, it.localizedMessage, it) }
            return@withContext result.getOrNull()
        }


    fun fetchEntities(
        vararg entityTypes: EntityType,
        error: (Exception) -> Unit = {}
    ): Flow<Map<String, DatabaseEntity>> {
        return repository
            .fetchEntities(entityTypes = entityTypes, failure = error)
            .flowOn(dispatcher)
    }


    suspend fun getEntities(
        vararg entityTypes: EntityType,
        query: String? = null,
        error: (Exception) -> Unit = {},
        success: (Map<String, DatabaseEntity>) -> Unit
    ) = withContext(dispatcher) {
        repository.getEntities(
            query = query,
            entityTypes = entityTypes,
            success = success,
            failure = error
        )
    }


    suspend fun deleteEntity(
        id: String,
        type: EntityType,
        error: (Exception) -> Unit,
        success: () -> Unit
    ) =
        withContext(dispatcher) {
            repository.deleteEntity(id, type) { result ->
                result.exceptionOrNull()?.let {
                    Log.e(TAG, it.localizedMessage, it)
                    error(it as Exception)
                }
                result.getOrNull()?.let { success() }
            }
        }
}