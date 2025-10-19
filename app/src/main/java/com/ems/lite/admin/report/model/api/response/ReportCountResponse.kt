package com.ems.lite.admin.report.model.api.response

import com.ems.lite.admin.model.response.CommonResponse
import com.google.gson.annotations.SerializedName

class ReportCountResponse : CommonResponse() {

    @SerializedName("total_org_count")
    var totalOrgCount: Int = 0

    @SerializedName("total_off_count")
    var totalOffCount: Int = 0

    @SerializedName("total_dw_count")
    var totalDwCount: Int = 0

    @SerializedName("total_ben_count")
    var totalBenCount: Int = 0

    @SerializedName("total_issue_count")
    var totalIssueCount: Int = 0
}