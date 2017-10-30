package com.obaied.mailme.injection.module

import com.obaied.mailme.data.DataManager
import com.obaied.mailme.ui.notes.NotesAdapter
import com.obaied.mailme.ui.notes.NotesPresenter
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by ab on 13.10.17.
 */
@Module
class NotesFragmentModule {
    @Provides
    fun providesNotesAdapter() = NotesAdapter()

    @Provides
    fun providesNotesPresenter(dataManager: DataManager,
                               compositeDisposable: CompositeDisposable,
                               schedulerProvider: SchedulerProvider)
            = NotesPresenter(dataManager,
            compositeDisposable,
            schedulerProvider)
}