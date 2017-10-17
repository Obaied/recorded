package com.obaied.mailme.injection.builder

import com.obaied.mailme.injection.module.NotesActivityModule
import com.obaied.mailme.injection.module.RecordingActivityModule
import com.obaied.mailme.injection.scope.ActivityScope
import dagger.Module
import com.obaied.mailme.injection.subcomponent.BaseActivitySubcomponent
import com.obaied.mailme.ui.notes.NotesActivity
import com.obaied.mailme.ui.recording.RecordingActivity
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
}