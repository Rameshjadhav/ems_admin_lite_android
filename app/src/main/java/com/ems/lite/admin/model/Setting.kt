package com.ems.lite.admin.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Setting() : Parcelable {

    @SerializedName("_id")
    var id: Int = 0

    @SerializedName("voting_date")
    var votingDate: String? = null

    @SerializedName("voting_time")
    var votingTime: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("share_image")
    var shareImage: String? = null

    @SerializedName("print_image")
    var printImage: String? = null

    @SerializedName("mainbanner")
    var mainBanner: String? = null

    @SerializedName("village_no")
    var villageNo:Long =0

    var shareImageBitmap: Bitmap? = null
    var printImageBitmap: Bitmap? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        votingDate = parcel.readString()
        votingTime = parcel.readString()
        message = parcel.readString()
        shareImage = parcel.readString()
        printImage = parcel.readString()
        mainBanner = parcel.readString()
        villageNo = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(votingDate)
        parcel.writeString(votingTime)
        parcel.writeString(message)
        parcel.writeString(shareImage)
        parcel.writeString(printImage)
        parcel.writeString(mainBanner)
        parcel.writeLong(villageNo)
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