package com.ems.lite.admin.model.table

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


@Entity
class Village() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @SerializedName("ac_no")
    var acNo: Long = 0

    @SerializedName("div_no")
    var divNo: Long = 0

    @SerializedName("village_no")
    var villageNo: Long = 0

    @SerializedName("village_name")
    var villageName: String? = null

    @SerializedName("village_name_eng")
    var villageNameEng: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        acNo = parcel.readLong()
        divNo = parcel.readLong()
        villageNo = parcel.readLong()
        villageName = parcel.readString()
        villageNameEng = parcel.readString()
    }

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            villageNameEng ?: ""
        } else {
            villageName ?: ""
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(acNo)
        parcel.writeLong(divNo)
        parcel.writeLong(villageNo)
        parcel.writeString(villageName)
        parcel.writeString(villageNameEng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Village> {
        override fun createFromParcel(parcel: Parcel): Village {
            return Village(parcel)
        }

        override fun newArray(size: Int): Array<Village?> {
            return arrayOfNulls(size)
        }
    }
}


