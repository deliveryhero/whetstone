package com.deliveryhero.whetstone.view

import com.deliveryhero.whetstone.injector.MembersInjectorMap
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@BindingContainer
@ContributesTo(ViewScope::class)
public interface ViewModule {

    @Multibinds(allowEmpty = true)
    public fun membersInjectors(): MembersInjectorMap
}
