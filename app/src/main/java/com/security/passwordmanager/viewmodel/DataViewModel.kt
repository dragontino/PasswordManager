package com.security.passwordmanager.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.security.passwordmanager.*
import com.security.passwordmanager.data.Data
import com.security.passwordmanager.data.DataRepository
import com.security.passwordmanager.data.DataType
import com.security.passwordmanager.data.MainDatabase

class DataViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val COPY_LABEL = "data copy"

        fun getInstance(owner: ViewModelStoreOwner) =
            ViewModelProvider(owner)[DataViewModel::class.java]
    }

    private val mApplication = application
    private val dataRepository: DataRepository
    private val cryptographer = Cryptographer(application)

    private val email: String get() = AppPreferences(mApplication).email

    init {
        val dataDao = MainDatabase.getDatabase(application).dataDao()
        dataRepository = DataRepository(dataDao)
    }

    fun addData(data: Data) {
        data.email = email
        dataRepository.addData(data.encrypt(cryptographer::encrypt))
    }

    fun updateData(data: Data) =
        dataRepository.updateData(data.encrypt(cryptographer::encrypt))

    fun getDataList() =
        dataRepository.getDataList(email).decrypt()

    fun getAccountList(key: String, type: DataType) =
        dataRepository.getAccountList(email, key, type).decrypt()

    fun getAccountList(data: Data) = getAccountList(data.key, data.type)


    fun searchData(query: String, type: DataType? = null): List<Data> =
        dataRepository.searchData(email, query, type)

    fun deleteData(data: Data) = dataRepository.deleteData(data)

    fun deleteRecords(data: Data) = dataRepository.deleteRecords(data)

    fun copyText(text: String) {
        val clipboard = mApplication.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(COPY_LABEL, text)
        clipboard.setPrimaryClip(clip)
        // TODO: 29.04.2022 сделать очистку буфера
        showToast(mApplication, R.string.clipText)
    }

    fun copyData(data: Data) {
        val dataString = data.toString(mApplication)
        copyText(dataString)
    }

    fun copyAccountList(accountList: List<Data>) {
        val first = accountList[0].toString(mApplication)
        val result = buildString(first) {
            for (index in 1 until accountList.size) {
                val current = accountList[index].toString(mApplication, false)
                append("\n", current)
            }
        }
        copyText(result)
    }



    private fun MutableList<Data>.decrypt(): List<Data> {
        for (i in indices) {
            val data = this[i]
            this[i] = data.decrypt(cryptographer::decrypt)
        }
        return this
    }
}