package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Relative() : Parcelable {

    @SerializedName("rel_id")
    var relId: String? = null

    @SerializedName("village_name")
    var villageName: String? = null

    @SerializedName("relative_name")
    var relativeName: String? = null

    @SerializedName("relative_number")
    var relativeNumber: String? = null

    @SerializedName("head_relation")
    var headRelation: String? = null

    @SerializedName("voter_card_no")
    var voterCardNo: String? = null

    @SerializedName("tal_id")
    var talId: String? = null

    @SerializedName("taluka_name")
    var talukaName: String? = null

    @SerializedName("profession_no")
    var professionNo: Long = 0

    @SerializedName("profession_name")
    var professionName: String? = null

    @SerializedName("user_id")
    var userId: Long = 0

    @SerializedName("name")
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        relId = parcel.readString()
        villageName = parcel.readString()
        relativeName = parcel.readString()
        relativeNumber = parcel.readString()
        headRelation = parcel.readString()
        voterCardNo = parcel.readString()
        talId = parcel.readString()
        talukaName = parcel.readString()
        professionNo = parcel.readLong()
        professionName = parcel.readString()
        userId = parcel.readLong()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(relId)
        parcel.writeString(villageName)
        parcel.writeString(relativeName)
        parcel.writeString(relativeNumber)
        parcel.writeString(headRelation)
        parcel.writeString(voterCardNo)
        parcel.writeString(talId)
        parcel.writeString(talukaName)
        parcel.writeLong(professionNo)
        parcel.writeString(professionName)
        parcel.writeLong(userId)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Relative> {
        override fun createFromParcel(parcel: Parcel): Relative {
            return Relative(parcel)
        }

        override fun newArray(size: Int): Array<Relative?> {
            return arrayOfNulls(size)
        }
    }


}