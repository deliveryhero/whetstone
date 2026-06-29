package com.deliveryhero.whetstone.sample

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.sample.library.MainViewModel
import com.deliveryhero.whetstone.viewmodel.ViewModelScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap

@BindingContainer
@ContributesTo(ViewModelScope::class)
public interface MainViewModelBindingsModule {
    @Binds
    @IntoMap
    @ClassKey(MainViewModel::class)
    public fun binds(target: MainViewModel): ViewModel
}
