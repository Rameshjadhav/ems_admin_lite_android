package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Setting() : Parcelable {

    @SerializedName("_id")
    var id: Int = 0

    @SerializedName("settingimage")
    var settingImage: String? = null

    @SerializedName("votingdate")
    var votingDate: String? = null

    @SerializedName("votingtime")
    var votingTime: String? = null

    @SerializedName("massage")
    var massage: String? = null

    @SerializedName("shareimage")
    var shareImage: String? = null

    @SerializedName("printimage")
    var printImage: String? = null

    @SerializedName("mainbanner")
    var mainBanner: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        settingImage = parcel.readString()
        votingDate = parcel.readString()
        votingTime = parcel.readString()
        massage = parcel.readString()
        shareImage = parcel.readString()
        printImage = parcel.readString()
        mainBanner = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(settingImage)
        parcel.writeString(votingDate)
        parcel.writeString(votingTime)
        parcel.writeString(massage)
        parcel.writeString(shareImage)
        parcel.writeString(printImage)
        parcel.writeString(mainBanner)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Setting> {
        override fun createFromParcel(parcel: Parcel): Setting {
            return Setting(parcel)
        }

        override fun newArray(size: Int): Array<Setting?> {
            return arrayOfNulls(size)
        }
    }

}