package com.obaied.mailme.injection.module

import com.obaied.mailme.ui.audioplayer_service.AudioPlayerManager
import com.obaied.mailme.ui.notes.NotesActivity
import dagger.Module
import dagger.Provides

/**
 * Created by ab on 13.10.17.
 */
@Module
class NotesActivityModule {
    @Provides
    fun provideAudioPlayerManager(activity: NotesActivity): AudioPlayerManager = AudioPlayerManager(activity)
}