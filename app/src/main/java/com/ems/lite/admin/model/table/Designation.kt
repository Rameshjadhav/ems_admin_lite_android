package com.ems.lite.admin.model.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName

@Entity
class Designation() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @SerializedName("designation_name_eng")
    var designationNameEng: String? = null

    @SerializedName("designation_name")
    var designationName: String? = null

    @SerializedName("designation_no")
    var designationNo: Long = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        designationNameEng = parcel.readString()
        designationName = parcel.readString()
        designationNo = parcel.readLong()
    }

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString() && !designationNameEng.isNullOrEmpty()) {
            designationNameEng!!
        } else if (!designationName.isNullOrEmpty()) {
            designationName!!
        } else ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(designationNameEng)
        parcel.writeString(designationName)
        parcel.writeLong(designationNo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Designation> {
        override fun createFromParcel(parcel: Parcel): Designation {
            return Designation(parcel)
        }

        override fun newArray(size: Int): Array<Designation?> {
            return arrayOfNulls(size)
        }
    }
}