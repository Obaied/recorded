package com.obaied.mailme.injection.component

import android.app.Application
import com.obaied.mailme.injection.builder.BaseFragmentBuilder
import com.obaied.mailme.GlobalApplication
import com.obaied.mailme.injection.builder.BaseActivityBuilder
import com.obaied.mailme.injection.module.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        AndroidInjectionModule::class,

        BaseActivityBuilder::class,
        NotesActivityModule::class,
        RecordingActivityModule::class,

        BaseFragmentBuilder::class,
        NotesFragmentModule::class,
        RecordingFragmentModule::class,

        ApplicationModule::class
))
interface ApplicationComponent : AndroidInjector<Application> {
    fun inject(app: GlobalApplication)
}

