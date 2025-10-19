package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.google.gson.annotations.SerializedName

class User() : Parcelable {

    @SerializedName("user_id", alternate = ["id"])
    var userId: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("mobileno")
    var mobileNo: String? = null

    @SerializedName("isactive", alternate = ["is_active"])
    var isActive: Int = 0

    @SerializedName("ac_no")
    var acNo: Long = 0

    @SerializedName("ac_name")
    var acName: String? = null

    @SerializedName("div_no")
    var divNo: Long = 0

    @SerializedName("division_name")
    var divName: String? = null

    @SerializedName("village_no")
    var villageNo: Long = 0

    @SerializedName("village_name")
    var villageName: String? = null

    @SerializedName("login_type")
    var type: String? = Enums.Type.SINGLE.toString()

    @SerializedName("booth_no")
    var boothNo: Long = 0

    @SerializedName("from")
    var from: Int = 0

    @SerializedName("to")
    var to: Int = 0

    @SerializedName("booths")
    var booths: String? = null

    @SerializedName("booth_name")
    var boothName: String? = null

    @SerializedName("card_no")
    var cardNo: String? = null

    constructor(parcel: Parcel) : this() {
        userId = parcel.readInt()
        name = parcel.readString()
        mobileNo = parcel.readString()
        isActive = parcel.readInt()
        acNo = parcel.readLong()
        acName = parcel.readString()
        divNo = parcel.readLong()
        divName = parcel.readString()
        villageNo = parcel.readLong()
        villageName = parcel.readString()
        type = parcel.readString()
        boothNo = parcel.readLong()
        from = parcel.readInt()
        to = parcel.readInt()
        booths = parcel.readString()
        boothName = parcel.readString()
        cardNo = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(name)
        parcel.writeString(mobileNo)
        parcel.writeInt(isActive)
        parcel.writeLong(acNo)
        parcel.writeString(acName)
        parcel.writeLong(divNo)
        parcel.writeString(divName)
        parcel.writeLong(villageNo)
        parcel.writeString(villageName)
        parcel.writeString(type)
        parcel.writeLong(boothNo)
        parcel.writeInt(from)
        parcel.writeInt(to)
        parcel.writeString(booths)
        parcel.writeString(boothName)
        parcel.writeString(cardNo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}