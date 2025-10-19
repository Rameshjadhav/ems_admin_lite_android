package com.ems.lite.admin.model.table

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.utils.Enums
import com.ems.lite.admin.utils.Prefs
import com.google.gson.annotations.SerializedName


class CountBy() : Parcelable {

    @SerializedName("id")
    var id: Int? = 0

    @SerializedName("cardno")
    var cardNo: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("nameEng")
    var nameEng: String? = null

    @SerializedName("totalCount")
    var totalCount: Int = 0

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        cardNo = parcel.readString()
        name = parcel.readString()
        nameEng = parcel.readString()
        totalCount = parcel.readInt()
    }

    override fun toString(): String {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            if (!nameEng.isNullOrEmpty())
                nameEng!! else ""
        } else {
            if (!name.isNullOrEmpty())
                name!! else ""
        }
    }

    fun getDisplayName(): String? {
        return if (Prefs.lang == Enums.Language.en.toString()) {
            nameEng
        } else {
            name
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(cardNo)
        parcel.writeString(name)
        parcel.writeString(nameEng)
        parcel.writeInt(totalCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CountBy> {
        override fun createFromParcel(parcel: Parcel): CountBy {
            return CountBy(parcel)
        }

        override fun newArray(size: Int): Array<CountBy?> {
            return arrayOfNulls(size)
        }
    }
}