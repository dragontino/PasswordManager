package com.security.passwordmanager.domain.model.entity

import com.google.firebase.database.Exclude
import com.security.passwordmanager.domain.model.EncryptionHelper
import com.security.passwordmanager.domain.model.UserData

sealed interface DatabaseEntity : UserData, Comparable<DatabaseEntity> {
    @get:Exclude val type: EntityType
    @get:Exclude val primaryKey: String
    @get:Exclude val valueToCompare: String

    override fun compareTo(other: DatabaseEntity): Int {
        return this.valueToCompare.compareTo(other.valueToCompare)
    }

    fun compareByPrimaryKey(primaryValue: String): Int
    override fun encrypt(encryption: EncryptionHelper): DatabaseEntity?
    override fun decrypt(decryption: EncryptionHelper): DatabaseEntity?
}