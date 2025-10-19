package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.table.Profession
import com.google.gson.annotations.SerializedName

class ProfessionListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<Profession>? = null


}