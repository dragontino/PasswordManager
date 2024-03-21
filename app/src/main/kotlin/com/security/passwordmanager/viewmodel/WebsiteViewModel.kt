package com.security.passwordmanager.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.UID
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.model.entity.Website
import com.security.passwordmanager.domain.model.settings.Settings
import com.security.passwordmanager.domain.usecase.EntityUseCase
import com.security.passwordmanager.domain.usecase.GetWebsiteDomainNameUseCase
import com.security.passwordmanager.domain.usecase.GetWebsiteLogoUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import com.security.passwordmanager.domain.util.Encrypt
import com.security.passwordmanager.model.ChildStatus
import com.security.passwordmanager.model.ComposableWebsite
import com.security.passwordmanager.model.WebsiteMapper.mapToComposable
import com.security.passwordmanager.model.contains
import com.security.passwordmanager.model.convertToString
import com.security.passwordmanager.view.composables.dialogs.ConfirmExitDialog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WebsiteViewModel @AssistedInject constructor(
    private val entityUseCase: EntityUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val getWebsiteDomainNameUseCase: GetWebsiteDomainNameUseCase,
    private val getWebsiteLogoUseCase: GetWebsiteLogoUseCase,
    private val exceptionMessage: ExceptionMessage,
    @Assisted private val id: String
) : EventsViewModel() {

    var state by mutableStateOf(ViewModelState.Loading)

    var settings by mutableStateOf(Settings())
        private set

    var website by mutableStateOf(ComposableWebsite())

    var currentAccountPosition: Int? by mutableStateOf(null)

    var showErrors by mutableStateOf(false)

    var needUpdateWebsiteName by mutableStateOf(false)


    val isNew: Boolean
        get() = id.isBlank()


    val isInEdit
        get() = website.updatedProperties.isNotEmpty() ||
                website.accounts.any { it.updatedProperties.isNotEmpty() }


    init {
        settingsUseCase.fetchSettings()
            .onEach { settings = it }
            .launchIn(viewModelScope)

        snapshotFlow { id }
            .map {
                state = ViewModelState.Loading
                delay(300)

                entityUseCase.getEntityRecordById(
                    id = it,
                    type = EntityType.Website,
                    error = { throwable ->
                        exceptionMessage.getMessage(throwable)?.let(::showSnackbar)
                    },
                    success = { entity ->
                        website = (entity as Website).mapToComposable()
                        state = ViewModelState.Ready
                    },
                )
            }
            .launchIn(viewModelScope)
    }


    fun updateAutofill(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.updateSettingsProperty(Settings::autofill.name, newValue) {
                it.exceptionOrNull()
                    ?.let(exceptionMessage::getMessage)
                    ?.let(::showSnackbar)
            }
        }
    }


    fun onBackPress(context: Context) {
        if (isInEdit) {
            openDialog(
                type = ConfirmExitDialog(
                    onConfirm = {
                        saveInfo(
                            context = context,
                            success = { navigateTo(null) }
                        )
                    },
                    onDismiss = { navigateTo(null) },
                    onClose = ::closeDialog
                )
            )
        } else {
            navigateTo(null)
        }
    }


    fun saveInfo(context: Context, success: () -> Unit) {
        showErrors = true
        website.updateErrors(context)

        when {
            website.haveErrors -> showSnackbar(context.getString(R.string.invalid_data))
            id.isBlank() -> addWebsite(success = success)
            else -> updateWebsite(success = success)
        }
    }


    /*private fun inspectWebsite(
        website: Website,
        inspectionResult: suspend (isNew: Boolean, id: String?) -> Unit
    ) {
        viewModelScope.launch {
            entityUseCase.checkIfEntityRecordIsNew(website) {
                viewModelScope.launch(Dispatchers.Main) {
                    inspectionResult()
                    if (it.isSuccess) {
                        inspectionResult(it.getOrNull() != null, it.getOrNull())
                    } else {
                        inspectionResult(true, null)
                    }
                }
            }
        }
    }*/

