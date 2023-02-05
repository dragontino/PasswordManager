package com.security.passwordmanager.data.room

import androidx.room.*
import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Website
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DataDao {

    /**
     * Добавление в базу
     */
    @Insert(entity = Website::class, onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addWebsite(website: Website)

    @Insert(entity = BankCard::class, onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addBankCard(bankCard: BankCard)


    /**
     * Обновление элементов
     */
    @Update(entity = Website::class, onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateWebsite(website: Website)

    @Update(entity = BankCard::class, onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateBankCard(bankCard: BankCard)


    /**
     * Получение всех элементов
     */
    @Query("SELECT * FROM WebsiteTable WHERE email = :email ORDER BY nameWebsite ASC")
    abstract fun getWebsiteList(email: String): Flow<List<Website>>

    @Query("SELECT * FROM BankTable WHERE email = :email ORDER BY bankName ASC")
    abstract fun getBankCardList(email: String): Flow<List<BankCard>>


    /**
     * Получение списка аккаунтов по заданному ключу
     */
    @Query("SELECT * FROM WebsiteTable WHERE email = :email AND address = :address ORDER BY nameWebsite ASC")
    abstract suspend fun getWebsiteAccountList(email: String, address: String): MutableList<Website>

    @Query("SELECT * FROM BankTable WHERE email = :email AND bankName = :bankName ORDER BY bankName ASC")
    abstract suspend fun getBankAccountList(email: String, bankName: String): MutableList<BankCard>


    /**
     * Поиск
     */
    @Query(
        """
        SELECT * FROM WebsiteTable 
        WHERE email = :email
        AND (
            address LIKE '%' || :query || '%' 
            OR nameWebsite LIKE '%' || :query || '%' 
            OR nameAccount LIKE '%' || :query || '%'
        )
        ORDER BY nameWebsite ASC
        """
    )
    abstract suspend fun searchWebsite(email: String, query: String): List<Website>

    @Query(
        """
        SELECT * FROM BankTable
        WHERE email = :email
        AND (
            bankName LIKE '%' || :query || '%' 
            OR bankCardName LIKE '%' || :query || '%'
        )
        ORDER BY bankName
        """
    )
    abstract suspend fun searchBankCard(email: String, query: String): List<BankCard>


    @Transaction
    open suspend fun searchData(email: String, query: String): Array<List<Data>> {
        return arrayOf(
            searchWebsite(email, query),
            searchBankCard(email, query)
        )
    }


    /**
     * Удаление одного элемента
     * @param website – объект, который будет удалён
     */
    @Delete(entity = Website::class)
    abstract suspend fun deleteWebsite(website: Website)

    @Delete(entity = BankCard::class)
    abstract suspend fun deleteBankCard(bankCard: BankCard)


    /**
     * удаление нескольких элементов по ключу
     */
    @Query("DELETE FROM WebsiteTable WHERE email = :email AND address = :url")
    abstract suspend fun deleteWebsite(email: String, url: String)

    @Query("DELETE FROM BankTable WHERE email = :email AND bankName = :bankName")
    abstract suspend fun deleteBankCard(email: String, bankName: String)


    @Query("SELECT COUNT(*) FROM WebsiteTable, BankTable")
    abstract suspend fun getCountRows(): Int
}