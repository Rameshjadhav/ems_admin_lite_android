package com.ems.lite.admin.report.model

import android.os.Parcel
import android.os.Parcelable
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Village

class ReportFilter() : Parcelable {

    var village: Village? = null
    var booth: Booth? = null

    constructor(parcel: Parcel) : this() {
        village = parcel.readParcelable(Village::class.java.classLoader)
        booth = parcel.readParcelable(Booth::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(village, flags)
        parcel.writeParcelable(booth, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReportFilter> {
        override fun createFromParcel(parcel: Parcel): ReportFilter {
            return ReportFilter(parcel)
        }

        override fun newArray(size: Int): Array<ReportFilter?> {
            return arrayOfNulls(size)
        }
    }

}