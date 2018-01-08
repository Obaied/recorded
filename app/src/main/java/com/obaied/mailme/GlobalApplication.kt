package com.joseph.mailme

import android.app.Activity
import android.app.Application
import android.content.Context
import com.joseph.mailme.data.local.NotificationChannelManager
import com.joseph.mailme.injection.component.DaggerApplicationComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by ab on 02/04/2017.
 */

class GlobalApplication :
        Application(),
        HasActivityInjector {
    companion object {
        lateinit var appContext: Context
            private set
    }

    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        appContext = this

        initLeakCanary()
        initTimber()
        initDagger()

        NotificationChannelManager.makeNotificationChannels(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>
            = activityInjector

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }

        LeakCanary.install(this)
    }

    private fun initDagger() {
        DaggerApplicationComponent
                .create()
                .inject(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
