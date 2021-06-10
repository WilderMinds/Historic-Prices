package com.samdev.historicprices

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


/**
 * @author Sam
 */
@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}