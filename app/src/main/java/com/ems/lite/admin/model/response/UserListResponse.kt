package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.User
import com.google.gson.annotations.SerializedName

class UserListResponse : CommonResponse() {

    @SerializedName("list")
    var list: ArrayList<User>? = null

    @SerializedName("keyword")
    var keyword: String? = null
}