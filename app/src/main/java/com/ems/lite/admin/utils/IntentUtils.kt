@file:Suppress("INFERRED_TYPE_VARIABLE_INTO_POSSIBLE_EMPTY_INTERSECTION", "DEPRECATION")

package com.ems.lite.admin.utils

import android.content.Intent
import android.os.Build
import com.ems.lite.admin.model.User
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.model.table.Village

object IntentUtils {
    fun putVillageToIntent(intent: Intent, village: Village?) {
        intent.putExtra(IntentConstants.VILLAGE, village)
    }

    fun getVillageFromIntent(intent: Intent?): Village? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(IntentConstants.VILLAGE, Village::class.java)
        } else {
            intent?.getParcelableExtra(IntentConstants.VILLAGE)
        }
    }

    fun putBoothToIntent(intent: Intent, booth: Booth?) {
        intent.putExtra(IntentConstants.BOOTH, booth)
    }

    fun getBoothFromIntent(intent: Intent?): Booth? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(IntentConstants.BOOTH, Booth::class.java)
        } else {
            intent?.getParcelableExtra(IntentConstants.BOOTH)
        }
    }

    fun putUserToIntent(intent: Intent, user: User?) {
        intent.putExtra(IntentConstants.USER, user)
    }

    fun getUserFromIntent(intent: Intent?): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(IntentConstants.USER, User::class.java)
        } else {
            intent?.getParcelableExtra(IntentConstants.USER)
        }
    }
}