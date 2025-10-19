package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.Relative
import com.google.gson.annotations.SerializedName

class RelativeListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Relative>? = null

    @SerializedName("keyword")
    var keyword: String? = null

}