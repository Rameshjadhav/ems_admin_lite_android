package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.RelativeCount
import com.google.gson.annotations.SerializedName

class RelativeCountListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<RelativeCount>? = null

    @SerializedName("keyword")
    var keyword: String? = null

}