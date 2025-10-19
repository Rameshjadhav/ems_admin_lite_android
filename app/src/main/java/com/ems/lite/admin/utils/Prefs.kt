package com.ems.lite.admin.utils

import android.content.SharedPreferences
import com.ems.lite.admin.app.MyApplication
import com.ems.lite.admin.model.Setting
import com.ems.lite.admin.model.User
import com.google.gson.Gson

object Prefs {
    private const val IS_VOTER_SYNC = "is_voter_sync"
    private const val USER_OBJECT = "user_object"
    private const val IS_VILLAGE_SYNC = "is_ward_sync"
    private const val IS_BOOTH_SYNC = "is_booth_sync"
    private const val IS_CAST_SYNC = "is_cast_sync"
    private const val IS_RELIGION_SYNC = "is_religion_sync"
    private const val IS_DESIGNATION_SYNC = "is_designation_sync"
    private const val IS_PROFESSION_SYNC = "is_profession_sync"
    private const val IS_STATUS_SYNC = "is_status_sync"
    private const val PREFS_FILENAME = "Volunteer"
    private const val IS_LOGIN = "IS_LOGIN"
    private const val SHARE_URL = "SHARE_URL"
    private const val TOTAL_COUNT = "total_count"
    private const val LIMIT = "limit"
    private const val LANGUAGE = "language"
    private const val VOTING_DATE = "VOTING_DATE"
    private const val VOTING_TIME = "voting_time"
    private const val HEADER_IMAGE = "header_image"
    private const val FOOTER_MESSAGE = "footer_message"
    private const val SETTING = "setting"
    private const val PASSWORD = "password"
    private const val IS_GENERAL_MSG = "is_general_msg"
    private const val IS_WITH_IMAGE_MSG = "is_with_image_msg"
    private const val PRINTER_NAME = "printer_name"
    private const val PRINT_URL = "PRINT_URL"

    private val prefs: SharedPreferences =
        MyApplication.instance!!.applicationContext!!.getSharedPreferences(PREFS_FILENAME, 0)
    val gson = Gson()

    var isVoterSync: Boolean
        get() = prefs.getBoolean(IS_VOTER_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_VOTER_SYNC, value).apply()
    var isVillageSync: Boolean
        get() = prefs.getBoolean(IS_VILLAGE_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_VILLAGE_SYNC, value).apply()
    var isBoothSync: Boolean
        get() = prefs.getBoolean(IS_BOOTH_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_BOOTH_SYNC, value).apply()
    var isReligionSync: Boolean
        get() = prefs.getBoolean(IS_RELIGION_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_RELIGION_SYNC, value).apply()
    var isCastSync: Boolean
        get() = prefs.getBoolean(IS_CAST_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_CAST_SYNC, value).apply()
    var isDesignationSync: Boolean
        get() = prefs.getBoolean(IS_DESIGNATION_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_DESIGNATION_SYNC, value).apply()
    var isProfessionSync: Boolean
        get() = prefs.getBoolean(IS_PROFESSION_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_PROFESSION_SYNC, value).apply()
    var isStatusSync: Boolean
        get() = prefs.getBoolean(IS_STATUS_SYNC, false)
        set(value) = prefs.edit().putBoolean(IS_STATUS_SYNC, value).apply()
    var password: String?
        get() = prefs.getString(PASSWORD, null)
        set(value) = prefs.edit().putString(PASSWORD, value).apply()

    var user: User?
        get() = gson.fromJson(
            prefs.getString(USER_OBJECT, null),
            User::class.java
        )
        set(value) = prefs.edit().putString(USER_OBJECT, gson.toJson(value))
            .apply()

    var isLogin: Boolean
        get() = prefs.getBoolean(IS_LOGIN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGIN, value).apply()

    var shareImageUrl: String?
        get() = prefs.getString(SHARE_URL, "")
        set(value) = prefs.edit().putString(SHARE_URL, value).apply()
    var printImageUrl: String?
        get() = prefs.getString(PRINT_URL, "")
        set(value) = prefs.edit().putString(PRINT_URL, value).apply()

    var totalCount: Long
        get() = prefs.getLong(TOTAL_COUNT, 0)
        set(value) = prefs.edit().putLong(TOTAL_COUNT, value).apply()
    var limit: Long
        get() = prefs.getLong(LIMIT, 2000)
        set(value) = prefs.edit().putLong(LIMIT, value).apply()
    var lang: String
        get() = prefs.getString(LANGUAGE, Enums.Language.en.toString())!!
        set(value) = prefs.edit().putString(LANGUAGE, value).apply()
    var votingDate: String?
        get() = prefs.getString(VOTING_DATE, "")
        set(value) = prefs.edit().putString(VOTING_DATE, value).apply()
    var votingTime: String?
        get() = prefs.getString(VOTING_TIME, "")
        set(value) = prefs.edit().putString(VOTING_TIME, value).apply()

    var headerImage: String?
        get() = prefs.getString(HEADER_IMAGE, "")
        set(value) = prefs.edit().putString(HEADER_IMAGE, value).apply()

    var footerMessage: String?
        get() = prefs.getString(FOOTER_MESSAGE, "")
        set(value) = prefs.edit().putString(FOOTER_MESSAGE, value).apply()
    var printerName: String?
        get() = prefs.getString(PRINTER_NAME, "")
        set(value) = prefs.edit().putString(PRINTER_NAME, value).apply()
    var setting: Setting?
        get() = gson.fromJson(
            prefs.getString(SETTING, null),
            Setting::class.java
        )
        set(value) = prefs.edit().putString(SETTING, gson.toJson(value))
            .apply()
    var isGeneralMsg: Boolean
        get() = prefs.getBoolean(IS_GENERAL_MSG, false)
        set(value) = prefs.edit().putBoolean(IS_GENERAL_MSG, value).apply()
    var isWithImageMsg: Boolean
        get() = prefs.getBoolean(IS_WITH_IMAGE_MSG, true)
        set(value) = prefs.edit().putBoolean(IS_WITH_IMAGE_MSG, value).apply()
}