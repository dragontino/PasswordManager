package com.security.passwordmanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class DataDao {

    @Insert(entity = Website.class, onConflict = OnConflictStrategy.REPLACE)
    public abstract void addWebsite(Website website);

    @Insert(entity = BankCard.class, onConflict = OnConflictStrategy.REPLACE)
    public abstract void addBankCard(BankCard bankCard);

    @Update(entity = Website.class, onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateWebsite(Website website);

    @Update(entity = BankCard.class, onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateBankCard(BankCard bankCard);

    @Query("SELECT * FROM WebsiteTable ORDER BY nameWebsite ASC")
    public abstract List<Website> getWebsiteList();

    @Query("SELECT * FROM BankTable ORDER BY bankName ASC")
    public abstract List<BankCard> getBankCardList();

    @Query("SELECT * FROM WebsiteTable WHERE address = :address")
    public abstract List<Website> getAccountList(String address);

    @Query("SELECT * FROM BankTable WHERE bankName = :bankName")
    public abstract List<BankCard> getBankAccountList(String bankName);

    @Query("SELECT * FROM WebsiteTable " +
            "WHERE nameWebsite LIKE '%' || :query || '%' OR " +
            "address LIKE '%' || :query || '%' OR " +
            "nameAccount LIKE '%' || :query || '%' " +
            "ORDER BY nameWebsite ASC")
    public abstract List<Website> searchWebsite(String query);

    @Query("SELECT * FROM BankTable " +
            "WHERE bankName LIKE '%' || :query || '%' " +
            "ORDER BY bankName")
    public abstract List<BankCard> searchBankCard(String query);

    @Delete(entity = Website.class)
    public abstract void deleteWebsite(Website website);

    @Delete(entity = BankCard.class)
    public abstract void deleteBankCard(BankCard bankCard);

    @Query("DELETE FROM WebsiteTable WHERE address = :url")
    public abstract void deleteWebsite(String url);

    @Query("DELETE FROM BankTable WHERE bankName = :bankName")
    public abstract void deleteBankCard(String bankName);

    @Transaction
    public void search(String query) {
        searchWebsite(query);
        searchBankCard(query);
    }
}
