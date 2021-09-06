package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.scope.ViewModelScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
@ContributesTo(ViewModelScope::class)
public object ViewModelModule {

    // Workaround for: https://stackoverflow.com/questions/61558178/provide-a-multibiding-of-an-empty-map-of-providers-with-dagger-2
    @Provides
    @IntoMap
    @ViewModelKey(NullViewModel::class)
    public fun provideViewModel(): ViewModel {
        error("${NullViewModel::class.java.name} should never be instantiated.")
    }

    internal class NullViewModel : ViewModel()
}
