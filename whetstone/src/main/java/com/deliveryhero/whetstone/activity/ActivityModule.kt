package com.deliveryhero.whetstone.activity

import android.app.Activity
import android.content.Context
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ActivityScope::class)
public interface ActivityModule {

    @Binds
    @ForScope(ActivityScope::class)
    public fun Activity.bindContext(): Context

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
