package com.deliveryhero.whetstone.sample

import com.deliveryhero.whetstone.view.ViewScope
import dev.zacsweers.metro.Binds
import dagger.MembersInjector
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap

@BindingContainer
@ContributesTo(ViewScope::class)
public interface MainViewBindingsModule {
    @Binds
    @IntoMap
    @ClassKey(MainView::class)
    public fun binds(target: MembersInjector<MainView>): MembersInjector<*>
}
