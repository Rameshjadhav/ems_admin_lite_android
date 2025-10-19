package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName


data class VoterDetailRequest(
    @SerializedName("_id") var _id: Int,
)