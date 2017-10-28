package com.obaied.mailme

import android.app.Activity
import android.app.Application
import android.content.Context
import com.obaied.mailme.data.local.NotificationChannelManager
import com.obaied.mailme.injection.component.DaggerApplicationComponent
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

        initTimber()
        initDagger()

        NotificationChannelManager.makeNotificationChannels(this)
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

    override fun activityInjector(): AndroidInjector<Activity>
            = activityInjector
}
