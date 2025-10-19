package com.ems.lite.admin.model.request

import com.ems.lite.admin.model.response.CommonResponse
import com.ems.lite.admin.model.table.Voter
import com.google.gson.annotations.SerializedName

class VoterListResponse : CommonResponse() {

    @SerializedName("Voter")
    var voterList: ArrayList<Voter>? = null

    @SerializedName("shareImageUrl")
    var shareImageUrl: String? = null

    @SerializedName("list")
    var list: ArrayList<Voter>? = null

    @SerializedName("totalcount")
    var totalCount: Long = 0

    @SerializedName("limit")
    var limit: Long = 5000

    @SerializedName("keyword")
    var keyword: String? = null

    @SerializedName("count")
    var count: Long = 0
}