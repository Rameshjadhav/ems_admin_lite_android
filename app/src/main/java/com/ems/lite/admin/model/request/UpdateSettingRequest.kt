package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class UpdateSettingRequest constructor(
    @SerializedName("user_id") var userId: Int?,
    @SerializedName("village_no") val villageNo:Long?,
    @SerializedName("voting_date") val votingDate:String?,
    @SerializedName("voting_time") val votingTime:String?,
    @SerializedName("message") val message:String?)