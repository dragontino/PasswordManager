package com.security.passwordmanager.domain.model.entity

import com.security.passwordmanager.domain.model.UserData
import com.security.passwordmanager.domain.util.Encrypt

sealed interface DatabaseEntity : UserData, Comparable<DatabaseEntity> {
    val type: EntityType
    val stringToCompare: String
    val keyName: String
    val keyValue: String

    override fun compareTo(other: DatabaseEntity): Int {
        return this.stringToCompare.compareTo(other.stringToCompare)
    }

    override fun encrypt(encryption: Encrypt): DatabaseEntity

    override fun decrypt(decryption: Encrypt): DatabaseEntity
}