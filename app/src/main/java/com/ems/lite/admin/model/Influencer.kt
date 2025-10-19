package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName

class Influencer() : Parcelable {
    @SerializedName("ac_no")
    var acNo: Long = 0

    @SerializedName("div_no")
    var divNo: Long = 0

    @SerializedName("village_no")
    var villageNo: Long = 0

    @SerializedName("booth_no")
    var boothNo: Long = 0

    @SerializedName("voter_name")
    var voterName: String? = null

    @SerializedName("voter_name_eng")
    var voterNameEng: String? = null

    @SerializedName("card_no")
    var cardNo: String? = null

    @SerializedName("mobile_no")
    var mobileNo: String? = null

    @SerializedName("whatsapp_no")
    var whatsAppNo: String? = null

    @SerializedName("booth_name")
    var boothName: String? = null

    @SerializedName("booth_name_eng")
    var boothNameEng: String? = null

    @SerializedName("ref_voter_no")
    var refVoterNo: String? = null

    var totalCount: Int = 0

    constructor(parcel: Parcel) : this() {
        acNo = parcel.readLong()
        divNo = parcel.readLong()
        villageNo = parcel.readLong()
        boothNo = parcel.readLong()
        voterName = parcel.readString()
        voterNameEng = parcel.readString()
        cardNo = parcel.readString()
        mobileNo = parcel.readString()
        whatsAppNo = parcel.readString()
        boothName = parcel.readString()
        boothNameEng = parcel.readString()
        refVoterNo = parcel.readString()
        totalCount = parcel.readInt()
    }


    fun getFullName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            voterNameEng
        } else {
            voterName
        }
    }

    fun getFullBoothName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            boothNameEng
        } else {
            boothName
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(acNo)
        parcel.writeLong(divNo)
        parcel.writeLong(villageNo)
        parcel.writeLong(boothNo)
        parcel.writeString(voterName)
        parcel.writeString(voterNameEng)
        parcel.writeString(cardNo)
        parcel.writeString(mobileNo)
        parcel.writeString(whatsAppNo)
        parcel.writeString(boothName)
        parcel.writeString(boothNameEng)
        parcel.writeString(refVoterNo)
        parcel.writeInt(totalCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Influencer> {
        override fun createFromParcel(parcel: Parcel): Influencer {
            return Influencer(parcel)
        }

        override fun newArray(size: Int): Array<Influencer?> {
            return arrayOfNulls(size)
        }
    }

}