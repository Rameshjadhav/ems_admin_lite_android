package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Voter
import com.google.gson.annotations.SerializedName

class BannerResponse : CommonResponse() {

    @SerializedName("banner")
    var banner: String? = null

}