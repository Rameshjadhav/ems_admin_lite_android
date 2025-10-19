package com.ems.lite.admin.model.response

import com.ems.lite.admin.model.User
import com.google.gson.annotations.SerializedName

class LoginResponse : CommonResponse() {

    @SerializedName("User", alternate = ["user"])
    var user: User? = null
}