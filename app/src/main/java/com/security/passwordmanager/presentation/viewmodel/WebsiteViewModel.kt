package com.security.passwordmanager.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.animationTimeMillis
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.model.dao.usersdatachild.Account
import com.security.passwordmanager.data.repository.DataRepository
import com.security.passwordmanager.presentation.model.data.ChildStatus
import com.security.passwordmanager.presentation.model.data.ComposableAccount
import com.security.passwordmanager.presentation.model.data.ComposableWebsite
import com.security.passwordmanager.presentation.model.data.contains
import com.security.passwordmanager.presentation.model.enums.DataType
import com.security.passwordmanager.presentation.view.composablelements.DeleteDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WebsiteViewModel(repository: DataRepository) : DataViewModel(repository) {


    override val enterScreenFabAnimation = slideInHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    ) { it / 2 }


    override val exitScreenFabAnimation = slideOutHorizontally(
        animationSpec = tween(
            durationMillis = animationTimeMillis,
            easing = LinearEasing
        )
    ) { it / 2 }

    var id by mutableStateOf("")

    var website by mutableStateOf(ComposableWebsite())

    var showDialog by mutableStateOf(false)
        private set

    var dialogContent: @Composable () -> Unit by mutableStateOf({})
        private set


    var showErrors by mutableStateOf(false)

    var needUpdateName by mutableStateOf(false)


    val isInEdit get() =
        website.updatedProperties.isNotEmpty() ||
                website.accounts.any { it.updatedProperties.isNotEmpty() }


    fun getBottomSheetEditName(context: Context) = when {
        currentAccount.isNameRenaming -> context.getString(R.string.cancel_renaming_data)
        else -> context.getString(R.string.rename_data)
    }


    internal var currentAccountPosition by mutableStateOf(-1)

    val currentAccount get() = website.accounts
        .getOrElse(currentAccountPosition) { ComposableAccount() }


    init {
        snapshotFlow { id }
            .map {
                viewModelState = DataViewModelState.Loading
                delay(100)

                repository.getData(id = it, dataType = DataType.Website) { result ->
                    website = when (result) {
                        is Result.Success -> result.data.convertToComposable() as ComposableWebsite
                        else -> ComposableWebsite()
                    }
                }

                delay(100)
                viewModelState = DataViewModelState.Ready
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



    fun getDomainNameByUrl(context: Context, resultAction: (Result<String>) -> Unit) {
        if (website.name.isNotBlank()) return

        val url = website.address
        if (url.isBlank()) {
            resultAction(
                Result.Error(
                    Exception(
                        context.getString(R.string.empty_url))
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
        viewModelState = DataViewModelState.Loading

        val website = composableWebsite.convertToDao()

        inspectData(website) { isNew, id ->
            if (!isNew) {
                viewModelState = DataViewModelState.Ready
                composableWebsite.updatedProperties.remove(website::address.name)
                updateWebsite(websiteId = id!!, website = composableWebsite, result)
                return@inspectData
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
                    viewModelState = DataViewModelState.Ready
                }
            }
        }
    }


    fun updateWebsite(
        websiteId: String = this.id,
        website: ComposableWebsite = this.website,
        result: suspend (success: Boolean) -> Unit = {}
    ) {
        viewModelState = DataViewModelState.Loading

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
                    viewModelState = DataViewModelState.Ready
                }
            )
        }
    }



    internal fun deleteWebsite(
        @StringRes titleRes: Int = R.string.deletion_data_confirmation,
        resultAction: (success: Boolean) -> Unit = {}
    ) {
        fun deleteData() = viewModelScope.launch {
            repository.deleteData(id, DataType.Website) {
                viewModelState = when (it) {
                    is Result.Loading -> DataViewModelState.Loading
                    else -> {
                        resultAction(it is Result.Success)
                        DataViewModelState.Ready
                    }
                }
            }
        }
        
        openDialog {
            DeleteDialog(
                text = stringResource(titleRes),
                onConfirm = {
                    closeDialog()
                    deleteData()
                },
                onDismiss = ::closeDialog
            )
        }
    }
}