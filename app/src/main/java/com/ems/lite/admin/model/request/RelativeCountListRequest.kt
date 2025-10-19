package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class RelativeCountListRequest constructor(
    @SerializedName("village_no") var villageNo: Long?,
    @SerializedName("booth_no") var boothNo: Long? = 0,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("keyword") val keyword: String?,
    @SerializedName("offset") val offset: Long,
)