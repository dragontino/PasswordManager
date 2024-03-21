package com.security.passwordmanager.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.R
import com.security.passwordmanager.data.util.isValidUrl
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.UserData
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.model.settings.Settings
import com.security.passwordmanager.domain.usecase.EntityUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import com.security.passwordmanager.model.AccountMapper.convertToString
import com.security.passwordmanager.model.BankCardMapper.convertToString
import com.security.passwordmanager.model.convertToString
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


abstract class EntityViewModel<D : DatabaseEntity>(
    protected val entityUseCase: EntityUseCase,
    settingsUseCase: SettingsUseCase,
    protected val exceptionMessage: ExceptionMessage
) : EventsViewModel() {

    protected val _listState = mutableStateOf(ListState.Loading)
    val listState = derivedStateOf { _listState.value }

    private val _settings = mutableStateOf(Settings())
    val settings = derivedStateOf { _settings.value }

    protected val _entities = mutableStateOf<Map<String, D>>(mapOf())
    val entities = derivedStateOf { _entities.value }

    val openedItemId = mutableStateOf<String?>(null)


    init {
        settingsUseCase.fetchSettings()
            .onEach { _settings.value = it }
            .launchIn(viewModelScope)
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


    fun deleteEntity(
        id: String,
        entityType: EntityType,
        success: () -> Unit
    ) {
        viewModelScope.launch {
            _listState.value = ListState.Loading

            entityUseCase.deleteEntity(
                id = id,
                type = entityType,
                error = {
                    exceptionMessage.getMessage(it)?.let(::showSnackbar)
                    _listState.value = ListState.Stable
                },
                success = {
                    success()
                    _listState.value = when {
                        entities.value.isEmpty() -> ListState.Empty
                        else -> ListState.Stable
                    }
                }
            )
        }
    }


    fun copyText(
        text: String,
        context: Context,
        clipboardManager: ClipboardManager
    ) {
        _listState.value = ListState.Loading
        viewModelScope.launch {
            try {
                clipboardManager.setText(AnnotatedString(text))
                _listState.value = ListState.Stable
                showSnackbar(context.getString(R.string.copy_text_successful))
            } catch (exception: RuntimeException) {
                _listState.value = ListState.Stable
                showSnackbar(context.getString(
                        R.string.copy_text_exception,
                        exception.localizedMessage
                    ))
            }
        }
    }


    fun copyData(
        data: UserData,
        context: Context,
        clipboardManager: ClipboardManager
    ) {
        val dataString = when (data) {
            is Account -> data.convertToString(context)
            is BankCard -> data.convertToString(context)
            else -> (data as DatabaseEntity).convertToString(context)
        }
        copyText(dataString, context, clipboardManager)
    }
}