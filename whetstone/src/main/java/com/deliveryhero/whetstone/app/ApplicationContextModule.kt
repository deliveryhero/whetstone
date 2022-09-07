package com.deliveryhero.whetstone.app

import android.app.Application
import android.content.Context
import com.deliveryhero.whetstone.ForScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module

@Module
@ContributesTo(ApplicationScope::class)
public interface ApplicationContextModule {

    @Binds
    @ForScope(ApplicationScope::class)
    public fun bindContext(application: Application): Context
}
