package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Voter
import com.google.gson.annotations.SerializedName

class VoterListResponse : CommonResponse() {

    @SerializedName("shareImageUrl")
    var shareImageUrl: String? = null

    @SerializedName("list")
    var list: ArrayList<Voter>? = null

    @SerializedName("totalcount")
    var totalCount: Long = 0

    @SerializedName("limit")
    var limit: Long = 5000
}