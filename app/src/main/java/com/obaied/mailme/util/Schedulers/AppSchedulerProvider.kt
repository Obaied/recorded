package com.joseph.mailme.util.Schedulers

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by ab on 09/04/2017.
 */

class AppSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler
            = AndroidSchedulers.mainThread()

    override fun computation(): Scheduler
            = Schedulers.computation()

    override fun trampoline(): Scheduler
            = Schedulers.trampoline()

    override fun newThread(): Scheduler
            = Schedulers.newThread()

    override fun io(): Scheduler
            = Schedulers.io()
}