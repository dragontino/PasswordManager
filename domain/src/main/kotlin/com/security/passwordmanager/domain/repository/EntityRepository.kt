package com.security.passwordmanager.domain.repository

import com.security.passwordmanager.domain.model.EncryptionHelper
import com.security.passwordmanager.domain.model.IconSite
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.util.Encrypt
import kotlinx.coroutines.flow.Flow

interface EntityRepository {

    /**
     * Функция, которая добавляет новый объект [DatabaseEntity] в соответствующую таблицу
     * @param entity объект для добавления в таблицу
     * @param newId callback, отправляющий id новой записи в таблице
     */
    suspend fun insertEntity(entity: DatabaseEntity, newId: (Result<String>) -> Unit)


    /**
     * Функция, которая обновляет поля объекта, реализующего интерфейс [DatabaseEntity],
     * тип которого [entityType]. Новые значения полей передаются в [updatesMap]
     * @param id идентификатор записи в таблице, данные которой нужно обновить
     * @param entityType тип данных, в зависимости от которого определяется, к какой таблице обращаться
     * @param updatesMap словарь, ключами которого являются имена полей класса, а значениями – новые значения этих полей
     * @param encryptAction лямбда функция, шифрующая каждое значение, переданное в [updatesMap]
     * @param resultAction результат выполнения
     */
    suspend fun <V : Any> updateEntity(
        id: String,
        entityType: EntityType,
        updatesMap: Map<String, V?>,
        encryptAction: (value: V, encrypt: EncryptionHelper) -> V?,
        resultAction: (Result<Unit>) -> Unit
    )


    /**
     * Функция, которая возвращает список всех данных типов [entityTypes] в отсортированном виде.
     * В качестве ключа сортировки используется [DatabaseEntity] класс.
     *
     * В отличие от [getEntities], эта функция автоматически возвращает новые данные,
     * если они поменялись в БД (без необходимости повторного вызова функции).
     * @param entityTypes типы данных, которые нужно извлечь
     * @return результат извлечения данных
     */
    fun fetchEntities(
        vararg entityTypes: EntityType,
        failure: (Exception) -> Unit
    ): Flow<Map<String, DatabaseEntity>>


    /**
     * Возвращает список данных типов [entityTypes], подходящих под запрос [query].
     * Если в поисковый запрос [query] передана пустая строка, функция вернет список всех данных
     * Обратите внимание, что эта функция, в отличие от [fetchEntities] возвращает единоразовый снимок базы.
     * То есть при обновлении данных, функцию придется перевызывать
     * @param entityTypes типы данных, которые нужно извлечь
     * @param query запрос, по которому идёт поиск подходящих элементов.
     * Если передано значение null, возвращаются все записи.
     *
     * Для определения подходит элемент под критерии поиска или нет используется функция contains интерфейса UserData
     * @param success вызывается в случае успешного извлечения данных одного типа
     * @param failure вызывается, если не удалось извлечь какой-либо тип данных
     */
    suspend fun getEntities(
        query: String?,
        vararg entityTypes: EntityType,
        success: (Map<String, DatabaseEntity>) -> Unit,
        failure: (Exception) -> Unit
    )


    /**
     * Функция, которая проверяет, есть ли [entity] в базе данных
     * @param entity элемент, который нужно найти
     * @return id записи или null, если такой записи нет в бд
     */
    suspend fun findRecordInDB(entity: DatabaseEntity): Result<String?>


    /**
     * Возвращает объект [DatabaseEntity] из таблицы [entityType] с id [id]
     * @param id id элемента в таблице
     * @param entityType тип данных, которые нужно извлечь
     * @param resultAction лямбда функция, в которую передается результат
     */
    suspend fun getEntityRecordById(
        id: String,
        entityType: EntityType,
        resultAction: (Result<DatabaseEntity>) -> Unit
    )


    /** Удаление данных из таблицы. В зависимости от [entityType] удаляются данные из разных таблиц
     * @param id id элемента, который нужно удалить
     * @param entityType тип объекта, который нужно удалить
     * @param resultAction результат удаления
     */
    suspend fun deleteEntity(
        id: String,
        entityType: EntityType,
        resultAction: (Result<Unit>) -> Unit
    )


    suspend fun getWebsiteBody(url: String): Result<String>

    suspend fun getWebsiteIcons(url: String): Result<IconSite>
}