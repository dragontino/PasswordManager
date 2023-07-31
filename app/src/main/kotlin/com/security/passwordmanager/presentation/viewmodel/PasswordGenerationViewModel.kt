package com.security.passwordmanager.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.security.passwordmanager.colorize
import com.security.passwordmanager.data.Result
import com.security.passwordmanager.data.repository.PasswordGenerationRepository
import com.security.passwordmanager.presentation.view.theme.Orange
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PasswordGenerationViewModel(
    private val repository: PasswordGenerationRepository
) : ViewModel() {

    companion object {
        private const val specialSymbols = "!@#\$%^&*—_+=;:.,/?\'\"\\|`~()[]{}<>"

        private val DigitColor = Color.Blue
        private val SpecialSymbolColor = Orange

        const val maxPasswordLength = 40
    }


    enum class State {
        Loading,
        Ready
    }


    var state by mutableStateOf(State.Ready)


    var password by mutableStateOf("")
        private set

    var passwordLength by mutableStateOf(16)

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
        state = State.Loading
        colorizedPassword = AnnotatedString("")

        val result = repository.generateStrongPassword(
            length = passwordLength,
            useUppercase = useUppercase,
            useLowercase = useLowercase,
            useDigits = useDigits,
            specialCharacters = if (useSpecialChars) specialCharacters else null
        )

        password = when (result) {
            is Result.Error -> result.exception.message ?: ""
            is Result.Loading -> ""
            is Result.Success -> result.data
        }

        colorizedPassword = when (result) {
            is Result.Success -> password.colorize { char ->
                when {
                    char.isDigit() -> DigitColor
                    char in specialSymbols -> SpecialSymbolColor
                    else -> Color.Unspecified
                }
            }
            else -> AnnotatedString("")
        }

        state = State.Ready
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