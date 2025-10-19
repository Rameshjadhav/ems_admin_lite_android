package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class VoterListRequest(
    @SerializedName("village_no") var villageNo: Long?,
    @SerializedName("booth_no") var boothNo: Long?,
    @SerializedName("vote_type") var voteType: String?,
    @SerializedName("color") var color: String?,
    @SerializedName("offset") var offset: Long,
)