package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import kotlin.reflect.KClass

@BindingContainer
@ContributesTo(ViewModelScope::class)
public interface ViewModelModule {

    @Multibinds(allowEmpty = true)
    public fun provideViewModel(): Map<KClass<*>, ViewModel>
}
