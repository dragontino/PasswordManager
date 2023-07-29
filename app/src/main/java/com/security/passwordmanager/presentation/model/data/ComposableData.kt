package com.security.passwordmanager.presentation.model.data

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.security.passwordmanager.data.model.dao.FirebaseDao
import kotlin.reflect.KMutableProperty0

sealed interface ComposableData {
    fun convertToDao(): FirebaseDao

    /**
     * Определяет, есть ошибки или нет
     */
    val haveErrors: Boolean

    /**
     * Обновление значения ошибок
     * @param context контекст для использования строковых ресурсов
     */
    fun updateErrors(context: Context)

    /**
     * Список свойств, значения которых поменялись.
     * Ключом является название свойства (e. g. ::login.name),
     * значением - пара значений: изначальное значение и текущее
     */
    val updatedProperties: SnapshotStateMap<String, Pair<String, String>>


    /**
     * Функция, которая обновляет значение свойства [property] класса
     * и обновляет словарь [updatedProperties]
     *
     * Пример использования:
     * ```
     * account.updateValue(account::login, "new_login")
     * ```
     *
     * @param property свойство класса, которое нужно обновить
     * @param newValue новое значение свойства
     */
    fun <T : Any> updateValue(property: KMutableProperty0<T>, newValue: T) {
        val previousValue = property.get()
        property.set(newValue)

        with(updatedProperties) {
            val changeHistory = this[property.name]

            when {
                changeHistory == null -> this[property.name] =
                    previousValue.toString() to newValue.toString()

                changeHistory.first == newValue.toString() -> remove(property.name)

                else -> this[property.name] = changeHistory.copy(second = newValue.toString())
            }
        }
    }
}


operator fun <T> SnapshotStateMap<String, Pair<String, String>>.contains(property: KMutableProperty0<T>) =
    this.contains(property.name)