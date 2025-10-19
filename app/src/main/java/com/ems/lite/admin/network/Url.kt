package com.ems.lite.admin.network

import com.ems.lite.admin.BuildConfig
import com.ems.lite.admin.utils.Prefs

internal object Url {
    //    var HOST =
//        BuildConfig.BASE_URL_PROTOCOL.toLowerCase(Locale.ENGLISH) + "://" + BuildConfig.BASE_URL + "/"
    const val HOST = BuildConfig.BASE_URL + "/"
    const val API = HOST
    fun getBaseUrl(url: String): String {
        return if (!Prefs.password.isNullOrEmpty() && !url.contains(Prefs.password!!)) {
            url.replace(HOST, HOST + "MH${Prefs.password}/")
        } else
            url
    }
}