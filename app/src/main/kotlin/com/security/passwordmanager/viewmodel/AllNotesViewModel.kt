package com.security.passwordmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.usecase.EntityUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AllNotesViewModel @Inject constructor(
    entityUseCase: EntityUseCase,
    settingsUseCase: SettingsUseCase,
    exceptionMessage: ExceptionMessage
) : EntityViewModel<DatabaseEntity>(entityUseCase, settingsUseCase, exceptionMessage) {

    var showFab by mutableStateOf(true)

    var notifyUserAboutFinishApp by mutableStateOf(true)


    init {
        entityUseCase
            .fetchEntities(
                EntityType.All,
                error = { exceptionMessage.getMessage(it)?.let(::showSnackbar) }
            )
            .onEach {
                _entities.value = it
                _listState.value = when {
                    it.isEmpty() -> ListState.Empty
                    else -> ListState.Stable
                }
            }
            .launchIn(viewModelScope)
    }


    suspend fun refreshData() {
        _listState.value = ListState.Loading
        delay(100)

        entityUseCase.getEntities(
            EntityType.All,
            error = { exception ->
                exceptionMessage.getMessage(exception)?.let(::showSnackbar)
                _listState.value = when {
                    entities.value.isEmpty() -> ListState.Empty
                    else -> ListState.Stable
                }
            },
            success = {
                _entities.value = it
                _listState.value = when {
                    it.isEmpty() -> ListState.Empty
                    else -> ListState.Stable
                }
            }
        )
    }
}