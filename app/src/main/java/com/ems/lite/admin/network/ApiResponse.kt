package com.ems.lite.admin.network


import androidx.collection.ArrayMap
import com.ems.lite.admin.utils.Logger
import retrofit2.Response
import java.io.IOException
import java.util.Collections.emptyMap
import java.util.regex.Pattern

/**
 * Custom API-Response class
 * @param <T>
</T> */
class ApiResponse<T> {
    val code: Int
    val body: T?
    private val errorMessage: String?
    private val links: MutableMap<String, String>

    val isSuccessful: Boolean
        get() = code in 200..299

    val nextPage: Int?
        get() {
            val next = links[NEXT_LINK] ?: return null
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                return null
            }
            return try {
                Integer.parseInt(matcher.group(1)!!)
            } catch (ex: NumberFormatException) {
                Logger.d("cannot parse next page from %s", next)
                null
            }

        }

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message
        links = emptyMap()
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()!!.string()
                } catch (ignored: IOException) {
                    Logger.errorLog("error while parsing response")
                }

            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
        val linkHeader = response.headers()["link"]
        if (linkHeader == null) {
            links = emptyMap()
        } else {
            links = ArrayMap()
            val matcher = LINK_PATTERN.matcher(linkHeader)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
        }
    }

    companion object {
        /**
         * Regex pattern for identifying header link in a response.
         */
        private val LINK_PATTERN = Pattern
            .compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        /**
         * Regex pattern for checking if a given link is of a "page" type.
         */
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"
    }
}