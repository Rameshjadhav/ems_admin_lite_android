package com.ems.lite.admin.report.model.api.request

import com.google.gson.annotations.SerializedName

data class ReportCountRequest constructor(
    @SerializedName("user_id") val userId: Int?
)