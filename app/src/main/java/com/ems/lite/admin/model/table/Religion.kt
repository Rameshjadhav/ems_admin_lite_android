package com.ems.lite.admin.model.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


@Entity
class Religion() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @SerializedName("religion_no")
    var religionNo: Long = 0

    @SerializedName("religion_name")
    var religionName: String? = null

    @SerializedName("religion_name_eng")
    var religionNameEng: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        religionNo = parcel.readLong()
        religionName = parcel.readString()
        religionNameEng = parcel.readString()
    }

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString() && !religionNameEng.isNullOrEmpty()) {
            religionNameEng!!
        } else if (!religionNameEng.isNullOrEmpty()) {
            religionName!!
        } else ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(religionNo)
        parcel.writeString(religionName)
        parcel.writeString(religionNameEng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Religion> {
        override fun createFromParcel(parcel: Parcel): Religion {
            return Religion(parcel)
        }

        override fun newArray(size: Int): Array<Religion?> {
            return arrayOfNulls(size)
        }
    }

}


