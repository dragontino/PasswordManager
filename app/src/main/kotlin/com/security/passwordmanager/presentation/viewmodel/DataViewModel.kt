package com.security.passwordmanager.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.FirebaseDao
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.isValidUrl
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.launch


abstract class DataViewModel<D : UsersData>(protected val repository: DataRepository) :
    ViewModel() {

    protected companion object {
        private const val COPY_LABEL = "data copy"
    }


    enum class DataViewModelState {
        PreLoading,
        Loading,
        EmptyList,
        Ready
    }


    abstract var viewModelState: DataViewModelState

    var dataList: Map<String, D> by mutableStateOf(mapOf())
        protected set

    var openedItemId: String? by mutableStateOf(null)

    abstract var bottomSheetContent: @Composable (ColumnScope.() -> Unit)

    var dialogContent: @Composable () -> Unit by mutableStateOf({})
        private set

    var showDialog by mutableStateOf(false)
        private set


    fun openDialog(content: @Composable () -> Unit) {
        dialogContent = content
        showDialog = true
    }


    fun closeDialog() {
        showDialog = false
        dialogContent = {}
    }


    @Composable
    open fun screenShape(): CornerBasedShape =
        MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )


    fun createIntentForUrl(address: String): Intent? {
        val urlString = when {
            address.startsWith("https://") || address.startsWith("http://") -> address
            else -> "https://$address"
        }

        return when {
            urlString.isValidUrl() -> Intent(Intent.ACTION_VIEW, urlString.toUri())
            else -> null
        }
    }


    fun deleteData(
        id: String,
        dataType: DataType,
        result: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            repository.deleteData(id, dataType) {
                when (it) {
                    is Result.Loading -> viewModelState = DataViewModelState.Loading
                    else -> result(it is Result.Success)
                }
            }

            viewModelState = when {
                dataList.isEmpty() -> DataViewModelState.EmptyList
                else -> DataViewModelState.Ready
            }
        }
    }


    fun copyText(context: Context, text: String, result: (message: String) -> Unit) {
        viewModelState = DataViewModelState.Loading
        viewModelScope.launch {
            try {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(COPY_LABEL, text)
                clipboard.setPrimaryClip(clip)
                viewModelState = DataViewModelState.Ready
                result(
                    context.getString(R.string.copy_text_successful)
                )
            } catch (exception: RuntimeException) {
                viewModelState = DataViewModelState.Ready
                result(
                    context.getString(
                        R.string.copy_text_exception,
                        exception.localizedMessage
                    )
                )
            }
        }
    }


    fun copyData(context: Context, data: FirebaseDao, result: (message: String) -> Unit) {
        val dataString = data.convertToString(context)
        copyText(context, dataString, result)
    }
}