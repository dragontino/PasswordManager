package com.security.passwordmanager.data.repository

import com.security.passwordmanager.data.model.BankCard
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.model.Website
import com.security.passwordmanager.data.room.DataDao
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

class DataRepository(private val dataDao: DataDao) {

    suspend fun addData(data: Data) = when (data) {
        is Website -> dataDao.addWebsite(data)
        is BankCard -> dataDao.addBankCard(data)
    }


    suspend fun updateData(data: Data) = when (data) {
        is Website -> dataDao.updateWebsite(data)
        is BankCard -> dataDao.updateBankCard(data)
    }


    @ExperimentalCoroutinesApi
    fun getDataList(email: String, dataType: DataType = DataType.All): Flow<List<DataUI>> =
        when (dataType) {
            DataType.All ->
                dataDao
                    .getWebsiteList(email)
                    .combine(dataDao.getBankCardList(email)) { w, b -> w + b }
            DataType.Website -> dataDao.getWebsiteList(email)
            DataType.BankCard -> dataDao.getBankCardList(email)
        }.mapLatest {
            it.toDataUIList()
        }


    suspend fun getAccountList(
        email: String,
        key: String,
        dataType: DataType,
    ): MutableList<out Data> {
        println("countRows = ${dataDao.getCountRows()}")
        return when (dataType) {
            DataType.Website, DataType.All -> dataDao.getWebsiteAccountList(email, key)
            DataType.BankCard -> dataDao.getBankAccountList(email, key)
        }
    }


    /** Поиск **/
    suspend fun searchData(
        email: String,
        query: String,
        dataType: DataType = DataType.All
    ): List<DataUI> {
        if (query.isBlank()) return emptyList()

        return when (dataType) {
            DataType.All -> dataDao
                .searchData(email, query)
                .reduce { acc, dataList -> acc + dataList }
            DataType.Website -> dataDao.searchWebsite(email, query)
            DataType.BankCard -> dataDao.searchBankCard(email, query)
        }.toDataUIList()
    }


    /** Удаляет только 1 запись в бд **/
    suspend fun deleteData(data: Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data)
        else -> dataDao.deleteBankCard(data as BankCard)
    }

    /** Удаляет несколько записей в бд **/
    suspend fun deleteRecords(data: Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data.email, data.key)
        else -> dataDao.deleteBankCard(data.email, data.key)
    }


    /** Merging 2 sorted lists **/
    private operator fun List<Data>.plus(otherList: List<Data>): List<Data> {
        val resultList = ArrayList<Data>()

        val firstIterator = this.listIterator()
        val secondIterator = otherList.listIterator()

        var firstIndex = 0
        var secondIndex = 0

        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            val firstData = this.listIterator(firstIndex).next()
            val secondData = otherList.listIterator(secondIndex).next()

            if (firstData < secondData) {
                resultList.add(firstData)
                firstIterator.next()
                firstIndex++
            } else {
                resultList.add(secondData)
                secondIterator.next()
                secondIndex++
            }
        }

        when {
            firstIterator.hasNext() -> resultList.addAll(firstIterator.asSequence())
            secondIterator.hasNext() -> resultList.addAll(secondIterator.asSequence())
        }

        return resultList
    }


    private fun List<Data>.toDataUIList() =
        groupBy { it.key }
        .map { entry ->
            DataUI(
                title = entry.value[0].observe(),
                accountList = entry.value.map { it.observe() }.toMutableList()
            )
        }
}