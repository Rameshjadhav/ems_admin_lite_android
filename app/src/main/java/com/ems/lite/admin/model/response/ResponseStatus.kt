package com.ems.lite.admin.model.response

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by Ramesh on 30/12/2020
 */
open class ResponseStatus() : Parcelable {
    var statusCode = 0
    private var message: String? = null

    constructor(parcel: Parcel) : this() {
        statusCode = parcel.readInt()
        message = parcel.readString()
    }

    fun getMessage(): String {
        return if (message == null) {
            ""
        } else message!!
    }

    open fun setMessage(message: String?) {
        this.message = message
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(statusCode)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    @SuppressLint("ParcelCreator")
    companion object CREATOR : Creator<ResponseStatus> {
        const val STATUS_CODE_SUCCESS = 200
        const val STATUS_CODE_CREATED = 201

        //    const val STATUS_CODE_CONFLICT = 409;
        // This is custom code for handling timeout error
        const val STATUS_CODE_ERROR_TIMEOUT = 5002
        override fun createFromParcel(parcel: Parcel): ResponseStatus {
            return ResponseStatus(parcel)
        }

        override fun newArray(size: Int): Array<ResponseStatus?> {
            return arrayOfNulls(size)
        }
    }
}