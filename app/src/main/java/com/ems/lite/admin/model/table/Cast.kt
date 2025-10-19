package com.ems.lite.admin.model.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


@Entity
class Cast {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @SerializedName("cast_name_eng")
    var castNameEng: String? = null

    @SerializedName("cast_name")
    var castName: String? = null

    @SerializedName("cast_no")
    var castNo: Long = 0

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            if (!castNameEng.isNullOrEmpty())
                castNameEng!! else ""
        } else {
            if (!castName.isNullOrEmpty())
                castName!! else ""
        }
    }

    fun getName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            castNameEng
        } else {
            castName
        }
    }
}