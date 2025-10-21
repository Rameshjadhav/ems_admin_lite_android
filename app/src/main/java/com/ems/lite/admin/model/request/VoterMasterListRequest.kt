package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class VoterMasterListRequest constructor(
    @SerializedName("user_id") var userId: Int?,
    @SerializedName("village_no") val villageNo:Long?,
    @SerializedName("booth_no") val boothNo:Long?,
    @SerializedName("from") val from:Int?,
    @SerializedName("to") val to:Int?,
    @SerializedName("booths") val booths:String?)