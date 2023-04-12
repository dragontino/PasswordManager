package com.security.passwordmanager.presentation.model.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.security.passwordmanager.R
import com.security.passwordmanager.data.model.dao.usersdata.Website

class ComposableWebsite(
    address: String = "",
    name: String = "",
    logoUrl: String? = null,
    accounts: List<ComposableAccount> = listOf(ComposableAccount())
) : ComposableData {

    private companion object {
        const val MaxUrlLength = 768
    }


    var address by mutableStateOf(address)
    var name by mutableStateOf(name)
    var logoUrl by mutableStateOf(logoUrl)
    val accounts = ComposableChildren(children = accounts).apply {
        if (isEmpty()) add(ComposableAccount())
    }

    var errorAddressMessage by mutableStateOf("")
    var errorNameMessage by mutableStateOf("")

    override val updatedProperties = mutableStateMapOf<String, Pair<String, String>>()


    override fun convertToDao() =
        Website(address, name, logoUrl, accounts.associate { it.uid.toString() to it.convertToDao() })


    override val haveErrors: Boolean
        get() = errorAddressMessage.isNotBlank() ||
                errorNameMessage.isNotBlank() ||
                accounts.any { it.haveErrors }


    fun updateAddressError(context: Context) {
        errorAddressMessage = when {
            address.isBlank() -> context.getString(R.string.empty_url)
            address.length > MaxUrlLength ->
                context.getString(R.string.too_long_url, MaxUrlLength)
            else -> ""
        }
    }

    fun updateNameError(context: Context) {
        errorNameMessage = if (name.isBlank())
            context.getString(R.string.empty_website_name)
        else ""
    }

    override fun updateErrors(context: Context) {
        updateAddressError(context)
        updateNameError(context)
        accounts.forEach {
            it.updateErrors(context)
        }
    }
}