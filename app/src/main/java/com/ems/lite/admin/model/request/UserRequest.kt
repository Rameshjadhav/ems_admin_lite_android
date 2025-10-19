package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("user_id") val userId:Int?,
)
