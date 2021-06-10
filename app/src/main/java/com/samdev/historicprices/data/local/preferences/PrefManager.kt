package com.samdev.historicprices.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.samdev.historicprices.AppConstants
import javax.inject.Inject

/**
 * @author Sam
 */

class PrefManager @Inject constructor(
    context: Context
) : IPrefManager {

    private var preferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)

    override fun setFirstRun(status: Boolean) {
        preferences.edit()
            .putBoolean(AppConstants.PREF_IS_FIRST_RUN, status)
            .apply()
    }

    override fun isFirstRun(): Boolean {
        return preferences.getBoolean(AppConstants.PREF_IS_FIRST_RUN, true)
    }
}