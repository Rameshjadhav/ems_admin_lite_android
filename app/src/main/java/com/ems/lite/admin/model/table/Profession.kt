package com.ems.lite.admin.model.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


@Entity
class Profession {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @SerializedName("profession_name_eng")
    var professionNameEng: String? = null

    @SerializedName("profession_name")
    var professionName: String? = null

    @SerializedName("profession_no")
    var professionNo: Long = 0

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString() && !professionNameEng.isNullOrEmpty()) {
            professionNameEng!!
        } else if (!professionName.isNullOrEmpty()) {
            professionName!!
        } else ""
    }
}





