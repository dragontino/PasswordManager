package com.security.passwordmanager.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.buildString
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DataViewModel(
    private val dataRepository: DataRepository,
    private val preferences: AppPreferences,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    companion object {
        private const val COPY_LABEL = "data copy"
    }


    fun addData(data: Data) {
        println("email = ${preferences.email}")
        data.email = preferences.email
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.addData(data.encrypt(cryptoManager::encrypt))
        }
    }

    fun updateData(data: Data) = viewModelScope.launch(Dispatchers.IO) {
        dataRepository.updateData(data.encrypt(cryptoManager::encrypt))
    }

    fun getDataUIList(dataType: DataType? = null) =
        dataRepository
            .getDataUIList(preferences.email, dataType)
            .map { it.decrypt() }


    suspend fun getAccountList(key: String, dataType: DataType) =
        dataRepository
            .getAccountList(preferences.email, key, dataType)
            .decrypt()


    fun searchData(query: String, type: DataType = DataType.All) =
        dataRepository.searchData(preferences.email, query, type).map { it.decrypt() }


    fun deleteData(data: Data) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteData(data)
        }
    }

    fun deleteRecords(data: Data) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteRecords(data)
        }
    }

    fun copyText(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(COPY_LABEL, text)
        clipboard.setPrimaryClip(clip)
        // TODO: 29.04.2022 сделать очистку буфера
        showToast(context, R.string.clipText)
    }

    fun copyData(context: Context, data: Data) {
        val dataString = data.toString(context)
        copyText(context, dataString)
    }

    fun copyAccountList(context: Context, accountList: List<Data>) {
        val first = accountList[0].toString(context)
        val result = buildString(first) {
            for (index in 1 until accountList.size) {
                val current = accountList[index].toString(context, false)
                append("\n", current)
            }
        }
        copyText(context, result)
    }


    private inline fun <reified T : Parcelable> List<T>.decrypt(): List<T> {
        when (T::class.java) {
            DataUI::class.java -> {
                for (dataUI in this) {
                    dataUI as DataUI
                    dataUI.title.decrypt(cryptoManager::decrypt)
                    dataUI.accountList.forEach { it.decrypt(cryptoManager::decrypt) }
                }
            }
            Data::class.java -> {
                forEach { (it as Data).decrypt(cryptoManager::decrypt) }
            }
        }

        return this
    }
}