package com.security.passwordmanager.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.webkit.URLUtil

fun Context.checkNetworkConnection(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val capabilities = connectivityManager
        .getNetworkCapabilities(connectivityManager.activeNetwork)

    return capabilities != null
}


fun String.isValidUrl() = URLUtil.isValidUrl(this)

