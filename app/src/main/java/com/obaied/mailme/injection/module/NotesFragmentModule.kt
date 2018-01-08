package com.joseph.mailme.injection.module

import com.joseph.mailme.data.DataManager
import com.joseph.mailme.ui.notes.NotesAdapter
import com.joseph.mailme.ui.notes.NotesPresenter
import com.joseph.mailme.util.Schedulers.SchedulerProvider
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