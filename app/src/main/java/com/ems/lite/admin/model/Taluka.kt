package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Taluka() : Parcelable {

    @SerializedName("tal_id")
    var talId: String? = null

    @SerializedName("taluka_name")
    var talukaName: String? = null

    constructor(parcel: Parcel) : this() {
        talId = parcel.readString()
        talukaName = parcel.readString()
    }

    override fun toString(): String {
        return talukaName ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(talId)
        parcel.writeString(talukaName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Taluka> {
        override fun createFromParcel(parcel: Parcel): Taluka {
            return Taluka(parcel)
        }

        override fun newArray(size: Int): Array<Taluka?> {
            return arrayOfNulls(size)
        }
    }


}