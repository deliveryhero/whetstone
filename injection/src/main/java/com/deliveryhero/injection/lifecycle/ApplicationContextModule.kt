package com.deliveryhero.injection.lifecycle

import android.app.Application
import android.content.Context
import com.deliveryhero.injection.ForScope
import com.deliveryhero.injection.scope.ApplicationScope
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
