package com.deliveryhero.whetstone.view

import android.view.View
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.activity.ActivityScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance

/**
 * A Dagger component that has the lifetime of the [android.view.View].
 */
@ContributesSubcomponent(scope = ViewScope::class, parentScope = ActivityScope::class)
@SingleIn(ViewScope::class)
public interface ViewComponent {
    public val membersInjectorMap: MembersInjectorMap

    /**
     * Interface for creating an [ViewComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(@BindsInstance view: View): ViewComponent
    }

    @ContributesTo(ActivityScope::class)
    public interface ParentComponent {
        public fun getViewComponentFactory(): Factory
    }
}
