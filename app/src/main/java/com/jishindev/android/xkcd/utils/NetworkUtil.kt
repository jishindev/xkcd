package com.jishindev.android.xkcd.utils

import android.content.Context
import android.net.ConnectivityManager

fun Context.isConnected(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = connectivityManager?.activeNetworkInfo
    return networkInfo?.isConnected ?: false
}