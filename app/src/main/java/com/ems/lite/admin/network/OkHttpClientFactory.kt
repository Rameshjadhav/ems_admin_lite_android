package com.ems.lite.admin.network

import com.ems.lite.admin.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class OkHttpClientFactory {
    private var okHttpClient: OkHttpClient? = null

    fun getOkHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = instantiateOkHttpClient()
        }
        return okHttpClient!!
    }

    private fun instantiateOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.readTimeout(360, TimeUnit.SECONDS)
        builder.connectTimeout(360, TimeUnit.SECONDS)
        // Custom Dns to avoid ipv6 incompatibility with some Android devices
        //  builder.dns(new PreferIpv4Dns());
        builder.addInterceptor { chain ->
            //this is where we will add whatever we want to our request headers.
            val basicRequest = chain.request()
            val requestBuilder = basicRequest.newBuilder()
            requestBuilder.url(Url.getBaseUrl(basicRequest.url.toString()))
            /**
             * Option Header
             */

            /**
             * Option Header
             */
            if (NetworkUtils.isNotEmpty(basicRequest.header(NetworkService.HEADER_BEAR))) {
                // fall back to basic if there is no bearer token
                val authorizationValue =
                    if (NetworkUtils.getAuthParams().containsKey(NetworkUtils.AUTHORIZATION))
                        NetworkUtils.getAuthParams()[NetworkUtils.AUTHORIZATION]
                    else
                        NetworkUtils.onBoardEncodeBase64Header(":")
                requestBuilder.addHeader(NetworkService.HEADER_AUTHORIZATION, authorizationValue!!)
                requestBuilder.removeHeader(NetworkService.HEADER_BEAR)
            } else if (NetworkUtils.isNotEmpty(basicRequest.header(NetworkService.HEADER_BASIC))) {
                requestBuilder.addHeader(
                    NetworkService.HEADER_AUTHORIZATION, NetworkUtils.onBoardEncodeBase64Header(":")
                )
                requestBuilder.removeHeader(NetworkService.HEADER_BASIC)
            } else if (NetworkUtils.isNotEmpty(basicRequest.header(NetworkService.HEADER_OPTION))) {
                //Will improve
//                val oAuthToken = SharedPreferenceManager.oAuthToken
//                if (oAuthToken != null) {
//                    requestBuilder.addHeader(
//                        NetworkService.HEADER_AUTHORIZATION,
//                        (NetworkUtils.getAuthParams()[NetworkUtils.AUTHORIZATION]!!)
//                    )
//                    requestBuilder.removeHeader(NetworkService.HEADER_OPTION)
//                } else {
                    requestBuilder.addHeader(
                        NetworkService.HEADER_AUTHORIZATION,
                        NetworkUtils.onBoardEncodeBase64Header(":")
                    )
                    requestBuilder.removeHeader(NetworkService.HEADER_OPTION)
//                }
            }

            /**
             * Default Header
             */

            /**
             * Default Header
             */
            requestBuilder.addHeader(
                NetworkService.HEADER_USER_AGENT, NetworkUtils.APP_PLATFORM_USER_AGENT
            )
            requestBuilder.addHeader(NetworkService.HEADER_APP_VERSION, BuildConfig.API_VERSION)
            requestBuilder.addHeader(
                NetworkService.HEADER_APP_VERSION_NAME, BuildConfig.APP_VERSION
            )
            requestBuilder.addHeader(NetworkService.HEADER_API_VERSION, BuildConfig.API_VERSION)
//            requestBuilder.addHeader("x-Localization", SharedPreferenceManager.appLanguage)
            requestBuilder.addHeader("Accept", "application/json")
            chain.proceed(requestBuilder.build())
        }
        if (BuildConfig.ENABLED) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        return builder.build()
    }
}
