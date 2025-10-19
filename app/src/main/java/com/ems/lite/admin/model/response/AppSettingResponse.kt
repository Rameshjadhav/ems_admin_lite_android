package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.Setting
import com.ems.lite.admin.model.table.Voter
import com.google.gson.annotations.SerializedName

class AppSettingResponse : CommonResponse() {

    @SerializedName("info")
    var info: Setting? = null

}