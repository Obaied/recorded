package com.obaied.mailme.injection.subcomponent

import com.obaied.mailme.ui.base.BaseFragment
import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by ab on 13.10.17.
 */
@Subcomponent
interface BaseFragmentSubcomponent : AndroidInjector<BaseFragment> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BaseFragment>()
}