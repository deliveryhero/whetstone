package com.deliveryhero.whetstone.sample

import android.app.Application
import android.content.res.Resources
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(ApplicationScope::class)
object MainModule {

    @Provides
    fun providesResources(application: Application): Resources = application.resources
}
