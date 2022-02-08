package com.security.passwordmanager.data

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.security.passwordmanager.Cryptographer
import com.security.passwordmanager.R

class DataViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val COPY_LABEL = "DataViewModel_copy"
    }

    private val mApplication : Application = application
    private val dataRepository : DataRepository
    private val mCryptographer = Cryptographer(application)

    init {
        val dataDao = MainDatabase.getDatabase(application).dataDao()
        dataRepository = DataRepository(dataDao)
    }

    fun addData(data : Data) =
        dataRepository.addData(data.encrypt(mCryptographer))

    fun updateData(data: Data) =
        dataRepository.updateData(data.encrypt(mCryptographer))

    fun getDataList(): List<Data> {
        val dataList = dataRepository.getDataList()
        decryptList(dataList.toMutableList())
        return dataList
    }

    fun getAccountList(key : String, type : DataType) : List<Data> {
        val accountList = dataRepository.getAccountList(key, type)
        decryptList(accountList.toMutableList())
        return accountList
    }

    fun searchData(query: String): List<Data> = dataRepository.searchData(query)

    fun deleteData(data : Data) = dataRepository.deleteData(data)

    fun deleteRecords(data : Data) = dataRepository.deleteRecords(data)

    fun swap(first : Data, second : Data) = dataRepository.swap(first, second)

    fun copyText(text : String) {
        val clipboard = mApplication.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(COPY_LABEL, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(mApplication, R.string.clipText, Toast.LENGTH_SHORT).show()
    }

    fun copyData(data: Data) {
        val dataString = data.toString(mApplication, true)
        copyText(dataString)
    }

    fun copyAccountList(accountList: List<Data>) {
        val builder = StringBuilder(
                accountList[0].toString(mApplication, true))

        for (index in 1 until accountList.size) {
            val d = accountList[index]
            builder.append("\n").append(d.toString(mApplication, false))
        }
        copyText(builder.toString())
    }



    private fun decryptList(list: MutableList<Data>) {
        for (i in list.indices) {
            val data = list[i]
            list[i] = data.decrypt(mCryptographer)
        }
    }
}