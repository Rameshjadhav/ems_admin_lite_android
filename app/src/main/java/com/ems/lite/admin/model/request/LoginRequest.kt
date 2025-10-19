package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest constructor(
    @SerializedName("mobile_no") val phoneNumber: String?,
    @SerializedName("unlock_code") val unlockCode: String?
)