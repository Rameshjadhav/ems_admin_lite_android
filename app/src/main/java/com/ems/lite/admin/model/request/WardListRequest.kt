package com.ems.lite.admin.model.request

import com.ems.lite.admin.model.table.Village
import com.google.gson.annotations.SerializedName



class WardListRequest  {

    @SerializedName("Ward")
    var villageList: ArrayList<Village>? = null

}