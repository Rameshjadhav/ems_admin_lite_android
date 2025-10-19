package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Cast
import com.google.gson.annotations.SerializedName

class CastListResponse : CommonResponse() {
    @SerializedName("list")
    var castList: ArrayList<Cast>? = null
}