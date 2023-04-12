package com.security.passwordmanager.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.FirebaseDao
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


abstract class DataViewModel(protected val repository: DataRepository) : ViewModel() {

    protected companion object {
        private const val COPY_LABEL = "data copy"

        const val TAG = "DataViewModel"
    }


    enum class DataViewModelState {
        Loading,
        EmptyList,
        Ready
    }



    internal var viewModelState by mutableStateOf(DataViewModelState.Loading)

    abstract val enterScreenFabAnimation: EnterTransition
    abstract val exitScreenFabAnimation: ExitTransition


    @Composable
    open fun screenShape(): Shape =
        MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )



    protected fun inspectData(
        data: UsersData,
        inspectionResult: (isNew: Boolean, id: String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.findDataInDatabase(data) {
                if (it is Result.Success) {
                    inspectionResult(false, it.data)
                } else if (it is Result.Error) {
                    inspectionResult(true, null)
                }
            }
        }
    }



//    protected fun <D : UsersData> inspectDataAndLoadToDatabase(
//        data: D,
//        inspectionResult: suspend (isNew: Boolean, id: String?) -> Unit = { _, _ -> },
//        loadingResult: (id: String?) -> Unit = {}
//    ) {
//        viewModelState = DataViewModelState.Loading
//
//        viewModelScope.launch {
//            repository.findDataInDatabase(data) { id ->
//                viewModelScope.launch {
//                    inspectionResult(id is Result.Error, null)
//
//                    if (id == null) {
//                        repository.addData(data) {
//                            viewModelState = when (it) {
//                                is Result.Loading -> DataViewModelState.Loading
//                                is Result.Error -> {
//                                    it.exception.printStackTrace()
//                                    loadingResult(null)
//                                    DataViewModelState.Ready
//                                }
//                                is Result.Success -> {
//                                    loadingResult(it.data)
//                                    DataViewModelState.Ready
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

//    fun <T> updateData(
//        id: String,
//        dataType: DataType,
//        dataUpdates: Map<String, T>,
//        encryptValue: (value: T, encryption: (String) -> String) -> T,
//        resultAction: (Result<Unit>) -> Unit = {}
//    ) {
//        viewModelState = DataViewModelState.Loading
//        viewModelScope.launch {
//            repository.updateData(id, dataType, dataUpdates, encryptValue, resultAction)
//            viewModelState = DataViewModelState.Ready
//        }
//    }


    fun fetchDataList(dataType: DataType): Flow<Map<String, UsersData>> =
        repository.fetchDataList(dataType)


    suspend fun getDataList(
        dataType: DataType = DataType.All,
        query: String? = null,
        resultAction: (Result<Map<String, UsersData>>) -> Unit
    ) =
        repository.getDataList(dataType, query, resultAction)

    fun copyText(context: Context, text: String, result: (message: String) -> Unit) {
        viewModelState = DataViewModelState.Loading
        viewModelScope.launch {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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