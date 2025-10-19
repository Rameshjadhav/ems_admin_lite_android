package com.ems.lite.admin.model.request

import com.ems.lite.admin.model.table.Voter
import com.google.gson.annotations.SerializedName



class SaveVoterListRequest  {

    @SerializedName("Voter")
    var voterList: ArrayList<Voter>? = null

}