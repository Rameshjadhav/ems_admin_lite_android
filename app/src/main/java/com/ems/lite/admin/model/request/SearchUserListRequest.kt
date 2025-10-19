package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class SearchUserListRequest(
    @SerializedName("keyword") var keyword: String?,
    @SerializedName("offset") var offset: Long
)