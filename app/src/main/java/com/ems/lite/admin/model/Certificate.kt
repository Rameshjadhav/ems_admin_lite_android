package com.ems.lite.admin.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Certificate() : Parcelable {
//    @SerializedName("uuid")
//    var uuid: String? = null
//
//    @SerializedName("matrix_no")
//    var matrixNo: String? = null
//
//    @SerializedName("serial_no")
//    var serialNo: String? = null
//
//    @SerializedName("name")
//    var name: String? = null
//
//    @SerializedName("program_e")
//    var programE: String? = null
//
//    @SerializedName("description")
//    var description: String? = null
//
//    @SerializedName("faculty")
//    var faculty: String? = null
//
//    @SerializedName("issue_date")
//    var issueDate: String? = null


    @SerializedName("blockchainurl")
    var blockchainurl: String? = null

    @SerializedName("blockchainstatus")
    var blockchainstatus: String? = null // success

    @SerializedName("blockchainverificationstatus")
    var blockchainverificationstatus: String? = null // success

    @SerializedName("blockchaintimestamp")
    var blockchaintimestamp: String? = null

    @SerializedName("blockchaintransactionid")
    var blockchaintransactionid: String? = null

    @SerializedName("blockchaindata")
    var blockchaindata: String? = null

    @SerializedName("file_url")
    var fileUrl: FileUrl? = null

    constructor(parcel: Parcel) : this() {
        blockchainurl = parcel.readString()
        blockchainstatus = parcel.readString()
        blockchainverificationstatus = parcel.readString()
        blockchaintimestamp = parcel.readString()
        blockchaintransactionid = parcel.readString()
        blockchaindata = parcel.readString()
        fileUrl = parcel.readParcelable(FileUrl::class.java.classLoader)
    }

    class FileUrl() : Parcelable {
        var en: String? = null
        var bm: String? = null

        constructor(parcel: Parcel) : this() {
            en = parcel.readString()
            bm = parcel.readString()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(en)
            parcel.writeString(bm)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FileUrl> {
            override fun createFromParcel(parcel: Parcel): FileUrl {
                return FileUrl(parcel)
            }

            override fun newArray(size: Int): Array<FileUrl?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(blockchainurl)
        parcel.writeString(blockchainstatus)
        parcel.writeString(blockchainverificationstatus)
        parcel.writeString(blockchaintimestamp)
        parcel.writeString(blockchaintransactionid)
        parcel.writeString(blockchaindata)
        parcel.writeParcelable(fileUrl, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Certificate> {
        override fun createFromParcel(parcel: Parcel): Certificate {
            return Certificate(parcel)
        }

        override fun newArray(size: Int): Array<Certificate?> {
            return arrayOfNulls(size)
        }
    }
}