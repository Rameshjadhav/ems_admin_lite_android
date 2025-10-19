package com.ems.lite.admin.report.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs

class ReportOption() : Parcelable {
    var icon: Int = 0

    var title: String? = null

    var type: String = Enums.ReportType.ALPHABETICAL.toString()

    constructor(parcel: Parcel) : this() {
        icon = parcel.readInt()
        title = parcel.readString()
        type = parcel.readString() ?: Enums.ReportType.ALPHABETICAL.toString()
    }


    fun getFullName(): String? {
        return title
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(icon)
        parcel.writeString(title)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReportOption> {
        override fun createFromParcel(parcel: Parcel): ReportOption {
            return ReportOption(parcel)
        }

        override fun newArray(size: Int): Array<ReportOption?> {
            return arrayOfNulls(size)
        }
    }

}