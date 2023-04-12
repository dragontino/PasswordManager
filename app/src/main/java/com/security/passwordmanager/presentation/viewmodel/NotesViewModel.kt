package com.security.passwordmanager.presentation.viewmodel

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.animationTimeMillis
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.isValidUrl
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotesViewModel(dataRepository: DataRepository) : DataViewModel(dataRepository) {
    enum class TopBarState {
        Search,
        Navigate,
        Hidden
    }


    internal var topBarState by mutableStateOf(TopBarState.Navigate)

    var dataType by mutableStateOf(DataType.All)

    var query by mutableStateOf("")

    var openedItemId: String? by mutableStateOf(null)

    var showFab by mutableStateOf(true)

    var notifyUserAboutFinishApp by mutableStateOf(true)

    private var _dataList by mutableStateOf(mapOf<String, UsersData>())

    private var _searchList by mutableStateOf(mapOf<String, UsersData>())


    val dataList: Map<String, UsersData> get() = when (topBarState) {
        TopBarState.Search -> _searchList
        else -> _dataList
    }


    init {
        snapshotFlow { dataType to query }
            .onEach {
                viewModelState = DataViewModelState.Loading
                delay(200)

                getDataList(dataType = it.first, query = it.second) { result ->
                    if (result is Result.Success) {
                        _searchList = result.data
                        openedItemId = when {
                            result.data.size == 1 && topBarState == TopBarState.Search ->
                                _searchList.keys.firstOrNull()
                            else -> null
                        }
                    }
                    else if (result is Result.Error) {
                        result.exception.printStackTrace()
                    }

                    viewModelState = if (dataList.isEmpty()) {
                        DataViewModelState.EmptyList
                    } else {
                        DataViewModelState.Ready
                    }
                }
            }
            .launchIn(viewModelScope)


        fetchDataList(dataType)
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
            showFab = false
            viewModelState = DataViewModelState.EmptyList
        }
    }


    fun closeSearchbar() {
        viewModelScope.launch {
            viewModelState = DataViewModelState.Loading
            query = ""
            delay(400)
            topBarState = TopBarState.Navigate
            showFab = true
            viewModelState = if (dataList.isEmpty()) {
                DataViewModelState.EmptyList
            } else {
                DataViewModelState.Ready
            }
        }
    }


    fun createIntentForUrl(address: String): Intent? {
        val urlString = when {
            address.startsWith("https://") || address.startsWith("http://") -> address
            else -> "https://www.$address"
        }

        return when {
            urlString.isValidUrl() -> Intent(Intent.ACTION_VIEW, urlString.toUri())
            else -> null
        }
    }


    suspend fun refreshData(errorMessage: (String?) -> Unit = {}) {
        viewModelState = DataViewModelState.Loading
        delay(100)

        val query = if (topBarState == TopBarState.Search) query else null

        getDataList(dataType, query) {
            if (it is Result.Success) {
                _dataList = it.data
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


    fun deleteData(
        id: String,
        dataType: DataType,
        result: (success: Boolean) -> Unit = {}
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



    @OptIn(ExperimentalAnimationApi::class)
    val enterDataChildAnimation = fadeIn(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 3,
            easing = LinearOutSlowInEasing,
        ),
    ) + expandVertically(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = LinearOutSlowInEasing,
        ),
    ) + scaleIn(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 4,
            easing = FastOutSlowInEasing
        ),
        initialScale = 0.6f
    )

    @OptIn(ExperimentalAnimationApi::class)
    val exitDataChildAnimation = fadeOut(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 3,
            easing = FastOutLinearInEasing
        )
    ) + shrinkVertically(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 2,
            easing = FastOutLinearInEasing
        )
    ) + scaleOut(
        animationSpec = tween(
            durationMillis = animationTimeMillis / 4,
            delayMillis = animationTimeMillis / 8,
            easing = FastOutLinearInEasing
        ),
        targetScale = 0.6f
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