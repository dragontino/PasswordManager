package com.security.passwordmanager.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Parcelable
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.buildString
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch


abstract class DataViewModel(
    private val dataRepository: DataRepository,
    private val preferences: AppPreferences,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    companion object {
        private const val COPY_LABEL = "data copy"
    }


    enum class DataViewModelState {
        Loading,
        EmptyList,
        Ready
    }



    internal var viewModelState by mutableStateOf(DataViewModelState.Loading)

    var showFab by mutableStateOf(true)

    abstract val enterScreenFabAnimation: EnterTransition
    abstract val exitScreenFabAnimation: ExitTransition


    @Composable
    open fun screenShape(): Shape =
        MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )






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


    suspend fun searchData(query: String, dataType: DataType) = dataRepository
        .searchData(preferences.email, query, dataType).decrypt()


    @ExperimentalCoroutinesApi
    fun getDataUIList(dataType: DataType) = dataRepository
        .getDataList(preferences.email, dataType)
        .mapLatest { it.decrypt() }



    suspend fun getAccountList(key: String, dataType: DataType) =
        dataRepository
            .getAccountList(preferences.email, key, dataType)
            .decrypt()


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

    fun copyText(context: Context, text: String, resultMessage: (resultMessage: String) -> Unit) {
        viewModelState = DataViewModelState.Loading
        viewModelScope.launch {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(COPY_LABEL, text)
                clipboard.setPrimaryClip(clip)
                viewModelState = DataViewModelState.Ready
                resultMessage(
                    context.getString(R.string.copy_text_successful)
                )
            } catch (exception: RuntimeException) {
                viewModelState = DataViewModelState.Ready
                resultMessage(
                    context.getString(
                        R.string.copy_text_exception,
                        exception.localizedMessage
                    )
                )
            }
        }
    }

    fun copyData(context: Context, data: Data, resultMessage: (resultMessage: String) -> Unit) {
        val dataString = data.toString(context)
        copyText(context, dataString, resultMessage)
    }


    fun copyAccountList(
        context: Context,
        accountList: List<Data>,
        resultMessage: (resultMessage: String) -> Unit
    ) {
        val first = accountList[0].toString(context)
        val text = buildString(first) {
            for (index in 1 until accountList.size) {
                val current = accountList[index].toString(context, false)
                append("\n", current)
            }
        }
        copyText(context, text, resultMessage)
    }


    private inline fun <reified T : Parcelable> List<T>.decrypt(): List<T> {
        forEach {
            when (it) {
                is DataUI -> {
                    it.title.decrypt(cryptoManager::decrypt)
                    it.accountList.forEach { observableData ->
                        observableData.decrypt(cryptoManager::decrypt)
                    }
                }
                is Data -> it.decrypt(cryptoManager::decrypt)
            }
        }
        return this
    }
}