package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Designation
import com.google.gson.annotations.SerializedName

class DesignationListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Designation>? = null
}