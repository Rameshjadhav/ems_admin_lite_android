package com.ems.lite.admin.app

import android.app.Application
import com.ems.lite.admin.network.OkHttpClientFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    companion object {
        var instance: MyApplication? = null
        var CHANNEL_ID = "FetchingVoterService"
        private var okHttpClientFactory: OkHttpClientFactory? = null
        fun getOkHttpClientFactory(): OkHttpClientFactory {
            if (okHttpClientFactory == null) {
                okHttpClientFactory = OkHttpClientFactory()
            }
            return okHttpClientFactory!!
        }
    }
//    private var connectivityReceiver: ConnectivityReceiver? = null
//
//    private fun getConnectivityReceiver(): ConnectivityReceiver? {
//        if (connectivityReceiver == null) connectivityReceiver = ConnectivityReceiver()
//        return connectivityReceiver
//    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}