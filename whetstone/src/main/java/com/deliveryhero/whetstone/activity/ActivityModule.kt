package com.deliveryhero.whetstone.activity

import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ActivityScope::class)
public interface ActivityModule {

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
