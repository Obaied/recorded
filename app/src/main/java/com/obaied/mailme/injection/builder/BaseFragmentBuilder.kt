package com.obaied.mailme.injection.builder

import com.obaied.mailme.injection.scope.FragmentScope
import com.obaied.mailme.injection.module.NotesFragmentModule
import com.obaied.mailme.injection.module.RecordingFragmentModule
import com.obaied.mailme.injection.subcomponent.BaseFragmentSubcomponent
import com.obaied.mailme.ui.notes.NotesFragment
import com.obaied.mailme.ui.recording.RecordingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ab on 12.10.17.
 */
@Module(subcomponents = arrayOf(BaseFragmentSubcomponent::class))
abstract class BaseFragmentBuilder {
    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(NotesFragmentModule::class))
    abstract fun providesNotesFragmentInjector(): NotesFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = arrayOf(RecordingFragmentModule::class))
    abstract fun providesRecordingFragmentInjector(): RecordingFragment
}