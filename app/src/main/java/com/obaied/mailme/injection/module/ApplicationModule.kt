package com.obaied.mailme.injection.module

import com.obaied.mailme.data.local.PrefManager
import com.obaied.mailme.ui.navigation.Navigator
import com.obaied.mailme.util.Schedulers.AppSchedulerProvider
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

/**
 * Created by ab on 18/03/2017.
 */

@Module
class ApplicationModule {
    @Provides
    @Singleton
    fun provideNavigator(): Navigator = Navigator()

    @Provides
    fun providesCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun providesSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

    @Provides
    fun providesPrefManager(): PrefManager = PrefManager()
}
