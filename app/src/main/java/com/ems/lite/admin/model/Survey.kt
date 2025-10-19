package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName

class Survey() : Parcelable {

    @SerializedName("name")
    var name: String? = null

    @SerializedName("booth_name")
    var boothname: String? = null

    @SerializedName("booth_name_eng")
    var boothNameEng: String? = null

    @SerializedName("mobileCount")
    var mobileCount: Long = 0

    @SerializedName("casteCount")
    var casteCount: Long = 0

    @SerializedName("totalcount")
    var totalcount: Long = 0

    fun getBoothName(): String {
        return if (Prefs.lang == Enums.Language.en.toString() && !boothNameEng.isNullOrEmpty()) {
            boothNameEng ?: ""
        } else if (!boothname.isNullOrEmpty()) {
            boothname ?: ""
        } else {
            boothNameEng ?: ""
        }
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        boothname = parcel.readString()
        mobileCount = parcel.readLong()
        casteCount = parcel.readLong()
        totalcount = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(boothname)
        parcel.writeLong(mobileCount)
        parcel.writeLong(casteCount)
        parcel.writeLong(totalcount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Survey> {
        override fun createFromParcel(parcel: Parcel): Survey {
            return Survey(parcel)
        }

        override fun newArray(size: Int): Array<Survey?> {
            return arrayOfNulls(size)
        }
    }
}