/*
    private suspend fun insertEntity(
        entity: DatabaseEntity,
        resultId: (kotlin.Result<String>) -> Unit = {}
    ) {
        entityUseCase.insertEntity(entity) {
            viewModelScope.launch(Dispatchers.Main) {
                resultId(it)
            }
        }
    }*/


    fun getDomainNameByUrl(
        context: Context,
        failure: () -> Unit,
        success: (String) -> Unit
    ) {
        if (website.name.isNotBlank()) return

        val url = website.address
        if (url.isBlank()) {
            showSnackbar(context.getString(R.string.empty_url))
            return failure()
        }

        viewModelScope.launch {
            val domainNameResult = getWebsiteDomainNameUseCase.getWebsiteDomainName(url)
            domainNameResult.getOrNull()?.let(success)
            domainNameResult.exceptionOrNull()
                ?.let(exceptionMessage::getMessage)
                ?.let(::showSnackbar)
                ?.also { failure() }
        }
    }


    private fun addWebsite(
        composableWebsite: ComposableWebsite = this.website,
        success: () -> Unit
    ) {
        viewModelScope.launch {
            state = ViewModelState.Loading
            val website = composableWebsite.mapToUserData()
            val websiteId = entityUseCase.checkIfEntityRecordIsNew(entity = website)

            if (websiteId == null) {
                val logoUrl = getWebsiteLogoUseCase
                    .getWebsiteLogo(websiteUrl = website.address)
                    .getOrNull()

                entityUseCase.insertEntity(website.copy(logoUrl = logoUrl)) { result ->
                    result.getOrNull()?.let { success() }
                    result.exceptionOrNull()
                        ?.let(exceptionMessage::getMessage)
                        ?.let(::showSnackbar)
                    state = ViewModelState.Ready
                }
            } else {
                updateWebsite(
                    websiteId = websiteId,
                    website = composableWebsite,
                    success = success
                )
            }
        }
    }


    /**
     * Функция, которая обновляет данные об сайте в базе данных
     * @param websiteId id записи, данные которой нужно обновить
     * @param website текущий объект [ComposableWebsite],
     * его данные сравнятся с теми, что записаны в бд и несовпадающие строки обновятся
     * @param success вызывается, если данные успешно обновились.
     */
    private fun updateWebsite(
        websiteId: String = this.id,
        website: ComposableWebsite = this.website,
        success: () -> Unit
    ) {
        fun packWebsiteUpdates() = website
            .updatedProperties
            .mapValues { it.value.second }
            .toMutableMap()

        fun packAccountsUpdates() = website
            .accounts
            .info
            .flatMap { (accountId, status) -> packAccountUpdates(status, website, accountId) }
            .toMap()



        viewModelScope.launch {
            state = ViewModelState.Loading

            val websiteUpdates = packWebsiteUpdates()
            val accountsUpdates = packAccountsUpdates()

            val existingWebsiteId = checkIfRecordNotExisting(website)

            if (
                existingWebsiteId == null &&
                (website::address in website.updatedProperties || website.logoUrl == null)
            ) {
                getWebsiteLogoUseCase
                    .getWebsiteLogo(website.address)
                    .getOrNull()
                    ?.let { websiteUpdates[website::logoUrl.name] = it }
            }

            entityUseCase.updateEntity(
                id = existingWebsiteId ?: websiteId,
                type = EntityType.Website,
                updatesMap = websiteUpdates + accountsUpdates,
                encryptValue = { value: Any, encryption: Encrypt ->
                    when (value) {
                        is Account -> {
                            value.encrypt(encryption)
                            value
                        }

                        is String -> encryption(value)
                        else -> value
                    }
                },
            ) {
                viewModelScope.launch(Dispatchers.Main) {
                    it.exceptionOrNull()
                        ?.let(exceptionMessage::getMessage)
                        ?.let(::showSnackbar)
                    it.getOrNull()?.let { success() }
                    state = ViewModelState.Ready
                }
            }
        }
    }

    private suspend fun checkIfRecordNotExisting(website: ComposableWebsite): String? {
        return when {
            website::address in website.updatedProperties -> {
                entityUseCase.checkIfEntityRecordIsNew(entity = website.mapToUserData())
            }
            else -> null
        }
    }

    private fun packAccountUpdates(
        status: ChildStatus,
        website: ComposableWebsite,
        accountId: UID
    ): List<Pair<String, Any?>> {
        val path = "${website::accounts.name}/$accountId"

        return when (status) {
            ChildStatus.Created -> {
                val account = website.accounts
                    .first { it.uid == accountId }
                    .mapToUserData()

                return listOf(path to account)
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


    fun deleteWebsite(success: () -> Unit) {
        viewModelScope.launch {
            entityUseCase.deleteEntity(
                id = id,
                type = EntityType.Website,
                error = {
                    exceptionMessage.getMessage(it)?.let(::showSnackbar)
                    state = ViewModelState.Ready
                },
                success = success.also { state = ViewModelState.Ready }
            )
        }
    }


    fun copyText(
        text: String,
        context: Context,
        clipboardManager: ClipboardManager
    ) {
        state = ViewModelState.Loading
        viewModelScope.launch {
            try {
                clipboardManager.setText(AnnotatedString(text))
                state = ViewModelState.Ready
                showSnackbar(context.getString(R.string.copy_text_successful))
            } catch (exception: RuntimeException) {
                state = ViewModelState.Ready
                showSnackbar(context.getString(
                        R.string.copy_text_exception,
                        exception.localizedMessage,
                    ))
            }
        }
    }


    fun copyWebsite(
        context: Context,
        clipboardManager: ClipboardManager,
        website: Website = this.website.mapToUserData()
    ) {
        val dataString = website.convertToString(context)
        copyText(dataString, context, clipboardManager)
    }


    private fun Website.applyLogo(callback: (Website) -> Unit) {
        viewModelScope.launch {
            getWebsiteLogoUseCase
                .getWebsiteLogo(address)
                .getOrNull()
                ?.let { callback(copy(logoUrl = it)) }
                ?: callback(this@applyLogo)
        }
    }


    private fun insertWebsite(website: Website, newId: (Result<String>) -> Unit) {
        viewModelScope.launch {
            entityUseCase.insertEntity(website, newId)
        }
    }


    @AssistedFactory
    interface Factory {
        fun create(id: String): WebsiteViewModel
    }
}