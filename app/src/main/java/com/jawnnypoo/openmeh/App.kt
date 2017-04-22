package com.jawnnypoo.openmeh

import android.app.Application

import com.crashlytics.android.Crashlytics
import com.jawnnypoo.openmeh.api.MehClient
import com.jawnnypoo.openmeh.github.GitHubClient
import com.novoda.simplechromecustomtabs.SimpleChromeCustomTabs

import io.fabric.sdk.android.Fabric
import timber.log.Timber

/**
 * MehService
 */
class App : Application() {

    companion object {
        private lateinit var instance: App

        fun get(): App {
            return instance
        }
    }

    lateinit var meh: MehClient

    override fun onCreate() {
        super.onCreate()

        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
        }
        meh = MehClient.create()
        GitHubClient.init()
        SimpleChromeCustomTabs.initialize(this)
    }
}
