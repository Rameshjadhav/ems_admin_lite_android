package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class DivisionListRequest constructor(
    @SerializedName("ac_no") var acNo: Long? = 0,
    @SerializedName("div_no") var divNo: Long? = 0,
)