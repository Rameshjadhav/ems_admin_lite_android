package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Voter
import com.google.gson.annotations.SerializedName

class VoterDetailResponse : CommonResponse() {

    @SerializedName("list")
    var voter: Voter? = null
}