package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Religion
import com.google.gson.annotations.SerializedName

class ReligionListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Religion>? = null
}