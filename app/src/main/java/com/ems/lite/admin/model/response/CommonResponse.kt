package com.ems.lite.admin.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by admin on 9/6/2020.
 */
open class CommonResponse : ResponseStatus() {

    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("user_found")
    var userFound = true

    @SerializedName("next_offset")
    var nextOffset: Long = 0

    var status: String? = null

    var error: Error? = null
}