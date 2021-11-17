package com.deliveryhero.whetstone

import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentFactory
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.MergeComponent
import dagger.Component

// DO NOT CHANGE THIS FILE.
//
// We plan to make it to be "auto-generated" during compilation.
// Therefore, please do not modify it manually.

@MergeComponent(ApplicationScope::class)
@SingleIn(ApplicationScope::class)
public interface GeneratedApplicationComponent : ApplicationComponent {

    @Component.Factory
    public interface Factory : ApplicationComponentFactory
}
