package com.security.passwordmanager.data

import androidx.room.*
import com.security.passwordmanager.Pair
import com.security.passwordmanager.model.BankCard
import com.security.passwordmanager.model.Website

@Dao
abstract class DataDao {

    /**
     * Добавление в базу
     */
    @Insert(entity = Website::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun addWebsite(website: Website)

    @Insert(entity = BankCard::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun addBankCard(bankCard: BankCard)


    /**
     * Обновление элементов
     */
    @Update(entity = Website::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateWebsite(website: Website)

    @Update(entity = BankCard::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateBankCard(bankCard: BankCard)


    /**
     * Получение всех элементов
     */
    @Query("SELECT * FROM WebsiteTable WHERE email = :email ORDER BY nameWebsite ASC")
    abstract suspend fun getWebsiteList(email: String): MutableList<Website>

    @Query("SELECT * FROM BankTable WHERE email = :email ORDER BY bankName ASC")
    abstract suspend fun getBankCardList(email: String): MutableList<BankCard>


    /**
     * Поиск
     */
    @Query("SELECT * FROM WebsiteTable " +
            "WHERE email = :email AND (" +
            "nameWebsite LIKE '%' || :query || '%' OR " +
            "address LIKE '%' || :query || '%' OR " +
            "nameAccount LIKE '%' || :query || '%') " +
            "ORDER BY nameWebsite ASC")
    abstract suspend fun searchWebsite(email: String, query: String): MutableList<Website>

    @Query("SELECT * FROM BankTable " +
            "WHERE email = :email AND (" +
            "bankName LIKE '%' || :query || '%' OR " +
            "bankCardName LIKE '%' || :query || '%') " +
            "ORDER BY bankName")
    abstract suspend fun searchBankCard(email: String, query: String): MutableList<BankCard>


    /**
     * Удаление одного элемента
     */
    @Delete(entity = Website::class)
    abstract fun deleteWebsite(website: Website)

    @Delete(entity = BankCard::class)
    abstract fun deleteBankCard(bankCard: BankCard)


    /**
     * удаление нескольких элементов по ключу
     */
    @Query("DELETE FROM WebsiteTable WHERE email = :email AND address = :url")
    abstract fun deleteWebsite(email: String, url: String)

    @Query("DELETE FROM BankTable WHERE email = :email AND bankName = :bankName")
    abstract fun deleteBankCard(email: String, bankName: String)


    @Transaction
    open suspend fun search(email: String, query: String) =
        Pair(searchWebsite(email, query), searchBankCard(email, query))
}