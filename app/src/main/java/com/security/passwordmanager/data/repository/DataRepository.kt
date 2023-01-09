package com.security.passwordmanager.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.data.room.DataDao
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.slice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.min

class DataRepository(private val dataDao: DataDao) {

    suspend fun addData(data: Data) = when (data) {
        is Website -> dataDao.addWebsite(data)
        is BankCard -> dataDao.addBankCard(data)
    }


    suspend fun updateData(data: Data) = when (data) {
        is Website -> dataDao.updateWebsite(data)
        is BankCard -> dataDao.updateBankCard(data)
    }


    fun getDataUIList(email: String, dataType: DataType? = null): LiveData<List<DataUI>> =
        when (dataType) {
            DataType.Website -> dataDao.getWebsiteList(email).asLiveData(Dispatchers.IO)
            DataType.BankCard -> dataDao.getBankCardList(email).asLiveData(Dispatchers.IO)
            DataType.All, null -> dataDao
                .getWebsiteList(email)
                .asLiveData(Dispatchers.IO)
                .merge(dataDao.getBankCardList(email).asLiveData(Dispatchers.IO))
                { websites, bankCards -> sortedConcatenation(websites, bankCards) }
        }.toDataUIList()


    suspend fun getAccountList(
        email: String,
        key: String,
        dataType: DataType,
    ): MutableList<out Data> {
        println(dataDao.getCountRows())
        return when (dataType) {
            DataType.Website, DataType.All -> dataDao.getWebsiteAccountList(email, key)
            DataType.BankCard -> dataDao.getBankAccountList(email, key)
        }
    }


    fun searchData(email: String, query: String?, type: DataType? = null) : LiveData<List<DataUI>> {
        if (query == null || query.isBlank())
            return getDataUIList(email, type)

        return when (type) {
            DataType.Website -> dataDao.searchWebsite(email, query).asLiveData(Dispatchers.IO)
            DataType.BankCard -> dataDao.searchBankCard(email, query).asLiveData(Dispatchers.IO)
            DataType.All, null -> dataDao.run {
                searchWebsite(email, query)
                .asLiveData(Dispatchers.IO)
                .merge(searchBankCard(email, query).asLiveData(Dispatchers.IO))
                { websites, bankCards -> sortedConcatenation(websites, bankCards) }
            }

        }.toDataUIList()
    }

    //удаляет только 1 запись в бд
    suspend fun deleteData(data: Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data)
        else -> dataDao.deleteBankCard(data as BankCard)
    }

    //удаляет несколько записей в бд
    suspend fun deleteRecords(data: Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data.email, data.key)
        else -> dataDao.deleteBankCard(data.email, data.key)
    }




    private fun sortedConcatenation(
        websiteList: MutableList<Website>?,
        bankCardList: MutableList<BankCard>?
    ): List<Data> {
        when {
            bankCardList == null || bankCardList.isEmpty() -> return websiteList ?: emptyList()
            websiteList == null || websiteList.isEmpty() -> return bankCardList
        }

        websiteList ?: return emptyList()
        bankCardList ?: return emptyList()

        val resultList = ArrayList<Data>()
        val minLength = min(websiteList.size, bankCardList.size)
        var w = 0
        var b = 0

        while (w < minLength && b < minLength) {
            val website = websiteList[w]
            val bankCard = bankCardList[b]
            if (website > bankCard) {
                resultList.add(website)
                w++
            }
            else {
                resultList.add(bankCard)
                b++
            }
        }

        if (w > b)
            resultList.addAll(bankCardList.slice(fromIndex = b))
        else if (w < b)
            resultList.addAll(websiteList.slice(fromIndex = w))

        return resultList
    }


    private fun <T, K, R> LiveData<T>.merge(liveData: LiveData<K>, block: (T?, K?) -> R): LiveData<R> {
        val result = MediatorLiveData<R>()
        result.addSource(this) {
            result.value = block(this.value, liveData.value)
        }

        result.addSource(liveData) {
            result.value = block(this.value, liveData.value)
        }

        return result
    }


    private fun LiveData<out List<Data>>.toDataUIList() = map { dataList ->
        dataList
            .groupBy { it.key }
            .map {
                DataUI(
                    title = it.value[0],
                    accountList = it.value.toMutableList()
                )
            }
    }
}