package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AllNotesViewModel(dataRepository: DataRepository) : DataViewModel<UsersData>(dataRepository) {

    var showFab by mutableStateOf(true)

    var notifyUserAboutFinishApp by mutableStateOf(true)

    override var bottomSheetContent: @Composable (ColumnScope.() -> Unit) by mutableStateOf({})

    override var viewModelState by mutableStateOf(DataViewModelState.PreLoading)


    init {
        viewModelState = DataViewModelState.PreLoading
        repository.fetchDataList(dataType = DataType.All)
            .onEach {
                dataList = it
                viewModelState = if (dataList.isEmpty()) {
                    DataViewModelState.EmptyList
                } else {
                    DataViewModelState.Ready
                }
            }
            .launchIn(viewModelScope)
    }


    suspend fun refreshData(errorMessage: (String?) -> Unit = {}) {
        viewModelState = DataViewModelState.Loading
        delay(100)

        repository.getDataList(DataType.All) {
            if (it is Result.Success) {
                dataList = it.data
            } else if (it is Result.Error) {
                errorMessage(it.exception.localizedMessage)
                it.exception.printStackTrace()
            }

            viewModelState = when {
                dataList.isEmpty() -> DataViewModelState.EmptyList
                else -> DataViewModelState.Ready
            }
        }
    }
}