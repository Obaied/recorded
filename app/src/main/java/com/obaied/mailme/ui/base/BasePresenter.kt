package com.joseph.mailme.ui.base

import com.joseph.mailme.data.DataManager
import com.joseph.mailme.util.Schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by ab on 18/03/2017.
 */

open class BasePresenter<T : MvpView>
@Inject constructor(dataManager: DataManager,
                    compositeDisposable: CompositeDisposable,
                    schedulerProvider: SchedulerProvider) {

    protected var dataManager: DataManager = dataManager
        private set

    protected var mCompositeDisposable = compositeDisposable
        private set

    protected var schedulerProvider = schedulerProvider
        private set

    var mvpView: T? = null
        private set

    open fun attachView(mvpView: T) {
        this.mvpView = mvpView
    }

    open fun detachView() {
        mCompositeDisposable.dispose()
        mvpView = null
    }

    private val isViewAttached: Boolean
        get() = mvpView != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    class MvpViewNotAttachedException
        : RuntimeException("Please call Presenter.attachView(MvpView) before"
            + " requesting data to the Presenter")
}