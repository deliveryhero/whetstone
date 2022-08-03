package com.deliveryhero.whetstone.sample

import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import javax.inject.Singleton

// DO NOT CHANGE THIS FILE.
//
// We plan to make it to be "auto-generated" during compilation.
// Therefore, please do not modify it manually.

@MergeComponent(ApplicationScope::class)
@SingleIn(ApplicationScope::class)
@Singleton
public interface GeneratedApplicationComponent : ApplicationComponent {

    @Component.Factory
    public interface Factory : ApplicationComponent.Factory

    public companion object Default: Factory by DaggerGeneratedApplicationComponent.factory()
}
