package com.ems.lite.admin.utils

object Enums {

    enum class Language {
        en, kn, mr, te, ta
    }

    enum class Committee {
        ADMIN, MEMBER
    }

    enum class Gender {
        M, F
    }

    enum class Type {
        ALL, SINGLE, FROM_TO, CUSTOM
    }

    enum class ReportType {
        ELECTION_DAY, FAMILY, ALPHABETICAL, VOTER_NO, SURNAME, ADDRESS, MOBILE, INFLUENCER,
        CAST, CAST_SURVEY, STATUS, PROFESSION, OUTSTATION, DUPLICATE, IMP_VOTER,
        AGE, DEAD, RELATIVE, BOOTH_COMMITTEE, MOBILE_CAST_COUNT
    }

    enum class Status {
        SELECT, GREEN, RED, YELLOW, ORANGE, OTHER
    }

    enum class HomeOptionType {
        SEARCH_VOTER, REPORT, IMPORT_DATA, SYNC_DATA, REFRESH_MASTERS, SETTINGS, ACTIVATE_USERS, EXPORT_TO_EXCEL, UPDATE_BOOTH
    }
}