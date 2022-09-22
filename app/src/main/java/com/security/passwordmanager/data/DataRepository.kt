package com.security.passwordmanager.data

import com.security.passwordmanager.model.*
import com.security.passwordmanager.slice
import kotlin.math.min

class DataRepository(private val dataDao: DataDao) {

    fun addData(data: Data) = when (data) {
        is Website -> dataDao.addWebsite(data)
        is BankCard -> dataDao.addBankCard(data)
        else -> TODO()
    }


    fun updateData(data: Data) = when (data) {
        is Website -> dataDao.updateWebsite(data)
        is BankCard -> dataDao.updateBankCard(data)
        else -> TODO()
    }


    suspend fun getDataUIList(email: String, dataType: DataType? = null): List<DataUI> =
        when (dataType) {
            DataType.Website -> dataDao.getWebsiteList(email)
            DataType.BankCard -> dataDao.getBankCardList(email)
            null -> sortedConcatenation(
                dataDao.getWebsiteList(email),
                dataDao.getBankCardList(email)
            )
        }.toDataUIList()


    suspend fun searchData(email: String, query: String?, type: DataType? = null) : List<DataUI> {
        if (query == null || query.isBlank())
            return getDataUIList(email, type)

        return when (type) {
            DataType.Website -> dataDao.searchWebsite(email, query)
            DataType.BankCard -> dataDao.searchBankCard(email, query)
            null -> {
                val search = dataDao.search(email, query)
                sortedConcatenation(websiteList = search.first, bankCardList = search.second)
            }
        }.toDataUIList()
    }

    //удаляет только 1 запись в бд
    fun deleteData(data: Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data)
        else -> dataDao.deleteBankCard(data as BankCard)
    }

    //удаляет несколько записей в бд
    fun deleteRecords(data: Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data.email, data.key)
        else -> dataDao.deleteBankCard(data.email, data.key)
    }




    private fun sortedConcatenation(
        websiteList: MutableList<Website>,
        bankCardList: MutableList<BankCard>
    ): List<Data> {
        if (bankCardList.isEmpty())
            return websiteList
        else if (websiteList.isEmpty())
            return bankCardList

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


    private fun List<Data>.toDataUIList() =
        groupBy { it.key }
        .map {
            DataUI(
                title = it.value[0],
                accountList = it.value.toMutableList()
            )
        }
}