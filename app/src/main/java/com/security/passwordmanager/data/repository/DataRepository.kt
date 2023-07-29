package com.security.passwordmanager.data.repository

import android.accounts.NetworkErrorException
import android.security.keystore.UserNotAuthenticatedException
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.security.passwordmanager.R
import com.security.passwordmanager.checkNetworkConnection
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.usersdata.Bank
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.model.dao.usersdata.Website
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL
import java.util.UUID
import kotlin.reflect.KClass

class DataRepository(
    auth: FirebaseAuth,
    database: DatabaseReference,
    cryptoManager: CryptoManager
) : FirebaseEncryptedRepository(auth, database, cryptoManager) {

    private companion object {
        const val WebsiteReference = "Websites"
        const val BankReference = "Banks"

        val References = mapOf(
            DataType.Website to (WebsiteReference to Website::class),
            DataType.Bank to (BankReference to Bank::class)
        )
    }


    /**
     * Функция, которая добавляет новый объект [UsersData] в таблицу
     * @param data объект для добавления в таблицу
     * @param resultId callback, отправляющий id новой записи в таблице
     */
    suspend fun addData(data: UsersData, resultId: (Result<String>) -> Unit = {}) =
        withContext(Dispatchers.IO) {
            resultId(Result.Loading)

            val userId = currentUser?.uid

            if (userId == null) {
                resultId(Result.Error(Exception(context.getString(R.string.cannot_add_data))))
                return@withContext
            }


            val reference = getDatabaseReference(userId).child(
                when (data) {
                    is Bank -> BankReference
                    is Website -> WebsiteReference
                }
            )

            val key = reference.push().key ?: UUID.randomUUID().toString()

            data.encrypt { it.encrypt(userId) }

            reference
                .child(key)
                .setValue(data)
                .addOnSuccessListener {
                    resultId(Result.Success(key))
                }
                .addOnFailureListener {
                    resultId(Result.Error(it))
                }
        }


    /**
     * Функция, которая обновляет поля объекта, реализующего интерфейс [UsersData],
     * тип которого [dataType]. Новые значения полей передаются в [dataUpdates]
     * @param id id записи в таблице, данные которой нужно обновить
     * @param dataType тип данных, в зависимости от которого определяется, к какой таблице обращаться
     * @param dataUpdates словарь, ключами которого являются имена полей класса,
     * @param encryptValue лямбда функция, шифрующая каждое значение, переданное в [dataUpdates]
     * @param resultAction результат выполнения
     * а значениями - новые значения этих полей
     */
    suspend fun <T : Any?> updateData(
        id: String,
        dataType: DataType,
        dataUpdates: Map<String, T>,
        encryptValue: (value: T, encryption: (String) -> String) -> T,
        resultAction: (Result<Unit>) -> Unit = {}
    ) = withContext(Dispatchers.IO) {

        if (dataUpdates.isEmpty()) {
            resultAction(Result.Success(Unit))
            return@withContext
        }

        val userId = currentUser?.uid

        if (userId == null) {
            resultAction(
                Result.Error(
                    UserNotAuthenticatedException(context.getString(R.string.cannot_add_data))
                )
            )
            return@withContext
        }

        val ref = when (dataType) {
            DataType.Bank -> BankReference
            else -> WebsiteReference
        }

        val update = dataUpdates
            .mapKeys { "/$ref/$id/${it.key}" }
            .mapValues { entry ->
                entry.value?.let { value ->
                    encryptValue(value) { it.encrypt(userId) }
                }
            }

        getDatabaseReference(userId)
            .updateChildren(update)
            .addOnCompleteListener {
                resultAction(Result.Success(Unit))
            }
            .addOnFailureListener {
                resultAction(Result.Error(it))
            }
    }


    /**
     * Функция, которая возвращает список всех данных типа [dataType].
     * При изменении данных в базе эта функция, в отличие от [getDataList] автоматически возвращает обновленные данные
     * без необходимости перевызова функции
     * @param dataType тип данных, которые нужно извлечь
     * @return результат извлечения данных
     */
    fun fetchDataList(dataType: DataType = DataType.All) = References[dataType]
        ?.let { pair ->
            fetchDataMap(tableName = pair.first, kClass = pair.second).map { map ->
                map.toList().sortedBy { it.second }.toMap()
            }
        }
        ?: References
            .map { fetchDataMap(tableName = it.value.first, kClass = it.value.second) }
            .combine { map, map2 ->
                map + map2
            }
            .map { map ->
                map.toList().sortedBy { it.second }.toMap()
            }


    private fun <T : UsersData> fetchDataMap(
        tableName: String,
        kClass: KClass<T>,
    ) = callbackFlow {
        val uid = currentUser?.uid

        if (uid == null) {
            awaitClose {
                UserNotAuthenticatedException(context.getString(R.string.cannot_fetch_data)).printStackTrace()
            }
            return@callbackFlow
        }

        val path = getDatabaseReference(uid).child(tableName)


        path.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val resultList = snapshot
                        .children
                        .filter { it.key != null && it.value != null }
                        .associate { it.key!! to it.getValue(kClass.java)!! }
                        .onEach { entry ->
                            entry.value.decrypt { it.decrypt(uid) }
                        }

                    trySendBlocking(resultList)
                }

                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            },
        ).also {
            awaitClose {
                path.removeEventListener(it)
            }
        }
    }


    /**
     * Функция, которая проверяет, есть ли [data] в базе данных, и, если есть, возвращает её id
     * @param data элемент, который нужно найти
     * @param resultAction лямбда функция, куда передается результат выполнения
     */
    internal suspend fun findDataInDatabase(
        data: UsersData,
        resultAction: (resultId: Result<String>) -> Unit
    ) = withContext(Dispatchers.IO) {

        val userId = currentUser?.uid

        if (userId == null) {
            resultAction(
                Result.Error(
                    UserNotAuthenticatedException(context.getString(R.string.cannot_fetch_data))
                )
            )
            return@withContext
        }

        val dataTable = References[data.type]!!.first

        getDatabaseReference(userId)
            .child(dataTable)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataItems = snapshot.children

                    val dataId = dataItems.find {
                        val encryptedValue = it.child(data.keyName).value.toString()
                        return@find encryptedValue.decrypt(userId) == data.keyValue
                    }?.key

                    when (dataId) {
                        null -> resultAction(Result.Error(NullPointerException()))
                        else -> resultAction(Result.Success(dataId))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    resultAction(Result.Error(error.toException()))
                }
            })
    }


    /**
     * Возвращает список данных типа [dataType], подходящих под запрос [query].
     * Если в поисковый запрос [query] передана пустая строка, функция вернет список всех данных
     * Обратите внимание, что эта функция, в отличие от [fetchDataList] возвращает единоразовый снимок базы.
     * То есть при обновлении данных, функцию придется перевызывать
     * @param dataType тип данных, которые нужно извлечь
     * @param query запрос, по которому идёт поиск подходящих элементов.
     * Для определения подходит элемент под критерии поиска или нет используется функция [UsersData.contains]
     * @param resultAction лямбда функция, в которую передается результат извлечения данных
     */
    suspend fun getDataList(
        dataType: DataType = DataType.All,
        query: String? = null,
        resultAction: (Result<Map<String, UsersData>>) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (query != null && query.isBlank()) {
            resultAction(Result.Success(emptyMap()))
            return@withContext
        }

        val uid = currentUser?.uid

        if (uid == null) {
            resultAction(
                Result.Error(
                    UserNotAuthenticatedException(context.getString(R.string.cannot_fetch_data))
                )
            )
            return@withContext
        }

        getDatabaseReference(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataTables = References
                        .filter { it.key == dataType }
                        .ifEmpty { References }
                        .values

                    val resultList = dataTables
                        .map { pair ->
                            snapshot
                                .child(pair.first)
                                .children
                                .filter { it.key != null && it.value != null }
                                .associate { it.key!! to it.getValue(pair.second.java)!! }
                                .onEach { entry ->
                                    entry.value.decrypt { it.decrypt(uid) }
                                }
                        }
                        .reduce { acc, map ->
                            (acc + map).toSortedMap()
                        }
                        .let {
                            if (query != null) {
                                it.filterValues { data -> query in data }
                            } else it
                        }
                        .toList()
                        .sortedBy { it.second }
                        .toMap()

                    resultAction(Result.Success(resultList))
                }

                override fun onCancelled(error: DatabaseError) {
                    resultAction(Result.Error(error.toException()))
                }
            })
    }


    /**
     * Возвращает объект [UsersData] из таблицы [dataType] с id [id]
     * @param id id элемента в таблице
     * @param dataType тип данных, которые нужно извлечь
     * @param resultAction лямбда функция, в которую передается результат
     */
    suspend fun getData(
        id: String,
        dataType: DataType,
        resultAction: (Result<UsersData>) -> Unit
    ) = withContext(Dispatchers.IO) {
        val uid = currentUser?.uid

        if (uid == null || dataType == DataType.All) {
            resultAction(
                Result.Error(
                    UserNotAuthenticatedException(context.getString(R.string.cannot_fetch_data))
                )
            )
            return@withContext
        }

        val ref = References[dataType]!!

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot
                    .getValue(ref.second.java)
                    ?.apply {
                        decrypt { it.decrypt(uid) }
                    }

                when (data) {
                    null -> resultAction(
                        Result.Error(
                            NullPointerException(context.getString(R.string.cannot_fetch_data))
                        )
                    )
                    else -> resultAction(Result.Success(data))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultAction(Result.Error(error.toException()))
            }

        }

        getDatabaseReference(uid)
            .child(ref.first)
            .child(id)
            .addListenerForSingleValueEvent(listener)
    }



    /** Удаление данных из таблицы. В зависимости от [dataType] удаляются данные из разных таблиц
     * @param id id элемента, который нужно удалить
     * @param dataType тип объекта, который нужно удалить
     * @param resultAction результат удаления
     */
    suspend fun deleteData(
        id: String,
        dataType: DataType,
        resultAction: (Result<Unit>) -> Unit
    ) = withContext(Dispatchers.IO) {

        resultAction(Result.Loading)
        val uid = currentUser?.uid

        if (uid == null || dataType == DataType.All) {
            resultAction(
                Result.Error(NullPointerException(context.getString(R.string.cannot_delete_account)))
            )
            return@withContext
        }

        val dataTable = References[dataType]?.first!!

        getDatabaseReference(uid)
            .child(dataTable)
            .child(id)
            .removeValue { error, _ ->
                when (error) {
                    null -> resultAction(Result.Success(Unit))
                    else -> resultAction(Result.Error(error.toException()))
                }
            }
    }


    /**
     * Метод, который определяет название сайта по домену
     * @param address URL адрес сайта
     * @return название сайта
     */
    suspend fun getWebsiteDomainName(address: String): Result<String> =
        withContext(Dispatchers.IO) {
            if (!context.checkNetworkConnection()) {
                return@withContext Result.Error(
                    NetworkErrorException(
                        context.getString(R.string.check_internet_connection)
                    )
                )
            }


            val url = address.parseToUrl()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            return@withContext try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException(
                            context.getString(
                                R.string.server_request_error,
                                response.code,
                                response.message
                            )
                        )
                    }


                    val titleRegex = "<title>(.*?)</title>".toRegex(RegexOption.IGNORE_CASE)
                    val body =
                        response.body?.string() ?: return@withContext Result.Success(url.host)

                    titleRegex
                        .find(body)
                        ?.groupValues
                        ?.firstOrNull()
                        ?.removeSurrounding(
                            prefix = "<title>",
                            suffix = "</title>"
                        )
                        ?.let { Result.Success(it) }
                        ?: throw IOException(
                            context.getString(
                                R.string.cannot_find_website_title,
                                address,
                            ),
                        )
                }
            } catch (e: IOException) {
                Result.Error(e.getRightMessages())
            }
        }




    /**
     * Находит лого сайта, который расположен на заданном [address]
     * @param address адрес сайта
     * @return лого сайта
     */
    suspend fun getWebsiteLogo(address: String): Result<String> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://besticon-demo.herokuapp.com/allicons.json?url=$address")
            .build()


        client.newCall(request).execute().use { response ->
            return@withContext when {
                !response.isSuccessful -> {
                    val exception = IOException(
                        context.getString(R.string.load_icon_exception, address, response.code),
                    )
                    Result.Error(exception)
                }
                else -> try {
                    val iconUrl = parseIconUrl(response.body?.string() ?: "", request, address)
                    Result.Success(iconUrl)
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
        }
    }


    private inline fun <T : Any> List<Flow<T>>.combine(
        crossinline combination: (T, T) -> T
    ) = reduceOrNull { acc, flow ->
        acc.combine(flow) { f, s -> combination(f, s) }
    } ?: emptyFlow()



    private fun String.parseToUrl(): URL = when {
        startsWith("http://") || startsWith("https://") -> URL(this)
        else -> URL("https://$this")
    }



    /**
     * Извлекает из сайта url иконки
     * @param jsonString json, который нужно парсить
     * @param request запрос на сайт
     * @param address адрес сайта
     * @throws JsonSyntaxException
     * @throws NullPointerException
     * @return URL иконки
     */
    @kotlin.jvm.Throws(NullPointerException::class, JsonSyntaxException::class)
    private fun parseIconUrl(jsonString: String, request: Request, address: String): String {
        val site = Gson().fromJson(jsonString, Site::class.java)
        val iconUrl = site
            ?.icons
            ?.find { icon -> "favicon" in icon.url }
            ?.url
            ?: site?.icons?.find { "apple" in it.url }?.url
            ?: site?.icons?.firstOrNull()?.url
            ?: throw NullPointerException("Cannot find the icon in url $address")

        return when {
            iconUrl.toUri().isAbsolute -> iconUrl
            else -> "${request.url}$iconUrl"
        }
    }


    private data class Site(
        val url: String,
        val icons: List<Icon>
    )

    private data class Icon(
        val url: String,
        val width: Int,
        val height: Int,
        val format: String,
        val bytes: Int,
        val error: String?,
        val sha1sum: String
    )
}