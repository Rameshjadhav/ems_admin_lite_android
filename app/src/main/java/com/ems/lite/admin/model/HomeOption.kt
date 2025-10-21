package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs

class HomeOption() : Parcelable {
    var icon: Int = 0

    var title: String? = null

    var titleEng: String? = null

    var type: String? = null

    constructor(parcel: Parcel) : this() {
        icon = parcel.readInt()
        title = parcel.readString()
        titleEng = parcel.readString()
        type = parcel.readString()
    }


    fun getFullName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            titleEng
        } else {
            title
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(icon)
        parcel.writeString(title)
        parcel.writeString(titleEng)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeOption> {
        override fun createFromParcel(parcel: Parcel): HomeOption {
            return HomeOption(parcel)
        }

        override fun newArray(size: Int): Array<HomeOption?> {
            return arrayOfNulls(size)
        }
    }

}