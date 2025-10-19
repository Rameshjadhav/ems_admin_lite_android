package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class RelativeListRequest constructor(
    @SerializedName("voter_card_no") val voterCardNo: String?,
    @SerializedName("keyword") val keyword: String?,
    @SerializedName("offset") val offset: Long,
)