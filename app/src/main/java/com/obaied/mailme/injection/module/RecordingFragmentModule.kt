package com.obaied.mailme.injection.module

import com.obaied.mailme.data.DataManager
import com.obaied.mailme.ui.recording.RecordingPresenter
import com.obaied.mailme.util.Schedulers.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by ab on 13.10.17.
 */
@Module
class RecordingFragmentModule {
    @Provides
    fun providesRecordingPresenter(dataManager: DataManager,
                                   compositeDisposable: CompositeDisposable,
                                   schedulerProvider: SchedulerProvider)
            = RecordingPresenter(dataManager,
            compositeDisposable,
            schedulerProvider)
}