package com.joseph.mailme.injection.builder

import com.joseph.mailme.injection.module.AboutActivityModule
import com.joseph.mailme.injection.module.NotesActivityModule
import com.joseph.mailme.injection.module.RecordingActivityModule
import com.joseph.mailme.injection.scope.ActivityScope
import com.joseph.mailme.injection.subcomponent.BaseActivitySubcomponent
import com.joseph.mailme.ui.about.AboutActivity
import com.joseph.mailme.ui.notes.NotesActivity
import com.joseph.mailme.ui.recording.RecordingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ab on 12.10.17.
 */
@Module(subcomponents = arrayOf(BaseActivitySubcomponent::class))
abstract class BaseActivityBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(NotesActivityModule::class))
    abstract fun providesNotesActivityInjector(): NotesActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(RecordingActivityModule::class))
    abstract fun providesRecordingActivityInjector(): RecordingActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(AboutActivityModule::class))
    abstract fun providesAboutActivityInjector(): AboutActivity
}