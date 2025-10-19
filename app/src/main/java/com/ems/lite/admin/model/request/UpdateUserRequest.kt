package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("user_id") val userId:Int,
    @SerializedName("village_no") val villageNo:Long?,
    @SerializedName("booth_no") val boothNo:Long?,
    @SerializedName("from") val from:Int,
    @SerializedName("to") val to:Int,
    @SerializedName("booths") val booths:String?,
    @SerializedName("login_type") val type:String?,
    @SerializedName("isactive") val isActive:Int,
)
