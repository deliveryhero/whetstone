package com.deliveryhero.whetstone.service

import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ServiceScope::class)
public interface ServiceModule {

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
