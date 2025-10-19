package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.Taluka
import com.google.gson.annotations.SerializedName

class TalukaListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Taluka>? = null
}