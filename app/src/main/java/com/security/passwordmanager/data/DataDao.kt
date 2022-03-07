package com.security.passwordmanager.data

import androidx.room.*
import com.security.passwordmanager.Pair

@Dao
abstract class DataDao {
    @Insert(entity = Website::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun addWebsite(website: Website)

    @Insert(entity = BankCard::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun addBankCard(bankCard: BankCard)

    @Update(entity = Website::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateWebsite(website: Website)

    @Update(entity = BankCard::class, onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateBankCard(bankCard: BankCard)

    @Query("SELECT * FROM WebsiteTable ORDER BY nameWebsite ASC")
    abstract fun getWebsiteList(): MutableList<Website>

    @Query("SELECT * FROM BankTable ORDER BY bankName ASC")
    abstract fun getBankCardList(): MutableList<BankCard>

    @Query("SELECT * FROM WebsiteTable WHERE address = :address")
    abstract fun getAccountList(address: String): MutableList<Website>

    @Query("SELECT * FROM BankTable WHERE bankName = :bankName")
    abstract fun getBankAccountList(bankName: String): MutableList<BankCard>

    @Query("SELECT * FROM WebsiteTable " +
            "WHERE nameWebsite LIKE '%' || :query || '%' OR " +
            "address LIKE '%' || :query || '%' OR " +
            "nameAccount LIKE '%' || :query || '%' " +
            "ORDER BY nameWebsite ASC")
    abstract fun searchWebsite(query: String): MutableList<Website>

    @Query("SELECT * FROM BankTable " +
            "WHERE bankName LIKE '%' || :query || '%' " +
            "ORDER BY bankName")
    abstract fun searchBankCard(query: String): MutableList<BankCard>

    @Delete(entity = Website::class)
    abstract fun deleteWebsite(website: Website)

    @Delete(entity = BankCard::class)
    abstract fun deleteBankCard(bankCard: BankCard)

    @Query("DELETE FROM WebsiteTable WHERE address = :url")
    abstract fun deleteWebsite(url: String)

    @Query("DELETE FROM BankTable WHERE bankName = :bankName")
    abstract fun deleteBankCard(bankName: String)

    @Transaction
    open fun search(query: String) = Pair(searchWebsite(query), searchBankCard(query))
}