package com.joseph.mailme.injection.subcomponent

import com.joseph.mailme.ui.base.BaseActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by ab on 13.10.17.
 */
@Subcomponent
interface BaseActivitySubcomponent : AndroidInjector<BaseActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BaseActivity>()
}