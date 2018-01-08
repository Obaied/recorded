package com.joseph.mailme.util.Schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by ab on 09/04/2017.
 */
class TestSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler
            = Schedulers.trampoline()

    override fun computation(): Scheduler
            = Schedulers.trampoline()

    override fun trampoline(): Scheduler
            = Schedulers.trampoline()

    override fun newThread(): Scheduler
            = Schedulers.trampoline()

    override fun io(): Scheduler
            = Schedulers.trampoline()
}
