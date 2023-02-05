package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.animationTimeMillis
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(
    dataRepository: DataRepository,
    preferences: AppPreferences,
    cryptoManager: CryptoManager
) : DataViewModel(dataRepository, preferences, cryptoManager) {
    enum class TopBarState {
        Search,
        Navigate,
        Hidden
    }


    internal var topBarState by mutableStateOf(TopBarState.Navigate)

    var dataType by mutableStateOf(DataType.All)

    var query by mutableStateOf("")


    private var _dataList by mutableStateOf(listOf<DataUI>())

    private var _searchList by mutableStateOf(listOf<DataUI>())


    val dataList: List<DataUI> get() = when (topBarState) {
        TopBarState.Search -> _searchList
        else -> _dataList
    }


    init {
        snapshotFlow { query to dataType }
            .map {
                searchData(query = it.first, dataType = it.second)
            }
            .onEach {
                viewModelState = DataViewModelState.Loading
                delay(200)
                _searchList = it

                viewModelState = if (dataList.isEmpty()) {
                    DataViewModelState.EmptyList
                } else {
                    DataViewModelState.Ready
                }
            }
            .launchIn(viewModelScope)



        getDataUIList(dataType)
            .onEach {
                viewModelState = DataViewModelState.Loading
                _dataList = it

                viewModelState = if (it.isEmpty()) {
                    DataViewModelState.EmptyList
                } else {
                    DataViewModelState.Ready
                }
            }
            .launchIn(viewModelScope)
    }



    fun openSearchbar() {
        viewModelScope.launch {
            viewModelState = DataViewModelState.Loading
            delay(100)
            topBarState = TopBarState.Search
            viewModelState = DataViewModelState.EmptyList
        }
    }


    fun closeSearchbar() {
        viewModelScope.launch {
            viewModelState = DataViewModelState.Loading
            query = ""
            delay(400)
            topBarState = TopBarState.Navigate
            viewModelState = if (dataList.isEmpty()) {
                DataViewModelState.EmptyList
            } else {
                DataViewModelState.Ready
            }
        }
    }



    suspend fun refreshData() {
        viewModelState = DataViewModelState.Loading
        delay(100)

        if (topBarState == TopBarState.Search) {
            _searchList = searchData(query, dataType)
            delay(100)

            viewModelState = when {
                _searchList.isEmpty() -> DataViewModelState.EmptyList
                else -> DataViewModelState.Ready
            }
        }
        else {
            getDataUIList(dataType).collect {
                _dataList = it
                delay(100)
                viewModelState = when {
                    dataList.isEmpty() -> DataViewModelState.EmptyList
                    else -> DataViewModelState.Ready
                }
            }
        }
    }


    val enterFabAnimation = fadeIn(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 }



    val exitFabAnimation = fadeOut(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = LinearEasing
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = FastOutSlowInEasing
        )
    ) { it / 2 }



    override val enterScreenFabAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    )

    override val exitScreenFabAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    )



    val enterDataItemAnimation = fadeIn(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 4,
            easing = LinearOutSlowInEasing,
        ),
    ) + expandVertically(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = LinearOutSlowInEasing,
        ),
    )

    val exitDataItemAnimation = fadeOut(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = FastOutLinearInEasing
        )
    ) + shrinkVertically(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = FastOutLinearInEasing
        )
    )



    @Composable
    override fun screenShape() = when {
        topBarState != TopBarState.Hidden -> {
            MaterialTheme.shapes.large.copy(
                bottomStart = CornerSize(0),
                bottomEnd = CornerSize(0)
            )
        }
        else -> RoundedCornerShape(0)
    }
}