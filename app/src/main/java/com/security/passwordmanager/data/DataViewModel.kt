package com.security.passwordmanager.data

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.security.passwordmanager.Cryptographer
import com.security.passwordmanager.R
import com.security.passwordmanager.getString
import com.security.passwordmanager.settings.EnumPreferences.APP_PREFERENCES
import com.security.passwordmanager.settings.EnumPreferences.APP_PREFERENCES_LOGIN
import com.security.passwordmanager.showToast

class DataViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val COPY_LABEL = "data copy"
    }

    private val mApplication = application
    private val dataRepository: DataRepository
    private val cryptographer = Cryptographer(application)

    private val email: String get() {
        val preferences = mApplication
            .getSharedPreferences(APP_PREFERENCES.title, Context.MODE_PRIVATE)

        return preferences.getString(APP_PREFERENCES_LOGIN.title) ?: ""
    }

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
        showToast(mApplication, R.string.clipText)
    }

    fun copyData(data: Data) {
        val dataString = data.toString(mApplication)
        copyText(dataString)
    }

    fun copyAccountList(accountList: List<Data>) {
        val builder = StringBuilder(
                accountList[0].toString(mApplication))

        for (index in 1 until accountList.size) {
            val d = accountList[index]
            builder.append("\n").append(d.toString(mApplication, false))
        }
        copyText(builder.toString())
    }



    private fun MutableList<Data>.decrypt(): List<Data> {
        for (i in indices) {
            val data = this[i]
            this[i] = data.decrypt(cryptographer::decrypt)
        }
        return this
    }
}