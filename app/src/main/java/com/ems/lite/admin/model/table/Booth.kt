package com.ems.lite.admin.model.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


@Entity
class Booth() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @SerializedName("ac_no")
    var acNo: Long = 0

    @SerializedName("div_no")
    var divNo: Long = 0

    @SerializedName("village_no")
    var villageNo: Long = 0

    @SerializedName("booth_no")
    var boothNo: Long = 0

    @SerializedName("booth_name")
    var boothName: String? = null

    @SerializedName("booth_name_eng")
    var boothNameEng: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        acNo = parcel.readLong()
        divNo = parcel.readLong()
        villageNo = parcel.readLong()
        boothNo = parcel.readLong()
        boothName = parcel.readString()
        boothNameEng = parcel.readString()
    }

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            boothNameEng ?: ""
        } else {
            boothName ?: ""
        }
    }

    fun getName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            boothNameEng
        } else {
            boothName
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(acNo)
        parcel.writeLong(divNo)
        parcel.writeLong(villageNo)
        parcel.writeLong(boothNo)
        parcel.writeString(boothName)
        parcel.writeString(boothNameEng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booth> {
        override fun createFromParcel(parcel: Parcel): Booth {
            return Booth(parcel)
        }

        override fun newArray(size: Int): Array<Booth?> {
            return arrayOfNulls(size)
        }
    }
}


