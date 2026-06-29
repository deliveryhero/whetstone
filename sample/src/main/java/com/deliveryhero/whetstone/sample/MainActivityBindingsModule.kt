package com.deliveryhero.whetstone.sample

import com.deliveryhero.whetstone.activity.ActivityScope
import dev.zacsweers.metro.Binds
import dagger.MembersInjector
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap

@BindingContainer
@ContributesTo(ActivityScope::class)
public interface MainActivityBindingsModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    public fun binds(target: MembersInjector<MainActivity>): MembersInjector<*>
}
