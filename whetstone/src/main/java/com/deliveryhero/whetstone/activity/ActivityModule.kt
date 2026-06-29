package com.deliveryhero.whetstone.activity

import com.deliveryhero.whetstone.injector.MembersInjectorMap
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds

@BindingContainer
@ContributesTo(ActivityScope::class)
public interface ActivityModule {

    @Multibinds(allowEmpty = true)
    public fun membersInjectors(): MembersInjectorMap
}
