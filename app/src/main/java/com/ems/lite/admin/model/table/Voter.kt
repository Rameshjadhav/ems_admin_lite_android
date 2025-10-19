package com.ems.lite.admin.model.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName

@Entity
class Voter() : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var _id: Int = 0

    @SerializedName("sr_no")
    var srNo: Long = 0

    @SerializedName("ac_no")
    var acNo: Long = 0

    @SerializedName("div_no")
    var divNo: Long = 0

    @SerializedName("village_no")
    var villageNo: Long = 0

    @SerializedName("booth_no")
    var boothNo: Long = 0

    @SerializedName("section_no")
    var sectionNo: Long = 0

    @SerializedName("section_name")
    var sectionName: String? = null

    @SerializedName("voter_no")
    var voterNo: Long = 0

    @SerializedName("voter_name")
    var voterName: String? = null

    @SerializedName("voter_name_eng")
    var voterNameEng: String? = null

    @SerializedName("voter_fname")
    var voterFName: String? = null

    @SerializedName("voter_mname")
    var voterMName: String? = null

    @SerializedName("voter_lname")
    var voterLName: String? = null

    @SerializedName("voter_fname_eng")
    var voterFNameEng: String? = null

    @SerializedName("voter_mname_eng")
    var voterMNameEng: String? = null

    @SerializedName("voter_lname_eng")
    var voterLNameEng: String? = null

    @SerializedName("house_no")
    var houseNo: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("outstation_address")
    var outstationAddress: String? = null

    @SerializedName("per_address")
    var perAddress: String? = null

    @SerializedName("bdate")
    var bDate: String? = null

    @SerializedName("mobile_no")
    var mobileNo: String? = null

    @SerializedName("whatsapp_no")
    var whatsAppNo: String? = null

    @SerializedName("sex")
    var sex: String? = null

    @SerializedName("age")
    var age: Int = 0

    @SerializedName("card_no")
    var cardNo: String? = null

    @SerializedName("cast_no")
    var castNo: Long = 0

    @SerializedName("is_dead")
    var dead: Int = 0

    @SerializedName("profession_no")
    var professionNo: Long = 0

    @SerializedName("designation_no")
    var designationNo: Long = 0

    @SerializedName("voted")
    var voted: Int = 0

    @SerializedName("is_updated")
    var updated: Int = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("ref_voter_no")
    var refVoterNo: String? = null

    @SerializedName("committee_designation")
    var committeeDesignation: String? = null

    @SerializedName("is_vip")
    var vip: Int = 0

    @SerializedName("voter_status_name")
    var voterStatusName: String? = null

    @SerializedName("religion_no")
    var religionNo: Long = 0

    @SerializedName("user_id")
    var userId: Long = 0

    @SerializedName("remark1")
    var remark1: String? = null

    @SerializedName("remark2")
    var remark2: String? = null

    @SerializedName("is_completed_family")
    var completedFamily:Int =0

    @SerializedName("family_head")
    var familyHead:Int =0

    constructor(parcel: Parcel) : this() {
        _id = parcel.readInt()
        srNo = parcel.readLong()
        acNo = parcel.readLong()
        divNo = parcel.readLong()
        villageNo = parcel.readLong()
        boothNo = parcel.readLong()
        sectionNo = parcel.readLong()
        sectionName = parcel.readString()
        voterNo = parcel.readLong()
        voterName = parcel.readString()
        voterNameEng = parcel.readString()
        voterFName = parcel.readString()
        voterMName = parcel.readString()
        voterLName = parcel.readString()
        voterFNameEng = parcel.readString()
        voterMNameEng = parcel.readString()
        voterLNameEng = parcel.readString()
        houseNo = parcel.readString()
        address = parcel.readString()
        outstationAddress = parcel.readString()
        perAddress = parcel.readString()
        bDate = parcel.readString()
        mobileNo = parcel.readString()
        whatsAppNo = parcel.readString()
        sex = parcel.readString()
        age = parcel.readInt()
        cardNo = parcel.readString()
        castNo = parcel.readLong()
        dead = parcel.readInt()
        professionNo = parcel.readLong()
        designationNo = parcel.readLong()
        voted = parcel.readInt()
        updated = parcel.readInt()
        message = parcel.readString()
        image = parcel.readString()
        refVoterNo = parcel.readString()
        committeeDesignation = parcel.readString()
        vip = parcel.readInt()
        voterStatusName = parcel.readString()
        religionNo = parcel.readLong()
        userId = parcel.readLong()
        remark1 = parcel.readString()
        remark2 = parcel.readString()
        completedFamily = parcel.readInt()
        familyHead = parcel.readInt()
    }

    override fun toString(): String {
        return getFullName()
    }

    fun getFullName(): String {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            voterNameEng ?: ""
        } else {
            voterName ?: ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(_id)
        parcel.writeLong(srNo)
        parcel.writeLong(acNo)
        parcel.writeLong(divNo)
        parcel.writeLong(villageNo)
        parcel.writeLong(boothNo)
        parcel.writeLong(sectionNo)
        parcel.writeString(sectionName)
        parcel.writeLong(voterNo)
        parcel.writeString(voterName)
        parcel.writeString(voterNameEng)
        parcel.writeString(voterFName)
        parcel.writeString(voterMName)
        parcel.writeString(voterLName)
        parcel.writeString(voterFNameEng)
        parcel.writeString(voterMNameEng)
        parcel.writeString(voterLNameEng)
        parcel.writeString(houseNo)
        parcel.writeString(address)
        parcel.writeString(outstationAddress)
        parcel.writeString(perAddress)
        parcel.writeString(bDate)
        parcel.writeString(mobileNo)
        parcel.writeString(whatsAppNo)
        parcel.writeString(sex)
        parcel.writeInt(age)
        parcel.writeString(cardNo)
        parcel.writeLong(castNo)
        parcel.writeInt(dead)
        parcel.writeLong(professionNo)
        parcel.writeLong(designationNo)
        parcel.writeInt(voted)
        parcel.writeInt(updated)
        parcel.writeString(message)
        parcel.writeString(image)
        parcel.writeString(refVoterNo)
        parcel.writeString(committeeDesignation)
        parcel.writeInt(vip)
        parcel.writeString(voterStatusName)
        parcel.writeLong(religionNo)
        parcel.writeLong(userId)
        parcel.writeString(remark1)
        parcel.writeString(remark2)
        parcel.writeInt(completedFamily)
        parcel.writeInt(familyHead)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Voter> {
        override fun createFromParcel(parcel: Parcel): Voter {
            return Voter(parcel)
        }

        override fun newArray(size: Int): Array<Voter?> {
            return arrayOfNulls(size)
        }
    }

}