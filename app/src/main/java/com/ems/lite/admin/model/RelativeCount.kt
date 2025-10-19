package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


class RelativeCount() :Parcelable{

    @SerializedName("voter_card_no")
    var voterCardNo: String? = null

    @SerializedName("voter_name")
    var voterName: String? = null

    @SerializedName("voter_name_eng")
    var voterNameEng: String? = null

    @SerializedName("relative_count")
    var relativeCount: Int = 0

    constructor(parcel: Parcel) : this() {
        voterCardNo = parcel.readString()
        voterName = parcel.readString()
        voterNameEng = parcel.readString()
        relativeCount = parcel.readInt()
    }

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            if (!voterNameEng.isNullOrEmpty())
                voterNameEng!! else ""
        } else {
            if (!voterName.isNullOrEmpty())
                voterName!! else ""
        }
    }
    fun getDisplayName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            voterNameEng
        } else {
            voterName
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(voterCardNo)
        parcel.writeString(voterName)
        parcel.writeString(voterNameEng)
        parcel.writeInt(relativeCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RelativeCount> {
        override fun createFromParcel(parcel: Parcel): RelativeCount {
            return RelativeCount(parcel)
        }

        override fun newArray(size: Int): Array<RelativeCount?> {
            return arrayOfNulls(size)
        }
    }
}