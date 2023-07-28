package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchViewModel(dataRepository: DataRepository) : DataViewModel<UsersData>(dataRepository) {

    var dataType = DataType.All

    var query by mutableStateOf("")

    override var bottomSheetContent: @Composable (ColumnScope.() -> Unit) by mutableStateOf({})

    override var viewModelState by mutableStateOf(DataViewModelState.PreLoading)


    init {
//        viewModelScope.launch {
//            viewModelState = DataViewModelState.PreLoading
//            delay(4000)
//            viewModelState = DataViewModelState.Ready
//        }


        snapshotFlow { query }
            .onEach {
                delay(50)
                if (viewModelState != DataViewModelState.PreLoading) {
                    viewModelState = DataViewModelState.Loading
                }
                delay(300)

                repository.getDataList(dataType = dataType, query = it) { result ->
                    if (result is Result.Success) {
                        dataList = result.data
                        openedItemId = when (result.data.size) {
                            1 -> dataList.keys.firstOrNull()
                            else -> null
                        }
                    } else if (result is Result.Error) {
                        result.exception.printStackTrace()
                    }

                    viewModelState = when {
                        dataList.isEmpty() -> DataViewModelState.EmptyList
                        else -> DataViewModelState.Ready
                    }
                }
            }
            .launchIn(viewModelScope)
    }


    suspend fun refreshData(errorMessage: (String?) -> Unit) {
        viewModelState = DataViewModelState.Loading
        delay(100)

        repository.getDataList(dataType, query) { result ->
            if (result is Result.Success) {
                dataList = result.data
            } else if (result is Result.Error) {
                errorMessage(result.exception.localizedMessage)
                result.exception.printStackTrace()
            }

            viewModelState = when {
                dataList.isEmpty() -> DataViewModelState.EmptyList
                else -> DataViewModelState.Ready
            }
        }
    }


    @Composable
    override fun screenShape() = CutCornerShape(0)
}