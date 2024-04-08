package com.security.passwordmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.security.passwordmanager.data.crypto.CryptoManager
import com.security.passwordmanager.data.retrofit.RetrofitService
import com.security.passwordmanager.data.util.checkNetworkConnection
import com.security.passwordmanager.domain.model.DecryptionException
import com.security.passwordmanager.domain.model.EncryptionException
import com.security.passwordmanager.domain.model.EncryptionHelper
import com.security.passwordmanager.domain.model.EntityDeletionException
import com.security.passwordmanager.domain.model.EntityInsertionException
import com.security.passwordmanager.domain.model.IconSite
import com.security.passwordmanager.domain.model.InternetConnectionLostException
import com.security.passwordmanager.domain.model.LoadEntitiesException
import com.security.passwordmanager.domain.model.LoadEntityException
import com.security.passwordmanager.domain.model.LoadIconException
import com.security.passwordmanager.domain.model.LoadWebsiteNameException
import com.security.passwordmanager.domain.model.ServerRequestException
import com.security.passwordmanager.domain.model.UserNotAuthenticatedException
import com.security.passwordmanager.domain.model.entity.Bank
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.model.entity.Website
import com.security.passwordmanager.domain.repository.EntityRepository
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

class EntityRepositoryImpl(
    override val auth: FirebaseAuth,
    override val database: FirebaseDatabase,
    cryptoManager: CryptoManager,
    private val retrofitService: RetrofitService
) : EncryptionRepository(cryptoManager), EntityRepository, FirebaseRepository {


    override suspend fun insertEntity(entity: DatabaseEntity, newId: (Result<String>) -> Unit) {
        val userId = currentUser?.uid
            ?: return newId(Result.failure(UserNotAuthenticatedException))

        val reference = getDatabaseReference(userId).child(
            when (entity) {
                is Bank -> Reference.Banks.name
                is Website -> Reference.Websites.name
            }
        )

        val key = reference.push().key ?: UUID.randomUUID().toString()
        val encryptedEntity = entity.encrypt(EncryptionHelperImpl(reference.key!!, key))
            ?: return newId(Result.failure(EncryptionException))

        reference
            .child(key)
            .setValue(encryptedEntity)
            .addOnSuccessListener {
                newId(Result.success(key))
            }
            .addOnFailureListener {
                newId(Result.failure(it.mapOrNull() ?: EntityInsertionException(entity.type)))
            }
    }


    override suspend fun <V : Any> updateEntity(
        id: String,
        entityType: EntityType,
        updatesMap: Map<String, V?>,
        encryptAction: (value: V, encrypt: EncryptionHelper) -> V?,
        resultAction: (Result<Unit>) -> Unit
    ) {
        val userId = currentUser?.uid
            ?: return resultAction(Result.failure(UserNotAuthenticatedException))

        if (updatesMap.isEmpty()) {
            return resultAction(Result.success(Unit))
        }

        val ref = when (entityType) {
            EntityType.Bank -> Reference.Banks.name
            else -> Reference.Websites.name
        }

        val update = updatesMap
            .mapValues { entry ->
                entry.value?.let { value ->
                    val encryptionHelper = EncryptionHelperImpl(userId, ref, id, entry.key)
                    encryptAction(value, encryptionHelper)
                        ?: return resultAction(Result.failure(EncryptionException))
                }
            }
            .mapKeys { "$ref/$id/${it.key}" }

        getDatabaseReference(userId)
            .updateChildren(update)
            .addOnCompleteListener {
                resultAction(Result.success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.failure(it.map()))
            }
    }


    override fun fetchEntities(
        vararg entityTypes: EntityType,
        failure: (Exception) -> Unit
    ): Flow<Map<String, DatabaseEntity>> {
        
        val uid = currentUser?.uid

        if (uid == null) {
            failure(UserNotLoggedException)
            return emptyFlow()
        }

        val tables = when (EntityType.All) {
            in entityTypes -> Reference.valuesMap
            else -> entityTypes.associateWith { Reference.valuesMap[it]!! }
        }

        val flowList = mutableListOf<Flow<Map<String, DatabaseEntity>>>()
        val exceptionList = mutableListOf<EntityType>()

        tables.forEach { (type, ref) ->
            val flow = fetchEntity(uid, ref).mapNotNull { result ->
                result.exceptionOrNull()?.let { exceptionList += type }
                result.getOrNull()
            }
            flowList += flow
        }

        val result = flowList
            .combine { map1, map2 -> map1 + map2 }
            .map { map -> map.toList().sortedBy { it.second }.toMap() }

        if (exceptionList.isNotEmpty()) {
            failure(LoadEntitiesException(exceptionList))
        }
        return result
    }


    override suspend fun getEntities(
        query: String?,
        vararg entityTypes: EntityType,
        success: (Map<String, DatabaseEntity>) -> Unit,
        failure: (Exception) -> Unit
    ) {
        if (query != null && query.isBlank()) {
            return success(emptyMap())
        }

        val uid = currentUser?.uid
            ?: return failure(UserNotAuthenticatedException)

        val tables = when (EntityType.All) {
            in entityTypes -> Reference.valuesMap
            else -> entityTypes.associateWith { Reference.valuesMap[it]!! }
        }

        getEntities(uid, tables.values, query) { result ->
            result.getOrNull()?.let { map ->
                val sortedResults = map.toList().sortedBy { it.second }.toMap()
                success(sortedResults)
            }
            result.exceptionOrNull()?.let { failure(it as Exception) }
        }
    }


    override suspend fun findRecordInDB(
        entity: DatabaseEntity
    ): Result<String?> = suspendCoroutine { continuation ->
        val userId = currentUser?.uid
            ?: return@suspendCoroutine continuation.resume(Result.failure(UserNotAuthenticatedException))

        val tableName = when (entity) {
            is Website -> Reference.Websites.name
            is Bank -> Reference.Banks.name
        }
        val encryptionHelper = EncryptionHelperImpl(userId, tableName)

        fun DataSnapshot.checkSnapshot(): Boolean {
            val encryptedValue = child(entity.primaryKey).value?.toString() ?: return false
            val decryptedValue = encryptionHelper.decrypt(
                value = encryptedValue,
                valueName = key!!
            )
            return decryptedValue?.let(entity::compareByPrimaryKey) == 0
        }

        getDatabaseReference(userId)
            .child(tableName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataId = snapshot.children
                        .find(DataSnapshot::checkSnapshot)
                        ?.key

                    continuation.resume(Result.success(dataId))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(Result.failure(error.toException().map()))
                }
            })
    }


    override suspend fun getEntityRecordById(
        id: String,
        entityType: EntityType,
        resultAction: (Result<DatabaseEntity>) -> Unit
    ) {
        val uid = currentUser?.uid ?: return resultAction(Result.failure(UserNotAuthenticatedException))
        val ref = Reference.valuesMap[entityType]
            ?: return resultAction(Result.failure(LoadEntitiesException(listOf(entityType))))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val encryptedEntity = snapshot.getValue(ref.kotlinClass.java) ?: return resultAction(
                    Result.failure(LoadEntitiesException(listOf(entityType)))
                )

                val encryptionHelper = EncryptionHelperImpl(uid, ref.name, id)
                when (val entity = encryptedEntity.decrypt(encryptionHelper)) {
                    null -> resultAction(Result.failure(DecryptionException))
                    else -> resultAction(Result.success(entity))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultAction(Result.failure(error.toException().map()))
            }
        }

        getDatabaseReference(uid)
            .child(ref.name)
            .child(id)
            .addListenerForSingleValueEvent(listener)
    }


    override suspend fun deleteEntity(
        id: String,
        entityType: EntityType,
        resultAction: (Result<Unit>) -> Unit
    ) {
        val uid = currentUser?.uid ?: return resultAction(Result.failure(UserNotAuthenticatedException))
        val tableName = Reference.valuesMap[entityType]?.name
            ?: return resultAction(Result.failure(EntityDeletionException(entityType)))

        getDatabaseReference(uid)
            .child(tableName)
            .child(id)
            .removeValue { error, _ ->
                when (error) {
                    null -> resultAction(Result.success(Unit))
                    else -> resultAction(Result.failure(error.toException().map()))
                }
            }
    }


    override suspend fun getWebsiteBody(url: String): Result<String> {
        if (!context.checkNetworkConnection()) {
            return Result.failure(InternetConnectionLostException)
        }

        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(url.parseToUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            val response = retrofit
                .create(RetrofitService::class.java)
                .getHtml(url.parseToUrl())
                .execute()

            return when {
                response.isSuccessful -> {
                    response.body()
                        ?.let { Result.success(it) }
                        ?: Result.failure(Exception())
                }

                else -> Result.failure(
                    ServerRequestException(
                        code = response.code(),
                        message = response.message(),
                    ),
                )
            }
        } catch (e: Exception) {
            Result.failure(
                e.mapOrNull() ?: LoadWebsiteNameException(
                    url = url,
                    code = 404,
                    message = e.localizedMessage
                )
            )
        }
    }


    override suspend fun getWebsiteIcons(url: String): Result<IconSite> {
        if (!context.checkNetworkConnection()) {
            return Result.failure(InternetConnectionLostException)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(url.parseToUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        return try {
            val response = retrofit
                .create(RetrofitService::class.java)
                .getWebsiteIcons(url)
                .execute()

            val iconSite = response.body()

            when {
                iconSite == null || !response.isSuccessful -> Result.failure(
                    LoadIconException(url = url, code = response.code())
                )
                else -> Result.success(iconSite)
            }
        } catch (e: Exception) {
            Result.failure(
                e.mapOrNull() ?: LoadIconException(url = url, code = 404)
            )
        }
    }


    /* ----- PRIVATE FUNCTIONS ----- */
    private fun fetchEntity(
        uid: String,
        reference: Reference
    ): Flow<Result<Map<String, DatabaseEntity>>> = callbackFlow {
        val path = getDatabaseReference(uid).child(reference.name)

        path.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val resultList = snapshot.convertToEntityMap(uid, reference)
                    trySendBlocking(Result.success(resultList))
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException()
                        .map()
                        .also { it.printStackTrace() }
                        .let {
                            trySendBlocking(Result.failure(it))
                        }
                }
            },
        ).also {
            awaitClose {
                path.removeEventListener(it)
            }
        }
    }


    private fun getEntities(
        uid: String,
        references: Iterable<Reference>,
        query: String?,
        resultAction: (Result<Map<String, DatabaseEntity>>) -> Unit
    ) {
        getDatabaseReference(uid).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entitiesMap = buildMap {
                        references.forEach { ref ->
                            snapshot.child(ref.name)
                                .decryptEntity(uid, ref)
                                ?.let(::putAll)
                        }
                    }
                    when (query) {
                        null -> resultAction(Result.success(entitiesMap))
                        else -> resultAction(Result.success(entitiesMap.filterValues { query in it }))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    resultAction(Result.failure(error.toException().map()))
                }
            },
        )
    }


    private fun DataSnapshot.decryptEntity(
        uid: String,
        reference: Reference
    ): Map<String, DatabaseEntity>? = children
        .filter { it.key != null && it.value != null }
        .associate { it.key!! to it.getValue(reference.kotlinClass.java)!! }
        .mapValues { entry ->
            val encryptionHelper = EncryptionHelperImpl(uid, this.key!!, entry.key)
            entry.value.decrypt(encryptionHelper) ?: return null
        }


    private fun <K : Any, V: Comparable<V>> Iterable<Map<K, V>>.combine(): Map<K, V> {
        return fold<Map<K, V>, MutableMap<K, V>>(mutableMapOf()) { acc, map ->
            acc.putAll(map)
            acc
        }.toList().sortedBy { it.second }.toMap()
    }


    private fun String.parseToUrl(): HttpUrl = when {
        startsWith("http://") || startsWith("https://") -> HttpUrl.get(this)
        else -> HttpUrl.get("https://$this")
    }


    private sealed class Reference(
        val name: String,
        val kotlinClass: KClass<out DatabaseEntity>
    ) {
        companion object {
            val valuesMap: Map<EntityType, Reference> = mapOf(
                EntityType.Website to Websites,
                EntityType.Bank to Banks
            )
        }

        data object Websites : Reference(
            name = "Websites",
            kotlinClass = Website::class
        )

        data object Banks : Reference(
            name = "Banks",
            kotlinClass = Bank::class
        )
    }
}