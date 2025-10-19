package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class UpdatedVoterListRequest(
    @SerializedName("offset") var offset: Long,
)