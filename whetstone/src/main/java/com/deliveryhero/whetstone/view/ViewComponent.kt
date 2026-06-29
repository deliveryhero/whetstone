package com.deliveryhero.whetstone.view

import android.view.View
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.activity.ActivityScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

/**
 * A Metro component that has the lifetime of the [android.view.View].
 */
@GraphExtension
public interface ViewComponent {
    public val membersInjectorMap: MembersInjectorMap

    /**
     * Interface for creating an [ViewComponent].
     */
    @GraphExtension.Factory
    public interface Factory {
        public fun create(@Provides view: View): ViewComponent
    }

    @ContributesTo(ActivityScope::class)
    public interface ParentComponent {
        public fun getViewComponentFactory(): Factory
    }
}
