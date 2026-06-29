package com.deliveryhero.whetstone.sample

import com.deliveryhero.whetstone.service.ServiceScope
import dev.zacsweers.metro.Binds
import dagger.MembersInjector
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap

@BindingContainer
@ContributesTo(ServiceScope::class)
public interface MainServiceBindingsModule {
    @Binds
    @IntoMap
    @ClassKey(MainService::class)
    public fun binds(target: MembersInjector<MainService>): MembersInjector<*>
}
