package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Booth
import com.google.gson.annotations.SerializedName

class BoothListResponse : CommonResponse() {

    @SerializedName("Booth")
    var boothList: ArrayList<Booth>? = null

    @SerializedName("list")
    var list: ArrayList<Booth>? = null
}