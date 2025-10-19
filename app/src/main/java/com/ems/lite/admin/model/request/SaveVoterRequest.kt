package com.ems.lite.admin.model.request

import com.google.gson.annotations.SerializedName


data class SaveVoterRequest constructor(
    var _id: Int = 0,
    @SerializedName("mobile_no") var mobileNo: String? = null,
    @SerializedName("per_address") var perAddress: String? = null,
    @SerializedName("bdate") var bDate: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("house_no") var houseNo: String? = null,
    @SerializedName("outstation_address") var outstationAddress: String? = null,
    @SerializedName("cast_no") var castNo: Long = 0,
    @SerializedName("voter_status_name") var voterStatusName: String? = null,
    @SerializedName("profession_no") var professionNo: Long = 0,
    @SerializedName("designation_no") var designationNo: Long = 0,
    @SerializedName("committee_designation") var committeeDesignation: String? = null,
    @SerializedName("religion_no") var religionNo: Long = 0,
    @SerializedName("is_vip") var isVip: Int = 0,
    @SerializedName("user_id") var userId: Long? = null,
    @SerializedName("remark1") var remark1: String? = null,
    @SerializedName("remark2") var remark2: String? = null,
    @SerializedName("is_updated") var isUpdated: Int = 0,
    @SerializedName("ref_voter_no") var refVoterNo: String? = null,
    @SerializedName("is_dead") var isDead: Int = 0
)
