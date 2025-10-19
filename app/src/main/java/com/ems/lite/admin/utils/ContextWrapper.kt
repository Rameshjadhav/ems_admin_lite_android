package com.ems.lite.admin.utils

import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.Locale

class ContextWrapper(base: Context?) : android.content.ContextWrapper(base) {

    companion object {
        fun wrap(context1: Context, newLocale: Locale?): ContextWrapper {
            var context = context1
            val configuration = context1.resources.configuration
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    configuration.setLocale(newLocale)
                    val localeList = LocaleList(newLocale)
                    LocaleList.setDefault(localeList)
                    context = context.createConfigurationContext(configuration)
                }
                else -> {
                    configuration.locale = newLocale
                    context.resources.updateConfiguration(
                        configuration,
                        context.resources.displayMetrics
                    )
                }
            }
            return ContextWrapper(context)
        }
    }
}