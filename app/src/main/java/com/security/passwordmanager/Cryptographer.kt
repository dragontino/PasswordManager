package com.security.passwordmanager

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Secure

class Cryptographer(val context: Context) {

    companion object {
        private const val EXTRA_LETTER = 198.toChar()
        private const val SIZE = 13
        private val keyTable = Array(SIZE) { CharArray(SIZE) }
    }

    init {
        fillData()
    }

    fun encrypt(defaultString: String) = crypt(defaultString, 1)

    fun decrypt(defaultString: String) = crypt(defaultString, -1)



    private fun crypt(defaultString: String, next : Int) : String {

        val cryptString = StringBuilder(defaultString)
        var elem = 0

        while (elem < cryptString.length) {
            val current = cryptString[elem]
            var future =
                    if (elem + 1 >= cryptString.length) ' '
                    else cryptString[elem + 1]

            if (next == 1) {
                if (cryptString.length % 2 != 0 && elem == cryptString.length - 1)
                    cryptString.append(EXTRA_LETTER)
                else if (current == cryptString[elem + 1])
                    cryptString.insert(elem + 1, EXTRA_LETTER)

                future = cryptString[elem + 1]
            }

            val first = getIndex(current.toString()) ?: return ""
            val second = getIndex(future.toString()) ?: return ""

            when {
                first[0] == second[0] -> {
                    first[1] = Math.floorMod(first[1] + next, SIZE)
                    second[1] = Math.floorMod(second[1] + next, SIZE)
                }
                first[1] == second[1] -> {
                    first[0] = Math.floorMod(first[0] + next, SIZE)
                    second[0] = Math.floorMod(second[0] + next, SIZE)
                }
                else -> {
                    val buff: Int = first[1]
                    first[1] = second[1]
                    second[1] = buff
                }
            }
            cryptString[elem] = keyTable[first[0]][first[1]]
            cryptString[elem + 1] = keyTable[second[0]][second[1]]

            elem += 2
        }

        if (next == -1) {
            elem = 0
            while (elem < cryptString.length) {
                if (cryptString[elem] == EXTRA_LETTER) cryptString.deleteCharAt(elem) else elem++
            }
        }

        return cryptString.toString()
    }



    @SuppressLint("HardwareIds")
    private fun fillData() {
        val key = checkedKey(
                Secure.getString(context.contentResolver, Secure.ANDROID_ID))
        var code = 32

        for (index in 0 until SIZE * SIZE) {
            val i = index / SIZE
            val j = index % SIZE
            if (index < key.length) keyTable[i][j] = key[index]
            else {
                while (key.contains(code.toChar())) code = getNextCode(code)
                keyTable[i][j] = code.toChar()
                code = getNextCode(code)
            }
        }
    }


    private fun getNextCode(currentCode: Int) = when (currentCode) {
        126 -> 163
        163 -> 8470
        8470 -> 167
        167 -> 177
        177 -> 214
        214 -> 223
        223 -> 230
        230 -> 1025
        1025 -> 1040
        1103 -> 1105
        1105 -> EXTRA_LETTER.code
        else -> currentCode + 1
    }


    private fun getIndex(element : String) : Pair<Int, Int>? {
        for (i in 0 until SIZE) {
            val j = keyTable[i].toString().indexOf(element)

            if (j > -1) return Pair(i, j)
        }

        return null
    }

    private fun checkedKey(key: String): String {
        val builder = StringBuilder(key)
        var index = 0

        while (index < builder.length) {
            val elem = builder[index].toString()
            if (builder.substring(0, index).contains(elem)) {
                builder.deleteCharAt(index)
                continue
            }
            index++
        }
        return builder.toString()
    }
}