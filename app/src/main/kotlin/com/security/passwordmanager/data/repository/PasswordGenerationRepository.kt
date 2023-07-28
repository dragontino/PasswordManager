package com.security.passwordmanager.data.repository

import android.content.Context
import com.security.passwordmanager.R
import com.security.passwordmanager.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class PasswordGenerationRepository(private val context: Context) {

    private companion object {
        const val LowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
        const val UppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val Digits = "0123456789"
    }


    suspend fun generateStrongPassword(
        length: Int,
        useUppercase: Boolean,
        useLowercase: Boolean,
        useDigits: Boolean,
        specialCharacters: String?
    ): Result<String> = withContext(Dispatchers.Default) {

        if (length < 4) {
            return@withContext Result.Error(
                Exception(context.getString(R.string.password_length_exception))
            )
        }

        val lowercaseLetters = if (useLowercase) LowercaseLetters else null
        val uppercaseLetters = if (useUppercase) UppercaseLetters else null
        val digits = if (useDigits) Digits else null

        val allCharacters = arrayOf(lowercaseLetters, uppercaseLetters, digits, specialCharacters)
            .filterNotNull()

        if (allCharacters.isEmpty()) {
            return@withContext Result.Error(
                Exception(context.getString(R.string.password_generation_exception))
            )
        }

        delay(1000)

        val resultString = buildString(length) {
            allCharacters.forEach {
                append(getRandomCharacter(it))
            }

            repeat(length - allCharacters.size) {
                append(getRandomCharacter(allCharacters.joinToString("")))
            }
        }.toList().shuffled().joinToString("")

        return@withContext Result.Success(resultString)
    }


    private fun getRandomCharacter(characters: String): Char {
        val randomIndex = Random.nextInt(characters.length)
        return characters[randomIndex]
    }
}