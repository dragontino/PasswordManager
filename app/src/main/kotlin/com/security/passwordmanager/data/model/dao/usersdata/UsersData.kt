package com.security.passwordmanager.data.model.dao.usersdata

import com.security.passwordmanager.data.UID
import com.security.passwordmanager.data.model.dao.FirebaseDao
import com.security.passwordmanager.presentation.model.data.ComposableData
import com.security.passwordmanager.presentation.model.enums.DataType

sealed interface UsersData : FirebaseDao, Comparable<UsersData> {
    val type: DataType
    val stringToCompare: String
    val keyName: String
    val keyValue: String


    fun convertToComposable(): ComposableData

    override fun convertToComposable(uid: UID) = convertToComposable()

    override fun compareTo(other: UsersData): Int {
        return this.stringToCompare.compareTo(other.stringToCompare)
    }
}