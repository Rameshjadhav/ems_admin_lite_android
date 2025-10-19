package com.ems.lite.admin.utils

import android.util.Log
import com.ems.lite.admin.BuildConfig

object Logger {
    private val TAG = Logger::class.java.simpleName

    fun i(msg: String) {
        if (BuildConfig.ENABLED) {
            Log.i(TAG, msg)
        }
    }

    fun i(TAG: String, msg: String) {
        if (BuildConfig.ENABLED) {
            Log.i(TAG, msg)
        }
    }

    fun d(TAG: String, msg: String) {
        if (BuildConfig.ENABLED) {
            Log.d(TAG, msg)
        }
    }

    fun d(msg: String) {
        if (BuildConfig.ENABLED) {
            Log.d(TAG, msg)
        }
    }

    fun e(TAG: String, msg: String) {
        if (BuildConfig.ENABLED) {
            Log.e(TAG, msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.ENABLED) {
            Log.e(TAG, msg)
        }
    }


    fun errorLog(message: String) {
        if (BuildConfig.ENABLED) {
            Log.e(TAG, message)
        }
    }

}
