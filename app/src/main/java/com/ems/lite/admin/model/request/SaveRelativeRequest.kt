package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName

data class SaveRelativeRequest constructor(
    @SerializedName("rel_id") val relId: String?,
    @SerializedName("voter_card_no") val voterCardNo: String?,
    @SerializedName("tal_id") val talId: String?,
    @SerializedName("village_name") val villageName: String?,
    @SerializedName("relative_name") val relativeName: String?,
    @SerializedName("relative_number") val relativeNumber: String?,
    @SerializedName("head_relation") val headRelation: String?,
    @SerializedName("profession_no") val professionNo: Long?,
    @SerializedName("user_id") val userId: Int?
)