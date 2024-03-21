package com.security.passwordmanager.viewmodel

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.domain.model.ExceptionMessage
import com.security.passwordmanager.domain.model.entity.DatabaseEntity
import com.security.passwordmanager.domain.model.entity.EntityType
import com.security.passwordmanager.domain.usecase.EntityUseCase
import com.security.passwordmanager.domain.usecase.SettingsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchViewModel @AssistedInject constructor(
    entityUseCase: EntityUseCase,
    settingsUseCase: SettingsUseCase,
    exceptionMessage: ExceptionMessage,
    @Assisted private val entityType: EntityType
) : EntityViewModel<DatabaseEntity>(entityUseCase, settingsUseCase, exceptionMessage) {

    val query = mutableStateOf("")

    init {
        snapshotFlow { query.value }
            .onEach {
                delay(50)
                _listState.value = ListState.Loading
                delay(300)

                entityUseCase.getEntities(
                    entityType,
                    query = it,
                    error = { exception ->
                        exceptionMessage.getMessage(exception)?.let(::showSnackbar)
                        _listState.value = ListState.Stable
                    },
                    success = { value ->
                        _entities.value = value
                        openedItemId.value = when (value.size) {
                            1 -> value.keys.firstOrNull()
                            else -> null
                        }

                        _listState.value = when {
                            value.isEmpty() -> ListState.Empty
                            else -> ListState.Stable
                        }
                    },
                )
            }
            .launchIn(viewModelScope)
    }


    suspend fun refreshData() {
        _listState.value = ListState.Loading
        delay(100)

        entityUseCase.getEntities(
            entityType,
            query = query.value,
            error = {
                exceptionMessage.getMessage(it)?.let(::showSnackbar)
                _listState.value = ListState.Stable
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


    @Composable
    override fun screenShape() = CutCornerShape(0)

    @AssistedFactory
    interface Factory {
        fun create(type: EntityType): SearchViewModel
    }
}