package com.security.passwordmanager.viewmodel

import android.content.Context
import android.content.Intent
import android.webkit.URLUtil
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
import com.security.passwordmanager.domain.R
import com.security.passwordmanager.domain.model.Account
import com.security.passwordmanager.domain.model.BankCard
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.Settings
import com.security.passwordmanager.domain.model.UserData
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.usecase.EntityUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


abstract class EntityViewModel<D : DatabaseEntity>(
    protected val entityUseCase: EntityUseCase,
    settingsUseCase: SettingsUseCase,
    protected val exceptionMessage: ExceptionMessage
) : EventsViewModel() {

    protected val innerListState = mutableStateOf(ListState.Loading)
    val listState = derivedStateOf { innerListState.value }

    private val _settings = mutableStateOf(Settings())
    val settings = derivedStateOf { _settings.value }

    protected val innerEntities = mutableStateOf<Map<String, D>>(mapOf())
    val entities = derivedStateOf { innerEntities.value }

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
            URLUtil.isValidUrl(urlString) -> Intent(Intent.ACTION_VIEW, urlString.toUri())
            else -> null
        }
    }


    fun deleteEntity(
        id: String,
        entityType: EntityType,
        success: () -> Unit
    ) {
        viewModelScope.launch {
            innerListState.value = ListState.Loading

            entityUseCase.deleteEntity(
                id = id,
                type = entityType,
                error = {
                    exceptionMessage.getMessage(it)?.let(::showSnackbar)
                    innerListState.value = ListState.Stable
                },
                success = {
                    success()
                    innerListState.value = when {
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
        innerListState.value = ListState.Loading
        viewModelScope.launch {
            try {
                clipboardManager.setText(AnnotatedString(text))
                innerListState.value = ListState.Stable
                showSnackbar(context.getString(R.string.copy_text_successful))
            } catch (exception: RuntimeException) {
                innerListState.value = ListState.Stable
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
            is Account -> data.convertToString(context.resources)
            is BankCard -> data.convertToString(context.resources)
            else -> (data as DatabaseEntity).convertToString(context.resources)
        }
        copyText(dataString, context, clipboardManager)
    }
}