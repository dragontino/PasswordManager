package com.security.passwordmanager.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.animationTimeMillis
import com.security.passwordmanager.buildString
import com.security.passwordmanager.data.AppPreferences
import com.security.passwordmanager.data.CryptoManager
import com.security.passwordmanager.data.model.Data
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.DataUI
import com.security.passwordmanager.presentation.model.ObservableBankCard
import com.security.passwordmanager.presentation.model.ObservableWebsite
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalCoroutinesApi::class)
class DataViewModel(
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


    enum class TopBarState {
        Search,
        Navigate,
        Hidden
    }




    internal var viewModelState by mutableStateOf(DataViewModelState.Loading)

    internal var topBarState by mutableStateOf(TopBarState.Navigate)

    var dataType by mutableStateOf(DataType.All)

    var query by mutableStateOf("")

    var showFab by mutableStateOf(true)

    private var dataList by mutableStateOf(listOf<DataUI>())

    private var searchList by mutableStateOf(listOf<DataUI>())

    val currentList: List<DataUI> get() = when (topBarState) {
        TopBarState.Search -> searchList
        else -> dataList
    }


    var accountList = mutableStateListOf(
        when (dataType) {
            DataType.BankCard -> ObservableBankCard()
            else -> ObservableWebsite()
        }
    )



    init {
        viewModelScope.launch {
            delay(animationTimeMillis + 100L)
        }

        snapshotFlow { query to dataType }
            .mapLatest {
                dataRepository.searchData(
                    preferences.email,
                    query = it.first,
                    dataType = it.second
                ).decrypt()
            }
            .onEach {
                viewModelState = DataViewModelState.Loading
                delay(200)
                searchList = it
                viewModelState = if (currentList.isEmpty()) {
                    DataViewModelState.EmptyList
                } else {
                    DataViewModelState.Ready
                }
            }
            .launchIn(viewModelScope)


        dataRepository
            .getDataList(preferences.email, dataType)
            .mapLatest { it.decrypt() }
            .onEach {
                dataList = it
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
            viewModelState = if (currentList.isEmpty()) {
                DataViewModelState.EmptyList
            } else {
                DataViewModelState.Ready
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



    val enterScreenFabAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    )

    val exitScreenFabAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    )




    @Composable
    fun screenShape() = if (topBarState != TopBarState.Hidden) {
        MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0),
            bottomEnd = CornerSize(0)
        )
    } else {
        RoundedCornerShape(0)
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