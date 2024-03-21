package com.security.passwordmanager.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.domain.model.PasswordParameters
import com.security.passwordmanager.domain.usecase.GeneratePasswordUseCase
import com.security.passwordmanager.util.colorize
import com.security.passwordmanager.view.theme.Orange
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class PasswordGenerationViewModel @Inject constructor(
    private val generatePasswordUseCase: GeneratePasswordUseCase
) : ViewModel() {

    companion object {
        private const val specialSymbols = "!@#\$%^&*—_+=;:.,/?\'\"\\|`~()[]{}<>"

        private val DigitColor = Color.Blue
        private val SpecialSymbolColor = Orange

        const val maxPasswordLength = 40
    }


    var state by mutableStateOf(ViewModelState.Ready)


    var password by mutableStateOf("")
        private set

    var passwordLength by mutableIntStateOf(16)

    var useUppercase by mutableStateOf(true)

    var useLowercase by mutableStateOf(true)

    var useDigits by mutableStateOf(true)

    var useSpecialChars by mutableStateOf(true)

    var showDialog by mutableStateOf(false)


    var colorizedPassword by mutableStateOf(AnnotatedString(""))


    var specialCharacters by mutableStateOf(specialSymbols)
        private set


    val mayResetSpecialSymbols get() = specialCharacters != specialSymbols


    init {
//        if (password.isBlank()) {
//            viewModelScope.launch { generatePassword() }
//        }

        snapshotFlow {
            arrayOf(
                passwordLength,
                useUppercase,
                useLowercase,
                useDigits,
                useSpecialChars,
                specialCharacters
            )
        }.map {
            generatePassword()
        }.launchIn(viewModelScope)
    }


    private suspend fun generatePassword() {
        state = ViewModelState.Loading
        colorizedPassword = AnnotatedString("")

        val params = PasswordParameters(
            length = passwordLength,
            useUppercase = useUppercase,
            useLowercase = useLowercase,
            useDigits = useDigits,
            specialCharacters = if (useSpecialChars) specialCharacters else null
        )

        val result = generatePasswordUseCase.generateStrongPassword(params)
        password = result.getOrNull() ?: ""

        colorizedPassword = when {
            password.isNotBlank() -> password.colorize { char ->
                when {
                    char.isDigit() -> DigitColor
                    char in specialSymbols -> SpecialSymbolColor
                    else -> Color.Unspecified
                }
            }
            else -> AnnotatedString("")
        }

        state = ViewModelState.Ready
    }


    fun regeneratePassword() {
        viewModelScope.launch {
            generatePassword()
        }
    }


    fun updateSpecialCharacters(newChars: String) {
        specialCharacters = newChars
            .groupingBy { it }
            .eachCount()
            .keys
            .filter { it in specialSymbols }
            .joinToString("")
    }


    fun showHistory() {

    }


    fun resetSpecialCharacters() {
        specialCharacters = specialSymbols
    }
}