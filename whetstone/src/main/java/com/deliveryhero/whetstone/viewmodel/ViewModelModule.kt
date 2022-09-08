package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ViewModelScope::class)
public interface ViewModelModule {

    @Multibinds
    public fun provideViewModel(): Map<Class<*>, ViewModel>
}
