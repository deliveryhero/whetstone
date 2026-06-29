package com.deliveryhero.whetstone.service

import com.deliveryhero.whetstone.injector.MembersInjectorMap
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@BindingContainer
@ContributesTo(ServiceScope::class)
public interface ServiceModule {

    @Multibinds(allowEmpty = true)
    public fun membersInjectors(): MembersInjectorMap
}
