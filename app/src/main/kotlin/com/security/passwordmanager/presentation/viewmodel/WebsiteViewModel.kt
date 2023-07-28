package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.usersdata.UsersData
import com.security.passwordmanager.data.model.dao.usersdata.Website
import com.security.passwordmanager.data.model.dao.usersdatachild.Account
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.data.ChildStatus
import com.security.passwordmanager.presentation.model.data.ComposableAccount
import com.security.passwordmanager.presentation.model.data.ComposableWebsite
import com.security.passwordmanager.presentation.model.data.contains
import com.security.passwordmanager.presentation.model.enums.DataType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WebsiteViewModel(private val repository: DataRepository) : ViewModel() {
    companion object {
        private const val TAG = "WebsiteViewModel"
    }


    enum class State {
        PreLoading,
        Loading,
        Ready
    }


    var state by mutableStateOf(State.PreLoading)

    var id by mutableStateOf("")

    var website by mutableStateOf(ComposableWebsite())

    var showDialog by mutableStateOf(false)
        private set

    var dialogContent: @Composable () -> Unit by mutableStateOf({})
        private set

    var showErrors by mutableStateOf(false)

    var needUpdateWebsiteName by mutableStateOf(false)

    internal var currentAccountPosition by mutableStateOf(-1)

    val currentAccount
        get() = website.accounts
            .getOrElse(currentAccountPosition) { ComposableAccount() }


    val isInEdit
        get() =
            website.updatedProperties.isNotEmpty() ||
                    website.accounts.any { it.updatedProperties.isNotEmpty() }


    fun getBottomSheetEditName(context: Context) = when {
        currentAccount.isNameRenaming -> context.getString(R.string.cancel_renaming_data)
        else -> context.getString(R.string.rename_data)
    }


    init {
        snapshotFlow { id }
            .map {
                state = State.PreLoading
                delay(300)

                repository.getData(id = it, dataType = DataType.Website) { result ->
                    website = when (result) {
                        is Result.Success -> result.data.convertToComposable() as ComposableWebsite
                        else -> ComposableWebsite()
                    }
                }

                delay(100)
                state = State.Ready
            }
            .launchIn(viewModelScope)
    }


    fun openDialog(content: @Composable () -> Unit) {
        dialogContent = content
        showDialog = true
    }

    fun closeDialog() {
        dialogContent = {}
        showDialog = false
    }


    private fun inspectWebsite(
        website: Website,
        inspectionResult: suspend (isNew: Boolean, id: String?) -> Unit
    ) {
        viewModelScope.launch {
            repository.findDataInDatabase(website) {
                viewModelScope.launch(Dispatchers.Main) {
                    if (it is Result.Success) {
                        inspectionResult(false, it.data)
                    } else if (it is Result.Error) {
                        inspectionResult(true, null)
                    }
                }
            }
        }
    }


    private suspend fun addData(
        data: UsersData,
        resultId: suspend (Result<String>) -> Unit = {}
    ) {
        repository.addData(data) {
            viewModelScope.launch(Dispatchers.Main) {
                resultId(it)
            }
        }
    }


    /**
     * Функция, которая обновляет поля объекта, реализующего интерфейс [UsersData],
     * тип которого [dataType]. Новые значения полей передаются в [dataUpdates]
     * @param id идентификатор записи в таблице, данные которой нужно обновить
     * @param dataType тип данных
     * @param dataUpdates словарь новых данных. Ключами являются имена полей класса,
     * а значениями – новые значения этих полей
     * @param encryptValue лямбда функция, шифрующая каждое значение, переданное в [dataUpdates]
     * @param resultAction результат выполнения.
     * Вызывается по окончании загрузки и работает на главном потоке
     */
    private suspend fun <T> updateData(
        id: String,
        dataType: DataType,
        dataUpdates: Map<String, T>,
        encryptValue: (value: T, encryption: (String) -> String) -> T,
        resultAction: suspend (Result<Unit>) -> Unit = {}
    ) {
        repository.updateData(id, dataType, dataUpdates, encryptValue) {
            viewModelScope.launch(Dispatchers.Main) {
                resultAction(it)
            }
        }
    }


    fun getDomainNameByUrl(context: Context, resultAction: (Result<String>) -> Unit) {
        if (website.name.isNotBlank()) return

        val url = website.address
        if (url.isBlank()) {
            resultAction(
                Result.Error(
                    Exception(
                        context.getString(R.string.empty_url)
                    )
                ),
            )
            return
        }

        viewModelScope.launch {
            when (val domainNameResult = repository.getWebsiteDomainName(url)) {
                is Result.Success -> resultAction(domainNameResult)
                is Result.Error -> {
                    Log.w(TAG, domainNameResult.exception)
                    resultAction(domainNameResult)
                }

                is Result.Loading -> {}
            }
        }
    }


    fun addWebsite(
        composableWebsite: ComposableWebsite = this.website,
        result: suspend (success: Boolean) -> Unit = {}
    ) {
        state = State.Loading

        val website = composableWebsite.convertToDao()

        inspectWebsite(website) { isNew, id ->
            if (!isNew) {
                state = State.Ready
                composableWebsite.updatedProperties.remove(website::address.name)
                updateWebsite(websiteId = id!!, website = composableWebsite, result)
                return@inspectWebsite
            }


            viewModelScope.launch {
                val resultUrl = repository.getWebsiteLogo(website.address)

                if (resultUrl is Result.Success) {
                    website.logoUrl = resultUrl.data
                } else if (resultUrl is Result.Error) {
                    Log.w(TAG, resultUrl.exception)
                }

                addData(data = website) {
                    if (it is Result.Success) {
                        this@WebsiteViewModel.id = it.data
                    }
                    if (it !is Result.Loading) result(it is Result.Success)
                    state = State.Ready
                }
            }
        }
    }


    /**
     * Функция, которая обновляет данные об сайте в базе данных
     * @param websiteId id записи, данные которой нужно обновить
     * @param website текущий объект [ComposableWebsite],
     * его данные сравнятся с теми, что записаны в бд и несовпадающие строки обновятся
     * @param result результат выполенения, выполняется на фоновом потоке
     */
    fun updateWebsite(
        websiteId: String = this.id,
        website: ComposableWebsite = this.website,
        result: suspend (success: Boolean) -> Unit = {}
    ) {
        state = State.Loading

        val dataUpdates = website
            .updatedProperties
            .mapValues { it.value.second }
            .toMutableMap()

        val accountsUpdates = website
            .accounts
            .info
            .flatMap { (accountId, status) ->
                val path = "${website::accounts.name}/$accountId"

                when (status) {
                    ChildStatus.Created -> {
                        val account = website.accounts
                            .first { it.uid == accountId }
                            .convertToDao()

                        return@flatMap listOf(path to account)
                    }

                    ChildStatus.Deleted -> listOf(path to null)

                    ChildStatus.Updated -> website
                        .accounts
                        .first { it.uid == accountId }
                        .updatedProperties
                        .map { (property, changes) ->
                            "$path/$property" to changes.second
                        }
                }
            }
            .toMap()

        viewModelScope.launch {
            if (website::address in website.updatedProperties) {
                when (val logoUrlResult = repository.getWebsiteLogo(website.address)) {
                    is Result.Success ->
                        dataUpdates[website::logoUrl.name] = logoUrlResult.data

                    is Result.Error -> {
                        Log.w(TAG, logoUrlResult.exception)
                        dataUpdates[website::logoUrl.name] = ""
                    }

                    else -> {}
                }
            }

            updateData(
                id = websiteId,
                dataType = DataType.Website,
                dataUpdates = dataUpdates + accountsUpdates,
                encryptValue = { value, encryption ->
                    when (value) {
                        is Account -> {
                            value.encrypt(encryption)
                            value
                        }

                        is String -> encryption(value)
                        else -> value
                    }
                },
                resultAction = {
                    if (it != Result.Loading) result(it is Result.Success)
                    state = State.Ready
                }
            )
        }
    }


    fun deleteWebsite(resultAction: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            repository.deleteData(id, DataType.Website) {
                state = when (it) {
                    is Result.Loading -> State.Loading
                    else -> {
                        resultAction(it is Result.Success)
                        State.Ready
                    }
                }
            }
        }
    }


    fun copyText(
        context: Context,
        clipboardManager: ClipboardManager,
        text: String,
        result: (resultMessage: String) -> Unit
    ) {
        state = State.Loading
        viewModelScope.launch {
            try {
                clipboardManager.setText(AnnotatedString(text))
                state = State.Ready
                result(
                    context.getString(R.string.copy_text_successful)
                )
            } catch (exception: RuntimeException) {
                state = State.Ready
                result(
                    context.getString(
                        R.string.copy_text_exception,
                        exception.localizedMessage,
                    ),
                )
            }
        }
    }


    fun copyWebsite(
        context: Context,
        clipboardManager: ClipboardManager,
        website: Website = this.website.convertToDao(),
        result: (resultMessage: String) -> Unit
    ) {
        val dataString = website.convertToString(context)
        copyText(context, clipboardManager, dataString, result)
    }
}