package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Village
import com.google.gson.annotations.SerializedName

class VillageListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Village>? = null
}