package com.deliveryhero.injection.viewmodel

import com.deliveryhero.injection.scope.ViewModelScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ViewModelScope::class)
public interface ViewModelModule {

    @Multibinds
    public fun bindViewModelMap(): ViewModelMap
}
