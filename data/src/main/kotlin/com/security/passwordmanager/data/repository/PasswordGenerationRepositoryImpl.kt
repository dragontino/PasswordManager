package com.security.passwordmanager.data.repository

import com.security.passwordmanager.domain.model.PasswordGenerationException
import com.security.passwordmanager.domain.model.PasswordLengthException
import com.security.passwordmanager.domain.model.PasswordParameters
import com.security.passwordmanager.domain.repository.PasswordGenerationRepository
import kotlinx.coroutines.delay
import kotlin.random.Random

class PasswordGenerationRepositoryImpl : PasswordGenerationRepository {

    private companion object {
        const val LowercaseLetters = "abcdefghijklmnopqrstuvwxyz"
        const val UppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        const val Digits = "0123456789"
    }


    override suspend fun generateStrongPassword(params: PasswordParameters): Result<String> {
        // TODO: 24.10.2023 переделать
        if (params.length < 4) {
            return Result.failure(PasswordLengthException)
        }

        val lowercaseLetters = if (params.useLowercase) LowercaseLetters else null
        val uppercaseLetters = if (params.useUppercase) UppercaseLetters else null
        val digits = if (params.useDigits) Digits else null

        val allCharacters = arrayOf(lowercaseLetters, uppercaseLetters, digits, params.specialCharacters)
            .filterNotNull()

        if (allCharacters.isEmpty()) {
            return Result.failure(PasswordGenerationException)
        }

        delay(1000)

        val resultString = buildString(params.length) {
            allCharacters.forEach {
                append(getRandomCharacter(it))
            }

            repeat(length - allCharacters.size) {
                append(getRandomCharacter(allCharacters.joinToString("")))
            }
        }.toList().shuffled().joinToString("")

        return Result.success(resultString)
    }


    private fun getRandomCharacter(characters: String): Char {
        val randomIndex = Random.nextInt(characters.length)
        return characters[randomIndex]
    }
}