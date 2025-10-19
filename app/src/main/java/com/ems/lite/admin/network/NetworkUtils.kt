package com.ems.lite.admin.network

import android.util.Base64
import androidx.annotation.Nullable
import com.ems.lite.admin.BuildConfig
import com.ems.lite.admin.utils.Logger
import java.util.*


/**
 * Created by Ramesh on 30/12/20.
 */
open class NetworkUtils {

    companion object {
        const val AUTHORIZATION = "Authorization"
        private const val CLIENT_ID_KEY = "client_id"
        private const val CLIENT_SECRET_KEY = "client_secret"
        private const val AUTH_CLIENT_ID = BuildConfig.CLIENT_ID
        private const val AUTH_CLIENT_SECRET = BuildConfig.CLIENT_SECRET
        const val APP_PLATFORM_USER_AGENT = "android_app"
        private val authParams: MutableMap<String, String> = HashMap()

        private val clientIdSecretKeyAuthParam: Map<String, String>
            get() {
                val hashMap = HashMap<String, String>()
                hashMap[CLIENT_ID_KEY] = AUTH_CLIENT_ID
                hashMap[CLIENT_SECRET_KEY] = AUTH_CLIENT_SECRET
                return hashMap
            }

        /**
         * Methods for handling auth params used by the Request classes.
         */
        fun putAuthParam(key: String, value: String) {
            putUserAgentParam(
                NetworkService.HEADER_USER_AGENT,
                APP_PLATFORM_USER_AGENT
            )
            authParams[key] = value
        }

        private fun putUserAgentParam(key: String, value: String) {
            authParams[key] = value
        }

        fun getAuthParams(): Map<String, String> {
            return authParams
        }

        fun clearAuthParams() {
            authParams.clear()
        }

        fun onBoardEncodeBase64Header(sData: String): String {
            val hashMap = clientIdSecretKeyAuthParam as HashMap<String, String>
            val cred = String.format(
                "%s:%s", hashMap[CLIENT_ID_KEY], hashMap[CLIENT_SECRET_KEY]
            )
            val auth = "Basic " + Base64.encodeToString(cred.toByteArray(), Base64.NO_WRAP)
            Logger.d("Header Basic: $auth")
            return auth
        }

        private fun isEmpty(@Nullable text: String?): Boolean {
            return text == null || text.isEmpty()
        }

        fun isNotEmpty(@Nullable text: String?): Boolean {
            return !isEmpty(text)
        }
    }
}
