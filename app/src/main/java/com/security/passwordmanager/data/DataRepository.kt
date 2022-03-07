package com.security.passwordmanager.data
import com.security.passwordmanager.data.DataType.BANK_CARD
import com.security.passwordmanager.data.DataType.WEBSITE
import kotlin.math.min

class DataRepository(private val dataDao: DataDao) {

    fun addData(data : Data) = if (data is Website)
        dataDao.addWebsite(data)
    else
        dataDao.addBankCard(data as BankCard)


    fun updateData(data: Data) = if (data is Website)
        dataDao.updateWebsite(data)
    else
        dataDao.updateBankCard(data as BankCard)


    fun getDataList() : List<Data> {
        val websiteList = dataDao.getWebsiteList()
        val bankCardList = dataDao.getBankCardList()

        return sortedConcat(websiteList, bankCardList)
    }

    fun getAccountList(key : String, type : DataType): List<Data> {
        val accountList = dataDao.getAccountList(key)

        return when (type) {
            BANK_CARD -> {
                val bankAccountList = dataDao.getBankAccountList(key)

                sortedConcat(accountList, bankAccountList) { d1, d2 ->
                    d1.id > d2.id
                }
            }
            WEBSITE -> accountList
        }
    }

    fun searchData(query : String?) : List<Data> {
        if (query == null || query.isEmpty())
            return getDataList()

        val search = dataDao.search(query)

        return sortedConcat(websiteList = search.first, bankCardList = search.second)
    }

    //удаляет только 1 запись в бд
    fun deleteData(data : Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data)
        else -> dataDao.deleteBankCard(data as BankCard)
    }

    //удаляет несколько записей в бд
    fun deleteRecords(data : Data) = when (data) {
        is Website -> dataDao.deleteWebsite(data.key)
        else -> dataDao.deleteBankCard(data.key)
    }


    private fun sortedConcat(
        websiteList: MutableList<Website>,
        bankCardList: MutableList<BankCard>,
        //TODO переделать
        funCompare: (Data, Data) -> Boolean = { d1, d2 -> d1 > d2 }
    ): List<Data> {

        if (bankCardList.isEmpty())
            return websiteList.check()
        else if (websiteList.isEmpty())
            return bankCardList.check()

        val dataList = ArrayList<Data>()

        val min = min(websiteList.size, bankCardList.size)
        var w = 0
        var b = 0

        while (w < min && b < min) {
            val website = websiteList[w]
            val bankCard = bankCardList[b]
            if (funCompare(website, bankCard)) {
                dataList.checkAndAdd(website)
                w++
            } else {
                dataList.checkAndAdd(bankCard)
                b++
            }
        }

        if (w > b) while (b < bankCardList.size) {
            dataList.checkAndAdd(bankCardList[b])
            b++
        } else if (w < b) while (w < websiteList.size) {
            dataList.checkAndAdd(websiteList[w])
            w++
        }

        return dataList
    }

    private fun MutableList<Data>.checkAndAdd(value: Data) {
        this.forEach { if (value == it) return }
        add(value)
    }

    private fun <T : Data>MutableList<T>.check() : List<Data> {
        var index = 0

        while (index < size) {
            if (this[index] in subList(index + 1, size)) {
                removeAt(index)
            }
            index++
        }

        return this
    }
}