package com.security.passwordmanager.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.security.passwordmanager.*
import com.security.passwordmanager.data.DataRepository
import com.security.passwordmanager.data.MainDatabase
import com.security.passwordmanager.model.Data
import com.security.passwordmanager.model.DataType
import com.security.passwordmanager.model.DataUI

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

    suspend fun getDataUIList(dataType: DataType? = null) =
        dataRepository.getDataUIList(email, dataType).decrypt()


    suspend fun searchData(query: String, type: DataType? = null) =
        dataRepository.searchData(email, query, type).decrypt()


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


    private fun List<DataUI>.decrypt(): List<DataUI> {
        for (dataUI in this) {
            dataUI.title.decrypt(cryptographer::decrypt)
            dataUI.accountList.forEach { it.decrypt(cryptographer::decrypt) }
        }
        return this
    }
}