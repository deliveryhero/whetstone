package com.deliveryhero.whetstone.view

import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ViewScope::class)
public interface ViewModule {

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
